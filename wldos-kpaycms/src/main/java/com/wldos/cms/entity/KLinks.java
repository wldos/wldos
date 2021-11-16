

package com.wldos.cms.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class KLinks {
	@Id
	private Long id;

	private String linkUrl;

	private String linkName;

	private String linkImage;

	private String linkTarget;

	private String linkDescription;

	private String linkVisible;

	private Long linkOwner;

	private Long linkRating;

	private java.sql.Timestamp linkUpdated;

	private String linkRel;

	private String linkNotes;

	private String linkRss;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkImage() {
		return linkImage;
	}

	public void setLinkImage(String linkImage) {
		this.linkImage = linkImage;
	}

	public String getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getLinkVisible() {
		return linkVisible;
	}

	public void setLinkVisible(String linkVisible) {
		this.linkVisible = linkVisible;
	}

	public Long getLinkOwner() {
		return linkOwner;
	}

	public void setLinkOwner(Long linkOwner) {
		this.linkOwner = linkOwner;
	}

	public Long getLinkRating() {
		return linkRating;
	}

	public void setLinkRating(Long linkRating) {
		this.linkRating = linkRating;
	}

	public Timestamp getLinkUpdated() {
		return linkUpdated;
	}

	public void setLinkUpdated(Timestamp linkUpdated) {
		this.linkUpdated = linkUpdated;
	}

	public String getLinkRel() {
		return linkRel;
	}

	public void setLinkRel(String linkRel) {
		this.linkRel = linkRel;
	}

	public String getLinkNotes() {
		return linkNotes;
	}

	public void setLinkNotes(String linkNotes) {
		this.linkNotes = linkNotes;
	}

	public String getLinkRss() {
		return linkRss;
	}

	public void setLinkRss(String linkRss) {
		this.linkRss = linkRss;
	}
}
