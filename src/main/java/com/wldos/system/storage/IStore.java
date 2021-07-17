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
import com.wldos.system.storage.file.vo.FileInfo;

import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储接口。
 *
 * @author 树悉猿
 * @date 2021/5/30
 * @version 1.0
 */
public interface IStore {
	/**
	 * 根据文件id从文件服务获取文件信息
	 *
	 * @param fileId
	 * @return
	 */
	FileInfo getFileInfoById(Long fileId);

	/**
	 * 通过文件id获取文件web url，用于安全要求并且用户个人访问的文件
	 *
	 * @param fileId
	 * @param defaultUrl 默认URL，如果id为空，返回默认url
	 * @return
	 */
	String getFileUrl(Long fileId, String defaultUrl);

	/**
	 * 通过文件真实路径获取文件web url，用于大量公开访问的文件
	 *
	 * @param filePath
	 * @param defaultUrl 默认URL，如果path为空，返回默认url
	 * @return
	 */
	String getFileUrl(String filePath, String defaultUrl);

	/**
	 * 上传文件到文件服务
	 *
	 * @param request 文件流来源
	 * @param response
	 * @param file 文件
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException;

	/**
	 * 上传文件到文件服务
	 *
	 * @param request 文件流来源
	 * @param response
	 * @param file 文件
	 * @param widthHeight 压缩：[像素宽度,像素高度]
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] widthHeight)
			throws IOException;

	/**
	 * 上传文件到文件服务,同时创建多尺寸缩略图
	 *
	 * @param request 文件流来源
	 * @param response
	 * @param file 文件
	 * @param thumbnailList 缩略图密度集
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	List<Thumbnail> storePicWithThumbnails(HttpServletRequest request, HttpServletResponse response, MultipartFile file,
			List<Thumbnail> thumbnailList) throws IOException;
}
