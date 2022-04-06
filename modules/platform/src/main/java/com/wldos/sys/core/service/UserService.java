/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.wldos.base.entity.EntityAssists;
import com.wldos.common.res.PageableResult;
import com.wldos.base.entity.AuditFields;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.base.service.BaseService;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.common.Constants;
import com.wldos.sys.base.dto.MenuAndRoute;
import com.wldos.auth.vo.Login;
import com.wldos.auth.vo.MobileModifyParams;
import com.wldos.auth.vo.PasswdModifyParams;
import com.wldos.auth.model.AccSecurity;
import com.wldos.auth.model.AccountConfigKey;
import com.wldos.auth.service.PasswdStatusCheck;
import com.wldos.auth.vo.AccountInfo;
import com.wldos.sys.base.vo.Menu;
import com.wldos.auth.vo.Register;
import com.wldos.sys.base.entity.WoDomain;
import com.wldos.sys.base.service.AuthService;
import com.wldos.sys.base.service.DomainService;
import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.core.entity.WoOrgUser;
import com.wldos.sys.core.entity.WoUser;
import com.wldos.sys.core.entity.WoUsermeta;
import com.wldos.sys.base.enums.ResourceEnum;
import com.wldos.sys.base.enums.SysOptionEnum;
import com.wldos.sys.base.enums.UserStatusEnum;
import com.wldos.sys.core.repo.UserRepo;
import com.wldos.sys.core.vo.User;
import com.wldos.support.auth.vo.Token;
import com.wldos.support.auth.vo.UserInfo;
import com.wldos.sys.core.repo.OrgRepo;
import com.wldos.sys.core.repo.OrgUserRepo;
import com.wldos.sys.base.dto.Tenant;
import com.wldos.sys.base.repo.CompanyRepo;
import com.wldos.sys.base.vo.Domain;

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

	public UserService(AuthService authService, OrgRepo orgRepo, OrgUserRepo orgUserRepo, CompanyRepo companyRepo, DomainService domainService, UserMetaService userMetaService) {
		this.authService = authService;
		this.orgRepo = orgRepo;
		this.orgUserRepo = orgUserRepo;
		this.companyRepo = companyRepo;
		this.domainService = domainService;
		this.userMetaService = userMetaService;
	}

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
		UserInfo userInfo = this.queryUserInfo(userId);
		user.setUserInfo(userInfo);
		MenuAndRoute mar = this.authService.queryMenuAndRouteByUserId(domainId, comId, ResourceEnum.MENU.getValue(), userId);
		if (!ObjectUtils.isBlank(mar)) {
			user.setMenu(mar.getMenu());
			user.setRoute(mar.getRoute());
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, comId, userId);
		user.setCurrentAuthority(currentAuthority);
		Token refreshToken = this.refreshToken(request);
		if (refreshToken != null) { // 自动token续签，由于前端仅打开布局时请求一次不能保证持续续签
			user.setToken(refreshToken);
		}
		return user;
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
		return this.authService.queryMenuByUserId(domainId, comId, ResourceEnum.ADMIN_MENU.getValue(), curUserId);
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
		MenuAndRoute mar = this.authService.queryMenuAndRouteByUserId(domainId, Constants.TOP_COM_ID, ResourceEnum.MENU.getValue(), Constants.GUEST_ID);
		if (!ObjectUtils.isBlank(mar)) {
			user.setMenu(mar.getMenu());
			user.setRoute(mar.getRoute());
		}
		List<String> currentAuthority = this.authService.queryAuthorityButton(domainId, Constants.TOP_COM_ID, Constants.GUEST_ID);
		user.setCurrentAuthority(currentAuthority);

		return user;
	}

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

		userInfo.setId(woUser.getId());
		userInfo.setUsername(woUser.getUsername());
		userInfo.setNickname(woUser.getNickname());
		// 获取头像
		userInfo.setAvatar(this.getAvatarUrl(woUser.getAvatar()));
		userInfo.setRemark(woUser.getRemark());

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

		return accInfo;
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
	}

	/**
	 * 根据人员id查找所属主企业信息（租户）
	 *
	 * @param userId 用户id
	 * @return 租户信息
	 */
	public Tenant queryTenantInfoByTAdminId(Long userId) {
		Tenant tenant = this.companyRepo.queryTenantInfoByTAdminId(userId);

		if (tenant == null) { // 用户没有设置主企业，默认为平台
			return this.companyRepo.tenant;
		}

		return tenant;
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
	 * 根据登陆名查询用户实例
	 *
	 * @param username 登陆名
	 * @return 用户实例
	 */
	public WoUser findByLoginName(String username) {
		return this.entityRepo.findByLoginName(username);
	}

	public boolean existsByLoginName(String username) {
		return this.entityRepo.existsByLoginName(username);
	}

	public boolean existsByEmail(String email) {
		return this.entityRepo.existsByEmail(email);
	}

	/**
	 * 注册用户
	 * @param domainId 来源域id
	 * @param register 注册实体
	 * @return 用户实例
	 */
	public WoUser createUser(Long domainId, Register register) {
		// 注册一个用户就是创建用户，然后挂到平台注册用户默认归属组(普通会员组)上，平台会员组属于系统基础设施已经默认设置好
		WoUser woUser = new WoUser();
		regUserCopier.copy(register, woUser, null);
		woUser.setStatus(UserStatusEnum.NORMAL.toString());

		woUser.setDomainId(domainId);

		long userId = register.getId();
		String userIp = register.getRegisterIp();

		WoOrgUser woOrgUser = new WoOrgUser();
		woOrgUser.setUserId(userId);
		woOrgUser.setArchId(Constants.TOP_ARCH_ID);
		woOrgUser.setComId(Constants.TOP_COM_ID);
		woOrgUser.setIsValid(ValidStatusEnum.VALID.toString());
		WoOrg org = this.entityRepo.queryOrgByDefaultRole(SysOptionEnum.DEFAULT_GROUP.toString());
		woOrgUser.setOrgId(org.getId());
		// 初始化实体公共参数
		EntityAssists.beforeInsert(woUser, userId, userId, userIp, true);
		EntityAssists.beforeInsert(woOrgUser, this.nextId(), userId, userIp, true);

		this.save(woUser);
		this.orgUserRepo.save(woOrgUser);

		return woUser;
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

		this.update(woUser);
	}

	@Value("${token.access.header}")
	private String tokenHeader;

	@Value("${wldos.domain.header}")
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

			return this.jwtTool.refresh(domain, refreshToken, userId, tenantId, domainId, expireTime);
		} catch (NumberFormatException e) {
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
	public PageableResult<WoUser> queryUserForPage(WoUser woUser, PageQuery pageQuery) {
		pageQuery.appendParam(AuditFields.DELETE_FLAG, DeleteFlagEnum.NORMAL.toString())
		.appendParam(AuditFields.IS_VALID, ValidStatusEnum.VALID.toString()); // 注意枚举类型必须转换为String，否则jdbc模板无法自动转换，会导致查询结果为空

		return this.execQueryForPage(WoUser.class, WoOrgUser.class, "wo_user", "wo_org_user", "user_id", pageQuery);
	}

	/**
	 * 根据域名返回域的seo信息
	 *
	 * @param domain 域名
	 * @return seo信息
	 */
	public Domain findByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);
		Domain seoInfo = new Domain();
		this.domCopier.copy(woDomain, seoInfo, null);
		return seoInfo;
	}

	/**
	 * 取回请求域的slogan
	 *
	 * @return 请求域的slogan信息
	 */
	public String querySloganByDomain(String domain) {
		WoDomain woDomain = this.domainService.findByDomain(domain);

		return ObjectUtils.string(woDomain.getSlogan());
	}

	public void accountConfig(AccSecurity sec, Long curUserId) {
		// 先查询是否存在扩展属性

		// 遍历扩展属性，并按meta key判断是否已存在，已存在取id并设置新值，未存在只设置新值

		// 根据是否有id，确定对meta表展开insert还是update操作

	}

	/**
	 * 更新密码强度
	 *
	 * @param passwd 密码原文
	 * @param uid 用户id
	 */
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

	/**
	 * 修改用户密保手机
	 *
	 * @param mobileModifyParams 密保手机修改参数
	 * @return 修改结果
	 */
	public Login changeMobile(MobileModifyParams mobileModifyParams) {
		Long uid = mobileModifyParams.getId();
		// 验证原密保手机
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

		// 保存新密保手机（后期加入手机验证）
		usermeta.setMetaValue(mobile);
		this.userMetaService.update(usermeta);

		login.setStatus("ok");

		return login;
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
}
