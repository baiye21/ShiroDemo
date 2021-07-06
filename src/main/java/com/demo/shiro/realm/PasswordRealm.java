package com.demo.shiro.realm;

import com.demo.entity.UserMaster;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.demo.service.IUserService;
import com.demo.shiro.CustomizedToken;
import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:  用户密码认证Realm
*/
@Slf4j
public class PasswordRealm extends AuthorizingRealm {

	@Autowired
	@Lazy
	private IUserService iUserService;

	/**
	 * shiro 赋权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		log.debug("PasswordRealm 不做赋权处理");

		// password login 不做赋权处理
		return null;
	}

	/**
	 * shiro 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {

		log.debug("PasswordRealm权限认证开始,传递的token:{}", authenticationToken);

		CustomizedToken token = (CustomizedToken) authenticationToken;

		log.debug("PasswordRealm转换的自定义token:{}", token);

		// 找出数据库中的对象 和用户输入的对象做出对比
		// 根据userid查询用户
		UserMaster userMaster = iUserService.getUserByUserId(token.getUsername());

		if (userMaster == null) {
			log.debug("该账号不存在:{}", token.getUsername());
			// 抛出账号不存在异常
			throw new UnknownAccountException();
		}

		// 用户密码
		Object hashedCredentials = userMaster.getPassWord();

		/***
		 * param1 : 数据库用户<br>
		 * param2 : 密码<br>
		 * param3 : 加密所用盐值 <br>
		 * param4 : 当前realm的名称<br>
		 */
		return new SimpleAuthenticationInfo(userMaster, hashedCredentials,
				ByteSource.Util.bytes(userMaster.getUserId()), getName());

	}
}