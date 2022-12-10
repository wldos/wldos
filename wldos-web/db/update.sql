/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */
-- 新增字段或更新2022-4月~10月，如果系统运行数据库不报错请忽略本脚本！！！
ALTER TABLE `k_term_type`
    MODIFY COLUMN `content_id`  bigint(20) NULL DEFAULT NULL  AFTER `class_type`;

ALTER TABLE `k_terms`
    ADD COLUMN `info_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' ,
    ADD COLUMN `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' ,
    ADD COLUMN `display_order` int(10) DEFAULT NULL ,
    ADD COLUMN `create_by` bigint(20) unsigned DEFAULT NULL ,
    ADD COLUMN `create_time` datetime DEFAULT NULL ,
    ADD COLUMN `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    ADD COLUMN `update_by` bigint(20) unsigned DEFAULT NULL ,
    ADD COLUMN `update_time` datetime DEFAULT NULL ,
    ADD COLUMN `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    ADD COLUMN `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
    ADD COLUMN `versions` int(10) DEFAULT NULL ;

ALTER TABLE `wo_domain`
    MODIFY COLUMN `slogan`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL  AFTER `site_description`;

ALTER TABLE `wo_org`
    ADD COLUMN `org_logo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ;

ALTER TABLE `wo_user`
    ADD COLUMN `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '注册会员' ,
    ADD COLUMN `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '网络用户' ;

-- 用后无效2022-09-15
INSERT INTO `wo_org` VALUES (300, 'un_active', '待激活用户', NULL, 'platform', 0, 0, 0, 3, '1', 0, '2022-9-15 20:20:13', NULL, 0, '2022-9-15 20:20:26', NULL, 'normal', NULL);
INSERT INTO `wo_options` VALUES (149, 'un_active_group', NULL, 'un_active', NULL, NULL);

-- 2022-10-22
CREATE TABLE `wo_oauth_login_user` (
                                       `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT ,
                                       `user_id` bigint(20) unsigned DEFAULT NULL ,
                                       `oauth_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                                       `open_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `union_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_by` bigint(20) unsigned DEFAULT NULL ,
                                       `create_time` datetime DEFAULT NULL ,
                                       `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `update_by` bigint(20) unsigned DEFAULT NULL ,
                                       `update_time` datetime DEFAULT NULL ,
                                       `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                                       `versions` int(10) DEFAULT NULL ,
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ;

CREATE TABLE `wo_mail` (
                           `id` bigint(20) NOT NULL,
                           `from_addr` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                           `to_addr` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                           `content` text COLLATE utf8mb4_unicode_ci ,
                           `status` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                           `result` text COLLATE utf8mb4_unicode_ci ,
                           `create_by` bigint(20) unsigned DEFAULT '0' ,
                           `create_time` datetime DEFAULT NULL ,
                           `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `update_by` bigint(20) unsigned DEFAULT NULL ,
                           `update_time` datetime DEFAULT NULL ,
                           `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
                           `versions` int(10) DEFAULT NULL ,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;