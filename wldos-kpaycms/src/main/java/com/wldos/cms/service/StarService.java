

package com.wldos.cms.service;

import java.util.List;

import com.wldos.cms.entity.KStars;
import com.wldos.cms.repo.StarRepo;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.system.core.service.AuthService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class StarService extends BaseService<StarRepo, KStars, Long> {

	private final PostService postService;

	private final AuthService authService;

	public StarService(PostService postService, AuthService authService) {
		this.postService = postService;
		this.authService = authService;
	}

	public List<KStars> queryStarsByObjectId(Long contentId) {
		return this.entityRepo.queryStarsByObjectId(contentId);
	}

	
	public int starObject(Long objId, Long userId) {
		KStars stars = this.entityRepo.queryStarByObjectIdAndUserId(objId, userId);
		if (stars == null) {

			stars = new KStars(this.nextId(), objId, userId, ValidStatusEnum.VALID.toString(), null);
			this.insertSelective(stars);
			return 1;
		} else {

			this.entityRepo.updateStar(stars.getId());
		}


		int add = !this.authService.isGuest(userId) && ValidStatusEnum.VALID.toString().equals(stars.getStars()) ? -1 : 1;
		this.postService.updateStarCount(objId, add);

		return add;
	}

	
	public int likeObject(Long objId, Long userId) {
		KStars stars = this.entityRepo.queryStarByObjectIdAndUserId(objId, userId);
		if (stars == null) {

			stars = new KStars(this.nextId(), objId, userId, null, ValidStatusEnum.VALID.toString());
			this.insertSelective(stars);
			return 1;
		} else {

			this.entityRepo.updateLike(stars.getId());
		}


		int add = !this.authService.isGuest(userId) && ValidStatusEnum.VALID.toString().equals(stars.getLikes()) ? -1 : 1;
		this.postService.updateLikeCount(objId, add);

		return add;
	}
}
