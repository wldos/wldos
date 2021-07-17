/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.file.service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.system.storage.file.entity.WoFile;
import com.wldos.system.storage.file.repo.FileRepo;
import com.wldos.system.storage.file.util.UploadUtils;
import com.wldos.system.storage.file.vo.FileInfo;
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
	String uploadPath;

	/**
	 * 存储文件并保存和返回文件信息
	 *
	 * @param file
	 * @param prefix
	 * @param usrId
	 * @param userIp
	 * @return
	 * @throws IOException
	 */
	public FileInfo storeAndSaveInfo(MultipartFile file, String prefix, Long usrId, String userIp) throws IOException {
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		String storeName = UploadUtils.genFilename(ext);

		String filePathName =  this.uploadPath + storeName;

		File dest = new File(filePathName);
		dest = UploadUtils.getUniqueFile(dest);
		UploadUtils.checkDirAndCreate(dest.getParentFile());
		file.transferTo(dest);

		FileInfo info = new FileInfo();
		info.setName(origName);
		info.setPath(storeName);
		String url = this.genWebUrl(prefix, storeName);
		info.setUrl(url);
		WoFile woFile = new WoFile();
		long id = this.nextId();
		info.setId(id);
		woFile.setName(origName);
		woFile.setPath(storeName); // 保存相对路径，相对于uploadPath的路径，uploadPath割接时不会影响到数据库
		woFile.setMimeType(file.getContentType());
		EntityAssists.beforeInsert(woFile, id, usrId, userIp, true);

		this.save(woFile);

		return info;
	}

	/**
	 * 根据文件ID获取文件信息
	 * @todo 后期实现对象访问权限控制（私密文件验证个人信息、文件URL加密）
	 *
	 * @param fileId
	 * @param storeUrl 文件服务URL
	 * @return
	 */
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

	private String genWebUrl(String storeUrl, String filePath) {
		return storeUrl + filePath;
	}
}
