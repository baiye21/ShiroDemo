package com.demo.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 自定义shiro UsernamePasswordToken，追加loginType
*/
public class CustomizedToken extends UsernamePasswordToken {

    /**
     *
     */
    private static final long serialVersionUID = 344643816797746392L;

    /**
     * 登录类型
     * 判断是密码登录还是token
     */
    public String loginType;

    public CustomizedToken(final String username, final String password, final String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "loginType=" + loginType + ",username=" + super.getUsername() + ",password="
                + String.valueOf(super.getPassword());
    }

}
