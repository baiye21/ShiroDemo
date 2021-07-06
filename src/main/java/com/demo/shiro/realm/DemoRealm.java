package com.demo.shiro.realm;

import com.demo.entity.UserMaster;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.demo.common.Const;
import com.demo.common.RedisConst;
import com.demo.pojo.common.UserAccessInfo;
import com.demo.service.IUserService;
import com.demo.shiro.JwtToken;
import com.demo.util.JwtUtil;
import com.demo.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
/**
 * 用户登录鉴权和获取用户授权
 */
@Slf4j
public class DemoRealm extends AuthorizingRealm {

	@Autowired
	@Lazy
	private IUserService iUserService;

	@Autowired
	@Lazy
	private RedisUtil redisUtil;

	/**
	 * 必须重写此方法，不然Shiro会报错
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	/**
	 * 功能： 获取用户权限信息，包括角色以及权限。只有当触发检测用户权限时才会调用此方法，例如checkRole,checkPermission
	 *
	 * @param principals token
	 * @return AuthorizationInfo 权限信息
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		log.info("demoRealm doGetAuthorizationInfo 用户赋权 ");

		String userid = null;

		if (principals != null) {

			// 此处的principals为 UserMaster
			Object PrimaryPrincipal = principals.getPrimaryPrincipal();

			if (PrimaryPrincipal instanceof UserMaster) {

				UserMaster userMaster = (UserMaster) PrimaryPrincipal;

				userid = userMaster.getUserId();

			} else {

				// 此处的principals为token
				userid = JwtUtil.getClaim(principals.toString(), Const.TOKEN_CLAIM_USERID);
			}
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		UserAccessInfo userAccessInfo = iUserService.getUserAccessInfo(userid);

		/**
		 * 设置用户拥有的角色集合，<br>
		 * 
		 * accountType = 1 管理员 admin <br>
		 * accountType = 2 领导  Leader <br>
		 * accountType = 3 普通用户 user <br>
		 * 
		 */
		info.setRoles(userAccessInfo.getRoleSet());

		// TODO 设置用户拥有的权限集合，比如“sys:role:add,sys:user:add”
		info.addStringPermissions(userAccessInfo.getPermissionSet());

		return info;
	}

	/**
	 * 功能： 用来进行身份认证，也就是说验证用户输入的账号和密码是否正确，获取身份验证信息，错误抛出异常
	 *
	 * @param auth 用户身份信息 token
	 * @return 返回封装了用户信息的 AuthenticationInfo 实例
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {

		String token = (String) auth.getCredentials();

		if (StringUtils.isBlank(token)) {

			log.error("token为空，身份认证失败！");

			throw new AuthenticationException("token为空!");
		}

		// 获取token携带的用户名
		String userid = JwtUtil.getClaim(token, Const.TOKEN_CLAIM_USERID);

		// 帐号为空
		if (StringUtils.isBlank(userid)) {

			throw new AuthenticationException("token中用户为空(The userid in Token is empty.)");
		}

		// UserMaster表查询用户
		UserMaster userMaster = iUserService.getUserByUserId(userid);

		if (userMaster == null) {

			throw new AuthenticationException("该帐号不存在(The account does not exist.)");
		}

		String refreshTokenKey = RedisConst.PREFIX_SHIRO_REFRESH_TOKEN + userid;

		// 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
		if (JwtUtil.verify(token, Const.TOKEN_SECRET) && redisUtil.hasKey(refreshTokenKey)) {

			// 获取RefreshToken的时间戳
			String currentTimeMillisRedis = redisUtil.get(refreshTokenKey).toString();

			// 获取AccessToken时间戳，与RefreshToken的时间戳对比
			if (JwtUtil.getClaim(token, Const.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {

				// principal，集成redis时，这里必须为对象，确保key唯一，且pojo实现序列化
				// credentials 这里使用token
				return new SimpleAuthenticationInfo(token, token, "demoRealm");

			}
		}
		throw new AuthenticationException("token expired or incorrect.");

		// 校验token有效性
		// UserMaster loginUser = this.checkUserTokenIsEffect(token);

		// principal，集成redis时，这里必须为对象，确保key唯一，且pojo实现序列化
		// credentials 这里使用token
		// return new SimpleAuthenticationInfo(token, token, getName());
	}

	/**
	 * 校验token的有效性
	 *
	 * @param token
	 */
	/*
	 * public UserMaster checkUserTokenIsEffect(String token) throws
	 * AuthenticationException { // L解密获得username，用于和数据库进行对比 String username =
	 * JwtUtil.getUserName(token); if (username == null) { throw new
	 * AuthenticationException("token非法无效!"); }
	 * 
	 * // L查询用户信息 UserMaster loginUser = new UserMaster(); UserMaster sysUser =
	 * iUserService.getUserByUserName(username); if (sysUser == null) { throw new
	 * AuthenticationException("用户不存在!"); }
	 * 
	 * // L校验token是否超时失效 & 或者账号密码是否错误 if (!jwtTokenRefresh(token, username,
	 * sysUser.getPassWord())) { throw new AuthenticationException("Token失效请重新登录!");
	 * }
	 * 
	 * // L判断用户状态 if (!"0".equals(sysUser.getDelFlg())) { throw new
	 * AuthenticationException("账号已被删除,请联系管理员!"); }
	 * 
	 * // L对象属性复制 BeanCopier copier = BeanCopier.create(UserMaster.class,
	 * UserMaster.class, false); copier.copy(sysUser, loginUser, null); //
	 * BeanUtils.copyProperties(sysUser, loginUser);
	 * 
	 * return loginUser; }
	 */

	/**
	 * JWTToken刷新生命周期 （解决用户一直在线操作，提供Token失效问题）
	 * 1、登录成功后将用户的JWT生成的Token作为k、v存储到cache缓存里面(这时候k、v值一样)
	 * 2、当该用户再次请求时，通过JWTFilter层层校验之后会进入到doGetAuthenticationInfo进行身份验证
	 * 3、当该用户这次请求JWTToken值还在生命周期内，则会通过重新PUT的方式k、v都为Token值，缓存中的token值生命周期时间重新计算(这时候k、v值一样)
	 * 4、当该用户这次请求jwt生成的token值已经超时，但该token对应cache中的k还是存在，则表示该用户一直在操作只是JWT的token失效了，程序会给token对应的k映射的v值重新生成JWTToken并覆盖v值，该缓存生命周期重新计算
	 * 5、当该用户这次请求jwt在生成的token值已经超时，并在cache中不存在对应的k，则表示该用户账户空闲超时，返回用户信息已失效，请重新登录。
	 * 6、每次当返回为true情况下，都会给Response的Header中设置Authorization，该Authorization映射的v为cache对应的v值。
	 * 7、注：当前端接收到Response的Header中的Authorization值会存储起来，作为以后请求token使用
	 * 参考方案：https://blog.csdn.net/qq394829044/article/details/82763936
	 *
	 * @param userName
	 * @param passWord
	 * @return
	 */
	/*
	 * public boolean jwtTokenRefresh(String token, String userName, String
	 * passWord) {
	 * 
	 * String cacheToken = String.valueOf(redisUtil.get(Const.PREFIX_USER_TOKEN +
	 * token));
	 * 
	 * if (StringUtils.isNotEmpty(cacheToken)) { // L校验token有效性 if
	 * (!JwtUtil.verify(cacheToken, userName, passWord)) {
	 * 
	 * String newAuthorization = JwtUtil.sign(userName, passWord);
	 * redisUtil.set(Const.PREFIX_USER_TOKEN + token, newAuthorization); // L设置超时时间
	 * redisUtil.expire(Const.PREFIX_USER_TOKEN + token, Const.TOKEN_EXPIRE_TIME /
	 * 1000);
	 * 
	 * } else { redisUtil.set(Const.PREFIX_USER_TOKEN + token, cacheToken); //
	 * L设置超时时间 redisUtil.expire(Const.PREFIX_USER_TOKEN + token,
	 * Const.TOKEN_EXPIRE_TIME / 1000); } return true; } return false; }
	 */

}
