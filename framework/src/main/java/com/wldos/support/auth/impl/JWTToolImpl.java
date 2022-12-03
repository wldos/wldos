/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.auth.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.wldos.common.Constants;
import com.wldos.common.enums.RedisKeyEnum;
import com.wldos.common.utils.AddressUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.auth.IJwt;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.auth.entity.LoginInfo;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.cache.ICache;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static io.jsonwebtoken.impl.TextCodec.BASE64;

/**
 * 获取token，token验签。
 *
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class JWTToolImpl implements JWTTool {
	/** 创建token的私钥，产品化应该保证该私钥可以定制 */
	@Value("${app.secret_key:wldos-app-key}")
	private String secretKey;

	/** 签发者 */
	@Value("${app.token.issuer:wldos}")
	private String issuer;

	/** jwt有效时间，默认30分钟 */
	@Value("${app.token_timeout:30}")
	private int tokenTimeout;

	@Value("${token.access.header:X-CU-AccessToken-WLDOS}")
	protected String tokenHeader;

	@Value("${wldos.domain.header:domain-wldos}")
	protected String domainHeader;

	private ICache cache;

	@Autowired
	public JWTToolImpl(ICache cache) {
		this.cache = cache;
	}

	private JWTToolImpl(String secretKey, int tokenTimeout) {
		this.secretKey = secretKey;
		this.tokenTimeout = tokenTimeout;
	}

	/**
	 * 容器内获取accessToken
	 *
	 * @param jwt 封装jwt实体
	 * @return token 新token
	 */
	@Override
	public String genToken(JWT jwt) {
		return this.genToken(jwt, issuer);
	}

	/** 刷新token时效是访问token时效的倍数 */
	private static final int REFRESH_TIMES = 4;

	/**
	 * 通过定制的私钥和超时获取一个jwt工具类实例
	 *
	 * @param secretKey 密钥
	 * @param tokenTimeout 超时时长
	 * @return 实例
	 */
	public static JWTToolImpl newJWTTool(String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout);
	}

	/**
	 * 静态方式调用生成token
	 *
	 * @param jwt jwt实体
	 * @param secretKey 私钥
	 * @param tokenTimeout jwt有效期
	 * @return token 新token
	 */
	public static String genToken(JWT jwt, String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout).genToken(jwt);
	}

	/**
	 * 静态方式验签并解析jwt
	 *
	 * @param token token
	 * @param secretKey secretKey
	 * @param tokenTimeout tokenTimeout
	 * @return 验签结果
	 */
	@Override
	public Claims verifyToken(String token, String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout).verifyToken(token);
	}

	private final JwtBuilder jwtBuilder = Jwts.builder();

	/**
	 * 获取一枚新token
	 *
	 * @param header 令牌的元数据，并且包含签名或加密算法的类型
	 * @param issuer 签发者
	 * @return token 新token
	 */
	@Override
	public String genToken(Map<String, Object> header, JWT jwt, String issuer) {
		String token = null;
		try {
			if (!ObjectUtils.isBlank(header))
				jwtBuilder.setHeader(header); // 包含了令牌的元数据，并且包含签名或加密算法的类型
			token = this.buildToken(jwtBuilder, jwt, issuer);
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
	 * @return token 新token
	 */
	@Override
	public String genToken(JWT jwt, String issuer) {
		String token = null;
		try {
			token = this.buildToken(jwtBuilder, jwt, issuer);
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

	private String buildToken(JwtBuilder jwtBuilder, JWT jwt, String issuer) {
		long now = System.currentTimeMillis();
		Date startDate = new Date(now);
		Date expireDate = new Date(now + (long) this.tokenTimeout * 1000L * 60L);
		SecretKey key = this.secretKeyEncryption();
		return jwtBuilder
				.setId(jwt.getId()) // 唯一ID防止重放
				.setSubject(jwt.getUserId().toString())
				.setIssuedAt(startDate) // 签发时间
				.setExpiration(expireDate) // 过期时间
				.setIssuer(issuer) // 签发者
				.claim(IJwt.KEY_JWT_TENANT, jwt.getTenantId()) // 携带的信息载荷
				.claim(IJwt.KEY_JWT_DOMAIN, jwt.getDomainId())
				.signWith(SignatureAlgorithm.HS256, key) // 签名算法和密钥，又称签名和签证
				.compact(); // 执行契约签发token
	}

	/**
	 * token验签并返回token解析
	 *
	 * @param token token字面值
	 * @return token解析数据，解析异常返回null
	 */
	@Override
	public Claims verifyToken(String token) {
		try {
			SecretKey key = this.secretKeyEncryption();

			//OK, we can trust this JWT
			return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		}
		catch (ExpiredJwtException e) {
			return e.getClaims(); // 用于自定义超时刷新机制
		}
		catch (Exception e) {
			log.error("token验证异常，token={}", token);
			return null;
		}
	}

	/**
	 * token转换jwt实体
	 *
	 * @param token token字面值
	 * @return jwt实例
	 */
	private JWT popJwt(String token) {

		if (Constants.ENUM_TYPE_ROLE_GUEST.equals(token)) {
			// 游客专门处理，支持游客的默认授权、监控、管理
			return this.jwtGuest;
		}
		try {
			return this.popJwt(this.verifyToken(token));
		} catch (Exception e) {
			log.error("用户使用了非法token：{}", token);
			// 非法token，返回游客
			return this.jwtGuest;
		}
	}
	// 游客
	private final JWT jwtGuest = new JWT(Constants.GUEST_ID, Constants.TOP_COM_ID, null);

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
	@Override
	public JWT popJwt(String token, String userIp, String reqUri, String domain, Long domainId) {

		if (ObjectUtils.isBlank(token)) {
			log.info("发现token为空，ip: {}, reqUri: {}, reqDomain: {}", userIp, reqUri, domain);
			// 以游客处理
			return this.jwtGuest;
		}
		return this.popJwt(token);
	}

	/**
	 * 根据token解析创建jwt实体
	 *
	 * @param claims token解析数据
	 * @return jwt实体
	 */
	private JWT popJwt(Claims claims) {

		if (claims == null || claims.isEmpty()) {
			return null; // 解析异常token
		}
		Date expireDate = claims.getExpiration();
		boolean isExpired = (System.currentTimeMillis() - expireDate.getTime()) > 0;

		return new JWT(Long.parseLong(claims.getSubject()), Long.parseLong((claims.get(IJwt.KEY_JWT_TENANT).toString())),
				Long.parseLong((claims.get(IJwt.KEY_JWT_DOMAIN).toString())), claims.getId(), isExpired, claims.getIssuedAt(), expireDate);
	}

	/**
	 * 刷新token。
	 *
	 * @param token token
	 * @param userId 用户id
	 * @param tenantId 租户id
	 * @param expireTime 超期时间
	 * @return 刷新token
	 */
	@Override
	public Token refresh(String domain, String token, Long userId, Long tenantId, Long domainId, long expireTime) {
		if (Constants.ENUM_TYPE_ROLE_GUEST.equals(token))
			return null; // 游客首次请求不需要刷新

		if (System.currentTimeMillis() - expireTime < 0) // 没超期，无须刷新
			return null;

		// 在刷新期，开始刷新
		if (this.isValid(expireTime)) {
			JWT jwt = new JWT(userId, tenantId, domainId);

			// 更新token日志，绑定新的token id
			Token refreshToken = new Token(this.genToken(jwt), this.getRefreshTime());
			String logJson = this.takTokenRec(domainId, userId); // 只记录登陆时获取的信息，降低续签性能损耗
			this.recToken(domainId, userId, logJson);

			return refreshToken;
		}

		return null;
	}

	/**
	 * 刷新指定域、指定token
	 *
	 * @param request 请求
	 * @return 刷新token，如果token已超过刷新期限，返回null
	 */
	@Override
	public Token refreshToken(HttpServletRequest request) {
		try {
			String domain = request.getHeader(this.domainHeader);
			String refreshToken = request.getHeader(this.tokenHeader);
			Long userId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID));
			Long tenantId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
			Long domainId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_DOMAIN));
			long expireTime = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));

			return this.refresh(domain, refreshToken, userId, tenantId, domainId, expireTime);
		} catch (NumberFormatException e) {
			log.error("refreshToken异常：{}", e.getMessage());
			return null;
		}
	}

	private int refreshTime;

	/**
	 * 获取刷新周期，分钟数
	 *
	 * @return 刷新周期分钟数
	 */
	@PostConstruct
	@Override
	public int getRefreshTime() {
		if (refreshTime == 0) {
			refreshTime = tokenTimeout * REFRESH_TIMES;
		}
		return refreshTime;
	}

	/**
	 * 判断一个token是否过期，本逻辑为wldos系统规则。
	 *
	 * @param jwt java web token
	 * @return 是否过期
	 */
	@Override
	public boolean isExpired(JWT jwt) {
		if (jwt == null) // 过期时校验结果为空
			return true;
		if (Constants.GUEST_ID.equals(jwt.getUserId())) { // 游客不设限
			return false;
		}

		// 已超出刷新期，判定为无效
		return !this.isValid(jwt.getExpireDate().getTime());
	}

	/**
	 *  token是否有效
	 *  防止token重放和伪造，在分布式环境中两种方案：1.共享token缓存，共享缓存服务（redis或自研），共享缓存成最大安全隐患；
	 *  2.token id存储身份标识，根据标识实现负载均衡，分发到所属主机重复1的处理，优点是系统伸缩方便，缺点是如果不能做得一步到位需要二次转发校验，存在更多的宕机风险，解决方案：实现节点宕机恢复机制
	 *  3.服务端不做token缓存，每次请求验证token、签发新token，以api为单元，保证每次请求该api的token是首次使用，问题是解决不了并发请求
	 *  4.开启https，阻止截获攻击，每次请求判断token已过期但未超出刷新周期则签发新token否则不签发，客户端检测到新token设置到store，老token未过期也可以使用，同时存储已签发token（期限是过期时间）仅用于确定在线用户
	 * 	暂使用方案4
	 *
	 * @param expireTime token超期时间
	 * @return 是否有效，已超刷新期认为无效
	 */
	private boolean isValid(long expireTime) {
		long outTime = System.currentTimeMillis() - expireTime;

		// 已超出刷新期，判定为无效
		return outTime - this.expireOffSet <= 0L;
	}

	private long expireOffSet;

	@PostConstruct
	@Override
	public long getExpireOffSet() {
		if (expireOffSet == 0L) {
			expireOffSet = (long) this.tokenTimeout * 1000L * 60L * (REFRESH_TIMES - 1);
		}
		return expireOffSet;
	}

	/**
	 * 记录token和相关json格式的日志到缓存
	 *
	 * @param userId tokenId
	 * @param logJson 登陆信息
	 */
	private void recToken(Long domainId, Long userId, String logJson) {
		log.info(logJson); /* 有效期为刷新周期，超期后刷新期内活跃的用户自动续签继承登陆信息，超期后直到刷新期超时仍无动作的用户视为离线，用于判定在线用户 */
		this.cache.set(this.logKey(domainId, userId), logJson, this.refreshTime, TimeUnit.MINUTES);
	}

	/**
	 * 取出token会话信息
	 *
	 * @param jwt java web token
	 * @return token 登录信息
	 */
	@Override
	public String takTokenRec(JWT jwt) {
		return ObjectUtils.string(this.cache.get(this.logKey(jwt.getDomainId(), jwt.getUserId())));
	}

	/**
	 * 取出token会话信息
	 *
	 * @param userId 用户id
	 * @return token 登录信息
	 */
	@Override
	public String takTokenRec(Long domainId, Long userId) {
		return ObjectUtils.string(this.cache.get(this.logKey(domainId, userId)));
	}

	/**
	 * 取出运行时token记录，用于统计在线用户
	 *
	 * @return 在线token记录列表
	 */
	@Override
	public List<Object> takTokenRec() {
		return this.cache.getByPrefix(RedisKeyEnum.WLDOS_TOKEN.toString());
	}

	/**
	 * 记录日志
	 *
	 * @param domain domain
	 * @param jwt java web token
	 * @param request request
	 */
	@Override
	@Async
	public void recLog(String domain, JWT jwt, HttpServletRequest request) {

		final String ip = IpUtils.getClientIp(request);
		String address = AddressUtils.getRealAddressByIP(ip);
		final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		String os = userAgent.getOperatingSystem().getName();
		String useragent = userAgent.getBrowser().getName();

		LoginInfo loginInfo = new LoginInfo(jwt.getId(), jwt.getUserId(), null, domain, ip, address, useragent, os, System.currentTimeMillis());
		this.recToken(jwt.getDomainId(), jwt.getUserId(), JSON.toJSONString(loginInfo, false));
	}

	/**
	 * 通过token从系统缓存中删除某个token，使其无效
	 *
	 * @param domainId 域名id
	 * @param token token
	 */
	@Override
	public void delToken(Long domainId, String token) {
		JWT jwt = this.popJwt(token);
		this.delTokenById(domainId, jwt.getUserId());
	}

	/**
	 * 通过userId从系统缓存中删除某个token，使其无效
	 *
	 * @param userId user id
	 */
	@Override
	public void delTokenById(Long domainId, Long userId) {
		this.cache.remove(this.logKey(domainId, userId));
	}

	@Override
	public void setTokenHeader(ServerHttpResponse response, Token token) {
		HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();
		res.setHeader("token", token.getAccessToken());
		res.setHeader("refresh", Integer.toString(token.getRefresh()));
	}

	private SecretKey secretKeyEncryption() {
		byte[] encodeKey = BASE64.encode(this.secretKey).getBytes(StandardCharsets.UTF_8);

		return new SecretKeySpec(encodeKey, 0, this.secretKey.length(), "AES");
	}


	private String logKey(Long domainId, Long userId) {
		return RedisKeyEnum.WLDOS_TOKEN.toString() + ":" + domainId + ":" + userId;
	}
}