package com.demo.shiro;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import com.demo.common.Const;
import com.demo.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:   shiro自定义session管理器
*/
@Slf4j
public class ShiroSessionManager extends DefaultWebSessionManager {

	public ShiroSessionManager() {
		super();
	}

	private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

		// 如果请求头中有 Authorization
		String token = WebUtils.toHttp(request).getHeader(Const.TOKEN_HEADER_NAME);

		if (!StringUtils.isEmpty(token)) {

			if (JwtUtil.verify(token, Const.TOKEN_SECRET)) {

				String id = JwtUtil.getClaim(token, Const.JSESSIONID);

				log.debug("ShiroSessionManager从http header 取出token中的JSESSIONID:{}", id);

				request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
						REFERENCED_SESSION_ID_SOURCE);
				request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
				request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);

				return id;
			}

			return super.getSessionId(request, response);
		} else {
			// 否则按默认规则从cookie取sessionId
			return super.getSessionId(request, response);
		}
	}

}
