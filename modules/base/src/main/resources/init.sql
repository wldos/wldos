
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
                              `karma` int(11) NOT NULL DEFAULT '0',
                              `approved` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `user_agent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `comment_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                              `create_by` bigint(20) unsigned DEFAULT '0',
                              `create_time` datetime DEFAULT NULL,
                              `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `update_by` bigint(20) unsigned DEFAULT NULL,
                              `update_time` datetime DEFAULT NULL,
                              `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `versions` int(10) DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `comment_post_ID` (`pub_id`) USING BTREE,
                              KEY `comment_approved_date_gmt` (`approved`) USING BTREE,
                              KEY `comment_parent` (`parent_id`) USING BTREE,
                              KEY `comment_author_email` (`author_email`(10)) USING BTREE,
                              KEY `comment_delete_flag` (`delete_flag`) USING BTREE,
                              KEY `comment_create_by` (`create_by`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_comments
-- ----------------------------

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
                                        `meta_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `meta_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `meta_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `data_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `enum_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                        `pub_type` varchar(20) DEFAULT NULL,
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
                             KEY `meta_key` (`meta_key`(191)) USING BTREE,
                             KEY `post_id` (`pub_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_pubmeta
-- ----------------------------
INSERT INTO `k_pubmeta` VALUES ('278644087062183946', '278644086323986440', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('278644087066378248', '278644086323986440', 'ornPrice', '0');
INSERT INTO `k_pubmeta` VALUES ('278644087070572553', '278644086323986440', 'city', '110100');
INSERT INTO `k_pubmeta` VALUES ('278644087074766855', '278644086323986440', 'subTitle', '示例文章');
INSERT INTO `k_pubmeta` VALUES ('278644087074766856', '278644086323986440', 'province', '110000');
INSERT INTO `k_pubmeta` VALUES ('278644508954640385', '278644508812034051', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('278645100573802502', '278645100074680329', 'subTitle', '示例文章');
INSERT INTO `k_pubmeta` VALUES ('278645100577996803', '278645100074680329', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('278987600350068743', '278645100074680329', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('279019923963559943', '279019922097094657', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('279019923967754245', '279019922097094657', 'mainPic1', '/2023/04/26172220XNamhlAP.jpg');
INSERT INTO `k_pubmeta` VALUES ('279019923971948555', '279019922097094657', 'mainPic2', '/2023/04/26172228U7XkZpq6.jpg');
INSERT INTO `k_pubmeta` VALUES ('279019923976142855', '279019922097094657', 'telephone', '11205823492');
INSERT INTO `k_pubmeta` VALUES ('279019923976142856', '279019922097094657', 'cover', '/2023/04/261722102KkXbfWu.jpg');
INSERT INTO `k_pubmeta` VALUES ('279019923976142857', '279019922097094657', 'city', '130300');
INSERT INTO `k_pubmeta` VALUES ('279019923976142858', '279019922097094657', 'contact', '李老师');
INSERT INTO `k_pubmeta` VALUES ('279019923976142859', '279019922097094657', 'subTitle', '示例文集');
INSERT INTO `k_pubmeta` VALUES ('279019923980337162', '279019922097094657', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('279019923980337163', '279019922097094657', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('279020188947103748', '279020188926132232', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('279020609967144960', '279020608851460100', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279020889236488195', '279020889219710981', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279020939819794439', '279020939807211529', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279020970064920586', '279020970048143369', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021070430420992', '279020970048143369', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('279021070434615301', '279020970048143369', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('279021081234948099', '279021081096536068', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021081973145602', '279021081956368386', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021084804300810', '279021084787523594', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021086964367371', '279021086947590147', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021087824199689', '279021087803228166', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021088038109184', '279021088025526281', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279021088797278218', '279021088780500998', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021089707442184', '279021089694859270', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021099593416706', '279021099580833802', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021101669597187', '279021101648625670', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021102546206722', '279021102529429512', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021102852390919', '279021102839808000', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('279021103573811208', '279021103557033986', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021104131653643', '279021104119070728', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021105079566341', '279021105071177732', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021105780015114', '279021105759043584', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021106652430337', '279021106639847427', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021107784892422', '279021107768115200', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279021108741193732', '279021108724416515', 'views', '2');
INSERT INTO `k_pubmeta` VALUES ('279021109378727939', '279021109257093124', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('279023412403945479', '279019922097094657', 'views', '8');
INSERT INTO `k_pubmeta` VALUES ('279036753616748544', '279036753583194115', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('279109743171780618', '279036753583194115', 'views', '3');
INSERT INTO `k_pubmeta` VALUES ('279427262554226697', '279427262445174791', 'attachMetadata', '{\"width\":560,\"height\":347,\"path\":\"/2023/04/27202200y1hCTrJK.jpg\",\"srcset\":[{\"type\":null,\"width\":560,\"height\":347,\"path\":\"/2023/04/27202200y1hCTrJK.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":92,\"path\":\"/2023/04/27202200y1hCTrJK-150x92.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":185,\"path\":\"/2023/04/27202200y1hCTrJK-300x185.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279427262558420998', '279427262445174791', 'attachPath', '/2023/04/27202200y1hCTrJK.jpg');
INSERT INTO `k_pubmeta` VALUES ('279427562916724746', '279427562895753223', 'attachMetadata', '{\"width\":361,\"height\":246,\"path\":\"/2023/04/272023118Sr7RKYm.jpg\",\"srcset\":[{\"type\":null,\"width\":361,\"height\":246,\"path\":\"/2023/04/272023118Sr7RKYm.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":102,\"path\":\"/2023/04/272023118Sr7RKYm-150x102.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":204,\"path\":\"/2023/04/272023118Sr7RKYm-300x204.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279427562925113348', '279427562895753223', 'attachPath', '/2023/04/272023118Sr7RKYm.jpg');
INSERT INTO `k_pubmeta` VALUES ('279427884212994052', '279427884196216841', 'attachMetadata', '{\"width\":361,\"height\":246,\"path\":\"/2023/04/27202428eqGqQIHa.jpg\",\"srcset\":[{\"type\":null,\"width\":361,\"height\":246,\"path\":\"/2023/04/27202428eqGqQIHa.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":102,\"path\":\"/2023/04/27202428eqGqQIHa-150x102.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":204,\"path\":\"/2023/04/27202428eqGqQIHa-300x204.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279427884217188358', '279427884196216841', 'attachPath', '/2023/04/27202428eqGqQIHa.jpg');
INSERT INTO `k_pubmeta` VALUES ('279433044389707781', '279433044364541957', 'attachMetadata', '{\"width\":395,\"height\":424,\"path\":\"/2023/04/27204458gk70kINw.jpg\",\"srcset\":[{\"type\":null,\"width\":395,\"height\":424,\"path\":\"/2023/04/27204458gk70kINw.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":161,\"path\":\"/2023/04/27204458gk70kINw-150x161.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":322,\"path\":\"/2023/04/27204458gk70kINw-300x322.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279433044393902090', '279433044364541957', 'attachPath', '/2023/04/27204458gk70kINw.jpg');
INSERT INTO `k_pubmeta` VALUES ('279433122550562823', '279433122529591298', 'attachMetadata', '{\"width\":271,\"height\":277,\"path\":\"/2023/04/272045178fZMXiwv.jpg\",\"srcset\":[{\"type\":null,\"width\":271,\"height\":277,\"path\":\"/2023/04/272045178fZMXiwv.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":153,\"path\":\"/2023/04/272045178fZMXiwv-150x153.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279433122554757131', '279433122529591298', 'attachPath', '/2023/04/272045178fZMXiwv.jpg');
INSERT INTO `k_pubmeta` VALUES ('279434949568413700', '279434949543247876', 'attachMetadata', '{\"width\":2048,\"height\":1536,\"path\":\"/2023/04/27205230DTjcqTGj.jpg\",\"srcset\":[{\"type\":null,\"width\":2048,\"height\":1536,\"path\":\"/2023/04/27205230DTjcqTGj.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":112,\"path\":\"/2023/04/27205230DTjcqTGj-150x112.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":225,\"path\":\"/2023/04/27205230DTjcqTGj-300x225.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":768,\"path\":\"/2023/04/27205230DTjcqTGj-1024x768.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"huge\",\"width\":1536,\"height\":1152,\"path\":\"/2023/04/27205230DTjcqTGj-1536x1152.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"mediumLarge\",\"width\":768,\"height\":576,\"path\":\"/2023/04/27205230DTjcqTGj-768x576.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279434949572608007', '279434949543247876', 'attachPath', '/2023/04/27205230DTjcqTGj.jpg');
INSERT INTO `k_pubmeta` VALUES ('279435158889349121', '279435158876766209', 'attachMetadata', '{\"width\":1080,\"height\":2340,\"path\":\"/2023/04/27205321BFF648y3.jpg\",\"srcset\":[{\"type\":null,\"width\":1080,\"height\":2340,\"path\":\"/2023/04/27205321BFF648y3.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":325,\"path\":\"/2023/04/27205321BFF648y3-150x325.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":650,\"path\":\"/2023/04/27205321BFF648y3-300x650.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":2218,\"path\":\"/2023/04/27205321BFF648y3-1024x2218.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"mediumLarge\",\"width\":768,\"height\":1664,\"path\":\"/2023/04/27205321BFF648y3-768x1664.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279435158893543425', '279435158876766209', 'attachPath', '/2023/04/27205321BFF648y3.jpg');
INSERT INTO `k_pubmeta` VALUES ('279435862949412865', '279435862928441353', 'attachMetadata', '{\"width\":763,\"height\":600,\"path\":\"/2023/04/27205611NFJErASO.jpg\",\"srcset\":[{\"type\":null,\"width\":763,\"height\":600,\"path\":\"/2023/04/27205611NFJErASO.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":117,\"path\":\"/2023/04/27205611NFJErASO-150x117.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":235,\"path\":\"/2023/04/27205611NFJErASO-300x235.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279435862953607177', '279435862928441353', 'attachPath', '/2023/04/27205611NFJErASO.jpg');
INSERT INTO `k_pubmeta` VALUES ('279435917534085131', '279435917517307907', 'attachMetadata', '{\"width\":643,\"height\":357,\"path\":\"/2023/04/27205623KRNjyg3d.jpg\",\"srcset\":[{\"type\":null,\"width\":643,\"height\":357,\"path\":\"/2023/04/27205623KRNjyg3d.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":83,\"path\":\"/2023/04/27205623KRNjyg3d-150x83.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":166,\"path\":\"/2023/04/27205623KRNjyg3d-300x166.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279435917538279432', '279435917517307907', 'attachPath', '/2023/04/27205623KRNjyg3d.jpg');
INSERT INTO `k_pubmeta` VALUES ('279435993627148289', '279435993610371082', 'attachMetadata', '{\"width\":349,\"height\":266,\"path\":\"/2023/04/27205642XD5bS8RS.jpg\",\"srcset\":[{\"type\":null,\"width\":349,\"height\":266,\"path\":\"/2023/04/27205642XD5bS8RS.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":114,\"path\":\"/2023/04/27205642XD5bS8RS-150x114.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":228,\"path\":\"/2023/04/27205642XD5bS8RS-300x228.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279435993627148290', '279435993610371082', 'attachPath', '/2023/04/27205642XD5bS8RS.jpg');
INSERT INTO `k_pubmeta` VALUES ('279436508721233926', '279436508696068099', 'attachMetadata', '{\"width\":395,\"height\":424,\"path\":\"/2023/04/272058448ayVrKkk.jpg\",\"srcset\":[{\"type\":null,\"width\":395,\"height\":424,\"path\":\"/2023/04/272058448ayVrKkk.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":161,\"path\":\"/2023/04/272058448ayVrKkk-150x161.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":322,\"path\":\"/2023/04/272058448ayVrKkk-300x322.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279436508725428235', '279436508696068099', 'attachPath', '/2023/04/272058448ayVrKkk.jpg');
INSERT INTO `k_pubmeta` VALUES ('279436579865018378', '279436579852435456', 'attachMetadata', '{\"width\":285,\"height\":226,\"path\":\"/2023/04/27205902sWJNGyCg.jpg\",\"srcset\":[{\"type\":null,\"width\":285,\"height\":226,\"path\":\"/2023/04/27205902sWJNGyCg.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":118,\"path\":\"/2023/04/27205902sWJNGyCg-150x118.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279436579869212678', '279436579852435456', 'attachPath', '/2023/04/27205902sWJNGyCg.jpg');
INSERT INTO `k_pubmeta` VALUES ('279439800973049865', '279439799488266251', 'city', '370200');
INSERT INTO `k_pubmeta` VALUES ('279439800977244166', '279439799488266251', 'cover', '/2023/04/27211132Yfd3EyvO.jpg');
INSERT INTO `k_pubmeta` VALUES ('279439800981438470', '279439799488266251', 'contact', 'wldos');
INSERT INTO `k_pubmeta` VALUES ('279439800985632768', '279439799488266251', 'privacyLevel', 'public');
INSERT INTO `k_pubmeta` VALUES ('279439800985632769', '279439799488266251', 'subTitle', '免费发布信息');
INSERT INTO `k_pubmeta` VALUES ('279439800985632770', '279439799488266251', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('279439800985632771', '279439799488266251', 'province', '370000');
INSERT INTO `k_pubmeta` VALUES ('279439800985632772', '279439799488266251', 'telephone', '11205823492');
INSERT INTO `k_pubmeta` VALUES ('279439800989827074', '279439799488266251', 'mainPic1', '/2023/04/27211144XJEexPLS.jpg');
INSERT INTO `k_pubmeta` VALUES ('279440246089367563', '279440244893990923', 'ornPrice', '10');
INSERT INTO `k_pubmeta` VALUES ('279440246089367564', '279440244893990923', 'province', '140000');
INSERT INTO `k_pubmeta` VALUES ('279440246089367565', '279440244893990923', 'city', '140300');
INSERT INTO `k_pubmeta` VALUES ('279440246089367566', '279440244893990923', 'subTitle', '免费发布信息');
INSERT INTO `k_pubmeta` VALUES ('279440376704188427', '279440376687411200', 'attachMetadata', '{\"width\":395,\"height\":424,\"path\":\"/2023/04/272114076gumFYJC.jpg\",\"srcset\":[{\"type\":null,\"width\":395,\"height\":424,\"path\":\"/2023/04/272114076gumFYJC.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":161,\"path\":\"/2023/04/272114076gumFYJC-150x161.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":322,\"path\":\"/2023/04/272114076gumFYJC-300x322.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279440376708382724', '279440376687411200', 'attachPath', '/2023/04/272114076gumFYJC.jpg');
INSERT INTO `k_pubmeta` VALUES ('279440443200684039', '279440443175518218', 'attachMetadata', '{\"width\":374,\"height\":226,\"path\":\"/2023/04/27211423UmumWhEH.jpg\",\"srcset\":[{\"type\":null,\"width\":374,\"height\":226,\"path\":\"/2023/04/27211423UmumWhEH.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":90,\"path\":\"/2023/04/27211423UmumWhEH-150x90.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":181,\"path\":\"/2023/04/27211423UmumWhEH-300x181.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279440443204878343', '279440443175518218', 'attachPath', '/2023/04/27211423UmumWhEH.jpg');
INSERT INTO `k_pubmeta` VALUES ('279440647530397701', '279440244893990923', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279474604267454475', '279439799488266251', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279475363029630978', '279475362996076552', 'attachMetadata', '{\"width\":733,\"height\":529,\"path\":\"/2023/04/27233308HB8BrDRc.jpg\",\"srcset\":[{\"type\":null,\"width\":733,\"height\":529,\"path\":\"/2023/04/27233308HB8BrDRc.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":108,\"path\":\"/2023/04/27233308HB8BrDRc-150x108.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":216,\"path\":\"/2023/04/27233308HB8BrDRc-300x216.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279475363029630979', '279475362996076552', 'attachPath', '/2023/04/27233308HB8BrDRc.jpg');
INSERT INTO `k_pubmeta` VALUES ('279491771436285954', '279491771406925828', 'attachMetadata', '{\"width\":395,\"height\":424,\"path\":\"/2023/04/28003820karQeT8V.jpg\",\"srcset\":[{\"type\":null,\"width\":395,\"height\":424,\"path\":\"/2023/04/28003820karQeT8V.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":161,\"path\":\"/2023/04/28003820karQeT8V-150x161.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":322,\"path\":\"/2023/04/28003820karQeT8V-300x322.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('279491771440480267', '279491771406925828', 'attachPath', '/2023/04/28003820karQeT8V.jpg');
INSERT INTO `k_pubmeta` VALUES ('279496821151809546', '279496821072117767', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('279497240531877896', '279497240485740546', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279521285612683265', '279521285574934534', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279521307435646980', '279521307418869765', 'views', '0');
INSERT INTO `k_pubmeta` VALUES ('279532165716951040', '279532165675008001', 'views', '1');
INSERT INTO `k_pubmeta` VALUES ('280909762526887942', '279496821072117767', 'cover', '/noPic.jpg');
INSERT INTO `k_pubmeta` VALUES ('280909762526887943', '279496821072117767', 'privacyLevel', 'reward');
INSERT INTO `k_pubmeta` VALUES ('280909762526887944', '279496821072117767', 'subTitle', '副标题突出卖点');
INSERT INTO `k_pubmeta` VALUES ('280909762526887945', '279496821072117767', 'province', '130000');
INSERT INTO `k_pubmeta` VALUES ('280909762526887946', '279496821072117767', 'ornPrice', '1200');
INSERT INTO `k_pubmeta` VALUES ('280909762526887947', '279496821072117767', 'city', '130300');
INSERT INTO `k_pubmeta` VALUES ('280909762526887948', '279496821072117767', 'contact', '李老师');
INSERT INTO `k_pubmeta` VALUES ('280909762526887949', '279496821072117767', 'telephone', '11205823492');
INSERT INTO `k_pubmeta` VALUES ('280910649349554179', '280910649282445320', 'attachMetadata', '{\"width\":2048,\"height\":2730,\"path\":\"/2023/05/01223622CgZwfUC1.jpg\",\"srcset\":[{\"type\":null,\"width\":2048,\"height\":2730,\"path\":\"/2023/05/01223622CgZwfUC1.jpg\",\"mimeType\":null},{\"type\":\"thumbnail\",\"width\":150,\"height\":200,\"path\":\"/2023/05/01223622CgZwfUC1-150x200.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"medium\",\"width\":300,\"height\":400,\"path\":\"/2023/05/01223622CgZwfUC1-300x400.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"large\",\"width\":1024,\"height\":1365,\"path\":\"/2023/05/01223622CgZwfUC1-1024x1365.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"huge\",\"width\":1536,\"height\":2048,\"path\":\"/2023/05/01223622CgZwfUC1-1536x2048.jpg\",\"mimeType\":\"image/jpeg\"},{\"type\":\"mediumLarge\",\"width\":768,\"height\":1024,\"path\":\"/2023/05/01223622CgZwfUC1-768x1024.jpg\",\"mimeType\":\"image/jpeg\"}]}');
INSERT INTO `k_pubmeta` VALUES ('280910649353748487', '280910649282445320', 'attachPath', '/2023/05/01223622CgZwfUC1.jpg');

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
) ENGINE=InnoDB AUTO_INCREMENT=280910649282445321 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_pubs
-- ----------------------------
INSERT INTO `k_pubs` VALUES ('278645100074680329', '<p>Hello，World！</p>\n<p>你好，世界！</p>\n<p>从此，开启你的创作之路吧！</p>\n<p><strong>Hello，World！</strong></p>\n<p><strong>你好，世界！</strong></p>\n<p><strong>从此，开启你的创作之路吧！</strong></p>\n<h2><em><span style=\"color: #2dc26b;\">Hello，World！</span></em></h2>\n<h2><em><span style=\"color: #2dc26b;\">你好，世界！</span></em></h2>\n<h2><em><span style=\"color: #2dc26b;\">从此，开启你的创作之路吧！</span></em></h2>\n<p><span style=\"font-size: 18pt;\"><strong><span style=\"text-decoration: underline;\">Hello，World！</span></strong></span></p>\n<p><span style=\"font-size: 18pt;\"><strong><span style=\"text-decoration: underline;\">你好，世界！</span></strong></span></p>\n<p><span style=\"font-size: 18pt;\"><strong><span style=\"text-decoration: underline;\">从此，开启你的创作之路吧！</span></strong></span></p>', '示例文章', null, 'publish', null, null, 'shiliwenzhang', '0', 'post', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-25 16:33:59', '127.0.0.1', '1', '2023-04-26 15:19:22', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279019922097094657', null, '示例文集-文集是可产品化的作品合集', '文集是可产品化的作品合集，相比较单篇文章具有更强的聚焦能力。几乎无限多的篇章容量，打破时空制约的创作分享空间，是成就创作梦想的优秀解决方案。创作是没有门槛的，创作也是没有上限的，在一切都透明的现代社会里，唯有你的创造力是不可复制的。\n', 'publish', null, null, 'shiliwenji-wenjishikechanpinhuadezuopinheji', '0', 'book', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:23:23', '127.0.0.1', '1', '2023-04-26 17:42:31', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279020188926132232', '<p>开始第一篇作品创作。</p>\n<p><strong>开始第一篇作品创作</strong>。开始第一篇作品创作。开始第一篇作品创作。</p>\n<p><span style=\"text-decoration: underline;\">开始第一篇作品创作。</span></p>\n<p><em><span style=\"color: #2dc26b;\">开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。</span></em></p>\n<p>&nbsp;</p>\n<p><span style=\"background-color: #2dc26b;\">开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。开始第一篇作品创作。</span></p>\n<p><span style=\"background-color: #2dc26b;\">字数统计。</span></p>', '第一篇作品标题', null, 'inherit', null, null, 'shi\'li', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:24:27', '127.0.0.1', '1', '2023-05-01 22:47:41', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279020608851460100', '<p>开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。</p>\n<p>开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。</p>\n<p>开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。开始第二篇作品创作。</p>\n<p>。。。</p>\n<p>以此类推，写一本电子书。</p>', '第二篇作品标题', null, 'inherit', null, null, 'de\'r', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:26:07', '127.0.0.1', '1', '2023-04-26 17:27:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279020889219710981', '', '第三篇作品标题', null, 'inherit', null, null, 'dierpianzuopinbiaoti', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:27:14', '127.0.0.1', '1', '2023-04-26 17:27:25', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279020939807211529', '', '第四篇作品标题', null, 'inherit', null, null, 'dierpianzuopinbiaoti1', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:27:26', '127.0.0.1', '1', '2023-04-26 17:27:31', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279020970048143369', '<p>这是内容。</p>', '第五篇作品标题', null, 'inherit', null, null, 'dierpianzuopinbiaoti11', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:27:33', '127.0.0.1', '1', '2023-05-02 00:05:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021081096536068', null, '2023-04-26 17:27:59', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:00', '127.0.0.1', '1', '2023-04-26 17:28:00', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021081956368386', null, '2023-04-26 17:27:59', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:00', '127.0.0.1', '1', '2023-04-26 17:28:00', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021084787523594', null, '2023-04-26 17:28:00', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:01', '127.0.0.1', '1', '2023-04-26 17:28:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021086947590147', null, '2023-04-26 17:28:01', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:01', '127.0.0.1', '1', '2023-04-26 17:28:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021087803228166', null, '2023-04-26 17:28:01', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:01', '127.0.0.1', '1', '2023-04-26 17:28:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021088025526281', null, '2023-04-26 17:28:01', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:01', '127.0.0.1', '1', '2023-04-26 17:28:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021088780500998', null, '2023-04-26 17:28:01', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:02', '127.0.0.1', '1', '2023-04-26 17:28:02', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021089694859270', null, '2023-04-26 17:28:01', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:02', '127.0.0.1', '1', '2023-04-26 17:28:02', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021099580833802', null, '2023-04-26 17:28:04', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:04', '127.0.0.1', '1', '2023-04-26 17:28:04', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021101648625670', null, '2023-04-26 17:28:04', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021102529429512', null, '2023-04-26 17:28:04', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021102839808000', null, '2023-04-26 17:28:04', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021103557033986', null, '2023-04-26 17:28:05', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021104119070728', null, '2023-04-26 17:28:05', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021105071177732', null, '2023-04-26 17:28:05', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:05', '127.0.0.1', '1', '2023-04-26 17:28:05', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021105759043584', null, '2023-04-26 17:28:05', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:06', '127.0.0.1', '1', '2023-04-26 17:28:06', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021106639847427', null, '2023-04-26 17:28:05', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:06', '127.0.0.1', '1', '2023-04-26 17:28:06', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021107768115200', null, '2023-04-26 17:28:06', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:06', '127.0.0.1', '1', '2023-04-26 17:28:06', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021108724416515', null, '2023-04-26 17:28:06', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:06', '127.0.0.1', '1', '2023-04-26 17:28:06', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279021109257093124', '<p>电子书自动按篇章目录分页展示，配合wldos创作工具开发的专用电子书模板，可以展开不同体裁的作品创作和展现。</p>\n<p>从此，开启你的线上创作之路&hellip;&hellip;</p>', '第N篇作品标题', null, 'inherit', null, null, 'dierpianzuopinbiaoti111', '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 17:28:06', '127.0.0.1', '1', '2023-04-26 17:29:36', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279036753583194115', '<p><img src=\"http://localhost:8000/store/2023/04/27202428eqGqQIHa.jpg\" width=\"361\" height=\"246\" /></p>\n<p>&nbsp;</p>\n<p><img src=\"http://localhost:8000/store/2023/04/27205902sWJNGyCg.jpg\" alt=\"\" width=\"285\" height=\"226\" /></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>', '关于登录后不自动刷新仍显示登录按钮的紧急通知', null, 'publish', null, null, 'guanyudengluhoubuzidongshuaxinrengxianshidengluanniudejinjitongzhi', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-26 18:30:16', '127.0.0.1', '1', '2023-04-27 20:59:03', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279427262445174791', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:22:01', '127.0.0.1', '1', '2023-04-27 20:22:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279427562895753223', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:23:13', '127.0.0.1', '1', '2023-04-27 20:23:13', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279427884196216841', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:24:29', '127.0.0.1', '1', '2023-04-27 20:24:29', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279433044364541957', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:44:59', '127.0.0.1', '1', '2023-04-27 20:44:59', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279433122529591298', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:45:18', '127.0.0.1', '1', '2023-04-27 20:45:18', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279434949543247876', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:52:34', '127.0.0.1', '1', '2023-04-27 20:52:34', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279435158876766209', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:53:24', '127.0.0.1', '1', '2023-04-27 20:53:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279435862928441353', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:56:11', '127.0.0.1', '1', '2023-04-27 20:56:11', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279435917517307907', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:56:24', '127.0.0.1', '1', '2023-04-27 20:56:24', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279435993610371082', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:56:43', '127.0.0.1', '1', '2023-04-27 20:56:43', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279436508696068099', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:58:45', '127.0.0.1', '1', '2023-04-27 20:58:45', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279436579852435456', null, 'file', null, 'inherit', null, null, null, '279036753583194115', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 20:59:02', '127.0.0.1', '1', '2023-04-27 20:59:02', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279439799488266251', '<p>信息的详情页，支持富文本编辑，你可以在这里加入信息简介和多媒体图文，支持带格式的图文信息。</p>\n<p><img src=\"http://localhost:8000/store/2023/04/28003820karQeT8V.jpg\" width=\"693\" height=\"744\" /></p>\n<p>2023-04-28</p>', '示例信息', '免费发布信息可以发布各种便民信息，分享你的闲置物品，秀出你的日常心得，购物体验，各种分享。', 'publish', null, null, 'simple-info', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-27 21:11:50', '127.0.0.1', '1', '2023-04-28 00:49:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279440244893990923', '<p><img style=\"float: left;\" src=\"http://localhost:8000/store/2023/04/27211423UmumWhEH.jpg\" alt=\"\" width=\"374\" height=\"226\" /></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>这是一个图片信息。</p>', '示例信息', null, 'publish', null, null, 'simple-info', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-27 21:13:36', '127.0.0.1', '1', '2023-04-27 21:15:01', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279440376687411200', null, 'file', null, 'inherit', null, null, null, '279440244893990923', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 21:14:08', '127.0.0.1', '1', '2023-04-27 21:14:08', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279440443175518218', null, 'file', null, 'inherit', null, null, null, '279440244893990923', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 21:14:23', '127.0.0.1', '1', '2023-04-27 21:14:23', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279475362996076552', null, 'file', null, 'inherit', null, null, null, '279439799488266251', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-27 23:33:09', '127.0.0.1', '1', '2023-04-27 23:33:09', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279491771406925828', null, 'file', null, 'inherit', null, null, null, '279439799488266251', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-04-28 00:38:21', '127.0.0.1', '1', '2023-04-28 00:38:21', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279496821072117767', '<p>这是信息的详情，描述信息的主题和出售的商品，要图文并茂，记得留下你的联系方式，最好同城自提奥。</p>\n<p><img src=\"http://localhost:8000/store/2023/05/01223622CgZwfUC1.jpg\" width=\"569\" height=\"758\" /></p>\n<p>一个真实的图，胜过千言万语，此处省略五百字。</p>\n<p>联系方式：地球村五里屯，老子徒弟张天封。</p>\n<p>要求：地球村同城自提，非诚勿扰。</p>', '这是信息的标题可以写一个重点突出简明易懂', null, 'publish', null, null, 'bieming1', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-28 00:58:25', '127.0.0.1', '1', '2023-05-01 22:38:52', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279497240485740546', '<p><strong>通过信息你可以了解到你寻找的事物在哪里。</strong></p>\n<p>这是内容区域。</p>\n<p>可以编辑多媒体内容。</p>\n<p>&nbsp;</p>', '信息中人们生产生活中不可或缺的行动指南', null, 'publish', null, null, 'test', '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-28 01:00:05', '127.0.0.1', '1', '2023-05-02 00:04:28', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279521285574934534', null, '2023-04-28 02:35:37', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-28 02:35:38', '127.0.0.1', '1', '2023-04-28 02:35:38', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279521307418869765', null, '2023-04-28 02:35:42', null, 'inherit', null, null, null, '279019922097094657', 'chapter', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-28 02:35:43', '127.0.0.1', '1', '2023-04-28 02:35:43', '127.0.0.1', 'normal', '1');
INSERT INTO `k_pubs` VALUES ('279532165675008001', null, 'test', null, 'publish', null, null, null, '0', 'info', '1533544727530094592', '0', null, '0', '0', '0', null, '1', '2023-04-28 03:18:52', '127.0.0.1', '1', '2023-04-28 03:18:52', '127.0.0.1', 'deleted', '1');
INSERT INTO `k_pubs` VALUES ('280910649282445320', null, 'file', null, 'inherit', null, null, null, '279496821072117767', 'attachment', '1533544727530094592', '0', 'image', '0', '0', '0', null, '1', '2023-05-01 22:36:28', '127.0.0.1', '1', '2023-05-01 22:36:28', '127.0.0.1', 'normal', '1');

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
                                 KEY `term_object_id` (`object_id`) USING BTREE,
                                 KEY `term_object_type` (`object_id`,`term_type_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of k_term_object
-- ----------------------------
INSERT INTO `k_term_object` VALUES ('278645100359892994', '278645100074680329', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('278645100494110721', '278645100074680329', '278644086865051658', '0');
INSERT INTO `k_term_object` VALUES ('278645100498305024', '278645100074680329', '278645100447973379', '0');
INSERT INTO `k_term_object` VALUES ('278645100502499330', '278645100074680329', '71289715400818698', '0');
INSERT INTO `k_term_object` VALUES ('279019923330220033', '279019922097094657', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279019923850313730', '279019922097094657', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279019923854508037', '279019922097094657', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279020188989046793', '279020188926132232', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279020189026795526', '279020188926132232', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279020189026795527', '279020188926132232', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279020610017476616', '279020608851460100', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279020610055225348', '279020608851460100', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279020610059419653', '279020608851460100', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279020889265848331', '279020889219710981', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279020889299402757', '279020889219710981', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279020889303597060', '279020889219710981', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279020939857543168', '279020939807211529', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279020939891097602', '279020939807211529', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279020939895291912', '279020939807211529', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279020970094280707', '279020970048143369', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279020970127835139', '279020970048143369', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279020970132029446', '279020970048143369', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021081272696834', '279021081096536068', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021081306251274', '279021081096536068', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021081310445569', '279021081096536068', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021082002505732', '279021081956368386', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021082027671555', '279021081956368386', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021082031865861', '279021081956368386', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021084833660932', '279021084787523594', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021084863021067', '279021084787523594', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021084867215360', '279021084787523594', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021086993727489', '279021086947590147', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021087014699009', '279021086947590147', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021087018893315', '279021086947590147', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021087849365515', '279021087803228166', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021087887114249', '279021087803228166', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021087891308545', '279021087803228166', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021088071663627', '279021088025526281', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021088101023755', '279021088025526281', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021088105218056', '279021088025526281', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021088830832647', '279021088780500998', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021088855998468', '279021088780500998', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021088860192770', '279021088780500998', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021089736802313', '279021089694859270', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021089770356736', '279021089694859270', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021089774551045', '279021089694859270', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021099635359746', '279021099580833802', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021099668914185', '279021099580833802', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021099668914186', '279021099580833802', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021101698957318', '279021101648625670', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021101732511755', '279021101648625670', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021101736706059', '279021101648625670', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021102575566850', '279021102529429512', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021102818836483', '279021102529429512', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021102823030788', '279021102529429512', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021102881751050', '279021102839808000', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021102919499787', '279021102839808000', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021102923694088', '279021102839808000', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021103598977032', '279021103557033986', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021103632531463', '279021103557033986', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021103636725765', '279021103557033986', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021104161013765', '279021104119070728', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021104198762496', '279021104119070728', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021104198762497', '279021104119070728', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021105113120772', '279021105071177732', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021105142480903', '279021105071177732', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021105146675200', '279021105071177732', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021105809375239', '279021105759043584', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021105838735361', '279021105759043584', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021105842929669', '279021105759043584', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021106677596171', '279021106639847427', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021106706956296', '279021106639847427', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021106711150602', '279021106639847427', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021107810058248', '279021107768115200', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021107835224068', '279021107768115200', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021107839418372', '279021107768115200', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021108766359557', '279021108724416515', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021108791525379', '279021108724416515', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021108795719689', '279021108724416515', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279021109412282374', '279021109257093124', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279021109445836802', '279021109257093124', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279021109450031106', '279021109257093124', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279036753599971338', '279036753583194115', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279439800624922624', '279439799488266251', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279439800905940992', '279439799488266251', '279439800809472000', '0');
INSERT INTO `k_term_object` VALUES ('279439800910135306', '279439799488266251', '278644086865051658', '0');
INSERT INTO `k_term_object` VALUES ('279440246043230215', '279440244893990923', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279496821101477895', '279496821072117767', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279497240506712067', '279497240485740546', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279521285717540874', '279521285574934534', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279521285801426947', '279521285574934534', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279521285801426948', '279521285574934534', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279521307481784330', '279521307418869765', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('279521307515338763', '279521307418869765', '279019923711901705', '0');
INSERT INTO `k_term_object` VALUES ('279521307532115968', '279521307418869765', '279019923695124487', '0');
INSERT INTO `k_term_object` VALUES ('279532165704368133', '279532165675008001', '1520551727518629888', '0');
INSERT INTO `k_term_object` VALUES ('280909763252502537', '279496821072117767', '279439800809472000', '0');
INSERT INTO `k_term_object` VALUES ('280909763252502538', '279496821072117767', '278644086865051658', '0');

-- ----------------------------
-- Table structure for `k_term_type`
-- ----------------------------
DROP TABLE IF EXISTS `k_term_type`;
CREATE TABLE `k_term_type` (
                               `id` bigint(20) unsigned NOT NULL,
                               `term_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                               `class_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
                               `description` longtext COLLATE utf8mb4_unicode_ci,
                               `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                               `count` bigint(20) unsigned NOT NULL DEFAULT '0',
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
INSERT INTO `k_term_type` VALUES ('110143305179381764', '110143305179381764', 'category', null, '1522026041568837640', '1');
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
INSERT INTO `k_term_type` VALUES ('255837126054821891', '255837126054821891', 'category', null, '1520551727518629888', '0');
INSERT INTO `k_term_type` VALUES ('255837278387748875', '255837278387748875', 'category', null, '1520551727518629888', '0');
INSERT INTO `k_term_type` VALUES ('255934838326607873', '255934838326607873', 'category', null, '255837126054821891', '0');
INSERT INTO `k_term_type` VALUES ('255937404745728001', '255937404745728001', 'category', null, '1520552344769183746', '0');
INSERT INTO `k_term_type` VALUES ('256180863515672580', '256180863515672580', 'category', null, '255837126054821891', '0');
INSERT INTO `k_term_type` VALUES ('256182380301828103', '256182380301828103', 'category', null, '255837278387748875', '1');
INSERT INTO `k_term_type` VALUES ('256182477127335938', '256182477127335938', 'category', null, '255837278387748875', '0');
INSERT INTO `k_term_type` VALUES ('256182549105786886', '256182549105786886', 'category', null, '256182380301828103', '0');
INSERT INTO `k_term_type` VALUES ('256182788319526912', '256182788319526912', 'category', null, '1', '0');
INSERT INTO `k_term_type` VALUES ('258055544854200322', '258055544854200322', 'tag', null, '0', '1');
INSERT INTO `k_term_type` VALUES ('258055544875171851', '258055544875171851', 'tag', null, '0', '1');
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
                           `slug` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `info_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1',
                           `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '1',
                           `display_order` int(10) DEFAULT NULL,
                           `create_by` bigint(20) unsigned DEFAULT NULL,
                           `create_time` datetime DEFAULT NULL,
                           `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `update_by` bigint(20) unsigned DEFAULT NULL,
                           `update_time` datetime DEFAULT NULL,
                           `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `versions` int(10) DEFAULT NULL,
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
INSERT INTO `k_terms` VALUES ('255837126054821891', 'Java技术', 'javajs', '1', '1', '1', '1', '2023-02-21 18:03:14', '127.0.0.1', '1', '2023-02-21 18:03:14', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('255837278387748875', '云|平台', 'yunpingt', '1', '1', '2', '1', '2023-02-21 18:03:50', '127.0.0.1', '1', '2023-02-21 18:03:50', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('255934838326607873', '重孙节点', 'java-child', '1', '1', '1', '1', '2023-02-22 00:31:31', '127.0.0.1', '1', '2023-02-22 16:50:29', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('255937404745728001', '软件转让', 'softzr', '1', '1', '1', '1', '2023-02-22 00:41:42', '127.0.0.1', '1', '2023-02-22 00:41:42', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('256180863515672580', '重孙兄弟', 'csxd', '1', '1', '2', '1', '2023-02-22 16:49:07', '127.0.0.1', '1', '2023-02-22 16:49:07', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('256182380301828103', '云孙子', 'ysz', '1', '1', '1', '1', '2023-02-22 16:55:09', '127.0.0.1', '1', '2023-02-22 16:55:09', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('256182477127335938', '云兄弟', 'yxd', '1', '1', '2', '1', '2023-02-22 16:55:32', '127.0.0.1', '1', '2023-02-22 16:55:32', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('256182549105786886', '他大爷的', 'tdyd', '1', '1', '1', '1', '2023-02-22 16:55:49', '127.0.0.1', '1', '2023-02-22 16:55:49', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('256182788319526912', 'iPad', 'ipad', '1', '1', '5', '1', '2023-02-22 16:56:46', '127.0.0.1', '1', '2023-02-22 16:56:46', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('258055544854200322', '电动车', 'diandongche', '1', '1', null, '1', '2023-02-27 20:58:26', '127.0.0.1', '1', '2023-02-27 20:58:26', '127.0.0.1', 'normal', '1');
INSERT INTO `k_terms` VALUES ('258055544875171851', '雅迪', 'yadi', '1', '1', null, '1', '2023-02-27 20:58:26', '127.0.0.1', '1', '2023-02-27 20:58:26', '127.0.0.1', 'normal', '1');
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
                                          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                          `user_id` bigint(20) unsigned DEFAULT NULL,
                                          `bind_account` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `third_domain` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_by` bigint(20) unsigned DEFAULT NULL,
                                          `create_time` datetime DEFAULT NULL,
                                          `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `update_by` bigint(20) unsigned DEFAULT NULL,
                                          `update_time` datetime DEFAULT NULL,
                                          `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `versions` int(10) DEFAULT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of wo_account_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_app`
-- ----------------------------
DROP TABLE IF EXISTS `wo_app`;
CREATE TABLE `wo_app` (
                          `id` bigint(20) unsigned NOT NULL,
                          `app_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `app_secret` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `app_code` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `app_desc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `app_type` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `com_id` bigint(20) unsigned DEFAULT NULL,
                          `is_valid` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_by` bigint(20) unsigned DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `update_by` bigint(20) unsigned DEFAULT NULL,
                          `update_time` datetime DEFAULT NULL,
                          `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `versions` int(10) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `app_code` (`app_code`),
                          KEY `app_type` (`app_type`),
                          KEY `app_com_id` (`com_id`),
                          KEY `app_is_valid_del` (`is_valid`,`delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
                                   `id` bigint(20) unsigned NOT NULL,
                                   `arch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `arch_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `arch_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `com_id` bigint(20) unsigned DEFAULT NULL,
                                   `parent_id` bigint(20) unsigned DEFAULT NULL,
                                   `display_order` int(10) DEFAULT NULL,
                                   `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `create_by` bigint(20) unsigned DEFAULT NULL,
                                   `create_time` datetime DEFAULT NULL,
                                   `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `update_by` bigint(20) unsigned DEFAULT NULL,
                                   `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `update_time` datetime DEFAULT NULL,
                                   `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                   `versions` int(10) DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `un_com_arch` (`arch_code`,`com_id`),
                                   KEY `arch_code` (`arch_code`),
                                   KEY `arch_com_id` (`com_id`),
                                   KEY `arch_parent_id` (`parent_id`),
                                   KEY `arch_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                                `id` bigint(20) unsigned NOT NULL,
                                `role_id` bigint(20) unsigned DEFAULT NULL,
                                `resource_id` bigint(20) unsigned DEFAULT NULL,
                                `app_id` bigint(20) unsigned DEFAULT NULL,
                                `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `create_by` bigint(20) unsigned DEFAULT NULL,
                                `create_time` datetime DEFAULT NULL,
                                `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `update_by` bigint(20) unsigned DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                `versions` int(10) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `auth_role_id` (`role_id`),
                                KEY `auth_res_id` (`resource_id`),
                                KEY `auth_app_id` (`app_id`),
                                KEY `auth_is_valid_del` (`is_valid`,`delete_flag`),
                                KEY `auth_role_res_app` (`role_id`,`resource_id`,`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                               `id` bigint(20) unsigned NOT NULL,
                               `user_id` bigint(20) unsigned DEFAULT NULL,
                               `com_id` bigint(20) unsigned DEFAULT NULL,
                               `is_main` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_by` bigint(20) unsigned DEFAULT NULL,
                               `create_time` datetime DEFAULT NULL,
                               `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `update_by` bigint(20) unsigned DEFAULT NULL,
                               `update_time` datetime DEFAULT NULL,
                               `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `versions` int(10) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `com_user_id` (`user_id`),
                               KEY `com_id` (`com_id`),
                               KEY `com_is_main` (`is_main`),
                               KEY `com_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                              `id` bigint(20) unsigned NOT NULL,
                              `com_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `com_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `com_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `parent_id` bigint(20) unsigned DEFAULT NULL,
                              `display_order` int(10) DEFAULT NULL,
                              `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `create_by` bigint(20) unsigned DEFAULT NULL,
                              `create_time` datetime DEFAULT NULL,
                              `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `update_by` bigint(20) unsigned DEFAULT NULL,
                              `update_time` datetime DEFAULT NULL,
                              `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `versions` int(10) DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `un_com_code` (`com_code`),
                              KEY `com_parent_id` (`parent_id`),
                              KEY `com_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                             `id` bigint(20) unsigned NOT NULL,
                             `com_id` bigint(20) unsigned DEFAULT NULL,
                             `site_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `second_domain` varchar(255) DEFAULT NULL,
                             `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `site_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `site_logo` varchar(50) DEFAULT NULL,
                             `favicon` varchar(50) DEFAULT NULL,
                             `site_title` varchar(255) DEFAULT NULL,
                             `site_keyword` varchar(500) DEFAULT NULL,
                             `site_description` varchar(500) DEFAULT NULL,
                             `slogan` varchar(100) DEFAULT NULL,
                             `foot` text,
                             `flink` text,
                             `copy` text,
                             `parent_id` bigint(20) DEFAULT NULL,
                             `display_order` int(10) DEFAULT NULL,
                             `cname_domain` varchar(255) DEFAULT NULL,
                             `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `create_by` bigint(20) unsigned DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `update_by` bigint(20) unsigned DEFAULT NULL,
                             `update_time` datetime DEFAULT NULL,
                             `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `versions` int(10) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uni_idx_site_domain` (`site_domain`),
                             UNIQUE KEY `uni_second_dom` (`second_domain`),
                             KEY `domain_com_id` (`com_id`),
                             KEY `dom_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_domain
-- ----------------------------
INSERT INTO `wo_domain` VALUES ('1', '0', 'wldos.com', 'www', 'WLDOS', 'http://www.wldos.com', '/logo-wldos.svg', null, 'WLDOS互联网开放运营系统', '业务中台|多租户|SaaS', 'WLDOS互联网开放运营系统是一个业务中台的最佳实践', null, '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-5730-355</strong></span>\r\n</p>\r\n<p>Q Q： 583716365 306991142</p>\r\n<p>邮箱： support@zhiletu.com</p>\r\n<p>地址： 山东省济南市长清区海棠路5555</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\r\n<a href=\"https://www.xiupu.net\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\r\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\r\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37011302000311\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\r\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> 鲁公网安备 37011302000311号</a>&nbsp;\r\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备20011831号</a>\r\n<a href=\"https://www.zhiletu.com/privacy\">法律声明</a> | <a href=\"https://www.zhiletu.com/privacy\">隐私协议</a> | Copyright © 2021\r\n<a href=\"https://www.zhiletu.com/\" rel=\"nofollow\">智乐兔</a> 版权所有\r\n</p>', null, '2', null, '1', '1', '2021-08-02 18:39:15', '192.168.1.23', '1', '2021-09-12 21:08:29', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('93037725495246854', '1508132284859596808', 'tenant.com', 'tenant', '租户域演示', 'http://www.tenant.com', '/logo-wldos.svg', '', '租户域演示网站', '租户域演示', '租户域演示', null, null, null, null, null, '1', 't.com', '1', '92829405966680072', '2021-11-29 12:16:32', '192.168.1.23', '1', '2023-03-24 17:37:53', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('134430616675074056', '0', 'test.com', 'test', 'test', 'test.com', null, null, 'test', 'test', 'test', null, null, null, null, null, '7', 'test', '1', '1', '2022-03-23 17:37:07', '192.168.1.23', '1', '2022-03-23 17:37:07', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('134434749914202120', '0', 'tst.com', 'tst', 'tst', 'tst.com', '/202203/23175316LUhpaUfQ.svg', null, 'tst', 'tst', 'sts', null, null, null, null, null, '8', null, '1', '1', '2022-03-23 17:53:32', '192.168.1.23', '1', '2022-03-23 17:53:32', '192.168.1.23', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('1533544727530094592', '0', 'localhost', 'localhost', 'WLDOS-KPayCMS', 'http://localhost:8000', '/logo-wldos.svg', '/2023/03/03004258r0wEVSCe.png', 'WLDOS云应用支撑平台_云物互联驱动', 'WLDOS|内容付费|多租户|多站|SaaS|微服务|serviceMesh|云管端', '基于WLDOS搭建云物互联应用', 'WLDOS云应用支撑平台 云物互联驱动', '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-ABCD-EFG</strong></span>\r\n</p>\r\n<p>Q Q： 583ABC365 30DEFQ142</p>\r\n<p>邮箱： support#abcdefg.com</p>\r\n<p>服务： 周一至周六 9:00~17:30</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\n<a href=\"https://www.xiupu.cc\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37xxxx20xxxxx\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> X公网安备 3701xxx20xxxxx号</a>&nbsp;\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备2xx1xxxx号</a>\n<a href=\"https://www.wldos.com/privacy\">法律声明</a> | <a href=\"https://www.wldos.com/privacy\">隐私协议</a> | Copyright © 2022\n<a href=\"https://www.wldos.com/\" rel=\"nofollow\">WLDOS</a> 版权所有\n</p>', null, '6', 'god.com,192.168.1.23', '1', '1', '2021-08-06 18:11:32', '1', '1', '2023-04-09 21:17:12', '127.0.0.1', 'normal', '1');
INSERT INTO `wo_domain` VALUES ('1533985924929208330', '0', 'xiupu.cc', 'xiupu', '嗅谱网', 'https://www.xiupu.net', '', '', '嗅谱网_找谱·写谱·出谱-嗅谱·秀谱·说谱', '找谱·写谱·出谱-嗅谱·秀谱·说谱', '一个和各种谱打交道的靠谱网站', null, '<div>\r\n<h3>关于本站</h3>\r\n<div>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>会员通道</h3>\r\n<div>\r\n<p>\r\n<a href=\"https://www.wldos.com/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.wldos.com/register-2\" rel=\"nofollow\">注册</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/account\" rel=\"nofollow\">个人中心</a></p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\r\n</p>\r\n<p><a href=\"https://www.wldos.com/archives-category/blog\">技术博客</a></p>\r\n<p><a href=\"https://www.wldos.com/help\">会员帮助</a></p>\r\n</div>\r\n</div>\r\n<div><h3>服务领域</h3>\r\n<div>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prosite\">网站建设</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/protools\">软件工具</a>\r\n</p>\r\n<p>\r\n  <a href=\"https://www.wldos.com/archives-category/shopproduct/prodev\">开发框架</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/proengine\">应用引擎</a>\r\n</p>\r\n<p><a\r\n  href=\"https://www.wldos.com/archives-category/shopproduct/resolution\">解决方案</a>\r\n</p>\r\n</div>\r\n</div>\r\n<div>\r\n<h3>官方微信</h3>\r\n<div>\r\n<p>\r\n  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\r\n       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\r\n</p>\r\n</div>\r\n</div>\r\n<div style=\"padding:0; width:28%;\">\r\n<h3>联系方式</h3>\r\n<div>\r\n<p>\r\n  <span><strong>1566-5730-355</strong></span>\r\n</p>\r\n<p>Q Q： 583716365 306991142</p>\r\n<p>邮箱： support@zhiletu.com</p>\r\n<p>地址： 山东省济南市长清区海棠路5555</p>\r\n<p>&nbsp;</p>\r\n<p>\r\n  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\r\n  </a>\r\n  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\r\n    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\r\n  </a>\r\n  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\r\n  </a>\r\n  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=zhiletu.com&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\r\n  </a>\r\n  <a href=\"mailto:support@zhiletu.com\" rel=\"noopener nofollow noreferrer\">\r\n    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\r\n  </a>\r\n</p>\r\n</div>\r\n</div>', '<strong>友情链接：</strong>\r\n<a href=\"https://www.xiupu.net\" target=\"_blank\" rel=\"noopener noreferrer\">嗅谱网</a>\r\n<a href=\"http://www.wldos.com\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>', '<p>\r\n<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37011302000311\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\r\n<img src=\"https://www.wldos.com/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> 鲁公网安备 37011302000311号</a>&nbsp;\r\n<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">鲁ICP备20011831号</a>\r\n<a href=\"https://www.zhiletu.com/privacy\">法律声明</a> | <a href=\"https://www.zhiletu.com/privacy\">隐私协议</a> | Copyright © 2021\r\n<a href=\"https://www.zhiletu.com/\" rel=\"nofollow\">智乐兔</a> 版权所有\r\n</p>', null, '4', null, '0', '1', '2021-08-03 23:52:25', '192.168.1.23', '1', '2023-03-22 22:29:26', '127.0.0.1', 'normal', '1');

-- ----------------------------
-- Table structure for `wo_domain_app`
-- ----------------------------
DROP TABLE IF EXISTS `wo_domain_app`;
CREATE TABLE `wo_domain_app` (
                                 `id` bigint(21) unsigned NOT NULL,
                                 `app_id` bigint(21) unsigned DEFAULT NULL,
                                 `domain_id` bigint(21) DEFAULT NULL,
                                 `com_id` bigint(21) unsigned DEFAULT NULL,
                                 `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `create_by` bigint(20) unsigned DEFAULT NULL,
                                 `create_time` datetime DEFAULT NULL,
                                 `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `update_by` bigint(20) unsigned DEFAULT NULL,
                                 `update_time` datetime DEFAULT NULL,
                                 `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `versions` int(10) DEFAULT NULL,
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
                                      `module_name` varchar(50) NOT NULL DEFAULT 'static',
                                      `resource_id` bigint(20) unsigned NOT NULL,
                                      `app_id` bigint(20) unsigned DEFAULT NULL,
                                      `term_type_id` bigint(20) DEFAULT '0',
                                      `domain_id` bigint(20) unsigned NOT NULL,
                                      `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_by` bigint(20) unsigned DEFAULT NULL,
                                      `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `update_by` bigint(20) unsigned DEFAULT NULL,
                                      `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `update_time` datetime DEFAULT NULL,
                                      `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                      `versions` int(10) unsigned DEFAULT NULL,
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
INSERT INTO `wo_domain_resource` VALUES ('6', 'admin', '100', '1506113043159498757', '0', '1533544727530094592', '1', '本地开发环境', '1', '123', '2021-09-24 01:37:34', '1', '111', '2021-09-24 01:37:42', 'normal', '0');
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
                           KEY `file_is_valid_del` (`is_valid`,`delete_flag`) USING BTREE,
                           KEY `file_mime_type` (`mime_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_file
-- ----------------------------
INSERT INTO `wo_file` VALUES ('1', 'logo-wldos.png', '/logo-wldos.png', 'image/png', '1', '1', '2021-09-06 13:49:41', '192.168.1.23', '1', '2021-09-06 13:49:57', '192.168.1.23', 'normal', '0');
INSERT INTO `wo_file` VALUES ('278691924353400836', '微信图片_20201101001849.jpg', '/2023/04/251940028L135dId.jpg', 'image/jpeg', '1', '1', '2023-04-25 19:40:03', '127.0.0.1', '1', '2023-04-25 19:40:03', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279019618484011011', '1.jpg', '/2023/04/261722102KkXbfWu.jpg', 'image/jpeg', '1', '1', '2023-04-26 17:22:11', '127.0.0.1', '1', '2023-04-26 17:22:11', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279019658082435076', '1.jpg', '/2023/04/26172220XNamhlAP.jpg', 'image/jpeg', '1', '1', '2023-04-26 17:22:20', '127.0.0.1', '1', '2023-04-26 17:22:20', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279019692148572166', '1.jpg', '/2023/04/26172228U7XkZpq6.jpg', 'image/jpeg', '1', '1', '2023-04-26 17:22:29', '127.0.0.1', '1', '2023-04-26 17:22:29', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279427261052665857', 'mceu_13278027911682598120279.jpg', '/2023/04/27202200y1hCTrJK.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:22:01', '127.0.0.1', '1', '2023-04-27 20:22:01', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279427560479834123', 'imagetools0.jpg', '/2023/04/272023118Sr7RKYm.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:23:12', '127.0.0.1', '1', '2023-04-27 20:23:12', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279427883516739587', 'imagetools2.jpg', '/2023/04/27202428eqGqQIHa.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:24:29', '127.0.0.1', '1', '2023-04-27 20:24:29', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279433041936039941', '1.jpg', '/2023/04/27204458gk70kINw.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:44:59', '127.0.0.1', '1', '2023-04-27 20:44:59', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279433120906395658', 'imagetools3.jpg', '/2023/04/272045178fZMXiwv.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:45:18', '127.0.0.1', '1', '2023-04-27 20:45:18', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279434937006473227', 'mceu_96566091911682599948509.jpg', '/2023/04/27205230DTjcqTGj.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:52:31', '127.0.0.1', '1', '2023-04-27 20:52:31', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279435150861451275', 'mceu_86668192121682600000117.jpg', '/2023/04/27205321BFF648y3.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:53:22', '127.0.0.1', '1', '2023-04-27 20:53:22', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279435861590458374', 'imagetools4.jpg', '/2023/04/27205611NFJErASO.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:56:11', '127.0.0.1', '1', '2023-04-27 20:56:11', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279435915323686917', 'imagetools5.jpg', '/2023/04/27205623KRNjyg3d.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:56:24', '127.0.0.1', '1', '2023-04-27 20:56:24', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279435992834424833', 'imagetools6.jpg', '/2023/04/27205642XD5bS8RS.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:56:42', '127.0.0.1', '1', '2023-04-27 20:56:42', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279436506686996481', '1.jpg', '/2023/04/272058448ayVrKkk.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:58:45', '127.0.0.1', '1', '2023-04-27 20:58:45', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279436579252649984', 'imagetools0.jpg', '/2023/04/27205902sWJNGyCg.jpg', 'image/jpeg', '1', '1', '2023-04-27 20:59:02', '127.0.0.1', '1', '2023-04-27 20:59:02', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279439726780006411', 'QQ图片20200729123735.jpg', '/2023/04/27211132Yfd3EyvO.jpg', 'image/jpeg', '1', '1', '2023-04-27 21:11:33', '127.0.0.1', '1', '2023-04-27 21:11:33', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279439778458025993', '32.jpg', '/2023/04/27211144XJEexPLS.jpg', 'image/jpeg', '1', '1', '2023-04-27 21:11:45', '127.0.0.1', '1', '2023-04-27 21:11:45', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279440375739498501', '1.jpg', '/2023/04/272114076gumFYJC.jpg', 'image/jpeg', '1', '1', '2023-04-27 21:14:07', '127.0.0.1', '1', '2023-04-27 21:14:07', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279440442445709322', 'imagetools0.jpg', '/2023/04/27211423UmumWhEH.jpg', 'image/jpeg', '1', '1', '2023-04-27 21:14:23', '127.0.0.1', '1', '2023-04-27 21:14:23', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279475360810844161', 'mceu_51644307811682609587883.jpg', '/2023/04/27233308HB8BrDRc.jpg', 'image/jpeg', '1', '1', '2023-04-27 23:33:08', '127.0.0.1', '1', '2023-04-27 23:33:08', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('279491769397854214', 'mceu_30519060711682613500250.jpg', '/2023/04/28003820karQeT8V.jpg', 'image/jpeg', '1', '1', '2023-04-28 00:38:21', '127.0.0.1', '1', '2023-04-28 00:38:21', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_file` VALUES ('280910627539173378', 'mceu_53578573011682951782370.jpg', '/2023/05/01223622CgZwfUC1.jpg', 'image/jpeg', '1', '1', '2023-05-01 22:36:23', '127.0.0.1', '1', '2023-05-01 22:36:23', '127.0.0.1', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_mail`
-- ----------------------------
DROP TABLE IF EXISTS `wo_mail`;
CREATE TABLE `wo_mail` (
                           `id` bigint(20) NOT NULL,
                           `from_addr` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `to_addr` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `content` text COLLATE utf8mb4_unicode_ci,
                           `status` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `result` text COLLATE utf8mb4_unicode_ci,
                           `create_by` bigint(20) unsigned DEFAULT '0',
                           `create_time` datetime DEFAULT NULL,
                           `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `update_by` bigint(20) unsigned DEFAULT NULL,
                           `update_time` datetime DEFAULT NULL,
                           `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `versions` int(10) DEFAULT NULL,
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
INSERT INTO `wo_mail` VALUES ('268888154186629121', 'wldos.com@88.com', '3385115357@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"http://http://localhost:8000/user/active/verify=268888145382785029\" target=\"_blank\">激活账号</a>http://http://localhost:8000/user/active/verify=268888145382785029<br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '268888145382785029', '2023-03-29 18:23:22', '127.0.0.1', '268888145382785029', '2023-03-29 18:23:23', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('268914110947311625', 'wldos.com@88.com', '2902825652@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"http://localhost:8000/user/active/verify=268914102432874506\" target=\"_blank\">激活账号http://localhost:8000/user/active/verify=268914102432874506</a><br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '268914102432874506', '2023-03-29 20:06:30', '127.0.0.1', '268914102432874506', '2023-03-29 20:06:32', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('268915702396272643', 'wldos.com@88.com', '2902825652@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"http://localhost:8000/user/active/verify=268915694989131787\" target=\"_blank\">激活账号http://localhost:8000/user/active/verify=268915694989131787</a><br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '268915694989131787', '2023-03-29 20:12:50', '127.0.0.1', '268915694989131787', '2023-03-29 20:12:50', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('268929746314379268', 'wldos.com@88.com', '2902825652@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"http://localhost:8000/user/active/verify=268929738777214984\" target=\"_blank\">激活账号http://localhost:8000/user/active/verify=268929738777214984</a><br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '268929738777214984', '2023-03-29 21:08:38', '127.0.0.1', '268929738777214984', '2023-03-29 21:08:39', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_mail` VALUES ('269269459126632455', 'wldos.com@88.com', '2902825652@qq.com', '<!DOCTYPE html> <html lang=\"zh\"><head><meta charset=\"UTF-8\"/><title>账户激活</title></head><body>您好，这是验证邮件，请点击下面的链接完成验证，</body></html><a href=\"http://localhost:8000/user/active/verify=269269451643994119\" target=\"_blank\">激活账号http://localhost:8000/user/active/verify=269269451643994119</a><br/>如果以上链接无法点击，请将它复制到您的浏览器地址栏中进入访问，该链接24小时内有效。', '1', null, '269269451643994119', '2023-03-30 19:38:32', '127.0.0.1', '269269451643994119', '2023-03-30 19:38:34', '127.0.0.1', 'normal', '0');

-- ----------------------------
-- Table structure for `wo_oauth_login_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_oauth_login_user`;
CREATE TABLE `wo_oauth_login_user` (
                                       `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                       `user_id` bigint(20) unsigned DEFAULT NULL,
                                       `oauth_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `open_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `union_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `create_by` bigint(20) unsigned DEFAULT NULL,
                                       `create_time` datetime DEFAULT NULL,
                                       `create_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `update_by` bigint(20) unsigned DEFAULT NULL,
                                       `update_time` datetime DEFAULT NULL,
                                       `update_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `delete_flag` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                       `versions` int(10) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=214858324877426693 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
                              `option_name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `option_value` longtext COLLATE utf8mb4_unicode_ci,
                              `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `option_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'no',
                              `app_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `option_name` (`option_key`),
                              KEY `options_app_type` (`option_type`),
                              KEY `opetions_app_code` (`app_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=269981894250774530 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of wo_options
-- ----------------------------
INSERT INTO `wo_options` VALUES ('1', 'oauth_login_wechat', null, '{\"appId\":\"zldfsdf23423\",\"appSecret\":\"51ea12330acslipwefjsdfesf0934434sf\",\"redirectUri\":\"http://www.wldos.com\",\"scope\":\"snsapi_login\",\"codeUri\":\"https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect\",\"accessTokenUri\":\"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code\",\"refreshTokenUri\":\"\",\"userInfoUri\":\"https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s\"}', null, 'normal', null);
INSERT INTO `wo_options` VALUES ('45', 'default_group', null, 'freeuser', null, 'normal', null);
INSERT INTO `wo_options` VALUES ('149', 'un_active_group', null, 'un_active', null, 'normal', null);
INSERT INTO `wo_options` VALUES ('253373739874041856', 'oauth_login_qq', null, '{\"appId\":\"10sdfsf6\",\"appSecret\":\"sdfsdfwewfsdsdfdsV\",\"redirectUri\":\"https://www.wldos.com\",\"scope\":\"get_user_info\",\"codeUri\":\"https://graph.qq.com/oauth2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=%s\",\"accessTokenUri\":\"https://graph.qq.com/oauth2.0/token?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s\",\"refreshTokenUri\":null,\"userInfoUri\":\"https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s\"}', null, 'normal', null);
INSERT INTO `wo_options` VALUES ('253385974528786442', 'oauth_login_weibo', null, '{\"appId\":\"1werwerwe\",\"appSecret\":\"3sfdsdfsfsdfsdfsfwvcxsdf\",\"redirectUri\":\"https://www.wldos.com\",\"scope\":\"all\",\"codeUri\":\"https://api.weibo.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=%s\",\"accessTokenUri\":\"https://api.weibo.com/oauth2/access_token?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s\",\"refreshTokenUri\":null,\"userInfoUri\":\"https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s\"}', null, 'normal', null);
INSERT INTO `wo_options` VALUES ('268459378072010761', 'wldos_platform_domain', null, 'localhost', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268459378084593673', 'wldos_req_protocol', null, 'http', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268459378097176581', 'wldos_cms_defaultTermTypeId', null, '1', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268459378109759494', 'wldos_cms_content_maxLength', null, '33031', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268459378122342403', 'wldos_cms_tag_maxTagNum', null, '5', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268459378134925319', 'wldos_cms_tag_tagLength', null, '30', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507090779815937', 'wldos_system_multitenancy_switch', null, 'true', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507090792398857', 'wldos_system_multidomain_switch', null, 'true', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507343822176260', 'wldos_platform_user_register_emailaction', null, 'true', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507343834759168', 'spring_mail_host', null, 'smtp.xx.com', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507343847342087', 'spring_mail_username', null, 'yourname', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507343859924993', 'spring_mail_password', null, 'wixxxxxxx', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268507343876702209', 'wldos_mail_fromMail_addr', null, 'yourname@xxx.com', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268563415123542023', 'wldos_cms_comment_audit', null, 'true', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('268993001648996359', 'wldos_file_store_path', null, 'E:\\\\Temp', null, 'auto_reload', 'sys_option');
INSERT INTO `wo_options` VALUES ('269981894250774529', 'wldos_platform_adminEmail', null, '306991142@qq.com', null, 'auto_reload', 'sys_option');

-- ----------------------------
-- Table structure for `wo_org`
-- ----------------------------
DROP TABLE IF EXISTS `wo_org`;
CREATE TABLE `wo_org` (
                          `id` bigint(20) unsigned NOT NULL,
                          `org_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `org_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `org_logo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `org_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `arch_id` bigint(20) unsigned DEFAULT NULL,
                          `com_id` bigint(20) unsigned DEFAULT NULL,
                          `parent_id` bigint(20) unsigned DEFAULT NULL,
                          `display_order` int(10) DEFAULT NULL,
                          `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `create_by` bigint(20) unsigned DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `update_by` bigint(20) unsigned DEFAULT NULL,
                          `update_time` datetime DEFAULT NULL,
                          `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `versions` int(10) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `un_com_arch_org` (`org_code`,`arch_id`,`com_id`),
                          KEY `org_type` (`org_type`),
                          KEY `org_arch_id` (`arch_id`),
                          KEY `org_com_id` (`com_id`),
                          KEY `org_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                               `id` bigint(20) unsigned NOT NULL,
                               `user_id` bigint(20) unsigned DEFAULT NULL,
                               `user_com_id` bigint(20) unsigned DEFAULT NULL,
                               `org_id` bigint(20) unsigned DEFAULT NULL,
                               `arch_id` bigint(20) unsigned DEFAULT NULL,
                               `com_id` bigint(20) unsigned DEFAULT NULL,
                               `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_by` bigint(20) unsigned DEFAULT NULL,
                               `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_time` datetime DEFAULT NULL,
                               `update_by` bigint(20) unsigned DEFAULT NULL,
                               `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `update_time` datetime DEFAULT NULL,
                               `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
                               `versions` int(10) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `org_user_id` (`user_id`),
                               KEY `org_id` (`org_id`),
                               KEY `org_user_arch` (`arch_id`),
                               KEY `org_user_com` (`com_id`),
                               KEY `org_user_is_valid_del` (`is_valid`,`delete_flag`),
                               KEY `org_user_arch_com` (`user_id`,`user_com_id`,`org_id`,`arch_id`,`com_id`) USING BTREE,
                               KEY `org_u_com_id` (`user_com_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
INSERT INTO `wo_org_user` VALUES ('268888153029001220', '268888145382785029', null, '200', '0', '0', '1', '268888145382785029', '127.0.0.1', '2023-03-29 18:23:21', '268888145382785029', '127.0.0.1', '2023-03-29 18:23:21', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('268914109286367238', '268914102432874506', null, '300', '0', '0', '1', '268914102432874506', '127.0.0.1', '2023-03-29 20:06:30', '268914102432874506', '127.0.0.1', '2023-03-29 20:06:30', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('268915701775515651', '268915694989131787', null, '200', '0', '0', '1', '268915694989131787', '127.0.0.1', '2023-03-29 20:12:50', '268915694989131787', '127.0.0.1', '2023-03-29 20:12:50', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('268929745500684298', '268929738777214984', null, '200', '0', '0', '1', '268929738777214984', '127.0.0.1', '2023-03-29 21:08:38', '268929738777214984', '127.0.0.1', '2023-03-29 21:08:38', 'normal', '0');
INSERT INTO `wo_org_user` VALUES ('269269458258411526', '269269451643994119', null, '200', '0', '0', '1', '269269451643994119', '127.0.0.1', '2023-03-30 19:38:32', '269269451643994119', '127.0.0.1', '2023-03-30 19:38:32', 'normal', '0');
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
                             `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `parent_id` bigint(20) unsigned DEFAULT NULL,
                             `display_order` int(10) DEFAULT NULL,
                             `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `create_by` bigint(20) unsigned DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `update_by` bigint(20) unsigned DEFAULT NULL,
                             `update_time` datetime DEFAULT NULL,
                             `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                             `versions` int(10) DEFAULT NULL,
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
                               `id` bigint(20) unsigned NOT NULL,
                               `resource_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `resource_path` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `resource_type` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `request_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `target` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `app_id` bigint(20) unsigned DEFAULT NULL,
                               `parent_id` bigint(20) unsigned DEFAULT NULL,
                               `display_order` int(10) DEFAULT NULL,
                               `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_by` bigint(20) unsigned DEFAULT NULL,
                               `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_time` datetime DEFAULT NULL,
                               `update_by` bigint(20) unsigned DEFAULT NULL,
                               `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `update_time` datetime DEFAULT NULL,
                               `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `versions` int(10) DEFAULT NULL,
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
                           `id` bigint(20) unsigned NOT NULL,
                           `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `role_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `role_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `parent_id` bigint(20) unsigned DEFAULT NULL,
                           `display_order` int(10) DEFAULT NULL,
                           `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `create_by` bigint(20) unsigned DEFAULT NULL,
                           `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                           `create_time` datetime DEFAULT NULL,
                           `update_by` bigint(20) unsigned DEFAULT NULL,
                           `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `update_time` datetime DEFAULT NULL,
                           `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `versions` int(10) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uni_role_code` (`role_code`),
                           KEY `role_type` (`role_type`),
                           KEY `role_parent_id` (`parent_id`),
                           KEY `role_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                               `id` bigint(20) unsigned NOT NULL,
                               `role_id` bigint(20) unsigned DEFAULT NULL,
                               `org_id` bigint(20) unsigned DEFAULT NULL,
                               `arch_id` bigint(20) unsigned DEFAULT NULL,
                               `com_id` bigint(20) unsigned DEFAULT NULL,
                               `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_by` bigint(20) unsigned DEFAULT NULL,
                               `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `create_time` datetime DEFAULT NULL,
                               `update_by` bigint(20) unsigned DEFAULT NULL,
                               `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `update_time` datetime DEFAULT NULL,
                               `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `versions` int(10) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `org_role_id` (`role_id`),
                               KEY `org_role_org_id` (`org_id`),
                               KEY `org_arch_com` (`org_id`,`arch_id`,`com_id`),
                               KEY `org_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                                          `id` bigint(20) unsigned NOT NULL,
                                          `subject_type_id` bigint(20) unsigned DEFAULT NULL,
                                          `role_id` bigint(20) unsigned DEFAULT NULL,
                                          `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_by` bigint(20) unsigned DEFAULT NULL,
                                          `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `create_time` datetime DEFAULT NULL,
                                          `update_by` bigint(20) unsigned DEFAULT NULL,
                                          `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `update_time` datetime DEFAULT NULL,
                                          `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                          `versions` int(10) DEFAULT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY `sub_type_id` (`subject_type_id`),
                                          KEY `sub_role_id` (`role_id`),
                                          KEY `sub_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_subject_association
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_authentication`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_authentication`;
CREATE TABLE `wo_subject_authentication` (
                                             `id` bigint(20) unsigned NOT NULL,
                                             `subject_type_id` bigint(20) unsigned DEFAULT NULL,
                                             `subject_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `user_id` bigint(20) unsigned DEFAULT NULL,
                                             `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `create_by` bigint(20) unsigned DEFAULT NULL,
                                             `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `create_time` datetime DEFAULT NULL,
                                             `update_by` bigint(20) unsigned DEFAULT NULL,
                                             `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `update_time` datetime DEFAULT NULL,
                                             `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                             `versions` int(10) DEFAULT NULL,
                                             PRIMARY KEY (`id`),
                                             KEY `sub_type_id` (`subject_type_id`),
                                             KEY `sub_user_id` (`user_id`),
                                             KEY `sub_is_valid_del` (`is_valid`,`delete_flag`),
                                             KEY `sub_code` (`subject_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_subject_authentication
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_define`;
CREATE TABLE `wo_subject_define` (
                                     `id` bigint(20) unsigned NOT NULL,
                                     `subject_type_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `subject_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `create_by` bigint(20) unsigned DEFAULT NULL,
                                     `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `create_time` datetime DEFAULT NULL,
                                     `update_by` bigint(20) unsigned DEFAULT NULL,
                                     `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `update_time` datetime DEFAULT NULL,
                                     `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `versions` int(10) DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uni_subject_type_code` (`subject_type_code`) USING BTREE,
                                     KEY `sub_def_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_subject_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_subject_model_define`
-- ----------------------------
DROP TABLE IF EXISTS `wo_subject_model_define`;
CREATE TABLE `wo_subject_model_define` (
                                           `id` bigint(20) unsigned DEFAULT NULL,
                                           `subject_type_id` bigint(20) unsigned NOT NULL,
                                           `subject_model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `subject_type_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `create_by` bigint(20) unsigned DEFAULT NULL,
                                           `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `create_time` datetime DEFAULT NULL,
                                           `update_by` bigint(20) unsigned DEFAULT NULL,
                                           `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `update_time` datetime DEFAULT NULL,
                                           `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                           `versions` int(10) DEFAULT NULL,
                                           PRIMARY KEY (`subject_type_id`),
                                           KEY `sub_model_type_id` (`subject_type_id`),
                                           KEY `sub_is_valid_del` (`is_valid`,`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_subject_model_define
-- ----------------------------

-- ----------------------------
-- Table structure for `wo_user`
-- ----------------------------
DROP TABLE IF EXISTS `wo_user`;
CREATE TABLE `wo_user` (
                           `id` bigint(20) unsigned NOT NULL,
                           `login_name` varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                           `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `passwd` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `domain_id` bigint(20) unsigned DEFAULT NULL,
                           `id_card` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `birthday` datetime DEFAULT NULL,
                           `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `telephone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '注册会员',
                           `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '网络用户',
                           `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `qq` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `email` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `remark` varchar(230) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `display_order` bigint(20) DEFAULT NULL,
                           `is_real` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `invite_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `recommend_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `register_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `create_by` bigint(20) unsigned DEFAULT NULL,
                           `create_time` datetime DEFAULT NULL,
                           `create_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `update_by` bigint(20) unsigned DEFAULT NULL,
                           `update_time` datetime DEFAULT NULL,
                           `update_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `delete_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `versions` int(10) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uni_login` (`login_name`) USING BTREE,
                           KEY `user_status` (`status`),
                           KEY `user_sex` (`sex`),
                           KEY `user_del` (`delete_flag`),
                           KEY `user_prov_city_area_coun` (`province`,`city`,`area`,`country`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of wo_user
-- ----------------------------
INSERT INTO `wo_user` VALUES ('1', 'admin', '龙神', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'normal', '0', null, 'admin', 'man', null, '15665730355', '0531-268888888', '注册会员', '网络用户', '长清区崮云湖街道', null, '30699@qq.com', '/2023/03/24183400aQPgA5XN.jpg', 'WLDOS之父，独立研发了WLDOS云应用支撑平台。', '0', '0', 'China', '370000', '370100', null, '0', null, '192.168.1.23', '0', '2021-05-08 12:15:05', '192.168.1.23', '1', '2023-03-24 18:34:01', '127.0.0.1', 'normal', '1');
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
INSERT INTO `wo_user` VALUES ('268888145382785029', '3385115357@qq.com', '3385115357', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '3385115357@qq.com', null, null, null, null, null, null, null, null, null, null, '127.0.0.1', '268888145382785029', '2023-03-29 18:23:21', '127.0.0.1', '268888145382785029', '2023-03-29 18:23:21', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_user` VALUES ('268914102432874506', '29028256512@qq.com', '29028256521', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '2902825652@qq.com', null, null, null, null, null, null, null, null, null, null, '127.0.0.1', '268914102432874506', '2023-03-29 20:06:30', '127.0.0.1', '1', '2023-03-29 20:12:16', '127.0.0.1', 'normal', '0');
INSERT INTO `wo_user` VALUES ('269269451643994119', '2902825652@qq.com', '2902825652', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'normal', '1533544727530094592', null, null, null, null, null, null, null, null, null, null, '2902825652@qq.com', null, null, null, null, null, null, null, null, null, null, '127.0.0.1', '269269451643994119', '2023-03-30 19:38:32', '127.0.0.1', '269269451643994119', '2023-03-30 19:38:32', '127.0.0.1', 'normal', '0');
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
                               `meta_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `meta_value` longtext COLLATE utf8mb4_unicode_ci,
                               PRIMARY KEY (`id`),
                               KEY `user_id` (`user_id`),
                               KEY `meta_key` (`meta_key`(191))
) ENGINE=InnoDB AUTO_INCREMENT=269269458744950785 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
INSERT INTO `wo_usermeta` VALUES ('268888153649758214', '268888145382785029', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('268914110121033731', '268914102432874506', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('268915702140420107', '268915694989131787', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('268929746012389385', '268929738777214984', 'passStatus', 'medium');
INSERT INTO `wo_usermeta` VALUES ('269269458744950784', '269269451643994119', 'passStatus', 'medium');
