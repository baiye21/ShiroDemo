package com.demo.entity;

import java.io.Serializable;
import java.util.Date;

public class UserMaster {

    private Integer id;

    private String userId;

    private String userName;

    private String passWord;

    private String accountType;

    private String permissionType;

    private String delFlg;

    private String regAccount;

    private Date regTime;

    private String updAccount;

    private Date updTime;

    public UserMaster(Integer id, String userId, String userName, String passWord, String accountType, String permissionType, String delFlg, String regAccount, Date regTime, String updAccount, Date updTime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.accountType = accountType;
        this.permissionType = permissionType;
        this.delFlg = delFlg;
        this.regAccount = regAccount;
        this.regTime = regTime;
        this.updAccount = updAccount;
        this.updTime = updTime;
    }

    public UserMaster() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord == null ? null : passWord.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType == null ? null : permissionType.trim();
    }

    public String getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg == null ? null : delFlg.trim();
    }

    public String getRegAccount() {
        return regAccount;
    }

    public void setRegAccount(String regAccount) {
        this.regAccount = regAccount == null ? null : regAccount.trim();
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getUpdAccount() {
        return updAccount;
    }

    public void setUpdAccount(String updAccount) {
        this.updAccount = updAccount == null ? null : updAccount.trim();
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}