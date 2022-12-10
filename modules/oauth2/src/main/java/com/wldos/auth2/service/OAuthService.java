/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wldos.auth.service.StateCodeService;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.Register;
import com.wldos.auth2.entity.WoOauthLoginUser;
import com.wldos.auth2.model.AccessTokenEntity;
import com.wldos.auth2.model.OAuthConfig;
import com.wldos.auth2.model.OAuthUser;
import com.wldos.auth2.vo.OAuthLoginParams;
import com.wldos.base.Base;
import com.wldos.base.entity.EntityAssists;
import com.wldos.common.Constants;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultJson;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.HttpUtils;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.auth.LoginUtils;
import com.wldos.support.auth.vo.JWT;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.sys.base.entity.WoOptions;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.base.service.OptionsService;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.service.MailService;
import com.wldos.sys.core.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.Lazy;
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
public class OAuthService extends Base {
	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);

	private final JWTTool jwtTool;

	@Autowired
	@Lazy
	@Qualifier("commonOperation")
	private LoginUtils loginUtils;

	@Autowired
	protected ResultJson resJson;

	private final AuthService authService;

	private final UserService userService;

	private final OAuthLoginUserService oAuthLoginUserService;

	private final StateCodeService stateCodeService;

	private final MailService mailService;

	private final OptionsService optionsService;

	public OAuthService(JWTTool jwtTool, AuthService authService, UserService userService, OAuthLoginUserService oAuthLoginUserService, StateCodeService stateCodeService, MailService mailService, OptionsService optionsService) {
		this.jwtTool = jwtTool;
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

		String settings = this.optionsService.findSettingsByKey(this.genOAuthSettingsKey(oAuthLoginParams.getAuthType()));
		if (ObjectUtils.isBlank(settings)) {
			throw new RuntimeException("社会化登录配置为空，请先配置" + this.genOAuthSettingsKey(oAuthLoginParams.getAuthType()));
		}

		OAuthConfig oAuthConfig = this.resJson.readEntity(settings, new TypeReference<OAuthConfig>() {});
		String accessTokenUri = this.extractAccessTokenUriFromConfig(oAuthConfig, oAuthLoginParams.getCode());

		String accessTokenJson = HttpUtils.sendGet(accessTokenUri);

		AccessTokenEntity tokenEntity = this.resJson.readEntity(accessTokenJson, new TypeReference<AccessTokenEntity>() {});

		String userInfoUri = this.extractUserInfoUriFromConfig(oAuthConfig, tokenEntity);
		String userInfoJson = HttpUtils.sendGet(userInfoUri);
		OAuthUser oAuthUser = this.resJson.readEntity(userInfoJson, new TypeReference<OAuthUser>() {});
		WoOauthLoginUser oAuthLoginUser = this.oAuthLoginUserService.findByUnionId(oAuthUser.getOpenId(), oAuthUser.getUnionId());
		WoUser woUser;
		if (ObjectUtils.isBlank(oAuthLoginUser)) {
			Register register = Register.of(this.nextId(), oAuthUser.getNickname(), oAuthUser.getNickname(), curUserIp);
			woUser = this.userService.createUser(domainId, register, false);
			oAuthUser.setOauthType(oAuthLoginParams.getAuthType());
			oAuthLoginUser = this.oAuthLoginUserService.createOAuthLoginUser(woUser, oAuthUser);

			String headImgUrl = oAuthUser.getHeadImgUrl();
			this.userService.uploadAvatar(request, response, headImgUrl, new int[] { 144, 144 }, woUser.getId(), curUserIp);
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

	private String extractUserInfoUriFromConfig(OAuthConfig oAuthConfig, AccessTokenEntity tokenEntity) {
		return String.format(oAuthConfig.getUserInfoUri(), tokenEntity.getAccessToken(), tokenEntity.getOpenId());
	}

	private String extractAccessTokenUriFromConfig(OAuthConfig oAuthConfig, String authorizationCode) {
		String accessTokenUri = oAuthConfig.getAccessTokenUri();

		return String.format(accessTokenUri, oAuthConfig.getAppId(), oAuthConfig.getAppSecret(), authorizationCode);
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

		String redirectSuFix = "/user/auth/login/"+oAuthType;

		if (!oAuthConfig.getRedirectUri().contains(redirectPrefix)) {
			String realRedirect = "_" + redirectPrefix + redirectSuFix;

			stateCode += URLEncoder.encode(realRedirect, StandardCharsets.UTF_8.name());
		}

		String redirectUri = URLEncoder.encode(oAuthConfig.getRedirectUri() + redirectSuFix, StandardCharsets.UTF_8.name());

		return this.resJson.ok("url", String.format(oAuthConfig.getCodeUri(), oAuthConfig.getAppId(), redirectUri,
				oAuthConfig.getScope(), stateCode));
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

		options.setValue(value);
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

		return this.resJson.readEntity(options.getValue(), new TypeReference<OAuthConfig>() {});
	}
}