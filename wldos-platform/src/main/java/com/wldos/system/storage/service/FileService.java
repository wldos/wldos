/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.storage.entity.WoFile;
import com.wldos.system.storage.enums.FileAccessPolicyEnum;
import com.wldos.system.storage.repo.FileRepo;
import com.wldos.system.storage.util.StoreUtils;
import com.wldos.system.storage.vo.FileInfo;
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileService extends BaseService<FileRepo, WoFile, Long> {
	@Value("${wldos.file.store.path}")
	private String uploadPath;

	public FileInfo storeAndSaveInfo(MultipartFile file, String storeUrl, Long usrId, String userIp) throws IOException {
		String fileName = StoreUtils.getSecurityFileName(file.getOriginalFilename());
		String ext = ObjectUtil.string(FilenameUtils.getExtension(fileName)).toLowerCase(Locale.ENGLISH);
		String storeName = StoreUtils.genFilename(ext);

		String filePathName =  this.getOsPathname(storeName);

		File dest = new File(filePathName);
		dest = StoreUtils.getUniqueFile(dest);
		StoreUtils.checkDirAndCreate(dest.getParentFile());
		file.transferTo(dest);

		FileInfo info = new FileInfo();
		info.setName(fileName);
		info.setPath(storeName);
		String url = this.genWebUrl(storeUrl, storeName);
		info.setUrl(url);
		WoFile woFile = new WoFile();
		long id = this.nextId();
		info.setId(id);
		woFile.setName(fileName);
		woFile.setPath(storeName);
		woFile.setMimeType(file.getContentType());
		EntityAssists.beforeInsert(woFile, id, usrId, userIp, true);

		this.save(woFile);

		return info;
	}

	public FileInfo getFileInfoById(Long fileId, String storeUrl) {
		WoFile file = this.findById(fileId);

		if (file == null)
			return null;

		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(file.getId());
		fileInfo.setName(file.getName());
		fileInfo.setPath(file.getPath());
		String url = this.genWebUrl(storeUrl, file.getPath());
		fileInfo.setUrl(url);
		fileInfo.setMimeType(file.getMimeType());

		return fileInfo;
	}

	public String genOssUrl(String storeUrl, FileAccessPolicyEnum accessPolicy) {
		return storeUrl + this.getVirtualPath(accessPolicy);
	}

	public String genWebUrl(String storeUrl, String filePath) {
		return storeUrl + this.getVirtualPath() + filePath;
	}

	public String getOsPathname(String storeName) {
		return this.getRealStorePath() + storeName;
	}

	public String getRealStorePath() {
		return this.uploadPath + this.getVirtualPath();
	}

	private String getVirtualPath() {
		return StoreUtils.PUBLIC_AREA;
	}

	private String getVirtualPath(FileAccessPolicyEnum accessPolicy) {
		return accessPolicy.equals(FileAccessPolicyEnum.PUBLIC) ? StoreUtils.PUBLIC_AREA
				: StoreUtils.PRIVATE_AREA;
	}
}
