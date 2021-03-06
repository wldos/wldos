/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.file.controller;

import java.io.IOException;
import java.util.Locale;

import com.wldos.support.controller.RepoController;
import com.wldos.system.storage.file.entity.WoFile;
import com.wldos.system.storage.file.service.FileService;
import com.wldos.system.storage.file.vo.FileInfo;
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作controller。
 *
 * @author 树悉猿
 * @date 2021/5/30
 * @version 1.0
 */
@RequestMapping("file")
public class FileController extends RepoController<FileService, WoFile> {
	@Value("${wldos.file.store.url}")
	private String storeUrl;

	@Value("${wldos.file.store.path}")
	private String uploadPath;

	/**
	 * 按扩展名存储文件，文件的物理存储名为随机码，多用于附件类文件上传，只关心真实名称，不关心存储名称。
	 *
	 * @param file 上传文件
	 * @return 返回文件信息
	 */
	@PostMapping("store")
	public String store(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		// @todo 需要实现根据用户角色判定是否支持的文件扩展名，该逻辑与前端配合

		// @todo 后期实现图片加水印功能，其他格式实现病毒查杀功能

		// @todo 默认实现上传到本地，因为本模块是支持独立部署的文件存储服务，后期实现：存储到数据库、sftp

		String path = this.request.getContextPath() + this.uploadPath;

		FileInfo info = this.service.storeAndSaveInfo(file, this.storeUrl, this.getCurUserId(), this.getUserIp());

		return this.resJson.ok(info);
	}

	// @todo 按文件名称存储文件，需要在seo等场合展示有意义的文件名称，比如图片的英文名称有一定意义，是seo要求，logo可能需要定义包含logo字样的图片名称，此种情况下名称冲突时自动加数字
}
