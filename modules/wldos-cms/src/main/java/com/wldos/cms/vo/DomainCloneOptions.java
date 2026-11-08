/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 子站复制选项。
 */
@Getter
@Setter
public class DomainCloneOptions {

	/** 复制门户菜单域资源关联（menu / plugin_menu，共享 wo_resource） */
	private boolean copyMenu = true;

	/** 复制全部域资源关联（含 home/static-org 等，共享 wo_resource） */
	private boolean copyDomainResource = true;

	/** 复制 CMS 正式 page 行 */
	private boolean copyPages = true;

	/** 复制文档库（doc 根 + chapter 章节树） */
	private boolean copyDocs = true;

	/** 复制内容关联的分类与标签（k_term_object） */
	private boolean copyTermBindings = true;

	/** 复制 wo_domain 站点配置（页脚/QR/i18n/管理端首页）；默认 false，子站继承根域 */
	private boolean copySiteConfig = false;

	/** 复制源域 wo_domain_option（不含已迁至 wo_domain 的站点项）；默认 false */
	private boolean copyDomainOptions = false;
}
