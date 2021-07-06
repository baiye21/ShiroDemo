package com.demo.service;

import com.demo.common.ServerResponse;
import com.demo.entity.UserMaster;
import com.demo.pojo.common.UserAccessInfo;
import com.demo.pojo.user.UserDTO;


/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public interface IUserService {

	/**
	 * 普通用户注册
	 *
	 * @param userDto
	 * @return
	 */
	ServerResponse<String> userRegister(UserDTO userDto);

	/**
	 * 获取用户
	 * 
	 * @param userId
	 * @return
	 */
	UserMaster getUserByUserId(String userId);

	/**
	 * 获取用户类别set
	 *
	 * @param userId
	 * @return
	 */
	UserAccessInfo getUserAccessInfo(String userId);
}
