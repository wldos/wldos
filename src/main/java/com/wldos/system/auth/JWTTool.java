/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.storage.ICache;
import com.wldos.system.sysenum.RedisKeyEnum;
import com.wldos.system.sysenum.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 获取token，token验签。
 *
 * @Title JWT token实现类
 * @Package com.wldos.system.auth
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
@Slf4j
@Component
public class JWTTool {
	// 创建token的私钥，产品化应该保证该私钥可以定制
	@Value("${app.secret_key}")
	private String secretKey;

	// jwt有效时间，默认30分钟
	@Getter
	@Value("${app.token_timeout}")
	private long tokenTimeout;

	@Autowired
	private ICache cache;

	public JWTTool() {
	}

	private JWTTool(String secretKey, long tokenTimeout) {
		this.secretKey = secretKey;
		this.tokenTimeout = tokenTimeout;
	}

	/**
	 * 容器内获取accessToken
	 *
	 * @param jwt 封装jwt实体
	 * @return token
	 */
	public String genToken(JWT jwt) {
		return this.genToken(null, jwt, "wldos");
	}

	/** 刷新token时效是访问token时效的倍数 */
	private static final int refreshTimes = 4;

	/**
	 * 容器内获取refreshToken, 时限是accessToken设置时限的4倍。刷新token可以被刷新期方案取代，
	 * 使用isCanRefresh方法判断访问token是否可刷新的方案代替。
	 *
	 * @param jwt 封装jwt实体
	 * @return token
	 */
	public String genRefreshToken(JWT jwt) {
		return this.genToken(jwt, this.tokenTimeout * refreshTimes);
	}

	/**
	 * 容器内获取token,可以指定时限
	 *
	 * @param jwt 封装jwt实体
	 * @param tokenTimeout 时限，多用于指定refreshToken
	 * @return token
	 */
	public String genToken(JWT jwt, long tokenTimeout) {
		return this.genToken(null, jwt, "wldos");
	}

	/**
	 * 通过定制的私钥和超时获取一个jwt工具类实例
	 *
	 * @param secretKey
	 * @param tokenTimeout
	 * @return
	 */
	public static JWTTool newJWTTool(String secretKey, long tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout);
	}

	/**
	 * 静态方式调用生成token
	 *
	 * @param jwt jwt实体
	 * @param secretKey 私钥
	 * @param tokenTimeout jwt有效期
	 * @return token
	 */
	public static String genToken(JWT jwt, String secretKey, long tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout).genToken(jwt);
	}

	/**
	 * 静态方式验签并解析jwt
	 *
	 * @param token
	 * @param secretKey
	 * @param tokenTimeout
	 * @return
	 */
	public Jws<Claims> verifyToken(String token, String secretKey, long tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout).verifyToken(token);
	}

	/**
	 * 获取一枚新token
	 *
	 * @param header
	 * @param issuer
	 * @return
	 */
	public String genToken(Map<String, Object> header, IJWT jwt,
			String issuer) {
		String token = null;
		try {
			long now = System.currentTimeMillis();
			Date startDate = new Date(now);
			Date expireDate = new Date(now + this.tokenTimeout * 1000 * 60);
			SecretKey key = this.SecretKeyEncryption();
			token = Jwts.builder()
					//.setHeader(header) // 包含了令牌的元数据，并且包含签名或加密算法的类型
					.setId(jwt.getId()) // 唯一ID防止重放
					.setSubject(jwt.getUserId().toString())
					.setIssuedAt(startDate) // 签发时间
					.setExpiration(expireDate) // 过期时间
					.setIssuer(issuer) // 签发者
					.claim(IJWT.key_jwt_login, jwt.getLoginName())
					.claim(IJWT.key_jwt_name, jwt.getAccountName())
					.claim(IJWT.key_jwt_tenant, jwt.getTenantId())
					//.setClaims(jwt) // 携带的信息载荷
					.signWith(SignatureAlgorithm.HS256, key) // 签名算法和密钥，又称签名和签证
					.compact(); // 执行契约签发token
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

	/**
	 * token验签并返回token解析
	 *
	 * @param token
	 * @return
	 */
	public Jws<Claims> verifyToken(String token) {
		try {
			SecretKey key = this.SecretKeyEncryption();
			Jws<Claims> jws = Jwts.parser()
					.setSigningKey(key)
					.parseClaimsJws(token);

			//OK, we can trust this JWT
			return jws;
		}
		catch (Exception e) {
			log.error("token验证异常，token="+token);
			return null;
		}
	}

	/**
	 * token转换jwt实体
	 *
	 * @param token
	 * @return
	 */
	public JWT popJwt(String token) {
		if (ObjectUtil.isBlank(token) || UserRoleEnum.guest.toString().equals(token)) {
			log.info("发现token为空，请检查是游客，还是非法请求，还是token获取异常");
			// 游客专门处理，支持游客的默认授权、监控、管理
			return new JWT(PubConstants.GUEST_ID, PubConstants.TOP_COM_ID, UserRoleEnum.guest.toString(), UserRoleEnum.guest.toString());
		}

		return this.popJwt(this.verifyToken(token));
	}

	/**
	 * 填充jwt属性创建jwt实体
	 *
	 * @param claimsJws
	 * @return
	 */
	public JWT popJwt(Jws<Claims> claimsJws) {
		if (claimsJws == null) {
			return null;
		}
		Claims claims = claimsJws.getBody();

		if (claims == null || claims.isEmpty()) {
			return null;
		}
		boolean isExpired = (System.currentTimeMillis() - claims.getExpiration().getTime()) > 0;
		JWT jwt = new JWT(Long.parseLong(claims.getSubject()), Long.parseLong((claims.get(IJWT.key_jwt_tenant).toString())),
				Objects.toString(claims.get(IJWT.key_jwt_login)), Objects.toString(claims.get(IJWT.key_jwt_name)), claims.getId(), isExpired);
		return jwt;
	}

	/**
	 * 判断访问token是否在刷新窗口，如果过期判断访问token时限的刷新期限是否过期，超出刷新期拒绝，防止恶意刷新。
	 *
	 * @param refreshToken
	 * @return
	 */
	public boolean isCanRefresh(String refreshToken) {
		if (UserRoleEnum.guest.toString().equals(refreshToken))
			return false; // 游客不需要刷新
		Jws<Claims> claimsJws = this.verifyToken(refreshToken);
		if (claimsJws == null) {
			log.error("非法token，无法解析="+refreshToken);
			return false;
		}
		Claims claims = claimsJws.getBody();
		long outTime = claims.getExpiration().getTime();

		boolean isExpired = (outTime = (System.currentTimeMillis() - outTime)) > 0;

		if (!isExpired) // 没超期，无须刷新
			return false;

		isExpired = (outTime - (this.tokenTimeout * 1000 * 60 * (refreshTimes - 1))) > 0;

		if (isExpired) // 已超出刷新期，拒绝刷新
			return false;

		return true;
	}

	/**
	 * 判断一个token是否过期，本逻辑为wldos系统规则。
	 *
	 * @param token
	 * @return
	 */
	public boolean isExpired(String token) {
		return this.isExpired(this.popJwt(token));
	}

	/**
	 * 判断一个token是否过期，本逻辑为wldos系统规则。
	 *
	 * @param jwt
	 * @return
	 */
	public boolean isExpired(JWT jwt) {
		if (UserRoleEnum.guest.toString().equals(jwt.getLoginName())) { // 游客不设限
			return false;
		}
		if (jwt.getIsExpired())
			return true;

		String token = ObjectUtil.string(this.cache.get(RedisKeyEnum.wldosToken.toString() + ":" + jwt.getId()));
		if (ObjectUtil.isBlank(token)) {
			return true;
		}
		return false;
	}

	/**
	 * 记录token和相关json格式的日志到缓存
	 *
	 * @param token
	 * @param logJson
	 */
	public void recToken(String token, String logJson) {
		Jws<Claims> claimsJws = this.verifyToken(token);
		Claims claims = claimsJws.getBody();
		String tokenId = claims.getId();
		long expire = claims.getExpiration().getTime() - System.currentTimeMillis();
		if (expire <= 0) {
			// 已过期，略过
			return;
		}
		this.cache.set(RedisKeyEnum.wldosToken.toString() + ":" + tokenId, logJson, this.tokenTimeout/* expire / (1000 * 60) //不整除时为0 token即可过期 */, TimeUnit.MINUTES);
	}

	/**
	 * 取出token会话信息
	 *
	 * @param jwt
	 * @return
	 */
	public String takTokenRec(JWT jwt) {
		String tokenId = jwt.getId();
		return ObjectUtil.string(this.cache.get(RedisKeyEnum.wldosToken.toString() + ":" + tokenId));
	}

	/**
	 * 通过token从系统缓存中删除某个token，使其无效
	 *
	 * @param token
	 */
	public void delToken(String token) {
		JWT jwt = this.popJwt(token);
		this.delTokenById(jwt.getId());
	}

	/**
	 * 通过tokenId从系统缓存中删除某个token，使其无效
	 *
	 * @param tokenId
	 */
	public void delTokenById(String tokenId) {
		this.cache.remove(RedisKeyEnum.wldosToken + ":" + tokenId);
	}

	private SecretKey SecretKeyEncryption() {
		byte[] encodeKey = Base64Codec.BASE64.encode(this.secretKey).getBytes(StandardCharsets.UTF_8);
		SecretKey key = new SecretKeySpec(encodeKey, 0, this.secretKey.length(), "AES");

		return key;
	}
}