

package com.wldos.cms.repo;

import java.util.List;

import com.wldos.cms.entity.KComments;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CommentRepo extends PagingAndSortingRepository<KComments, Long>{

	@Query("select c.* from k_comments c where c.delete_flag=:delFlag and c.approved=:comStatus and c.post_id=:postId")
	List<KComments> queryCommentsByPostId(Long postId, String delFlag, String comStatus);
}
