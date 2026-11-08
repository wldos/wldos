/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.enums;

import io.github.wldos.common.Constants;

/**
 * 系统配置选项枚举。
 *
 * @author Yuanxi Universe
 * @date 2021/4/27
 * @version 1.0
 */
public enum SysOptionEnum {

	DEFAULT_GROUP("默认用户组", "default_group"),
	UN_ACTIVE_GROUP("待激活用户组", "un_active_group"),
	OAUTH_LOGIN_WECHAT("社会化登录微信", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeChat.getValue()),
	OAUTH_LOGIN_QQ("社会化登录QQ", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.QQ.getValue()),
	OAUTH_LOGIN_WEIBO("社会化登录微博", Constants.OAUTH_SETTINGS_PREFIX + OAuthTypeEnum.WeiBo.getValue()),
	/** 门户顶栏搜索下拉提示：值为 JSON 字符串数组，如 ["热词1","热词2"]；也可用纯文本每行一条；在后台「系统配置」维护 optionKey=portal_search_hints */
	PORTAL_SEARCH_HINTS("门户首页搜索下拉提示", "portal_search_hints"),
	/**
	 * 门户个人中心页签 JSON（全局 {@code wo_options}）；可由 {@code wo_domain_option} 按域覆盖，键同为 {@code portal_account_center_tabs}。
	 * type 允许：info、book、applications、projects、permission、shortcuts、login_log、op_log。
	 * 全局与域级均无有效配置时，前端使用内置 CMS 默认四个页签。
	 */
	PORTAL_ACCOUNT_CENTER_TABS("门户个人中心页签", "portal_account_center_tabs"),
	/** 管理端顶栏「使用文档」是否显示；域级 {@code wo_domain_option} 可覆盖。true/false，默认 true */
	ADMIN_HEADER_HELP_ENABLED("管理端使用文档开关", "admin_header_help_enabled"),
	/** 管理端顶栏「使用文档」链接；为空则不展示 */
	ADMIN_HEADER_HELP_URL("管理端使用文档链接", "admin_header_help_url"),
	/**
	 * 管理端首页（/admin Tab）JSON：module/url/extraProps/title。
	 * 域级 {@code wo_domain_option} 优先；子站未配时继承根域名主站；再回退全局 {@code wo_options}；最后 builtin（sys/monitor）。
	 */
	ADMIN_HOME_CONFIG("管理端首页配置", "admin_home_config"),
	/** 门户页脚 i18n JSON：{ "zh-CN": { foot, flink, copy }, "en-US": {...} } */
	PORTAL_FOOTER_I18N("门户页脚多语言", "portal_footer_i18n"),
	/** 门户页脚 QR 码指向 URL（前端 qrcode.react 渲染） */
	PORTAL_QR_URL("门户页脚二维码链接", "portal_qr_url"),
	/** 租户入驻场景登记 JSON 数组，键 platform.provision_scenarios */
	PLATFORM_PROVISION_SCENARIOS("租户入驻场景", "platform.provision_scenarios"),
	/** 平台官方运营主体 wo_company.id */
	PLATFORM_OPERATOR_COM_ID("平台运营公司", "platform.operator_com_id"),
	/** 平台官网主站 wo_domain.id */
	PLATFORM_OPERATOR_PRIMARY_DOMAIN_ID("平台运营主站", "platform.operator_primary_domain_id"),
	OTHER("其他", "other");

	private final String title;

	private final String key;

	SysOptionEnum(String title, String key) {
		this.title = title;
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "{title: '" + this.title + "', key: '" + this.key + "'}";
	}
}