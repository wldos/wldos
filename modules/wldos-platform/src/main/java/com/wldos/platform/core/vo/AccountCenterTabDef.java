/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 门户「个人中心」页签定义，由系统配置 {@code portal_account_center_tabs} 解析而来。
 *
 * <p>{@code type} 为功能类型（前后端约定）；{@code key} 为页签唯一标识（用于 Card activeTabKey）；
 * {@code title} 为空时前端使用内置中文默认标题。服务端按 {@link com.wldos.platform.core.service.AccountCenterTabAccessService} 结合角色与资源权限裁剪可见页签。</p>
 */
@Getter
@Setter
public class AccountCenterTabDef {

	private String key;

	/** 参见 {@link com.wldos.platform.core.service.OptionsService#parseAccountCenterTabs} 白名单 */
	private String type;

	/** 可选展示标题 */
	private String title;

	/**
	 * 是否要求账号具备管理端菜单资源（能力/权限维，见 {@link UserService#queryAdminMenuByUser}）。
	 * 个人中心页签是否展示以 {@link com.wldos.platform.core.service.AccountCenterTabAccessService} 为准，本质依赖角色与资源/按钮权限；
	 * 「用户端 / 管理端」仅在前端需要按不同页面装配不同 Tab 集合时有辅助意义，与公有互联网用户是否具备管理功能相比常为次维。
	 * null 表示按 {@link com.wldos.platform.core.service.AccountCenterTabAccessService} 对 {@code type} 的默认策略。
	 */
	private Boolean requireManageSide;

	/**
	 * 额外路由/资源路径校验：须至少有一条通过 {@link AuthService#authorityRouteCheck} 返回 200。
	 * 与 {@code type=permission} 时默认校验 {@code /admin/sys/user/adminMenu} 可叠加。
	 */
	private List<String> resourcePaths;

	/**
	 * 须在用户 {@code currentAuthority}（按钮/资源授权码）中至少命中一项。
	 */
	private List<String> authorityNeedAny;

	/**
	 * 须在用户主体认证链路上解析出的 {@code wo_role.role_code} 中至少命中一项。
	 */
	private List<String> roleNeedAny;
}
