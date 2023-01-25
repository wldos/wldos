/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.entity.KPubmeta;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.MiniPub;
import com.wldos.cms.vo.PubMember;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 发布内容repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface PubRepo extends PagingAndSortingRepository<KPubs, Long> {
	/**
	 * 根据发布内容id查询发布内容信息和行业门类
	 *
	 * @param pid 发布内容id
	 * @return 内容信息
	 */
	@Query("select p.*, c.id industry_id from k_pubs p left join k_model_industry c on p.industry_type=c.industry_code where p.delete_flag='normal' and p.id=:pid")
	ContModelDto queryContModel(Long pid);

	/**
	 * 根据作品id、发布类型查询作品的某类子实体(图片附件、章节等)
	 *
	 * @param pid 发布内容id
	 * @param pubType 发布内容类型
	 * @return 子类附件信息
	 */
	@Query("select p.*, c.id industry_id from k_pubs p left join k_model_industry c on p.industry_type=c.industry_code where p.delete_flag='normal' and p.parent_id=:pid and p.pub_type=:pubType")
	List<ContModelDto> queryContSubModel(Long pid, String pubType);

	/**
	 * 批量查询发布内容们的元数据
	 *
	 * @param ids 发布内容集合
	 * @return 元数据集合
	 */
	@Query("select m.* from k_pubmeta m where m.pub_id in (:ids)")
	List<KPubmeta> queryPubExt(List<Long> ids);

	/**
	 * 按发布内容id查询作者信息
	 *
	 * @param pubIds 发布内容id
	 * @return 帖子作者
	 */
	@Query("select s.id pub_id, u.id, u.nickname, u.avatar from k_pubs s left join wo_user u on s.create_by=u.id where s.id in (:pubIds)")
	List<PubMember> queryMembersByPubIds(List<Long> pubIds);

	/**
	 * 按帖子id查询作者信息
	 *
	 * @param pubId 帖子id
	 * @return 帖子作者
	 */
	@Query("select s.id pub_id, u.id, u.nickname, u.avatar from k_pubs s left join wo_user u on s.create_by=u.id where s.id =:pubId")
	PubMember queryMemberByPubId(Long pubId);

	/**
	 * 根据展现类型、作品集id查询章节
	 *
	 * @param bookId 文集id或者帖子id
	 * @param pubType 帖子展现类型：章节类型
	 * @param deleteFlag 是否删除：见DeleteFlagEnum
	 * @return 章节列表
	 */
	@Query("select s.id, s.pub_title from k_pubs s where s.delete_flag=:deleteFlag and s.parent_id=:bookId and s.pub_type=:pubType")
	List<Chapter> queryPubsByParentId(Long bookId, String pubType, String deleteFlag);

	/**
	 * 帖子评论数加1
	 *
	 * @param pubId 帖子id
	 */
	@Modifying
	@Query("update k_pubs set comment_count=ABS(comment_count+(:count)) where id=:pubId")
	void updateCommentCountByPubId(Long pubId, int count);

	/**
	 * 帖子点赞数加1
	 *
	 * @param pubId 帖子id
	 */
	@Modifying
	@Query("update k_pubs set star_count=ABS(star_count+(:count)) where id=:pubId")
	void updateStarCountByPubId(Long pubId, int count);

	/**
	 * 帖子关注数加1
	 *
	 * @param pubId 帖子id
	 */
	@Modifying
	@Query("update k_pubs set like_count=ABS(like_count+(:count)) where id=:pubId")
	void updateLikeCountByPubId(Long pubId, int count);

	/**
	 * 帖子查看数更新
	 *
	 * @param pubId 帖子id
	 */
	@Modifying
	@Query("update k_pubs set views=:views where id=:pubId")
	void updateViewsByPubId(Long pubId, @Param("views") int views);

	/**
	 * 查询上一篇帖子
	 *
	 * @param pid 帖子id
	 * @return 上一篇
	 */
	@Query("select s.* from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='post' and t.pub_status='publish' and t.id < :pid order by id desc limit 1)")
	MiniPub queryPrev(Long pid);

	/**
	 * 查询指定分类下上一篇
	 *
	 * @param pid 帖子id
	 * @param termTypeId 分类id
	 * @return 上一篇
	 */
	@Query("select s.* from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='post' and t.pub_status='publish' and t.id < :pid and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id desc limit 1)")
	MiniPub queryPrev(Long pid, Long termTypeId);

	/**
	 * 查询指定作品集内上一章
	 *
	 * @param pid 章节id
	 * @param parentId 作品集id
	 * @return 上一章
	 */
	@Query("select s.* from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='chapter' and t.pub_status='publish' and t.parent_id=:parentId and t.id < :pid order by id desc limit 1)")
	MiniPub queryPrevChapter(Long pid, Long parentId);

	/**
	 * 查询下一篇
	 *
	 * @param pid 帖子id
	 * @return 下一篇
	 */
	@Query("select s.id, s.pub_title from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='post' and t.pub_status='publish' and t.id > :pid order by id asc limit 1)")
	MiniPub queryNext(Long pid);

	/**
	 * 查询指定分类下下一篇
	 *
	 * @param pid 帖子id
	 * @param termTypeId 分类id
	 * @return 下一篇
	 */
	@Query("select * from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='post' and t.pub_status='publish' and t.id > :pid and exists(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id asc limit 1)")
	MiniPub queryNext(Long pid, Long termTypeId);

	/**
	 * 查询指定作品集内下一章
	 *
	 * @param pid 帖子id
	 * @param parentId 作品集id
	 * @return 下一章
	 */
	@Query("select s.id, s.pub_title from k_pubs s where s.delete_flag='normal' and s.id = (select id from k_pubs t where t.delete_flag='normal' and t.pub_type='chapter' and t.pub_status='publish' and t.parent_id = :parentId and t.id > :pid order by id asc limit 1)")
	MiniPub queryNextChapter(Long pid, Long parentId);

	/**
	 * 在指定标签或分类范围内查询相关发布
	 * 章节和作品集一样需要设置分类
	 *
	 * @param pubType 发布的内容展现类型：作品集、章节、附件、页面等，表现形式不同
	 * @param industryType 行业门类：各大类属于不同领域（业务模型相同）
	 * @param termTypeIds 标签、分类等
	 * @param num 查询数量
	 * @return 相关帖子
	 */
	@Query("select t.id, t.pub_title from k_pubs t where t.delete_flag='normal' and t.pub_type=:pubType and t.industry_type=:industryType and t.pub_status='publish' and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id in (:termTypeIds)) order by id desc limit 1,:num")
	List<MiniPub> queryRelatedPubs(String pubType, String industryType, List<Long> termTypeIds, int num);

	@Modifying
	@Query("update k_pubs set pub_status=:pubStatus where id=:id")
	void changePubStatus(Long id, String pubStatus);

	/**
	 * 根据内容别名判断是否已存在
	 *
	 * @param pubName 内容别名
	 * @return 是否
	 */
	@Query("select count(1) from k_pubs p where p.pub_name=:pubName and p.id != :pubId")
	boolean existsByPubNameAndId(String pubName, Long pubId);

	/**
	 * 根据id批量查询帖子列表
	 *
	 * @param pubIds 帖子列表
	 * @return 帖子列表
	 */
	List<KPubs> findAllByIdIn(List<Long> pubIds);
}
