
package com.demo.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.demo.filter.JwtFilter;
import com.demo.shiro.RedisCacheManager;
import com.demo.shiro.ShiroSessionManager;
import com.demo.shiro.realm.JwtRealm;
import com.demo.shiro.realm.PasswordRealm;
import com.demo.shiro.realm.DemoRealm;
import com.demo.shiro.realm.UserModularRealmAuthenticator;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: Shiro配置
*/
@Configuration
public class ShiroConfig {

	/**
	 * 密码登录时指定匹配器，
	 * 
	 * @return HashedCredentialsMatcher
	 */
	@Bean("hashedCredentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		// 设置哈希算法名称
		matcher.setHashAlgorithmName("MD5");
		// 设置哈希迭代次数
		matcher.setHashIterations(666);
		// 设置存储凭证十六进制编码
		matcher.setStoredCredentialsHexEncoded(true);
		return matcher;
	}

	/**
	 * 如果需要密码匹配器则需要进行指定 密码登录Realm
	 * @return PasswordRealm
	 */
	@Bean
	public PasswordRealm passwordRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher) {
		PasswordRealm passwordRealm = new PasswordRealm();
		passwordRealm.setCredentialsMatcher(matcher);
		return passwordRealm;
	}

	/**
	 * jwtRealm
	 * @return JwtRealm
	 */
	@Bean
	public JwtRealm jwtRealm() {
		return new JwtRealm();
	}

	/**
	 * demoRealm
	 * @return DemoRealm
	 */
	@Bean
	public DemoRealm demoRealm() {
		return new DemoRealm();
	}

	/**
	 * Shiro内置过滤器，可以实现拦截器相关的拦截器
	 * 常用的过滤器：
	 * anon：无需认证（登录）可以访问
	 * authc：必须认证才可以访问
	 * user：如果使用rememberMe的功能可以直接访问
	 * perms：该资源必须得到资源权限才可以访问
	 * role：该资源必须得到角色权限才可以访问
	 **/
	@Bean
	public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager) {
		
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 设置未登录跳转url
		// shiroFilterFactoryBean.setUnauthorizedUrl("/user/unLogin");

		Map<String, String> filterMap = new LinkedHashMap<String, String>();

		// 只有不需要权限认证(anon)的 需要明确写入filterMap
		filterMap.put("/hello", "anon");
		filterMap.put("/register/*", "anon");
		filterMap.put("/login/*", "anon");

		// 添加自定义过滤器并且取名为jwt
		Map<String, Filter> filter = new HashMap<String, Filter>(1);
		filter.put("jwt", new JwtFilter());
		shiroFilterFactoryBean.setFilters(filter);

		// 过滤链定义，从上向下顺序执行，所以放在最为下边
		filterMap.put("/**", "jwt");

		// 未授权界面返回JSON
		// shiroFilterFactoryBean.setUnauthorizedUrl("/sys/common/403");
		// shiroFilterFactoryBean.setLoginUrl("/sys/common/403");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

		return shiroFilterFactoryBean;
	}

	@Bean
	public UserModularRealmAuthenticator userModularRealmAuthenticator() {
		// 自己重写的ModularRealmAuthenticator
		UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
		modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		return modularRealmAuthenticator;
	}

	/**
	 * SecurityManager 是 Shiro 架构的核心，通过它来链接Realm和用户(文档中称之为Subject.)
	 */
	@Bean
	public SecurityManager securityManager(
			@Qualifier("passwordRealm") PasswordRealm passwordRealm,
			@Qualifier("jwtRealm") JwtRealm jwtRealm,
			@Qualifier("demoRealm") DemoRealm demoRealm,
			@Qualifier("userModularRealmAuthenticator") UserModularRealmAuthenticator userModularRealmAuthenticator) {

		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

		// 设置realm
		securityManager.setAuthenticator(userModularRealmAuthenticator);
		List<Realm> realms = new ArrayList<>();
		// 添加多个realm
		realms.add(passwordRealm);
		realms.add(jwtRealm);
		realms.add(demoRealm);

		/*
		 * 关闭shiro自带的session，详情见文档
		 * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
		 */
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();

		// 不需要将 Shiro Session 中的东西存到任何地方（包括 Http Session 中）
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);

		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		securityManager.setSubjectDAO(subjectDAO);

		// 禁止创建shiro session
		// securityManager.setSubjectFactory(subjectFactory());

		//  解决第一次访问没有JSESSIONID导致404错误
		// securityManager.setSessionManager(mySessionManager());

		//  自定义sessionManager
		securityManager.setSessionManager(shiroSessionManager());

		// 注入记住我管理器
		// securityManager.setRememberMeManager(rememberMeManager());

		// ★★★★★★★★★★★★★★ 使用自定义的 redisCacheManage ★★★★★★★★★★★★★★
		// securityManager.setCacheManager(redisCacheManager());

		// securityManager设置自定义认证规则
		securityManager.setRealms(realms);

		return securityManager;
	}

	@Bean
	public ShiroSessionManager shiroSessionManager() {

		ShiroSessionManager shiroSessionManager = new ShiroSessionManager();

		// TODO redis 配置session持久化
		shiroSessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());

		return shiroSessionManager;
	}

	@Bean
	public DefaultWebSessionManager mySessionManager() {
		DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();

		// defaultWebSessionManager.setGlobalSessionTimeout(60 * 30 * 1000);
		defaultWebSessionManager.setDeleteInvalidSessions(true);
		defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
		defaultWebSessionManager.setSessionIdCookieEnabled(true);

		// 将sessionIdUrlRewritingEnabled属性设置成false
		defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
		return defaultWebSessionManager;
	}

	@Bean
	public RedisCacheManager redisCacheManager() {
		return new RedisCacheManager();
	}

	/**
	 * cookie对象; rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
	 * 
	 * @return
	 */
//	@Bean
//	public SimpleCookie rememberMeCookie() {
//
//		// 这个参数是cookie的名称
//		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//
//		// <!-- 记住我cookie生效时间30天 ,单位秒;-->
//		simpleCookie.setMaxAge(259200);
//
//		return simpleCookie;
//	}

	/**
	 * cookie管理对象;
	 * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
	 * 
	 * @return
	 */
//	@Bean
//	public CookieRememberMeManager rememberMeManager() {
//
//		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//		cookieRememberMeManager.setCookie(rememberMeCookie());
//		// rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//		cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
//		return cookieRememberMeManager;
//	}

	/**
	 * Shiro生命周期处理器
	 */
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 
	 * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),<br>
	 * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
	 *
	 * @return DefaultAdvisorAutoProxyCreator
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}
}
