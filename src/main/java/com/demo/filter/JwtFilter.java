
package com.demo.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.demo.common.Const;
import com.demo.common.ConstCode;
import com.demo.common.RedisConst;
import com.demo.common.ResponseCode;
import com.demo.common.ServerResponse;
import com.demo.shiro.JwtToken;
import com.demo.util.JsonConverUtil;
import com.demo.util.JwtUtil;
import com.demo.util.RedisUtil;
import com.demo.util.SysTimeUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 核心JWT拦截器，拦截Shiro过滤链anon之外的请求
*/
/**
 * 这个类最主要的目的是:当请求需要校验权限，token是否具有权限时，构造出主体subject执行login()
 */
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	JsonConverUtil jsonConverUtil;

	/**
	 * 执行登录认证
	 * 
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return 是否成功
	 */
	@Override
	// 这个方法判断 尝试进行登录的操作,如果token存在,那么进行提交登录,如果不存在说明可能是正在进行登录或者做其它的事情 直接放过即可
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		try {

			executeLogin(request, response);

			return true;
		} catch (Exception e) {
			// return false;
			// throw new AuthenticationException("Token失效请重新登录");
			// 认证出现异常，传递错误信息msg
			String msg = e.getMessage();
			// 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
			Throwable throwable = e.getCause();

			if (throwable != null && throwable instanceof SignatureVerificationException) {
				// 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
				msg = "token或者密钥不正确(" + throwable.getMessage() + ")";

			} else if (throwable != null && throwable instanceof TokenExpiredException) {

				// 该异常为JWT的AccessToken已过期(TokenExpiredException)，
				// 判断RefreshToken未过期就进行AccessToken刷新
				if (this.refreshToken(request, response)) {
					return true;
				} else {
					msg = "token已过期(" + throwable.getMessage() + ")";
				}
			} else {
				// 应用异常不为空
				if (throwable != null) {
					// 获取应用异常msg
					msg = throwable.getMessage();
				}
			}
			/**
			 * 错误两种处理方式
			 * 1. 将非法请求转发到/401的Controller处理，抛出自定义无权访问异常被全局捕捉再返回Response信息
			 * 2. 无需转发，直接返回Response信息 一般使用第二种(更方便)
			 */
			// 直接返回Response信息
			this.response401(request, response, msg);
			return false;
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		this.sendChallenge(request, response);
		return false;
	}

	/**
	 * 执行登录
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response)
			throws  AuthenticationException {

		// log.info("进入JwtFilter类中...");

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		// head部存放的 authorization token
		String token = httpServletRequest.getHeader(Const.TOKEN_HEADER_NAME);

		// 判断token是否存在
		/*
		 * if (token == null) { return false; }
		 */
		if (!StringUtils.isNotBlank(token)) {

			log.error("获取到的token为空");

			throw new AuthenticationException();
		}

		// log.info("获取到的token是:{}", token);
		// JwtToken token = new JwtToken(this.getAuthzHeader(request))
		JwtToken jwtToken = new JwtToken(token);

		try {
			log.debug("提交UserModularRealmAuthenticator查找对应的realm...");

			getSubject(request, response).login(jwtToken);

		} catch (AuthenticationException e) {
			log.debug("捕获到身份认证异常{}", e.getMessage());
			throw e;
		}
		return true;
	}

	/**
	 * 刷新AccessToken，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
	 */
	private boolean refreshToken(ServletRequest request, ServletResponse response) {
		// Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		String token = this.getAuthzHeader(request);

		// Token的帐号信息获取
		String userid = JwtUtil.getClaim(token, Const.TOKEN_CLAIM_USERID);

		// Redis中RefreshToken的key
		String refreshTokenKey = RedisConst.PREFIX_SHIRO_REFRESH_TOKEN + userid;

		// Redis中RefreshToken是否存在
		if (redisUtil.hasKey(refreshTokenKey)) {

			// Redis中RefreshToken还存在，获取RefreshToken的时间戳
			String currentTimeMillisRedis = redisUtil.get(refreshTokenKey).toString();
			// 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
			if (JwtUtil.getClaim(token, Const.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
				// 获取当前最新时间戳
				String currentTimeMillis = String.valueOf(SysTimeUtil.getTime());

				/**
				 * ★★★
				 * 这里重新设置RefreshToken为一天，可能会导致RefreshToken一直续期，
				 * 可以一开始设置RefreshToken大一些
				 * 比如为7天，续期时不在重置，这样一次登录的Token Refresh有效期就是一周，一周后必须重新登录。
				 * ★★★
				 * */
				// 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间
				redisUtil.set(refreshTokenKey, currentTimeMillis, Const.REFRESH_TOKEN_EXPIRE_TIME);

				// 账号类型
				String accountType = JwtUtil.getClaim(token, Const.ACCOUNT_TPYE);

				String sessionId = JwtUtil.getClaim(token,Const.JSESSIONID);
				// 刷新AccessToken，设置时间戳为当前最新时间戳
				token = JwtUtil.loginSign(userid, accountType, currentTimeMillis, sessionId,
						Const.TOKEN_SECRET);

				// 将新刷新的AccessToken再次进行Shiro的登录
				JwtToken jwtToken = new JwtToken(token);

				// 提交给DemoRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
				this.getSubject(request, response).login(jwtToken);

				// 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;

				httpServletResponse.setHeader(Const.TOKEN_HEADER_NAME, token);

				httpServletResponse.setHeader(Const.TOKEN_ACCESS_CONTROL, Const.TOKEN_HEADER_NAME);

				return true;
			}
		}
		return false;
	}

	/**
	 * 无需转发，直接返回Response信息
	 */
	private void response401(ServletRequest req, ServletResponse response, String msg) {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();

			// object 转 json字符串
			String data = JSON.toJSONString(ServerResponse.createByErrorCodeMessage(ResponseCode.UNAUTHORIZED.getCode(),
					"UNAUTHORIZED"));

			out.append(data);

		} catch (IOException e) {

			log.error("response401 : {}",e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		// httpServletResponse.setHeader("Access-control-Allow-Origin",
		// httpServletRequest.getHeader("Origin"));

		if (StringUtils.isNotEmpty(httpServletRequest.getHeader("Origin"))) {
			httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		} else {
			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		}

		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE,HEAD");
		// httpServletResponse.setHeader("Access-Control-Allow-Headers",
		// httpServletRequest.getHeader("Access-Control-Request-Headers"));
		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				"Content-Type,Origin,Accept,Authorization,X-Requested-With,No-Cache,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,X-E4M-With");

		// 跨域时会首先发送一个option请求，给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}
}