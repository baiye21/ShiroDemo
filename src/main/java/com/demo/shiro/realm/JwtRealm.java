package com.demo.shiro.realm;

import com.demo.entity.UserMaster;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import com.demo.common.Const;
import com.demo.common.ResponseCode;
import com.demo.service.IUserService;
import com.demo.shiro.JwtToken;
import com.demo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
@Slf4j
public class JwtRealm extends AuthorizingRealm {

	@Autowired
	@Lazy
	private IUserService iUserService;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

		log.info("JwtRealm 不做赋权处理");

		// 不做赋权处理
		return null;

		/**
		 * String token = principalCollection.toString();
		 * 
		 * String userName = JwtUtil.getUserName(token);
		 * 
		 * log.info("JwtRealm身份认证开始，获取到的token是:{}", token);
		 * 
		 * UserMaster userMaster = iUserService.getUserByUserName(userName);
		 * 
		 * SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		 * 
		 * if (userMaster != null) { String permission = userMaster.getPermissionType();
		 * 
		 * UserAccessInfo userAccessInfo =
		 * iUserService.getUserAccessInfo(userMaster.getUserName());
		 * 
		 * 
		 * 设置用户拥有的角色集合，<br>
		 * 
		 * accountType = 1 管理员 admin <br>
		 * accountType = 2 经纪人 agent <br>
		 * accountType = 3 普通用户user <br>
		 * 
		 * 
		 * info.setRoles(userAccessInfo.getRoleSet());
		 * 
		 * // TODO 设置用户拥有的权限集合，比如“sys:role:add,sys:user:add” Set<String> permissionSet =
		 * new HashSet<String>(); permissionSet.add(permission); //
		 * sysUserService.getUserPermissionsSet(username);
		 * 
		 * info.addStringPermissions(permissionSet); } return info;
		 */

		// 这里的空指针异常不需要处理 无论此处抛出什么异常 shiro均认为身份有问题
		// LinkedHashMap<String, Object> stringObjectLinkedHashMap =
		// userMapper.selectUserPermissionById(userId);
		// 获取所有的角色
		// String roleName = String.valueOf(stringObjectLinkedHashMap.get("roleName"));
		// 获取所有的权限
		// List<String> permissionsNameList = Splitter.on(",")
		// .splitToList(String.valueOf(stringObjectLinkedHashMap.get("permissionsNameList")));
		// SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		// 添加角色
		// authorizationInfo.addRole(roleName);
		// 添加权限
		// authorizationInfo.addStringPermissions(permissionsNameList);
		// return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		
		// log.info("jwtRealm中关于身份验证的方法执行...传递的token是:{}", authenticationToken);
		String token = (String) authenticationToken.getCredentials();
		
		// 解密获得userid，用于和数据库进行对比
		String userid = JwtUtil.getUserId(token);
		
		if (userid == null) {
			throw new AuthenticationException(ResponseCode.TOKEN_EXCEPTION.getDesc());
		}
		
		UserMaster user = iUserService.getUserByUserId(userid);
		// L校验用户是否存在
		if (user == null) {
			throw new AuthenticationException(ResponseCode.ACCOUNT_UNUSUAL.getDesc());
		}
		// L操作时校验的是非对称加密是否成立.
		// if (!JwtUtil.verify(token, Const.TOKEN_SECRET)) {
		if (!JwtUtil.verify(token, Const.TOKEN_SECRET)) {
			// log.info("token校验无效...");
			throw new AuthenticationException(ResponseCode.TOKEN_EXCEPTION.getDesc());
		}

		try {
			// log.info("进行身份验证时,用户提供的token有效");
			return new SimpleAuthenticationInfo(user, token, getName());

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			throw new AuthenticationException(ResponseCode.ACCOUNT_UNUSUAL.getDesc());
		}

	}
}
