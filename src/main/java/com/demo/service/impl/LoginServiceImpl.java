package com.demo.service.impl;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.dao.UserMasterMapper;
import com.demo.entity.UserMaster;
import com.demo.pojo.login.LoginDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.common.Const;
import com.demo.common.LoginEnum;
import com.demo.common.RedisConst;
import com.demo.common.ServerResponse;
import com.demo.pojo.common.UserInfo;
import com.demo.pojo.login.SimpleUserInfo;
import com.demo.service.ILoginService;
import com.demo.shiro.CustomizedToken;
import com.demo.util.JwtUtil;
import com.demo.util.RedisUtil;
import com.demo.util.SysTimeUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 用戶login logout
*/
@Service
@Slf4j
public class LoginServiceImpl implements ILoginService {

	@Autowired
	UserMasterMapper userMasterMapper;

	@Autowired
	RedisUtil redisUtil;

	/**
	 * 用户login
	 *
	 * @param loginDTO
	 * @param request
	 * @param response
	 * @return
	 */
	@Override
	public ServerResponse login(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {

		// 用户ID
		String userId = loginDTO.getUserId();
		// 密码
		String passWord = loginDTO.getPassWord();

		// 获取Subject
		Subject subject = SecurityUtils.getSubject();

		// 校验数据库中此user是否存在
		UserMaster userMaster = userMasterMapper.selectByUserId(userId);

		if (userMaster == null) {
			// 该用户未注册 status 602
			return ServerResponse.createByBizError("该用户未注册");
		}

		userId = userMaster.getUserId();

		// 制作CustomizedToken执行登录(shiro UsernamePasswordToken)
		CustomizedToken customizedToken = new CustomizedToken(userId, passWord, LoginEnum.BY_PASSWORD.getLoginType());

		// 记住密码
		//customizedToken.setRememberMe(userLoginDTO.getRememberMe());

		try {

			// shiro 用户认证
			subject.login(customizedToken);

		} catch (Exception e) {

			log.error(e.getMessage());

			// 用户认证失败 密码错误 status 400
			return ServerResponse.createByErrorMessage("用户认证失败,用户名/密码错误。");
		}

        // shiro认证通过后的sessionID
		String sessionId = String.valueOf(subject.getSession().getId());

		log.debug("登录用户 : {}, j_session_id : {}", userId, sessionId);

		// 清除可能存在的shiro权限信息缓存
		if (redisUtil.hasKey(RedisConst.PREFIX_SHIRO_CACHE + userId)) {

			redisUtil.del(RedisConst.PREFIX_SHIRO_CACHE + userId);
		}

		// RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
		String currentTimeMillis = String.valueOf(SysTimeUtil.getTime());

		/**
		 * 将发给用户认证的access token保存到redis中，之后请求时header携带token，
		 * ★★需要重写shiro的sessionManager去redis中获取该token作为sessionid来进行认证★★
		 */
		// redis 存存储refresh Token信息, key shiro:refresh_token:username 当前时间 过期时间一天
		redisUtil.set(RedisConst.PREFIX_SHIRO_REFRESH_TOKEN + userId, currentTimeMillis,
				Const.REFRESH_TOKEN_EXPIRE_TIME);

		UserInfo userInfo = userMasterMapper.getUserInfo(userId);

		String accountType = userInfo.getAccountType();

		// 发给用户认证的access token 之后请求时header携带token，过期时间 3小时
		String token = JwtUtil.loginSign(userId, accountType, currentTimeMillis, sessionId, Const.TOKEN_SECRET);

		// Authorization
		response.setHeader(Const.TOKEN_HEADER_NAME, token);
        //  Access-Control-Expose-Headers : Authorization
		response.setHeader(Const.TOKEN_ACCESS_CONTROL, Const.TOKEN_HEADER_NAME);

		// 返回 SimpleUserInfo token, UserInfo
		return ServerResponse.createBySuccess(new SimpleUserInfo(token, userInfo));
	}

	@Override
	public ServerResponse<String> logout(HttpServletRequest request) {
		try {
			String token = "";
			// 获取头部信息
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				if (Const.TOKEN_HEADER_NAME.equalsIgnoreCase(key)) {
					token = value;
				}
			}
			// 校验token
			if (StringUtils.isBlank(token)) {
				// TODO
				return ServerResponse.createByErrorMessage("认证失败");
			}

			String userId = JwtUtil.getClaim(token, Const.TOKEN_CLAIM_USERID);

			if (StringUtils.isBlank(userId)) {
				return ServerResponse.createByErrorMessage("认证失败");
			}
			// shiro权限信息缓存 清除
			if (redisUtil.hasKey(RedisConst.PREFIX_SHIRO_CACHE + userId)) {
				redisUtil.del(RedisConst.PREFIX_SHIRO_CACHE + userId);
			}
			// RefreshToken清除
			redisUtil.del(RedisConst.PREFIX_SHIRO_REFRESH_TOKEN + userId);

			return ServerResponse.createBySuccess();

		} catch (Exception e) {

			log.error("login out error:{}", e.getMessage());

			return ServerResponse.createByServerError();
		}
	}

}
