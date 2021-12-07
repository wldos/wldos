/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.cms.dto.LevelNode;
import com.wldos.cms.entity.KTermType;
import com.wldos.cms.entity.KTerms;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.enums.PostTypeEnum;
import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.dto.PostPicture;
import com.wldos.cms.dto.Thumbnail;
import com.wldos.cms.entity.KModelContent;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.TermTypeEnum;
import com.wldos.cms.model.Attachment;
import com.wldos.cms.model.IMeta;
import com.wldos.cms.model.KModelMetaKey;
import com.wldos.cms.model.MainPicture;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Book;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Breadcrumb;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.ContentExt;
import com.wldos.cms.vo.SeoCrumbs;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.Post;
import com.wldos.cms.vo.PostMember;
import com.wldos.cms.vo.PostUnit;
import com.wldos.cms.vo.Product;
import com.wldos.cms.vo.Term;
import com.wldos.cms.vo.TypeDomainTerm;
import com.wldos.support.Base;
import com.wldos.support.Constants;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.DateUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.TreeUtil;
import com.wldos.support.vo.ViewNode;
import com.wldos.system.core.service.DomainService;
import com.wldos.system.core.service.RegionService;
import com.wldos.system.enums.RedisKeyEnum;
import com.wldos.system.storage.IStore;
import com.wldos.system.vo.City;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class KCMSService extends Base {

	private final BeanCopier contCopier = BeanCopier.create(ContModelDto.class, Product.class, false);

	private final BeanCopier artCopier = BeanCopier.create(ContModelDto.class, Article.class, false);

	private final BeanCopier contExtCopier = BeanCopier.create(ContentExt.class, ContentExt.class, false);

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

	private final DomainService domainService;

	private final IStore store;

	public KCMSService(ContentService contentService, PostService postService, PostmetaService postmetaService, ContentExtService contentExtService,
			TermService termService, RegionService regionService, DomainService domainService, IStore store) {
		this.contentService = contentService;
		this.postService = postService;
		this.postmetaService = postmetaService;
		this.contentExtService = contentExtService;
		this.termService = termService;
		this.regionService = regionService;
		this.domainService = domainService;
		this.store = store;
	}


	public PageableResult<BookUnit> queryContentList(PageQuery pageQuery) {
		return this.postService.queryPostWithExtList(pageQuery);
	}



	private final BeanCopier postCopier = BeanCopier.create(Post.class, KPosts.class, false);


	public Long insertSelective(Post post, Long userId, String userIp) {



		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);


		KModelContent content = this.termService.queryContentTypeByTermType(post.getTermTypeId());
		posts.setPostType(PostTypeEnum.BOOK.toString());
		posts.setContentType(content.getContentCode());
		posts.setPostStatus(PostStatusEnum.PUBLISH.toString());
		Long id = this.nextId();
		EntityAssists.beforeInsert(posts, id, userId, userIp, false);



		this.postService.insertSelective(posts);


		this.termService.saveTermObject(post.getTermTypeId(), id);

		List<ContentExt> contentExt = post.getContentExt();

		if (ObjectUtil.isBlank(contentExt)) {
			contentExt = new ArrayList<>();
			this.appendPubMeta(contentExt);
		}



		this.createPostMeta(contentExt, id);

		return id;
	}


	public void update(Post post, Long userId, String userIp) {
		List<ContentExt> contentExt = post.getContentExt();

		if (!ObjectUtil.isBlank(contentExt) && !contentExt.isEmpty()) {


			List<KPostmeta> postMetas = contentExt.parallelStream().map(ext -> {
				KPostmeta postMeta = new KPostmeta();
				String key = ext.getMetaKey();
				String value = ext.getMetaValue();
				if (ObjectUtil.isBlank(value) && ObjectUtil.isBlank(ext.getMetaId()))
					return null;
				postMeta.setId(ext.getMetaId());
				postMeta.setPostId(ext.getPostId());
				postMeta.setMetaKey(key);
				postMeta.setMetaValue(value);
				return postMeta;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			List<KPostmeta> postMetasU = postMetas.parallelStream().filter(p -> p.getId() != null).collect(Collectors.toList());
			List<KPostmeta> postMetasN = postMetas.parallelStream().filter(p -> p.getId() == null).collect(Collectors.toList());
			if (!postMetasN.isEmpty())
				this.postmetaService.insertSelectiveAll(postMetasN);
			if (!postMetasU.isEmpty())
				this.postmetaService.updateAll(postMetasU);
		}

		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);
		EntityAssists.beforeUpdated(posts, userId, userIp);



		this.postService.update(posts);
	}


	public void delete(Post post, Long curUserId, String userIp) {


		this.postService.deleteById(post.getId());


	}


	public Product productInfo(Long pid) {
		this.updatePubMeta(pid);


		ContModelDto contBody = this.postService.queryContModel(pid);


		List<KPostmeta> metas = this.postmetaService.queryPostMetaByPostId(pid);


		Product product = new Product();
		this.contCopier.copy(contBody, product, null);

		this.termAndTagHandle(product, pid);


		List<Long> tIds = product.getTermTypeIds();
		this.genSeoAndCrumbs(product, tIds.get(0));


		String[] mainPics = {KModelMetaKey.PUB_META_KEY_MAIN_PIC1, KModelMetaKey.PUB_META_KEY_MAIN_PIC2, KModelMetaKey.PUB_META_KEY_MAIN_PIC3,
				KModelMetaKey.PUB_META_KEY_MAIN_PIC4};
		List<MainPicture> pictures = Arrays.stream(mainPics).parallel().map(pic ->
				exact(metas, pic)).collect(Collectors.toList());
		product.setMainPic(pictures);


		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(product, pubMeta);

		return (Product) this.handleContent(product, metas, contBody, pid);
	}


	private void genSeoAndCrumbs(IMeta iMeta, Long termTypeId) {
		SeoCrumbs seoCrumbs = new SeoCrumbs();
		seoCrumbs.setTitle(iMeta.getPostTitle());
		seoCrumbs.setDescription(this.postService.genPostExcerpt(iMeta.getPostContent(), 140));
		StringBuilder keywords = new StringBuilder();
		List<Term> tags = iMeta.getTags();
		if (!ObjectUtil.isBlank(tags)) {
			tags.forEach(tag -> keywords.append(tag.getName()).append(","));
			keywords.deleteCharAt(keywords.length() - 1);
			seoCrumbs.setKeywords(keywords.toString());
		}

		List<LevelNode> nodes = this.termService.queryTermTreeByChildId(termTypeId);
		List<Long> termTypeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
		List<KTerms> terms = this.termService.queryKTermsByTermTypeIds(termTypeIds);
		List<Breadcrumb> crumb =
		terms.parallelStream().map(term -> {
			Breadcrumb bc = new Breadcrumb();
			bc.setPath(iMeta instanceof Product ? "/product/" + iMeta.getContentType() + "/category/" + term.getSlug()
					: "/archives/" + iMeta.getContentType() + "/category/" + term.getSlug());
			bc.setBreadcrumbName(term.getName());
			return bc;
		}).collect(Collectors.toList());
		seoCrumbs.setCrumbs(crumb);

		iMeta.setSeoCrumbs(seoCrumbs);
	}


	public SeoCrumbs genSeoCrumbs(TypeDomainTerm params) {
		SeoCrumbs seoCrumbs = new SeoCrumbs();
		String contentType = params.getContentType();
		String slugTerm = params.getSlugTerm();
		String tempType = params.getTempType();

		if (ObjectUtil.isBlank(slugTerm)) {
			KModelContent content = this.contentService.findByContentCode(contentType);
			String name = content == null ? "所有领域" : content.getContentName();
			seoCrumbs.setTitle(name);
			seoCrumbs.setDescription("内容领域：" + name);
			seoCrumbs.setKeywords(name);

			List<Breadcrumb> crumb = new ArrayList<>();
			Breadcrumb bcb = new Breadcrumb();
			bcb.setPath("/" + tempType + "/" + contentType);
			bcb.setBreadcrumbName(name);
			seoCrumbs.setCrumbs(crumb);
			return seoCrumbs;
		}


		Term term = this.termService.queryTermBySlugTerm(slugTerm);

		String name = term.getName();
		seoCrumbs.setTitle(name);
		seoCrumbs.setDescription("分类存档：" + name);
		seoCrumbs.setKeywords(name);


		List<LevelNode> nodes = this.termService.queryTermTreeByChildId(term.getTermTypeId());
		List<Long> termTypeIds = nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());

		List<KTerms> terms = this.termService.queryKTermsByTermTypeIds(termTypeIds);
		List<Breadcrumb> crumb =
				terms.parallelStream().map(t -> {
					Breadcrumb bc = new Breadcrumb();
					bc.setPath("/" + tempType + "/" + contentType + "/category/" + t.getSlug());
					bc.setBreadcrumbName(t.getName());
					return bc;
				}).collect(Collectors.toList());
		seoCrumbs.setCrumbs(crumb);
		return seoCrumbs;
	}

	private void termAndTagHandle(IMeta iMeta, Long pid) {
		List<Term> termsType = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.CATEGORY.toString());

		List<Long> termTypeIds = termsType.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
		iMeta.setTermTypeIds(termTypeIds);

		List<Term> terms = this.termService.findAllByObjectAndClassType(pid, TermTypeEnum.TAG.toString());

		iMeta.setTags(terms);
	}

	private void populateMeta(Product product, Map<String, String> pubMeta) {
		product.setCover(pubMeta.get(KModelMetaKey.PUB_META_KEY_COVER));
		String subTitle = pubMeta.get(KModelMetaKey.PUB_META_KEY_SUB_TITLE);
		if (!ObjectUtil.isBlank(subTitle))
			product.setSubTitle(subTitle);
		String ornPrice = pubMeta.get(KModelMetaKey.PUB_META_KEY_ORN_PRICE);
		if (!ObjectUtil.isBlank(ornPrice))
			product.setOrnPrice(new BigDecimal(ornPrice));
		String pstPrice = pubMeta.get(KModelMetaKey.PUB_META_KEY_PST_PRICE);
		if (!ObjectUtil.isBlank(pstPrice))
			product.setOrnPrice(new BigDecimal(ornPrice));
		String contact = pubMeta.get(KModelMetaKey.PUB_META_KEY_CONTACT);
		if (!ObjectUtil.isBlank(contact))
			product.setContact(ObjectUtil.hideName(contact));
		String telephone = pubMeta.get(KModelMetaKey.PUB_META_KEY_TELEPHONE);
		if (!ObjectUtil.isBlank(telephone)) {
			product.setTelephone(ObjectUtil.hidePhone(telephone));
			product.setRealNo(telephone);
		}
		String city = pubMeta.get(KModelMetaKey.PUB_META_KEY_CITY);
		if (!ObjectUtil.isBlank(city)) {
			City region = this.regionService.queryRegionInfoByCode(city);
			product.setCity(region.getName());
			product.setProv(region.getProvName());
		}
		String county = pubMeta.get(KModelMetaKey.PUB_META_KEY_COUNTY);
		if (!ObjectUtil.isBlank(county))
			product.setCounty(county);
		String views = pubMeta.get(KModelMetaKey.PUB_META_KEY_VIEWS);
		if (!ObjectUtil.isBlank(views))
			product.setViews(views);
	}

	private IMeta handleContent(IMeta iMeta, List<KPostmeta> metas, ContModelDto contBody, Long pid) {

		List<ContentExt> contentExt = this.contentExtService.queryExtPropsByContentId(contBody.getContentId());

		contentExt = this.getContentExt(contentExt, metas);

		iMeta.setContentExt(contentExt);




		List<ContModelDto> contAttach = this.postService.queryContAttach(pid);

		if (ObjectUtil.isBlank(contAttach))
			return iMeta;

		List<Long> pIds = contAttach.parallelStream().map(ContModelDto::getId).collect(Collectors.toList());

		List<KPostmeta> metasAttach = this.postmetaService.queryPostMetaByPostIds(pIds);


		Map<Long, List<KPostmeta>> attMetaGroup = metasAttach.stream()
				.collect(Collectors.groupingBy(KPostmeta::getPostId));

		if (ObjectUtil.isBlank(attMetaGroup))
			return iMeta;

		List<Attachment> attachments = this.getAttachments(attMetaGroup);



		String content = iMeta.getPostContent();
		if (ObjectUtil.isBlank(content))
			return iMeta;

		content = filterContent(attachments, content);

		iMeta.setPostContent(content);

		return iMeta;
	}

	private MainPicture exact(List<KPostmeta> metas, String pic) {
		MainPicture mp = new MainPicture();
		mp.setKey(pic);
		for (KPostmeta meta : metas) {
			if (meta.getMetaKey().equals(pic)) {
				mp.setUrl(this.store.getFileUrl(meta.getMetaValue(), null));
				break;
			}
		}
		if (mp.getUrl() == null) {
			String noPicUrl = this.store.getFileUrl("/noPic.jpg", "");
			mp.setUrl(noPicUrl);
		}

		return mp;
	}

	private String filterContent(List<Attachment> attachments, String content) {
		for (Attachment a : attachments) {
			String attMeta = a.getAttachMetadata();
			try {
				PostPicture picture = new ObjectMapper().readValue(attMeta, new TypeReference<PostPicture>() {});


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
						ObjectUtil.isBlank(width) ? picture.getWidth() : width);

				content = contentPre + contentFix.replaceFirst("/>", srcset + "/>");
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		return content;
	}


	public Long insertChapter(Post post, Long userId, String userIp) {



		KPosts posts = new KPosts();
		this.postCopier.copy(post, posts, null);

		Long id = this.nextId();
		EntityAssists.beforeInsert(posts, id, userId, userIp, false);



		this.postService.insertSelective(posts);

		List<ContentExt> contentExt = post.getContentExt();

		if (ObjectUtil.isBlank(contentExt)) {
			contentExt = new ArrayList<>();
			this.appendPubMeta(contentExt);
		}



		this.createPostMeta(contentExt, id);

		return id;
	}

	private void createPostMeta(List<ContentExt> contentExt, Long id) {
		List<KPostmeta> postMetas = contentExt.parallelStream().map(ext -> {
			KPostmeta postMeta = new KPostmeta();
			String key = ext.getMetaKey();
			String value = ext.getMetaValue();
			if (ObjectUtil.isBlank(value))
				return null;
			postMeta.setId(this.nextId());
			postMeta.setPostId(id);
			postMeta.setMetaKey(key);
			postMeta.setMetaValue(value);
			return postMeta;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		this.postmetaService.insertSelectiveAll(postMetas);
	}


	public Book queryBook(Long bookId) {
		KPosts post = this.postService.findById(bookId);
		List<Chapter> chapters = this.postService.queryPostsByParentId(bookId);

		Book book = new Book();
		book.setId(post.getId());
		book.setPostTitle(post.getPostTitle());
		book.setContentType(post.getContentType());
		book.setChapter(chapters);

		return book;
	}


	public Chapter queryChapter(Long chapterId) {
		KPosts post = this.postService.findById(chapterId);
		Chapter chapter = new Chapter();
		chapter.setParentId(post.getParentId());
		chapter.setId(post.getId());
		chapter.setPostTitle(ObjectUtil.string(post.getPostTitle()));
		chapter.setPostContent(ObjectUtil.string(post.getPostContent()));
		chapter.setPostStatus(post.getPostStatus());
		chapter.setContentType(post.getContentType());

		return chapter;
	}


	public Chapter createChapter(Post chapter, Long curUserId, String userIp) {
		chapter.setPostTitle(DateUtils.format(new Date(), DateUtils.TIME_PATTER));


		chapter.setPostStatus(PostStatusEnum.INHERIT.toString());
		chapter.setPostType(PostTypeEnum.CHAPTER.toString());
		Long id = this.insertChapter(chapter, curUserId, userIp);


		List<Term> termsType = this.termService.findAllByObjectAndClassType(chapter.getParentId(), TermTypeEnum.CATEGORY.toString());


		for (Term term : termsType) {
			Long termTypeId = term.getTermTypeId();
			this.termService.saveTermObject(termTypeId, id);
		}

		return this.queryChapter(id);
	}


	public void saveChapter(Post chapter, Long curUserId, String userIp) {
		this.update(chapter, curUserId, userIp);
	}


	public void savePostAttach(Attachment attach, KPosts post) {


		Map<String, Object> objectMap = ObjectUtil.toMap(attach);
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


	public List<Thumbnail> getSrcset() throws JsonProcessingException {

		return new ObjectMapper().readValue(this.thumbnail, new TypeReference<List<Thumbnail>>() {});
	}


	public Map<String, Object> readParamsSideCar(String pageName) throws JsonProcessingException {
		Map<String, Map<String, Object>> config = new ObjectMapper().readValue(this.sideCar, new TypeReference<Map<String, Map<String, Object>>>() {});
		Map<String, Object> params = null;

		if (ObjectUtil.isBlank(config) || ObjectUtil.isBlank(params = config.get(pageName))) {
			params = new HashMap<>();
			params.put(KModelMetaKey.SIDECAR_CONF_LIST_NUM, 10);
			params.put(KModelMetaKey.SIDECAR_CONF_TYPE, PostTypeEnum.BOOK.toString());
			params.put(KModelMetaKey.SIDECAR_CONF_SORTER, "{\"id\":\"descend\"}");
		}

		params.put(KModelMetaKey.SIDECAR_CONF_PAGENO, 1);

		return params;
	}


	public PageableResult<BookUnit> queryProductPortal(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtil.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			List<Object> ids = this.queryOwnIds(termType.getId());

			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.postService.queryPostWithExtList(pageQuery);
	}


	public PageableResult<BookUnit> queryProductDomain(String domain, PageQuery pageQuery) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		List<Object> ids = this.queryOwnIds(termTypeIds);

		if (ids.isEmpty())
			return new PageableResult<>();


		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryPostWithExtList(pageQuery);
	}


	public PageableResult<BookUnit> queryProductCategory(String domain, String slugCategory, PageQuery pageQuery) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);

		Long typeId = termType.getId();
		List<Object> ids = this.queryOwnIds(typeId);

		if (!termTypeIds.contains(typeId)) {
			termTypeIds = this.termService.queryChildIds(termTypeIds);


			ids = termTypeIds.parallelStream().filter(ids::contains).collect(Collectors.toList());

			if (ids.isEmpty())
				return new PageableResult<>();
		}

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryPostWithExtList(pageQuery);
	}


	public PageableResult<PostUnit> queryArchivesCategoryPortal(String slugCategory, PageQuery pageQuery) {
		if (!ObjectUtil.isBlank(slugCategory)) {
			KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);
			List<Object> ids = this.queryOwnIds(termType.getId());

			this.filterByParentTermTypeId(ids, pageQuery);
		}

		return this.postService.queryArchives(pageQuery);
	}


	public PageableResult<PostUnit> queryArchivesDomain(String domain, PageQuery pageQuery) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		List<Object> ids = this.queryOwnIds(termTypeIds);

		if (ids.isEmpty())
			return new PageableResult<>();


		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryArchives(pageQuery);
	}


	public PageableResult<PostUnit> queryArchivesCategory(String domain, String slugCategory, PageQuery pageQuery) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		KTermType termType = this.termService.queryTermTypeBySlug(slugCategory);

		Long typeId = termType.getId();
		List<Object> ids = this.queryOwnIds(typeId);

		if (!termTypeIds.contains(typeId)) {
			termTypeIds = this.termService.queryChildIds(termTypeIds);

			ids = termTypeIds.parallelStream().filter(ids::contains).collect(Collectors.toList());

			if (ids.isEmpty())
				return new PageableResult<>();
		}

		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryArchives(pageQuery);
	}


	private void filterByParentTermTypeId(List<Object> termTypeIds, PageQuery pageQuery) {

		pageQuery.pushFilter("termTypeId", termTypeIds);
	}


	private List<Object> queryOwnIds(Long termTypeId) {
		List<LevelNode> nodes = this.termService.queryTermTreeByParentId(termTypeId);

		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}


	private List<Object> queryOwnIds(List<Long> termTypeIds) {
		List<LevelNode> nodes = new ArrayList<>();
		for (Long id : termTypeIds) {
			List<LevelNode> temp = this.termService.queryTermTreeByParentId(id);
			nodes.addAll(temp);
		}


		return nodes.parallelStream().map(LevelNode::getId).collect(Collectors.toList());
	}


	public PageableResult<PostUnit> queryArchivesUserDomain(String domain, PageQuery pageQuery) {
		List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);

		List<Object> ids = this.queryOwnIds(termTypeIds);

		if (ids.isEmpty())
			return new PageableResult<>();


		this.filterByParentTermTypeId(ids, pageQuery);

		return this.postService.queryArchivesUser(pageQuery);
	}


	public PageableResult<PostUnit> queryBookChapter(Long bookId, PageQuery pageQuery) {
		pageQuery.pushParam("parentId", bookId);
		return this.postService.queryArchives(pageQuery);
	}


	public Article queryArticle(Long pid) {
		this.updatePubMeta(pid);

		ContModelDto contBody = this.postService.queryContModel(pid);


		List<KPostmeta> metas = this.postmetaService.queryPostMetaByPostId(pid);


		Article article = new Article();
		this.artCopier.copy(contBody, article, null);


		PostMember member = this.postService.queryMemberByPostId(pid);
		article.setMember(member);


		this.termAndTagHandle(article, pid);


		List<Long> tIds = article.getTermTypeIds();
		this.genSeoAndCrumbs(article, tIds.get(0));


		this.setPrevNextAndRelated(pid, article);


		Map<String, String> pubMeta = metas.stream().collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

		this.populateMeta(article, pubMeta);

		return (Article) this.handleContent(article, metas, contBody, pid);
	}

	private void setPrevNextAndRelated(Long pid, Article article) {
		String postType = article.getPostType();
		List<Long> termTypeIds = article.getTermTypeIds();
		MiniPost prev;
		MiniPost next;
		if (PostTypeEnum.POST.toString().equals(postType)) {
			if (ObjectUtil.isBlank(termTypeIds))
				return;
			prev = this.postService.queryPrevPost(pid, termTypeIds.get(0));
			next = this.postService.queryNextPost(pid, termTypeIds.get(0));
		} else {
			Long parentId = article.getParentId();
			prev = this.postService.queryPrevChapter(pid, parentId);
			next = this.postService.queryNextChapter(pid, parentId);
		}

		article.setPrev(prev);
		article.setNext(next);

		if (ObjectUtil.isBlank(termTypeIds))
			return;
		String contentType = article.getContentType();
		List<Term> tags = article.getTags();
		List<Long> tagTermTypeIds = ObjectUtil.isBlank(tags) ? termTypeIds : tags.parallelStream().map(Term::getTermTypeId).collect(Collectors.toList());
		List<MiniPost> relPosts = this.postService.queryRelatedPosts(postType, contentType, tagTermTypeIds, 8);
		if (ObjectUtil.isBlank(relPosts)) {
			relPosts = this.postService.queryRelatedPosts(postType, contentType, termTypeIds, 8);
		}
		article.setRelPosts(relPosts);
	}

	private List<ContentExt> getContentExt(List<ContentExt> contentExt, List<KPostmeta> metas) {
		return contentExt.parallelStream().map(cont -> {
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

	private List<Attachment> getAttachments(Map<Long, List<KPostmeta>> attMetaGroup) {
		return attMetaGroup.values().stream().map(curMetas -> {

			Map<String, String> attMeta = curMetas.stream()
					.collect(Collectors.toMap(KPostmeta::getMetaKey, KPostmeta::getMetaValue, (k1, k2) -> k1));

			Attachment attach = new Attachment();
			attach.setAttachPath(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_PATH));
			attach.setAttachMetadata(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_METADATA));
			attach.setAttachFileAlt(attMeta.get(KModelMetaKey.ATT_META_KEY_ATTACH_FILE_ALT));

			return attach;
		}).filter(a -> !ObjectUtil.isBlank(a.getAttachMetadata())).collect(Collectors.toList());
	}

	private void populateMeta(Article article, Map<String, String> pubMeta) {
		String cover = pubMeta.get(KModelMetaKey.PUB_META_KEY_COVER);
		if (!ObjectUtil.isBlank(cover))
			article.setCover(cover);
		String views = pubMeta.get(KModelMetaKey.PUB_META_KEY_VIEWS);
		if (!ObjectUtil.isBlank(views))
			article.setViews(views);
	}


	private void appendPubMeta(List<ContentExt> contentExtList) {
		ContentExt contentExt = new ContentExt();
		contentExt.setMetaKey(KModelMetaKey.PUB_META_KEY_VIEWS);
		contentExt.setMetaValue("0");

		contentExtList.add(contentExt);
	}


	private void updatePubMeta(Long pid) {
		if (!this.postmetaService.isExistsViews(pid)) {
			List<ContentExt> contentExtList = new ArrayList<>();
			this.appendPubMeta(contentExtList);
			this.createPostMeta(contentExtList, pid);
		} else
			this.postmetaService.increasePostViews(1, pid);
	}


	public List<ViewNode> queryCategoryByDomain(String domain) {
		String key = RedisKeyEnum.WLDOS_TERM_TREE.toString() + domain;
		String value = ObjectUtil.string(this.cache.get(key));

		try {
			ObjectMapper om = new ObjectMapper();
			if (ObjectUtil.isBlank(value)) {

				List<Term> allTerms = this.termService.findAllCategory();


				List<ViewNode> viewNodes = allTerms.parallelStream().map(res -> {
					ViewNode authRes = new ViewNode();
					authRes.setId(res.getId());
					authRes.setParentId(res.getParentId());
					authRes.setTitle(res.getName());
					authRes.setKey(res.getTermTypeId().toString());

					return authRes;
				}).collect(Collectors.toList());

				viewNodes = TreeUtil.buildFlatTree(viewNodes, Constants.TOP_TERM_ID);

				List<Long> termTypeIds = this.domainService.queryTermTypeIdsByDomain(domain);
				List<Long> allTypeIds = this.termService.queryChildIds(termTypeIds);


				viewNodes = viewNodes.parallelStream().filter(v -> v.getChildren() != null).map(v -> {
					List<ViewNode> vc = v.getChildren();
					vc = vc.parallelStream().filter(t -> allTypeIds.contains(t.getId())).collect(Collectors.toList());
					if (vc.isEmpty())
						return null;
					v.setChildren(vc);
					return v;
				}).filter(Objects::nonNull).collect(Collectors.toList());

				if (ObjectUtil.isBlank(viewNodes))
					return new ArrayList<>();

				value = om.writeValueAsString(viewNodes);

				this.cache.set(key, value, 12, TimeUnit.HOURS);

				return viewNodes;
			}

			return om.readValue(value, new TypeReference<List<ViewNode>>() {});
		}
		catch (JsonProcessingException e) {
			getLog().error("从缓存获取域的分类树异常: {}", e.getMessage());
		}

		return new ArrayList<>();
	}
}
