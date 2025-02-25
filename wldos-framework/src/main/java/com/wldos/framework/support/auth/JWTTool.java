/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.auth.vo.Token;
import io.jsonwebtoken.Claims;

import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.annotation.Async;

/**
 * 获取token，token验签。
 *
 * @author 元悉宇宙
 * @date 2021-03-23
 * @version V1.0
 */
@SuppressWarnings("unused")
public interface JWTTool {

	/**
	 * 容器内获取accessToken
	 *
	 * @param jwt 封装jwt实体
	 * @return token 新token
	 */
	String genToken(JWT jwt);

	/**
	 * 静态方式验签并解析jwt
	 *
	 * @param token token
	 * @param secretKey secretKey
	 * @param tokenTimeout tokenTimeout
	 * @return 验签结果
	 */
	Claims verifyToken(String token, String secretKey, int tokenTimeout);

	/**
	 * 获取一枚新token
	 *
	 * @param header 令牌的元数据，并且包含签名或加密算法的类型
	 * @param issuer 签发者
	 * @return token 新token
	 */
	String genToken(Map<String, Object> header, JWT jwt, String issuer);

	/**
	 * 获取一枚新token
	 *
	 * @param issuer 签发者
	 * @return token 新token
	 */
	String genToken(JWT jwt, String issuer);

	/**
	 * token验签并返回token解析
	 *
	 * @param token token字面值
	 * @return token解析数据，解析异常返回null
	 */
	Claims verifyToken(String token);

	/**
	 * token转换jwt实体
	 *
	 * @param token token字面值
	 * @param userIp 用户IP
	 * @param reqUri 请求url
	 * @param domain 请求的域名
	 * @param domainId 请求的域id
	 * @return jwt实例
	 */
	JWT popJwt(String token, String userIp, String reqUri, String domain, Long domainId);

	/**
	 * 刷新token。
	 *
	 * @param token token
	 * @param userId 用户id
	 * @param tenantId 租户id
	 * @param expireTime 超期时间
	 * @return 刷新token
	 */
	Token refresh(String domain, String token, Long userId, Long tenantId, Long domainId, long expireTime);

	/**
	 * 刷新指定域、指定token
	 *
	 * @param request 请求
	 * @return 刷新token，如果token已超过刷新期限，返回null
	 */
	Token refreshToken(HttpServletRequest request);

	/**
	 * 获取刷新周期，分钟数
	 *
	 * @return 刷新周期分钟数
	 */
	@PostConstruct
	int getRefreshTime();

	@PostConstruct
	long getExpireOffSet();

	/**
	 * 判断一个token是否过期，本逻辑为wldos系统规则。
	 *
	 * @param jwt java web token
	 * @return 是否过期
	 */
	boolean isExpired(JWT jwt);

	/**
	 * 取出token会话信息
	 *
	 * @param jwt java web token
	 * @return token 登录信息
	 */
	String takTokenRec(JWT jwt);

	/**
	 * 取出token会话信息
	 *
	 * @param userId 用户id
	 * @return token 登录信息
	 */
	String takTokenRec(Long domainId, Long userId);

	/**
	 * 取出运行时token记录，用于统计在线用户
	 *
	 * @return 在线token记录列表
	 */
	List<Object> takTokenRec();

	/**
	 * 记录日志
	 *
	 * @param domain domain
	 * @param jwt java web token
	 * @param request request
	 */
	@Async
	void recLog(String domain, JWT jwt, HttpServletRequest request);

	/**
	 * 通过token从系统缓存中删除某个token，使其无效
	 *
	 * @param domainId 域名id
	 * @param token token
	 */
	void delToken(Long domainId, String token);

	/**
	 * 通过userId从系统缓存中删除某个token，使其无效
	 *
	 * @param userId user id
	 */
	void delTokenById(Long domainId, Long userId);

	/**
	 * 设置token刷新header
	 *
	 * @param response 响应
	 * @param token token
	 */
	void setTokenHeader(ServerHttpResponse response, Token token);
}