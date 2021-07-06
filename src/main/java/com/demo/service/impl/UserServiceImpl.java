package com.demo.service.impl;

import com.demo.common.Const;
import com.demo.common.ConstCode;
import com.demo.common.ServerResponse;
import com.demo.dao.UserMasterMapper;
import com.demo.entity.UserMaster;
import com.demo.pojo.common.UserAccessInfo;
import com.demo.pojo.user.UserDTO;
import com.demo.service.IUserService;
import com.demo.util.ShiroMD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMasterMapper userMasterMapper;

	/**
	 * 用户注册
	 * @param userDto
	 * @return
	 */
	@Transactional
	public ServerResponse<String> userRegister(UserDTO userDto) {

		// 用户名
		String userId = userDto.getUserId();
		// 用户密码
		String passWord = userDto.getPassWord();

		// MD5密码加密 salt为userId
		String md5PassWord = ShiroMD5Util.encryptPassword(passWord, userId);

		// 用户主表entity
		UserMaster userMaster = new UserMaster();

		// 属性copy
		BeanCopier copier = BeanCopier.create(UserDTO.class, UserMaster.class, false);
		copier.copy(userDto, userMaster, null);

		// 加密后的密码
		userMaster.setPassWord(md5PassWord);

		// 删除FLG : 0
		userMaster.setDelFlg(ConstCode.STRING_ZERO);

		Date date = new Date();
		userMaster.setRegAccount(userId);
		userMaster.setRegTime(date);
		userMaster.setUpdAccount(userId);
		userMaster.setUpdTime(date);

		userMasterMapper.insert(userMaster);

		// 用户注册成功
		return ServerResponse.createBySuccessMessage("用户注册成功");
	}

	/**
	 * 通过用户名 获取用户 delflg != '1'
	 *
	 * @param userId
	 * @return
	 */
	public UserMaster getUserByUserId(String userId) {

		return userMasterMapper.selectByUserId(userId);
	}

	/**
	 * 获取用户类别set
	 *
	 * @param userId
	 * @return
	 */
	public UserAccessInfo getUserAccessInfo(String userId) {

		UserAccessInfo userAccessInfo = new UserAccessInfo();

		// 用户角色set
		Set<String> roleSet = new HashSet<String>();

		// 用户权限set
		Set<String> permissionSet = new HashSet<String>();

		UserMaster userMaster = userMasterMapper.selectByUserId(userId);

		// 账号类型
		String accountType = userMaster.getAccountType();

		// 管理员 accountType = 1
		if (Const.ADMIN_USER_CODE.equals(accountType)) {

			// admin
			roleSet.add(Const.ADMIN_USER);

			// Leader accountType = 2
		} else if (Const.LEADWER_USER_CODE.equals(accountType)) {

			// Leader
			roleSet.add(Const.LEADER_USER);

			// 普通用户 accountType = 3
		} else if (Const.NORMAL_USER_CODE.equals(accountType)) {

			// user
			roleSet.add(Const.NORMAL_USER);
		}

		// 用户权限类型
		String permissionType = userMaster.getPermissionType();

		if (Const.LEVEL_001_CODE.equals(permissionType)) {
			permissionSet.add(Const.LEVEL_001);

		} else if (Const.LEVEL_002_CODE.equals(permissionType)) {
			permissionSet.add(Const.LEVEL_002);

		} else if (Const.LEVEL_003_CODE.equals(permissionType)) {
			permissionSet.add(Const.LEVEL_003);

		} else if (Const.LEVEL_004_CODE.equals(permissionType)) {
			permissionSet.add(Const.LEVEL_004);

		} else if (Const.LEVEL_005_CODE.equals(permissionType)) {
			permissionSet.add(Const.LEVEL_005);
		}

		userAccessInfo.setRoleSet(roleSet);

		userAccessInfo.setPermissionSet(permissionSet);

		return userAccessInfo;
	}
}
