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
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.wldos.support.cache.ICache;
import com.wldos.support.util.AddressUtils;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.Constants;
import com.wldos.system.auth.entity.LoginInfo;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.enums.UserRoleEnum;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class JWTTool {

	@Value("${app.secret_key}")
	private String secretKey;

	@Value("${app.token.issuer}")
	private String issuer;

	@Value("${app.token_timeout}")
	private int tokenTimeout;

	private ICache cache;

	@Autowired
	public JWTTool(ICache cache) {
		this.cache = cache;
	}

	private JWTTool(String secretKey, int tokenTimeout) {
		this.secretKey = secretKey;
		this.tokenTimeout = tokenTimeout;
	}

	public String genToken(JWT jwt) {
		return this.genToken(jwt, issuer);
	}

	private static final int REFRESH_TIMES = 4;

	public static JWTTool newJWTTool(String secretKey, int tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout);
	}

	public static String genToken(JWT jwt, String secretKey, int tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout).genToken(jwt);
	}

	public Claims verifyToken(String token, String secretKey, int tokenTimeout) {
		return new JWTTool(secretKey, tokenTimeout).verifyToken(token);
	}

	private final JwtBuilder jwtBuilder = Jwts.builder();

	public String genToken(Map<String, Object> header, IJwt jwt, String issuer) {
		String token = null;
		try {
			if (!ObjectUtil.isBlank(header))
				jwtBuilder.setHeader(header);
			token = this.buildToken(jwtBuilder, jwt, issuer);
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

	public String genToken(IJwt jwt, String issuer) {
		String token = null;
		try {
			token = this.buildToken(jwtBuilder, jwt, issuer);
		}
		catch (Exception e) {
			log.error("token生成异常", e);
		}
		return token;
	}

	private String buildToken(JwtBuilder jwtBuilder, IJwt jwt, String issuer) {
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
				.signWith(SignatureAlgorithm.HS256, key)
				.compact();
	}

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

	public JWT popJwt(String token) {

		if (UserRoleEnum.GUEST.toString().equals(token)) {

			return this.jwtGuest;
		}

		return this.popJwt(this.verifyToken(token));
	}

	private final JWT jwtGuest = new JWT(Constants.GUEST_ID, Constants.TOP_COM_ID);

	public JWT popJwt(String token, String userIp, String reqUri) {

		if (ObjectUtil.isBlank(token)) {
			log.info("发现token为空，ip: {}, reqUri: {}", userIp, reqUri);

			return this.jwtGuest;
		}
		return this.popJwt(token);
	}

	public JWT popJwt(Claims claims) {

		if (claims == null || claims.isEmpty()) {
			return null;
		}
		Date expireDate = claims.getExpiration();
		boolean isExpired = (System.currentTimeMillis() - expireDate.getTime()) > 0;

		return new JWT(Long.parseLong(claims.getSubject()), Long.parseLong((claims.get(IJwt.KEY_JWT_TENANT).toString())),
				claims.getId(), isExpired, claims.getIssuedAt(), expireDate);
	}

	public Token refresh(String domain, HttpServletRequest request, String token, Long userId, Long tenantId, long expireTime) {
		if (UserRoleEnum.GUEST.toString().equals(token))
			return null;

		if (System.currentTimeMillis() - expireTime < 0)
			return null;

		if (this.isValid(expireTime)) {
			JWT jwt = new JWT(userId, tenantId);

			String accessToken = this.genToken(jwt);

			Token refreshToken = new Token();
			refreshToken.setRefresh(this.getRefreshTime());
			refreshToken.setAccessToken(accessToken);

			String logJson = this.takTokenRec(userId);
			this.recToken(userId.toString(), logJson);

			return refreshToken;
		}

		return null;
	}

	private int refreshTime;

	@PostConstruct
	public int getRefreshTime() {
		if (refreshTime == 0) {
			refreshTime = tokenTimeout * REFRESH_TIMES;
		}
		return refreshTime;
	}

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
	public long getExpireOffSet() {
		if (expireOffSet == 0L) {
			expireOffSet = (long) this.tokenTimeout * 1000L * 60L * (REFRESH_TIMES - 1);
		}
		return expireOffSet;
	}

	public void recToken(String userId, String logJson) {
		log.info(logJson);
		this.cache.set(this.logKey(userId), logJson, this.refreshTime, TimeUnit.MINUTES);
	}

	public String takTokenRec(JWT jwt) {
		String userId = jwt.getUserId().toString();
		return ObjectUtil.string(this.cache.get(this.logKey(userId)));
	}

	public String takTokenRec(String userId) {
		return ObjectUtil.string(this.cache.get(this.logKey(userId)));
	}

	public String takTokenRec(Long userId) {
		return ObjectUtil.string(this.cache.get(this.logKey(userId.toString())));
	}

	@Async
	public void recLog(String domain, JWT jwt, HttpServletRequest request) {
		final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		final String ip = IpUtils.getClientIp(request);
		String address = AddressUtils.getRealAddressByIP(ip);

		LoginInfo loginInfo = new LoginInfo();

		String os = userAgent.getOperatingSystem().getName();
		String useragent = userAgent.getBrowser().getName();
		loginInfo.setId(jwt.getId());
		loginInfo.setUserId(jwt.getUserId());
		loginInfo.setDomain(domain);
		loginInfo.setUserAgent(useragent);
		loginInfo.setLoginIP(ip);
		loginInfo.setNetLocation(address);
		loginInfo.setOs(os);
		loginInfo.setLoginTime(System.currentTimeMillis());
		this.recToken(jwt.getUserId().toString(), JSON.toJSONString(loginInfo, false));
	}

	public void delToken(String domain, String token) {
		JWT jwt = this.popJwt(token);
		this.delTokenById(domain, jwt.getId());
	}

	public void delTokenById(String domain, String userId) {
		this.cache.remove(this.logKey(userId));
	}

	private SecretKey secretKeyEncryption() {
		byte[] encodeKey = BASE64.encode(this.secretKey).getBytes(StandardCharsets.UTF_8);

		return new SecretKeySpec(encodeKey, 0, this.secretKey.length(), "AES");
	}

	private String logKey(String userId) {
		return RedisKeyEnum.WLDOS_TOKEN.toString() + ":" + userId;
	}
}