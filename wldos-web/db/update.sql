/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */
-- 新增字段或更新2023-1月，如果系统运行数据库不报错请忽略本脚本！！！
ALTER TABLE `wo_options`
    CHANGE COLUMN `key` `option_key`  varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL AFTER `id`,
    CHANGE COLUMN `name` `option_name`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置key' AFTER `option_key`,
    CHANGE COLUMN `value` `option_value`  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL AFTER `option_name`;

