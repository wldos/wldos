/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 站点会员角色/分群：wo_role、wo_org 复合唯一索引启动自检。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
public class RuntimeKindSchemaSelfHealBootstrap implements ApplicationRunner {

	private static final String ROLE_LEGACY_INDEX = "uni_role_code";
	private static final String ROLE_SITE_INDEX = "uk_role_com_domain_code";
	private static final String ORG_LEGACY_INDEX = "un_com_arch_org";
	private static final String ORG_SITE_INDEX = "uk_org_com_domain_code";

	private final DataSource dataSource;

	@Value("${wldos.platform.runtime-kind.schema-self-heal-enabled:true}")
	private boolean enabled;

	public RuntimeKindSchemaSelfHealBootstrap(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			log.info("[runtime-kind.schema] disabled by config");
			return;
		}
		try {
			ensureRoleIndexes();
			ensureOrgIndexes();
		}
		catch (Exception ex) {
			log.error("[runtime-kind.schema] index self-heal failed: {}", ex.getMessage(), ex);
		}
	}

	private void ensureRoleIndexes() throws Exception {
		if (!tableExists("wo_role")) {
			return;
		}
		if (!indexExists("wo_role", ROLE_SITE_INDEX)) {
			execute("ALTER TABLE `wo_role` ADD UNIQUE INDEX `" + ROLE_SITE_INDEX
					+ "` (`com_id`, `domain_id`, `role_code`)");
			log.info("[runtime-kind.schema] added {}", ROLE_SITE_INDEX);
		}
		if (indexExists("wo_role", ROLE_LEGACY_INDEX)) {
			execute("ALTER TABLE `wo_role` DROP INDEX `" + ROLE_LEGACY_INDEX + "`");
			log.info("[runtime-kind.schema] dropped legacy {}", ROLE_LEGACY_INDEX);
		}
	}

	private void ensureOrgIndexes() throws Exception {
		if (!tableExists("wo_org")) {
			return;
		}
		if (!indexExists("wo_org", ORG_SITE_INDEX)) {
			execute("ALTER TABLE `wo_org` ADD UNIQUE INDEX `" + ORG_SITE_INDEX
					+ "` (`com_id`, `domain_id`, `org_code`)");
			log.info("[runtime-kind.schema] added {}", ORG_SITE_INDEX);
		}
		if (indexExists("wo_org", ORG_LEGACY_INDEX)) {
			execute("ALTER TABLE `wo_org` DROP INDEX `" + ORG_LEGACY_INDEX + "`");
			log.info("[runtime-kind.schema] dropped legacy {}", ORG_LEGACY_INDEX);
		}
	}

	private boolean tableExists(String tableName) throws Exception {
		try (java.sql.Connection conn = this.dataSource.getConnection();
			 java.sql.PreparedStatement ps = conn.prepareStatement(
					 "SELECT COUNT(1) FROM information_schema.tables "
							 + "WHERE table_schema = DATABASE() AND table_name = ?")) {
			ps.setString(1, tableName);
			try (java.sql.ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private boolean indexExists(String tableName, String indexName) throws Exception {
		try (java.sql.Connection conn = this.dataSource.getConnection();
			 java.sql.PreparedStatement ps = conn.prepareStatement(
					 "SELECT COUNT(1) FROM information_schema.statistics "
							 + "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?")) {
			ps.setString(1, tableName);
			ps.setString(2, indexName);
			try (java.sql.ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private void execute(String sql) throws Exception {
		try (java.sql.Connection conn = this.dataSource.getConnection();
			 java.sql.Statement statement = conn.createStatement()) {
			statement.executeUpdate(sql);
		}
	}
}
