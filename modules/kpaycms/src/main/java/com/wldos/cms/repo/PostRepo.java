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
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.PostMember;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 帖子repository操作类
 *
 * @author 树悉猿
 * @date 2021/4/17
 * @version 1.0
 */
public interface PostRepo extends PagingAndSortingRepository<KPosts, Long> {
	/**
	 * 根据帖子id查询帖子信息和内容类型
	 *
	 * @param pid 帖子id
	 * @return 内容信息
	 */
	@Query("select p.*, c.id content_id from k_posts p left join k_model_content c on p.content_type=c.content_code where p.delete_flag='normal' and p.id=:pid")
	ContModelDto queryContModel(Long pid);

	/**
	 * 根据作品id、帖子类型查询作品的某类子实体(图片附件、章节等)
	 *
	 * @param pid 帖子id
	 * @param postType 帖子类型
	 * @return 子类附件信息
	 */
	@Query("select p.*, c.id content_id from k_posts p left join k_model_content c on p.content_type=c.content_code where p.delete_flag='normal' and p.parent_id=:pid and p.post_type=:postType")
	List<ContModelDto> queryContSubModel(Long pid, String postType);

	/**
	 * 批量查询帖子们的元数据
	 *
	 * @param ids 帖子集合
	 * @return 元数据集合
	 */
	@Query("select m.* from k_postmeta m where m.post_id in (:ids)")
	List<KPostmeta> queryPostExt(List<Long> ids);

	/**
	 * 按帖子id查询作者信息
	 *
	 * @param postIds 帖子id
	 * @return 帖子作者
	 */
	@Query("select s.id post_id, u.id, u.nickname, u.avatar from k_posts s left join wo_user u on s.create_by=u.id where s.id in (:postIds)")
	List<PostMember> queryMembersByPostIds(List<Long> postIds);

	/**
	 * 按帖子id查询作者信息
	 *
	 * @param postId 帖子id
	 * @return 帖子作者
	 */
	@Query("select s.id post_id, u.id, u.nickname, u.avatar from k_posts s left join wo_user u on s.create_by=u.id where s.id =:postId")
	PostMember queryMemberByPostId(Long postId);

	/**
	 * 根据展现类型、作品集id查询章节
	 *
	 * @param bookId 文集id或者帖子id
	 * @param postType 帖子展现类型：章节类型
	 * @param deleteFlag 是否删除：见DeleteFlagEnum
	 * @return 章节列表
	 */
	@Query("select s.id, s.post_title from k_posts s where s.delete_flag=:deleteFlag and s.parent_id=:bookId and s.post_type=:postType")
	List<Chapter> queryPostsByParentId(Long bookId, String postType, String deleteFlag);

	/**
	 * 帖子评论数加1
	 *
	 * @param postId 帖子id
	 */
	@Modifying
	@Query("update k_posts set comment_count=ABS(comment_count+(:count)) where id=:postId")
	void updateCommentCountByPostId(Long postId, int count);

	/**
	 * 帖子点赞数加1
	 *
	 * @param postId 帖子id
	 */
	@Modifying
	@Query("update k_posts set star_count=ABS(star_count+(:count)) where id=:postId")
	void updateStarCountByPostId(Long postId, int count);

	/**
	 * 帖子关注数加1
	 *
	 * @param postId 帖子id
	 */
	@Modifying
	@Query("update k_posts set like_count=ABS(like_count+(:count)) where id=:postId")
	void updateLikeCountByPostId(Long postId, int count);

	/**
	 * 帖子查看数更新
	 *
	 * @param postId 帖子id
	 */
	@Modifying
	@Query("update k_posts set views=:views where id=:postId")
	void updateViewsByPostId(Long postId, @Param("views") int views);

	/**
	 * 查询上一篇帖子
	 *
	 * @param pid 帖子id
	 * @return 上一篇
	 */
	@Query("select s.* from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='post' and t.post_status='publish' and t.id < :pid order by id desc limit 1)")
	MiniPost queryPrev(Long pid);

	/**
	 * 查询指定分类下上一篇
	 *
	 * @param pid 帖子id
	 * @param termTypeId 分类id
	 * @return 上一篇
	 */
	@Query("select s.* from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='post' and t.post_status='publish' and t.id < :pid and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id desc limit 1)")
	MiniPost queryPrev(Long pid, Long termTypeId);

	/**
	 * 查询指定作品集内上一章
	 *
	 * @param pid 章节id
	 * @param parentId 作品集id
	 * @return 上一章
	 */
	@Query("select s.* from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='chapter' and t.post_status='publish' and t.parent_id=:parentId and t.id < :pid order by id desc limit 1)")
	MiniPost queryPrevChapter(Long pid, Long parentId);

	/**
	 * 查询下一篇
	 *
	 * @param pid 帖子id
	 * @return 下一篇
	 */
	@Query("select s.id, s.post_title from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='post' and t.post_status='publish' and t.id > :pid order by id asc limit 1)")
	MiniPost queryNext(Long pid);

	/**
	 * 查询指定分类下下一篇
	 *
	 * @param pid 帖子id
	 * @param termTypeId 分类id
	 * @return 下一篇
	 */
	@Query("select * from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='post' and t.post_status='publish' and t.id > :pid and exists(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id asc limit 1)")
	MiniPost queryNext(Long pid, Long termTypeId);

	/**
	 * 查询指定作品集内下一章
	 *
	 * @param pid 帖子id
	 * @param parentId 作品集id
	 * @return 下一章
	 */
	@Query("select s.id, s.post_title from k_posts s where s.delete_flag='normal' and s.id = (select id from k_posts t where t.delete_flag='normal' and t.post_type='chapter' and t.post_status='publish' and t.parent_id = :parentId and t.id > :pid order by id asc limit 1)")
	MiniPost queryNextChapter(Long pid, Long parentId);

	/**
	 * 在指定标签或分类范围内查询相关帖子
	 * 章节和作品集一样需要设置分类
	 *
	 * @param postType 帖子展现类型：作品集、章节、附件、页面等，表现形式不同
	 * @param contentType 业务大类：文章、年谱、菜谱等业务划分，各大类属于不同领域（业务不同）
	 * @param termTypeIds 若干标签、分类等
	 * @param num 查询数量
	 * @return 相关帖子
	 */
	@Query("select t.id, t.post_title from k_posts t where t.delete_flag='normal' and t.post_type=:postType and t.content_type=:contentType and t.post_status='publish' and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id in (:termTypeIds)) order by id desc limit 1,:num")
	List<MiniPost> queryRelatedPosts(String postType, String contentType, List<Long> termTypeIds, int num);

	@Modifying
	@Query("update k_posts set post_status=:postStatus where id=:id")
	void changePostStatus(Long id, String postStatus);

	/**
	 * 根据帖子别名判断是否已存在
	 *
	 * @param postName 帖子别名
	 * @return 是否
	 */
	@Query("select count(1) from k_posts p where p.post_name=:postName and p.id != :postId")
	boolean existsByPostNameAndId(String postName, Long postId);
}
