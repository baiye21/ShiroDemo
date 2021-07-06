package com.demo.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.ToString;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: Response
*/
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
//※json序列化时忽略null字段
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ServerResponse<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2352160439530912617L;

	// 状态码
	private int status;

	// msg
	private String msg;

	// 返回数据 
	private T data;

	private ServerResponse(int status) {
		this.status = status;
	}

	private ServerResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}

	private ServerResponse(int status, String msg, T data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	private ServerResponse(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	@JsonIgnore
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	public int getStatus() {
		return status;
	}

	public T getData() {
		return data;
	}

	public String getMsg() {
		return msg;
	}

	/**
	 * 请求成功 status 200 无消息
	 * 
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess() {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}

	/**
	 * 请求成功 status 200
	 * 
	 * @param msg 成功消息code
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
	}

	/**
	 * 请求成功 status 200
	 * 
	 * @param data 返回数据
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess(T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
	}

	/**
	 * 请求成功 status 200
	 * 
	 * @param msg 成功消息code
	 * @param data   返回数据
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}

	/**
	 * 请求失败 status 400
	 * 
	 * @return
	 */
	public static <T> ServerResponse<T> createByError() {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
	}

	/**
	 * 请求失败 status 400
	 * 
	 * @param errorMessage 错误消息
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
	}

	/**
	 * 请求失败 status
	 * 
	 * @param status 错误状态status
	 * @param errorMessage    错误消息
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorCodeMessage(int status, String errorMessage) {
		return new ServerResponse<T>(status, errorMessage);
	}

	/**
	 * 请求失败 status 400
	 * 
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorAllInfo(int status, String msgCode, T data) {
		return new ServerResponse<T>(status, msgCode, data);
	}

	/**
	 * 单项目校验失败
	 * 
	 * @param data 校验信息 List<msgInfo>
	 * @return
	 */
	public static <T> ServerResponse<T> createByCheckError(T data) {
		return new ServerResponse<T>(ResponseCode.CHECK_ERROR.getCode(), ResponseCode.CHECK_ERROR.getDesc(), data);
	}

	/**
	 * 业务校验失败
	 * 
	 * @param msgCode 错误消息code
	 * @return
	 */
	public static <T> ServerResponse<T> createByBizError(String msgCode) {
		return new ServerResponse<T>(ResponseCode.BIZ_ERROR.getCode(), msgCode);
	}

	/**
	 * 业务校验失败(带有项目msgList)
	 * 
	 * @param msgCode 错误消息code
	 * @param data       校验信息 List<msgInfo>
	 * @return
	 */
	public static <T> ServerResponse<T> createByBizItemError(String msgCode, T data) {
		return new ServerResponse<T>(ResponseCode.BIZ_ITEM_ERROR.getCode(), ResponseCode.BIZ_ITEM_ERROR.getDesc(),
				data);
	}

	/**
	 * 服务器错误 status 500
	 * 
	 * @return
	 */
	public static <T> ServerResponse<T> createByServerError() {
		return new ServerResponse<T>(ResponseCode.SERVER_ERROR.getCode(), ResponseCode.SERVER_ERROR.getDesc());
	}
}
