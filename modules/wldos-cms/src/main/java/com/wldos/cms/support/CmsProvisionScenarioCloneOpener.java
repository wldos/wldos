/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.support;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.wldos.cms.service.DomainCloneService;
import com.wldos.cms.vo.DomainCloneOptions;
import io.github.wldos.platform.support.domain.IProvisionScenarioCloneOpener;
import io.github.wldos.platform.support.domain.ProvisionCloneOptions;

/**
 * CMS 子站复制实现的租户入驻场景克隆。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Component
@Primary
public class CmsProvisionScenarioCloneOpener implements IProvisionScenarioCloneOpener {

	private final DomainCloneService domainCloneService;

	public CmsProvisionScenarioCloneOpener(DomainCloneService domainCloneService) {
		this.domainCloneService = domainCloneService;
	}

	@Override
	public boolean supportsCmsClone() {
		return true;
	}

	@Override
	public void cloneFromTemplate(long sourceDomainId, long targetDomainId, ProvisionCloneOptions options) {
		if (options == null) {
			return;
		}
		if (!options.isCopyDomainResource()
				&& !options.isCopyMenu()
				&& !options.isCopyPages()
				&& !options.isCopyDocs()
				&& !options.isCopyTermBindings()
				&& !options.isCopySiteConfig()
				&& !options.isCopyDomainOptions()) {
			return;
		}
		DomainCloneOptions cmsOptions = toCmsOptions(options);
		this.domainCloneService.cloneSite(sourceDomainId, targetDomainId, cmsOptions);
	}

	private static DomainCloneOptions toCmsOptions(ProvisionCloneOptions source) {
		DomainCloneOptions target = new DomainCloneOptions();
		target.setCopyMenu(source.isCopyMenu());
		target.setCopyDomainResource(source.isCopyDomainResource());
		target.setCopyPages(source.isCopyPages());
		target.setCopyDocs(source.isCopyDocs());
		target.setCopyTermBindings(source.isCopyTermBindings());
		target.setCopySiteConfig(source.isCopySiteConfig());
		target.setCopyDomainOptions(source.isCopyDomainOptions());
		return target;
	}
}
