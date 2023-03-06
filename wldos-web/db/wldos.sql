/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.24
Source Server Version : 50729
Source Host           : 192.168.1.24:3306
Source Database       : wldos

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2023-02-17 21:45:41
*/

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
-- Records of k_comments
-- ----------------------------
INSERT INTO `k_comments` VALUES ('81146214038028294', '1545223091294748672', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-10-27 16:43:55', '192.168.1.23', '1', '2021-10-27 16:43:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('81148030079385607', '1545223091294748672', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-10-27 16:51:08', '192.168.1.23', '1', '2021-10-27 16:51:08', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('81148068658593794', '1545223091294748672', '龙神', null, null, null, '干啥呢', '0', '1', null, null, '81148030079385607', '1', '2021-10-27 16:51:17', '192.168.1.23', '1', '2021-10-27 16:51:17', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('81171206641467395', '1544414732358434826', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-10-27 18:23:14', '192.168.1.23', '1', '2021-10-27 18:23:14', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('81171244939657216', '1544414732358434826', '龙神', null, null, null, '你好', '0', '1', null, null, '81171206641467395', '1', '2021-10-27 18:23:23', '192.168.1.23', '1', '2021-10-27 18:23:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('83309870280523787', '1544401257229893635', '龙神', null, null, null, '讲得很好，这是很透彻的解析！', '0', '1', null, null, '0', '1', '2021-11-02 16:01:31', '192.168.1.23', '1', '2021-11-02 16:01:31', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('245425828037050371', '71095902778605572', '龙神', null, null, null, '测试评论。', '0', '1', null, null, '0', '1', '2023-01-24 00:32:27', '192.168.1.23', '1', '2023-01-24 00:32:27', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531030094781005833', '0', null, null, null, null, '<p>你好，感受呢</p>', '0', null, null, null, '0', '0', '2021-07-26 20:07:00', '192.168.1.23', '0', '2021-07-26 20:07:00', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531051319343366147', '0', null, null, null, null, '<p>士大夫</p>', '0', null, null, null, '0', '0', '2021-07-26 21:31:20', '192.168.1.23', '0', '2021-07-26 21:31:20', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531054120156971019', '1525843814879248388', '龙神', null, null, null, '<p>1234</p>', '0', '1', null, null, '0', '0', '2021-07-26 21:42:28', '192.168.1.23', '0', '2021-07-26 21:42:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531060601556221955', '1525843814879248388', '龙神', null, null, null, '123', '0', '1', null, null, '0', '1', '2021-07-26 22:08:13', '192.168.1.23', '1', '2021-07-26 22:08:13', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531096812706512898', '1525843814879248388', '龙神', null, null, null, '2323', '0', '1', null, null, '0', '1', '2021-07-27 00:32:07', '192.168.1.23', '1', '2021-07-27 00:32:07', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531278924860538883', '1531257784574328835', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-07-27 12:35:45', '192.168.1.23', '1', '2021-07-27 12:35:45', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531299488656441345', '1531257784574328835', '龙神', null, null, null, '1212', '0', '1', null, null, '0', '1', '2021-07-27 13:57:28', '192.168.1.23', '1', '2021-07-27 13:57:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531299832702615552', '1527684704723714055', '龙神', null, null, null, '你好，世界！', '0', '1', null, null, '0', '1', '2021-07-27 13:58:50', '192.168.1.23', '1', '2021-07-27 13:58:50', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531303294911823873', '1527684704723714055', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-07-27 14:12:36', '192.168.1.23', '1', '2021-07-27 14:12:36', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531354834938806273', '1527684704723714055', '龙神', null, null, null, '你好', '0', '1', null, null, '1531299832702615552', '1', '2021-07-27 17:37:24', '192.168.1.23', '1', '2021-07-27 17:37:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531355755743723527', '1527684704723714055', '龙神', null, null, null, '感受呢', '0', '1', null, null, '1531303294911823873', '1', '2021-07-27 17:41:03', '192.168.1.23', '1', '2021-07-27 17:41:03', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531365018297876488', '1527684704723714055', '龙神', null, null, null, '盖楼', '0', '1', null, null, '1531355755743723527', '1', '2021-07-27 18:17:52', '192.168.1.23', '1', '2021-07-27 18:17:52', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531367064103206917', '1527684704723714055', '龙神', null, null, null, '继续盖楼', '0', '1', null, null, '1531365018297876488', '1', '2021-07-27 18:26:00', '192.168.1.23', '1', '2021-07-27 18:26:00', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531390519439704069', '1527684704723714055', '龙神', null, null, null, '这是3楼。', '0', '1', null, null, '1531365018297876488', '1', '2021-07-27 19:59:12', '192.168.1.23', '1', '2021-07-27 19:59:12', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531391924288274433', '1527684704723714055', '龙神', null, null, null, '这是2单元4楼。', '0', '1', null, null, '1531390519439704069', '1', '2021-07-27 20:04:47', '192.168.1.23', '1', '2021-07-27 20:04:47', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531821589951856644', '1522680911237922817', 'wldos', null, null, null, '这是一个牛叉的应用。', '0', '1', null, null, '0', '100', '2021-07-29 00:32:07', '192.168.1.23', '100', '2021-07-29 00:32:07', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531821688304091136', '1522680911237922817', 'wldos', null, null, null, '确实很牛叉', '0', '1', null, null, '1531821589951856644', '100', '2021-07-29 00:32:30', '192.168.1.23', '100', '2021-07-29 00:32:30', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1531821888007487494', '1531257784574328835', 'wldos', null, null, null, '你好，能吃吗', '0', '1', null, null, '1531278924860538883', '100', '2021-07-29 00:33:18', '192.168.1.23', '100', '2021-07-29 00:33:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1541173455181496326', '1529498101626880001', '龙神', null, null, null, '哈喽，你在干啥呢？', '0', '1', null, null, '0', '1', '2021-08-23 19:53:05', '192.168.1.23', '1', '2021-08-23 19:53:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1541173506364588032', '1529498101626880001', '龙神', null, null, null, '自娱自乐呢。', '0', '1', null, null, '1541173455181496326', '1', '2021-08-23 19:53:18', '192.168.1.23', '1', '2021-08-23 19:53:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1541173566615765001', '1529498101626880001', '龙神', null, null, null, '自说自话吗', '0', '1', null, null, '1541173506364588032', '1', '2021-08-23 19:53:32', '192.168.1.23', '1', '2021-08-23 19:53:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1541173859747282952', '1529498101626880001', '龙神', null, null, null, '没内容也能放出来，没作发布状态过滤吧', '0', '1', null, null, '0', '1', '2021-08-23 19:54:42', '192.168.1.23', '1', '2021-08-23 19:54:42', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1541176621235093514', '1525843966499143682', '龙神', null, null, null, '牛逼', '0', '1', null, null, '0', '1', '2021-08-23 20:05:40', '192.168.1.23', '1', '2021-08-23 20:05:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_comments` VALUES ('1549902403000254465', '33', '龙神', null, null, null, '你好', '0', '1', null, null, '0', '1', '2021-09-16 21:58:49', '192.168.1.23', '1', '2021-09-16 21:58:49', '192.168.1.23', 'normal', '1');

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
-- Records of k_model_pub_type_ext
-- ----------------------------
INSERT INTO `k_model_pub_type_ext` VALUES ('1', 'sub_title', '副标题', '子描述', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('4', 'first_name', '谱主姓氏', '', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('5', 'last_name', '谱主名字', '', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('6', 'start_year', '起始年份', '', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('10', 'privacy_level', '隐私级别', '隐私级别：查看码可见token、打赏可见reward、公开public', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('11', 'prov', '省分', '', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('12', 'city', '地市', '', null, null, 'annual');
INSERT INTO `k_model_pub_type_ext` VALUES ('13', 'county', '区县', '', null, null, 'annual');

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
-- Records of k_pubmeta
-- ----------------------------
INSERT INTO `k_pubmeta` VALUES ('67460601367674887', '1521588799557779463', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('70297172261388299', '1521202459829780484', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('71090908490809350', '71090908285288456', 'subTitle', '副标题随便写');
INSERT INTO `k_pubmeta` VALUES ('71090908490809351', '71090908285288456', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('71090908490809352', '71090908285288456', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('71090908490809353', '71090908285288456', 'mainPic2', '/202109/29224613p5zzr2iT.png');
INSERT INTO `k_pubmeta` VALUES ('71090908490809354', '71090908285288456', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('71090908490809355', '71090908285288456', 'cover', '/202109/292246000FlN8EhQ.png');
INSERT INTO `k_pubmeta` VALUES ('71090908490809356', '71090908285288456', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('71090908490809357', '71090908285288456', 'mainPic3', '/202109/29224621yvp9Styu.png');
INSERT INTO `k_pubmeta` VALUES ('71090908490809358', '71090908285288456', 'mainPic1', '/202109/29224607rb6ytSDr.png');
INSERT INTO `k_pubmeta` VALUES ('71090908490809359', '71090908285288456', 'contact', 'a');
INSERT INTO `k_pubmeta` VALUES ('71090908490809360', '71090908285288456', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('71090908490809361', '71090908285288456', 'mainPic4', '/202109/29224627I0BIXIAG.png');
INSERT INTO `k_pubmeta` VALUES ('71095902837325835', '71095902778605572', 'views', '27');
INSERT INTO `k_pubmeta` VALUES ('71098015298535427', '71098015290146827', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('71098018825945097', '71098018817556482', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('71098019899686921', '71098019769663489', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('71098022152028167', '71098022001033220', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('71098022823116804', '71098022814728198', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('80366983217594370', '80366982974324744', 'subTitle', '一代始皇，天下归一');
INSERT INTO `k_pubmeta` VALUES ('80366983217594371', '80366982974324744', 'mainPic3', '/202110/25130720qp8tNiQu.jpg');
INSERT INTO `k_pubmeta` VALUES ('80366983217594372', '80366982974324744', 'contact', 'a');
INSERT INTO `k_pubmeta` VALUES ('80366983217594373', '80366982974324744', 'mainPic4', '/202110/251307273aNlFcDa.jpg');
INSERT INTO `k_pubmeta` VALUES ('80366983217594374', '80366982974324744', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('80366983217594375', '80366982974324744', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('80366983217594376', '80366982974324744', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('80366983217594377', '80366982974324744', 'cover', '/202110/25130629i6WlfTwV.jpg');
INSERT INTO `k_pubmeta` VALUES ('80366983217594378', '80366982974324744', 'mainPic2', '/202110/25130711HZSjhRO3.jpg');
INSERT INTO `k_pubmeta` VALUES ('80366983217594379', '80366982974324744', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('80366983217594380', '80366982974324744', 'city', '140200');
INSERT INTO `k_pubmeta` VALUES ('80366983217594381', '80366982974324744', 'mainPic1', '/202110/25130659KG4VdUoA.jpg');
INSERT INTO `k_pubmeta` VALUES ('80367041732329472', '80366982974324744', 'views', '73');
INSERT INTO `k_pubmeta` VALUES ('80444794729054215', '37', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('81101003261919232', '81101003052204043', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('81101003266113542', '81101003052204043', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('81101003266113543', '81101003052204043', 'cover', '/202110/27134357k2rwtVXA.jpg');
INSERT INTO `k_pubmeta` VALUES ('81101003266113544', '81101003052204043', 'contact', '155');
INSERT INTO `k_pubmeta` VALUES ('81101003266113545', '81101003052204043', 'subTitle', '弹出小类选择框，点击选定的小类打开信息编辑页；移动端需要点击分类弹出小类选择框。');
INSERT INTO `k_pubmeta` VALUES ('81101003266113546', '81101003052204043', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('81101003266113547', '81101003052204043', 'city', '370200');
INSERT INTO `k_pubmeta` VALUES ('81101003266113548', '81101003052204043', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('81101003266113549', '81101003052204043', 'mainPic1', '/202110/27134409OwZqEzrc.jpg');
INSERT INTO `k_pubmeta` VALUES ('81101029346295811', '81101003052204043', 'views', '104');
INSERT INTO `k_pubmeta` VALUES ('81129178398375940', '38', 'views', '10');
INSERT INTO `k_pubmeta` VALUES ('81145962375593986', '71090908285288456', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('81146063550595073', '41', 'views', '9');
INSERT INTO `k_pubmeta` VALUES ('81146084782161920', '40', 'views', '7');
INSERT INTO `k_pubmeta` VALUES ('81146092826836996', '35', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('81163801224462347', '32', 'views', '6');
INSERT INTO `k_pubmeta` VALUES ('81163810867167236', '36', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('81758432651821061', '1521631894928277513', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('81759135378096139', '81759135164186628', 'views', '22');
INSERT INTO `k_pubmeta` VALUES ('81759221889810437', '81759221873033221', 'attachMetadata', '{\"width\":1500,\"height\":1500,\"path\":\"/202110/29091946mtf7GN9G.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":150,\"path\":\"/202110/29091946mtf7GN9G-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":300,\"path\":\"/202110/29091946mtf7GN9G-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":1024,\"path\":\"/202110/29091946mtf7GN9G-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('81759221889810438', '81759221873033221', 'attachPath', '/202110/29091946mtf7GN9G.png');
INSERT INTO `k_pubmeta` VALUES ('83281719466835974', '1522009400529305604', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('83293843341623296', '83293842246909960', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('83294775290806275', '83294775278223366', 'attachMetadata', '{\"width\":1206,\"height\":737,\"path\":\"/202111/02150130L0dJbfGn.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":92,\"path\":\"/202111/02150130L0dJbfGn-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":183,\"path\":\"/202111/02150130L0dJbfGn-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":626,\"path\":\"/202111/02150130L0dJbfGn-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('83294775295000586', '83294775278223366', 'attachPath', '/202111/02150130L0dJbfGn.png');
INSERT INTO `k_pubmeta` VALUES ('83320963325411339', '83320962197143557', 'subTitle', '跟随一线顶级架构师，讲述从0到1实现开发内容创作平台的最佳实践。');
INSERT INTO `k_pubmeta` VALUES ('83320963325411340', '83320962197143557', 'mainPic2', '/202111/02164408JOLxyjYh.jpg');
INSERT INTO `k_pubmeta` VALUES ('83320963325411341', '83320962197143557', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('83320963325411342', '83320962197143557', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('83320963325411343', '83320962197143557', 'cover', '/202111/02164347HziO70hS.jpg');
INSERT INTO `k_pubmeta` VALUES ('83320963325411344', '83320962197143557', 'ornPrice', '88.8');
INSERT INTO `k_pubmeta` VALUES ('83320963325411345', '83320962197143557', 'mainPic3', '/202111/02164518fkIzz7Ez.jpg');
INSERT INTO `k_pubmeta` VALUES ('83320963325411346', '83320962197143557', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('83320963325411347', '83320962197143557', 'mainPic4', '/202111/021645243ieC908Z.jpg');
INSERT INTO `k_pubmeta` VALUES ('83320963325411348', '83320962197143557', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('83320963325411349', '83320962197143557', 'mainPic1', '/202111/02164358zkG983cb.jpg');
INSERT INTO `k_pubmeta` VALUES ('83320963325411350', '83320962197143557', 'contact', '王先生');
INSERT INTO `k_pubmeta` VALUES ('83322023729348619', '83322023720960005', 'views', '30');
INSERT INTO `k_pubmeta` VALUES ('83323531061542913', '83323531053154305', 'attachMetadata', '{\"width\":1080,\"height\":1440,\"path\":\"/202111/02165547DRqRlzdc.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":113,\"height\":150,\"path\":\"/202111/02165547DRqRlzdc-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":225,\"height\":300,\"path\":\"/202111/02165547DRqRlzdc-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":768,\"height\":1024,\"path\":\"/202111/02165547DRqRlzdc-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('83323531065737222', '83323531053154305', 'attachPath', '/202111/02165547DRqRlzdc.jpg');
INSERT INTO `k_pubmeta` VALUES ('93807978881925126', '93807978626072585', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('93807978886119426', '93807978626072585', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('93807978890313737', '93807978626072585', 'mainPic2', '/202112/01151711DnHJ6Voj.png');
INSERT INTO `k_pubmeta` VALUES ('93807978890313738', '93807978626072585', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('93807978890313739', '93807978626072585', 'contact', '121');
INSERT INTO `k_pubmeta` VALUES ('93807978890313740', '93807978626072585', 'cover', '/202112/01151651PuN46CWJ.png');
INSERT INTO `k_pubmeta` VALUES ('93807978890313741', '93807978626072585', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('93807978890313742', '93807978626072585', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('93807978890313743', '93807978626072585', 'mainPic1', '/202112/011517008FmvzqJO.png');
INSERT INTO `k_pubmeta` VALUES ('93807978890313744', '93807978626072585', 'subTitle', '1212');
INSERT INTO `k_pubmeta` VALUES ('93808020200013825', '93808020195819520', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('94132269997539336', '1', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('94132465779261442', '25', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('94132527154511879', '29', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('94428624372547586', '94428624192192519', 'subTitle', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字');
INSERT INTO `k_pubmeta` VALUES ('94428624372547587', '94428624192192519', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('94428624372547588', '94428624192192519', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('94428624372547589', '94428624192192519', 'mainPic3', '/202112/03082238BEDiwSQY.png');
INSERT INTO `k_pubmeta` VALUES ('94428624372547590', '94428624192192519', 'mainPic2', '/202112/03082227oAu75trM.png');
INSERT INTO `k_pubmeta` VALUES ('94428624372547591', '94428624192192519', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('94428624372547592', '94428624192192519', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('94428624372547593', '94428624192192519', 'cover', '/202112/0308221009Ydmmho.png');
INSERT INTO `k_pubmeta` VALUES ('94428624372547594', '94428624192192519', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('94428624372547595', '94428624192192519', 'mainPic1', '/202112/03082219aEU9SAlh.png');
INSERT INTO `k_pubmeta` VALUES ('94428624372547596', '94428624192192519', 'mainPic4', '/202112/03082247G20hDk21.png');
INSERT INTO `k_pubmeta` VALUES ('94428624372547597', '94428624192192519', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('94428863368183816', '94428624192192519', 'views', '14');
INSERT INTO `k_pubmeta` VALUES ('94435069977608196', '93807978626072585', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('94454789309120521', '94454789267177482', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('94454789313314826', '94454789267177482', 'reward', '122');
INSERT INTO `k_pubmeta` VALUES ('94454789313314827', '94454789267177482', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('94454789313314828', '94454789267177482', 'mainPic1', '/202112/03100722Q8lphply.png');
INSERT INTO `k_pubmeta` VALUES ('94454789313314829', '94454789267177482', 'city', '150300');
INSERT INTO `k_pubmeta` VALUES ('94454789313314830', '94454789267177482', 'cover', '/202112/03100705TAlsaDX7.png');
INSERT INTO `k_pubmeta` VALUES ('94454789313314831', '94454789267177482', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('94454789313314832', '94454789267177482', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('94454789313314833', '94454789267177482', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('94454789313314834', '94454789267177482', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('94454852517281796', '94454852508893185', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('94455570494046218', '94454789267177482', 'views', '10');
INSERT INTO `k_pubmeta` VALUES ('94823522804350985', '94823522682716168', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('94823522804350986', '94823522682716168', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('94823522804350987', '94823522682716168', 'cover', '/202112/041020043IGjKqKj.png');
INSERT INTO `k_pubmeta` VALUES ('94823522804350988', '94823522682716168', 'mainPic1', '/202112/04103236w1w4dzzS.png');
INSERT INTO `k_pubmeta` VALUES ('94823522804350989', '94823522682716168', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('94823522804350990', '94823522682716168', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('94823522804350991', '94823522682716168', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('94823522804350992', '94823522682716168', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('94823522804350993', '94823522682716168', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('94847219216859143', '94847219032309763', 'ornPrice', '120');
INSERT INTO `k_pubmeta` VALUES ('94847219221053448', '94847219032309763', 'mainPic1', '/202112/041206457oZfpxsN.png');
INSERT INTO `k_pubmeta` VALUES ('94847219221053449', '94847219032309763', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('94847219221053450', '94847219032309763', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('94847219225247748', '94847219032309763', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('94847219225247749', '94847219032309763', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('94847219225247750', '94847219032309763', 'cover', '/202112/04120635BRG7m5Jt.png');
INSERT INTO `k_pubmeta` VALUES ('94847219225247751', '94847219032309763', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('94847219225247752', '94847219032309763', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('94847669458616328', '94847669290844168', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('94847669462810633', '94847669290844168', 'subTitle', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字');
INSERT INTO `k_pubmeta` VALUES ('94847669467004930', '94847669290844168', 'mainPic1', '/202112/13211558Fd64KeG3.jpg');
INSERT INTO `k_pubmeta` VALUES ('94847669467004931', '94847669290844168', 'province', '220000');
INSERT INTO `k_pubmeta` VALUES ('94847669467004932', '94847669290844168', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('94847669467004933', '94847669290844168', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('94847669467004934', '94847669290844168', 'city', '220600');
INSERT INTO `k_pubmeta` VALUES ('94847669467004935', '94847669290844168', 'cover', '/202112/13210027zTPYkXAw.png');
INSERT INTO `k_pubmeta` VALUES ('94847669467004936', '94847669290844168', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('94847732872298507', '94847669290844168', 'views', '28');
INSERT INTO `k_pubmeta` VALUES ('94971164393914369', '94847219032309763', 'views', '15');
INSERT INTO `k_pubmeta` VALUES ('94974166366339074', '94823522682716168', 'views', '9');
INSERT INTO `k_pubmeta` VALUES ('94993989133582337', '94993989058084875', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95010184142438401', '95010184129855499', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('95361649021206532', '95361647884550155', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('95361651927859204', '95361651915276297', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361652829634561', '95361652821245954', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361657288179717', '95361655048421378', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361657313345541', '95361657300762629', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361657317539843', '95361657304956938', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361657317539844', '95361657304956937', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95361657485312009', '95361657476923397', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('95562118251331585', '95562118209388549', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('95634448910565387', '95634448897982464', 'attachMetadata', '{\"width\":1997,\"height\":1550,\"path\":\"/202112/06161456g3YiQTKp.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":116,\"path\":\"/202112/06161456g3YiQTKp-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":233,\"path\":\"/202112/06161456g3YiQTKp-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":795,\"path\":\"/202112/06161456g3YiQTKp-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('95634448914759687', '95634448897982464', 'attachPath', '/202112/06161456g3YiQTKp.png');
INSERT INTO `k_pubmeta` VALUES ('95634681446973448', '95634681434390530', 'attachMetadata', '{\"width\":3840,\"height\":2114,\"path\":\"/202112/06161551XtgywxDj.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":83,\"path\":\"/202112/06161551XtgywxDj-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":165,\"path\":\"/202112/06161551XtgywxDj-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":564,\"path\":\"/202112/06161551XtgywxDj-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('95634681446973449', '95634681434390530', 'attachPath', '/202112/06161551XtgywxDj.png');
INSERT INTO `k_pubmeta` VALUES ('95998983718748171', '83320962197143557', 'views', '17');
INSERT INTO `k_pubmeta` VALUES ('96473821503733764', '96473821474373639', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('96473821503733765', '96473821474373639', 'mainPic1', '/202112/08235009RJTY9EIP.png');
INSERT INTO `k_pubmeta` VALUES ('96473821503733766', '96473821474373639', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('96473821503733767', '96473821474373639', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('96473821507928068', '96473821474373639', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('96473821507928069', '96473821474373639', 'cover', '/202112/08235003ykvABD03.png');
INSERT INTO `k_pubmeta` VALUES ('96473821507928070', '96473821474373639', 'city', '140400');
INSERT INTO `k_pubmeta` VALUES ('96473821507928071', '96473821474373639', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('96473821507928072', '96473821474373639', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('96473864730230789', '96473821474373639', 'views', '18');
INSERT INTO `k_pubmeta` VALUES ('98240382140858370', '98240381033562114', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98240390567215106', '98240390416220171', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98240396040781830', '98240395973672962', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98240401019420673', '98240401011032073', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98240404139982856', '98240403997376517', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98241028785094665', '98241028642488321', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98241033931505671', '98241033923117058', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98241038037729283', '98241038029340679', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98241041112154115', '98241041103765508', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98242517486190595', '98242517477801992', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98242524385820677', '98242524377432073', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('98243082849009669', '0', 'mainPic3', '/202112/13210043GvgBWKer.jpg');
INSERT INTO `k_pubmeta` VALUES ('98251839033229313', '94847219032309763', 'mainPic2', '/202112/13213520FsMLXz6r.png');
INSERT INTO `k_pubmeta` VALUES ('98259930067943428', '98259928771903499', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('98259930067943429', '98259928771903499', 'mainPic1', '/202112/13214702c0sR8bOU.png');
INSERT INTO `k_pubmeta` VALUES ('98259930067943430', '98259928771903499', 'ornPrice', '120');
INSERT INTO `k_pubmeta` VALUES ('98259930067943431', '98259928771903499', 'city', '520300');
INSERT INTO `k_pubmeta` VALUES ('98259930067943432', '98259928771903499', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('98259930067943433', '98259928771903499', 'termTypeId', '1520551727518629888');
INSERT INTO `k_pubmeta` VALUES ('98259930067943434', '98259928771903499', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('98259930067943435', '98259928771903499', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('98259930067943436', '98259928771903499', 'cover', '/202112/132146557sdVpsXG.png');
INSERT INTO `k_pubmeta` VALUES ('98259930067943437', '98259928771903499', 'province', '520000');
INSERT INTO `k_pubmeta` VALUES ('98259977446801408', '98259928771903499', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('98260088260313098', '98260087136239626', 'views', '26');
INSERT INTO `k_pubmeta` VALUES ('98260974055374850', '98259928771903499', 'mainPic2', '/202112/13221121bdSTBXT3.png');
INSERT INTO `k_pubmeta` VALUES ('98260974059569155', '98259928771903499', 'mainPic3', '/202112/132211288imCXfr3.png');
INSERT INTO `k_pubmeta` VALUES ('98260974059569156', '98259928771903499', 'mainPic4', '/202112/13221136296PuOQ8.jpg');
INSERT INTO `k_pubmeta` VALUES ('98536685789954054', '98536685760593923', 'attachMetadata', '{\"width\":1994,\"height\":1424,\"path\":\"/202112/14162724uSLO9LW5.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":107,\"path\":\"/202112/14162724uSLO9LW5-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":214,\"path\":\"/202112/14162724uSLO9LW5-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":731,\"path\":\"/202112/14162724uSLO9LW5-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('98536685789954055', '98536685760593923', 'attachPath', '/202112/14162724uSLO9LW5.png');
INSERT INTO `k_pubmeta` VALUES ('100046688531169287', '100046688501809161', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100077295990652928', '100077295961292809', 'views', '11');
INSERT INTO `k_pubmeta` VALUES ('100419908979572737', '83322023720960005', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100419908979572738', '83322023720960005', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100678105812353027', '1544356320987758602', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100678105816547332', '1544356320987758602', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100678577759633409', '1544401216171851784', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100678577759633410', '1544401216171851784', 'cover', '/202112/20141914LlWNQa0k.jpg');
INSERT INTO `k_pubmeta` VALUES ('100678864071213058', '1544401257229893635', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100678864075407369', '1544401257229893635', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100679111207993344', '1544401654996713472', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100679111212187655', '1544401654996713472', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100679240816181249', '1544414732358434826', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100679240820375556', '1544414732358434826', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100685022118985737', '100685022098014208', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100685088967802887', '100685088955219977', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100688161484226561', '1541803026658541572', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100688161488420872', '1541803026658541572', 'cover', '/202112/20145507EOlEEULS.jpg');
INSERT INTO `k_pubmeta` VALUES ('100716409215696905', '100716409064701963', 'ornPrice', '120');
INSERT INTO `k_pubmeta` VALUES ('100716409219891206', '100716409064701963', 'city', '360200');
INSERT INTO `k_pubmeta` VALUES ('100716409224085506', '100716409064701963', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('100716409224085507', '100716409064701963', 'cover', '/202112/20163502ahSswdge.jpg');
INSERT INTO `k_pubmeta` VALUES ('100716409224085508', '100716409064701963', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100716409224085509', '100716409064701963', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100716409224085510', '100716409064701963', 'mainPic1', '/202112/20163511EFXCjUf4.jpg');
INSERT INTO `k_pubmeta` VALUES ('100716409224085511', '100716409064701963', 'subTitle', '这是一个测试数据，输入50个字，继续输入。1');
INSERT INTO `k_pubmeta` VALUES ('100716409224085512', '100716409064701963', 'province', '360000');
INSERT INTO `k_pubmeta` VALUES ('100716481022181379', '100716409064701963', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('100717380041883655', '100717380033495049', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100717505564819464', '100717505548042250', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100771756731187208', '100717505548042250', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100771756731187209', '100717505548042250', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100784898831990787', '100784898597109765', 'subTitle', '这是一个测试数据，输入50个字，继续输入。');
INSERT INTO `k_pubmeta` VALUES ('100784898836185094', '100784898597109765', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100784898836185095', '100784898597109765', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('100784898836185096', '100784898597109765', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('100784898840379393', '100784898597109765', 'mainPic1', '/202112/20212044MnyiR1Iy.jpg');
INSERT INTO `k_pubmeta` VALUES ('100784898844573699', '100784898597109765', 'city', '150200');
INSERT INTO `k_pubmeta` VALUES ('100784898844573700', '100784898597109765', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100784898844573701', '100784898597109765', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('100784898844573702', '100784898597109765', 'cover', '/202112/20212036CqHqprtQ.jpg');
INSERT INTO `k_pubmeta` VALUES ('100785354358571018', '100785354299850760', 'views', '16');
INSERT INTO `k_pubmeta` VALUES ('100792307562496011', '100792307554107401', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100792492275449856', '100792492258672646', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100795744157089800', '100795744148701191', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100824163934715906', '100824163745972224', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100824163938910213', '100824163745972224', 'contact', '王老师');
INSERT INTO `k_pubmeta` VALUES ('100824163938910214', '100824163745972224', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('100824163938910215', '100824163745972224', 'subTitle', '在工作台直接新建作品，比单击信息发布更方便录入内容，沉浸式创作大屏');
INSERT INTO `k_pubmeta` VALUES ('100824163938910216', '100824163745972224', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('100824163938910217', '100824163745972224', 'mainPic1', '/202112/202356593zC8ms91.jpg');
INSERT INTO `k_pubmeta` VALUES ('100824163938910218', '100824163745972224', 'cover', '/202112/202356507aFrPlOJ.jpg');
INSERT INTO `k_pubmeta` VALUES ('100824163938910219', '100824163745972224', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100824163938910220', '100824163745972224', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('100826521200672774', '100826521129369604', 'cover', '/202112/21000322l5nC88dT.jpg');
INSERT INTO `k_pubmeta` VALUES ('100826521200672775', '100826521129369604', 'mainPic3', '/202112/210004028Jy8YQiO.jpg');
INSERT INTO `k_pubmeta` VALUES ('100826521200672776', '100826521129369604', 'mainPic1', '/202112/21000340Tuq44WtG.jpg');
INSERT INTO `k_pubmeta` VALUES ('100826521200672777', '100826521129369604', 'mainPic4', '/202112/21000600dCvzPnak.jpg');
INSERT INTO `k_pubmeta` VALUES ('100826521200672778', '100826521129369604', 'contact', '王老师');
INSERT INTO `k_pubmeta` VALUES ('100826521200672779', '100826521129369604', 'mainPic2', '/202112/210003494yByGP73.jpg');
INSERT INTO `k_pubmeta` VALUES ('100826521200672780', '100826521129369604', 'city', '130300');
INSERT INTO `k_pubmeta` VALUES ('100826521200672781', '100826521129369604', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('100826521200672782', '100826521129369604', 'subTitle', '在工作台直接新建作品，比单击信息发布更方便录入内容，沉浸式创作大屏');
INSERT INTO `k_pubmeta` VALUES ('100826521200672783', '100826521129369604', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('100826521200672784', '100826521129369604', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100826521200672785', '100826521129369604', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('100831394046590980', '100831393706852359', 'subTitle', '在工作台直接新建作品，比单击信息发布更方便录入内容，沉浸式创作大屏');
INSERT INTO `k_pubmeta` VALUES ('100831394050785290', '100831393706852359', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('100831394050785291', '100831393706852359', 'contact', '王老师');
INSERT INTO `k_pubmeta` VALUES ('100831394050785292', '100831393706852359', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100831394050785293', '100831393706852359', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('100831394050785294', '100831393706852359', 'cover', '/202112/21001601QjwvXrZl.jpg');
INSERT INTO `k_pubmeta` VALUES ('100831394050785295', '100831393706852359', 'city', '140200');
INSERT INTO `k_pubmeta` VALUES ('100831394050785296', '100831393706852359', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100831394050785297', '100831393706852359', 'mainPic1', '/202112/21001610FWO9ij9q.jpg');
INSERT INTO `k_pubmeta` VALUES ('100832820093173768', '100832820072202250', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100832825147310080', '100832825126338562', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100832836371267586', '100832836350296067', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100832839730905088', '100832839705739270', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100832842062938114', '100832842033577987', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('100833128944943112', '100833128928165888', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833145772490762', '100833145684410378', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833150176509956', '100833150159732736', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833157113888779', '100833157101305857', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833160041512961', '100833158049218560', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833160100233220', '100833160083456005', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833160158953481', '100833160087650311', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833290870243336', '100833289259630596', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100833296138289163', '100833296096346117', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100837501934682122', '100837501867573253', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('100837501934682123', '100837501867573253', 'mainPic1', '/202112/210049548a2GC7DV.jpg');
INSERT INTO `k_pubmeta` VALUES ('100837501934682124', '100837501867573253', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('100837501934682125', '100837501867573253', 'subTitle', '在工作台直接新建作品，比单击信息发布更方便录入内容，沉浸式创作大屏');
INSERT INTO `k_pubmeta` VALUES ('100837501934682126', '100837501867573253', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100837501934682127', '100837501867573253', 'cover', '/202112/21004946HqYAjWYM.jpg');
INSERT INTO `k_pubmeta` VALUES ('100837501934682128', '100837501867573253', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('100837501938876426', '100837501867573253', 'city', '150300');
INSERT INTO `k_pubmeta` VALUES ('100837501938876427', '100837501867573253', 'contact', '王老师');
INSERT INTO `k_pubmeta` VALUES ('100849044445380618', '100849044428603392', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('100849322817142790', '100849044428603392', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('100849322825531397', '100849044428603392', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('100849851500773378', '100831393706852359', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('100852240777658369', '100824163745972224', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('101013606289293313', '98260087136239626', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('101013606293487621', '98260087136239626', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('101016014578958339', '100717380033495049', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('101016014583152647', '100717380033495049', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('102062618710818822', '100784898597109765', 'views', '19');
INSERT INTO `k_pubmeta` VALUES ('102084367871557632', '102084367846391813', 'attachMetadata', '{\"width\":450,\"height\":166,\"path\":\"/202112/24112439wk5s5bFe.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":55,\"path\":\"/202112/24112439wk5s5bFe-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":111,\"path\":\"/202112/24112439wk5s5bFe-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":378,\"path\":\"/202112/24112439wk5s5bFe-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('102084367875751943', '102084367846391813', 'attachPath', '/202112/24112439wk5s5bFe.png');
INSERT INTO `k_pubmeta` VALUES ('108315677002743813', '108315676512010250', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('108315677002743814', '108315676512010250', 'mainPic1', '/2023/01/24000401r6v5K5va.jpg');
INSERT INTO `k_pubmeta` VALUES ('108315677002743815', '108315676512010250', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('108315677002743816', '108315676512010250', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('108315677002743817', '108315676512010250', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('108315677002743818', '108315676512010250', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('108315677002743819', '108315676512010250', 'ornPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('108315677002743820', '108315676512010250', 'cover', '/2023/01/24000353R6Kpfb9G.jpg');
INSERT INTO `k_pubmeta` VALUES ('108315677002743821', '108315676512010250', 'subTitle', '测试作品新增合集');
INSERT INTO `k_pubmeta` VALUES ('108321428832174085', '108321427372556297', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('108321428832174086', '108321427372556297', 'cover', '/202201/10162810dTyIOZ59.png');
INSERT INTO `k_pubmeta` VALUES ('108321428832174087', '108321427372556297', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('108321428832174088', '108321427372556297', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('108321428832174089', '108321427372556297', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('108321428832174090', '108321427372556297', 'mainPic1', '/202201/10162826NDuF1zSP.png');
INSERT INTO `k_pubmeta` VALUES ('108321428832174091', '108321427372556297', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('108321428832174092', '108321427372556297', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('108321428836368386', '108321427372556297', 'subTitle', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字');
INSERT INTO `k_pubmeta` VALUES ('108322899338379266', '108322899132858375', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('108324278438117377', '108324277305655298', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('108324579182297098', '108324277305655298', 'cover', '/2023/01/24000224gQ55IKkw.jpg');
INSERT INTO `k_pubmeta` VALUES ('108324579186491397', '108324277305655298', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('108325789641654281', '108325789582934025', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('108325789645848582', '108325789582934025', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('108325789645848583', '108325789582934025', 'subTitle', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字');
INSERT INTO `k_pubmeta` VALUES ('108325789645848584', '108325789582934025', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('108325789645848585', '108325789582934025', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('108325789645848586', '108325789582934025', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('108325789645848587', '108325789582934025', 'cover', '/202201/10164503sLfL87Ba.jpg');
INSERT INTO `k_pubmeta` VALUES ('108325789645848588', '108325789582934025', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('108325789645848589', '108325789582934025', 'mainPic1', '/202201/10164515E4EK1kZz.jpg');
INSERT INTO `k_pubmeta` VALUES ('108326074728497153', '108325789582934025', 'views', '53');
INSERT INTO `k_pubmeta` VALUES ('109053851878473734', '108321427372556297', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('111610378539679750', '1529498096614686730', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('112622016260653060', '112622016239681542', 'attachMetadata', '{\"width\":682,\"height\":562,\"path\":\"/202201/22131730EnjO34yN.png\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":124,\"path\":\"/202201/22131730EnjO34yN-150x150.png\",\"mimeType\":\"image/png\"},{\"type\":\"medium\",\"width\":300,\"height\":247,\"path\":\"/202201/22131730EnjO34yN-300x300.png\",\"mimeType\":\"image/png\"},{\"type\":\"large\",\"width\":1024,\"height\":844,\"path\":\"/202201/22131730EnjO34yN-1024x1024.png\",\"mimeType\":\"image/png\"}]}');
INSERT INTO `k_pubmeta` VALUES ('112622016264847363', '112622016239681542', 'attachPath', '/202201/22131730EnjO34yN.png');
INSERT INTO `k_pubmeta` VALUES ('112622107230912517', '108322899132858375', 'cover', '/202201/221317498mXbIuCF.png');
INSERT INTO `k_pubmeta` VALUES ('121734339315286020', '1521636090356350977', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('125294558972657664', '125294558309957640', 'ornPrice', '120');
INSERT INTO `k_pubmeta` VALUES ('125294558976851972', '125294558309957640', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('125294558976851973', '125294558309957640', 'mainPic2', '/202202/26123306BX09Z3XY.png');
INSERT INTO `k_pubmeta` VALUES ('125294558976851974', '125294558309957640', 'mainPic3', '/202202/26123324tnZQQSD4.png');
INSERT INTO `k_pubmeta` VALUES ('125294558976851975', '125294558309957640', 'mainPic1', '/202202/2612324844U3NCBZ.png');
INSERT INTO `k_pubmeta` VALUES ('125294558976851976', '125294558309957640', 'cover', '/202202/26123215aPbC152k.png');
INSERT INTO `k_pubmeta` VALUES ('125294558976851977', '125294558309957640', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('125294558976851978', '125294558309957640', 'subTitle', '测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性');
INSERT INTO `k_pubmeta` VALUES ('125294558976851979', '125294558309957640', 'contact', '李先生');
INSERT INTO `k_pubmeta` VALUES ('125294558976851980', '125294558309957640', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('125294558976851981', '125294558309957640', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('125295747109928966', '125294558309957640', 'views', '16');
INSERT INTO `k_pubmeta` VALUES ('144962470750633985', '1529498078247829511', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('145300883710328834', '1521588846055833611', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('145317068392808458', '1529498100704133120', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('145327062303555589', '1529498076502999050', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('150293642426892295', '150293641017606149', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150293642426892296', '150293641017606149', 'subTitle', '技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布');
INSERT INTO `k_pubmeta` VALUES ('150293642426892297', '150293641017606149', 'cover', '/202205/06121055eOSoFhLW.jpg');
INSERT INTO `k_pubmeta` VALUES ('150293642426892298', '150293641017606149', 'contact', '张');
INSERT INTO `k_pubmeta` VALUES ('150293642426892299', '150293641017606149', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150293642426892300', '150293641017606149', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150293642426892301', '150293641017606149', 'mainPic1', '/202205/06121102HFczsswR.jpg');
INSERT INTO `k_pubmeta` VALUES ('150293642426892302', '150293641017606149', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150293642426892303', '150293641017606149', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('150295020922650625', '150293641017606149', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('150295498393829380', '150295498133782530', 'contact', '张');
INSERT INTO `k_pubmeta` VALUES ('150295498393829381', '150295498133782530', 'subTitle', 'React基础-组件化开发的哲学React基础-组件化开发的哲学');
INSERT INTO `k_pubmeta` VALUES ('150295498393829382', '150295498133782530', 'cover', '/202205/06121801un7jisZj.jpg');
INSERT INTO `k_pubmeta` VALUES ('150295498393829383', '150295498133782530', 'mainPic1', '/202205/06121816OAbRj3ID.jpg');
INSERT INTO `k_pubmeta` VALUES ('150295498393829384', '150295498133782530', 'mainPic2', '/202205/06121822tVbUuQxv.jpg');
INSERT INTO `k_pubmeta` VALUES ('150295498393829385', '150295498133782530', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150295498393829386', '150295498133782530', 'ornPrice', '24');
INSERT INTO `k_pubmeta` VALUES ('150295498393829387', '150295498133782530', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150295498393829388', '150295498133782530', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150295498393829389', '150295498133782530', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150297119760760844', '150297118515052555', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150297119760760845', '150297118515052555', 'contact', '张');
INSERT INTO `k_pubmeta` VALUES ('150297119760760846', '150297118515052555', 'ornPrice', '56');
INSERT INTO `k_pubmeta` VALUES ('150297119760760847', '150297118515052555', 'mainPic1', '/202205/061224437WHuMyO3.jpg');
INSERT INTO `k_pubmeta` VALUES ('150297119760760848', '150297118515052555', 'mainPic4', '/202205/06122452fnaqf8V6.jpg');
INSERT INTO `k_pubmeta` VALUES ('150297119760760849', '150297118515052555', 'reward', '34');
INSERT INTO `k_pubmeta` VALUES ('150297119760760850', '150297118515052555', 'cover', '/202205/06122436nrhb1YT7.jpg');
INSERT INTO `k_pubmeta` VALUES ('150297119760760851', '150297118515052555', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150297119760760852', '150297118515052555', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('150297119760760853', '150297118515052555', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150297119760760854', '150297118515052555', 'subTitle', 'React基础-组合组件代替组件继承React基础-组合组件代替组件继承');
INSERT INTO `k_pubmeta` VALUES ('150297403455094787', '150297118515052555', 'views', '7');
INSERT INTO `k_pubmeta` VALUES ('150300214410526723', '150300213835907079', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150300214427303936', '150300213835907079', 'cover', '/202205/06123701NbqQMCxN.jpg');
INSERT INTO `k_pubmeta` VALUES ('150300214427303937', '150300213835907079', 'mainPic1', '/202205/06123707DAaVlWSP.jpg');
INSERT INTO `k_pubmeta` VALUES ('150300214427303938', '150300213835907079', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150300214427303939', '150300213835907079', 'contact', '张');
INSERT INTO `k_pubmeta` VALUES ('150300214427303940', '150300213835907079', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150300214427303941', '150300213835907079', 'subTitle', 'React基础-状态提升React基础-状态提升React基础-状态提升React基础-状态提升');
INSERT INTO `k_pubmeta` VALUES ('150300214427303942', '150300213835907079', 'ornPrice', '56');
INSERT INTO `k_pubmeta` VALUES ('150300214427303943', '150300213835907079', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150300613892816902', '150300613628575749', 'contact', '张');
INSERT INTO `k_pubmeta` VALUES ('150300613892816903', '150300613628575749', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150300613892816904', '150300613628575749', 'mainPic2', '/202205/06123837VxCbEDDz.png');
INSERT INTO `k_pubmeta` VALUES ('150300613892816905', '150300613628575749', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150300613892816906', '150300613628575749', 'cover', '/202205/06123822sB2PjZBI.png');
INSERT INTO `k_pubmeta` VALUES ('150300613892816907', '150300613628575749', 'subTitle', 'React基础-表单React基础-表单React基础-表单React基础-表单React基础-表单');
INSERT INTO `k_pubmeta` VALUES ('150300613892816908', '150300613628575749', 'ornPrice', '56');
INSERT INTO `k_pubmeta` VALUES ('150300613892816909', '150300613628575749', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150300613892816910', '150300613628575749', 'mainPic1', '/202205/06123828C2ltc4Eu.png');
INSERT INTO `k_pubmeta` VALUES ('150300613892816911', '150300613628575749', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150301236830846983', '150301236507885576', 'contact', '徐老师');
INSERT INTO `k_pubmeta` VALUES ('150301236830846984', '150301236507885576', 'cover', '/202205/06124058Bvesfnef.png');
INSERT INTO `k_pubmeta` VALUES ('150301236830846985', '150301236507885576', 'ornPrice', '120');
INSERT INTO `k_pubmeta` VALUES ('150301236830846986', '150301236507885576', 'subTitle', 'React基础-列表和keyReact基础-列表和keyReact基础-列表和key');
INSERT INTO `k_pubmeta` VALUES ('150301236830846987', '150301236507885576', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150301236830846988', '150301236507885576', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150301236830846989', '150301236507885576', 'mainPic1', '/202205/06124106xvt9bVW5.png');
INSERT INTO `k_pubmeta` VALUES ('150301236830846990', '150301236507885576', 'mainPic3', '/202205/06124114R4RDz2N7.png');
INSERT INTO `k_pubmeta` VALUES ('150301236830846991', '150301236507885576', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150301236830846992', '150301236507885576', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150301922343698434', '150301922083651586', 'contact', '徐老师');
INSERT INTO `k_pubmeta` VALUES ('150301922343698435', '150301922083651586', 'cover', '/202205/06124332fm5QTY3I.png');
INSERT INTO `k_pubmeta` VALUES ('150301922343698436', '150301922083651586', 'mainPic1', '/202205/06124340nz9SOHqM.png');
INSERT INTO `k_pubmeta` VALUES ('150301922343698437', '150301922083651586', 'subTitle', 'WLDOS云应用支撑平台简介WLDOS云应用支撑平台简介');
INSERT INTO `k_pubmeta` VALUES ('150301922343698438', '150301922083651586', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150301922343698439', '150301922083651586', 'ornPrice', '250');
INSERT INTO `k_pubmeta` VALUES ('150301922343698440', '150301922083651586', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150301922343698441', '150301922083651586', 'mainPic2', '/202205/061243579RoNwPJw.png');
INSERT INTO `k_pubmeta` VALUES ('150301922343698442', '150301922083651586', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150301922343698443', '150301922083651586', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150341156328882186', '150341155934617611', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('150341156328882187', '150341155934617611', 'contact', '徐老师');
INSERT INTO `k_pubmeta` VALUES ('150341156328882188', '150341155934617611', 'ornPrice', '750');
INSERT INTO `k_pubmeta` VALUES ('150341156328882189', '150341155934617611', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('150341156328882190', '150341155934617611', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('150341156328882191', '150341155934617611', 'cover', '/202205/06151943xAkB5Yo2.png');
INSERT INTO `k_pubmeta` VALUES ('150341156328882192', '150341155934617611', 'subTitle', '互联网时代，企业如何借助业务中台迎战市场？互联网时代，企业如何借助业务中台迎战市场？');
INSERT INTO `k_pubmeta` VALUES ('150341156328882193', '150341155934617611', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('150341156328882194', '150341155934617611', 'mainPic1', '/202205/06151951JTjIsxjL.png');
INSERT INTO `k_pubmeta` VALUES ('150341739441995782', '1521175523606839304', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('151375661776945159', '150341155934617611', 'views', '23');
INSERT INTO `k_pubmeta` VALUES ('151376227458531333', '150300613628575749', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('151380931693428741', '108315676512010250', 'views', '15');
INSERT INTO `k_pubmeta` VALUES ('151453718286090246', '150295498133782530', 'views', '7');
INSERT INTO `k_pubmeta` VALUES ('151800310583181318', '1521175556540514304', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('151805331987152907', '150301922083651586', 'views', '16');
INSERT INTO `k_pubmeta` VALUES ('157641603813130243', '157641603456614408', 'city', '110100');
INSERT INTO `k_pubmeta` VALUES ('157641603817324553', '157641603456614408', 'subTitle', '技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布');
INSERT INTO `k_pubmeta` VALUES ('157641603817324554', '157641603456614408', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('157641603817324555', '157641603456614408', 'province', '110000');
INSERT INTO `k_pubmeta` VALUES ('157641603817324556', '157641603456614408', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('157641603817324557', '157641603456614408', 'mainPic1', '/202205/26184913EpyKZnxC.png');
INSERT INTO `k_pubmeta` VALUES ('157641603817324558', '157641603456614408', 'ornPrice', '56');
INSERT INTO `k_pubmeta` VALUES ('157641603817324559', '157641603456614408', 'contact', '徐老师');
INSERT INTO `k_pubmeta` VALUES ('157641603821518855', '157641603456614408', 'cover', '/202205/26184817Qcdk9yNO.png');
INSERT INTO `k_pubmeta` VALUES ('157641630618927112', '157641603456614408', 'views', '13');
INSERT INTO `k_pubmeta` VALUES ('242130095515025413', '150300213835907079', 'views', '11');
INSERT INTO `k_pubmeta` VALUES ('245756388529848327', '1521455551368314886', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('247261089024884738', '247261088727089159', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('247261867697422340', '247261867479318531', 'city', '110100');
INSERT INTO `k_pubmeta` VALUES ('247261867701616640', '247261867479318531', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('247261867701616641', '247261867479318531', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('247261867701616642', '247261867479318531', 'province', '110000');
INSERT INTO `k_pubmeta` VALUES ('247261867701616643', '247261867479318531', 'subTitle', '测试可信者发布作品无需审核');
INSERT INTO `k_pubmeta` VALUES ('247261867701616644', '247261867479318531', 'ornPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('247261867701616645', '247261867479318531', 'mainPic1', '/2023/01/29020810IMIzfRO5.jpg');
INSERT INTO `k_pubmeta` VALUES ('247261867701616646', '247261867479318531', 'cover', '/2023/01/29020802bAK0QivY.jpg');
INSERT INTO `k_pubmeta` VALUES ('247261867701616647', '247261867479318531', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('247261947884126215', '247261867479318531', 'views', '26');
INSERT INTO `k_pubmeta` VALUES ('247471524307189760', '247471524206526466', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('247472124486926342', '247471524206526466', 'cover', '/2023/01/29222840UpxulnKY.jpg');
INSERT INTO `k_pubmeta` VALUES ('247587694721024006', '247471524206526466', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('247589103793258501', '247589103533211650', 'city', '130100');
INSERT INTO `k_pubmeta` VALUES ('247589103793258502', '247589103533211650', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('247589103793258503', '247589103533211650', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('247589103793258504', '247589103533211650', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('247589103793258505', '247589103533211650', 'mainPic1', '/2023/01/292348284OWggsrb.jpg');
INSERT INTO `k_pubmeta` VALUES ('247589103793258506', '247589103533211650', 'ornPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('247589103793258507', '247589103533211650', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('247589103797452803', '247589103533211650', 'subTitle', '测试发布信息');
INSERT INTO `k_pubmeta` VALUES ('247589103797452804', '247589103533211650', 'cover', '/2023/01/29234819UfLgcsOq.jpg');
INSERT INTO `k_pubmeta` VALUES ('247589133702840322', '247589103533211650', 'views', '13');
INSERT INTO `k_pubmeta` VALUES ('247615834415611907', '247615834260422660', 'city', '110100');
INSERT INTO `k_pubmeta` VALUES ('247615834415611908', '247615834260422660', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('247615834415611909', '247615834260422660', 'cover', '/2023/01/30013359JGQFj2J1.jpg');
INSERT INTO `k_pubmeta` VALUES ('247615834415611910', '247615834260422660', 'subTitle', '测试作品新增合集');
INSERT INTO `k_pubmeta` VALUES ('247615834415611911', '247615834260422660', 'province', '110000');
INSERT INTO `k_pubmeta` VALUES ('247615834415611912', '247615834260422660', 'mainPic1', '/2023/01/30013406YAc95xHF.jpg');
INSERT INTO `k_pubmeta` VALUES ('247615834415611913', '247615834260422660', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('247615834415611914', '247615834260422660', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('247615834419806210', '247615834260422660', 'ornPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('247622646120497155', '247615834260422660', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('247643764151730176', '247643764122370050', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('248045421847035909', '248045421826064392', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('248227360319127560', '248227360134578178', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('248227360319127561', '248227360134578178', 'subTitle', '测试文章别名');
INSERT INTO `k_pubmeta` VALUES ('248227360319127562', '248227360134578178', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('248227360319127563', '248227360134578178', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('248227360319127564', '248227360134578178', 'cover', '/2023/01/31180416XsHumokb.jpg');
INSERT INTO `k_pubmeta` VALUES ('248227360319127565', '248227360134578178', 'mainPic1', '/2023/01/31180423Js68AHKN.jpg');
INSERT INTO `k_pubmeta` VALUES ('248227360319127566', '248227360134578178', 'telephone', '15552852719');
INSERT INTO `k_pubmeta` VALUES ('248227360319127567', '248227360134578178', 'ornPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('248227360319127568', '248227360134578178', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('248249387511889920', '248227360134578178', 'views', '40');
INSERT INTO `k_pubmeta` VALUES ('249437170284806152', '249437170188337159', 'views', '12');
INSERT INTO `k_pubmeta` VALUES ('249439439617179658', '249439401633562627', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('249735731740327946', '249735731727745026', 'views', '20');
INSERT INTO `k_pubmeta` VALUES ('249736875829018630', '249736875816435719', 'attachMetadata', '{\"width\":1966,\"height\":2826,\"path\":\"/2023/02/04220259mYTP9WQ3.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":104,\"height\":150,\"path\":\"/2023/02/04220259mYTP9WQ3-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":209,\"height\":300,\"path\":\"/2023/02/04220259mYTP9WQ3-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":712,\"height\":1024,\"path\":\"/2023/02/04220259mYTP9WQ3-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('249736875833212929', '249736875816435719', 'attachPath', '/2023/02/04220259mYTP9WQ3.jpg');
INSERT INTO `k_pubmeta` VALUES ('249737542052265988', '249737542039683081', 'attachMetadata', '{\"width\":1228,\"height\":972,\"path\":\"/2023/02/04220539WcQsiwDw.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":119,\"path\":\"/2023/02/04220539WcQsiwDw-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":237,\"path\":\"/2023/02/04220539WcQsiwDw-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":811,\"path\":\"/2023/02/04220539WcQsiwDw-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('249737542056460299', '249737542039683081', 'attachPath', '/2023/02/04220539WcQsiwDw.jpg');
INSERT INTO `k_pubmeta` VALUES ('249738206924947457', '249734249511043078', 'views', '48');
INSERT INTO `k_pubmeta` VALUES ('249778098451169282', '249778098358894594', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('249778108630745090', '249778108618162185', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778109964533765', '249778109956145155', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('249778112275595265', '249778112263012362', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778113278033926', '249778113269645312', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778114456633353', '249778114439856138', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778115463266309', '249778115450683394', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778116637671430', '249778116625088523', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778117048713218', '249778117040324619', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778117912739849', '249778117904351239', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778118839681026', '249778118831292422', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778119682736130', '249778119670153227', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778121033302019', '249778121020719109', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778122031546370', '249778122023157765', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778122677469189', '249778122664886282', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778123688296455', '249778123646353417', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778125189857282', '249778125173080067', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778126263599105', '249778126251016202', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778127471558658', '249778127463170055', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778128335585283', '249778128327196674', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778129354801156', '249778129346412544', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778130449514503', '249778130441125894', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778131355484165', '249778131347095560', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778132265648134', '249778132257259520', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778133180006401', '249778133171617796', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778134136307720', '249778134123724800', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249778135050665995', '249778135042277383', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('249800696631312386', '150301236507885576', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('252542677585412096', '252542677308588037', 'views', '17');
INSERT INTO `k_pubmeta` VALUES ('252547675744550918', '252542677308588037', 'cover', '/2023/02/12161140mQbzeBAM.png');
INSERT INTO `k_pubmeta` VALUES ('252547675744550919', '252542677308588037', 'subTitle', '测试信息发布');
INSERT INTO `k_pubmeta` VALUES ('252547675744550920', '252542677308588037', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('252547675744550921', '252542677308588037', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('252547675744550922', '252542677308588037', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('252547675744550923', '252542677308588037', 'contact', '张老师');
INSERT INTO `k_pubmeta` VALUES ('252547675744550924', '252542677308588037', 'telephone', '15665730935');
INSERT INTO `k_pubmeta` VALUES ('252547675744550925', '252542677308588037', 'mainPic2', '/2023/02/12161158BCmPgyDI.jpeg');
INSERT INTO `k_pubmeta` VALUES ('252547675744550926', '252542677308588037', 'mainPic1', '/2023/02/12161150TaD4kDux.jpeg');
INSERT INTO `k_pubmeta` VALUES ('252924035658858503', '252924035583361031', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('253994822406946819', '98241028642488321', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('253994822415335430', '98241028642488321', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254140838229753860', '254140838116507657', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('254140838233948165', '254140838116507657', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254140838233948166', '254140838116507657', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254140838233948167', '254140838116507657', 'cover', '/2023/02/17014229cWVkaYy9.jpg');
INSERT INTO `k_pubmeta` VALUES ('254140838233948168', '254140838116507657', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254140838233948169', '254140838116507657', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('254140838233948170', '254140838116507657', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254140838233948171', '254140838116507657', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254140838233948172', '254140838116507657', 'mainPic1', '/2023/02/170142439iqiMLqy.jpg');
INSERT INTO `k_pubmeta` VALUES ('254141403596767238', '254141403579990027', 'attachMetadata', '{\"width\":1080,\"height\":1920,\"path\":\"/2023/02/17014501F5kvgzNi.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":84,\"height\":150,\"path\":\"/2023/02/17014501F5kvgzNi-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":169,\"height\":300,\"path\":\"/2023/02/17014501F5kvgzNi-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":576,\"height\":1024,\"path\":\"/2023/02/17014501F5kvgzNi-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254141403600961536', '254141403579990027', 'attachPath', '/2023/02/17014501F5kvgzNi.jpg');
INSERT INTO `k_pubmeta` VALUES ('254141567715688458', '254140838116507657', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('254180658524110858', '254180658352144392', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('254180658524110859', '254180658352144392', 'cover', '/2023/02/17041011r2XfQtlh.jpg');
INSERT INTO `k_pubmeta` VALUES ('254180658524110860', '254180658352144392', 'mainPic1', '/2023/02/17041019EL6CGxyB.jpg');
INSERT INTO `k_pubmeta` VALUES ('254180658524110861', '254180658352144392', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254180658524110862', '254180658352144392', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254180658524110863', '254180658352144392', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254180658528305158', '254180658352144392', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('254180658528305159', '254180658352144392', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254180658528305160', '254180658352144392', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254182050357755908', '254182050299035648', 'city', '210300');
INSERT INTO `k_pubmeta` VALUES ('254182050357755909', '254182050299035648', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254182050357755910', '254182050299035648', 'mainPic1', '/2023/02/170426297OEhw10A.jpg');
INSERT INTO `k_pubmeta` VALUES ('254182050357755911', '254182050299035648', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254182050357755912', '254182050299035648', 'cover', '/2023/02/17042615YxfGxur4.jpg');
INSERT INTO `k_pubmeta` VALUES ('254182050357755913', '254182050299035648', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254182050357755914', '254182050299035648', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254182050357755915', '254182050299035648', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254182050357755916', '254182050299035648', 'province', '210000');
INSERT INTO `k_pubmeta` VALUES ('254182115805675531', '254182115797286920', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17042647UD6iZ8MD.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17042647UD6iZ8MD-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17042647UD6iZ8MD-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17042647UD6iZ8MD-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254182115805675532', '254182115797286920', 'attachPath', '/2023/02/17042647UD6iZ8MD.jpg');
INSERT INTO `k_pubmeta` VALUES ('254182155710283782', '254182155659952128', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254182155710283783', '254182155659952128', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254182155710283784', '254182155659952128', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254182155710283785', '254182155659952128', 'mainPic1', '/2023/02/170426297OEhw10A.jpg');
INSERT INTO `k_pubmeta` VALUES ('254182155710283786', '254182155659952128', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254182155710283787', '254182155659952128', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254182155710283788', '254182155659952128', 'cover', '/2023/02/17042615YxfGxur4.jpg');
INSERT INTO `k_pubmeta` VALUES ('254183939367419905', '254183939329671176', 'city', '210300');
INSERT INTO `k_pubmeta` VALUES ('254183939367419906', '254183939329671176', 'cover', '/2023/02/17043353rYo3FjB7.jpg');
INSERT INTO `k_pubmeta` VALUES ('254183939367419907', '254183939329671176', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254183939367419908', '254183939329671176', 'mainPic1', '/2023/02/17043400FSYKLJ2H.jpg');
INSERT INTO `k_pubmeta` VALUES ('254183939367419909', '254183939329671176', 'province', '210000');
INSERT INTO `k_pubmeta` VALUES ('254183939367419910', '254183939329671176', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254183939367419911', '254183939329671176', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254183939367419912', '254183939329671176', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254183939367419913', '254183939329671176', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254183978579968008', '254183978571579403', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17043411K4tmS9ZN.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17043411K4tmS9ZN-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17043411K4tmS9ZN-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17043411K4tmS9ZN-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254183978579968009', '254183978571579403', 'attachPath', '/2023/02/17043411K4tmS9ZN.jpg');
INSERT INTO `k_pubmeta` VALUES ('254184545679228937', '254184545666646016', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17043626MsVjuzyB.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17043626MsVjuzyB-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17043626MsVjuzyB-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17043626MsVjuzyB-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254184545679228938', '254184545666646016', 'attachPath', '/2023/02/17043626MsVjuzyB.jpg');
INSERT INTO `k_pubmeta` VALUES ('254188052410974217', '254188052377419785', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254188052415168519', '254188052377419785', 'province', '220000');
INSERT INTO `k_pubmeta` VALUES ('254188052415168520', '254188052377419785', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254188052415168521', '254188052377419785', 'cover', '/2023/02/17045004vkvjlUmi.jpg');
INSERT INTO `k_pubmeta` VALUES ('254188052415168522', '254188052377419785', 'city', '220300');
INSERT INTO `k_pubmeta` VALUES ('254188052415168523', '254188052377419785', 'mainPic1', '/2023/02/17045012bjuSjeyy.jpg');
INSERT INTO `k_pubmeta` VALUES ('254188087190142977', '254188087181754377', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17045031CkPsqmbz.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17045031CkPsqmbz-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17045031CkPsqmbz-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17045031CkPsqmbz-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254188087190142978', '254188087181754377', 'attachPath', '/2023/02/17045031CkPsqmbz.jpg');
INSERT INTO `k_pubmeta` VALUES ('254189937817731078', '254189937775788040', 'city', '150400');
INSERT INTO `k_pubmeta` VALUES ('254189937817731079', '254189937775788040', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('254189937817731080', '254189937775788040', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254189937817731081', '254189937775788040', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254189937817731082', '254189937775788040', 'cover', '/2023/02/17045744oc1PQ3bl.jpg');
INSERT INTO `k_pubmeta` VALUES ('254189937817731083', '254189937775788040', 'mainPic1', '/2023/02/17045751CHL3JlFD.jpg');
INSERT INTO `k_pubmeta` VALUES ('254189937821925378', '254189937775788040', 'contact', '小李子');
INSERT INTO `k_pubmeta` VALUES ('254189937821925379', '254189937775788040', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('254189937821925380', '254189937775788040', 'telephone', '11000043432');
INSERT INTO `k_pubmeta` VALUES ('254189987159523337', '254189987146940419', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17045804snVlmMr6.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17045804snVlmMr6-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17045804snVlmMr6-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17045804snVlmMr6-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254189987159523338', '254189987146940419', 'attachPath', '/2023/02/17045804snVlmMr6.jpg');
INSERT INTO `k_pubmeta` VALUES ('254190801689165832', '254190801680777216', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17050118fuaOZhXG.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17050118fuaOZhXG-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17050118fuaOZhXG-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17050118fuaOZhXG-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254190801689165833', '254190801680777216', 'attachPath', '/2023/02/17050118fuaOZhXG.jpg');
INSERT INTO `k_pubmeta` VALUES ('254193576011546630', '254193575990575110', 'subTitle', '信息也可以富文本编辑');
INSERT INTO `k_pubmeta` VALUES ('254193576011546631', '254193575990575110', 'cover', '/2023/02/17051211ipuvDnwf.jpg');
INSERT INTO `k_pubmeta` VALUES ('254193576011546632', '254193575990575110', 'mainPic1', '/2023/02/170512196LVbUXVz.jpg');
INSERT INTO `k_pubmeta` VALUES ('254195015198883840', '249735731727745026', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('254195702653698056', '254193575990575110', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('254195702653698057', '254193575990575110', 'city', '140400');
INSERT INTO `k_pubmeta` VALUES ('254196093097263106', '254196093080485894', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17052220fYwW4BpE.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17052220fYwW4BpE-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17052220fYwW4BpE-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17052220fYwW4BpE-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254196093097263107', '254196093080485894', 'attachPath', '/2023/02/17052220fYwW4BpE.jpg');
INSERT INTO `k_pubmeta` VALUES ('254398094175289348', '254398094150123522', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('254411500861177862', '254411500802457611', 'city', '500200');
INSERT INTO `k_pubmeta` VALUES ('254411500861177863', '254411500802457611', 'mainPic1', '/2023/02/17193808tHlrGIbf.jpg');
INSERT INTO `k_pubmeta` VALUES ('254411500861177864', '254411500802457611', 'subTitle', '信息发布');
INSERT INTO `k_pubmeta` VALUES ('254411500861177865', '254411500802457611', 'province', '500000');
INSERT INTO `k_pubmeta` VALUES ('254411500861177866', '254411500802457611', 'cover', '/2023/02/171937583KIeMpmV.jpg');
INSERT INTO `k_pubmeta` VALUES ('254411500861177867', '254411500802457611', 'contact', '信息');
INSERT INTO `k_pubmeta` VALUES ('254411500861177868', '254411500802457611', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('254411500861177869', '254411500802457611', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('254411660710297611', '254411660689326086', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/171938556L6h8kMK.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/171938556L6h8kMK-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/171938556L6h8kMK-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/171938556L6h8kMK-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254411660710297612', '254411660689326086', 'attachPath', '/2023/02/171938556L6h8kMK.jpg');
INSERT INTO `k_pubmeta` VALUES ('254416724527071239', '254416724518682630', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17195902QvJDtorO.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17195902QvJDtorO-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17195902QvJDtorO-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17195902QvJDtorO-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254416724527071240', '254416724518682630', 'attachPath', '/2023/02/17195902QvJDtorO.jpg');
INSERT INTO `k_pubmeta` VALUES ('254421138834898947', '254421138742624264', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17201634Y58N5lLn.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17201634Y58N5lLn-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17201634Y58N5lLn-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17201634Y58N5lLn-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254421138839093252', '254421138742624264', 'attachPath', '/2023/02/17201634Y58N5lLn.jpg');
INSERT INTO `k_pubmeta` VALUES ('254422333766942721', '254422333699833866', 'subTitle', '信息发布');
INSERT INTO `k_pubmeta` VALUES ('254422333766942722', '254422333699833866', 'mainPic1', '/2023/02/17202117XN5KQfyN.jpg');
INSERT INTO `k_pubmeta` VALUES ('254422333766942723', '254422333699833866', 'cover', '/2023/02/17202109OMHS4dN3.jpg');
INSERT INTO `k_pubmeta` VALUES ('254424854371680265', '254424854338125830', 'subTitle', '信息发布');
INSERT INTO `k_pubmeta` VALUES ('254429303056023557', '254429302993108996', 'subTitle', '信息发布');
INSERT INTO `k_pubmeta` VALUES ('254429704874541067', '254429704866152448', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/2023/02/17205037aV5ZluqQ.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"/2023/02/17205037aV5ZluqQ-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"/2023/02/17205037aV5ZluqQ-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"/2023/02/17205037aV5ZluqQ-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254429704878735363', '254429704866152448', 'attachPath', '/2023/02/17205037aV5ZluqQ.jpg');
INSERT INTO `k_pubmeta` VALUES ('254430174762418177', '254430174745640968', 'attachMetadata', '{\"width\":1001,\"height\":789,\"path\":\"/2023/02/17205229uPlwJ9zT.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":118,\"path\":\"/2023/02/17205229uPlwJ9zT-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":236,\"path\":\"/2023/02/17205229uPlwJ9zT-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":807,\"path\":\"/2023/02/17205229uPlwJ9zT-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('254430174762418178', '254430174745640968', 'attachPath', '/2023/02/17205229uPlwJ9zT.jpg');
INSERT INTO `k_pubmeta` VALUES ('254430352709959686', '254429302993108996', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1520933243109163018', '1520933242983333892', 'subTitle', '轩辕年谱之凤鸣岐山武王伐纣');
INSERT INTO `k_pubmeta` VALUES ('1520933243113357313', '1520933242983333892', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1520933243117551622', '1520933242983333892', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('1520933243117551623', '1520933242983333892', 'pstPrice', '1');
INSERT INTO `k_pubmeta` VALUES ('1520933243117551624', '1520933242983333892', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('1521140839837515784', '1521140839510360067', 'subTitle', '一个非常值钱的买卖');
INSERT INTO `k_pubmeta` VALUES ('1521140839841710089', '1521140839510360067', 'pstPrice', '11');
INSERT INTO `k_pubmeta` VALUES ('1521140839850098694', '1521140839510360067', 'city', '140200');
INSERT INTO `k_pubmeta` VALUES ('1521140839850098695', '1521140839510360067', 'ornPrice', '1000');
INSERT INTO `k_pubmeta` VALUES ('1521140839850098696', '1521140839510360067', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1521150609654988810', '1521150609617240074', 'subTitle', '行业精英大咖们的创业史');
INSERT INTO `k_pubmeta` VALUES ('1521150609659183108', '1521150609617240074', 'pstPrice', '23');
INSERT INTO `k_pubmeta` VALUES ('1521150609663377412', '1521150609617240074', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1521150609667571716', '1521150609617240074', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('1521150609667571717', '1521150609617240074', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1521168871621050369', '1521168871583301633', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('1521168871625244683', '1521168871583301633', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('1521168871629438981', '1521168871583301633', 'subTitle', '让让他有人提议');
INSERT INTO `k_pubmeta` VALUES ('1521175523703308297', '1521175523606839304', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('1521175523707502603', '1521175523606839304', 'subTitle', '312312312321');
INSERT INTO `k_pubmeta` VALUES ('1521175523711696897', '1521175523606839304', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('1521202151477133316', '1521202151401635845', 'subTitle', '无论你是谁你都是独一无二的品牌');
INSERT INTO `k_pubmeta` VALUES ('1521202151481327622', '1521202151401635845', 'province', '210000');
INSERT INTO `k_pubmeta` VALUES ('1521202151489716234', '1521202151401635845', 'city', '210300');
INSERT INTO `k_pubmeta` VALUES ('1521202151493910531', '1521202151401635845', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('1521455522486337545', '1521455522322759681', 'city', '150300');
INSERT INTO `k_pubmeta` VALUES ('1521455522490531848', '1521455522322759681', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('1521455522494726151', '1521455522322759681', 'subTitle', '寄意寒星荃不察，我以我血荐轩辕');
INSERT INTO `k_pubmeta` VALUES ('1521588799608111107', '1521588799557779463', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('1521588799612305418', '1521588799557779463', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('1521588799616499713', '1521588799557779463', 'subTitle', '中华草龟饲养员专业数据');
INSERT INTO `k_pubmeta` VALUES ('1521630976249872386', '1521630976207929350', 'city', '150300');
INSERT INTO `k_pubmeta` VALUES ('1521630976254066690', '1521630976207929350', 'subTitle', '一个非常值钱的买卖');
INSERT INTO `k_pubmeta` VALUES ('1521630976258260992', '1521630976207929350', 'province', '150000');
INSERT INTO `k_pubmeta` VALUES ('1521631894953443337', '1521631894928277513', 'city', '310100');
INSERT INTO `k_pubmeta` VALUES ('1521631894957637643', '1521631894928277513', 'subTitle', '知识图谱方法实践与应用研究');
INSERT INTO `k_pubmeta` VALUES ('1521631894961831936', '1521631894928277513', 'province', '310000');
INSERT INTO `k_pubmeta` VALUES ('1521636090398294024', '1521636090356350977', 'city', '350200');
INSERT INTO `k_pubmeta` VALUES ('1521636090402488327', '1521636090356350977', 'province', '350000');
INSERT INTO `k_pubmeta` VALUES ('1521636090406682628', '1521636090356350977', 'subTitle', '视频教程是当下火热的在线教育方式');
INSERT INTO `k_pubmeta` VALUES ('1521834554457505796', '1521834554293927942', 'city', '430400');
INSERT INTO `k_pubmeta` VALUES ('1521834554461700101', '1521834554293927942', 'subTitle', '记录自我、探索世界、继往开来的内容付费系统');
INSERT INTO `k_pubmeta` VALUES ('1521834554461700102', '1521834554293927942', 'province', '430000');
INSERT INTO `k_pubmeta` VALUES ('1521989344072220672', '1521989344030277641', 'subTitle', '献礼100周年SaaS平台，你可能不相信的伟大成就，诞生在一个不起眼的小镇');
INSERT INTO `k_pubmeta` VALUES ('1521989344076414981', '1521989344030277641', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1521989344080609288', '1521989344030277641', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1521989344084803584', '1521989344030277641', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('1521989344084803585', '1521989344030277641', 'cover', '/202107/01211828rUwaz6Jg.png');
INSERT INTO `k_pubmeta` VALUES ('1521989344084803586', '1521989344030277641', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1522186898051350533', '1522186897946492931', 'ornPrice', '1222');
INSERT INTO `k_pubmeta` VALUES ('1522186898055544837', '1522186897946492931', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1522186898059739136', '1522186897946492931', 'subTitle', '千亿级流量神话的网站架构知多少，解密你所不了解的核心技术');
INSERT INTO `k_pubmeta` VALUES ('1522186898063933445', '1522186897946492931', 'cover', '/202107/021018257KTVJ5Q1.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522186898063933446', '1522186897946492931', 'city', '130300');
INSERT INTO `k_pubmeta` VALUES ('1522186898063933447', '1522186897946492931', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522186898063933448', '1522186897946492931', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522298023271120896', '1522298023115931652', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522298023279509513', '1522298023115931652', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('1522298023283703813', '1522298023115931652', 'city', '370300');
INSERT INTO `k_pubmeta` VALUES ('1522298023287898119', '1522298023115931652', 'subTitle', '内容付费是引爆差异化、碎片化价值的重要阵地，只要你有价值，就有受众，你的价值决定市场蛋糕有多大');
INSERT INTO `k_pubmeta` VALUES ('1522298023287898120', '1522298023115931652', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522298023287898121', '1522298023115931652', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522298023292092422', '1522298023115931652', 'cover', '/202107/02174847OqqlA5yd.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522298023296286728', '1522298023115931652', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1522298023296286729', '1522298023115931652', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1522302799048261639', '1522302798985347082', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522302799052455944', '1522302798985347082', 'ornPrice', '234');
INSERT INTO `k_pubmeta` VALUES ('1522302799060844552', '1522302798985347082', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522302799065038848', '1522302798985347082', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522302799065038849', '1522302798985347082', 'city', '140200');
INSERT INTO `k_pubmeta` VALUES ('1522302799065038850', '1522302798985347082', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1522302799065038851', '1522302798985347082', 'subTitle', '让让他有人提议');
INSERT INTO `k_pubmeta` VALUES ('1522302799069233158', '1522302798985347082', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1522306517894348806', '1522306517864988680', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522306517898543105', '1522306517864988680', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('1522306517902737419', '1522306517864988680', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1522306517906931712', '1522306517864988680', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522306517915320327', '1522306517864988680', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522306517919514630', '1522306517864988680', 'reward', '12');
INSERT INTO `k_pubmeta` VALUES ('1522306517923708934', '1522306517864988680', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1522306517927903234', '1522306517864988680', 'cover', '/202107/02182232rHBLqMmM.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522306517932097546', '1522306517864988680', 'subTitle', '实务操作法律法规和典型案例，你知道的我都知道，我知道的你不知道。');
INSERT INTO `k_pubmeta` VALUES ('1522307826777243653', '1522307826739494915', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522307826781437958', '1522307826739494915', 'province', '520000');
INSERT INTO `k_pubmeta` VALUES ('1522307826785632258', '1522307826739494915', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522307826789826568', '1522307826739494915', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522307826794020868', '1522307826739494915', 'subTitle', '行业精英大咖们的创业史');
INSERT INTO `k_pubmeta` VALUES ('1522307826798215172', '1522307826739494915', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1522307826802409482', '1522307826739494915', 'city', '520300');
INSERT INTO `k_pubmeta` VALUES ('1522307826802409483', '1522307826739494915', 'ornPrice', '1000');
INSERT INTO `k_pubmeta` VALUES ('1522308366353481729', '1522308366324121602', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522308366357676039', '1522308366324121602', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522308366361870337', '1522308366324121602', 'province', '620000');
INSERT INTO `k_pubmeta` VALUES ('1522308366361870338', '1522308366324121602', 'cover', '/202107/021829531a2eER8W.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522308366361870339', '1522308366324121602', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522308366366064650', '1522308366324121602', 'subTitle', '无论你是谁你都是独一无二的品牌');
INSERT INTO `k_pubmeta` VALUES ('1522308366366064651', '1522308366324121602', 'city', '620300');
INSERT INTO `k_pubmeta` VALUES ('1522308366366064652', '1522308366324121602', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1522308366366064653', '1522308366324121602', 'reward', '98');
INSERT INTO `k_pubmeta` VALUES ('1522309813900066816', '1522309813845540866', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522309813904261128', '1522309813845540866', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522309813908455432', '1522309813845540866', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522309813912649737', '1522309813845540866', 'city', '650400');
INSERT INTO `k_pubmeta` VALUES ('1522309813916844043', '1522309813845540866', 'ornPrice', '234');
INSERT INTO `k_pubmeta` VALUES ('1522309813921038336', '1522309813845540866', 'subTitle', '让让他有人提议');
INSERT INTO `k_pubmeta` VALUES ('1522309813929426950', '1522309813845540866', 'cover', '/202107/02183538sK6GHThs.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522309813933621251', '1522309813845540866', 'province', '650000');
INSERT INTO `k_pubmeta` VALUES ('1522309813933621252', '1522309813845540866', 'reward', '66');
INSERT INTO `k_pubmeta` VALUES ('1522670207504138251', '1522670207286034441', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522670207508332548', '1522670207286034441', 'subTitle', '轩辕年谱之凤鸣岐山武王伐纣');
INSERT INTO `k_pubmeta` VALUES ('1522670207516721156', '1522670207286034441', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522670207516721157', '1522670207286034441', 'privacyLevel', 'token');
INSERT INTO `k_pubmeta` VALUES ('1522670207516721158', '1522670207286034441', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('1522670207516721159', '1522670207286034441', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('1522680911355363337', '1522680911237922817', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522680911359557636', '1522680911237922817', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522680911363751940', '1522680911237922817', 'ornPrice', '12');
INSERT INTO `k_pubmeta` VALUES ('1522680911367946245', '1522680911237922817', 'city', '130400');
INSERT INTO `k_pubmeta` VALUES ('1522680911372140547', '1522680911237922817', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1522680911372140548', '1522680911237922817', 'privacyLevel', 'token');
INSERT INTO `k_pubmeta` VALUES ('1522680911372140549', '1522680911237922817', 'cover', '/202107/03191011eDPm74Rf.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522680911372140550', '1522680911237922817', 'subTitle', '轩辕年谱之凤鸣岐山武王伐纣');
INSERT INTO `k_pubmeta` VALUES ('1522683503124856836', '1522683503091302409', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522683503129051137', '1522683503091302409', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1522683503133245449', '1522683503091302409', 'subTitle', '明初定都于南京应天府。建文年间，燕王朱棣自北平起兵，发动靖难之变。');
INSERT INTO `k_pubmeta` VALUES ('1522683503137439750', '1522683503091302409', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522683503141634059', '1522683503091302409', 'city', '210500');
INSERT INTO `k_pubmeta` VALUES ('1522683503145828355', '1522683503091302409', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522683503150022666', '1522683503091302409', 'province', '210000');
INSERT INTO `k_pubmeta` VALUES ('1522683503150022667', '1522683503091302409', 'cover', '/202107/031918222a5apAz7.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522683503150022668', '1522683503091302409', 'ornPrice', '234');
INSERT INTO `k_pubmeta` VALUES ('1522687132850372609', '1522687132816818177', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522687132854566915', '1522687132816818177', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522687132858761223', '1522687132816818177', 'subTitle', '以传承人为图书内容主线、通过口述史的方式进行呈现，不仅展现“非遗”项目中实践技艺的劳动和艺术价值');
INSERT INTO `k_pubmeta` VALUES ('1522687132862955522', '1522687132816818177', 'cover', '/202107/03193340dslCqmz4.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522687132867149826', '1522687132816818177', 'ornPrice', '1000');
INSERT INTO `k_pubmeta` VALUES ('1522687132871344138', '1522687132816818177', 'city', '130700');
INSERT INTO `k_pubmeta` VALUES ('1522687132875538443', '1522687132816818177', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1522687132879732745', '1522687132816818177', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1522688308215988232', '1522688308178239492', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1522688308220182536', '1522688308178239492', 'city', '510400');
INSERT INTO `k_pubmeta` VALUES ('1522688308224376841', '1522688308178239492', 'province', '510000');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571140', '1522688308178239492', 'ornPrice', '234');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571141', '1522688308178239492', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571142', '1522688308178239492', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571143', '1522688308178239492', 'cover', '/202107/031939372gG1ucLR.jpg');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571144', '1522688308178239492', 'subTitle', '岁馀以前，余先后撰中国政治思想史七册及中国教学思想史六册，历时四载，全部毕事。时余精力尚健，');
INSERT INTO `k_pubmeta` VALUES ('1522688308228571145', '1522688308178239492', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1523328202516905991', '1522999953312104459', 'url', 'http://192.168.1.23:8088/202107/05140224d7mGqaI2.jpg');
INSERT INTO `k_pubmeta` VALUES ('1523328202521100290', '1522999953312104459', 'attachPath', '/202107/05140224d7mGqaI2.jpg');
INSERT INTO `k_pubmeta` VALUES ('1523328202525294592', '1522999953312104459', 'attachMetadata', '{\"status\":200,\"message\":\"ok\",\"data\":{\"width\":960,\"height\":720,\"path\":\"/202107/05140224d7mGqaI2.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":113,\"path\":\"/202107/05140224d7mGqaI2.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":225,\"path\":\"/202107/05140224d7mGqaI2.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":768,\"path\":\"/202107/05140224d7mGqaI2.jpg\",\"mimeType\":\"image/jpeg\"}]}}');
INSERT INTO `k_pubmeta` VALUES ('1523338621809770500', '1522999953312104459', 'attachMetadata', '{\"width\":960,\"height\":720,\"path\":\"/202107/05144348pHnDlhO7.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":113,\"path\":\"/202107/05144348pHnDlhO7.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":225,\"path\":\"/202107/05144348pHnDlhO7.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":768,\"path\":\"/202107/05144348pHnDlhO7.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1523338621813964801', '1522999953312104459', 'attachPath', '/202107/05144348pHnDlhO7.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524804824847794177', '1524804823476256770', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1524804824851988486', '1524804823476256770', 'privacyLevel', 'token');
INSERT INTO `k_pubmeta` VALUES ('1524804824856182788', '1524804823476256770', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1524804824860377096', '1524804823476256770', 'cover', '/202107/09152523CsaPVExK.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524804824864571402', '1524804823476256770', 'mainPic1', '/202107/09152532nJJymuWY.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524804824864571403', '1524804823476256770', 'subTitle', '让让他有人提议');
INSERT INTO `k_pubmeta` VALUES ('1524804824868765702', '1524804823476256770', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('1524804824868765703', '1524804823476256770', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1524804824868765704', '1524804823476256770', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1524888103236059144', '1524888103068286980', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1524888103240253445', '1524888103068286980', 'province', '110000');
INSERT INTO `k_pubmeta` VALUES ('1524888103244447750', '1524888103068286980', 'city', '110100');
INSERT INTO `k_pubmeta` VALUES ('1524888103248642059', '1524888103068286980', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1524888103252836355', '1524888103068286980', 'ornPrice', '1000');
INSERT INTO `k_pubmeta` VALUES ('1524888103257030660', '1524888103068286980', 'mainPic4', '/202107/09211633PPJktYr0.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524888103261224963', '1524888103068286980', 'mainPic3', '/202107/09211805IA7XE38a.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524888103265419266', '1524888103068286980', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1524888103269613573', '1524888103068286980', 'mainPic1', '/202107/09211753HmzSGMPp.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524888103273807872', '1524888103068286980', 'mainPic2', '/202107/092110047vy2g6yk.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524888103278002179', '1524888103068286980', 'subTitle', '宝妈时光的美好回忆，记那些不曾回去的昨天，仿佛身临其境般神一般的存在');
INSERT INTO `k_pubmeta` VALUES ('1524888103282196490', '1524888103068286980', 'cover', '/202107/09210918OicmUK6C.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524888103286390792', '1524888103068286980', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1524911616835633160', '1524911615640256512', 'subTitle', '如果需要，这里可以放一些关于产品的常见问题说明。如果需要这里可以放一些关于产品的常见问题说明');
INSERT INTO `k_pubmeta` VALUES ('1524911616839827465', '1524911615640256512', 'mainPic4', '/202107/09225239VDqvLzzO.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524911616844021768', '1524911615640256512', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216071', '1524911615640256512', 'mainPic2', '/202107/09225228uWwcU0qP.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216072', '1524911615640256512', 'cover', '/202107/09225313GJDgE0VC.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216073', '1524911615640256512', 'city', '140200');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216074', '1524911615640256512', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216075', '1524911615640256512', 'mainPic1', '/202107/09225220CAhpIVRO.jpg');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216076', '1524911615640256512', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216077', '1524911615640256512', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216078', '1524911615640256512', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1524911616848216079', '1524911615640256512', 'mainPic3', '/202107/09225250nMSJ4mSU.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525836714912432131', '1525836482199863300', 'attachMetadata', '{\"width\":560,\"height\":347,\"path\":\"/202107/12121020z9HS5TXu.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":93,\"path\":\"E:\\\\Temp/202107/12121020z9HS5TXu-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":186,\"path\":\"E:\\\\Temp/202107/12121020z9HS5TXu-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":635,\"path\":\"E:\\\\Temp/202107/12121020z9HS5TXu-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1525836714916626437', '1525836482199863300', 'attachPath', '/202107/12121020z9HS5TXu.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525843815072186376', '1525843814879248388', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('1525843815080574981', '1525843814879248388', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769280', '1525843814879248388', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769281', '1525843814879248388', 'mainPic2', '/202107/12123754KtjSITZz.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769282', '1525843814879248388', 'mainPic4', '/202107/12123831vbhmXVRi.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769283', '1525843814879248388', 'mainPic1', '/202107/12123325Ihi1lj7b.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769284', '1525843814879248388', 'cover', '/202107/12123246VN5IZP1d.jpg');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769285', '1525843814879248388', 'mainPic3', '/202107/12123806EnBEdNA9.png');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769286', '1525843814879248388', 'subTitle', '你的卖点你的价值你的宣传你的名言你的吐槽你想说的一句话');
INSERT INTO `k_pubmeta` VALUES ('1525843815084769287', '1525843814879248388', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('1525843815088963591', '1525843814879248388', 'ornPrice', '256');
INSERT INTO `k_pubmeta` VALUES ('1525843815093157889', '1525843814879248388', 'contact', '唐三');
INSERT INTO `k_pubmeta` VALUES ('1525843815097352202', '1525843814879248388', 'reward', '50');
INSERT INTO `k_pubmeta` VALUES ('1525844152818515976', '1525843966499143682', 'attachMetadata', '{\"width\":550,\"height\":406,\"path\":\"/202107/12123954phlxk42c.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":111,\"path\":\"E:\\\\Temp/202107/12123954phlxk42c-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":221,\"path\":\"E:\\\\Temp/202107/12123954phlxk42c-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":756,\"path\":\"E:\\\\Temp/202107/12123954phlxk42c-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1525844152822710282', '1525843966499143682', 'attachPath', '/202107/12123954phlxk42c.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705067646981', '1527684704723714055', 'subTitle', 'wldos是一个原生云平台，从冗繁项目实践中精简优化而来，面向世界，面向未来，开放自由');
INSERT INTO `k_pubmeta` VALUES ('1527684705071841288', '1527684704723714055', 'city', '430400');
INSERT INTO `k_pubmeta` VALUES ('1527684705076035587', '1527684704723714055', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424202', '1527684704723714055', 'mainPic3', '/202107/17143318x9NSyo7H.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424203', '1527684704723714055', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424204', '1527684704723714055', 'mainPic2', '/202107/17143301qRRCrAMS.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424205', '1527684704723714055', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424206', '1527684704723714055', 'ornPrice', '1000');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424207', '1527684704723714055', 'mainPic1', '/202107/17143240IRlMQj7I.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424208', '1527684704723714055', 'mainPic4', '/202107/17143332lFnd2J4O.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424209', '1527684704723714055', 'cover', '/202107/171432192HQnmgy1.jpg');
INSERT INTO `k_pubmeta` VALUES ('1527684705084424210', '1527684704723714055', 'province', '430000');
INSERT INTO `k_pubmeta` VALUES ('1531257784746295304', '1531257784574328835', 'mainPic4', '/202107/27111140NW6ngca5.jpg');
INSERT INTO `k_pubmeta` VALUES ('1531257784750489610', '1531257784574328835', 'city', '130300');
INSERT INTO `k_pubmeta` VALUES ('1531257784754683908', '1531257784574328835', 'ornPrice', '123');
INSERT INTO `k_pubmeta` VALUES ('1531257784758878214', '1531257784574328835', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1531257784763072522', '1531257784574328835', 'mainPic3', '/202107/27111121Hr4rrTF9.png');
INSERT INTO `k_pubmeta` VALUES ('1531257784767266816', '1531257784574328835', 'subTitle', '轩辕年谱之凤鸣岐山武王伐纣');
INSERT INTO `k_pubmeta` VALUES ('1531257784771461124', '1531257784574328835', 'telephone', '18660802025');
INSERT INTO `k_pubmeta` VALUES ('1531257784779849734', '1531257784574328835', 'mainPic1', '/202107/27111057jDC85Pgu.jpg');
INSERT INTO `k_pubmeta` VALUES ('1531257784784044041', '1531257784574328835', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1531257784788238345', '1531257784574328835', 'mainPic2', '/202107/27111108Q9ZPYfBw.jpg');
INSERT INTO `k_pubmeta` VALUES ('1531257784792432650', '1531257784574328835', 'cover', '/202107/27111044jywdPf5p.jpg');
INSERT INTO `k_pubmeta` VALUES ('1531257784796626946', '1531257784574328835', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1540446858145087488', '1531257784574328835', 'views', '109');
INSERT INTO `k_pubmeta` VALUES ('1540449618785648650', '1527684704723714055', 'views', '67');
INSERT INTO `k_pubmeta` VALUES ('1540702408741142530', '1525843814879248388', 'views', '116');
INSERT INTO `k_pubmeta` VALUES ('1540768445977247751', '1522688308178239492', 'views', '40');
INSERT INTO `k_pubmeta` VALUES ('1541023699574636551', '1522683503091302409', 'views', '9');
INSERT INTO `k_pubmeta` VALUES ('1541168156634693637', '1524911615640256512', 'views', '157');
INSERT INTO `k_pubmeta` VALUES ('1541171492406607872', '1529498106118979585', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1541171551953141761', '1529498103988273161', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1541172778652844042', '1529498102742564872', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1541172873909682179', '1529498102721593344', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('1541173026435547143', '1529498101626880001', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1541174158012629000', '1525843966499143682', 'views', '2032');
INSERT INTO `k_pubmeta` VALUES ('1541179345750114310', '1527695423368249352', 'views', '9');
INSERT INTO `k_pubmeta` VALUES ('1541179423722225665', '1527685691383398407', 'views', '10');
INSERT INTO `k_pubmeta` VALUES ('1541448785989386249', '1538942586760904711', 'views', '1949');
INSERT INTO `k_pubmeta` VALUES ('1541492409720553476', '1538942961920425988', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('1541514967052369929', '1522999953312104459', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('1541768862500569097', '1541768862404100102', 'subTitle', '互联网开放运营平台，独特的轻量级企业业务中台架构，可以实现一租多域的多租、多域SaaS');
INSERT INTO `k_pubmeta` VALUES ('1541768862504763403', '1541768862404100102', 'mainPic2', '/202108/251118362Iqxb3Pf.png');
INSERT INTO `k_pubmeta` VALUES ('1541768862508957704', '1541768862404100102', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('1541768862513152001', '1541768862404100102', 'mainPic4', '/202108/251118569kfNcZrm.png');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346315', '1541768862404100102', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346316', '1541768862404100102', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346317', '1541768862404100102', 'mainPic1', '/202108/25111750HOJDk9mF.png');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346318', '1541768862404100102', 'mainPic3', '/202108/25111825jdfmAilb.png');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346319', '1541768862404100102', 'cover', '/202108/25111621MIHFQ3X4.png');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346320', '1541768862404100102', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1541768862517346321', '1541768862404100102', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1541768862521540613', '1541768862404100102', 'ornPrice', '5000000');
INSERT INTO `k_pubmeta` VALUES ('1541803026671124483', '1541803026658541572', 'views', '59');
INSERT INTO `k_pubmeta` VALUES ('1541804009950199816', '1541768862404100102', 'views', '232');
INSERT INTO `k_pubmeta` VALUES ('1541836145470128135', '1522308366324121602', 'views', '25');
INSERT INTO `k_pubmeta` VALUES ('1541856026928267267', '1522309813845540866', 'views', '15');
INSERT INTO `k_pubmeta` VALUES ('1541856240288317440', '1522298023115931652', 'views', '23');
INSERT INTO `k_pubmeta` VALUES ('1542187370828972037', '1524888103068286980', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('1542190914806136834', '1529498105087180804', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1542543578026917897', '1521989344030277641', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('1542543610117537792', '1521834554293927942', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('1542543675003420672', '1522302798985347082', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1542544016226828289', '1522307826739494915', 'views', '7');
INSERT INTO `k_pubmeta` VALUES ('1542544053677768705', '1521140839510360067', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1542546292643381255', '1522306517864988680', 'views', '31');
INSERT INTO `k_pubmeta` VALUES ('1542548544149962762', '1522687132816818177', 'views', '30');
INSERT INTO `k_pubmeta` VALUES ('1542550917421383685', '1523370148656496651', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1542552455405551621', '1522680911237922817', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('1542552589027688453', '1525836855404838913', 'views', '20');
INSERT INTO `k_pubmeta` VALUES ('1543284244914814985', '1522186897946492931', 'views', '15');
INSERT INTO `k_pubmeta` VALUES ('1543294847268077577', '1521455522322759681', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('1543712519252262915', '1521202151401635845', 'views', '13');
INSERT INTO `k_pubmeta` VALUES ('1543714171401191431', '1522670207286034441', 'views', '7');
INSERT INTO `k_pubmeta` VALUES ('1544018400351272970', '1525836858034667529', 'views', '17');
INSERT INTO `k_pubmeta` VALUES ('1544035910261850113', '1525836856323391496', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('1544035965391781891', '1525836854565978121', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('1544036002473623561', '1525836852162641930', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1544036021561901056', '1525836851571245059', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('1544036056320098305', '1525836842528325642', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1544036074397548554', '1525836482199863300', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('1544037529120587785', '1525836857233555464', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('1544057356287655947', '1525836853693562886', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('1544160818937643019', '1521150609617240074', 'views', '4');
INSERT INTO `k_pubmeta` VALUES ('1544356231925907456', '1544356231896547328', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('1544356231930101763', '1544356231896547328', 'mainPic1', '/202109/01143912YQboaB0y.png');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296074', '1544356231896547328', 'mainPic3', '/202109/01144002lV1aQSTW.jpg');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296075', '1544356231896547328', 'subTitle', '业务中台在国民经济中的位置越来越重要');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296076', '1544356231896547328', 'cover', '/202109/01143856Nsz2ilbt.png');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296077', '1544356231896547328', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296078', '1544356231896547328', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296079', '1544356231896547328', 'city', '370100');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296080', '1544356231896547328', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296081', '1544356231896547328', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296082', '1544356231896547328', 'mainPic2', '/202109/01143943fehF4DiP.jpg');
INSERT INTO `k_pubmeta` VALUES ('1544356231934296083', '1544356231896547328', 'mainPic4', '/202109/01144014ZzWE8UDf.jpg');
INSERT INTO `k_pubmeta` VALUES ('1544356320996147203', '1544356320987758602', 'views', '45');
INSERT INTO `k_pubmeta` VALUES ('1544386503438417920', '1544386503421640713', 'views', '22');
INSERT INTO `k_pubmeta` VALUES ('1544401216180240395', '1544401216171851784', 'views', '18');
INSERT INTO `k_pubmeta` VALUES ('1544401257238282246', '1544401257229893635', 'views', '46');
INSERT INTO `k_pubmeta` VALUES ('1544401655005102081', '1544401654996713472', 'views', '37');
INSERT INTO `k_pubmeta` VALUES ('1544414732375212035', '1544414732358434826', 'views', '49');
INSERT INTO `k_pubmeta` VALUES ('1544414748170960898', '1544414748162572291', 'views', '40');
INSERT INTO `k_pubmeta` VALUES ('1544422560057966598', '1544422560041189382', 'views', '64');
INSERT INTO `k_pubmeta` VALUES ('1544422567922286600', '1544422567918092296', 'views', '117');
INSERT INTO `k_pubmeta` VALUES ('1544454752880476165', '1544356231896547328', 'views', '228');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634763', '1545223091294748672', 'mainPic1', '/202109/04000351Tfxqjosp.jpeg');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634764', '1545223091294748672', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634765', '1545223091294748672', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634766', '1545223091294748672', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634767', '1545223091294748672', 'city', '120100');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634768', '1545223091294748672', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634769', '1545223091294748672', 'cover', '/202109/04000337YZNOOEFx.jpg');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634770', '1545223091294748672', 'province', '120000');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634771', '1545223091294748672', 'mainPic3', '/202109/04000408709PsC0q.jpeg');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634772', '1545223091294748672', 'mainPic2', '/202109/040003595xcM0wTs.jpeg');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634773', '1545223091294748672', 'mainPic4', '/202109/04000416xQgQgiTs.png');
INSERT INTO `k_pubmeta` VALUES ('1545223091378634774', '1545223091294748672', 'subTitle', '互联网开放运营平台，独特的轻量级企业业务中台架构，可以实现一租多域的多租、多域SaaS');
INSERT INTO `k_pubmeta` VALUES ('1545411878616481793', '1545223091294748672', 'views', '116');
INSERT INTO `k_pubmeta` VALUES ('1545419674586103813', '1545419674577715201', 'views', '10');
INSERT INTO `k_pubmeta` VALUES ('1545419688439889926', '1545419687328399367', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419693762461701', '1545419693603078146', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419696899801091', '1545419696891412482', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419699735150600', '1545419699571572739', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419700498513920', '1545419700490125321', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419701295431685', '1545419701287043081', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419703463886849', '1545419703451303937', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419704063672323', '1545419703975591939', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419704579571721', '1545419704566988800', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419704701206529', '1545419704692817929', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419705665896451', '1545419705657507840', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419706500562947', '1545419706353762314', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545419706928381955', '1545419706919993348', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1545489774823522314', '1545419674577715201', 'attachMetadata', '{\"width\":1920,\"height\":1080,\"path\":\"/202109/041744342o5pmfTR.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":84,\"path\":\"E:\\\\Temp/store/202109/041744342o5pmfTR-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":169,\"path\":\"E:\\\\Temp/store/202109/041744342o5pmfTR-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":576,\"path\":\"E:\\\\Temp/store/202109/041744342o5pmfTR-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1545489774823522315', '1545419674577715201', 'attachPath', '/202109/041744342o5pmfTR.jpg');
INSERT INTO `k_pubmeta` VALUES ('1545506566274990089', '1545419674577715201', 'attachMetadata', '{\"width\":600,\"height\":600,\"path\":\"/202109/04185118oZpFiPp9.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":150,\"path\":\"/202109/04185118oZpFiPp9-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":300,\"path\":\"/202109/04185118oZpFiPp9-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":1024,\"path\":\"/202109/04185118oZpFiPp9-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1545506566274990090', '1545419674577715201', 'attachPath', '/202109/04185118oZpFiPp9.jpg');
INSERT INTO `k_pubmeta` VALUES ('1545506639545286658', '1545419674577715201', 'attachMetadata', '{\"width\":960,\"height\":720,\"path\":\"/202109/04185136DNuAsjPM.jpg\",\"srcset\":[{\"type\":\"thumbnail\",\"width\":150,\"height\":113,\"path\":\"/202109/04185136DNuAsjPM-150x150.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":225,\"path\":\"/202109/04185136DNuAsjPM-300x300.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":768,\"path\":\"/202109/04185136DNuAsjPM-1024x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('1545506639549480963', '1545419674577715201', 'attachPath', '/202109/04185136DNuAsjPM.jpg');
INSERT INTO `k_pubmeta` VALUES ('1545903377850482692', '1521202243919593483', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('1546121183812567042', '1521168871583301633', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('1547574569808347139', '1520933242983333892', 'views', '5');
INSERT INTO `k_pubmeta` VALUES ('1547701724336799752', '1547701724307439620', 'subTitle', '关于测试的所有过程');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994049', '1547701724307439620', 'mainPic4', '/202109/102014020rrgsZvG.png');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994050', '1547701724307439620', 'ornPrice', '50');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994051', '1547701724307439620', 'cover', '/202109/102013279T2uqrn5.jpg');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994052', '1547701724307439620', 'contact', '树悉猿');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994053', '1547701724307439620', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994054', '1547701724307439620', 'city', '130200');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994055', '1547701724307439620', 'mainPic3', '/202109/10201352B99IKc4V.jpg');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994056', '1547701724307439620', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('1547701724340994057', '1547701724307439620', 'telephone', '15665730355');
INSERT INTO `k_pubmeta` VALUES ('1547701724345188359', '1547701724307439620', 'mainPic1', '/202109/10201334lfZZZhIP.jpg');
INSERT INTO `k_pubmeta` VALUES ('1547701724345188360', '1547701724307439620', 'mainPic2', '/202109/10201342KQ7TUvLR.jpg');
INSERT INTO `k_pubmeta` VALUES ('1547701778569150471', '1547701778556567556', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('1547989241980174340', '1547701724307439620', 'views', '19');
INSERT INTO `k_pubmeta` VALUES ('1548354494500880388', '1522187456745226244', 'views', '72');
INSERT INTO `k_pubmeta` VALUES ('1548449415811481602', '39', 'views', '15');
INSERT INTO `k_pubmeta` VALUES ('1549902341083938822', '33', 'views', '15');

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
-- Records of k_pubs
-- ----------------------------
INSERT INTO `k_pubs` VALUES ('71095902778605572', '<p>各种文字和图片排版你随意。</p>\n<p>我又来了。</p>\n<p>&nbsp;</p>', '测试一些分类是否正确', null, 'inherit', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '1', '0', '1', '20', '1', '2021-09-29 23:07:34', '192.168.1.23', '1', '2021-09-29 23:30:51', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('71098015290146827', null, '2021-09-29 23:15:57', null, 'offline', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-29 23:15:58', '192.168.1.23', '1', '2021-09-29 23:15:58', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('71098018817556482', null, '2021-09-29 23:15:58', null, 'offline', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-29 23:15:59', '192.168.1.23', '1', '2021-09-29 23:15:59', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('71098019769663489', null, '2021-09-29 23:15:58', null, 'offline', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-29 23:15:59', '192.168.1.23', '1', '2021-09-29 23:15:59', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('71098022001033220', null, '2021-09-29 23:15:59', null, 'offline', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-29 23:15:59', '192.168.1.23', '1', '2021-09-29 23:15:59', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('71098022814728198', '<p>1212</p>', '2021-09-29 23:15:59', null, 'offline', null, null, null, '71090908285288456', 'chapter', '1533544727530094592', null, null, '0', '0', '1', null, '1', '2021-09-29 23:16:00', '192.168.1.23', '1', '2021-09-29 23:49:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('80366982974324744', '秦始皇嬴政（前259年—前210年） [1]  ，嬴姓，赵氏 [2]  ，名政（一说名“正”），又称赵政 [3]  、祖龙 [4-5]  ，也有吕政一说。 [144]  秦庄襄王和赵姬之子。 [6]  中国古代杰出的政治家、战略家、改革家，首次完成中国大一统的政治人物，也是中国第一个称皇帝的君主。\n嬴政出生于赵国都城邯郸，后回到秦国。前247年继承王位，时年十三岁。 [7]  前238年，平定长信侯嫪毐的叛乱，之后又除掉权臣吕不韦，开始独揽大政。 [8]  重用李斯、王翦等人，自前230年至前221年，先后灭韩、赵、魏、楚、燕、齐六国，完成了统一中国大业，建立起一个中央集权的统一的多民族国家——秦朝 [9]  。', '秦始皇年谱', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', '70', '1', '2021-10-25 13:07:32', '192.168.1.23', '1', '2021-10-25 13:07:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('81101003052204043', '选择分类\nPC端鼠标移动到分类上，弹出小类选择框，点击选定的小类打开信息编辑页；移动端需要点击分类弹出小类选择框。\n填写信息\n填写信息需要上传封面图和主图，如果不上传，会展示默认封面，默认封面不具备显著特征不易被识别，主图至少上传一张。\n选择分类\nPC端鼠标移动到分类上，弹出小类选择框，点击选定的小类打开信息编辑页；移动端需要点击分类弹出小类选择框。\n填写信息\n填写信息需要上传封面图和主图，如果不上传，会展示默认封面，默认封面不具备显著特征不易被识别，主图至少上传一张。', '测试一张主图', null, 'publish', null, null, 'ceshiyizhangzhutu', '0', 'book', '1533544727530094592', null, null, '0', '0', '0', '100', '1', '2021-10-27 13:44:16', '192.168.1.23', '1', '2021-12-20 17:46:09', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('81759135164186628', '<div class=\"para\" data-pid=\"1\">王阳明(1472年10月31日亥时&mdash;1529年1月9日辰时)，汉族，幼名云，五岁更名守仁，字伯安。因曾筑室修道于会稽山阳明洞，自号阳明子，世称阳明先生。明朝浙江绍兴府余姚县(今属宁波余姚)人，伟大的思想家、哲学家、军事家、文学家、书法家、教育家，精通儒道佛三家，阳明心学宗师及集大成者。王守仁（心学集大成者）与孔子（儒学创始人）、孟子（儒学集大成者）、朱熹（理学集大成者）合称为孔孟朱王，公认为&ldquo;立德、立功、立言&rdquo;的真三不朽。阳明父亲是成化十七年(1481年)状元龙山公王华先生。</div>\n<div class=\"para\" data-pid=\"2\">弘治十二年（1499年）进士，历任刑部主事、贵州龙场驿丞、庐陵知县、右佥都御史、南赣巡抚、两广总督等职，晚年官至两京兵部尚书、都察院左都御史兼两广巡抚。因平定宁王朱宸濠之乱军功而被封为新建伯，隆庆年间追赠新建侯，谥文成，故又称王文成公。万历年间，从祀于孔庙，奉祀孔庙东庑第五十八位。</div>\n<div class=\"para\" data-pid=\"3\">阳明心学是明朝中后期影响最大的哲学思想，传至日本、朝鲜半岛、中国台湾及东南亚，弟子及仰慕者极众，包括明明了凡。阳明文章博大昌达，行墨间有俊爽之气，有《王阳明全集》，代表作《瘗旅文》、《大学问》、《象祠记》、《尊经阁记》和《传习录》。</div>\n<p>1472年 宪宗成化八年壬辰夏历九月三十日亥时，出生于浙江省绍兴府余姚县龙泉山附近瑞云楼。先生母亲怀胎14个月，在祖母岑氏&ldquo;神仙驾祥云送子&rdquo;的梦中，先生诞生。祖父竹轩翁给先生取名&ldquo;云&rdquo;。<br />1473年 成化九年癸巳，二岁，余姚。<br />1474年 成化十年甲午，三岁，余姚。没有咿呀学语，父母很着急，爷爷竹轩翁(1421-1490，名王伦，字天叙)坚信&ldquo;贵人语迟&rdquo;。<br />1475年 成化十一年乙未，四岁，余姚。<br />1476年 成化十二年丙申，五岁，余姚，仍未开口说话。有神僧过而说：&ldquo;好个孩儿，可惜道破&rdquo;。竹轩翁为先生更名 &ldquo;守仁&rdquo;，即能说话，且能背诵竹轩翁所读之书。<br />1477年 成化十三年丁酉，六岁，余姚。<br />1478年 成化十四年戊戌，七岁，余姚，沉迷于中国象棋。母亲反对，在一次震怒后把象棋扔进河中，先生写《哭象棋》诗。<br />1479年 成化十五年己亥，八岁，余姚。<br />1480年 成化十六年庚子，九岁，余姚。<br />1481年 成化十七年辛丑，十岁，余姚。父龙山公王华中状元，入京师。<br />1482年 成化十八年壬寅，十一岁，入京师。竹轩翁因龙山公迎养，携先生入京师。途经金山寺，先生赋诗《过金山寺》和《蔽月山房》。<br />1483年 成化十九年癸卯，十二岁，京师。请教老师：何为人生第一等事？师说：读书登第，汝父也。先生说：恐未是，当读书做圣贤耳。<br />1484年 成化二十年甲辰，十三岁，京师。生母郑氏去世，先生为母守孝三年。先生叹生命有限，为先生后来修道家神仙养生术埋下伏笔。守仁&ldquo;被窝藏鸟&rdquo;智斗继母，迫使继母态度大改善。<br />1485年 成化二十一年乙巳，十四岁，京师。<br />1486年 成化二十二年丙午，十五岁，京师。先生出居庸关(今北京昌平区境内)，逐胡儿骑射，凭吊古战场，缅怀先辈于谦，慨然有经略四方之志。经月始返，夜梦拜谒伏波将军马援庙。<br />1487年 成化二十三年丁未，十六岁，京师。先生感慨时事，屡次欲上书皇帝，被父龙山公止之。<br />1488年 孝宗弘治元年戊申，十七岁，洪都。七月，与诸氏完婚于江西洪都(今南昌)。诸氏名&ldquo;芸&rdquo;，浙江余姚人，父诸养和时任江西布政使参议。新婚日，偶入铁柱宫，与道士相对而坐忘归。新婚期间，潜心书法，书艺大进。<br />1489年 弘治二年已酉，十八岁，寓江西，先生始慕圣学。十二月，携夫人归余姚，乘船路经广信(今江西上饶)，识理学大儒娄谅(号一斋)，信&ldquo;圣人必可学而至&rdquo;。一改活泼性格，严肃求成圣人。<br />1490年 弘治三年庚戌，十九岁，余姚。受一斋先生所授&ldquo;格物致知&rdquo;之学，遍读朱熹著作，思宋儒&ldquo;物有表里精粗，一草一木皆具至理&rdquo;，格竹七日，无果，患咳嗽病。是年，竹轩翁在京仙逝，王华扶竹轩翁灵柩归余姚，丁忧三年。龙山公嘱咐弟王冕等人为守仁讲经析义，先生学业大有长进。<br />1491年 弘治四年辛亥，二十岁，余姚。王家搬迁至山阴(今浙江绍兴越城区王衙弄19号)，余姚老宅由钱氏居住。<br />1492年 弘治五年壬子，二十一岁，在越城。杭州秋闱，中举浙江乡试。孙燧和胡世宁同举，后宁王造反，&ldquo;三人好做事&rdquo;。父王华丁忧期满，回京复命。<br />1493年 弘治六年癸丑，二十二岁。京师春闱，会试不第，首辅李东阳戏曰：待汝做来科状元，试作《来科状元赋》。先生拈笔而就。有忌者曰：此子若取第，目中无我辈矣。归余姚，结诗社于龙泉山寺，对弈联诗。<br />1494年 弘治七年甲寅，二十三岁。龙泉诗社，吐故纳新，吸收了很多当地知识分子。<br />1495年 弘治八年乙卯，二十四岁，越城。<br />1496年 弘治九年丙辰，二十五岁，京师。春闱，竟为忌者所抑，会试再不第。先生曰：汝以不得第为耻，吾以不得第动心为耻。是年，钱德洪（1496&mdash;1574）出生于余姚瑞云楼，后撰《瑞云楼记》。<br />1497年 弘治十年丁已，二十六岁。寓京师，时边关甚急。苦学诸家兵法，以果核列阵为戏，想借雄成圣，但被人讥笑为赵括&ldquo;纸上谈兵&rdquo;，且无施展舞台。<br />1498年 弘治十一年戊午，二十七岁，京师。接受现实，立下探究理学之志，苦读朱熹《四书集注》，循序致精，居敬持志，然物理吾心终若判而为二。偶闻道士谈养生，产生遗世入山的念头。<br />1499年 弘治十二年己未，二十八岁，京师。春闱会试第二名，殿试赐进士出身，二甲第七(全国第十名)，观政工部，全国第217名是伍文定。结交李梦阳等前七子。秋，钦差督造威宁伯王越墓，竣工，出威宁伯宝剑赠先生，与梦相符，欣然接受。是年，因&ldquo;会试泄题案&rdquo;，唐伯虎落第，不得为官。<br />1500年 弘治十三年庚申，二十九岁。在京师，授刑部云南清吏司主事，上书《陈言边务疏》。<br />1501年 弘治十四年辛酉，三十岁，在京师。奉命到直隶、淮安审决积案重囚，平反多件冤案。游九华山，出入佛寺道观，做《九华山赋》。<br />1502年 弘治十五年壬戌，三十一岁。五月复命，八月告病归越城，筑室会稽山阳明洞天，静坐行导引术，能先知，后因其簸弄精神，不能成圣，摒去。自号&ldquo;阳明子&rdquo;，人称&ldquo;阳明先生&rdquo;。是年，先生渐悟二氏之非。<br />1503年 弘治十六年癸亥，三十二岁。来杭州西湖疗养，劝归虎跑寺已闭关三年的得道高僧回乡孝母。<br />1504年 弘治十七年甲子，三十三岁，京师。秋季，主考山东乡式，撰写《山东乡试录》，拜谒孔庙，登泰山。九月改兵部武选清吏司主事(正六品)。是年，穆孔晖(1479～1539，今山东聊城人)中解元。<br />1505年 弘治十八年乙丑，三十四岁，京师。开门授徒。与湛若水（1466～1560，号甘泉，广州府增城人）定交，共倡圣学。后，若水为阳明撰写墓志铭。<br />1506年 武宗正德元年丙寅，三十五岁，京师，徐爱拜师，未收。刘瑾擅权，二月，先生为南京言官戴铣上疏，下诏狱，廷杖四十，贬谪贵州修文龙场驿驿丞。父王华明升暗降调任南京吏部尚书。<br />1507年 正德二年丁卯，三十六岁，南下赴谪，刘瑾派刺客追杀，至钱塘江，假言投江脱之，过武夷山，去南京看望时任南京吏部尚书的父亲。十二月回越城，正式收徐爱为首席大弟子。<br />1508年 正德三年戊辰，三十七岁。春，至贵州修文县龙场，途中收多名弟子，包括冀元亨（1482－1521）。大悟&ldquo;圣人之道，吾性自足，向之求理于事物者误也&rdquo;，史称&ldquo;龙场悟道&rdquo;。龙场作《瘗旅文》和《象祠记》收录于《古文观止》。<br />1509年 正德四年己巳，三十八岁，贵阳。受提学副使席书聘请主讲文明书院，始揭&ldquo;知行合一&rdquo;之旨。<br />1510年 正德五年庚午，三十九岁。刘瑾伏诛，三月，任江西庐陵知县，路过辰州、常州时教人静坐工夫。十一月入京，住大兴隆寺，和若水、黄绾(1477-1551，字宗贤、号久庵，浙江省黄岩县洞黄人，官至南京礼部尚书兼翰林学士)订终日共学。十二月升南京刑部四川清吏司主事。<br />1511年 正德六年辛未，四十岁，京师。正月调吏部验封清吏司主事。二月为会试同考官。十月升文选清吏司员外郎。<br />1512年 正德七年壬申，四十一岁，京师。三月升考功清吏司郎中，穆孔晖、黄绾、徐爱等几十人同受业，讲学内容由徐爱记录整理，名《传习录》。十二月升南京太仆寺少卿，赴任南京便道归省，徐爱升南京工部员外郎，与先生同舟回越城。<br />1513年 正德八年癸酉，四十二岁，二月回越城。十月至滁州，督马政。地僻官闲，日与门人游琅铘山水间。新旧学生大集滁州，教人静坐入道。<br />1514年 正德九年甲戌，四十三岁。四月，升南京鸿胪寺卿，五月至南京，在南京教人&ldquo;存天理、去私欲&rdquo;。<br />1515年 正德十年乙亥，四十四岁，京师。上疏请归，不允。八月写《谏迎佛疏》，用儒家思想的博大精深衬托出了佛家思想的各种不足，未上。立正宪为嗣子，时年八岁。<br />1516年 正德十一年丙子，四十五岁，在南京。九月，经兵部尚书王琼特荐，升都察院左佥都御史，巡抚南赣汀漳等处。十月，回越城看望祖母和父亲，祖母岑氏九十七高龄。<br />1517年 正德十二年丁丑，四十六岁。正月至赣，二月先平漳寇，四月班师驻军上杭，五月奏设福建平和县，六月上疏请疏通盐法，九月改授提督南赣汀漳等处军务，得旗牌，可便宜行事。十月平横水、桶岗等地，行十家牌法，自此，伍文定并肩作战。十二月班师，闰十二月奏设江西崇义县。<br />1518年 正德十三年戊寅，四十七岁。正月，征三浰，三月上疏乞致仕，不允，平大帽山、浰头，四月班师，立社学教化沿途当地百姓。五月奏设广东和平县。历经一年又三月，危害多年的四省流民暴乱被阳明先生平定。六月，升都察院右副都御史，世袭百户，辞免，不允。七月，刻古本《大学》、《朱子晚年定论》。八月，门人薛侃在赣州刻《传习录》。九月，修濂溪书院，四方学者云集于此。徐爱卒，先生为之恸哭。十一月，再请疏通盐法。<br />1519年 正德十四年已卯，四十八岁，在江西。六月，奉命勘处福建叛军，至丰城，闻宁王朱宸濠反，遂返吉安，起义兵，平宁王之乱。仅43日，宁王之乱宣告失败，先生安葬娄妃(娄素珍，宁王妃，一斋先生小女儿，始终劝说宁王勿反)。八月，武宗南下，与前来抢功悦君的宦官张忠、许泰群小周旋。祖母岑氏仙逝，乞便道省葬，不允。<br />1520年 正德十五年庚辰，四十九岁，在江西。王艮投门下，艮后创泰州学派。阳明自言在应付宦官刁难时全靠良知指引。十二月，武宗回驾入宫。<br />1521年 正德十六年辛已，五十岁，在江西。正月，居南昌，始揭&ldquo;致良知&rdquo;之教。三月，正德崩。世宗嘉靖上台，冀元亨先前被群小折磨，出狱几日卒。五月，集门人于白鹿洞。六月升南京兵部尚书。八月回越城，九月归余姚省祖茔，访瑞云楼，钱德洪等拜入门下。十二月，归越城为父王华祝寿，封&ldquo;新建伯&rdquo;，特进光禄大夫柱国，兼两京兵部尚书。<br />1522年 壬午 世宗嘉靖元年。五十一岁，山阴。正月，疏辞封爵，二月，父王华仙逝，享年七十七，丁忧。首辅杨廷和旨意倡议禁遏王学。<br />1523年 癸未 嘉靖二年，五十二岁，山阴。来从学者日众。南京刑部主事桂萼大礼议得宠。九月，改葬龙山公于天柱峰，郑太夫人于徐山。<br />1524年 嘉靖三年甲申，五十三岁。山阴。正月，门人日进，南大吉拜入门下。四月，服阕，朝中屡有荐者，有人以大礼见问，不答。八月中秋，宴门人于天泉桥，盛况空前。十月，南大吉续刻《传习录》，增五卷。<br />1525年 嘉靖四年乙酉，五十四岁，山阴。正月夫人诸芸卒，四月祔葬于徐山。应门人绍兴知府南大吉邀请为嶯山书院书院撰写《尊经阁记》，收录于《古文观止》。六月，礼部尚书席书力荐先生入阁，未果。九月，归余姚省祖茔，会门人于龙泉山中天阁，决定每月四次在中天阁授课。十月，建阳明书院于越城。<br />1526年 嘉靖五年丙戌，五十五岁，在绍兴系统讲授心学理论。十一月，继室张氏生子正聪，七年后，黄绾为保护孤幼收为婿，改名正亿。十二月为&ldquo;惜阴会&rdquo;作《惜阴说》。<br />1527年 嘉靖六年丁亥，五十六岁，山阴。五月命兼都察院左都御史，征广西思恩、田州。九月，出征广西思恩、田州。出发前夜，天泉桥上证道，与钱德洪、王畿立善恶四句教法，谓&ldquo;天泉证道&rdquo;。十二月，抵达广西梧州，开府议事。十二月命兼任两广巡抚。<br />1528年 戊子 嘉靖七年，五十七岁，在梧州。二月平定思田之乱，然后兴学校，抚新民。七月破八寨、断藤峡之乱。九月，冯恩奉钦赐至广州，赏思田之功。十月，病重，上疏请告，被桂萼压住。期间，拜谒伏波庙，祀增城先祖庙。<br />十一月，启程返家，二十九日辰时 (公元1529年1月9日8时)许，病逝于江西南安府大庾县青龙铺码头舟上，年五十七岁，门人周积等人陪伴，留下&ldquo;此心光明，亦复何言&rdquo;临终遗言。<br />1530年 嘉靖九年庚寅，十一月魂归浙江绍兴洪溪(今兰亭)，苍天为之哭泣。洪溪离越城三十里，为先生亲自选择。<br />1567年 隆庆元年丁卯，五月，下诏赠先生为新建候，谥文成，永为一代之宗臣，实耀千年之史册。<br />1584年 万历十二年甲申，先生从祀于孔庙，奉祀孔庙东庑第五十八位。&ldquo;立德、立功、立言&rdquo;，先生乃&ldquo;真三不朽&rdquo;。</p>\n<p>&nbsp;</p>', '王阳明年谱', null, 'inherit', null, null, null, '81101003052204043', 'chapter', '1533544727530094592', null, null, '0', '0', '0', '20', '1', '2021-10-29 09:19:27', '192.168.1.23', '1', '2021-11-02 14:56:39', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('81759221873033221', null, 'file', null, 'inherit', null, null, null, '81759135164186628', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '1', '2021-10-29 09:19:48', '192.168.1.23', '1', '2021-10-29 09:19:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('83293842246909960', '<p><strong><img style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"http://192.168.1.23:8088/store/202111/02150130L0dJbfGn.png\" width=\"435\" height=\"266\" /></strong></p>\n<p><strong>秦昭王48年， 公元前259&nbsp;年，1岁</strong><strong>。</strong>正月，嬴政生于赵都邯郸，取名赵政。</p>\n<p><strong>秦昭王50年，公元 前257&nbsp;年，3岁。</strong>秦国围攻邯郸，子楚（嬴政之父）与吕不韦合谋逃离赵国，返回咸阳。</p>\n<p><strong>秦昭襄王56年</strong><strong>，</strong><strong>公元前251年</strong><strong>，</strong><strong>9岁。</strong>始皇及母回秦国。</p>\n<p><strong>秦孝文王元年，公元前250&nbsp;年，10&nbsp;岁。</strong>安国君即位，子楚被立为太子，嬴政由赵归秦。</p>\n<p><strong>秦庄襄王元年，公元前249年，11岁。&nbsp;</strong>子楚即位，吕不韦任丞相，封文信侯，灭东周。</p>\n<p><strong>秦庄襄王2年，公元 前248年，12岁。</strong>秦将蒙骜攻克魏国高都、汲，占领赵国榆次、新城孟狼等37城 。</p>\n<p><strong>秦庄襄王3年，公元 前247年，13岁。</strong>5月庄襄王死，秦王政即位，骊山陵开始建设 。</p>\n<p><strong>秦王政元年，公元前246年，14&nbsp;岁。</strong>吕不韦以相国身份执政。庄襄王死,嬴政继秦王位,由太后处理朝政,尊吕不韦为相国.同年,秦击取晋阳,建成郑国渠.</p>\n<p><strong>秦王政</strong><strong>3</strong><strong>年</strong><strong>，</strong><strong>公元前244年</strong><strong>，</strong><strong>16岁。</strong>蒙骜击韩，攻取13城。</p>\n<p><strong>秦王政</strong><strong>5</strong><strong>年</strong><strong>，</strong><strong>公元前242年</strong><strong>，</strong><strong>18岁。</strong>蒙骜攻取魏酸枣二十城,初置东郡。</p>\n<p><strong>秦王政6年，公元 前241年，19岁 。</strong>《吕氏春秋》完成。魏,赵,韩,燕,楚五国联军攻秦。</p>\n<p><strong>秦王政9年，公元 前238年，22岁。</strong>赴雍举行加冕仪式，后宫事变，嫪毐一门被抄斩。秦王亲政，平定嫪毐之乱，灭其三族。</p>\n<p><strong>秦王政10年，公元 前237年，23&nbsp;岁。</strong>罢免吕不韦，嬴政亲政，李斯力谏，撤回逐客令。</p>\n<p><strong>秦王政</strong><strong>11</strong><strong>年</strong><strong>，</strong><strong>公元前236年</strong><strong>，</strong><strong>24岁。</strong>罢免吕不韦相位，逼迁蜀地。招揽人才，重用客卿。王翦攻齐，取九城。</p>\n<p><strong>秦王政12年，公元 前235年，25岁。&nbsp;</strong>吕不韦自杀。</p>\n<p><strong>秦王政13年，公元 前234&nbsp;年，26岁。</strong>秦军攻赵，韩两国。定平阳,宜安.韩派韩非来使,遭李斯毒死.</p>\n<p><strong>秦王政14年，公元 前233年，27&nbsp;岁。</strong>韩非出使秦国，李斯、姚贾陷害韩非，致使韩非于幽禁地云阳离宫自杀。</p>\n<p><strong>秦王政17年，公元 前230&nbsp;年，30岁。</strong>内史腾攻入韩都，俘虏韩王，韩灭。调兵遣将,开始统一大业.派内史腾率师灭韩。</p>\n<p><strong>秦王政18年，公元 前229年，31岁。&nbsp;</strong>秦军攻赵。</p>\n<p><strong>秦王政19年，公元 前228&nbsp;年，32岁。</strong>秦军攻破赵都邯郸，赵公子逃亡代郡。嬴政赴邯郸活埋太后一族亲属。赵灭。派王翦猛攻赵国,大破赵军,俘赵王迁,占领赵都邯郸。</p>\n<p><strong>秦王政20年，公元 前227年，&nbsp;33岁。</strong>燕太子丹派荆轲刺杀嬴政，失败。秦王在咸阳宫遇刺.杀荆轲,增兵赵地,派王翦进攻燕国。</p>\n<p><strong>秦王政21&nbsp;年，公元前226&nbsp;年，34岁。</strong>秦军攻入燕都，燕王逃亡辽东。秦军王贲进攻楚国。</p>\n<p><strong>秦王政22年，公元 前225年，&nbsp;35&nbsp;岁。</strong>灭魏。派王贲击魏，水灌大梁，消灭魏国。</p>\n<p><strong>秦王政23&nbsp;年，公元前224&nbsp;年，36&nbsp;岁。</strong>秦军准备总攻楚国。</p>\n<p><strong>秦王政24&nbsp;年，公元前223&nbsp;年，37岁。</strong>秦军攻陷楚都，俘虏楚王。派王翦,蒙武攻克楚都寿春,俘楚王负刍,楚国灭亡.</p>\n<p><strong>秦王政25年，公元 前222&nbsp;年，38岁。</strong>秦国吞并长江以南的楚国领土。辽东燕国余党被灭。派王贲攻占辽东，俘燕王喜，消灭燕国残余势力。</p>\n<p><strong>秦王政26年，公元 前221年，39岁。</strong>齐国降秦，齐灭。</p>\n<p>六国尽灭，统一天下。秦王号始皇帝，改&ldquo;命&rdquo;为&ldquo;制&rdquo;，&ldquo;令&rdquo;为&ldquo;诏&rdquo;，皇帝自称&ldquo;朕&rdquo;。全国设36郡，统一度量衡、文字、车轨和货币。没收天下兵器，迁12万户富豪于咸阳。</p>\n<p>灭掉齐国,吞并天下.秦王政改称始皇帝,统一各种制度。</p>\n<p><strong>秦始皇27年，公元 前220年，&nbsp;40&nbsp;岁。</strong>秦始皇到陇西、北地巡游，造信宫，修建自咸阳通往各地的驰道。</p>\n<p>第一次出巡,修驰道,完善道路系统,加强各地之间的联系.厉行车同轨,书同文,统一度量衡.</p>\n<p><strong>秦始皇28年，公元 前219&nbsp;年，41岁。</strong>秦始皇巡游天下，于泰山封禅、刻名立碑，派徐福往东海蓬莱求仙药。第二次出巡,巡视郡县,登封泰山.开凿灵渠.始建阿房宫.</p>\n<p><strong>秦始皇29年，公元 前218&nbsp;年，42&nbsp;岁。</strong>秦始皇东巡中，张良在博浪沙以巨石狙击，未遂。</p>\n<p>第三次出巡，遭张良率力士于博浪沙行刺，误中副车。天下各郡大索十天。</p>\n<p><strong>秦始皇31年，公元 前216&nbsp;年，44岁。</strong>秦始皇于咸阳遇袭。</p>\n<p>重申(重农抑商)政策，令(黔首自实田)。</p>\n<p><strong>秦始皇32年，公元 前215&nbsp;年，45&nbsp;岁。</strong>秦始皇赴碣石刻碑，蒙恬驱逐匈奴，开始修筑长城。</p>\n<p>派蒙恬北击匈奴，尽取河南之地。第四次出巡，巡视北边，自上郡回咸阳。</p>\n<p><strong>秦始皇33年，公元 前214&nbsp;年，46&nbsp;岁。</strong>秦朝征服南越，设立南海，桂林，象郡。</p>\n<p>平定南越，西瓯，移民戍边，修筑举世闻名的万里长城。</p>\n<p><strong>秦始皇34年，公元 前213年，&nbsp;47&nbsp;岁。</strong>李斯建议焚毁除秦记、医药学、占卜和农艺技术以外的其他书籍。</p>\n<p><strong>秦始皇35&nbsp;年，公元前212年，&nbsp;48&nbsp;岁。</strong>秦始皇开始营建阿房宫，坑杀儒生460余人，长子扶苏劝谏，被贬到上郡蒙恬军处。 筑九原通甘泉直道。</p>\n<p><strong>秦始皇36&nbsp;年，公元前211&nbsp;年，49岁。&nbsp;</strong>出现所谓&ldquo;祖龙将死&rdquo;的传闻，预示秦始皇的死 。</p>\n<p><strong>秦始皇37年，公元前210年，&nbsp;50岁。</strong>秦始皇携次子胡亥巡游天下，病死沙丘。赵高与李斯合谋逼死扶苏，拥立胡亥，葬秦始皇于骊山陵，蒙恬被迫自杀。</p>\n<p>第五次东巡，病死于沙丘。</p>', '秦始皇大事年表摘录', null, 'inherit', null, null, null, '80366982974324744', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-11-02 14:57:50', '192.168.1.23', '1', '2021-12-19 00:49:07', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('83294775278223366', null, 'file', null, 'inherit', null, null, null, '83293842246909960', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '1', '2021-11-02 15:01:32', '192.168.1.23', '1', '2021-11-02 15:01:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('83320962197143557', 'KPayCMS是内容付费系统，以本团队自研云应用支持平台WLDOS平台为基础框架延申开发，主要功能包括：信息发布、内容创作、内容发布、内容展示和内容管理等。本系统立足于强大的WLDOS平台，采用云原生技术，支持多租、多域和多应用开放架构，致力于用一套系统解决内容创作领域数据格式复杂、数据分散、工具缺乏、难以变现等问题，真正打造一款符合社会大众使用的通用、基础内容处理平台，以此为基础，进一步衍生多维生态，开创新的互联网经济模式，为社会大众创业提供一种可落地的解决方案。', 'KPayCMS内容付费系统的开发干货', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', '10', '1', '2021-11-02 16:45:35', '192.168.1.23', '1', '2021-12-19 19:32:45', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('83322023720960005', '<p>一、支持图文混排</p>\n<p><img src=\"http://192.168.1.23:8088/store/202111/02165547DRqRlzdc.jpg\" width=\"311\" height=\"415\" />这是从本地上传的一张图片，右键可以设置图片的链接、alt等相关信息。</p>\n<p>二、文本格式支持定制</p>\n<p>我们输入一段文本，然后定制不同的格式。</p>\n<p><strong>KPayCMS-内容付费平台，智慧创作的生产力工具</strong>。<span style=\"color: #3598db;\"><em>KPayCMS-内容付费平台，智慧创作的生产力工具</em></span>。<span style=\"color: #e03e2d;\">KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具</span>。<span style=\"text-decoration: underline;\">KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具</span>。<span style=\"background-color: #2dc26b;\">KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具。KPayCMS-内容付费平台，智慧创作的生产力工具</span>。</p>\n<p>三、支持上传视频</p>\n<p><video src=\"http://192.168.1.23:8088/store/202111/02170115S8GPgnaS.mp4\" controls=\"controls\" width=\"300\" height=\"150\"> </video>这是本地上传的一段视频，也可以直接引用网络上的视频源，只需设置引用的视频地址和视频展示尺寸即可。</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>', 'KPayCMS内容付费系统之内容创作', null, 'inherit', null, null, 'kpaycms', '83320962197143557', 'chapter', '1533544727530094592', null, null, '0', '2', '1', '30', '1', '2021-11-02 16:49:49', '192.168.1.23', '1', '2021-12-20 14:12:44', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('83323531053154305', null, 'file', null, 'inherit', null, null, null, '83322023720960005', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '1', '2021-11-02 16:55:48', '192.168.1.23', '1', '2021-11-02 16:55:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('93807978626072585', '121212', '12121212', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '100', '2021-12-01 15:17:15', '192.168.1.23', '100', '2021-12-01 15:17:15', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('93808020195819520', '<p>年谱的内容开始了。</p>\n<p>&nbsp;</p>', '这是章节内容一', null, 'inherit', null, null, null, '93807978626072585', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '100', '2021-12-01 15:17:25', '192.168.1.23', '100', '2021-12-01 15:17:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94428624192192519', '信息广播是指通过广播技术实现对信息的传播。在信息发布领域，信息广播是一种基本的，也是最早开始投入使用并延续至今的信息发布方式。从早期的广播电台、电视，到现在的互联网，广播依然是人们获取信息的主要来源渠道。\n早期的信息广播业务主要是音频、视频广播业务。随着社会发展和技术进步，数据广播成为基于数字电视传输标准之上，除MPEG-2视频和音频内容之外的另一个重要的业务扩展。数据广播包括经由卫星、电缆或地面设施下载软件，通过广播信道传输因特网服务、互动TV等。根据对数据传输的不同需求，数据广播标准确定了5种不同的应用领域。针对每一种应用领域，规范出了一种数据广播的封装格式。\n数据管道（Data Piping）规范支持数字电视系统中简单异步端到端的数据广播业务，数据直接在TS包的载荷中传输。数据流（Data Streaming）规范支持数字电视系统中面向流的端到端的数据广播业务，可以通过异步或同步的方式进行传输。异步数据流定义为流中只有数据，没有任何定时的需要。同步数据流定义为流中有数据和定时需要，并在接收机处可将数据和时钟重新生成为与发送端同步的数据流。多协议封装（Multiprotocol Encapsulation）规范支持数字电视系统中需要用通信协议中的数据报来传输的数据广播业务；数据轮播（Data Carousels）规范支持数字电视系统中周期性数据模块传输的数据广播业务；对象轮播（Object Carousel）规范用来支持数字电视系统中需要对DSM-CCU-U对象进行周期性广播的数据广播业务。\n信息广播是指通过广播技术实现对信息的传播。在信息发布领域，信息广播是一种基本的，也是最早开始投入使用并延续至今的信息发布方式。从早期的广播电台、电视，到现在的互联网，广播依然是人们获取信息的主要来源渠道。\n早期的信息广播业务主要是音频、视频广播业务。随着社会发展和技术进步，数据广播成为基于数字电视传输标准之上，除MPEG-2视频和音频内容之外的另一个重要的业务扩展。数据广播包括经由卫星、电缆或地面设施下载软件，通过广播信道传输因特网服务、互动TV等。根据对数据传输的不同需求，数据广播标准确定了5种不同的应用领域。针对每一种应用领域，规范出了一种数据广播的封装格式。\n数据管道（Data Piping）规范支持数字电视系统中简单异步端到端的数据广播业务，数据直接在TS包的载荷中传输。数据流（Data Streaming）规范支持数字电视系统中面向流的端到端的数据广播业务，可以通过异步或同步的方式进行传输。异步数据流定义为流中只有数据，没有任何定时的需要。同步数据流定义为流中有数据和定时需要，并在接收机处可将数据和时钟重新生成为与发送端同步的数据流。多协议封装（Multiprotocol Encapsulation）规范支持数字电视系统中需要用通信协议中的数据报来传输的数据广播业务；数据轮播（Data Carousels）规范支持数字电视系统中周期性数据模块传输的数据广播业务；对象轮播（Object Carousel）规范用来支持数字电视系统中需要对DSM-CCU-U对象进行周期性广播的数据广播业务。\n信息广播是指通过广播技术实现对信息的传播。在信息发布领域，信息广播是一种基本的，也是最早开始投入使用并延续至今的信息发布方式。从早期的广播电台、电视，到现在的互联网，广播依然是人们获取信息的主要来源渠道。', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '1508132284859596808', null, '0', '0', '0', null, '92829405966680072', '2021-12-03 08:23:28', '192.168.1.23', '92829405966680072', '2021-12-03 08:23:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94454789267177482', '1111', '测试一下作品管理', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '1508132284859596808', null, '0', '0', '0', '10', '92829405966680072', '2021-12-03 10:07:27', '192.168.1.23', '92829405966680072', '2021-12-03 10:07:27', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94454852508893185', '<p>测试完毕</p>\n<p>结果正确。</p>', '测试一下标题', null, 'inherit', null, null, null, '94454789267177482', 'chapter', '1533544727530094592', '1508132284859596808', null, '0', '0', '0', null, '92829405966680072', '2021-12-03 10:07:42', '192.168.1.23', '92829405966680072', '2021-12-03 10:08:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94823522682716168', '对于金融行业的ICP许可证是需要申请企业在相关的主管部门进行前置审批的，只有通过前置审批的金融行业才可以申请ICP许可证，其实不管是金融行业，还有医疗，教育，新闻出版，音乐，游戏还有广播类的都需要有前置审批。金融行业性质的网站大多都是以营利性质为主的，像网络贷款的网站业绩P2P理财网站，这些都需要申请ICP许可证，这些网站有具体在线交易的性质所以也必须要要申请办理EDI经营许可证。尤其是金融行业的网站，网页APP还有小程序这些都是需要有ICP备案，ICP许可证还有EDI经营许可证的。这也是企业合法合规的必要精力以及必有资质证书，如果没有那你企业就是属于违法经营，而且网站还具有很高的风险性，对自己对客户都没有信息安全保障！', '测试一下作品管理', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-04 10:32:40', '192.168.1.23', '100', '2021-12-04 10:32:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94847219032309763', '日期是没有关系的因为毕竟是新公司嘛1.应携带营业执照原件及复印件、组织机构代码证原件及复印件，有雇工的个体工商户携带个体工商户营业执照原件及复印件，并提供单位开户银行、账号、邮政编码、法人身份证号、法人联系电话、业务经办人及联系电话等相关材料信息，到注册地所属区、市社会保险经办机构办理。符合条件办理社会保险登记，发给社会保险登记证。日期是没有关系的因为毕竟是新公司嘛1.应携带营业执照原件及复印件、组织机构代码证原件及复印件，有雇工的个体工商户携带个体工商户营业执照原件及复印件，并提供单位开户银行、账号、邮政编码、法人身份证号、法人联系电话、业务经办人及联系电话等相关材料信息，到注册地所属区、市社会保险经办机构办理。符合条件办理社会保险登记，发给社会保险登记证。', '一个字两个字三个字一个字两字三个字一个字两个字三个字一个字两个字三个字一个字两个字三个字一个字两个字', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-04 12:06:49', '192.168.1.23', '100', '2021-12-13 21:36:33', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94847669290844168', '日期是没有关系的因为毕竟是新公司嘛1.应携带营业执照原件及复印件、组织机构代码证原件及复印件，有雇工的个体工商户携带个体工商户营业执照原件及复印件，并提供单位开户银行、账号、邮政编码、法人身份证号、法人联系电话、业务经办人及联系电话等相关材料信息，到注册地所属区、市社会保险经办机构办理。符合条件办理社会保险登记，发给社会保险登记证。日期是没有关系的因为毕竟是新公司嘛1.应携带营业执照原件及复印件、组织机构代码证原件及复印件，有雇工的个体工商户携带个体工商户营业执照原件及复印件，并提供单位开户银行、账号、邮政编码、法人身份证号、法人联系电话、业务经办人及联系电话等相关材料信息，到注册地所属区、市社会保险经办机构办理。符合条件办理社会保险登记，发给社会保险登记证。', '作品配置功能第4次', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-04 12:08:37', '192.168.1.23', '100', '2021-12-13 21:18:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('94993989058084875', null, '2021-12-04 21:50:01', null, 'inherit', null, null, null, '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-04 21:50:02', '192.168.1.23', '100', '2021-12-04 21:50:02', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95010184129855499', '<p><img src=\"http://192.168.1.23:8088/store/202112/06161456g3YiQTKp.png\" width=\"792\" height=\"615\" /></p>\n<p>图文+视频多种素材。</p>\n<p><video style=\"width: 774px; height: 387px;\" src=\"http://192.168.1.23:8088/store/202112/06161849bZwWrTht.mp4\" controls=\"controls\" width=\"300\" height=\"150\"> </video></p>\n<p>上传视频教程很容易。</p>\n<p>&nbsp;</p>', '测试标题测试标题测试标题测试标题测试标题测试标题', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-04 22:54:23', '192.168.1.23', '100', '2021-12-06 16:51:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95361647884550155', '<p>123，455 554 444</p>\n<p><img src=\"http://192.168.1.23:8088/store/202112/24112439wk5s5bFe.png\" width=\"1006\" height=\"371\" /></p>', '这是标题标题标题', null, 'offline', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:10:59', '192.168.1.23', '100', '2021-12-24 11:24:41', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95361651915276297', null, '2021-12-05 22:10:59', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:00', '192.168.1.23', '100', '2021-12-05 22:11:00', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95361652821245954', null, '2021-12-05 22:10:59', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:00', '192.168.1.23', '100', '2021-12-05 22:11:00', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95361655048421378', null, '2021-12-05 22:11:00', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:00', '192.168.1.23', '100', '2021-12-05 22:11:00', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95361657300762629', null, '2021-12-05 22:11:00', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:01', '192.168.1.23', '100', '2021-12-05 22:11:01', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95361657304956937', null, '2021-12-05 22:11:00', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:01', '192.168.1.23', '100', '2021-12-05 22:11:01', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95361657304956938', '', '删除测试', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:01', '192.168.1.23', '100', '2021-12-13 20:26:05', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95361657476923397', '', '标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-05 22:11:01', '192.168.1.23', '100', '2021-12-13 16:59:18', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('95562118209388549', '<p>标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题<br />标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题标题</p>', '这是内容的标题，这是标题', null, 'inherit', null, null, null, '94847219032309763', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-06 11:27:34', '192.168.1.23', '100', '2021-12-13 18:44:20', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95634448897982464', null, 'file', null, 'inherit', null, null, null, '95010184129855499', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '100', '2021-12-06 16:14:59', '192.168.1.23', '100', '2021-12-06 16:14:59', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('95634681434390530', null, 'file', null, 'inherit', null, null, null, '95010184129855499', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '100', '2021-12-06 16:15:55', '192.168.1.23', '100', '2021-12-06 16:15:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('96473821474373639', '王阳明(1472年10月31日亥时—1529年1月9日辰时)，汉族，幼名云，五岁更名守仁，字伯安。因曾筑室修道于会稽山阳明洞，自号阳明子，世称阳明先生。明朝浙江绍兴府余姚县(今属宁波余姚)人，伟大的思想家、哲学家、军事家、文学家、书法家、教育家，精通儒道佛三家，阳明心学宗师及集大成者。王守仁（心学集大成者）与孔子（儒学创始人）、孟子（儒学集大成者）、朱熹（理学集大成者）合称为孔孟朱王，公认为“立德、立功、立言”的真三不朽。阳明父亲是成化十七年(1481年)状元龙山公王华先生。\n弘治十二年（1499年）进士，历任刑部主事、贵州龙场驿丞、庐陵知县、右佥都御史、南赣巡抚、两广总督等职，晚年官至两京兵部尚书、都察院左都御史兼两广巡抚。因平定宁王朱宸濠之乱军功而被封为新建伯，隆庆年间追赠新建侯，谥文成，故又称王文成公。万历年间，从祀于孔庙，奉祀孔庙东庑第五十八位。\n阳明心学是明朝中后期影响最大的哲学思想，传至日本、朝鲜半岛、中国台湾及东南亚，弟子及仰慕者极众，包括明明了凡。阳明文章博大昌达，行墨间有俊爽之气，有《王阳明全集》，代表作《瘗旅文》、《大学问》、《象祠记》、《尊经阁记》和《传习录》。', '测试一下作品管理', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-08 23:50:21', '192.168.1.23', '100', '2021-12-08 23:50:21', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98240381033562114', null, '2021-12-13 20:50:02', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:50:02', '192.168.1.23', '100', '2021-12-13 20:50:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98240390416220171', null, '2021-12-13 20:50:04', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:50:04', '192.168.1.23', '100', '2021-12-13 20:50:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98240395973672962', null, '2021-12-13 20:50:05', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:50:06', '192.168.1.23', '100', '2021-12-13 20:50:06', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('98240401011032073', null, '2021-12-13 20:50:06', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:50:07', '192.168.1.23', '100', '2021-12-13 20:50:07', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('98240403997376517', null, '2021-12-13 20:50:07', null, 'inherit', null, null, null, '94847669290844168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:50:08', '192.168.1.23', '100', '2021-12-13 20:50:08', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('98241028642488321', '<p>这是正文.</p>', '测试标题', '', 'inherit', null, null, '123', '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:52:36', '192.168.1.23', '100', '2023-02-16 16:03:30', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98241033923117058', '<p>.......</p>', '2021-12-13 20:52:37', null, 'inherit', null, null, '2021-12-13 20:52:37', '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:52:38', '192.168.1.23', '100', '2023-02-16 16:03:36', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98241038029340679', null, '2021-12-13 20:52:38', null, 'inherit', null, null, null, '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:52:39', '192.168.1.23', '100', '2021-12-13 20:52:39', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98241041103765508', null, '2021-12-13 20:52:39', null, 'inherit', null, null, null, '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:52:39', '192.168.1.23', '100', '2021-12-13 20:52:39', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('98242517477801992', null, '2021-12-13 20:58:31', null, 'inherit', null, null, null, '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:58:31', '192.168.1.23', '100', '2021-12-13 20:58:31', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98242524377432073', null, '2021-12-13 20:58:33', null, 'inherit', null, null, null, '94823522682716168', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 20:58:33', '192.168.1.23', '100', '2021-12-13 20:58:33', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('98259928771903499', '在这个函数组件中，我们使用了 useEffect 来模拟了 class 组件的生命周期。我们知道，当 useEffect 没有第二个参数的时候，它模拟了 componentDidMount 和 componentDidUpdate，而此时 return 返回的函数，就不仅仅是模拟了 componentWillUnMount 的功能，因为 props 更新，它也会触发 componentDidUpdate。\n\n所以说，当模拟 componentDidMount 和 componentDidUpdate 的时候，返回的函数并不完全等同于 componentWillUnMount，它也会触发 componentDidUpdate。\n\n总结：\n\nuseEffect 依赖 []，组件销毁执行 fn，等同于componentWillUnMount\nuseEffect 无依赖或依赖[a,b]，组件更新时也会执行 fn，即下一次执行 useEffect 之前，就会执行 fn，无论更新还是卸载', '信息发布功能测试', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 22:07:43', '192.168.1.23', '100', '2021-12-13 22:11:52', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98260087136239626', '<p>目前已经支持多个分类关联。</p>\n<p>下一步实现标签功能，到时候信息、作品、内容都可以添加标签，进一步优化数据关联关系。</p>', '经测试信息发布功能正常', null, 'inherit', null, null, '', '98259928771903499', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '100', '2021-12-13 22:08:20', '192.168.1.23', '100', '2021-12-21 12:29:51', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('98536685760593923', null, 'file', null, 'inherit', null, null, null, '1544356320987758602', null, '1533544727530094592', null, 'image', '0', '0', '0', null, '1', '2021-12-14 16:27:27', '192.168.1.23', '1', '2021-12-14 16:27:27', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100046688501809161', null, '2021-12-18 20:27:39', null, 'inherit', null, null, null, '81101003052204043', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-18 20:27:39', '192.168.1.23', '1', '2021-12-18 20:27:39', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100077295961292809', '<p>作品的标签设置后，只有此后新建的内容才能继承到。分类，也是如此，如果过程中作品的分类发生了变更，只有新添加的内容能同步继承，这样必须做一个约束：作品与内容的分类以谁的为准，要不要做强关联。</p>\n<p>理论上讲，应以作品为准，当作品变更，内容同步更新，不过由于作品内容较多，不建议作强关联，首先，内容的分类应该继承作品，这是模型确定的结果，而标签则无此限制，标签仅是反映了当下内容的信息特征，可以脱离业务边界而设置。</p>', '看一下标签是否已经和作品一样设置了', null, 'inherit', null, null, null, '81101003052204043', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2021-12-18 22:29:17', '192.168.1.23', '1', '2021-12-18 22:35:44', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100685022098014208', null, '2021-12-20 14:44:09', null, 'inherit', null, null, null, '1541768862404100102', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 14:44:10', '192.168.1.23', '1', '2021-12-20 14:44:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100685088955219977', null, '2021-12-20 14:44:25', null, 'inherit', null, null, null, '1541768862404100102', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 14:44:26', '192.168.1.23', '1', '2021-12-20 14:44:26', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100716409064701963', '1、广大而周遍的籍録是谱之范式。本义：依照事物的类别、系统制的表册。\n2、同本义 [table;chart;book]\n谱，牒也。――《广雅》\n谱，籍录也。――《说文解字》。朱骏声曰：“桓君山云，太史公三代世表，旁行斜上，普效周谱，是谱之名起于周代也。”\n十二曰谱系，以纪世施继序。――《旧唐书·经籍志上》\n3、又如：食谱；歌谱；谱学（研究氏族或宗族世系的学科）；谱录（用谱表的方式记录世系）；家谱；棋谱\n4、符号或文字列记乐歌的音节、声调，以便奏唱的籍录 [music;music score]\n自历代至于本朝，雅乐皆先制乐章而后成谱，崇宁以后乃先制谱后命词。――《宋史·乐志》\n5、又如：简谱；五线谱；工尺谱；乐谱\n6、大致的把握 [a considerable degree of assurance;a fair amount of confidence]。如：做事有谱儿\n7、将放射物或波的诸分量按某种可变特征（如波长、质量或能量）分开并排列而成的一种系列 [spectrum]。如：光谱；能谱 [6] \n8、按照事物的类别或系统编排记录 [excerpt and edit;take notes]\n谱，布也。布列见其事也，亦曰绪也，主绪人世类相继如统绪也。――《释名》\n自殷以前诸侯不可得而谱。――《史记·三代世表》\n谱汝诸孙中。――清·全祖望《梅花岭记》\n9、又如：谱列（按照事物的系统和类别编排）；谱注（记叙记载）；谱状（记载家族及历史的书籍）\n10、写曲 [compose music;set to music]。如：把这首诗词谱成歌曲；这首歌是谁谱的曲', '测试一下作品管理', null, 'publish', null, null, 'ceshiyixiazuopinguanli', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 16:48:53', '192.168.1.23', '1', '2021-12-20 16:53:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100717380033495049', '<p>不报错了吧</p>\n<p>&nbsp;</p>', '测试一下标题', null, 'inherit', null, null, '', '100716409064701963', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 16:52:45', '192.168.1.23', '1', '2021-12-21 12:39:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100717505548042250', null, '2021-12-20 16:53:14', null, 'inherit', null, null, '', '100716409064701963', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 16:53:15', '192.168.1.23', '1', '2021-12-20 20:28:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100784898597109765', '5个常用Java 代码混淆器 助你保护你的代码\nhttp://news.csdn.net/a/20090731/212891.html \n关键词：Java  | 感谢ydj9931的提供 | \n【CSDN 7月23日消息】从事Java编程的人都知道，可以通过逆向工程反编译得到Java程序的源代码，这种反编译工具之一就是JAD。因此，为保护我们的劳动 成果，尽可能给反编译人员制造障碍，我们可以使用Java Obfuscator（Java混淆器）保护Java的类文件。\n\nJava Obfuscator的原理就是将字节码转换为一个逻辑上的对等物，这种转换后的版本极难拆散。即使有人试图去反编译，过程将极其艰难复杂，并很难绕过转换后模糊晦涩的编码。主要的过程如下：\n\n用一个常规编译器（比如JDK）编译Java源代码运行混淆器，在受保护的环境下生成编译类文件。最后生成的会是一个不同的输出文档，也许扩展名也会不同。\n\n这个被重命名为.class file的文件在功能上与原字节码是对等的，由于虚拟机仍然可以对其进行解译，因此对性能不会产生影响。\n\n以下是一些可用来混淆（obfuscate）Java字节码的工具：\n\nZelix KlassMaster\n\nZelix KlassMaster是一款用Java写的实用工具，能读取和修改Java类文件，可以运行在任何支持1.1.6版Java虚拟机的平台上。\n下载：http://www.zdnetindia.com/downloads/info/898255.html\n\nCinnabar Canner\n\nCanner通过创建一个原生Windows可执行文件（EXE文件）保护你的代码不被逆向工程反编译，这个可执行文件包含了你的应用程序类和资源的全部加密版本，只有在被JVM调用到内存中时才处于非加密状态。', '测试信息发布-分类选择', null, 'publish', null, null, 'ceshixinxifabu-fenleixuanze', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 21:21:02', '192.168.1.23', '1', '2021-12-20 21:21:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100785354299850760', '<p>内容和作品有一致的分类和标签。1</p>', '测试信息发布分类目录', null, 'inherit', null, null, null, '100784898597109765', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2021-12-20 21:22:51', '192.168.1.23', '1', '2021-12-23 11:55:16', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100792307554107401', null, '2021-12-20 21:50:28', null, 'publish', null, null, null, '0', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-20 21:50:29', '192.168.1.23', '1', '2021-12-20 21:50:29', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100792492258672646', null, '2021-12-20 21:51:12', null, 'publish', null, null, null, '0', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-20 21:51:13', '192.168.1.23', '1', '2021-12-20 21:51:13', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100795744148701191', null, '2021-12-20 22:04:08', null, 'publish', null, null, null, '0', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-20 22:04:08', '192.168.1.23', '1', '2021-12-20 22:04:08', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100824163745972224', '刚开始我是在脑海里构思，我的想法很多，但是能完整形成一个故事的很少。要么就是只想好了题目，想不到剧情。要么就是想好了大概的内容，却连自己都打动不了，没意思。\n\n于是我灵机一动，尝试先自言自语说出来。想到什么说什么，想到哪里说哪里。\n\n我先是告诉自己一个大概的背景，然后再开始尝试解释为什么我会出现在这里，我看见了、遇见了、察觉了什么，用来反推更细节的逻辑。\n\n当出现其他人物时，我开始尝试用自问自答的方式回答。用不同的角色去解释“我”的疑惑，让它看起来很怪，但是很合理、很有趣。\n\n我很快就陷入了自我沉浸般的体验里，一边听歌，一边不断挖坑填坑。\n\n这种感觉实在是太上头，导致我不知觉就走了1.5小时，脚都走痛了才开始有了自我意识。\n\n这是我第一次产生这种感觉，终于明白了那些真正热爱自己做的事情的人，为什么能茶不思饭不想的琢磨这件事。原来当一个人深度沉浸在自己的世界里的时候，对物质世界的感知就会变弱。\n\n在回家的途中，我感觉自己被治愈了。我感受到了很强烈的爱、安全感、归属感，它们都来自于我创作的那个空间。最重要的是，它们时刻储存在我的大脑的某个地方，可以随时随地的等待我重新开始读取进度。\n\n这一刻，我仿佛已不需要更多的人出现在我的生命中，我也不再渴望还有哪个人能给我带来快乐，包容我、理解我，和我一起幸福生活。\n\n因为我无意间还创造出了超级完美的另一个人，我都不敢想象。我们非常近距离的接触、相处，见过这般耀眼的人物，我还如何能按照以前的期盼继续生活。\n\n作者：雾太大WUTAIDA\n链接：https://www.jianshu.com/p/1ffc1e1a8021\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。', '测试新建作品-工作台', null, 'publish', null, null, 'ceshixinjianzuopin-gongzuotai', '0', 'book', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-20 23:57:04', '192.168.1.23', '1', '2021-12-21 00:02:12', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100826521129369604', '刚开始我是在脑海里构思，我的想法很多，但是能完整形成一个故事的很少。要么就是只想好了题目，想不到剧情。要么就是想好了大概的内容，却连自己都打动不了，没意思。\n\n于是我灵机一动，尝试先自言自语说出来。想到什么说什么，想到哪里说哪里。\n\n我先是告诉自己一个大概的背景，然后再开始尝试解释为什么我会出现在这里，我看见了、遇见了、察觉了什么，用来反推更细节的逻辑。\n\n当出现其他人物时，我开始尝试用自问自答的方式回答。用不同的角色去解释“我”的疑惑，让它看起来很怪，但是很合理、很有趣。\n\n我很快就陷入了自我沉浸般的体验里，一边听歌，一边不断挖坑填坑。\n\n这种感觉实在是太上头，导致我不知觉就走了1.5小时，脚都走痛了才开始有了自我意识。\n\n这是我第一次产生这种感觉，终于明白了那些真正热爱自己做的事情的人，为什么能茶不思饭不想的琢磨这件事。原来当一个人深度沉浸在自己的世界里的时候，对物质世界的感知就会变弱。\n\n在回家的途中，我感觉自己被治愈了。我感受到了很强烈的爱、安全感、归属感，它们都来自于我创作的那个空间。最重要的是，它们时刻储存在我的大脑的某个地方，可以随时随地的等待我重新开始读取进度。\n\n这一刻，我仿佛已不需要更多的人出现在我的生命中，我也不再渴望还有哪个人能给我带来快乐，包容我、理解我，和我一起幸福生活。\n\n因为我无意间还创造出了超级完美的另一个人，我都不敢想象。我们非常近距离的接触、相处，见过这般耀眼的人物，我还如何能按照以前的期盼继续生活。\n\n作者：雾太大WUTAIDA\n链接：https://www.jianshu.com/p/1ffc1e1a8021\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。', '测试新建作品-工作台1', null, 'publish', null, null, 'ceshixinjianzuopin-gongzuotai1', '0', 'book', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:06:26', '192.168.1.23', '1', '2021-12-21 00:06:26', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100831393706852359', '刚开始我是在脑海里构思，我的想法很多，但是能完整形成一个故事的很少。要么就是只想好了题目，想不到剧情。要么就是想好了大概的内容，却连自己都打动不了，没意思。\n\n于是我灵机一动，尝试先自言自语说出来。想到什么说什么，想到哪里说哪里。\n\n我先是告诉自己一个大概的背景，然后再开始尝试解释为什么我会出现在这里，我看见了、遇见了、察觉了什么，用来反推更细节的逻辑。\n\n当出现其他人物时，我开始尝试用自问自答的方式回答。用不同的角色去解释“我”的疑惑，让它看起来很怪，但是很合理、很有趣。\n\n我很快就陷入了自我沉浸般的体验里，一边听歌，一边不断挖坑填坑。\n\n这种感觉实在是太上头，导致我不知觉就走了1.5小时，脚都走痛了才开始有了自我意识。\n\n这是我第一次产生这种感觉，终于明白了那些真正热爱自己做的事情的人，为什么能茶不思饭不想的琢磨这件事。原来当一个人深度沉浸在自己的世界里的时候，对物质世界的感知就会变弱。\n\n在回家的途中，我感觉自己被治愈了。我感受到了很强烈的爱、安全感、归属感，它们都来自于我创作的那个空间。最重要的是，它们时刻储存在我的大脑的某个地方，可以随时随地的等待我重新开始读取进度。\n\n这一刻，我仿佛已不需要更多的人出现在我的生命中，我也不再渴望还有哪个人能给我带来快乐，包容我、理解我，和我一起幸福生活。\n\n因为我无意间还创造出了超级完美的另一个人，我都不敢想象。我们非常近距离的接触、相处，见过这般耀眼的人物，我还如何能按照以前的期盼继续生活。\n\n作者：雾太大WUTAIDA\n链接：https://www.jianshu.com/p/1ffc1e1a8021\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。', '测试新建作品-工作台2', null, 'publish', null, null, 'ceshixinjianzuopin-gongzuotai2', '0', 'book', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:25:48', '192.168.1.23', '1', '2021-12-21 00:25:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100832820072202250', null, '2021-12-21 00:31:27', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:31:28', '192.168.1.23', '1', '2021-12-21 00:31:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100832825126338562', null, '2021-12-21 00:31:28', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:31:29', '192.168.1.23', '1', '2021-12-21 00:31:29', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100832836350296067', null, '2021-12-21 00:31:31', null, 'inherit', null, null, null, '100824163745972224', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:31:32', '192.168.1.23', '1', '2021-12-21 00:31:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100832839705739270', null, '2021-12-21 00:31:32', null, 'inherit', null, null, null, '100824163745972224', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:31:32', '192.168.1.23', '1', '2021-12-21 00:31:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100832842033577987', null, '2021-12-21 00:31:32', null, 'inherit', null, null, null, '100824163745972224', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:31:33', '192.168.1.23', '1', '2021-12-21 00:31:33', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833128928165888', null, '2021-12-21 00:32:41', null, 'inherit', null, null, null, '100824163745972224', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:41', '192.168.1.23', '1', '2021-12-21 00:32:41', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833145684410378', null, '2021-12-21 00:32:45', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:45', '192.168.1.23', '1', '2021-12-21 00:32:45', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833150159732736', null, '2021-12-21 00:32:46', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:46', '192.168.1.23', '1', '2021-12-21 00:32:46', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833157101305857', null, '2021-12-21 00:32:48', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:48', '192.168.1.23', '1', '2021-12-21 00:32:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833158049218560', null, '2021-12-21 00:32:48', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:48', '192.168.1.23', '1', '2021-12-21 00:32:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833160083456005', null, '2021-12-21 00:32:48', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:49', '192.168.1.23', '1', '2021-12-21 00:32:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833160087650311', null, '2021-12-21 00:32:48', null, 'inherit', null, null, null, '100826521129369604', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:32:49', '192.168.1.23', '1', '2021-12-21 00:32:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833289259630596', null, '2021-12-21 00:33:19', null, 'inherit', null, null, null, '100831393706852359', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:33:20', '192.168.1.23', '1', '2021-12-21 00:33:20', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100833296096346117', null, '2021-12-21 00:33:21', null, 'inherit', null, null, null, '100831393706852359', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:33:21', '192.168.1.23', '1', '2021-12-21 00:33:21', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100837501867573253', '刚开始我是在脑海里构思，我的想法很多，但是能完整形成一个故事的很少。要么就是只想好了题目，想不到剧情。要么就是想好了大概的内容，却连自己都打动不了，没意思。\n\n于是我灵机一动，尝试先自言自语说出来。想到什么说什么，想到哪里说哪里。\n\n我先是告诉自己一个大概的背景，然后再开始尝试解释为什么我会出现在这里，我看见了、遇见了、察觉了什么，用来反推更细节的逻辑。\n\n当出现其他人物时，我开始尝试用自问自答的方式回答。用不同的角色去解释“我”的疑惑，让它看起来很怪，但是很合理、很有趣。\n\n我很快就陷入了自我沉浸般的体验里，一边听歌，一边不断挖坑填坑。\n\n这种感觉实在是太上头，导致我不知觉就走了1.5小时，脚都走痛了才开始有了自我意识。\n\n这是我第一次产生这种感觉，终于明白了那些真正热爱自己做的事情的人，为什么能茶不思饭不想的琢磨这件事。原来当一个人深度沉浸在自己的世界里的时候，对物质世界的感知就会变弱。\n\n在回家的途中，我感觉自己被治愈了。我感受到了很强烈的爱、安全感、归属感，它们都来自于我创作的那个空间。最重要的是，它们时刻储存在我的大脑的某个地方，可以随时随地的等待我重新开始读取进度。\n\n这一刻，我仿佛已不需要更多的人出现在我的生命中，我也不再渴望还有哪个人能给我带来快乐，包容我、理解我，和我一起幸福生活。\n\n因为我无意间还创造出了超级完美的另一个人，我都不敢想象。我们非常近距离的接触、相处，见过这般耀眼的人物，我还如何能按照以前的期盼继续生活。\n\n作者：雾太大WUTAIDA\n链接：https://www.jianshu.com/p/1ffc1e1a8021\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。', '测试新建作品-工作台3', null, 'publish', null, null, 'ceshixinjianzuopin-gongzuotai3', '0', 'book', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 00:50:04', '192.168.1.23', '1', '2021-12-21 00:50:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('100849044428603392', '<p>这是跨域隔离的一篇文章。</p>', '测试跨域隔离', null, 'inherit', null, null, '', '100837501867573253', 'chapter', '93037725495246854', '0', null, '0', '0', '0', null, '1', '2021-12-21 01:35:56', '192.168.1.23', '1', '2021-12-21 01:37:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('102084367846391813', null, 'file', null, 'inherit', null, null, null, '95361647884550155', null, '1533544727530094592', '0', 'image', '0', '0', '0', null, '100', '2021-12-24 11:24:40', '192.168.1.23', '100', '2021-12-24 11:24:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('108315676512010250', '测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集', '测试作品新增', null, 'publish', null, null, 'ceshizuopinxinzeng', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2022-01-10 16:05:40', '192.168.1.23', '1', '2023-01-24 00:04:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('108321427372556297', '首先要明白多域的作用是给云服务商提供的，企业在多种领域存在业务，不同领域的业务上云在线运营的时候肯定不能混在一起，传统解决方案是不同业务部门搭建各自的业务系统，形成了众多业务烟囱系统，这就是“业务孤岛”，非常不利于企业的业务资源共享，比如客户、供应商资源。为了共享企业资源实现共赢，不少企业开始考虑建设“业务中台”，现实是整合已经形成业务孤岛的烟囱系统非一朝一夕之功，有一种系统可以提供分领域的运营方式，企业各方可以在这种系统上共建互惠，各自业务相对独立又互通有无，时间久了，自然就是“业务中台”了，这就是多域系统的作用。\n\nwldos平台支持多域，而且是独立域名的多域隔离，由于涉及到域名解析和静态资源服务器的转发规则支持，如果想以云服务的形式给大量用户(或租户)应用这套功能，需要服务器搭建专门的网络服务支持。\n\n如果只是想用一个网站实例部署多个域名，可用apache或nginx配置多份虚拟主机规则，然后在系统后台-领域管理-多域管理下配置上需要的每个独立域名即可，这样，就拥有了一套Java系统支撑的多域站群。', '测试作品新增', null, 'publish', null, null, '', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-01-10 16:28:31', '192.168.1.23', '1', '2022-12-10 16:01:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('108322899132858375', '<p><img src=\"http://192.168.1.23:8088/store/202201/22131730EnjO34yN.png\" /></p>', '测试tag是否正常显式的用例', null, 'inherit', null, null, 'ceshitagshifouzhengchangxianshideyongli', '108321427372556297', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-01-10 16:34:22', '192.168.1.23', '1', '2022-12-10 16:10:26', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('108324277305655298', '<p>首先要明白多域的作用是给云服务商提供的，企业在多种领域存在业务，不同领域的业务上云在线运营的时候肯定不能混在一起，传统解决方案是不同业务部门搭建各自的业务系统，形成了众多业务烟囱系统，这就是&ldquo;业务孤岛&rdquo;，非常不利于企业的业务资源共享，比如客户、供应商资源。为了共享企业资源实现共赢，不少企业开始考虑建设&ldquo;业务中台&rdquo;，现实是整合已经形成业务孤岛的烟囱系统非一朝一夕之功，有一种系统可以提供分领域的运营方式，企业各方可以在这种系统上共建互惠，各自业务相对独立又互通有无，时间久了，自然就是&ldquo;业务中台&rdquo;了，这就是多域系统的作用。</p>\n<p>wldos平台支持多域，而且是独立域名的多域隔离，由于涉及到域名解析和静态资源服务器的转发规则支持，如果想以云服务的形式给大量用户(或租户)应用这套功能，需要服务器搭建专门的网络服务支持。</p>\n<p>如果只是想用一个网站实例部署多个域名，可用apache或nginx配置多份虚拟主机规则，然后在系统后台-领域管理-多域管理下配置上需要的每个独立域名即可，这样，就拥有了一套Java系统支撑的多域站群。</p>', '多域的作用是给云服务商提供', null, 'inherit', null, null, 'duoyudezuoyongshijiyunfuwushangtigong', '108315676512010250', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-01-10 16:39:50', '192.168.1.23', '1', '2023-01-24 00:02:43', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('108325789582934025', '<p>首先要明白多域的作用是给云服务商提供的，企业在多种领域存在业务，不同领域的业务上云在线运营的时候肯定不能混在一起，传统解决方案是不同业务部门搭建各自的业务系统，形成了众多业务烟囱系统，这就是&ldquo;业务孤岛&rdquo;，非常不利于企业的业务资源共享，比如客户、供应商资源。为了共享企业资源实现共赢，不少企业开始考虑建设&ldquo;业务中台&rdquo;，现实是整合已经形成业务孤岛的烟囱系统非一朝一夕之功，有一种系统可以提供分领域的运营方式，企业各方可以在这种系统上共建互惠，各自业务相对独立又互通有无，时间久了，自然就是&ldquo;业务中台&rdquo;了，这就是多域系统的作用。 wldos平台支持多域，而且是独立域名的多域隔离，由于涉及到域名解析和静态资源服务器的转发规则支持，如果想以云服务的形式给大量用户(或租户)应用这套功能，需要服务器搭建专门的网络服务支持。 如果只是想用一个网站实例部署多个域名，可用apache或nginx配置多份虚拟主机规则，然后在系统后台-领域管理-多域管理下配置上需要的每个独立域名即可，这样，就拥有了一套Java系统支撑的多域站群。</p>', '测试信息发布', null, 'publish', null, null, 'ceshixinxifabu', '0', 'info', '1533544727530094592', '0', null, '0', '30', '0', '50', '1', '2022-01-10 16:45:51', '192.168.1.23', '1', '2023-02-03 23:49:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('112622016239681542', null, 'file', null, 'inherit', null, null, null, '108322899132858375', null, '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2022-01-22 13:17:31', '192.168.1.23', '1', '2022-01-22 13:17:31', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('125294558309957640', '测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性测试信息发布易用性。', '测试信息发布易用性', null, 'publish', null, null, 'ceshixinxifabuyiyongxing', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2022-02-26 12:33:41', '192.168.1.23', '1', '2022-02-26 12:33:41', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150293641017606149', '技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布技术推广测试信息发布', '技术推广测试信息发布', null, 'publish', null, null, 'jishutuiguangceshixinxifabu', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-05-06 12:11:06', '192.168.1.23', '1', '2022-05-06 12:11:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150295498133782530', '我们认为，React 是用 JavaScript 构建快速响应的大型 Web 应用程序的首选方式。它在 Facebook 和 Instagram 上表现优秀。\n\nReact 最棒的部分之一是引导我们思考如何构建一个应用。在这篇文档中，我们将会通过 React 构建一个可搜索的产品数据表格来更深刻地领会 React 哲学。', 'React基础-组件化开发的哲学', null, 'publish', null, null, 'Reactjichu-zujianhuakaifadezhexue', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-05-06 12:18:29', '192.168.1.23', '1', '2022-05-06 12:18:29', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150297118515052555', 'React 有十分强大的组合模式。我们推荐使用组合而非继承来实现组件间的代码重用。\n在这篇文档中，我们将考虑初学 React 的开发人员使用继承时经常会遇到的一些问题，并展示如何通过组合思想来解决这些问题。\n包含关系\n有些组件无法提前知晓它们子组件的具体内容。在 Sidebar（侧边栏）和 Dialog（对话框）等展现通用容器（box）的组件中特别容易遇到这种情况。', 'React基础-组合组件代替组件继承', null, 'publish', null, null, 'Reactjichu-zuhezujiandaitizujianjicheng', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-05-06 12:24:55', '192.168.1.23', '1', '2022-05-06 12:24:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150300213835907079', '通常，多个组件需要反映相同的变化数据，这时我们建议将共享状态提升到最近的共同父组件中去，然后子组件通过props获取该数据或操作。让我们看看它是如何运作的。\n在本节中，我们将创建一个用于计算水在给定温度下是否会沸腾的温度计算器。\n我们将从一个名为 BoilingVerdict 的组件开始，它接受 celsius 温度作为一个 prop，并据此打印出该温度是否足以将水煮沸的结果。', 'React基础-状态提升', null, 'publish', null, null, 'Reactjichu-zhuangtaitisheng', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2022-05-06 12:37:13', '192.168.1.23', '1', '2022-05-06 12:37:13', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150300613628575749', '由于在表单元素上设置了 value 属性，因此显示的值将始终为 this.state.value，这使得 React 的 state 成为唯一数据源。由于 handlechange 在每次按键时都会执行并更新 React 的 state，因此显示的值将随着用户输入而更新。\n对于受控组件来说，输入的值始终由 React 的 state 驱动。你也可以将 value 传递给其他 UI 元素，或者通过其他事件处理函数重置，但这意味着你需要编写更多的代码。', 'React基础-表单React基础-表单', null, 'publish', null, null, 'Reactjichu-biaodanReactjichu-biaodan', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2022-05-06 12:38:49', '192.168.1.23', '1', '2022-05-06 12:38:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150301236507885576', '<p>如果列表项目的顺序可能会变化，我们不建议使用索引来用作 key 值，因为这样做会导致性能变差，还可能引起组件状态的问题。可以看看 Robin Pokorny 的深度解析使用索引作为 key 的负面影响这一篇文章。如果你选择不指定显式的 key 值，那么 React 将默认使用索引用作为列表项目的 key 值。 要是你有兴趣了解更多的话，这里有一篇文章深入解析为什么 key 是必须的可以参考。</p>', 'React基础-列表和key', null, 'publish', null, null, 'Reactjichu-liebiaohekey', '0', 'info', '1533544727530094592', '0', null, '0', '2', '1', null, '1', '2022-05-06 12:41:17', '192.168.1.23', '1', '2023-02-04 20:49:34', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150301922083651586', '<p>WLDOS是一款面向互联网的云应用软件支撑平台，致力于云管端，基于多域架构，支持多租、多应用的SaaS系统软件，本站基于WLDOS搭建，新功能持续开放中。 WLDOS云应用支撑平台，基于springboot二次封装的轻量级快速开发框架，SaaS应用架构，后期支持脱离springboot独立运行。默认支持多域(站)系统，也可以单站模式运行，默认支持多租户运行模式，同时支持单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构，具体部署方式不作强定义，自行规划。</p>', 'WLDOS云应用支撑平台简介', null, 'publish', null, null, 'WLDOSyunyingyongzhichengpingtaijianjie', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2022-05-06 12:44:01', '192.168.1.23', '1', '2023-02-15 16:13:42', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('150341155934617611', '<p>中台是相较于前台、后台而产生的一个概念，业务中台以企业为服务对象，整合后台资源，转换为方便前台使用的功能，可以说，业务中台实现了后台资源到前台服务的转化，可以方便企业，快速挖掘、响应、引领用户需求。 　　业务中台是构建构建全渠道一体化运营管理的平台，它将数字技术运用到企业各个领域，改变运用和管理方式，让企业可以更好地为客户创造价值。</p>', '互联网时代，企业如何借助业务中台迎战市场？', null, 'publish', null, null, 'hulianwangshidai，qiyeruhejiezhuyewuzhongtaiyingzhanshichang？', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', '20', '1', '2022-05-06 15:19:55', '192.168.1.23', '1', '2023-02-04 01:16:38', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('157641603456614408', '<p>二十四节气，是历法中表示自然节律变化以及确立&ldquo;十二月建&rdquo;的特定节令。一岁四时，春夏秋冬各三个月，每月两个节气，每个节气均有其独特的含义。廿四节气准确的反映了自然节律变化，在人们日常生活中发挥了极为重要的作用。它不仅是指导农耕生产的时节体系，更是包含有丰富民俗事象的民俗系统。廿四节气蕴含着悠久的文化内涵和历史积淀，是中华民族悠久历史文化的重要组成部分。 [1-2] [12] &ldquo;二十四节气&rdquo;是上古农耕文明的产物，它是上古先民顺应农时，通过观察天体运行，认知一岁中时令、气候、物候等变化规律所形成的知识体系。廿四节气最初是依据斗转星移制定，北斗七星循环旋转，斗柄顺时针旋转一圈为一周期，谓之一&ldquo;岁&rdquo;（摄提）。现行的&ldquo;二十四节气&rdquo;是依据太阳在回归黄道上的位置制定，即把太阳周年运动轨迹划分为24等份，每15&deg;为1等份，每1等份为一个节气，始于立春，终于大寒。 [3-4]</p>', '技术推广测试信息发布改为文章', null, 'publish', null, null, 'jishutuiguangceshixinxifabu', '0', 'post', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2022-05-26 18:49:17', '192.168.1.23', '1', '2023-01-31 06:03:07', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247261088727089159', '<p>测试可信者发布是否直接发布，无需审核!</p>', '测试可信者', null, 'inherit', null, null, 'ceshi', '108321427372556297', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-01-29 02:05:07', '192.168.1.23', '1', '2023-01-29 02:05:44', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247261867479318531', '测试可信者发布作品无需审核测试可信者发布作品无需审核测试可信者发布作品无需审核测试可信者发布作品无需审核测试可信者发布作品无需审核测试可信者发布作品无需审核', '测试可信者发布作品无需审核', null, 'publish', null, null, 'ceshikexinzhefabuzuopinwuxushenhe', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', '20', '1', '2023-01-29 02:08:13', '192.168.1.23', '1', '2023-01-31 06:09:14', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247471524206526466', '<ol>\n<li>这是一条信息。</li>\n<li>3</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li>&nbsp;</li>\n<li><!-- pagebreak --></li>\n</ol>', '测试标签的别名重复问题', null, 'inherit', null, null, 'ceshibi', '247261867479318531', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-01-29 16:01:19', '192.168.1.23', '1', '2023-02-17 19:20:41', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247589103533211650', '<p>测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇测试上一篇下一篇。</p>', '测试上一篇下一篇', null, 'publish', null, null, 'ceshifabuxinxi', '0', 'post', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2023-01-29 23:48:32', '192.168.1.23', '1', '2023-02-03 23:58:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247615834260422660', '测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集测试作品新增合集', '测试可信者发布作品无需审核', null, 'publish', null, null, 'ceshikexinzhefabuzuopinwuxushenhe1', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-01-30 01:34:45', '192.168.1.23', '1', '2023-01-30 02:02:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('247643764122370050', '<p>你好，干啥呢。</p>', '测试自动保存', null, 'inherit', null, null, '2023-01-30 03:25:44', '247615834260422660', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-01-30 03:25:44', '192.168.1.23', '1', '2023-02-04 00:54:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('248045421826064392', '<p>121212</p>', '2023-01-31 06:01:47', null, 'inherit', null, null, '2023-01-31 06:01:47', '247261867479318531', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-01-31 06:01:47', '192.168.1.23', '1', '2023-02-04 01:23:49', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('248227360134578178', '<p>测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名测试文章别名</p>\n<p>这是一片文章。一片好看的文章，你的才华不该被埋没，是金子总能发光。</p>', '测试文章别名', null, 'publish', null, null, 'ceshibieming', '0', 'post', '1533544727530094592', '0', null, '0', '0', '0', '40', '1', '2023-01-31 18:04:44', '192.168.1.23', '1', '2023-02-03 18:32:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249437170188337159', '<p>121</p>', '测试行业门类去掉后正常', null, 'publish', null, null, 'ceshixingyemenleiqudiaohouzhengchang', '0', 'post', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2023-02-04 02:12:06', '192.168.1.23', '1', '2023-02-04 02:12:14', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249439401633562627', '<p>测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11测试行业门类去掉后正常11</p>', '测试行业门类去掉后正常11', null, 'publish', null, null, '测试行业门类去掉后正常11', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-04 02:20:58', '192.168.1.23', '1', '2023-02-04 20:38:34', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('249734249511043078', '明白WLDOS是什么，解决了什么问题，开发者能做什么，开发者如何最快上手，wldos-framework框架简明介绍，wldos平台多模块介绍，第一个模块开发demo。', 'WLDOS开发文档', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', '0', null, '0', '2', '1', '40', '1', '2023-02-04 21:52:35', '192.168.1.23', '1', '2023-02-04 21:52:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249735731727745026', '<p><strong>WLDOS平台</strong></p>\n<p>元宇宙融合虚拟与现实的理念与作者所想不谋而合，WLDOS（音：汉[\'wou\'da\'si]，World Operating System）为了这种目标孵化，是可以平台化和边缘化的云物互联支撑系统，面向社区开源、线上服务和业态孵化而生。<br /><br />WLDOS&reg;寓意：面向世界的系统，面向未来的系统，我的系统。</p>\n<p>解决痛点：想利用网络化拓展业务，对如何拓展业务有困惑，需要一个可落地的软件搭建平台，同时不需要昂贵的容器化基础设施。</p>\n<p>两条腿走路：WLDOS云应用支撑平台 + 内容付费业务场景实现。</p>\n<p><strong>功能说明</strong></p>\n<p>WLDOS是个软件家族，目前由开发框架、支撑平台和内容付费三大板块构成，其中框架和支撑平台是通用支撑，内容付费是基于通用支撑展开的最佳实践。<br /><br />输出两个项目：WLDOS云应用支撑平台（管理端）和WLDOS内容付费系统（业务端）。应用功能结构如下：</p>\n<p><img src=\"http://localhost:8088/store/2023/02/04220259mYTP9WQ3.jpg\" width=\"1124\" height=\"1616\" /></p>\n<p><img src=\"http://localhost:8088/store/2023/02/04220539WcQsiwDw.jpg\" width=\"982\" height=\"777\" /></p>\n<p><strong>技术说明</strong></p>\n<p>语言：Java8、ReactJs17。 框架：springboot2.4.6（支持升级到2.5）。<br />ORM：spring-data-jdbc2.1.9，连接池采用boot自带hikari。<br />前端：ReactJs17，AntD ProV4.5。<br />中间件：tomcat9（支持换成其他）。<br />辅助：自带cache、自带JWT、自带文件服务。 兼容性：后端jdk1.8，前端IE11+、Google Chrome、Edge等。</p>\n<p>应用架构：前后端分离，前端ReactJs，后端springMVC(2.0计划推出webflux架构版)，JWT认证，无状态。</p>\n<p><strong>1.0核心功能：系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。</strong></p>\n<p><strong>2.0规划功能：服务集成与开放能力、插件扩展管理能力、类serviceMesh Ad-hoc架构支撑能力。</strong></p>\n<p><strong>3.0规划功能：软件工厂、云物互联支撑能力、智能建模与机器人系统。</strong></p>', 'WLDOS是什么', '这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要这是摘要', 'inherit', null, null, 'W', '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', '20', '1', '2023-02-04 21:58:28', '192.168.1.23', '1', '2023-02-17 05:18:04', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249736875816435719', null, 'file', null, 'inherit', null, null, null, '249735731727745026', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-04 22:03:01', '192.168.1.23', '1', '2023-02-04 22:03:01', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249737542039683081', null, 'file', null, 'inherit', null, null, null, '249735731727745026', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-04 22:05:40', '192.168.1.23', '1', '2023-02-04 22:05:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778098358894594', '<p>121</p>', '2023-02-05 00:46:49', null, 'inherit', null, null, 'test001', '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:49', '192.168.1.23', '1', '2023-02-17 18:41:14', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778108618162185', null, '2023-02-05 00:46:51', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:52', '192.168.1.23', '1', '2023-02-05 00:46:52', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('249778109956145155', null, '2023-02-05 00:46:52', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:52', '192.168.1.23', '1', '2023-02-05 00:46:52', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778112263012362', null, '2023-02-05 00:46:52', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:53', '192.168.1.23', '1', '2023-02-05 00:46:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778113269645312', null, '2023-02-05 00:46:52', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:53', '192.168.1.23', '1', '2023-02-05 00:46:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778114439856138', null, '2023-02-05 00:46:53', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:53', '192.168.1.23', '1', '2023-02-05 00:46:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778115450683394', null, '2023-02-05 00:46:53', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:53', '192.168.1.23', '1', '2023-02-05 00:46:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778116625088523', null, '2023-02-05 00:46:53', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:54', '192.168.1.23', '1', '2023-02-05 00:46:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778117040324619', null, '2023-02-05 00:46:53', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:54', '192.168.1.23', '1', '2023-02-05 00:46:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778117904351239', null, '2023-02-05 00:46:53', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:54', '192.168.1.23', '1', '2023-02-05 00:46:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778118831292422', null, '2023-02-05 00:46:54', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:54', '192.168.1.23', '1', '2023-02-05 00:46:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778119670153227', null, '2023-02-05 00:46:54', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:54', '192.168.1.23', '1', '2023-02-05 00:46:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778121020719109', null, '2023-02-05 00:46:54', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:55', '192.168.1.23', '1', '2023-02-05 00:46:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778122023157765', null, '2023-02-05 00:46:54', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:55', '192.168.1.23', '1', '2023-02-05 00:46:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778122664886282', null, '2023-02-05 00:46:55', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:55', '192.168.1.23', '1', '2023-02-05 00:46:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778123646353417', null, '2023-02-05 00:46:55', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:55', '192.168.1.23', '1', '2023-02-05 00:46:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778125173080067', null, '2023-02-05 00:46:55', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:56', '192.168.1.23', '1', '2023-02-05 00:46:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778126251016202', null, '2023-02-05 00:46:55', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:56', '192.168.1.23', '1', '2023-02-05 00:46:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778127463170055', null, '2023-02-05 00:46:56', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:56', '192.168.1.23', '1', '2023-02-05 00:46:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778128327196674', null, '2023-02-05 00:46:56', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:56', '192.168.1.23', '1', '2023-02-05 00:46:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778129346412544', null, '2023-02-05 00:46:56', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:57', '192.168.1.23', '1', '2023-02-05 00:46:57', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778130441125894', null, '2023-02-05 00:46:56', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:57', '192.168.1.23', '1', '2023-02-05 00:46:57', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778131347095560', null, '2023-02-05 00:46:57', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:57', '192.168.1.23', '1', '2023-02-05 00:46:57', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778132257259520', null, '2023-02-05 00:46:57', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:57', '192.168.1.23', '1', '2023-02-05 00:46:57', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778133171617796', null, '2023-02-05 00:46:57', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:58', '192.168.1.23', '1', '2023-02-05 00:46:58', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778134123724800', null, '2023-02-05 00:46:57', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:58', '192.168.1.23', '1', '2023-02-05 00:46:58', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('249778135042277383', null, '2023-02-05 00:46:58', null, 'inherit', null, null, null, '249734249511043078', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-05 00:46:58', '192.168.1.23', '1', '2023-02-05 00:46:58', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('252542677308588037', '<p>121sdssadasasd</p>', '测试信息发布是否正常', '撒旦发射点撒发射点发射点撒旦发射点撒旦飞洒地方', 'publish', null, null, 'ceshiinfo', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', '10', '1', '2023-02-12 15:52:16', '192.168.1.23', '1', '2023-02-17 17:12:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('252924035583361031', null, '2023-02-13 17:07:39', null, 'inherit', null, null, null, '108315676512010250', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-13 17:07:39', '192.168.1.23', '1', '2023-02-13 17:07:39', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254140838116507657', '<p>信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑。</p>\n<p>这个信息涉及的美图如下：</p>\n<p><img src=\"http://localhost:8000/store/2023/02/17014501F5kvgzNi.jpg\" /></p>', '测试信息富文本编辑器', null, 'publish', null, null, 'ceshixinxifuwenbenbianjiqi', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 01:42:48', '127.0.0.1', '1', '2023-02-17 01:45:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254141403579990027', null, 'file', null, 'inherit', null, null, null, '254140838116507657', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 01:45:02', '127.0.0.1', '1', '2023-02-17 01:45:02', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254180658352144392', '<p>这是内容。</p>\n<p><img src=\"http://localhost:8000/store/2023/02/17052220fYwW4BpE.jpg\" width=\"1170\" height=\"658\" /></p>', '测试信息富文本编辑器', '信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑。', 'publish', null, null, 'ceshixinxifuwenbenbianjiqi1111', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:21:01', '127.0.0.1', '1', '2023-02-17 05:22:27', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254182050299035648', '<p>123123</p>', '测试信息富文本编辑器', '信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑。', 'publish', null, null, 'ceshixinxifuwenbenbianjiqi11111', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:26:33', '127.0.0.1', '1', '2023-02-17 17:12:04', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254182115797286920', null, 'file', null, 'inherit', null, null, null, '254182050299035648', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 04:26:49', '127.0.0.1', '1', '2023-02-17 04:26:49', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254182155659952128', '<p>去问我去我去饿</p>', '测试信息富文本编辑器', '信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑。', 'publish', null, null, 'ceshixinxifuwenbenbianjiqi1', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:26:58', '127.0.0.1', '1', '2023-02-17 16:30:38', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254183939329671176', '<p>3</p>', '测试信息富文本编辑器', '信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑信息也可以富文本编辑。', 'publish', null, null, 'ceshixinxifuwenbenbianjiqi11', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:34:04', '127.0.0.1', '1', '2023-02-17 18:40:42', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254183978571579403', null, 'file', null, 'inherit', null, null, null, '254183939329671176', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 04:34:13', '127.0.0.1', '1', '2023-02-17 04:34:13', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254184545666646016', null, 'file', null, 'inherit', null, null, null, '254183939329671176', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 04:36:28', '127.0.0.1', '1', '2023-02-17 04:36:28', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254188052377419785', '<p>2</p>', '测试信息富文本编辑器', null, 'publish', null, null, 'ceshixinxifuwenbenbianjiqi1111111', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:50:24', '127.0.0.1', '1', '2023-02-17 18:40:39', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254188087181754377', null, 'file', null, 'inherit', null, null, null, '254188052377419785', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 04:50:33', '127.0.0.1', '1', '2023-02-17 04:50:33', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254189937775788040', '<p>1</p>\n<p><img src=\"http://localhost:8000/store/2023/02/17195902QvJDtorO.jpg\" /></p>', '测试信息富文本编辑器', null, 'publish', null, null, 'ceshixinxifuwenbenbianjiqi111111', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 04:57:54', '127.0.0.1', '1', '2023-02-17 19:59:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254189987146940419', null, 'file', null, 'inherit', null, null, null, '254189937775788040', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 04:58:06', '127.0.0.1', '1', '2023-02-17 04:58:06', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254190801680777216', null, 'file', null, 'inherit', null, null, null, '254189937775788040', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 05:01:20', '127.0.0.1', '1', '2023-02-17 05:01:20', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254193575990575110', null, '测试信息富文本编辑器', null, 'publish', null, null, 'ceshixinxifuwenbenbianjiqi111', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 05:12:21', '127.0.0.1', '1', '2023-02-17 05:20:48', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254196093080485894', null, 'file', null, 'inherit', null, null, null, '254180658352144392', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 05:22:21', '127.0.0.1', '1', '2023-02-17 05:22:21', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254398094150123522', null, '2023-02-17 18:45:02', null, 'inherit', null, null, null, '247615834260422660', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 18:45:02', '127.0.0.1', '1', '2023-02-17 18:45:02', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254411500802457611', '<p><img src=\"http://localhost:8000/store/2023/02/17201634Y58N5lLn.jpg\" /></p>', '测试信息发布', '', 'publish', null, null, '', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 19:38:19', '127.0.0.1', '1', '2023-02-17 20:17:10', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254411660689326086', null, 'file', null, 'inherit', null, null, null, '254411500802457611', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 19:38:57', '127.0.0.1', '1', '2023-02-17 19:38:57', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254416724518682630', null, 'file', null, 'inherit', null, null, null, '254189937775788040', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 19:59:04', '127.0.0.1', '1', '2023-02-17 19:59:04', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254421138742624264', null, 'file', null, 'inherit', null, null, null, '254411500802457611', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 20:16:36', '127.0.0.1', '1', '2023-02-17 20:16:36', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254422333699833866', null, '测试信息发布', null, 'publish', null, null, null, '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 20:21:21', '127.0.0.1', '1', '2023-02-17 20:21:21', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254424854338125830', '', '测试信息发布', null, 'publish', null, null, 'ceshixinxifabu1', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 20:31:22', '127.0.0.1', '1', '2023-02-17 20:47:16', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('254429302993108996', '<p><img src=\"http://localhost:8000/store/2023/02/17205229uPlwJ9zT.jpg\" width=\"200\" height=\"158\" /></p>\n<p>&nbsp;</p>\n<p>这是详情。</p>', '测试信息发布', null, 'publish', null, null, '', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-02-17 20:49:03', '127.0.0.1', '1', '2023-02-17 20:53:04', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254429704866152448', null, 'file', null, 'inherit', null, null, null, '254429302993108996', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 20:50:39', '127.0.0.1', '1', '2023-02-17 20:50:39', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('254430174745640968', null, 'file', null, 'inherit', null, null, null, '254429302993108996', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-02-17 20:52:31', '127.0.0.1', '1', '2023-02-17 20:52:31', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519009285308858375', '<p>你好n21</p>', '2021-06-23 16:00:351224你好1我们1223吃了没223', null, null, null, null, null, '25', null, '1533544727530094592', null, null, '0', '0', '0', null, '0', '2021-06-23 16:00:35', '127.0.0.1', '1', '2021-06-24 10:02:09', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519009335221075974', '<p>干点什么呢，有时候会不知道感受。</p><p>下面我们就看一下吧。</p><p>效果不错啊。</p><p>这是什么神能力。</p><p><br data-mce-bogus=\"1\"></p>', '2021-06-23 16:00:47', null, null, null, null, null, '25', null, '1533544727530094592', null, null, '0', '0', '0', null, '0', '2021-06-23 16:00:47', '127.0.0.1', '1', '2021-06-24 10:46:00', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519009343114756104', '<p>121334你好11</p>', '2021-06-23 16:00:4漂亮一下', null, 'in_review', null, null, null, '25', null, '1533544727530094592', null, null, '0', '0', '0', null, '0', '2021-06-23 16:00:49', '127.0.0.1', '1', '2021-06-25 16:13:54', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519010893476315141', '<p>123213</p>', '2021-06-23 16:06:581', null, 'offline', null, null, null, '25', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-23 16:06:59', '127.0.0.1', '1', '2021-06-24 10:31:00', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519352828959309835', '<p>sdfsdf&nbsp;</p>', '2021-06-24 14:45:421121', null, 'offline', null, null, null, '2', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-24 14:45:43', '127.0.0.1', '1', '2021-06-24 17:38:13', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519703804027256838', '<p>标题不错啊。文章也不错。</p><p>这里是编辑区。超文本编辑，开源支持多媒体格式。上传附件试一下。</p>', '测试一下标题', null, 'inherit', null, null, null, '25', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-25 14:00:22', '127.0.0.1', '1', '2021-06-25 14:14:54', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1519760188735406090', '<p>WLDOS®</p><p>你是一条小狗。<a title=\"百度\" href=\"baidu.com\" data-mce-href=\"baidu.com\">baidu.com</a></p><p><br></p><p><br></p><table style=\"border-collapse: collapse; width: 100%;\" border=\"1\" data-mce-style=\"border-collapse: collapse; width: 100%;\"><tbody><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\">售价</td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\">库存</td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\">日期</td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\">操作人</td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\">备注</td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr><tr><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23609%;\" data-mce-style=\"width: 9.23609%;\"><br></td><td style=\"width: 9.23956%;\" data-mce-style=\"width: 9.23956%;\"><br></td></tr></tbody></table>', '2021 8岁-吃了没', null, 'offline', null, null, null, '1', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-25 17:44:25', '127.0.0.1', '1', '2021-06-25 17:48:18', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1520570574275526667', null, '1', null, null, null, null, null, '0', null, '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-27 23:24:36', '127.0.0.1', '1', '2021-06-27 23:24:36', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1520797778628493318', null, '2021-06-23 16:00:49123123撒旦发射点s的发射点发射点士大夫', null, null, null, null, null, '0', null, '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-28 14:27:25', '127.0.0.1', '1', '2021-06-28 14:27:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1520798657561673728', null, '2021-06-23 16:00:49123123撒旦发射点s的发射点发射点士大夫', null, null, null, null, null, '0', null, '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-28 14:30:55', '127.0.0.1', '1', '2021-06-28 14:30:55', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1520933242983333892', null, '轩辕年谱之凤鸣岐山', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-28 23:25:43', '127.0.0.1', '1', '2021-06-28 23:25:43', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521140839510360067', null, '我测试个作品', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:10:38', '127.0.0.1', '1', '2021-06-29 13:10:38', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521148593432084485', null, '2021-06-29 13:41:26', null, 'offline', null, null, null, '1521140839510360067', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:41:26', '127.0.0.1', '1', '2021-06-29 13:41:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150078001790986', '<p>你好，在干嘛？</p><p>吃饭了吗。</p><p>你好，小熊。</p><p><br data-mce-bogus=\"1\"></p>', '2021-06-29 13:47:20', null, 'offline', null, null, null, '1521140839510360067', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:47:20', '127.0.0.1', '1', '2021-06-29 13:47:41', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150609617240074', null, '创业年谱集锦', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:49:27', '127.0.0.1', '1', '2021-06-29 13:49:27', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150645444984842', '<p>创业经历：1234343434。</p>', '2021创业开始', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:49:35', '127.0.0.1', '1', '2021-06-29 13:50:00', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150826722803715', null, '2021-06-29 13:50:18', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:19', '127.0.0.1', '1', '2021-06-29 13:50:19', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150831143600129', null, '2021-06-29 13:50:19', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:20', '127.0.0.1', '1', '2021-06-29 13:50:20', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150832150233099', '<p>24567778</p>', '2021-06-29 13:50:1912', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:20', '127.0.0.1', '1', '2021-06-29 15:02:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150834394185729', null, '2021-06-29 13:50:20', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:21', '127.0.0.1', '1', '2021-06-29 13:50:21', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150836642332680', null, '2021-06-29 13:50:21', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:21', '127.0.0.1', '1', '2021-06-29 13:50:21', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150837560885252', null, '2021-06-29 13:50:21', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:21', '127.0.0.1', '1', '2021-06-29 13:50:21', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150837791571973', '<p>手动</p>', '2021-06-29 13:50:21', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:21', '127.0.0.1', '1', '2021-06-29 14:47:38', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150839842586633', null, '2021-06-29 13:50:21', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:22', '127.0.0.1', '1', '2021-06-29 13:50:22', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150840597561344', null, '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:22', '127.0.0.1', '1', '2021-06-29 13:50:22', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150841105072138', null, '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:22', '127.0.0.1', '1', '2021-06-29 13:50:22', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150841218318342', '<p>是</p>', '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:22', '127.0.0.1', '1', '2021-06-29 14:34:34', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150842325614592', '<p>士大夫但是</p>', '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:22', '127.0.0.1', '1', '2021-06-29 14:23:44', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150843135115267', '<p>请问请问</p>', '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:23', '127.0.0.1', '1', '2021-06-29 14:22:53', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150844045279241', null, '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:23', '127.0.0.1', '1', '2021-06-29 13:50:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150844724756487', '<p>sdf</p><p>sd</p><p>sdf</p><p>sdf</p><p>s</p><p>df</p><p>sdf</p><p>sd</p><p>f</p><p>sdf</p><p>sd</p><p>f</p><p>sd</p><p>f</p><p>sd</p><p>fsd</p><p>f</p><p>sd</p><p>fs</p><p>df</p><p>sd</p><p>fs</p><p>df</p><p>sdf</p><p>sd</p><p>f</p><p>sd</p><p>fs</p><p>df</p><p>sd</p><p>fssdf</p><p><br data-mce-bogus=\"1\"></p><p>f</p><p>sdf</p><p><br data-mce-bogus=\"1\"></p>', '2021-06-29 13:50:22', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:23', '127.0.0.1', '1', '2021-06-29 14:05:37', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150845530062856', null, '2021-06-29 13:50:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:23', '127.0.0.1', '1', '2021-06-29 13:50:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150846230511624', null, '2021-06-29 13:50:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:23', '127.0.0.1', '1', '2021-06-29 13:50:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150847191007242', null, '2021-06-29 13:50:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150847258116102', null, '2021-06-29 13:50:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150848172474370', null, '2021-06-29 13:50:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150848977780742', null, '2021-06-29 13:50:24', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150849879556102', null, '2021-06-29 13:50:24', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150850517090304', null, '2021-06-29 13:50:24', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:24', '127.0.0.1', '1', '2021-06-29 13:50:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150851565666305', null, '2021-06-29 13:50:24', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:25', '127.0.0.1', '1', '2021-06-29 13:50:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150852559716360', '<p><br data-mce-bogus=\"1\"></p>', '2021-06-29 13:50:24', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:25', '127.0.0.1', '1', '2021-06-29 13:51:46', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150853243387907', null, '2021-06-29 13:50:25', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:25', '127.0.0.1', '1', '2021-06-29 13:50:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150855256653832', null, '2021-06-29 13:50:25', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:26', '127.0.0.1', '1', '2021-06-29 13:50:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150856124874760', null, '2021-06-29 13:50:25', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:26', '127.0.0.1', '1', '2021-06-29 13:50:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150856313618434', null, '2021-06-29 13:50:25', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:26', '127.0.0.1', '1', '2021-06-29 13:50:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150858377216007', null, '2021-06-29 13:50:26', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:26', '127.0.0.1', '1', '2021-06-29 13:50:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150859161550858', null, '2021-06-29 13:50:26', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:26', '127.0.0.1', '1', '2021-06-29 13:50:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150859492900874', '<p>sdfsdfsdfsdfsdfds</p>', '2021-06-29 13:50:26', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:27', '127.0.0.1', '1', '2021-06-29 14:03:39', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150912743784450', null, '2021-06-29 13:50:39', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:39', '127.0.0.1', '1', '2021-06-29 13:50:39', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150920251588609', null, '2021-06-29 13:50:41', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:41', '127.0.0.1', '1', '2021-06-29 13:50:41', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150923653169162', null, '2021-06-29 13:50:41', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:42', '127.0.0.1', '1', '2021-06-29 13:50:42', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521150928455647239', null, '2021-06-29 13:50:42', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:50:43', '127.0.0.1', '1', '2021-06-29 13:50:43', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521151097062473737', null, '2021-06-29 13:51:23', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:51:23', '127.0.0.1', '1', '2021-06-29 13:51:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521151106805841927', null, '2021-06-29 13:51:25', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:51:25', '127.0.0.1', '1', '2021-06-29 13:51:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521151112963080199', null, '2021-06-29 13:51:26', null, 'inherit', null, null, null, '1521150609617240074', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 13:51:27', '127.0.0.1', '1', '2021-06-29 13:51:27', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521168871583301633', null, '1213312', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 15:02:01', '127.0.0.1', '1', '2021-06-29 15:02:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521168895650217988', null, '2021-06-29 15:02:06', null, 'inherit', null, null, null, '1521168871583301633', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 15:02:07', '127.0.0.1', '1', '2021-06-29 15:02:07', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521168901677432839', null, '2021-06-29 15:02:08', null, 'inherit', null, null, null, '1521168871583301633', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 15:02:08', '127.0.0.1', '1', '2021-06-29 15:02:08', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521175523606839304', null, '1312312312', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1521171388547383299', '2021-06-29 15:28:27', '192.168.1.23', '1521171388547383299', '2021-06-29 15:28:27', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521175556540514304', null, '2021-06-29 15:28:34', null, 'inherit', null, null, null, '1521175523606839304', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1521171388547383299', '2021-06-29 15:28:35', '192.168.1.23', '1521171388547383299', '2021-06-29 15:28:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521175571082166282', '<p>你好</p><p>赶上呢</p><p>蓝色东风科技</p><p>个 关怀</p>', '2021-06-29生日纪念', null, 'inherit', null, null, null, '1521175523606839304', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1521171388547383299', '2021-06-29 15:28:38', '192.168.1.23', '1521171388547383299', '2021-06-29 15:30:14', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521202151401635845', null, '年谱是个人最好的品牌载体', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 17:14:15', '192.168.1.23', '1', '2021-06-29 17:14:15', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521202243919593483', '<p><img src=\"http://192.168.1.23:8088/store/202106/30183100e2AG6LVK.\" alt=\"\" width=\"229\" height=\"246\" /></p>\n<p>时年1987年，你2岁了。时年8月。士大夫。我们不知道是什么，但是我们知道他们的电话。有时候我常常想。我们试一下。不自动保存，就没事。</p>\n<p>然后，你说，他们不可以。我们知道那是什么。我们不知道是什么。但我们也知道。</p>\n<p>你可能不知道，那是什么意义。但是，我知道你什么都懂。</p>\n<p>这该死的光标总数，在最前面出现。士大夫</p>\n<p>这光标怎么样了。你猜。你好弄啥来，我不知道。</p>\n<p>我们发现，光标。手动保存试一下。</p>\n<p>我们部长的。<img style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"http://192.168.1.23:8088/store/202106/30183805TV0Cs23n.jpg\" alt=\"gouzi\" width=\"398\" height=\"257\" /></p>\n<p>在试一下。你好，在干啥。资治通鉴共10卷。??点击[http://pinyin.cn/e381491]查看表情ᥬ?᭄????</p>\n<p>我们知道，在中国。我们在感受你知道吗，你吃饭了吗。你是一条够。试一下。123.</p>\n<p>&nbsp;</p>\n<p>woshi.</p>', '1986年 1岁', null, 'inherit', null, null, null, '1521202151401635845', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 17:14:38', '192.168.1.23', '1', '2021-06-30 21:37:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521202459829780484', '<p>时年1987年，你2岁了。时年8月。士大夫。我们不知道是什么，但是我们知道他们的电话。有时候我常常想。我们试一下。不自动保存，就没事。</p>\n<p>然后，你说，他们不可以。我们知道那是什么。我们不知道是什么。但我们也知道。</p>\n<p>你可能不知道，那是什么意义。但是，我知道你什么都懂。</p>\n<p>这该死的光标总数，在最前面出现。士大夫</p>\n<p>这光标怎么样了。你猜。你好弄啥来，我不知道。</p>\n<p>我们发现，光标。手动保存试一下。</p>\n<p>我们部长的。</p>\n<p>在试一下。</p>\n<p>ni kan.</p>\n<p>撒大大是</p>\n<p>时年1987年，你2岁了。时年8月。士大夫。我们不知道是什么，但是我们知道他们的电话。有时候我常常想。我们试一下。不自动保存，就没事。</p>\n<p>然后，你说，他们不可以。我们知道那是什么。我们不知道是什么。但我们也知道。</p>\n<p>你可能不知道，那是什么意义。但是，我知道你什么都懂。</p>\n<p>这该死的光标总数，在最前面出现。士大夫</p>\n<p>这光标怎么样了。你猜。你好弄啥来，我不知道。</p>\n<p>我们发现，光标。手动保存试一下。</p>\n<p>我们部长的。士大夫</p>\n<p>在试一是。士大夫是楼上的是</p>\n<p>从来没有这样一个好看的。1987年2岁。你说。你好，我看一下。欧克！http://192.168.1.23:8000/space/book/1521202151401635845/chapter/1521202459829780484</p>\n<p>&nbsp;</p>', '1987年 2岁地方手动', null, 'inherit', null, null, null, '1521202151401635845', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 17:15:29', '192.168.1.23', '1', '2021-06-30 13:57:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521249146870874121', '<blockquote>\n<p>手动。士大夫士大夫但是Google。<a title=\"百度\" href=\"https://www.baidu.com\" target=\"_blank\" rel=\"noopener\">https://www.baidu.com</a></p>\n</blockquote>', '2021-06-29 20:21:00', null, 'inherit', null, null, null, '1521202151401635845', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-29 20:21:00', '192.168.1.23', '1', '2021-06-30 15:03:03', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521455522322759681', null, '鲁迅年谱', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 10:01:04', '192.168.1.23', '1', '2021-06-30 10:01:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521455551368314886', null, '2021-06-30 10:01:10', null, 'inherit', null, null, null, '1521455522322759681', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 10:01:11', '192.168.1.23', '1', '2021-06-30 10:01:11', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521588799557779463', null, '小龟的成长日记', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 18:50:40', '192.168.1.23', '1', '2021-06-30 18:50:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521588846055833611', null, '2021-06-30 18:50:50', null, 'inherit', null, null, null, '1521588799557779463', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 18:50:51', '192.168.1.23', '1', '2021-06-30 18:50:51', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521589407232737289', null, '2021-06-30 18:53:04', null, 'inherit', null, null, null, '1521202151401635845', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 18:53:04', '192.168.1.23', '1', '2021-06-30 18:53:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521630976207929350', null, '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 21:38:15', '192.168.1.23', '1', '2021-06-30 21:38:15', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521631010525724675', '', '2021-06-30 21:38:23', null, 'inherit', null, null, null, '1521630976207929350', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 21:38:23', '192.168.1.23', '1', '2021-07-05 16:45:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521631894928277513', null, '知识图谱收集资料', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 21:41:54', '192.168.1.23', '1', '2021-06-30 21:41:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521631932786065410', '<p>功能，我测试一下视频上传功能。</p>\n<p>现在在手机端。</p>\n<p>&nbsp;</p>', '2021-06-30 21:42:03', null, 'inherit', null, null, null, '1521631894928277513', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 21:42:03', '192.168.1.23', '1', '2021-06-30 22:06:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521636090356350977', null, '上传视频教程', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-06-30 21:58:35', '192.168.1.23', '1', '2021-06-30 21:58:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521834554293927942', null, '轩辕年谱', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-01 11:07:12', '192.168.1.23', '1', '2021-07-01 11:07:12', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521834591077974021', '', '知识付费即内容付费——知识对象', null, 'inherit', null, null, null, '1521834554293927942', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-01 11:07:21', '192.168.1.23', '1', '2021-07-05 16:45:43', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1521989344030277641', '这是一个非常好的系统。', '2021年建党100周年测试程序', null, 'offline', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-01 21:22:17', '192.168.1.23', '1', '2021-07-01 21:22:17', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522009400529305604', '<p><img src=\"http://192.168.1.23:8088/store/202107/012242152TknQ1J6.jpg\" width=\"372\" height=\"209\" /></p>\n<p>这是八达岭长城。</p>', '2021-07-01 22:41:58', null, 'inherit', null, null, null, '1521989344030277641', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-01 22:41:59', '192.168.1.23', '1', '2021-07-01 22:43:00', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522186897946492931', '这是一个殿堂级的技术盛宴，是真正能够推动人类生产力进步的利器，这是对信息科技的顶级加持，是全行业的龙头。', '计算机年谱', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 10:27:17', '192.168.1.23', '1', '2021-07-02 10:27:17', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522187456745226244', '<p><img src=\"http://192.168.1.23:8088/store/202107/02103111g8DQkpG6.jpg\" /></p>\n<p><strong>信息革命</strong></p>\n<p><span style=\"color: #2dc26b;\">信息技术自人类社会形成以来就存在，并随着科学技术的进步而不断变革。语言、文字是人类传达信息的初步方式，烽火台则是远距离传达信息的最简单手段</span>。纸张和印刷术使信息流通范围大大扩展。自19世纪中期以后，人类学会利用电和电磁波以来，信息技术的变革大大加快。电报、电话、收音机、电视机的发明使人类的信息交流与传递快速而有效。第二次世界大战以后，半导体、集成电路、计算机的发明，数字通信、卫星通信的发展形成了新兴的电子信息技术，使人类利用信息的手段发生了质的飞跃。具体讲，<strong>人类</strong>不仅能在全球任何两个有相应设施的地点之间准确地交换信息，还可利用机器收集、加工、处理、控制、存储信息。机器开始取代了人的部分脑力劳动，扩大和延伸了人的思维、神经和感官的功能，使人们可以从事更富有创造性的劳动。这是前所未有的变革，是人类在改造自然中的一次新的飞跃。</p>\n<p><br /><span style=\"text-decoration: underline; color: #3598db;\">信息技术革命不仅为人类提供了新的生产手段，带来了生产力的大发展和组织管理方式的变化，还引起了产业结构和经济结构的变化。这些变化将进一步引起人们价值观念、社会意识的变化，从而社会结构和政治体制也将随之而变。例如计算机的推广普及促进了工厂自动化、办公自动化和家庭自动化，形成所谓&ldquo;3A&rdquo;革命。计算机和通信技术融合形成的信息通信网推动了经济的国际化。金融界组成的全球金融信息网使资金可以克服时差，在一昼夜间经全球流通而大大增值。跨国公司已控制着很大部分的生产与国际贸易。同时，这种系统还扩展了人们受教育的机会，使更多的人可以从事更富创造性的劳动。信息的广泛流通促进了权力分散化、决策民主化。随着人们教育水平的提高，将有更多的人参与各种决策。这一形势的发展必然带来社会结构的变革。总之，现代信息技术的出现和进一步发展将使人们生产方式和生活方式发生巨大变化，引起经济和社会变革，使人类走向新的文明</span>。</p>\n<p><strong>信息高速公路</strong></p>\n<p><br />回顾过去几千年，人类社会的进步在很大程度上依赖于基础设施的建设，如住房、道路、农田水利、工厂等等都是人类社会发展不可缺少的基础。20世纪，信息高速公路这一全球基础设施兴建。<br />美国政府制定&ldquo;信息高速公路&rdquo;的政策基于5项原则：其一，鼓励私人企业增加投资；其二，促进并保护私人企业间的竞争；其三，公众都有机会获得服务；其四，避免在信息拥有方面出现&ldquo;贫富不均&rdquo;现象；其五，维护技术设计上的灵活性。<br />半年后日本政府也决定建立全国超高速信息网。1994年2月16日，欧洲委员会宣布将建立自己的&ldquo;信息高速公路&rdquo;。新加坡的&ldquo;信息高速公路&rdquo;计划也已完成。这些情况说明，第二次信息革命已拉开序幕。<br />自40年代中期计算机问世以来，在全世界范围内兴起的第一次信息革命对人类社会产生了空前的影响，信息产业应运而生，人类迈向信息社会。到90年代初期，美国每年应用计算机完成的工作量相当于4000亿人一年的工作量。以信息技术为基础的产业已占发达国家内生产总值的一半以上。在世界各国纷纷提出建立信息高速公路之际，全世界拥有4亿台计算机和10亿部电话（1994年初），但是，全球范围内的信息传递仍不畅通。美国计划在10&mdash;15年内建成的&ldquo;信息高速公路&rdquo;是指数字化大容量光纤通信网络，用以把政府机构、企业、大学、科研机构和家庭的计算机联网。用90年代计算机网络传输33卷《大不列颠百科全书》需要13小时，而利用&ldquo;信息高速公路&rdquo;将只需4.7秒。<br />&ldquo;信息高速公路&rdquo;掀起的第二次信息革命的特征是网络化、多媒体化。&ldquo;信息高速公路&rdquo;能传递数据、图像、声音等信息，其服务范围包括教育、卫生、娱乐、商业、金融和科研等，并将采取双向交流形式。<br />在当今经济竞争中，掌握信息并使之转化为经济优势，就将取得胜利。著名美国未来学家阿尔文&middot;托夫勒在他的《力量转移》一书中指出：以信息为基础创造财富体系的崛起是当代经济方面最重要的事情，知识已成军事和经济中最重要的因素。日本全国科技政策研究所预测，从2011&mdash;2020年的10年内，人类知识将比现增加3&mdash;4倍。美国《未来学家》1994年1&mdash;2月号载文指出，到2110年，信息技术应用范围将涉及到90%的劳动力，大大促进经济的发展，改变人们的生活和工作方式。<br />但是，第二次信息革命也有可能扩大南北差距，发展中国家因信息技术、资金和人才不足而更加处于不利地位，因此应高度重视第二次信息革命，加紧培养人才，争取迎头赶上；发达国家应实行优惠政策，帮助发展中国家迎接第二次信息革命的到来。</p>\n<p>&nbsp;</p>', '概论信息技术革命', null, 'inherit', null, null, 'gailunxinxijishugeming', '1522186897946492931', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 10:29:31', '192.168.1.23', '1', '2023-01-30 13:39:42', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522298023115931652', '内容付费是引爆差异化、碎片化价值的重要阵地，只要你有价值，就有受众，你的价值决定市场蛋糕有多大', '内容付费是供求双方的刚需', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 17:48:52', '192.168.1.23', '1', '2021-07-02 17:48:52', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522302798985347082', '士大夫', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 18:07:50', '192.168.1.23', '1', '2021-07-02 18:07:50', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1522306517864988680', '是', '商标实务指南', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 18:22:37', '192.168.1.23', '1', '2021-07-02 18:22:37', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522307826739494915', '123', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 18:27:49', '192.168.1.23', '1', '2021-07-02 18:27:49', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1522308366324121602', '99', '轩辕年谱之凤鸣岐山', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 18:29:58', '192.168.1.23', '1', '2021-07-02 18:29:58', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522309813845540866', '568', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-02 18:35:43', '192.168.1.23', '1', '2021-07-02 18:35:43', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1522670207286034441', null, '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '0', '2021-07-03 18:27:47', '192.168.1.23', '0', '2021-07-03 18:27:47', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522680911237922817', '123213', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '2', '0', '0', null, '1', '2021-07-03 19:10:19', '192.168.1.23', '1', '2021-07-03 19:10:19', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1522683503091302409', '明初定都于南京应天府。建文年间，燕王朱棣自北平起兵，发动靖难之变。永乐元年(1403年)，朱棣颁诏改北平为北京。从永乐元年至三年，明成祖多次下令从各地迁入人口至北京。永乐五年闰七月，朱棣颁诏开始营建紫禁城。建造紫禁城和改造北京是同时进行的，以原来的元大都城为基础改建。紫禁城工程开始后不久，即受到长陵建设及永乐八年、十一年两次北伐蒙古战役影响而放慢，至永乐十六年六月方才开始重新开工。', '故宫的前世今生', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-03 19:20:37', '192.168.1.23', '1', '2021-07-03 19:20:37', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1522687132816818177', '八达岭长城传说是北京地区传统民间传说之一，它植根于民间，方言特点突出，把浪漫主义和现实主义巧妙结合起来，具有浓郁的神话色彩和传奇色彩，是中国长城文化的重要组成部分。2008年6月7日，八达岭长城传说经国务院批准列入第二批*非物质文化遗产名录。池尚明，“八达岭长城传说”北京市级代表性传承人，北京民间研究会会员。十八岁开始搜集整理八达岭长城传说故事，师从孟广臣等前辈，系统掌握民间文学搜集整理的方针和原则。池尚明从事八达岭传说故事的搜集整理三十余年，搜集整理了200多篇有关长城的故事传说，在《民间文学》等杂志发表，被多部民间故事集选用。池尚明搜集长城传说，注重实地走访和原始材料，搜集的故事传说题材广泛，整理故事严谨、规范，较好地保留了故事传说的原貌。1983年，为完成《民间文学三套集成》北京卷的工作，池尚明自发组织张泉、张振发、刘永臣等民间文学爱好者深入到八达岭长城沿线村庄采风，并指导他们科学规范地整理故事，使他们成为长城传说搜集整理的主力。', '八达岭', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-03 19:35:03', '192.168.1.23', '1', '2021-07-03 19:35:03', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522688308178239492', '余今以该馆老成凋谢；硕果仅存，唯余一人，是举舍余莫属，只以创始之二十五年，余尚未参加，此段资料，搜集不易，姑努力为之。于是自一九七二年开始，尝试计画搜罗，，从事撰著，期以一年完成。不料老病旋即突发，疲惫万分；甫及半，即搁笔半载。最近病况渐趋稳定，决以馀生完成是举，健康生命，在所不计，卒于去年终全部脱稿。唯后段系扶病写作，资料排列，不无颠倒；且属文潦草，初校势须自任。幸而因病鲜外出，得以全日在家为之，每次工作一二小时，辄休息时许，竞日睡眠多次，以致夜间失眠，其苦滋甚。', '商务印书馆与新教育年谱', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-03 19:39:43', '192.168.1.23', '1', '2021-07-03 19:39:43', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1522999953312104459', '<p><img src=\"http://192.168.1.23:8088/store/202107/05102209XE5kjWmC.jpg\" width=\"473\" height=\"508\" /><img /></p>\n<p><img src=\"http://192.168.1.23:8088/store/202107/04183507MXbbuxtv.jpg\" />1</p>\n<p><img src=\"http://192.168.1.23:8088/store/202107/05144348pHnDlhO7.jpg\" width=\"482\" height=\"362\" /></p>\n<p>你好，弄山呢，你好。</p>\n<p>第三方。士大夫士大夫。不更新当前章节，就可以，只更新兄弟组件需要的当前章节信息。</p>\n<p>你好，感受呢。以及浏览，这是一个下午。测试一下。</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>', '商务印书馆与新教育年谱', null, 'inherit', null, null, null, '1522688308178239492', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-04 16:18:05', '192.168.1.23', '1', '2021-07-07 14:52:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1523370148656496651', '<p>老泪纵横。</p>', '2021-07-05 16:49:06', null, 'inherit', null, null, null, '1522687132816818177', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-05 16:49:06', '192.168.1.23', '1', '2021-07-05 16:49:16', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1524804823476256770', '1312312', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-09 15:49:59', '192.168.1.23', '1', '2021-07-09 15:49:59', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1524888103068286980', '宝妈时光的美好回忆，记那些不曾回去的昨天，仿佛身临其境般神一般的存在', '记xx成长记', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-09 21:20:55', '192.168.1.23', '1', '2021-07-09 21:20:55', '192.168.1.23', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('1524911615640256512', '如果需要，这里可以放一些关于产品的常见问题说明。如果需要，这里可以放一些关于产品的常见问题说明。如果需要', '这是标题伟大的标题标题50个字', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-09 22:54:21', '192.168.1.23', '1', '2021-07-09 22:54:21', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836482199863300', '<p>颜色太红了吧！长太息以掩涕兮，哀民生之多艰！</p>\n<p><img src=\"http://192.168.1.23:8088/store/202107/12121020z9HS5TXu.jpg\" /></p>\n<p>1 《咏柳》&mdash;&mdash;贺知章(唐)</p>\n<p>碧玉妆成一树高, 万条垂下绿丝绦。 不知细叶谁裁出, 二月春风似剪刀。</p>\n<p>2 《送元二使安西》&mdash;&mdash;王维(唐)</p>\n<p>渭城朝雨悒轻尘, 客舍青青柳色新。 劝君更尽一杯酒, 西出阳关无故人。</p>\n<p>3 《黄鹤楼送孟浩然之广陵》&mdash;&mdash;李白(唐)</p>\n<p>故人西辞黄鹤楼, 烟花三月下扬州。 孤帆远影碧空尽, 惟见长江天际流。</p>\n<p>4 《绝句》&mdash;&mdash;杜甫(唐)</p>\n<p>两个黄鹂鸣翠柳, 一行白鹭上青天。 窗含西岭千秋雪, 门泊东吴万里船。</p>\n<p>5 《江畔独步寻花》(选一)&mdash;&mdash;杜甫(唐)</p>\n<p>黄师塔前江水东, 春光懒困倚微风。 桃花一簇开无主, 可爱深红爱浅红。</p>\n<p>6 《春夜喜雨》&mdash;&mdash;杜甫(唐)</p>\n<p>好雨知时节 , 当春乃发生 。 随风潜入夜 , 润物细无声 。</p>\n<p>野径云俱黑 , 江船火独明 。 晓看红湿处 , 花重锦官城 。</p>\n<p>7 《渔歌子》&mdash;&mdash;张志和(唐)</p>\n<p>西塞山前白鹭飞, 桃花流水鳜鱼肥。青箬笠, 绿蓑衣, 斜风细雨不须归。</p>\n<p>8 《滁州西涧》&mdash;&mdash;韦应物(唐)</p>\n<p>独怜幽草涧边生, 上有黄鹂深树鸣。春潮带雨晚来急, 野渡无人舟自横。</p>\n<p>9 《竹枝词》(选一)&mdash;&mdash;刘禹锡(唐)</p>\n<p>杨柳青青江水平, 闻郎江上唱歌声。东边日出西边雨, 道是无晴却有晴。</p>\n<p>10 《乌衣巷》&mdash;&mdash;刘禹锡(唐)</p>\n<p>朱雀桥边野草花, 乌衣巷口夕阳斜。 旧时王谢堂前燕, 飞入寻常百姓家。</p>\n<p>11 《赋得古原草送别》&mdash;&mdash;白居易(唐)</p>\n<p>离离原上草 , 一岁一枯荣 。 野火烧不尽 , 春风吹又生。</p>\n<p>远芳侵古道 , 晴翠接荒城 。又送王孙去 , 萋萋满别情 。</p>\n<p>12 《大林寺桃花》&mdash;&mdash;白居易(唐)</p>\n<p>人间四月芳菲尽, 山寺桃花始盛开。长恨春归无觅处, 不知转入此中来。</p>\n<p>13 《绝 句 》&mdash;&mdash;僧志南(宋)</p>\n<p>古木阴中系短篷，杖藜扶我过桥东。 沾衣欲湿杏花雨，吹面不寒杨柳风。</p>\n<p>14 《题都城南庄》&mdash;&mdash;崔护(唐)</p>\n<p>去年今日此门中, 人面桃花相映红。 人面不知何处去, 桃花依旧笑春风。</p>\n<p>15 《清明》&mdash;&mdash;杜牧(唐)</p>\n<p>清明时节雨纷纷, 路上行人欲断魂。借问酒家何处有? 牧童遥指杏花村。</p>\n<p>16 《江南春》&mdash;&mdash;杜牧(唐)</p>\n<p>千里莺啼绿映红, 水村山郭酒旗风。南朝四百八十寺, 多少楼台烟雨中。</p>\n<p>17 《惠崇&mdash;春江晓景》&mdash;&mdash;苏轼(宋)</p>\n<p>竹外桃花三两枝, 春江水暖鸭先知. 蒌蒿满地芦芽短, 正是河豚欲上时。</p>\n<p>18 《宿新市徐公店》&mdash;&mdash;杨万里(宋)</p>\n<p>篱落疏疏一径深, 树头花落未成阴。儿童急走追黄蝶, 飞入菜花无处寻。</p>\n<p>19 《春日》&mdash;&mdash;朱熹(宋)</p>\n<p>胜日寻芳泗水滨, 无边光景一时新。等闲识得东风面, 万紫千红总是春。</p>\n<p>20 《游园不值》&mdash;&mdash;叶绍翁(宋)</p>\n<p>应怜屐齿印苍苔, 小扣柴扉久不开。春色满园关不住, 一枝红杏出墙来。</p>\n<p>21 《村居》&mdash;&mdash;高鼎(清)</p>\n<p>草长莺飞二月天, 拂堤杨柳醉春归。儿童散学归来早, 忙趁东风放纸鸢。</p>\n<p>22 《春晓》&mdash;&mdash;&mdash;&mdash;孟浩然 (唐)</p>\n<p>春眠不觉晓，处处闻啼鸟，夜来风雨声，花落知多少。</p>\n<p>23 《晴景》&mdash;&mdash; 王驾(唐)</p>\n<p>雨前初现花间蕊，雨后全无叶底花。蜂蝶纷纷过墙去，疑是春色在邻家。</p>\n<p>24 钱塘湖春行&mdash;&mdash; 白居易( 唐)</p>\n<p>孤山寺北贾亭西，水面初平云脚低。几处早莺争暖树，谁家新燕啄春泥。</p>\n<p>乱花渐欲迷人眼，浅草才能没马蹄。最爱湖东行不足，绿杨阴里白沙堤。</p>\n<p>25 次北固山下 &mdash;&mdash;王湾(唐)</p>\n<p>客路青山外，行舟绿水前。潮平两岸阔，风正一帆悬。</p>\n<p>海日生残夜，江春入旧年。乡书何处达?归雁洛阳边。</p>\n<p>26 春望&mdash;&mdash;杜甫(唐)</p>\n<p>国破山河在，城春草木深。感时花溅泪，恨别鸟惊心。</p>\n<p>烽火连三月，家书抵万金。白头搔更短，浑欲不胜簪。</p>\n<p>27 约客 &mdash;&mdash;赵师秀(南宋)</p>\n<p>黄梅时节家家雨，青草池塘处处蛙。有约不来过夜半，闲敲棋子落灯花。</p>\n<p>28 春夜洛城闻笛 &mdash;&mdash;李白(唐)</p>\n<p>谁家玉笛暗飞声，散入春风满洛城。此夜曲中闻折柳，何人不起故园情。</p>\n<p>29 江南逢李龟年 &mdash;&mdash;杜甫(唐)</p>\n<p>岐王宅里寻常见，崔九堂前几度闻。正是江南好风景，落花时节又逢君。</p>\n<p>30 月夜 &mdash;&mdash; 刘方平(唐)</p>\n<p>更深月色半人家，北斗阑干南斗斜。今夜偏知春气暖，虫声新透绿窗纱。</p>\n<p>31《春游湖》&mdash;&mdash;徐俯　(宋)</p>\n<p>双飞燕子几时回?夹岸桃花蘸水开。春雨断桥人不度，小舟撑出柳阴来。</p>\n<p>32《春游曲》&mdash;&mdash;王涯　(唐)</p>\n<p>万树江边杏，新开一夜风。满园深浅色，照在绿波中。</p>', '颜色太红了吧！长太息以掩涕兮，哀民生之多艰', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:09:26', '192.168.1.23', '1', '2021-07-12 12:12:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836842528325642', null, '2021-07-12 12:10:51', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:52', '192.168.1.23', '1', '2021-07-12 12:10:52', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836848811393025', null, '2021-07-12 12:10:53', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:53', '192.168.1.23', '1', '2021-07-12 12:10:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836851072122880', null, '2021-07-12 12:10:53', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:54', '192.168.1.23', '1', '2021-07-12 12:10:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836851571245059', null, '2021-07-12 12:10:54', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:54', '192.168.1.23', '1', '2021-07-12 12:10:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836852162641930', null, '2021-07-12 12:10:54', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:54', '192.168.1.23', '1', '2021-07-12 12:10:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836852456243207', null, '2021-07-12 12:10:54', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:54', '192.168.1.23', '1', '2021-07-12 12:10:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836853693562886', null, '2021-07-12 12:10:54', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:55', '192.168.1.23', '1', '2021-07-12 12:10:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836854565978121', null, '2021-07-12 12:10:54', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:55', '192.168.1.23', '1', '2021-07-12 12:10:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836855404838913', null, '2021-07-12 12:10:55', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:55', '192.168.1.23', '1', '2021-07-12 12:10:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836856323391496', null, '2021-07-12 12:10:55', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:55', '192.168.1.23', '1', '2021-07-12 12:10:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836857233555464', null, '2021-07-12 12:10:55', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:55', '192.168.1.23', '1', '2021-07-12 12:10:55', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525836858034667529', null, '2021-07-12 12:10:55', null, 'inherit', null, null, null, '1524911615640256512', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:10:56', '192.168.1.23', '1', '2021-07-12 12:10:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525843814879248388', '你的各种简介，很长很长的简介。如果你可以，可以选择富文本编辑模式，录入图文并茂的作品详情宣传海报。你可以随心驰骋，这是你的地盘！', '你的作品你的内容你的everything', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-12 12:38:34', '192.168.1.23', '1', '2021-07-12 12:38:34', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1525843966499143682', '<p><img src=\"http://192.168.1.23:8088/store/202107/12123954phlxk42c.jpg\" width=\"350\" height=\"258\" /></p>\n<h3>两条傻狗的日常</h3>\n<p><video style=\"width: 602px; height: 301px;\" src=\"http://192.168.1.23:8088/store/202107/12131413BvKthFFT.mp4\" controls=\"controls\" width=\"602\" height=\"301\"> </video></p>\n<p><strong>摸鱼番外篇</strong></p>\n<p>赋诗一首：</p>\n<h1>《滕王阁序》</h1>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_1_0\"><span class=\"body-zhushi-span\">&nbsp; &nbsp; &nbsp;<span style=\"color: #e03e2d;\"> 豫章</span></span><span style=\"color: #e03e2d;\"><span class=\"body-zhushi-span\">故</span>郡，</span></span><span style=\"color: #e03e2d;\"><span id=\"body_1_1\"><span class=\"body-zhushi-span\">洪都</span>新府。</span><span id=\"body_1_2\"><span class=\"body-zhushi-span\">星分翼轸</span>，</span><span id=\"body_1_3\">地接<span class=\"body-zhushi-span\">衡</span><span class=\"body-zhushi-span\">庐</span>。</span><span id=\"body_1_4\">襟<span class=\"body-zhushi-span\">三江</span>而<span class=\"body-zhushi-span\">带</span>五湖，</span><span id=\"body_1_5\">控<span class=\"body-zhushi-span\">蛮荆</span>而<span class=\"body-zhushi-span\">引</span>瓯越。</span><span id=\"body_1_6\"><span class=\"body-zhushi-span\">物华天宝</span>，</span><span id=\"body_1_7\"><span class=\"body-zhushi-span\">龙光射牛斗之墟</span>；</span><span id=\"body_1_8\">人<span class=\"body-zhushi-span\">杰</span>地<span class=\"body-zhushi-span\">灵</span>，</span><span id=\"body_1_9\"><span class=\"body-zhushi-span\">徐孺</span>下陈蕃之榻。</span><span id=\"body_1_10\"><span class=\"body-zhushi-span\">雄</span><span class=\"body-zhushi-span\">州</span>雾列，</span><span id=\"body_1_11\">俊<span class=\"body-zhushi-span\">采</span>星驰。</span><span id=\"body_1_12\">台隍<span class=\"body-zhushi-span\">枕</span>夷夏之交，</span><span id=\"body_1_13\">宾主尽<span class=\"body-zhushi-span\">东南之美</span>。</span><span id=\"body_1_14\"><span class=\"body-zhushi-span\">都督</span><span class=\"body-zhushi-span\">阎公</span>之雅望，</span><span id=\"body_1_15\"><span class=\"body-zhushi-span\">棨戟</span><span class=\"body-zhushi-span\">遥临</span>；</span><span id=\"body_1_16\"><span class=\"body-zhushi-span\">宇文新州</span>之<span class=\"body-zhushi-span\">懿范</span>，</span><span id=\"body_1_17\"><span class=\"body-zhushi-span\">襜帷</span><span class=\"body-zhushi-span\">暂驻</span>。</span><span id=\"body_1_18\"><span class=\"body-zhushi-span\">十旬休假</span>，</span><span id=\"body_1_19\"><span class=\"body-zhushi-span\">胜友</span>如云；</span><span id=\"body_1_20\">千里逢迎，</span><span id=\"body_1_21\">高朋满座。</span><span id=\"body_1_22\"><span class=\"body-zhushi-span\">腾蛟起凤</span>，</span><span id=\"body_1_23\"><span class=\"body-zhushi-span\">孟学士</span>之<span class=\"body-zhushi-span\">词宗</span>；</span><span id=\"body_1_24\"><span class=\"body-zhushi-span\">紫电青霜</span>，</span><span id=\"body_1_25\"><span class=\"body-zhushi-span\">王将军</span>之<span class=\"body-zhushi-span\">武库</span>。</span><span id=\"body_1_26\"><span class=\"body-zhushi-span\">家君作宰</span>，</span><span id=\"body_1_27\"><span class=\"body-zhushi-span\">路出名区</span>；</span><span id=\"body_1_28\">童子何知，</span><span id=\"body_1_29\">躬逢<span class=\"body-zhushi-span\">胜</span>饯。</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_2_0\">&nbsp; &nbsp; &nbsp; <span style=\"text-decoration: underline;\">时<span class=\"body-zhushi-span\">维</span>九月，</span></span><span style=\"text-decoration: underline;\"><span id=\"body_2_1\"><span class=\"body-zhushi-span\">序</span>属<span class=\"body-zhushi-span\">三秋</span>。</span><span id=\"body_2_2\"><span class=\"body-zhushi-span\">潦水</span>尽而寒潭清，</span><span id=\"body_2_3\">烟光凝而暮山紫。</span><span id=\"body_2_4\"><span class=\"body-zhushi-span\">俨</span><span class=\"body-zhushi-span\">骖騑</span>于<span class=\"body-zhushi-span\">上路</span>，</span><span id=\"body_2_5\"><span class=\"body-zhushi-span\">访</span>风景于<span class=\"body-zhushi-span\">崇阿</span>；</span><span id=\"body_2_6\">临帝子之<span class=\"body-zhushi-span\">长洲</span>，</span><span id=\"body_2_7\">得天人之<span class=\"body-zhushi-span\">旧馆</span>。</span><span id=\"body_2_8\"><span class=\"body-zhushi-span\">层</span>峦耸翠，</span><span id=\"body_2_9\"><span class=\"body-zhushi-span\">上</span>出重霄；</span><span id=\"body_2_10\"><span class=\"body-zhushi-span\">飞阁流丹</span>，</span><span id=\"body_2_11\">下<span class=\"body-zhushi-span\">临</span>无地。</span><span id=\"body_2_12\"><span class=\"body-zhushi-span\">鹤汀凫渚</span>，</span><span id=\"body_2_13\">穷岛屿之<span class=\"body-zhushi-span\">萦回</span>；</span><span id=\"body_2_14\">桂殿兰宫，</span><span id=\"body_2_15\"><span class=\"body-zhushi-span\">即冈峦之体势</span>。</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_3_0\"><span class=\"body-zhushi-span\">&nbsp; &nbsp; &nbsp; &nbsp;<strong>披</strong></span><strong><span class=\"body-zhushi-span\">绣闼</span>，</strong></span><strong><span id=\"body_3_1\">俯<span class=\"body-zhushi-span\">雕甍</span>，</span><span id=\"body_3_2\">山原<span class=\"body-zhushi-span\">旷</span>其<span class=\"body-zhushi-span\">盈视</span>，</span><span id=\"body_3_3\">川泽<span class=\"body-zhushi-span\">纡</span>其<span class=\"body-zhushi-span\">骇瞩</span>。</span><span id=\"body_3_4\"><span class=\"body-zhushi-span\">闾阎</span><span class=\"body-zhushi-span\">扑</span>地，</span><span id=\"body_3_5\"><span class=\"body-zhushi-span\">钟鸣鼎食</span>之家；</span><span id=\"body_3_6\"><span class=\"body-zhushi-span\">舸</span>舰<span class=\"body-zhushi-span\">弥</span>津，</span><span id=\"body_3_7\"><span class=\"body-zhushi-span\">青雀黄龙</span>之<span class=\"body-zhushi-span\">舳</span>。</span><span id=\"body_3_8\">云<span class=\"body-zhushi-span\">销</span>雨<span class=\"body-zhushi-span\">霁</span>，</span><span id=\"body_3_9\"><span class=\"body-zhushi-span\">彩</span>彻<span class=\"body-zhushi-span\">区</span>明。</span><span id=\"body_3_10\">落霞与孤鹜<span class=\"body-zhushi-span\">齐</span>飞，</span><span id=\"body_3_11\">秋水共长天一色。</span><span id=\"body_3_12\">渔舟唱晚，</span><span id=\"body_3_13\">响<span class=\"body-zhushi-span\">穷</span><span class=\"body-zhushi-span\">彭蠡</span>之滨；</span><span id=\"body_3_14\">雁阵惊寒，</span><span id=\"body_3_15\">声断<span class=\"body-zhushi-span\">衡阳</span>之<span class=\"body-zhushi-span\">浦</span>。</span></strong></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_4_0\"><span class=\"body-zhushi-span\">&nbsp; &nbsp; &nbsp; &nbsp;<em>遥</em></span><em>襟<span class=\"body-zhushi-span\">甫</span>畅，</em></span><em><span id=\"body_4_1\">逸<span class=\"body-zhushi-span\">兴</span><span class=\"body-zhushi-span\">遄</span>飞。</span><span id=\"body_4_2\"><span class=\"body-zhushi-span\">爽籁</span>发而清风生，</span><span id=\"body_4_3\">纤歌凝而白云<span class=\"body-zhushi-span\">遏</span>。</span><span id=\"body_4_4\"><span class=\"body-zhushi-span\">睢园绿竹</span>，</span><span id=\"body_4_5\">气<span class=\"body-zhushi-span\">凌</span><span class=\"body-zhushi-span\">彭泽</span>之樽；</span><span id=\"body_4_6\"><span class=\"body-zhushi-span\">邺水</span><span class=\"body-zhushi-span\">朱华</span>，</span><span id=\"body_4_7\"><span class=\"body-zhushi-span\">光照临川之笔</span>。</span><span id=\"body_4_8\"><span class=\"body-zhushi-span\">四美</span>具，</span><span id=\"body_4_9\"><span class=\"body-zhushi-span\">二难</span>并。</span><span id=\"body_4_10\">穷<span class=\"body-zhushi-span\">睇眄</span>于<span class=\"body-zhushi-span\">中天</span>，</span><span id=\"body_4_11\">极娱游于暇日。</span><span id=\"body_4_12\">天高地<span class=\"body-zhushi-span\">迥</span>，</span><span id=\"body_4_13\">觉<span class=\"body-zhushi-span\">宇宙</span>之无穷；</span><span id=\"body_4_14\">兴尽<span class=\"body-zhushi-span\">悲</span>来，</span><span id=\"body_4_15\">识<span class=\"body-zhushi-span\">盈虚</span>之有<span class=\"body-zhushi-span\">数</span>。</span><span id=\"body_4_16\">望长安于日下，</span><span id=\"body_4_17\">目<span class=\"body-zhushi-span\">吴会</span>于云间。</span><span id=\"body_4_18\">地势极而<span class=\"body-zhushi-span\">南溟</span>深，</span><span id=\"body_4_19\"><span class=\"body-zhushi-span\">天柱</span>高而<span class=\"body-zhushi-span\">北辰</span>远。</span><span id=\"body_4_20\"><span class=\"body-zhushi-span\">关山</span><span class=\"body-zhushi-span\">难</span>越，</span><span id=\"body_4_21\">谁悲<span class=\"body-zhushi-span\">失路</span>之人？</span><span id=\"body_4_22\"><span class=\"body-zhushi-span\">萍水相逢</span>，</span><span id=\"body_4_23\">尽是他乡之客。</span><span id=\"body_4_24\">怀<span class=\"body-zhushi-span\">帝阍</span>而<span class=\"body-zhushi-span\">不</span>见，</span><span id=\"body_4_25\">奉宣室以何年？</span></em></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_5_0\">&nbsp; &nbsp; &nbsp; &nbsp;<span style=\"font-family: \'comic sans ms\', sans-serif;\">嗟乎！</span></span><span style=\"font-family: \'comic sans ms\', sans-serif;\"><span id=\"body_5_1\">时运<span class=\"body-zhushi-span\">不齐</span>，</span><span id=\"body_5_2\"><span class=\"body-zhushi-span\">命途</span>多舛。</span><span id=\"body_5_3\"><span class=\"body-zhushi-span\">冯唐易老</span>，</span><span id=\"body_5_4\"><span class=\"body-zhushi-span\">李广难封</span>。</span><span id=\"body_5_5\"><span class=\"body-zhushi-span\">屈贾谊于长沙</span>，</span><span id=\"body_5_6\">非无<span class=\"body-zhushi-span\">圣主</span>；</span><span id=\"body_5_7\">窜<span class=\"body-zhushi-span\">梁鸿</span>于海曲，</span><span id=\"body_5_8\">岂乏<span class=\"body-zhushi-span\">明时</span>？</span><span id=\"body_5_9\">所赖君子见<span class=\"body-zhushi-span\">机</span>，</span><span id=\"body_5_10\"><span class=\"body-zhushi-span\">达人知命</span>。</span><span id=\"body_5_11\"><span class=\"body-zhushi-span\">老当益壮</span>，</span><span id=\"body_5_12\">宁移白首之心？</span><span id=\"body_5_13\">穷且益坚，</span><span id=\"body_5_14\">不<span class=\"body-zhushi-span\">坠</span><span class=\"body-zhushi-span\">青云之志</span>。</span><span id=\"body_5_15\"><span class=\"body-zhushi-span\">酌贪泉而觉爽</span>，</span><span id=\"body_5_16\"><span class=\"body-zhushi-span\">处涸辙</span>以犹欢。</span><span id=\"body_5_17\">北海虽赊，</span><span id=\"body_5_18\">扶摇可接；</span><span id=\"body_5_19\">东隅已逝，</span><span id=\"body_5_20\">桑榆非晚。</span><span id=\"body_5_21\"><span class=\"body-zhushi-span\">孟尝</span>高洁，</span><span id=\"body_5_22\">空余报国之情；</span><span id=\"body_5_23\"><span class=\"body-zhushi-span\">阮籍</span>猖狂，</span><span id=\"body_5_24\">岂效穷途之哭！</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_6_0\">&nbsp; &nbsp; &nbsp; <span style=\"font-family: \'arial black\', sans-serif;\">&nbsp;<span style=\"font-family: arial, helvetica, sans-serif;\">勃，</span></span></span><span style=\"font-family: arial, helvetica, sans-serif;\"><span id=\"body_6_1\"><span class=\"body-zhushi-span\">三尺</span><span class=\"body-zhushi-span\">微命</span>，</span><span id=\"body_6_2\"><span class=\"body-zhushi-span\">一介</span>书生。</span><span id=\"body_6_3\">无路请缨，</span><span id=\"body_6_4\">等<span class=\"body-zhushi-span\">终军</span>之弱冠；</span><span id=\"body_6_5\">有怀<span class=\"body-zhushi-span\">投笔</span>，</span><span id=\"body_6_6\">慕<span class=\"body-zhushi-span\">宗悫</span>之长风。</span><span id=\"body_6_7\">舍<span class=\"body-zhushi-span\">簪笏</span>于<span class=\"body-zhushi-span\">百龄</span>，</span><span id=\"body_6_8\"><span class=\"body-zhushi-span\">奉晨昏</span>于万里。</span><span id=\"body_6_9\"><span class=\"body-zhushi-span\">非谢家之宝树</span>，</span><span id=\"body_6_10\"><span class=\"body-zhushi-span\">接孟氏之芳邻</span>。</span><span id=\"body_6_11\">他日趋庭，</span><span id=\"body_6_12\">叨陪鲤对；</span><span id=\"body_6_13\">今兹<span class=\"body-zhushi-span\">捧袂</span>，</span><span id=\"body_6_14\"><span class=\"body-zhushi-span\">喜托龙门</span>。</span><span id=\"body_6_15\">杨意不逢，</span><span id=\"body_6_16\">抚凌云而自惜；</span><span id=\"body_6_17\">钟期既遇，</span><span id=\"body_6_18\">奏流水以何惭？</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span id=\"body_7_0\">&nbsp;<span style=\"background-color: #f1c40f;\"> &nbsp; &nbsp; &nbsp;呜乎！</span></span><span style=\"background-color: #f1c40f;\"><span id=\"body_7_1\">胜地不<span class=\"body-zhushi-span\">常</span>，</span><span id=\"body_7_2\">盛筵难<span class=\"body-zhushi-span\">再</span>；</span><span id=\"body_7_3\"><span class=\"body-zhushi-span\">兰亭</span>已矣，</span><span id=\"body_7_4\"><span class=\"body-zhushi-span\">梓泽</span>丘墟。</span><span id=\"body_7_5\"><span class=\"body-zhushi-span\">临别赠言</span>，</span><span id=\"body_7_6\">幸承恩于伟饯；</span><span id=\"body_7_7\">登高作赋，</span><span id=\"body_7_8\">是所望于群公。</span><span id=\"body_7_9\">敢竭鄙怀，</span><span id=\"body_7_10\"><span class=\"body-zhushi-span\">恭疏短引</span>；</span><span id=\"body_7_11\"><span class=\"body-zhushi-span\">一言均赋</span>，</span><span id=\"body_7_12\"><span class=\"body-zhushi-span\">四韵俱成</span>。</span><span id=\"body_7_13\">请洒潘江，</span><span id=\"body_7_14\">各倾陆海云尔：</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span style=\"color: #b96ad9;\"><span id=\"body_8_0\">滕王高阁临江<span class=\"body-zhushi-span\">渚</span>，</span><span id=\"body_8_1\">佩玉鸣鸾罢歌舞。</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span style=\"color: #b96ad9;\"><span id=\"body_9_0\">画栋朝飞南浦云，</span><span id=\"body_9_1\">珠帘暮卷西山雨。</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span style=\"color: #b96ad9;\"><span id=\"body_10_0\">闲云潭影日悠悠，</span><span id=\"body_10_1\">物换星移几度秋。</span></span></p>\n<p id=\"body_p\" class=\"poem-detail-main-text\"><span style=\"color: #b96ad9;\"><span id=\"body_11_0\">阁中帝子今何在？</span><span id=\"body_11_1\">槛外长江空自流。</span></span></p>\n<p class=\"poem-detail-main-text\">&nbsp;</p>', '这是第一篇你想写点啥发点啥你随意', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '1', '0', '0', null, '1', '2021-07-12 12:39:10', '192.168.1.23', '1', '2021-07-22 13:25:37', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1527684704723714055', 'wldos是一个原生云平台，从冗繁项目实践中精简优化而来，面向世界，面向未来，开放自由。', 'wldos开发历程', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '8', '0', '0', '60', '1', '2021-07-17 14:33:37', '192.168.1.23', '1', '2021-07-17 14:33:37', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1527685691383398407', '<p><img src=\"http://192.168.1.23:8088/store/202107/22214141cU3rwzCu.png\" width=\"966\" height=\"750\" /></p>\n<p><img src=\"http://192.168.1.23:8088/store/202107/22215202ffH6nuPK.png\" width=\"970\" height=\"843\" /></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>', '2021-07-17 14:37:31', null, 'inherit', null, null, null, '1527684704723714055', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-17 14:37:32', '192.168.1.23', '1', '2021-07-22 21:53:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1527695423368249352', '<p>WLDOS的后端采用spring boot2.4.6框架，立足于成熟平台的稳定可靠，不盲从不成熟的解决方案和技术，在spring boot框架的基础上做了浅层封装，根据研发者15年的开发经验独辟蹊径、规避传统模式弊端，真正打造了一款老少皆宜的开发框架，开箱即用，无缝迁移。</p>\n<p>持久层采用了spring-data-jdbc框架，并做了深度封装，既支持类jpa的数据库编程，也支持传统jdbc的方式访问数据库，但是封装规避了传统jdbc的痛点，同时不失jdbc的高性能，对诸如树状格式数据提供了jdbc级的原生支持，开箱即用，不管哪种方式都能让你体会到超越mybaties plus的开发体验。</p>\n<p>既然不要过度配置，本框架也做了大量支撑，你不需要给每个实体写mybaties那可恶的xml mapper，也不需要配置那个恶心的yml，只需要回归到最简单的开始，用最简洁的方式实现最复杂的业务，对于标准实体和其子集的VO，你甚至不需要再造任何操作数据库的轮子，框架已经集成了，你需要的只是声明相关的接口或者实现类。</p>\n<p>WLDOS原生实现了互联网运营模式，多租户、多应用和多资源支持，支持多场景合并管理，甚至支持多域名的绑定，真正实现了一套系统解决所有问题，这已经不是一个简单的快速开发平台，而是一个随时准备拥抱任何业务的支撑系统。</p>\n<p><img src=\"http://192.168.1.23:8088/2021/07/xynp.jpg\" alt=\"xynp\" width=\"813\" height=\"759\" /></p>\n<p><img src=\"http://192.168.1.23:8088/2021/07/xxtx.jpg\" alt=\"信息发布\" width=\"802\" height=\"704\" /></p>', 'wldos开发历程简介', null, 'inherit', null, null, null, '1527684704723714055', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-17 15:16:12', '192.168.1.23', '1', '2021-07-18 00:01:32', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498008651743234', null, '2021-07-22 14:39:01', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:02', '192.168.1.23', '1', '2021-07-22 14:39:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498009117310976', null, '2021-07-22 14:39:02', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:02', '192.168.1.23', '1', '2021-07-22 14:39:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498012695052291', null, '2021-07-22 14:39:02', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:03', '192.168.1.23', '1', '2021-07-22 14:39:03', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498014934810635', null, '2021-07-22 14:39:03', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:03', '192.168.1.23', '1', '2021-07-22 14:39:03', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498015576539146', null, '2021-07-22 14:39:03', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:04', '192.168.1.23', '1', '2021-07-22 14:39:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498016096632834', null, '2021-07-22 14:39:03', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:04', '192.168.1.23', '1', '2021-07-22 14:39:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498017380089858', null, '2021-07-22 14:39:04', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:04', '192.168.1.23', '1', '2021-07-22 14:39:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498018483191816', null, '2021-07-22 14:39:04', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:04', '192.168.1.23', '1', '2021-07-22 14:39:04', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498019569516549', null, '2021-07-22 14:39:04', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:05', '192.168.1.23', '1', '2021-07-22 14:39:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498020630675458', null, '2021-07-22 14:39:04', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:05', '192.168.1.23', '1', '2021-07-22 14:39:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498020924276739', null, '2021-07-22 14:39:04', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:05', '192.168.1.23', '1', '2021-07-22 14:39:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498022027378693', null, '2021-07-22 14:39:05', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:05', '192.168.1.23', '1', '2021-07-22 14:39:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498022971097089', null, '2021-07-22 14:39:05', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:05', '192.168.1.23', '1', '2021-07-22 14:39:05', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498023839318024', null, '2021-07-22 14:39:05', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:06', '192.168.1.23', '1', '2021-07-22 14:39:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498026787913738', null, '2021-07-22 14:39:06', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:06', '192.168.1.23', '1', '2021-07-22 14:39:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498029136723968', null, '2021-07-22 14:39:06', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:07', '192.168.1.23', '1', '2021-07-22 14:39:07', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498043883896841', null, '2021-07-22 14:39:10', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:10', '192.168.1.23', '1', '2021-07-22 14:39:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498073671843851', null, '2021-07-22 14:39:17', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:17', '192.168.1.23', '1', '2021-07-22 14:39:17', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498075978711048', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:18', '192.168.1.23', '1', '2021-07-22 14:39:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498076502999050', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:18', '192.168.1.23', '1', '2021-07-22 14:39:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498077048258561', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:18', '192.168.1.23', '1', '2021-07-22 14:39:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498078247829511', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:19', '192.168.1.23', '1', '2021-07-22 14:39:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498079380291592', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:19', '192.168.1.23', '1', '2021-07-22 14:39:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498079430623240', null, '2021-07-22 14:39:18', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:19', '192.168.1.23', '1', '2021-07-22 14:39:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498080642777092', null, '2021-07-22 14:39:19', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:19', '192.168.1.23', '1', '2021-07-22 14:39:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498081712324612', null, '2021-07-22 14:39:19', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:19', '192.168.1.23', '1', '2021-07-22 14:39:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498083440377857', null, '2021-07-22 14:39:19', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:20', '192.168.1.23', '1', '2021-07-22 14:39:20', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498091875123209', null, '2021-07-22 14:39:21', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:22', '192.168.1.23', '1', '2021-07-22 14:39:22', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498092525240323', null, '2021-07-22 14:39:21', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:22', '192.168.1.23', '1', '2021-07-22 14:39:22', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498092969836544', null, '2021-07-22 14:39:22', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:22', '192.168.1.23', '1', '2021-07-22 14:39:22', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498095264120837', null, '2021-07-22 14:39:22', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:23', '192.168.1.23', '1', '2021-07-22 14:39:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498096367222792', null, '2021-07-22 14:39:22', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:23', '192.168.1.23', '1', '2021-07-22 14:39:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498096614686730', null, '2021-07-22 14:39:22', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:23', '192.168.1.23', '1', '2021-07-22 14:39:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498097646485509', null, '2021-07-22 14:39:23', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:23', '192.168.1.23', '1', '2021-07-22 14:39:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498098783141899', null, '2021-07-22 14:39:23', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:23', '192.168.1.23', '1', '2021-07-22 14:39:23', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498099697500168', null, '2021-07-22 14:39:23', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:24', '192.168.1.23', '1', '2021-07-22 14:39:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498100704133120', null, '2021-07-22 14:39:23', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:24', '192.168.1.23', '1', '2021-07-22 14:39:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498101626880001', null, '2021-07-22 14:39:24', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '4', '0', '0', null, '1', '2021-07-22 14:39:24', '192.168.1.23', '1', '2021-07-22 14:39:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498102721593344', null, '2021-07-22 14:39:24', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:24', '192.168.1.23', '1', '2021-07-22 14:39:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498102742564872', null, '2021-07-22 14:39:24', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:24', '192.168.1.23', '1', '2021-07-22 14:39:24', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498103988273161', null, '2021-07-22 14:39:24', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:25', '192.168.1.23', '1', '2021-07-22 14:39:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498105087180804', null, '2021-07-22 14:39:24', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:25', '192.168.1.23', '1', '2021-07-22 14:39:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498106118979585', null, '2021-07-22 14:39:25', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:25', '192.168.1.23', '1', '2021-07-22 14:39:25', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1529498120195063808', null, '2021-07-22 14:39:28', null, 'inherit', null, null, null, '1525843814879248388', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-07-22 14:39:29', '192.168.1.23', '1', '2021-07-22 14:39:29', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1531257784574328835', '1234', '测试一下标题', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '2', '0', '0', null, '1', '2021-07-27 11:11:45', '192.168.1.23', '1', '2021-07-27 11:11:45', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1538942586760904711', '<p>据国家卫健委网站消息，8月16日0&mdash;24时，31个省（自治区、直辖市）和新疆生产建设兵团报告新增确诊病例42例，其中境外输入病例36例（云南15例，广东9例，上海7例，广西2例，山东1例，四川1例，陕西1例），本土病例6例（江苏3例，湖北3例）；无新增死亡病例；新增疑似病例1例，为境外输入病例（在上海）。</p>\n<p>当日新增治愈出院病例52例，解除医学观察的密切接触者2735人，重症病例较前一日减少3例。</p>\n<p>境外输入现有确诊病例762例（其中重症病例10例），现有疑似病例1例。累计确诊病例7948例，累计治愈出院病例7186例，无死亡病例。</p>\n<p>截至8月16日24时，据31个省（自治区、直辖市）和新疆生产建设兵团报告，现有确诊病例1928例（其中重症病例67例），累计治愈出院病例87908例，累计死亡病例4636例，累计报告确诊病例94472例，现有疑似病例1例。累计追踪到密切接触者1151965人，尚在医学观察的密切接触者44471人。</p>\n<p>31个省（自治区、直辖市）和新疆生产建设兵团报告新增无症状感染者17例（均为境外输入）；当日转为确诊病例1例（为境外输入）；当日解除医学观察16例（境外输入15例）；尚在医学观察的无症状感染者495例（境外输入393例）。</p>\n<p>累计收到港澳台地区通报确诊病例27961例。其中，香港特别行政区12036例（出院11750例，死亡212例），澳门特别行政区63例（出院57例），台湾地区15862例（出院13178例，死亡821例）。</p>\n<p>&nbsp;</p>', '国家卫健委：31省份新增本土确诊6例，其中江苏3例、湖北3例', null, 'inherit', null, null, null, '1531257784574328835', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-08-17 16:08:25', '192.168.1.23', '1', '2021-08-17 16:09:34', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1538942961920425988', null, '2021-08-17 16:09:54', null, 'inherit', null, null, null, '1531257784574328835', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-08-17 16:09:54', '192.168.1.23', '1', '2021-08-17 16:09:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1541768862404100102', 'WLDOS是World operation system或者World open system的缩写，表示驱动世界的开放系统，我们的理念是简化世界的复杂度，用一套系统协助解决所有问题。WLDOS是一个类SaaS(软件即服务)的开发平台，同时也是一个准系统，支持快速展开二次开发实现符合互联网需求的软件平台。\nWLDOS支持多租户架构，有符合RBAC的权限体系设置，前后端分离，无状态服务，支持单实例部署，也支持多实例部署，自带文件服务方便图片、多媒体等附件的上传下载。\n\nWLDOS的前端采用react框架，在Ant design pro v4.5的基础上做深度定制，响应式布局，原生支持全终端浏览。\n\nWLDOS的后端采用spring boot2.4.6框架，立足于成熟平台的稳定可靠，不盲从不成熟的解决方案和技术，在spring boot框架的基础上做了浅层封装，根据研发者多年的开发经验独辟蹊径、规避传统模式弊端，真正打造了一款老少皆宜的开发框架，开箱即用，无缝迁移。', ' Java语言开发的SaaS版前后端分离开发平台WLDOS', null, 'publish', null, null, ' JavayuyankaifadeSaaSbanqianhouduanfenlikaifapingtaiWLDOS', '0', 'book', '1533544727530094592', null, null, '0', '0', '0', '230', '1', '2021-08-25 11:19:01', '192.168.1.23', '1', '2021-12-20 14:44:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1541803026658541572', '<h4>介绍</h4>\n<p>WLDOS是一款互联网开放运营平台，独特的轻量级企业业务中台架构，可以实现一租多域的多租、多域SaaS，本项目目前开发中，敬请期待。<br />WLDOS开发平台，基于springboot实现轻量级快速开发框架，SaaS应用架构。默认支持多租户运行模式，同时支持关闭以单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构。</p>\n<h4><a id=\"关于发音\" class=\"anchor\" href=\"https://www.wldos.com/#%E5%85%B3%E4%BA%8E%E5%8F%91%E9%9F%B3\"></a>关于发音</h4>\n<p>英：[wel\'dɑ:s] 美：[wɛl\'dɑ:s]。</p>\n<h4><a id=\"软件架构\" class=\"anchor\" href=\"https://www.wldos.com/#%E8%BD%AF%E4%BB%B6%E6%9E%B6%E6%9E%84\"></a>软件架构</h4>\n<p>框架技术：springboot2.4.6，spring-data-jdbc，定制封装。</p>\n<p>应用架构：前后端分离，前端ReactJs，后端springMVC，JWT认证，无状态单实例SaaS架构，兼容springCloud，支持融入serviceMesh。</p>\n<p>软件架构说明 统一响应json格式如下：</p>\n<pre><span id=\"LC1\" class=\"line\"><span class=\"c1\">// 用户权限模板：</span></span>\n<span id=\"LC2\" class=\"line\"><span class=\"p\">{</span></span>\n<span id=\"LC3\" class=\"line\">    <span class=\"dl\">\"</span><span class=\"s2\">data</span><span class=\"dl\">\"</span><span class=\"p\">:{</span></span>\n<span id=\"LC4\" class=\"line\">        <span class=\"dl\">\"</span><span class=\"s2\">userInfo</span><span class=\"dl\">\"</span><span class=\"p\">:{</span></span>\n<span id=\"LC5\" class=\"line\">            <span class=\"dl\">\"</span><span class=\"s2\">id</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">1502803624724185094</span><span class=\"dl\">\"</span><span class=\"p\">,</span></span>\n<span id=\"LC6\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">name</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">nihao</span><span class=\"dl\">\"</span><span class=\"p\">,</span></span>\n<span id=\"LC7\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">avatar</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">http://192.168.1.23:8088/2021/04/zhiletudouyin-e1618196547818-150x150.png</span><span class=\"dl\">\"</span></span>\n<span id=\"LC8\" class=\"line\">        <span class=\"p\">},</span></span>\n<span id=\"LC9\" class=\"line\">        <span class=\"dl\">\"</span><span class=\"s2\">menu</span><span class=\"dl\">\"</span><span class=\"p\">:[</span></span>\n<span id=\"LC10\" class=\"line\">            <span class=\"p\">{</span></span>\n<span id=\"LC11\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">path</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">/</span><span class=\"dl\">\"</span><span class=\"p\">,</span></span>\n<span id=\"LC12\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">icon</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">home</span><span class=\"dl\">\"</span><span class=\"p\">,</span></span>\n<span id=\"LC13\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">name</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">首页</span><span class=\"dl\">\"</span><span class=\"p\">,</span></span>\n<span id=\"LC14\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">id</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">100</span><span class=\"p\">,</span></span>\n<span id=\"LC15\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">parentId</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">0</span><span class=\"p\">,</span></span>\n<span id=\"LC16\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">isLeaf</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"kc\">true</span><span class=\"p\">,</span></span>\n<span id=\"LC17\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">childCount</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">0</span><span class=\"p\">,</span></span>\n<span id=\"LC18\" class=\"line\">                <span class=\"dl\">\"</span><span class=\"s2\">index</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">0</span></span>\n<span id=\"LC19\" class=\"line\">            <span class=\"p\">}</span></span>\n<span id=\"LC20\" class=\"line\">        <span class=\"p\">],</span></span>\n<span id=\"LC21\" class=\"line\">        <span class=\"dl\">\"</span><span class=\"s2\">currentAuthority</span><span class=\"dl\">\"</span><span class=\"p\">:[</span></span>\n<span id=\"LC22\" class=\"line\">            <span class=\"dl\">\"</span><span class=\"s2\">user</span><span class=\"dl\">\"</span></span>\n<span id=\"LC23\" class=\"line\">        <span class=\"p\">],</span></span>\n<span id=\"LC24\" class=\"line\">        <span class=\"dl\">\"</span><span class=\"s2\">isManageSide</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">0</span></span>\n<span id=\"LC25\" class=\"line\">    <span class=\"p\">},</span></span>\n<span id=\"LC26\" class=\"line\">    <span class=\"dl\">\"</span><span class=\"s2\">status</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"mi\">200</span><span class=\"p\">,</span></span>\n<span id=\"LC27\" class=\"line\">    <span class=\"dl\">\"</span><span class=\"s2\">message</span><span class=\"dl\">\"</span><span class=\"p\">:</span><span class=\"dl\">\"</span><span class=\"s2\">ok</span><span class=\"dl\">\"</span></span>\n<span id=\"LC28\" class=\"line\"><span class=\"p\">}</span></span>\n<span id=\"LC29\" class=\"line\"><span class=\"c1\">// 前端路由模板：</span></span>\n<span id=\"LC30\" class=\"line\"><span class=\"nl\">menu</span><span class=\"p\">:</span> <span class=\"p\">[</span></span>\n<span id=\"LC31\" class=\"line\">    <span class=\"p\">{</span></span>\n<span id=\"LC32\" class=\"line\">        <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC33\" class=\"line\">        <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">home</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC34\" class=\"line\">        <span class=\"na\">icon</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">home</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC35\" class=\"line\">    <span class=\"p\">},</span></span>\n<span id=\"LC36\" class=\"line\">    <span class=\"p\">{</span></span>\n<span id=\"LC37\" class=\"line\">        <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/form</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC38\" class=\"line\">        <span class=\"na\">icon</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">form</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC39\" class=\"line\">        <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">form</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC40\" class=\"line\">        <span class=\"na\">children</span><span class=\"p\">:</span> <span class=\"p\">[</span></span>\n<span id=\"LC41\" class=\"line\">            <span class=\"p\">{</span></span>\n<span id=\"LC42\" class=\"line\">                <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">basic-form</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC43\" class=\"line\">                <span class=\"na\">icon</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">smile</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC44\" class=\"line\">                <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/form/basic-form</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC45\" class=\"line\">            <span class=\"p\">},</span></span>\n<span id=\"LC46\" class=\"line\">        <span class=\"p\">],</span></span>\n<span id=\"LC47\" class=\"line\">    <span class=\"p\">},</span></span>\n<span id=\"LC48\" class=\"line\">    <span class=\"p\">{</span></span>\n<span id=\"LC49\" class=\"line\">        <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/list</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC50\" class=\"line\">        <span class=\"na\">icon</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">table</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC51\" class=\"line\">        <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">list</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC52\" class=\"line\">        <span class=\"na\">children</span><span class=\"p\">:</span> <span class=\"p\">[</span></span>\n<span id=\"LC53\" class=\"line\">            <span class=\"p\">{</span></span>\n<span id=\"LC54\" class=\"line\">                <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/list/search</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC55\" class=\"line\">                <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">search-list</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC56\" class=\"line\">                <span class=\"na\">children</span><span class=\"p\">:</span> <span class=\"p\">[</span></span>\n<span id=\"LC57\" class=\"line\">                    <span class=\"p\">{</span></span>\n<span id=\"LC58\" class=\"line\">                        <span class=\"na\">name</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">articles</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC59\" class=\"line\">                        <span class=\"na\">icon</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">smile</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC60\" class=\"line\">                        <span class=\"na\">path</span><span class=\"p\">:</span> <span class=\"dl\">\'</span><span class=\"s1\">/list/search/articles</span><span class=\"dl\">\'</span><span class=\"p\">,</span></span>\n<span id=\"LC61\" class=\"line\">                    <span class=\"p\">},</span></span>\n<span id=\"LC62\" class=\"line\">                <span class=\"p\">],</span></span>\n<span id=\"LC63\" class=\"line\">            <span class=\"p\">},</span></span>\n<span id=\"LC64\" class=\"line\">        <span class=\"p\">],</span></span>\n<span class=\"line\">    <span class=\"p\">},]<br /></span></span></pre>\n<h4>安装教程</h4>\n<p>先部署后端：</p>\n<ol>\n<li>后端工程下载到本地，用idea打开项目。</li>\n<li>安装mysql数据库脚本，生成数据库。</li>\n<li>项目更新maven库。</li>\n<li>在idea控制台执行mvn spring-boot:run运行项目 再部署前端：</li>\n<li>下载本地后，打开前端项目，执行npm install安装依赖库。</li>\n<li>执行npm start启动前端项目。</li>\n<li>超级管理员admin，密码同名称。</li>\n</ol>\n<h4><a id=\"使用说明\" class=\"anchor\" href=\"https://www.wldos.com/#%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E\"></a>使用说明</h4>\n<ol>\n<li>浏览器访问localhost:8000,用户名、密码都是admin,注意浏览器要是有谷歌浏览器。</li>\n<li>点击左侧管理菜单，使用系统管理功能。</li>\n<li>登陆使用JWT认证。</li>\n</ol>\n<h4><a id=\"效果预览\" class=\"anchor\" href=\"https://www.wldos.com/#%E6%95%88%E6%9E%9C%E9%A2%84%E8%A7%88\"></a>效果预览</h4>\n<p>说明：开源版和图示logo不同，开源版默认不含内容管理模块，图示系统是在WLDOS框架基础上开发的内容付费平台。</p>\n<p><img src=\"http://192.168.1.23:8088/store/202108/251336540TgKIXny.png\" /></p>\n<p><img src=\"http://192.168.1.23:8088/store/202108/25133732Hqa9DTUm.png\" /></p>\n<pre><span id=\"LC65\" class=\"line\"></span></pre>', 'WLDOS开发平台简介', null, 'inherit', null, null, 'wldos-platform', '1541768862404100102', 'chapter', '1533544727530094592', null, null, '0', '0', '0', '50', '1', '2021-08-25 13:34:47', '192.168.1.23', '1', '2021-12-20 14:56:38', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544356231896547328', '深入探讨中台战略在产业互联网数字化经济转型中的重要作用。', '业务中台研究', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', '220', '1', '2021-09-01 14:40:18', '192.168.1.23', '1', '2021-09-01 14:40:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544356320987758602', '<p>编辑导语：自从阿里提出中台概念后，各行各业不断推出了中台的应用与落实。关于中台的概念和应用已经有很多文章都讲过了，但是具体的企业建设的文章还是比较少。本文作者就以自身工作实践为基础，分享了自己关于企业中台建设的一些思考和实践，与大家分享。</p>\n<p>最近回答了一个关于会不会读死书的问题。个人理解为这个问题实际是，看书看多了，会不会变成一个不切实际的理想主义者？</p>\n<p>就个人成长而言，一方面是要脚踏实地，参与工作本身就是给自己营造了这样一个大环境，同时项目经理岗位面临的最急迫的事情往往就是如何采取卓有成效的手段定义并达成现实的阶段性目标，这么来看还是沉得下来的；另一方面还是要仰望星空，要回答我们的业务愿景是怎样的？我们的产品演进路径是怎样？我们的工作标准什么？。</p>\n<p>上述两个方面，一个是道，一个是术，都是需要自己通过不断的吸收更加先进的知识经验并不断总结反思的，就目前情况评估，现在的阅读量还是远远不够的。</p>\n<p>这篇文章算是对最近一年干的事的简单总结，从具体事情上来说，包括：政务中台项目投标及相关项目建设；经济中台的落地探索与实践；基于经济中台上的应用场景的建设。而这篇文章要说的不是具体做的事务而是&ldquo;仰望天空&rdquo;的遐想，一些理想化的推理。因此，本文算是基于我现有的经验和认知水平，简单串联了&ldquo;企业中台&rdquo;，&ldquo;城市中台&rdquo;及&ldquo;经济中台&rdquo;等概念并开展遐想和推理的结果，漏洞自然很多，有待实践的检验。</p>\n<p>一、企业中台</p>\n<p>1. 对于企业中台的基本理解</p>\n<p>个人理解，企业中台概念的产生是因为互联网巨头发展为具有多个产品部门的形态，造成大量的烟囱式管理系统，要想整合这些技术、数据和业务于是集团内部就有了建设拉通相应能力基础设施的需求。</p>\n<p>当这些互联网厂商包装这些实践形成所谓中台产品并出售给传统企业的时候，中台似乎更多的变成了以解决企业当前营销问题为导向，支撑企业开展消费者运营为核心的基础设施。</p>\n<p>我理解上述目标也是企业中台和城市中台最大的区别。企业中台的出发点是解决企业这个以营利为目的的市场主体的生存和发展问题，核心就是怎么满足不断变化的市场需求。</p>\n<p>因此是站在市场或用户&ldquo;需求侧&rdquo;的角度出发来规划整个企业中台建设。而城市中台，是站在&ldquo;供给侧&rdquo;结构性改革出发，解决的是为城市公众提供有效的公共服务资源供给并推动城市有效治理的问题。</p>\n<p>从企业中台的建设路径来看，核心是围绕业务数据化和数据业务化开展的，这一点上我觉与城市中台有共通之处。其建设的最终的目标应该是实现企业基于数据驱动的运营。</p>\n<p>相比传统企业信息化系统而言，企业中台试图解决的是企业全价值链条的问题，比如汽车零售就是试图解决&ldquo;围绕消费者认知&mdash;体验&mdash;考虑&mdash;购买&mdash;使用&mdash;服务&rdquo;的企业经营问题，而不单是企业某个职能部门管理问题；另一个方面是支撑企业中台的技术进一步进化，用到了一系列更专业的技术理念以提供有效的支撑，包括：云服务、容器、微服务化等。</p>\n<p>2. 企业中台建设中面临的困境</p>\n<p>如果基于上述判断推演，是否带来了一些建设层面的现实问题：对于普通企业而言是否需要这样庞大的技术支撑？从实施层面，需要一个多么卓越的组织才能够成功实施？这样的系统建设成本又有多高？这也是阅读《中台实践：数字化转型方法论与解决方案》这本书给我带来的疑问。</p>\n<p>这样的资源要求与现实的矛盾对于大多数企业我想都是存在的问题，在后面的经济中台或者是产业互联网建设案例中，我似乎看到了适合更大多数企业的解决方案，这部分在后面的经济中台模块中阐述。</p>\n<p>继续梳理企业中台的建设过程，会出现的另一个问题是在数据和业务双中台概念里，数据中台的建设逻辑似乎更容易成立和易于落地，而涉及业务中台的实践就不是那么的简单。</p>\n<p>从业务本身来看具备以下特点：首先，业务的特性明显，不同的行业、不同的公司之间业务存在显著区别，这一点在我做erp项目的时候就深有感触；其次，业务流程本身也不是一尘不变的，受公司文化影响较大，变更及整合业务流程更是涉及到公司本身的变革。</p>\n<p>上述情况意味着，数字化基础设施建设厂商在业务中台建设过程中需要有深厚的行业积淀，也带来了实施周期和投入方面的现实问题。</p>\n<p>单从业务中台的建设目标来看，其就是要为企业抽象出通用业务能力并与现有业务应用系统深度融合并赋能，为企业提供一个相对稳定的通用业务能力底座。</p>\n<p>业务中台对业务本身进行抽象的同时也意味着他是不能直接带来所谓的业务价值的，因为中台不直接承载具体的业务功能和动作，也无法直接触达企业的合作伙伴或顾客。因此在进一步的实施的时候，要回答的就是哪些能力属于通用业务能力需要沉淀到业务中台中去？切分标准是什么？以及具体的业务架构规划和技术实现问题。</p>\n<p>同样，这个问题在城市中台和经济中台的建设中也会遇到，后面进一步讨论。</p>\n<p>二、城市中台</p>\n<p>1. 智慧城市的演进与城市中台的定位</p>\n<p>城市中台是通过服务于城市管理者（政府），使城市管理者为居民提供更有效公共服务的数字化基础设施。站在政府的作用和价值出发，个人认为，政府一方面就是要维护市场公平，促进市场充分的竞争和发展活力；另一方面，是提供公共服务，兜底民生相关问题，具体就包括医疗、教育等领域。</p>\n<p>这两个方面，站在智慧城市建设层面，后者主要是基于现有电子政务建设的延伸，同时结合了物联网、云计算、大数据等新一代技术；而第一个方面，在当今消费互联网迅猛发展的趋势下，经济交易数据都已经被互联网消费巨头垄断，政府其实更多的还是政策手段的直接干预（比如出台反垄断法），并没有找到很好的决策支撑工具。</p>\n<p>结合后面阐述的经济中台并与城市中台进行比较，我认为第一个方面政府作用的发挥需要依赖于经济中台的建设，目前了解这个现在在全国范围内都还是探索阶段；而第二个方面价值的体现，依赖于现有城市中台的建设。</p>\n<p>我们从智慧城市角度出发讨论城市中台建设，这里借用黄奇帆在《结构性改革》一书中提出的智慧城市建设阶段的版本框架：</p>\n<p>智慧城市1.0版：数字化，目的是让我们生活的世界可以通过数字表达出来；2.0版：网络化，就是通过网络将数字化的要件联系起来，实现数据交互共享；3.0版：智能化，在网络传输的基础上实现局部智能反应与调控；4.0版：智慧化，借助万物互联，使城市各部分功能在人类智慧的驱动下优化运行。对照现有的城市中台建设逻辑来看，包括三个方面：&ldquo;聚&rdquo;、&ldquo;通&rdquo;、&ldquo;用&rdquo;。理解&ldquo;聚&rdquo;和&ldquo;通&rdquo;是基于智慧城市总体建设1.0版本到2.0版本的过程，即数字化到网络化的建设；而&ldquo;用&rdquo;追求的就是智慧城市3.0和4.0版本的高版本动作。因此这么来看，城市中台在其中发挥的是智慧城市基础设施或底座的作用。</p>\n<p>2. 城市中台与企业中台的比较</p>\n<p>基于上述框架的4.0版即智能化，比较企业中台和城市中台，他们的核心目的都是要通过数字驱动运营，只是说主体不一样，一个是企业，一个是城市。</p>\n<p>进一步通过城市中台大的分类与企业中台进行比较，通常包括技术中台、数据中台、业务中台三个组成部分。城市中台建设中技术中台被单独抽离出来了，系统实现层面有一个单独的封装和开放平台。</p>\n<p>个人理解为相比企业中台，城市中台需要面向更广阔的技术需求方提供通用技术服务能力，而站在企业层面并没有太大的需要。在数据中台层面，企业中台的数据体量也许并没有城市中台大，但是其业务的复杂性也是相当高的，二者在这一方面也许并不能放在同一维度进行比较。</p>\n<p>3. 关于业务中台建设问题的继续探讨</p>\n<p>在这里想站在城市中台建设层面继续讨论业务中台建设遇到的问题：包括资源困境和业务难以抽离及如何为具体业务赋能两个方面的问题。</p>\n<p>站在城市管理者的角度，个人理解城市中台也是要抽离这许多电子政务服务应用共同的业务属性，为后续应用建设提供共性能力，并基于此开展有效运营。</p>\n<p>城市中台的业务抽离是否更加困难？首先，对政府提供的服务而言，其业务属性本身就不像企业场景和后面的经济场景那么强，这里的比较标准是涉及交易业务数量多少来看的；同时这样的整合不仅仅是企业部门之间了，而涉及到城市公共服务部门之间的业务协同，是否难度就更高了。</p>\n<p>也许正因如此，目前无论是&ldquo;城市中台&rdquo;还是&ldquo;城市大脑&rdquo;建设，更多的都是基于某个公共服务场景数据本身的融合、共享及单个场景支撑的智能化应用，典型的如智慧交通，涉及城市级别的跨部门业务沉淀及协同比较少，目前我个人理解还处于探索阶段。</p>\n<p>我们不妨从数据中台建设的角度考察业务中台建设问题的解决路径。黄奇帆在今年的外滩金融峰会曾说&ldquo;数字化平台具有全空域信息、全流程信息、全场景信息、全解析信息和全价值信息的&lsquo;五全信息&rsquo;，任何一个传统产业链一旦能够利用&lsquo;五全信息&rsquo;，就会立即形成新的经济组织方式，从而对传统产业构成颠覆性的冲击。&rdquo;</p>\n<p>这个观点是否提供了一种推理依据，当数据本身足够的全面、足够的场景化、足够的准确，他自然会带来业务模式的变革，就能实现所谓&ldquo;数据的业务化&rdquo;，因此数据本身不仅仅能够支撑业务，甚至数据就能带来业务甚至是商业模式的变革。</p>\n<p>进一步推理，在具体的建设中我们是否可以将数据化的标准提高来作为业务变革和支撑的有效的抓手？</p>\n<p>这样的逻辑是否会带来一个悖论：数据本来就产生于业务，要产生这样的良性循环，成了回答先有&ldquo;鸡&rdquo;还是先有&ldquo;蛋&rdquo;的问题。总结来看，个人认为还是要依赖于具体的业务运营手段，而这些支撑业务运营的不是&ldquo;中台&rdquo;而是前端应用。</p>\n<p>企业中台的建设依赖于大量面向消费者的前端应用，同理城市中台也应该是如此。于是我们回到了企业中台建设面临的一样的问题，我们是否需要先建设大量的前端应用或者是接入和整合这些应用。这样看来，这个硬骨头还是要啃下去。</p>\n<p>三、经济中台</p>\n<p>1. 经济中台解决的问题</p>\n<p>经济中台相比城市中台个人理解是一个更加宏大的概念。如果讲城市有明显的地理界限及行政区划的划分，那经济并不是如此，经济活动通常是不受区域限制的。</p>\n<p>这里说的经济概念更像是产业的集合，而经济中台建设解决的问题我认为其中一个核心就是前文所说的城市管理者通过一个有效的抓手或决策工具&ldquo;维护市场公平，促进市场充分的竞争和发展活力&rdquo;的问题；另一个核心，我认为甚至更重要的是通过经济中台解决&ldquo;贯通生产、分配、流通、消费各环节，打破行业垄断和地方保护，形成国民经济良性循环&rdquo;的问题。</p>\n<p>如果基于上述定位，就使经济中台相比城市中台即具有明显的市场属性，又具有明显的政务属性。这里市场属性指的是产业发展归根结底是市场主体的自愿行为；政务属性是指政府这只有形的手在其中要发挥更&ldquo;有效&rdquo;作用，基于后者政府可以通过&ldquo;产业政策&rdquo;和宏观调控两个维度开展具体的动作。</p>\n<p>站在政府这只手的角度，我们会在建设过程中遇到的问题是政府这只手的边界是什么？他希望做什么？他能做什么？这个需要在实践中结合具体的项目运营动作去探索。</p>\n<p>2. 产业链视角下的经济中台</p>\n<p>站在市场这只手的角度，我们不妨降维到产业层面来推导一下经济中台的发展前景及路径，这里还是从企业中台面临的业务中台建设困境说起。</p>\n<p>在前面阐述的企业和政府维度业务中台的建设过程中，都遇到了资源缺乏及业务本身的复杂性带来的业务协同和整合的困难。记得在政务中台的建设中，我们的方式是通过构建或整合应用前端实现，我们是否有更加有效的途径。最近在电商领域看到了一种解决方式，这种方案是shopify和有赞这类产业互联网企业实施的。</p>\n<p>他们的回答是，面向电商产业链上的实体企业，只提供配置化的开店工具，实体企业不需要再单独建设前端支撑应用，电商产业平台通过利用先进的技术架构，面向市场上已经具备的各种服务应用，平滑的接入若干应用能力即可实现为实体企业提供生产、分配、流通、消费等各环节服务。这种方式与其说是产业中台不如更形象的说是产业的&ldquo;路由器&rdquo;。</p>\n<p>针对这种方式，他们的服务对象更多的是小微企业。正如有赞创始人所说：&ldquo;有赞的客户主要以中小企业为主，大家的需求更加趋同。&lsquo;范标准化&rsquo;是中小企业服务的趋势，就好像一个好的收银机可以满足所有小卖部的需求一样。&rdquo;</p>\n<p>站在这个层面上，进一步分析一下其商业模式。这类商业模式相比传统电商其最大的不同是为实体企业提供了一个独立的工具，在关键的销售渠道等方面依赖于实体企业自身的&ldquo;私域流量&rdquo;，而不需要依赖于淘宝等中心化平台的&ldquo;公域流量&rdquo;。</p>\n<p>站在消费者体验来比较，消费者不需要通过&ldquo;淘宝&rdquo;就能访问到具体的卖家，可以直接通过访问卖家店铺的私有域名就行，至于是通过微信、抖音、美团等渠道进入都是可行的。</p>\n<p>这样的变化意味着实体企业能够更加全面的掌握自己服务的消费者群体的用户画像等一些列核心运营数据，更有助于打造自己的核心品牌，经营自主性随之也大大提高。而在这类模式出现之前往往只有大型企业才能独立开辟属于自己的&ldquo;私域流量&rdquo;，中小企业通常会受制于建设成本高昂和平台垄断等问题。</p>\n<p>因此，总结来看，shopify和有赞的方法实际上是一种产业互联网建设模式，他通过为产业链上的若干实体企业赋能，进一步增强了实体企业的数字化能力，客观上也达到了&ldquo;去中心化&rdquo;（相比现有的中心化电商平台来看）的效果。</p>\n<p>站在产业互联网发展的角度来看，这类方式的出发点不是站在单个企业数字化，而是站在产业的角度为链条上的企业去赋能，从而解决他们自身数字化基础设施薄弱的问题。</p>\n<p>需要指出的是，这种模式和苹果应用超市或微信小程序出发点上有本质的不同，后一种还是巨头的圈地行为，是为了将流量更好的流在以巨头为核心的生态圈的做法。</p>\n<p>当然，目前了解的产业互联网的做法不能局限在shopify和有赞这一具体模式上，包括工业互联网平台、供应链金融平台都是具备相关特征的实践模式。</p>\n<p>但他们的出发点都是共同的，这个平台的建设者的基本立场一定是中立的，而不是既当裁判员也当运动员，如果基于此推导这样的平台不会是围绕核心企业的自建中台系统演化而来的，也不会是消费互联网巨头的&ldquo;圈地&rdquo;行为。</p>\n<p>从现实的问题出发，即解决消费互联网巨头带来的垄断问题，也能佐证产业互联网模式的合理性：</p>\n<p>从发展的角度来看，消费互联网巨头们提供了众多互联网场景下交易及服务的基础设施，能解决线下交易场景下由于信息不对称等原因带来的垄断等问题，但目前来看随着其发展也带来了垄断，消费互联网所具有的网络效应等特点带来的实际效果是&ldquo;赢家通吃&rdquo;的商业竞争结果。</p>\n<p>那面对这种情况解决的办法是什么？政府层面当然可以出台反垄断法，但从解放生产力的角度来看，我想如果产业链上的企业如果都具备了基本的数字化能力，那自然政府担心的市场公平竞争问题能够得到更有效的解决。</p>\n<p>因此，我们需要更加&ldquo;基础设施化&rdquo;的平台工具，这个平台就是要均等开放的为产业链条上的每个企业进行数字化赋能，而不是&ldquo;割韭菜式&rdquo;的服务。</p>\n<p>站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。</p>\n<p>如果要对照这样的发展阶段，个人认为：消费互联网的巨头代表的就是中心化的商业文明，产业互联网应该代表就是去中心化的商业文明。</p>\n<p>这里面总的趋势是随着商业进化基础设施的普及程度会越来越高，相关市场主体的数字化能力会越来越强，这当中没有绝对的边界，其实是一个渐进发展的过程。因此产业互联网评估应该是比消费互联网平台提供更加底层和革命的基础设施。</p>\n<p>3. 从产业互联网回到经济中台建设实践</p>\n<p>如果我们要为产业链条上的企业提供更加革命的数字化基础设施，搭建均等化和开放包容的平台，实施层面可以从哪些方面考虑？</p>\n<p>站在技术层面，中台归根结底还是一个技术底座，在这个底座肯定是要有效的吸收企业中台、政务中台建设中先进的经验，这里面包括：业务中台及数据中台的建设经验；站在市场层面，要为经济中台要为产业链条上的相关利益主体提供一整套共赢的运营服务方案，教实体企业（特别是产业发展的薄弱环节）怎么中台提供的科技工具，武装自己开展有效运营；站在政府层面，结合前文所述的政府的关注点和可以采用的动作协同运营，有效发挥政府这只手的作用。</p>\n<p>四、综述</p>\n<p>综合来看，企业中台，城市中台，经济中台所站的视角是不一样的，一个是站在企业主的视角；一个是在城市管理者视角；而经济中台是站在产业角度解决产业畅通及协同共赢的问题。</p>\n<p>但是归根结底，他们都是数字化基础设施，只是这个基础设施的服务对象不同，基础设施化的程度不同，业务中台、技术中台、数据中台连通、整合赋能的对象不同，但他们也都是运用科技手段围绕数字化，围绕数据业务化，业务数据化来开展的。</p>\n<p>本文由 @特立独行的猪 原创发布于人人都是产品经理，未经作者许可，禁止转载。</p>\n<p>题图来自pexels，基于CC0协议</p>\n<p>因此，我们需要更加&ldquo;基础设施化&rdquo;的平台工具，这个平台就是要均等开放的为产业链条上的每个企业进行数字化赋能，而不是&ldquo;割韭菜式&rdquo;的服务。</p>\n<p>站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢</p>\n<p>我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。<br />综合来看，企业中台，城市中台，经济中台所站的视角是不一样的，一个是站在企业主的视角；一个是在城市管理者视角；而经济中台是站在产业角度解决产业畅通及协同共赢的问题。</p>\n<p>但是归根结底，他们都是数字化基础设施，只是这个基础设施的服务对象不同，基础设施化的程度不同，业务中台、技术中台、数据中台连通、整合赋能的对象不同，但他们也都是运用科技手段围绕数字化，围绕数据业务化，业务数据化来开展的。</p>\n<p>本文由 @特立独行的猪 原创发布于人人都是产品经理，未经作者许可，禁止转载。题图来自pexels，基于CC0协议因此，我们需要更加&ldquo;基础设施化&rdquo;的平台工具，这个平台就是要均等开放的为产业链条上的每个企业进行数字化赋能，而不是&ldquo;割韭菜式&rdquo;的服务。</p>\n<p>站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，<br />我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。<br />就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。<br />综合来看，企业中台，城市中台，经济中台所站的视角是不一样的，一个是站在企业主的视角；一个是在城市管理者视角；而经济中台是站在产业角度解决产业畅通及协同共赢的问题。</p>\n<p>但是归根结底，他们都是数字化基础设施，只是这个基础设施的服务对象不同，基础设施化的程度不同，业务中台、技术中台、数据中台连通、整合赋能的对象不同，但他们也都是运用科技手段围绕数字化，围绕数据业务化，业务数据化来开展的。</p>\n<p>本文由 @特立独行的猪 原创发布于人人都是产品经理，未经作者许可，禁止转载。题图来自pexels，基于CC0协议因此，我们需要更加&ldquo;基础设施化&rdquo;的平台工具，这个平台就是要均等开放的为产业链条上的每个企业进行数字化赋能，而不是&ldquo;割韭菜式&rdquo;的服务。</p>\n<p>站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。</p>\n<p>就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。站在商业进化历程的角度，也许能够更好的阐述&ldquo;基础设施化&rdquo;这个动态的发展过程。这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢我们不知道多少字了。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。</p>\n<p>这里引用刘润在《商业简史》中提炼的商业进化历程：即商业的核心是围绕交易，商业发展从商业原始社会到线段商业文明（如丝绸之路）到中心化商业文明再到去中心化商业文明，而每一次商业的进步都是依靠消除信息和信用不对称来解决的。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性，而这些平台工具都应该是基础设施的范畴。随着整体社会层面的数字化水平的提高，也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和技术工具来消除这样的不对称性。随着整体也呈现出越来越基础设施的趋势。你好干啥呢。就目前来看，我们肯定是越来越依赖科学和你好干啥呢。</p>', '关于企业中台、城市中台、经济中台的实践思考与比较探讨', null, 'inherit', null, null, '', '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '0', '1', '40', '1', '2021-09-01 14:40:40', '192.168.1.23', '1', '2021-12-20 14:16:41', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544386503421640713', '<p><img src=\"http://192.168.1.23:8088/store/202109/01164638X8YZGiUj.png\" /></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">2020年被认为是继2019年这个&ldquo;产业互联网元年&rdquo;之后，数字化转型在全社会范围内大面积落地的重要承接之年。</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">明略科技的创始人兼CEO吴明辉，是北大数学系出身的连续成功创业者，更是国内一直推动AI和大数据的实践者。近期，在明略科技2020年会上，吴明辉发表《建设智能时代的企业中台》演讲，根据多年的行业经验并结合明略科技十多年的创业历程，站在产业的高度，对智能时代的发展趋势和进步阶梯进行了深入的分析，对中台帮助产业升级和国民经济提升的重要意义进行了深入阐发，引人深思。</span></p>\n<p><span class=\"bjh-p\"><img src=\"http://192.168.1.23:8088/store/202109/01164728jKwF85AI.jpg\" /></span></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-h3\">1、数字化转型的三个台阶</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">首先，说一下我对2020年的行业预判，结合行业内的实践和我的个人体会，我认为，今年是建立智能时代的企业中台的关键之年、落地之年，这很大程度上是因为产业升级的压力倒逼和中台体系逐渐成熟决定的。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">为什么明略科技的产业实践使我能有这样的洞察？很大程度上是因为，明略科技是一个完整的参与了产业互联网服务于数字化转型的历史进程，并多次迭代的公司，我们在这个过程中逐步积累了相应的核心技术和能力，与时代一起进步。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">回顾一下过往，我们之所以能抓住智能时代的这一历史机遇，也是因为过去我们走过了三个阶段。</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">很重要的第一个阶段，是2006年的时候，我们在市场上创造了一种新的产品服务形式，即<span class=\"bjh-strong\">通过大数据的技术，帮助所有的客户全流量分析监测它的广告投放，并追踪后续的消费者的用户行为</span>，也就是我创办的第一家企业秒针系统所具备的能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在这些服务的过程中，大量的用户行为在遵守用户数据安全与隐私保护的前提下以数字化的方式沉淀下来，最终汇聚成了我们后来的核心数据资产，沉淀出来了今天我们核心数据中台的建设能力、处理能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第二个阶段，是<span class=\"bjh-strong\">明略数据这个平台的诞生，</span>那是2014年。在明略数据所代表的的2.0时代里，我们把1.0时代中积累的数据采集、分析加以提升，在&ldquo;AI+Big Data+Cloud&rdquo;合流的大背景下，我们打造了多源异构数据的融合处理、知识图谱、感知到认知再到分析推理等多方面的能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">这些领先于时代的技术能力与广阔的市场空间中的各种需求相匹配，使我们为政府公共服务、世界五百强企业、轨道交通、制造业和金融业都提供了大量的人工智能和大数据服务，这不仅让我们有了很高的发展天花板，也使得我们在许多细分领域取得了长足进展。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">在最近一年多的时间里，我们将公司升级为明略科技，开始了全面拥抱智能时代的历程。</span>其中，我们启动了新的业务，目的是迎接新数据、新场景、新服务三者交融的3.0时代的到来。</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\"><img src=\"http://192.168.1.23:8088/store/202109/01164833RxiSVtBk.jpg\" /></div>\n<div class=\"index-module_mediaWrap_213jB\">&nbsp;</div>\n<div class=\"index-module_mediaWrap_213jB\">\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">特别要提一下，&ldquo;智能时代&rdquo;不仅仅是因明略科技的战略需求而定义的，我们之所以把它定位成我们要攀上的第三个重要的历史台阶，是因为它本身就是历史的大势所趋、行业共识，它的最核心的特点，就是全行业全产业链的数字化。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">为什么说是全行业、全产业的数字化大趋势呢？</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们可以看一看，大家都知道电子商务已经彻底的颠覆了零售业，阿里、京东、拼多多都是时代的宠儿，它们已经全面数字化了。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">但是所有的电商加起来占到智慧零售的多少呢？我估计大概是30%。也就是说，还有大量的线下交易，非数字化的人、货、场，还有很多互联网的光芒没有照到的地方，我们认为它们的下一步也都是向数字化、网络化、智能化方向发展，所以明略科技拥有非常广阔的市场空间，我们所在的行业也有非常广阔的增量空间。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">那具体的爆发点在哪里呢？我认为是在中台的全行业普及和落地。</span>业内的共识是，中台就是资源和数据的交换匹配，企业内部、企业之间、企业与社会这三个层次，都需要有各自的中台，以完成高效率的数据匹配和能力输出，这是产业数字化升级的刚需所决定的。那么，什么是中台呢？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">现在，全行业、全产业链的数字化，必然需要一个新的管理架构来作为中枢，中台就此出现，甚至出现了中台热，但一个现实是，很多人搞不清中台是什么。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-h3\">2、中台的前世今生和未来</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">研究中台是一个非常有趣的工作。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们看到有人把中台翻译成middleware，有人翻译成middle office，我是赞成后一种翻译方法的。middleware其实是一个技术词汇，做软件出身的人应该知道它指的是中间件、中间页这一类技术，它和我们所说的中台不是一个概念。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">我比较赞成middle office的翻译，这个翻译从字面理解，是&ldquo;位居其中的办公处所&rdquo;。</span>如果大家翻字典，office这个词有一个重要含义是提供某种功能的&ldquo;处所&rdquo;，这个含义是最接近&ldquo;中台&rdquo;的概念的。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">所以，大家可以看到，中台是一个管理词汇，一个管理学上的概念，它不是一种技术，也不是一个软件或者硬件，所以我们永远不能去&lsquo;卖一个中台&rsquo;给我们的客户。我们要做的帮用户搭建中台。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">中台是怎么出现的？</span>从广义上说，现代企业制度建立以来，这个概念就在逐渐形成；从狭义上说，就我们今天所谈的数据中台而言，它是较早发育在行业科技属性强、数字化程度充分的一些行业里，比如明略科技很熟悉的数字营销领域。因此，我们就以此为例，再做详解。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">熟悉数字营销领域的人可能对SSP、DSP、Exchange等概念耳熟能详。</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">首先是SSP，即Supply-Side Platform（供应方平台）。我们经历了从每个媒体都有自己的销售平台，到全行业、全球形成几个巨型的SSP的过程，它们逐渐把全球的媒体供给都数字化了。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">因为SSP的巨型化，这种能力的提升对应的是DSP，也即需求方平台(全称：Demand-Side Platform)的提升。我们可以看到，在供给方和需求方都数字化了以后，就出现了Ad exchange。大家可以看到，在国内是BAT，在全球是谷歌、Facebook掌握了这些平台，并且在不断提升非常精准匹配的能力。其实，这几个概念同样适合于其它行业，因为所有的行业都是由供给、需求、居间调度和匹配等三元因子共同作用而推动的，因此中台也适应于大多数经济领域、社会服务领域的数字化转型。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们很早就发现，在数字营销这个本身数字化非常充分的行业里（它的数字化的充分程度，超过了绝大多数的传统行业），自然而然地形成了一个在供给和需求之间进行居间服务的需求，我们可以看作是整个行业供需双方需要高效匹配和调用的需求，催生了middleoffice，也就是中台概念的出现。</span></p>\n<p><span class=\"bjh-p\"><img src=\"http://192.168.1.23:8088/store/202109/01164917U91qTj66.jpg\" /></span></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们发现，中台需求往往起源于企业的多元化需求，而企业的多元化则往往是其业务起飞期的标志，是企业和行业繁荣和成熟的阶段性产物。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">那么，问题就出现了，什么样的管理结构能够兼顾多元化的发展和资源利用的专注高效的呢？</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">那就是中台，多元化需要有一个很好的策略前提，就是能够把企业不同业务领域的核心公共模块真正打造成扎实的中台。只有这个公共模块打造扎实，每一个局部的小团队作战背后才会有强大的中台支撑，才能在专注的基础上实现多元化。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">就明略科技来说，我们的业务越来越多元，但本质只是在做一件事情，那就是我们一直都是在做各行各业的多源异构的数据治理。所以我们的本质是&mdash;&mdash;帮助客户把这些数据整理到一起并可以灵活调用，进而使其可以面向未来的智能时代，为此自然需要打造一个企业最强大的中台。比如，在数字营销领域，我们已经推出了明略科技的营销智能平台MIP（Marketing Intelligence Platform），它可以帮助企业更快去推动数字化转型。而从营销智能开始，是因为我们在这一领域有特别深入的积累。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">但是，我想要强调的是，明略科技的MIP能力，将不仅仅限于营销领域，也不仅仅限于我们已经取得优势的公共服务、交通、金融等领域。这是因为我们意识到，数字化时代的转型，需要我们为整个产业界提供更好的中台能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">为什么呢？这是因为随着产业互联网的勃兴，跨行业、全产业的数据交换已经成为历史的必然。</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">明略科技最早服务过的日化用品巨头，现在它们的整个平台在走向数字化，每一支实体产品都在虚拟世界有自己的数字代码；还记得我们考察过的一些新兴的零售企业，它们虽然是线下实体企业，但通过各种各样的传感器、SaaS和PaaS系统，其实已经完全数字化了。那么，在人、货、场都走向数字化之后，我们可以想象一幅什么样的图景呢？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们可以看到，首先在制造业，全流程的生产、库存和分销的数字化，使得社会的物质产品可以无缝和淘宝、京东这样的线上电商企业对接，更可以和具有充分数字化能力的线下智能零售业对接。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">而围绕这些产品的销售，所产生的营销需求会自动和智能营销平台对接，所产生的金融和支付需求会和智能金融平台对接，销售的结果会用数字化的形式进行分析和经验积累并沉淀为know-how&hellip;&hellip;大家会看到，全社会的产业资源，都在进行积极的数据交换，这里面的核心是什么？是对多源异构数据处理和中台能力的极大需求，也是明略科技3.0时代社会价值的最大体现。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">所以，让我们总结&mdash;&mdash;一个经济体最核心的工作就是交换，交换就是Market，中台最核心的工作就是Market，而我们把这种能力数据化了，使之可以利用巨大的算力和人工智能进一步优化协同和匹配。</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\">\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">所以，明略科技以及行业里类似的头部企业的紧迫任务是&mdash;&mdash;帮助用户搭建智能化时代的数据中台。</span>而且，令人非常兴奋的是，我们要服务的不仅仅是30%已经数字化转型了的企业，我们还将面对70%没有数字化转型彻底完成的企业，它们将是中国社会经济的增量来源，也是这个行业无尽的蓝海。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-h3\">3、智能化时代的数据中台，实现路径是什么？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们认为，智能化时代的企业中台建设是一个非常严肃的&ldquo;活儿&rdquo;，它应该遵循一定的规律。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">我们可以看到，从广义上来讲，中台有很多种，比如数据中台、资源中台、能力中台，但我们总结起来，<span class=\"bjh-strong\">一个企业值得放到中台上沉淀的其实就是两个东西：</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">1.这个企业经过长期的业务发展起来的核心能力，比如京东的物流能力、比如滴滴的调度能力；</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">2.这个企业经过长期的运转积淀下来的核心资源，比如淘宝上成千上万的B端商家、比如腾讯以10亿为单位的用户和用户行为数据；</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\"><img src=\"http://192.168.1.23:8088/store/202109/01165041ueLS9RdU.jpg\" /></div>\n<div class=\"index-module_mediaWrap_213jB\">&nbsp;</div>\n<div class=\"index-module_mediaWrap_213jB\">\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">那么对于企业来说，这两个&ldquo;核心&rdquo;怎么中台化呢？我的看法是：</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">对于核心能力，我们采取自上而下萃取行业know-how的方法，逐步形成能力闭环；对于核心资源，我们要实现数据的整合以及资源的连接，也就是我们强调的2.0时代积累的多源异构数据的处理能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">那么，这些东西最终以什么样的形态沉淀呢？</span>大家可能知道了，还是数据，是经过千辛万苦的dirty work最终帮助客户沉淀下来的宝贵的数据。以这些数据为基础形成的数据中台，才可能有智慧，才能够帮助组织高效运转、加速创新，从个体到企业，从行业到产业，我们一手萃取核心能力，一手通过多源异构数据治理、融合、分析能力，实现从局部优化到全局优化的过程，最终产生新的闭环。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在数字化转型比较充分的行业里，全社会、全产业的数据中台已经形成了，比如阿里就是电商的大中台、美团是餐饮和本地生活服务的大中台、滴滴是出行服务的大中台，这些大中台已经在协调和优化资源分配和运行效率上，为我们的社会提供了很好的帮助。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">但明略科技或者说整个产业互联网界的责任是什么呢？</span>是我们还要帮助许多数字化转型不充分、互联网能力没有赋能到的线下行业、传统行业进行中台建设，那么我们最紧迫的任务可能有这样几个步骤：</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第一，是帮助企业收集和沉淀全局的数据，全局这个概念意味着，不断地打破数据壁垒和数据孤岛，所以这是一个非常重的活儿；</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第二，是建立基于数据的反馈，这个很重要。比如现在的线下新零售卖场里都有很多传感器，精确到客户会在什么货架前停留多久，这种基于视觉感知后产生的数据反馈，极大的有助于精细化运营的开展；</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第三，等到数据和反馈能力都具备了，其实我们就具备了全局优化的能力，所有的决策都有可能从人工做决策转变到自动化做决策。数字营销行业已经实现了人工决策到自动决策的转变，未来更多的行业在实现了数据交换、实时反馈和全局优化后，还能够更高效的实现机器的自动决策，进一步提高效率。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在文章的最后一部分，我将分享一个非常重要的概念，就是</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">以客户为中心的倒金字塔服务力模型，</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">它的核心原理，是不断提高服务力来增强客户粘性，不断积累know-how，连接资源和服务。</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\"><img src=\"http://192.168.1.23:8088/store/202109/01165141znd2Jymw.jpg\" /></div>\n<div class=\"index-module_mediaWrap_213jB\">\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在倒金字塔结构中，最上方的是客户，这是由于我们身处供过于求的时代，&ldquo;客户至上&rdquo;的地位决定的，也是因为这个倒金字塔所要进行的最重要的萃取行业know-how的实践，都是和客户一起产生的。有人可能觉得，这个倒金字塔结构有些像咖啡杯的滤纸，如果有这种感觉就对了，因为没有比这种结构更能形象地反映&ldquo;萃取&rdquo;和&ldquo;沉淀&rdquo;其实是同时进行的。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">只有进行了足够的萃取，以及大量的资源和数据的集中、结构化、可智能配置化，我们可以说，中台的建设开始了。它将给前端输出千人千面的服务，让客户和我们紧密相连，也可以给后台源源不断的沉积智慧资产，从而满足企业数字化转型和数据大脑的打造。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">最终，中台的建设，将成为数字化转型时代最核心、最刚需的工作，我们作为行业的一份子，对此的推动将对国计民生、经济发展产生重要的积极影响，我们责任在肩、任重道远。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">文中题图基于VRF授权。</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\">&nbsp;</div>\n</div>\n</div>\n<div class=\"index-module_mediaWrap_213jB\">&nbsp;</div>\n</div>\n</div>\n</div>\n</div>', '明略科技吴明辉：建设智能时代的企业中台，是产业升级的刚需', null, 'inherit', null, null, null, '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '1', '0', '20', '1', '2021-09-01 16:40:36', '192.168.1.23', '1', '2021-09-01 16:51:46', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544401216171851784', '<p>导读：2015年阿里巴巴提出&ldquo;大中台，小前台&rdquo;的中台战略，通过实施中台战略找到能够快速应对外界变化，整合阿里各种基础能力，高效支撑业务创新的机制。阿里巴巴中台战略最早从业务中台和数据中台建设开始，采用了双中台的建设模式，到后来发展出了移动中台、技术中台和研发中台等，这些中台的能力综合在一起就构成了阿里巴巴企业级数字化能力。传统企业在技术能力、组织架构和商业模式等方面与阿里巴巴存在非常大的差异，在实施中台战略时是否可以照搬阿里巴巴中台建设模式？传统企业中台数字化转型需要提升哪些方面的基本能力呢？下面我们一起来分析分析。</p>\n<p><img src=\"http://192.168.1.23:8088/store/202109/01174417yWREWiP2.jpg\" /></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">00 中台能力总体框架</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">中台建设过程从根本上讲是企业自身综合能力持续优化和提升的过程，最终目标是实现企业级业务能力复用和不同业务板块能力的联通和融合。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">企业级的综合能力，一般包含以下四种：业务能力、数据能力、技术能力和组织能力，如图2-1所示。</span></p>\n<p><span class=\"bjh-p\"><img src=\"http://192.168.1.23:8088/store/202109/01174529pkX3seQX.jpg\" /></span></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">▲图2-1 企业中台数字化转型基本能力框架</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务能力主要体现为对中台领域模型的构建能力，对领域模型的持续演进能力，企业级业务能力的复用、融合和产品化运营能力，以及快速响应市场的商业模式创新能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据能力主要体现为企业级的数据融合能力、数据服务能力以及对商业模式创新和企业数字化运营的支撑能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">技术能力主要体现为对设备、网络等基础资源的自动化运维和管理能力，对微服务等分布式技术架构体系化的设计、开发和架构演进能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">组织能力主要体现为一体化的研发运营能力和敏捷的中台产品化运营能力，还体现为快速建设自适应的组织架构和中台建设方法体系等方面的能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">这些能力相辅相成，融合在一起为企业中台数字化转型发挥最大效能。接下来，我们一起来看看在不同的领域应该如何实现这些能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">01 业务中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">企业所有能力建设都是服务于前台一线业务的。从这个角度来讲，所有中台应该都可以称为业务中台。但我们所说的业务中台一般是指支持企业线上核心业务的中台。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务中台承载了企业核心关键业务，是企业的核心业务能力，也是企业数字化转型的重点。业务中台的建设目标是：&ldquo;将可复用的业务能力沉淀到业务中台，实现企业级业务能力复用和各业务板块之间的联通和协同，确保关键业务链路的稳定高效，提升业务创新效能。&rdquo;</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务中台的主要目标是实现企业级业务能力的复用，所以业务中台建设需优先解决业务能力重复建设和复用的问题。通过重构业务模型，将分散在不同渠道和业务场景（例如：互联网应用和传统核心应用）重复建设的业务能力，沉淀到企业级中台业务模型，面向企业所有业务场景和领域，实现能力复用和流程融合。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">图2-2是一个业务中台示例。在业务中台设计时，我们可以将用户管理、订单管理、商品管理和支付等这些通用的能力，通过业务领域边界划分和领域建模，沉淀到用户中心、订单中心、商品中心和支付中心等业务中台，然后基于分布式微服务技术体系完成微服务建设，形成企业级解决方案，面向前台应用提供可复用的业务能力。</span></p>\n</div>\n<div class=\"index-module_mediaWrap_213jB\"><img src=\"http://192.168.1.23:8088/store/202109/01174615WP90gdIo.jpg\" /></div>\n<div class=\"index-module_mediaWrap_213jB\">\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">▲图2-2 业务中台示例</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在技术实现上，中台的系统落地可以采用微服务架构。微服务是目前公认的业务中台技术最佳实现，可以有效提升业务扩展能力，实现业务能力复用。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">在业务建模上，中台领域建模可以采用领域驱动设计（DDD）方法，通过划分业务限界上下文边界，构建中台领域模型，根据领域模型完成微服务拆分和设计。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务中台可以面向前台应用提供基于API接口级的业务服务能力，也可以将领域模型所在的微服务和微前端组合为业务单元，以组件的形式面向前台应用，提供基于微前端的页面级服务能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务中台建设完成后，前台应用就可以联通和组装各个不同中台业务板块，既提供企业级一体化业务能力支撑，又可以提供灵活的场景化销售能力支撑。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">02 数据中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据中台与业务中台相辅相成，共同支持前台一线业务。数据中台除了拥有传统数据平台的统计分析和决策支持功能外，会更多聚焦于为前台一线交易类业务提供智能化的数据服务，支持企业流程智能化、运营智能化和商业模式创新，实现&ldquo;业务数据化和数据业务化&rdquo;。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">最近几年，数据应用领域出现了很多新的趋势。数据中台建设模式也随着这些趋势在发生变化，主要体现在以下几点。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第一，数据应用技术发展迅猛。近几年涌现出了大量新的数据应用技术，如NoSQL、NewSQL和分布式数据库等，以及与数据采集、数据存储、数据建模和数据挖掘等大数据相关的技术。这些技术解决业务问题的能力越来越强，但同时也增加了技术实现的复杂度。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第二，数据架构更加灵活。在从单体向微服务架构转型后，企业业务和数据形态也发生了很大的变化，数据架构已经从集中式架构向分布式架构转变。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第三，数据来源更加多元化，数据格式更加多样化。随着车联网、物联网、LBS和社交媒体等数据的引入，数据来源已从单一的业务数据向复杂的多源数据转变，数据格式也已经从以结构化为主向结构化与非结构化多种模式混合的方向转变。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">第四，数据智能化应用将会越来越广泛。在数字新基建的大背景下，未来企业将汇集多种模式下的数据，借助深度学习和人工智能等智能技术，优化业务流程，实现业务流程的智能化，通过用户行为分析提升用户体验，实现精准营销、反欺诈和风险管控，实现数字化和智能化的产品运营以及AIOps等，提升企业数字智能化水平。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">面对复杂的数据领域，如何建设数据中台管理并利用好这些数据？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">这对企业来说是一个非常重要的课题。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据中台的大部分数据来源于业务中台，经过数据建模和数据分析等操作后，将加工后的数据，返回业务中台为前台应用提供数据服务，或直接以数据类应用的方式面向前台应用提供API数据服务。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据中台一般包括数据采集、数据集成、数据治理、数据应用和数据资产管理，另外还有诸如数据标准和指标建设，以及数据仓库或大数据等技术应用。图2-3是2017年阿里云栖大会上的一个数据中台示例。</span></p>\n<p><span class=\"bjh-p\"><img src=\"http://192.168.1.23:8088/store/202109/011747452F91NAvK.jpg\" /></span></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">▲图2-3 数据中台示例（图参考：2017年阿里云栖大会）</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">综上所述，数据中台建设需要做好以下三方面的工作。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">一是建立统一的企业级数据标准指标体系，解决数据来源多元化和标准不统一的问题。企业在统一的数据标准下，规范有序地完成数据采集、数据建模、数据分析、数据集成、数据应用和数据资产管理。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">二是建立与企业能力相适应的数据研发、分析、应用和资产管理技术体系。结合企业自身技术能力和数据应用场景，选择合适的技术体系构建数据中台。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">三是构建支持前台一线业务的数据中台。业务中台微服务化后，虽然提升了应用的高可用能力，但是随着数据和应用的拆分，会形成更多的数据孤岛，会增加应用和数据集成的难度。在业务中台建设的同时，需要同步启动数据中台建设，整合业务中台数据，消除不同业务板块核心业务链条之间的数据孤岛，对外提供统一的一致的数据服务。用&ldquo;业务+数据&rdquo;双中台模式，支持业务、数据和流程的融合。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据中台投入相对较大，收益周期较长，但会给企业带来巨大的潜在商业价值，也是企业未来数字化运营的重要基础。企业可以根据业务发展需求，制定好阶段性目标，分步骤、有计划地整合好现有数据平台，演进式推进数据中台建设。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">03 技术中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">业务中台落地时需要有很多的技术组件支撑，这些不同技术领域的技术组件就组成了技术中台。业务中台大多采用微服务架构，以保障系统高可用性，有效应对高频海量业务访问场景，所以技术中台会有比较多的微服务相关的技术组件。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">一般来说，技术中台会有以下几类关键技术领域的组件，如API网关、前端开发框架、微服务开发框架、微服务治理组件、分布式数据库以及分布式架构下诸如复制、同步等数据处理相关的关键技术组件，如图2-4所示。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">1. API网关</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">微服务架构一般采用前后端分离设计，前端页面逻辑和后端微服务业务逻辑独立开发、独立部署，通过网关实现前后端集成。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">前台应用接入中台微服务的技术组件一般是API网关。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">API网关主要包括：鉴权、降级限流、流量分析、负载均衡、服务路由和访问日志等功能。API网关可以帮助用户，方便地管理微服务API接口，实现安全的前后端分离，实现高效的系统集成和精细的服务监控。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">2. 开发框架</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">开发框架主要包括前端开发框架和后端微服务开发框架。基于前、后端开发框架，分别完成前端页面逻辑和后端业务逻辑的开发。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">前端开发框架主要是面向PC端或者移动端应用，用于构建系统表示层，规范前后端交互，降低前端开发成本。</span></p>\n<p><span class=\"bjh-p\"><img src=\"http://192.168.1.23:8088/store/202109/01174926uKet7tng.jpg\" /></span></p>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">▲图2-4 技术中台关键技术领域</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">微服务开发框架用于构建企业级微服务应用。一般具备自动化配置、快速开发、方便调试及部署等特性，提供微服务注册、发现、通信、容错和监控等服务治理基础类库，帮助开发人员快速构建产品级的微服务应用。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">开发框架一般都支持代码自动生成、本地调试和依赖管理等功能。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">3. 微服务治理</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">微服务治理是在微服务的运行过程中，针对微服务的运行状况采取的动态治理策略，如服务注册、发现、限流、熔断和降级等，以保障微服务能够持续稳定运行。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">微服务治理主要应用于微服务运行中的状态监控、微服务运行异常时的治理策略配置等场景，保障微服务在常见异常场景下的自恢复能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">微服务治理技术组件一般包括服务注册、服务发现、服务通信、配置中心、服务熔断、容错和微服务监控等组件。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">常见的微服务治理有Dubbo、Spring Cloud和Service Mesh等技术体系。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">4. 分布式数据库</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分布式数据库一般都具有较强的数据线性扩展能力，它们大多采用数据多副本机制实现数据库高可用，具有可扩展和低成本等技术优势。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分布式数据库一般包括三类：交易型分布式数据库、分析型分布式数据库和交易分析混合型分布式数据库。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">交易型分布式数据库用于解决交易型业务的数据库计算能力，它支持数据分库、分片、数据多副本，具有高可用的特性，提供统一的运维界面，具备高性能的交易型业务数据处理能力。主要应用于具有跨区域部署和高可用需求，需支持高并发和高频访问的核心交易类业务场景。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分析型分布式数据库通过横向扩展能力和并行计算能力，提升数据整体计算能力和吞吐量，支持海量数据的分析。主要应用于大规模结构化数据的统计分析、高性能交互式分析等场景，如数据仓库、数据集市等。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">交易分析混合型分布式数据库通过资源隔离、分时和数据多副本等技术手段，基于不同的数据存储、访问性能和容量等需求，使用不同的存储介质和分布式计算引擎，同时满足业务交易和分析需求。主要应用于数据规模大和访问并发量大，需要解决交易型数据同步到分析型数据库时成本高的问题，需要解决数据库入口统一的问题，需要支持高可用和高扩展性等数据处理业务场景。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">5. 数据处理组件</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">为了提高应用性能和业务承载能力，降低微服务的耦合度，实现分布式架构下的分布式事务等要求，技术中台还有很多数据处理相关的基础技术组件。如：分布式缓存、搜索引擎、数据复制、消息中间件和分布式事务等技术组件。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分布式缓存是将高频热点数据集分布于多个内存集群节点，以复制、分发、分区和失效相结合的方式进行维护，解决高并发热点数据访问性能问题，降低后台数据库访问压力，提升系统吞吐能力。典型的开源分布式缓存技术组件有Redis。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">搜索引擎主要解决大数据量的快速搜索和分析等需求。将业务、日志类等不同类型的数据，加载到搜索引擎，提供可扩展和近实时的搜索能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据复制主要解决数据同步需求，实现同构、异构数据库间以及跨数据中心的数据复制，满足数据多级存储、交换和整合需求。主要应用于基于表或库的业务数据迁移、业务数据向数据仓库复制等数据迁移场景。数据复制技术组件大多采用数据库日志捕获和解析技术，在技术选型时需考虑数据复制技术组件与源端数据库的适配能力。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">消息中间件主要适用于数据最终一致性的业务场景，它采用异步化的设计，实现数据同步转异步操作，支持海量异步数据调用，并通过削峰填谷设计提高业务吞吐量和承载能力。它被广泛用于微服务之间的数据异步传输、大数据日志采集和流计算等场景。另外，在领域驱动设计的领域事件驱动模型中，消息中间件是实现领域事件数据最终一致性的非常关键的技术组件，可以实现微服务之间的解耦，满足&ldquo;高内聚，松耦合&rdquo;设计原则。典型的开源消息中间件有Kafka等。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分布式事务主要是解决分布式架构下事务一致性的问题。单体应用被拆分成微服务后，原来单体应用大量的内部调用会变成跨微服务访问，业务调用链路中任意一个节点出现问题，都可能造成数据不一致。分布式事务是基于分布式事务模型，保证跨数据库或跨微服务调用场景下的数据一致性。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">分布式事务虽然可以实时保证数据的一致性，但过多的分布式事务设计会导致系统性能下降。因此微服务设计时应优先采用基于消息中间件的最终数据一致性机制，尽量避免使用分布式事务。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">技术中台是业务中台建设的关键技术基础。在中台建设过程中，可以根据业务需要不断更新和吸纳新的技术组件，也可以考虑将一些不具有明显业务含义的通用组件（如认证等），通过抽象和标准化设计后纳入技术中台统一管理。为了保证业务中台的高性能和稳定性，在技术组件选型时一定要记住：尽可能选用成熟的技术组件。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">关于作者：</span><span class=\"bjh-strong\">欧创新</span>，某大型保险公司架构师，拥有十多年的软件架构设计经验。热衷于DDD、中台和分布式微服务架构设计。在DDD、中台和分布式微服务架构设计方面有深厚的积累，擅长分布式微服务架构设计。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">邓頔</span>，某大型保险公司高级工程师，全国青年岗位能手。致力于基于DDD的企业级中台微服务架构改造实践，精通前端开发相关技术栈，拥有丰富的企业级微前端实战经验。</span></p>\n</div>\n</div>\n</div>\n</div>\n</div>', '终于有人把业务中台、数据中台、技术中台都讲明白了', null, 'inherit', null, null, 'yewuzhongtai', '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '0', '1', null, '1', '2021-09-01 17:39:04', '192.168.1.23', '1', '2021-12-20 14:19:19', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544401257229893635', '<div>\n<blockquote>\n<p>企业级能力复用平台<br />&mdash;&mdash;摘自ThoughtWorks王健博客</p>\n</blockquote>\n<blockquote>\n<p>中台的本质是提炼各个业务线的共性需求。<br />&mdash;&mdash;摘自《阿里巴巴中台战略思想》</p>\n</blockquote>\n<p>按我自己的理解，作个定义：</p>\n<blockquote>\n<p>业务中台是提炼各业务线的共性需求，沉淀相对稳定的可共享的业务服务能力，支持快速多变的前台业务需求的能力平台。</p>\n</blockquote>\n<p>业务中台应该是强调整合与复用（共享）的。顺应的是以不变应万变的思路。<br />业务中台应该是快速响应前台需求的，支持新业务热启动的。</p>\n<div>\n<div>\n<h1>二、概念区分</h1>\n<p>前 台 &ne; 前 端<br />后 台 &ne; 后 端<br />中 台 &ne; 平 台</p>\n<blockquote>\n<p>中台和平台都是某种共性能力，区分两者的重点一是看是否具备业务属性，二是看是否是一种组织。中台是支持多个前台业务且具备业务属性的共性能力组织，平台是支持多个前台或中台业务且不具备业务属性的共性能力。<br />&mdash;&mdash;摘自《网易汪源：所有的中台都是业务中台》</p>\n</blockquote>\n<p>前端、后端、平台，更多的是一个技术的概念。运行在客户端设备上的如浏览器、手机、平板、电脑上的程序，我们说是前端程序。运行在客户（用户）看不见的（数据中心/云）服务器上的程序，我们说是后端程序。平台有很多的延伸意义，但在软件技术上，平台多是指能快速帮助前端或后端程序开发的半成品中间件。<br />　　前台、后台、中台，更多的是公司组织结构、公司业务运营的概念。我们平时可能会说，公司前台有客服接待，公司后台有行政支持，公司中台有运营调度。 前台系统、中台系统、后台系统，都是可以包括前端程序与后端程序的。<br />　　前台员工是直接为公司创收的人。 前台主要由面向客户的角色组成。 因此，在财务公司或投资银行，前台部门可能包括销售和交易<br />　　中台部门的角色连接着前台和后台，为前台提供业务支持，在创收方面比后台发挥更直接的作用。<br />　　后台部门由不直接为业务创收的区域组成，但提供重要的支持和管理。<br />　　前台、中台、后台是相互支撑体系。<br />前台系统：面向客户，关注客户需求，与用户体验直接相关。一般是客户直接操作的应用系统，或者直接接触客户的一线销售人员客服人员操作的应用系统。<br />中台系统：面向业务领域，关注行业趋势，关注共享创新，与市场响应直接相关，快速响应前台需求。<br />后台系统：综合支持。<br />业务中台的&ldquo;中&rdquo;字，本质上强调了这些应用系统是为谁服务的。</p>\n<h1>三、康威定律</h1>\n<blockquote>\n<p>Organizations which design systems are constrained to produce designs which are copies of the communication structures of these organizations.<br />　　　　　　　　　　　　　　　&mdash;&mdash; Melvin Conway(1967)</p>\n</blockquote>\n<p>中文直译大概的意思就是：设计系统的组织，其产生的设计等同于组织之内、组织之间的沟通结构。<br />或者说：软件系统的架构，反映了公司的组织结构</p>\n</div>\n<img src=\"http://192.168.1.23:8088/store/202109/01182306voBB06Il.webp\" alt=\"\" width=\"792\" height=\"218\" /></div>\n<div>\n<div>\n<div>\n<article class=\"_2rhmJa\">\n<p>业务中台是需要遵循公司的组织结构与运营体系的。</p>\n<h1>四、为什么要大中台、小前台？</h1>\n<p>市场的发展变化越来越快了，以前我们想的是&ldquo;我们可以做什么？&rdquo;，现在我们需要想的是&ldquo;客户需要什么？&rdquo;。这两个问句看似差别不大，实质是从产品转向客户。以前是以产品为中心，现在是以客户为中心。以前是我们有什么资源可以为客户提供什么产品，现在是客户有什么困扰需要我们提供什么服务帮助解决。<br />　　<strong>为什么是这样变化的呢？</strong><br />　　可能这就是科技的进步所带来的魔力。在以前客户的需求我们未必能及时的知道，即使清楚也未必能及时响应，即使想及时响应也未必有资源响应。而现在PPT都能造车的时代，资源可能不再是限制，能不能理解客户的需求，以最快的速度响应，获得市场先机，构建良性循环的生态，变得特别的重要。<br />　　这里小前台的&ldquo;小&rdquo;，说的是轻量，能快速构建，但数量可能非常多。客户需要什么就造什么。这里大中台的&ldquo;大&rdquo;，说的是沉淀，沉淀下来的可供多业务线共享的业务服务能力。&ldquo;大&rdquo;&ldquo;小&rdquo;与开发资源、代码数量无关。</p>\n<h1>五、建设业务中台的挑战？</h1>\n<p>建设业务中台要求对业务领域有更多的理解，要懂业务。需要了解的不只是应用系统内的业务流程，而且包括应用系统外的业务流程，例如企业的资金流、物流，而不止是信息流。需要了解行业动态，需要对业务流程的优化有想法，对业务不能知其然，而不知其所以然。<br />　　参与业务中台建设的，不应该只是技术专家，也应该有业务专家。根据康威定律，业务中台的建设与组织架构密切相关，也需要组织的高层支持。<br />　　<strong>业务中台能不能采购？</strong><br />　　我的想法是，业务中台的一小部分功能可以采购，绝大部分的功能不能采购。从业务中台的定义上来说，它是与一家公司的主营业务密切相关的，而即使是同一行业的同类型公司，其组织结构、经营战略、企业文化、业务范围、服务模式基本都是不同的，而正是这些不同之处才构成了这家公司的市场竟争力，这些不同之处也必然导致已有的IT资产的不同。同类型公司可共享的业务服务能力可能是相似的，但支撑它的IT资产却不能相同，因为需要匹配这家企业的传承，才有竟争力。可以通过组织变革、系统演变的方式得以实现。采购回来的系统只是在模仿成熟的同类型企业，是无法有效落地的，即使自废武功强行推动落地，也是不会有竟争力的。<br />　　那哪些功能是可能采购的呢？有两块功能应该是业务中台里相对通用的，有可能采购的。一是能力地图，二是BOSS（Business Operations Supporting System）业务运营支持系统。</p>\n</article>\n<div class=\"_1kCBjS\">\n<div class=\"_18vaTa\">\n<div class=\"_3BUZPB ant-dropdown-trigger\">\n<div class=\"_2Bo4Th\">&nbsp;</div>\n</div>\n</div>\n</div>\n</div>\n<br />作者：红茶码字<br />链接：https://www.jianshu.com/p/4e8eb7e21dd2<br />来源：简书<br />著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。</div>\n</div>\n</div>', '业务中台-概念', null, 'inherit', null, null, '', '1544356231896547328', 'chapter', '1533544727530094592', null, null, '1', '0', '1', null, '1', '2021-09-01 17:39:13', '192.168.1.23', '1', '2021-12-20 14:19:42', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544401654996713472', '<p>什么是业务中台?</p>\n<p>　　中台是相较于前台、后台而产生的一个概念，业务中台以企业为服务对象，整合后台资源，转换为方便前台使用的功能，可以说，业务中台实现了后台资源到前台服务的转化，可以方便企业，快速挖掘、响应、引领用户需求。</p>\n<p>　　业务中台是构建构建全渠道一体化运营管理的平台，它将数字技术运用到企业各个领域，改变运用和管理方式，让企业可以更好地为客户创造价值。</p>\n<p>　　业务中台的作用</p>\n<p>　　在激烈的市场竞争中，赢得用户便能赢得市场。敏锐的企业，自当以&ldquo;客户至上&rdquo;为基础，以科技以动力，建立业务中台，通过平台化的力量先发制人，抢占市场先机。</p>\n<p>　　为什么要打造业务中台?方便企业及时、统一管理数据信息，是业务中台的一大作用。企业业务开展受阻，或者没法及时了解用户需求，可能都是因为缺乏数据管理及数据共享。借助业务中台，我们可以对业务数据进行统一管理、实时共享，方便业务人员更快了解业务变化、感知用户需求。</p>\n<p>　　业务中台的作用不仅局限于数据管理，它可以帮助企业进行全局优化。碎片化的业务数据，往往让我们找不到头绪。产品研发部门没法及时对接营销人员，营销人员也没法获得一手信息。在业务中台的支撑下，企业可以从业务全局进行把控，提高营销效率与业务成交率。</p>\n<p>　　业务中台的作用，还包括帮助企业完成数字化转型升级。市场环境千变万化，业务中台的打造可以方便企业进行用户管理中心及营销活动管理，快速响应用户需求，降低业务开发成本，提升服务品质。</p>\n<p>　　时代发展日新月异，业务中台的作用愈发凸显，如何借助业务中台迎战市场成为摆在企业发展面前的重要课题。希望更多企业能以业务中台为依托，进行资源整合管理，进行资源合理分配，用业务连接数据，驱动企业转型升级。</p>', '互联网时代，企业如何借助业务中台迎战市场？', null, 'inherit', null, null, '', '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '0', '1', null, '1', '2021-09-01 17:40:48', '192.168.1.23', '1', '2021-12-20 14:20:41', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544414732358434826', '<p><img src=\"http://192.168.1.23:8088/store/202109/011853410c1dqYGv.png\" width=\"1112\" height=\"952\" /></p>\r\n<p>业务中台是企业实现各业务板块之间链接和协同，持续提升业务创新效率，确保关键业务链路的稳定高效和经济性兼顾的思想体系，包含了技术和组织两大部分，通过&ldquo;方法+工具+业务理解&rdquo;加以实现。</p>\r\n<p>特步通过分销零售全渠道业务中台的建设，实现了全国7000+门店的商品库存交易实时感知，业务数据统一可视，有效的提升了全渠道会员营销的效率。</p>', '业务中台技术解决方案', null, 'inherit', null, null, '', '1544356231896547328', 'chapter', '1533544727530094592', null, null, '2', '2', '3', null, '1', '2021-09-01 18:32:46', '192.168.1.23', '1', '2021-12-20 14:21:12', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544414748162572291', '<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\"><span class=\"bjh-strong\">编者按：</span>本文转载自网易副总裁，网易杭州研究院执行院长汪源的个人公众号<span class=\"bjh-strong\">&ldquo;冷技术热思考&rdquo;</span>（欢迎搜索关注）。上周的云创峰会上，汪源承诺会写三篇文章，力求说清楚什么是中台，什么时候要考虑建中台，怎么建中台。今天是第一篇，目标是厘清什么是中台。<span class=\"bjh-strong\">以下为原文</span><span class=\"bjh-strong\">：</span></span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">中台的概念一热，很多似是而非的东西都在往中台的概念上凑，一下子出现很多中台，如业务中台、数据中台、技术中台、算法中台、移动中台等等。特别是很多原来称作平台的，现在也都摇身一变成了中台，赶时髦。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">一个概念太过宽泛是不利的，如果随随便便都是中台，必然导致很多所谓的中台项目失败，导致中台无用论。所以有必要对中台的概念做一个比较准确的定义。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">什么是中台？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>要定义中台，重要的是要能比较明确的区分中台和平台。<span class=\"bjh-strong\">中台和平台都是某种共性能力，区分两者的重点一是看是否具备业务属性，二是看是否是一种组织。</span>中台是支持多个前台业务且具备业务属性的共性能力组织，平台是支持多个前台或中台业务且不具备业务属性的共性能力。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>为什么要强调中台必须具备业务属性？可以来看一个例子。我们可以分析什么叫数据中台。如果一个企业把所有业务的数据都存储在Oracle里，我们能说这个Oracle数据库是数据中台吗？显然大家都会说不是（否则中台不是几十年的老古董了？）。那么现在很多企业换成了Hadoop，所有业务数据都在一个Hadoop集群里，能说是数据中台吗？显然也不是，这个Hadoop无非跟原来的Oracle一样存了一堆数据而已。有人说这是因为这个Hadoop集群只是一个系统，中台必须是一个组织。那么我们再加上建设和维护这个Hadoop集群的团队，整个加起来就是中台了吗？</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>仍然不是，因为这个团队是不需要为业务负责的，不具备业务属性。而现在大家比较公认的数据中台，指的是确保OneID、OneData得以实现的组织，使得数据不再是各前端业务独立管理，而是通过统一的团队在数据标识、指标、数据仓库等方面实现了跨业务的整合。之所以这样大家会认为是名符其实的数据中台，是因为指标一定是面向业务的，数据仓库的建设一定也包含了一些业务逻辑。所以那个大大的Hadoop并不是数据中台，而是大数据平台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">我们还可以看到是中台还是平台与所在的业务环境相关</span>。同样的能力对A业务来说可能具备业务属性从而是中台，但对B业务来说没有业务属性从而是平台。比如说IDC建设和运维对AWS来说可谓至关重要的业务中台，而对绝大多数企业来说只能说是平台。PaaS平台对SaaS厂商来说是业务中台，但对绝大多数企业来说也只能说是平台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>所以，不具备业务属性的能力，即便是共性的，即便有一个专职的部门在做，即便对业务非常重要，也不能称之为中台，而还是应该称之为平台。否则就会出现很多与业务八杆子打不着的各种中台，混淆视听。因此，应该说所有中台都是业务中台，没有别的类型的中台。数据中台、搜索中台、内容中台、零售中台等等，都是特定形式的业务中台，也还是业务中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>中台的定义还要求以下两点：</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">1. 中台是一种共性能力组织，支持了多个业务。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">2. 中台支持的是多个前台业务。</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>第一点不用多说，只支持一个业务的能力至少暂时不能称为中台（当然可以有进一步建设为中台的规划或可能性）。之所以强调第二点是因为有太多的公司的业务不是靠前台打下来的，而是靠财务后台做账做出来的。理论上可以有，但我们应该支持这样增强做账能力的中台吗？对于那些专业提供做账服务的公司，还真需要这样的中台，但这时做账就是它的前台业务了。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>中台的定义并没有限定中台的建设层次。中台可以在很多个层次上建设，并不是说必须是企业或集团级别的。BU和BG层面建设中台往往更常见，也通常很有意义。即便更小的层面比方几十人的小部门，中台也很有价值。比如一个小团队也可以做电商业务，这时如果有一套好用的电商中台那就帮了大忙了，而事实上业界也有很多公司在提供这样的能力。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">典型的中台有哪些？</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>除了常说的业务中台，我们还经常听到数据中台、用户中台、搜索中台、推荐中台、内容中台、技术中台、算法中台、移动中台、研发中台等等一系列的XX中台的说法，但这些中台未必都是真正的中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>前面已经说过，广义上讲业务中台包含了所有中台，不同的XX中台都是业务中台的细分方向，反映的是该中台在业务领域或者技术上的某些特征。但大家往往只用业务中台来指称在线业务中台。基于这个假定，当前典型的真正的中台大致只有以下几个：</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">01</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">（狭义的）业务中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>一般指在线业务为典型特征的中台。在OLDI（Online Data-Intensive）时代，越来越多的企业的核心业务都是在线业务，因此把在线业务中台简称为业务中台。但对那些不是以在线业务为主的企业，它需要的业务中台可能就不是在线业务中台了，而是数据中台或别的什么中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">02</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">数据中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>一般指以数据采集、数据集成、数据治理，指标体系和数据仓库统一建设等数据管理活动为典型特征的中台。同样，在OLDI时代，数据中台越来越重要。狭义的业务中台也就是在线业务中台负责OLDI中的OL（Online），数据中台负责OLDI中的DI（Data-Intensive）。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">03</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">用户中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>用户中台可以认为是一种特殊的数据中台，一般以用户ID统一、全域用户画像建设、全域会员体系建设等为典型特征。用户中台很通用，比更广义的数据中台往往更常见。很多企业没能力建设更全面的数据中台，但建设了会员中心等用户中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">04</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">内容中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>内容中台往往也可以认为是一种特殊的数据中台，一般以内容的采买、内容爬取、内容的加工处理、内容安全保障等为典型特征。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">05</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-p\">搜索推荐中台</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>这两个中台比较像，因为搜索和推荐的技术比较相似。这两个中台一般是为推荐和搜索系统提供一套相对标准的工作流程，同时支持流程各环节的可定制能力，从而支持多个前端推荐搜索业务的快速开发。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>当然还有很多其他根据业务需要建设的中台，比方说对美团/饿了吗来说，本地配送体系可以建设为中台，前提是这个体系不仅用于送餐。在电商行业，往往渠道运营用单独的系统和团队来支持各个BU（一般按品类分），也可以说是中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p><span class=\"bjh-strong\">技术/算法/移动/研发中台当前基本不存在</span></p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>一般来说，没有技术中台，这是因为以技术为典型特征，又具备业务属性的中台太难找了，没有一个很好的案例。可以看看业界所谓的技术中台，包含了从IaaS到中间件等一系列在线业务技术，但能称这些为中台吗？可以把里面每个模块都拿出来分析，保证你找不到一个跟业务相关的字眼。所以这些并不是中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>其实A公司也只说业务中台和数据中台。其他的中台都是某些咨询公司或不明真相的群众牵强附会造出来的。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>并不是说不能有技术中台，而是没必要特别的称作技术中台而非业务中台。对于提供技术服务的企业，它的业务前台就是技术前台，它的业务中台就是技术中台。比方说SaaS厂商的中台往往是个PaaS，这时这个PaaS可以称之为技术中台，但也是这个产商的业务中台。同样的一个PaaS，对于大多数别的企业，就变成只是支撑业务但本身没有业务属性的技术平台了。所以，为了避免混淆，导致把平台说成中台，不如坚持认为不存在技术中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>同样的道理，移动中台似乎只对做移动应用开发业务（比如说很多外包产商）的企业来说才是中台，但对这些企业来说移动中台也就是它的业务中台，所以也宁可不搞出一个移动中台这样的新名词为好。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>那么，什么才是研发中台？H公司有专职的研发部负责支持所有前端业务的研发，让听得见炮火的人指挥战斗，可能是名副其实的研发中台。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>总之一句话，当前并没有好的技术 / 算法 / 移动 / 研发中台，那些出来宣传这些中台的要么是自己搞不清中台概念，糊涂，要么就是骗子。不过没有这些中台说明整个行业在这方面的积累还不够，是一种不足，希望过几年有真正的这些中台出来。</p>\n</div>\n<div class=\"index-module_textWrap_3ygOc\">\n<p>这是关于中台系列的第一篇，目的是厘清什么是中台，什么不是中台。下一篇将讨论什么时候要建中台及怎么建设中台，敬请期待。</p>\n<p>&nbsp;</p>\n</div>', '所有的中台都是业务中台', null, 'inherit', null, null, null, '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '2', '1', null, '1', '2021-09-01 18:32:50', '192.168.1.23', '1', '2021-09-01 19:04:37', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544422560041189382', '<p>编辑导语：&ldquo;中台&rdquo;一词近来经常被提及。作为软件平台的演化，中台在为产品服务、不断提升效率时，我们也可以思考&mdash;&mdash;中台的价值究竟体现在何处？内容中台与其他中台的相同点是什么？它自身的能力体系划分又可以如何界定？</p>\n<p><img src=\"http://192.168.1.23:8088/store/202109/01204936x2711mcl.jpg\" /></p>\n<h2 id=\"toc-1\">一、前言</h2>\n<p>距离上一篇中台的文章已经差不多过去一年了，在700+需求后，我开始逐渐去思考，中台的能力是什么？中台对业务的价值在哪里？中台如何去更进一步？</p>\n<h2 id=\"toc-2\">二、中台</h2>\n<p>中台概念相信大家都多多少少的知道一些，简单点说，就是将通用的业务抽离，把这个整合成一个产品，这个产品为其他需要用到这个业务的产品提供服务，避免重复造轮子。</p>\n<h3>内容中台</h3>\n<p>和其他中台一样，既然是为了避免重复造轮子，那么内容中台就需要将各业务的的内容模块的通用能力抽离，形成内容中台，为其他产品提供内容服务，从而降低成本。同时，内容中台需要不断提升自己的中台能力，从而提升效率与服务质量。</p>\n<h2 id=\"toc-3\">三、内容中台能力体系</h2>\n<p>内容中台能力体系主要分为三个：</p>\n<ol>\n<li>内容基础体系；</li>\n<li>内容理解体系；</li>\n<li>内容质量体系。</li>\n</ol>\n<p>当然这个体系的划分没有一定的标准，大家也可以尝试从业务流程上去划分能力体系。</p>\n<h3>1. 内容基础体系</h3>\n<p>内容基础体系可以说是整个内容中台中最基础、最基本的体系。缺少这个，可以说整个内容中台就是空谈。</p>\n<p>内容基础体系主要负责的是内容标准化建设，包括入库、质检、审核、安全等全链路产品能力建设及优化，对平台效率和合理性负责。</p>\n<p>将内容从生产到消费整体流程进行拆解，可以简单的分为入库、处理、出库。那么抽出最基本的元素，就是入库、出库，也就是我生产完，直接消费，这样就完成了内容的任务。</p>\n<p>内容基础体系的核心就是入库、出库。</p>\n<p>入库就是把内容从外部获取过来的这样一个过程，常见的做法有爬虫、RSS及自己平台生产。</p>\n<p>出库就是输出可用内容池子或者内容接口的过程。这个可用的内容显然不是入库的所有内容，需要剔除一部分不符合平台规范的东西，特别要注意可能带来风险的内容。</p>\n<p>内容基础体系的核心指标就是采集数、审核数、审核效率以及稳定性。</p>\n<p>中台产品需要制定入库的规则，包括资源的分配策略、爬虫监控等。产品还需要对审核流程进行优化，将流程进行拆解抽离，一个模块只干一件事情，从而最大化效率，进而提升整个基础体系的效率。比如审核只负责安全，通过后才进入下一个环节。</p>\n<h3>2. 内容理解体系</h3>\n<p>内容理解体系是中台的大脑，缺少这个，内容中台输出的内容可能有点傻傻的。想象一下，你去买衣服，但店家把所有的衣服都包好随意丢在地上，当然你可能一眼就看到了你想要的，但我想大多数时候，大部分人都会直接离开这家店。</p>\n<p>内容理解体系就像是按照衣服的特征，比如袖子长度、厚度、穿着位置等做了个分类，分门别类进行存放、展示。</p>\n<p>回到内容上，内容理解体系，主要是对内容进的发布源、内容、发布时间等做打标策略，提取内容的特征后，就可以通过这些特征可以把难以理解的内容变成一个个小标签。</p>\n<p>作为中台产品，需要规定内容分类的颗粒度与特征要素，颗粒度通常细化到二级分类，比如汽车&mdash;宝马、财经&mdash;IPO。当然可以用更细颗粒度的分类方法，不过越细，分类越难，维护成本越高。</p>\n<p>制定好规则后就需要提供相应的分类工具供支持部门使用，主要是对结果进行复核确认，并提高算法准确度。内容理解技术主要用的是文本聚类和文本语义识别，计算出各特征的权重，取权重大于特定数值的特征作为内容的标签，从而实现内容理解的目的。</p>\n<h3>3. 内容质量体系</h3>\n<p>内容质量体系的目的就是将内容进行&ldquo;质量&rdquo;分类、发现，这里的质量和内容战略有关，将符合战略的优质文章进行挖掘，从而达成平台的目标。</p>\n<p>那这一块一是通过前期的数据和经验积累，二是通过内容分发策略的配合。最终通过内容数据与平台战略不断修正质量策略，达到提前挖掘优质内容的目的，并提升个业务的内容分发水平。</p>\n<h2 id=\"toc-4\">四、结语</h2>\n<p>内容中台能力体系就说到这吧，五一临近，希望可以在假期建立属于自己的中台产品方法论，也欢迎大家来一起探讨~</p>\n<p>&nbsp;</p>\n<p>作者：宇觞醉月；公众号：阿宅的产品笔记</p>\n<p>本文由 @宇觞醉月 原创发布于人人都是产品经理，未经作者许可，禁止转载。</p>\n<p>题图来自Unsplash，基于CC0协议。</p>', '千万级内容中台能力体系', null, 'inherit', null, null, null, '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '8', '8', null, '1', '2021-09-01 19:03:52', '192.168.1.23', '1', '2021-09-01 20:49:40', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1544422567918092296', '<p>在外部对手环伺、内部组织架构新调整大背景下, &ldquo;企鹅号&rdquo;将如何玩转内容生态?</p>\n<p>2018年9月30日，腾讯对组织架构进行了公司历史上新一轮的调整。在新成立的平台与内容事业群（PCG）中，整合了原社交网络事业群（SNG）、原移动互联网事业群（MIG）、原网络媒体事业群（OMG）中，与社交平台、流量平台、数字内容、核心技术等高度关联且具有高融合性的板块，使其成为了一个整体。</p>\n<p>可以说，在腾讯本轮架构调整中，两个新成立的BG（云与智慧产业事业群（CSIG）、平台与内容事业群（PCG））被给予厚望，分别承担着消费互联网与产业互联网生态融合、社交与内容生态创新的重要探索。</p>\n<p>社交与内容生态的融合创新，这个将是PCG未来的命题，如何打造？在刚刚结束的腾讯全球合作伙伴大会上，腾讯副总裁林松涛分享腾讯内容生态布局，并阐述了腾讯以内容贯穿消费互联网的详细计划。</p>\n<p>&ldquo;这是在去年腾讯的大内容战略的一次升级，腾讯已经形成了内容生态的总体布局，既以微信、QQ和QQ空间形成用户平台；以腾讯新闻、微信公众平台、微信看一看、QQ看点、快报、浏览器等综合信息流产品，腾讯视频、微视等视频影音产品，共同形成内容产品矩阵；而<strong>企鹅号定位为内容中台</strong>。&rdquo;</p>\n<p><img src=\"http://192.168.1.23:8088/store/202109/01205159NiKJq5bZ.jpg\" /></p>\n<p>1</p>\n<p><strong>打造内容中台？</strong></p>\n<p>值得注意的是，&ldquo;企鹅号&rdquo;首次公开正式对外公布了其&ldquo;内容中台&rdquo;的平台定位，为什么？</p>\n<p>在上一段中，筱瞧来了提炼出几个关键词：平台、矩阵和中台。深入分析，你会发现，这个大计划中其实是分三个层次的，其中：</p>\n<p>第一层为<strong>用户平台</strong>，包括微信、QQ和QQ空间等，这个是腾讯内容生态的基础用户平台，是一个巨大的流量池；</p>\n<p>第二层为<strong>内容产品</strong>，包括腾讯新闻、微信公众平台、微信看一看、QQ看点等；这一部分你也可以理解为，它既是腾讯的内容产品矩阵，也是企鹅号&ldquo;一点接入，全平台分发&rdquo;的内容触达用户渠道；</p>\n<p>第三层就是&ldquo;内容中台&rdquo;，&ldquo;企鹅号&rdquo;作为腾讯的内容中台，它其实更像一个内容调度工作的中枢系统，影响着腾讯大内容生态海量内容和流量的去向，将更多优质的内容触达给真正需要的用户。</p>\n<p>筱瞧认为，这个&ldquo;内容中台&rdquo;才是核心武器。去年，当企鹅号宣布&ldquo;百亿计划&rdquo;扶持内容创业者时，筱瞧曾撰文《拿300亿去切万亿内容大蛋糕，你和你的企鹅号够班麽》，这篇文章其实就提到了类似中台的概念，而在这次大会上，筱瞧认为，企鹅号的&ldquo;内容中台&rdquo;定位愈发清晰和落地起来了。</p>\n<p>&ldquo;内容中台&rdquo;为什么重要，因为以目前的内容生态环境来说，吸引内容创业者的不再是巨头&ldquo;吆喝&rdquo;的重金扶持，而是，内容平台方如何让内容创作者在你的平台上可以产生源源不断提供优质内容的动力。他们不希望平台只是作为一个内容分发的渠道，而创作者永远不知道他们的精准用户在哪里，哪个渠道真正适合他们？用户希望看哪些内容？我如何更懂用户？</p>\n<p>这正是&ldquo;内容中台&rdquo;的历史使命。</p>\n<p>企鹅号这次全新发布的APP其中一个重要的功能就是通过后台大数据分析，提供优秀的数据工具产品来帮助内容创业者更好了解他的用户，得到更多的创作思路和灵感。</p>\n<p>另一方面，通过内容标签和人工运营相结合的方式，企鹅号可以将好内容与合适的内容平台进行匹配，真正让创作者只专注做内容，把渠道分发的事情放心交给企鹅号，让好内容被分发到真正找到适合的渠道，实现流量和收益的最大化。</p>\n<p>2</p>\n<p><strong>内容生态打法迭代</strong></p>\n<p>作为内容中台，企鹅号想要聚集更多的优秀内容创业者产出更多的好内容，则需要生态的打法。</p>\n<p>&ldquo;企鹅号目前已经有250多万作者，日流量同比增长300%以上，年收入超过百万的创作者超过1000家，创作者单月收入最高超过3000万。&rdquo; 腾讯内容平台部副总经理、腾讯短视频产品部副总经理陈鹏表示。面向未来，企鹅号发布了&ldquo;能力升级、商业升级和创作者生态升级&rdquo;的三大升级政策。</p>\n<p>在能力升级方面，企鹅号不断强化流量优势，在过去的一年中，除了传统的几大平台包括微信看一看、QQ浏览器、腾讯视频等，目前新增了微视和yoo视频两大短视频内容新平台，进一步扩展了内容分发矩阵，实现了从社交平台到新闻资讯平台、长视频、短视频，全面的覆盖。</p>\n<p>在商业升级方面，企鹅号将提供多元的扶持方案。其中，企鹅号的补贴政策将向原创、独家、稀缺品类和高消费时长的优质内容倾斜，在内容分成政策上则将引入更多原生广告形态。</p>\n<p>而在精品内容上，企鹅号重磅推出TOP计划，面向新赛道新平台联合出品优质内容，重点扶持潜力型创作者。企鹅号&ldquo;TOP计划&rdquo;旨在通过更高收益、更多模式、更多流量三方面激发创作力，联合潜力创作者出品优质内容, 不埋没每一个潜力创作者。</p>\n<p>据筱瞧了解，TOP计划将提供不低于50亿的专项内容创作基金、超100亿的全平台日流量，重点扶持超10000个潜力型创作者。</p>\n<p>在创作者生态升级方面，企鹅号将带来创作工具、创作空间、创作成长和创作投资的全面升级，重点聚焦创作能力、版权能力、二次创作能力和社群能力的打造。首次发布具有创作、成长及运营管理功能的企鹅号APP，提供大数据热点分析、线上培训交流、图文视频发布等功能。</p>\n<p>企鹅号还特别增强内容版权化能力，提供更强的保护机制和更多追偿手段，同时引入更多原创版权，开放二次创作权利，保证内容合规创作和原创作者权益。</p>\n<p>与此同时，企鹅号还升级了社群能力，将提供从线上APP到线下的培训计划，聚集各品类资源，促进创作者相互学习和交流。通过WeSpace打造线下拍摄、培训和聚会文创的集合体，企鹅号将为创作者提供一站式拍摄和后期设备，结合腾讯特色的IP场景、用户自定义场景，帮助创作者们创作更优质的短视频内容。目前，WeSpace腾讯短视频空间主要布局深圳、北京、上海等10多个城市。</p>', '三位一体”内容生态，企鹅号为什么要做“内容中台”？', null, 'inherit', null, null, null, '1544356231896547328', 'chapter', '1533544727530094592', null, null, '0', '4', '2', null, '1', '2021-09-01 19:03:54', '192.168.1.23', '1', '2021-09-01 20:52:56', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545223091294748672', '世界知识产权组织在1977年版的《供发展中国家使用的许可证贸易手册》中，给技术下的定义：“技术是制造一种产品的系统知识，所采用的一种工艺或提供的一项服务，不论这种知识是否反映在一项发明、一项外形设计、一项实用新型或者一种植物新品种，或者反映在技术情报或技能中，或者反映在专家为设计、安装、开办或维修一个工厂或为管理一个工商业企业或其活动而提供的服务或协助等方面。”这是至今为止国际上给技术所下的最为全面和完整的定义。知识产权组织把世界上所有能带来经济效益的科学知识都定义为技术。', '技术 （技术在世界知识产权中的定义）', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '3', '0', '0', null, '1', '2021-09-04 00:04:54', '192.168.1.23', '1', '2021-09-04 00:04:54', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419674577715201', '<div>\n<div>\n<p>一、国家新闻出版署、国家版权局直属事业单位</p>\n<p>二、国家版权登记机构，我国唯一的计算机软件著作权登记、著作权质权登记机构</p>\n<p>以上是&ldquo;中国版权保护中心的说明&rdquo;，如果做为一个普通网站看，哪就还行，哪不了不用。</p>\n<p>但是！作为敢注明 国家新闻出版署、国家版权局直属事业单位 的一家网站，简直烂！烂到家了！烂到不能用！</p>\n<p>一、版权无法直接查询，现代大数据时代，互联网工业化下的一家网站，对自己的主要业务查询功能是怎么样的？</p>\n</div>\n</div>\n<p><img src=\"http://192.168.1.23:8088/store/202109/04185118oZpFiPp9.jpg\" /></p>\n<p><img src=\"http://192.168.1.23:8088/store/202109/04185136DNuAsjPM.jpg\" /></p>', '2021-09-04 13:06:02', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:03', '192.168.1.23', '1', '2021-09-04 18:52:11', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419687328399367', '<p>1234534234</p>', '2021-09-04 13:06:05', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:06', '192.168.1.23', '1', '2021-09-09 23:17:28', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419693603078146', '<p>121312312</p>', '2021-09-04 13:06:07', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:07', '192.168.1.23', '1', '2021-09-04 14:10:51', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419696891412482', null, '2021-09-04 13:06:08', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:08', '192.168.1.23', '1', '2021-09-04 13:06:08', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419699571572739', null, '2021-09-04 13:06:08', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:09', '192.168.1.23', '1', '2021-09-04 13:06:09', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419700490125321', null, '2021-09-04 13:06:09', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:09', '192.168.1.23', '1', '2021-09-04 13:06:09', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419701287043081', null, '2021-09-04 13:06:09', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:09', '192.168.1.23', '1', '2021-09-04 13:06:09', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419703451303937', null, '2021-09-04 13:06:09', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:10', '192.168.1.23', '1', '2021-09-04 13:06:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419703975591939', null, '2021-09-04 13:06:09', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:10', '192.168.1.23', '1', '2021-09-04 13:06:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419704566988800', null, '2021-09-04 13:06:10', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:10', '192.168.1.23', '1', '2021-09-04 13:06:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419704692817929', null, '2021-09-04 13:06:10', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:10', '192.168.1.23', '1', '2021-09-04 13:06:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419705657507840', null, '2021-09-04 13:06:10', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:10', '192.168.1.23', '1', '2021-09-04 13:06:10', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419706353762314', null, '2021-09-04 13:06:10', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:11', '192.168.1.23', '1', '2021-09-04 13:06:11', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1545419706919993348', null, '2021-09-04 13:06:10', null, 'inherit', null, null, null, '1545223091294748672', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1', '2021-09-04 13:06:11', '192.168.1.23', '1', '2021-09-04 13:06:11', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1547701724307439620', '详情有很多', '测试之道', null, 'publish', null, null, null, '0', 'book', '1533544727530094592', null, null, '0', '0', '0', null, '1547698179520774144', '2021-09-10 20:14:06', '192.168.1.23', '1547698179520774144', '2021-09-10 20:14:06', '192.168.1.23', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('1547701778556567556', '<div class=\"para\" data-pid=\"2\"><strong>作品目录</strong></div>\n<div class=\"para\" data-pid=\"3\">第1章：测试基础知识</div>\n<div class=\"para\" data-pid=\"4\">1.1 现状和前景</div>\n<div class=\"para\" data-pid=\"5\">1.1.1 现状</div>\n<div class=\"para\" data-pid=\"6\">1.1.2 前景</div>\n<div class=\"para\" data-pid=\"7\">1.2 测试简介</div>\n<div class=\"para\" data-pid=\"8\">1.3 测试内容</div>\n<div class=\"para\" data-pid=\"9\">1.3.1 测试方法</div>\n<div class=\"para\" data-pid=\"10\">1.3.2 测试分类</div>\n<div class=\"para\" data-pid=\"11\">1.3.3 测试流程</div>\n<div class=\"para\" data-pid=\"12\">1.3.4 测试阶段</div>\n<div class=\"para\" data-pid=\"13\">1.3.5 测试模型</div>\n<div class=\"para\" data-pid=\"14\">1.4 测试工具</div>\n<div class=\"para\" data-pid=\"15\">第2章：前端自动化 - Selenium</div>\n<div class=\"para\" data-pid=\"16\">2.1&nbsp;<a href=\"https://baike.baidu.com/item/Selenium/18266\" target=\"_blank\" rel=\"noopener\" data-lemmaid=\"18266\">Selenium</a>&ndash; IDE</div>\n<div class=\"para\" data-pid=\"17\">2.1.1 Selenium IDE下载</div>\n<div class=\"para\" data-pid=\"18\">2.1.2 Selenium - IDE 工具特点</div>\n<div class=\"para\" data-pid=\"19\">2.1.3 Selenium IDE 测试创建</div>\n<div class=\"para\" data-pid=\"20\">2.2 Selenium &ndash; Java</div>\n<div class=\"para\" data-pid=\"21\">2.2.1 环境安装设置</div>\n<div class=\"para\" data-pid=\"22\">2.2.2 下载并配置<a href=\"https://baike.baidu.com/item/Eclipse/61703\" target=\"_blank\" rel=\"noopener\" data-lemmaid=\"61703\">Eclipse</a></div>\n<div class=\"para\" data-pid=\"23\">2.2.3 配置Firebug</div>\n<div class=\"para\" data-pid=\"24\">2.2.4 配置Selenium的WebDriver</div>\n<div class=\"para\" data-pid=\"25\">2.3 Selenium - Webdriver</div>\n<div class=\"para\" data-pid=\"26\">2.3.1 Selenium RC 对比 Webdriver</div>\n<div class=\"para\" data-pid=\"27\">2.3.2 Webdriver脚本使用</div>\n<div class=\"para\" data-pid=\"28\">2.3.3 Selenium 定位器</div>\n<div class=\"para\" data-pid=\"29\">2.3.4 等待机制</div>\n<div class=\"para\" data-pid=\"30\">2.3.5 捕捉屏幕截图</div>\n<div class=\"para\" data-pid=\"31\">2.4 Selenium - Python</div>\n<div class=\"para\" data-pid=\"32\">2.4.1 下载和安装<a href=\"https://baike.baidu.com/item/Python\" target=\"_blank\" rel=\"noopener\">Python</a>&nbsp;Selenium</div>\n<div class=\"para\" data-pid=\"33\">2.4.2 编写测试代码</div>\n<div class=\"para\" data-pid=\"34\">第3章：项目构建管理</div>\n<div class=\"para\" data-pid=\"35\">3.1&nbsp;<a href=\"https://baike.baidu.com/item/Maven\" target=\"_blank\" rel=\"noopener\">Maven</a>&nbsp;是什么<sup class=\"sup--normal\" data-sup=\"1\" data-ctrmap=\":1,\">&nbsp;[1]</sup><a class=\"sup-anchor\" name=\"ref_[1]_20892336\"></a>&nbsp;</div>\n<div class=\"para\" data-pid=\"36\">3.2 Maven 安装配置</div>\n<div class=\"para\" data-pid=\"37\">3.3 Maven POM</div>\n<div class=\"para\" data-pid=\"38\">3.4 Maven 构建配置文件</div>\n<div class=\"para\" data-pid=\"39\">3.5 Maven 存储库</div>\n<div class=\"para\" data-pid=\"40\">3.5.1 本地库</div>\n<div class=\"para\" data-pid=\"41\">3.5.2 中央存储库</div>\n<div class=\"para\" data-pid=\"42\">3.5.3 远程仓库</div>\n<div class=\"para\" data-pid=\"43\">3.5.4 Maven 依赖搜索序列</div>\n<div class=\"para\" data-pid=\"44\">3.6 Maven 插件</div>\n<div class=\"para\" data-pid=\"45\">3.6.1 什么是Maven插件</div>\n<div class=\"para\" data-pid=\"46\">3.6.2 插件类型</div>\n<div class=\"para\" data-pid=\"47\">3.6.3 Eclipse IDE集成Maven</div>\n<div class=\"para\" data-pid=\"48\">3.6.4 Eclipse 构建Maven项目</div>\n<div class=\"para\" data-pid=\"49\">第4章：测试框架</div>\n<div class=\"para\" data-pid=\"50\">4.1 TestNG 介绍</div>\n<div class=\"para\" data-pid=\"51\">4.2 TestNG 环境设置</div>\n<div class=\"para\" data-pid=\"52\">4.3 TestNG Eclipse 插件</div>\n<div class=\"para\" data-pid=\"53\">4.4 TestNG 获取方式</div>\n<div class=\"para\" data-pid=\"54\">4.5 TestNG 编写测试</div>\n<div class=\"para\" data-pid=\"55\">4.6 TestNG 基本注解</div>\n<div class=\"para\" data-pid=\"56\">4.6.1 TestNG套件测试</div>\n<div class=\"para\" data-pid=\"57\">4.6.2 TestNG Ignore测试</div>\n<div class=\"para\" data-pid=\"58\">4.6.3 TestNG 组测试</div>\n<div class=\"para\" data-pid=\"59\">4.6.4 TestNG 异常测试</div>\n<div class=\"para\" data-pid=\"60\">4.6.5 TestNG 依赖测试</div>\n<div class=\"para\" data-pid=\"61\">4.6.6 TestNG 参数化测试</div>\n<div class=\"para\" data-pid=\"62\">4.6.7 TestNG 测试结果报告</div>\n<div class=\"para\" data-pid=\"63\">第5章：持续集成</div>\n<div class=\"para\" data-pid=\"64\">5.1&nbsp;<a href=\"https://baike.baidu.com/item/Jenkins\" target=\"_blank\" rel=\"noopener\">Jenkins</a>&nbsp;简介<sup class=\"sup--normal\" data-sup=\"1\" data-ctrmap=\":1,\">&nbsp;[1]</sup><a class=\"sup-anchor\" name=\"ref_[1]_20892336\"></a>&nbsp;</div>\n<div class=\"para\" data-pid=\"65\">5.2 Jenkins 安装</div>\n<div class=\"para\" data-pid=\"66\">5.3 Jenkins 启动</div>\n<div class=\"para\" data-pid=\"67\">5.4 Jenkins 任务</div>\n<div class=\"para\" data-pid=\"68\">5.4.1 与svn工具集成</div>\n<div class=\"para\" data-pid=\"69\">5.4.2 设置构建触发器</div>\n<div class=\"para\" data-pid=\"70\">5.4.3 配置如何构建</div>\n<div class=\"para\" data-pid=\"71\">5.4.4 构建开始</div>\n<div class=\"para\" data-pid=\"72\">5.5 Jenkins 邮件通知</div>\n<div class=\"para\" data-pid=\"73\">第6章：Linux Shell 编程</div>\n<div class=\"para\" data-pid=\"74\">6.1&nbsp;<a href=\"https://baike.baidu.com/item/Shell/99702\" target=\"_blank\" rel=\"noopener\" data-lemmaid=\"99702\">Shell</a>&nbsp;简介<sup class=\"sup--normal\" data-sup=\"1\" data-ctrmap=\":1,\">&nbsp;[1]</sup><a class=\"sup-anchor\" name=\"ref_[1]_20892336\"></a>&nbsp;</div>\n<div class=\"para\" data-pid=\"75\">6.1.1 几种常见的Shell</div>\n<div class=\"para\" data-pid=\"76\">6.1.2 什么时候使用Shell</div>\n<div class=\"para\" data-pid=\"77\">6.2 Shell 实例</div>\n<div class=\"para\" data-pid=\"78\">6.2.1 配置Java环境</div>\n<div class=\"para\" data-pid=\"79\">6.2.2 配置Tomcat和Apache负载均衡</div>\n<div class=\"para\" data-pid=\"80\">6.2.3 配置MySQL环境</div>\n<div class=\"para\" data-pid=\"81\">6.2.4 配置Hadoop环境</div>\n<div class=\"para\" data-pid=\"82\">第7章：敏捷模式</div>\n<div class=\"para\" data-pid=\"83\">7.1 价值观</div>\n<div class=\"para\" data-pid=\"84\">7.2 原则</div>\n<div class=\"para\" data-pid=\"85\">7.3 实践</div>\n<div class=\"para\" data-pid=\"86\">第8章：移动端自动化测试</div>\n<div class=\"para\" data-pid=\"87\">8.1 Android 环境搭建</div>\n<div class=\"para\" data-pid=\"88\">8.2 Monkey 测试</div>\n<div class=\"para\" data-pid=\"89\">8.2.1 高级篇：Monkey UI自动化</div>\n<div class=\"para\" data-pid=\"90\">8.2.2 用Monkey实现登录QQ</div>\n<div class=\"para\" data-pid=\"91\">8.3 MonkeyRunner 测试</div>\n<div class=\"para\" data-pid=\"92\">8.4&nbsp;<a href=\"https://baike.baidu.com/item/Robotium\" target=\"_blank\" rel=\"noopener\">Robotium</a>&nbsp;测试</div>\n<div class=\"para\" data-pid=\"93\">8.5 Appium 测试</div>\n<div class=\"para\" data-pid=\"94\">第9章：接口自动化测试</div>\n<div class=\"para\" data-pid=\"95\">9.1 接口自动化测试 - 基于Python<sup class=\"sup--normal\" data-sup=\"1\" data-ctrmap=\":1,\">&nbsp;[1]</sup><a class=\"sup-anchor\" name=\"ref_[1]_20892336\"></a>&nbsp;</div>\n<div class=\"para\" data-pid=\"96\">9.1.1 Get 请求</div>\n<div class=\"para\" data-pid=\"97\">9.1.2 Post 请求</div>', '测试之道目录', null, 'inherit', null, null, null, '1547701724307439620', 'chapter', '1533544727530094592', null, null, '0', '0', '0', null, '1547698179520774144', '2021-09-10 20:14:19', '192.168.1.23', '1547698179520774144', '2021-09-10 20:15:29', '192.168.1.23', 'normal', '1');

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
-- Records of k_stars
-- ----------------------------
INSERT INTO `k_stars` VALUES ('67851930685849605', '1544422567918092296', '0', '1', '0');
INSERT INTO `k_stars` VALUES ('68197800572796937', '1544422560041189382', '0', '0', '0');
INSERT INTO `k_stars` VALUES ('68197934966685700', '1544414748162572291', '0', '1', '1');
INSERT INTO `k_stars` VALUES ('68203626234560512', '1544414732358434826', '0', '1', '1');
INSERT INTO `k_stars` VALUES ('68206691511615492', '1544401654996713472', '0', '1', '1');
INSERT INTO `k_stars` VALUES ('68206715511422976', '1544401257229893635', '0', '1', '1');
INSERT INTO `k_stars` VALUES ('68206731370086410', '1544401216171851784', '0', '1', '1');
INSERT INTO `k_stars` VALUES ('68211264917782532', '1544422567918092296', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('68211309817806849', '1544422560041189382', '1', '1', '1');
INSERT INTO `k_stars` VALUES ('68211334031523840', '1544414748162572291', '1', '0', '1');
INSERT INTO `k_stars` VALUES ('68211345456807943', '1544414732358434826', '1', '0', '1');
INSERT INTO `k_stars` VALUES ('68211355149844487', '1544401654996713472', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('68211455687311367', '1544386503421640713', '1', '1', '1');
INSERT INTO `k_stars` VALUES ('68211634977030146', '1544422560041189382', '100', '1', '1');
INSERT INTO `k_stars` VALUES ('68211654874808321', '1544414748162572291', '100', '0', '1');
INSERT INTO `k_stars` VALUES ('68211664907583495', '1544401257229893635', '100', '1', '0');
INSERT INTO `k_stars` VALUES ('68211678782341122', '1544386503421640713', '100', '0', '1');
INSERT INTO `k_stars` VALUES ('68211686277562370', '1544356320987758602', '100', '1', '1');
INSERT INTO `k_stars` VALUES ('68477351995555847', '1544401257229893635', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('68477360677765120', '1544401216171851784', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('79071460401528841', '71098022814728198', '79070766156136451', '1', '1');
INSERT INTO `k_stars` VALUES ('79071469180207108', '71095902778605572', '79070766156136451', '1', '1');
INSERT INTO `k_stars` VALUES ('79071479733075976', '1544422567918092296', '79070766156136451', '0', '1');
INSERT INTO `k_stars` VALUES ('79071497412067334', '1544422560041189382', '79070766156136451', '0', '1');
INSERT INTO `k_stars` VALUES ('79071504072622090', '1544414748162572291', '79070766156136451', '0', '1');
INSERT INTO `k_stars` VALUES ('91631755686756357', '1544386503421640713', '0', '0', '1');
INSERT INTO `k_stars` VALUES ('100677371184201735', '83322023720960005', '1', '1', '1');
INSERT INTO `k_stars` VALUES ('249785005362561025', '249734249511043078', '1', '1', '1');
INSERT INTO `k_stars` VALUES ('249785017744146442', '150301236507885576', '1', '1', '1');
INSERT INTO `k_stars` VALUES ('249785035708350475', '249439401633562627', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785045636268033', '249437170188337159', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785054469472256', '150341155934617611', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785066356129799', '247589103533211650', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785083674411013', '248227360134578178', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785093023514633', '247261867479318531', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785102087405576', '157641603456614408', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785108416610309', '247615834260422660', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785115878277120', '108315676512010250', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785124875059207', '108321427372556297', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785134589067265', '150301922083651586', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785143896227840', '150300613628575749', '1', '1', '0');
INSERT INTO `k_stars` VALUES ('249785157473189890', '150300213835907079', '1', '1', '0');

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
-- Records of k_term_object
-- ----------------------------
INSERT INTO `k_term_object` VALUES ('2', '14', '2', '0');
INSERT INTO `k_term_object` VALUES ('3', '15', '2', '0');
INSERT INTO `k_term_object` VALUES ('4', '16', '2', '0');
INSERT INTO `k_term_object` VALUES ('5', '17', '2', '0');
INSERT INTO `k_term_object` VALUES ('6', '18', '3', '0');
INSERT INTO `k_term_object` VALUES ('7', '19', '3', '0');
INSERT INTO `k_term_object` VALUES ('8', '20', '3', '0');
INSERT INTO `k_term_object` VALUES ('9', '21', '3', '0');
INSERT INTO `k_term_object` VALUES ('10', '22', '2', '0');
INSERT INTO `k_term_object` VALUES ('71090908448866309', '71090908285288456', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71095902875074563', '71095902778605572', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71098015319506954', '71098015290146827', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71098018838528001', '71098018817556482', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71098019916464128', '71098019769663489', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71098022172999682', '71098022001033220', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('71098022835699712', '71098022814728198', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('80357728066387968', '1524911615640256512', '1', '0');
INSERT INTO `k_term_object` VALUES ('80366983184039941', '80366982974324744', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('81170491843985408', '1522680911237922817', '1', '0');
INSERT INTO `k_term_object` VALUES ('81170570042589193', '1527684704723714055', '1', '0');
INSERT INTO `k_term_object` VALUES ('81758509797654539', '1531257784574328835', '1', '0');
INSERT INTO `k_term_object` VALUES ('81759135508119558', '81759135164186628', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('83281719718494209', '1522009400529305604', '1', '0');
INSERT INTO `k_term_object` VALUES ('83293843408732166', '83293842246909960', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('83320963308634117', '83320962197143557', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('83322023746125832', '83322023720960005', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('93807978869342217', '93807978626072585', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('93808020216791047', '93808020195819520', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('94132270324695043', '1', '1', '0');
INSERT INTO `k_term_object` VALUES ('94132465837981703', '25', '1', '0');
INSERT INTO `k_term_object` VALUES ('94132527196454917', '29', '1', '0');
INSERT INTO `k_term_object` VALUES ('94428624334798851', '94428624192192519', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('94454789292343300', '94454789267177482', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('94454852542447623', '94454852508893185', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('94823522779185161', '94823522682716168', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('94847219195887624', '94847219032309763', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('94847669303427081', '94847669290844168', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('94993989150359563', '94993989058084875', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('95010184171798538', '95010184129855499', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361649058955275', '95361647884550155', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361651940442122', '95361651915276297', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361652842217483', '95361652821245954', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361657317539845', '95361655048421378', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361657338511361', '95361657300762629', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361657342705670', '95361657304956938', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361657346899979', '95361657304956937', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95361657502089227', '95361657476923397', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('95562118310051840', '95562118209388549', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('96473821486956555', '96473821474373639', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98240382199578632', '98240381033562114', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98240390583992322', '98240390416220171', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98240396057559047', '98240395973672962', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98240401036197891', '98240401011032073', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98240404156760070', '98240403997376517', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('98241028877369346', '98241028642488321', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98241034069917706', '98241033923117058', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98241038054506500', '98241038029340679', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98241041128931338', '98241041103765508', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98242517511356426', '98242517477801992', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98242524402597898', '98242524377432073', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('98259928931287043', '98259928771903499', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('98260088281284614', '98260087136239626', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100046688560529418', '100046688501809161', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('100077296028401666', '100077295961292809', '1', '0');
INSERT INTO `k_term_object` VALUES ('100077296049373191', '100077295961292809', '1', '0');
INSERT INTO `k_term_object` VALUES ('100097483955945479', '81101003052204043', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100105173440577542', '81101003052204043', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100116068925358082', '81101003052204043', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100333831874854916', '83320962197143557', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('100395257121390596', '83320962197143557', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('100419910082674698', '83322023720960005', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('100420166564364290', '83322023720960005', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100650318829371394', '83322023720960005', '100650318774845450', '0');
INSERT INTO `k_term_object` VALUES ('100650318829371395', '83322023720960005', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100676032337199108', '83322023720960005', '100676032286867466', '0');
INSERT INTO `k_term_object` VALUES ('100678105954959369', '1544356320987758602', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100678105959153665', '1544356320987758602', '100678105904627722', '0');
INSERT INTO `k_term_object` VALUES ('100678105980125189', '1544356320987758602', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100678577847713792', '1544401216171851784', '100678577793187847', '0');
INSERT INTO `k_term_object` VALUES ('100678577851908101', '1544401216171851784', '100678577805770752', '0');
INSERT INTO `k_term_object` VALUES ('100678577851908102', '1544401216171851784', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100678577851908103', '1544401216171851784', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100678577851908104', '1544401216171851784', '100678577801576448', '0');
INSERT INTO `k_term_object` VALUES ('100678864322871306', '1544401257229893635', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100678864327065602', '1544401257229893635', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100679112399175681', '1544401654996713472', '100679112361426951', '0');
INSERT INTO `k_term_object` VALUES ('100679112399175682', '1544401654996713472', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100679112399175683', '1544401654996713472', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100679112403369986', '1544401654996713472', '100679112365621253', '0');
INSERT INTO `k_term_object` VALUES ('100679240937816071', '1544414732358434826', '100679240883290118', '0');
INSERT INTO `k_term_object` VALUES ('100679240937816072', '1544414732358434826', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100685006822359044', '1541768862404100102', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100685006830747656', '1541768862404100102', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('100685006830747657', '1541768862404100102', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100685022139957257', '100685022098014208', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100685022160928771', '100685022098014208', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100685088997163008', '100685088955219977', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100685089039106059', '100685088955219977', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100688161576501249', '1541803026658541572', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100688161752662022', '1541803026658541572', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100688161752662023', '1541803026658541572', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('100688161752662024', '1541803026658541572', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('100688161752662025', '1541803026658541572', '100688161706524672', '0');
INSERT INTO `k_term_object` VALUES ('100716409182142474', '100716409064701963', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100717380062855174', '100717380033495049', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100717484387778561', '100716409064701963', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100717484387778562', '100716409064701963', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100717505581596681', '100717505548042250', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100717505610956806', '100717505548042250', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100717505610956807', '100717505548042250', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100771756802490369', '100717505548042250', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('100784898752299012', '100784898597109765', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100784898806824971', '100784898597109765', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100784898811019273', '100784898597109765', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100785209122406401', '1522187456745226244', '1', '0');
INSERT INTO `k_term_object` VALUES ('100785354383736837', '100785354299850760', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100785354421485576', '100785354299850760', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100785354425679879', '100785354299850760', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100792307583467526', '0', '1', '0');
INSERT INTO `k_term_object` VALUES ('100792307600244740', '100792307554107401', '1', '0');
INSERT INTO `k_term_object` VALUES ('100792492300615683', '0', '1', '0');
INSERT INTO `k_term_object` VALUES ('100792492325781508', '100792492258672646', '1', '0');
INSERT INTO `k_term_object` VALUES ('100795744240975876', '0', '1', '0');
INSERT INTO `k_term_object` VALUES ('100795744253558793', '100795744148701191', '1', '0');
INSERT INTO `k_term_object` VALUES ('100824163909550088', '100824163745972224', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100825456271081477', '100824163745972224', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100825456325607432', '100824163745972224', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100825456329801736', '100824163745972224', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100826521141952516', '100826521129369604', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100826521175506955', '100826521129369604', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100826521175506956', '100826521129369604', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100826521175506957', '100826521129369604', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100826521175506958', '100826521129369604', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100831393866235905', '100831393706852359', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('100831393866235906', '100831393706852359', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100831394000453636', '100831393706852359', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100831394000453637', '100831393706852359', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100831394000453638', '100831393706852359', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100831394000453639', '100831393706852359', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832820130922504', '100832820072202250', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100832820160282631', '100832820072202250', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100832820164476930', '100832820072202250', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100832820168671240', '100832820072202250', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100832820168671241', '100832820072202250', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832825189253131', '100832825126338562', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100832825243779076', '100832825126338562', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100832825247973385', '100832825126338562', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100832825247973386', '100832825126338562', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100832825247973387', '100832825126338562', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832836425793544', '100832836350296067', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100832836497096709', '100832836350296067', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832836501291016', '100832836350296067', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100832836505485316', '100832836350296067', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100832839768653834', '100832839705739270', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100832839818985472', '100832839705739270', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832839823179778', '100832839705739270', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100832839827374088', '100832839705739270', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100832842159407105', '100832842033577987', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100832842226515977', '100832842033577987', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100832842230710272', '100832842033577987', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100832842234904584', '100832842033577987', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100833128999469059', '100833128928165888', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833129041412106', '100833128928165888', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833129045606410', '100833128928165888', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833129049800711', '100833128928165888', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('100833145801850891', '100833145684410378', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833145831211019', '100833145684410378', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833145835405316', '100833145684410378', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833145835405317', '100833145684410378', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833145839599621', '100833145684410378', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833150210064385', '100833150159732736', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833150247813120', '100833150159732736', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833150252007424', '100833150159732736', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833150256201728', '100833150159732736', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833150256201729', '100833150159732736', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833157143248898', '100833157101305857', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833157176803335', '100833157101305857', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833157180997633', '100833157101305857', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833157185191944', '100833157101305857', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833157185191945', '100833157101305857', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833160066678789', '100833158049218560', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833160108621824', '100833158049218560', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833160112816135', '100833158049218560', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833160117010438', '100833158049218560', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833160117010439', '100833158049218560', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833160263811075', '100833160083456005', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833160268005377', '100833160087650311', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('100833160288976903', '100833160087650311', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833160293171207', '100833160087650311', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833160293171208', '100833160087650311', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833160293171209', '100833160087650311', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833160351891465', '100833160083456005', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833160351891466', '100833160083456005', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833160351891467', '100833160083456005', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833160351891468', '100833160083456005', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833290891214859', '100833289259630596', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('100833290895409155', '100833289259630596', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100833290916380682', '100833289259630596', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833290920574982', '100833289259630596', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833290920574983', '100833289259630596', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833290920574984', '100833289259630596', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100833296171843589', '100833296096346117', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('100833296171843590', '100833296096346117', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('100833296343810057', '100833296096346117', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100833296348004353', '100833296096346117', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('100833296352198657', '100833296096346117', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100833296356392965', '100833296096346117', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('100837501888544779', '100837501867573253', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('100837501913710596', '100837501867573253', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100837501917904904', '100837501867573253', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100849044478935040', '100849044428603392', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('100849044504100864', '100849044428603392', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('100849044508295176', '100849044428603392', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('100849322884251649', '100849044428603392', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('101013606540951561', '98260087136239626', '101013606482231304', '0');
INSERT INTO `k_term_object` VALUES ('101013606540951562', '98260087136239626', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('101016014696398859', '100717380033495049', '101016014650261508', '0');
INSERT INTO `k_term_object` VALUES ('108315676864331782', '108315676512010250', '1', '0');
INSERT INTO `k_term_object` VALUES ('108315676977577985', '108315676512010250', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('108315676981772299', '108315676512010250', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('108321428584710145', '108321427372556297', '1', '0');
INSERT INTO `k_term_object` VALUES ('108321428685373445', '108321427372556297', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('108321428689567746', '108321427372556297', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('108322899510345733', '108322899132858375', '1', '0');
INSERT INTO `k_term_object` VALUES ('108322899573260299', '108322899132858375', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('108322899577454603', '108322899132858375', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('108324278459088896', '108324277305655298', '1', '0');
INSERT INTO `k_term_object` VALUES ('108324278480060426', '108324277305655298', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('108324278484254729', '108324277305655298', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('108324580407033857', '108324277305655298', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('108325789629071364', '108325789582934025', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('109134873575997447', '1522687132816818177', '1', '0');
INSERT INTO `k_term_object` VALUES ('111610378778755075', '1529498096614686730', '1', '0');
INSERT INTO `k_term_object` VALUES ('125294558565810183', '125294558309957640', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('125294558947491840', '125294558309957640', '125294558846828551', '0');
INSERT INTO `k_term_object` VALUES ('125294558951686144', '125294558309957640', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('125351435047714823', '1527695423368249352', '1', '0');
INSERT INTO `k_term_object` VALUES ('144962472126365704', '1529498078247829511', '1', '0');
INSERT INTO `k_term_object` VALUES ('144984620467404803', '1529498102721593344', '1', '0');
INSERT INTO `k_term_object` VALUES ('144984659377963018', '1529498101626880001', '1', '0');
INSERT INTO `k_term_object` VALUES ('145300885006368777', '1521588846055833611', '1', '0');
INSERT INTO `k_term_object` VALUES ('145317068422168582', '1529498100704133120', '1', '0');
INSERT INTO `k_term_object` VALUES ('145327062332915720', '1529498076502999050', '1', '0');
INSERT INTO `k_term_object` VALUES ('145350213167136776', '1525836858034667529', '1', '0');
INSERT INTO `k_term_object` VALUES ('150293642246537225', '150293641017606149', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('150293642410115081', '150293641017606149', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('150295498133782531', '150295498133782530', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('150295498330914824', '150295498133782530', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('150297118515052556', '150297118515052555', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('150297119760760842', '150297118515052555', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('150297119760760843', '150297118515052555', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('150300214343417865', '150300213835907079', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('150300613762793481', '150300613628575749', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('150300613892816901', '150300613628575749', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('150301236696629253', '150301236507885576', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('150301236763738116', '150301236507885576', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('150301922276589573', '150301922083651586', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('150341156001726469', '150341155934617611', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('150341156328882185', '150341155934617611', '100650318774845450', '0');
INSERT INTO `k_term_object` VALUES ('151800310658678788', '1521175556540514304', '1', '0');
INSERT INTO `k_term_object` VALUES ('157641603796353025', '157641603456614408', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('157641603796353026', '157641603456614408', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('215965624690196484', '1521202243919593483', '1', '0');
INSERT INTO `k_term_object` VALUES ('229352398267269129', '108322899132858375', '229352398221131781', '0');
INSERT INTO `k_term_object` VALUES ('245756389624561674', '1521455551368314886', '1', '0');
INSERT INTO `k_term_object` VALUES ('246809205944270850', '150301922083651586', '1', '0');
INSERT INTO `k_term_object` VALUES ('247062419188924422', '30', '1', '0');
INSERT INTO `k_term_object` VALUES ('247261089062633472', '247261088727089159', '1', '0');
INSERT INTO `k_term_object` VALUES ('247261089117159435', '247261088727089159', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('247261089117159436', '247261088727089159', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('247261867626119176', '247261867479318531', '1', '0');
INSERT INTO `k_term_object` VALUES ('247261867672256513', '247261867479318531', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('247261867672256514', '247261867479318531', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('247261867672256515', '247261867479318531', '100650318774845450', '0');
INSERT INTO `k_term_object` VALUES ('247471524395270151', '247471524206526466', '1', '0');
INSERT INTO `k_term_object` VALUES ('247471524433018880', '247471524206526466', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('247471524437213188', '247471524206526466', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('247471524437213189', '247471524206526466', '100650318774845450', '0');
INSERT INTO `k_term_object` VALUES ('247472124637921287', '247471524206526466', '247472124566618119', '0');
INSERT INTO `k_term_object` VALUES ('247472807994900486', '247471524206526466', '247472807927791621', '0');
INSERT INTO `k_term_object` VALUES ('247587825046437897', '247261867479318531', '247472807927791621', '0');
INSERT INTO `k_term_object` VALUES ('247589103554183170', '247589103533211650', '1', '0');
INSERT INTO `k_term_object` VALUES ('247589103763898371', '247589103533211650', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('247615834356891656', '247615834260422660', '1', '0');
INSERT INTO `k_term_object` VALUES ('247643764181090311', '247643764122370050', '1', '0');
INSERT INTO `k_term_object` VALUES ('248042265310314506', '157641603456614408', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('248042265398394890', '157641603456614408', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('248045421880590347', '248045421826064392', '1', '0');
INSERT INTO `k_term_object` VALUES ('248045421914144769', '248045421826064392', '100650318774845450', '0');
INSERT INTO `k_term_object` VALUES ('248045421918339073', '248045421826064392', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('248045421922533383', '248045421826064392', '247472807927791621', '0');
INSERT INTO `k_term_object` VALUES ('248045421922533384', '248045421826064392', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('248227360222658565', '248227360134578178', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('248227360293961734', '248227360134578178', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249075488547061765', '108325789582934025', '1', '0');
INSERT INTO `k_term_object` VALUES ('249403647486246915', '247589103533211650', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249437170247057416', '249437170188337159', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249439401658728459', '249439401633562627', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('249734249527820296', '249734249511043078', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249734249712369667', '249734249511043078', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249734249716563976', '249734249511043078', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249734249716563977', '249734249511043078', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249735731761299462', '249735731727745026', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249735731786465282', '249735731727745026', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249735731786465283', '249735731727745026', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249735731786465284', '249735731727745026', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778099554271232', '249778098358894594', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778099583631362', '249778098358894594', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778099587825664', '249778098358894594', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778099587825665', '249778098358894594', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778108647522307', '249778108618162185', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778108668493828', '249778108618162185', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778108668493829', '249778108618162185', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778108668493830', '249778108618162185', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778109985505280', '249778109956145155', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778110006476806', '249778109956145155', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778110006476807', '249778109956145155', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778110006476808', '249778109956145155', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778112292372486', '249778112263012362', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778112313344000', '249778112263012362', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778112313344001', '249778112263012362', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778112313344002', '249778112263012362', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778113299005445', '249778113269645312', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778113319976960', '249778113269645312', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778113324171272', '249778113269645312', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778113324171273', '249778113269645312', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778114481799171', '249778114439856138', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778114506965001', '249778114439856138', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778114506965002', '249778114439856138', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778114506965003', '249778114439856138', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778115480043520', '249778115450683394', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778115501015042', '249778115450683394', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778115501015043', '249778115450683394', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778115501015044', '249778115450683394', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778116658642951', '249778116625088523', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778116683808779', '249778116625088523', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778116688003079', '249778116625088523', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778116688003080', '249778116625088523', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778117073879047', '249778117040324619', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778117094850563', '249778117040324619', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778117094850564', '249778117040324619', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778117099044868', '249778117040324619', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778117933711365', '249778117904351239', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778117954682890', '249778117904351239', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778117954682891', '249778117904351239', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778117954682892', '249778117904351239', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778118856458246', '249778118831292422', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778118881624067', '249778118831292422', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778118881624068', '249778118831292422', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778118881624069', '249778118831292422', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778119707901956', '249778119670153227', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778119846313986', '249778119670153227', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778119850508293', '249778119670153227', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778119850508294', '249778119670153227', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778121062662148', '249778121020719109', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778121087827975', '249778121020719109', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778121087827976', '249778121020719109', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778121087827977', '249778121020719109', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778122052517898', '249778122023157765', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778122073489413', '249778122023157765', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778122077683715', '249778122023157765', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778122077683716', '249778122023157765', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778122698440715', '249778122664886282', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778122719412233', '249778122664886282', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778122719412234', '249778122664886282', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778122719412235', '249778122664886282', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778125143719940', '249778123646353417', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778125173080066', '249778123646353417', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778125173080068', '249778123646353417', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778125173080069', '249778123646353417', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778125210828805', '249778125173080067', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778125235994629', '249778125173080067', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778125235994630', '249778125173080067', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778125240188934', '249778125173080067', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778126284570634', '249778126251016202', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778126305542146', '249778126251016202', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778126305542147', '249778126251016202', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778126305542148', '249778126251016202', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778127496724481', '249778127463170055', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778127521890309', '249778127463170055', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778127521890310', '249778127463170055', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778127521890311', '249778127463170055', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778128364945408', '249778128327196674', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778128385916936', '249778128327196674', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778128390111243', '249778128327196674', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778128390111244', '249778128327196674', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778129375772682', '249778129346412544', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778129396744198', '249778129346412544', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778129396744199', '249778129346412544', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778129396744200', '249778129346412544', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778130470486022', '249778130441125894', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778130491457540', '249778130441125894', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778130491457541', '249778130441125894', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778130491457542', '249778130441125894', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778131376455688', '249778131347095560', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778131397427206', '249778131347095560', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778131397427207', '249778131347095560', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778131397427208', '249778131347095560', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778132286619653', '249778132257259520', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778132307591174', '249778132257259520', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778132307591175', '249778132257259520', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778132307591176', '249778132257259520', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778133200977925', '249778133171617796', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778133221949446', '249778133171617796', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778133221949447', '249778133171617796', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778133221949448', '249778133171617796', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778134165667849', '249778134123724800', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778134195027970', '249778134123724800', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778134195027971', '249778134123724800', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('249778134199222274', '249778134123724800', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778135071637512', '249778135042277383', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('249778135092609028', '249778135042277383', '71291917238452229', '0');
INSERT INTO `k_term_object` VALUES ('249778135092609029', '249778135042277383', '249734249670426635', '0');
INSERT INTO `k_term_object` VALUES ('249778135092609030', '249778135042277383', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('252542677501526018', '252542677308588037', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('252542677560246278', '252542677308588037', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('252542677564440578', '252542677308588037', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('252542677564440579', '252542677308588037', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('252924035700801543', '252924035583361031', '1', '0');
INSERT INTO `k_term_object` VALUES ('252924036799709187', '252924035583361031', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('252924036799709188', '252924035583361031', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('254004323155623938', '1525843814879248388', '1', '0');
INSERT INTO `k_term_object` VALUES ('254140838133284870', '254140838116507657', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('254140838196199424', '254140838116507657', '71291808291405829', '0');
INSERT INTO `k_term_object` VALUES ('254140838200393734', '254140838116507657', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('254140838200393735', '254140838116507657', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('254180658377310211', '254180658352144392', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254180658494750730', '254180658352144392', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('254180658498945028', '254180658352144392', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('254180658498945029', '254180658352144392', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('254182050307424267', '254182050299035648', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254182050332590087', '254182050299035648', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('254182155668340742', '254182155659952128', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254182155693506565', '254182155659952128', '71290499521757194', '0');
INSERT INTO `k_term_object` VALUES ('254183939338059782', '254183939329671176', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254183939354837000', '254183939329671176', '71290587878965250', '0');
INSERT INTO `k_term_object` VALUES ('254188052390002688', '254188052377419785', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254189937784176648', '254189937775788040', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254189937805148170', '254189937775788040', '71291679253643267', '0');
INSERT INTO `k_term_object` VALUES ('254193575998963721', '254193575990575110', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('254398094229815307', '254398094150123522', '1', '0');
INSERT INTO `k_term_object` VALUES ('254411500815040518', '254411500802457611', '157613316802002952', '0');
INSERT INTO `k_term_object` VALUES ('254411500848594944', '254411500802457611', '71290335121817601', '0');
INSERT INTO `k_term_object` VALUES ('254422333720805379', '254422333699833866', '1520507717504647173', '0');
INSERT INTO `k_term_object` VALUES ('254424854354903050', '254424854338125830', '1520552344769183746', '0');
INSERT INTO `k_term_object` VALUES ('254429303018274820', '254429302993108996', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1520933243079802880', '32', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521140839728463882', '33', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521150609634017285', '34', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521168871600078855', '35', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521175523678142472', '36', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521202151451967488', '37', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521455522448588806', '38', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521588799578750986', '39', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1521630976228900873', '40', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521631894936666115', '41', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521636090377322496', '1520933242983333892', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521834554423951364', '1521140839510360067', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1521989344051249163', '1521150609617240074', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1522186898021990401', '1521168871583301633', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1522298023233372168', '1521175523606839304', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1522302799010512900', '1521202151401635845', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1522306517877571591', '1521455522322759681', '1520479861269512197', '0');
INSERT INTO `k_term_object` VALUES ('1522307826760466442', '1521588799557779463', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1522308366336704517', '1521630976207929350', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1522309813862318082', '1521631894928277513', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('1522670207466389509', '1521636090356350977', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('1522680911317614595', '1521834554293927942', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('1522683503103885321', '1521989344030277641', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('1522687132833595401', '1522186897946492931', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('1522688308195016706', '1522298023115931652', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('1524804824684216326', '1522302798985347082', '1520528949428011010', '0');
INSERT INTO `k_term_object` VALUES ('1524888103139590151', '1522306517864988680', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('1524911616781107203', '1522307826739494915', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1525843814896025611', '1522308366324121602', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('1527684704895680521', '1522309813845540866', '1520528863558025224', '0');
INSERT INTO `k_term_object` VALUES ('1531257784700157952', '1522670207286034441', '1520480022150430731', '0');
INSERT INTO `k_term_object` VALUES ('1541768862479597577', '1541768862404100102', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544356231909130240', '1544356231896547328', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544356321012924426', '1544356320987758602', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544386503467778055', '1544386503421640713', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544401216197017601', '1544401216171851784', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544401257250865159', '1544401257229893635', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544401655030267913', '1544401654996713472', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544414732408766471', '1544414732358434826', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544414748187738119', '1544414748162572291', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544422560074743811', '1544422560041189382', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1544422567939063812', '1544422567918092296', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1545223091357663239', '1545223091294748672', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419674628046850', '1545419674577715201', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419688515387401', '1545419687328399367', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419693837959170', '1545419693603078146', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419696937549826', '1545419696891412482', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419699772899337', '1545419699571572739', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419700532068354', '1545419700490125321', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419701328986115', '1545419701287043081', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419703715545096', '1545419703451303937', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419704105615366', '1545419703975591939', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419704621514762', '1545419704566988800', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419704738955264', '1545419704692817929', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419705707839495', '1545419705657507840', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419706542505987', '1545419706353762314', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1545419706970324995', '1545419706919993348', '1520552212724105224', '0');
INSERT INTO `k_term_object` VALUES ('1547701724324216834', '1547701724307439620', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('1547701778581733379', '1547701778556567556', '1520551727518629888', '0');

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
-- Records of k_term_type
-- ----------------------------
INSERT INTO `k_term_type` VALUES ('1', '1', 'category', '', '0', '1134');
INSERT INTO `k_term_type` VALUES ('2', '2', 'nav_menu', '', '0', '5');
INSERT INTO `k_term_type` VALUES ('3', '3', 'nav_menu', '', '0', '4');
INSERT INTO `k_term_type` VALUES ('4', '4', 'category', '年谱目录分类', '0', '0');
INSERT INTO `k_term_type` VALUES ('71289715400818698', '71289715400818698', 'tag', 'wldos多应用支撑平台。wldos多应用支撑平台。wldos多应用支撑平台。wldos多应用支撑平台。wldos多应用支撑平台。', '0', '60');
INSERT INTO `k_term_type` VALUES ('71290335121817601', '71290335121817601', 'tag', '人物年谱', '0', '20');
INSERT INTO `k_term_type` VALUES ('71290499521757194', '71290499521757194', 'tag', '历史年谱', '0', '23');
INSERT INTO `k_term_type` VALUES ('71290587878965250', '71290587878965250', 'tag', '学术年谱', '0', '25');
INSERT INTO `k_term_type` VALUES ('71291679253643267', '71291679253643267', 'tag', '行业年谱', '0', '21');
INSERT INTO `k_term_type` VALUES ('71291808291405829', '71291808291405829', 'tag', null, '0', '21');
INSERT INTO `k_term_type` VALUES ('71291917238452229', '71291917238452229', 'tag', null, '0', '54');
INSERT INTO `k_term_type` VALUES ('100650318774845450', '100650318774845450', 'tag', null, '0', '10');
INSERT INTO `k_term_type` VALUES ('100676032286867466', '100676032286867466', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100678105904627722', '100678105904627722', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100678577793187847', '100678577793187847', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100678577801576448', '100678577801576448', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100678577805770752', '100678577805770752', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100679112361426951', '100679112361426951', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100679112365621253', '100679112365621253', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100679240883290118', '100679240883290118', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('100688161706524672', '100688161706524672', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('101013606482231304', '101013606482231304', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('101016014650261508', '101016014650261508', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('103223403340283911', '103223403340283911', 'category', '制作乐谱', '1522025656049385473', '0');
INSERT INTO `k_term_type` VALUES ('103341899445354499', '103341899445354499', 'category', null, '1522025656049385473', '0');
INSERT INTO `k_term_type` VALUES ('103345302938501124', '103345302938501124', 'category', null, '1522025656049385473', '0');
INSERT INTO `k_term_type` VALUES ('103385314031747082', '103385314031747082', 'category', null, '1522017666332278790', '0');
INSERT INTO `k_term_type` VALUES ('110142922835017731', '110142922835017731', 'category', null, '1522025767231995910', '0');
INSERT INTO `k_term_type` VALUES ('110143305179381764', '110143305179381764', 'category', null, '1522026041568837640', '0');
INSERT INTO `k_term_type` VALUES ('110143513372049409', '110143513372049409', 'category', null, '1522026601579724811', '0');
INSERT INTO `k_term_type` VALUES ('110143959885070345', '110143959885070345', 'category', null, '1522027230431723524', '0');
INSERT INTO `k_term_type` VALUES ('110144217339838464', '110144217339838464', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110144526262910980', '110144526262910980', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110144808371798021', '110144808371798021', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110144897282654217', '110144897282654217', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145003385962502', '110145003385962502', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145153420410884', '110145153420410884', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145338275971083', '110145338275971083', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145489002479622', '110145489002479622', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145638785269764', '110145638785269764', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145859829284874', '110145859829284874', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110145919925272586', '110145919925272586', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110146157687783435', '110146157687783435', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110146523150073862', '110146523150073862', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110146590405738498', '110146590405738498', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110146868672643073', '110146868672643073', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110147056069951493', '110147056069951493', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('110147209954770954', '110147209954770954', 'category', null, '1522027695852666887', '0');
INSERT INTO `k_term_type` VALUES ('111544060796911620', '111544060796911620', 'category', null, '4', '0');
INSERT INTO `k_term_type` VALUES ('125294558846828551', '125294558846828551', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('157594196077363202', '157594196077363202', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('157596477166370821', '157596477166370821', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('157613316802002952', '157613316802002952', 'category', null, '1522025100346048522', '1');
INSERT INTO `k_term_type` VALUES ('157613846664232962', '157613846664232962', 'category', null, '1522025100346048522', '0');
INSERT INTO `k_term_type` VALUES ('157640817825726464', '157640817825726464', 'category', null, '1522028131523411968', '0');
INSERT INTO `k_term_type` VALUES ('229352398221131781', '229352398221131781', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('247472124566618119', '247472124566618119', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('247472807927791621', '247472807927791621', 'tag', null, '0', '3');
INSERT INTO `k_term_type` VALUES ('249734249670426635', '249734249670426635', 'tag', null, '0', '29');
INSERT INTO `k_term_type` VALUES ('250847297604861960', '250847297604861960', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1520479861269512197', '1520479861269512197', 'category', null, '4', '35');
INSERT INTO `k_term_type` VALUES ('1520480022150430731', '1520480022150430731', 'category', null, '4', '17');
INSERT INTO `k_term_type` VALUES ('1520481497194872837', '1520481497194872837', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1520507717504647173', '1520507717504647173', 'category', null, '1520481497194872837', '1');
INSERT INTO `k_term_type` VALUES ('1520507854301872132', '1520507854301872132', 'category', null, '1520481497194872837', '0');
INSERT INTO `k_term_type` VALUES ('1520508106803167234', '1520508106803167234', 'category', null, '1520481497194872837', '0');
INSERT INTO `k_term_type` VALUES ('1520528863558025224', '1520528863558025224', 'category', null, '4', '25');
INSERT INTO `k_term_type` VALUES ('1520528949428011010', '1520528949428011010', 'category', null, '4', '7');
INSERT INTO `k_term_type` VALUES ('1520551727518629888', '1520551727518629888', 'category', null, '1', '89');
INSERT INTO `k_term_type` VALUES ('1520552212724105224', '1520552212724105224', 'category', null, '1', '23');
INSERT INTO `k_term_type` VALUES ('1520552344769183746', '1520552344769183746', 'category', null, '1', '9');
INSERT INTO `k_term_type` VALUES ('1520552631105929220', '1520552631105929220', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1520552839994851334', '1520552839994851334', 'category', null, '1520552631105929220', '0');
INSERT INTO `k_term_type` VALUES ('1520553045905817607', '1520553045905817607', 'category', null, '1520552631105929220', '0');
INSERT INTO `k_term_type` VALUES ('1520553269307031556', '1520553269307031556', 'category', null, '1520552631105929220', '0');
INSERT INTO `k_term_type` VALUES ('1520553543710982147', '1520553543710982147', 'category', null, '1520552631105929220', '0');
INSERT INTO `k_term_type` VALUES ('1522017666332278790', '1522017666332278790', 'category', '制作年谱服务', '0', '0');
INSERT INTO `k_term_type` VALUES ('1522024940970885127', '1522024940970885127', 'category', null, '1522017666332278790', '0');
INSERT INTO `k_term_type` VALUES ('1522025100346048522', '1522025100346048522', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522025222337380358', '1522025222337380358', 'category', null, '1522025100346048522', '0');
INSERT INTO `k_term_type` VALUES ('1522025656049385473', '1522025656049385473', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522025767231995910', '1522025767231995910', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026041568837640', '1522026041568837640', 'category', '线上出租知识、内容类', '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026269613146117', '1522026269613146117', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026601579724811', '1522026601579724811', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026728344174603', '1522026728344174603', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026804894416903', '1522026804894416903', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522026919600242699', '1522026919600242699', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027076064559113', '1522027076064559113', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027230431723524', '1522027230431723524', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027318260449291', '1522027318260449291', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027383041474561', '1522027383041474561', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027479413997569', '1522027479413997569', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027568819781632', '1522027568819781632', 'category', '测试', '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027695852666887', '1522027695852666887', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027830473048070', '1522027830473048070', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522027924211548168', '1522027924211548168', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028028079292427', '1522028028079292427', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028131523411968', '1522028131523411968', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028337551818753', '1522028337551818753', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028440077385734', '1522028440077385734', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028770252996615', '1522028770252996615', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522028888591089674', '1522028888591089674', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522029221132288003', '1522029221132288003', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522029609935880200', '1522029609935880200', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522029895844806666', '1522029895844806666', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522030055626817539', '1522030055626817539', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522030229187117064', '1522030229187117064', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522030689604255755', '1522030689604255755', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522030830335737867', '1522030830335737867', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522031020211879945', '1522031020211879945', 'category', null, '0', '0');
INSERT INTO `k_term_type` VALUES ('1522031215049883657', '1522031215049883657', 'category', null, '0', '0');

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
-- Records of k_terms
-- ----------------------------
INSERT INTO `k_terms` VALUES ('1', '信息科技和软件', 'ruanjian', '1', '1', '1', null, null, null, '1', '2023-02-07 23:34:48', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('2', '主菜单', '%e4%b8%bb%e8%8f%9c%e5%8d%95', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('3', '次要菜单', '%e6%ac%a1%e8%a6%81%e8%8f%9c%e5%8d%95', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('4', '年谱', 'book', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('5', '未知', 'none', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71289715400818698', 'wldos', 'wldos', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71290335121817601', '人物', 'renwu', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71290499521757194', '历史', 'lishi', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71290587878965250', '学术', 'xueshu', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71291679253643267', '行业', 'hangye', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71291808291405829', '互联网', 'Internet', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('71291917238452229', '软件开发', 'dev', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100650318774845450', 'kpaycms', 'kpaycms', '1', '1', null, null, null, null, '1', '2023-01-29 22:20:02', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('100676032286867466', '内容付费', 'neirongfufei', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100678105904627722', '中台', 'zhongtai', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100678577793187847', '啥是中台', 'shashizhongtai', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100678577801576448', '中台概念', 'zhongtaigainian', '1', '1', null, null, null, null, '1', '2023-02-07 23:35:08', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('100678577805770752', '技术术语', 'jishushuyu', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100679112361426951', '业务中台', 'yewuzhongtai', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100679112365621253', '市场', 'shichang', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100679240883290118', '中台解决方案', 'zhongtaijiejuefangan', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('100688161706524672', '云平台', 'yunpingtai', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('101013606482231304', '标签出来了', 'biaoqianchulaile', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('101016014650261508', '儿童年谱', 'ertongnianpu', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('103223403340283911', '乐谱制作', 'yuepu001', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('103341899445354499', '乐谱', 'yuepu002', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('103345302938501124', '乐谱制作', 'yuepu003', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('103385314031747082', '年谱打印', 'npdy', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110142922835017731', '编程技能', 'bcjn', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110143305179381764', '办公设备', 'bgsb', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110143513372049409', '家谱服务', 'jpfw', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110143959885070345', '祖传秘方', 'zcmf', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110144217339838464', '乐谱', 'yp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110144526262910980', '钢琴谱', 'gqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110144808371798021', '吉他谱', 'jtp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110144897282654217', '提琴谱', 'tqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145003385962502', '萨克斯', 'sks', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145153420410884', '二胡曲谱', 'ehqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145338275971083', '笙箫谱', 'sxp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145489002479622', '葫芦丝谱', 'hlsp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145638785269764', '古筝曲谱', 'gzqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145859829284874', '戏曲谱', 'xqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110145919925272586', '五线谱', 'wxp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110146157687783435', '笛子曲谱', 'dzqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110146523150073862', '手风琴', 'sfq', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110146590405738498', '胡琴谱', 'hqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110146868672643073', '花鼓戏谱', 'hgxp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110147056069951493', '古筝古琴', 'gzgq', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('110147209954770954', '其他乐谱', 'qtyp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('111544060796911620', '年谱文化', 'npwh', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('125294558846828551', '测试', 'ceshi', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('157613316802002952', '测试书籍', 'cssj', '1', '1', '1', '1', '2022-05-26 16:56:53', '192.168.1.23', '1', '2022-05-26 16:56:53', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('157613846664232962', '历史书籍', 'lssj', '1', '1', '2', '1', '2022-05-26 16:58:59', '192.168.1.23', '1', '2022-05-26 16:58:59', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('157640817825726464', '测试节点', 'lssj1', '1', '1', '1', '1', '2022-05-26 18:46:10', '192.168.1.23', '1', '2023-01-31 05:34:27', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('229352398221131781', 'tag', 'tag', '1', '1', null, null, null, null, null, null, null, null, null);
INSERT INTO `k_terms` VALUES ('247472124566618119', '别名测试', 'biemingceshi', '1', '1', null, null, null, null, null, null, null, null, null);
INSERT INTO `k_terms` VALUES ('247472807927791621', '别名', 'bieming', '1', '1', null, null, null, null, null, null, null, 'normal', null);
INSERT INTO `k_terms` VALUES ('249734249670426635', '文档', 'wendang', '1', '1', null, '1', '2023-02-04 21:52:35', '192.168.1.23', '1', '2023-02-04 21:52:35', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('250847297604861960', '科技', 'keji', '1', '1', '1', '1', '2023-02-07 23:35:26', '192.168.1.23', '1', '2023-02-07 23:35:26', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520479861269512197', '人物年谱', 'rwnp', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520480022150430731', '历史年谱', 'lsnp', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520481497194872837', '菜谱', '满汉全席', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520507717504647173', '满汉全席', 'mhqx', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520507854301872132', '八大菜系', 'bdcx', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520508106803167234', '冀冀鲁豫', 'jjly', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520528863558025224', '行业年谱', 'hynp', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520528949428011010', '学术年谱', 'xsnp', '0', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520551727518629888', '技术分享', 'jsfx', '1', '1', '1', null, null, null, '1', '2023-01-24 20:51:45', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520552212724105224', '技术推广', 'jstg', '1', '1', '4', null, null, null, '1', '2023-01-24 20:52:18', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520552344769183746', '技术转让', 'jszr', '1', '1', '2', null, null, null, '1', '2023-01-24 20:51:57', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520552631105929220', '源码教程', 'ymjc', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520552839994851334', 'CMS源码', 'cmsym', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520553045905817607', '视频教程', 'spjc', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520553269307031556', '原创音频', 'ycyp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1520553543710982147', '平面设计', 'pmsj', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522017666332278790', '本地服务', 'bdfw', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522024940970885127', '年谱制作', 'npzz', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522025100346048522', '古书典籍', 'gsdj', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522025222337380358', '家藏珍本', 'jczb', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522025656049385473', '二手回收', 'eshs', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522025767231995910', '教育培训', 'jypx', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026041568837640', '在线出租', 'zxcz', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026269613146117', '武术器械', 'wsqx', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026601579724811', '宗谱族谱', 'zpzp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026728344174603', '琴棋书画', 'qqsh', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026804894416903', '兵器谱', 'bqp1', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522026919600242699', '脸谱戏谱', 'lpxp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027076064559113', '历史年表', 'lsnb', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027230431723524', '药谱医谱', 'ypyp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027318260449291', '画谱', 'hp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027383041474561', '书法谱', 'sfp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027479413997569', '诗词歌赋', 'scgf', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027568819781632', '舞谱', 'wp', '1', '1', '10', null, null, null, '1', '2023-02-04 01:37:13', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027695852666887', '曲谱歌谱', 'qpgp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027830473048070', '食谱药膳', 'spys', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522027924211548168', '特产谱', 'tcp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028028079292427', '品牌谱', 'ppp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028131523411968', '农业节气', 'nyjq', '1', '1', '15', null, null, null, '1', '2023-01-31 05:32:36', '192.168.1.23', 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028337551818753', '五谷杂粮', 'wgzl', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028440077385734', '兵器谱', 'bqp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028770252996615', '网站源码', 'wzym', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522028888591089674', '科普学习', 'kpxx', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522029221132288003', '知识图谱', 'zstp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522029609935880200', '软件开发', 'rjkf', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522029895844806666', '故事大全', 'gsdq', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522030055626817539', '动漫游艺', 'dmyy', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522030229187117064', '商业BP', 'sybp', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522030689604255755', '社区服务', 'sqfw', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522030830335737867', '练摊经验', 'ltjy', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522031020211879945', '热门爱好', 'rmah', '1', '1', null, null, null, null, null, null, null, 'normal', '1');
INSERT INTO `k_terms` VALUES ('1522031215049883657', '网红打卡', 'whdk', '1', '1', null, null, null, null, null, null, null, 'normal', '1');

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
-- Records of wo_app
-- ----------------------------
INSERT INTO `wo_app` VALUES ('79317353314828295', '测试应用', 'wldos-test', 'test', '应用配置的演示。', 'private', '0', '1', '1', '2021-10-22 15:36:41', '192.168.1.23', '1', '2021-10-22 15:41:03', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504586670225932123', '创作工具', 'wldos-book', 'book', '各种作品创作工具', 'app', '0', '1', '100', '2021-04-28 17:33:04', '192.168.1.23', '1', '2023-02-12 14:19:17', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504617964850823176', '信息发布', 'wldos-info', 'info', '信息发布平台', 'app', '0', '1', '0', '2021-05-14 22:54:37', '127.0.0.1', '0', '2021-05-14 22:54:37', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504618238889869317', '内容管理', 'wldos-cms', 'cms', '通用CMS', 'app', '0', '1', '0', '2021-05-14 22:55:43', '127.0.0.1', '0', '2021-05-14 22:55:43', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504619730199822347', '智能客服', 'wldos-custom', 'cust', '在线客服系统', 'app', '0', '1', '0', '2021-05-14 23:01:38', '127.0.0.1', '0', '2021-05-14 23:01:38', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504620293595512835', '舆情监测', 'wldos-spider', 'spide', '一个简单的信息抓取、分析工具', 'app', '0', '1', '0', '2021-05-14 23:03:52', '127.0.0.1', '0', '2021-05-14 23:03:52', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504620908216238080', '问答系统', 'wldos-question', 'quest', '加强版的问卷调查发布、收集工具加强版的问卷调查发布、收集工具', 'app', '0', '1', '0', '2021-05-14 23:06:19', '127.0.0.1', '1', '2021-12-27 18:24:43', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504621411327197195', '工作流平台', 'wldos-workflow', 'wflow', '通用自定义工作流引擎的工作流', 'app', '0', '1', '0', '2021-05-14 23:08:19', '127.0.0.1', '0', '2021-05-14 23:08:19', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504628840064532482', '报表平台', 'wldos-report', 'repor', '定制化报表系统', 'app', '0', '1', '0', '2021-05-14 23:37:50', '127.0.0.1', '0', '2021-05-14 23:37:50', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504629388687884291', '云评估平台', 'wldos-assess', 'asses', '一个简单的评估引擎', 'app', '0', '1', '0', '2021-05-14 23:40:01', '127.0.0.1', '0', '2021-05-14 23:40:01', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1504630177560969219', '快速开发平台', 'wldos-egine', 'dev', '全方位快速搭建企业级应用平台', 'app', '0', '1', '0', '2021-05-14 23:43:09', '127.0.0.1', '0', '2021-05-14 23:43:09', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1506005013902311434', '系统管理', 'wldos-admin', 'admin', 'SaaS平台管理端', 'platform', '0', '1', '1', '2021-05-18 18:46:16', '127.0.0.1', '1', '2021-05-18 18:46:16', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1506113043159498757', '门户', 'wldos-portal', '/', 'SaaS平台前端门户', 'app', '0', '1', '0', '2021-05-19 01:55:32', '127.0.0.1', '0', '2021-05-19 01:55:32', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_app` VALUES ('1511494438434291716', '用户设置', 'abcdefgjdlfjsd', 'user', '前端用户设置', 'app', '0', '1', '1', '2021-06-02 22:19:16', '127.0.0.1', '1', '2021-06-02 22:19:16', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_app` VALUES ('1533901932104171527', '文档', 'wldos-doc', 'doc', '文档库', 'platform', '0', '1', '1', '2021-08-03 18:18:39', '192.168.1.23', '1', '2021-08-03 18:18:39', '192.168.1.23', 'normal', '1');

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
-- Records of wo_architecture
-- ----------------------------
INSERT INTO `wo_architecture` VALUES ('100', 'org', '组织机构', null, '0', '0', '2', '1', '0', '2021-05-08 14:58:35', '0', '0', '127.0.0.1', '2021-05-26 23:13:40', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('200', 'team', '团队', null, '0', '0', '3', '1', '0', '2021-05-08 15:00:33', '0', '0', '0', '2021-05-08 15:00:41', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('300', 'group', '群组', null, '0', '0', '4', '1', '0', '2021-05-08 15:01:19', '0', '0', '0', '2021-05-08 15:01:32', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('400', 'circle', '圈子', null, '0', '0', '5', '1', '0', '2021-05-08 15:02:37', '0', '0', '0', '2021-05-08 15:02:46', 'normal', null);
INSERT INTO `wo_architecture` VALUES ('93033238315581451', 'test', 'test', null, '1508132284859596808', '0', null, '1', '92829405966680072', '2021-11-29 11:58:42', '192.168.1.23', '92829405966680072', '192.168.1.23', '2021-11-29 11:58:42', 'normal', '1');
INSERT INTO `wo_architecture` VALUES ('103228366573453322', 'test-c', '子体系', null, '0', '93033238315581451', '1', '1', '1', '2021-12-27 15:10:30', '192.168.1.23', '1', '192.168.1.23', '2021-12-27 15:10:30', 'normal', '1');
INSERT INTO `wo_architecture` VALUES ('1529501287100104708', 'finance', '金融模块', '这是一个示例，作为平台唯一的超级管理员有权限查看所有数据，当然可以设置租户数据保密，那样任何人都看不到对方数据。', '1508972831958548480', '0', '6', '1', '1', '2021-07-22 14:52:04', '192.168.1.23', '1', '192.168.1.23', '2021-07-22 14:52:04', 'normal', '1');

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
-- Records of wo_auth_role
-- ----------------------------
INSERT INTO `wo_auth_role` VALUES ('94177914326269957', '91933147798355971', '91697439556943878', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914330464264', '91933147798355971', '91681543228669952', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914334658566', '91933147798355971', '91682380176867329', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914338852874', '91933147798355971', '91657329280991232', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914343047174', '91933147798355971', '91683279729246214', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914343047175', '91933147798355971', '91698429899227137', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914343047176', '91933147798355971', '91681269747466245', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914343047177', '91933147798355971', '91659367716929536', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914540179467', '91933147798355971', '91698261745385474', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914544373771', '91933147798355971', '91697876372733961', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914569539589', '91933147798355971', '91698614343745546', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('94177914573733894', '91933147798355971', '91697684151975939', '1506005013902311434', '1', '1', '2021-12-02 15:47:15', '192.168.1.23', '1', '2021-12-02 15:47:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227151183880', '91960652726976520', '1531059437984989189', '1504618238889869317', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378177', '91960652726976520', '100', '1506113043159498757', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378178', '91960652726976520', '1506128956323708934', '1506005013902311434', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378179', '91960652726976520', '72188271301148676', '1504618238889869317', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378180', '91960652726976520', '1523270396090695683', '1504617964850823176', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378181', '91960652726976520', '91697439556943878', '1506005013902311434', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227155378182', '91960652726976520', '1506127499163779081', '1504617964850823176', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227159572491', '91960652726976520', '1522661730694119427', '1504617964850823176', '1', '1', '2022-01-16 17:18:04', '192.168.1.23', '1', '2022-01-16 17:18:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('110508227176349698', '91960652726976520', '110507156089520135', '1504618238889869317', '1', '1', '2022-01-16 17:18:05', '192.168.1.23', '1', '2022-01-16 17:18:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684869', '1506681836080381960', '1531059437984989189', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684870', '1506681836080381960', '1542939849472524294', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684871', '1506681836080381960', '1511496878801993736', '1511494438434291716', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684872', '1506681836080381960', '1533941630155538434', '1533901932104171527', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684873', '1506681836080381960', '1511495802770079746', '1511494438434291716', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574555684874', '1506681836080381960', '1522661730694119427', '1504617964850823176', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574559879171', '1506681836080381960', '1539042480926408715', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574564073476', '1506681836080381960', '1511494161530535947', '1511494438434291716', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574589239298', '1506681836080381960', '1522997390516862983', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574610210817', '1506681836080381960', '1542170368806666244', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574610210818', '1506681836080381960', '1523270396090695683', '1504617964850823176', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574614405121', '1506681836080381960', '156908317952360451', '1504617964850823176', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574618599432', '1506681836080381960', '1511736612090462209', '1511494438434291716', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574622793731', '1506681836080381960', '1539043101486268425', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574626988039', '1506681836080381960', '1506127499163779081', '1504617964850823176', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574626988040', '1506681836080381960', '103211568641785859', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574631182343', '1506681836080381960', '1533950643823886345', '1533901932104171527', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574635376648', '1506681836080381960', '1538994469231837188', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574643765253', '1506681836080381960', '1539043349348663305', '1504618238889869317', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('156908574647959559', '1506681836080381960', '100', '1506113043159498757', '1', '1', '2022-05-24 18:16:29', '192.168.1.23', '1', '2022-05-24 18:16:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137892106240', '2', '1511495802770079746', '1511494438434291716', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300544', '2', '1542939849472524294', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300545', '2', '1511496878801993736', '1511494438434291716', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300546', '2', '1531059437984989189', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300547', '2', '1533950643823886345', '1533901932104171527', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300548', '2', '246159055248867338', '1533901932104171527', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137896300549', '2', '1511736612090462209', '1511494438434291716', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137900494855', '2', '1538994469231837188', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137925660677', '2', '246158602809294853', '1533901932104171527', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137950826497', '2', '1511494161530535947', '1511494438434291716', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137963409414', '2', '1506127499163779081', '1504617964850823176', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137967603713', '2', '1539043349348663305', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137975992324', '2', '1523270396090695683', '1504617964850823176', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137980186626', '2', '246163063116054536', '1533901932104171527', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137980186627', '2', '1522661730694119427', '1504617964850823176', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137984380931', '2', '1539042480926408715', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137988575237', '2', '1522997390516862983', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163137996963844', '2', '129381329150853120', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163138013741063', '2', '1539043101486268425', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163138017935360', '2', '1542170368806666244', '1504618238889869317', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163138026323971', '2', '100', '1506113043159498757', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163138030518276', '2', '156908317952360451', '1504617964850823176', '1', '1', '2023-01-26 01:22:16', '192.168.1.23', '1', '2023-01-26 01:22:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647108', '1', '246159055248867338', '1533901932104171527', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647109', '1', '156908317952360451', '1504617964850823176', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647110', '1', '1542170368806666244', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647111', '1', '1539043101486268425', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647112', '1', '246158602809294853', '1533901932104171527', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171513647113', '1', '246163063116054536', '1533901932104171527', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171517841408', '1', '1542939849472524294', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171526230021', '1', '100', '1506113043159498757', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171555590144', '1', '1539042480926408715', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171580755969', '1', '1538994469231837188', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171589144583', '1', '1539043349348663305', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171589144584', '1', '1533950643823886345', '1533901932104171527', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163171589144585', '1', '1522997390516862983', '1504618238889869317', '1', '1', '2023-01-26 01:22:24', '192.168.1.23', '1', '2023-01-26 01:22:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208155086853', '1509213016482824194', '1523270396090695683', '1504617964850823176', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208159281162', '1509213016482824194', '1539042480926408715', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208159281163', '1509213016482824194', '1506122443605590022', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208159281164', '1509213016482824194', '1542170368806666244', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208159281165', '1509213016482824194', '1509212249457868808', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208159281166', '1509213016482824194', '93481427766263818', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208163475466', '1509213016482824194', '1511495802770079746', '1511494438434291716', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208167669760', '1509213016482824194', '1509177915053096962', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208180252682', '1509213016482824194', '1531059437984989189', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208205418498', '1509213016482824194', '1506127499163779081', '1504617964850823176', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208218001408', '1509213016482824194', '1538994469231837188', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208218001409', '1509213016482824194', '1520466841923403786', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208218001410', '1509213016482824194', '93529533534879755', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208222195715', '1509213016482824194', '152162057265528840', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208222195716', '1509213016482824194', '1506125438066016267', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208226390022', '1509213016482824194', '1509211664167911432', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208234778635', '1509213016482824194', '72188271301148676', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208238972935', '1509213016482824194', '246158602809294853', '1533901932104171527', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208247361546', '1509213016482824194', '151359177717628933', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208259944449', '1509213016482824194', '203336650390683654', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208264138762', '1509213016482824194', '1539043349348663305', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208268333057', '1509213016482824194', '1520374289564090377', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208268333058', '1509213016482824194', '156908317952360451', '1504617964850823176', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208272527363', '1509213016482824194', '1509184818118311946', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208276721673', '1509213016482824194', '127144910604910596', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208280915970', '1509213016482824194', '93474930835505163', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208289304584', '1509213016482824194', '1522661730694119427', '1504617964850823176', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208293498891', '1509213016482824194', '1511736612090462209', '1511494438434291716', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208306081798', '1509213016482824194', '1506128956323708934', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208322859013', '1509213016482824194', '220685278113349639', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208322859014', '1509213016482824194', '1518555863791091716', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208327053322', '1509213016482824194', '129381329150853120', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208331247627', '1509213016482824194', '246159055248867338', '1533901932104171527', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208331247628', '1509213016482824194', '1511494161530535947', '1511494438434291716', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208339636234', '1509213016482824194', '1506101733801771011', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208343830539', '1509213016482824194', '1528063986125946885', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208343830540', '1509213016482824194', '1506128052832878593', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208343830541', '1509213016482824194', '1509179615117754368', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208356413450', '1509213016482824194', '1532485035156488196', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208381579265', '1509213016482824194', '1533950643823886345', '1533901932104171527', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208385773579', '1509213016482824194', '1522997390516862983', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208385773580', '1509213016482824194', '1511496878801993736', '1511494438434291716', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208385773581', '1509213016482824194', '246163063116054536', '1533901932104171527', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208389967878', '1509213016482824194', '1506107866432061443', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208398356489', '1509213016482824194', '72187658739826691', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208402550793', '1509213016482824194', '93441238276685825', '1506005013902311434', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208402550794', '1509213016482824194', '100', '1506113043159498757', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208427716616', '1509213016482824194', '1542939849472524294', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163208431910913', '1509213016482824194', '1539043101486268425', '1504618238889869317', '1', '1', '2023-01-26 01:22:32', '192.168.1.23', '1', '2023-01-26 01:22:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246465859587', '1525946478916976648', '91692030788026373', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246470053899', '1525946478916976648', '1520374289564090377', '1504618238889869317', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246474248196', '1525946478916976648', '129381329150853120', '1504618238889869317', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246478442507', '1525946478916976648', '91682380176867329', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246486831113', '1525946478916976648', '151359177717628933', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246507802635', '1525946478916976648', '1539042480926408715', '1504618238889869317', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246520385538', '1525946478916976648', '91683279729246214', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246528774144', '1525946478916976648', '1522997390516862983', '1504618238889869317', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246537162762', '1525946478916976648', '91692305787568136', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246591688709', '1525946478916976648', '1518555863791091716', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246616854530', '1525946478916976648', '1532485035156488196', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246621048843', '1525946478916976648', '1511495802770079746', '1511494438434291716', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246633631745', '1525946478916976648', '91657329280991232', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246633631746', '1525946478916976648', '246163063116054536', '1533901932104171527', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246637826048', '1525946478916976648', '1539043349348663305', '1504618238889869317', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246642020359', '1525946478916976648', '152162057265528840', '1506005013902311434', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246646214662', '1525946478916976648', '1511496878801993736', '1511494438434291716', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246675574790', '1525946478916976648', '1511736612090462209', '1511494438434291716', '1', '1', '2023-01-26 01:22:41', '192.168.1.23', '1', '2023-01-26 01:22:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246679769094', '1525946478916976648', '1511494161530535947', '1511494438434291716', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246692352004', '1525946478916976648', '91692604820471817', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246700740613', '1525946478916976648', '1522661730694119427', '1504617964850823176', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246709129227', '1525946478916976648', '91681543228669952', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246709129228', '1525946478916976648', '1542939849472524294', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246713323524', '1525946478916976648', '246159055248867338', '1533901932104171527', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246713323525', '1525946478916976648', '91659367716929536', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246713323526', '1525946478916976648', '91689774567047170', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246713323527', '1525946478916976648', '72187658739826691', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246725906437', '1525946478916976648', '91689150362337290', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246738489355', '1525946478916976648', '91690561644969994', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246755266565', '1525946478916976648', '91690096047865861', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246763655169', '1525946478916976648', '72188271301148676', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246763655170', '1525946478916976648', '1506128956323708934', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246763655171', '1525946478916976648', '1539043101486268425', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246763655172', '1525946478916976648', '1531059437984989189', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246772043777', '1525946478916976648', '91688588170412035', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246776238087', '1525946478916976648', '1538994469231837188', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246776238088', '1525946478916976648', '93529533534879755', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246793015296', '1525946478916976648', '91688861186048007', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246809792518', '1525946478916976648', '91686165863383045', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246813986823', '1525946478916976648', '1523270396090695683', '1504617964850823176', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246818181129', '1525946478916976648', '1506128052832878593', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246818181130', '1525946478916976648', '246158602809294853', '1533901932104171527', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246818181131', '1525946478916976648', '1509212249457868808', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246826569738', '1525946478916976648', '156908317952360451', '1504617964850823176', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246826569739', '1525946478916976648', '91681269747466245', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246826569740', '1525946478916976648', '1509179615117754368', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246834958341', '1525946478916976648', '1506101733801771011', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246843346955', '1525946478916976648', '1533950643823886345', '1533901932104171527', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246860124169', '1525946478916976648', '1506127499163779081', '1504617964850823176', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246872707079', '1525946478916976648', '1509184818118311946', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246872707080', '1525946478916976648', '93481427766263818', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246876901380', '1525946478916976648', '100', '1506113043159498757', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246881095686', '1525946478916976648', '1509211664167911432', '1506005013902311434', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246881095687', '1525946478916976648', '1542170368806666244', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_auth_role` VALUES ('246163246885289991', '1525946478916976648', '1520466841923403786', '1504618238889869317', '1', '1', '2023-01-26 01:22:42', '192.168.1.23', '1', '2023-01-26 01:22:42', '192.168.1.23', 'normal', '0');

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
-- Records of wo_com_user
-- ----------------------------
INSERT INTO `wo_com_user` VALUES ('76410037242347529', '100', '0', '1', '1', '1', '2021-10-14 15:04:03', '192.168.1.23', '1', '2021-10-14 15:04:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_com_user` VALUES ('76410119840776197', '1502770377176825865', '0', '1', '1', '1', '2021-10-14 15:04:22', '192.168.1.23', '1', '2021-10-14 15:04:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_com_user` VALUES ('79375255673159685', '1502777260587532299', '0', '1', '1', '1', '2021-10-22 19:26:46', '192.168.1.23', '1', '2021-10-22 19:26:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_com_user` VALUES ('91933652180189185', '1', '0', '1', '1', '1', '2021-11-26 11:09:21', '192.168.1.23', '1', '2021-11-26 11:09:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_com_user` VALUES ('92816916029816835', '81514195872038918', '1508132284859596808', '1', '1', '1', '2021-11-28 21:39:07', '192.168.1.23', '1', '2021-11-28 21:39:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_com_user` VALUES ('92829651731922951', '92829405966680072', '1508132284859596808', '1', '1', '1', '2021-11-28 22:29:44', '192.168.1.23', '1', '2021-11-28 22:29:44', '192.168.1.23', 'normal', '0');

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
-- Records of wo_company
-- ----------------------------
INSERT INTO `wo_company` VALUES ('91950680261705729', 'test', 'test', null, '0', '5', '1', '100', '2021-11-26 12:17:01', '192.168.1.23', '1', '2023-01-14 21:59:21', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_company` VALUES ('242125723271872513', 'testc', 'test子集', null, '91950680261705729', '1', '1', '1', '2023-01-14 21:59:01', '192.168.1.23', '1', '2023-01-14 21:59:01', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508132284859596808', 'aadfk18665377sdkfjsd', '树悉猿科技', '专业软件研发，专业软件研发专业软件研发专业软件研发专业软件研发专业软件研发', '0', '3', '1', '0', '2021-05-24 15:39:16', '127.0.0.1', '1', '2021-12-27 17:01:04', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972085879947270', '886887854321', '天工开物科技有限公司', null, '0', '2', '1', '0', '2021-05-26 23:16:21', '127.0.0.1', '1', '2021-09-26 15:25:18', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972512004456457', '88688785432123423', '泥巴巴和稀泥广告咨询有限公司', null, '0', '5', '1', '0', '2021-05-26 23:18:02', '127.0.0.1', '0', '2021-05-26 23:18:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_company` VALUES ('1508972831958548480', 'babala100', '给你未来科创股份有限公司', null, '0', '6', '1', '0', '2021-05-26 23:19:18', '127.0.0.1', '0', '2021-05-26 23:19:18', '127.0.0.1', 'normal', '1');

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
-- Records of wo_domain
-- ----------------------------
INSERT INTO `wo_domain` VALUES ('1', '0', 'wldos.com', 'www', 'WLDOS', 'http://www.wldos.com', '/logo-wldos.svg', 'WLDOS互联网开放运营系统', '业务中台|多租户|SaaS', 'WLDOS互联网开放运营系统是一个业务中台的最佳实践', null, '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-5730-355</strong></span>\r\n</p>\r\n<p>Q Q： 583716365 306991142</p>\r\n<p>邮箱： support@zhiletu.com</p>\r\n<p>地址： 山东省济南市长清区海棠路5555</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\r\n<a href=\"https://www.xiupu.net\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\r\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\r\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37011302000311\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\r\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> 鲁公网安备 37011302000311号</a>&nbsp;\r\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备20011831号</a>\r\n<a href=\"https://www.zhiletu.com/privacy\">法律声明</a> | <a href=\"https://www.zhiletu.com/privacy\">隐私协议</a> | Copyright © 2021\r\n<a href=\"https://www.zhiletu.com/\" rel=\"nofollow\">智乐兔</a> 版权所有\r\n</p>', null, '2', null, '1', '1', '2021-08-02 18:39:15', '192.168.1.23', '1', '2021-09-12 21:08:29', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('93037725495246854', '1508132284859596808', 'tenant.com', 'tenant', '租户域演示', 'http://www.tenant.com', '/logo-wldos.svg', '租户域演示网站', '租户域演示', '租户域演示', null, null, null, null, null, '1', 't.com', '1', '92829405966680072', '2021-11-29 12:16:32', '192.168.1.23', '1', '2022-02-18 01:37:41', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('134430616675074056', '0', 'test.com', 'test', 'test', 'test.com', null, 'test', 'test', 'test', null, null, null, null, null, '7', 'test', '1', '1', '2022-03-23 17:37:07', '192.168.1.23', '1', '2022-03-23 17:37:07', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('134434749914202120', '0', 'tst.com', 'tst', 'tst', 'tst.com', '/202203/23175316LUhpaUfQ.svg', 'tst', 'tst', 'sts', null, null, null, null, null, '8', null, '1', '1', '2022-03-23 17:53:32', '192.168.1.23', '1', '2022-03-23 17:53:32', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('1533544727530094592', '0', 'localhost', 'localhost', 'WLDOS-KPayCMS', 'http://localhost:8000', '/logo-wldos.svg', '基于WLDOS开发的内容付费系统', 'WLDOS|内容付费|多租户|多站|SaaS|微服务|serviceMesh|云管端', '基于WLDOS搭建云物互联应用', 'KPayCMS 是WLDOS开发的 内容付费系统', '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-ABCD-EFG</strong></span>\r\n</p>\r\n<p>Q Q： 583ABC365 30DEFQ142</p>\r\n<p>邮箱： support#abcdefg.com</p>\r\n<p>服务： 周一至周六 9:00~17:30</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\n<a href=\"https://www.xiupu.cc\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37xxxx20xxxxx\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> X公网安备 3701xxx20xxxxx号</a>&nbsp;\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备2xx1xxxx号</a>\n<a href=\"https://www.wldos.com/privacy\">法律声明</a> | <a href=\"https://www.wldos.com/privacy\">隐私协议</a> | Copyright © 2022\n<a href=\"https://www.wldos.com/\" rel=\"nofollow\">WLDOS</a> 版权所有\n</p>', null, '6', 'god.com,192.168.1.23', '1', '1', '2021-08-06 18:11:32', '1', '1', '2023-02-15 17:47:29', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('1533985924929208330', '0', 'xiupu.cc', 'xiupu', '嗅谱网', 'https://www.xiupu.net', '', '嗅谱网_找谱·写谱·出谱-嗅谱·秀谱·说谱', '找谱·写谱·出谱-嗅谱·秀谱·说谱', '一个和各种谱打交道的靠谱网站', null, '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-5730-355</strong></span>\r\n</p>\r\n<p>Q Q： 583716365 306991142</p>\r\n<p>邮箱： support@zhiletu.com</p>\r\n<p>地址： 山东省济南市长清区海棠路5555</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\r\n<a href=\"https://www.xiupu.net\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\r\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\r\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37011302000311\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\r\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> 鲁公网安备 37011302000311号</a>&nbsp;\r\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备20011831号</a>\r\n<a href=\"https://www.zhiletu.com/privacy\">法律声明</a> | <a href=\"https://www.zhiletu.com/privacy\">隐私协议</a> | Copyright © 2021\r\n<a href=\"https://www.zhiletu.com/\" rel=\"nofollow\">智乐兔</a> 版权所有\r\n</p>', null, '4', null, '0', '1', '2021-08-03 23:52:25', '192.168.1.23', '1', '2023-01-26 00:10:22', '192.168.1.23', 'normal', '1');

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
-- Records of wo_domain_app
-- ----------------------------
INSERT INTO `wo_domain_app` VALUES ('93048206385659906', '1506113043159498757', '93037725495246854', '1508132284859596808', '1', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('93048207471984651', '1504617964850823176', '93037725495246854', '1508132284859596808', '1', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('93048207476178950', '1504618238889869317', '93037725495246854', '1508132284859596808', '1', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('93048207476178951', '1511494438434291716', '93037725495246854', '1508132284859596808', '1', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', '92829405966680072', '2021-11-29 12:58:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556163317767', '1504629388687884291', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556171706373', '1504630177560969219', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556175900676', '79317353314828295', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556180094984', '1504619730199822347', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556180094985', '1504620908216238080', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556180094986', '1504586670225932123', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556180094987', '1511494438434291716', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556184289287', '1504628840064532482', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556234620935', '1504620293595512835', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556234620936', '1504618238889869317', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556251398152', '1504621411327197195', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556255592451', '1506113043159498757', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556255592452', '1504617964850823176', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556255592453', '1506005013902311434', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('96000556255592454', '1533901932104171527', '1534253403467333638', '0', '1', '1', '2021-12-07 16:29:46', '192.168.1.23', '1', '2021-12-07 16:29:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('100850427156414468', '1506005013902311434', '93037725495246854', '1508132284859596808', '1', '1', '2021-12-21 01:41:26', '192.168.1.23', '1', '2021-12-21 01:41:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('110440154578731019', '1506005013902311434', '1533985924929208330', '0', '1', '1', '2022-01-16 12:47:35', '192.168.1.23', '1', '2022-01-16 12:47:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('110440154591313922', '1504617964850823176', '1533985924929208330', '0', '1', '1', '2022-01-16 12:47:35', '192.168.1.23', '1', '2022-01-16 12:47:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('110440154595508235', '1506113043159498757', '1533985924929208330', '0', '1', '1', '2022-01-16 12:47:35', '192.168.1.23', '1', '2022-01-16 12:47:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('110440154599702530', '1511494438434291716', '1533985924929208330', '0', '1', '1', '2022-01-16 12:47:35', '192.168.1.23', '1', '2022-01-16 12:47:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('110440154603896838', '1504618238889869317', '1533985924929208330', '0', '1', '1', '2022-01-16 12:47:35', '192.168.1.23', '1', '2022-01-16 12:47:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('246160895495553030', '1533901932104171527', '1533544727530094592', '0', '1', '1', '2023-01-26 01:13:21', '192.168.1.23', '1', '2023-01-26 01:13:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884734824457', '1506005013902311434', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884751601665', '1506113043159498757', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884755795969', '1504617964850823176', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884764184577', '1504586670225932123', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884764184578', '1511494438434291716', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533531884764184579', '1504618238889869317', '1532487189283913738', '0', '1', '1', '2021-08-02 17:48:13', '192.168.1.23', '1', '2021-08-02 17:48:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544854940467205', '1504628840064532482', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544854965633031', '1504617964850823176', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855095656454', '1504618238889869317', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855112433675', '1506113043159498757', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855120822278', '1504619730199822347', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855125016584', '1506005013902311434', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855133405184', '1504586670225932123', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855145988103', '1504621411327197195', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855183736833', '1511494438434291716', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855208902661', '1504630177560969219', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855234068481', '1504629388687884291', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855263428612', '1504620908216238080', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1533544855271817222', '1504620293595512835', '1533544727530094592', '0', '1', '1', '2021-08-02 18:39:45', '192.168.1.23', '1', '2021-08-02 18:39:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1536714949485314059', '1506005013902311434', '1', '0', '1', '1', '2021-08-11 12:36:35', '192.168.1.23', '1', '2021-08-11 12:36:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1536714949497896968', '1504618238889869317', '1', '0', '1', '1', '2021-08-11 12:36:35', '192.168.1.23', '1', '2021-08-11 12:36:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354262884352', '1504629388687884291', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354283855883', '1533901932104171527', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354288050184', '1506005013902311434', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354292244489', '1504620293595512835', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354296438786', '1511494438434291716', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354300633089', '1504628840064532482', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354300633090', '1504630177560969219', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354300633091', '1506113043159498757', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354346770440', '1504586670225932123', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354355159043', '1504617964850823176', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354355159044', '1504618238889869317', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354363547648', '1504620908216238080', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354367741959', '1504621411327197195', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_domain_app` VALUES ('1546146354371936260', '1504619730199822347', '1', '0', '1', '1', '2021-09-06 13:13:37', '192.168.1.23', '1', '2021-09-06 13:13:37', '192.168.1.23', 'normal', '0');

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
-- Records of wo_domain_resource
-- ----------------------------
INSERT INTO `wo_domain_resource` VALUES ('3', 'static', '1506101733801771011', '1506005013902311434', '0', '1', '1', '系统管理', '1', '127.0.0.1', '2021-09-09 22:31:29', '1', '127.0.0.1', '2021-09-09 22:31:38', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('5', 'default', '100', '1506113043159498757', '4', '1532487189283913738', '1', '轩辕年谱首页门户组件映射', '1', '127.0.0.1', '2021-09-24 01:29:33', '1', '127.0.0.1', '2021-09-24 01:29:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('6', 'category', '100', '1506113043159498757', '0', '1533544727530094592', '1', '本地开发环境', '1', '123', '2021-09-24 01:37:34', '1', '111', '2021-09-24 01:37:42', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('72187658739826689', 'static', '72187658739826691', '1504618238889869317', '0', '1533544727530094592', '1', '管理一个作品，文章、年谱、视频等', '1', '192.168.1.23', '2021-10-02 23:25:49', '1', '192.168.1.23', '2021-10-02 23:27:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('72188271301148674', 'static', '72188271301148676', '1504618238889869317', '0', '1533544727530094592', '1', '作品合集管理，对应一个产品信息', '1', '192.168.1.23', '2021-10-02 23:28:15', '1', '192.168.1.23', '2021-10-02 23:28:15', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('76422235884797960', 'static', '1542939849472524294', '1504618238889869317', '1520551727518629888', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-10-14 15:52:31', '1', '192.168.1.23', '2021-10-14 15:52:31', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291112153094', 'static', '91681543228669952', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291221204998', 'static', '91657329280991232', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291225399301', 'static', '91682380176867329', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291233787912', 'static', '91683279729246214', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291237982213', 'static', '91659367716929536', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93037291237982214', 'static', '91681269747466245', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-29 12:14:49', '1', '192.168.1.23', '2021-11-29 12:14:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721951047686', 'static', '1539042480926408715', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721959436289', 'default', '100', '1506113043159498757', '4', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721963630595', 'static', '1522997390516862983', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721967824903', 'static', '1511496878801993736', '1511494438434291716', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721967824904', 'static', '1511736612090462209', '1511494438434291716', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721967824905', 'archives', '1542170368806666244', '1504618238889869317', '1', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721967824906', 'static', '1538994469231837188', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056721967824907', 'static', '1511494161530535947', '1511494438434291716', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056722018156554', 'static', '1511495802770079746', '1511494438434291716', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056722018156555', 'static', '1539043101486268425', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056722030739459', 'static', '1539043349348663305', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93056722030739460', 'static', '1542939849472524294', '1504618238889869317', '0', '93037725495246854', '1', null, '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', '92829405966680072', '192.168.1.23', '2021-11-29 13:32:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93451201405435909', 'static', '93441238276685825', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-30 15:39:33', '1', '192.168.1.23', '2021-11-30 15:39:33', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93476224014925824', 'static', '93474930835505163', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-30 17:18:59', '1', '192.168.1.23', '2021-11-30 17:18:59', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93482803548307460', 'static', '93481427766263818', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-30 17:45:07', '1', '192.168.1.23', '2021-11-30 17:45:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93536387409297419', 'static', '93529533534879755', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-30 21:18:03', '1', '192.168.1.23', '2021-11-30 21:18:03', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('93537786595557378', 'static', '1520374289564090377', '1504618238889869317', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-11-30 21:23:36', '1', '192.168.1.23', '2021-11-30 21:23:36', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895876775941', 'static', '91683279729246214', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553157', 'static', '91694508451479553', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553158', 'static', '1506101733801771011', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553159', 'static', '91284578737242117', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553160', 'static', '1522997390516862983', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553161', 'static', '91686165863383045', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553162', 'static', '1506128052832878593', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895893553163', 'static', '91651964644540418', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895906136071', 'static', '1528063986125946885', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895906136072', 'static', '91681543228669952', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913283', 'static', '91696827939340293', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913284', 'static', '1533941630155538434', '1533901932104171527', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913285', 'static', '1506126590341988354', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913286', 'static', '91688588170412035', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913287', 'static', '1506127499163779081', '1504617964850823176', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913288', 'static', '91652500538179591', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913289', 'static', '91682380176867329', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895922913290', 'static', '72187658739826691', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895939690501', 'static', '91694018015707139', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895939690502', 'static', '1506128956323708934', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895939690503', 'static', '91688861186048007', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895939690504', 'static', '1532485035156488196', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895939690505', 'static', '1506126989799112705', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895956467719', 'static', '1520466841923403786', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895956467720', 'static', '1533950643823886345', '1533901932104171527', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895956467721', 'static', '91697439556943878', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895956467722', 'static', '93474930835505163', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895973244939', 'static', '1506122443605590022', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895973244940', 'static', '91694308215406602', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895973244941', 'static', '91693516968345605', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895973244942', 'static', '1509211664167911432', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022154', 'static', '91684501538390022', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022155', 'static', '91651120842850304', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022156', 'static', '1511496878801993736', '1511494438434291716', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022157', 'static', '91697684151975939', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022158', 'static', '91693753137020928', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022159', 'static', '91316759807311877', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022160', 'static', '91684884843249671', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000895990022161', 'static', '91689150362337290', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896006799362', 'static', '91657329280991232', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896006799363', 'static', '93481427766263818', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896006799364', 'static', '91651539862208517', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896006799365', 'static', '1511736612090462209', '1511494438434291716', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576585', 'static', '1506125438066016267', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576586', 'static', '1522661730694119427', '1504617964850823176', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576587', 'static', '1506107866432061443', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576588', 'static', '1539043349348663305', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576589', 'static', '91689774567047170', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896023576590', 'static', '91683874691268617', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353798', 'archives', '100', '1506113043159498757', '4', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353799', 'static', '91685468166078466', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353800', 'archives', '1542170368806666244', '1504618238889869317', '1', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353801', 'static', '1523270396090695683', '1504617964850823176', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353802', 'static', '91282165825454087', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353803', 'static', '91684261066358788', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896040353804', 'static', '91659367716929536', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131019', 'static', '91698261745385474', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131020', 'static', '1509179615117754368', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131021', 'static', '1542939849472524294', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131022', 'static', '1511494161530535947', '1511494438434291716', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131023', 'static', '91283767764369409', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896057131024', 'static', '91681269747466245', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896069713927', 'static', '91685770244046853', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896069713928', 'static', '1531059437984989189', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896069713929', 'static', '1511495802770079746', '1511494438434291716', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896069713930', 'static', '93441238276685825', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896069713931', 'static', '91697876372733961', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491146', 'static', '91692030788026373', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491147', 'static', '1538994469231837188', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491148', 'static', '72188271301148676', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491149', 'static', '91698429899227137', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491150', 'static', '1509177915053096962', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896086491151', 'static', '1509184818118311946', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896103268358', 'static', '91690096047865861', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896912769029', 'static', '1509212249457868808', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896912769030', 'static', '1518555863791091716', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896929546249', 'static', '1539042480926408715', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896929546250', 'static', '91692305787568136', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896929546251', 'static', '91685236665663490', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896929546252', 'static', '91698614343745546', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896946323462', 'static', '93529533534879755', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896958906378', 'static', '91690561644969994', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896963100683', 'static', '91691322072285184', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896963100684', 'static', '91692604820471817', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896963100685', 'static', '1520374289564090377', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896963100686', 'static', '1539043101486268425', '1504618238889869317', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('96000896975683587', 'static', '91691541182726152', '1506005013902311434', '0', '1534253403467333638', '1', null, '1', '192.168.1.23', '2021-12-07 16:31:07', '1', '192.168.1.23', '2021-12-07 16:31:07', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319565893632', 'static', '91693753137020928', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319586865156', 'static', '91693516968345605', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319595253765', 'static', '91681269747466245', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319599448075', 'static', '72188271301148676', '1504618238889869317', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319603642370', 'static', '72187658739826691', '1504618238889869317', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319603642371', 'static', '91698614343745546', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319603642372', 'static', '1520466841923403786', '1504618238889869317', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319607836675', 'static', '1520374289564090377', '1504618238889869317', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319616225290', 'static', '1532485035156488196', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319624613894', 'static', '91659367716929536', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319628808200', 'static', '1509179615117754368', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319633002499', 'static', '91694018015707139', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319637196806', 'static', '1531059437984989189', '1504618238889869317', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319641391111', 'static', '1522661730694119427', '1504617964850823176', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319645585409', 'static', '93529533534879755', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319649779713', 'static', '91697876372733961', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319666556931', 'static', '91697439556943878', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319670751238', 'static', '91681543228669952', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319670751239', 'static', '91697684151975939', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319670751240', 'static', '1523270396090695683', '1504617964850823176', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319670751241', 'static', '1506128052832878593', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319679139845', 'static', '91698261745385474', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319708499971', 'static', '91694508451479553', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319712694281', 'static', '91683279729246214', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319716888583', 'static', '1506128956323708934', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319721082887', 'static', '91698429899227137', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319721082888', 'static', '1506127499163779081', '1504617964850823176', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319721082889', 'static', '91696827939340293', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319721082890', 'static', '91682380176867329', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319725277184', 'static', '91657329280991232', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('100851319729471491', 'static', '91694308215406602', '1506005013902311434', '0', '93037725495246854', '1', null, '1', '192.168.1.23', '2021-12-21 01:44:58', '1', '192.168.1.23', '2021-12-21 01:44:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('103212258273443844', 'static', '103211568641785859', '1504618238889869317', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2021-12-27 14:06:30', '1', '192.168.1.23', '2021-12-27 14:06:30', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495428845578', 'static', '1522997390516862983', '1504618238889869317', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495433039882', 'static', '1511494161530535947', '1511494438434291716', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495437234181', 'static', '1511736612090462209', '1511494438434291716', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495441428486', 'static', '1509179615117754368', '1506005013902311434', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495441428487', 'static', '1511495802770079746', '1511494438434291716', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495441428488', 'static', '1511496878801993736', '1511494438434291716', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110440495441428489', 'static', '100', '1506113043159498757', '0', '1533985924929208330', '1', null, '1', '192.168.1.23', '2022-01-16 12:48:56', '1', '192.168.1.23', '2022-01-16 12:48:56', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('110509205027995652', 'static', '110507156089520135', '1504618238889869317', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-01-16 17:21:58', '1', '192.168.1.23', '2022-01-16 17:21:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('127146036830060547', 'static', '127144910604910596', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-03-03 15:10:47', '1', '192.168.1.23', '2022-03-03 15:10:47', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('151362799058731014', 'static', '151359177717628933', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-05-09 10:59:33', '1', '192.168.1.23', '2022-05-09 10:59:33', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('152162688139182081', 'static', '152162057265528840', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-05-11 15:58:02', '1', '192.168.1.23', '2022-05-11 15:58:02', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('156912433340530690', 'static', '156908317952360451', '1504617964850823176', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-05-24 18:31:49', '1', '192.168.1.23', '2022-05-24 18:31:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('203337660995649541', 'static', '203336650390683654', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-09-29 21:09:06', '1', '192.168.1.23', '2022-09-29 21:09:06', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('220687628014108675', 'static', '220685278113349639', '1506005013902311434', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2022-11-16 18:11:41', '1', '192.168.1.23', '2022-11-16 18:11:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('246160012279988224', 'static', '129381329150853120', '1504618238889869317', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2023-01-26 01:09:50', '1', '192.168.1.23', '2023-01-26 01:09:50', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('246161015691722760', 'static', '246159055248867338', '1533901932104171527', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2023-01-26 01:13:50', '1', '192.168.1.23', '2023-01-26 01:13:50', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('246161015700111361', 'static', '246158602809294853', '1533901932104171527', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2023-01-26 01:13:50', '1', '192.168.1.23', '2023-01-26 01:13:50', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('246163374031421447', 'static', '246163063116054536', '1533901932104171527', '0', '1533544727530094592', '1', null, '1', '192.168.1.23', '2023-01-26 01:23:12', '1', '192.168.1.23', '2023-01-26 01:23:12', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506101733801771009', 'static', '1506101733801771011', '1506005013902311434', '0', '1532487189283913738', '1', '系统管理', '0', '127.0.0.1', '2021-05-19 01:10:35', '1', '192.168.1.23', '2021-08-18 14:29:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506101733801771010', 'static', '1506101733801771011', '1506005013902311434', '0', '1533544727530094592', '1', '系统管理', '0', '127.0.0.1', '2021-05-19 01:10:35', '1', '192.168.1.23', '2021-08-18 14:29:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506107866432061441', 'static', '1506107866432061443', '1506005013902311434', '0', '1532487189283913738', '1', 'SaaS平台上的独立应用', '0', '127.0.0.1', '2021-05-19 01:34:57', '1', '192.168.1.23', '2021-07-18 15:41:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506107866432061442', 'static', '1506107866432061443', '1506005013902311434', '0', '1533544727530094592', '1', 'SaaS平台上的独立应用', '0', '127.0.0.1', '2021-05-19 01:34:57', '1', '192.168.1.23', '2021-07-18 15:41:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506107866432061443', 'static', '1506107866432061443', '1506005013902311434', '0', '1', '1', 'SaaS平台上的独立应用', '0', '127.0.0.1', '2021-05-19 01:34:57', '1', '192.168.1.23', '2021-07-18 15:41:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506122443605590020', 'static', '1506122443605590022', '1506005013902311434', '0', '1532487189283913738', '1', '菜单、操作、服务、静态资源', '0', '127.0.0.1', '2021-05-19 02:32:53', '1', '192.168.1.23', '2021-07-18 15:42:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506122443605590021', 'static', '1506122443605590022', '1506005013902311434', '0', '1533544727530094592', '1', '菜单、操作、服务、静态资源', '0', '127.0.0.1', '2021-05-19 02:32:53', '1', '192.168.1.23', '2021-07-18 15:42:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506122443605590022', 'static', '1506122443605590022', '1506005013902311434', '0', '1', '1', '菜单、操作、服务、静态资源', '0', '127.0.0.1', '2021-05-19 02:32:53', '1', '192.168.1.23', '2021-07-18 15:42:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506125438066016265', 'static', '1506125438066016267', '1506005013902311434', '0', '1532487189283913738', '1', '业务场景细分的资源集', '0', '127.0.0.1', '2021-05-19 02:44:47', '1', '192.168.1.23', '2021-07-18 15:42:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506125438066016266', 'static', '1506125438066016267', '1506005013902311434', '0', '1533544727530094592', '1', '业务场景细分的资源集', '0', '127.0.0.1', '2021-05-19 02:44:47', '1', '192.168.1.23', '2021-07-18 15:42:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506125438066016267', 'static', '1506125438066016267', '1506005013902311434', '0', '1', '1', '业务场景细分的资源集', '0', '127.0.0.1', '2021-05-19 02:44:47', '1', '192.168.1.23', '2021-07-18 15:42:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126590341988352', 'static', '1506126590341988354', '1506005013902311434', '0', '1532487189283913738', '1', '逻辑删除一个应用', '0', '127.0.0.1', '2021-05-19 02:49:22', '0', '127.0.0.1', '2021-05-19 02:49:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126590341988353', 'static', '1506126590341988354', '1506005013902311434', '0', '1533544727530094592', '1', '逻辑删除一个应用', '0', '127.0.0.1', '2021-05-19 02:49:22', '0', '127.0.0.1', '2021-05-19 02:49:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126590341988354', 'static', '1506126590341988354', '1506005013902311434', '0', '1', '1', '逻辑删除一个应用', '0', '127.0.0.1', '2021-05-19 02:49:22', '0', '127.0.0.1', '2021-05-19 02:49:22', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126989799112703', 'static', '1506126989799112705', '1506005013902311434', '0', '1532487189283913738', '1', '新增一个应用', '0', '127.0.0.1', '2021-05-19 02:50:57', '0', '127.0.0.1', '2021-05-19 02:50:57', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126989799112704', 'static', '1506126989799112705', '1506005013902311434', '0', '1533544727530094592', '1', '新增一个应用', '0', '127.0.0.1', '2021-05-19 02:50:57', '0', '127.0.0.1', '2021-05-19 02:50:57', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506126989799112705', 'static', '1506126989799112705', '1506005013902311434', '0', '1', '1', '新增一个应用', '0', '127.0.0.1', '2021-05-19 02:50:57', '0', '127.0.0.1', '2021-05-19 02:50:57', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506127499163779079', 'static', '1506127499163779081', '1504617964850823176', '0', '1532487189283913738', '1', '供求信息发布', '0', '127.0.0.1', '2021-05-19 02:52:58', '1', '192.168.1.23', '2021-09-04 12:50:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506127499163779080', 'static', '1506127499163779081', '1504617964850823176', '0', '1533544727530094592', '1', '供求信息发布', '0', '127.0.0.1', '2021-05-19 02:52:58', '1', '192.168.1.23', '2021-09-04 12:50:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506127499163779081', 'static', '1506127499163779081', '1504617964850823176', '0', '1', '1', '供求信息发布', '0', '127.0.0.1', '2021-05-19 02:52:58', '1', '192.168.1.23', '2021-09-04 12:50:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128052832878591', 'static', '1506128052832878593', '1506005013902311434', '0', '1532487189283913738', '1', '内容管理系统', '0', '127.0.0.1', '2021-05-19 02:55:10', '1', '192.168.1.23', '2021-07-13 21:50:24', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128052832878592', 'static', '1506128052832878593', '1506005013902311434', '0', '1533544727530094592', '1', '内容管理系统', '0', '127.0.0.1', '2021-05-19 02:55:10', '1', '192.168.1.23', '2021-07-13 21:50:24', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128052832878593', 'static', '1506128052832878593', '1506005013902311434', '0', '1', '1', '内容管理系统', '0', '127.0.0.1', '2021-05-19 02:55:10', '1', '192.168.1.23', '2021-07-13 21:50:24', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128956323708932', 'static', '1506128956323708934', '1506005013902311434', '0', '1532487189283913738', '1', null, '0', '127.0.0.1', '2021-05-19 02:58:46', '1', '192.168.1.23', '2021-07-13 21:53:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128956323708933', 'static', '1506128956323708934', '1506005013902311434', '0', '1533544727530094592', '1', null, '0', '127.0.0.1', '2021-05-19 02:58:46', '1', '192.168.1.23', '2021-07-13 21:53:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1506128956323708934', 'static', '1506128956323708934', '1506005013902311434', '0', '1', '1', null, '0', '127.0.0.1', '2021-05-19 02:58:46', '1', '192.168.1.23', '2021-07-13 21:53:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509177915053096960', 'static', '1509177915053096962', '1506005013902311434', '0', '1532487189283913738', '1', '租户一般只独立的法人主体。', '0', '127.0.0.1', '2021-05-27 12:54:14', '1', '192.168.1.23', '2021-07-18 15:42:40', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509177915053096961', 'static', '1509177915053096962', '1506005013902311434', '0', '1533544727530094592', '1', '租户一般只独立的法人主体。', '0', '127.0.0.1', '2021-05-27 12:54:14', '1', '192.168.1.23', '2021-07-18 15:42:40', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509177915053096962', 'static', '1509177915053096962', '1506005013902311434', '0', '1', '1', '租户一般只独立的法人主体。', '0', '127.0.0.1', '2021-05-27 12:54:14', '1', '192.168.1.23', '2021-07-18 15:42:40', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509179615117754366', 'static', '1509179615117754368', '1506005013902311434', '0', '1532487189283913738', '1', '系统管理的入口菜单', '0', '127.0.0.1', '2021-05-27 13:00:59', '1', '192.168.1.23', '2021-09-23 11:05:44', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509179615117754367', 'static', '1509179615117754368', '1506005013902311434', '0', '1533544727530094592', '1', '系统管理的入口菜单', '0', '127.0.0.1', '2021-05-27 13:00:59', '1', '192.168.1.23', '2021-08-18 14:30:55', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509179615117754368', 'static', '1509179615117754368', '1506005013902311434', '0', '1', '1', '系统管理的入口菜单', '0', '127.0.0.1', '2021-05-27 13:00:59', '1', '192.168.1.23', '2021-08-18 14:30:55', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509184818118311944', 'static', '1509184818118311946', '1506005013902311434', '0', '1532487189283913738', '1', '定义体系结构的设置', '0', '127.0.0.1', '2021-05-27 13:21:40', '1', '192.168.1.23', '2021-07-18 15:42:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509184818118311945', 'static', '1509184818118311946', '1506005013902311434', '0', '1533544727530094592', '1', '定义体系结构的设置', '0', '127.0.0.1', '2021-05-27 13:21:40', '1', '192.168.1.23', '2021-07-18 15:42:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509184818118311946', 'static', '1509184818118311946', '1506005013902311434', '0', '1', '1', '定义体系结构的设置', '0', '127.0.0.1', '2021-05-27 13:21:40', '1', '192.168.1.23', '2021-07-18 15:42:49', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509211664167911430', 'static', '1509211664167911432', '1506005013902311434', '0', '1532487189283913738', '1', '组织管理', '0', '127.0.0.1', '2021-05-27 15:08:21', '1', '192.168.1.23', '2021-07-18 15:43:00', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509211664167911431', 'static', '1509211664167911432', '1506005013902311434', '0', '1533544727530094592', '1', '组织管理', '0', '127.0.0.1', '2021-05-27 15:08:21', '1', '192.168.1.23', '2021-07-18 15:43:00', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509211664167911432', 'static', '1509211664167911432', '1506005013902311434', '0', '1', '1', '组织管理', '0', '127.0.0.1', '2021-05-27 15:08:21', '1', '192.168.1.23', '2021-07-18 15:43:00', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509212249457868806', 'static', '1509212249457868808', '1506005013902311434', '0', '1532487189283913738', '1', '注册用户管理', '0', '127.0.0.1', '2021-05-27 15:10:40', '1', '192.168.1.23', '2021-07-18 15:43:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509212249457868807', 'static', '1509212249457868808', '1506005013902311434', '0', '1533544727530094592', '1', '注册用户管理', '0', '127.0.0.1', '2021-05-27 15:10:40', '1', '192.168.1.23', '2021-07-18 15:43:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1509212249457868808', 'static', '1509212249457868808', '1506005013902311434', '0', '1', '1', '注册用户管理', '0', '127.0.0.1', '2021-05-27 15:10:40', '1', '192.168.1.23', '2021-07-18 15:43:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511494161530535945', 'static', '1511494161530535947', '1511494438434291716', '0', '1532487189283913738', '1', '前端用户设置', '1', '127.0.0.1', '2021-06-02 22:18:10', '1', '127.0.0.1', '2021-06-02 22:21:39', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511494161530535946', 'static', '1511494161530535947', '1511494438434291716', '0', '1533544727530094592', '1', '前端用户设置', '1', '127.0.0.1', '2021-06-02 22:18:10', '1', '127.0.0.1', '2021-06-02 22:21:39', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511494161530535947', 'static', '1511494161530535947', '1511494438434291716', '0', '1', '1', '前端用户设置', '1', '127.0.0.1', '2021-06-02 22:18:10', '1', '127.0.0.1', '2021-06-02 22:21:39', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511495802770079744', 'static', '1511495802770079746', '1511494438434291716', '0', '1532487189283913738', '1', null, '1', '127.0.0.1', '2021-06-02 22:24:42', '1', '127.0.0.1', '2021-06-02 22:27:53', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511495802770079745', 'static', '1511495802770079746', '1511494438434291716', '0', '1533544727530094592', '1', null, '1', '127.0.0.1', '2021-06-02 22:24:42', '1', '127.0.0.1', '2021-06-02 22:27:53', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511495802770079746', 'static', '1511495802770079746', '1511494438434291716', '0', '1', '1', null, '1', '127.0.0.1', '2021-06-02 22:24:42', '1', '127.0.0.1', '2021-06-02 22:27:53', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511496878801993734', 'static', '1511496878801993736', '1511494438434291716', '0', '1532487189283913738', '1', null, '1', '127.0.0.1', '2021-06-02 22:28:58', '1', '127.0.0.1', '2021-06-02 22:28:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511496878801993735', 'static', '1511496878801993736', '1511494438434291716', '0', '1533544727530094592', '1', null, '1', '127.0.0.1', '2021-06-02 22:28:58', '1', '127.0.0.1', '2021-06-02 22:28:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511496878801993736', 'static', '1511496878801993736', '1511494438434291716', '0', '1', '1', null, '1', '127.0.0.1', '2021-06-02 22:28:58', '1', '127.0.0.1', '2021-06-02 22:28:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511736612090462207', 'static', '1511736612090462209', '1511494438434291716', '0', '1532487189283913738', '1', '个人基本信息查询', '1', '127.0.0.1', '2021-06-03 14:21:35', '1', '127.0.0.1', '2021-06-03 14:21:35', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511736612090462208', 'static', '1511736612090462209', '1511494438434291716', '0', '1533544727530094592', '1', '个人基本信息查询', '1', '127.0.0.1', '2021-06-03 14:21:35', '1', '127.0.0.1', '2021-06-03 14:21:35', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1511736612090462209', 'static', '1511736612090462209', '1511494438434291716', '0', '1', '1', '个人基本信息查询', '1', '127.0.0.1', '2021-06-03 14:21:35', '1', '127.0.0.1', '2021-06-03 14:21:35', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1518555863791091714', 'static', '1518555863791091716', '1506005013902311434', '0', '1532487189283913738', '1', '游客访问时，跳转到登陆', '1', '127.0.0.1', '2021-06-22 09:58:51', '1', '127.0.0.1', '2021-06-22 09:58:51', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1518555863791091715', 'static', '1518555863791091716', '1506005013902311434', '0', '1533544727530094592', '1', '游客访问时，跳转到登陆', '1', '127.0.0.1', '2021-06-22 09:58:51', '1', '127.0.0.1', '2021-06-22 09:58:51', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1518555863791091716', 'static', '1518555863791091716', '1506005013902311434', '0', '1', '1', '游客访问时，跳转到登陆', '1', '127.0.0.1', '2021-06-22 09:58:51', '1', '127.0.0.1', '2021-06-22 09:58:51', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1520374289564090375', 'static', '1520374289564090377', '1506005013902311434', '0', '1532487189283913738', '1', '全内容管理平台分类管理', '1', '127.0.0.1', '2021-06-27 10:24:38', '1', '192.168.1.23', '2021-07-13 21:50:43', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1520374289564090377', 'static', '1520374289564090377', '1506005013902311434', '0', '1', '1', '全内容管理平台分类管理', '1', '127.0.0.1', '2021-06-27 10:24:38', '1', '192.168.1.23', '2021-07-13 21:50:43', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1520466841923403784', 'static', '1520466841923403786', '1504618238889869317', '0', '1532487189283913738', '1', '标签管理', '1', '127.0.0.1', '2021-06-27 16:32:24', '1', '192.168.1.23', '2021-07-18 00:10:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1520466841923403785', 'static', '1520466841923403786', '1504618238889869317', '0', '1533544727530094592', '1', '标签管理', '1', '127.0.0.1', '2021-06-27 16:32:24', '1', '192.168.1.23', '2021-07-18 00:10:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1520466841923403786', 'static', '1520466841923403786', '1504618238889869317', '0', '1', '1', '标签管理', '1', '127.0.0.1', '2021-06-27 16:32:24', '1', '192.168.1.23', '2021-07-18 00:10:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522661730694119425', 'static', '1522661730694119427', '1504617964850823176', '0', '1532487189283913738', '1', '信息发布时上传封面图片等', '1', '192.168.1.23', '2021-07-03 17:54:06', '1', '192.168.1.23', '2021-09-04 12:51:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522661730694119426', 'static', '1522661730694119427', '1504617964850823176', '0', '1533544727530094592', '1', '信息发布时上传封面图片等', '1', '192.168.1.23', '2021-07-03 17:54:06', '1', '192.168.1.23', '2021-09-04 12:51:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522661730694119427', 'static', '1522661730694119427', '1504617964850823176', '0', '1', '1', '信息发布时上传封面图片等', '1', '192.168.1.23', '2021-07-03 17:54:06', '1', '192.168.1.23', '2021-09-04 12:51:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522997390516862981', 'static', '1522997390516862983', '1504618238889869317', '0', '1532487189283913738', '1', '内容创作', '1', '192.168.1.23', '2021-07-04 16:07:54', '1', '192.168.1.23', '2021-09-23 11:05:58', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522997390516862982', 'static', '1522997390516862983', '1504618238889869317', '0', '1533544727530094592', '1', '内容创作', '1', '192.168.1.23', '2021-07-04 16:07:54', '1', '192.168.1.23', '2021-07-04 16:11:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1522997390516862983', 'static', '1522997390516862983', '1504618238889869317', '0', '1', '1', '内容创作', '1', '192.168.1.23', '2021-07-04 16:07:54', '1', '192.168.1.23', '2021-07-04 16:11:41', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1523270396090695681', 'static', '1523270396090695683', '1504617964850823176', '0', '1532487189283913738', '1', '信息发布第一步', '1', '192.168.1.23', '2021-07-05 10:12:43', '1', '192.168.1.23', '2021-09-04 13:03:42', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1523270396090695682', 'static', '1523270396090695683', '1504617964850823176', '0', '1533544727530094592', '1', '信息发布第一步', '1', '192.168.1.23', '2021-07-05 10:12:43', '1', '192.168.1.23', '2021-09-04 13:03:42', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1523270396090695683', 'static', '1523270396090695683', '1504617964850823176', '0', '1', '1', '信息发布第一步', '1', '192.168.1.23', '2021-07-05 10:12:43', '1', '192.168.1.23', '2021-09-04 13:03:42', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1528063986125946883', 'static', '1528063986125946885', '1506005013902311434', '0', '1532487189283913738', '1', '系统配置项', '1', '192.168.1.23', '2021-07-18 15:40:44', '1', '192.168.1.23', '2021-07-18 15:41:03', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1528063986125946884', 'static', '1528063986125946885', '1506005013902311434', '0', '1533544727530094592', '1', '系统配置项', '1', '192.168.1.23', '2021-07-18 15:40:44', '1', '192.168.1.23', '2021-07-18 15:41:03', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1528063986125946885', 'static', '1528063986125946885', '1506005013902311434', '0', '1', '1', '系统配置项', '1', '192.168.1.23', '2021-07-18 15:40:44', '1', '192.168.1.23', '2021-07-18 15:41:03', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1531059437984989187', 'static', '1531059437984989189', '1504618238889869317', '0', '1532487189283913738', '1', '对文章或作品发表评论', '1', '192.168.1.23', '2021-07-26 22:03:36', '0', '192.168.1.23', '2021-07-26 22:04:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1531059437984989188', 'static', '1531059437984989189', '1504618238889869317', '0', '1533544727530094592', '1', '对文章或作品发表评论', '1', '192.168.1.23', '2021-07-26 22:03:36', '0', '192.168.1.23', '2021-07-26 22:04:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1531059437984989189', 'static', '1531059437984989189', '1504618238889869317', '0', '1', '1', '对文章或作品发表评论', '1', '192.168.1.23', '2021-07-26 22:03:36', '0', '192.168.1.23', '2021-07-26 22:04:52', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1532485035156488194', 'static', '1532485035156488196', '1506005013902311434', '0', '1532487189283913738', '1', '新增站点，创建独立的域。', '1', '192.168.1.23', '2021-07-30 20:28:25', '1', '192.168.1.23', '2021-08-03 23:45:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1532485035156488195', 'static', '1532485035156488196', '1506005013902311434', '0', '1533544727530094592', '1', '新增站点，创建独立的域。', '1', '192.168.1.23', '2021-07-30 20:28:25', '1', '192.168.1.23', '2021-08-03 23:45:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1532485035156488196', 'static', '1532485035156488196', '1506005013902311434', '0', '1', '1', '新增站点，创建独立的域。', '1', '192.168.1.23', '2021-07-30 20:28:25', '1', '192.168.1.23', '2021-08-03 23:45:09', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533544727530094592', 'archives', '100', '1506113043159498757', '1', '1', '1', 'wldos平台首页门户组件映射', '1', '127.0.0.1', '2021-09-07 17:44:24', '1', '127.0.0.1', '2021-09-07 17:44:40', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533941630155538432', 'static', '1533941630155538434', '1533901932104171527', '0', '1532487189283913738', '0', '项目介绍', '1', '192.168.1.23', '2021-08-03 20:56:24', '1', '192.168.1.23', '2021-08-03 21:21:45', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533941630155538433', 'static', '1533941630155538434', '1533901932104171527', '0', '1533544727530094592', '1', '项目介绍', '1', '192.168.1.23', '2021-08-03 20:56:24', '1', '192.168.1.23', '2021-08-03 21:21:45', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533941630155538434', 'static', '1533941630155538434', '1533901932104171527', '0', '1', '1', '项目介绍', '1', '192.168.1.23', '2021-08-03 20:56:24', '1', '192.168.1.23', '2021-08-03 21:21:45', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533950643823886343', 'static', '1533950643823886345', '1533901932104171527', '0', '1532487189283913738', '0', 'wldos中台javadoc', '1', '192.168.1.23', '2021-08-03 21:32:13', '1', '192.168.1.23', '2021-08-03 21:32:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533950643823886344', 'static', '1533950643823886345', '1533901932104171527', '0', '1533544727530094592', '1', 'wldos中台javadoc', '1', '192.168.1.23', '2021-08-03 21:32:13', '1', '192.168.1.23', '2021-08-03 21:32:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1533950643823886345', 'static', '1533950643823886345', '1533901932104171527', '0', '1', '1', 'wldos中台javadoc', '1', '192.168.1.23', '2021-08-03 21:32:13', '1', '192.168.1.23', '2021-08-03 21:32:13', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1538994469231837186', 'static', '1538994469231837188', '1504618238889869317', '0', '1532487189283913738', '1', '人物年谱存档', '1', '192.168.1.23', '2021-08-17 19:34:35', '1', '192.168.1.23', '2021-08-28 16:13:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1538994469231837187', 'static', '1538994469231837188', '1504618238889869317', '1520479861269512197', '1533544727530094592', '1', '人物年谱存档', '1', '192.168.1.23', '2021-08-17 19:34:35', '1', '192.168.1.23', '2021-08-28 16:13:01', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539042480926408713', 'static', '1539042480926408715', '1504618238889869317', '0', '1532487189283913738', '1', '历史年表大事记', '1', '192.168.1.23', '2021-08-17 22:45:21', '1', '192.168.1.23', '2021-08-28 16:13:44', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539042480926408714', 'static', '1539042480926408715', '1504618238889869317', '0', '1533544727530094592', '1', '历史年表大事记', '1', '192.168.1.23', '2021-08-17 22:45:21', '1', '192.168.1.23', '2021-08-28 16:13:44', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539043101486268423', 'static', '1539043101486268425', '1504618238889869317', '0', '1532487189283913738', '1', '行业发展史', '1', '192.168.1.23', '2021-08-17 22:47:49', '1', '192.168.1.23', '2021-08-28 16:13:23', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539043101486268424', 'static', '1539043101486268425', '1504618238889869317', '0', '1533544727530094592', '1', '行业发展史', '1', '192.168.1.23', '2021-08-17 22:47:49', '1', '192.168.1.23', '2021-08-28 16:13:23', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539043349348663303', 'static', '1539043349348663305', '1504618238889869317', '0', '1532487189283913738', '1', '学术研究史', '1', '192.168.1.23', '2021-08-17 22:48:49', '1', '192.168.1.23', '2021-08-28 16:13:34', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1539043349348663304', 'static', '1539043349348663305', '1504618238889869317', '0', '1533544727530094592', '1', '学术研究史', '1', '192.168.1.23', '2021-08-17 22:48:49', '1', '192.168.1.23', '2021-08-28 16:13:34', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1542170368806666242', 'static', '1542170368806666244', '1504618238889869317', '0', '1532487189283913738', '1', '内容领域：文章类', '1', '192.168.1.23', '2021-08-26 13:54:28', '1', '192.168.1.23', '2021-09-15 12:26:54', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1542170368806666243', 'static', '1542170368806666244', '1504618238889869317', '0', '1533544727530094592', '1', '内容领域：文章类', '1', '192.168.1.23', '2021-08-26 13:54:28', '1', '192.168.1.23', '2021-09-07 16:04:10', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1542170368806666244', 'static', '1542170368806666244', '1504618238889869317', '0', '1', '1', '内容领域：文章类', '1', '192.168.1.23', '2021-08-26 13:54:28', '1', '192.168.1.23', '2021-09-07 16:04:10', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1542939849472524292', 'static', '1542939849472524294', '1504618238889869317', '0', '1532487189283913738', '1', '技术分享', '1', '192.168.1.23', '2021-08-28 16:52:07', '1', '192.168.1.23', '2021-09-16 09:15:20', 'normal', '0');
INSERT INTO `wo_domain_resource` VALUES ('1544340805720391680', 'static', '1544340805720391680', '1504618238889869317', '0', '1', '1', null, '1', '192.168.1.23', '2021-09-01 13:39:01', '1', '192.168.1.23', '2021-09-01 13:39:01', 'normal', '0');

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
-- Records of wo_file
-- ----------------------------
INSERT INTO `wo_file` VALUES ('1', 'logo-wldos.png', '/logo-wldos.png', 'image/png', '1', '1', '2021-09-06 13:49:41', '192.168.1.23', '1', '2021-09-06 13:49:57', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('69275403593367560', '1.jpg', '/202109/24223333bxrm8BYD.jpg', 'image/jpeg', '1', '1', '2021-09-24 22:33:33', '192.168.1.23', '1', '2021-09-24 22:33:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('69282084796678149', '1.jpg', '/202109/242300068Xd7dLrE.jpg', 'image/jpeg', '1', '1', '2021-09-24 23:00:06', '192.168.1.23', '1', '2021-09-24 23:00:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('69921277579280390', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202109/26172001IFAhPgzd.jpg', 'image/jpeg', '1', '1', '2021-09-26 17:20:02', '192.168.1.23', '1', '2021-09-26 17:20:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('71090477396049931', 'mobile.png', '/202109/292246000FlN8EhQ.png', 'image/png', '1', '1', '2021-09-29 22:46:01', '192.168.1.23', '1', '2021-09-29 22:46:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('71090507360157697', 'mobileatt.png', '/202109/29224607rb6ytSDr.png', 'image/png', '1', '1', '2021-09-29 22:46:08', '192.168.1.23', '1', '2021-09-29 22:46:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('71090529413808129', 'mobilefoot.png', '/202109/29224613p5zzr2iT.png', 'image/png', '1', '1', '2021-09-29 22:46:13', '192.168.1.23', '1', '2021-09-29 22:46:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('71090564406886404', 'mobilemenu.png', '/202109/29224621yvp9Styu.png', 'image/png', '1', '1', '2021-09-29 22:46:21', '192.168.1.23', '1', '2021-09-29 22:46:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('71090590881333253', 'pad.png', '/202109/29224627I0BIXIAG.png', 'image/png', '1', '1', '2021-09-29 22:46:28', '192.168.1.23', '1', '2021-09-29 22:46:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('78655136265322507', 'QQ图片20211016214239.png', '/202110/201945151nGiy6hd.png', 'image/png', '1', '100', '2021-10-20 19:45:16', '192.168.1.23', '100', '2021-10-20 19:45:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('79071047992393730', 'QQ图片20211016214239.png', '/202110/21231756DhqmsZdL.png', 'image/png', '1', '79070766156136451', '2021-10-21 23:17:57', '192.168.1.23', '79070766156136451', '2021-10-21 23:17:57', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80366722549989383', 'qsh.jpg', '/202110/25130629i6WlfTwV.jpg', 'image/jpeg', '1', '1', '2021-10-25 13:06:30', '192.168.1.23', '1', '2021-10-25 13:06:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80366848173588483', 'qsh.jpg', '/202110/25130659KG4VdUoA.jpg', 'image/jpeg', '1', '1', '2021-10-25 13:07:00', '192.168.1.23', '1', '2021-10-25 13:07:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80366896038985738', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202110/25130711HZSjhRO3.jpg', 'image/jpeg', '1', '1', '2021-10-25 13:07:11', '192.168.1.23', '1', '2021-10-25 13:07:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80366936690180107', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202110/25130720qp8tNiQu.jpg', 'image/jpeg', '1', '1', '2021-10-25 13:07:21', '192.168.1.23', '1', '2021-10-25 13:07:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80366965773484043', '111.jpg', '/202110/251307273aNlFcDa.jpg', 'image/jpeg', '1', '1', '2021-10-25 13:07:28', '192.168.1.23', '1', '2021-10-25 13:07:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430201877217286', 'c1.jpg', '/202110/25171844EOpmiaNP.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:18:44', '192.168.1.23', '1', '2021-10-25 17:18:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430261683798021', 'c2.jpg', '/202110/25171858yUDp6QA9.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:18:59', '192.168.1.23', '1', '2021-10-25 17:18:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430413987364870', 'c3.jpg', '/202110/25171935ScCgDcXS.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:19:35', '192.168.1.23', '1', '2021-10-25 17:19:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430473076719618', 'c1.jpg', '/202110/25171949HUtz8UID.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:19:49', '192.168.1.23', '1', '2021-10-25 17:19:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430556350431239', 'c5.jpg', '/202110/25172008hwATvmfS.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:20:09', '192.168.1.23', '1', '2021-10-25 17:20:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430617750847491', 'c6.jpg', '/202110/25172023cbDnPpun.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:20:24', '192.168.1.23', '1', '2021-10-25 17:20:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430689855127562', 'c4.jpg', '/202110/25172040DNTtRRtO.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:20:41', '192.168.1.23', '1', '2021-10-25 17:20:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80430808520376331', 'c8.jpg', '/202110/25172109yqZazLKU.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:21:09', '192.168.1.23', '1', '2021-10-25 17:21:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80437151763447817', '微信图片_20211025174441.jpg', '/202110/25174621GRmlQxIE.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:46:21', '192.168.1.23', '1', '2021-10-25 17:46:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('80438538920443907', '微信图片_20211025175036.jpg', '/202110/251751525MfJ4zxt.jpg', 'image/jpeg', '1', '1', '2021-10-25 17:51:52', '192.168.1.23', '1', '2021-10-25 17:51:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81100927688949769', 'c6.jpg', '/202110/27134357k2rwtVXA.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:43:58', '192.168.1.23', '1', '2021-10-27 13:43:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81100978058346499', 'c8.jpg', '/202110/27134409OwZqEzrc.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:44:10', '192.168.1.23', '1', '2021-10-27 13:44:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81101401863405572', 'c4.jpg', '/202110/27134550Plbvde27.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:45:51', '192.168.1.23', '1', '2021-10-27 13:45:51', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81101465327419392', 'c7.jpg', '/202110/27134606QuterOVQ.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:46:06', '192.168.1.23', '1', '2021-10-27 13:46:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81101757632659456', 'c2.jpg', '/202110/27134715v8akXSVb.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:47:16', '192.168.1.23', '1', '2021-10-27 13:47:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81102273603354635', 'c3.jpg', '/202110/27134918ZcVwhVy1.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:49:19', '192.168.1.23', '1', '2021-10-27 13:49:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81102532014424069', 'c5.jpg', '/202110/27135020yEJUB93t.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:50:20', '192.168.1.23', '1', '2021-10-27 13:50:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81104828018376713', 'cc.jpg', '/202110/27135927zxoQTnQC.jpg', 'image/jpeg', '1', '1', '2021-10-27 13:59:28', '192.168.1.23', '1', '2021-10-27 13:59:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81106450425495558', 'c7.jpg', '/202110/27140554nicEG7eZ.jpg', 'image/jpeg', '1', '1', '2021-10-27 14:05:55', '192.168.1.23', '1', '2021-10-27 14:05:55', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81118503928250379', '微信图片_20211027144334.jpg', '/202110/27145348Va7wEuwD.jpg', 'image/jpeg', '1', '1', '2021-10-27 14:53:48', '192.168.1.23', '1', '2021-10-27 14:53:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('81759217854889986', 'mceu_74837837711635470386380.png', '/202110/29091946mtf7GN9G.png', 'image/png', '1', '1', '2021-10-29 09:19:47', '192.168.1.23', '1', '2021-10-29 09:19:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83294769397809157', 'mceu_70133940611635836490299.png', '/202111/02150130L0dJbfGn.png', 'image/png', '1', '1', '2021-11-02 15:01:31', '192.168.1.23', '1', '2021-11-02 15:01:31', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83320509518495747', 'c1.jpg', '/202111/02164347HziO70hS.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:43:47', '192.168.1.23', '1', '2021-11-02 16:43:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83320553667739654', 'c8.jpg', '/202111/02164358zkG983cb.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:43:58', '192.168.1.23', '1', '2021-11-02 16:43:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83320599415013378', 'cc.jpg', '/202111/02164408JOLxyjYh.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:44:09', '192.168.1.23', '1', '2021-11-02 16:44:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83320890315161604', 'c4.jpg', '/202111/02164518fkIzz7Ez.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:45:18', '192.168.1.23', '1', '2021-11-02 16:45:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83320917171290114', 'c6.jpg', '/202111/021645243ieC908Z.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:45:25', '192.168.1.23', '1', '2021-11-02 16:45:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83323528221999113', 'mceu_83205252511635843346794.jpg', '/202111/02165547DRqRlzdc.jpg', 'image/jpeg', '1', '1', '2021-11-02 16:55:47', '192.168.1.23', '1', '2021-11-02 16:55:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('83324906407378949', '12131413BvKthFFT.mp4', '/202111/02170115S8GPgnaS.mp4', 'video/mp4', '1', '1', '2021-11-02 17:01:16', '192.168.1.23', '1', '2021-11-02 17:01:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('93807878382206979', 'foot.png', '/202112/01151651PuN46CWJ.png', 'image/png', '1', '100', '2021-12-01 15:16:51', '192.168.1.23', '100', '2021-12-01 15:16:51', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('93807919394111491', 'home.png', '/202112/011517008FmvzqJO.png', 'image/png', '1', '100', '2021-12-01 15:17:01', '192.168.1.23', '100', '2021-12-01 15:17:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('93807963908259846', 'prod1.png', '/202112/01151711DnHJ6Voj.png', 'image/png', '1', '100', '2021-12-01 15:17:11', '192.168.1.23', '100', '2021-12-01 15:17:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94428297539797001', 'home.png', '/202112/0308221009Ydmmho.png', 'image/png', '1', '92829405966680072', '2021-12-03 08:22:11', '192.168.1.23', '92829405966680072', '2021-12-03 08:22:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94428335456305159', 'post-foot.png', '/202112/03082219aEU9SAlh.png', 'image/png', '1', '92829405966680072', '2021-12-03 08:22:20', '192.168.1.23', '92829405966680072', '2021-12-03 08:22:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94428367395930113', 'login.png', '/202112/03082227oAu75trM.png', 'image/png', '1', '92829405966680072', '2021-12-03 08:22:27', '192.168.1.23', '92829405966680072', '2021-12-03 08:22:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94428413331947525', 'prod1.png', '/202112/03082238BEDiwSQY.png', 'image/png', '1', '92829405966680072', '2021-12-03 08:22:38', '192.168.1.23', '92829405966680072', '2021-12-03 08:22:38', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94428450581561352', 'res.png', '/202112/03082247G20hDk21.png', 'image/png', '1', '92829405966680072', '2021-12-03 08:22:47', '192.168.1.23', '92829405966680072', '2021-12-03 08:22:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94454702273118211', 'arch.png', '/202112/03100705TAlsaDX7.png', 'image/png', '1', '92829405966680072', '2021-12-03 10:07:06', '192.168.1.23', '92829405966680072', '2021-12-03 10:07:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94454773089746950', 'orga.png', '/202112/03100722Q8lphply.png', 'image/png', '1', '92829405966680072', '2021-12-03 10:07:23', '192.168.1.23', '92829405966680072', '2021-12-03 10:07:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94820355567960065', 'blog.png', '/202112/041020043IGjKqKj.png', 'image/png', '1', '100', '2021-12-04 10:20:04', '192.168.1.23', '100', '2021-12-04 10:20:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94823510452125699', 'orga.png', '/202112/04103236w1w4dzzS.png', 'image/png', '1', '100', '2021-12-04 10:32:37', '192.168.1.23', '100', '2021-12-04 10:32:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94847160576294922', 'home.png', '/202112/04120635BRG7m5Jt.png', 'image/png', '1', '100', '2021-12-04 12:06:35', '192.168.1.23', '100', '2021-12-04 12:06:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94847202871656448', 'prod1.png', '/202112/041206457oZfpxsN.png', 'image/png', '1', '100', '2021-12-04 12:06:45', '192.168.1.23', '100', '2021-12-04 12:06:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94847612395110400', 'arch.png', '/202112/04120822CFfgqbZ1.png', 'image/png', '1', '100', '2021-12-04 12:08:23', '192.168.1.23', '100', '2021-12-04 12:08:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('94847650961735689', 'prod1.png', '/202112/04120832krHPq5cc.png', 'image/png', '1', '100', '2021-12-04 12:08:32', '192.168.1.23', '100', '2021-12-04 12:08:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('95634438697435141', 'mceu_5029113811638778496704.png', '/202112/06161456g3YiQTKp.png', 'image/png', '1', '100', '2021-12-06 16:14:57', '192.168.1.23', '100', '2021-12-06 16:14:57', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('95634668855672837', 'mceu_1791288121638778551392.png', '/202112/06161551XtgywxDj.png', 'image/png', '1', '100', '2021-12-06 16:15:52', '192.168.1.23', '100', '2021-12-06 16:15:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('95635415601168386', 'fengjing.mp4', '/202112/06161849bZwWrTht.mp4', 'video/mp4', '1', '100', '2021-12-06 16:18:50', '192.168.1.23', '100', '2021-12-06 16:18:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('96473744571809792', 'space.png', '/202112/08235003ykvABD03.png', 'image/png', '1', '100', '2021-12-08 23:50:03', '192.168.1.23', '100', '2021-12-08 23:50:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('96473772736561157', 'login.png', '/202112/08235009RJTY9EIP.png', 'image/png', '1', '100', '2021-12-08 23:50:10', '192.168.1.23', '100', '2021-12-08 23:50:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98243005732536322', 'QQ图片20211119090909.png', '/202112/13210027zTPYkXAw.png', 'image/png', '1', '100', '2021-12-13 21:00:28', '192.168.1.23', '100', '2021-12-13 21:00:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98243069855055873', '微信图片_20211106142618.jpg', '/202112/13210043GvgBWKer.jpg', 'image/jpeg', '1', '100', '2021-12-13 21:00:43', '192.168.1.23', '100', '2021-12-13 21:00:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98246910482759688', '微信图片_20211106142618.jpg', '/202112/13211558Fd64KeG3.jpg', 'image/jpeg', '1', '100', '2021-12-13 21:15:59', '192.168.1.23', '100', '2021-12-13 21:15:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98251784381448195', 'QQ图片20211119090909.png', '/202112/13213520FsMLXz6r.png', 'image/png', '1', '100', '2021-12-13 21:35:21', '192.168.1.23', '100', '2021-12-13 21:35:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98253001736241156', 'wldos.png', '/202112/13214011xlU4NqAT.png', 'image/png', '1', '100', '2021-12-13 21:40:11', '192.168.1.23', '100', '2021-12-13 21:40:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98253043461177351', 'rt4qft8f4oe.jpg', '/202112/132140211CW50jcU.jpg', 'image/jpeg', '1', '100', '2021-12-13 21:40:21', '192.168.1.23', '100', '2021-12-13 21:40:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98253078777217025', '微信图片_20211106142618.jpg', '/202112/13214029HaK8STXf.jpg', 'image/jpeg', '1', '100', '2021-12-13 21:40:29', '192.168.1.23', '100', '2021-12-13 21:40:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98253108875542528', 'wldos.png', '/202112/13214036c0RRKTUP.png', 'image/png', '1', '100', '2021-12-13 21:40:37', '192.168.1.23', '100', '2021-12-13 21:40:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98253152534052875', 'rt4qft8f4oe.jpg', '/202112/1321404770rgbyi6.jpg', 'image/jpeg', '1', '100', '2021-12-13 21:40:47', '192.168.1.23', '100', '2021-12-13 21:40:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98254698080550918', 'QQ图片20211119090909.png', '/202112/132146557sdVpsXG.png', 'image/png', '1', '100', '2021-12-13 21:46:56', '192.168.1.23', '100', '2021-12-13 21:46:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98254728535392259', 'wldos.png', '/202112/13214702c0sR8bOU.png', 'image/png', '1', '100', '2021-12-13 21:47:03', '192.168.1.23', '100', '2021-12-13 21:47:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98260848125591552', 'QQ图片20211113135245.png', '/202112/13221121bdSTBXT3.png', 'image/png', '1', '100', '2021-12-13 22:11:22', '192.168.1.23', '100', '2021-12-13 22:11:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98260877292781573', '注册商标用户失败.png', '/202112/132211288imCXfr3.png', 'image/png', '1', '100', '2021-12-13 22:11:29', '192.168.1.23', '100', '2021-12-13 22:11:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98260909752500232', 'rt4qft8f4oe.jpg', '/202112/13221136296PuOQ8.jpg', 'image/jpeg', '1', '100', '2021-12-13 22:11:36', '192.168.1.23', '100', '2021-12-13 22:11:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('98536678068240390', 'mceu_62214670311639470444321.png', '/202112/14162724uSLO9LW5.png', 'image/png', '1', '1', '2021-12-14 16:27:25', '192.168.1.23', '1', '2021-12-14 16:27:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100678750732730376', 'zltdcode.jpg', '/202112/20141914LlWNQa0k.jpg', 'image/jpeg', '1', '1', '2021-12-20 14:19:15', '192.168.1.23', '1', '2021-12-20 14:19:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100687779848699910', '1.jpg', '/202112/20145507EOlEEULS.jpg', 'image/jpeg', '1', '1', '2021-12-20 14:55:07', '192.168.1.23', '1', '2021-12-20 14:55:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100694570414293002', '1.jpg', '/202112/201522066QRjzJUJ.jpg', 'image/jpeg', '1', '1', '2021-12-20 15:22:06', '192.168.1.23', '1', '2021-12-20 15:22:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100710961305534473', '1.jpg', '/202112/20162714SdE4as6I.jpg', 'image/jpeg', '1', '1', '2021-12-20 16:27:14', '192.168.1.23', '1', '2021-12-20 16:27:14', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100710989738721281', '1.jpg', '/202112/20162721yajjb2Ru.jpg', 'image/jpeg', '1', '1', '2021-12-20 16:27:21', '192.168.1.23', '1', '2021-12-20 16:27:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100712926529896455', '1.jpg', '/202112/20163502ahSswdge.jpg', 'image/jpeg', '1', '1', '2021-12-20 16:35:03', '192.168.1.23', '1', '2021-12-20 16:35:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100712964219912192', '111.jpg', '/202112/20163511EFXCjUf4.jpg', 'image/jpeg', '1', '1', '2021-12-20 16:35:12', '192.168.1.23', '1', '2021-12-20 16:35:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100781208796577797', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202112/2021062246UNMthr.jpg', 'image/jpeg', '1', '1', '2021-12-20 21:06:23', '192.168.1.23', '1', '2021-12-20 21:06:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100781237456257025', '330625-140G502062028.jpg', '/202112/20210629kCB9ptbI.jpg', 'image/jpeg', '1', '1', '2021-12-20 21:06:29', '192.168.1.23', '1', '2021-12-20 21:06:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100784789285158922', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202112/20212036CqHqprtQ.jpg', 'image/jpeg', '1', '1', '2021-12-20 21:20:36', '192.168.1.23', '1', '2021-12-20 21:20:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100784823149969411', '111.jpg', '/202112/20212044MnyiR1Iy.jpg', 'image/jpeg', '1', '1', '2021-12-20 21:20:44', '192.168.1.23', '1', '2021-12-20 21:20:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100824107613601803', '1.jpg', '/202112/202356507aFrPlOJ.jpg', 'image/jpeg', '1', '1', '2021-12-20 23:56:50', '192.168.1.23', '1', '2021-12-20 23:56:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100824146536742919', '32.jpg', '/202112/202356593zC8ms91.jpg', 'image/jpeg', '1', '1', '2021-12-20 23:57:00', '192.168.1.23', '1', '2021-12-20 23:57:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100825750648307712', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202112/21000322l5nC88dT.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:03:22', '192.168.1.23', '1', '2021-12-21 00:03:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100825826032533508', '32.jpg', '/202112/21000340Tuq44WtG.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:03:40', '192.168.1.23', '1', '2021-12-21 00:03:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100825865467379722', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202112/210003494yByGP73.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:03:50', '192.168.1.23', '1', '2021-12-21 00:03:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100825917967482884', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202112/210004028Jy8YQiO.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:04:02', '192.168.1.23', '1', '2021-12-21 00:04:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100826414132674565', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202112/21000600dCvzPnak.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:06:00', '192.168.1.23', '1', '2021-12-21 00:06:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100828935051984901', '43a7d933c895d1438d0b16fc77f082025baf07eb.jpg', '/202112/21001601QjwvXrZl.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:16:01', '192.168.1.23', '1', '2021-12-21 00:16:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100828974474248192', '1111.jpg', '/202112/21001610FWO9ij9q.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:16:11', '192.168.1.23', '1', '2021-12-21 00:16:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100837428307869706', '32.jpg', '/202112/21004946HqYAjWYM.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:49:46', '192.168.1.23', '1', '2021-12-21 00:49:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('100837461140881408', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202112/210049548a2GC7DV.jpg', 'image/jpeg', '1', '1', '2021-12-21 00:49:54', '192.168.1.23', '1', '2021-12-21 00:49:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('102084365514358784', 'imagetools3.png', '/202112/24112439wk5s5bFe.png', 'image/png', '1', '100', '2021-12-24 11:24:39', '192.168.1.23', '100', '2021-12-24 11:24:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('104347304992620555', '4.jpg', '/202112/30171645f7L1wYZn.jpg', 'image/jpeg', '1', '1', '2021-12-30 17:16:46', '192.168.1.23', '1', '2021-12-30 17:16:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('104347472034971653', '1_1.jpg', '/202112/30171726Fkzzd61W.jpg', 'image/jpeg', '1', '1', '2021-12-30 17:17:26', '192.168.1.23', '1', '2021-12-30 17:17:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108315531993071620', 'home.png', '/202201/10160505FQmG0VVg.png', 'image/png', '1', '1', '2022-01-10 16:05:05', '192.168.1.23', '1', '2022-01-10 16:05:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108315624263565312', 'prod.png', '/202201/10160527nUCKNkJj.png', 'image/png', '1', '1', '2022-01-10 16:05:27', '192.168.1.23', '1', '2022-01-10 16:05:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108321343943655430', 'home.png', '/202201/10162810dTyIOZ59.png', 'image/png', '1', '1', '2022-01-10 16:28:11', '192.168.1.23', '1', '2022-01-10 16:28:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108321408468828163', 'post.png', '/202201/10162826NDuF1zSP.png', 'image/png', '1', '1', '2022-01-10 16:28:26', '192.168.1.23', '1', '2022-01-10 16:28:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108325559214981126', 'home.png', '/202201/10164455hZKO9GjG.png', 'image/png', '1', '1', '2022-01-10 16:44:56', '192.168.1.23', '1', '2022-01-10 16:44:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108325590730981381', '4.jpg', '/202201/10164503sLfL87Ba.jpg', 'image/jpeg', '1', '1', '2022-01-10 16:45:03', '192.168.1.23', '1', '2022-01-10 16:45:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('108325640882274314', '4_2.jpg', '/202201/10164515E4EK1kZz.jpg', 'image/jpeg', '1', '1', '2022-01-10 16:45:15', '192.168.1.23', '1', '2022-01-10 16:45:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('112622012200566790', 'mceu_79354852121642828649074.png', '/202201/22131730EnjO34yN.png', 'image/png', '1', '1', '2022-01-22 13:17:30', '192.168.1.23', '1', '2022-01-22 13:17:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('112622094379565059', 'QQ图片20220122104745.png', '/202201/221317498mXbIuCF.png', 'image/png', '1', '1', '2022-01-22 13:17:50', '192.168.1.23', '1', '2022-01-22 13:17:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('121734502867976200', 'QQ图片20220121173519.png', '/202202/16164717Uolg0ygf.png', 'image/png', '1', '1', '2022-02-16 16:47:17', '192.168.1.23', '1', '2022-02-16 16:47:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('122236675062153227', 'QQ图片20220121173409.png', '/202202/18020244z0hSmsld.png', 'image/png', '1', '1', '2022-02-18 02:02:44', '192.168.1.23', '1', '2022-02-18 02:02:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125290976743440394', 'QQ图片20211118163944.png', '/202202/26121926XXk39cF6.png', 'image/png', '1', '1', '2022-02-26 12:19:27', '192.168.1.23', '1', '2022-02-26 12:19:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125291886458617864', 'QQ图片20220224181941.png', '/202202/26122303kTfo8Atl.png', 'image/png', '1', '1', '2022-02-26 12:23:04', '192.168.1.23', '1', '2022-02-26 12:23:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125292025629818880', 'QQ图片20220121173409.png', '/202202/26122336GCNVWZ45.png', 'image/png', '1', '1', '2022-02-26 12:23:37', '192.168.1.23', '1', '2022-02-26 12:23:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125294201953501186', 'QQ图片20220121173519.png', '/202202/26123215aPbC152k.png', 'image/png', '1', '1', '2022-02-26 12:32:16', '192.168.1.23', '1', '2022-02-26 12:32:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125294339614752778', 'QQ图片20220224181941.png', '/202202/2612324844U3NCBZ.png', 'image/png', '1', '1', '2022-02-26 12:32:48', '192.168.1.23', '1', '2022-02-26 12:32:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125294413887488006', 'Untitled.png', '/202202/26123306BX09Z3XY.png', 'image/png', '1', '1', '2022-02-26 12:33:06', '192.168.1.23', '1', '2022-02-26 12:33:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('125294490932658182', 'QQ图片20220121173409.png', '/202202/26123324tnZQQSD4.png', 'image/png', '1', '1', '2022-02-26 12:33:24', '192.168.1.23', '1', '2022-02-26 12:33:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('132632492382011399', 'favicon.png', '/202203/18183200ow6yRB4v.png', 'image/png', '1', '1', '2022-03-18 18:32:00', '192.168.1.23', '1', '2022-03-18 18:32:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134359108606935047', 'logo.svg', '/202203/23125257V3rF4Vx7.svg', 'image/svg+xml', '1', '1', '2022-03-23 12:52:58', '192.168.1.23', '1', '2022-03-23 12:52:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134396355892920331', '商标注册经营范围.txt', '/202203/23152058GOFejOiS.txt', 'text/plain', '1', '1', '2022-03-23 15:20:58', '192.168.1.23', '1', '2022-03-23 15:20:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134425362155880449', 'wldos.svg', '/202203/231716136lcmxsMe.svg', 'image/svg+xml', '1', '1', '2022-03-23 17:16:14', '192.168.1.23', '1', '2022-03-23 17:16:14', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134430439310540802', 'wldos.svg', '/202203/23173624RNn7HdAM.svg', 'image/svg+xml', '1', '1', '2022-03-23 17:36:24', '192.168.1.23', '1', '2022-03-23 17:36:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134433348790173697', 'wldos.svg', '/202203/23174758w6YqssKd.svg', 'image/svg+xml', '1', '1', '2022-03-23 17:47:58', '192.168.1.23', '1', '2022-03-23 17:47:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134433999205089286', 'wldos.svg', '/202203/23175033bZ8WPfSP.svg', 'image/svg+xml', '1', '1', '2022-03-23 17:50:33', '192.168.1.23', '1', '2022-03-23 17:50:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134434686274027526', 'wldos.svg', '/202203/23175316LUhpaUfQ.svg', 'image/svg+xml', '1', '1', '2022-03-23 17:53:17', '192.168.1.23', '1', '2022-03-23 17:53:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134446202557743109', 'wldos - 副本2.svg', '/202203/23183902OruG22r8.svg', 'image/svg+xml', '1', '1', '2022-03-23 18:39:03', '192.168.1.23', '1', '2022-03-23 18:39:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134446346413981704', 'wldos蓝底头像.png', '/202203/23183936b1vNqZHV.png', 'image/png', '1', '1', '2022-03-23 18:39:37', '192.168.1.23', '1', '2022-03-23 18:39:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134446458896826373', 'favicon.png', '/202203/23184003TSn5n6pp.png', 'image/png', '1', '1', '2022-03-23 18:40:04', '192.168.1.23', '1', '2022-03-23 18:40:04', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('134446481453793281', 'logo.svg', '/202203/23184009VU9NFFTw.svg', 'image/svg+xml', '1', '1', '2022-03-23 18:40:09', '192.168.1.23', '1', '2022-03-23 18:40:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150293595891089408', '141fc006a606326e0763dc07ea04f04f1d71f0f4497b47b6841c36aa83c870fe.jpg', '/202205/06121055eOSoFhLW.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:10:56', '192.168.1.23', '1', '2022-05-06 12:10:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150293626782138377', '9b1678bfca3666c7ecc4b8c4b5d9ee5e48b1e4309aa1016185c227dd0847b6bf.jpg', '/202205/06121102HFczsswR.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:11:03', '192.168.1.23', '1', '2022-05-06 12:11:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150295384342315016', 'CrummockWater_ZH-CN9317792500_1920x1080.jpg', '/202205/06121801un7jisZj.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:18:02', '192.168.1.23', '1', '2022-05-06 12:18:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150295443331006466', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/202205/06121816OAbRj3ID.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:18:16', '192.168.1.23', '1', '2022-05-06 12:18:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150295472464642052', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/202205/06121822tVbUuQxv.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:18:23', '192.168.1.23', '1', '2022-05-06 12:18:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150297038164770820', 'QQ图片20200729124240.jpg', '/202205/06122436nrhb1YT7.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:24:36', '192.168.1.23', '1', '2022-05-06 12:24:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150297067055136771', 'CrummockWater_ZH-CN9317792500_1920x1080.jpg', '/202205/061224437WHuMyO3.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:24:43', '192.168.1.23', '1', '2022-05-06 12:24:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150297108268367878', 'c59f53a654721cb8fd3bc4e9fc6ff7a626646b0a0ca21102a0a12e9c5ef42a83.jpg', '/202205/06122452fnaqf8V6.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:24:53', '192.168.1.23', '1', '2022-05-06 12:24:53', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150300162342436872', '1.jpg', '/202205/06123701NbqQMCxN.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:37:01', '192.168.1.23', '1', '2022-05-06 12:37:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150300190893064193', '3_1.jpg', '/202205/06123707DAaVlWSP.jpg', 'image/jpeg', '1', '1', '2022-05-06 12:37:08', '192.168.1.23', '1', '2022-05-06 12:37:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150300502269804554', 'QQ图片20220121173519.png', '/202205/06123822sB2PjZBI.png', 'image/png', '1', '1', '2022-05-06 12:38:22', '192.168.1.23', '1', '2022-05-06 12:38:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150300530535219211', 'QQ图片20220121173409.png', '/202205/06123828C2ltc4Eu.png', 'image/png', '1', '1', '2022-05-06 12:38:29', '192.168.1.23', '1', '2022-05-06 12:38:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150300565800927237', 'QQ图片20220122104745.png', '/202205/06123837VxCbEDDz.png', 'image/png', '1', '1', '2022-05-06 12:38:37', '192.168.1.23', '1', '2022-05-06 12:38:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301158409945092', 'QQ图片20220224181941.png', '/202205/06124058Bvesfnef.png', 'image/png', '1', '1', '2022-05-06 12:40:59', '192.168.1.23', '1', '2022-05-06 12:40:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301192983592967', 'QQ图片20220224181941.png', '/202205/06124106xvt9bVW5.png', 'image/png', '1', '1', '2022-05-06 12:41:07', '192.168.1.23', '1', '2022-05-06 12:41:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301225007104001', '111.png', '/202205/06124114R4RDz2N7.png', 'image/png', '1', '1', '2022-05-06 12:41:15', '192.168.1.23', '1', '2022-05-06 12:41:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301802827005958', 'QQ图片20220121173409.png', '/202205/06124332fm5QTY3I.png', 'image/png', '1', '1', '2022-05-06 12:43:32', '192.168.1.23', '1', '2022-05-06 12:43:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301838960934922', 'QQ图片20220121173519.png', '/202205/06124340nz9SOHqM.png', 'image/png', '1', '1', '2022-05-06 12:43:41', '192.168.1.23', '1', '2022-05-06 12:43:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150301909127446538', 'QQ图片20220224181941.png', '/202205/061243579RoNwPJw.png', 'image/png', '1', '1', '2022-05-06 12:43:58', '192.168.1.23', '1', '2022-05-06 12:43:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150341109163933701', 'QQ图片20220121173519.png', '/202205/06151943xAkB5Yo2.png', 'image/png', '1', '1', '2022-05-06 15:19:44', '192.168.1.23', '1', '2022-05-06 15:19:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('150341142261186565', 'QQ图片20220121173409.png', '/202205/06151951JTjIsxjL.png', 'image/png', '1', '1', '2022-05-06 15:19:52', '192.168.1.23', '1', '2022-05-06 15:19:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('157641352037449737', '111.png', '/202205/26184817Qcdk9yNO.png', 'image/png', '1', '1', '2022-05-26 18:48:17', '192.168.1.23', '1', '2022-05-26 18:48:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('157641587660865546', 'Untitled.png', '/202205/26184913EpyKZnxC.png', 'image/png', '1', '1', '2022-05-26 18:49:13', '192.168.1.23', '1', '2022-05-26 18:49:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('198599187726254080', '111.png', '/202209/16192006kkX6YHpz.png', 'image/png', '1', '198575893094514697', '2022-09-16 19:20:06', '192.168.1.23', '198575893094514697', '2022-09-16 19:20:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('206153834653466630', 'QQ图片20220121173409.png', '/202210/07153934bCadUF4i.png', 'image/png', '1', '1', '2022-10-07 15:39:34', '192.168.1.23', '1', '2022-10-07 15:39:34', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('206153909341437962', '111.png', '/202210/07153952jKaJFIvA.png', 'image/png', '1', '1', '2022-10-07 15:39:52', '192.168.1.23', '1', '2022-10-07 15:39:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('214120915558645761', '0.jpg', '/202210/29151754cbK7I3ty.jpg', 'image/jpg', '1', '0', '2022-10-29 15:17:54', '192.168.1.23', '0', '2022-10-29 15:17:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('214132161074872323', '0.jpg', '/202210/29160235bMkgWuTg.jpg', 'image/jpg', '1', '0', '2022-10-29 16:02:36', '192.168.1.23', '0', '2022-10-29 16:02:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('214145229104267265', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2022/10/29165431nSPtJhlt.jpg', 'image/jpeg', '1', '214132159892078601', '2022-10-29 16:54:31', '192.168.1.23', '214132159892078601', '2022-10-29 16:54:31', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('214858325762424833', '0.jpg', '/2022/10/31160806yDuzotP7.jpg', 'image/jpg', '1', '0', '2022-10-31 16:08:07', '192.168.1.23', '0', '2022-10-31 16:08:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('245418265442697226', '88.jpg', '/2023/01/24000224gQ55IKkw.jpg', 'image/jpeg', '1', '1', '2023-01-24 00:02:24', '192.168.1.23', '1', '2023-01-24 00:02:24', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('245418641122312199', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/2023/01/24000353R6Kpfb9G.jpg', 'image/jpeg', '1', '1', '2023-01-24 00:03:54', '192.168.1.23', '1', '2023-01-24 00:03:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('245418674794184712', '111.jpg', '/2023/01/24000401r6v5K5va.jpg', 'image/jpeg', '1', '1', '2023-01-24 00:04:02', '192.168.1.23', '1', '2023-01-24 00:04:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247261821362946056', '330625-140G502062028.jpg', '/2023/01/29020802bAK0QivY.jpg', 'image/jpeg', '1', '1', '2023-01-29 02:08:02', '192.168.1.23', '1', '2023-01-29 02:08:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247261856200835079', '2121.jpg', '/2023/01/29020810IMIzfRO5.jpg', 'image/jpeg', '1', '1', '2023-01-29 02:08:10', '192.168.1.23', '1', '2023-01-29 02:08:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247472110364704770', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/2023/01/29160338cfXPX2gG.jpg', 'image/jpeg', '1', '1', '2023-01-29 16:03:39', '192.168.1.23', '1', '2023-01-29 16:03:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247472744035958787', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/2023/01/29160609fMOiiXsw.jpg', 'image/jpeg', '1', '1', '2023-01-29 16:06:10', '192.168.1.23', '1', '2023-01-29 16:06:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247474434936061955', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/2023/01/29161253l1Iiz71f.jpg', 'image/jpeg', '1', '1', '2023-01-29 16:12:53', '192.168.1.23', '1', '2023-01-29 16:12:53', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247569007351873543', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/2023/01/29222840UpxulnKY.jpg', 'image/jpeg', '1', '1', '2023-01-29 22:28:41', '192.168.1.23', '1', '2023-01-29 22:28:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247589050001309707', '88.jpg', '/2023/01/29234819UfLgcsOq.jpg', 'image/jpeg', '1', '1', '2023-01-29 23:48:20', '192.168.1.23', '1', '2023-01-29 23:48:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247589088358219785', '20100810080644774.jpg', '/2023/01/292348284OWggsrb.jpg', 'image/jpeg', '1', '1', '2023-01-29 23:48:29', '192.168.1.23', '1', '2023-01-29 23:48:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247615641637011466', '111.jpg', '/2023/01/30013359JGQFj2J1.jpg', 'image/jpeg', '1', '1', '2023-01-30 01:33:59', '192.168.1.23', '1', '2023-01-30 01:33:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('247615669826928646', '88.jpg', '/2023/01/30013406YAc95xHF.jpg', 'image/jpeg', '1', '1', '2023-01-30 01:34:06', '192.168.1.23', '1', '2023-01-30 01:34:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('248227241905537030', '111.jpg', '/2023/01/31180416XsHumokb.jpg', 'image/jpeg', '1', '1', '2023-01-31 18:04:16', '192.168.1.23', '1', '2023-01-31 18:04:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('248227270430998536', '20160203-0d4f897abc3842c0a1bd03bd34f682d3.jpg', '/2023/01/31180423Js68AHKN.jpg', 'image/jpeg', '1', '1', '2023-01-31 18:04:23', '192.168.1.23', '1', '2023-01-31 18:04:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249432626133909506', '111.jpg', '/2023/02/04015402zkFFJWUr.jpg', 'image/jpeg', '1', '1', '2023-02-04 01:54:02', '192.168.1.23', '1', '2023-02-04 01:54:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249434015522275339', '88.jpg', '/2023/02/04015933QDXKoFBW.jpg', 'image/jpeg', '1', '1', '2023-02-04 01:59:34', '192.168.1.23', '1', '2023-02-04 01:59:34', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249434076171911168', '111.jpg', '/2023/02/04015948QDET6C5W.jpg', 'image/jpeg', '1', '1', '2023-02-04 01:59:48', '192.168.1.23', '1', '2023-02-04 01:59:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249435959846420480', '111.jpg', '/2023/02/04020717j5acKl2p.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:07:17', '192.168.1.23', '1', '2023-02-04 02:07:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249435998123638788', '111.jpg', '/2023/02/04020726nqb3SMlE.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:07:26', '192.168.1.23', '1', '2023-02-04 02:07:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249436591844147206', '111.jpg', '/2023/02/04020947br5dP4zp.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:09:48', '192.168.1.23', '1', '2023-02-04 02:09:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249436617060302855', '2121.jpg', '/2023/02/04020953K0MS2E01.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:09:54', '192.168.1.23', '1', '2023-02-04 02:09:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249437084041527303', '111.jpg', '/2023/02/04021145BbIm4UJ9.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:11:45', '192.168.1.23', '1', '2023-02-04 02:11:45', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249437154921070592', '2222.jpg', '/2023/02/040212027s0dB4NG.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:12:02', '192.168.1.23', '1', '2023-02-04 02:12:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249438105501351937', '111.jpg', '/2023/02/040215488Y5bXwjY.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:15:49', '192.168.1.23', '1', '2023-02-04 02:15:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249438134412689409', '2121.jpg', '/2023/02/04021555rMl5ziCm.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:15:56', '192.168.1.23', '1', '2023-02-04 02:15:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249439280430759940', '111.jpg', '/2023/02/04022028YuYQTntn.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:20:29', '192.168.1.23', '1', '2023-02-04 02:20:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249439309484703751', '2121.jpg', '/2023/02/04022035qAVlM5Tw.jpg', 'image/jpeg', '1', '1', '2023-02-04 02:20:36', '192.168.1.23', '1', '2023-02-04 02:20:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249721696034668548', '项目logo和名称.png', '/2023/02/04210241wlDvZWhS.png', 'image/png', '1', '1', '2023-02-04 21:02:42', '192.168.1.23', '1', '2023-02-04 21:02:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249723389438771211', '项目logo和名称.png', '/2023/02/0421092588kZmpm8.png', 'image/png', '1', '1', '2023-02-04 21:09:26', '192.168.1.23', '1', '2023-02-04 21:09:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249733332892041221', '项目logo和名称.png', '/2023/02/04214856tPGMvJ12.png', 'image/png', '1', '1', '2023-02-04 21:48:56', '192.168.1.23', '1', '2023-02-04 21:48:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249733429663023108', '项目logo和名称.png', '/2023/02/04214919VhBcVvw3.png', 'image/png', '1', '1', '2023-02-04 21:49:19', '192.168.1.23', '1', '2023-02-04 21:49:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249736867964698634', 'mceu_76364307811675519379080.jpg', '/2023/02/04220259mYTP9WQ3.jpg', 'image/jpeg', '1', '1', '2023-02-04 22:02:59', '192.168.1.23', '1', '2023-02-04 22:02:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('249737539707650057', 'mceu_80393933821675519539283.jpg', '/2023/02/04220539WcQsiwDw.jpg', 'image/jpeg', '1', '1', '2023-02-04 22:05:39', '192.168.1.23', '1', '2023-02-04 22:05:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252542582727032837', '项目logo和名称.png', '/2023/02/121551537JrqrJJt.png', 'image/png', '1', '1', '2023-02-12 15:51:54', '192.168.1.23', '1', '2023-02-12 15:51:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252542621427875840', 'wldos-produce.png', '/2023/02/12155202ETlfoy57.png', 'image/png', '1', '1', '2023-02-12 15:52:03', '192.168.1.23', '1', '2023-02-12 15:52:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252542659122085896', 'wldos-platform.jpeg', '/2023/02/12155211Y831WtYv.jpeg', 'image/jpeg', '1', '1', '2023-02-12 15:52:12', '192.168.1.23', '1', '2023-02-12 15:52:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252547561810477059', '项目logo和名称.png', '/2023/02/12161140mQbzeBAM.png', 'image/png', '1', '1', '2023-02-12 16:11:41', '192.168.1.23', '1', '2023-02-12 16:11:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252547600679092227', 'wldos-platform.jpeg', '/2023/02/12161150TaD4kDux.jpeg', 'image/jpeg', '1', '1', '2023-02-12 16:11:50', '192.168.1.23', '1', '2023-02-12 16:11:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252547635399540739', 'wldos-kpaycms.jpeg', '/2023/02/12161158BCmPgyDI.jpeg', 'image/jpeg', '1', '1', '2023-02-12 16:11:58', '192.168.1.23', '1', '2023-02-12 16:11:58', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('252547669453094922', 'wldos-produce.png', '/2023/02/121612064lAAi5ap.png', 'image/png', '1', '1', '2023-02-12 16:12:07', '192.168.1.23', '1', '2023-02-12 16:12:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('253045444397678600', '项目logo和名称.png', '/2023/02/14011005pO9zjtdw.png', 'image/png', '1', '1', '2023-02-14 01:10:05', '192.168.1.23', '1', '2023-02-14 01:10:05', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('253045766058852354', '项目logo和名称.png', '/2023/02/14011121EcTCWtjt.png', 'image/png', '1', '1', '2023-02-14 01:11:22', '192.168.1.23', '1', '2023-02-14 01:11:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254140761801146373', '141fc006a606326e0763dc07ea04f04f1d71f0f4497b47b6841c36aa83c870fe.jpg', '/2023/02/17014229cWVkaYy9.jpg', 'image/jpeg', '1', '1', '2023-02-17 01:42:29', '127.0.0.1', '1', '2023-02-17 01:42:29', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254140823113482242', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/170142439iqiMLqy.jpg', 'image/jpeg', '1', '1', '2023-02-17 01:42:44', '127.0.0.1', '1', '2023-02-17 01:42:44', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254141398987227143', 'mceu_52787083711676569501110.jpg', '/2023/02/17014501F5kvgzNi.jpg', 'image/jpeg', '1', '1', '2023-02-17 01:45:01', '127.0.0.1', '1', '2023-02-17 01:45:01', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254164515650453514', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/170316525CRbnTLR.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:16:53', '127.0.0.1', '1', '2023-02-17 03:16:53', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254164552522579972', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/17031701rtSeyG1h.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:17:01', '127.0.0.1', '1', '2023-02-17 03:17:01', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254167964500803593', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/17033034pZh09fZd.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:30:35', '127.0.0.1', '1', '2023-02-17 03:30:35', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254168006720667655', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17033045zZ36Hul3.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:30:45', '127.0.0.1', '1', '2023-02-17 03:30:45', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254172146431606784', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17034711kWxACMRe.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:47:12', '127.0.0.1', '1', '2023-02-17 03:47:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254172188538224644', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17034722RC0aA1gK.jpg', 'image/jpeg', '1', '1', '2023-02-17 03:47:22', '127.0.0.1', '1', '2023-02-17 03:47:22', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254177935573565451', 'c59f53a654721cb8fd3bc4e9fc6ff7a626646b0a0ca21102a0a12e9c5ef42a83.jpg', '/2023/02/17041011r2XfQtlh.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:10:12', '127.0.0.1', '1', '2023-02-17 04:10:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254177964417794048', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/17041019EL6CGxyB.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:10:19', '127.0.0.1', '1', '2023-02-17 04:10:19', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254181975548149770', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17042615YxfGxur4.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:26:15', '127.0.0.1', '1', '2023-02-17 04:26:15', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254182036432666624', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/170426297OEhw10A.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:26:30', '127.0.0.1', '1', '2023-02-17 04:26:30', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254182111712034824', 'mceu_93150838211676579207752.jpg', '/2023/02/17042647UD6iZ8MD.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:26:48', '127.0.0.1', '1', '2023-02-17 04:26:48', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254183897436962824', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17043353rYo3FjB7.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:33:54', '127.0.0.1', '1', '2023-02-17 04:33:54', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254183928000856064', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17043400FSYKLJ2H.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:34:01', '127.0.0.1', '1', '2023-02-17 04:34:01', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254183974410829827', 'mceu_60142095011676579651870.jpg', '/2023/02/17043411K4tmS9ZN.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:34:12', '127.0.0.1', '1', '2023-02-17 04:34:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254184541690445829', 'mceu_34229099321676579786886.jpg', '/2023/02/17043626MsVjuzyB.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:36:27', '127.0.0.1', '1', '2023-02-17 04:36:27', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254187968982073350', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17045004vkvjlUmi.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:50:04', '127.0.0.1', '1', '2023-02-17 04:50:04', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254188002335178760', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17045012bjuSjeyy.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:50:12', '127.0.0.1', '1', '2023-02-17 04:50:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254188083016810498', 'mceu_45156506611676580631471.jpg', '/2023/02/17045031CkPsqmbz.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:50:32', '127.0.0.1', '1', '2023-02-17 04:50:32', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254189899913805825', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/17045744oc1PQ3bl.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:57:45', '127.0.0.1', '1', '2023-02-17 04:57:45', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254189929877913601', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17045751CHL3JlFD.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:57:52', '127.0.0.1', '1', '2023-02-17 04:57:52', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254189983107825665', 'mceu_80164350511676581084494.jpg', '/2023/02/17045804snVlmMr6.jpg', 'image/jpeg', '1', '1', '2023-02-17 04:58:05', '127.0.0.1', '1', '2023-02-17 04:58:05', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254190796806995978', 'mceu_81028093021676581278460.jpg', '/2023/02/17050118fuaOZhXG.jpg', 'image/jpeg', '1', '1', '2023-02-17 05:01:19', '127.0.0.1', '1', '2023-02-17 05:01:19', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254193537939849220', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17051211ipuvDnwf.jpg', 'image/jpeg', '1', '1', '2023-02-17 05:12:12', '127.0.0.1', '1', '2023-02-17 05:12:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254193568713457669', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/170512196LVbUXVz.jpg', 'image/jpeg', '1', '1', '2023-02-17 05:12:19', '127.0.0.1', '1', '2023-02-17 05:12:19', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254196088848433153', 'mceu_27256587911676582539916.jpg', '/2023/02/17052220fYwW4BpE.jpg', 'image/jpeg', '1', '1', '2023-02-17 05:22:20', '127.0.0.1', '1', '2023-02-17 05:22:20', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254411418992558088', '141fc006a606326e0763dc07ea04f04f1d71f0f4497b47b6841c36aa83c870fe.jpg', '/2023/02/171937583KIeMpmV.jpg', 'image/jpeg', '1', '1', '2023-02-17 19:37:59', '127.0.0.1', '1', '2023-02-17 19:37:59', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254411459928965120', 'f6eae778684d7f14900651aa6daa6621e1a42232ff560332310fe4b9f4940cb8.jpg', '/2023/02/17193808tHlrGIbf.jpg', 'image/jpeg', '1', '1', '2023-02-17 19:38:09', '127.0.0.1', '1', '2023-02-17 19:38:09', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254411655941373955', 'mceu_19395743111676633935385.jpg', '/2023/02/171938556L6h8kMK.jpg', 'image/jpeg', '1', '1', '2023-02-17 19:38:56', '127.0.0.1', '1', '2023-02-17 19:38:56', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254416720366321675', 'mceu_49640552211676635142881.jpg', '/2023/02/17195902QvJDtorO.jpg', 'image/jpeg', '1', '1', '2023-02-17 19:59:03', '127.0.0.1', '1', '2023-02-17 19:59:03', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254421132698632196', 'mceu_73019330721676636194214.jpg', '/2023/02/17201634Y58N5lLn.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:16:35', '127.0.0.1', '1', '2023-02-17 20:16:35', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254422285473726472', 'ecb5597d8bf86edc278989c6f12366e373a6dcb8ff506c6c5ff5623d100ec140.jpg', '/2023/02/17202109OMHS4dN3.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:21:10', '127.0.0.1', '1', '2023-02-17 20:21:10', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254422316784205833', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17202117XN5KQfyN.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:21:17', '127.0.0.1', '1', '2023-02-17 20:21:17', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254424393442836483', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17202932JnlnxeW3.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:29:32', '127.0.0.1', '1', '2023-02-17 20:29:32', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254424427915821059', 'fab4f88aa53e2aa0fcc64c98fe0a371d7f9df14639436c998b938038471f29c1.jpg', '/2023/02/17202940zevQSOvV.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:29:41', '127.0.0.1', '1', '2023-02-17 20:29:41', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254429700114006026', 'mceu_29031953411676638237468.jpg', '/2023/02/17205037aV5ZluqQ.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:50:38', '127.0.0.1', '1', '2023-02-17 20:50:38', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('254430170761052170', 'imagetools0.jpg', '/2023/02/17205229uPlwJ9zT.jpg', 'image/jpeg', '1', '1', '2023-02-17 20:52:30', '127.0.0.1', '1', '2023-02-17 20:52:30', '127.0.0.1', 'normal', '0');
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
INSERT INTO `wo_file` VALUES ('1527684379916812291', '微信图片_20201031183101.jpg', '/202107/171432192HQnmgy1.jpg', 'image/jpeg', '1', '1', '2021-07-17 14:32:19', '192.168.1.23', '1', '2021-07-17 14:32:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1527684471302307843', '20110222082818104208.jpg', '/202107/17143240IRlMQj7I.jpg', 'image/jpeg', '1', '1', '2021-07-17 14:32:41', '192.168.1.23', '1', '2021-07-17 14:32:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1527684559466577924', '1900800517827128683.jpg', '/202107/17143301qRRCrAMS.jpg', 'image/jpeg', '1', '1', '2021-07-17 14:33:02', '192.168.1.23', '1', '2021-07-17 14:33:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1527684627099729930', 'd02788e9b33e18c358622e.jpg', '/202107/17143318x9NSyo7H.jpg', 'image/jpeg', '1', '1', '2021-07-17 14:33:18', '192.168.1.23', '1', '2021-07-17 14:33:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1527684688399482888', '20100810080644774.jpg', '/202107/17143332lFnd2J4O.jpg', 'image/jpeg', '1', '1', '2021-07-17 14:33:33', '192.168.1.23', '1', '2021-07-17 14:33:33', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529478371901882374', 'QQ图片20200729123735.jpg', '/202107/22132100Af4df8Zd.jpg', 'image/jpeg', '1', '1', '2021-07-22 13:21:00', '192.168.1.23', '1', '2021-07-22 13:21:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529478415770107910', '2121.jpg', '/202107/22132110P3nq8OHd.jpg', 'image/jpeg', '1', '1', '2021-07-22 13:21:11', '192.168.1.23', '1', '2021-07-22 13:21:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529478450842877960', '0723c72e79bfce9a875be02e5fe041d2a07b1d39b6b56c57d62f5cab100cdd7c.jpg', '/202107/22132119LEqy1duj.jpg', 'image/jpeg', '1', '1', '2021-07-22 13:21:19', '192.168.1.23', '1', '2021-07-22 13:21:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529478487064887303', '1038209_1200x1000_0.jpg', '/202107/22132127UDOqynw6.jpg', 'image/jpeg', '1', '1', '2021-07-22 13:21:28', '192.168.1.23', '1', '2021-07-22 13:21:28', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529478533214814211', '20110222082818104208.jpg', '/202107/22132138QgCcicd8.jpg', 'image/jpeg', '1', '1', '2021-07-22 13:21:39', '192.168.1.23', '1', '2021-07-22 13:21:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529497274841481219', '2012815167420064.jpg', '/202107/22143607AWQXe05M.jpg', 'image/jpeg', '1', '1', '2021-07-22 14:36:07', '192.168.1.23', '1', '2021-07-22 14:36:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529497305338265603', '1038209_1200x1000_0.jpg', '/202107/22143614PQpAABBu.jpg', 'image/jpeg', '1', '1', '2021-07-22 14:36:14', '192.168.1.23', '1', '2021-07-22 14:36:14', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529497350649331714', '330625-140G502062028.jpg', '/202107/22143625KMW7aJM0.jpg', 'image/jpeg', '1', '1', '2021-07-22 14:36:25', '192.168.1.23', '1', '2021-07-22 14:36:25', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529497424628465675', 'QQ图片20160609124809.png', '/202107/221436420ulhJZBi.png', 'image/png', '1', '1', '2021-07-22 14:36:43', '192.168.1.23', '1', '2021-07-22 14:36:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529497733266325509', '1900800517827128683.jpg', '/202107/22143756hPw1HJUK.jpg', 'image/jpeg', '1', '1', '2021-07-22 14:37:56', '192.168.1.23', '1', '2021-07-22 14:37:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529604376184406021', 'mceu_11891426611626961301547.png', '/202107/22214141cU3rwzCu.png', 'image/png', '1', '1', '2021-07-22 21:41:42', '192.168.1.23', '1', '2021-07-22 21:41:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1529606977453998084', 'mceclip0.png', '/202107/22215202ffH6nuPK.png', 'image/png', '1', '1', '2021-07-22 21:52:02', '192.168.1.23', '1', '2021-07-22 21:52:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1531257529074106376', '330625-140G502062028.jpg', '/202107/27111044jywdPf5p.jpg', 'image/jpeg', '1', '1', '2021-07-27 11:10:44', '192.168.1.23', '1', '2021-07-27 11:10:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1531257583897853961', '606297099851665269.jpg', '/202107/27111057jDC85Pgu.jpg', 'image/jpeg', '1', '1', '2021-07-27 11:10:57', '192.168.1.23', '1', '2021-07-27 11:10:57', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1531257630622400514', 'Borneo_FF_768_HD_ZH-CN1688139761.jpg', '/202107/27111108Q9ZPYfBw.jpg', 'image/jpeg', '1', '1', '2021-07-27 11:11:09', '192.168.1.23', '1', '2021-07-27 11:11:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1531257683810369538', 'bg_corner_tr.png', '/202107/27111121Hr4rrTF9.png', 'image/png', '1', '1', '2021-07-27 11:11:21', '192.168.1.23', '1', '2021-07-27 11:11:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1531257765850955784', 'lsnb.jpg', '/202107/27111140NW6ngca5.jpg', 'image/jpeg', '1', '1', '2021-07-27 11:11:41', '192.168.1.23', '1', '2021-07-27 11:11:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1534154263144087561', 'TallGrass_ZH-CN12379752699_1366x768.jpg', '/202108/04110119v2Y66WF9.jpg', 'image/jpeg', '1', '1', '2021-08-04 11:01:20', '192.168.1.23', '1', '2021-08-04 11:01:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768192972210183', 'QQ图片20210825111403.png', '/202108/25111621MIHFQ3X4.png', 'image/png', '1', '1', '2021-08-25 11:16:22', '192.168.1.23', '1', '2021-08-25 11:16:22', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768462917615625', 'QQ图片20210825111516.png', '/202108/25111726Pnn9yP6G.png', 'image/png', '1', '1', '2021-08-25 11:17:26', '192.168.1.23', '1', '2021-08-25 11:17:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768563933233161', 'QQ图片20210825111516.png', '/202108/25111750HOJDk9mF.png', 'image/png', '1', '1', '2021-08-25 11:17:50', '192.168.1.23', '1', '2021-08-25 11:17:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768646644908040', 'QQ图片20210825111516.png', '/202108/25111810vCPsLJvA.png', 'image/png', '1', '1', '2021-08-25 11:18:10', '192.168.1.23', '1', '2021-08-25 11:18:10', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768712772304904', 'QQ图片20210825111320.png', '/202108/25111825jdfmAilb.png', 'image/png', '1', '1', '2021-08-25 11:18:26', '192.168.1.23', '1', '2021-08-25 11:18:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768756615364610', 'QQ图片20210825111541.png', '/202108/251118362Iqxb3Pf.png', 'image/png', '1', '1', '2021-08-25 11:18:36', '192.168.1.23', '1', '2021-08-25 11:18:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541768840883126282', 'QQ图片20210825111516.png', '/202108/251118569kfNcZrm.png', 'image/png', '1', '1', '2021-08-25 11:18:56', '192.168.1.23', '1', '2021-08-25 11:18:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541803560039792649', 'mceu_76057113311629869813773.png', '/202108/251336540TgKIXny.png', 'image/png', '1', '1', '2021-08-25 13:36:54', '192.168.1.23', '1', '2021-08-25 13:36:54', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1541803719503036427', 'mceu_83404207521629869851628.png', '/202108/25133732Hqa9DTUm.png', 'image/png', '1', '1', '2021-08-25 13:37:32', '192.168.1.23', '1', '2021-08-25 13:37:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544355886910849031', 'QQ图片20210901143828.png', '/202109/01143856Nsz2ilbt.png', 'image/png', '1', '1', '2021-09-01 14:38:56', '192.168.1.23', '1', '2021-09-01 14:38:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544355954871156746', '221040_3d1c748e_7754170.png', '/202109/01143912YQboaB0y.png', 'image/png', '1', '1', '2021-09-01 14:39:12', '192.168.1.23', '1', '2021-09-01 14:39:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544356085813133322', 'depend.jpg', '/202109/01143943fehF4DiP.jpg', 'image/jpeg', '1', '1', '2021-09-01 14:39:44', '192.168.1.23', '1', '2021-09-01 14:39:44', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544356166515736582', '2121.jpg', '/202109/01144002lV1aQSTW.jpg', 'image/jpeg', '1', '1', '2021-09-01 14:40:03', '192.168.1.23', '1', '2021-09-01 14:40:03', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544356213844262921', '1.jpg', '/202109/01144014ZzWE8UDf.jpg', 'image/jpeg', '1', '1', '2021-09-01 14:40:14', '192.168.1.23', '1', '2021-09-01 14:40:14', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544356572520169473', 'mceu_4820211811630478499364.png', '/202109/0114413984v4gg7I.png', 'image/png', '1', '1', '2021-09-01 14:41:40', '192.168.1.23', '1', '2021-09-01 14:41:40', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544388025639419907', 'mceu_92846795211630485998111.png', '/202109/01164638X8YZGiUj.png', 'image/png', '1', '1', '2021-09-01 16:46:39', '192.168.1.23', '1', '2021-09-01 16:46:39', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544388236625494018', 'mceu_5158868321630486048552.jpg', '/202109/01164728jKwF85AI.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:47:29', '192.168.1.23', '1', '2021-09-01 16:47:29', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544388509074898954', 'mceu_45554245631630486113447.jpg', '/202109/01164833RxiSVtBk.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:48:34', '192.168.1.23', '1', '2021-09-01 16:48:34', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544388690323357696', 'mceu_36128042441630486156697.jpg', '/202109/01164917U91qTj66.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:49:17', '192.168.1.23', '1', '2021-09-01 16:49:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544388984197267462', 'mceu_20066792151630486226725.jpg', '/202109/01165027HBw1zZt7.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:50:27', '192.168.1.23', '1', '2021-09-01 16:50:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544389044385529859', 'mceu_65519516061630486241045.jpg', '/202109/01165041ueLS9RdU.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:50:42', '192.168.1.23', '1', '2021-09-01 16:50:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544389294802255873', 'mceu_98362466471630486300741.jpg', '/202109/01165141znd2Jymw.jpg', 'image/jpeg', '1', '1', '2021-09-01 16:51:41', '192.168.1.23', '1', '2021-09-01 16:51:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544402534387073029', 'mceu_29810057081630489457342.jpg', '/202109/01174417yWREWiP2.jpg', 'image/jpeg', '1', '1', '2021-09-01 17:44:18', '192.168.1.23', '1', '2021-09-01 17:44:18', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544402835722649604', 'mceu_76990574091630489529255.jpg', '/202109/01174529pkX3seQX.jpg', 'image/jpeg', '1', '1', '2021-09-01 17:45:30', '192.168.1.23', '1', '2021-09-01 17:45:30', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544403027251347463', 'mceu_91457185101630489574724.jpg', '/202109/01174615WP90gdIo.jpg', 'image/jpeg', '1', '1', '2021-09-01 17:46:15', '192.168.1.23', '1', '2021-09-01 17:46:15', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544403406324154368', 'mceu_408327425111630489665237.jpg', '/202109/011747452F91NAvK.jpg', 'image/jpeg', '1', '1', '2021-09-01 17:47:46', '192.168.1.23', '1', '2021-09-01 17:47:46', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544403829344878592', 'mceu_845293614121630489766126.jpg', '/202109/01174926uKet7tng.jpg', 'image/jpeg', '1', '1', '2021-09-01 17:49:27', '192.168.1.23', '1', '2021-09-01 17:49:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544412302992392192', '3393346-febb469a95661cba.webp', '/202109/01182306voBB06Il.webp', 'image/webp', '1', '1', '2021-09-01 18:23:07', '192.168.1.23', '1', '2021-09-01 18:23:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544419997594075142', 'mceu_42923012111630493620879.png', '/202109/011853410c1dqYGv.png', 'image/png', '1', '1', '2021-09-01 18:53:41', '192.168.1.23', '1', '2021-09-01 18:53:41', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544449168059842563', 'mceu_69047500911630500575612.jpg', '/202109/01204936x2711mcl.jpg', 'image/jpeg', '1', '1', '2021-09-01 20:49:36', '192.168.1.23', '1', '2021-09-01 20:49:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1544449767434272777', 'mceu_63606070821630500718512.jpg', '/202109/01205159NiKJq5bZ.jpg', 'image/jpeg', '1', '1', '2021-09-01 20:51:59', '192.168.1.23', '1', '2021-09-01 20:51:59', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545213503308742662', 'lcZK5rQQzEK5r251aeNE.jpg', '/202109/03232647s4VYLwEX.jpg', 'image/jpeg', '1', '1', '2021-09-03 23:26:48', '192.168.1.23', '1', '2021-09-03 23:26:48', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545216361131327488', 'lcZK5rQQzEK5r251aeNE.jpg', '/202109/03233809mXWIWW3i.jpg', 'image/jpeg', '1', '1', '2021-09-03 23:38:09', '192.168.1.23', '1', '2021-09-03 23:38:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545220080497508363', 'lcZK5rQQzEK5r251aeNE.jpg', '/202109/03235255YRLdovgu.jpg', 'image/jpeg', '1', '1', '2021-09-03 23:52:56', '192.168.1.23', '1', '2021-09-03 23:52:56', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545222771068026884', 'lcZK5rQQzEK5r251aeNE.jpg', '/202109/04000337YZNOOEFx.jpg', 'image/jpeg', '1', '1', '2021-09-04 00:03:37', '192.168.1.23', '1', '2021-09-04 00:03:37', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545222831436644352', '562c11dfa9ec8a13f05ee624bca45988a2ecc0d9.jpeg', '/202109/04000351Tfxqjosp.jpeg', 'image/jpeg', '1', '1', '2021-09-04 00:03:52', '192.168.1.23', '1', '2021-09-04 00:03:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545222863686647814', 'b03533fa828ba61e0ea3f5acb6999b0c314e5913.jpeg', '/202109/040003595xcM0wTs.jpeg', 'image/jpeg', '1', '1', '2021-09-04 00:04:00', '192.168.1.23', '1', '2021-09-04 00:04:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545222901099839495', 'd62a6059252dd42a611dc866469c93b2c8eab812.jpeg', '/202109/04000408709PsC0q.jpeg', 'image/jpeg', '1', '1', '2021-09-04 00:04:08', '192.168.1.23', '1', '2021-09-04 00:04:08', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545222935530881025', 'aa18972bd40735fa7246332464fc03b50e240828.png', '/202109/04000416xQgQgiTs.png', 'image/png', '1', '1', '2021-09-04 00:04:17', '192.168.1.23', '1', '2021-09-04 00:04:17', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545472401567563787', 'mceu_53572828211630744533819.jpg', '/202109/04163534QZ8RNwuA.jpg', 'image/jpeg', '1', '1', '2021-09-04 16:35:34', '192.168.1.23', '1', '2021-09-04 16:35:34', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545473097712975873', 'mceu_2409736511630744699852.jpg', '/202109/04163819wSHOXTaM.jpg', 'image/jpeg', '1', '1', '2021-09-04 16:38:20', '192.168.1.23', '1', '2021-09-04 16:38:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545488257152368648', 'mceu_43273690811630748313938.jpg', '/202109/04173834UaO8H5hi.jpg', 'image/jpeg', '1', '1', '2021-09-04 17:38:34', '192.168.1.23', '1', '2021-09-04 17:38:34', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545489769819717634', 'mceu_69139117021630748674461.jpg', '/202109/041744342o5pmfTR.jpg', 'image/jpeg', '1', '1', '2021-09-04 17:44:35', '192.168.1.23', '1', '2021-09-04 17:44:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545506563808739329', 'mceu_86886984231630752678421.jpg', '/202109/04185118oZpFiPp9.jpg', 'image/jpeg', '1', '1', '2021-09-04 18:51:19', '192.168.1.23', '1', '2021-09-04 18:51:19', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1545506635971739649', 'mceu_74659088541630752696042.jpg', '/202109/04185136DNuAsjPM.jpg', 'image/jpeg', '1', '1', '2021-09-04 18:51:36', '192.168.1.23', '1', '2021-09-04 18:51:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701091613458432', '88.jpg', '/202109/10201135YqsgC7J0.jpg', 'image/jpeg', '1', '1547698179520774144', '2021-09-10 20:11:35', '192.168.1.23', '1547698179520774144', '2021-09-10 20:11:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701560872189961', '2f5ca0f1c9b6d02ea87df74fcc_560.jpg', '/202109/102013279T2uqrn5.jpg', 'image/jpeg', '1', '1547698179520774144', '2021-09-10 20:13:27', '192.168.1.23', '1547698179520774144', '2021-09-10 20:13:27', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701592270749703', '11.jpg', '/202109/10201334lfZZZhIP.jpg', 'image/jpeg', '1', '1547698179520774144', '2021-09-10 20:13:35', '192.168.1.23', '1547698179520774144', '2021-09-10 20:13:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701624050991113', '330625-140G502062028.jpg', '/202109/10201342KQ7TUvLR.jpg', 'image/jpeg', '1', '1547698179520774144', '2021-09-10 20:13:42', '192.168.1.23', '1547698179520774144', '2021-09-10 20:13:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701666350546953', '1.jpg', '/202109/10201352B99IKc4V.jpg', 'image/jpeg', '1', '1547698179520774144', '2021-09-10 20:13:52', '192.168.1.23', '1547698179520774144', '2021-09-10 20:13:52', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('1547701709300219911', 'bg_corner_tr.png', '/202109/102014020rrgsZvG.png', 'image/png', '1', '1547698179520774144', '2021-09-10 20:14:02', '192.168.1.23', '1547698179520774144', '2021-09-10 20:14:02', '192.168.1.23', 'normal', '0');

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
-- Records of wo_mail
-- ----------------------------
INSERT INTO `wo_mail` VALUES ('192743754746740744', 'wldos.com@88.com', 'yuanxi2@zhiletu.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"/user/active/id=192743746152611843\">激活账号</a>', '1', null, '192743746152611843', '2022-08-31 15:32:42', '192.168.1.23', '192743746152611843', '2022-08-31 15:32:43', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('192753339045953547', 'wldos.com@88.com', '306991142@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"/user/active/id=192753329898176517\">激活账号</a>', '1', null, '192753329898176517', '2022-08-31 16:10:47', '192.168.1.23', '192753329898176517', '2022-08-31 16:10:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('198204863532417028', 'wldos.com@88.com', '583716365@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"https://localhost/user/active/verify=198204851142443011\" target=\"_blank\">激活账号</a>https://localhost/user/active/verify=198204851142443011<br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '198204851142443011', '2022-09-15 17:13:12', '192.168.1.23', '198204851142443011', '2022-09-15 17:13:13', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('198575902070325253', 'wldos.com@88.com', '583716365@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"https://localhost/user/active/verify=198575893094514697\" target=\"_blank\">激活账号</a>https://localhost/user/active/verify=198575893094514697<br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '198575893094514697', '2022-09-16 17:47:34', '192.168.1.23', '198575893094514697', '2022-09-16 17:47:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('201496584638873602', 'wldos.com@88.com', '306991142@qq.com', '175118', '1', null, '1', '2022-09-24 19:13:19', '0.0.0.0', '1', '2022-09-24 19:13:21', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('201498246547947526', 'wldos.com@88.com', '306991142@qq.com', '990263', '1', null, '1', '2022-09-24 19:19:56', '0.0.0.0', '1', '2022-09-24 19:19:56', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('201499057000726536', 'wldos.com@88.com', '306991142@qq.com', '611075', '1', null, '1', '2022-09-24 19:23:09', '0.0.0.0', '1', '2022-09-24 19:23:09', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('201499812856250372', 'wldos.com@88.com', '306991142@qq.com', '908660', '1', null, '1', '2022-09-24 19:26:09', '0.0.0.0', '1', '2022-09-24 19:26:10', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('201500240285188098', 'wldos.com@88.com', '306991142@qq.com', '697574', '1', null, '1', '2022-09-24 19:27:51', '0.0.0.0', '1', '2022-09-24 19:27:52', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202192876419596291', 'wldos.com@88.com', '306991142@qq.com', '744354', '1', null, '1', '2022-09-26 17:20:08', '0.0.0.0', '1', '2022-09-26 17:20:09', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202193662423777291', 'wldos.com@88.com', '306991142@qq.com', '413146', '1', null, '1', '2022-09-26 17:23:16', '0.0.0.0', '1', '2022-09-26 17:23:16', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202194545240883208', 'wldos.com@88.com', '306991142@qq.com', '464642', '1', null, '1', '2022-09-26 17:26:46', '0.0.0.0', '1', '2022-09-26 17:26:47', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202194903371530251', 'wldos.com@88.com', '306991142@qq.com', '749578', '1', null, '1', '2022-09-26 17:28:12', '0.0.0.0', '1', '2022-09-26 17:28:12', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202199034555449353', 'wldos.com@88.com', '306991142@qq.com', '210083', '1', null, '1', '2022-09-26 17:44:37', '0.0.0.0', '1', '2022-09-26 17:44:38', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202201517105266694', 'wldos.com@88.com', '306991142@qq.com', '987710', '1', null, '1', '2022-09-26 17:54:28', '0.0.0.0', '1', '2022-09-26 17:54:29', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202204710828687360', 'wldos.com@88.com', '306991142@qq.com', '939156', '1', null, '1', '2022-09-26 18:07:10', '0.0.0.0', '1', '2022-09-26 18:07:12', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202205449097494533', 'wldos.com@88.com', '306991142@qq.com', '541129', '1', null, '1', '2022-09-26 18:10:06', '0.0.0.0', '1', '2022-09-26 18:10:06', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202209610958553091', 'wldos.com@88.com', '306991142@qq.com', '195776', '1', null, '1', '2022-09-26 18:26:38', '0.0.0.0', '1', '2022-09-26 18:26:39', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202240758027829249', 'wldos.com@88.com', '306991142@qq.com', '228319', '1', null, '1', '2022-09-26 20:30:24', '0.0.0.0', '1', '2022-09-26 20:30:25', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202241429774974985', 'wldos.com@88.com', '306991142@qq.com', '649294', '1', null, '1', '2022-09-26 20:33:04', '0.0.0.0', '1', '2022-09-26 20:33:05', '0.0.0.0', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('202264058024083462', 'wldos.com@88.com', '306991142@qq.com', '963991', '1', null, '1', '2022-09-26 22:02:59', '0.0.0.0', '1', '2022-09-26 22:03:01', '0.0.0.0', 'normal', '0');

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
-- Records of wo_oauth_login_user
-- ----------------------------
INSERT INTO `wo_oauth_login_user` VALUES ('214132160290537481', '214132159892078601', 'wechat', 'oTKkX6BqkQWVx2YmBUUNQYZZuJh4', null, '214132159892078601', '2022-10-29 16:02:35', '192.168.1.23', '214132159892078601', '2022-10-29 16:02:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_oauth_login_user` VALUES ('214858324877426692', '214858324143423489', 'wechat', 'oTKkX6BmQvjm7dtnItBcCLHBAQdE', null, '214858324143423489', '2022-10-31 16:08:07', '192.168.1.23', '214858324143423489', '2022-10-31 16:08:07', '192.168.1.23', 'normal', '0');

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
  `app_type` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `option_name` (`option_key`),
  KEY `options_app_type` (`app_type`)
) ENGINE=InnoDB AUTO_INCREMENT=253385974528786443 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of wo_options
-- ----------------------------
INSERT INTO `wo_options` VALUES ('1', 'oauth_login_wechat', null, '{\"appId\":\"zldfsdf23423\",\"appSecret\":\"51ea12330acslipwefjsdfesf0934434sf\",\"redirectUri\":\"http://www.wldos.com\",\"scope\":\"snsapi_login\",\"codeUri\":\"https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect\",\"accessTokenUri\":\"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code\",\"refreshTokenUri\":\"\",\"userInfoUri\":\"https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s\"}', null, null);
INSERT INTO `wo_options` VALUES ('45', 'default_group', null, 'freeuser', null, null);
INSERT INTO `wo_options` VALUES ('149', 'un_active_group', null, 'un_active', null, null);
INSERT INTO `wo_options` VALUES ('253373739874041856', 'oauth_login_qq', null, '{\"appId\":\"1sdf6\",\"appSecret\":\"7ertegerge\",\"redirectUri\":\"https://www.wldos.com\",\"scope\":\"get_user_info\",\"codeUri\":\"https://graph.qq.com/oauth2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=%s\",\"accessTokenUri\":\"https://graph.qq.com/oauth2.0/token?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s\",\"refreshTokenUri\":null,\"userInfoUri\":\"https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s\"}', null, null);
INSERT INTO `wo_options` VALUES ('253385974528786442', 'oauth_login_weibo', null, '{\"appId\":\"1324sdfds3\",\"appSecret\":\"3ertertert43d61c3caewtre964\",\"redirectUri\":\"https://www.wldos.com\",\"scope\":\"all\",\"codeUri\":\"https://api.weibo.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=%s\",\"accessTokenUri\":\"https://api.weibo.com/oauth2/access_token?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s\",\"refreshTokenUri\":null,\"userInfoUri\":\"https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s\"}', null, null);

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
-- Records of wo_org
-- ----------------------------
INSERT INTO `wo_org` VALUES ('1', 'admins', '管理员', null, 'platform', '0', '0', '0', '2', '1', '0', '2021-05-08 14:17:20', '0', '1', '2021-11-26 11:08:06', '192.168.1.23', 'normal', null);
INSERT INTO `wo_org` VALUES ('2', 'user', '会员', null, 'platform', '0', '0', '0', '3', '1', '0', '2021-05-08 14:18:51', '0', '0', '2021-05-08 14:18:59', '0', 'normal', null);
INSERT INTO `wo_org` VALUES ('200', 'freeuser', '普通会员', null, 'platform', '0', '0', '2', '1', '1', '0', '2021-05-08 14:32:48', '0', '0', '2021-05-08 14:32:56', '0', 'normal', '1');
INSERT INTO `wo_org` VALUES ('201', 'vip', '普通VIP', null, 'platform', '0', '0', '2', '2', '1', '0', '2021-05-08 14:34:35', '0', '0', '2021-05-08 14:34:43', '0', 'normal', null);
INSERT INTO `wo_org` VALUES ('300', 'un_active', '待激活用户', null, 'platform', '0', '0', '0', '3', '1', '0', '2022-09-15 20:20:13', null, '0', '2022-09-15 20:20:26', null, 'normal', null);
INSERT INTO `wo_org` VALUES ('91933461561655297', 'admin', '超级管理员', null, 'platform', '0', '0', '1', '1', '1', '1', '2021-11-26 11:08:35', '192.168.1.23', '1', '2021-11-26 11:08:35', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('93032343456628744', 'dev', '研发部', null, 'org', '0', '1508132284859596808', '0', '1', '1', '92829405966680072', '2021-11-29 11:55:09', '192.168.1.23', '1', '2022-02-09 11:32:31', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('247227424555319302', 'can_trust', '可信会员', null, 'role_org', '0', '0', '0', '4', '1', '1', '2023-01-28 23:51:21', '192.168.1.23', '1', '2023-01-28 23:51:21', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508971189611708427', '1011012345120105', '网络斗士（中国）科技无限责任有限公司', null, 'org', '100', '1508132284859596808', '0', '4', '1', '0', '2021-05-26 23:12:47', '127.0.0.1', '1', '2021-07-30 20:51:12', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508973076528414721', '101101234512010501', '市场研发部', null, 'org', '100', '1508132284859596808', '1508971189611708427', '1', '1', '0', '2021-05-26 23:20:17', '127.0.0.1', '0', '2021-05-26 23:20:17', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508973453109805063', '1011012345120105011', '销售顾问群', null, 'group', '300', '1508132284859596808', '1508973076528414721', '1', '1', '0', '2021-05-26 23:21:47', '127.0.0.1', '0', '2021-05-26 23:21:47', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508974093827489796', '1011012345120106', '科技攻关部', null, 'org', '100', '1508132284859596808', '1508971189611708427', '2', '1', '0', '2021-05-26 23:24:19', '127.0.0.1', '0', '2021-05-26 23:24:19', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508974496115769349', '101101234512010501', 'WLDOS改造世界系统项目组', null, 'team', '200', '1508132284859596808', '1508974093827489796', '1', '1', '0', '2021-05-26 23:25:55', '127.0.0.1', '0', '2021-05-26 23:25:55', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508975215187247108', '1011012345120100', '顶级想象力', null, 'circle', '400', '1508972512004456457', '0', '10', '1', '0', '2021-05-26 23:28:47', '127.0.0.1', '0', '2021-05-26 23:29:19', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508976067088138249', '10110123451201001', '套圈', null, 'circle', '400', '1508972512004456457', '1508975215187247108', '1', '1', '0', '2021-05-26 23:32:10', '127.0.0.1', '0', '2021-05-26 23:32:10', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1508976740328456200', '101101234512010011', '圈长', null, 'role_org', '100', '1508972831958548480', '1508976067088138249', '1', '1', '0', '2021-05-26 23:34:50', '127.0.0.1', '0', '2021-05-26 23:34:50', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1525968690709446657', 'tadmin', '租户管理员', null, 'role_org', '0', '0', '0', '1', '1', '1', '2021-07-12 20:54:47', '192.168.1.23', '1', '2021-07-30 20:53:08', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_org` VALUES ('1526214941484957699', 'badmin', '二级管理员', null, 'platform', '0', '0', '1', '1', '1', '1', '2021-07-13 13:13:18', '192.168.1.23', '1', '2021-07-13 13:13:18', '192.168.1.23', 'normal', '1');

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
-- Records of wo_org_user
-- ----------------------------
INSERT INTO `wo_org_user` VALUES ('1', '100', null, '200', '0', '0', '1', '0', '0', '2021-05-08 21:41:13', '0', '0', '2021-05-08 21:41:20', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('79070766428766213', '79070766156136451', null, '200', '0', '0', '1', '79070766156136451', '192.168.1.23', '2021-10-21 23:16:50', '79070766156136451', '192.168.1.23', '2021-10-21 23:16:50', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('79725517352910853', '1502777260587532299', null, '200', '0', '0', '1', '1', '192.168.1.23', '2021-10-23 18:38:35', '1', '192.168.1.23', '2021-10-23 18:38:35', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('81514196123697153', '81514195872038918', null, '200', '0', '0', '1', '81514195872038918', '192.168.1.23', '2021-10-28 17:06:09', '81514195872038918', '192.168.1.23', '2021-10-28 17:06:09', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('81836119110107143', '81836118845865995', null, '200', '0', '0', '1', '81836118845865995', '192.168.1.23', '2021-10-29 14:25:21', '81836118845865995', '192.168.1.23', '2021-10-29 14:25:21', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('91933652163411973', '1', null, '91933461561655297', '0', '0', '1', '1', '192.168.1.23', '2021-11-26 11:09:21', '1', '192.168.1.23', '2021-11-26 11:09:21', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('91933738134061067', '100', null, '1526214941484957699', '0', '0', '1', '1', '192.168.1.23', '2021-11-26 11:09:41', '1', '192.168.1.23', '2021-11-26 11:09:41', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('92829406042177536', '92829405966680072', null, '200', '0', '0', '1', '92829405966680072', '192.168.1.23', '2021-11-28 22:28:45', '92829405966680072', '192.168.1.23', '2021-11-28 22:28:45', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('92829651706757129', '92829405966680072', '1508132284859596808', '1525968690709446657', '0', '0', '1', '1', '192.168.1.23', '2021-11-28 22:29:44', '1', '192.168.1.23', '2021-11-28 22:29:44', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('92832497399414789', '92832497370054659', null, '200', '0', '0', '1', '92832497370054659', '192.168.1.23', '2021-11-28 22:41:02', '92832497370054659', '192.168.1.23', '2021-11-28 22:41:02', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('119132584031207428', '119132574443028481', null, '200', '0', '0', '1', '119132574443028481', '192.168.1.23', '2022-02-09 12:28:11', '119132574443028481', '192.168.1.23', '2022-02-09 12:28:11', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('119560316967895046', '119560179352780806', null, '200', '0', '0', '1', '119560179352780806', '192.168.1.23', '2022-02-10 16:47:51', '119560179352780806', '192.168.1.23', '2022-02-10 16:47:51', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('189862995023282184', '189862984948563971', null, '200', '0', '0', '1', '189862984948563971', '192.168.1.23', '2022-08-23 16:45:35', '189862984948563971', '192.168.1.23', '2022-08-23 16:45:35', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('191677102189887493', '191677093767725063', null, '200', '0', '0', '1', '191677093767725063', '192.168.1.23', '2022-08-28 16:54:12', '191677093767725063', '192.168.1.23', '2022-08-28 16:54:12', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('192743753635250180', '192743746152611843', null, '200', '0', '0', '1', '192743746152611843', '192.168.1.23', '2022-08-31 15:32:42', '192743746152611843', '192.168.1.23', '2022-08-31 15:32:42', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('192753338718797834', '192753329898176517', null, '200', '0', '0', '1', '192753329898176517', '192.168.1.23', '2022-08-31 16:10:47', '192753329898176517', '192.168.1.23', '2022-08-31 16:10:47', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('198204860005007362', '198204851142443011', null, '200', '0', '0', '1', '198204851142443011', '192.168.1.23', '2022-09-15 17:13:11', '198204851142443011', '192.168.1.23', '2022-09-15 17:13:11', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('198575900266774535', '198575893094514697', null, '200', '0', '0', '1', '198575893094514697', '192.168.1.23', '2022-09-16 17:47:34', '198575893094514697', '192.168.1.23', '2022-09-16 17:47:34', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('214120914220662792', '0', null, '300', '0', '0', '1', '0', '192.168.1.23', '2022-10-29 15:17:54', '0', '192.168.1.23', '2022-10-29 15:17:54', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('214132159988547595', '214132159892078601', null, '200', '0', '0', '1', '214132159892078601', '192.168.1.23', '2022-10-29 16:02:35', '214132159892078601', '192.168.1.23', '2022-10-29 16:02:35', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('214858324277641220', '214858324143423489', null, '200', '0', '0', '1', '214858324143423489', '192.168.1.23', '2022-10-31 16:08:06', '214858324143423489', '192.168.1.23', '2022-10-31 16:08:06', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('247228766472552455', '1', '0', '247227424555319302', '0', '0', '1', '1', '192.168.1.23', '2023-01-28 23:56:41', '1', '192.168.1.23', '2023-01-28 23:56:41', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1502723278259273735', '1502723278108278788', null, '200', '0', '0', '1', '1502723278108278788', '127.0.0.1', '2021-05-09 17:25:49', '1502723278108278788', '127.0.0.1', '2021-05-09 17:25:49', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502726588278161413', '1502726588102000651', null, '200', '0', '0', '1', '1502726588102000651', '127.0.0.1', '2021-05-09 17:38:58', '1502726588102000651', '127.0.0.1', '2021-05-09 17:38:58', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502739709487136769', '1502739709344530437', null, '200', '0', '0', '1', '1502739709344530437', '127.0.0.1', '2021-05-09 18:31:06', '1502739709344530437', '127.0.0.1', '2021-05-09 18:31:06', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502742261293301768', '1502742261263941642', null, '200', '0', '0', '1', '1502742261263941642', '127.0.0.1', '2021-05-09 18:41:15', '1502742261263941642', '127.0.0.1', '2021-05-09 18:41:15', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502770377218768901', '1502770377176825865', null, '200', '0', '0', '1', '1502770377176825865', '127.0.0.1', '2021-05-09 20:32:58', '1502770377176825865', '127.0.0.1', '2021-05-09 20:32:58', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502773236945567747', '1502773236924596235', null, '200', '0', '0', '1', '1502773236924596235', '127.0.0.1', '2021-05-09 20:44:20', '1502773236924596235', '127.0.0.1', '2021-05-09 20:44:20', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502777813191278600', '1502777813036089350', null, '200', '0', '0', '1', '1502777813036089350', '127.0.0.1', '2021-05-09 21:02:31', '1502777813036089350', '127.0.0.1', '2021-05-09 21:02:31', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502783343708258304', '1502783343481765897', null, '200', '0', '0', '1', '1502783343481765897', '127.0.0.1', '2021-05-09 21:24:30', '1502783343481765897', '127.0.0.1', '2021-05-09 21:24:30', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1502803624921317382', '1502803624724185094', null, '200', '0', '0', '1', '1502803624724185094', '127.0.0.1', '2021-05-09 22:45:05', '1502803624724185094', '127.0.0.1', '2021-05-09 22:45:05', 'normal', '1');
INSERT INTO `wo_org_user` VALUES ('1509226657512865803', '1502739709344530437', null, '201', '0', '0', '1', '0', '127.0.0.1', '2021-05-27 16:07:55', '0', '127.0.0.1', '2021-05-27 16:07:55', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1521171389918920713', '1521171388547383299', null, '200', '0', '0', '1', '1521171388547383299', '192.168.1.23', '2021-06-29 15:12:01', '1521171388547383299', '192.168.1.23', '2021-06-29 15:12:01', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1526291590620561411', '100', null, '1508974093827489796', '100', '1508132284859596808', '1', '1', '192.168.1.23', '2021-07-13 18:17:52', '1', '192.168.1.23', '2021-07-13 18:17:52', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1533208873796288519', '1502742261263941642', null, '1508975215187247108', '400', '1508972512004456457', '1', '1', '192.168.1.23', '2021-08-01 20:24:41', '1', '192.168.1.23', '2021-08-01 20:24:41', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547396545381056516', '1547396545116815364', null, '200', '0', '0', '1', '1547396545116815364', '192.168.1.23', '2021-09-10 00:01:26', '1547396545116815364', '192.168.1.23', '2021-09-10 00:01:26', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547627860646871050', '1547627860437155848', null, '200', '0', '0', '1', '1547627860437155848', '192.168.1.23', '2021-09-10 15:20:36', '1547627860437155848', '192.168.1.23', '2021-09-10 15:20:36', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547650818123677699', '1547650817649721344', null, '200', '0', '0', '1', '1547650817649721344', '192.168.1.23', '2021-09-10 16:51:49', '1547650817649721344', '192.168.1.23', '2021-09-10 16:51:49', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547663029638447111', '1547663029483257866', null, '200', '0', '0', '1', '1547663029483257866', '192.168.1.23', '2021-09-10 17:40:20', '1547663029483257866', '192.168.1.23', '2021-09-10 17:40:20', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547670976733036555', '1547670975575408643', null, '200', '0', '0', '1', '1547670975575408643', '192.168.1.23', '2021-09-10 18:11:55', '1547670975575408643', '192.168.1.23', '2021-09-10 18:11:55', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547691631272706053', '1547691630899413002', null, '200', '0', '0', '1', '1547691630899413002', '192.168.1.23', '2021-09-10 19:34:00', '1547691630899413002', '192.168.1.23', '2021-09-10 19:34:00', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547693780165640200', '1547693779586826246', null, '200', '0', '0', '1', '1547693779586826246', '192.168.1.23', '2021-09-10 19:42:32', '1547693779586826246', '192.168.1.23', '2021-09-10 19:42:32', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547695225283723266', '1547695225065619462', null, '200', '0', '0', '1', '1547695225065619462', '192.168.1.23', '2021-09-10 19:48:16', '1547695225065619462', '192.168.1.23', '2021-09-10 19:48:16', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547696559332442119', '1547696496472408073', null, '200', '0', '0', '1', '1547696496472408073', '192.168.1.23', '2021-09-10 19:53:37', '1547696496472408073', '192.168.1.23', '2021-09-10 19:53:37', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('1547698181106221065', '1547698179520774144', null, '200', '0', '0', '1', '1547698179520774144', '192.168.1.23', '2021-09-10 20:00:01', '1547698179520774144', '192.168.1.23', '2021-09-10 20:00:01', 'normal', '0');

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
-- Records of wo_resource
-- ----------------------------
INSERT INTO `wo_resource` VALUES ('100', 'home', '首页', '/', 'home', 'menu', 'GET', '_self', '1506113043159498757', '0', '1', '1', '平台门户，内容中心', '0', '0', '2021-05-08 13:44:35', '1', '192.168.1.23', '2023-01-27 22:41:31', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('72187658739826691', 'chapter', '元素管理', '/admin/cms/pub/chapter', 'list', 'admin_menu', 'GET', '_self', '1504618238889869317', '1506128956323708934', '1', '1', '对作品的内容管理，包括篇章、剧集或附件等', '1', '192.168.1.23', '2021-10-02 23:25:49', '1', '192.168.1.23', '2023-01-27 18:09:00', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('72188271301148676', 'book', '作品管理', '/admin/cms/pub/book', 'list', 'admin_menu', 'GET', '_self', '1504618238889869317', '1506128956323708934', '2', '1', '作品是指帖子、图片、视频、音频、系列视频、系列音频、电子书等。', '1', '192.168.1.23', '2021-10-02 23:28:15', '1', '192.168.1.23', '2023-01-27 18:10:30', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91282165825454087', 'app-dels', '批量删除', '/admin/sys/app/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506107866432061443', '3', '1', '批量删除多个应用', '1', '192.168.1.23', '2021-11-24 16:00:34', '1', '192.168.1.23', '2021-11-24 16:00:34', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91283767764369409', 'app-dom', '删除域应用', '/admin/sys/domain/appDel', null, 'admin_button', 'DELETE', null, '1506005013902311434', '1506107866432061443', '4', '1', '从某个域下删除已添加的某个应用', '1', '192.168.1.23', '2021-11-24 16:06:56', '1', '192.168.1.23', '2021-11-24 16:06:56', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91284578737242117', 'app-config', '更新配置', '/admin/sys/app/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506107866432061443', '5', '1', '更新某个应用的配置', '1', '192.168.1.23', '2021-11-24 16:10:10', '1', '192.168.1.23', '2021-11-24 16:10:10', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91316759807311877', 'res-add', '新增', '/admin/sys/res/add', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506122443605590022', '1', '1', '新增资源', '1', '192.168.1.23', '2021-11-24 18:18:02', '1', '192.168.1.23', '2023-02-04 04:31:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91651120842850304', 'res-del', '删除', '/admin/sys/res/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506122443605590022', '2', '1', '删除一项', '1', '192.168.1.23', '2021-11-25 16:26:40', '1', '192.168.1.23', '2023-02-04 04:31:57', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91651539862208517', 'res-dels', '批量删除资源', '/admin/sys/res/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506122443605590022', '3', '1', '批量删除资源', '1', '192.168.1.23', '2021-11-25 16:28:20', '1', '192.168.1.23', '2021-11-25 16:28:20', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91651964644540418', 'res-update', '更新资源', '/admin/sys/res/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506122443605590022', '4', '1', '更新资源配置', '1', '192.168.1.23', '2021-11-25 16:30:01', '1', '192.168.1.23', '2021-11-25 16:30:01', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91652500538179591', 'res-domain', '删除域资源', '/admin/sys/domain/resDel', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506122443605590022', '5', '1', '删除某个域下的资源', '1', '192.168.1.23', '2021-11-25 16:32:09', '1', '192.168.1.23', '2021-11-25 16:32:09', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91657329280991232', 'dom-app', '添加域应用', '/admin/sys/domain/app', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1532485035156488196', '1', '1', '添加应用到域', '1', '192.168.1.23', '2021-11-25 16:51:20', '1', '192.168.1.23', '2023-02-04 04:31:02', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91659367716929536', 'dom-res', '添加域资源', '/admin/sys/domain/res', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1532485035156488196', '2', '1', '添加域资源', '1', '192.168.1.23', '2021-11-25 16:59:26', '1', '192.168.1.23', '2023-02-04 04:32:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91681269747466245', 'dom-del', '删除域名', '/admin/sys/domain/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1532485035156488196', '3', '1', '删除一项', '1', '192.168.1.23', '2021-11-25 18:26:28', '1', '192.168.1.23', '2021-11-25 18:33:13', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91681543228669952', 'dom-dels', '批量删除域名', '/admin/sys/domain/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1532485035156488196', '4', '1', '批量删除域名', '1', '192.168.1.23', '2021-11-25 18:27:33', '1', '192.168.1.23', '2021-11-25 18:27:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91682380176867329', 'dom-add', '新增域名', '/admin/sys/domain/add', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1532485035156488196', '5', '1', '新增一个域名', '1', '192.168.1.23', '2021-11-25 18:30:53', '1', '192.168.1.23', '2021-11-25 18:30:53', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91683279729246214', 'dom-update', '更新域名', '/admin/sys/domain/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1532485035156488196', '6', '1', '更新域名配置', '1', '192.168.1.23', '2021-11-25 18:34:27', '1', '192.168.1.23', '2021-11-25 18:34:27', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91683874691268617', 'role-update', '更新角色', '/admin/sys/role/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506125438066016267', '2', '1', '更新一个角色', '1', '192.168.1.23', '2021-11-25 18:36:49', '1', '192.168.1.23', '2021-11-25 18:36:49', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91684261066358788', 'role-del', '删除角色', '/admin/sys/role/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506125438066016267', '3', '1', '删除一个角色', '1', '192.168.1.23', '2021-11-25 18:38:21', '1', '192.168.1.23', '2021-11-25 18:38:21', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91684501538390022', 'role-dels', '批量删除角色', '/admin/sys/role/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506125438066016267', '4', '1', '批量删除角色', '1', '192.168.1.23', '2021-11-25 18:39:19', '1', '192.168.1.23', '2021-11-25 18:39:19', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91684884843249671', 'role-auth', '角色授权', '/admin/sys/role/auth', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506125438066016267', '5', '1', '给角色授予权限', '1', '192.168.1.23', '2021-11-25 18:40:50', '1', '192.168.1.23', '2021-11-25 18:40:50', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91685236665663490', 'com-update', '更新租户', '/admin/sys/com/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509177915053096962', '2', '1', '更新租户', '1', '192.168.1.23', '2021-11-25 18:42:14', '1', '192.168.1.23', '2023-02-04 04:33:00', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91685468166078466', 'com-del', '删除租户', '/admin/sys/com/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509177915053096962', '3', '1', '删除租户', '1', '192.168.1.23', '2021-11-25 18:43:09', '1', '192.168.1.23', '2021-11-25 18:43:09', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91685770244046853', 'com-dels', '批量删除租户', '/admin/sys/com/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509177915053096962', '5', '1', '批量删除租户', '1', '192.168.1.23', '2021-11-25 18:44:21', '1', '192.168.1.23', '2021-11-25 18:44:21', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91686165863383045', 'arch-del', '删除体系', '/admin/sys/arch/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509184818118311946', '2', '1', '删除体系', '1', '192.168.1.23', '2021-11-25 18:45:55', '1', '192.168.1.23', '2021-11-25 18:45:55', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91688588170412035', 'arch-dels', '批量删除体系', '/admin/sys/arch/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509184818118311946', '3', '1', '批量删除体系', '1', '192.168.1.23', '2021-11-25 18:55:33', '1', '192.168.1.23', '2021-11-25 18:55:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91688861186048007', 'arch-update', '更新体系', '/admin/sys/arch/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509184818118311946', '4', '1', '更新体系', '1', '192.168.1.23', '2021-11-25 18:56:38', '1', '192.168.1.23', '2021-11-25 18:56:38', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91689150362337290', 'org-del', '删除组织', '/admin/sys/org/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509211664167911432', '2', '1', '删除组织', '1', '192.168.1.23', '2021-11-25 18:57:47', '1', '192.168.1.23', '2021-11-25 18:57:47', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91689774567047170', 'org-dels', '批量删除组织', '/admin/sys/org/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509211664167911432', '3', '1', '批量删除组织', '1', '192.168.1.23', '2021-11-25 19:00:16', '1', '192.168.1.23', '2021-11-25 19:00:16', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91690096047865861', 'org-update', '更新组织', '/admin/sys/org/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509211664167911432', '4', '1', '更新组织', '1', '192.168.1.23', '2021-11-25 19:01:32', '1', '192.168.1.23', '2021-11-25 19:01:32', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91690561644969994', 'org-auth', '组织授权', '/admin/sys/org/auth', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509211664167911432', '5', '1', '组织授权', '1', '192.168.1.23', '2021-11-25 19:03:23', '1', '192.168.1.23', '2021-11-25 19:03:23', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91691322072285184', 'user-del', '删除用户', '/admin/sys/user/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509212249457868808', '2', '1', '删除用户', '1', '192.168.1.23', '2021-11-25 19:06:25', '1', '192.168.1.23', '2023-02-04 04:32:20', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91691541182726152', 'user-dels', '批量删除用户', '/admin/sys/user/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509212249457868808', '3', '1', '批量删除用户', '1', '192.168.1.23', '2021-11-25 19:07:17', '1', '192.168.1.23', '2021-11-25 19:07:17', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91692030788026373', 'user-org', '删除组织成员', '/admin/sys/org/staffDel', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1509212249457868808', '4', '1', '从组织中删除成员', '1', '192.168.1.23', '2021-11-25 19:09:14', '1', '192.168.1.23', '2021-11-25 19:09:14', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91692305787568136', 'user-update', '更新用户', '/admin/sys/user/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509212249457868808', '5', '1', '更新用户信息', '1', '192.168.1.23', '2021-11-25 19:10:19', '1', '192.168.1.23', '2021-11-25 19:10:19', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91692604820471817', 'user-pass', '修改用户密码', '/admin/sys/user/passwd4admin', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1509212249457868808', '6', '1', '修改用户密码', '1', '192.168.1.23', '2021-11-25 19:11:31', '1', '192.168.1.23', '2021-11-26 12:49:14', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91693516968345605', 'cat-del', '删除分类', '/admin/cms/category/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1520374289564090377', '2', '1', '删除分类', '1', '192.168.1.23', '2021-11-25 19:15:08', '1', '192.168.1.23', '2021-11-25 19:15:08', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91693753137020928', 'cat-dels', '批量删除分类', '/admin/cms/category/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1520374289564090377', '3', '1', '批量删除分类', '1', '192.168.1.23', '2021-11-25 19:16:04', '1', '192.168.1.23', '2021-11-25 19:16:04', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91694018015707139', 'cat-update', '更新分类', '/admin/cms/category/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1520374289564090377', '4', '1', '更新分类', '1', '192.168.1.23', '2021-11-25 19:17:07', '1', '192.168.1.23', '2021-11-25 19:17:07', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91694308215406602', 'tag-del', '删除标签', '/admin/cms/category/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1520466841923403786', '2', '1', '删除标签', '1', '192.168.1.23', '2021-11-25 19:18:17', '1', '192.168.1.23', '2021-11-25 19:18:17', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91694508451479553', 'tag-dels', '批量删除标签', '/admin/cms/category/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1520466841923403786', '3', '1', '批量删除标签', '1', '192.168.1.23', '2021-11-25 19:19:04', '1', '192.168.1.23', '2021-11-25 19:19:04', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91696827939340293', 'tag-update', '更新标签', '/admin/cms/category/update', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1520466841923403786', '4', '1', '更新标签', '1', '192.168.1.23', '2021-11-25 19:28:17', '1', '192.168.1.23', '2021-11-25 19:28:17', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91697439556943878', 'cha-off', '下线作品', '/admin/cms/post/offline', null, 'admin_button', 'POST', '_self', '1506005013902311434', '72187658739826691', '2', '1', '下线一个作品', '1', '192.168.1.23', '2021-11-25 19:30:43', '1', '192.168.1.23', '2021-11-25 19:30:43', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91697684151975939', 'cha-del', '删除作品', '/admin/cms/post/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '72187658739826691', '3', '1', null, '1', '192.168.1.23', '2021-11-25 19:31:42', '1', '192.168.1.23', '2021-11-25 19:31:42', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91697876372733961', 'cha-dels', '批量删除作品', '/admin/cms/post/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '72187658739826691', '4', '1', null, '1', '192.168.1.23', '2021-11-25 19:32:27', '1', '192.168.1.23', '2021-11-25 19:32:27', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91698261745385474', 'book-off', '下线合集', '/admin/cms/post/offline', null, 'admin_button', 'POST', '_self', '1506005013902311434', '72188271301148676', '2', '1', null, '1', '192.168.1.23', '2021-11-25 19:33:59', '1', '192.168.1.23', '2021-11-25 19:33:59', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91698429899227137', 'book-del', '删除合集', '/admin/cms/post/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '72188271301148676', '3', '1', null, '1', '192.168.1.23', '2021-11-25 19:34:39', '1', '192.168.1.23', '2021-11-25 19:34:39', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('91698614343745546', 'book-dels', '批量删除合集', '/admin/cms/post/deletes', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '72188271301148676', '4', '1', null, '1', '192.168.1.23', '2021-11-25 19:35:23', '1', '192.168.1.23', '2021-11-25 19:35:23', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('93441238276685825', 'resAdmin', '功能管理', '/admin/res', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '2', '1', '应用、资源统一管理应用、资源统一管理应用、资源统一管理', '1', '192.168.1.23', '2021-11-30 14:59:57', '1', '192.168.1.23', '2022-09-29 20:10:42', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('93474930835505163', 'auth', '权限管理', '/admin/auth', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '3', '1', '角色权限管理', '1', '192.168.1.23', '2021-11-30 17:13:50', '1', '192.168.1.23', '2021-11-30 17:13:50', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('93481427766263818', 'organ', '组织管理', '/admin/organ', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '4', '1', '租户、体系、组织与用户', '1', '192.168.1.23', '2021-11-30 17:39:39', '1', '192.168.1.23', '2021-11-30 17:39:39', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('93529533534879755', 'dom', '领域管理', '/admin/dom', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '5', '1', '业务分类、领域划分', '1', '192.168.1.23', '2021-11-30 20:50:48', '1', '192.168.1.23', '2021-11-30 20:50:48', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('127144910604910596', 'front', '用户菜单', '/admin/res/front', 'smile', 'admin_menu', 'GET', '_self', '1506005013902311434', '93441238276685825', '4', '1', '用户菜单管理', '1', '192.168.1.23', '2022-03-03 15:06:19', '1', '192.168.1.23', '2022-03-03 15:09:10', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('129381329150853120', 'info_article_blog', 'IT|互联网', '/info/category/blog', 'smile', 'menu', 'GET', '_self', '1504618238889869317', '1542170368806666244', '21', '0', null, '1', '192.168.1.23', '2022-03-09 19:13:03', '1', '192.168.1.23', '2023-02-03 02:20:37', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('151359177717628933', 'infoAdmin', '信息管理', '/admin/cms/pub/info', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506128052832878593', '8', '1', '分类信息管理。', '1', '192.168.1.23', '2022-05-09 10:45:10', '1', '192.168.1.23', '2023-01-24 20:37:43', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('152162057265528840', 'commentAdmin', '评论管理', '/admin/cms/comment', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506128052832878593', '9', '1', null, '1', '192.168.1.23', '2022-05-11 15:55:31', '1', '192.168.1.23', '2022-05-11 15:55:31', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('156908317952360451', 'info_article_blog', '信息', '/info/category/ruanjian', 'smile', 'menu', 'GET', '_blank', '1504617964850823176', '0', '4', '1', '分类信息发布', '1', '192.168.1.23', '2022-05-24 18:15:28', '1', '192.168.1.23', '2023-02-08 00:18:42', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('203336650390683654', 'sysReg', '版本信息', '/admin/sys/reg', 'smile', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '2', '1', '当前系统版本信息和license授权购买', '1', '192.168.1.23', '2022-09-29 21:05:05', '1', '192.168.1.23', '2022-09-29 21:05:05', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('220685278113349639', 'oauth', '社会化登录', '/admin/sys/oauth', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '3', '1', '社会化登录配置', '1', '192.168.1.23', '2022-11-16 18:02:20', '1', '192.168.1.23', '2022-11-30 19:07:47', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('246158602809294853', 'opensource', '开源社区', 'https://gitee.com/wldos/wldos', null, 'menu', 'GET', '_blank', '1533901932104171527', '1533950643823886345', '1', '1', null, '1', '192.168.1.23', '2023-01-26 01:04:14', '1', '192.168.1.23', '2023-01-26 01:04:14', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('246159055248867338', 'wldos', '演示环境', 'https://www.wldos.com/user/login?redirect=http%3A%2F%2Fwldos.com%2Fadmin%2Fres%2Fapp', null, 'menu', 'GET', '_blank', '1533901932104171527', '1533950643823886345', '2', '1', '官网', '1', '192.168.1.23', '2023-01-26 01:06:02', '1', '192.168.1.23', '2023-01-26 01:06:02', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('246163063116054536', 'wldos-help', '平台手册', 'https://www.wldos.com/archives-109766247429357571.html', null, 'menu', 'GET', '_blank', '1533901932104171527', '1533950643823886345', '3', '1', null, '1', '192.168.1.23', '2023-01-26 01:21:58', '1', '192.168.1.23', '2023-01-26 01:21:58', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1506101733801771011', 'sys', '系统管理', '/admin/sys', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '1', '1', '系统管理', '0', '127.0.0.1', '2021-05-19 01:10:35', '1', '192.168.1.23', '2021-08-18 14:29:41', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506107866432061443', 'app', '应用管理', '/admin/res/app', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93441238276685825', '1', '1', 'SaaS平台上的独立应用', '0', '127.0.0.1', '2021-05-19 01:34:57', '1', '192.168.1.23', '2021-11-30 17:08:06', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506122443605590022', 'res', '资源管理', '/admin/res/res', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93441238276685825', '3', '1', '菜单、操作、服务、静态资源', '0', '127.0.0.1', '2021-05-19 02:32:53', '1', '192.168.1.23', '2021-11-30 17:08:16', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506125438066016267', 'role', '角色管理', '/admin/auth/role', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93474930835505163', '4', '1', '业务场景细分的资源集', '0', '127.0.0.1', '2021-05-19 02:44:47', '1', '192.168.1.23', '2021-11-30 17:25:58', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506126590341988354', 'app-del', '删除', '/admin/sys/app/delete', null, 'admin_button', 'DELETE', '_self', '1506005013902311434', '1506107866432061443', '2', '1', '逻辑删除一个应用', '0', '127.0.0.1', '2021-05-19 02:49:22', '1', '192.168.1.23', '2023-02-04 04:32:45', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506126989799112705', 'app-add', '新建', '/admin/sys/app/add', null, 'admin_button', 'POST', '_self', '1506005013902311434', '1506107866432061443', '1', '1', '新增一个应用', '0', '127.0.0.1', '2021-05-19 02:50:57', '1', '192.168.1.23', '2023-02-04 04:31:19', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506127499163779081', 'infoAdd', '信息发布', '/info/add', 'list', 'button', 'POST', '_self', '1504617964850823176', '1506128052832878593', '4', '1', '供求信息发布', '0', '127.0.0.1', '2021-05-19 02:52:58', '1', '192.168.1.23', '2021-09-04 12:50:01', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506128052832878593', 'cms', '内容管理', '/admin/cms', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '0', '20', '1', '内容管理系统', '0', '127.0.0.1', '2021-05-19 02:55:10', '1', '192.168.1.23', '2021-07-13 21:50:24', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1506128956323708934', 'pub', '创作管理', '/admin/cms/pub', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506128052832878593', '3', '1', '内容管理：篇章管理、合集管理。', '0', '127.0.0.1', '2021-05-19 02:58:46', '1', '192.168.1.23', '2023-01-24 20:32:02', 'normal', '0');
INSERT INTO `wo_resource` VALUES ('1509177915053096962', 'com', '租户管理', '/admin/organ/com', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93481427766263818', '1', '1', '租户是独立的法人主体。', '0', '127.0.0.1', '2021-05-27 12:54:14', '1', '192.168.1.23', '2023-01-14 01:49:23', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509179615117754368', 'admin', '系统', '/admin', 'list', 'menu', 'GET', '_self', '1506005013902311434', '0', '20', '1', '系统管理的入口菜单', '0', '127.0.0.1', '2021-05-27 13:00:59', '1', '192.168.1.23', '2023-01-26 01:00:41', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509184818118311946', 'arch', '体系管理', '/admin/organ/arch', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93481427766263818', '2', '1', '定义体系结构的设置', '0', '127.0.0.1', '2021-05-27 13:21:40', '1', '192.168.1.23', '2023-01-14 01:49:34', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509211664167911432', 'org', '机构管理', '/admin/organ/org', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93481427766263818', '3', '1', '组织机构管理', '0', '127.0.0.1', '2021-05-27 15:08:21', '1', '192.168.1.23', '2023-01-14 01:49:44', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1509212249457868808', 'user', '用户管理', '/admin/organ/user', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93481427766263818', '4', '1', '注册用户管理', '0', '127.0.0.1', '2021-05-27 15:10:40', '1', '192.168.1.23', '2023-01-14 01:49:52', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511494161530535947', 'user', '用户配置', '/user', 'list', 'webServ', 'GET', '_self', '1511494438434291716', '0', '7', '1', '前端用户设置', '1', '127.0.0.1', '2021-06-02 22:18:10', '1', '127.0.0.1', '2021-06-02 22:21:39', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511495802770079746', 'userset', '个人设置', '/user/conf', 'list', 'button', 'POST', '_self', '1511494438434291716', '1511494161530535947', '1', '1', null, '1', '127.0.0.1', '2021-06-02 22:24:42', '1', '127.0.0.1', '2021-06-02 22:27:53', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511496878801993736', 'upavatar', '头像上传', '/user/uploadAvatar', 'list', 'button', 'POST', '_self', '1511494438434291716', '1511494161530535947', '2', '1', null, '1', '127.0.0.1', '2021-06-02 22:28:58', '1', '127.0.0.1', '2021-06-02 22:28:58', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1511736612090462209', 'curAccount', '账户信息', '/user/curAccount', 'list', 'button', 'GET', '_self', '1511494438434291716', '1511494161530535947', '3', '1', '个人基本信息查询', '1', '127.0.0.1', '2021-06-03 14:21:35', '1', '127.0.0.1', '2021-06-03 14:21:35', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1518555863791091716', 'adminMenu', '获取管理菜单', '/admin/sys/user/adminMenu', 'list', 'webServ', 'GET', '_self', '1506005013902311434', '1509212249457868808', '1', '1', '游客访问时，跳转到登陆', '1', '127.0.0.1', '2021-06-22 09:58:51', '1', '127.0.0.1', '2021-06-22 09:58:51', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1520374289564090377', 'category', '分类管理', '/admin/dom/category', 'list', 'admin_menu', 'GET', '_self', '1504618238889869317', '93529533534879755', '1', '1', '业务分类管理', '1', '127.0.0.1', '2021-06-27 10:24:38', '1', '192.168.1.23', '2021-11-30 20:56:03', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1520466841923403786', 'tag', '标签管理', '/admin/dom/tag', 'list', 'admin_menu', 'GET', '_self', '1504618238889869317', '93529533534879755', '2', '1', '标签管理', '1', '127.0.0.1', '2021-06-27 16:32:24', '1', '192.168.1.23', '2021-11-30 20:55:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1522661730694119427', 'infoUpload', '附件上传', '/info/upload', null, 'webServ', 'POST', '_self', '1504617964850823176', '1506128052832878593', '5', '1', '信息发布时上传封面图片等', '1', '192.168.1.23', '2021-07-03 17:54:06', '1', '192.168.1.23', '2021-09-04 12:51:20', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1522997390516862983', 'space', '创作', '/space/book', 'list', 'menu', 'GET', '_blank', '1504618238889869317', '0', '6', '1', '内容创作', '1', '192.168.1.23', '2021-07-04 16:07:54', '1', '192.168.1.23', '2023-01-25 11:02:04', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1523270396090695683', 'cateSel', '选择分类信息', '/info/category/tree', 'list', 'webServ', 'GET', '_self', '1504617964850823176', '1506128052832878593', '6', '1', '信息发布第一步', '1', '192.168.1.23', '2021-07-05 10:12:43', '1', '192.168.1.23', '2021-09-04 13:03:42', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1528063986125946885', 'config', '系统参数', '/admin/sys/options', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '1506101733801771011', '1', '1', '系统配置项', '1', '192.168.1.23', '2021-07-18 15:40:44', '1', '192.168.1.23', '2021-07-18 15:41:03', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1531059437984989189', 'comment', '发表评论', '/cms/comment/commit', 'list', 'button', 'POST', '_self', '1504618238889869317', '1506128052832878593', '7', '1', '对文章或作品发表评论', '1', '192.168.1.23', '2021-07-26 22:03:36', '0', '192.168.1.23', '2021-07-26 22:04:52', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1532485035156488196', 'domain', '多域管理', '/admin/dom/domain', 'list', 'admin_menu', 'GET', '_self', '1506005013902311434', '93529533534879755', '3', '1', '领域划分，多站点配置', '1', '192.168.1.23', '2021-07-30 20:28:25', '1', '192.168.1.23', '2021-11-30 20:57:36', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1533544727530094592', 'infoNew', '发布信息', '/info/flatTree', null, 'button', 'GET', '_self', '1504617964850823176', '1506128052832878593', '1', '1', '发布信息按钮', '1', '192.168.1.23', '2022-01-16 17:13:49', '1', '192.168.1.23', '2022-01-16 18:46:24', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1533950643823886345', 'help', '帮助', '#', 'list', 'menu', 'GET', '_blank', '1533901932104171527', '0', '10', '1', 'wldos帮助', '1', '192.168.1.23', '2021-08-03 21:32:13', '1', '192.168.1.23', '2023-01-26 01:21:12', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1538994469231837188', 'rwnp', '合集', '/product/category/rwnp', 'list', 'menu', 'GET', '_self', '1504618238889869317', '0', '3', '0', '付费作品合集。', '1', '192.168.1.23', '2021-08-17 19:34:35', '1', '192.168.1.23', '2023-02-03 02:21:47', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1539042480926408715', 'lsnp', '历史年表', '/product/category/lsnp', 'list', 'menu', 'GET', '_self', '1504618238889869317', '1538994469231837188', '1', '0', '历史年表大事记', '1', '192.168.1.23', '2021-08-17 22:45:21', '1', '192.168.1.23', '2023-02-03 02:22:13', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1539043101486268425', 'hynp', '行业年谱', '/product/category/hynp', 'list', 'menu', 'GET', '_self', '1504618238889869317', '1538994469231837188', '3', '0', '行业发展史', '1', '192.168.1.23', '2021-08-17 22:47:49', '1', '192.168.1.23', '2023-02-03 02:22:33', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1539043349348663305', 'xsnp', '学术年谱', '/product/category/xsnp', 'list', 'menu', 'GET', '_self', '1504618238889869317', '1538994469231837188', '2', '0', '学术研究史', '1', '192.168.1.23', '2021-08-17 22:48:49', '1', '192.168.1.23', '2023-02-03 02:22:24', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1542170368806666244', 'article-blog', '分享', '/archives/category/ruanjian', 'list', 'menu', 'GET', '_self', '1504618238889869317', '0', '2', '0', '公开发布的内容或作品', '1', '192.168.1.23', '2021-08-26 13:54:28', '1', '192.168.1.23', '2023-02-08 00:18:57', 'normal', '1');
INSERT INTO `wo_resource` VALUES ('1542939849472524294', 'a-jsfx', '技术分享', '/archives/category/jsfx', 'list', 'menu', 'GET', '_self', '1504618238889869317', '1542170368806666244', '1', '0', '技术分享', '1', '192.168.1.23', '2021-08-28 16:52:07', '1', '192.168.1.23', '2023-02-03 02:20:27', 'normal', '1');

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
-- Records of wo_role
-- ----------------------------
INSERT INTO `wo_role` VALUES ('1', 'guest', '游客', '未登录用户', 'sys_role', '0', '1', '1', '0', '0', '2021-05-08 11:20:34', '0', '127.0.0.1', '2021-05-20 15:30:23', 'normal', '1');
INSERT INTO `wo_role` VALUES ('2', 'user', '普通会员', '普通注册用户', 'sys_role', '0', '2', '1', '0', '0', '2021-05-08 13:47:13', '0', '127.0.0.1', '2021-05-20 15:31:02', 'normal', '1');
INSERT INTO `wo_role` VALUES ('91933147798355971', 'admin', '超级管理员', '超级管理员权限定义，默认超级管理员是admin用户组下的成员，不受权限规则限制。', 'sys_role', '1509213016482824194', '1', '1', '1', '192.168.1.23', '2021-11-26 11:07:20', '1', '192.168.1.23', '2021-12-07 17:34:04', 'normal', '1');
INSERT INTO `wo_role` VALUES ('91960652726976520', 'trole', '租户角色', '租户业务角色', 'tal_role', '0', '6', '1', '1', '192.168.1.23', '2021-11-26 12:56:38', '1', '192.168.1.23', '2021-11-26 12:56:38', 'normal', '1');
INSERT INTO `wo_role` VALUES ('91960788098138117', 'tauthor', '作者', null, 'tal_role', '91960652726976520', '1', '1', '1', '192.168.1.23', '2021-11-26 12:57:10', '1', '192.168.1.23', '2021-11-26 12:57:10', 'normal', '1');
INSERT INTO `wo_role` VALUES ('247228227491905546', 'can_trust', '可信者', '可信者不受内容发布审核规则约束，无需审核直接发布。一般是平台文档编辑。任何需要跳过规则的场景都可以依据可信者角色设置特别通道。', 'sys_role', '0', '7', '1', '1', '192.168.1.23', '2023-01-28 23:54:33', '1', '192.168.1.23', '2023-01-28 23:54:33', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1506681836080381960', 'vip', 'VIP会员', '付费用户', 'sys_role', '0', '3', '1', '0', '127.0.0.1', '2021-05-20 15:35:43', '0', '127.0.0.1', '2021-05-20 15:35:43', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506688462267006976', 'hvip', '高级VIP', '年费VIP', 'sys_role', '1506681836080381960', '12', '1', '0', '127.0.0.1', '2021-05-20 16:02:02', '0', '127.0.0.1', '2021-05-25 20:47:58', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506688973191954441', 'dvip', '钻石VIP', '3年以上年费', 'sys_role', '1506681836080381960', '23', '1', '0', '127.0.0.1', '2021-05-20 16:04:04', '0', '127.0.0.1', '2021-05-25 20:48:09', 'normal', null);
INSERT INTO `wo_role` VALUES ('1506689309763878914', 'avip', '终身VIP', '永久包年', 'sys_role', '1506681836080381960', '4', '1', '0', '127.0.0.1', '2021-05-20 16:05:24', '0', '127.0.0.1', '2021-05-25 20:48:17', 'normal', null);
INSERT INTO `wo_role` VALUES ('1508572257052180489', 'gvip', '普通VIP', '普通付费', 'sys_role', '1506681836080381960', '1', '1', '0', '127.0.0.1', '2021-05-25 20:47:34', '0', '127.0.0.1', '2021-05-25 20:47:34', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1509213016482824194', 'admins', '管理员', '系统管理员', 'sys_role', '0', '4', '1', '0', '127.0.0.1', '2021-05-27 15:13:43', '1', '192.168.1.23', '2021-12-07 17:33:54', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1525946478916976648', 'tadmin', '租户管理员', '租户（公司）的管理员，是平台超级管理员的子集。', 'sys_role', '0', '5', '1', '1', '192.168.1.23', '2021-07-12 19:26:31', '1', '192.168.1.23', '2021-07-12 19:26:31', 'normal', '1');
INSERT INTO `wo_role` VALUES ('1526213891793272839', 'badmin', '二级管理员', '二级管理员集成超级管理员功能权限。', 'sys_role', '1509213016482824194', '1', '1', '1', '192.168.1.23', '2021-07-13 13:09:07', '1', '192.168.1.23', '2021-12-07 17:34:24', 'normal', '1');

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
-- Records of wo_role_org
-- ----------------------------
INSERT INTO `wo_role_org` VALUES ('0', '1', '0', '0', '0', '1', '0', '0', '2021-05-08 12:10:41', '0', '0', '2021-05-08 12:10:47', 'normal', '1');
INSERT INTO `wo_role_org` VALUES ('91933514804150273', '1526213891793272839', '1526214941484957699', '0', '0', '1', '1', '192.168.1.23', '2021-11-26 11:08:48', '1', '192.168.1.23', '2021-11-26 11:08:48', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('91933564657647616', '91933147798355971', '91933461561655297', '0', '0', '1', '1', '192.168.1.23', '2021-11-26 11:09:00', '1', '192.168.1.23', '2021-11-26 11:09:00', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('247228495877029888', '247228227491905546', '247227424555319302', '0', '0', '1', '1', '192.168.1.23', '2023-01-28 23:55:37', '1', '192.168.1.23', '2023-01-28 23:55:37', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1508560523641929728', '2', '200', '0', '0', '1', '0', '127.0.0.1', '2021-05-25 20:00:57', '0', '127.0.0.1', '2021-05-25 20:00:57', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1508573862422036488', '1508572257052180489', '201', '0', '0', '1', '0', '127.0.0.1', '2021-05-25 20:53:57', '0', '127.0.0.1', '2021-05-25 20:53:57', 'normal', '0');
INSERT INTO `wo_role_org` VALUES ('1525968743872249860', '1525946478916976648', '1525968690709446657', '0', '0', '1', '1', '192.168.1.23', '2021-07-12 20:55:00', '1', '192.168.1.23', '2021-07-12 20:55:00', 'normal', '0');

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
-- Records of wo_user
-- ----------------------------
INSERT INTO `wo_user` VALUES ('1', 'admin', '龙神', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'normal', '0', null, 'admin', 'man', null, '15665730355', '0531-268888888', '注册会员', '网络用户', '长清区崮云湖街道', null, '30699@qq.com', '/202203/18183200ow6yRB4v.png', 'WLDOS之父，独立研发了WLDOS云应用支撑平台。', '0', '0', 'China', '370000', '370100', null, '0', null, '192.168.1.23', '0', '2021-05-08 12:15:05', '192.168.1.23', '1', '2023-02-13 19:03:23', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_user` VALUES ('100', 'wldos', '演示管理员', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '100', null, 'wldos', 'man', null, '15665730355', '0531-8868823', '注册会员', '网络用户', '朝阳区西祠胡同', null, 'wldos.com@88.com', '/202110/201945151nGiy6hd.png', 'wldos管家。', '1', '0', 'China', '370000', '370100', null, '100', null, '192.168.1.23', '0', '2021-05-08 12:15:09', '192.168.1.23', '1', '2021-11-28 22:30:27', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_user` VALUES ('79070766156136451', 'wldos001@163.com', 'wldos新用户', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, '15665730355', '0531-15665730355', '注册会员', '网络用户', '天桥区', null, 'wldos001@163.com', '/202110/21231756DhqmsZdL.png', '新用户测试', null, null, 'China', '130000', '130300', null, null, null, '192.168.1.23', '79070766156136451', '2021-10-21 23:16:50', '192.168.1.23', '1', '2021-10-28 17:06:50', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('81514195872038918', 'newer@163.com', '小怪兽', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'newer@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '81514195872038918', '2021-10-28 17:06:09', '192.168.1.23', '81514195872038918', '2021-10-28 17:06:09', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('81836118845865995', 'test@zhiletu.com', 'test1', 'n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '81836118845865995', '2021-10-29 14:25:21', '192.168.1.23', '81836118845865995', '2021-10-29 14:25:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('92829405966680072', 'tenant', '租户管理员', '6dqG01HPmnZC2MUBlcP0ZiIJEaFcF3gJvRFhpR6MXyQ=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'tenant@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '92829405966680072', '2021-11-28 22:28:45', '192.168.1.23', '1', '2021-11-28 22:31:23', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('92832497370054659', 'newAdd@zhiletu.com', '新增租户员工1', 'YCP7zBhPiUK5Ji9VlQTX+q0umi8nONiCXZJbTiUz4Xg=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'newAdd@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '92832497370054659', '2021-11-28 22:41:02', '192.168.1.23', '92832497370054659', '2021-11-28 22:41:02', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('119132574443028481', 'test2@qq.com', 'laolai', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test2@qq.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '119132574443028481', '2022-02-09 12:28:11', '192.168.1.23', '119132574443028481', '2022-02-09 12:28:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('119560179352780806', 'test3@qq.com', 'laolai', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test3@qq.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '119560179352780806', '2022-02-10 16:47:51', '192.168.1.23', '119560179352780806', '2022-02-10 16:47:51', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('189862984948563971', 'yuanxi@zhiletu.com', 'yuanxi', 'z3SigILGFO6PqhYuR2ups7XA+DGy7ppCKGzo0G+Kv9g=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, 'yuanxi@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '189862984948563971', '2022-08-23 16:45:35', '192.168.1.23', '189862984948563971', '2022-08-23 16:45:35', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('191677093767725063', 'yuanxi1@zhiletu.com', 'yuanxi', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, 'yuanxi1@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '191677093767725063', '2022-08-28 16:54:12', '192.168.1.23', '191677093767725063', '2022-08-28 16:54:12', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('192743746152611843', 'yuanxi2@zhiletu.com', 'yuanxi', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, 'yuanxi2@zhiletu.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '192743746152611843', '2022-08-31 15:32:42', '192.168.1.23', '192743746152611843', '2022-08-31 15:32:42', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('192753329898176517', '306991142@qq.com', 'wldostest', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '306991142@qq.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '192753329898176517', '2022-08-31 16:10:47', '192.168.1.23', '192753329898176517', '2022-08-31 16:10:47', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('198204851142443011', '5837163615@qq.com', 'wldos2022', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '583716365@qq.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '198204851142443011', '2022-09-15 17:13:11', '192.168.1.23', '198204851142443011', '2022-09-15 17:13:11', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('198575893094514697', '583716365@qq.com', 'yuanxi2023', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '583716365@qq.com', '/202209/16192006kkX6YHpz.png', null, null, null, null, null, null, null, null, null, '192.168.1.23', '198575893094514697', '2022-09-16 17:47:34', '192.168.1.23', '198575893094514697', '2022-09-16 19:20:06', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('214132159892078601', '行者', '行者', null, 'normal', '1', null, null, null, null, null, null, null, null, null, null, null, '/2022/10/29165431nSPtJhlt.jpg', null, null, null, null, null, null, null, null, null, '192.168.1.23', '214132159892078601', '2022-10-29 16:02:35', '192.168.1.23', '214132159892078601', '2022-10-29 16:54:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('214858324143423489', '元悉科技', '元悉科技', null, 'normal', '1', null, null, null, null, null, null, null, null, null, null, null, '/2022/10/31160806yDuzotP7.jpg', null, null, null, null, null, null, null, null, null, '192.168.1.23', '214858324143423489', '2022-10-31 16:08:06', '192.168.1.23', '214858324143423489', '2022-10-31 16:08:07', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1502723278108278788', 'support@zhiletu.com', null, 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', null, '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'support@zhiletu.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502723278108278788', '2021-05-09 17:25:49', '127.0.0.1', '1502723278108278788', '2021-05-09 17:25:49', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502726588102000651', '3069911421@qq.com', null, 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', null, '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '3069911421@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502726588102000651', '2021-05-09 17:38:58', '127.0.0.1', '1502726588102000651', '2021-05-09 17:38:58', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502739709344530437', '5837163651@qq.com', '小五义', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '583716365@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502739709344530437', '2021-05-09 18:31:06', '127.0.0.1', '0', '2021-05-26 22:45:15', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502742261263941642', '2345@qq.com', '2345', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '2345@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502742261263941642', '2021-05-09 18:41:15', '127.0.0.1', '1502742261263941642', '2021-05-09 18:41:15', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502770377176825865', '586@163.com', '586', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '586@163.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502770377176825865', '2021-05-09 20:32:58', '127.0.0.1', '0', '2021-05-26 22:46:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502773236924596235', '567@163.com', '菲菲', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '567@163.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502773236924596235', '2021-05-09 20:44:20', '127.0.0.1', '0', '2021-05-26 22:46:43', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502777260587532299', '1234@126.com', '没有五', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '1234@126.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502777260587532299', '2021-05-09 21:00:19', '127.0.0.1', '0', '2021-05-26 22:47:02', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502777813036089350', '23@123.com', '驿站之心', 'iYd4ehqGOz/arZ/gX71njz/QV0+eb60uS1NuT0yvtzg=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, '23@123.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502777813036089350', '2021-05-09 21:02:31', '127.0.0.1', '0', '2021-05-26 22:47:51', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502783343481765897', 'nihao1@123.com', '你好夏天', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'nihao1@123.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502783343481765897', '2021-05-09 21:24:29', '127.0.0.1', '1502783343481765897', '2021-05-09 21:24:29', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1502803624724185094', 'bbc@qq.com', 'nihao', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '0', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'bbc@qq.com', null, null, '0', null, null, null, null, null, null, null, '127.0.0.1', '1502803624724185094', '2021-05-09 22:45:05', '127.0.0.1', '1502803624724185094', '2021-05-09 22:45:05', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_user` VALUES ('1521171388547383299', 'jjunxiao@163.com', '223', '3zBoxWKPRqP11DsQnBBX8CfLPNhJ3yz1F5ep1YVFMDA=', 'normal', null, null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'jjunxiao@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1521171388547383299', '2021-06-29 15:12:01', '192.168.1.23', '1521171388547383299', '2021-06-29 15:12:01', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547396545116815364', 'test@126.com', 'test', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', null, null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test@126.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547396545116815364', '2021-09-10 00:01:26', '192.168.1.23', '1547396545116815364', '2021-09-10 00:01:26', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547627860437155848', 'abc@163.com', 'abc', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', null, null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'abc@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547627860437155848', '2021-09-10 15:20:36', '192.168.1.23', '1547627860437155848', '2021-09-10 15:20:36', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547650817649721344', 'abc1@163.com', 'test', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'abc1@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547650817649721344', '2021-09-10 16:51:49', '192.168.1.23', '1547650817649721344', '2021-09-10 16:51:49', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547663029483257866', 'abc2@163.com', 'abc', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'abc2@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547663029483257866', '2021-09-10 17:40:20', '192.168.1.23', '1547663029483257866', '2021-09-10 17:40:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547670975575408643', 'test3@163.com', 'test3', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, '15665730355', null, '注册会员', '网络用户', '长清区', null, 'test3@163.com', null, '个人', null, null, 'China', '120000', '120100', null, null, null, '192.168.1.23', '1547670975575408643', '2021-09-10 18:11:55', '192.168.1.23', '1547670975575408643', '2021-09-13 19:46:21', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547691630899413002', 'test31@163.com', 'test3', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test31@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547691630899413002', '2021-09-10 19:34:00', '192.168.1.23', '1547691630899413002', '2021-09-10 19:34:00', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547693779586826246', 'test4@163.com', 'test4', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test4@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547693779586826246', '2021-09-10 19:42:32', '192.168.1.23', '1547693779586826246', '2021-09-10 19:42:32', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547695225065619462', 'test5@163.com', 'test5', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test5@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547695225065619462', '2021-09-10 19:48:16', '192.168.1.23', '1547695225065619462', '2021-09-10 19:48:16', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547696496472408073', 'test6@163.com', 'test6', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, '注册会员', '网络用户', null, null, 'test6@163.com', null, null, null, null, null, null, null, null, null, null, '192.168.1.23', '1547696496472408073', '2021-09-10 19:53:20', '192.168.1.23', '1547696496472408073', '2021-09-10 19:53:20', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_user` VALUES ('1547698179520774144', 'test7@163.com', 'test7', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, '0533-19388888', '注册会员', '网络用户', '太原市', null, 'test7@163.com', '/202109/10201135YqsgC7J0.jpg', '个人简介', null, null, 'China', '140000', '140100', null, null, null, '192.168.1.23', '1547698179520774144', '2021-09-10 20:00:01', '192.168.1.23', '1547698179520774144', '2021-09-10 20:11:35', '192.168.1.23', 'normal', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=214155705741656076 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
INSERT INTO `wo_usermeta` VALUES ('77834711138484234', '1', 'passStatus', 'weak');
INSERT INTO `wo_usermeta` VALUES ('77879036710928394', '1', 'mobile', '18660802028');
INSERT INTO `wo_usermeta` VALUES ('78655037632069638', '100', 'mobile', '15665730355');
INSERT INTO `wo_usermeta` VALUES ('79070767984852994', '79070766156136451', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('79071227135311877', '79070766156136451', 'mobile', '15665730355');
INSERT INTO `wo_usermeta` VALUES ('81499734557769729', '1502739709344530437', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('81514196513767424', '81514195872038918', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('81836119546314755', '81836118845865995', 'passStatus', 'weak');
INSERT INTO `wo_usermeta` VALUES ('92829407447269378', '92829405966680072', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('92832498531876868', '92832497370054659', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('119132585889284101', '119132574443028481', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('119560318918246406', '119560179352780806', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('154369105084465158', '1', 'tags', '[{\"key\":\"tag-0\",\"label\":\"wldos之父\"},{\"key\":\"tag-1\",\"label\":\"研发高手的梦想\"},{\"key\":\"tag-2\",\"label\":\"java\"},{\"key\":\"tag-3\",\"label\":\"111\"},{\"key\":\"tag-4\",\"label\":\"测试\"}]');
INSERT INTO `wo_usermeta` VALUES ('186608232714387457', '1', 'bakEmail', 'support@zhiletu.com');
INSERT INTO `wo_usermeta` VALUES ('189862995564347401', '189862984948563971', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('191677105738268676', '191677093767725063', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('192743754088235011', '192743746152611843', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('192753338811072518', '192753329898176517', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('198204861032611850', '198204851142443011', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('198575901655089156', '198575893094514697', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('214155705741656075', '214132159892078601', 'tags', '[{\"key\":\"tag-0\",\"label\":\"第一登录人\"}]');
