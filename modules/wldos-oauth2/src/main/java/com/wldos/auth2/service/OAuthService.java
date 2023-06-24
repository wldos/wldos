/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.auth2.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wldos.auth.service.StateCodeService;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.Register;
import com.wldos.auth2.entity.WoOauthLoginUser;
import com.wldos.auth2.model.AccessTokenEntity;
import com.wldos.auth2.model.AccessTokenQQ;
import com.wldos.auth2.model.AccessTokenWeibo;
import com.wldos.auth2.model.OAuthConfig;
import com.wldos.auth2.model.OAuthUser;
import com.wldos.auth2.model.OAuthUserQQ;
import com.wldos.auth2.model.OAuthUserWechat;
import com.wldos.auth2.model.OAuthUserWeibo;
import com.wldos.auth2.model.OpenIdQQ;
import com.wldos.auth2.vo.OAuthLoginParams;
import com.wldos.base.NoRepoService;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.HttpUtils;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.support.system.entity.WoOptions;
import com.wldos.sys.base.enums.OAuthTypeEnum;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.base.service.OptionsService;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.service.MailService;
import com.wldos.sys.core.service.UserService;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth2.0登录相关认证、授权服务。
 *
 * @author 树悉猿
 * @date 2022/10/14
 * @version 1.0
 */
@Service
@SuppressWarnings({ "all" })
@Transactional(rollbackFor = Exception.class)
public class OAuthService extends NoRepoService {
	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);

	private final AuthService authService;

	private final UserService userService;

	private final OAuthLoginUserService oAuthLoginUserService;

	private final StateCodeService stateCodeService;

	private final MailService mailService;

	private final OptionsService optionsService;

	public OAuthService(AuthService authService, UserService userService, OAuthLoginUserService oAuthLoginUserService, StateCodeService stateCodeService, MailService mailService, OptionsService optionsService) {
		this.authService = authService;
		this.userService = userService;
		this.oAuthLoginUserService = oAuthLoginUserService;
		this.stateCodeService = stateCodeService;
		this.mailService = mailService;
		this.optionsService = optionsService;
	}

	/**
	 * OAuth2登录平台
	 *
	 * @param domain 域名
	 * @param domainId 域Id
	 * @param comId 用户主企业id
	 * @param loginAuthParams 登陆参数
	 * @param request 请求
	 * @param hexKeyCode 十六进制加密key
	 * @return 登录实例
	 */
	public Login login(String domain, Long domainId, Long comId, OAuthLoginParams oAuthLoginParams,
			HttpServletRequest request, HttpServletResponse response, String curUserIp) throws IOException {
		Login user = new Login();
		String stateCode = oAuthLoginParams.getState();
		if (ObjectUtils.isBlank(stateCode)) {
			user.setStatus("error");
			user.setNews("非法参数");
			user.setType(oAuthLoginParams.getAuthType());
			return user;
		}
		else {
			if (!this.stateCodeService.checkState(oAuthLoginParams.getState())) {
				user.setStatus("error");
				user.setNews("状态码错误");
				user.setType(oAuthLoginParams.getAuthType());
				return user;
			}
		}

		OAuthUser oAuthUser = this.getOAuthUserInfo(oAuthLoginParams.getAuthType(), oAuthLoginParams.getCode());
		WoOauthLoginUser oAuthLoginUser = this.oAuthLoginUserService.findByUnionId(oAuthUser.getOpenId(), oAuthUser.getUnionId());
		WoUser woUser;
		if (ObjectUtils.isBlank(oAuthLoginUser)) {
			Long uid = this.nextId();
			Register register = Register.of(uid, uid.toString(), oAuthUser.getNickname(), curUserIp);
			woUser = this.userService.createUser(domainId, register, false);
			oAuthUser.setOauthType(oAuthLoginParams.getAuthType());
			oAuthLoginUser = this.oAuthLoginUserService.createOAuthLoginUser(woUser, oAuthUser);

			if (!OAuthTypeEnum.WeiBo.getValue().equals(oAuthLoginParams.getAuthType())) { // 目前微博头像io 403
				String headImgUrl = oAuthUser.getHeadImgUrl();
				this.userService.uploadAvatar(request, response, headImgUrl, new int[] { 144, 144 }, woUser.getId(), curUserIp);
			}
		}
		else {
			woUser = this.userService.findById(oAuthLoginUser.getUserId());
		}

		user.setStatus("ok");
		Tenant tenant = this.userService.queryTenantInfoByTAdminId(woUser.getId());

		JWT jwt = new JWT(woUser.getId(), tenant.getId(), domainId);
		String accessToken = this.jwtTool.genToken(jwt);

		Token token = new Token(accessToken, this.jwtTool.getRefreshTime());
		user.setToken(token);

		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, comId, woUser.getId());
		user.setCurrentAuthority(currentAuthority);

		this.jwtTool.recLog(domain, jwt, request);

		return user;
	}

	private OAuthUser getOAuthUserInfo(String authType, String code) {
		String settings = this.optionsService.findSettingsByKey(this.genOAuthSettingsKey(authType));
		if (ObjectUtils.isBlank(settings)) {
			throw new RuntimeException("社会化登录配置为空，请先配置" + this.genOAuthSettingsKey(authType));
		}

		OAuthConfig oAuthConfig = this.resJson.readEntity(settings, new TypeReference<OAuthConfig>() {});
		String accessTokenUri = oAuthConfig.getAccessTokenUri();

		if (OAuthTypeEnum.WeChat.getValue().equals(authType)) {

			accessTokenUri = String.format(accessTokenUri, oAuthConfig.getAppId(), oAuthConfig.getAppSecret(), code);

			String accessTokenJson = HttpUtils.sendGet(accessTokenUri);

			AccessTokenEntity tokenEntity = this.resJson.readEntity(accessTokenJson, new TypeReference<AccessTokenEntity>() {});

			String userInfoUri = String.format(oAuthConfig.getUserInfoUri(), tokenEntity.getAccessToken(), tokenEntity.getOpenId());

			String userInfoJson = HttpUtils.sendGet(userInfoUri);

			return this.resJson.readEntity(userInfoJson, new TypeReference<OAuthUserWechat>() {});
		}
		else if (OAuthTypeEnum.WeiBo.getValue().equals(authType)) {

			String params = "client_id=" + oAuthConfig.getAppId() + "&client_secret=" + oAuthConfig.getAppSecret() +
					"&grant_type=authorization_code&code=" + code + "&redirect_uri=" + this.getRedirectUri(oAuthConfig.getRedirectUri(), this.getRedirectSufix(authType));

			String accessTokenJson = HttpUtils.sendPost(accessTokenUri, params, null);

			AccessTokenWeibo tokenEntity = this.resJson.readEntity(accessTokenJson, new TypeReference<AccessTokenWeibo>() {});

			String userInfoUri = String.format(oAuthConfig.getUserInfoUri(), tokenEntity.getAccessToken(), tokenEntity.getUid());

			String userInfoJson = HttpUtils.sendGet(userInfoUri);

			return this.resJson.readEntity(userInfoJson, new TypeReference<OAuthUserWeibo>() {});
		}
		else {
			// for qq
			accessTokenUri = String.format(accessTokenUri, oAuthConfig.getAppId(), oAuthConfig.getAppSecret(),
					this.getRedirectUri(oAuthConfig.getRedirectUri(), this.getRedirectSufix(authType)), code);

			String accessTokenJson = HttpUtils.sendGet(accessTokenUri);

			AccessTokenQQ tokenEntity = this.resJson.readEntity(accessTokenJson, new TypeReference<AccessTokenQQ>() {});

			// 需要另外获取openid
			String openIdstr = HttpUtils.sendGet("https://graph.qq.com/oauth2.0/me?access_token=" + tokenEntity.getAccessToken() + "&fmt=json");

			OpenIdQQ openIdQQ = this.resJson.readEntity(openIdstr, new TypeReference<OpenIdQQ>() {});
			String openid = openIdQQ.getOpenId();
			String userInfoUri = String.format(oAuthConfig.getUserInfoUri(), tokenEntity.getAccessToken(), openIdQQ.getClientId(), openid);

			String userInfoJson = HttpUtils.sendGet(userInfoUri);

			OAuthUserQQ oAuthUserQQ = this.resJson.readEntity(userInfoJson, new TypeReference<OAuthUserQQ>() {});
			oAuthUserQQ.setOpenId(openid);
			oAuthUserQQ.setUnionId(openid);

			return oAuthUserQQ;
		}
	}

	private String genOAuthSettingsKey(String authType) {
		return Constants.OAUTH_SETTINGS_PREFIX + authType;
	}

	/**
	 * 生成授权码申请url
	 *
	 * @param oAuthType OAuth服务商类型，比如微信、qq、微博
	 * @param redirectPrefix 实际要重定向的uri前缀，wldos平台的社会化登录默认授权根域或某个指定域，非授权域名需要显式标记才能共享
	 * @param request 请求对象
	 * @return url
	 */
	public String genAuthCodeUrl(String oAuthType, String redirectPrefix) throws UnsupportedEncodingException {

		String settings = this.optionsService.findSettingsByKey(this.genOAuthSettingsKey(oAuthType));
		if (ObjectUtils.isBlank(settings)) {
			throw new RuntimeException("社会化登录配置为空，请先配置" + this.genOAuthSettingsKey(oAuthType));
		}

		OAuthConfig oAuthConfig = this.resJson.readEntity(settings, new TypeReference<OAuthConfig>() {});

		String stateCode = this.stateCodeService.genState();

		String redirectSuFix = this.getRedirectSufix(oAuthType);
		String rdPrefix = URLDecoder.decode(redirectPrefix, StandardCharsets.UTF_8.name());
		if (!oAuthConfig.getRedirectUri().contains(rdPrefix)) {
			String realRedirect = "-" + rdPrefix + redirectSuFix;

			stateCode += URLEncoder.encode(realRedirect, StandardCharsets.UTF_8.name());
		}

		String redirectUri = this.getRedirectUri(oAuthConfig.getRedirectUri(), redirectSuFix);

		return this.resJson.ok("url", String.format(oAuthConfig.getCodeUri(), oAuthConfig.getAppId(), redirectUri,
				oAuthConfig.getScope(), stateCode));
	}

	private String getRedirectUri(String redirectUriConfig, String redirectSuFix) {
		try {
			return URLEncoder.encode(redirectUriConfig + redirectSuFix, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的编码异常", e);
		}
	}

	private String getRedirectSufix(String oAuthType) {
		return "/user/auth/login/" + oAuthType;
	}

	/**
	 * 配置社会化登录参数
	 *
	 * @param authType 社会化登录类型
	 * @param config 配置参数
	 */
	public void configOAuth(String authType, OAuthConfig config) {
		String key = this.genOAuthSettingsKey(authType);
		WoOptions options = this.optionsService.findByKey(key);
		String value = this.resJson.toJson(config, true);

		if (ObjectUtils.isBlank(options)) {
			options = WoOptions.of(this.nextId(), key, value);

			this.optionsService.insertSelective(options);
			return;
		}

		options.setOptionValue(value);
		this.optionsService.update(options);
	}

	/**
	 * 取回社会化登录配置
	 *
	 * @param authType 社会化登录类型
	 * @return 配置参数
	 */
	public OAuthConfig fetchOAuthConfig(String authType) {
		String key = this.genOAuthSettingsKey(authType);
		WoOptions options = this.optionsService.findByKey(key);
		if (options == null) {
			return new OAuthConfig();
		}

		return this.resJson.readEntity(options.getOptionValue(), new TypeReference<OAuthConfig>() {});
	}
}