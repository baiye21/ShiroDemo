package com.demo.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public class JwtToken implements AuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6196693722323489019L;

	private String token;

	public JwtToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}
}