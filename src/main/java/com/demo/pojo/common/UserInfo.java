package com.demo.pojo.common;

import lombok.Data;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:  用户信息
*/
@Data
public class UserInfo {

	// id
	private String userId;

	// 用户名
	private String userName;

	// 账号类型
	private String accountType;

	// 权限类型
	private String permissionType;
}
