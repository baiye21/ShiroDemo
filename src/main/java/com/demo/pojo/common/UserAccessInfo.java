package com.demo.pojo.common;

import java.util.Set;

import lombok.Data;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 　shiro赋权时,用户角色与权限
*/
@Data
public class UserAccessInfo {

	// 角色set
	private Set<String> roleSet;

	// 权限set
	private Set<String> permissionSet;

}
