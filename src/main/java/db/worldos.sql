/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.24
Source Server Version : 50729
Source Host           : 192.168.1.24:3306
Source Database       : worldos

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2021-07-14 20:13:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `wo_account_association`
-- ----------------------------
DROP TABLE IF EXISTS `wo_account_association`;
CREATE TABLE `wo_account_association` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账号关系表，主键不参与业务关联',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联后创建的账号id',
  `bind_account` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方关联账号,手机号也属于第三方账号',
  `third_domain` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '三方域（比如QQ号的域：qq.com）',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号关联表';

-- ----------------------------
-- Records of wo_account_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_app`
-- ----------------------------
DROP TABLE IF EXISTS `wo_app`;
CREATE TABLE `wo_app` (
  `id` bigint(20) unsigned NOT NULL COMMENT '应用id',
  `app_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用名称',
  `app_secret` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用秘钥',
  `app_code` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用编码：必须支持URL解析，最长5位，将作为请求路径基础path',
  `app_desc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'APP说明',
  `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效、1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用定义表';

-- ----------------------------
-- Records of wo_app
-- ----------------------------
INSERT INTO `wo_app` VALUES ('1504630177560969219', '快速开发平台', 'wldos-egine', 'dev', '全方位快速搭建企业级应用平台', '1', '0', '2021-05-14 23:43:09', '127.0.0.1', '0', '2021-05-14 23:43:09', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1506005013902311434', '系统管理', 'wldos-admin', 'admin', 'SaaS平台管理端', '1', '1', '2021-05-18 18:46:16', '127.0.0.1', '1', '2021-05-18 18:46:16', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1506113043159498757', '门户', 'wldos-portal', '/', 'SaaS平台前端门户', '1', '0', '2021-05-19 01:55:32', '127.0.0.1', '0', '2021-05-19 01:55:32', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_app` VALUES ('1511494438434291716', '用户设置', 'abcdefgjdlfjsd', 'user', '前端用户设置', '1', '1', '2021-06-02 22:19:16', '127.0.0.1', '1', '2021-06-02 22:19:16', '127.0.0.1', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_architecture`
-- ----------------------------
DROP TABLE IF EXISTS `wo_architecture`;
CREATE TABLE `wo_architecture` (
  `id` bigint(20) unsigned NOT NULL COMMENT '体系结构id',
  `arch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '体系 结构编码',
  `arch_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '体系结构类型名称：组织架构、团队、群组、圈子',
  `arch_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属公司id',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级体系结构',
  `display_order` int(10) DEFAULT NULL COMMENT '在上级公司下的展示顺序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_arch` (`arch_code`,`com_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体系结构定义表';

-- ----------------------------
-- Records of wo_architecture
-- ----------------------------
INSERT INTO `wo_architecture` VALUES ('100', 'org', '组织机构', null, '0', '0', '2', '1', '0', '2021-05-08 14:58:35', '0', '0', '127.0.0.1', '2021-05-26 23:13:40', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('200', 'team', '团队', null, '0', '0', '3', '1', '0', '2021-05-08 15:00:33', '0', '0', '0', '2021-05-08 15:00:41', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('300', 'group', '群组', null, '0', '0', '4', '1', '0', '2021-05-08 15:01:19', '0', '0', '0', '2021-05-08 15:01:32', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('400', 'circle', '圈子', null, '0', '0', '5', '1', '0', '2021-05-08 15:02:37', '0', '0', '0', '2021-05-08 15:02:46', 'normal', null);

-- ----------------------------
-- Table structure for `wo_auth_role`
-- ----------------------------
DROP TABLE IF EXISTS `wo_auth_role`;
CREATE TABLE `wo_auth_role` (
  `id` bigint(20) unsigned NOT NULL COMMENT '权限id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '拥有者id：可以为角色、组织或用户',
  `resource_id` bigint(20) unsigned DEFAULT NULL COMMENT '涉及资源',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属应用',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限定义表';

-- ----------------------------
-- Records of wo_auth_role
-- ----------------------------
INSERT INTO `wo_auth_role` VALUES ('1509212657572036617', '1', '100', '1506113043159498757', '1', '0', '2021-05-27 15:12:17', '127.0.0.1', '0', '2021-05-27 15:12:17', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1509212657576230917', '1', '101', '1506113043159498757', '1', '0', '2021-05-27 15:12:17', '127.0.0.1', '0', '2021-05-27 15:12:17', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475258183682', '2', '100', '1506113043159498757', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475262377991', '2', '1511494161530535947', '1511494438434291716', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475266572297', '2', '1522661730694119427', '1504618238889869317', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475270766592', '2', '1511496878801993736', '1511494438434291716', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475274960907', '2', '1511736612090462209', '1511494438434291716', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475279155205', '2', '1522997390516862983', '1504618238889869317', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475283349512', '2', '1523270396090695683', '1504618238889869317', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270475287543811', '2', '1511495802770079746', '1511494438434291716', '1', '1', '2021-07-05 10:13:02', '192.168.1.23', '1', '2021-07-05 10:13:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515468976134', '1506681836080381960', '1522661730694119427', '1504618238889869317', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170436', '1506681836080381960', '1511496878801993736', '1511494438434291716', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170437', '1506681836080381960', '1511736612090462209', '1511494438434291716', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170438', '1506681836080381960', '100', '1506113043159498757', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170439', '1506681836080381960', '1511495802770079746', '1511494438434291716', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170440', '1506681836080381960', '1511494161530535947', '1511494438434291716', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170441', '1506681836080381960', '1522997390516862983', '1504618238889869317', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270515473170442', '1506681836080381960', '1523270396090695683', '1504618238889869317', '1', '1', '2021-07-05 10:13:12', '192.168.1.23', '1', '2021-07-05 10:13:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549115682823', '1509213016482824194', '1522997390516862983', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549119877122', '1509213016482824194', '1522661730694119427', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549124071430', '1509213016482824194', '1518555863791091716', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549128265739', '1509213016482824194', '1509211664167911432', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549132460034', '1509213016482824194', '1520466841923403786', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549136654342', '1509213016482824194', '1509212249457868808', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549140848640', '1509213016482824194', '1506125438066016267', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549145042951', '1509213016482824194', '1509184818118311946', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549149237255', '1509213016482824194', '1506127499163779081', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549166014468', '1509213016482824194', '1509177915053096962', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549174403076', '1509213016482824194', '1511496878801993736', '1511494438434291716', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549178597383', '1509213016482824194', '1506101733801771011', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549182791686', '1509213016482824194', '1523270396090695683', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549186985995', '1509213016482824194', '1511495802770079746', '1511494438434291716', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549191180290', '1509213016482824194', '1511494161530535947', '1511494438434291716', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549195374597', '1509213016482824194', '1506122443605590022', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549203763200', '1509213016482824194', '1509179615117754368', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549207957512', '1509213016482824194', '1506128052832878593', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549237317642', '1509213016482824194', '1506126590341988354', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549258289157', '1509213016482824194', '1506126989799112705', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549262483462', '1509213016482824194', '1520374289564090377', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549283454976', '1509213016482824194', '100', '1506113043159498757', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549287649282', '1509213016482824194', '1506128956323708934', '1504618238889869317', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549291843593', '1509213016482824194', '1511736612090462209', '1511494438434291716', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1523270549296037897', '1509213016482824194', '1506107866432061443', '1506005013902311434', '1', '1', '2021-07-05 10:13:20', '192.168.1.23', '1', '2021-07-05 10:13:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240053129227', '1525946478916976648', '1511495802770079746', '1511494438434291716', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240057323531', '1525946478916976648', '1522997390516862983', '1504618238889869317', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240061517828', '1525946478916976648', '1511494161530535947', '1511494438434291716', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240065712133', '1525946478916976648', '100', '1506113043159498757', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240069906433', '1525946478916976648', '1509179615117754368', '1506005013902311434', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240074100743', '1525946478916976648', '1511736612090462209', '1511494438434291716', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240074100744', '1525946478916976648', '1522661730694119427', '1504618238889869317', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240074100745', '1525946478916976648', '1511496878801993736', '1511494438434291716', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240153792515', '1525946478916976648', '1523270396090695683', '1504618238889869317', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240166375431', '1525946478916976648', '1509212249457868808', '1506005013902311434', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240258650112', '1525946478916976648', '1506127499163779081', '1504618238889869317', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240262844418', '1525946478916976648', '1509184818118311946', '1506005013902311434', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240267038731', '1525946478916976648', '1509211664167911432', '1506005013902311434', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('1525947240275427338', '1525946478916976648', '1518555863791091716', '1506005013902311434', '1', '1', '2021-07-12 19:29:33', '192.168.1.23', '1', '2021-07-12 19:29:33', '192.168.1.23', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_com_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_com_user`;
CREATE TABLE `wo_com_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '用户公司关系id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属公司id',
  `is_main` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否主企业：1是0否',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户公司关系表';

-- ----------------------------
-- Records of wo_com_user
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_company`
-- ----------------------------
DROP TABLE IF EXISTS `wo_company`;
CREATE TABLE `wo_company` (
  `id` bigint(20) unsigned NOT NULL COMMENT '公司id',
  `com_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公司编码',
  `com_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公司名称',
  `com_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公司描述',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级公司',
  `display_order` int(10) DEFAULT NULL COMMENT '在上级公司下的排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_code` (`com_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营公司定义表';

-- ----------------------------
-- Records of wo_company
-- ----------------------------
INSERT INTO `wo_company` VALUES ('1508132284859596808', 'aadfk18665377sdkfjsd', '树悉猿科技', '专业软件研发', '0', '2', '1', '0', '2021-05-24 15:39:16', '127.0.0.1', '0', '2021-05-24 15:41:59', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972085879947270', '886887854321', '天工开物科技有限公司', null, '0', '2', '1', '0', '2021-05-26 23:16:21', '127.0.0.1', '0', '2021-05-26 23:16:21', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972512004456457', '88688785432123423', '泥巴巴广告咨询有限公司', null, '0', '5', '1', '0', '2021-05-26 23:18:02', '127.0.0.1', '0', '2021-05-26 23:18:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972831958548480', 'babala100', '给你未来科创股份有限公司', null, '0', '6', '1', '0', '2021-05-26 23:19:18', '127.0.0.1', '0', '2021-05-26 23:19:18', '127.0.0.1', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_domains`
-- ----------------------------
DROP TABLE IF EXISTS `wo_domains`;
CREATE TABLE `wo_domains` (
  `id` bigint(20) unsigned NOT NULL COMMENT '站点id',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '归属公司id',
  `site_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站点域名: 如163.com',
  `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站点名称: 如网易',
  `domain_level` tinyint(1) DEFAULT NULL COMMENT '域名级别：顶级域名、二级域名、三级域名',
  `site_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主页地址',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级站点：顶级为0',
  `display_order` int(10) DEFAULT NULL COMMENT '在父级站点下的排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户站点';

-- ----------------------------
-- Records of wo_domains
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_file`
-- ----------------------------
DROP TABLE IF EXISTS `wo_file`;
CREATE TABLE `wo_file` (
  `id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
  `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件物理存储路径名',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件mime类型',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效、1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_file
-- ----------------------------
INSERT INTO `wo_file` VALUES ('1512167591858847747', '1.jpg', '/202106/04185408t8iGC8cV.jpg', null, '1', '1', '2021-06-04 18:54:09', '127.0.0.1', '1', '2021-06-04 18:54:09', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1512190300764422149', '1.jpg', '/202106/04202422ZnYj8h4r.jpg', null, '1', '1', '2021-06-04 20:24:23', '127.0.0.1', '1', '2021-06-04 20:24:23', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1512515152905945097', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/202106/05175513QxWygOLb.jpg', null, '1', '1', '2021-06-05 17:55:14', '127.0.0.1', '1', '2021-06-05 17:55:14', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521580558379565061', 'function(){return e}', '/202106/30181754GxX4rTdi.', null, '1', '1', '2021-06-30 18:17:55', '192.168.1.23', '1', '2021-06-30 18:17:55', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521583854334951428', 'function(){return e}', '/202106/30183100e2AG6LVK.', null, '1', '1', '2021-06-30 18:31:01', '192.168.1.23', '1', '2021-06-30 18:31:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521585638436683785', '88.jpg', '/202106/30183805TV0Cs23n.jpg', null, '1', '1', '2021-06-30 18:38:06', '192.168.1.23', '1', '2021-06-30 18:38:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521634745096257543', 'blobid0.jpg', '/202106/30215313Z2FmT0Rn.jpg', null, '1', '1', '2021-06-30 21:53:14', '192.168.1.23', '1', '2021-06-30 21:53:14', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521636385069121547', 'blobid0.jpg', '/202106/30215944gxFMfw2o.jpg', null, '1', '1', '2021-06-30 21:59:45', '192.168.1.23', '1', '2021-06-30 21:59:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521636662757212170', 'blobid1.jpg', '/202106/30220051q4xYbYIJ.jpg', null, '1', '1', '2021-06-30 22:00:51', '192.168.1.23', '1', '2021-06-30 22:00:51', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521636694390652934', 'blobid0.jpg', '/202106/30220058cK86T4QK.jpg', null, '1', '1', '2021-06-30 22:00:59', '192.168.1.23', '1', '2021-06-30 22:00:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521640762001506310', 'AQV0ZH7Vlx07NMqbnqPe01041200gImL0E010.mp4', '/202106/30221708ISdKK2NM.mp4', null, '1', '1', '2021-06-30 22:17:08', '192.168.1.23', '1', '2021-06-30 22:17:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521641183298371589', 'AQV0ZH7Vlx07NMqbnqPe01041200gImL0E010.mp4', '/202106/30221848dfeJEYtC.mp4', null, '1', '1', '2021-06-30 22:18:49', '192.168.1.23', '1', '2021-06-30 22:18:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521835823939108867', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202107/01111214M5QM6WfZ.jpg', null, '1', '1', '2021-07-01 11:12:15', '192.168.1.23', '1', '2021-07-01 11:12:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521985398154903557', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/01210636HUzH0B8H.jpg', null, '1', '1', '2021-07-01 21:06:36', '192.168.1.23', '1', '2021-07-01 21:06:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521987552915669001', '2121.jpg', '/202107/01211509g955STA2.jpg', null, '1', '1', '2021-07-01 21:15:10', '192.168.1.23', '1', '2021-07-01 21:15:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521988203007623174', '88.jpg', '/202107/01211744Z4wQ5Q0p.jpg', null, '1', '1', '2021-07-01 21:17:45', '192.168.1.23', '1', '2021-07-01 21:17:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1521988385698922496', 'bg_corner_tr.png', '/202107/01211828rUwaz6Jg.png', null, '1', '1', '2021-07-01 21:18:28', '192.168.1.23', '1', '2021-07-01 21:18:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522009470196695045', 'mceu_95207466111625150534906.jpg', '/202107/012242152TknQ1J6.jpg', null, '1', '1', '2021-07-01 22:42:15', '192.168.1.23', '1', '2021-07-01 22:42:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522184665381388297', '2121.jpg', '/202107/021018257KTVJ5Q1.jpg', null, '1', '1', '2021-07-02 10:18:25', '192.168.1.23', '1', '2021-07-02 10:18:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522187881393340426', 'mceu_80768307111625193071662.jpg', '/202107/02103111g8DQkpG6.jpg', null, '1', '1', '2021-07-02 10:31:12', '192.168.1.23', '1', '2021-07-02 10:31:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522298005709570052', 'ccnp.jpg', '/202107/02174847OqqlA5yd.jpg', null, '1', '1', '2021-07-02 17:48:47', '192.168.1.23', '1', '2021-07-02 17:48:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522306499934339075', '1900800517827128683.jpg', '/202107/02182232rHBLqMmM.jpg', null, '1', '1', '2021-07-02 18:22:33', '192.168.1.23', '1', '2021-07-02 18:22:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522308348884205574', '17297645_1452082789360_mthumb.jpg', '/202107/021829531a2eER8W.jpg', null, '1', '1', '2021-07-02 18:29:53', '192.168.1.23', '1', '2021-07-02 18:29:53', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522309797810716681', '2012815167420064.jpg', '/202107/02183538sK6GHThs.jpg', null, '1', '1', '2021-07-02 18:35:39', '192.168.1.23', '1', '2021-07-02 18:35:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522330496558350339', 'qr97-fxnzanm4083255.jpg', '/202107/02195753VMoUhjFm.jpg', null, '1', '1', '2021-07-02 19:57:54', '192.168.1.23', '1', '2021-07-02 19:57:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522385510844776457', '1111.jpg', '/202107/02233630FUVgJKVt.jpg', null, '1', '1', '2021-07-02 23:36:30', '192.168.1.23', '1', '2021-07-02 23:36:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522593731115139077', '1.jpg', '/202107/03132353zi3jwRuY.jpg', null, '1', '1', '2021-07-03 13:23:54', '192.168.1.23', '1', '2021-07-03 13:23:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522611303189495812', 'IMG20180804084633.jpg', '/202107/03143343AkrLfSCj.jpg', null, '1', '1', '2021-07-03 14:33:43', '192.168.1.23', '1', '2021-07-03 14:33:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522617952490930183', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/03150008yaveYKVl.jpg', null, '1', '1', '2021-07-03 15:00:09', '192.168.1.23', '1', '2021-07-03 15:00:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522619469860093960', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.png', '/202107/03150610KbZYVNwy.png', null, '1', '1', '2021-07-03 15:06:11', '192.168.1.23', '1', '2021-07-03 15:06:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522629939522355203', '微信图片_20201031175041.jpg', '/202107/03154746itqWWyI5.jpg', null, '1', '1', '2021-07-03 15:47:47', '192.168.1.23', '1', '2021-07-03 15:47:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522640526524465157', 'IMG20180804084633.jpg', '/202107/03162950SWPrUVkw.jpg', null, '1', '1', '2021-07-03 16:29:51', '192.168.1.23', '1', '2021-07-03 16:29:51', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522641538400305156', 'IMG20180804084633.jpg', '/202107/03163352xl6pGxod.jpg', null, '1', '1', '2021-07-03 16:33:52', '192.168.1.23', '1', '2021-07-03 16:33:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522665021318545418', 'IMG20180804084633.jpg', '/202107/03180710UqrERnNx.jpg', null, '1', '0', '2021-07-03 18:07:11', '192.168.1.23', '0', '2021-07-03 18:07:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522672422776127489', 'IMG20180804084633.jpg', '/202107/03183635mC1chK2y.jpg', null, '1', '1', '2021-07-03 18:36:35', '192.168.1.23', '1', '2021-07-03 18:36:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522674815114854400', 'IMG20180804084633.jpg', '/202107/03184605Ew4lk6l1.jpg', null, '1', '1', '2021-07-03 18:46:06', '192.168.1.23', '1', '2021-07-03 18:46:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522678979081453579', 'IMG20180804084633.jpg', '/202107/03190238GHsf74KJ.jpg', null, '1', '1', '2021-07-03 19:02:39', '192.168.1.23', '1', '2021-07-03 19:02:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522680522786652164', '11.jpg', '/202107/03190846ZTcaCgET.jpg', null, '1', '1', '2021-07-03 19:08:47', '192.168.1.23', '1', '2021-07-03 19:08:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522680878291664898', '1038209_1200x1000_0.jpg', '/202107/03191011eDPm74Rf.jpg', null, '1', '1', '2021-07-03 19:10:11', '192.168.1.23', '1', '2021-07-03 19:10:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522682938139525122', 'lsnb.jpg', '/202107/031918222a5apAz7.jpg', null, '1', '1', '2021-07-03 19:18:23', '192.168.1.23', '1', '2021-07-03 19:18:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522684177367613440', '长城.jpg', '/202107/03192317Hqkt1aCv.jpg', null, '1', '1', '2021-07-03 19:23:18', '192.168.1.23', '1', '2021-07-03 19:23:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522686786757378058', '长城.jpg', '/202107/03193340dslCqmz4.jpg', null, '1', '1', '2021-07-03 19:33:40', '192.168.1.23', '1', '2021-07-03 19:33:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522688287143804930', '微信图片_20201031185321.jpg', '/202107/031939372gG1ucLR.jpg', null, '1', '1', '2021-07-03 19:39:38', '192.168.1.23', '1', '2021-07-03 19:39:38', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522732316237676550', 'QQ图片20160609124809.png', '/202107/03223435iBtHIBFe.png', null, '1', '1', '2021-07-03 22:34:35', '192.168.1.23', '1', '2021-07-03 22:34:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522748257394671618', '330625-140G502062028.jpg', '/202107/03233755URi8CZg7.jpg', null, '1', '1', '2021-07-03 23:37:56', '192.168.1.23', '1', '2021-07-03 23:37:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522750721426636803', 'IMG20180804084550.jpg', '/202107/03234743b7ZxneVI.jpg', null, '1', '1', '2021-07-03 23:47:43', '192.168.1.23', '1', '2021-07-03 23:47:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522974694974341129', '8MH7-fxnkkux1178471.jpg', '/202107/041437423EwJg7P1.jpg', null, '1', '1', '2021-07-04 14:37:43', '192.168.1.23', '1', '2021-07-04 14:37:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522989416868134913', '111.jpg', '/202107/04153612WKKICxeB.jpg', null, '1', '1', '2021-07-04 15:36:13', '192.168.1.23', '1', '2021-07-04 15:36:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522991675790245891', '捕获 (2).PNG', '/202107/041545117sZv9kZp.png', null, '1', '1', '2021-07-04 15:45:11', '192.168.1.23', '1', '2021-07-04 15:45:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522991918254571520', 'IMG20180804084633.jpg', '/202107/04154609q8qBFXpO.jpg', null, '1', '1', '2021-07-04 15:46:09', '192.168.1.23', '1', '2021-07-04 15:46:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1522992658243043328', 'QQ图片20160609124809.png', '/202107/04154905X74n7Duh.png', null, '1', '1', '2021-07-04 15:49:06', '192.168.1.23', '1', '2021-07-04 15:49:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523008567535910912', 'mceu_20011582911625388737448.jpg', '/202107/04165218xsISumIS.jpg', null, '1', '1', '2021-07-04 16:52:19', '192.168.1.23', '1', '2021-07-04 16:52:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523016206097498112', 'mceu_54513282311625390559475.dat', '/202107/04172239AauaheOt.dat', null, '1', '1', '2021-07-04 17:22:40', '192.168.1.23', '1', '2021-07-04 17:22:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523034442767712256', 'mceu_2133080311625394906994.jpg', '/202107/04183507MXbbuxtv.jpg', null, '1', '1', '2021-07-04 18:35:08', '192.168.1.23', '1', '2021-07-04 18:35:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523034743834853380', '2222.jpg', '/202107/041836197XjPxiKt.jpg', null, '1', '1', '2021-07-04 18:36:20', '192.168.1.23', '1', '2021-07-04 18:36:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523272095815614474', 'mceu_46950603311625451568273.png', '/202107/05101928SnsmpEPR.png', null, '1', '1', '2021-07-05 10:19:29', '192.168.1.23', '1', '2021-07-05 10:19:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523272769202733066', 'mceu_6078279621625451729050.jpg', '/202107/05102209XE5kjWmC.jpg', null, '1', '1', '2021-07-05 10:22:09', '192.168.1.23', '1', '2021-07-05 10:22:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523294794440949767', 'mceu_51942470731625456979772.jpg', '/202107/05114940Gmv9TzMp.jpg', null, '1', '1', '2021-07-05 11:49:40', '192.168.1.23', '1', '2021-07-05 11:49:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523328196858789892', 'mceu_74569273911625464943868.jpg', '/202107/05140224d7mGqaI2.jpg', null, '1', '1', '2021-07-05 14:02:24', '192.168.1.23', '1', '2021-07-05 14:02:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1523338616055185417', 'mceu_34672787721625467428008.jpg', '/202107/05144348pHnDlhO7.jpg', null, '1', '1', '2021-07-05 14:43:48', '192.168.1.23', '1', '2021-07-05 14:43:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524459455559417856', 'Borneo_FF_768_HD_ZH-CN1688139761.jpg', '/202107/08165737wdtCt0mJ.jpg', 'image/jpeg', '1', '1', '2021-07-08 16:57:37', '192.168.1.23', '1', '2021-07-08 16:57:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524740465731747843', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09113415LZC5c0pC.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:34:15', '192.168.1.23', '1', '2021-07-09 11:34:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524740528071688194', '2222.jpg', '/202107/09113430tx19MXvp.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:34:30', '192.168.1.23', '1', '2021-07-09 11:34:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524744907839422467', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09115154yTn9o0Zb.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:51:54', '192.168.1.23', '1', '2021-07-09 11:51:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524744955813871624', '111.jpg', '/202107/09115205MZ7e3UHM.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:52:06', '192.168.1.23', '1', '2021-07-09 11:52:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524745016090214408', '1038209_1200x1000_0.jpg', '/202107/091152201cDjrlzy.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:52:20', '192.168.1.23', '1', '2021-07-09 11:52:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524745056787546119', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202107/09115229TtZZDNX7.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:52:30', '192.168.1.23', '1', '2021-07-09 11:52:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524745114480197639', '606297099851665269.jpg', '/202107/09115243X3atwRcF.jpg', 'image/jpeg', '1', '1', '2021-07-09 11:52:44', '192.168.1.23', '1', '2021-07-09 11:52:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524745175918362629', 'bg_corner_tr.png', '/202107/091152587PDqqIBx.png', 'image/png', '1', '1', '2021-07-09 11:52:58', '192.168.1.23', '1', '2021-07-09 11:52:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524750727851458563', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09121502EWKWHY7h.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:15:02', '192.168.1.23', '1', '2021-07-09 12:15:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524754742416949254', '1.jpg', '/202107/091230594jkfYYaX.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:30:59', '192.168.1.23', '1', '2021-07-09 12:30:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524754786926903302', '111.jpg', '/202107/09123109A2cBSi8l.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:31:10', '192.168.1.23', '1', '2021-07-09 12:31:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524759543057924100', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09125003Fl8vmpMO.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:50:04', '192.168.1.23', '1', '2021-07-09 12:50:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524760144663724033', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09125227vOZSU6ca.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:52:27', '192.168.1.23', '1', '2021-07-09 12:52:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524760182232104966', '2121.jpg', '/202107/09125236n4srnXkZ.jpg', 'image/jpeg', '1', '1', '2021-07-09 12:52:36', '192.168.1.23', '1', '2021-07-09 12:52:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524767574541910019', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09132158XNZJWsRy.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:21:59', '192.168.1.23', '1', '2021-07-09 13:21:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524767980928024580', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/091323356sqMPlCS.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:23:35', '192.168.1.23', '1', '2021-07-09 13:23:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524769347126083593', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09132901SA3E5ErQ.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:29:01', '192.168.1.23', '1', '2021-07-09 13:29:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524769935058452488', '1.jpg', '/202107/09133121gjUgFy3M.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:31:21', '192.168.1.23', '1', '2021-07-09 13:31:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524772612072980490', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09134159b3iCRs2j.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:42:00', '192.168.1.23', '1', '2021-07-09 13:42:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524772648248852486', '88.jpg', '/202107/09134208SOB9gx76.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:42:08', '192.168.1.23', '1', '2021-07-09 13:42:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524773990891700234', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09134728IAT98BBt.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:47:28', '192.168.1.23', '1', '2021-07-09 13:47:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774027570888710', '2121.jpg', '/202107/09134737IiX6KyL4.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:47:37', '192.168.1.23', '1', '2021-07-09 13:47:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774460393701379', '88.jpg', '/202107/09134920j3DOmPra.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:49:20', '192.168.1.23', '1', '2021-07-09 13:49:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774494799577098', '111.jpg', '/202107/09134928Xk73Tjlc.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:49:29', '192.168.1.23', '1', '2021-07-09 13:49:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774533999542280', 'ccnp.jpg', '/202107/09134937i9gxIVjd.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:49:38', '192.168.1.23', '1', '2021-07-09 13:49:38', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774579126059017', 'ccnp.jpg', '/202107/09134948d1bN3smh.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:49:49', '192.168.1.23', '1', '2021-07-09 13:49:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524774619374600197', '1038209_1200x1000_0.jpg', '/202107/09134958MqS132m1.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:49:58', '192.168.1.23', '1', '2021-07-09 13:49:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524776416348651522', '330625-140G502062028.jpg', '/202107/09135706aCaPr33X.jpg', 'image/jpeg', '1', '1', '2021-07-09 13:57:07', '192.168.1.23', '1', '2021-07-09 13:57:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524781046751215617', '1038209_1200x1000_0.jpg', '/202107/09141530wG9wOU9E.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:15:31', '192.168.1.23', '1', '2021-07-09 14:15:31', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524782401293303815', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/091420538RUttrlE.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:20:54', '192.168.1.23', '1', '2021-07-09 14:20:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524788717101432836', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09144559RGA2yEvO.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:45:59', '192.168.1.23', '1', '2021-07-09 14:45:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524788782436106242', '330625-140G502062028.jpg', '/202107/09144614stJpKwaa.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:46:15', '192.168.1.23', '1', '2021-07-09 14:46:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524789047381901319', '1.jpg', '/202107/09144718k2Cx6t6N.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:47:18', '192.168.1.23', '1', '2021-07-09 14:47:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524790557100654594', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/091453185YQ1zWny.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:53:18', '192.168.1.23', '1', '2021-07-09 14:53:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524790607960784904', '111.jpg', '/202107/091453305LNocrGf.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:53:30', '192.168.1.23', '1', '2021-07-09 14:53:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524791307411308549', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09145616sP8gcIOj.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:56:17', '192.168.1.23', '1', '2021-07-09 14:56:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524791350679748608', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/0914562780hnMZQV.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:56:27', '192.168.1.23', '1', '2021-07-09 14:56:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524791392157220869', '330625-140G502062028.jpg', '/202107/091456377RZN11Xe.jpg', 'image/jpeg', '1', '1', '2021-07-09 14:56:37', '192.168.1.23', '1', '2021-07-09 14:56:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524793368278384643', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09150428e7zT2LyC.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:04:28', '192.168.1.23', '1', '2021-07-09 15:04:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524797099199873030', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09151917tEIOTfd3.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:19:18', '192.168.1.23', '1', '2021-07-09 15:19:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524797886328127494', '2222.jpg', '/202107/09152225w32eTJVs.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:22:25', '192.168.1.23', '1', '2021-07-09 15:22:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524797932087984139', '1038209_1200x1000_0.jpg', '/202107/09152236J7SAHMao.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:22:36', '192.168.1.23', '1', '2021-07-09 15:22:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524797985120763915', 'Borneo_FF_768_HD_ZH-CN1688139761.jpg', '/202107/091522495VPO9HOY.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:22:49', '192.168.1.23', '1', '2021-07-09 15:22:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524798035188170761', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/202107/09152300LbkZBEZT.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:23:01', '192.168.1.23', '1', '2021-07-09 15:23:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524798083179397127', '2012815167420064.jpg', '/202107/09152312B5XCO1rc.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:23:12', '192.168.1.23', '1', '2021-07-09 15:23:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524798634029924352', '1.jpg', '/202107/09152523CsaPVExK.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:25:24', '192.168.1.23', '1', '2021-07-09 15:25:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524798670239350795', '2121.jpg', '/202107/09152532nJJymuWY.jpg', 'image/jpeg', '1', '1', '2021-07-09 15:25:32', '192.168.1.23', '1', '2021-07-09 15:25:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524882841305530368', 'xy005.jpg', '/202107/09210000qsqovR7C.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:00:00', '192.168.1.23', '1', '2021-07-09 21:00:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524882915737649163', 'lsnb.jpg', '/202107/09210018UfQf860W.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:00:18', '192.168.1.23', '1', '2021-07-09 21:00:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885184151470087', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09210918OicmUK6C.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:09:19', '192.168.1.23', '1', '2021-07-09 21:09:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885328368418816', '39403_2.jpg', '/202107/09210953sPGxAc10.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:09:53', '192.168.1.23', '1', '2021-07-09 21:09:53', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885373457186821', '1.jpg', '/202107/092110047vy2g6yk.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:10:04', '192.168.1.23', '1', '2021-07-09 21:10:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885438275960842', '1.jpg', '/202107/09211019zURmAcIT.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:10:19', '192.168.1.23', '1', '2021-07-09 21:10:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885657499648001', '1038209_1200x1000_0.jpg', '/202107/09211111GcBkr0DU.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:11:12', '192.168.1.23', '1', '2021-07-09 21:11:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885767101005833', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/202107/092111376O6R0fld.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:11:38', '192.168.1.23', '1', '2021-07-09 21:11:38', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524885825687044102', '39403_2.jpg', '/202107/09211151is30ISXx.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:11:52', '192.168.1.23', '1', '2021-07-09 21:11:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524886474470375426', '39403_2.jpg', '/202107/09211426Xr5490Q7.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:14:27', '192.168.1.23', '1', '2021-07-09 21:14:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524887006584946689', '88.jpg', '/202107/09211633PPJktYr0.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:16:33', '192.168.1.23', '1', '2021-07-09 21:16:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524887344398385159', '39403_2.jpg', '/202107/09211753HmzSGMPp.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:17:54', '192.168.1.23', '1', '2021-07-09 21:17:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524887392305725440', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09211805IA7XE38a.jpg', 'image/jpeg', '1', '1', '2021-07-09 21:18:05', '192.168.1.23', '1', '2021-07-09 21:18:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524909953215283210', 'pbsg-fxnzanm3927909.jpg', '/202107/09224744N2sulH6C.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:47:44', '192.168.1.23', '1', '2021-07-09 22:47:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524910041023037451', 'ji.jpg', '/202107/09224805YKei92gD.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:48:05', '192.168.1.23', '1', '2021-07-09 22:48:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524910702628356104', 'u=3989241436,3933899785&fm=21&gp=0.jpg', '/202107/09225042eCI9r8Al.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:50:43', '192.168.1.23', '1', '2021-07-09 22:50:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524910794408116231', 'qsh.jpg', '/202107/09225104796Rppan.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:51:05', '192.168.1.23', '1', '2021-07-09 22:51:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524910859172364292', 'QQ图片20160214100827.png', '/202107/09225120uD4cicpj.png', 'image/png', '1', '1', '2021-07-09 22:51:20', '192.168.1.23', '1', '2021-07-09 22:51:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524911112680292361', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202107/09225220CAhpIVRO.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:52:21', '192.168.1.23', '1', '2021-07-09 22:52:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524911146729652229', '88.jpg', '/202107/09225228uWwcU0qP.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:52:29', '192.168.1.23', '1', '2021-07-09 22:52:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524911192384651268', 'p-sm-fxvkkcf6414403.jpg', '/202107/09225239VDqvLzzO.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:52:40', '192.168.1.23', '1', '2021-07-09 22:52:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524911236949131272', '39403_2.jpg', '/202107/09225250nMSJ4mSU.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:52:50', '192.168.1.23', '1', '2021-07-09 22:52:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1524911332411490305', '17297645_1452082789360_mthumb.jpg', '/202107/09225313GJDgE0VC.jpg', 'image/jpeg', '1', '1', '2021-07-09 22:53:13', '192.168.1.23', '1', '2021-07-09 22:53:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525836711863173130', 'mceu_15931776911626063020193.jpg', '/202107/12121020z9HS5TXu.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:10:21', '192.168.1.23', '1', '2021-07-12 12:10:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525842354762334208', '0930391S1-3.jpg', '/202107/12123246VN5IZP1d.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:32:46', '192.168.1.23', '1', '2021-07-12 12:32:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525842518549905410', '1038209_1200x1000_0.jpg', '/202107/12123325Ihi1lj7b.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:33:25', '192.168.1.23', '1', '2021-07-12 12:33:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525843649715945477', '330625-140G502062028.jpg', '/202107/12123754KtjSITZz.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:37:55', '192.168.1.23', '1', '2021-07-12 12:37:55', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525843698940297217', 'QQ图片20160214100827.png', '/202107/12123806EnBEdNA9.png', 'image/png', '1', '1', '2021-07-12 12:38:07', '192.168.1.23', '1', '2021-07-12 12:38:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525843750500876297', 'QQ图片20200828191238.png', '/202107/12123818Sh5ZVpZw.png', 'image/png', '1', '1', '2021-07-12 12:38:19', '192.168.1.23', '1', '2021-07-12 12:38:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525843801155485707', '39403_2.jpg', '/202107/12123831vbhmXVRi.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:38:31', '192.168.1.23', '1', '2021-07-12 12:38:31', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525844150566174730', 'mceu_58477693911626064794017.jpg', '/202107/12123954phlxk42c.jpg', 'image/jpeg', '1', '1', '2021-07-12 12:39:54', '192.168.1.23', '1', '2021-07-12 12:39:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525847314623283209', '5dce8f5a7cab37b98dc83100b91f6e4d.mp4', '/202107/12125228JYy9mxL4.mp4', 'video/mp4', '1', '1', '2021-07-12 12:52:29', '192.168.1.23', '1', '2021-07-12 12:52:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1525852787405668361', '5dce8f5a7cab37b98dc83100b91f6e4d.mp4', '/202107/12131413BvKthFFT.mp4', 'video/mp4', '1', '1', '2021-07-12 13:14:13', '192.168.1.23', '1', '2021-07-12 13:14:13', '192.168.1.23', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_options`
-- ----------------------------
DROP TABLE IF EXISTS `wo_options`;
CREATE TABLE `wo_options` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置key',
  `value` longtext COLLATE utf8mb4_unicode_ci,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `app_type` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `option_name` (`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=149 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of wo_options
-- ----------------------------
INSERT INTO `wo_options` VALUES ('1', 'siteurl', null, 'http://192.168.1.24', null, null);
INSERT INTO `wo_options` VALUES ('5', 'users_can_register', null, '0', null, null);
INSERT INTO `wo_options` VALUES ('6', 'admin_email', null, '306991142@qq.com', null, null);
INSERT INTO `wo_options` VALUES ('45', 'default_group', null, 'freeuser', null, null);

-- ----------------------------
-- Table structure for `wo_org`
-- ----------------------------
DROP TABLE IF EXISTS `wo_org`;
CREATE TABLE `wo_org` (
  `id` bigint(20) unsigned NOT NULL COMMENT '组织机构id',
  `org_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组织机构编码',
  `org_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组织机构名称',
  `org_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组织类型',
  `arch_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属体系结构id',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属公司id',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级体系结构',
  `display_order` int(10) DEFAULT NULL COMMENT '在上级公司下的排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_com_arch_org` (`org_code`,`arch_id`,`com_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织机构表';

-- ----------------------------
-- Records of wo_org
-- ----------------------------
INSERT INTO `wo_org` VALUES ('1', 'admin', '管理员', 'platform', '0', '0', '0', '2', '1', '0', '2021-05-08 14:17:20', '0', '0', '2021-05-08 14:17:28', '0', 'normal', null);
INSERT INTO `wo_org` VALUES ('2', 'user', '会员', 'platform', '0', '0', '0', '3', '1', '0', '2021-05-08 14:18:51', '0', '0', '2021-05-08 14:18:59', '0', 'normal', null);
INSERT INTO `wo_org` VALUES ('200', 'freeuser', '普通会员', 'platform', '0', '0', '2', '1', '1', '0', '2021-05-08 14:32:48', '0', '0', '2021-05-08 14:32:56', '0', 'normal', '1');
INSERT INTO `wo_org` VALUES ('201', 'vip', '普通VIP', 'platform', '0', '0', '2', '2', '1', '0', '2021-05-08 14:34:35', '0', '0', '2021-05-08 14:34:43', '0', 'normal', null);
INSERT INTO `wo_org` VALUES ('1508971189611708427', '1011012345120105', '网络斗士（中国）科技无限责任有限公司', 'org', '100', '1508132284859596808', '0', '4', '1', '0', '2021-05-26 23:12:47', '127.0.0.1', '0', '2021-05-26 23:12:47', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508973076528414721', '101101234512010501', '市场研发部', 'org', '100', '1508132284859596808', '1508971189611708427', '1', '1', '0', '2021-05-26 23:20:17', '127.0.0.1', '0', '2021-05-26 23:20:17', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508973453109805063', '1011012345120105011', '销售顾问群', 'group', '300', '1508132284859596808', '1508973076528414721', '1', '1', '0', '2021-05-26 23:21:47', '127.0.0.1', '0', '2021-05-26 23:21:47', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508974093827489796', '1011012345120106', '科技攻关部', 'org', '100', '1508132284859596808', '1508971189611708427', '2', '1', '0', '2021-05-26 23:24:19', '127.0.0.1', '0', '2021-05-26 23:24:19', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508974496115769349', '101101234512010501', 'WLDOS改造世界系统项目组', 'team', '200', '1508132284859596808', '1508974093827489796', '1', '1', '0', '2021-05-26 23:25:55', '127.0.0.1', '0', '2021-05-26 23:25:55', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508975215187247108', '1011012345120100', '顶级想象力', 'circle', '400', '1508972512004456457', '0', '10', '1', '0', '2021-05-26 23:28:47', '127.0.0.1', '0', '2021-05-26 23:29:19', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508976067088138249', '10110123451201001', '套圈', 'circle', '400', '1508972512004456457', '1508975215187247108', '1', '1', '0', '2021-05-26 23:32:10', '127.0.0.1', '0', '2021-05-26 23:32:10', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508976740328456200', '101101234512010011', '圈长', 'role_org', '100', '1508972831958548480', '1508976067088138249', '1', '1', '0', '2021-05-26 23:34:50', '127.0.0.1', '0', '2021-05-26 23:34:50', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1525968690709446657', 'tadmin', '租户管理员', 'role_org', '0', '0', '0', '1', '1', '1', '2021-07-12 20:54:47', '192.168.1.23', '1', '2021-07-12 20:54:47', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1526214941484957699', 'badmin', '二级管理员', 'platform', '0', '0', '1', '1', '1', '1', '2021-07-13 13:13:18', '192.168.1.23', '1', '2021-07-13 13:13:18', '192.168.1.23', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_org_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_org_user`;
CREATE TABLE `wo_org_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '用户组织关系id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `org_id` bigint(20) unsigned DEFAULT NULL COMMENT '组织机构id',
  `arch_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属体系结构id',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属公司id',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '''normal''' COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户组织关联表：用户与不同体系下的组织关系，类似岗位，但不全是岗位，比如群组内的人，组织不同，由于平台角色只授予组织，已经实现了岗位的功能，此表不再做岗位定义，仅做用户与组织的关联表。';

-- ----------------------------
-- Records of wo_org_user
-- ----------------------------
INSERT INTO `wo_org_user` VALUES ('0', '1', '1', '0', '0', '1', '0', '0', '2021-05-08 11:51:00', '0', '0', '2021-05-08 11:51:07', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1', '100', '200', '0', '0', '1', '0', '0', '2021-05-08 21:41:13', '0', '0', '2021-05-08 21:41:20', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502723278259273735', '1502723278108278788', '200', '0', '0', '1', '1502723278108278788', '127.0.0.1', '2021-05-09 17:25:49', '1502723278108278788', '127.0.0.1', '2021-05-09 17:25:49', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502726588278161413', '1502726588102000651', '200', '0', '0', '1', '1502726588102000651', '127.0.0.1', '2021-05-09 17:38:58', '1502726588102000651', '127.0.0.1', '2021-05-09 17:38:58', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502739709487136769', '1502739709344530437', '200', '0', '0', '1', '1502739709344530437', '127.0.0.1', '2021-05-09 18:31:06', '1502739709344530437', '127.0.0.1', '2021-05-09 18:31:06', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502742261293301768', '1502742261263941642', '200', '0', '0', '1', '1502742261263941642', '127.0.0.1', '2021-05-09 18:41:15', '1502742261263941642', '127.0.0.1', '2021-05-09 18:41:15', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502770377218768901', '1502770377176825865', '200', '0', '0', '1', '1502770377176825865', '127.0.0.1', '2021-05-09 20:32:58', '1502770377176825865', '127.0.0.1', '2021-05-09 20:32:58', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502773236945567747', '1502773236924596235', '200', '0', '0', '1', '1502773236924596235', '127.0.0.1', '2021-05-09 20:44:20', '1502773236924596235', '127.0.0.1', '2021-05-09 20:44:20', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502777260721750018', '1502777260587532299', '200', '0', '0', '1', '1502777260587532299', '127.0.0.1', '2021-05-09 21:00:19', '1502777260587532299', '127.0.0.1', '2021-05-09 21:00:19', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502777813191278600', '1502777813036089350', '200', '0', '0', '1', '1502777813036089350', '127.0.0.1', '2021-05-09 21:02:31', '1502777813036089350', '127.0.0.1', '2021-05-09 21:02:31', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502783343708258304', '1502783343481765897', '200', '0', '0', '1', '1502783343481765897', '127.0.0.1', '2021-05-09 21:24:30', '1502783343481765897', '127.0.0.1', '2021-05-09 21:24:30', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502803624921317382', '1502803624724185094', '200', '0', '0', '1', '1502803624724185094', '127.0.0.1', '2021-05-09 22:45:05', '1502803624724185094', '127.0.0.1', '2021-05-09 22:45:05', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1509226657512865803', '1502739709344530437', '201', '0', '0', '1', '0', '127.0.0.1', '2021-05-27 16:07:55', '0', '127.0.0.1', '2021-05-27 16:07:55', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1521171389918920713', '1521171388547383299', '200', '0', '0', '1', '1521171388547383299', '192.168.1.23', '2021-06-29 15:12:01', '1521171388547383299', '192.168.1.23', '2021-06-29 15:12:01', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1526282871518183431', '100', '1525968690709446657', '0', '0', '1', '1', '192.168.1.23', '2021-07-13 17:43:14', '1', '192.168.1.23', '2021-07-13 17:43:14', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1526291590620561411', '100', '1508974093827489796', '100', '1508132284859596808', '1', '1', '192.168.1.23', '2021-07-13 18:17:52', '1', '192.168.1.23', '2021-07-13 18:17:52', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_region`
-- ----------------------------
DROP TABLE IF EXISTS `wo_region`;
CREATE TABLE `wo_region` (
  `id` bigint(20) unsigned NOT NULL,
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国标区域编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地区名称',
  `level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地区级别：1 省级，2市级，3区县级',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '父级地区',
  `display_order` int(10) DEFAULT NULL COMMENT '排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效、1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_region
-- ----------------------------
INSERT INTO `wo_region` VALUES ('110000', '110000', '北京市', '1', '0', '110000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('110100', '110100', '市辖区', '2', '110000', '110100', '1', '1', '2021-06-07 16:50:26', '127', '1', '2021-06-07 16:50:36', '125', 'normal', '1');
INSERT INTO `wo_region` VALUES ('120000', '120000', '天津市', '1', '0', '120000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('120100', '120100', '市辖区', '2', '120000', '120100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130000', '130000', '河北省', '1', '0', '130000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130100', '130100', '石家庄市', '2', '130000', '130100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130200', '130200', '唐山市', '2', '130000', '130200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130300', '130300', '秦皇岛市', '2', '130000', '130300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130400', '130400', '邯郸市', '2', '130000', '130400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130500', '130500', '邢台市', '2', '130000', '130500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130600', '130600', '保定市', '2', '130000', '130600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130700', '130700', '张家口市', '2', '130000', '130700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130800', '130800', '承德市', '2', '130000', '130800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('130900', '130900', '沧州市', '2', '130000', '130900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('131000', '131000', '廊坊市', '2', '130000', '131000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('131100', '131100', '衡水市', '2', '130000', '131100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('139000', '139000', '省直辖县级行政区划', '2', '130000', '139000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140000', '140000', '山西省', '1', '0', '140000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140100', '140100', '太原市', '2', '140000', '140100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140200', '140200', '大同市', '2', '140000', '140200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140300', '140300', '阳泉市', '2', '140000', '140300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140400', '140400', '长治市', '2', '140000', '140400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140500', '140500', '晋城市', '2', '140000', '140500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140600', '140600', '朔州市', '2', '140000', '140600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140700', '140700', '晋中市', '2', '140000', '140700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140800', '140800', '运城市', '2', '140000', '140800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('140900', '140900', '忻州市', '2', '140000', '140900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('141000', '141000', '临汾市', '2', '140000', '141000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('141100', '141100', '吕梁市', '2', '140000', '141100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150000', '150000', '内蒙古自治区', '1', '0', '150000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150100', '150100', '呼和浩特市', '2', '150000', '150100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150200', '150200', '包头市', '2', '150000', '150200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150300', '150300', '乌海市', '2', '150000', '150300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150400', '150400', '赤峰市', '2', '150000', '150400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150500', '150500', '通辽市', '2', '150000', '150500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150600', '150600', '鄂尔多斯市', '2', '150000', '150600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150700', '150700', '呼伦贝尔市', '2', '150000', '150700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150800', '150800', '巴彦淖尔市', '2', '150000', '150800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('150900', '150900', '乌兰察布市', '2', '150000', '150900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('152200', '152200', '兴安盟', '2', '150000', '152200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('152500', '152500', '锡林郭勒盟', '2', '150000', '152500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('152900', '152900', '阿拉善盟', '2', '150000', '152900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210000', '210000', '辽宁省', '1', '0', '210000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210100', '210100', '沈阳市', '2', '210000', '210100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210200', '210200', '大连市', '2', '210000', '210200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210300', '210300', '鞍山市', '2', '210000', '210300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210400', '210400', '抚顺市', '2', '210000', '210400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210500', '210500', '本溪市', '2', '210000', '210500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210600', '210600', '丹东市', '2', '210000', '210600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210700', '210700', '锦州市', '2', '210000', '210700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210800', '210800', '营口市', '2', '210000', '210800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('210900', '210900', '阜新市', '2', '210000', '210900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('211000', '211000', '辽阳市', '2', '210000', '211000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('211100', '211100', '盘锦市', '2', '210000', '211100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('211200', '211200', '铁岭市', '2', '210000', '211200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('211300', '211300', '朝阳市', '2', '210000', '211300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('211400', '211400', '葫芦岛市', '2', '210000', '211400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220000', '220000', '吉林省', '1', '0', '220000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220100', '220100', '长春市', '2', '220000', '220100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220200', '220200', '吉林市', '2', '220000', '220200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220300', '220300', '四平市', '2', '220000', '220300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220400', '220400', '辽源市', '2', '220000', '220400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220500', '220500', '通化市', '2', '220000', '220500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220600', '220600', '白山市', '2', '220000', '220600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220700', '220700', '松原市', '2', '220000', '220700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('220800', '220800', '白城市', '2', '220000', '220800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('222400', '222400', '延边朝鲜族自治州', '2', '220000', '222400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230000', '230000', '黑龙江省', '1', '0', '230000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230100', '230100', '哈尔滨市', '2', '230000', '230100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230200', '230200', '齐齐哈尔市', '2', '230000', '230200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230300', '230300', '鸡西市', '2', '230000', '230300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230400', '230400', '鹤岗市', '2', '230000', '230400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230500', '230500', '双鸭山市', '2', '230000', '230500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230600', '230600', '大庆市', '2', '230000', '230600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230700', '230700', '伊春市', '2', '230000', '230700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230800', '230800', '佳木斯市', '2', '230000', '230800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('230900', '230900', '七台河市', '2', '230000', '230900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('231000', '231000', '牡丹江市', '2', '230000', '231000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('231100', '231100', '黑河市', '2', '230000', '231100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('231200', '231200', '绥化市', '2', '230000', '231200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('232700', '232700', '大兴安岭地区', '2', '230000', '232700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('310000', '310000', '上海市', '1', '0', '310000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('310100', '310100', '市辖区', '2', '310000', '310100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320000', '320000', '江苏省', '1', '0', '320000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320100', '320100', '南京市', '2', '320000', '320100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320200', '320200', '无锡市', '2', '320000', '320200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320300', '320300', '徐州市', '2', '320000', '320300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320400', '320400', '常州市', '2', '320000', '320400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320500', '320500', '苏州市', '2', '320000', '320500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320600', '320600', '南通市', '2', '320000', '320600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320700', '320700', '连云港市', '2', '320000', '320700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320800', '320800', '淮安市', '2', '320000', '320800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('320900', '320900', '盐城市', '2', '320000', '320900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('321000', '321000', '扬州市', '2', '320000', '321000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('321100', '321100', '镇江市', '2', '320000', '321100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('321200', '321200', '泰州市', '2', '320000', '321200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('321300', '321300', '宿迁市', '2', '320000', '321300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330000', '330000', '浙江省', '1', '0', '330000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330100', '330100', '杭州市', '2', '330000', '330100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330200', '330200', '宁波市', '2', '330000', '330200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330300', '330300', '温州市', '2', '330000', '330300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330400', '330400', '嘉兴市', '2', '330000', '330400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330500', '330500', '湖州市', '2', '330000', '330500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330600', '330600', '绍兴市', '2', '330000', '330600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330700', '330700', '金华市', '2', '330000', '330700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330800', '330800', '衢州市', '2', '330000', '330800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('330900', '330900', '舟山市', '2', '330000', '330900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('331000', '331000', '台州市', '2', '330000', '331000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('331100', '331100', '丽水市', '2', '330000', '331100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340000', '340000', '安徽省', '1', '0', '340000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340100', '340100', '合肥市', '2', '340000', '340100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340200', '340200', '芜湖市', '2', '340000', '340200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340300', '340300', '蚌埠市', '2', '340000', '340300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340400', '340400', '淮南市', '2', '340000', '340400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340500', '340500', '马鞍山市', '2', '340000', '340500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340600', '340600', '淮北市', '2', '340000', '340600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340700', '340700', '铜陵市', '2', '340000', '340700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('340800', '340800', '安庆市', '2', '340000', '340800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341000', '341000', '黄山市', '2', '340000', '341000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341100', '341100', '滁州市', '2', '340000', '341100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341200', '341200', '阜阳市', '2', '340000', '341200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341300', '341300', '宿州市', '2', '340000', '341300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341500', '341500', '六安市', '2', '340000', '341500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341600', '341600', '亳州市', '2', '340000', '341600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341700', '341700', '池州市', '2', '340000', '341700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('341800', '341800', '宣城市', '2', '340000', '341800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350000', '350000', '福建省', '1', '0', '350000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350100', '350100', '福州市', '2', '350000', '350100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350200', '350200', '厦门市', '2', '350000', '350200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350300', '350300', '莆田市', '2', '350000', '350300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350400', '350400', '三明市', '2', '350000', '350400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350500', '350500', '泉州市', '2', '350000', '350500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350600', '350600', '漳州市', '2', '350000', '350600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350700', '350700', '南平市', '2', '350000', '350700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350800', '350800', '龙岩市', '2', '350000', '350800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('350900', '350900', '宁德市', '2', '350000', '350900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360000', '360000', '江西省', '1', '0', '360000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360100', '360100', '南昌市', '2', '360000', '360100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360200', '360200', '景德镇市', '2', '360000', '360200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360300', '360300', '萍乡市', '2', '360000', '360300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360400', '360400', '九江市', '2', '360000', '360400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360500', '360500', '新余市', '2', '360000', '360500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360600', '360600', '鹰潭市', '2', '360000', '360600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360700', '360700', '赣州市', '2', '360000', '360700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360800', '360800', '吉安市', '2', '360000', '360800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('360900', '360900', '宜春市', '2', '360000', '360900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('361000', '361000', '抚州市', '2', '360000', '361000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('361100', '361100', '上饶市', '2', '360000', '361100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370000', '370000', '山东省', '1', '0', '370000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370100', '370100', '济南市', '2', '370000', '370100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370200', '370200', '青岛市', '2', '370000', '370200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370300', '370300', '淄博市', '2', '370000', '370300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370400', '370400', '枣庄市', '2', '370000', '370400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370500', '370500', '东营市', '2', '370000', '370500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370600', '370600', '烟台市', '2', '370000', '370600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370700', '370700', '潍坊市', '2', '370000', '370700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370800', '370800', '济宁市', '2', '370000', '370800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('370900', '370900', '泰安市', '2', '370000', '370900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371000', '371000', '威海市', '2', '370000', '371000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371100', '371100', '日照市', '2', '370000', '371100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371200', '371200', '莱芜市', '2', '370000', '371200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371300', '371300', '临沂市', '2', '370000', '371300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371400', '371400', '德州市', '2', '370000', '371400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371500', '371500', '聊城市', '2', '370000', '371500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371600', '371600', '滨州市', '2', '370000', '371600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('371700', '371700', '菏泽市', '2', '370000', '371700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410000', '410000', '河南省', '1', '0', '410000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410100', '410100', '郑州市', '2', '410000', '410100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410200', '410200', '开封市', '2', '410000', '410200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410300', '410300', '洛阳市', '2', '410000', '410300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410400', '410400', '平顶山市', '2', '410000', '410400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410500', '410500', '安阳市', '2', '410000', '410500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410600', '410600', '鹤壁市', '2', '410000', '410600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410700', '410700', '新乡市', '2', '410000', '410700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410800', '410800', '焦作市', '2', '410000', '410800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('410900', '410900', '濮阳市', '2', '410000', '410900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411000', '411000', '许昌市', '2', '410000', '411000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411100', '411100', '漯河市', '2', '410000', '411100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411200', '411200', '三门峡市', '2', '410000', '411200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411300', '411300', '南阳市', '2', '410000', '411300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411400', '411400', '商丘市', '2', '410000', '411400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411500', '411500', '信阳市', '2', '410000', '411500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411600', '411600', '周口市', '2', '410000', '411600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('411700', '411700', '驻马店市', '2', '410000', '411700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('419000', '419000', '省直辖县级行政区划', '2', '410000', '419000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420000', '420000', '湖北省', '1', '0', '420000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420100', '420100', '武汉市', '2', '420000', '420100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420200', '420200', '黄石市', '2', '420000', '420200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420300', '420300', '十堰市', '2', '420000', '420300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420500', '420500', '宜昌市', '2', '420000', '420500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420600', '420600', '襄阳市', '2', '420000', '420600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420700', '420700', '鄂州市', '2', '420000', '420700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420800', '420800', '荆门市', '2', '420000', '420800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('420900', '420900', '孝感市', '2', '420000', '420900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('421000', '421000', '荆州市', '2', '420000', '421000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('421100', '421100', '黄冈市', '2', '420000', '421100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('421200', '421200', '咸宁市', '2', '420000', '421200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('421300', '421300', '随州市', '2', '420000', '421300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('422800', '422800', '恩施土家族苗族自治州', '2', '420000', '422800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('429000', '429000', '省直辖县级行政区划', '2', '420000', '429000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430000', '430000', '湖南省', '1', '0', '430000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430100', '430100', '长沙市', '2', '430000', '430100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430200', '430200', '株洲市', '2', '430000', '430200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430300', '430300', '湘潭市', '2', '430000', '430300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430400', '430400', '衡阳市', '2', '430000', '430400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430500', '430500', '邵阳市', '2', '430000', '430500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430600', '430600', '岳阳市', '2', '430000', '430600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430700', '430700', '常德市', '2', '430000', '430700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430800', '430800', '张家界市', '2', '430000', '430800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('430900', '430900', '益阳市', '2', '430000', '430900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('431000', '431000', '郴州市', '2', '430000', '431000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('431100', '431100', '永州市', '2', '430000', '431100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('431200', '431200', '怀化市', '2', '430000', '431200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('431300', '431300', '娄底市', '2', '430000', '431300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('433100', '433100', '湘西土家族苗族自治州', '2', '430000', '433100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440000', '440000', '广东省', '1', '0', '440000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440100', '440100', '广州市', '2', '440000', '440100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440200', '440200', '韶关市', '2', '440000', '440200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440300', '440300', '深圳市', '2', '440000', '440300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440400', '440400', '珠海市', '2', '440000', '440400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440500', '440500', '汕头市', '2', '440000', '440500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440600', '440600', '佛山市', '2', '440000', '440600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440700', '440700', '江门市', '2', '440000', '440700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440800', '440800', '湛江市', '2', '440000', '440800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('440900', '440900', '茂名市', '2', '440000', '440900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441200', '441200', '肇庆市', '2', '440000', '441200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441300', '441300', '惠州市', '2', '440000', '441300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441400', '441400', '梅州市', '2', '440000', '441400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441500', '441500', '汕尾市', '2', '440000', '441500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441600', '441600', '河源市', '2', '440000', '441600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441700', '441700', '阳江市', '2', '440000', '441700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441800', '441800', '清远市', '2', '440000', '441800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('441900', '441900', '东莞市', '2', '440000', '441900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('442000', '442000', '中山市', '2', '440000', '442000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('445100', '445100', '潮州市', '2', '440000', '445100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('445200', '445200', '揭阳市', '2', '440000', '445200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('445300', '445300', '云浮市', '2', '440000', '445300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450000', '450000', '广西壮族自治区', '1', '0', '450000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450100', '450100', '南宁市', '2', '450000', '450100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450200', '450200', '柳州市', '2', '450000', '450200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450300', '450300', '桂林市', '2', '450000', '450300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450400', '450400', '梧州市', '2', '450000', '450400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450500', '450500', '北海市', '2', '450000', '450500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450600', '450600', '防城港市', '2', '450000', '450600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450700', '450700', '钦州市', '2', '450000', '450700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450800', '450800', '贵港市', '2', '450000', '450800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('450900', '450900', '玉林市', '2', '450000', '450900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('451000', '451000', '百色市', '2', '450000', '451000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('451100', '451100', '贺州市', '2', '450000', '451100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('451200', '451200', '河池市', '2', '450000', '451200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('451300', '451300', '来宾市', '2', '450000', '451300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('451400', '451400', '崇左市', '2', '450000', '451400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('460000', '460000', '海南省', '1', '0', '460000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('460100', '460100', '海口市', '2', '460000', '460100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('460200', '460200', '三亚市', '2', '460000', '460200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('460300', '460300', '三沙市', '2', '460000', '460300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('460400', '460400', '儋州市', '2', '460000', '460400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('469000', '469000', '省直辖县级行政区划', '2', '460000', '469000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('500000', '500000', '重庆市', '1', '0', '500000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('500100', '500100', '市辖区', '2', '500000', '500100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('500200', '500200', '县', '2', '500000', '500200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510000', '510000', '四川省', '1', '0', '510000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510100', '510100', '成都市', '2', '510000', '510100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510300', '510300', '自贡市', '2', '510000', '510300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510400', '510400', '攀枝花市', '2', '510000', '510400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510500', '510500', '泸州市', '2', '510000', '510500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510600', '510600', '德阳市', '2', '510000', '510600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510700', '510700', '绵阳市', '2', '510000', '510700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510800', '510800', '广元市', '2', '510000', '510800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('510900', '510900', '遂宁市', '2', '510000', '510900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511000', '511000', '内江市', '2', '510000', '511000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511100', '511100', '乐山市', '2', '510000', '511100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511300', '511300', '南充市', '2', '510000', '511300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511400', '511400', '眉山市', '2', '510000', '511400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511500', '511500', '宜宾市', '2', '510000', '511500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511600', '511600', '广安市', '2', '510000', '511600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511700', '511700', '达州市', '2', '510000', '511700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511800', '511800', '雅安市', '2', '510000', '511800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('511900', '511900', '巴中市', '2', '510000', '511900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('512000', '512000', '资阳市', '2', '510000', '512000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('513200', '513200', '阿坝藏族羌族自治州', '2', '510000', '513200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('513300', '513300', '甘孜藏族自治州', '2', '510000', '513300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('513400', '513400', '凉山彝族自治州', '2', '510000', '513400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520000', '520000', '贵州省', '1', '0', '520000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520100', '520100', '贵阳市', '2', '520000', '520100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520200', '520200', '六盘水市', '2', '520000', '520200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520300', '520300', '遵义市', '2', '520000', '520300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520400', '520400', '安顺市', '2', '520000', '520400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520500', '520500', '毕节市', '2', '520000', '520500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('520600', '520600', '铜仁市', '2', '520000', '520600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('522300', '522300', '黔西南布依族苗族自治州', '2', '520000', '522300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('522600', '522600', '黔东南苗族侗族自治州', '2', '520000', '522600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('522700', '522700', '黔南布依族苗族自治州', '2', '520000', '522700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530000', '530000', '云南省', '1', '0', '530000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530100', '530100', '昆明市', '2', '530000', '530100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530300', '530300', '曲靖市', '2', '530000', '530300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530400', '530400', '玉溪市', '2', '530000', '530400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530500', '530500', '保山市', '2', '530000', '530500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530600', '530600', '昭通市', '2', '530000', '530600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530700', '530700', '丽江市', '2', '530000', '530700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530800', '530800', '普洱市', '2', '530000', '530800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('530900', '530900', '临沧市', '2', '530000', '530900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('532300', '532300', '楚雄彝族自治州', '2', '530000', '532300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('532500', '532500', '红河哈尼族彝族自治州', '2', '530000', '532500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('532600', '532600', '文山壮族苗族自治州', '2', '530000', '532600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('532800', '532800', '西双版纳傣族自治州', '2', '530000', '532800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('532900', '532900', '大理白族自治州', '2', '530000', '532900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('533100', '533100', '德宏傣族景颇族自治州', '2', '530000', '533100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('533300', '533300', '怒江傈僳族自治州', '2', '530000', '533300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('533400', '533400', '迪庆藏族自治州', '2', '530000', '533400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540000', '540000', '西藏自治区', '1', '0', '540000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540100', '540100', '拉萨市', '2', '540000', '540100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540200', '540200', '日喀则市', '2', '540000', '540200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540300', '540300', '昌都市', '2', '540000', '540300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540400', '540400', '林芝市', '2', '540000', '540400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('540500', '540500', '山南市', '2', '540000', '540500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('542400', '542400', '那曲地区', '2', '540000', '542400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('542500', '542500', '阿里地区', '2', '540000', '542500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610000', '610000', '陕西省', '1', '0', '610000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610100', '610100', '西安市', '2', '610000', '610100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610200', '610200', '铜川市', '2', '610000', '610200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610300', '610300', '宝鸡市', '2', '610000', '610300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610400', '610400', '咸阳市', '2', '610000', '610400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610500', '610500', '渭南市', '2', '610000', '610500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610600', '610600', '延安市', '2', '610000', '610600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610700', '610700', '汉中市', '2', '610000', '610700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610800', '610800', '榆林市', '2', '610000', '610800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('610900', '610900', '安康市', '2', '610000', '610900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('611000', '611000', '商洛市', '2', '610000', '611000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620000', '620000', '甘肃省', '1', '0', '620000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620100', '620100', '兰州市', '2', '620000', '620100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620200', '620200', '嘉峪关市', '2', '620000', '620200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620300', '620300', '金昌市', '2', '620000', '620300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620400', '620400', '白银市', '2', '620000', '620400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620500', '620500', '天水市', '2', '620000', '620500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620600', '620600', '武威市', '2', '620000', '620600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620700', '620700', '张掖市', '2', '620000', '620700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620800', '620800', '平凉市', '2', '620000', '620800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('620900', '620900', '酒泉市', '2', '620000', '620900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('621000', '621000', '庆阳市', '2', '620000', '621000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('621100', '621100', '定西市', '2', '620000', '621100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('621200', '621200', '陇南市', '2', '620000', '621200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('622900', '622900', '临夏回族自治州', '2', '620000', '622900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('623000', '623000', '甘南藏族自治州', '2', '620000', '623000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('630000', '630000', '青海省', '1', '0', '630000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('630100', '630100', '西宁市', '2', '630000', '630100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('630200', '630200', '海东市', '2', '630000', '630200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632200', '632200', '海北藏族自治州', '2', '630000', '632200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632300', '632300', '黄南藏族自治州', '2', '630000', '632300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632500', '632500', '海南藏族自治州', '2', '630000', '632500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632600', '632600', '果洛藏族自治州', '2', '630000', '632600', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632700', '632700', '玉树藏族自治州', '2', '630000', '632700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('632800', '632800', '海西蒙古族藏族自治州', '2', '630000', '632800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640000', '640000', '宁夏回族自治区', '1', '0', '640000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640100', '640100', '银川市', '2', '640000', '640100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640200', '640200', '石嘴山市', '2', '640000', '640200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640300', '640300', '吴忠市', '2', '640000', '640300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640400', '640400', '固原市', '2', '640000', '640400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('640500', '640500', '中卫市', '2', '640000', '640500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('650000', '650000', '新疆维吾尔自治区', '1', '0', '650000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('650100', '650100', '乌鲁木齐市', '2', '650000', '650100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('650200', '650200', '克拉玛依市', '2', '650000', '650200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('650400', '650400', '吐鲁番市', '2', '650000', '650400', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('650500', '650500', '哈密市', '2', '650000', '650500', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('652300', '652300', '昌吉回族自治州', '2', '650000', '652300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('652700', '652700', '博尔塔拉蒙古自治州', '2', '650000', '652700', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('652800', '652800', '巴音郭楞蒙古自治州', '2', '650000', '652800', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('652900', '652900', '阿克苏地区', '2', '650000', '652900', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('653000', '653000', '克孜勒苏柯尔克孜自治州', '2', '650000', '653000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('653100', '653100', '喀什地区', '2', '650000', '653100', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('653200', '653200', '和田地区', '2', '650000', '653200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('654000', '654000', '伊犁哈萨克自治州', '2', '650000', '654000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('654200', '654200', '塔城地区', '2', '650000', '654200', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('654300', '654300', '阿勒泰地区', '2', '650000', '654300', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('659000', '659000', '自治区直辖县级行政区划', '2', '650000', '659000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('710000', '710000', '台湾省', '1', '0', '710000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('810000', '810000', '香港特别行政区', '1', '0', '810000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_region` VALUES ('820000', '820000', '澳门特别行政区', '1', '0', '820000', '1', '1', '2021-06-07 13:03:41', '127.0.0.1', '1', '2021-06-07 13:03:41', '127.0.0.1', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_resource`
-- ----------------------------
DROP TABLE IF EXISTS `wo_resource`;
CREATE TABLE `wo_resource` (
  `id` bigint(20) unsigned NOT NULL COMMENT '资源id',
  `resource_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源编码',
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源名称',
  `resource_path` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'icon声明，全局定义icon库，这里设置的是库中的icon英文名',
  `resource_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源类型：菜单、接口服务、数据服务、静态资源、其他',
  `request_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源操作方法：get、post、put、delete等',
  `target` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '打开方式：_self,_blank,_parent,_top',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属应用',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级资源',
  `display_order` int(10) DEFAULT NULL COMMENT '上级资源路径下的排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_app_res_code` (`resource_code`,`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源定义表';

-- ----------------------------
-- Records of wo_resource
-- ----------------------------
INSERT INTO `wo_resource` VALUES ('100', 'home', '首页', '/', 'home', 'menu', 'GET', '_self', '1506113043159498757', '0', '1', '1', '平台门户', '0', '0', '2021-05-08 13:44:35', '1', '127.0.0.1', '2021-05-27 21:38:20', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506101733801771011', 'sys', '系统管理', '/admin/sys', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '2', '1', '系统管理', '0', '127.0.0.1', '2021-05-19 01:10:35', '1', '192.168.1.23', '2021-07-13 19:20:21', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506107866432061443', 'app', '应用管理', '/admin/sys/app', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '1', '1', 'SaaS平台上的独立应用', '0', '127.0.0.1', '2021-05-19 01:34:57', '0', '127.0.0.1', '2021-05-27 12:54:54', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506122443605590022', 'res', '资源管理', '/admin/sys/res', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '2', '1', '菜单、操作、服务、静态资源', '0', '127.0.0.1', '2021-05-19 02:32:53', '0', '127.0.0.1', '2021-05-27 12:56:26', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506125438066016267', 'role', '角色管理', '/admin/sys/role', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '3', '1', '业务场景细分的资源集', '0', '127.0.0.1', '2021-05-19 02:44:47', '0', '127.0.0.1', '2021-05-27 13:17:12', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506126590341988354', 'app-del', '删除', '/admin/app/delete', null, 'button', 'DELETE', '_self', '1506005013902311434', '1506107866432061443', '0', '1', '逻辑删除一个应用', '0', '127.0.0.1', '2021-05-19 02:49:22', '0', '127.0.0.1', '2021-05-19 02:49:22', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506126989799112705', 'app-add', '新建', '/admin/app/add', null, 'button', 'POST', '_self', '1506005013902311434', '1506107866432061443', '0', '1', '新增一个应用', '0', '127.0.0.1', '2021-05-19 02:50:57', '0', '127.0.0.1', '2021-05-19 02:50:57', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1509177915053096962', 'com', '租户管理', '/admin/sys/com', 'list', 'admin_menu', 'GET', 'self', '1506005013902311434', '1506101733801771011', '4', '1', '租户一般只独立的法人主体。', '0', '127.0.0.1', '2021-05-27 12:54:14', '0', '127.0.0.1', '2021-05-27 13:18:44', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509179615117754368', 'admin', '系统', '/admin', 'list', 'menu', 'GET', '_blank', '1506005013902311434', '0', '2', '1', '系统管理的入口菜单', '0', '127.0.0.1', '2021-05-27 13:00:59', '1', '192.168.1.23', '2021-07-06 20:53:41', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509184818118311946', 'arch', '体系管理', '/admin/sys/arch', 'list', 'admin_menu', 'GET', 'self', '1506005013902311434', '1506101733801771011', '5', '1', '定义体系结构的设置', '0', '127.0.0.1', '2021-05-27 13:21:40', '1', '192.168.1.23', '2021-07-13 19:18:22', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509211664167911432', 'org', '组织管理', '/admin/sys/org', 'list', 'admin_menu', 'GET', 'self', '1506005013902311434', '1506101733801771011', '6', '1', '组织管理', '0', '127.0.0.1', '2021-05-27 15:08:21', '1', '192.168.1.23', '2021-07-13 19:18:38', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509212249457868808', 'user', '用户管理', '/admin/sys/user', 'list', 'admin_menu', 'GET', 'self', '1506005013902311434', '1506101733801771011', '7', '1', '注册用户管理', '0', '127.0.0.1', '2021-05-27 15:10:40', '1', '192.168.1.23', '2021-07-13 19:19:26', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511494161530535947', 'user', '用户配置', '/user', 'list', 'webServ', 'GET', 'self', '1511494438434291716', '0', '7', '1', '前端用户设置', '1', '127.0.0.1', '2021-06-02 22:18:10', '1', '127.0.0.1', '2021-06-02 22:21:39', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511495802770079746', 'userset', '个人设置', '/user/conf', 'list', 'button', 'POST', 'self', '1511494438434291716', '1511494161530535947', '1', '1', null, '1', '127.0.0.1', '2021-06-02 22:24:42', '1', '127.0.0.1', '2021-06-02 22:27:53', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511496878801993736', 'upavatar', '头像上传', '/user/uploadAvatar', 'list', 'button', 'POST', 'self', '1511494438434291716', '1511494161530535947', '2', '1', null, '1', '127.0.0.1', '2021-06-02 22:28:58', '1', '127.0.0.1', '2021-06-02 22:28:58', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511736612090462209', 'curAccount', '账户信息', '/user/curAccount', 'list', 'button', 'GET', 'self', '1511494438434291716', '1511494161530535947', '3', '1', '个人基本信息查询', '1', '127.0.0.1', '2021-06-03 14:21:35', '1', '127.0.0.1', '2021-06-03 14:21:35', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1518555863791091716', 'adminMenu', '获取管理菜单', '/admin/sys/user/adminMenu', 'list', 'webServ', 'GET', 'self', '1506005013902311434', '1509212249457868808', '1', '1', '游客访问时，跳转到登陆', '1', '127.0.0.1', '2021-06-22 09:58:51', '1', '127.0.0.1', '2021-06-22 09:58:51', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_role`
-- ----------------------------
DROP TABLE IF EXISTS `wo_role`;
CREATE TABLE `wo_role` (
  `id` bigint(20) unsigned NOT NULL COMMENT '角色id',
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色名称',
  `role_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `role_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色类型：系统角色(管理员、游客、普通用户、商户、政府)、业务角色(岗位、职务、功能)',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级体系结构',
  `display_order` int(10) DEFAULT NULL COMMENT '在上级公司下的排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效，1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色定义表';

-- ----------------------------
-- Records of wo_role
-- ----------------------------
INSERT INTO `wo_role` VALUES ('1', 'guest', '游客', '未登录用户', 'sys_role', '0', '1', '1', '0', '0', '2021-05-08 11:20:34', '0', '127.0.0.1', '2021-05-20 15:30:23', 'normal', '1');
INSERT INTO `wo_role` VALUES ('2', 'user', '普通会员', '普通注册用户', 'sys_role', '0', '2', '1', '0', '0', '2021-05-08 13:47:13', '0', '127.0.0.1', '2021-05-20 15:31:02', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1506681836080381960', 'vip', 'VIP会员', '付费用户', 'sys_role', '0', '3', '1', '0', '127.0.0.1', '2021-05-20 15:35:43', '0', '127.0.0.1', '2021-05-20 15:35:43', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506688462267006976', 'hvip', '高级VIP', '年费VIP', 'sys_role', '1506681836080381960', '12', '1', '0', '127.0.0.1', '2021-05-20 16:02:02', '0', '127.0.0.1', '2021-05-25 20:47:58', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506688973191954441', 'dvip', '钻石VIP', '3年以上年费', 'sys_role', '1506681836080381960', '23', '1', '0', '127.0.0.1', '2021-05-20 16:04:04', '0', '127.0.0.1', '2021-05-25 20:48:09', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506689309763878914', 'avip', '终身VIP', '永久包年', 'sys_role', '1506681836080381960', '4', '1', '0', '127.0.0.1', '2021-05-20 16:05:24', '0', '127.0.0.1', '2021-05-25 20:48:17', 'normal', null);
INSERT INTO `wo_role` VALUES ('1508572257052180489', 'gvip', '普通VIP', '普通付费', 'sys_role', '1506681836080381960', '1', '1', '0', '127.0.0.1', '2021-05-25 20:47:34', '0', '127.0.0.1', '2021-05-25 20:47:34', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1509213016482824194', 'admin', '管理员', '系统管理员', 'sys_role', '0', '4', '1', '0', '127.0.0.1', '2021-05-27 15:13:43', '0', '127.0.0.1', '2021-05-27 15:13:43', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1525946478916976648', 'tadmin', '租户管理员', '租户（公司）的管理员，是平台超级管理员的子集。', 'sys_role', '0', '5', '1', '1', '192.168.1.23', '2021-07-12 19:26:31', '1', '192.168.1.23', '2021-07-12 19:26:31', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1526213891793272839', 'badmin', '子管理员', '子管理员集成超级管理员功能权限，借助组织管理，可以阻止二级管理员访问租户数据。', 'sys_role', '1509213016482824194', '1', '1', '1', '192.168.1.23', '2021-07-13 13:09:07', '1', '192.168.1.23', '2021-07-13 13:15:45', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_role_org`
-- ----------------------------
DROP TABLE IF EXISTS `wo_role_org`;
CREATE TABLE `wo_role_org` (
  `id` bigint(20) unsigned NOT NULL COMMENT '角色用户id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id',
  `org_id` bigint(20) unsigned DEFAULT NULL COMMENT '组织机构id',
  `arch_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属体系结构id',
  `com_id` bigint(20) unsigned DEFAULT NULL COMMENT '所属公司id',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效，1有效，0无效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色用户关联表';

-- ----------------------------
-- Records of wo_role_org
-- ----------------------------
INSERT INTO `wo_role_org` VALUES ('0', '1', '0', '0', '0', '1', '0', '0', '2021-05-08 12:10:41', '0', '0', '2021-05-08 12:10:47', 'normal', '1');
INSERT INTO `wo_role_org` VALUES ('1508560523641929728', '2', '200', '0', '0', '1', '0', '127.0.0.1', '2021-05-25 20:00:57', '0', '127.0.0.1', '2021-05-25 20:00:57', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1508573862422036488', '1508572257052180489', '201', '0', '0', '1', '0', '127.0.0.1', '2021-05-25 20:53:57', '0', '127.0.0.1', '2021-05-25 20:53:57', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1509214125624573957', '1509213016482824194', '1', '0', '0', '1', '0', '127.0.0.1', '2021-05-27 15:18:07', '0', '127.0.0.1', '2021-05-27 15:18:07', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1525968743872249860', '1525946478916976648', '1525968690709446657', '0', '0', '1', '1', '192.168.1.23', '2021-07-12 20:55:00', '1', '192.168.1.23', '2021-07-12 20:55:00', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1526215647499567105', '1509213016482824194', '1526214941484957699', '0', '0', '1', '1', '192.168.1.23', '2021-07-13 13:16:06', '1', '192.168.1.23', '2021-07-13 13:16:06', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1526215647499567106', '1526213891793272839', '1526214941484957699', '0', '0', '1', '1', '192.168.1.23', '2021-07-13 13:16:06', '1', '192.168.1.23', '2021-07-13 13:16:06', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_subject_association`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_association`;
CREATE TABLE `wo_subject_association` (
  `id` bigint(20) unsigned NOT NULL COMMENT '角色主体关系id',
  `subject_type_id` bigint(20) unsigned DEFAULT NULL COMMENT '主体类型id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id，此角色应该为顶级公司顶级组织下的全局角色(系统保留角色)',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色主体类型关系表';

-- ----------------------------
-- Records of wo_subject_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_authentication`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_authentication`;
CREATE TABLE `wo_subject_authentication` (
  `id` bigint(20) unsigned NOT NULL COMMENT '认证主体id',
  `subject_type_id` bigint(20) unsigned DEFAULT NULL COMMENT '主体类型id',
  `subject_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主体社会信用统一编码或身份证号',
  `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主体名称，可能是法人名称、自然人姓名，前者不能重复，后者可以重复',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '认证账号',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_subject_code` (`subject_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主体身份认证表';

-- ----------------------------
-- Records of wo_subject_authentication
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_define`;
CREATE TABLE `wo_subject_define` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主体类型id',
  `subject_type_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主体类型编码，可以继承国标的组织机构类型，并根据实际业务衍生',
  `subject_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主体类型名称',
  `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主体类型描述',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效、1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_subject_type_code` (`subject_type_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证主体定义表';

-- ----------------------------
-- Records of wo_subject_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_model_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_model_define`;
CREATE TABLE `wo_subject_model_define` (
  `id` bigint(20) unsigned DEFAULT NULL COMMENT '主体模板id',
  `subject_type_id` bigint(20) unsigned NOT NULL COMMENT '主体类型id',
  `subject_model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板名称',
  `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板描述',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否有效：0无效、1有效',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`subject_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证主体模板定义表';

-- ----------------------------
-- Records of wo_subject_model_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_user`;
CREATE TABLE `wo_user` (
  `id` bigint(20) unsigned NOT NULL COMMENT '用户id',
  `login_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台用户登录名，又叫账号，可以修改，全局唯一（比如抖音认证后可以全局唯一）',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号显示名称，又叫昵称',
  `passwd` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录密码，可以为空，设置后可以以账号密码登录',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号状态：locked已锁定、cancelled已注销、normal正常',
  `domain_id` bigint(20) unsigned DEFAULT NULL COMMENT '账号归属的二方域id（二方域指本平台上设置的域）',
  `id_card` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号或法人身份证',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户实名认证后的真实名称，可以是自然人、法人',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `telephone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '固定电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通信地址',
  `qq` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'QQ号',
  `email` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像在文件服务器存储的真实相对路径',
  `remark` varchar(230) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `display_order` bigint(20) DEFAULT NULL COMMENT '排序',
  `is_real` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '是否实名认证：默认0，实名认证后：1',
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国家',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区',
  `invite_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邀请码',
  `recommend_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '推荐码：推荐人的邀请码',
  `register_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册IP，首次注册IP',
  `create_by` bigint(20) unsigned DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_by` bigint(20) unsigned DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '信息更新操作者的IP',
  `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除状态字典值：normal正常，deleted删除',
  `versions` int(10) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_login` (`login_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局用户表';

-- ----------------------------
-- Records of wo_user
-- ----------------------------
INSERT INTO `wo_user` VALUES ('1', 'admin', '龙神', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'normal', '0', null, 'admin', 'man', null, null, '0531-268888888', '长清区崮云湖街道', null, '306991142@qq.com', '/202107/04154905X74n7Duh.png', '第一个轻量级互联网SaaS系统研发者', '0', '0', 'China', '370000', '370100', null, '0', null, '192.168.1.23', '0', '2021-05-08 12:15:05', '192.168.1.23', '1', '2021-07-14 16:21:32', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('100', 'wldos', 'wldos', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '100', null, 'wldos', 'man', null, null, null, null, null, null, null, null, '1', '0', null, null, null, null, '100', null, '192.168.1.23', '0', '2021-05-08 12:15:09', '192.168.1.23', '0', '2021-05-08 12:15:25', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502723278108278788', 'support@zhiletu.com', null, '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', null, '0', null, null, null, null, null, null, null, null, 'support@zhiletu.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502723278108278788', '2021-05-09 17:25:49', '127.0.0.1', '1502723278108278788', '2021-05-09 17:25:49', '127.0.0.1', 'deleted', '1');
INSERT INTO `wo_user` VALUES ('1502726588102000651', '306991142@qq.com', null, '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', null, '0', null, null, null, null, null, null, null, null, '306991142@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502726588102000651', '2021-05-09 17:38:58', '127.0.0.1', '1502726588102000651', '2021-05-09 17:38:58', '127.0.0.1', 'deleted', '1');
INSERT INTO `wo_user` VALUES ('1502739709344530437', '583716365@qq.com', '小五义', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '583716365@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502739709344530437', '2021-05-09 18:31:06', '127.0.0.1', '0', '2021-05-26 22:45:15', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502742261263941642', '2345@qq.com', '2345', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '2345@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502742261263941642', '2021-05-09 18:41:15', '127.0.0.1', '1502742261263941642', '2021-05-09 18:41:15', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502770377176825865', '586@163.com', '586', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '586@163.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502770377176825865', '2021-05-09 20:32:58', '127.0.0.1', '0', '2021-05-26 22:46:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502773236924596235', '567@163.com', '菲菲', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '567@163.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502773236924596235', '2021-05-09 20:44:20', '127.0.0.1', '0', '2021-05-26 22:46:43', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502777260587532299', '1234@126.com', '没有五', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '1234@126.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502777260587532299', '2021-05-09 21:00:19', '127.0.0.1', '0', '2021-05-26 22:47:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502777813036089350', '23@123.com', '驿站之心', '47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=', 'normal', '0', null, null, null, null, null, null, null, null, '23@123.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502777813036089350', '2021-05-09 21:02:31', '127.0.0.1', '0', '2021-05-26 22:47:51', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502783343481765897', 'nihao1@123.com', '你好夏天', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '0', null, null, null, null, null, null, null, null, 'nihao1@123.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502783343481765897', '2021-05-09 21:24:29', '127.0.0.1', '1502783343481765897', '2021-05-09 21:24:29', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502803624724185094', 'bbc@qq.com', 'nihao', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '0', null, null, null, null, null, null, null, null, 'bbc@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502803624724185094', '2021-05-09 22:45:05', '127.0.0.1', '1502803624724185094', '2021-05-09 22:45:05', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1521171388547383299', 'jjunxiao@163.com', '223', '3zBoxWKPRqP11DsQnBBX8CfLPNhJ3yz1F5ep1YVFMDA=', 'normal', null, null, null, null, null, null, null, null, null, 'jjunxiao@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1521171388547383299', '2021-06-29 15:12:01', '192.168.1.23', '1521171388547383299', '2021-06-29 15:12:01', '192.168.1.23', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_usermeta`
-- ----------------------------
DROP TABLE IF EXISTS `wo_usermeta`;
CREATE TABLE `wo_usermeta` (
  `umeta_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `meta_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '元数据key',
  `meta_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '元数据值',
  PRIMARY KEY (`umeta_id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `meta_key` (`meta_key`(191)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_usermeta
-- ----------------------------
INSERT INTO `wo_usermeta` VALUES ('1', '1', 'nickname', 'wldos');
INSERT INTO `wo_usermeta` VALUES ('2', '1', 'first_name', '');
INSERT INTO `wo_usermeta` VALUES ('3', '1', 'last_name', '');
INSERT INTO `wo_usermeta` VALUES ('4', '1', 'description', '');
INSERT INTO `wo_usermeta` VALUES ('5', '1', 'rich_editing', 'true');
INSERT INTO `wo_usermeta` VALUES ('6', '1', 'syntax_highlighting', 'true');
INSERT INTO `wo_usermeta` VALUES ('7', '1', 'comment_shortcuts', 'false');
INSERT INTO `wo_usermeta` VALUES ('8', '1', 'admin_color', 'fresh');
INSERT INTO `wo_usermeta` VALUES ('9', '1', 'use_ssl', '0');
INSERT INTO `wo_usermeta` VALUES ('10', '1', 'show_admin_bar_front', 'true');
INSERT INTO `wo_usermeta` VALUES ('11', '1', 'locale', '');
INSERT INTO `wo_usermeta` VALUES ('12', '1', 'wo_capabilities', 'a:1:{s:13:\"administrator\";b:1;}');
INSERT INTO `wo_usermeta` VALUES ('13', '1', 'wo_user_level', '10');
INSERT INTO `wo_usermeta` VALUES ('14', '1', 'dismissed_wp_pointers', '');
INSERT INTO `wo_usermeta` VALUES ('15', '1', 'show_welcome_panel', '1');
INSERT INTO `wo_usermeta` VALUES ('16', '1', 'session_tokens', 'a:1:{s:64:\"d343a8293c6f86acec38ef26c5a1d264ba1064192a7893c6f6a34469385f9325\";a:4:{s:10:\"expiration\";i:1613560626;s:2:\"ip\";s:12:\"192.168.1.23\";s:2:\"ua\";s:115:\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36\";s:5:\"login\";i:1612351026;}}');
INSERT INTO `wo_usermeta` VALUES ('17', '1', 'wo_dashboard_quick_press_last_post_id', '4');
INSERT INTO `wo_usermeta` VALUES ('18', '1', 'community-events-location', 'a:1:{s:2:\"ip\";s:11:\"192.168.1.0\";}');
INSERT INTO `wo_usermeta` VALUES ('19', '1', 'nav_menu_recently_edited', '2');
INSERT INTO `wo_usermeta` VALUES ('20', '1', 'managenav-menuscolumnshidden', 'a:5:{i:0;s:11:\"link-target\";i:1;s:11:\"css-classes\";i:2;s:3:\"xfn\";i:3;s:11:\"description\";i:4;s:15:\"title-attribute\";}');
INSERT INTO `wo_usermeta` VALUES ('21', '1', 'metaboxhidden_nav-menus', 'a:2:{i:0;s:12:\"add-post_tag\";i:1;s:15:\"add-post_format\";}');
