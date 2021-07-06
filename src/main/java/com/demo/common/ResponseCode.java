package com.demo.common;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 返回状态码
*/
public enum ResponseCode {

	// 成功
	SUCCESS(200, "成功"),
	// 错误请求
	ERROR(400, "错误请求"),
	// 未授权
	UNAUTHORIZED(401, "未授权"),
	// 禁止请求
	FORBIDDEN(403, "禁止请求"),
	// 未找到资源
	NOT_FOUND(404, "未找到资源"),

	ACCOUNT_UNUSUAL(412, "账户异常，请联系管理员！"),

	TOKEN_EXPIRE(413, "用户身份过期,请重新登录!"),

	TOKEN_EXCEPTION(414, "token无效或过期"),

	// 服务器内部错误
	SERVER_ERROR(500, "服务器内部错误"),

	// 单项目校验错误
	CHECK_ERROR(601, "单项目校验错误"),
	// 业务逻辑错误
	BIZ_ERROR(602, "业务逻辑错误"),
	// 关联项目校验失败
	BIZ_ITEM_ERROR(603, "关联项目校验失败"),

	// 需要登录
	NEED_LOGIN(10, "NEED_LOGIN"),
	// 非法参数
	ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

	private final int code;
	private final String desc;

	ResponseCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

}
