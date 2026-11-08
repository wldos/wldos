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
import com.wldos.platform.core.entity.WoSlotBinding;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * 插槽绑定 Dao。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public interface SlotBindingDao extends BaseDao<WoSlotBinding, Long> {

	@Query("SELECT * FROM wo_slot_binding WHERE slot_code = :slotCode AND com_id = :comId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSlotBinding> findValidBySlotAndCom(@Param("slotCode") String slotCode, @Param("comId") Long comId);

	@Query("SELECT * FROM wo_slot_binding WHERE com_id = :comId AND delete_flag = 'normal' AND is_valid = '1'")
	List<WoSlotBinding> findValidByCom(@Param("comId") Long comId);

	/** 门户站分群目标上的有效插槽绑定（租户轴错误） */
	@Query("SELECT b.* FROM wo_slot_binding b "
			+ "INNER JOIN wo_org o ON b.target_type = 'org' AND b.target_id = o.id "
			+ "INNER JOIN wo_domain d ON b.domain_id = d.id "
			+ "WHERE b.delete_flag = 'normal' AND b.is_valid = '1' "
			+ "AND o.delete_flag = 'normal' AND d.delete_flag = 'normal' AND d.com_id = 0 AND o.com_id = 0")
	List<WoSlotBinding> findValidOrgBindingsOnCatalogSegmentTargets();
}
