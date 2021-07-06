package com.demo.aop;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30 
   Function: 日志AOP
*/
@Component
@Aspect
@Slf4j
public class WebLogAspect {

	@Pointcut("execution(public * com.demo.controller..*.*(..))")
	public void webLog() {
	}

	/**
	 * 记录请求参数
	 *
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		log.info("URL : {}", request.getRequestURL().toString());
		log.info("HTTP_METHOD : {}", request.getMethod());
		log.info("IP : {}", request.getRemoteAddr());

		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {

			String name = enu.nextElement();

			log.info("name:{},value:{}", name, request.getParameter(name));
		}
	}

	/**
	 * 记录返回结果
	 *
	 * @param ret
	 * @throws Throwable
	 */
	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) throws Throwable {

		log.info("RESPONSE :{} ", ret.toString());
	}
}