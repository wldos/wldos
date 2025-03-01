/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms;

import java.util.List;
import java.util.Map;

import com.wldos.platform.support.cms.dto.ContModelDto;
import com.wldos.platform.support.cms.dto.PubTypeExt;
import com.wldos.platform.support.cms.entity.KPubmeta;
import com.wldos.platform.support.cms.model.Attachment;
import com.wldos.platform.support.cms.model.IMeta;
import com.wldos.platform.support.cms.vo.RouteParams;
import com.wldos.platform.support.cms.vo.SeoCrumbs;
import com.wldos.platform.support.term.TermOpener;
import com.wldos.platform.support.region.RegionOpener;

/**
 * cms工具处理。
 *
 * @author 元悉宇宙
 * @date 2023/3/25
 * @version 1.0
 */
public interface CMSUtils {
	/**
	 * 根据路由参数获取tdk和面包屑数据
	 *
	 * @param params 路由参数
	 * @return tdk和面包屑数据
	 */
	SeoCrumbs genSeoCrumbs(RouteParams params, TermOpener termOpener);

	/**
	 * 统一创建seo和面包屑
	 *
	 * @param iMeta 发布类型接口
	 * @param termTypeId 直属分类id，多者取首
	 */
	void genSeoAndCrumbs(IMeta iMeta, Long termTypeId, PubOpener pubOpener, TermOpener termOpener);

	void termAndTagHandle(IMeta iMeta, Long pid, TermOpener termOpener);

	void populateMeta(IMeta product, Map<String, String> pubMeta, RegionOpener regionOpener);

	IMeta handleContent(IMeta iMeta, List<KPubmeta> metas, ContModelDto contBody, Long pid, PubTypeExtOpener pubTypeExtService, PubOpener pubService, PubmetaOpener pubmetaService);

	List<PubTypeExt> getPubTypeExt(List<PubTypeExt> pubTypeExt, List<KPubmeta> metas);

	List<Attachment> getAttachments(Map<Long, List<KPubmeta>> attMetaGroup);
}
