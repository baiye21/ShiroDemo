package com.demo.common;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:
*/
public class RedisConst {

	/**
	 * redis过期时间，以秒为单位，一分钟
	 */
	public static final int EXRP_MINUTE = 60;

	/**
	 * redis过期时间，以秒为单位，一小时
	 */
	public static final int EXRP_HOUR = 60 * 60;

	/**
	 * redis过期时间，以秒为单位，一天
	 */
	public static final int EXRP_DAY = 60 * 60 * 24;

	/**
	 * shiro 自定义redis cache存储时间
	 */
	public static final int SHIRO_CACHE_EXRP_TIME = 60 * 60;

	/**
	 * redis-key-前缀-shiro:cache:
	 */
	public static final String PREFIX_SHIRO_CACHE = "shiro:cache:";

	/**
	 * redis-key-前缀-shiro:access_token:
	 */
	public static final String PREFIX_SHIRO_ACCESS_TOKEN = "shiro:access_token:";

	/**
	 * redis-key-前缀-shiro:refresh_token:
	 */
	public static final String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

}
