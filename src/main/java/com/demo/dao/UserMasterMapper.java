package com.demo.dao;

import com.demo.entity.UserMaster;
import com.demo.pojo.common.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMasterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserMaster record);

    int insertSelective(UserMaster record);

    UserMaster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserMaster record);

    int updateByPrimaryKey(UserMaster record);

    /*---------updateByPrimaryKey 截至为自动生成------------------*/

    // 根据用户名查找用户
    UserMaster selectByUserId(String userId);

    // 获取用户信息
    UserInfo getUserInfo(String userId);
}