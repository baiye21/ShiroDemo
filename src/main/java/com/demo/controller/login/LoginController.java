package com.demo.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.common.ServerResponse;
import com.demo.pojo.login.LoginDTO;
import com.demo.service.ILoginService;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
@RestController
@RequestMapping("/login/")
public class LoginController {

	@Autowired
	ILoginService iLoginService;

	/**
	 * /login/login.do 用户登录
	 * @param userLoginDTO
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> login(@RequestBody LoginDTO userLoginDTO,
										HttpServletRequest request,
			                             HttpServletResponse response) throws Exception {

		return iLoginService.login(userLoginDTO, request, response);
	}

	/**
	 * /login/logout.do 用户退出登录
	 * 
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpServletRequest request) throws Exception {

		return iLoginService.logout(request);
	}

}
