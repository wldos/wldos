/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */
-- 仅针对wldos v1.0.1升级v1.5有用
-- 新增字段或更新2022-4月~10月
ALTER TABLE `k_term_type`
    MODIFY COLUMN `content_id`  bigint(20) NULL DEFAULT NULL COMMENT '归属的行业门类' AFTER `class_type`;

ALTER TABLE `k_terms`
    ADD COLUMN `info_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '是否推送信息发布门户',
    ADD COLUMN `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '是否有效：0无效、1有效',
    ADD COLUMN `display_order` int(10) DEFAULT NULL COMMENT '排序',
    ADD COLUMN `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
    ADD COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    ADD COLUMN `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    ADD COLUMN `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
    ADD COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    ADD COLUMN `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    ADD COLUMN `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
    ADD COLUMN `versions` int(10) DEFAULT NULL COMMENT '乐观锁';

ALTER TABLE `wo_domain`
    MODIFY COLUMN `slogan`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名品牌口号，用于登陆窗口或其他比较醒目的地方' AFTER `site_description`;

ALTER TABLE `wo_org`
    ADD COLUMN `org_logo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组织/群组/团队/圈子头像的实际存储相对路径';

ALTER TABLE `wo_user`
    ADD COLUMN `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '注册会员' COMMENT '头衔',
    ADD COLUMN `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '网络用户' COMMENT '组织、单位名称';

-- 用后无效2022-09-15
INSERT INTO `wo_org` VALUES (300, 'un_active', '待激活用户', NULL, 'platform', 0, 0, 0, 3, '1', 0, '2022-9-15 20:20:13', NULL, 0, '2022-9-15 20:20:26', NULL, 'normal', NULL);
INSERT INTO `wo_options` VALUES (149, 'un_active_group', NULL, 'un_active', NULL, NULL);

-- 2022-10-22
CREATE TABLE `wo_oauth_login_user` (
                                       `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账号关系表，主键不参与业务关联',
                                       `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联后创建的账号id',
                                       `oauth_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '社会化服务商类型：比如微信、qq、微博等的类型编码',
                                       `open_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `union_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
                                       `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                       `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
                                       `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号关联表：与账号表结合定义了账号关系链，保证第三方方式登录时可以通过链认证用户。用户登录后，还可以选择绑定多个第三方账';

