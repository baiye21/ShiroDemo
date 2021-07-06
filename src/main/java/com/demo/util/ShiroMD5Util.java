package com.demo.util;

import org.apache.shiro.crypto.hash.SimpleHash;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public class ShiroMD5Util {

	/**
	 * 使用MD5加密
	 * 
	 * @param password 需要加密的密码
	 * @param salt     加密的盐值 => 用户名
	 * @return 返回加密后的密码
	 */
	public static String encryptPassword(String password, String salt) {

		// MD5 哈希算法
		// salt 作为盐值
		// 666 hash迭代次数
		return String.valueOf(new SimpleHash("MD5", password, salt, 666));
	}

}
