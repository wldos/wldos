
set names utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `k_commentmeta`
-- ----------------------------
DROP TABLE IF EXISTS `k_commentmeta`;
CREATE TABLE `k_commentmeta` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `comment_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `meta_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meta_value` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `comment_id` (`comment_id`),
  KEY `meta_key` (`meta_key`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_commentmeta
-- ----------------------------

-- ----------------------------
-- Table structure for `k_comments`
-- ----------------------------
DROP TABLE IF EXISTS `k_comments`;
CREATE TABLE `k_comments` (
  `id` bigint(20) unsigned NOT NULL,
  `pub_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `author` tinytext COLLATE utf8mb4_unicode_ci,
  `author_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `author_url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `author_ip` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `karma` int(11) NOT NULL DEFAULT '0' ,
  `approved` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `user_agent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `comment_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' ,
  `create_by` bigint(20) unsigned DEFAULT '0' ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `comment_post_ID` (`pub_id`),
  KEY `comment_approved_date_gmt` (`approved`),
  KEY `comment_parent` (`parent_id`),
  KEY `comment_author_email` (`author_email`(10)),
  KEY `comment_delete_flag` (`delete_flag`) USING BTREE,
  KEY `comment_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_links`
-- ----------------------------
DROP TABLE IF EXISTS `k_links`;
CREATE TABLE `k_links` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `link_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_target` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_visible` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_owner` bigint(20) unsigned NOT NULL DEFAULT '1',
  `link_rating` int(11) NOT NULL DEFAULT '0',
  `link_updated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `link_rel` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link_notes` mediumtext COLLATE utf8mb4_unicode_ci,
  `link_rss` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `link_visible` (`link_visible`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_links
-- ----------------------------

-- ----------------------------
-- Table structure for `k_model_pub_type_ext`
-- ----------------------------
DROP TABLE IF EXISTS `k_model_pub_type_ext`;
CREATE TABLE `k_model_pub_type_ext` (
  `id` bigint(20) unsigned NOT NULL,
  `meta_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `meta_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `meta_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `data_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `enum_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ,
  `pub_type` varchar(20) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_meta_key` (`pub_type`,`meta_key`) USING BTREE,
  KEY `k_model_content_id` (`pub_type`),
  KEY `k_model_data_type` (`data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `k_pubmeta`
-- ----------------------------
DROP TABLE IF EXISTS `k_pubmeta`;
CREATE TABLE `k_pubmeta` (
  `id` bigint(20) unsigned NOT NULL,
  `pub_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `meta_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meta_value` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `meta_key` (`meta_key`(191)),
  KEY `post_id` (`pub_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_stars`
-- ----------------------------
DROP TABLE IF EXISTS `k_stars`;
CREATE TABLE `k_stars` (
                           `id` bigint(20) unsigned NOT NULL,
                           `object_id` bigint(20) unsigned NOT NULL ,
                           `user_id` bigint(20) unsigned NOT NULL ,
                           `stars` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' ,
                           `likes` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' ,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `star_obj_user` (`object_id`,`user_id`) USING BTREE,
                           KEY `star_obj_id` (`object_id`) USING BTREE,
                           KEY `star_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_pubs`
-- ----------------------------
DROP TABLE IF EXISTS `k_pubs`;
CREATE TABLE `k_pubs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `pub_content` longtext COLLATE utf8mb4_unicode_ci,
  `pub_title` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `pub_excerpt` text COLLATE utf8mb4_unicode_ci ,
  `pub_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `comment_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `pub_password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `pub_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' ,
  `pub_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `domain_id` bigint(20) unsigned DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `pub_mime_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `comment_count` int(10) NOT NULL DEFAULT '0' ,
  `star_count` int(10) DEFAULT '0' ,
  `like_count` int(10) DEFAULT '0' ,
  `views` int(10) unsigned DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) unsigned DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `post_name` (`pub_name`(191)) USING BTREE,
  KEY `post_parent` (`parent_id`) USING BTREE,
  KEY `post_create_by` (`create_by`) USING BTREE,
  KEY `post_delete_flag` (`delete_flag`) USING BTREE,
  KEY `post_type_code` (`pub_type`) USING BTREE,
  KEY `post_com_id` (`com_id`) USING BTREE,
  KEY `type_status_date` (`com_id`,`pub_type`,`domain_id`,`pub_status`,`create_time`,`id`) USING BTREE,
  KEY `post_domain_id` (`domain_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1547701778556567557 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_term_object`
-- ----------------------------
DROP TABLE IF EXISTS `k_term_object`;
CREATE TABLE `k_term_object` (
  `id` bigint(20) unsigned NOT NULL,
  `object_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `term_type_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `term_order` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `term_class_id` (`term_type_id`) USING BTREE,
  KEY `term_object_id` (`object_id`),
  KEY `term_object_type` (`object_id`,`term_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_term_type`
-- ----------------------------
DROP TABLE IF EXISTS `k_term_type`;
CREATE TABLE `k_term_type` (
  `id` bigint(20) unsigned NOT NULL,
  `term_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `class_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL ,
  `description` longtext COLLATE utf8mb4_unicode_ci,
  `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `count` bigint(20) unsigned NOT NULL DEFAULT '0' ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `term_id_class` (`term_id`,`class_type`) USING BTREE,
  KEY `class_type` (`class_type`) USING BTREE,
  KEY `term_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `k_termmeta`
-- ----------------------------
DROP TABLE IF EXISTS `k_termmeta`;
CREATE TABLE `k_termmeta` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `term_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `meta_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `meta_value` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `term_id` (`term_id`),
  KEY `meta_key` (`meta_key`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_termmeta
-- ----------------------------

-- ----------------------------
-- Table structure for `k_terms`
-- ----------------------------
DROP TABLE IF EXISTS `k_terms`;
CREATE TABLE `k_terms` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `slug` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `info_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' ,
  `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' ,
  `display_order` int(10) DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`(191)) USING BTREE,
  KEY `name` (`name`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `wo_account_association`
-- ----------------------------
DROP TABLE IF EXISTS `wo_account_association`;
CREATE TABLE `wo_account_association` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT ,
  `user_id` bigint(20) unsigned DEFAULT NULL ,
  `bind_account` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `third_domain` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
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

-- ----------------------------
-- Records of wo_account_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_app`
-- ----------------------------
DROP TABLE IF EXISTS `wo_app`;
CREATE TABLE `wo_app` (
  `id` bigint(20) unsigned NOT NULL ,
  `app_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `app_secret` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `app_code` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `app_desc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `app_type` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_code` (`app_code`),
  KEY `app_type` (`app_type`),
  KEY `app_com_id` (`com_id`),
  KEY `app_is_valid_del` (`is_valid`,`delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ;

-- ----------------------------
-- Table structure for `wo_architecture`
-- ----------------------------
DROP TABLE IF EXISTS `wo_architecture`;
CREATE TABLE `wo_architecture` (
  `id` bigint(20) unsigned NOT NULL ,
  `arch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `arch_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `arch_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_arch` (`arch_code`,`com_id`),
  KEY `arch_code` (`arch_code`),
  KEY `arch_com_id` (`com_id`),
  KEY `arch_parent_id` (`parent_id`),
  KEY `arch_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


-- ----------------------------
-- Table structure for `wo_auth_role`
-- ----------------------------
DROP TABLE IF EXISTS `wo_auth_role`;
CREATE TABLE `wo_auth_role` (
  `id` bigint(20) unsigned NOT NULL ,
  `role_id` bigint(20) unsigned DEFAULT NULL ,
  `resource_id` bigint(20) unsigned DEFAULT NULL ,
  `app_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `auth_role_id` (`role_id`),
  KEY `auth_res_id` (`resource_id`),
  KEY `auth_app_id` (`app_id`),
  KEY `auth_is_valid_del` (`is_valid`,`delete_flag`),
  KEY `auth_role_res_app` (`role_id`,`resource_id`,`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_com_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_com_user`;
CREATE TABLE `wo_com_user` (
  `id` bigint(20) unsigned NOT NULL ,
  `user_id` bigint(20) unsigned DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `is_main` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `com_user_id` (`user_id`),
  KEY `com_id` (`com_id`),
  KEY `com_is_main` (`is_main`),
  KEY `com_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_company`
-- ----------------------------
DROP TABLE IF EXISTS `wo_company`;
CREATE TABLE `wo_company` (
  `id` bigint(20) unsigned NOT NULL ,
  `com_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `com_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `com_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_code` (`com_code`),
  KEY `com_parent_id` (`parent_id`),
  KEY `com_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


-- ----------------------------
-- Table structure for `wo_domain`
-- ----------------------------
DROP TABLE IF EXISTS `wo_domain`;
CREATE TABLE `wo_domain` (
  `id` bigint(20) unsigned NOT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `site_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `second_domain` varchar(255) DEFAULT NULL ,
  `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `site_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `site_logo` varchar(50) DEFAULT NULL ,
  `favicon` varchar(50) DEFAULT NULL ,
  `site_title` varchar(255) DEFAULT NULL ,
  `site_keyword` varchar(500) DEFAULT NULL ,
  `site_description` varchar(500) DEFAULT NULL ,
  `slogan` varchar(100) DEFAULT NULL ,
  `foot` text ,
  `flink` text ,
  `copy` text ,
  `parent_id` bigint(20) DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `cname_domain` varchar(255) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_idx_site_domain` (`site_domain`),
  UNIQUE KEY `uni_second_dom` (`second_domain`),
  KEY `domain_com_id` (`com_id`),
  KEY `dom_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


-- ----------------------------
-- Table structure for `wo_domain_app`
-- ----------------------------
DROP TABLE IF EXISTS `wo_domain_app`;
CREATE TABLE `wo_domain_app` (
  `id` bigint(21) unsigned NOT NULL,
  `app_id` bigint(21) unsigned DEFAULT NULL ,
  `domain_id` bigint(21) DEFAULT NULL ,
  `com_id` bigint(21) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `dom_app_id` (`app_id`),
  KEY `domain_id` (`domain_id`),
  KEY `dom_com_id` (`com_id`),
  KEY `dom_is_valid_del` (`is_valid`,`delete_flag`),
  KEY `dom_app_com` (`app_id`,`domain_id`,`com_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wo_domain_resource`
-- ----------------------------
DROP TABLE IF EXISTS `wo_domain_resource`;
CREATE TABLE `wo_domain_resource` (
  `id` bigint(20) NOT NULL,
  `module_name` varchar(50) NOT NULL DEFAULT 'static' ,
  `resource_id` bigint(20) unsigned NOT NULL ,
  `app_id` bigint(20) unsigned DEFAULT NULL ,
  `term_type_id` bigint(20) DEFAULT '0' ,
  `domain_id` bigint(20) unsigned NOT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) unsigned DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dom_res_route` (`domain_id`,`resource_id`) USING BTREE,
  KEY `dom_res_valid` (`is_valid`,`delete_flag`),
  KEY `dom_res_app` (`domain_id`,`app_id`,`resource_id`),
  KEY `dom_res_module` (`module_name`),
  KEY `dom_res_did` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wo_file`
-- ----------------------------
DROP TABLE IF EXISTS `wo_file`;
CREATE TABLE `wo_file` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `file_is_valid_del` (`is_valid`,`delete_flag`),
  KEY `file_mime_type` (`mime_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wo_mail`
-- ----------------------------
DROP TABLE IF EXISTS `wo_mail`;
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

-- ----------------------------
-- Table structure for `wo_oauth_login_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_oauth_login_user`;
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
) ENGINE=InnoDB AUTO_INCREMENT=214858324877426693 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ;

-- ----------------------------
-- Table structure for `wo_options`
-- ----------------------------
DROP TABLE IF EXISTS `wo_options`;
CREATE TABLE `wo_options` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `option_key` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `option_name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `option_value` longtext COLLATE utf8mb4_unicode_ci,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `option_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'no' ,
  `app_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `option_name` (`option_key`),
  KEY `options_app_type` (`option_type`),
  KEY `opetions_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=269981894250774530 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for `wo_org`
-- ----------------------------
DROP TABLE IF EXISTS `wo_org`;
CREATE TABLE `wo_org` (
  `id` bigint(20) unsigned NOT NULL ,
  `org_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `org_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `org_logo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `org_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `arch_id` bigint(20) unsigned DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_arch_org` (`org_code`,`arch_id`,`com_id`),
  KEY `org_type` (`org_type`),
  KEY `org_arch_id` (`arch_id`),
  KEY `org_com_id` (`com_id`),
  KEY `org_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_org_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_org_user`;
CREATE TABLE `wo_org_user` (
  `id` bigint(20) unsigned NOT NULL ,
  `user_id` bigint(20) unsigned DEFAULT NULL ,
  `user_com_id` bigint(20) unsigned DEFAULT NULL ,
  `org_id` bigint(20) unsigned DEFAULT NULL ,
  `arch_id` bigint(20) unsigned DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'normal' ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `org_user_id` (`user_id`),
  KEY `org_id` (`org_id`),
  KEY `org_user_arch` (`arch_id`),
  KEY `org_user_com` (`com_id`),
  KEY `org_user_is_valid_del` (`is_valid`,`delete_flag`),
  KEY `org_user_arch_com` (`user_id`,`user_com_id`,`org_id`,`arch_id`,`com_id`) USING BTREE,
  KEY `org_u_com_id` (`user_com_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_region`
-- ----------------------------
DROP TABLE IF EXISTS `wo_region`;
CREATE TABLE `wo_region` (
  `id` bigint(20) unsigned NOT NULL,
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `region_code` (`region_code`),
  KEY `region_level` (`level`),
  KEY `region_parent_id` (`parent_id`),
  KEY `region_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wo_resource`
-- ----------------------------
DROP TABLE IF EXISTS `wo_resource`;
CREATE TABLE `wo_resource` (
  `id` bigint(20) unsigned NOT NULL ,
  `resource_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `resource_path` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `resource_type` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `request_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `target` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `app_id` bigint(20) unsigned DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_app_res_code` (`resource_code`,`app_id`) USING BTREE,
  KEY `res_type` (`resource_type`) USING BTREE,
  KEY `res_app_id` (`app_id`) USING BTREE,
  KEY `res_parent_id` (`parent_id`) USING BTREE,
  KEY `res_is_valid_del` (`is_valid`,`delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `wo_role`
-- ----------------------------
DROP TABLE IF EXISTS `wo_role`;
CREATE TABLE `wo_role` (
  `id` bigint(20) unsigned NOT NULL ,
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `role_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `role_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `parent_id` bigint(20) unsigned DEFAULT NULL ,
  `display_order` int(10) DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_role_code` (`role_code`),
  KEY `role_type` (`role_type`),
  KEY `role_parent_id` (`parent_id`),
  KEY `role_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_role_org`
-- ----------------------------
DROP TABLE IF EXISTS `wo_role_org`;
CREATE TABLE `wo_role_org` (
  `id` bigint(20) unsigned NOT NULL ,
  `role_id` bigint(20) unsigned DEFAULT NULL ,
  `org_id` bigint(20) unsigned DEFAULT NULL ,
  `arch_id` bigint(20) unsigned DEFAULT NULL ,
  `com_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `org_role_id` (`role_id`),
  KEY `org_role_org_id` (`org_id`),
  KEY `org_arch_com` (`org_id`,`arch_id`,`com_id`),
  KEY `org_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_subject_association`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_association`;
CREATE TABLE `wo_subject_association` (
  `id` bigint(20) unsigned NOT NULL ,
  `subject_type_id` bigint(20) unsigned DEFAULT NULL ,
  `role_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `sub_type_id` (`subject_type_id`),
  KEY `sub_role_id` (`role_id`),
  KEY `sub_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Records of wo_subject_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_authentication`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_authentication`;
CREATE TABLE `wo_subject_authentication` (
  `id` bigint(20) unsigned NOT NULL ,
  `subject_type_id` bigint(20) unsigned DEFAULT NULL ,
  `subject_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `user_id` bigint(20) unsigned DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `sub_type_id` (`subject_type_id`),
  KEY `sub_user_id` (`user_id`),
  KEY `sub_is_valid_del` (`is_valid`,`delete_flag`),
  KEY `sub_code` (`subject_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Records of wo_subject_authentication
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_define`;
CREATE TABLE `wo_subject_define` (
  `id` bigint(20) unsigned NOT NULL ,
  `subject_type_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `subject_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_subject_type_code` (`subject_type_code`) USING BTREE,
  KEY `sub_def_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Records of wo_subject_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_model_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_model_define`;
CREATE TABLE `wo_subject_model_define` (
  `id` bigint(20) unsigned DEFAULT NULL ,
  `subject_type_id` bigint(20) unsigned NOT NULL ,
  `subject_model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`subject_type_id`),
  KEY `sub_model_type_id` (`subject_type_id`),
  KEY `sub_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Records of wo_subject_model_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_user`;
CREATE TABLE `wo_user` (
  `id` bigint(20) unsigned NOT NULL ,
  `login_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL ,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `passwd` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `domain_id` bigint(20) unsigned DEFAULT NULL ,
  `id_card` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `birthday` datetime DEFAULT NULL ,
  `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `telephone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '注册会员' ,
  `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '网络用户' ,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `qq` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `email` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `remark` varchar(230) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `display_order` bigint(20) DEFAULT NULL ,
  `is_real` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `invite_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `recommend_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `register_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `create_by` bigint(20) unsigned DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL ,
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `versions` int(10) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_login` (`login_name`) USING BTREE,
  KEY `user_status` (`status`),
  KEY `user_sex` (`sex`),
  KEY `user_del` (`delete_flag`),
  KEY `user_prov_city_area_coun` (`province`,`city`,`area`,`country`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for `wo_usermeta`
-- ----------------------------
DROP TABLE IF EXISTS `wo_usermeta`;
CREATE TABLE `wo_usermeta` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `meta_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  `meta_value` longtext COLLATE utf8mb4_unicode_ci ,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `meta_key` (`meta_key`(191))
) ENGINE=InnoDB AUTO_INCREMENT=269269458744950785 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
