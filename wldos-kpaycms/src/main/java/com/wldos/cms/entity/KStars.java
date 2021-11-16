

package com.wldos.cms.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table
public class KStars {
	@Id
	private Long id;


	private Long objectId;

	private Long userId;

	private String stars;

	private String likes;

	public KStars() {}

	public KStars(Long id, Long objectId, Long userId, String stars, String likes) {
		this.id = id;
		this.objectId = objectId;
		this.userId = userId;
		this.stars = stars;
		this.likes = likes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}
}
