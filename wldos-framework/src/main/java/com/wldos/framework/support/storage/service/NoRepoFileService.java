/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.wldos.framework.common.IDGen;
import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.HttpUtils;
import com.wldos.framework.support.storage.entity.WoFile;
import com.wldos.framework.support.storage.dao.FileDao;
import com.wldos.framework.support.storage.utils.FileTypeUtils;
import com.wldos.framework.support.storage.utils.StoreUtils;
import com.wldos.framework.support.storage.vo.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作调停者代理service，用于开环解锁。
 *
 * @author 元悉宇宙
 * @date 2021/4/28
 * @version 1.0
 */
@RefreshScope
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class NoRepoFileService {
	@Value("${wldos_file_store_path:}")
	private String uploadPath;

	private final FileDao fileRepo;

	private final IDGen IDGen;

	public NoRepoFileService(FileDao fileRepo, IDGen IDGen) {
		this.fileRepo = fileRepo;
		this.IDGen = IDGen;
	}

	/**
	 * 存储文件并保存和返回文件信息
	 *
	 * @param file 文件
	 * @param storeUrl 前缀
	 * @param usrId 用户id
	 * @param userIp 用户ip
	 * @return 用户信息
	 * @throws IOException io
	 */
	public FileInfo storeAndSaveInfo(MultipartFile file, String storeUrl, Long usrId, String userIp) throws IOException {
		String fileName = StoreUtils.getSecurityFileName(file.getOriginalFilename());
		String ext = ObjectUtils.string(FilenameUtils.getExtension(fileName)).toLowerCase(Locale.ENGLISH);
		String storeName = StoreUtils.genFilename(ext);

		String filePathName = this.getOsPathname(storeName);

		File dest = new File(filePathName);
		dest = StoreUtils.getUniqueFile(dest);
		StoreUtils.checkDirAndCreate(dest.getParentFile());
		file.transferTo(dest);

		String url = this.genWebUrl(storeUrl, storeName);

		// 保存相对路径，相对于uploadPath的路径，uploadPath割接时不会影响到数据库
		String mimeType = file.getContentType();
		WoFile woFile = WoFile.of(fileName, storeName, mimeType);

		woFile = this.fileRepo.save(woFile);

		return new FileInfo(woFile.getId(), fileName, storeName, url, mimeType);
	}

	/**
	 * http下载、存储图片并保存和返回文件信息
	 *
	 * @param downloadUrl 文件下载url
	 * @param storeUrl 前缀
	 * @param usrId 用户id
	 * @param userIp 用户ip
	 * @return 用户信息
	 */
	public FileInfo storeImgAndSaveInfo(String downloadUrl, String storeUrl, Long usrId, String userIp) {
		InputStream in = HttpUtils.doGetInputStream(downloadUrl);
		if (null == in) {
			throw new RuntimeException("下载文件异常");
		}
		BufferedInputStream bis = new BufferedInputStream(in);

		byte[] head = new byte[3]; // 取扩展名，必须是3个字节
		String ext;
		try {
			ext = this.readType(in, head);
		}
		catch (IOException e) {
			throw new RuntimeException("获取图片类型异常", e);
		}
		String fileName = usrId + "." + ext;

		String storeName = StoreUtils.genFilename(ext);

		String filePathName = this.getOsPathname(storeName);

		File dest = new File(filePathName);
		dest = StoreUtils.getUniqueFile(dest);
		StoreUtils.checkDirAndCreate(dest.getParentFile());

		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(dest));

			byte[] bytes = new byte[1024];

			int bit;
			out.write(head, 0, head.length); // 写出头（为了获取类型预读取的部分）
			while ((bit = bis.read(bytes)) != -1) {
				out.write(bytes, 0, bit);
			}

			out.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("下载图片文件异常");
		}

		String url = this.genWebUrl(storeUrl, storeName);

		// 保存相对路径，相对于uploadPath的路径，uploadPath割接时不会影响到数据库
		String mimeType = "image/" + ext;
		WoFile woFile = WoFile.of(fileName, storeName, mimeType);

		woFile = this.fileRepo.save(woFile);

		return new FileInfo(woFile.getId(), fileName, storeName, url, mimeType);
	}

	// bufHead必须是3个字节
	private String readType(InputStream in, byte[] bufHead) throws IOException {

		int size = in.read(bufHead, 0, bufHead.length);
		if (size > 0) {
			String headCode = Objects.requireNonNull(FileTypeUtils.bytesToHexString(bufHead)).toUpperCase();
			return FileTypeUtils.checkPicType(headCode);
		}
		throw new RemoteException("获取图片类型失败");
	}

	/**
	 * 存储文件并保存和返回文件信息
	 *
	 * @param file 文件
	 * @param filePathName 要保存的完整文件路径名：物理存储路径+文件名
	 * @throws IOException io
	 */
	public void storeFile(MultipartFile file, String filePathName) throws IOException {

		File dest = new File(filePathName);
		dest = StoreUtils.getUniqueFile(dest);
		StoreUtils.checkDirAndCreate(dest.getParentFile());
		file.transferTo(dest);
	}

	/**
	 * 另存为输入流中内容到指定完整目录文件
	 *
	 * @param src 复制的源输入流
	 * @param filePathName 要保存的完整文件路径名：物理存储路径+文件名
	 * @throws IOException io异常
	 */
	public void storeFile(InputStream src, String filePathName) throws IOException {
		log.info("上传文件：{}", filePathName);
		StoreUtils.saveAsFile(src, filePathName);
	}

	/**
	 * 根据文件ID获取文件信息
	 * @ todo 后期实现对象访问权限控制（私密文件验证个人信息、文件URL加密）
	 *
	 * @param fileId 文件id
	 * @param storeUrl 文件服务URL
	 * @return 文件信息
	 */
	public FileInfo getFileInfoById(Long fileId, String storeUrl) {

		Optional<WoFile> oFile = this.fileRepo.findById(fileId);
		WoFile file = oFile.orElse(null);
		if (file == null)
			return null;

		String url = this.genWebUrl(storeUrl, file.getPath());

		return new FileInfo(file.getId(), file.getName(), file.getPath(), url, file.getMimeType());
	}

	/**
	 * 获取文件服务oss前缀url
	 *
	 * @param storeUrl 存储服务器url
	 * @param accessPolicy 文件访问策略枚举
	 * @return 文件完整web地址
	 */
	public String genOssUrl(String storeUrl, FileAccessPolicyEnum accessPolicy) {
		return storeUrl + StoreUtils.urlSeparator + this.getVirtualPath(accessPolicy);
	}

	/**
	 * 获取文件完整的web访问url
	 *
	 * @param storeUrl 存储服务器url
	 * @param filePath 文件存储相对路径
	 * @return 文件完整web地址
	 */
	public String genWebUrl(String storeUrl, String filePath) {
		return storeUrl + StoreUtils.urlSeparator + this.getVirtualPath() + filePath;
	}

	/**
	 * 获取文件在系统存储的完整路径名
	 *
	 * @param storeName 相对路径名
	 * @return 文件完整路径名
	 */
	public String getOsPathname(String storeName) {
		return this.getRealStorePath() + storeName;
	}

	/**
	 * 返回真实文件存储路径
	 *
	 * @return 文件存储路径
	 */
	public String getRealStorePath() {
		// 如果是相对路径
		return this.uploadPath + StoreUtils.fileSeparator + this.getVirtualPath();
	}

	private String getVirtualPath() {
		return StoreUtils.PUBLIC_AREA;
	}

	private String getVirtualPath(FileAccessPolicyEnum accessPolicy) {
		return (accessPolicy.equals(FileAccessPolicyEnum.PUBLIC) ? StoreUtils.PUBLIC_AREA
				: StoreUtils.PRIVATE_AREA);
	}
}