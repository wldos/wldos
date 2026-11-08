/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.internal.Base;
import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.ResultJson;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import io.github.wldos.platform.support.rbac.EffectiveScope;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import io.github.wldos.platform.support.rbac.DataScopeEntityAssert;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.framework.support.storage.StoredFileDownloadSupport;
import com.wldos.framework.support.web.annotation.NotResponseBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 抽象基础 controller，不允许直接继承。
 *
 * <p><b>继承 {@link Base} 的平台能力</b>（均为 {@code protected}，在子类中用 {@code this.xxx} 访问；定义见 SDK 源码或依赖 Sources）：</p>
 * <ul>
 *   <li>存储：{@link io.github.wldos.framework.support.internal.Base#store}{@code （}{@link io.github.wldos.framework.support.storage.IStore}{@code ，可选注入）}，
 *       如 {@code this.store.storeFileWithDigest(...)}、{@code this.store.getPublicUrl(...)}、
 *       {@link #downloadFile(String, String)} / {@link com.wldos.framework.support.storage.StoredFileDownloadSupport} 等（详见 {@link io.github.wldos.framework.support.storage.IStore}）。</li>
 *   <li>缓存 / Hook / JWT：{@link io.github.wldos.framework.support.internal.Base#cache}、{@link io.github.wldos.framework.support.internal.Base#wsHook}、{@link io.github.wldos.framework.support.internal.Base#jwtTool}</li>
 *   <li>主键 / Bean 解析：{@link io.github.wldos.framework.support.internal.Base#IDGen}、{@link io.github.wldos.framework.support.internal.Base#beanHelper}</li>
 *   <li>Redis / JDBC：{@link io.github.wldos.framework.support.internal.Base#stringRedisTemplate}、{@link io.github.wldos.framework.support.internal.Base#jdbcAggTemplate}、{@link io.github.wldos.framework.support.internal.Base#namedParamJdbcTemplate}</li>
 *   <li>运行与环境：{@link io.github.wldos.framework.support.internal.Base#webRoot}（{@code @PostConstruct} 初始化）、多租户/多域等配置字段见 {@link Base}</li>
 * </ul>
 *
 * <p><b>本层（Controller）额外注入</b>：{@link #service}、{@link #request}、{@link #response}、{@link #resJson}、{@link #commonOperate}。</p>
 *
 * @author Yuanxi Universe
 * @date 2021/5/5
 * @version 1.0
 * @see Base
 */
public abstract class NonEntityController<S extends NonEntityService> extends Base {

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	protected S service;

	/** 如果service层需要request，可以传给service，不要在service直接获取*/
	@Autowired
	protected HttpServletRequest request;

	/** 如果service层需要response，可以传给service，不要在service直接获取 */
	@Autowired
	protected HttpServletResponse response;

	@Autowired
	protected ResultJson resJson;

	/**
	 * 通用的jdbc和业务操作
	 */
	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	/**
	 * 获取请求用户id
	 */
	protected Long getUserId() {
		return this.commonOperate.getUserId();
	}

	/**
	 * 获取请求用户token
	 */
	protected String getToken() {
		return this.commonOperate.getToken();
	}

	/**
	 * 获取当前用户IP
	 */
	protected String getUserIp() {
		return this.commonOperate.getUserIp();
	}

	/** 是否多租户模式 */
	public boolean isMultiTenancy() {
		return this.commonOperate.isMultiTenancy();
	}

	/**
	 * 获取用户主企业id(租户)
	 */
	protected Long getTenantId() {
		return this.commonOperate.getTenantId();
	}

	/** 管理 2级 工作租户 id（workComId）。 */
	protected Long getWorkComId() {
		return this.commonOperate.getWorkComId();
	}

	/**
	 * 获取平台根域名
	 */
	protected String getPlatDomain() {
		return this.commonOperate.getPlatDomain();
	}
	/** 是否多站模式 */
	public boolean isMultiDomain() {
		return this.commonOperate.isMultiDomain();
	}
	/**
	 * 获取用户当前访问的域名
	 */
	protected String getDomain() {
		return this.commonOperate.getDomain();
	}

	/**
	 * 获取用户当前访问域名的id
	 *
	 * @return 域id
	 */
	protected Long getDomainId() {
		return this.commonOperate.getDomainId();
	}

	/**
	 * 针对分页查询应用域隔离
	 *
	 * @param pageQuery 分页查询参数
	 */
	protected void applyDomainFilter(PageQuery pageQuery) {
		this.commonOperate.applyDomainFilter(pageQuery);
	}

	/**
	 * 实体表分页查询应用租户隔离
	 *
	 * @param pageQuery 分页查询
	 */
	protected void applyTenantFilter(PageQuery pageQuery) {
		this.commonOperate.applyTenantFilter(pageQuery);
	}

	/** 门户/用户端 CMS：仅请求站点 domainId。 */
	protected void applyPortalDomainScope(PageQuery pageQuery) {
		this.commonOperate.applyPortalDomainScope(pageQuery);
	}

	/**
	 * 按 API Profile 注入数据范围。
	 *
	 * @param pageQuery 分页查询
	 * @param profile 数据范围配置
	 * @param entityClass 实体类
	 */
	protected void applyScope(PageQuery pageQuery, DataScopeProfile profile,
			Class<?> entityClass) {
		this.commonOperate.applyScope(pageQuery, profile, entityClass);
	}

	/**
	 * 审计日志分页：超管按请求 {@code comId}/{@code domainId} lens；其它管理员 {@link DataScopeProfile#AUDIT_DOMAIN}，
	 * 租户管理员可选 {@code domainId} 收窄站点；站点管理员仅当前登录站点。
	 */
	protected void applyAuditLogListScope(PageQuery pageQuery, Class<?> entityClass) {
		if (pageQuery == null) {
			return;
		}
		Long comId = pullQueryLong(pageQuery, "comId");
		Long domainId = pullQueryLong(pageQuery, "domainId");
		Long userId = this.getUserId();
		if (this.isAdmin(userId)) {
			DataScopeQueryHelper.applyAuditAdminLens(pageQuery, comId, domainId);
			return;
		}
		this.applyScope(pageQuery, DataScopeProfile.AUDIT_DOMAIN, entityClass);
		if (this.commonOperate.isTenantAdmin(userId) && domainId != null && domainId > 0) {
			pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);
		}
	}

	/**
	 * 租户管理员系统日志：替换 AUDIT_DOMAIN 的精确 {@code com_id} 为租户并集 lens（网关事件常 {@code com_id=0}）。
	 */
	protected void applySysLogTenantAdminUnionScope(PageQuery pageQuery, Long optionalDomainId) {
		if (pageQuery == null) {
			return;
		}
		Long userId = this.getUserId();
		if (userId == null || this.isAdmin(userId) || !this.commonOperate.isTenantAdmin(userId)) {
			return;
		}
		EffectiveScope effective = this.commonOperate.getEffectiveScope(userId);
		Long tenantCom = effective != null && effective.getComId() != null && effective.getComId() > 0
				? effective.getComId()
				: this.getTenantId();
		if (tenantCom == null || tenantCom <= 0) {
			return;
		}
		DataScopeQueryHelper.applySysLogTenantAdminRead(pageQuery, tenantCom);
		if (optionalDomainId != null && optionalDomainId > 0) {
			pageQuery.removeParam(Constants.COMMON_KEY_DOMAIN_COLUMN);
			pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, optionalDomainId);
		}
	}

	private static Long pullQueryLong(PageQuery pageQuery, String key) {
		if (pageQuery == null || key == null) {
			return null;
		}
		Object raw = pageQuery.getCondition().remove(key);
		if (raw == null || raw.toString().trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(raw.toString().trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	/** 保存前按操作者层级补默认 com/domain。 */
	protected void applyWriteScope(Object entity, WriteScopeTarget target) {
		this.commonOperate.applyWriteScope(entity, target);
	}

	/**
	 * 站点管理员不得访问租户级组织 API（基座定案）。
	 */
	protected void assertNotSiteAdminForTenantOrg() {
		this.assertNotSiteAdminForTenantScope("组织管理");
	}

	/**
	 * 站点管理员不得访问体系结构管理 API（基座定案，与组织管理同档）。
	 */
	protected void assertNotSiteAdminForArch() {
		this.assertNotSiteAdminForTenantScope("体系结构管理");
	}

	/**
	 * 站点管理员不得访问租户级应用定义写路径（应用为 TENANT_SCOPED，站点只读挂载，不可管理定义本体）。
	 */
	protected void assertNotSiteAdminForApp() {
		this.assertNotSiteAdminForTenantScope("应用定义管理");
	}

	/**
	 * 站点管理员不得访问的租户级管理场景（组织/体系/应用等）的统一拒绝入口。
	 */
	private void assertNotSiteAdminForTenantScope(String featureName) {
		Long userId = this.getUserId();
		if (userId == null || this.isAdmin(userId)) {
			return;
		}
		if (this.commonOperate.isDomainAdmin(userId) && !this.commonOperate.isTenantAdmin(userId)) {
			throw new BaseException("站点管理员无权访问" + featureName);
		}
	}

	/**
	 * 断言访问域名是否在用户所属域名范围内。
	 *
	 * @param domainId 域id
	 */	
	protected void assertBoundDomainAccess(Long domainId) {
		this.commonOperate.assertBoundDomainAccess(this.getUserId(), domainId);
	}

	/**
	 * 断言实体行级数据权限。
	 *
	 * @param entity 实体
	 */
	protected void assertEntityRowScope(Object entity) {
		if (entity == null || this.isAdmin(this.getUserId())) {
			return;
		}
		Long userId = this.getUserId();
		java.util.List<Long> orgRoots = this.commonOperate.getEffectiveOrgScopeRoots(userId);
		EffectiveScope effective =
				this.commonOperate.getEffectiveScope(userId);
		// 租户级操作者不按「当前访问域」卡 domain 轴：其范围覆盖租户全部站点，由 com/mainCom 轴兜底
		boolean skipDomainAxis = this.commonOperate.isTenantAdmin(userId);
		Long scopeComId = this.getWorkComId();
		if (scopeComId == null) {
			scopeComId = this.getTenantId();
		}
		DataScopeEntityAssert.assertRow(
				entity,
				userId,
				false,
				scopeComId,
				this.getDomainId(),
				this.commonOperate.listBoundDomainIds(userId),
				this.isMultiTenancy(),
				orgRoots,
				this.commonOperate::isOrgWithinSubtree,
				skipDomainAxis);
		if (effective != null) {
			DataScopeEntityAssert.assertEffectiveDimensions(
					entity,
					effective,
					this.commonOperate::isOrgWithinSubtree);
		}
	}

	/**
	 * token超期时间
	 *
	 * @return expireTime
	 */
	protected long getTokenExpTime() {
		return this.commonOperate.getTokenExpTime();
	}

	/**
	 * 安全起见，实时查询当前用户是否超级管理员
	 *
	 * @param userId 用户id
	 * @return 是否管理员
	 */
	public boolean isAdmin(Long userId) {
		return this.commonOperate.isAdmin(userId);
	}

	/**
	 * 实时查询当前用户是否可信者
	 *
	 * @param userId 用户id
	 * @return 是否可信者
	 */
	public boolean isCanTrust(Long userId) {
		return this.commonOperate.isCanTrust(userId);
	}

	/**
	 * 将 {@link io.github.wldos.framework.support.internal.Base#store} 中已存文件写出到 {@link #response}（附件下载）。
	 * <p>请使用 {@code void} 返回值；框架对 {@code void} 方法不启用全局响应包装，一般无需再标 {@link NotResponseBody}。</p>
	 *
	 * @param logicalPath      逻辑路径（与 {@code IStore} 一致）
	 * @param downloadFileName 下载文件名；{@code null} 或空则从路径末段推断
	 */
	protected void downloadFile(String logicalPath, String downloadFileName) throws IOException {
		this.downloadFile(logicalPath, downloadFileName, false);
	}

	/**
	 * 同 {@link #downloadFile(String, String)}，可选浏览器内联预览（{@code Content-Disposition: inline}）。
	 */
	protected void downloadFile(String logicalPath, String downloadFileName, boolean inline) throws IOException {
		StoredFileDownloadSupport.writeToResponse(this.store, this.response, logicalPath, downloadFileName, inline);
	}
}