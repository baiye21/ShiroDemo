package com.demo.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.common.ServerResponse;
import com.demo.pojo.login.LoginDTO;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public interface ILoginService {

	/**
	 * 用户login
	 * 
	 * @param LoginDTO
	 * @param request
	 * @param response
	 * @return
	 */
	ServerResponse<String> login(LoginDTO LoginDTO, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 用户logout
	 * 
	 * @param request
	 * @return
	 */
	ServerResponse<String> logout(HttpServletRequest request);
}
