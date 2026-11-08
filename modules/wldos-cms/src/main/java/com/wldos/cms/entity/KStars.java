/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import io.github.wldos.platform.support.rbac.RbacRowScope;

/**
 * 点赞、收藏实体表。USER_SCOPED：非本人点赞/收藏记录不可见、不可用、不可改。
 *
 * @author Yuanxi Universe
 * @date 2021/8/19
 * @version 1.0
 */
@Table
@Getter
@Setter
@RbacRowScope(comId = false, userId = true)
public class KStars {
	@Id
	private Long id;

	/** 发布的各种内容：帖子、产品、作品、评论等 */
	private Long objectId;

	private Long userId;

	private String stars;

	private String likes;

	public KStars() {
	}

	public static KStars of(Long id, Long objectId, Long userId, String stars, String likes) {
		return new KStars(id, objectId, userId, stars, likes);
	}

	private KStars(Long id, Long objectId, Long userId, String stars, String likes) {
		this.id = id;
		this.objectId = objectId;
		this.userId = userId;
		this.stars = stars;
		this.likes = likes;
	}
}
