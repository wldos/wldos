/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.storage.controller;

import java.io.IOException;
import java.util.Locale;

import com.wldos.framework.controller.RepoController;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.storage.entity.WoFile;
import com.wldos.support.storage.service.FileService;
import com.wldos.support.storage.utils.StoreUtils;
import com.wldos.support.storage.vo.FileInfo;
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作controller。
 *
 * @author 元悉宇宙
 * @date 2021/5/30
 * @version 1.0
 */
@RefreshScope
@RestController
@RequestMapping("file")
public class FileController extends RepoController<FileService, WoFile> {
	@Value("${wldos.file.store.url:http://www.wldos.com}")
	private String storeUrl;

	@Value("${wldos_file_store_path:}")
	private String uploadPath;

	/**
	 * 按扩展名存储文件，文件的物理存储名为随机码，多用于附件类文件上传，只关心真实名称，不关心存储名称。
	 *
	 * @param file 上传文件
	 * @return 返回文件信息
	 */
	@PostMapping("store")
	public FileInfo store(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		String fileName = StoreUtils.getSecurityFileName(file.getOriginalFilename());
		String ext = ObjectUtils.string(FilenameUtils.getExtension(fileName)).toLowerCase(Locale.ENGLISH);
		// @todo 需要实现根据用户角色判定是否支持的文件扩展名，该逻辑与前端配合

		// @todo 后期实现图片加水印功能，其他格式实现病毒查杀功能

		// @todo 默认实现上传到本地，因为本模块是支持独立部署的文件存储服务，后期实现：存储到数据库、sftp

		// String path = this.request.getContextPath() + this.uploadPath;

		return this.service.storeAndSaveInfo(file, this.storeUrl,
				this.getUserId(), this.getUserIp());
	}

	// @todo 按文件名称存储文件，需要在seo等场合展示有意义的文件名称，比如图片的英文名称有一定意义，是seo要求，logo可能需要定义包含logo字样的图片名称，此种情况下名称冲突时自动加数字
}