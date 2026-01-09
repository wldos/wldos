/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import com.wldos.common.res.PageableResult;
import com.wldos.framework.common.AuditFields;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.DomainAppDao;
import com.wldos.platform.core.dao.ResourceDao;
import com.wldos.platform.core.entity.WoApp;
import com.wldos.platform.core.entity.WoDomainApp;
import com.wldos.platform.core.enums.AppOriginEnum;
import com.wldos.platform.core.enums.AppTypeEnum;
import com.wldos.platform.support.plugins.IPluginDataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用管理service。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppService extends EntityService<AppDao, WoApp, Long> {

	@Autowired(required = false)
	@Lazy
	private IPluginDataService pluginDataService;

	@Autowired(required = false)
	private DomainResourceDao domainResourceDao;

	@Autowired(required = false)
	private AuthRoleDao authRoleDao;

	@Autowired(required = false)
	private DomainAppDao domainAppDao;

	@Autowired(required = false)
	private ResourceDao resourceDao;

	@Value("${wldos.plugins.debug:false}")
	private boolean wldosPluginsDebug;

	/**
	 * 查询应用和子表域预订应用关联
	 *
	 * @param pageQuery 分页参数和查询条件
	 * @return 应用分页数据
	 */
	public PageableResult<WoApp> queryAppForPage(PageQuery pageQuery) {
		pageQuery.appendParam(AuditFields.DELETE_FLAG, DeleteFlagEnum.NORMAL.toString())
				.appendParam(AuditFields.IS_VALID, ValidStatusEnum.VALID.toString()); // 注意枚举类型必须转换为String，否则jdbc模板无法自动转换，会导致查询结果为空

		return this.execQueryForPage(WoApp.class, WoDomainApp.class, "wo_app", "wo_domain_app", "app_id", pageQuery);
	}

    /**
     * 获取或创建插件对应的系统应用。
     * appCode 规范：纯英文字母（a-z），不使用数字
     */
    public WoApp getOrCreatePluginApp(String pluginCode, String pluginName) {
        // 生成5位短码（纯英文字母），避免与现有冲突
        String appCode = null;
        for (int i = 0; i < 10; i++) {
            String candidate = generateShortCode(5);
            if (this.entityRepo.findByAppCode(candidate) == null) { appCode = candidate; break; }
        }
        if (appCode == null) {
            // 退化保证：基于时间戳生成纯英文字母编码
            appCode = generateFallbackCode(System.currentTimeMillis());
        }
        WoApp exist = this.entityRepo.findByAppCode(appCode);
        if (exist != null) return exist; // 极小概率并发再查
        WoApp app = new WoApp();
        app.setAppCode(appCode);
        app.setAppName(pluginName != null && pluginName.trim().length() > 0 ? pluginName : pluginCode);
        // 默认范围走公共应用（APP），来源标记为插件市场
        app.setAppType(AppTypeEnum.APP.getValue());
        app.setAppOrigin(AppOriginEnum.PLUGIN.getValue());
        app.setIsValid(ValidStatusEnum.VALID.toString());
        app.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
        // saveOrUpdate 会自动填充 ID，直接返回保存后的实体
        return this.saveOrUpdate(app);
    }

    /**
     * 带来源参数的版本：按来源标记创建系统应用。
     */
    public WoApp getOrCreatePluginApp(String pluginCode, String pluginName, AppOriginEnum origin) {
        WoApp app = getOrCreatePluginApp(pluginCode, pluginName);
        if (origin != null && (app.getAppOrigin() == null || !app.getAppOrigin().equals(origin.getValue()))) {
            app.setAppOrigin(origin.getValue());
            this.saveOrUpdate(app);
        }
        return app;
    }

    /**
     * 生成指定位数的短码（纯英文字母，a-z）。
     */
    private String generateShortCode(int len) {
        final String alph = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(len);
        java.util.concurrent.ThreadLocalRandom r = java.util.concurrent.ThreadLocalRandom.current();
        for (int i = 0; i < len; i++) sb.append(alph.charAt(r.nextInt(alph.length())));
        return sb.toString();
    }

    /**
     * 生成退化的纯英文字母编码（基于时间戳）
     */
    private String generateFallbackCode(long timestamp) {
        final String alph = "abcdefghijklmnopqrstuvwxyz";
        // 使用时间戳的哈希值来生成纯英文字母编码
        int hash = Long.hashCode(timestamp);
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            int idx = Math.abs((hash >> (i * 5)) % alph.length());
            sb.append(alph.charAt(idx));
        }
        return sb.toString();
    }

	/**
	 * 删除应用前的预处理（检查是否是插件应用，如果是则先卸载插件）
	 */
	@Override
	protected void preDelete(WoApp entity) {
		if (entity == null || entity.getId() == null) {
			return;
		}

		// 检查是否是插件应用
		if (AppOriginEnum.PLUGIN.getValue().equals(entity.getAppOrigin())) {
			if (wldosPluginsDebug) {
				log.info("检测到插件应用删除请求: " + entity.getAppName() + " (appId: " + entity.getId() + ")");
			}

			// 如果是插件应用，需要通过插件卸载流程来删除
			// 因为插件卸载会清理所有关联数据（域资源、角色权限、域应用、资源、插件数据库表等）
			if (pluginDataService != null) {
				// 根据 appId 查找对应的 pluginCode
				boolean isUninstall = this.pluginDataService.uninstallPluginWithAppId(entity.getId());
				if (!isUninstall) {
					// 如果没有找到插件注册记录，可能是数据不一致，使用兜底清理逻辑
					if (wldosPluginsDebug) {
						log.info("未找到插件注册记录，使用兜底清理逻辑清理关联数据");
					}
					cleanupAppAssociations(entity.getId());
				}
			} else {
				// 如果插件服务不可用，使用兜底清理逻辑
				if (wldosPluginsDebug) {
					log.info("插件服务不可用，使用兜底清理逻辑清理关联数据");
				}
				cleanupAppAssociations(entity.getId());
			}
		} else {
			// 非插件应用，使用标准清理逻辑
			if (wldosPluginsDebug) {
				log.info("非插件应用删除，使用标准清理逻辑");
			}
			cleanupAppAssociations(entity.getId());
		}
	}

	/**
	 * 清理应用关联数据的兜底逻辑
	 * 用于非插件应用或插件服务不可用时的清理
	 */
	private void cleanupAppAssociations(Long appId) {
		if (appId == null) {
			return;
		}

		try {
			// 注意：删除顺序很重要，必须先删除关联关系，再删除资源，最后删除应用
			
			// 1. 先清理所有关联关系（按 appId）
			// 1.1 清理域资源映射（必须先清理，避免外键约束）
			if (domainResourceDao != null) {
				domainResourceDao.deleteByAppId(appId);
				if (wldosPluginsDebug) {
					log.info("已清理应用 " + appId + " 的域资源映射");
				}
			}
			
			// 1.2 清理权限关联（角色-资源关联）
			if (authRoleDao != null) {
				authRoleDao.deleteByAppId(appId);
				if (wldosPluginsDebug) {
					log.info("已清理应用 " + appId + " 的角色权限关联");
				}
			}
			
			// 1.3 清理域-应用映射
			if (domainAppDao != null) {
				domainAppDao.deleteByAppId(appId);
				if (wldosPluginsDebug) {
					log.info("已清理应用 " + appId + " 的域-应用映射");
				}
			}
			
			// 2. 删除资源本身
			if (resourceDao != null) {
				resourceDao.deleteByAppId(appId);
				if (wldosPluginsDebug) {
					log.info("已清理应用 " + appId + " 的资源");
				}
			}
			
		} catch (Exception e) {
			if (wldosPluginsDebug) {
				log.error("清理应用 " + appId + " 的关联数据时发生异常: " + e.getMessage());
				e.printStackTrace();
			}
			throw new RuntimeException("清理应用关联数据失败: " + e.getMessage(), e);
		}
	}
}
