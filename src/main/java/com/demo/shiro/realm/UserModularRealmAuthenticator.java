package com.demo.shiro.realm;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import com.demo.common.ConstCode;
import com.demo.shiro.CustomizedToken;
import com.demo.shiro.JwtToken;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
@Slf4j
public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {

	// 当subject.login()方法执行,下面的代码即将执行
	@Override
	protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		log.debug("UserModularRealmAuthenticator:method doAuthenticate() 执行 ");
		// 判断getRealms()是否返回为空
		assertRealmsConfigured();

		// 所有Realm
		Collection<Realm> realms = getRealms();

		// 盛放登录类型对应的所有Realm集合
		Collection<Realm> typeRealms = new ArrayList<>();

		if(authenticationToken instanceof  JwtToken) {
			log.debug("验证的Token类型是:{}", "JwtToken");
			typeRealms.clear();
			// 获取header部的token进行强制类型转换
			JwtToken jwtToken = (JwtToken) authenticationToken;

			for (Realm realm : realms) {

				if (realm.getName().contains("Demo")) {
					typeRealms.add(realm);
				}
			}

			return doSingleRealmAuthentication(typeRealms.iterator().next(), jwtToken);
		} else {
			typeRealms.clear();
			// 这个类型转换的警告不需要再关注 如果token错误 后面将会抛出异常信息
			CustomizedToken customizedToken = (CustomizedToken) authenticationToken;
			log.debug("验证的Token类型是:{}", "CustomizedToken");
			// 登录类型
			String loginType = customizedToken.getLoginType();

			log.debug("验证的realm类型是:{}", loginType);

			for (Realm realm : realms) {
				if (realm.getName().contains(loginType)) {
					log.debug("当前realm:{}被注入:", realm.getName());
					typeRealms.add(realm);
				}
			}
			// 判断是单Realm还是多Realm
			if (typeRealms.size() == ConstCode.NUM_1) {
				log.debug("一个realm");
				return doSingleRealmAuthentication(typeRealms.iterator().next(), customizedToken);
			} else {
				log.debug("多个realm");
				return doMultiRealmAuthentication(typeRealms, customizedToken);
			}
		}

		/*// 强制转换回自定义的Token
		try {
			log.debug("进入了UserModularRealmAuthenticator类...得到的authenticationToken是:{}", authenticationToken);

			// 获取header部的token进行强制类型转换
			JwtToken jwtToken = (JwtToken) authenticationToken;

			for (Realm realm : realms) {

				if (realm.getName().contains("Demo")) {
					typeRealms.add(realm);
				}
			}

			return doSingleRealmAuthentication(typeRealms.iterator().next(), jwtToken);

		} catch (ClassCastException e) {
			typeRealms.clear();
			// 这个类型转换的警告不需要再关注 如果token错误 后面将会抛出异常信息
			CustomizedToken customizedToken = (CustomizedToken) authenticationToken;
			// 登录类型
			String loginType = customizedToken.getLoginType();

			log.debug("验证的realm类型是:{}", loginType);

			for (Realm realm : realms) {

				log.debug("正在遍历的realm是:{}", realm.getName());

				if (realm.getName().contains(loginType)) {
					log.debug("当前realm:{}被注入:", realm.getName());
					typeRealms.add(realm);
				}
			}
			// 判断是单Realm还是多Realm
			if (typeRealms.size() == ConstCode.NUM_1) {
				log.debug("一个realm");
				return doSingleRealmAuthentication(typeRealms.iterator().next(), customizedToken);
			} else {
				log.debug("多个realm");
				return doMultiRealmAuthentication(typeRealms, customizedToken);
			}
		}*/
	}
}