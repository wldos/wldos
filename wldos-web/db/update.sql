ALTER TABLE `wo_options`
    CHANGE COLUMN `app_type` `option_type`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'no' COMMENT '选项类型：auto_reload、normal' AFTER `description`;
ALTER TABLE `wo_options`
    ADD COLUMN `app_code`  varchar(20) NULL COMMENT '模块编码' AFTER `option_type`,
    ADD INDEX `opetions_app_code` (`app_code`) USING BTREE ;
