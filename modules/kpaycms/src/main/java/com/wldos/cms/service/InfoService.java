/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.base.NoRepoService;
import com.wldos.cms.vo.InfoUnit;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.cms.dto.ContModelDto;
import com.wldos.support.cms.entity.KPubmeta;
import com.wldos.support.cms.model.KModelMetaKey;
import com.wldos.support.cms.model.MainPicture;
import com.wldos.support.cms.vo.Info;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.service.TermService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供求信息service。
 *
 * @author 树悉猿
 * @date 2022/01/05
 * @version 1.0
 */
@Slf4j
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoService extends NoRepoService {
	private final BeanCopier contCopier = BeanCopier.create(ContModelDto.class, Info.class, false);

	private final PubService pubService;

	private final TermService termService;

	private final PubmetaService pubmetaService;

	private final KCMSService kcmsService;

	public InfoService(PubService pubService, TermService termService, PubmetaService pubmetaService, KCMSService kcmsService) {
		this.pubService = pubService;
		this.termService = termService;
		this.pubmetaService = pubmetaService;
		this.kcmsService = kcmsService;
	}

	/**
	 * 根据实体字段自由查询供求信息列表，实体表：KPubs、KTermObject
	 *
	 * @param pageQuery 分页查询参数
	 * @return 信息列表页
	 */
	public PageableResult<InfoUnit> queryInfoDomain(PageQuery pageQuery) {

		// 判断是否指定类目
		Object termTypeId = pageQuery.getCondition().get("termTypeId");
		if (!ObjectUtils.isBlank(termTypeId)) {
			KTermType termType = this.termService.queryTermTypeById(Long.parseLong(String.valueOf(termTypeId)));
			if (termType == null)
				return new PageableResult<>();
			List<Object> ids = this.queryOwnIds(termType.getId());
			pageQuery.removeParam("termTypeId");
			this.filterByParentTermTypeId(ids, pageQuery);
		}

		this.handleCondition(pageQuery);

		return this.pubService.queryInfos(pageQuery);
	}

	/**
	 * 根据指定分类目录的别名查询分类目录下的作品列表，应包含子分类下的内容
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<InfoUnit> queryInfoCategory(String slugCategory, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
		if (termType == null)
			return new PageableResult<>();
		List<Object> ids = this.queryOwnIds(termType.getId());

		// 判断是否指定类目
		Object termTypeId = pageQuery.getCondition().get("termTypeId");
		if (!ObjectUtils.isBlank(termTypeId) && termType.getId().equals(Long.parseLong(String.valueOf(termTypeId)))) { // 实现动态表单后，应该删除此逻辑，使用动态路由切换
			pageQuery.removeParam("termTypeId");
		}
		this.filterByParentTermTypeId(ids, pageQuery);

		this.handleCondition(pageQuery);

		return this.pubService.queryInfos(pageQuery);
	}

	/**
	 * 根据指定分类目录的别名查询分类目录下的作品列表，应包含子分类下的内容
	 *
	 * @param slugTag 某个标签别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<InfoUnit> queryInfoTag(String slugTag, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugTag);
		if (termType == null)
			return new PageableResult<>();
		pageQuery.pushParam("termTypeId", termType.getId());

		this.handleCondition(pageQuery);

		return this.pubService.queryInfos(pageQuery);
	}

	private void handleCondition(PageQuery pageQuery) {

		// 处理price
		Object price = pageQuery.getCondition().get("price");
		String allPrice = "0,0";
		if (allPrice.equals(price)) { // 是全部
			pageQuery.removeParam("price");
		}

		// 处理city
		Object city = pageQuery.getCondition().get("city");
		long allCity = -1L;
		if (!ObjectUtils.isBlank(city) && allCity == Long.parseLong(city.toString())) { // 是全部
			pageQuery.removeParam("city");
		}
	}


	/**
	 * 查询供求信息详情
	 *
	 * @param pid 信息id
	 * @param isPreview 是否预览
	 * @return 供求信息
	 */
	public Info infoDetail(Long pid, boolean isPreview) {
		//@todo 发布状态不是已发布（子类型不是继承或者父类不是已发布），一律返回空。在发布阶段，可信用户（角色为可信用户）无需审核，默认都是已发布，并且修改次数不限制 （后期实现）

		ContModelDto contBody = this.pubService.queryContModel(pid);
		if (contBody == null)
			return null;

		if (!isPreview && this.kcmsService.pubStatusIsNotOk(contBody.getPubStatus(), contBody.getDeleteFlag(), contBody.getParentId())) {
			return null;
		}

		this.kcmsService.updatePubMeta(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPubmeta> metas = this.pubmetaService.queryPubMetaByPubId(pid);

		// 合并主体信息
		Info product = new Info();
		this.contCopier.copy(contBody, product, null);
		// 处理分类和标签
		this.kcmsService.termAndTagHandle(product, pid);

		// 生成seo和面包屑数据
		List<Long> tIds = product.getTermTypeIds();
		this.kcmsService.genSeoAndCrumbs(product, tIds.get(0)); // 多者取首

		// 处理主图（主图和封面一样是确定的，而附件是不确定的） 2.主图需要在信息发布时设置
		String[] mainPics = { KModelMetaKey.PUB_META_KEY_MAIN_PIC1, KModelMetaKey.PUB_META_KEY_MAIN_PIC2, KModelMetaKey.PUB_META_KEY_MAIN_PIC3,
				KModelMetaKey.PUB_META_KEY_MAIN_PIC4 };
		List<MainPicture> pictures = Arrays.stream(mainPics).parallel().map(pic ->
				this.kcmsService.exact(metas, pic)).filter(Objects::nonNull).collect(Collectors.toList());
		product.setMainPic(pictures);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

		this.kcmsService.populateMeta(product, pubMeta);

		return (Info) this.kcmsService.handleContent(product, metas, contBody, pid);
	}

	/**
	 * 填充分类过滤器
	 *
	 * @param termTypeIds 分类列表
	 * @param pageQuery 查询参数
	 */
	private void filterByParentTermTypeId(List<Object> termTypeIds, PageQuery pageQuery) {
		// 查询分类及其子分类
		pageQuery.pushFilter("termTypeId", termTypeIds);
	}

	/**
	 * 查询分类及其所有子类的id
	 *
	 * @param termTypeId 待取子分类id
	 * @return 命中分类id
	 */
	private List<Object> queryOwnIds(Long termTypeId) {
		List<LevelNode> nodes = this.termService.queryTermTreeByParentId(termTypeId);
		// 分类类型id用于从对象分类关联表[存档]中作筛选分类条件，filter: termTypeIds ? {termTypeId: termTypeIds} : {}
		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}
}
