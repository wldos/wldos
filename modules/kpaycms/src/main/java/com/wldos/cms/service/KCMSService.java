/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.base.NoRepoService;
import com.wldos.base.entity.EntityAssists;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.ListStyleEnum;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Geographic;
import com.wldos.cms.vo.MiniPub;
import com.wldos.cms.vo.Pub;
import com.wldos.cms.vo.PubMember;
import com.wldos.cms.vo.PubMeta;
import com.wldos.cms.vo.PubUnit;
import com.wldos.common.Constants;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ChineseUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.support.cms.CMSUtils;
import com.wldos.support.cms.dto.ContModelDto;
import com.wldos.support.cms.dto.PubTypeExt;
import com.wldos.support.cms.entity.KPubmeta;
import com.wldos.support.cms.model.Attachment;
import com.wldos.support.cms.model.IMeta;
import com.wldos.support.cms.model.KModelMetaKey;
import com.wldos.support.cms.model.MainPicture;
import com.wldos.support.cms.vo.Product;
import com.wldos.support.cms.vo.RouteParams;
import com.wldos.support.cms.vo.SeoCrumbs;
import com.wldos.support.region.vo.City;
import com.wldos.support.storage.IStore;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.support.term.dto.Term;
import com.wldos.support.term.enums.TermTypeEnum;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.enums.PubTypeEnum;
import com.wldos.sys.base.service.PubTypeExtService;
import com.wldos.sys.base.service.TermService;
import com.wldos.sys.core.service.RegionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * cms全局service。
 *
 * @author 树悉猿
 * @date 2021/6/13
 * @version 1.0
 */
@Slf4j
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class) // 符号错误异常是由于静默元数据没有创建成功，重新创建即可，不要回滚
public class KCMSService extends NoRepoService {

	private final BeanCopier contCopier = BeanCopier.create(ContModelDto.class, Product.class, false);

	private final BeanCopier artCopier = BeanCopier.create(ContModelDto.class, Article.class, false);

	private final BeanCopier pubMetaCopier = BeanCopier.create(ContModelDto.class, PubMeta.class, false);

	@Value("${wldos_file_pic_srcset}")
	private String thumbnail;

	@Value("${wldos_sidecar_config}")
	private String sideCar;

	@Autowired
	@Lazy
	private CMSUtils cmsUtils;

	private final PubService pubService;

	private final PubmetaService pubmetaService;

	private final PubTypeExtService pubTypeExtService;

	private final TermService termService;

	private final RegionService regionService;

	public KCMSService(PubService pubService, PubmetaService pubmetaService,
			PubTypeExtService pubTypeExtService, TermService termService, RegionService regionService) {
		this.pubService = pubService;
		this.pubmetaService = pubmetaService;
		this.pubTypeExtService = pubTypeExtService;
		this.termService = termService;
		this.regionService = regionService;
	}

	/**
	 * 作品列表页(存档模:，参数详细; 卡片模式，参数简约)
	 * 不传listStyle默认是卡片模式
	 *
	 * @param pageQuery 页面参数
	 * @return 分页数据
	 */
	public PageableResult<PubUnit> queryWorksList(PageQuery pageQuery) {
		// 列表样式：卡片式、存档式，卡片式要求的参数简约
		Object listStyle = pageQuery.getCondition().get("listStyle");

		return ListStyleEnum.archive.toString().equals(listStyle) ? this.pubService.queryArchives(pageQuery)
				: this.pubService.queryPubWithExtList(pageQuery);
	}

	// @todo 图片、附件类预处理和设置元数据存储

	private final BeanCopier pubCopier = BeanCopier.create(Pub.class, KPubs.class, false);

	/**
	 * 保存带扩展属性的内容
	 *
	 * @param pub 发布内容
	 * @param pubType 发布类型
	 * @param userId 用户id
	 * @param userIp 用户ip
	 */
	public Long insertSelective(Pub pub, String pubType, Long userId, String userIp) {

		if (ObjectUtils.isBlank(pub.getPubName()) && this.pubService.pubNameIsNull(pub.getId())) // 创建标题别名
			pub.setPubName(ChineseUtils.hanZi2Pinyin(pub.getPubTitle(), true));

		// 为便于结构化处理，对于图片等附件的处理，要在上传文件和编辑文件时将设置数据存储到pub metadata中，在发布内容渲染时再读出

		KPubs pubs = new KPubs();
		this.pubCopier.copy(pub, pubs, null);
		// 存在别名，自动加1设置不重复别名
		if (!ObjectUtils.isBlank(pubs.getPubName()))
			pubs.setPubName(this.pubService.existsAutoDiffPubName(pubs.getPubName(), pubs.getId()));

		pubs.setPubType(pubType);
		// 属于可信者用户组的会员跳过审核直接发布
		pubs.setPubStatus(this.pubService.isCanTrust(userId) ? PubStatusEnum.PUBLISH.toString() : PubStatusEnum.IN_REVIEW.toString());
		Long id = this.nextId();
		EntityAssists.beforeInsert(pubs, id, userId, userIp, false);

		// @todo 考虑嵌入过滤器hook：pubs = applyFilter("savePub", pubs);

		this.pubService.insertSelective(pubs);

		// 批量关联发布内容分类并计数
		List<Long> termTypeIds = pub.getTermTypeIds().stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
		this.termService.saveTermObject(termTypeIds, id);

		List<String> tagIds = pub.getTagIds();
		if (tagIds != null) {
			List<Long> newTagIds = this.termService.handleTag(tagIds, userId, userIp);
			this.termService.saveTermObject(newTagIds, id);
		}

		List<PubTypeExt> pubTypeExt = pub.getPubTypeExt();

		if (ObjectUtils.isBlank(pubTypeExt)) {
			pubTypeExt = new ArrayList<>();
			this.appendPubMeta(pubTypeExt); // 添加后台静默处理的元数据，比如查看数
		}

		// @todo 考虑嵌入过滤器hook：pubTypeExt = applyFilter("savePubTypeExt", pubTypeExt);
		// 保存扩展属性
		this.createPubMeta(pubTypeExt, id);

		return id;
	}

	/**
	 * 更新发布内容
	 *
	 * @param pub pub vo
	 * @param userId user id
	 * @param userIp user ip
	 */
	public void update(Pub pub, Long userId, String userIp) {
		if (ObjectUtils.isBlank(pub.getPubName()) && this.pubService.pubNameIsNull(pub.getId()))
			pub.setPubName(ChineseUtils.hanZi2Pinyin(pub.getPubTitle(), true));  // 创建标题别名

		List<PubTypeExt> pubTypeExt = pub.getPubTypeExt();

		if (!ObjectUtils.isBlank(pubTypeExt)) {
			// @todo 考虑嵌入过滤器hook：pubTypeExt = applyFilter("updatePubTypeExt", pubTypeExt); 图片等扩展属性需要特殊处理
			List<KPubmeta> pubMetas = this.pubmetaService.queryPubMetaByPubId(pub.getId());
			// 需要根据pubId+metaKey取出可能存在的pubMeta.id，如果不存在，以新属性创建
			Map<String, Long> keyMap = pubMetas.parallelStream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getId));
			// 保存扩展属性
			pubMetas = pubTypeExt.parallelStream().map(ext -> {

				String key = ext.getMetaKey();
				String value = ext.getMetaValue();
				if (ObjectUtils.isBlank(value))
					return null;
				return KPubmeta.of(keyMap.get(key), pub.getId(), key, value);
			}).filter(Objects::nonNull).collect(Collectors.toList());

			List<KPubmeta> pubMetasU = pubMetas.parallelStream().filter(p -> p.getId() != null).collect(Collectors.toList());
			List<KPubmeta> pubMetasN = pubMetas.parallelStream().filter(p -> p.getId() == null).collect(Collectors.toList());
			if (!pubMetasN.isEmpty())
				pubMetasN.forEach(pm -> pm.setId(this.nextId()));
			this.pubmetaService.insertSelectiveAll(pubMetasN);
			if (!pubMetasU.isEmpty())
				this.pubmetaService.updateAll(pubMetasU);
		}

		// 处理分类和标签
		List<SelectOption> tIds = pub.getTermTypeIds();
		if (tIds != null) {
			List<Long> termTypeIds = tIds.stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
			this.termService.updateTermObject(termTypeIds, pub.getId(), TermTypeEnum.CATEGORY.toString());
		}

		List<String> tagIds = pub.getTagIds();
		if (tagIds != null) {
			List<Long> newTagIds = this.termService.handleTag(tagIds, userId, userIp);
			this.termService.updateTermObject(newTagIds, pub.getId(), TermTypeEnum.TAG.toString());
		}

		KPubs pubs = new KPubs();
		this.pubCopier.copy(pub, pubs, null);
		EntityAssists.beforeUpdated(pubs, userId, userIp);

		// @todo 考虑嵌入过滤器hook：pubs = applyFilter("updatePub", pubs);

		// 设置不重复的别名
		if (!ObjectUtils.isBlank(pubs.getPubName()))
			pubs.setPubName(this.pubService.existsAutoDiffPubName(pubs.getPubName(), pubs.getId()));

		this.pubService.update(pubs);
	}

	/**
	 * 删除内容
	 *
	 * @param pub 发布内容
	 * @return 反馈
	 */
	public String delete(Pub pub) {
		// @todo 考虑嵌入操作hook: execHook("deletePub", pub);
		// 删除检查：检查存在内容，是复合类型作品不能删，否则是内容或单体类型作品，直接删除。
		List<Chapter> chapters = this.pubService.queryChapterByParentId(pub.getId(), DeleteFlagEnum.NORMAL.toString());
		if (chapters != null && chapters.size() > 0)
			return "存在内容，请先删除内容";
		// 逻辑删
		this.pubService.deleteById(pub.getId());
		return "ok";
		// 若是物理删，需要级联删除meta扩展属性，甚至附件
	}

	/**
	 * 查询详情页信息
	 *
	 * @param pid 产品id
	 * @param isPreview 是否预览
	 * @return 产品信息
	 */
	public Product productInfo(Long pid, boolean isPreview) {
		//@todo 发布状态不是已发布（子类型不是继承或者父类不是已发布），一律返回空。在发布阶段，可信用户（角色为可信用户）无需审核，默认都是已发布，并且修改次数不限制 （后期实现）
		ContModelDto contBody = this.pubService.queryContModel(pid);
		if (contBody == null)
			return null;

		if (!isPreview && this.pubStatusIsNotOk(contBody.getPubStatus(), contBody.getDeleteFlag(), contBody.getParentId())) {
			return null;
		}

		this.updatePubMeta(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPubmeta> metas = this.pubmetaService.queryPubMetaByPubId(pid);

		// 合并主体信息
		Product product = new Product();
		this.contCopier.copy(contBody, product, null);
		// 处理分类和标签
		this.termAndTagHandle(product, pid);

		// 生成seo和面包屑数据
		List<Long> tIds = product.getTermTypeIds();
		this.genSeoAndCrumbs(product, tIds.get(0)); // 多者取首

		// 处理主图（主图和封面一样是确定的，而附件是不确定的） 2.主图需要在信息发布时设置
		String[] mainPics = { KModelMetaKey.PUB_META_KEY_MAIN_PIC1, KModelMetaKey.PUB_META_KEY_MAIN_PIC2, KModelMetaKey.PUB_META_KEY_MAIN_PIC3,
				KModelMetaKey.PUB_META_KEY_MAIN_PIC4 };
		List<MainPicture> pictures = Arrays.stream(mainPics).parallel().map(pic ->
				exact(metas, pic)).filter(Objects::nonNull).collect(Collectors.toList());
		product.setMainPic(pictures);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(product, pubMeta);

		return (Product) this.handleContent(product, metas, contBody, pid);
	}

	/**
	 * 统一创建seo和面包屑
	 *
	 * @param iMeta 发布类型接口
	 * @param termTypeId 直属分类id，多者取首
	 */
	public void genSeoAndCrumbs(IMeta iMeta, Long termTypeId) {
		this.cmsUtils.genSeoAndCrumbs(iMeta, termTypeId, this.pubService, this.termService);
	}

	/**
	 * 根据路由参数获取tdk和面包屑数据
	 *
	 * @param params 路由参数
	 * @return tdk和面包屑数据
	 */
	public SeoCrumbs genSeoCrumbs(RouteParams params) {
		return this.cmsUtils.genSeoCrumbs(params, this.termService);
	}

	public void termAndTagHandle(IMeta iMeta, Long pid) {
		this.cmsUtils.termAndTagHandle(iMeta, pid, this.termService);
	}

	public void populateMeta(IMeta product, Map<String, String> pubMeta) {
		this.cmsUtils.populateMeta(product, pubMeta, this.regionService);
	}

	public IMeta handleContent(IMeta iMeta, List<KPubmeta> metas, ContModelDto contBody, Long pid) {
		return this.cmsUtils.handleContent(iMeta, metas, contBody, pid, this.pubTypeExtService, this.pubService, this.pubmetaService);
	}

	public MainPicture exact(List<KPubmeta> metas, String pic) {
		MainPicture mp = new MainPicture();
		mp.setKey(pic);
		for (KPubmeta meta : metas) {
			if (meta.getMetaKey().equals(pic)) {
				mp.setUrl(this.store.getFileUrl(meta.getMetaValue(), null));
				break;
			}
		}
		if (mp.getUrl() == null) {
			return null; // 为空的返回
		}

		return mp;
	}

	void createPubMeta(List<PubTypeExt> pubTypeExt, Long id) {
		List<KPubmeta> pubMetas = pubTypeExt.parallelStream().map(ext -> {
			KPubmeta pubMeta = new KPubmeta();
			String key = ext.getMetaKey();
			String value = ext.getMetaValue();
			if (ObjectUtils.isBlank(value))
				return null;
			pubMeta.setId(this.nextId());
			pubMeta.setPubId(id);
			pubMeta.setMetaKey(key);
			pubMeta.setMetaValue(value);
			return pubMeta;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		this.pubmetaService.insertSelectiveAll(pubMetas);
	}

	/**
	 * 创建作品时查询作品，无需考虑状态
	 *
	 * @param bookId works id
	 * @return a works info
	 */
	public Book queryBook(Long bookId) {
		KPubs pub = this.pubService.findById(bookId);
		if (pub == null)
			return null;

		// 单体结构直接查询自身填充Chapter
		List<Chapter> chapters = PubTypeEnum.isSingle(pub.getPubType()) ? this.pubService.queryChapterBySinglePubId(bookId)
				: this.pubService.queryChapterByParentId(bookId, DeleteFlagEnum.NORMAL.toString());
		return Book.of(pub.getId(), pub.getPubTitle(), pub.getPubType(), pub.getPubStatus(), chapters);
	}

	/**
	 * 保存发布内容图片等附件的元信息
	 *
	 * @param attach 附件
	 * @param pub 附件发布内容
	 */
	public void savePubAttach(Attachment attach, KPubs pub) {

		// 创建附件元数据
		Map<String, Object> objectMap = ObjectUtils.toMap(attach);
		List<KPubmeta> pubMetas =
				objectMap.entrySet().parallelStream().map(entry ->
						KPubmeta.of(this.nextId(), pub.getId(), entry.getKey(), entry.getValue().toString())).collect(Collectors.toList());

		this.pubService.insertSelective(pub);

		this.pubmetaService.insertSelectiveAll(pubMetas);
	}

	/**
	 * 获取配置的缩略图尺寸密度集
	 *
	 * @return 缩略图集
	 * @throws JsonProcessingException json异常
	 */
	public List<Thumbnail> getSrcset() throws JsonProcessingException {

		return new ObjectMapper().readValue(this.thumbnail, new TypeReference<List<Thumbnail>>() {});
	}

	/**
	 * 读取侧边栏配置信息
	 *
	 * @param pageName 页名
	 * @return 侧边栏配置信息
	 */
	public Map<String, Object> readParamsSideCar(String pageName) throws JsonProcessingException {
		Map<String, Map<String, Object>> config = new ObjectMapper().readValue(this.sideCar, new TypeReference<Map<String, Map<String, Object>>>() {});
		Map<String, Object> params;

		if (ObjectUtils.isBlank(config) || ObjectUtils.isBlank(params = config.get(pageName))) { // 取默认值
			params = new HashMap<>();
			params.put(KModelMetaKey.SIDECAR_CONF_LIST_NUM, 10);
			params.put(KModelMetaKey.SIDECAR_CONF_TYPE, PubTypeEnum.BOOK.getName());
			params.put(KModelMetaKey.SIDECAR_CONF_SORTER, "{\"id\":\"descend\"}");
		}

		params.put(KModelMetaKey.SIDECAR_CONF_PAGENO, 1); // 默认取第一页

		return params;
	}

	/**
	 * 门户跨域查询所有作品列表，针对产品模板。
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离，仅用于管理端数据分析不面向终端用户！！！
	 * 用户侧应该按 /租户/域/分类（/表示包含关系，第一个/表示平台） 的颗粒度钻取。
	 *
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<PubUnit> queryProductPortal(PageQuery pageQuery) {

		return this.pubService.queryPubWithExtList(pageQuery);
	}

	/**
	 * 门户跨域根据分类别名查询作品列表（含子分类下的内容），针对产品模板。
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离，仅用于管理端数据分析不面向终端用户！
	 * 用户侧应该按 /租户/域/分类（/表示包含关系，第一个/表示平台） 的颗粒度钻取。
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<PubUnit> queryProductPortalByCategory(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			if (termType == null)
				return new PageableResult<>();
			List<Object> ids = this.queryOwnIds(termType.getId());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.pubService.queryPubWithExtList(pageQuery);
	}

	/**
	 * 根据实体字段自由查询作品列表，实体表：KPubs、KTermObject，针对产品模板。
	 *
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<PubUnit> queryProductDomain(PageQuery pageQuery) {

		return this.pubService.queryPubWithExtList(pageQuery);
	}

	/**
	 * 根据指定分类目录的别名查询分类目录下的作品列表(含子分类下的内容)，针对产品模板。
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<PubUnit> queryProductCategory(String slugCategory, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
		if (termType == null)
			return new PageableResult<>();
		List<Object> ids = this.queryOwnIds(termType.getId());

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.pubService.queryPubWithExtList(pageQuery);
	}

	/**
	 * 门户跨域查询内容列表，针对存档模板。
	 * 门户有且仅有一个，并且跨域、跨租户查询，没有数据隔离，用户侧应该按 /租户/域（/表示包含关系，第一个/表示平台） 的颗粒度钻取。
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesPortal(PageQuery pageQuery) {

		return this.pubService.queryArchives(pageQuery);
	}

	/**
	 * 门户跨域根据分类目录的别名查询分类目录下的内容列表，应包含子分类下的内容
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesCategoryPortal(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			if (termType == null)
				return new PageableResult<>();
			List<Object> ids = this.queryOwnIds(termType.getId());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.pubService.queryArchives(pageQuery);
	}

	/**
	 * 自由组装条件查询内容列表
	 *
	 * @param pageQuery 分页查询参数，支持KPubs、KTermObjects所有字段
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesDomain(PageQuery pageQuery) {

		return this.pubService.queryArchives(pageQuery);
	}

	/**
	 * 查询指定分类目录下的内容列表，应包含子分类下的内容
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesCategory(String slugCategory, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
		if (termType == null)
			return new PageableResult<>();
		List<Object> ids = this.queryOwnIds(termType.getId());

		if (ids.isEmpty())
			return new PageableResult<>();

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.pubService.queryArchives(pageQuery);
	}

	/**
	 * 查询指定标签的内容列表
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesTag(String slugTag, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugTag);
		pageQuery.pushParam("termTypeId", termType.getId());

		return this.pubService.queryArchives(pageQuery);
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

	/**
	 * 根据域id查询所某个用户喜欢或关注的内容列表，应包含子分类下的内容
	 *
	 * @param domainId 域id
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PubUnit> queryArchivesUserDomain(Long domainId, PageQuery pageQuery) {
		pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);

		return this.pubService.queryArchivesUser(pageQuery);
	}

	/**
	 * 根据作品id查询作品下的章节内容
	 *
	 * @param bookId 作品id
	 * @param pageQuery 分页查询参数
	 * @return 作品章节存档列表
	 */
	public PageableResult<PubUnit> queryBookChapter(Long bookId, PageQuery pageQuery) {
		pageQuery.pushParam("parentId", bookId);
		return this.pubService.queryArchives(pageQuery);
	}

	/**
	 * 根据别名查询pubId
	 *
	 * @param pubName 发布内容别名
	 * @return 发布id
	 */
	public Long queryIdByPubName(String pubName) {
		return this.pubService.queryIdByPubName(pubName);
	}

	/**
	 * 查询篇章信息
	 *
	 * @param pid 发布内容id
	 * @param isPreview 是否预览
	 * @return 篇章实体
	 */
	public Article queryArticle(Long pid, boolean isPreview) { // @todo 以id访问业务对象，应该检查域隔离，防止恶意跨域请求，暂不处理
		//@todo 已删除或发布状态不是已发布（子类型不是继承或者父类不是已发布），一律返回空。在发布阶段，可信用户（角色为可信用户）无需审核，默认都是已发布，并且修改次数不限制 （后期实现）

		ContModelDto contBody = this.pubService.queryContModel(pid);

		if (contBody == null)
			return null;

		if (!isPreview && this.pubStatusIsNotOk(contBody.getPubStatus(), contBody.getDeleteFlag(), contBody.getParentId())) {
			return null;
		}

		this.updatePubMeta(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPubmeta> metas = this.pubmetaService.queryPubMetaByPubId(pid);

		// 合并主体信息
		Article article = new Article();
		this.artCopier.copy(contBody, article, null);

		// 查询作者
		PubMember member = this.pubService.queryMemberByPubId(pid);
		article.setMember(member);

		// 处理分类和标签
		this.termAndTagHandle(article, pid);

		// 生成seo和面包屑数据
		List<Long> tIds = article.getTermTypeIds();
		this.genSeoAndCrumbs(article, tIds.get(0)); // 多者取首

		// 查询上下篇、相关篇章
		this.setPrevNextAndRelated(pid, article);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(article, pubMeta);

		return (Article) this.handleContent(article, metas, contBody, pid);
	}

	/**
	 * 检查发布状态是否ok
	 * 约定：已删除或发布状态不是已发布（子类型不是继承或者父类不是已发布），一律属于异常，需要返回404
	 *
	 * @param pubStatus 发布状态
	 * @param parentId 发布parentId
	 * @return 是否
	 */
	public boolean pubStatusIsNotOk(String pubStatus, String deleteFlag, Long parentId) {
		if (parentId.equals(Constants.TOP_PUB_ID)) { // 主类型判断两个状态
			return !PubStatusEnum.PUBLISH.getValue().equals(pubStatus) || !DeleteFlagEnum.NORMAL.toString().equals(deleteFlag);
		}

		// 子类型，先判断父作品状态
		if (this.pubService.existsByIdAndPubStatusAndDeleteFlag(parentId, PubStatusEnum.PUBLISH.getValue(), DeleteFlagEnum.NORMAL.toString())) {
			return !PubStatusEnum.INHERIT.getValue().equals(pubStatus) || !DeleteFlagEnum.NORMAL.toString().equals(deleteFlag);
		}

		return true;
	}

	private void setPrevNextAndRelated(Long pid, Article article) {
		String pubType = article.getPubType();
		List<Long> termTypeIds = article.getTermTypeIds();
		MiniPub prev;
		MiniPub next;
		if (PubTypeEnum.POST.getName().equals(pubType)) {
			if (ObjectUtils.isBlank(termTypeIds))
				return;
			prev = this.pubService.queryPrevPub(pid, termTypeIds.get(0));
			next = this.pubService.queryNextPub(pid, termTypeIds.get(0));
		}
		else {
			Long parentId = article.getParentId();
			prev = this.pubService.queryPrevChapter(pid, parentId);
			next = this.pubService.queryNextChapter(pid, parentId);
		}

		article.setPrev(prev);
		article.setNext(next);

		if (ObjectUtils.isBlank(termTypeIds))
			return;
		List<Term> tags = article.getTags();
		List<Long> tagTermTypeIds = ObjectUtils.isBlank(tags) ? termTypeIds : tags.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
		List<MiniPub> relPubs = this.pubService.queryRelatedPubs(pubType, tagTermTypeIds, 8);
		if (ObjectUtils.isBlank(relPubs)) {
			relPubs = this.pubService.queryRelatedPubs(pubType, termTypeIds, 8);
		}
		article.setRelPubs(relPubs);
	}

	/**
	 * 读取作品元素内容
	 *
	 * @param pid 元素发布内容id
	 * @param isPreview 是否预览
	 * @return 元素
	 */
	public Article readElement(Long pid, boolean isPreview) {
		//@todo 检查付费设置，需要付费的灰色不可继续读取

		ContModelDto contBody = this.pubService.queryContModel(pid);

		if (contBody == null)
			return null;

		if (!isPreview && this.pubStatusIsNotOk(contBody.getPubStatus(), contBody.getDeleteFlag(), contBody.getParentId())) {
			return null;
		}

		this.updatePubMeta(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPubmeta> metas = this.pubmetaService.queryPubMetaByPubId(pid);

		// 合并主体信息
		Article article = new Article();
		this.artCopier.copy(contBody, article, null);

		// 查询作者
		PubMember member = this.pubService.queryMemberByPubId(pid);
		article.setMember(member);

		// 处理分类和标签
		this.termAndTagHandle(article, pid);

		// 生成seo和面包屑数据
		List<Long> tIds = article.getTermTypeIds();
		this.genSeoAndCrumbs(article, tIds.get(0)); // 多者取首

		// 查询上下篇、相关篇章
		this.setPrevNextAndRelated(pid, article);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(article, pubMeta);

		return (Article) this.handleContent(article, metas, contBody, pid);
	}

	public List<PubTypeExt> getPubTypeExt(List<PubTypeExt> pubTypeExt, List<KPubmeta> metas) {
		return this.cmsUtils.getPubTypeExt(pubTypeExt, metas);
	}

	public List<Attachment> getAttachments(Map<Long, List<KPubmeta>> attMetaGroup) {
		return this.cmsUtils.getAttachments(attMetaGroup);
	}

	private void populateMeta(Article article, Map<String, String> pubMeta) {
		String cover = pubMeta.get(KModelMetaKey.PUB_META_KEY_COVER);
		if (!ObjectUtils.isBlank(cover))
			article.setCover(cover);
		String views = pubMeta.get(KModelMetaKey.PUB_META_KEY_VIEWS);
		if (!ObjectUtils.isBlank(views))
			article.setViews(views);
	}

	/**
	 * 追加公共静默元数据
	 *
	 * @param pubTypeExtList 扩展属性列表
	 */
	void appendPubMeta(List<PubTypeExt> pubTypeExtList) {

		pubTypeExtList.add(PubTypeExt.of(KModelMetaKey.PUB_META_KEY_VIEWS, "0"));
	}

	/**
	 * 更新公共静默元数据
	 *
	 * @param pid 发布内容id
	 */
	public void updatePubMeta(Long pid) { // @todo 需要开启缓存、消息缓冲机制
		if (!this.pubmetaService.isExistsViews(pid)) {
			List<PubTypeExt> pubTypeExtList = new ArrayList<>();
			this.appendPubMeta(pubTypeExtList);
			this.createPubMeta(pubTypeExtList, pid);
		}
		else {
			this.pubmetaService.increasePubViews(1, pid);
			KPubmeta views = this.pubmetaService.queryByPubIdAndMetaKey(pid, KModelMetaKey.PUB_META_KEY_VIEWS);
			int viewNum = Integer.parseInt(views.getMetaValue());
			if (viewNum % 10 == 0) {
				// 每增加一定数量，更新一次发布内容热度，默认增加10，后期加入缓冲机制和调节机制
				this.pubService.updateViews(pid, viewNum);
			}
		}
	}

	/**
	 * 创作工作台专用-查询内容信息（不检查发布状态）
	 *
	 * @param pid 内容id
	 * @return 内容信息
	 */
	public PubMeta pubInfo(Long pid) {
		ContModelDto contBody = this.pubService.queryContModel(pid);

		if (contBody == null)
			return null;

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPubmeta> metas = this.pubmetaService.queryPubMetaByPubId(pid);

		// 合并主体信息
		PubMeta pub = new PubMeta();
		this.pubMetaCopier.copy(contBody, pub, null);
		// 取出分类和标签
		this.termAndTagFromPub(pub, pid);

		// 扩展属性 : 考虑静态模型 和 动态模型 的处理方式
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPubmeta::getMetaKey, KPubmeta::getMetaValue, (k1, k2) -> k1));

		// 处理地域信息
		String city = pubMeta.get(KModelMetaKey.PUB_META_KEY_CITY);
		if (!ObjectUtils.isBlank(city)) {
			City region = this.regionService.queryRegionInfoByCode(city);
			if (region != null) {
				Geographic geo = Geographic.of(region);
				pub.setGeographic(geo);
			}
		}

		// 无封面处理url
		if (!pubMeta.containsKey(KModelMetaKey.PUB_META_KEY_COVER)) {
			pubMeta.put(KModelMetaKey.PUB_META_KEY_COVER, MainPicture.noMainPicPath);
		}
		// 设置ossUrl，用于前端拼装文件类完整url
		pubMeta.put(IStore.KEY_OSS_URL, this.store.genOssUrl(FileAccessPolicyEnum.PUBLIC));

		pub.setPubTypeExt(pubMeta);

		return pub;
	}

	private void termAndTagFromPub(PubMeta pub, Long pid) {
		List<Term> termsType = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.CATEGORY.toString());
		// 分类目录
		List<SelectOption> termTypeIds = termsType.parallelStream().map(t -> SelectOption.of(t.getName(), t.getTermTypeId().toString())).collect(Collectors.toList());
		pub.setTermTypeIds(termTypeIds);
		// 标签
		List<Term> terms = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.TAG.toString());
		List<String> tagIds = terms.parallelStream().map(Term::getName).collect(Collectors.toList());

		pub.setTagIds(tagIds);
	}

	public boolean isValidTerm(List<Long> termTypeIds) {
		return this.termService.isValidTerm(termTypeIds);
	}

	public boolean isSamePubTypeStruct(Long id, String pubType) {
		if (ObjectUtils.isBlank(pubType))
			return true;
		KPubs pub = this.pubService.findById(id);
		if (PubTypeEnum.isSingle(pub.getPubType()))
			return PubTypeEnum.isSingle(pubType);
		return PubTypeEnum.isComplex(pubType);
	}
}