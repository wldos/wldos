/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.service;

import java.math.BigDecimal;
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
import com.wldos.base.Base;
import com.wldos.base.entity.EntityAssists;
import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.dto.PostPicture;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.model.Attachment;
import com.wldos.cms.model.IMeta;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.model.MainPicture;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Breadcrumb;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.Geographic;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.Post;
import com.wldos.cms.vo.PostMember;
import com.wldos.cms.vo.PostMeta;
import com.wldos.cms.vo.PostUnit;
import com.wldos.cms.vo.Product;
import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.cms.vo.TypeDomainTerm;
import com.wldos.common.Constants;
import com.wldos.common.dto.LevelNode;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ChineseUtils;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;
import com.wldos.common.vo.SelectOption;
import com.wldos.sys.base.dto.ContentExt;
import com.wldos.sys.base.dto.Term;
import com.wldos.sys.base.entity.KModelContent;
import com.wldos.sys.base.entity.KTermType;
import com.wldos.sys.base.enums.TempTypeEnum;
import com.wldos.sys.base.enums.TermTypeEnum;
import com.wldos.sys.base.service.ContentExtService;
import com.wldos.sys.base.service.ContentService;
import com.wldos.sys.core.service.RegionService;
import com.wldos.sys.base.service.TermService;
import com.wldos.sys.core.vo.City;
import com.wldos.support.storage.IStore;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.common.enums.FileAccessPolicyEnum;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
public class KCMSService extends Base {

	private final BeanCopier contCopier = BeanCopier.create(ContModelDto.class, Product.class, false);

	private final BeanCopier artCopier = BeanCopier.create(ContModelDto.class, Article.class, false);

	private final BeanCopier contExtCopier = BeanCopier.create(ContentExt.class, ContentExt.class, false);

	private final BeanCopier postMetaCopier = BeanCopier.create(ContModelDto.class, PostMeta.class, false);

	@Value("${wldos.file.pic.srcset}")
	private String thumbnail;

	@Value("${wldos.sidecar.config}")
	private String sideCar;

	private final ContentService contentService;

	private final PostService postService;

	private final PostmetaService postmetaService;

	private final ContentExtService contentExtService;

	private final TermService termService;

	private final RegionService regionService;

	public KCMSService(ContentService contentService, PostService postService, PostmetaService postmetaService, ContentExtService contentExtService,
			TermService termService, RegionService regionService) {
		this.contentService = contentService;
		this.postService = postService;
		this.postmetaService = postmetaService;
		this.contentExtService = contentExtService;
		this.termService = termService;
		this.regionService = regionService;
	}

	/**
	 * 作品列表页
	 *
	 * @param pageQuery 页面参数
	 * @return 分页数据
	 */
	public PageableResult<BookUnit> queryContentList(PageQuery pageQuery) {
		return this.postService.queryPostWithExtList(pageQuery);
	}

	// @todo 图片、附件类预处理和设置元数据存储

	private final BeanCopier postCopier = BeanCopier.create(Post.class, KPosts.class, false);

	/**
	 * 保存带扩展属性的内容
	 *
	 * @param post 帖子
	 * @param postType 帖子类型
	 * @param userId 用户id
	 * @param userIp 用户ip
	 */
	public Long insertSelective(Post post, String postType, Long userId, String userIp) {

		if (ObjectUtils.isBlank(post.getPostName())) // 创建标题别名
			post.setPostName(ChineseUtils.hanZi2Pinyin(post.getPostTitle(), true));

		// 为便于结构化处理，对于图片等附件的处理，要在上传文件和编辑文件时将设置数据存储到post metadata中，在帖子渲染时再读出

		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);
		// 存在别名，放弃设置别名
		if (this.postService.existsByPostName(posts.getPostName(), posts.getId()))
			posts.setPostName("");

		// 根据分类id获取内容类型，确定模板，统一所有带正文的内容模板为作品集-篇章结构，内容类型用于总览分类，不可以跨类型选择分类目录，同类型可以有多个分类
		KModelContent content = this.termService.queryContentTypeByTermType(Long.parseLong(post.getTermTypeIds().get(0).getValue()));
		posts.setPostType(postType);
		posts.setContentType(content.getContentCode());
		posts.setPostStatus(PostStatusEnum.PUBLISH.toString()); // @todo 后期加上审核功能
		Long id = this.nextId();
		EntityAssists.beforeInsert(posts, id, userId, userIp, false);

		// @todo 考虑嵌入过滤器hook：posts = applyFilter("savePost", posts);

		this.postService.insertSelective(posts);

		// 批量关联帖子分类并计数
		List<Long> termTypeIds = post.getTermTypeIds().stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
		this.termService.saveTermObject(termTypeIds, id);

		List<String> tagIds = post.getTagIds();
		if (tagIds != null) {
			List<Long> newTagIds = this.termService.handleTag(tagIds, content.getId());
			this.termService.saveTermObject(newTagIds, id);
		}

		List<ContentExt> contentExt = post.getContentExt();

		if (ObjectUtils.isBlank(contentExt)) {
			contentExt = new ArrayList<>();
			this.appendPubMeta(contentExt); // 添加后台静默处理的元数据，比如查看数
		}

		// @todo 考虑嵌入过滤器hook：contentExt = applyFilter("saveContentExt", contentExt);
		// 保存扩展属性
		this.createPostMeta(contentExt, id);

		return id;
	}

	/**
	 * 更新
	 *
	 * @param post post vo
	 * @param userId user id
	 * @param userIp user ip
	 */
	public void update(Post post, Long userId, String userIp) {
		if (ObjectUtils.isBlank(post.getPostName())) // 创建标题别名
			post.setPostName(ChineseUtils.hanZi2Pinyin(post.getPostTitle(), true));

		List<ContentExt> contentExt = post.getContentExt();

		if (!ObjectUtils.isBlank(contentExt) && !contentExt.isEmpty()) {
			// @todo 考虑嵌入过滤器hook：contentExt = applyFilter("updateContentExt", contentExt); 图片等扩展属性需要特殊处理
			List<KPostmeta> postMetas = this.postmetaService.queryPostMetaByPostId(post.getId());
			// 需要根据postId+metaKey取出可能存在的postMeta.id，如果不存在，以新属性创建
			Map<String, Long> keyMap = postMetas.parallelStream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getId));
			// 保存扩展属性
			postMetas = contentExt.parallelStream().map(ext -> {
				KPostmeta postMeta = new KPostmeta();
				String key = ext.getMetaKey();
				String value = ext.getMetaValue();
				if (ObjectUtils.isBlank(value))
					return null;
				postMeta.setId(keyMap.get(key));
				postMeta.setPostId(post.getId());
				postMeta.setMetaKey(key);
				postMeta.setMetaValue(value);
				return postMeta;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			List<KPostmeta> postMetasU = postMetas.parallelStream().filter(p -> p.getId() != null).collect(Collectors.toList());
			List<KPostmeta> postMetasN = postMetas.parallelStream().filter(p -> p.getId() == null).collect(Collectors.toList());
			if (!postMetasN.isEmpty())
				postMetasN.forEach(pm -> pm.setId(this.nextId()));
			this.postmetaService.insertSelectiveAll(postMetasN);
			if (!postMetasU.isEmpty())
				this.postmetaService.updateAll(postMetasU);
		}

		// 处理分类和标签
		List<SelectOption> tIds = post.getTermTypeIds();
		if (tIds != null) {
			List<Long> termTypeIds = tIds.stream().map(o -> Long.parseLong(o.getValue())).collect(Collectors.toList());
			this.termService.updateTermObject(termTypeIds, post.getId(), TermTypeEnum.CATEGORY.toString());
		}

		List<String> tagIds = post.getTagIds();
		if (tagIds != null) {
			KPosts dbPost = this.postService.findById(post.getId());
			KModelContent content = this.termService.queryModelContentByTypeCode(dbPost.getContentType());
			List<Long> newTagIds = this.termService.handleTag(tagIds, content.getId());
			this.termService.updateTermObject(newTagIds, post.getId(), TermTypeEnum.TAG.toString());
		}

		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);
		EntityAssists.beforeUpdated(posts, userId, userIp);

		// @todo 考虑嵌入过滤器hook：posts = applyFilter("updatePost", posts);

		// 存在别名，放弃设置别名
		if (this.postService.existsByPostName(posts.getPostName(), posts.getId()))
			posts.setPostName("");

		this.postService.update(posts);
	}

	/**
	 * 删除内容
	 *
	 * @param post 帖子
	 * @return 反馈
	 */
	public String delete(Post post) {
		// @todo 考虑嵌入操作hook: execHook("deletePost", post);
		// 删除检查：如果是作品，检查是否存在内容，如果是内容，直接删除。
		List<Chapter> chapters = this.postService.queryPostsByParentId(post.getId(), DeleteFlagEnum.NORMAL.toString());
		if (chapters != null && chapters.size() > 0)
			return "存在内容，请先删除内容";
		// 逻辑删
		this.postService.deleteById(post.getId());
		return "ok";
		// 若是物理删，需要级联删除meta扩展属性
	}

	/**
	 * 查询详情页信息
	 *
	 * @param pid 产品id
	 * @return 产品信息
	 */
	public Product productInfo(Long pid) {
		this.updatePubMeta(pid);

		// 根据id找到内容类型、模板类型 用于前端展示
		ContModelDto contBody = this.postService.queryContModel(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPostmeta> metas = this.postmetaService.queryPostMetaByPostId(pid);

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
				exact(metas, pic)).collect(Collectors.toList());
		product.setMainPic(pictures);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(product, pubMeta);

		return (Product) this.handleContent(product, metas, contBody, pid);
	}

	/**
	 * 统一创建seo和面包屑
	 *
	 * @param iMeta 内容类型接口
	 * @param termTypeId 直属分类id，多者取首
	 */
	public void genSeoAndCrumbs(IMeta iMeta, Long termTypeId) {
		SeoCrumbs seoCrumbs = new SeoCrumbs();
		seoCrumbs.setTitle(iMeta.getPostTitle());
		seoCrumbs.setDescription(this.postService.genPostExcerpt(iMeta.getPostContent(), 140));
		StringBuilder keywords = new StringBuilder();
		List<Term> tags = iMeta.getTags();
		if (!ObjectUtils.isBlank(tags)) { // 根据标签生成关键词
			tags.forEach(tag -> keywords.append(tag.getName()).append(","));
			keywords.deleteCharAt(keywords.length() - 1);
			seoCrumbs.setKeywords(keywords.toString());
		}
		else
			seoCrumbs.setKeywords(iMeta.getPostTitle());
		// 开始创建面包屑: 直属分类及所有父级分类
		List<LevelNode> nodes = this.termService.queryTermTreeByChildId(termTypeId);
		List<Long> termTypeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
		List<Term> terms = this.termService.queryAllByTermTypeIds(termTypeIds);
		List<Breadcrumb> crumb =
				terms.parallelStream().map(term -> {
					Breadcrumb bc = new Breadcrumb();
					String termPath = this.getPathByTermType(term.getClassType());
					bc.setPath(iMeta instanceof Product ? "/product/" + iMeta.getContentType() + termPath + term.getSlug()
							: "/archives/" + iMeta.getContentType() + termPath + term.getSlug());
					bc.setBreadcrumbName(term.getName());
					return bc;
				}).collect(Collectors.toList());
		seoCrumbs.setCrumbs(crumb);

		iMeta.setSeoCrumbs(seoCrumbs);
	}

	/**
	 * 根据内容领域和分类法(分类目录、标签等)获取tdk和面包屑数据
	 *
	 * @param params 领域分类参数
	 * @return tdk和面包屑数据
	 */
	public SeoCrumbs genSeoCrumbs(TypeDomainTerm params) {
		SeoCrumbs seoCrumbs = new SeoCrumbs();
		String contentType = params.getContentType();
		String slugTerm = params.getSlugTerm();
		String tempType = params.getTempType(); // 模板类型决定返回的面包屑链接到的模板前缀

		if (ObjectUtils.isBlank(slugTerm)) {
			KModelContent content = this.contentService.findByContentCode(contentType);
			String name = content == null ? "所有领域" : content.getContentName();
			seoCrumbs.setTitle(name);
			seoCrumbs.setDescription("内容领域：" + name);
			seoCrumbs.setKeywords(name);
			// 内容领域
			List<Breadcrumb> crumb = new ArrayList<>();
			Breadcrumb bcb = new Breadcrumb();
			bcb.setPath("/" + tempType + "/" + contentType);
			bcb.setBreadcrumbName(name);
			seoCrumbs.setCrumbs(crumb);
			return seoCrumbs;
		}

		// 获取当前分类项类型
		Term term = this.termService.queryTermBySlugTerm(slugTerm);

		String name = term.getName();
		seoCrumbs.setTitle(name);
		seoCrumbs.setDescription(this.getTemplateTypeByValue(tempType).getLabel() + "分类：" + name);
		seoCrumbs.setKeywords(name);

		// 开始创建面包屑: 直属分类及所有父级分类
		List<LevelNode> nodes = this.termService.queryTermTreeByChildId(term.getTermTypeId());
		List<Long> termTypeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());

		List<Term> terms = this.termService.queryAllByTermTypeIds(termTypeIds);
		List<Breadcrumb> crumb =
				terms.parallelStream().map(t -> {
					Breadcrumb bc = new Breadcrumb();
					bc.setPath("/" + tempType + "/" + contentType + this.getPathByTermType(t.getClassType()) + t.getSlug());
					bc.setBreadcrumbName(t.getName());
					return bc;
				}).collect(Collectors.toList());
		seoCrumbs.setCrumbs(crumb);
		return seoCrumbs;
	}

	private TempTypeEnum getTemplateTypeByValue(String value) {
		if (TempTypeEnum.PRODUCT.getValue().equals(value))
			return TempTypeEnum.PRODUCT;
		if (TempTypeEnum.ARCHIVES.getValue().equals(value))
			return TempTypeEnum.ARCHIVES;
		return TempTypeEnum.INFO;
	}

	// 目前支持目录和标签，根据分类项的类型决定面包屑的url类型
	private String getPathByTermType(String termType) {
		return TermTypeEnum.CATEGORY.toString().equals(termType) ? "/category/" : "/tag/";
	}

	public void termAndTagHandle(IMeta iMeta, Long pid) {
		List<Term> termsType = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.CATEGORY.toString());
		// 分类目录
		List<Long> termTypeIds = termsType.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
		iMeta.setTermTypeIds(termTypeIds);
		// 标签
		List<Term> terms = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.TAG.toString());

		iMeta.setTags(terms);
	}

	public void populateMeta(IMeta product, Map<String, String> pubMeta) {
		product.setCover(pubMeta.get(KModelMetaKey.PUB_META_KEY_COVER));
		String subTitle = pubMeta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE);
		if (!ObjectUtils.isBlank(subTitle))
			product.setSubTitle(subTitle);
		String ornPrice = pubMeta.get(KModelMetaKey.PUB_META_KEY_ORN_PRICE);
		if (!ObjectUtils.isBlank(ornPrice))
			product.setOrnPrice(new BigDecimal(ornPrice));
		String pstPrice = pubMeta.get(KModelMetaKey.PUB_META_KEY_PST_PRICE);
		if (!ObjectUtils.isBlank(pstPrice))
			product.setOrnPrice(new BigDecimal(ornPrice));
		String contact = pubMeta.get(KModelMetaKey.PUB_META_KEY_CONTACT);
		if (!ObjectUtils.isBlank(contact))
			product.setContact(ObjectUtils.hideName(contact));
		String telephone = pubMeta.get(KModelMetaKey.PUB_META_KEY_TELEPHONE);
		if (!ObjectUtils.isBlank(telephone)) {
			product.setTelephone(ObjectUtils.hidePhone(telephone));
			product.setRealNo(telephone);
		}
		String city = pubMeta.get(KModelMetaKey.PUB_META_KEY_CITY);
		if (!ObjectUtils.isBlank(city)) {
			City region = this.regionService.queryRegionInfoByCode(city);
			product.setCity(region.getName());
			product.setProv(region.getProvName());
		}
		String county = pubMeta.get(KModelMetaKey.PUB_META_KEY_COUNTY);
		if (!ObjectUtils.isBlank(county))
			product.setCounty(county);
		String views = pubMeta.get(KModelMetaKey.PUB_META_KEY_VIEWS);
		if (!ObjectUtils.isBlank(views))
			product.setViews(views);
	}

	public IMeta handleContent(IMeta iMeta, List<KPostmeta> metas, ContModelDto contBody, Long pid) {
		// 根据自定义内容类型找到自定义扩展属性集
		List<ContentExt> contentExt = this.contentExtService.queryExtPropsByContentId(contBody.getContentId());
		// 填充自定义属性
		contentExt = this.getContentExt(contentExt, metas);

		iMeta.setContentExt(contentExt);

		// 找到附件类，以此作为依据析取附件类元数据，这要求每一个 1.附件也要在帖子表存储，对附件的处理采用宽进严出，
		//  即使删帖，也不级联删除附件，附件的删除统一在用户文件库[媒体库]维护(反向级联：即不能删除被引用的文件)，
		// 帖子中图片编辑或删除时，都不级联更新附件(考虑复用和性能)，后期可能再优化
		List<ContModelDto> contAttach = this.postService.queryContAttach(pid);

		if (ObjectUtils.isBlank(contAttach))
			return iMeta;

		List<Long> pIds = contAttach.parallelStream().map(ContModelDto::getId).collect(Collectors.toList());
		// 查询内容主体附件的扩展属性值
		List<KPostmeta> metasAttach = this.postmetaService.queryPostMetaByPostIds(pIds);

		// 处理附件、图片类
		Map<Long, List<KPostmeta>> attMetaGroup = metasAttach.stream()
				.collect(Collectors.groupingBy(KPostmeta::getPostId)); // 按附件id归集自身元数据

		if (ObjectUtils.isBlank(attMetaGroup))
			return iMeta;

		List<Attachment> attachments = this.getAttachments(attMetaGroup);

		// 附件没必要输出客户端，仅作内容再处理：应用附件、图片的元数据作为展示格式，这要求内容的保存要根据附件path对内容相应附件html
		// 元素的样式作标记(暂时以附件url)，并在此处替换，以实现附件的复用和灵活更新
		String content = iMeta.getPostContent();
		if (ObjectUtils.isBlank(content))
			return iMeta;

		content = filterContent(attachments, content);

		iMeta.setPostContent(content);

		return iMeta;
	}

	public MainPicture exact(List<KPostmeta> metas, String pic) {
		MainPicture mp = new MainPicture();
		mp.setKey(pic);
		for (KPostmeta meta : metas) {
			if (meta.getMetaKey().equals(pic)) {
				mp.setUrl(this.store.getFileUrl(meta.getMetaValue(), null));
				break;
			}
		}
		if (mp.getUrl() == null) {
			String noPicUrl = this.store.getFileUrl(MainPicture.noMainPicPath, "");
			mp.setUrl(noPicUrl);
		}

		return mp;
	}

	private String filterContent(List<Attachment> attachments, String content) {
		for (Attachment a : attachments) {
			String attMeta = a.getAttachMetadata();
			try {
				PostPicture picture = new ObjectMapper().readValue(attMeta, new TypeReference<PostPicture>() {});

				// srcset配合居中、自适应css实现跨终端展示最优尺寸缩略图
				String template = "srcset=\"%s %sw, %s %sw, %s %sw, %s %sw\" sizes=\"(max-width: %spx) 100vw, %spx\"";
				String url = this.store.getFileUrl(picture.getPath(), null);
				List<Thumbnail> thumbnails = picture.getSrcset();
				String url300 = this.store.getFileUrl(thumbnails.get(0).getPath(), null);
				String url1024 = this.store.getFileUrl(thumbnails.get(1).getPath(), null);
				String url768 = this.store.getFileUrl(thumbnails.get(2).getPath(), null);
				int index = content.indexOf(url);
				if (index <= -1)
					continue;
				String contentPre = content.substring(0, index);
				String contentFix = content.substring(index);
				String width = contentFix.substring(contentFix.indexOf("width="),
						contentFix.indexOf("height=")).trim().replace("width=\"", "").replace("\"", "");

				String srcset = String.format(template, url, picture.getWidth(), url300, thumbnails.get(0).getWidth(),
						url768, thumbnails.get(2).getWidth(), url1024, thumbnails.get(1).getWidth(),
						ObjectUtils.isBlank(width) ? picture.getWidth() : width);

				content = contentPre + contentFix.replaceFirst("/>", srcset + "/>");
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		return content;
	}

	void createPostMeta(List<ContentExt> contentExt, Long id) {
		List<KPostmeta> postMetas = contentExt.parallelStream().map(ext -> {
			KPostmeta postMeta = new KPostmeta();
			String key = ext.getMetaKey();
			String value = ext.getMetaValue();
			if (ObjectUtils.isBlank(value))
				return null;
			postMeta.setId(this.nextId());
			postMeta.setPostId(id);
			postMeta.setMetaKey(key);
			postMeta.setMetaValue(value);
			return postMeta;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		this.postmetaService.insertSelectiveAll(postMetas);
	}

	/**
	 * 查询作品
	 *
	 * @param bookId works id
	 * @return a works info
	 */
	public Book queryBook(Long bookId) {
		KPosts post = this.postService.findById(bookId);
		List<Chapter> chapters = this.postService.queryPostsByParentId(bookId, DeleteFlagEnum.NORMAL.toString());

		Book book = new Book();
		book.setId(post.getId());
		book.setPostTitle(post.getPostTitle());
		book.setContentType(post.getContentType());
		book.setChapter(chapters);

		return book;
	}

	/**
	 * 保存帖子图片等附件的元信息
	 *
	 * @param attach 附件
	 * @param post 附件帖子
	 */
	public void savePostAttach(Attachment attach, KPosts post) {

		// 创建附件元数据
		Map<String, Object> objectMap = ObjectUtils.toMap(attach);
		List<KPostmeta> postMetas =
				objectMap.entrySet().parallelStream().map(entry -> {
					KPostmeta postMeta = new KPostmeta();
					postMeta.setId(this.nextId());
					postMeta.setPostId(post.getId());
					postMeta.setMetaKey(entry.getKey());
					postMeta.setMetaValue(entry.getValue().toString());

					return postMeta;
				}).collect(Collectors.toList());

		this.postService.insertSelective(post);

		this.postmetaService.insertSelectiveAll(postMetas);
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
			params.put(KModelMetaKey.SIDECAR_CONF_TYPE, ContModelTypeEnum.BOOK.toString());
			params.put(KModelMetaKey.SIDECAR_CONF_SORTER, "{\"id\":\"descend\"}");
		}

		params.put(KModelMetaKey.SIDECAR_CONF_PAGENO, 1); // 默认取第一页

		return params;
	}

	/**
	 * 门户跨域根据分类目录的别名查询分类目录下的作品列表，应包含子分类下的内容
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离
	 *
	 * @param contentType 内容类型
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<BookUnit> queryProductPortalByContent(String contentType, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(contentType)) {
			List<Term> terms = this.termService.findTagByContCode(contentType);
			List<Object> termTypeIds = terms.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(termTypeIds, pageQuery);
		}

		return this.postService.queryPostWithExtList(pageQuery);
	}

	/**
	 * 门户跨域根据分类目录的别名查询分类目录下的作品列表，应包含子分类下的内容
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<BookUnit> queryProductPortalByCategory(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			List<Object> ids = this.queryOwnIds(termType.getId());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.postService.queryPostWithExtList(pageQuery);
	}

	/**
	 * 根据实体字段自由查询作品列表，实体表：KPosts、KTermObject
	 *
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<BookUnit> queryProductDomain(PageQuery pageQuery) {

		return this.postService.queryPostWithExtList(pageQuery);
	}

	/**
	 * 根据指定分类目录的别名查询分类目录下的作品列表，应包含子分类下的内容
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 作品列表页
	 */
	public PageableResult<BookUnit> queryProductCategory(String slugCategory, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);

		List<Object> ids = this.queryOwnIds(termType.getId());

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryPostWithExtList(pageQuery);
	}

	/**
	 * 门户跨域根据分类目录的别名查询分类目录下的内容列表，应包含子分类下的内容
	 * 门户有且仅有一个，并且跨域、跨租户查询，没有数据隔离，应该根据内容类型查询，具体细分要按 类型/租户/域/分类 的维度钻取
	 *
	 * @param contentType 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PostUnit> queryArchivesPortal(String contentType, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(contentType)) {
			List<Term> terms = this.termService.findCategoryByContCode(contentType);
			List<Object> termTypeIds = terms.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(termTypeIds, pageQuery);
		}

		return this.postService.queryArchives(pageQuery);
	}

	/**
	 * 门户跨域根据分类目录的别名查询分类目录下的内容列表，应包含子分类下的内容
	 * 门户有且仅有一个，并且跨域查询，没有数据隔离
	 *
	 * @param slugCategory 某个分类目录别名
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PostUnit> queryArchivesCategoryPortal(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtils.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			List<Object> ids = this.queryOwnIds(termType.getId());
			// 查询分类及其子分类
			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.postService.queryArchives(pageQuery);
	}

	/**
	 * 自由组装条件查询内容列表
	 *
	 * @param pageQuery 分页查询参数，支持KPosts、KTermObjects所有字段
	 * @return 分类存档列表页
	 */
	public PageableResult<PostUnit> queryArchivesDomain(PageQuery pageQuery) {

		return this.postService.queryArchives(pageQuery);
	}

	/**
	 * 查询指定分类目录下的内容列表，应包含子分类下的内容
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PostUnit> queryArchivesCategory(String slugCategory, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
		List<Object> ids = this.queryOwnIds(termType.getId());

		if (ids.isEmpty())
			return new PageableResult<>();

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryArchives(pageQuery);
	}

	/**
	 * 查询指定标签的内容列表
	 *
	 * @param pageQuery 分页查询参数
	 * @return 分类存档列表页
	 */
	public PageableResult<PostUnit> queryArchivesTag(String slugTag, PageQuery pageQuery) {
		KTermType termType = this.termService.queryTermTypeBySlug(slugTag);
		pageQuery.pushParam("termTypeId", termType.getId());

		return this.postService.queryArchives(pageQuery);
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
	public PageableResult<PostUnit> queryArchivesUserDomain(Long domainId, PageQuery pageQuery) {
		pageQuery.pushParam(Constants.COMMON_KEY_DOMAIN_COLUMN, domainId);

		return this.postService.queryArchivesUser(pageQuery);
	}

	/**
	 * 根据作品id查询作品下的章节内容
	 *
	 * @param bookId 作品id
	 * @param pageQuery 分页查询参数
	 * @return 作品章节存档列表
	 */
	public PageableResult<PostUnit> queryBookChapter(Long bookId, PageQuery pageQuery) {
		pageQuery.pushParam("parentId", bookId);
		return this.postService.queryArchives(pageQuery);
	}

	/**
	 * 查询篇章信息
	 *
	 * @param pid 帖子id
	 * @return 篇章实体
	 */
	public Article queryArticle(Long pid) { // @todo 以id访问业务对象，应该检查域隔离，防止恶意跨域请求，暂不处理
		this.updatePubMeta(pid);
		// 根据id找到内容类型、模板类型 用于前端展示
		ContModelDto contBody = this.postService.queryContModel(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPostmeta> metas = this.postmetaService.queryPostMetaByPostId(pid);

		// 合并主体信息
		Article article = new Article();
		this.artCopier.copy(contBody, article, null);

		// 查询作者
		PostMember member = this.postService.queryMemberByPostId(pid);
		article.setMember(member);

		// 处理分类和标签
		this.termAndTagHandle(article, pid);

		// 生成seo和面包屑数据
		List<Long> tIds = article.getTermTypeIds();
		this.genSeoAndCrumbs(article, tIds.get(0)); // 多者取首

		// 查询上下篇、相关篇章
		this.setPrevNextAndRelated(pid, article);

		// 析取独立公共扩展属性
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(article, pubMeta);

		return (Article) this.handleContent(article, metas, contBody, pid);
	}

	private void setPrevNextAndRelated(Long pid, Article article) {
		String postType = article.getPostType();
		List<Long> termTypeIds = article.getTermTypeIds();
		MiniPost prev;
		MiniPost next;
		if (ContModelTypeEnum.POST.toString().equals(postType)) {
			if (ObjectUtils.isBlank(termTypeIds))
				return;
			prev = this.postService.queryPrevPost(pid, termTypeIds.get(0));
			next = this.postService.queryNextPost(pid, termTypeIds.get(0));
		}
		else {
			Long parentId = article.getParentId();
			prev = this.postService.queryPrevChapter(pid, parentId);
			next = this.postService.queryNextChapter(pid, parentId);
		}

		article.setPrev(prev);
		article.setNext(next);

		if (ObjectUtils.isBlank(termTypeIds))
			return;
		String contentType = article.getContentType();
		List<Term> tags = article.getTags();
		List<Long> tagTermTypeIds = ObjectUtils.isBlank(tags) ? termTypeIds : tags.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
		List<MiniPost> relPosts = this.postService.queryRelatedPosts(postType, contentType, tagTermTypeIds, 8);
		if (ObjectUtils.isBlank(relPosts)) {
			relPosts = this.postService.queryRelatedPosts(postType, contentType, termTypeIds, 8);
		}
		article.setRelPosts(relPosts);
	}

	public List<ContentExt> getContentExt(List<ContentExt> contentExt, List<KPostmeta> metas) {
		return contentExt.parallelStream().map(cont -> { // 填充自定义属性
			ContentExt ctx = null;
			for (KPostmeta meta : metas) {
				if (meta.getMetaKey().equals(cont.getMetaKey())) {
					ctx = new ContentExt();
					this.contExtCopier.copy(cont, ctx, null);
					ctx.setMetaValue(meta.getMetaValue());
					ctx.setPostId(meta.getPostId());
					ctx.setMetaId(meta.getId());
					break;
				}
			}
			return ctx;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<Attachment> getAttachments(Map<Long, List<KPostmeta>> attMetaGroup) {
		return attMetaGroup.values().stream().map(curMetas -> {
			// 元数据自身枚举不可能为空，故省略空处理
			Map<String, String> attMeta = curMetas.stream()
					.collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

			Attachment attach = new Attachment();
			attach.setAttachPath(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_PATH));
			attach.setAttachMetadata(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_METADATA));
			attach.setAttachFileAlt(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_FILE_ALT));

			return attach;
		}).filter(a -> !ObjectUtils.isBlank(a.getAttachMetadata())).collect(Collectors.toList());
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
	 * @param contentExtList 扩展属性列表
	 */
	void appendPubMeta(List<ContentExt> contentExtList) {
		ContentExt contentExt = new ContentExt();
		contentExt.setMetaKey(KModelMetaKey.PUB_META_KEY_VIEWS);
		contentExt.setMetaValue("0");

		contentExtList.add(contentExt);
	}

	/**
	 * 更新公共静默元数据
	 *
	 * @param pid 帖子id
	 */
	public void updatePubMeta(Long pid) { // @todo 需要开启缓存
		if (!this.postmetaService.isExistsViews(pid)) {
			List<ContentExt> contentExtList = new ArrayList<>();
			this.appendPubMeta(contentExtList);
			this.createPostMeta(contentExtList, pid);
		}
		else
			this.postmetaService.increasePostViews(1, pid);
	}

	/**
	 * 查询内容信息
	 *
	 * @param pid 内容id
	 * @return 内容信息
	 */
	public PostMeta postInfo(Long pid) {

		// 根据id找到内容类型、模板类型 用于前端展示
		ContModelDto contBody = this.postService.queryContModel(pid);

		// 查询内容主体的扩展属性值（含公共扩展(1封面、4主图)和自定义扩展）
		List<KPostmeta> metas = this.postmetaService.queryPostMetaByPostId(pid);

		// 合并主体信息
		PostMeta post = new PostMeta();
		this.postMetaCopier.copy(contBody, post, null);
		// 取出分类和标签
		this.termAndTagFromPost(post, pid);

		// 扩展属性 : 考虑静态模型 和 动态模型 的处理方式
		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

		// 处理地域信息
		String city = pubMeta.get(KModelMetaKey.PUB_META_KEY_CITY);
		if (!ObjectUtils.isBlank(city)) {
			City region = this.regionService.queryRegionInfoByCode(city);
			Geographic geo = new Geographic(region);
			post.setGeographic(geo);
		}

		// 图片(主图和封面)处理url
		String[] mainPics = { KModelMetaKey.PUB_META_KEY_MAIN_PIC1, KModelMetaKey.PUB_META_KEY_MAIN_PIC2, KModelMetaKey.PUB_META_KEY_MAIN_PIC3,
				KModelMetaKey.PUB_META_KEY_MAIN_PIC4, KModelMetaKey.PUB_META_KEY_COVER };
		Arrays.stream(mainPics).forEach(pic -> {
			if (!pubMeta.containsKey(pic)) {
				pubMeta.put(pic, MainPicture.noMainPicPath);
			}
		});
		// 设置ossUrl，用于前端拼装文件类完整url
		pubMeta.put(IStore.KEY_OSS_URL, this.store.genOssUrl(FileAccessPolicyEnum.PUBLIC));

		post.setContentExt(pubMeta);

		return post;
	}

	private void termAndTagFromPost(PostMeta post, Long pid) {
		List<Term> termsType = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.CATEGORY.toString());
		// 分类目录
		List<SelectOption> termTypeIds = termsType.parallelStream().map(t -> new SelectOption(t.getName(), t.getTermTypeId().toString())).collect(Collectors.toList());
		post.setTermTypeIds(termTypeIds);
		// 标签
		List<Term> terms = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.TAG.toString());
		List<String> tagIds = terms.parallelStream().map(Term::getName).collect(Collectors.toList());

		post.setTagIds(tagIds);
	}

	public boolean isSameContentType(List<Long> termTypeIds) {
		return this.termService.isSameContentType(termTypeIds);
	}

	public boolean isSameContentType(List<Long> termTypeIds, Long postId) {
		ContModelDto post = this.postService.queryContModel(postId);
		return this.termService.isSameContentType(termTypeIds, post.getContentId());
	}
}