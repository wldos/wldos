/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.storage.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.common.Constants;
import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.common.res.DomainResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.http.IpUtils;
import com.wldos.common.utils.img.ImageUtils;
import com.wldos.support.storage.IStore;
import com.wldos.support.storage.dto.Thumbnail;
import com.wldos.support.storage.service.NoRepoFileService;
import com.wldos.support.storage.vo.FileInfo;
import com.wldos.support.web.RestService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储。
 *
 * @author 树悉猿
 * @date 2021/5/30
 * @version 1.0
 */
@RefreshScope
@Slf4j
@Component
public class FileStore implements IStore {

	@Value("${wldos.file.store.url:https://www.wldos.com}")
	private String storeUrl;

	@Value("${gateway.proxy.prefix:/wldos}")
	private String gatewayPrefix;

	@Value("${wldos_file_store_path:}")
	private String uploadPath;

	@Value("${wldos.file.store.local:true}")
	private boolean isLocalStore;

	@Autowired
	@Lazy
	private RestService restService;

	private final NoRepoFileService fileService;

	public FileStore(NoRepoFileService fileService) {
		this.fileService = fileService;
	}

	@Override
	public FileInfo getFileInfoById(Long fileId) {
		return this.fileService.getFileInfoById(fileId, this.storeUrl);
	}

	@Override
	public String getFileUrl(Long fileId, String defaultUrl) {
		if (null == fileId) {
			return ObjectUtils.string(defaultUrl);
		}

		FileInfo avatar = this.getFileInfoById(fileId);

		String url = avatar.getUrl();
		return ObjectUtils.isBlank(avatar) || ObjectUtils.isBlank(url)
				? ObjectUtils.string(defaultUrl) : url;
	}

	@Override
	public String getFileUrl(String filePath, String defaultUrl) {
		return ObjectUtils.isBlank(filePath) ? ObjectUtils.string(defaultUrl) : this.fileService.genWebUrl(this.storeUrl, filePath);
	}

	@Override
	public String genOssUrl(FileAccessPolicyEnum accessPolicy) {
		return this.fileService.genOssUrl(this.storeUrl, accessPolicy);
	}

	@Override
	public FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException {
		FileInfo fileInfo = null;
		// 文件服务在本地则直接调用service存
		if (this.isLocalStore) {
			fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));
		}
		else { // 转发远程文件服务
			fileInfo = this.storeRemote(request, response, file);
		}

		return fileInfo;
	}

	@Override
	public FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] widthHeight) throws IOException {
		FileInfo fileInfo = null;
		// 文件服务在本地则直接调用service存
		if (this.isLocalStore) {
			fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));

			// 由于本服务支持本地部署和远程部署，再处理操作仅能用于文件服务器侧，客户端不能操作，所以再处理代码只能用在这里
			String src = this.fileService.getRealStorePath() + fileInfo.getPath();
			ImageUtils.imgThumb(src, src, widthHeight[0], widthHeight[1]);

		}
		else { // 转发远程文件服务
			fileInfo = this.storeRemote(request, response, file);
		}

		return fileInfo;
	}

	@Override
	public FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, String downloadUrl, int[] widthHeight) throws IOException {
		FileInfo fileInfo = null;
		// 文件服务在本地则直接调用service存
		if (this.isLocalStore) {
			fileInfo = this.fileService.storeImgAndSaveInfo(downloadUrl, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));

			// 由于本服务支持本地部署和远程部署，再处理操作仅能用于文件服务器侧，客户端不能操作，所以再处理代码只能用在这里
			String src = this.fileService.getRealStorePath() + fileInfo.getPath();
			ImageUtils.imgThumb(src, src, widthHeight[0], widthHeight[1]);
		}
		else { // 转发远程文件服务
			fileInfo = this.storeRemote(request, response, downloadUrl);
		}

		return fileInfo;
	}

	@Override
	public List<Thumbnail> storePicWithThumbnails(HttpServletRequest request, HttpServletResponse response, MultipartFile file, List<Thumbnail> thumbnailList) throws IOException {
		List<Thumbnail> thumbnails = new ArrayList<>();
		// 文件服务在本地则直接调用service存
		if (this.isLocalStore) {
			FileInfo fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));

			// 由于本服务支持本地部署和远程部署，再处理操作仅能用于文件服务器侧，客户端不能操作，所以再处理代码只能用在这里
			String realPathPreFix = this.fileService.getRealStorePath();
			String relativePath = fileInfo.getPath();
			String src = realPathPreFix + relativePath; // 默认图，全尺寸

			BufferedImage image = ImageIO.read(new File(src));
			int setWidth = Math.min(image.getWidth(), 2048); // 缩略图宽

			double scale = (double) image.getWidth() / image.getHeight(); // 原图宽高比
			int scaleHeight = (int) (setWidth / scale); // 缩放后高度
			ImageUtils.imgThumb(src, src, setWidth, scaleHeight); // 重新压缩全尺寸，最大2048像素，压缩必须保持宽高比，防止失真
			Thumbnail pubPicture = Thumbnail.of(setWidth, scaleHeight, fileInfo.getPath());
			thumbnails.add(pubPicture);

			String[] fInfo = relativePath.split("\\.");
			String noExtNamePath = fInfo[0];
			String extName = fInfo[1];

			for (Thumbnail tm : thumbnailList) { // 批量创建缩略图
				int thumbWidth = tm.getWidth();
				if (image.getWidth() < thumbWidth) // 不做放大处理
					continue;
				scaleHeight = (int) (thumbWidth / scale);
				// 缩略图文件名
				StringBuilder srcT = new StringBuilder(noExtNamePath).append("-").append(thumbWidth).append("x").append(scaleHeight).append(".").append(extName);
				String outPath = realPathPreFix + srcT;
				ImageUtils.imgThumb(src, outPath, thumbWidth, scaleHeight);

				ImageIO.read(new File(outPath));
				// 获取实际宽度 获取实际高度
				thumbnails.add(new Thumbnail(tm.getType(), thumbWidth, scaleHeight, srcT.toString(), file.getContentType()));
			}
		}
		else { // 转发远程文件服务
			FileInfo fileInfo = this.storeRemote(request, response, file);
		}

		return thumbnails;
	}

	@Override
	public void storeFile(MultipartFile file, String filePathName) throws IOException {
		this.fileService.storeFile(file, filePathName);
	}

	@Override
	public void saveAs(InputStream src, String dstPath) throws IOException {
		this.fileService.storeFile(src, dstPath);
	}

	/**
	 * 远程文件存储，暂未实现
	 *
	 * @param request 请求
	 * @param response 响应
	 * @param file 文件
	 * @return 文件信息
	 */
	public FileInfo storeRemote(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {
		FileInfo fileInfo = null;
		String targetUrl = this.storeUrl + this.gatewayPrefix + "/file/store";
		RestTemplate restTemplate = new RestTemplate();

		try {
			RequestEntity requestEntity = createRequestEntity(request, targetUrl);

			ResponseEntity responseEntity = restTemplate.postForEntity(targetUrl, requestEntity, String.class);

			String res = (String) responseEntity.getBody();

			DomainResult result = new ObjectMapper().readValue(res, DomainResult.class);
			fileInfo = (FileInfo) result.getData();
		}
		catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}

		return fileInfo;
	}

	private RequestEntity createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException {
		String queryString = request.getQueryString();
		String reqUrl = url + (queryString != null ? "?" + queryString : "");

		String method = request.getMethod();
		HttpMethod httpMethod = HttpMethod.resolve(method);
		MultiValueMap<String, String> headers = parseRequestHeader(request);
		byte[] body = parseRequestBody(request);
		return new RequestEntity<>(body, headers, httpMethod, new URI(reqUrl));
	}

	private byte[] parseRequestBody(HttpServletRequest request) throws IOException {
		InputStream inputStream = request.getInputStream();
		return StreamUtils.copyToByteArray(inputStream);
	}

	private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		List<String> headerNames = Collections.list(request.getHeaderNames());
		for (String headerName : headerNames) {
			List<String> headerValues = Collections.list(request.getHeaders(headerName));
			for (String headerValue : headerValues) {
				headers.add(headerName, headerValue);
			}
		}
		return headers;
	}


	public FileInfo storeRemote(HttpServletRequest request, HttpServletResponse response, String downloadUrl) {
		return null;
	}
}