

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.dto.ContModelDto;
import com.wldos.cms.vo.Chapter;
import com.wldos.cms.vo.MiniPost;
import com.wldos.cms.vo.PostMember;
import com.wldos.cms.entity.KPostmeta;
import com.wldos.cms.entity.KPosts;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface PostRepo extends PagingAndSortingRepository<KPosts, Long>{
	
	@Query("select p.*, d.id model_id, d.model_code, c.id content_id from k_posts p left join k_model_content c on p.content_type=c.content_code left join k_model d on c.model_id=d.id where p.delete_flag='normal' and p.id=:pid")
	ContModelDto queryContModel(Long pid);

	
	@Query("select p.*, d.id model_id, d.model_code, c.id content_id from k_posts p left join k_model_content c on p.content_type=c.content_code left join k_model d on c.model_id=d.id where p.delete_flag='normal' and p.parent_id=:pid and p.post_type=:postType")
	List<ContModelDto> queryContSubModel(Long pid, String postType);

	
	@Query("select m.* from k_postmeta m where m.post_id in (:ids)")
	List<KPostmeta> queryPostExt(List<Long> ids);

	
	@Query("select s.id post_id, u.id, u.nickname, u.avatar from k_posts s left join wo_user u on s.create_by=u.id where s.id in (:postIds)")
	List<PostMember> queryMembersByPostIds(List<Long> postIds);

	
	@Query("select s.id post_id, u.id, u.nickname, u.avatar from k_posts s left join wo_user u on s.create_by=u.id where s.id =:postId")
	PostMember queryMemberByPostId(Long postId);

	
	@Query("select s.id, s.post_title from k_posts s where s.parent_id=:bookId and s.post_type=:postType")
	List<Chapter> queryPostsByParentId(Long bookId, String postType);

	
	@Modifying
	@Query("update k_posts set comment_count=ABS(comment_count+(:count)) where id=:postId")
	void updateCommentCountByPostId(Long postId, int count);

	
	@Modifying
	@Query("update k_posts set star_count=ABS(star_count+(:count)) where id=:postId")
	void updateStarCountByPostId(Long postId, int count);

	
	@Modifying
	@Query("update k_posts set like_count=ABS(like_count+(:count)) where id=:postId")
	void updateLikeCountByPostId(Long postId, int count);

	
	@Query("select s.* from k_posts s where s.id = (select id from k_posts t where t.post_type='post' and t.post_status='publish' and t.id < :pid order by id desc limit 1)")
	MiniPost queryPrev(Long pid);

	
	@Query("select s.* from k_posts s where s.id = (select id from k_posts t where t.post_type='post' and t.post_status='publish' and t.id < :pid and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id desc limit 1)")
	MiniPost queryPrev(Long pid, Long termTypeId);

	
	@Query("select s.* from k_posts s where s.id = (select id from k_posts t where t.post_type='chapter' and t.post_status='publish' and t.parent_id=:parentId and t.id < :pid order by id desc limit 1)")
	MiniPost queryPrevChapter(Long pid, Long parentId);

	
	@Query("select s.id, s.post_title from k_posts s where s.id = (select id from k_posts t where t.post_type='post' and t.post_status='publish' and t.id > :pid order by id asc limit 1)")
	MiniPost queryNext(Long pid);

	
	@Query("select * from k_posts s where s.id = (select id from k_posts t where t.post_type='post' and t.post_status='publish' and t.id > :pid and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id = :termTypeId) order by id asc limit 1)")
	MiniPost queryNext(Long pid, Long termTypeId);

	
	@Query("select s.id, s.post_title from k_posts s where s.id = (select id from k_posts t where t.post_type='chapter' and t.post_status='publish' and t.parent_id = :parentId and t.id > :pid order by id asc limit 1)")
	MiniPost queryNextChapter(Long pid, Long parentId);

	
	@Query("select t.id, t.post_title from k_posts t where t.post_type=:postType and t.content_type=:contentType and t.post_status='publish' and EXISTS(select 1 from k_term_object o where o.object_id=t.id and o.term_type_id in (:termTypeIds)) order by id desc limit 1,:num")
	List<MiniPost> queryRelatedPosts(String postType, String contentType, List<Long> termTypeIds, int num);

	@Modifying
	@Query("update k_posts set post_status=:postStatus where id=:id")
	void changePostStatus(Long id, String postStatus);
}
