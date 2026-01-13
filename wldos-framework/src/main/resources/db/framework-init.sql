/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

# your database init sql
set names utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- ========== 框架层基础设施表 ==========

-- ----------------------------
-- Table structure for wo_file
-- 文件存储表（框架层基础设施实体）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `wo_file` (
  `id` BIGINT(20) UNSIGNED NOT NULL COMMENT '主键ID',
  `name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `path` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径（相对路径）',
  `mime_type` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `is_valid` CHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否有效（1-有效，0-无效）',
  `create_by` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NULL DEFAULT NULL COMMENT '创建时间',
  `create_ip` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建IP',
  `update_by` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NULL DEFAULT NULL COMMENT '更新时间',
  `update_ip` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新IP',
  `delete_flag` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '删除标志（normal-正常，deleted-已删除）',
  `versions` INT(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_is_valid_del`(`is_valid`, `delete_flag`) USING BTREE,
  INDEX `idx_file_mime_type`(`mime_type`) USING BTREE,
  INDEX `idx_file_path`(`path`) USING BTREE,
  INDEX `idx_file_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '文件存储表（框架层基础设施）';

SET FOREIGN_KEY_CHECKS=1;