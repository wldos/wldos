/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.platform.auth.enums.PasswdStatusEnum;
import com.wldos.platform.auth.model.AccSecurity;
import com.wldos.platform.auth.model.AccountConfigKey;
import com.wldos.platform.auth.model.ModifyParams;
import com.wldos.platform.auth.service.PasswdStatusCheck;
import com.wldos.platform.auth.vo.AccountCategoryTag;
import com.wldos.platform.auth.vo.AccountInfo;
import com.wldos.platform.auth.vo.BakEmailModifyParams;
import com.wldos.platform.auth.vo.Group;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.MFAModifyParams;
import com.wldos.platform.auth.vo.MobileModifyParams;
import com.wldos.platform.auth.vo.PasswdModifyParams;
import com.wldos.platform.auth.vo.Register;
import com.wldos.platform.auth.vo.SecQuestModifyParams;
import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.platform.support.web.enums.TemplateTypeEnum;
import io.github.wldos.framework.common.AuditFields;
import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.enums.BoolEnum;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.auth.vo.Token;
import com.wldos.platform.core.dao.*;
import com.wldos.platform.core.dao.CompanyDao;
import com.wldos.platform.core.dto.Tenant;
import com.wldos.platform.core.entity.*;
import com.wldos.platform.core.enums.SysOptionEnum;
import com.wldos.platform.core.enums.UserStatusEnum;
import com.wldos.platform.core.vo.AdminWorkOrgVO;
import com.wldos.platform.core.vo.Domain;
import com.wldos.platform.core.vo.UserAuth;
import com.wldos.platform.core.vo.UserAuthContext;
import com.wldos.platform.core.vo.UserOrgRoleVO;
import com.wldos.platform.core.vo.UserOrgVO;
import com.wldos.platform.core.vo.UserRoleSourceVO;
import io.github.wldos.common.dto.SQLTable;
import io.github.wldos.platform.support.auth.vo.UserInfo;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;
import io.github.wldos.platform.support.rbac.slot.BusinessSlot;
import io.github.wldos.platform.support.rbac.slot.SlotResolver;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import io.github.wldos.platform.support.resource.vo.DynSet;
import io.github.wldos.platform.support.resource.vo.Menu;
import io.github.wldos.platform.support.resource.vo.Route;
import io.github.wldos.framework.support.storage.vo.FileInfo;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.vo.User;
import io.github.wldos.framework.common.SaveOptions;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户相关service。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends EntityService<UserDao, WoUser, Long> {
	private final BeanCopier domCopier = BeanCopier.create(WoDomain.class, Domain.class, false);

	private final BeanCopier regUserCopier = BeanCopier.create(Register.class, WoUser.class, false);

	@Value("${wldos_user_avatar_default:}")
	private String defaultAvatar;

	@Value("${wldos.version:v1.0.0}")
	private String wldosVersion;

	private final AuthService authService;

	private final OrgDao orgRepo;

	private final OrgUserDao orgUserRepo;

	private final CompanyDao companyRepo;

	private final ComUserDao comUserRepo;

	private final DomainService domainService;

	private final UserMetaService userMetaService;

	public UserService(AuthService authService, OrgDao orgRepo, OrgUserDao orgUserRepo, CompanyDao companyRepo,
			ComUserDao comUserRepo, DomainService domainService, UserMetaService userMetaService) {
		this.authService = authService;
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.companyRepo = companyRepo;
		this.comUserRepo = comUserRepo;
		this.domainService = domainService;
		this.userMetaService = userMetaService;
	}

	@Autowired
	@Lazy
	private SlotResolver slotResolver;

	@Autowired
	@Lazy
	private OptionsService optionsService;

	@Autowired
	@Lazy
	private RoleOrgDao roleOrgRepo;

	@Autowired
	@Lazy
	private RoleDao roleDao;

	@Autowired
	@Lazy
	private RoleBindingDao roleBindingDao;

	@Autowired
	@Lazy
	private SubjectAuthenticationDao subjectAuthenticationDao;

	@Autowired
	@Lazy
	private SubjectAssociationDao subjectAssociationDao;

	@Autowired
	@Lazy
	private PlatformAdminDao platformAdminDao;

	@Autowired
	@Lazy
	private TenantAdminDao tenantAdminDao;

	@Autowired
	@Lazy
	private TrustManDao trustManDao;

	@Autowired
	@Lazy
	private DomainAdminDao domainAdminDao;

	@Autowired
	@Lazy
	private OrgOperationalKindBridge orgOperationalKindBridge;

	@Autowired
	@Lazy
	private PlatformOperatorSettingsService platformOperatorSettingsService;

	/**
	 * 查询某域下用户的授权资源信息
	 *
	 * @param domainId 请求的域id
	 * @param request 请求
	 * @param comId 用户主企业id
	 * @param userId 用户id
	 * @return 用户授权信息
	 */
	public User queryUser(Long domainId, HttpServletRequest request, Long comId, Long userId) {
		User user = new User();
		Long effectiveComId = this.resolvePortalAuthComId(userId, domainId, comId);
		if (effectiveComId == null || effectiveComId <= 0 || Objects.equals(effectiveComId, Constants.TOP_COM_ID)) {
			effectiveComId = comId;
		}
		if (userId != null && effectiveComId != null && effectiveComId > 0
				&& !Objects.equals(effectiveComId, Constants.TOP_COM_ID)) {
			WoUser persisted = this.findById(userId);
			if (persisted != null && (persisted.getMainComId() == null || persisted.getMainComId() <= 0
					|| Objects.equals(persisted.getMainComId(), Constants.TOP_COM_ID))) {
				this.syncUserMainComId(userId, effectiveComId, userId, null);
			}
		}
		UserInfo userInfo = this.queryUserInfo(userId);
		if (userInfo != null && userId != null) {
			WoUser persistedForTenant = this.findById(userId);
			if (persistedForTenant != null && persistedForTenant.getMainComId() != null
					&& persistedForTenant.getMainComId() > 0) {
				userInfo.setTenantId(persistedForTenant.getMainComId());
			}
		}
		user.setUserInfo(userInfo);
		Map<String, DynSet> dynRouteMap = this.authService.queryDynRoute(domainId);
		user.setRoute(toRouteMap(dynRouteMap));
		List<Menu> mar = this.authService.queryMenuByUserId(domainId, effectiveComId, new String[]{ResourceEnum.MENU.getValue(), ResourceEnum.PLUGIN_MENU.getValue()}, userId);
		if (!ObjectUtils.isBlank(mar)) {
			this.mergeDynSetToMenus(mar, dynRouteMap);
			user.setMenu(mar);
		}
		
		// 插件菜单通过 resourceType === 'plugin_menu' 识别，由前端动态加载
		
		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, effectiveComId, userId);
		user.setCurrentAuthority(currentAuthority);
		user.setAdmin(this.isAdmin(userId));
		user.setCanTrust(this.isCanTrust(userId));
		user.setTenantAdmin(this.commonOperate.isTenantAdmin(userId));
		Long operatorComId = this.commonOperate.getOperatorComId(userId);
		Long workComId = this.commonOperate.resolveWorkComId(userId, domainId);
		if (workComId == null || workComId <= 0 || Objects.equals(workComId, Constants.TOP_COM_ID)) {
			workComId = effectiveComId;
		}
		if (operatorComId == null || operatorComId <= 0 || Objects.equals(operatorComId, Constants.TOP_COM_ID)) {
			operatorComId = effectiveComId;
		}
		user.setWorkComId(workComId);
		user.setOperatorComId(operatorComId != null ? operatorComId : workComId);
		if (userInfo != null) {
			userInfo.setWorkComId(workComId);
			userInfo.setOperatorComId(user.getOperatorComId());
		}
		// token 续签由 EdgeHandler 统一写入响应头（token/refresh），前端 request 拦截器更新本地 accessToken；此处不再重复 setToken。
		return user;
	}

	/**
	 * 查询某域下指定用户的授权资源信息
	 *
	 * @param domainId 请求的域id
	 * @param request 请求
	 * @param comId 用户主企业id
	 * @param userId 用户id
	 * @return 用户授权信息
	 */
	public UserAuth queryUserAuth(Long domainId, HttpServletRequest request, Long comId, Long userId) {
		UserAuth user = new UserAuth();
		WoUser woUser = this.findById(userId);
		if (woUser == null) {
			return user;
		}
		UserAuthContext authContext = this.resolveUserAuthContext(woUser, domainId, comId);
		woUser.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		user.setUserInfo(woUser);
		this.applyAuthContextToUserAuth(user, authContext);
		List<Menu> mar = this.authService.queryMenuByUserId(authContext.getDomainId(), authContext.getComId(),
				new String[] { ResourceEnum.MENU.getValue(), ResourceEnum.PLUGIN_MENU.getValue() }, userId);
		if (!ObjectUtils.isBlank(mar)) {
			user.setMenu(mar);
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(authContext.getDomainId(),
				authContext.getComId(), userId);
		user.setCurrentAuthority(currentAuthority);
		return user;
	}

	/**
	 * 解析目标用户权限查询应使用的域与租户上下文。
	 */
	public UserAuthContext resolveUserAuthContext(Long userId, Long fallbackDomainId, Long fallbackComId) {
		WoUser woUser = this.findById(userId);
		return this.resolveUserAuthContext(woUser, fallbackDomainId, fallbackComId);
	}

	public UserAuthContext resolveUserAuthContext(WoUser woUser, Long fallbackDomainId, Long fallbackComId) {
		UserAuthContext ctx = new UserAuthContext();
		if (woUser == null) {
			return ctx;
		}
		Long domainId = woUser.getDomainId();
		if (domainId == null || domainId <= 0) {
			domainId = fallbackDomainId;
		}
		Long comId = woUser.getMainComId();
		if (comId == null || comId <= 0) {
			if (fallbackComId != null && fallbackComId > 0 && !Objects.equals(fallbackComId, Constants.TOP_COM_ID)) {
				comId = fallbackComId;
			}
		}
		if ((domainId == null || domainId <= 0) && comId != null && comId > 0) {
			WoDomain domain = this.domainService.findFirstByComId(comId);
			if (domain != null) {
				domainId = domain.getId();
			}
		}
		if (domainId != null && domainId > 0 && (comId == null || comId <= 0)) {
			comId = this.resolveComId(domainId);
		}
		if (domainId != null && domainId > 0) {
			Long workComId = this.commonOperate.resolveWorkComId(woUser.getId(), domainId);
			if (workComId != null && workComId > 0) {
				comId = workComId;
			}
		}
		if (comId == null || comId <= 0) {
			comId = Constants.TOP_COM_ID;
		}
		ctx.setDomainId(domainId);
		ctx.setComId(comId);
		this.fillAuthContextDisplayNames(ctx);
		return ctx;
	}

	private void fillAuthContextDisplayNames(UserAuthContext ctx) {
		Long domainId = ctx.getDomainId();
		if (domainId != null && domainId > 0) {
			WoDomain domain = this.domainService.findById(domainId);
			String domainLabel = null;
			if (domain != null) {
				domainLabel = ObjectUtils.isBlank(domain.getSiteName()) ? domain.getSiteDomain() : domain.getSiteName();
			}
			if (ObjectUtils.isBlank(domainLabel)) {
				ctx.setDomainName(null);
			}
			else {
				ctx.setDomainName(domainLabel);
			}
		}
		Long comId = ctx.getComId();
		if (comId != null && comId > 0) {
			String comLabel = this.companyRepo.findById(comId).map(c -> c.getComName()).orElse(null);
			if (ObjectUtils.isBlank(comLabel)) {
				ctx.setComName(null);
			}
			else {
				ctx.setComName(comLabel);
			}
		}
		else if (Objects.equals(comId, Constants.TOP_COM_ID)) {
			ctx.setComName("平台门户");
		}
	}

	private void applyAuthContextToUserAuth(UserAuth userAuth, UserAuthContext authContext) {
		if (userAuth == null || authContext == null) {
			return;
		}
		userAuth.setAuthDomainId(authContext.getDomainId());
		userAuth.setAuthComId(authContext.getComId());
		userAuth.setAuthDomainName(authContext.getDomainName());
		userAuth.setAuthComName(authContext.getComName());
	}

	/**
	 * 将用户迁入站点默认会员组：待激活组迁移，或业务机构挂靠时追加 FREEUSER 基线。
	 */
	public void promoteToMemberOrg(Long userId, Long domainId) {
		this.promoteToMemberOrgWithResult(userId, domainId);
	}

	/**
	 * 与 {@link #promoteToMemberOrg} 相同，返回激活链路兼容的结果码。
	 */
	public String promoteToMemberOrgWithResult(Long userId, Long domainId) {
		if (userId == null || userId <= 0) {
			return "invalid";
		}
		WoUser user = this.findById(userId);
		if (user == null) {
			return "invalid";
		}
		Long effectiveDomainId = domainId;
		if (effectiveDomainId == null || effectiveDomainId <= 0) {
			effectiveDomainId = user.getDomainId();
		}
		if (effectiveDomainId == null || effectiveDomainId <= 0) {
			Long mainComId = user.getMainComId();
			if (mainComId != null && mainComId > 0) {
				WoDomain domain = this.domainService.findFirstByComId(mainComId);
				if (domain != null) {
					effectiveDomainId = domain.getId();
				}
			}
		}
		if (effectiveDomainId == null || effectiveDomainId <= 0) {
			return "invalid";
		}
		Long comId = this.resolveComId(effectiveDomainId);
		WoOrg orgFreeuser = this.resolveSlotOrg(effectiveDomainId, BusinessSlot.ACTIVATE_MEMBER_ORG.getSlotCode());
		WoOrg siteUnActive = this.orgRepo.findByComIdAndDomainIdAndOrgCode(comId, effectiveDomainId,
				SystemOrgKind.UN_ACTIVE.getOrgCode());
		if (siteUnActive != null && siteUnActive.getId() != null) {
			WoOrgUser unActiveRow = this.orgUserRepo.queryByUserIdAndOrgId(siteUnActive.getId(), userId,
					ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
			if (unActiveRow != null) {
				return this.updateUserOrg(userId, siteUnActive.getId(), orgFreeuser.getId());
			}
		}
		WoOrgUser freeuserRow = this.orgUserRepo.queryByUserIdAndOrgId(orgFreeuser.getId(), userId,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (freeuserRow != null) {
			return "ok";
		}
		this.appendOrgUserMembership(userId, orgFreeuser, comId);
		return "ok";
	}

	/**
	 * 查询指定域、指定用户的管理菜单
	 *
	 * @param domainId 域id
	 * @param comId 用户主企业id
	 * @param curUserId 用户id
	 * @return 管理菜单列表
	 */
	public List<Menu> queryAdminMenuByUser(Long domainId, Long comId, Long curUserId) {
		return this.authService.queryMenuByUserId(domainId, comId, new String[] {ResourceEnum.ADMIN_MENU.getValue(), ResourceEnum.ADMIN_PLUGIN_MENU.getValue()}, curUserId);
	}

	/**
	 * 查询指定域、指定用户的管理端操作权限（admin_button resourceCode 列表）。
	 * 平台超管返回 {@code ["*"]}。
	 */
	public List<String> queryAdminAuthorityByUser(Long domainId, Long comId, Long userId) {
		if (this.isAdmin(userId)) {
			List<String> all = new java.util.ArrayList<>(1);
			all.add("*");
			return all;
		}
		return this.authService.queryAdminAuthorityButton(domainId, comId, userId);
	}

	/**
	 * 是否未登录或为游客。游客仅有统一身份 token，无账号实体，不提供个人中心等登录会员能力。
	 */
	public boolean isGuestUser(Long userId) {
		return userId == null || this.authService.isGuest(userId);
	}

	/**
	 * 查询游客权限
	 *
	 * @param domainId 域名id
	 * @return 游客也是用户
	 */
	public User queryGuest(Long domainId) {
		User user = new User();
		UserInfo userInfo = this.queryUserInfo(Constants.GUEST_ID);
		user.setUserInfo(userInfo);
		List<Menu> mar = this.authService.queryMenuByUserId(domainId, Constants.TOP_COM_ID, new String[] {ResourceEnum.MENU.getValue(), ResourceEnum.PLUGIN_MENU.getValue()}, Constants.GUEST_ID);
		if (!ObjectUtils.isBlank(mar)) {
			user.setMenu(mar);
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, Constants.TOP_COM_ID, Constants.GUEST_ID);
		user.setCurrentAuthority(currentAuthority);

		return user;
	}

	private final BeanCopier userCopier = BeanCopier.create(WoUser.class, UserInfo.class, false);



	/**
	 * 查询用户信息
	 *
	 * @param userId 用户id
	 * @return 用户信息
	 */
	public UserInfo queryUserInfo(Long userId) {
		UserInfo userInfo = new UserInfo();
		if (this.authService.isGuest(userId)) {
			return userInfo;
		}

		WoUser woUser = this.findById(userId);

		this.userCopier.copy(woUser, userInfo, null);
		// 获取头像
		userInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));

		return userInfo;
	}

	/**
	 * 查询一批用户的信息
	 *
	 * @param userIds 用户ids
	 * @return 用户信息结果集
	 */
	public List<UserInfo> queryUsersInfo(List<Long> userIds) {

		List<UserInfo> userInfos = this.entityRepo.queryUsersInfo(userIds);

		return userInfos.parallelStream().map(item -> {
			item.setAvatar(this.getAvatarUrl(item.getAvatar()));
			return item;
		}).collect(Collectors.toList());
	}

	/**
	 * 账户全面信息
	 *
	 * @param userId 用户id
	 * @return 账户信息
	 */
	public AccountInfo queryAccountInfo(Long userId) {
		AccountInfo accInfo = new AccountInfo();
		if (this.authService.isGuest(userId)) {
			return accInfo;
		}

		WoUser woUser = this.findById(userId);

		// 获取头像
		accInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		BeanUtils.copyProperties(woUser, accInfo, "avatar");

		// 获取辅助属性
		List<WoUsermeta> usermeta = this.userMetaService.findAllByUserId(userId);
		this.populateUserMeta(accInfo, usermeta);

		// 获取系统用户组: id, orgName, logo(群组/团队logo)
		List<Group> orgList = this.orgRepo.findAllByUserId(userId);
		orgList = orgList.stream().peek(org -> org.setOrgLogo(this.getAvatarUrl(org.getOrgLogo()))).collect(Collectors.toList());
		accInfo.setGroup(orgList);
		accInfo.setCategoryTags(this.buildAccountCategoryTags(userId, orgList));
		return accInfo;
	}

	private List<AccountCategoryTag> buildAccountCategoryTags(Long userId, List<Group> platformGroups) {
		List<AccountCategoryTag> tags = new ArrayList<>();
		if (!ObjectUtils.isBlank(platformGroups)) {
			for (Group g : platformGroups) {
				if (ObjectUtils.isBlank(g.getOrgName())) {
					continue;
				}
				AccountCategoryTag tag = new AccountCategoryTag();
				tag.setKind("membership");
				tag.setLabel(g.getOrgName());
				tag.setCode(g.getId() != null ? String.valueOf(g.getId()) : null);
				tags.add(tag);
			}
		}
		UserRoleSourceVO sources = this.queryUserRoleSources(userId);
		if (sources != null) {
			this.appendCategoryRoleTags(tags, sources.getIdentityRoles(), "identity");
			this.appendCategoryRoleTags(tags, sources.getSubjectRoles(), "subject");
			if (!ObjectUtils.isBlank(sources.getOrgRoles())) {
				for (UserOrgRoleVO org : sources.getOrgRoles()) {
					if (ObjectUtils.isBlank(org.getRoles())) {
						continue;
					}
					for (UserOrgRoleVO.RoleItem role : org.getRoles()) {
						this.appendCategoryRoleTag(tags, role, "org");
					}
				}
			}
		}
		return this.dedupeCategoryTags(tags);
	}

	private void appendCategoryRoleTags(List<AccountCategoryTag> tags, List<UserOrgRoleVO.RoleItem> roles, String kind) {
		if (ObjectUtils.isBlank(roles)) {
			return;
		}
		for (UserOrgRoleVO.RoleItem role : roles) {
			this.appendCategoryRoleTag(tags, role, kind);
		}
	}

	private void appendCategoryRoleTag(List<AccountCategoryTag> tags, UserOrgRoleVO.RoleItem role, String kind) {
		if (role == null) {
			return;
		}
		String label = !ObjectUtils.isBlank(role.getRoleName()) ? role.getRoleName() : role.getRoleCode();
		if (ObjectUtils.isBlank(label)) {
			return;
		}
		AccountCategoryTag tag = new AccountCategoryTag();
		tag.setKind(kind);
		tag.setLabel(label);
		tag.setCode(role.getRoleCode());
		tags.add(tag);
	}

	private List<AccountCategoryTag> dedupeCategoryTags(List<AccountCategoryTag> tags) {
		if (ObjectUtils.isBlank(tags)) {
			return Collections.emptyList();
		}
		LinkedHashSet<String> seenLabels = new LinkedHashSet<>();
		List<AccountCategoryTag> out = new ArrayList<>(tags.size());
		for (AccountCategoryTag tag : tags) {
			String label = tag.getLabel() != null ? tag.getLabel().trim() : "";
			if (label.isEmpty() || !seenLabels.add(label)) {
				continue;
			}
			out.add(tag);
		}
		return out;
	}

	private void populateUserMeta(AccountInfo accInfo, List<WoUsermeta> usermeta) {
		Map<String, String> metas = usermeta.parallelStream().collect(Collectors.toMap(WoUsermeta::getMetaKey, WoUsermeta::getMetaValue, (k1, k2) -> k1));
		AccSecurity ace = new AccSecurity();
		String email = metas.get(AccountConfigKey.SECURITY_KEY_BAK_EMAIL);
		if (!ObjectUtils.isBlank(email)) {
			ace.setBakEmail(email);
		}
		String pStatus = metas.get(AccountConfigKey.SECURITY_KEY_PASS_STATUS);
		if (!ObjectUtils.isBlank(pStatus)) {
			ace.setPassStatus(pStatus);
		}
		String mobile = metas.get(AccountConfigKey.SECURITY_KEY_MOBILE);
		if (!ObjectUtils.isBlank(mobile)) {
			ace.setMobile(mobile);
		}
		String quest = metas.get(AccountConfigKey.SECURITY_KEY_SEC_QUEST);
		if (!ObjectUtils.isBlank(quest)) {
			ace.setSecQuest(quest);
		}
		String mfa = metas.get(AccountConfigKey.SECURITY_KEY_MFA);
		if (!ObjectUtils.isBlank(mfa)) {
			ace.setMfa(mfa);
		}

		accInfo.setSec(ace);

		String tags = metas.get(AccountConfigKey.USER_KEY_TAGS);
		if (!ObjectUtils.isBlank(tags)) {
			try {
				List<Map<String, String>> tag = new ObjectMapper().readValue(tags, new TypeReference<List<Map<String, String>>>() {});
				accInfo.setTags(tag);
			}
			catch (JsonProcessingException e) {
				// nothing
			}
		}
	}

	/**
	 * 根据人员id查找所属主企业信息（租户）
	 *
	 * @param userId 用户id
	 * @return 租户信息
	 */
	public Tenant queryTenantInfoByTAdminId(Long userId) {
		Tenant tenant = this.companyRepo.queryTenantInfoByTAdminId(userId);
		if (tenant == null) {
			tenant = this.companyRepo.queryTenantInfoByComUserMain(userId);
			if (tenant != null) {
				this.syncUserMainComId(userId, tenant.getId(), userId, null);
			}
		}
		if (tenant == null) {
			return this.companyRepo.tenant;
		}
		return tenant;
	}

	/**
	 * 解析管理 2级 工作 com（tadmin 受托 / dadmin 绑定站）。
	 *
	 * @param userId 用户 id
	 * @param domainId 登录 Host 域 id
	 * @return 工作 com；非管理员返回 null
	 */
	public Long resolveWorkComId(Long userId, Long domainId) {
		return this.commonOperate.resolveWorkComId(userId, domainId);
	}

	/**
	 * 获取用户头像
	 *
	 * @param avatar 头像相对path
	 * @return 头像url
	 */
	public String getAvatarUrl(String avatar) {
		return this.store.getFileUrl(avatar, this.store.getFileUrl(this.defaultAvatar, null));
	}

	/**
	 * 按登录名在请求站点所属租户范围内查询可登录用户（正常或待激活）。
	 *
	 * @param username 登陆名
	 * @param requestDomainId 请求站点 wo_domain.id
	 * @return 用户实例，范围内不存在返回 null
	 */
	public WoUser findByLoginName(String username, Long requestDomainId) {
		String name = ObjectUtils.string(username).trim();
		List<Long> scope = this.resolveLoginScopeComIds(requestDomainId);
		if (ObjectUtils.isBlank(name) || scope.isEmpty()) {
			return null;
		}
		return this.entityRepo.findByLoginNameInComs(name, scope);
	}

	/**
	 * 请求站点对应的用户查找范围：站点所属租户；平台运营主站（所属租户为平台运营主体）同时覆盖平台层账号（main_com_id=0，如超管）。
	 */
	public List<Long> resolveLoginScopeComIds(Long requestDomainId) {
		if (requestDomainId == null || requestDomainId <= 0) {
			return Collections.emptyList();
		}
		Long siteComId = this.resolveComId(requestDomainId);
		List<Long> scope = new ArrayList<>(2);
		scope.add(siteComId);
		boolean primarySite = this.platformOperatorSettingsService.findPrimaryDomainId()
				.map(requestDomainId::equals).orElse(false);
		if (primarySite && !Objects.equals(siteComId, Constants.TOP_COM_ID)) {
			scope.add(Constants.TOP_COM_ID);
		}
		return scope;
	}

	/**
	 * 注册前预检用的用户归属租户：显式指定租户优先，其次显式站点所属租户，最后请求站点所属租户。
	 */
	public Long resolveRegisterScopeComId(Long requestDomainId, Register register) {
		Long fromRegister = register == null ? null : register.getComId();
		if (fromRegister != null && fromRegister > 0) {
			return fromRegister;
		}
		Long targetDomainId = register == null ? null : register.getDomainId();
		return this.resolveComId(targetDomainId != null && targetDomainId > 0 ? targetDomainId : requestDomainId);
	}

	/**
	 * 注册用的登录名：未单独填写时取邮箱。
	 */
	public String resolveRegisterLoginName(Register register) {
		if (register == null) {
			return "";
		}
		String loginName = ObjectUtils.string(register.getLoginName()).trim();
		return ObjectUtils.isBlank(loginName) ? ObjectUtils.string(register.getEmail()).trim() : loginName;
	}

	/**
	 * 唯一性检查范围：目标租户；目标租户为平台运营主体时同时覆盖平台层账号（main_com_id=0），与登录查找范围一致。
	 */
	private List<Long> resolveUniquenessScopeComIds(Long tenantComId) {
		if (!Objects.equals(tenantComId, Constants.TOP_COM_ID)
				&& this.platformOperatorSettingsService.isPlatformOperatorCom(tenantComId)) {
			return Arrays.asList(tenantComId, Constants.TOP_COM_ID);
		}
		return Collections.singletonList(tenantComId);
	}

	/**
	 * 租户内登录名是否已被占用（含已删除用户）。
	 */
	public boolean existsLoginNameInTenant(Long tenantComId, String loginName) {
		if (tenantComId == null || ObjectUtils.isBlank(loginName)) {
			return false;
		}
		return this.entityRepo.existsByComsAndLoginName(this.resolveUniquenessScopeComIds(tenantComId), loginName.trim());
	}

	/**
	 * 租户内邮箱是否已被正常用户占用。
	 */
	public boolean existsNormalEmailInTenant(Long tenantComId, String email) {
		if (tenantComId == null || ObjectUtils.isBlank(email)) {
			return false;
		}
		return this.entityRepo.existsNormalByComsAndEmail(this.resolveUniquenessScopeComIds(tenantComId), email.trim());
	}

	/**
	 * 按推荐码查询用户（用于推荐码校验）
	 *
	 * @param recommendCode 推荐码
	 * @return 用户，不存在或无效则 empty
	 */
	public java.util.Optional<WoUser> findByRecommendCode(String recommendCode) {
		if (recommendCode == null || recommendCode.trim().isEmpty()) {
			return java.util.Optional.empty();
		}
		return this.entityRepo.findByRecommendCode(recommendCode.trim());
	}

	/**
	 * 按邮箱在请求站点所属租户范围内查未删除用户。
	 */
	public List<WoUser> listNormalByEmail(String email, Long requestDomainId) {
		String normalized = ObjectUtils.string(email).trim();
		List<Long> scope = this.resolveLoginScopeComIds(requestDomainId);
		if (ObjectUtils.isBlank(normalized) || scope.isEmpty()) {
			return Collections.emptyList();
		}
		return this.entityRepo.listNormalByEmailInComs(normalized, scope);
	}

	/**
	 * 注册用户
	 * @param domainId 来源域id
	 * @param register 注册实体
	 * @param isEmailAction 是否开启邮箱激活
	 * @return 用户实例
	 */
	public WoUser createUser(Long domainId, Register register, boolean isEmailAction) {
		return this.createUser(domainId, register, isEmailAction, null, null);
	}

	public WoUser createUser(Long domainId, Register register, boolean isEmailAction, Long creatorUserId, Long tenantId) {
		// 注册一个用户就是创建用户，然后挂到平台注册用户默认归属组(普通会员组)上，平台会员组属于系统基础设施已经默认设置好
		WoUser woUser = new WoUser();
		regUserCopier.copy(register, woUser, null);
		this.applyRegisterLoginName(woUser, register);
		woUser.setStatus(this.normalizeRegisterUserStatus(register.getStatus(), isEmailAction));
		boolean usePendingMemberOrg = UserStatusEnum.notActive.getValue().equals(woUser.getStatus());

		Long effectiveDomainId = domainId;
		if (register.getOrgId() != null && register.getOrgId() > 0) {
			effectiveDomainId = this.resolveRegisterDomainId(register, domainId);
		}
		woUser.setDomainId(effectiveDomainId);

		long userId = register.getId();
		// String userIp = register.getRegisterIp();

		WoOrgUser woOrgUser = new WoOrgUser();
		woOrgUser.setUserId(userId);
		woUser.setId(userId);
		woOrgUser.setArchId(Constants.TOP_ARCH_ID);
		woOrgUser.setComId(Constants.TOP_COM_ID);
		woOrgUser.setIsValid(ValidStatusEnum.VALID.toString());
		if (register.getOrgId() != null && register.getOrgId() > 0) {
			WoOrg targetOrg = this.orgRepo.findById(register.getOrgId()).orElse(null);
			if (targetOrg != null && effectiveDomainId != null && effectiveDomainId > 0) {
				Long scopeComId = this.resolveMemberComIdForRegister(register, targetOrg, tenantId);
				targetOrg = this.orgOperationalKindBridge.resolveOperationalInstance(targetOrg, effectiveDomainId, scopeComId);
			}
			woOrgUser.setOrgId(targetOrg != null ? targetOrg.getId() : register.getOrgId());
			woOrgUser.setComId(this.resolveMemberComIdForRegister(register, targetOrg, tenantId));
			if (targetOrg != null && targetOrg.getArchId() != null) {
				woOrgUser.setArchId(targetOrg.getArchId());
			}
			else if (register.getArchId() != null) {
				woOrgUser.setArchId(register.getArchId());
			}
		}
		else if (creatorUserId != null) {
			// 调用方（管理端「新增用户」）显式指定了目标站点：必须落到该站点的会员分群实例，
			// 不采用「跟创建人挂同一编制组织」的旧捷径——那是给「同组织内添加下属」场景用的，
			// 对超管代管跨租户/跨站点新增会员没有意义（超管自身编制归属与新用户目标站点无关）。
			boolean explicitTargetDomain = register.getDomainId() != null && register.getDomainId() > 0;
			if (usePendingMemberOrg) {
				WoOrg org = this.resolveSiteOrg(effectiveDomainId, true);
				woOrgUser.setOrgId(org.getId());
				woOrgUser.setComId(org.getComId() != null ? org.getComId() : this.resolveComId(effectiveDomainId));
				woOrgUser.setArchId(org.getArchId() != null ? org.getArchId() : woOrgUser.getComId());
			}
			else if (explicitTargetDomain) {
				WoOrg org = this.resolveSiteOrg(effectiveDomainId, false);
				woOrgUser.setOrgId(org.getId());
				woOrgUser.setComId(org.getComId() != null ? org.getComId() : this.resolveComId(effectiveDomainId));
				woOrgUser.setArchId(org.getArchId() != null ? org.getArchId() : woOrgUser.getComId());
			}
			else {
				Long primaryOrgId = this.commonOperate.getPrimaryOrgId(creatorUserId);
				if (primaryOrgId != null) {
					woOrgUser.setOrgId(primaryOrgId);
					woOrgUser.setComId(tenantId != null ? tenantId : Constants.TOP_COM_ID);
				}
				else {
					WoOrg org = this.resolveSiteOrg(effectiveDomainId, false);
					woOrgUser.setOrgId(org.getId());
					woOrgUser.setComId(org.getComId() != null ? org.getComId() : this.resolveComId(effectiveDomainId));
					woOrgUser.setArchId(org.getArchId() != null ? org.getArchId() : woOrgUser.getComId());
				}
			}
		}
		else {
			WoOrg org = this.resolveSiteOrg(effectiveDomainId, usePendingMemberOrg);
			woOrgUser.setOrgId(org.getId());
			woOrgUser.setComId(org.getComId() != null ? org.getComId() : this.resolveComId(effectiveDomainId));
			woOrgUser.setArchId(org.getArchId() != null ? org.getArchId() : woOrgUser.getComId());
		}

		this.normalizeRegisterOrgUserCom(woOrgUser, effectiveDomainId);
		Long primaryComId = woOrgUser.getComId();
		if (primaryComId == null || primaryComId <= 0 || Objects.equals(primaryComId, Constants.TOP_COM_ID)) {
			primaryComId = this.resolveComId(effectiveDomainId);
			if (primaryComId != null && primaryComId > 0 && !Objects.equals(primaryComId, Constants.TOP_COM_ID)) {
				woOrgUser.setComId(primaryComId);
			}
		}
		boolean tenantUser = primaryComId != null && primaryComId > 0
				&& !Objects.equals(primaryComId, Constants.TOP_COM_ID);
		// 主企业须在首次落库前确定：(main_com_id, login_name) 唯一索引以它划分租户范围，平台层账号为 0
		Long tenantComId = tenantUser ? primaryComId : Constants.TOP_COM_ID;
		woUser.setMainComId(tenantComId);
		this.assertRegisterIdentityAvailable(tenantComId, woUser.getLoginName(), woUser.getEmail());

		this.saveOrUpdate(woUser);
		this.orgUserRepo.save(woOrgUser);
		this.ensureFreeuserBaselineIfNeeded(userId, effectiveDomainId, woOrgUser.getOrgId(), woOrgUser.getComId(),
				woUser.getStatus());

		return woUser;
	}

	private void assertRegisterIdentityAvailable(Long tenantComId, String loginName, String email) {
		if (this.existsLoginNameInTenant(tenantComId, loginName)) {
			throw new BaseException("登录名已存在", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (this.existsNormalEmailInTenant(tenantComId, email)) {
			throw new BaseException("注册邮箱已存在", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private void normalizeRegisterOrgUserCom(WoOrgUser woOrgUser, Long domainId) {
		if (woOrgUser == null || domainId == null || domainId <= 0) {
			return;
		}
		Long comId = woOrgUser.getComId();
		if (comId != null && comId > 0 && !Objects.equals(comId, Constants.TOP_COM_ID)) {
			return;
		}
		Long siteCom = this.resolveComId(domainId);
		if (siteCom == null || siteCom <= 0 || Objects.equals(siteCom, Constants.TOP_COM_ID)) {
			return;
		}
		woOrgUser.setComId(siteCom);
		if (woOrgUser.getArchId() == null || woOrgUser.getArchId() <= 0
				|| Objects.equals(woOrgUser.getArchId(), Constants.TOP_COM_ID)) {
			woOrgUser.setArchId(siteCom);
		}
	}

	private Long resolveMemberComIdForRegister(Register register, WoOrg targetOrg, Long tenantId) {
		Long fromRegister = register.getComId();
		if (fromRegister != null && fromRegister > 0 && !Objects.equals(fromRegister, Constants.TOP_COM_ID)) {
			return fromRegister;
		}
		if (targetOrg != null && targetOrg.getComId() != null && targetOrg.getComId() > 0
				&& !Objects.equals(targetOrg.getComId(), Constants.TOP_COM_ID)) {
			return targetOrg.getComId();
		}
		if (targetOrg != null && targetOrg.getDomainId() != null && targetOrg.getDomainId() > 0) {
			Long fromDomain = this.resolveComId(targetOrg.getDomainId());
			if (fromDomain != null && fromDomain > 0 && !Objects.equals(fromDomain, Constants.TOP_COM_ID)) {
				return fromDomain;
			}
		}
		if (tenantId != null && tenantId > 0) {
			return tenantId;
		}
		return Constants.TOP_COM_ID;
	}

	private void applyRegisterLoginName(WoUser woUser, Register register) {
		String loginName = ObjectUtils.isBlank(register.getLoginName())
				? ObjectUtils.string(register.getEmail()).trim()
				: register.getLoginName().trim();
		if (!ObjectUtils.isBlank(loginName)) {
			woUser.setLoginName(loginName);
		}
	}

	/**
	 * 根据密码修改参数更新用户密码
	 *
	 * @param passwdModifyParams 密码修改参数
	 */
	public void updateUser(PasswdModifyParams passwdModifyParams) {
		WoUser woUser = new WoUser();
		woUser.setId(passwdModifyParams.getId());
		woUser.setPasswd(passwdModifyParams.getPassword());

		this.saveOrUpdate(woUser);
	}

	/**
	 * 按站点解析默认用户组（注册/激活用）。
	 *
	 * @param domainId 站点 id
	 * @param userGroup {@link SysOptionEnum#DEFAULT_GROUP} 或 {@link SysOptionEnum#UN_ACTIVE_GROUP} 的 optionKey
	 * @return 本站组织实例
	 */
	public WoOrg queryUserOrg(Long domainId, String userGroup) {
		String slotCode = SysOptionEnum.UN_ACTIVE_GROUP.getKey().equals(userGroup)
				? BusinessSlot.REGISTER_PENDING_ORG.getSlotCode()
				: BusinessSlot.REGISTER_DEFAULT_ORG.getSlotCode();
		return this.resolveSlotOrg(domainId, slotCode);
	}

	private Long resolveComId(Long domainId) {
		if (domainId == null || domainId <= 0) {
			return Constants.TOP_COM_ID;
		}
		WoDomain domain = this.domainService.findById(domainId);
		if (domain == null || domain.getComId() == null) {
			return Constants.TOP_COM_ID;
		}
		return domain.getComId();
	}

	/**
	 * 门户 JWT / 网关鉴权用的站点租户 com（排除平台 TOP_COM_ID）。
	 */
	public Long resolveSiteComIdByDomain(Long domainId) {
		Long comId = this.resolveComId(domainId);
		if (comId != null && comId > 0 && !Objects.equals(comId, Constants.TOP_COM_ID)) {
			return comId;
		}
		return null;
	}

	/**
	 * 有效组织成员关系上的 com_id（与 wo_auth_role 租户过滤对齐用）。
	 */
	public Long resolvePrimaryMembershipComId(Long userId) {
		if (userId == null) {
			return null;
		}
		WoOrgUser row = this.orgUserRepo.findFirstValidMembershipByUserId(userId);
		if (row == null || row.getComId() == null || row.getComId() <= 0
				|| Objects.equals(row.getComId(), Constants.TOP_COM_ID)) {
			return null;
		}
		return row.getComId();
	}

	/**
	 * 网关鉴权 com：JWT 已为站点租户则沿用；否则回退 main_com、成员 com、访问域站点 com。
	 */
	public Long resolvePortalAuthComId(Long userId, Long domainId, Long jwtComId) {
		if (jwtComId != null && jwtComId > 0 && !Objects.equals(jwtComId, Constants.TOP_COM_ID)) {
			return jwtComId;
		}
		WoUser user = userId != null ? this.findById(userId) : null;
		if (user != null && user.getMainComId() != null && user.getMainComId() > 0
				&& !Objects.equals(user.getMainComId(), Constants.TOP_COM_ID)) {
			return user.getMainComId();
		}
		Long membershipCom = this.resolvePrimaryMembershipComId(userId);
		if (membershipCom != null) {
			return membershipCom;
		}
		return this.resolveSiteComIdByDomain(domainId);
	}

	/**
	 * 门户 JWT 的 home/work com：优先 main_com_id、请求域租户、成员 com、站点 com，
	 * 避免 OAuth 等链路误用 TOP_COM_ID 导致网关鉴权 403 与菜单为空。
	 *
	 * @return {@code [homeComId, workComId]}
	 */
	public Long[] resolvePortalTokenComIds(Long userId, Long domainId, Long contextComId) {
		WoUser user = userId != null ? this.findById(userId) : null;
		Long homeComId = null;
		if (user != null && user.getMainComId() != null && user.getMainComId() > 0
				&& !Objects.equals(user.getMainComId(), Constants.TOP_COM_ID)) {
			homeComId = user.getMainComId();
		}
		if (homeComId == null && contextComId != null && contextComId > 0
				&& !Objects.equals(contextComId, Constants.TOP_COM_ID)) {
			homeComId = contextComId;
		}
		if (homeComId == null) {
			Tenant tenant = this.queryTenantInfoByTAdminId(userId);
			if (tenant != null && tenant.getId() != null && tenant.getId() > 0
					&& !Objects.equals(tenant.getId(), Constants.TOP_COM_ID)) {
				homeComId = tenant.getId();
			}
		}
		if (homeComId == null || homeComId <= 0 || Objects.equals(homeComId, Constants.TOP_COM_ID)) {
			Long membershipCom = this.resolvePrimaryMembershipComId(userId);
			if (membershipCom != null) {
				homeComId = membershipCom;
			}
		}
		if (homeComId == null || homeComId <= 0 || Objects.equals(homeComId, Constants.TOP_COM_ID)) {
			Long siteComId = this.resolveSiteComIdByDomain(domainId);
			if (siteComId != null) {
				homeComId = siteComId;
			}
		}
		if (homeComId == null || homeComId <= 0) {
			homeComId = Constants.TOP_COM_ID;
		}
		Long workComId = this.resolveWorkComId(userId, domainId);
		if (workComId == null || workComId <= 0) {
			workComId = homeComId;
		}
		return new Long[] { homeComId, workComId };
	}

	/**
	 * 组织节点建用户时解析注册站点：显式 domainId → 组织绑站 → 同租户会话域 → 单站兜底；多站歧义则拒绝。
	 */
	private Long resolveRegisterDomainId(Register register, Long operatorDomainId) {
		WoOrg targetOrg = null;
		if (register.getOrgId() != null && register.getOrgId() > 0) {
			targetOrg = this.orgRepo.findById(register.getOrgId()).orElse(null);
		}
		Long targetComId = register.getComId();
		if ((targetComId == null || targetComId <= 0) && targetOrg != null) {
			targetComId = targetOrg.getComId();
		}
		if (register.getDomainId() != null && register.getDomainId() > 0) {
			this.assertDomainBelongsToTenant(register.getDomainId(), targetComId);
			return register.getDomainId();
		}
		if (targetOrg != null && targetOrg.getDomainId() != null && targetOrg.getDomainId() > 0) {
			return targetOrg.getDomainId();
		}
		if (operatorDomainId != null && operatorDomainId > 0 && targetComId != null && targetComId > 0) {
			Long operatorComId = this.resolveComId(operatorDomainId);
			if (Objects.equals(operatorComId, targetComId)) {
				return operatorDomainId;
			}
		}
		if (targetComId != null && targetComId > 0) {
			int siteCount = this.domainService.countValidByComId(targetComId);
			if (siteCount == 1) {
				WoDomain soleSite = this.domainService.findFirstByComId(targetComId);
				if (soleSite != null) {
					return soleSite.getId();
				}
			}
			if (siteCount > 1) {
				throw new BaseException("请选择目标站点");
			}
		}
		if (targetComId == null || targetComId <= 0 || Objects.equals(targetComId, Constants.TOP_COM_ID)) {
			return operatorDomainId;
		}
		throw new BaseException("请选择目标站点");
	}

	private void assertDomainBelongsToTenant(Long domainId, Long targetComId) {
		if (domainId == null || domainId <= 0) {
			return;
		}
		WoDomain domain = this.domainService.findById(domainId);
		if (domain == null) {
			throw new BaseException("目标站点不存在");
		}
		if (targetComId != null && targetComId > 0
				&& domain.getComId() != null
				&& !Objects.equals(domain.getComId(), targetComId)) {
			throw new BaseException("目标站点与组织归属租户不一致");
		}
	}

	private WoOrg resolveSiteOrg(Long domainId, boolean unActive) {
		String slotCode = unActive
				? BusinessSlot.REGISTER_PENDING_ORG.getSlotCode()
				: BusinessSlot.REGISTER_DEFAULT_ORG.getSlotCode();
		WoOrg org = this.resolveSlotOrg(domainId, slotCode);
		this.assertMemberOrgMatchesRegisterLifecycle(org, unActive);
		return org;
	}

	private String normalizeRegisterUserStatus(String rawStatus, boolean emailActivation) {
		if (emailActivation) {
			return UserStatusEnum.notActive.getValue();
		}
		if (ObjectUtils.isBlank(rawStatus)) {
			return UserStatusEnum.normal.getValue();
		}
		String trimmed = rawStatus.trim();
		for (UserStatusEnum item : UserStatusEnum.values()) {
			if (item.getValue().equals(trimmed) || item.getLabel().equals(trimmed)) {
				return item.getValue();
			}
		}
		return trimmed;
	}

	private void assertMemberOrgMatchesRegisterLifecycle(WoOrg org, boolean pending) {
		if (org == null || ObjectUtils.isBlank(org.getOrgCode())) {
			return;
		}
		SystemOrgKind expected = pending ? SystemOrgKind.UN_ACTIVE : SystemOrgKind.FREEUSER;
		if (expected.getOrgCode().equals(org.getOrgCode())) {
			return;
		}
		String slotHint = pending ? BusinessSlot.REGISTER_PENDING_ORG.getSlotCode()
				: BusinessSlot.REGISTER_DEFAULT_ORG.getSlotCode();
		throw new BaseException(
				(pending ? "待激活用户须进入待激活分群（un_active），" : "正常用户须进入普通会员分群（freeuser），")
						+ "当前插槽 " + slotHint + " 解析到 org_code=" + org.getOrgCode()
						+ "，请检查 插槽 绑定或站点 组织类型 实例",
				ResultCode.VALIDATION_ERROR.getCode());
	}

	/** 插槽解析分群实例：绑定优先，缺省按插槽平台默认角色解析本站实例 */
	private WoOrg resolveSlotOrg(Long domainId, String slotCode) {
		Long comId = this.resolveComId(domainId);
		if (comId == null || comId <= 0 || Objects.equals(comId, Constants.TOP_COM_ID)) {
			// 平台门户站（catalog 轴）不承载会员分群实例，出厂即不做 provision；
			// 会员相关插槽（待激活/普通会员）只在具体租户站点上才有实例。
			throw new BaseException("平台门户站不支持会员用户，请选择具体租户站点", ResultCode.VALIDATION_ERROR.getCode());
		}
		Long orgId = this.slotResolver.resolveOrg(slotCode, comId, domainId);
		if (orgId == null) {
			throw new BaseException("站点组织实例未就绪: " + slotCode);
		}
		return this.orgRepo.findById(orgId)
				.orElseThrow(() -> new BaseException("站点组织实例不存在: " + slotCode));
	}

	/**
	 * 更新用户所在组织
	 *
	 * @param userId 用户id
	 * @param orgIdOld 原组织
	 * @param orgIdNew 新组织
	 * @return String 无效用户或已激活过：“invalid”，激活成功："ok"
	 */
	public String updateUserOrg(Long userId, Long orgIdOld, Long orgIdNew) {
		WoOrgUser orgUser = this.orgUserRepo.queryByUserIdAndOrgId(orgIdOld, userId, ValidStatusEnum.VALID.toString(),
				DeleteFlagEnum.NORMAL.toString());
		if (orgUser == null)
			return "invalid";
		orgUser.setOrgId(orgIdNew);
		WoOrg newOrg = this.orgRepo.findById(orgIdNew).orElse(null);
		if (newOrg != null) {
			Long domainId = newOrg.getDomainId() != null && newOrg.getDomainId() > 0
					? newOrg.getDomainId()
					: null;
			Long comId = newOrg.getComId();
			if (comId == null || comId <= 0 || Objects.equals(comId, Constants.TOP_COM_ID)) {
				comId = domainId != null ? this.resolveComId(domainId) : orgUser.getComId();
			}
			if (comId != null && comId > 0 && !Objects.equals(comId, Constants.TOP_COM_ID)) {
				orgUser.setComId(comId);
				orgUser.setArchId(newOrg.getArchId() != null ? newOrg.getArchId() : comId);
			}
		}
		this.commonOperate.dynamicUpdateByEntity(orgUser, true);
		if (orgUser.getComId() != null && orgUser.getComId() > 0
				&& !Objects.equals(orgUser.getComId(), Constants.TOP_COM_ID)) {
			this.syncUserMainComId(userId, orgUser.getComId(), userId, null);
		}
		return "ok";
	}

	private void ensureFreeuserBaselineIfNeeded(Long userId, Long domainId, Long primaryOrgId, Long comId,
			String userStatus) {
		if (UserStatusEnum.notActive.getValue().equals(userStatus)) {
			return;
		}
		if (primaryOrgId == null || primaryOrgId <= 0 || domainId == null || domainId <= 0) {
			return;
		}
		WoOrg primaryOrg = this.orgRepo.findById(primaryOrgId).orElse(null);
		if (primaryOrg == null) {
			return;
		}
		SystemOrgKind kind = SystemOrgKind.ofOrgCode(primaryOrg.getOrgCode());
		if (kind == SystemOrgKind.FREEUSER || kind == SystemOrgKind.UN_ACTIVE) {
			return;
		}
		WoOrg freeuserOrg = this.resolveSlotOrg(domainId, BusinessSlot.REGISTER_DEFAULT_ORG.getSlotCode());
		WoOrgUser existing = this.orgUserRepo.queryByUserIdAndOrgId(freeuserOrg.getId(), userId,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (existing != null) {
			return;
		}
		Long effectiveComId = comId != null && comId > 0 ? comId : this.resolveComId(domainId);
		this.appendOrgUserMembership(userId, freeuserOrg, effectiveComId);
	}

	private void appendOrgUserMembership(Long userId, WoOrg org, Long comId) {
		if (org != null && (org.getDomainId() == null || org.getDomainId() <= 0)) {
			SystemOrgKind kind = SystemOrgKind.ofOrgCode(org.getOrgCode());
			if (kind != null && OrgOperationalKindBridge.isSiteBootstrapKind(kind)) {
				throw new BaseException("禁止向平台内置分群模板写入成员");
			}
		}
		WoOrgUser woOrgUser = new WoOrgUser();
		woOrgUser.setUserId(userId);
		woOrgUser.setOrgId(org.getId());
		woOrgUser.setArchId(org.getArchId() != null ? org.getArchId() : comId);
		woOrgUser.setComId(org.getComId() != null ? org.getComId() : comId);
		woOrgUser.setIsValid(ValidStatusEnum.VALID.toString());
		this.orgUserRepo.save(woOrgUser);
	}

	@Value("${token.access.header:"+Constants.TOKEN_ACCESS_HEADER+"}")
	private String tokenHeader;

	@Value("${wldos.domain.header:"+Constants.WLDOS_DOMAIN_HEADER+"}")
	private String domainHeader;

	/**
	 * 刷新指定域、指定token
	 *
	 * @param request 请求
	 * @return 刷新token，如果token已超过刷新期限，返回null
	 */
	public Token refreshToken(HttpServletRequest request) {
		try {
			String domain = request.getHeader(this.domainHeader);
			String refreshToken = request.getHeader(this.tokenHeader);
			Long userId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID));
			Long tenantId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_TENANT));
			Long domainId = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_DOMAIN));
			long expireTime = Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME));
			int refresh = Integer.parseInt(request.getHeader(Constants.CONTEXT_KEY_TOKEN_REFRESH));

			return this.jwtTool.refresh(domain, refreshToken, userId, tenantId, domainId, expireTime, refresh);
		}
		catch (NumberFormatException e) {
			getLog().error("refreshToken异常：{}", e.getMessage());
			return null;
		}
	}

	/**
	 * 查询用户分页，支持查询、过滤、排序
	 *
	 * @param woUser 用户
	 * @param pageQuery 分页参数
	 * @return 用户分页数据
	 */
	public PageData<WoUser> queryUserForPage(WoUser woUser, PageQuery pageQuery) {
		pageQuery.appendParam(AuditFields.DELETE_FLAG, DeleteFlagEnum.NORMAL.toString())
				.appendParam(AuditFields.IS_VALID, ValidStatusEnum.VALID.toString()); // 注意枚举类型必须转换为String，否则jdbc模板无法自动转换，会导致查询结果为空

		return this.execQueryForPage(WoUser.class, WoOrgUser.class, "wo_user", "wo_org_user", "user_id", pageQuery);
	}

	/**
	 * 指定租户下的租户管理员分页（数据来自 {@code wo_tenant_admin} 登记册，与站点 {@code wo_domain_admin} 列表同模式）。
	 */
	public PageData<WoUser> listTenantAdminsForCompany(Long comId, PageQuery pageQuery) {
		if (comId == null || comId <= 0) {
			throw new BaseException("须指定租户", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (pageQuery.getCondition() != null) {
			pageQuery.getCondition().remove("userComId");
			pageQuery.getCondition().remove("uComId");
			pageQuery.getCondition().remove("orgId");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select u.* from wo_tenant_admin ta ");
		sql.append("inner join wo_user u on ta.user_id = u.id ");
		sql.append("where ta.com_id = ? and ta.delete_flag = 'normal' and ta.is_valid = '1' ");
		List<Object> params = new ArrayList<>();
		params.add(comId);
		return this.commonOperate.execQueryForPage(
				WoUser.class, sql.toString(), params, pageQuery, SQLTable.of(WoUser.class, "u"));
	}

	/**
	 * 查询用户分页（用户管理全局列表专用），在 {@link #queryUserForPage} 基础上附带用户的主公司/主组织名称，
	 * 供列表展示"公司""组织"两列；仍只返回已加入至少一个组织的用户，与旧方法过滤语义一致。
	 * 主公司取 {@code wo_user.main_com_id}，主组织取 {@code wo_org_user} 最早一条有效记录。
	 *
	 * @param pageQuery 分页参数
	 * @return 用户（含公司/组织）分页数据
	 */
	/**
	 * 用户归属组织及每个组织授予的角色（权限查看「归属与角色」页签）。
	 * 角色经组织挂接到人（group-based RBAC），本视图同时是「基于人维护」的入口数据源。
	 */
	public List<UserOrgRoleVO> queryUserOrgRoles(Long userId) {
		List<WoOrg> orgs = this.orgRepo.queryByUserId(userId);
		if (ObjectUtils.isBlank(orgs)) {
			return Collections.emptyList();
		}
		List<Long> orgIds = orgs.stream().map(WoOrg::getId).collect(Collectors.toList());
		List<WoRoleOrg> roleOrgs = this.roleOrgRepo.queryRoleByOrgIds(orgIds);
		List<Long> roleIds = roleOrgs.stream().map(WoRoleOrg::getRoleId).distinct().collect(Collectors.toList());
		Map<Long, WoRole> roleById = new HashMap<>();
		if (!roleIds.isEmpty()) {
			this.roleDao.findAllById(roleIds).forEach(role -> roleById.put(role.getId(), role));
		}
		Map<Long, List<UserOrgRoleVO.RoleItem>> rolesByOrg = new HashMap<>();
		for (WoRoleOrg roleOrg : roleOrgs) {
			WoRole role = roleById.get(roleOrg.getRoleId());
			if (role == null) {
				continue;
			}
			UserOrgRoleVO.RoleItem item = new UserOrgRoleVO.RoleItem();
			item.setRoleId(role.getId());
			item.setRoleName(role.getRoleName());
			item.setRoleCode(role.getRoleCode());
			rolesByOrg.computeIfAbsent(roleOrg.getOrgId(), key -> new ArrayList<>()).add(item);
		}
		List<UserOrgRoleVO> result = new ArrayList<>(orgs.size());
		for (WoOrg org : orgs) {
			UserOrgRoleVO vo = new UserOrgRoleVO();
			vo.setOrgId(org.getId());
			vo.setOrgName(org.getOrgName());
			vo.setOrgCode(org.getOrgCode());
			vo.setArchId(org.getArchId());
			vo.setComId(org.getComId());
			vo.setRoles(rolesByOrg.getOrDefault(org.getId(), Collections.emptyList()));
			result.add(vo);
		}
		return result;
	}

	/**
	 * 用户角色四来源视图（权限查看「角色来源」）：组织角色 + 直挂角色 + 主体角色 + 身份角色。
	 * 身份角色按身份登记册判定所属身份组，再取身份组上挂接的角色。
	 */
	public UserRoleSourceVO queryUserRoleSources(Long userId) {
		UserRoleSourceVO vo = new UserRoleSourceVO();
		vo.setOrgRoles(this.queryUserOrgRoles(userId));

		List<Long> directRoleIds = this.roleBindingDao.findValidByTarget(WoRoleBinding.TARGET_USER, userId).stream()
				.map(WoRoleBinding::getRoleId).distinct().collect(Collectors.toList());
		vo.setDirectRoles(this.toRoleItems(directRoleIds));

		List<Long> subjectTypeIds = this.subjectAuthenticationDao.findValidByUserId(userId).stream()
				.map(WoSubjectAuthentication::getSubjectTypeId).distinct().collect(Collectors.toList());
		List<Long> subjectRoleIds = subjectTypeIds.isEmpty()
				? Collections.emptyList()
				: this.subjectAssociationDao.findValidBySubjectTypeIds(subjectTypeIds).stream()
						.map(WoSubjectAssociation::getRoleId).distinct().collect(Collectors.toList());
		vo.setSubjectRoles(this.toRoleItems(subjectRoleIds));

		List<String> identityCodes = new ArrayList<>(4);
		if (this.platformAdminDao.findByUserId(userId) != null) {
			identityCodes.add(Constants.AdminOrgCode);
		}
		if (this.tenantAdminDao.findByUserId(userId) != null) {
			identityCodes.add(Constants.TAdminOrgCode);
		}
		if (this.trustManDao.findByUserId(userId) != null) {
			identityCodes.add(Constants.CanTrustOrgCode);
		}
		if (!ObjectUtils.isBlank(this.domainAdminDao.findByUserId(userId))) {
			identityCodes.add(Constants.DomainAdminOrgCode);
		}
		List<Long> identityRoleIds = Collections.emptyList();
		if (!identityCodes.isEmpty()) {
			List<Long> identityOrgIds = this.orgRepo.findByOrgCodes(identityCodes).stream()
					.map(WoOrg::getId).collect(Collectors.toList());
			if (!identityOrgIds.isEmpty()) {
				identityRoleIds = this.roleOrgRepo.queryRoleByOrgIds(identityOrgIds).stream()
						.map(WoRoleOrg::getRoleId).distinct().collect(Collectors.toList());
			}
		}
		vo.setIdentityRoles(this.toRoleItems(identityRoleIds));
		return vo;
	}

	private List<UserOrgRoleVO.RoleItem> toRoleItems(List<Long> roleIds) {
		if (ObjectUtils.isBlank(roleIds)) {
			return Collections.emptyList();
		}
		List<UserOrgRoleVO.RoleItem> items = new ArrayList<>(roleIds.size());
		this.roleDao.findAllById(roleIds).forEach(role -> {
			UserOrgRoleVO.RoleItem item = new UserOrgRoleVO.RoleItem();
			item.setRoleId(role.getId());
			item.setRoleName(role.getRoleName());
			item.setRoleCode(role.getRoleCode());
			items.add(item);
		});
		return items;
	}

	public PageData<UserOrgVO> queryUserWithOrgForPage(PageQuery pageQuery) {
		pageQuery.appendParam(AuditFields.DELETE_FLAG, DeleteFlagEnum.NORMAL.toString());
		boolean domainScoped = pageQuery.getCondition() != null
				&& pageQuery.getCondition().containsKey(Constants.COMMON_KEY_DOMAIN_COLUMN);
		StringBuilder sql = new StringBuilder();
		sql.append("select a.*, (select ou.org_id from wo_org_user ou where ou.user_id = a.id and ou.delete_flag = 'normal' ")
			.append(" and ou.is_valid = '1' order by ou.id limit 1) as org_id, (select o.org_name from wo_org_user ou join wo_org o on ou.org_id = o.id ")
			.append(" where ou.user_id = a.id and ou.delete_flag = 'normal' and ou.is_valid = '1' order by ou.id limit 1) as org_name, ")
			.append(" (select group_concat(o.org_name order by ou.id separator '、') from wo_org_user ou join wo_org o on ou.org_id = o.id ")
			.append(" where ou.user_id = a.id and ou.delete_flag = 'normal' and ou.is_valid = '1') as orgNames, ")
			.append(" case when a.main_com_id is null or a.main_com_id = 0 then '平台' ")
			.append(" when c.com_name is not null and c.com_name <> '' then c.com_name ")
			.append(" else concat('公司 ID ', a.main_com_id) end as com_name, ")
			.append(" a.main_com_id as com_id, ")
			.append(" case when d.site_name is not null and d.site_name <> '' then d.site_name ")
			.append(" when d.site_domain is not null and d.site_domain <> '' then d.site_domain ")
			.append(" else null end as domain_name ")
			.append(" from wo_user a ")
			.append(" left join wo_company c on c.id = a.main_com_id and c.delete_flag = 'normal' and c.is_valid = '1' ")
			.append(" left join wo_domain d on d.id = a.domain_id and d.delete_flag = 'normal' ")
			.append(" where a.delete_flag = 'normal' ");
		if (!domainScoped) {
			sql.append(" and exists (select 1 from wo_org_user b where b.user_id = a.id and b.delete_flag = 'normal' and b.is_valid = '1')");
		}
		return this.commonOperate.execQueryForPage(UserOrgVO.class, sql.toString(), pageQuery, SQLTable.of(WoUser.class, "a"));
	}

	/**
	 * 根据域名返回域的seo信息
	 *
	 * @param domain 域名
	 * @return seo信息
	 */
	public Domain findByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);
		if (woDomain == null) {
			return null;
		}
		Domain seoInfo = new Domain();
		this.domCopier.copy(woDomain, seoInfo, null);
		if (this.optionsService != null) {
			this.optionsService.applyPortalFooter(seoInfo, woDomain.getId());
		}
		seoInfo.setVersion(this.wldosVersion);
		return seoInfo;
	}

	/**
	 * 取回请求域的slogan
	 *
	 * @return 请求域的slogan信息
	 */
	public String querySloganByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);
		if (woDomain == null) {
			return "";
		}
		return ObjectUtils.string(woDomain.getSlogan());
	}

	/**
	 * 根据域id返回域的url
	 *
	 * @param domainId 域id
	 * @return seo信息
	 */
	public String getDomainUrlById(Long domainId) {
		WoDomain woDomain = this.domainService.findById(domainId);
		return woDomain.getSiteUrl();
	}

	public void accountConfig(AccSecurity sec, Long curUserId) {
		// 先查询是否存在扩展属性

		// 遍历扩展属性，并按meta key判断是否已存在，已存在取id并设置新值，未存在只设置新值

		// 根据是否有id，确定对meta表展开insert还是update操作

	}

	/**
	 * 用户标签配置
	 *
	 * @param tags 标签json串
	 * @param curUserId 当前用户id
	 */
	public void tagsConfig(String tags, Long curUserId) {
		this.userMetaConfig(tags, AccountConfigKey.USER_KEY_TAGS, curUserId);
	}

	/**
	 * 更新密码强度
	 *
	 * @param passwd 密码原文
	 * @param uid 用户id
	 */
	public void updatePasswdStatus(String passwd, Long uid) {
		PasswdStatusEnum status = PasswdStatusCheck.check(passwd);
		this.userMetaConfig(status.getLevel(), AccountConfigKey.SECURITY_KEY_PASS_STATUS, uid);
	}

	private void userMetaConfig(String metaValue, String metaKey, Long curUserId) {
		WoUsermeta usermeta = this.userMetaService.findByUserIdAndMetaKey(curUserId, metaKey);
		if (usermeta == null) {
			this.userMetaAdd(metaValue, metaKey, curUserId);
			return;
		}

		this.userMetaModify(usermeta, metaValue);
	}

	/**
	 * 修改用户密保手机
	 *
	 * @param params 密保手机修改参数
	 * @return 修改结果
	 */
	public Login changeMobile(MobileModifyParams params) {
		Long uid = params.getId();

		return this.handleModifyParams(params, uid, AccountConfigKey.SECURITY_KEY_MOBILE, "原密保手机错误");
	}

	/**
	 * 修改用户密保问题
	 *
	 * @param params 密保问题修改参数
	 * @return 修改结果
	 */
	public Login changeSecQuest(SecQuestModifyParams params) {
		Long uid = params.getId();

		return this.handleModifyParams(params, uid, AccountConfigKey.SECURITY_KEY_SEC_QUEST, "原密保问题错误");
	}

	/**
	 * 修改用户备用邮箱
	 *
	 * @param params 备用邮箱修改参数
	 * @return 修改结果
	 */
	public Login changeBakEmail(BakEmailModifyParams params) {
		Long uid = params.getId();

		return this.handleModifyParams(params, uid, AccountConfigKey.SECURITY_KEY_BAK_EMAIL, "原备用邮箱错误");
	}

	/**
	 * 修改用户MFA设备
	 *
	 * @param params MFA设备修改
	 * @return 修改结果
	 */
	public Login changeMFA(MFAModifyParams params) {
		Long uid = params.getId();

		return this.handleModifyParams(params, uid, AccountConfigKey.SECURITY_KEY_MFA, "原MFA校验失败");
	}

	private Login handleModifyParams(ModifyParams params, Long uid, String metaKey, String errorInfo) {
		// 获取存档参数值
		WoUsermeta usermeta = this.userMetaService.findByUserIdAndMetaKey(uid, metaKey);

		Login login = new Login();
		String secQuest = params.getNew();
		if (usermeta == null) {
			this.userMetaAdd(secQuest, metaKey, uid);

			login.setStatus("ok");
			return login;
		}
		// 验证原参数值
		if (!params.getOld().equals(usermeta.getMetaValue())) {
			login.setStatus("error");
			login.setNews(errorInfo);
			return login;
		}

		// 保存新参数值
		this.userMetaModify(usermeta, secQuest);

		login.setStatus("ok");

		return login;
	}

	private void userMetaAdd(String metaValue, String metaKey, Long curUserId) {
		WoUsermeta usermeta = new WoUsermeta();
		usermeta.setId(this.nextId());
		usermeta.setUserId(curUserId);
		usermeta.setMetaKey(metaKey);
		usermeta.setMetaValue(metaValue);

		this.userMetaService.saveOrUpdate(usermeta, SaveOptions.forImport());
	}

	private void userMetaModify(WoUsermeta usermeta, String metaValue) {

		usermeta.setMetaValue(metaValue);
		this.userMetaService.saveOrUpdate(usermeta, SaveOptions.forImport());
	}

	/**
	 * 查询租户管理员用户组信息
	 *
	 * @return 租户管理组
	 */
	public WoOrg queryTenantAdminOrg() {
		return this.orgRepo.findByOrgCodeAndIsValidAndDeleteFlag(Constants.TAdminOrgCode, ValidStatusEnum.VALID.toString(),
				DeleteFlagEnum.NORMAL.toString());
	}

	/**
	 * 更新用户头像及其信息
	 *
	 * @param request 请求
	 * @param response 响应
	 * @param file 头像文件
	 * @param avatarSize 头像像素
	 * @param userId 用户id
	 * @param userIp 用户操作ip
	 * @throws IOException io异常
	 */
	public void uploadAvatar(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] avatarSize, Long userId,
			String userIp) throws IOException {
		// 调用文件存储服务, 头像尺寸一般规定144x144px
		FileInfo fileInfo = this.store.storeFile(request, response, file, avatarSize);

		WoUser user = new WoUser();
		user.setId(userId);
		user.setAvatar(fileInfo.getPath());
		this.saveOrUpdate(user);
	}

	/**
	 * 下载用户头像上传并更新其信息
	 *
	 * @param request 请求
	 * @param response 响应
	 * @param avatarUrl 头像下载url
	 * @param avatarSize 头像像素
	 * @param userId 用户id
	 * @param userIp 用户操作ip
	 * @throws IOException io异常
	 */
	public void uploadAvatar(HttpServletRequest request, HttpServletResponse response, String avatarUrl, int[] avatarSize, Long userId,
			String userIp) throws IOException {
		// 调用文件存储服务, 头像尺寸一般规定144x144px
		FileInfo fileInfo = this.store.storeFile(request, response, avatarUrl, avatarSize);

		WoUser user = new WoUser();
		user.setId(userId);
		user.setAvatar(fileInfo.getPath());
		this.saveOrUpdate(user);
	}

	public String checkRoute(String route, Long userId, Long domainId, Long tenantId, HttpServletRequest request) {
		return this.authService.authorityRouteCheck(route, userId, domainId, tenantId, request);
	}

	/**
	 * 查询分域动态路由配置
	 *
	 * @param domainId 域名id
	 * @return 动态路由配置
	 */
	public Map<String, DynSet> queryDynRoute(Long domainId) {
		return this.authService.queryDynRoute(domainId);
	}

	/**
	 * 将 DynSet 按 path 合并到菜单树中
	 *
	 * @param dynRouteMap 动态路由配置映射
	 */
	private static Map<String, Route> toRouteMap(Map<String, DynSet> dynRouteMap) {
		if (dynRouteMap == null || dynRouteMap.isEmpty()) {
			return new HashMap<>(0);
		}
		Map<String, Route> routes = new HashMap<>(dynRouteMap.size() * 2);
		for (Map.Entry<String, DynSet> entry : dynRouteMap.entrySet()) {
			DynSet dynSet = entry.getValue();
			if (dynSet == null) {
				continue;
			}
			Route route = new Route();
			route.setModule(dynSet.getModule());
			route.setCategory(dynSet.getCategory());
			route.setUrl(dynSet.getUrl());
			route.setExtraProps(dynSet.getExtraProps());
			routes.put(entry.getKey(), route);
		}
		return routes;
	}

	private void mergeDynSetToMenus(List<Menu> menus, Map<String, DynSet> dynRouteMap) {
		if (menus == null || dynRouteMap == null || dynRouteMap.isEmpty()) {
			return;
		}

		for (Menu menu : menus) {
			String path = menu.getPath();
			if (path != null && dynRouteMap.containsKey(path)) {
				DynSet dynSet = dynRouteMap.get(path);

				// 如果url不为空并且module类型为component，则设置到 component
				if (dynSet.getUrl() != null && !dynSet.getUrl().trim().isEmpty()
					&& TemplateTypeEnum.COMPONENT.getValue().equals(dynSet.getModule())) {
					menu.setComponent(dynSet.getUrl());
				}
			}

			// 递归处理子菜单
			if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
				this.mergeDynSetToMenus(menu.getChildren(), dynRouteMap);
			}
		}
	}

	public List<AdminWorkOrgVO> listAdminWorkOrgs(Long userId) {
		if (userId == null) {
			return java.util.Collections.emptyList();
		}
		String sql = "select ou.org_id, o.org_name, ou.arch_id, a.arch_name "
				+ "from wo_org_user ou join wo_org o on ou.org_id = o.id "
				+ "left join wo_architecture a on ou.arch_id = a.id "
				+ "where ou.user_id = ? and ou.delete_flag = 'normal' and ou.is_valid = '1' "
				+ "order by ou.id";
		List<Map<String, Object>> rows = this.commonOperate.getJdbcOperations().queryForList(sql, userId);
		Long currentOrgId = this.commonOperate.getWorkingOrgId(userId);
		if (currentOrgId == null) {
			currentOrgId = this.commonOperate.getPrimaryOrgId(userId);
		}
		List<AdminWorkOrgVO> result = new java.util.ArrayList<>();
		for (Map<String, Object> row : rows) {
			AdminWorkOrgVO vo = new AdminWorkOrgVO();
			Long orgId = Long.parseLong(ObjectUtils.string(row.get("org_id")));
			vo.setOrgId(orgId);
			vo.setOrgName(ObjectUtils.string(row.get("org_name")));
			Object archId = row.get("arch_id");
			if (archId != null) {
				vo.setArchId(Long.parseLong(ObjectUtils.string(archId)));
			}
			vo.setArchName(ObjectUtils.string(row.get("arch_name")));
			vo.setCurrent(currentOrgId != null && currentOrgId.equals(orgId));
			result.add(vo);
		}
		return result;
	}

	public void setAdminWorkingOrg(Long userId, Long orgId) {
		if (userId == null || orgId == null) {
			return;
		}
		String checkSql = "select 1 from wo_org_user where user_id=? and org_id=? and delete_flag='normal' and is_valid='1' limit 1";
		List<Map<String, Object>> exists = this.commonOperate.getJdbcOperations().queryForList(checkSql, userId, orgId);
		if (ObjectUtils.isBlank(exists)) {
			throw new BaseException("无权切换到该组织");
		}
		this.userMetaConfig(String.valueOf(orgId), AccountConfigKey.ADMIN_WORKING_ORG_ID, userId);
	}

	/**
	 * 设置用户主组织：只原地迁移该用户"主组织"这一条 {@code wo_org_user} 记录（最早一条有效记录）的 {@code org_id}，
	 * 并同步该记录的 {@code archId}/{@code comId} 为新组织的对应值；用户还兼职的其它组织记录不受影响。
	 * 用户当前没有任何组织记录时，新增一条主组织记录。
	 *
	 * @param userId 用户id
	 * @param newOrgId 新组织id
	 */
	@Transactional
	public void setUserPrimaryOrg(Long userId, Long newOrgId, Long operatorId, String operatorIp) {
		WoOrg newOrg = this.orgRepo.findById(newOrgId)
				.orElseThrow(() -> new BaseException("组织不存在"));
		Long oldOrgId = this.commonOperate.getPrimaryOrgId(userId);
		if (oldOrgId == null) {
			WoOrgUser row = new WoOrgUser();
			row.setId(this.nextId());
			row.setUserId(userId);
			row.setOrgId(newOrgId);
			row.setArchId(newOrg.getArchId());
			row.setComId(newOrg.getComId());
			row.setIsValid(ValidStatusEnum.VALID.toString());
			row.setCreateBy(operatorId);
			row.setCreateIp(operatorIp);
			row.setUpdateBy(operatorId);
			row.setUpdateIp(operatorIp);
			this.orgUserRepo.save(row);
			return;
		}
		WoOrgUser row = this.orgUserRepo.queryByUserIdAndOrgId(oldOrgId, userId,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (row == null) {
			return;
		}
		row.setOrgId(newOrgId);
		row.setArchId(newOrg.getArchId());
		row.setComId(newOrg.getComId());
		row.setUpdateBy(operatorId);
		row.setUpdateIp(operatorIp);
		this.commonOperate.dynamicUpdateByEntity(row, true);
	}

	/**
	 * 设置用户主企业：把用户与目标公司的关联标记为主企业（isMain=YES），其余关联同步标记为非主；
	 * 用户与目标公司尚无关联记录时新增一条。
	 *
	 * @param userId 用户id
	 * @param newComId 新主企业公司id
	 */
	@Transactional
	public void setUserPrimaryCompany(Long userId, Long newComId, Long operatorId, String operatorIp) {
		List<WoComUser> rows = this.comUserRepo.queryAllByUserIds(Collections.singletonList(userId),
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		WoComUser target = null;
		for (WoComUser row : rows) {
			if (newComId.equals(row.getComId())) {
				target = row;
				continue;
			}
			if (BoolEnum.YES.toString().equals(row.getIsMain())) {
				row.setIsMain(BoolEnum.NO.toString());
				row.setUpdateBy(operatorId);
				row.setUpdateIp(operatorIp);
				this.comUserRepo.save(row);
			}
		}
		if (target != null) {
			if (!BoolEnum.YES.toString().equals(target.getIsMain())) {
				target.setIsMain(BoolEnum.YES.toString());
				target.setUpdateBy(operatorId);
				target.setUpdateIp(operatorIp);
				this.comUserRepo.save(target);
			}
			this.syncUserMainComId(userId, newComId, operatorId, operatorIp);
			return;
		}
		WoComUser row = new WoComUser();
		row.setUserId(userId);
		row.setComId(newComId);
		row.setIsMain(BoolEnum.YES.toString());
		row.setIsValid(ValidStatusEnum.VALID.toString());
		row.setCreateBy(operatorId);
		row.setCreateIp(operatorIp);
		row.setUpdateBy(operatorId);
		row.setUpdateIp(operatorIp);
		this.comUserRepo.save(row);
		this.syncUserMainComId(userId, newComId, operatorId, operatorIp);
	}

	public void syncUserMainComId(Long userId, Long mainComId, Long operatorId, String operatorIp) {
		if (userId == null || mainComId == null) {
			return;
		}
		WoUser user = this.findById(userId);
		if (user != null) {
			user.setMainComId(mainComId);
			user.setUpdateBy(operatorId);
			user.setUpdateIp(operatorIp);
			this.saveOrUpdate(user);
			io.github.wldos.platform.support.rbac.EffectiveScopeCache.invalidate(this.cache, userId);
		}
	}

}
