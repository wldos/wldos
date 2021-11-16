

package com.wldos.cms.vo;

import java.sql.Timestamp;
import java.util.List;

import com.wldos.cms.model.CMeta;
import com.wldos.cms.model.IMeta;


public class Article extends CMeta implements IMeta {


	private Long id;

	private String postTitle;

	private String postContent;

	private String postName;

	private String postType;

	private Long parentId;

	private PostMember member;

	private Long commentCount;

	private Long starCount;

	private Long likeCount;

	private Timestamp updateTime;

	private Long contentId;

	private String contentType;

	private List<Comment> comments;


	private List<Term> tags;

	private List<Long> termTypeIds;


	private MiniPost prev;


	private MiniPost next;


	private List<MiniPost> relPosts;


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

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public PostMember getMember() {
		return member;
	}

	public void setMember(PostMember member) {
		this.member = member;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getStarCount() {
		return starCount;
	}

	public void setStarCount(Long starCount) {
		this.starCount = starCount;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Term> getTags() {
		return tags;
	}

	public void setTags(List<Term> tags) {
		this.tags = tags;
	}

	public List<Long> getTermTypeIds() {
		return termTypeIds;
	}

	public void setTermTypeIds(List<Long> termTypeIds) {
		this.termTypeIds = termTypeIds;
	}

	public MiniPost getPrev() {
		return prev;
	}

	public void setPrev(MiniPost prev) {
		this.prev = prev;
	}

	public MiniPost getNext() {
		return next;
	}

	public void setNext(MiniPost next) {
		this.next = next;
	}

	public List<MiniPost> getRelPosts() {
		return relPosts;
	}

	public void setRelPosts(List<MiniPost> relPosts) {
		this.relPosts = relPosts;
	}

	public SeoCrumbs getSeoCrumbs() {
		return seoCrumbs;
	}

	public void setSeoCrumbs(SeoCrumbs seoCrumbs) {
		this.seoCrumbs = seoCrumbs;
	}
}
