package com.wldos.platform.core.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import com.wldos.framework.mvc.service.NonEntityService;

/**
 * {@code wo_org_closure} 维护：组织 CRUD 时增量更新闭包表。
 */
@Service
public class OrgClosureService extends NonEntityService {

	private final JdbcOperations jdbcOperations;

	public OrgClosureService(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	public void onOrgInserted(Long orgId, Long parentId) {
		if (orgId == null) {
			return;
		}
		this.linkOrg(orgId, parentId);
	}

	public void onOrgUpdated(Long orgId, Long oldParentId, Long newParentId) {
		if (orgId == null) {
			return;
		}
		if (oldParentId != null && oldParentId.equals(newParentId)) {
			return;
		}
		this.rebuildSubtree(orgId);
	}

	public void onOrgDeleted(Long orgId) {
		if (orgId == null) {
			return;
		}
		this.jdbcOperations.update(
				"delete from wo_org_closure where descendant_id in "
						+ "(select descendant_id from (select descendant_id from wo_org_closure where ancestor_id = ?) t)",
				orgId);
	}

	private void linkOrg(Long orgId, Long parentId) {
		if (parentId == null || parentId <= 0) {
			this.jdbcOperations.update(
					"insert ignore into wo_org_closure (ancestor_id, descendant_id, depth) values (?, ?, 0)",
					orgId, orgId);
			return;
		}
		this.jdbcOperations.update(
				"insert ignore into wo_org_closure (ancestor_id, descendant_id, depth) "
						+ "select c.ancestor_id, ?, c.depth + 1 from wo_org_closure c where c.descendant_id = ?",
				orgId, parentId);
		this.jdbcOperations.update(
				"insert ignore into wo_org_closure (ancestor_id, descendant_id, depth) values (?, ?, 0)",
				orgId, orgId);
	}

	private void rebuildSubtree(Long rootOrgId) {
		List<Long> descendants = this.jdbcOperations.queryForList(
				"select descendant_id from wo_org_closure where ancestor_id = ?",
				Long.class,
				rootOrgId);
		if (descendants.isEmpty()) {
			return;
		}
		for (Long descendantId : descendants) {
			Long parentId = this.jdbcOperations.query(
					"select parent_id from wo_org where id = ? and delete_flag='normal'",
					rs -> rs.next() ? rs.getLong("parent_id") : null,
					descendantId);
			this.jdbcOperations.update("delete from wo_org_closure where descendant_id = ?", descendantId);
			this.linkOrg(descendantId, parentId);
		}
	}
}
