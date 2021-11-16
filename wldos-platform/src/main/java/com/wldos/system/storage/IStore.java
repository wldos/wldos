/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.cms.dto.Thumbnail;
import com.wldos.system.storage.vo.FileInfo;

import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储接口。
 *
 * @author 树悉猿
 * @date 2021/5/30
 * @version 1.0
 */
public interface IStore {
	
	FileInfo getFileInfoById(Long fileId);
	
	String getFileUrl(Long fileId, String defaultUrl);
	
	String getFileUrl(String filePath, String defaultUrl);
	
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException;
	
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] widthHeight)
			throws IOException;
	
	List<Thumbnail> storePicWithThumbnails(HttpServletRequest request, HttpServletResponse response, MultipartFile file,
			List<Thumbnail> thumbnailList) throws IOException;
}
