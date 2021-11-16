

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostTypeEnum;
import com.wldos.cms.service.PostService;
import com.wldos.cms.vo.AuditPost;
import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/admin/cms/post")
@RestController
public class PostController extends RepoController<PostService, KPosts> {

	@RequestMapping("chapter")
	public PageableResult<AuditPost> adminPosts(@RequestParam Map<String, Object> params) {


		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", PostTypeEnum.CHAPTER.toString());
		return this.service.queryAdminPosts(pageQuery);
	}


	@RequestMapping("book")
	public PageableResult<AuditPost> adminBooks(@RequestParam Map<String, Object> params) {


		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", PostTypeEnum.BOOK.toString());
		return this.service.queryAdminPosts(pageQuery);
	}


	@PostMapping("publish")
	public Boolean publishPost(@RequestBody  AuditPost post) {
		this.service.publishPost(post);
		return Boolean.TRUE;
	}


	@PostMapping("offline")
	public Boolean offlinePost(@RequestBody  AuditPost post) {
		this.service.offlinePost(post);
		return Boolean.TRUE;
	}
}
