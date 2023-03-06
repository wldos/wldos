ALTER TABLE `wo_domain`
    ADD COLUMN `favicon`  varchar(50) NULL COMMENT '网站favicon' AFTER `site_logo`;