package com.demo.common;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public enum LoginEnum {

	BY_SHIRO("Demo"),

	BY_PASSWORD("Password"),

	BY_CODE("Code");

	public String getLoginType() {
		return loginType;
	}

	private String loginType;

	LoginEnum(String loginType) {
		this.loginType = loginType;
	}
}