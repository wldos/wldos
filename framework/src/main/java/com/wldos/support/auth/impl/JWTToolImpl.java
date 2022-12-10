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
 * @author 树悉猿
 * @date 2021-03-23
 * @version V1.0
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class JWTToolImpl implements JWTTool {
	@Value("${app.secret_key:wldos-app-key}")
	private String secretKey;

	@Value("${app.token.issuer:wldos}")
	private String issuer;

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

	@Override
	public String genToken(JWT jwt) {
		return this.genToken(jwt, issuer);
	}

	private static final int REFRESH_TIMES = 4;

	public static JWTToolImpl newJWTTool(String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout);
	}

	public static String genToken(JWT jwt, String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout).genToken(jwt);
	}

	@Override
	public Claims verifyToken(String token, String secretKey, int tokenTimeout) {
		return new JWTToolImpl(secretKey, tokenTimeout).verifyToken(token);
	}

	private final JwtBuilder jwtBuilder = Jwts.builder();

	@Override
	public String genToken(Map<String, Object> header, JWT jwt, String issuer) {
		String token = null;
		try {
			if (!ObjectUtils.isBlank(header))
				jwtBuilder.setHeader(header);
			token = this.buildToken(jwtBuilder, jwt, issuer);
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

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
				.setId(jwt.getId())
				.setSubject(jwt.getUserId().toString())
				.setIssuedAt(startDate)
				.setExpiration(expireDate)
				.setIssuer(issuer)
				.claim(IJwt.KEY_JWT_TENANT, jwt.getTenantId())
				.claim(IJwt.KEY_JWT_DOMAIN, jwt.getDomainId())
				.signWith(SignatureAlgorithm.HS256, key)
				.compact();
	}

	@Override
	public Claims verifyToken(String token) {
		try {
			SecretKey key = this.secretKeyEncryption();

			return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		}
		catch (ExpiredJwtException e) {
			return e.getClaims();
		}
		catch (Exception e) {
			log.error("token验证异常，token={}", token);
			return null;
		}
	}

	private JWT popJwt(String token) {

		if (Constants.ENUM_TYPE_ROLE_GUEST.equals(token)) {
			return this.jwtGuest;
		}
		try {
			return this.popJwt(this.verifyToken(token));
		} catch (Exception e) {
			log.error("用户使用了非法token：{}", token);
			return this.jwtGuest;
		}
	}

	private final JWT jwtGuest = new JWT(Constants.GUEST_ID, Constants.TOP_COM_ID, null);

	@Override
	public JWT popJwt(String token, String userIp, String reqUri, String domain, Long domainId) {

		if (ObjectUtils.isBlank(token)) {
			log.info("发现token为空，ip: {}, reqUri: {}, reqDomain: {}", userIp, reqUri, domain);

			return this.jwtGuest;
		}
		return this.popJwt(token);
	}

	private JWT popJwt(Claims claims) {

		if (claims == null || claims.isEmpty()) {
			return null;
		}
		Date expireDate = claims.getExpiration();
		boolean isExpired = (System.currentTimeMillis() - expireDate.getTime()) > 0;

		return new JWT(Long.parseLong(claims.getSubject()), Long.parseLong((claims.get(IJwt.KEY_JWT_TENANT).toString())),
				Long.parseLong((claims.get(IJwt.KEY_JWT_DOMAIN).toString())), claims.getId(), isExpired, claims.getIssuedAt(), expireDate);
	}

	@Override
	public Token refresh(String domain, String token, Long userId, Long tenantId, Long domainId, long expireTime) {
		if (Constants.ENUM_TYPE_ROLE_GUEST.equals(token))
			return null;

		if (System.currentTimeMillis() - expireTime < 0)
			return null;

		if (this.isValid(expireTime)) {
			JWT jwt = new JWT(userId, tenantId, domainId);

			Token refreshToken = new Token(this.genToken(jwt), this.getRefreshTime());
			String logJson = this.takTokenRec(domainId, userId);
			this.recToken(domainId, userId, logJson);

			return refreshToken;
		}

		return null;
	}

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

	@PostConstruct
	@Override
	public int getRefreshTime() {
		if (refreshTime == 0) {
			refreshTime = tokenTimeout * REFRESH_TIMES;
		}
		return refreshTime;
	}

	@Override
	public boolean isExpired(JWT jwt) {
		if (jwt == null)
			return true;
		if (Constants.GUEST_ID.equals(jwt.getUserId())) {
			return false;
		}

		return !this.isValid(jwt.getExpireDate().getTime());
	}

	private boolean isValid(long expireTime) {
		long outTime = System.currentTimeMillis() - expireTime;

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

	private void recToken(Long domainId, Long userId, String logJson) {
		log.info(logJson);
		this.cache.set(this.logKey(domainId, userId), logJson, this.refreshTime, TimeUnit.MINUTES);
	}

	@Override
	public String takTokenRec(JWT jwt) {
		return ObjectUtils.string(this.cache.get(this.logKey(jwt.getDomainId(), jwt.getUserId())));
	}

	@Override
	public String takTokenRec(Long domainId, Long userId) {
		return ObjectUtils.string(this.cache.get(this.logKey(domainId, userId)));
	}

	@Override
	public List<Object> takTokenRec() {
		return this.cache.getByPrefix(RedisKeyEnum.WLDOS_TOKEN.toString());
	}

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

	@Override
	public void delToken(Long domainId, String token) {
		JWT jwt = this.popJwt(token);
		this.delTokenById(domainId, jwt.getUserId());
	}

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