package com.demo.handler;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.common.ResponseCode;
import com.demo.common.ServerResponse;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 全局异常处理
*/
@ControllerAdvice
@Slf4j
public class GloabllExceptionHandler {

	/**
	 * 运行时异常
	 * 
	 * @param
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ServerResponse<String> ExceptionHandler(RuntimeException e) {

		log.error("RuntimeException: {}", e.getMessage());

		// 500 服务器发生错误！;
		return ServerResponse.createByErrorAllInfo(ResponseCode.SERVER_ERROR.getCode(),
				ResponseCode.SERVER_ERROR.getDesc(), e.getMessage());
	}

	/**
	 * shiro 认证异常
	 * 
	 * @param
	 * @return
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public ServerResponse<String> AuthenticationExceptionHandler(AuthenticationException e) {

		log.error("AuthenticationException: {}", e.getMessage());

		// status:401 msg :认证失败
		return ServerResponse.createByErrorCodeMessage(ResponseCode.UNAUTHORIZED.getCode(),
				"AuthenticationException");
	}

	/**
	 * shiro 赋权异常
	 * 
	 * @param
	 * @return
	 */
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseBody
	public ServerResponse<String> UnauthorizedExceptionHandler(UnauthorizedException e) {

		// TODO 该用户没有访问权限
		return ServerResponse.createByErrorCodeMessage(ResponseCode.UNAUTHORIZED.getCode(),
				"UnauthorizedException");
	}
}
