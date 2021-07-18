/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.wldos.support.cache.ICache;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.jsonwebtoken.impl.TextCodec.BASE64;

/**
 * 获取token，token验签。
 *
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
@Slf4j
@Component
public class JWTTool {
	/** 创建token的私钥，产品化应该保证该私钥可以定制 */
	@Value("${app.secret_key}")
	private String secretKey;

	/** 签发者 */
	@Value("${app.token.issuer}")
	private String issuer;

	/** jwt有效时间，默认30分钟 */
	@Getter
	@Value("${app.token_timeout}")
	private long tokenTimeout;

	private ICache cache;

	private JWTTool(String secretKey, long tokenTimeout) {
		this.secretKey = secretKey;
		this.tokenTimeout = tokenTimeout;
	}

	@Autowired
	public JWTTool(ICache cache) {
		this.cache = cache;
	}

	/**
	 * 容器内获取accessToken
	 *
	 * @param jwt 封装jwt实体
	 * @return token
	 */
	public String genToken(JWT jwt) {
		return this.genToken(jwt, issuer);
	}

	/** 刷新token时效是访问token时效的倍数 */
	private static final int REFRESH_TIMES = 4;

	/**
	 * 容器内获取refreshToken, 时限是accessToken设置时限的4倍。刷新token可以被刷新期方案取代，
	 * 使用isCanRefresh方法判断访问token是否可刷新的方案代替。
	 *
	 * @param jwt 封装jwt实体
	 * @return token
	 */
	public String genRefreshToken(JWT jwt) {
		return this.genToken(jwt, this.tokenTimeout * REFRESH_TIMES);
	}

	/**
	 * 容器内获取token,可以指定时限
	 *
	 * @param jwt 封装jwt实体
	 * @param tokenTimeout 时限，多用于指定refreshToken
	 * @return token
	 */
	public String genToken(JWT jwt, long tokenTimeout) {
		return this.genToken(jwt, issuer);
	}

	/**
	 * 通过定制的私钥和超时获取一个jwt工具类实例
	 *
	 * @param secretKey 密钥
	 * @param tokenTimeout 超时时长
	 * @return 实例
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
	 * @param token token
	 * @param secretKey secretKey
	 * @param tokenTimeout tokenTimeout
	 * @return 验签结果
	 */
	public Jws<Claims> verifyToken(String token, String secretKey, long tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout).verifyToken(token);
	}

	/**
	 * 获取一枚新token
	 *
	 * @param header 令牌的元数据，并且包含签名或加密算法的类型
	 * @param issuer 签发者
	 * @return token
	 */
	public String genToken(Map<String, Object> header, IJwt jwt, String issuer) {
		String token = null;
		try {
			long now = System.currentTimeMillis();
			Date startDate = new Date(now);
			Date expireDate = new Date(now + this.tokenTimeout * 1000 * 60);
			SecretKey key = this.secretKeyEncryption();
			JwtBuilder jwtBuilder = Jwts.builder();
			if (!ObjectUtil.isBlank(header))
				jwtBuilder.setHeader(header); // 包含了令牌的元数据，并且包含签名或加密算法的类型
			token = jwtBuilder
					.setId(jwt.getId()) // 唯一ID防止重放
					.setSubject(jwt.getUserId().toString())
					.setIssuedAt(startDate) // 签发时间
					.setExpiration(expireDate) // 过期时间
					.setIssuer(issuer) // 签发者
					.claim(IJwt.KEY_JWT_LOGIN, jwt.getLoginName()) // 携带的信息载荷
					.claim(IJwt.KEY_JWT_NAME, jwt.getAccountName())
					.claim(IJwt.KEY_JWT_TENANT, jwt.getTenantId())
					.signWith(SignatureAlgorithm.HS256, key) // 签名算法和密钥，又称签名和签证
					.compact(); // 执行契约签发token
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

	/**
	 * 获取一枚新token
	 *
	 * @param issuer 签发者
	 * @return token
	 */
	public String genToken(IJwt jwt, String issuer) {
		String token = null;
		try {
			long now = System.currentTimeMillis();
			Date startDate = new Date(now);
			Date expireDate = new Date(now + this.tokenTimeout * 1000 * 60);
			SecretKey key = this.secretKeyEncryption();
			JwtBuilder jwtBuilder = Jwts.builder();
			token = jwtBuilder
					.setId(jwt.getId()) // 唯一ID防止重放
					.setSubject(jwt.getUserId().toString())
					.setIssuedAt(startDate) // 签发时间
					.setExpiration(expireDate) // 过期时间
					.setIssuer(issuer) // 签发者
					.claim(IJwt.KEY_JWT_LOGIN, jwt.getLoginName()) // 携带的信息载荷
					.claim(IJwt.KEY_JWT_NAME, jwt.getAccountName())
					.claim(IJwt.KEY_JWT_TENANT, jwt.getTenantId())
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
			SecretKey key = this.secretKeyEncryption();

			//OK, we can trust this JWT
			return Jwts.parser().setSigningKey(key).parseClaimsJws(token);
		}
		catch (Exception e) {
			log.error("token验证异常，token=" + token);
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
		if (ObjectUtil.isBlank(token) || UserRoleEnum.GUEST.toString().equals(token)) {
			log.info("发现token为空，请检查是游客，还是非法请求，还是token获取异常");
			// 游客专门处理，支持游客的默认授权、监控、管理
			return new JWT(PubConstants.GUEST_ID, PubConstants.TOP_COM_ID, UserRoleEnum.GUEST.toString(), UserRoleEnum.GUEST.toString());
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

		return new JWT(Long.parseLong(claims.getSubject()), Long.parseLong((claims.get(IJwt.KEY_JWT_TENANT).toString())),
				Objects.toString(claims.get(IJwt.KEY_JWT_LOGIN)), Objects.toString(claims.get(IJwt.KEY_JWT_NAME)), claims.getId(), isExpired);
	}

	/**
	 * 判断访问token是否在刷新窗口，如果过期判断访问token时限的刷新期限是否过期，超出刷新期拒绝，防止恶意刷新。
	 *
	 * @param refreshToken
	 * @return
	 */
	public boolean isCanRefresh(String refreshToken) {
		if (UserRoleEnum.GUEST.toString().equals(refreshToken))
			return false; // 游客不需要刷新
		Jws<Claims> claimsJws = this.verifyToken(refreshToken);
		if (claimsJws == null) {
			log.error("非法token，无法解析=" + refreshToken);
			return false;
		}
		Claims claims = claimsJws.getBody();
		long outTime = claims.getExpiration().getTime();

		boolean isExpired = (outTime = (System.currentTimeMillis() - outTime)) > 0;

		if (!isExpired) // 没超期，无须刷新
			return false;

		isExpired = (outTime - (this.tokenTimeout * 1000 * 60 * (REFRESH_TIMES - 1))) > 0;

		// 已超出刷新期，拒绝刷新
		return !isExpired;
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
		if (UserRoleEnum.GUEST.toString().equals(jwt.getLoginName())) { // 游客不设限
			return false;
		}
		if (jwt.getIsExpired())
			return true;

		String token = ObjectUtil.string(this.cache.get(RedisKeyEnum.WLDOS_TOKEN.toString() + ":" + jwt.getId()));

		return ObjectUtil.isBlank(token);
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
		this.cache.set(RedisKeyEnum.WLDOS_TOKEN.toString() + ":" + tokenId, logJson, this.tokenTimeout/* expire / (1000 * 60) //不整除时为0 token即可过期 */, TimeUnit.MINUTES);
	}

	/**
	 * 取出token会话信息
	 *
	 * @param jwt
	 * @return
	 */
	public String takTokenRec(JWT jwt) {
		String tokenId = jwt.getId();
		return ObjectUtil.string(this.cache.get(RedisKeyEnum.WLDOS_TOKEN.toString() + ":" + tokenId));
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
		this.cache.remove(RedisKeyEnum.WLDOS_TOKEN + ":" + tokenId);
	}

	private SecretKey secretKeyEncryption() {
		byte[] encodeKey = BASE64.encode(this.secretKey).getBytes(StandardCharsets.UTF_8);

		return new SecretKeySpec(encodeKey, 0, this.secretKey.length(), "AES");
	}
}