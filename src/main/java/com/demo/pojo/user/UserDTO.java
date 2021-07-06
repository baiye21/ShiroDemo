package com.demo.pojo.user;

import lombok.Data;

import java.io.Serializable;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function:
*/
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -4371193837464516163L;

    private String userId;

    private String userName;

    private String passWord;

    private String accountType;

    private String permissionType;
}
