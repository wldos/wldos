/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.dao;

import java.util.List;

import com.wldos.framework.mvc.dao.BaseDao;
import com.wldos.platform.core.entity.WoSubjectAssociation;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 主体授权（主体类型 ↔ 角色）仓库操作类。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface SubjectAssociationDao extends BaseDao<WoSubjectAssociation, Long> {

	@Query("SELECT * FROM wo_subject_association WHERE (com_id = :comId OR com_id IS NULL OR com_id = 0) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSubjectAssociation> findValidByCom(@Param("comId") Long comId);

	@Query("SELECT * FROM wo_subject_association WHERE delete_flag = 'normal' AND is_valid = '1'")
	List<WoSubjectAssociation> findAllValid();

	@Query("SELECT * FROM wo_subject_association WHERE subject_type_id IN (:subjectTypeIds) AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSubjectAssociation> findValidBySubjectTypeIds(@Param("subjectTypeIds") List<Long> subjectTypeIds);
}
