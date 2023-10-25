/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.storage.service;

import java.io.IOException;

import com.wldos.framework.service.RepoService;
import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.support.storage.entity.WoFile;
import com.wldos.support.storage.repo.FileRepo;
import com.wldos.support.storage.vo.FileInfo;

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
public class FileService extends RepoService<FileRepo, WoFile, Long> {

	private final NoRepoFileService noRepoFileService;

	public FileService(NoRepoFileService noRepoFileService) {
		this.noRepoFileService = noRepoFileService;
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
		return this.noRepoFileService.storeAndSaveInfo(file, storeUrl, usrId, userIp);
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
		return this.noRepoFileService.getFileInfoById(fileId, storeUrl);
	}

	/**
	 * 获取文件服务oss前缀url
	 *
	 * @param storeUrl 存储服务器url
	 * @param accessPolicy 文件访问策略枚举
	 * @return 文件完整web地址
	 */
	public String genOssUrl(String storeUrl, FileAccessPolicyEnum accessPolicy) {
		return this.noRepoFileService.genOssUrl(storeUrl, accessPolicy);
	}

	/**
	 * 获取文件完整的web访问url
	 *
	 * @param storeUrl 存储服务器url
	 * @param filePath 文件存储相对路径
	 * @return 文件完整web地址
	 */
	public String genWebUrl(String storeUrl, String filePath) {
		return this.noRepoFileService.genWebUrl(storeUrl, filePath);
	}

	/**
	 * 获取文件在系统存储的完整路径名
	 *
	 * @param storeName 相对路径名
	 * @return 文件完整路径名
	 */
	public String getOsPathname(String storeName) {
		return this.noRepoFileService.getOsPathname(storeName);
	}

	/**
	 * 返回真实文件存储路径
	 *
	 * @return 文件存储路径
	 */
	public String getRealStorePath() {
		return this.noRepoFileService.getRealStorePath();
	}
}