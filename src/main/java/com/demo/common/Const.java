package com.demo.common;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public class Const {

	/** 1:管理员 */
	public static final String ADMIN_USER_CODE = "1";
	/** 2:领导 */
	public static final String LEADWER_USER_CODE = "2";
	/** 3:普通 */
	public static final String NORMAL_USER_CODE = "3";

	/** 1:管理员 */
	public static final String ADMIN_USER = "admin";
	/** 2:leader */
	public static final String LEADER_USER = "leader";
	/** 3:普通 */
	public static final String NORMAL_USER = "user";

	/** 权限Level 001~005 */
	public static final String LEVEL_001_CODE = "001";
	public static final String LEVEL_002_CODE = "002";
	public static final String LEVEL_003_CODE = "003";
	public static final String LEVEL_004_CODE = "004";
	public static final String LEVEL_005_CODE = "005";
	public static final String LEVEL_001 = "LEVEL_001";
	public static final String LEVEL_002 = "LEVEL_002";
	public static final String LEVEL_003 = "LEVEL_003";
	public static final String LEVEL_004 = "LEVEL_004";
	public static final String LEVEL_005 = "LEVEL_005";

	/** 999999:系统 */
	public static final String SYS_USER = "sys_user";
	public static final String SYS_USER_CODE = "999999";

	/** 登录认证token签名的字符串 */
	public static final String TOKEN_SECRET = "demo";

	/** login之后的access Token 载荷中存放的信息 */

	/** userid */
	public static final String TOKEN_CLAIM_USERID = "userid";
	/** login 时间戳 */
	public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";
	/** shiro token id */
	public static final String JSESSIONID = "sessionID";
	/** 账号类型 */
	public static final String ACCOUNT_TPYE = "accountType";

	/**
	 * login返回时 Reaponse
	 * token请求头名称
	 */
	public static final String TOKEN_HEADER_NAME = "Authorization";

	public static final String TOKEN_ACCESS_CONTROL = "Access-Control-Expose-Headers";

	/**
	 * 认证access token过期时间 3 小时
	 */
	public static final Long ACCESS_TOKEN_EXPIRE_TIME = 3 * 60 * 60 * 1000L;

	/**
	 * redis 刷新refresh token过期时间 1天
	 */
	public static final Long REFRESH_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;

}
