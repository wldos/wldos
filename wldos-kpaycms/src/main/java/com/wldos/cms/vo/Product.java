

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import com.wldos.cms.model.IMeta;
import com.wldos.cms.model.KModelMeta;

public class Product extends KModelMeta implements IMeta {


	private Long id;

	private String postTitle;

	private String postContent;

	private String postName;

	private Long parentId;

	private String guid;

	private String postType;

	private String contentType;

	private String postMimeType;

	private Long commentCount;

	private Long createBy;

	private Timestamp createTime;

	private Long updateBy;

	private Timestamp updateTime;

	private Long contentId;

	private List<Long> termTypeIds;


	private List<Term> tags;


	private SeoCrumbs seoCrumbs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getPostMimeType() {
		return postMimeType;
	}

	public void setPostMimeType(String postMimeType) {
		this.postMimeType = postMimeType;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public List<Long> getTermTypeIds() {
		return termTypeIds;
	}

	public void setTermTypeIds(List<Long> termTypeIds) {
		this.termTypeIds = termTypeIds;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}

	public SeoCrumbs getSeoCrumbs() {
		return seoCrumbs;
	}

	public void setSeoCrumbs(SeoCrumbs seoCrumbs) {
		this.seoCrumbs = seoCrumbs;
	}
}
