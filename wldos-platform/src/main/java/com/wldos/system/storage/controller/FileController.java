/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.controller;

import java.io.IOException;
import java.util.Locale;

import com.wldos.support.controller.RepoController;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.storage.entity.WoFile;
import com.wldos.system.storage.service.FileService;
import com.wldos.system.storage.util.StoreUtils;
import com.wldos.system.storage.vo.FileInfo;
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
	
	@PostMapping("store")
	public FileInfo store(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		String fileName = StoreUtils.getSecurityFileName(file.getOriginalFilename());
		String ext = ObjectUtil.string(FilenameUtils.getExtension(fileName)).toLowerCase(Locale.ENGLISH);

		return this.service.storeAndSaveInfo(file, this.storeUrl,
				this.getCurUserId(), this.getUserIp());
	}

}
