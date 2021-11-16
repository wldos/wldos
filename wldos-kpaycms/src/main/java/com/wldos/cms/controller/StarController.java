

package com.wldos.cms.controller;

import com.wldos.cms.entity.KStars;
import com.wldos.cms.service.StarService;
import com.wldos.cms.vo.Post;
import com.wldos.support.controller.RepoController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("cms")
@RestController
public class StarController extends RepoController<StarService, KStars> {

	@PostMapping("star")
	public int starObject(@RequestBody Post post) {
		return this.service.starObject(post.getId(), this.getCurUserId());
	}

	@PostMapping("like")
	public int likeObject(@RequestBody Post post) {
		return this.service.likeObject(post.getId(), this.getCurUserId());
	}
}
