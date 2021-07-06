package com.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Data;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

	/**
	 * endPoint是一个URL，域名，IPv4或者IPv6地址
	 */
	private String endpoint;

	/**
	 * TCP/IP端口号
	 */
	private int port;

	/**
	 * accessKey类似于用户ID，账户唯一标识
	 */
	private String accessKey;

	/**
	 * secretKey是账户的密码
	 */
	private String secretKey;

	/**
	 * 如果是true，则用的是https而不是http,默认值是true
	 */
	private Boolean secure;

	/**
	 * 默认存储桶名称
	 */
	private String bucketDefaultName;

	@Bean("minioClient")
	public MinioClient getMinioClient() throws InvalidEndpointException, InvalidPortException {

		MinioClient minioClient = new MinioClient(endpoint, port, accessKey, secretKey, secure);

		return minioClient;
	}

}
