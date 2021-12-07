/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.entity.AuditFields;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.auth.dto.MenuAndRoute;
import com.wldos.system.auth.vo.Login;
import com.wldos.system.auth.vo.MobileModifyParams;
import com.wldos.system.auth.vo.PasswdModifyParams;
import com.wldos.system.auth.model.AccSecurity;
import com.wldos.system.auth.model.AccountConfigKey;
import com.wldos.system.auth.service.PasswdStatusCheck;
import com.wldos.system.auth.vo.AccountInfo;
import com.wldos.system.auth.vo.Menu;
import com.wldos.system.auth.vo.Register;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.auth.vo.UserInfo;
import com.wldos.system.core.entity.WoDomain;
import com.wldos.system.core.entity.WoUsermeta;
import com.wldos.system.core.repo.OrgRepo;
import com.wldos.system.core.repo.OrgUserRepo;
import com.wldos.system.enums.ResourceEnum;
import com.wldos.system.storage.IStore;
import com.wldos.system.enums.SysOptionEnum;
import com.wldos.system.enums.UserStatusEnum;
import com.wldos.system.auth.dto.Tenant;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoOrgUser;
import com.wldos.system.core.entity.WoUser;
import com.wldos.system.core.repo.CompanyRepo;
import com.wldos.system.core.repo.UserRepo;
import com.wldos.system.vo.Domain;
import com.wldos.system.vo.User;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户相关service。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends BaseService<UserRepo, WoUser, Long> {
	private final BeanCopier domCopier = BeanCopier.create(WoDomain.class, Domain.class, false);
	private final BeanCopier regUserCopier = BeanCopier.create(Register.class, WoUser.class, false);
	@Value("${wldos.user.avatar.default}")
	private String defaultAvatar;

	private final AuthService authService;

	private final OrgRepo orgRepo;

	private final OrgUserRepo orgUserRepo;

	private final CompanyRepo companyRepo;

	private final DomainService domainService;

	private final UserMetaService userMetaService;

	private final IStore store;

	public UserService(AuthService authService, OrgRepo orgRepo, OrgUserRepo orgUserRepo, CompanyRepo companyRepo, DomainService domainService, UserMetaService userMetaService, IStore store) {
		this.authService = authService;
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.companyRepo = companyRepo;
		this.domainService = domainService;
		this.userMetaService = userMetaService;
		this.store = store;
	}

	public User queryUser(String domain, HttpServletRequest request, Long comId, Long userId) {
		User user = new User();
		UserInfo userInfo = this.queryUserInfo(userId);
		user.setUserInfo(userInfo);
		MenuAndRoute mar = this.authService.queryMenuAndRouteByUserId(domain, comId, ResourceEnum.MENU.toString(), userId);
		if (!ObjectUtil.isBlank(mar)) {
			user.setMenu(mar.getMenu());
			user.setRoute(mar.getRoute());
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(domain, comId, userId);
		user.setCurrentAuthority(currentAuthority);
		Token refreshToken = this.refreshToken(request);
		if (refreshToken != null) {
			user.setToken(refreshToken);
		}
		return user;
	}

	public List<Menu> queryAdminMenuByUser(String domain, Long comId, Long curUserId) {
		return this.authService.queryMenuByUserId(domain, comId, ResourceEnum.ADMIN_MENU.toString(), curUserId);
	}

	public User queryGuest(String domain) {
		User user = new User();
		UserInfo userInfo = this.queryUserInfo(Constants.GUEST_ID);
		user.setUserInfo(userInfo);
		MenuAndRoute mar = this.authService.queryMenuAndRouteByUserId(domain, Constants.TOP_COM_ID, ResourceEnum.MENU.toString(), Constants.GUEST_ID);
		if (!ObjectUtil.isBlank(mar)) {
			user.setMenu(mar.getMenu());
			user.setRoute(mar.getRoute());
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(domain, Constants.TOP_COM_ID, Constants.GUEST_ID);
		user.setCurrentAuthority(currentAuthority);

		return user;
	}

	public UserInfo queryUserInfo(Long userId) {
		UserInfo userInfo = new UserInfo();
		if (this.authService.isGuest(userId)) {
			return userInfo;
		}

		WoUser woUser = this.findById(userId);

		userInfo.setId(woUser.getId());
		userInfo.setUsername(woUser.getUsername());
		userInfo.setNickname(woUser.getNickname());

		userInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		userInfo.setRemark(woUser.getRemark());

		return userInfo;
	}

	public List<UserInfo> queryUsersInfo(List<Long> userIds) {

		List<UserInfo> userInfos = this.entityRepo.queryUsersInfo(userIds);

		return userInfos.parallelStream().map(item -> {
			item.setAvatar(this.getAvatarUrl(item.getAvatar()));
			return item;
		}).collect(Collectors.toList());
	}

	public AccountInfo queryAccountInfo(Long userId) {
		AccountInfo accInfo = new AccountInfo();
		if (this.authService.isGuest(userId)) {
			return accInfo;
		}

		WoUser woUser = this.findById(userId);

		accInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		BeanUtils.copyProperties(woUser, accInfo, "avatar");

		List<WoUsermeta> usermeta = this.userMetaService.findAllByUserId(userId);
		this.populateUserMeta(accInfo, usermeta);

		return accInfo;
	}

	private void populateUserMeta(AccountInfo accInfo, List<WoUsermeta> usermeta) {
		Map<String, String> metas = usermeta.parallelStream().collect(Collectors.toMap(WoUsermeta::getMetaKey, WoUsermeta::getMetaValue, (k1, k2) -> k1));
		AccSecurity ace = new AccSecurity();
		String email = metas.get(AccountConfigKey.SECURITY_KEY_BAK_EMAIL);
		if (!ObjectUtil.isBlank(email)) {
			ace.setBakEmail(email);
		}
		String pStatus = metas.get(AccountConfigKey.SECURITY_KEY_PASS_STATUS);
		if (!ObjectUtil.isBlank(pStatus)) {
			ace.setPassStatus(pStatus);
		}
		String mobile = metas.get(AccountConfigKey.SECURITY_KEY_MOBILE);
		if (!ObjectUtil.isBlank(mobile)) {
			ace.setMobile(mobile);
		}
		String quest = metas.get(AccountConfigKey.SECURITY_KEY_SEC_QUEST);
		if (!ObjectUtil.isBlank(quest)) {
			ace.setSecQuest(quest);
		}
		String mfa = metas.get(AccountConfigKey.SECURITY_KEY_MFA);
		if (!ObjectUtil.isBlank(mfa)) {
			ace.setMfa(mfa);
		}

		accInfo.setSec(ace);
	}

	public Tenant queryTenantInfoByTAdminId(Long userId) {
		Tenant tenant = this.companyRepo.queryTenantInfoByTAdminId(userId);

		if (tenant == null) {
			return this.companyRepo.tenant;
		}

		return tenant;
	}

	public String getAvatarUrl(String avatar) {
		return this.store.getFileUrl(avatar, this.store.getFileUrl(this.defaultAvatar, null));
	}

	public WoUser findByLoginName(String username) {
		return this.entityRepo.findByLoginName(username);
	}

	public boolean existsByLoginName(String username) {
		return this.entityRepo.existsByLoginName(username);
	}

	public boolean existsByEmail(String email) {
		return this.entityRepo.existsByEmail(email);
	}

	public WoUser createUser(String domain, Register register) {

		WoUser woUser = new WoUser();
		regUserCopier.copy(register, woUser, null);
		woUser.setStatus(UserStatusEnum.NORMAL.toString());

		WoDomain dom = this.domainService.queryDomainByName(domain);
		if (dom != null) {
			woUser.setDomainId(dom.getId());
		}

		long userId = register.getId();
		String userIp = register.getRegisterIp();

		WoOrgUser woOrgUser = new WoOrgUser();
		woOrgUser.setUserId(userId);
		woOrgUser.setArchId(Constants.TOP_ARCH_ID);
		woOrgUser.setComId(Constants.TOP_COM_ID);
		woOrgUser.setIsValid(ValidStatusEnum.VALID.toString());
		WoOrg org = this.entityRepo.queryOrgByDefaultRole(SysOptionEnum.DEFAULT_GROUP.toString());
		woOrgUser.setOrgId(org.getId());

		EntityAssists.beforeInsert(woUser, userId, userId, userIp, true);
		EntityAssists.beforeInsert(woOrgUser, this.nextId(), userId, userIp, true);

		this.save(woUser);
		this.orgUserRepo.save(woOrgUser);

		return woUser;
	}

	public void updateUser(PasswdModifyParams passwdModifyParams) {
		WoUser woUser = new WoUser();
		woUser.setId(passwdModifyParams.getId());
		woUser.setPasswd(passwdModifyParams.getPassword());

		this.update(woUser);
	}

	@Value("${token.access.header}")
	private String tokenHeader;

	@Value("${wldos.domain.header}")
	private String domainHeader;

	public Token refreshToken(HttpServletRequest request) {
		try {
			String domain = request.getHeader(this.domainHeader);
			String refreshToken = request.getHeader(this.tokenHeader);
			Long userId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID));
			Long tenantId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
			long expireTime = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));

			return this.jwtTool.refresh(domain, request, refreshToken, userId, tenantId, expireTime);
		} catch (NumberFormatException e) {
			getLog().error("refreshToken异常：{}", e.getMessage());
			return null;
		}
	}

	public PageableResult<WoUser> queryUserForPage(WoUser woUser, PageQuery pageQuery) {
		pageQuery.appendParam(AuditFields.DELETE_FLAG, DeleteFlagEnum.NORMAL.toString())
		.appendParam(AuditFields.IS_VALID, ValidStatusEnum.VALID.toString());

		return this.execQueryForPage(WoUser.class, WoOrgUser.class, "wo_user", "wo_org_user", "user_id", pageQuery);
	}

	public Domain findByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);
		Domain seoInfo = new Domain();
		this.domCopier.copy(woDomain, seoInfo, null);
		return seoInfo;
	}

	public String querySloganByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return ObjectUtil.string(woDomain.getSlogan());
	}

	public void accountConfig(AccSecurity sec, Long curUserId) {
	}

	public void updatePasswdStatus(String passwd, Long uid) {
		String status = PasswdStatusCheck.check(passwd);
		WoUsermeta usermeta = this.userMetaService.findByUserIdAndMetaKey(uid, AccountConfigKey.SECURITY_KEY_PASS_STATUS);
		if (usermeta == null) {
			usermeta = new WoUsermeta();
			usermeta.setId(this.nextId());
			usermeta.setUserId(uid);
			usermeta.setMetaKey(AccountConfigKey.SECURITY_KEY_PASS_STATUS);
			usermeta.setMetaValue(status);

			this.userMetaService.insertSelective(usermeta);
			return;
		}

		usermeta.setMetaValue(status);
		this.userMetaService.update(usermeta);
	}

	public Login changeMobile(MobileModifyParams mobileModifyParams) {
		Long uid = mobileModifyParams.getId();

		WoUsermeta usermeta = this.userMetaService.findByUserIdAndMetaKey(uid, AccountConfigKey.SECURITY_KEY_MOBILE);

		Login login = new Login();
		String mobile = mobileModifyParams.getMobile();
		if (usermeta == null) {
			usermeta = new WoUsermeta();
			usermeta.setId(this.nextId());
			usermeta.setUserId(uid);
			usermeta.setMetaKey(AccountConfigKey.SECURITY_KEY_MOBILE);
			usermeta.setMetaValue(mobile);

			this.userMetaService.insertSelective(usermeta);

			login.setStatus("ok");
			return login;
		}

		if (!mobileModifyParams.getOldMobile().equals(usermeta.getMetaValue())) {
			login.setStatus("error");
			login.setNews("原密保手机错误");
			return login;
		}

		usermeta.setMetaValue(mobile);
		this.userMetaService.update(usermeta);

		login.setStatus("ok");

		return login;
	}

	public WoOrg queryTenantAdminOrg() {
		return this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(),
				DeleteFlagEnum.NORMAL.toString());
	}
}
