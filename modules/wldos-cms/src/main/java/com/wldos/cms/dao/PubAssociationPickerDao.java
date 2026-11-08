/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.dao;

import com.wldos.cms.entity.KPubs;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.query.CmsKpubsDiscoverySql;
import com.wldos.cms.vo.AuditPub;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.common.CommonOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产品/协议等「关联 CMS 文档」选择器专用分页查询（与内容管理 {@code queryAdminPubs} 隔离）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Repository
public class PubAssociationPickerDao {

	@Autowired
	@Lazy
	private CommonOperation commonOperation;

	/**
	 * 仅返回已发布的主作品（{@code parent_id=0}、{@code pub_status=publish}），不含章节行。
	 * 域/租户范围由 {@code pageQuery}（Controller 侧 {@code applyScope} 后）动态追加。
	 */
	public PageData<AuditPub> pagePublishedForAssociation(PageQuery pageQuery) {
		Sort sort = pageQuery.getSorter();
		Map<String, List<Object>> filter = pageQuery.getFilter();
		Map<String, Object> condition = pageQuery.getCondition();
		String keyword = extractAndRemoveKeyword(condition);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = this.commonOperation.querySqlByTable("k_pubs", KPubs.class, params, condition, filter);
		appendAssociationPickerFixedWhere(sql);
		appendKeywordFilter(sql, params, keyword);
		String countSql = "select count(1) as total from ( " + sql + " ) w";
		List<Map<String, Object>> all = this.commonOperation.getJdbcOperations().queryForList(countSql, params.toArray());
		int total = Integer.parseInt(ObjectUtils.string(all.get(0).get("total")));
		this.commonOperation.orderSql(sort, sql);
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();
		int totalPageNum = total == 0 ? 1 : (total - 1) / pageSize + 1;
		currentPage = Math.min(currentPage, totalPageNum);
		List<AuditPub> list = this.commonOperation.execQueryForPage(AuditPub.class, sql.toString(), currentPage,
				pageSize, params.toArray());
		return new PageData<>(total, currentPage, pageSize, list);
	}

	private static void appendAssociationPickerFixedWhere(StringBuilder sql) {
		String publish = PubStatusEnum.PUBLISH.getValue();
		String normal = DeleteFlagEnum.NORMAL.toString();
		CmsKpubsDiscoverySql.appendCanonicalPubsOnly(sql, "a");
		sql.append(" AND a.pub_type <> 'page' ");
		sql.append(" AND a.delete_flag = '").append(normal).append("' ");
		sql.append(" AND COALESCE(a.parent_id, ").append(Constants.TOP_PUB_ID).append(") = ")
				.append(Constants.TOP_PUB_ID).append(' ');
		sql.append(" AND a.pub_status = '").append(publish).append("' ");
	}

	private static String extractAndRemoveKeyword(Map<String, Object> condition) {
		if (condition == null) {
			return null;
		}
		Object raw = condition.remove("keyword");
		if (raw == null) {
			return null;
		}
		String keyword = ObjectUtils.string(raw).trim();
		return keyword.isEmpty() ? null : keyword;
	}

	private static void appendKeywordFilter(StringBuilder sql, List<Object> params, String keyword) {
		if (keyword == null) {
			return;
		}
		sql.append(" and (instr(a.pub_title, ?) > 0 or instr(a.pub_content, ?) > 0) ");
		params.add(keyword);
		params.add(keyword);
	}
}
