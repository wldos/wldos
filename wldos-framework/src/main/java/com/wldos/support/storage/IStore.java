/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.support.storage.vo.FileInfo;

import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储接口。
 *
 * @author 元悉宇宙
 * @date 2021/5/30
 * @version 1.0
 */
public interface IStore {
	/**
	 * 根据文件id从文件服务获取文件信息
	 *
	 * @param fileId 文件id
	 * @return 文件信息
	 */
	FileInfo getFileInfoById(Long fileId);

	/**
	 * 通过文件id获取文件web url，用于安全要求并且用户个人访问的文件
	 *
	 * @param fileId 文件id
	 * @param defaultUrl 默认URL，如果id为空，返回默认url
	 * @return 文件url
	 */
	String getFileUrl(Long fileId, String defaultUrl);

	/**
	 * 通过文件真实路径获取文件web url，用于大量公开访问的文件
	 *
	 * @param filePath 文件真实路径
	 * @param defaultUrl 默认URL，如果filePath为空，返回默认url
	 * @return 文件url
	 */
	String getFileUrl(String filePath, String defaultUrl);

	/**
	 * 获取文件服务oss前缀url：https://xxx.com/xxx
	 *
	 * @param accessPolicy 文件访问策略枚举
	 * @return 文件完整web地址
	 */
	String genOssUrl(FileAccessPolicyEnum accessPolicy);

	/**
	 * 上传文件到文件服务
	 *
	 * @param request 文件流来源
	 * @param response 响应
	 * @param file 文件
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException;

	/**
	 * 上传文件到文件服务
	 *
	 * @param request 文件流来源
	 * @param response 响应
	 * @param file 文件
	 * @param widthHeight 压缩：[像素宽度,像素高度]
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] widthHeight)
			throws IOException;

	/**
	 * 通过http请求下载图片文件并上传文件到文件服务
	 *
	 * @param request 文件流来源
	 * @param response 响应
	 * @param downloadUrl 文件下载url
	 * @param widthHeight 压缩：[像素宽度,像素高度]
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, String downloadUrl, int[] widthHeight)
			throws IOException;

	/**
	 * 上传文件到文件服务,同时创建多尺寸缩略图
	 *
	 * @param request 文件流来源
	 * @param response 响应
	 * @param file 文件
	 * @param thumbnailList 缩略图密度集
	 * @return 上传完成返回文件存储信息
	 * @throws IOException
	 */
	List<Thumbnail> storePicWithThumbnails(HttpServletRequest request, HttpServletResponse response, MultipartFile file,
			List<Thumbnail> thumbnailList) throws IOException;

	/**
	 * 指定完整目录文件名上传文件
	 *
	 * @param file 上传的文件
	 * @param filePathName 完整文件路径名
	 * @throws IOException io异常
	 */
	void storeFile(MultipartFile file, String filePathName) throws IOException;

	/**
	 * 另存为输入流中内容到指定完整目录文件
	 *
	 * @param src 复制的源输入流
	 * @param dstPath 另存为的目标目录
	 * @throws IOException io异常
	 */
	void saveAs(InputStream src, String dstPath) throws IOException;

	/** 文件服务前缀url变量名：https://xxxx.com/xxx */
	String KEY_OSS_URL = "ossUrl";
}