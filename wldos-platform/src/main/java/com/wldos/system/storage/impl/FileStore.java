/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.impl;

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
import com.wldos.cms.dto.Thumbnail;
import com.wldos.support.controller.web.DomainResult;
import com.wldos.support.util.IpUtils;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.Constants;
import com.wldos.support.util.img.ImageUtil;
import com.wldos.system.gateway.RestService;
import com.wldos.system.storage.IStore;
import com.wldos.system.storage.enums.FileAccessPolicyEnum;
import com.wldos.system.storage.service.FileService;
import com.wldos.system.storage.vo.FileInfo;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
@Component
public class FileStore implements IStore {

	@Value("${wldos.file.store.url}")
	private String storeUrl;

	@Value("${gateway.proxy.prefix}")
	private String gatewayPrefix;

	@Value("${wldos.file.store.path}")
	private String uploadPath;

	@Value("${wldos.file.store.local}")
	private boolean isLocalStore;

	private final RestService restService;

	private final FileService fileService;

	public FileStore(RestService restService, FileService fileService) {
		this.restService = restService;
		this.fileService = fileService;
	}

	@Override
	public FileInfo getFileInfoById(Long fileId) {
		return this.fileService.getFileInfoById(fileId, this.storeUrl);
	}

	@Override
	public String getFileUrl(Long fileId, String defaultUrl) {
		if (null == fileId) {
			return ObjectUtil.string(defaultUrl);
		}

		FileInfo avatar = this.getFileInfoById(fileId);

		String url = avatar.getUrl();
		return ObjectUtil.isBlank(avatar) || ObjectUtil.isBlank(url)
				? ObjectUtil.string(defaultUrl) : url;
	}

	@Override
	public String getFileUrl(String filePath, String defaultUrl) {
		return ObjectUtil.isBlank(filePath) ? ObjectUtil.string(defaultUrl) : this.fileService.genWebUrl(this.storeUrl, filePath);
	}

	@Override
	public String genOssUrl(FileAccessPolicyEnum accessPolicy) {
		return this.fileService.genOssUrl(this.storeUrl, accessPolicy);
	}

	@Override
	public FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException {
		FileInfo fileInfo = null;

		if (this.isLocalStore) {
			fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));
		} else {
			fileInfo = this.storeRemote(request, response, file);
		}

		return fileInfo;
	}

	@Override
	public FileInfo storeFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, int[] widthHeight) throws IOException {
		FileInfo fileInfo = null;

		if (this.isLocalStore) {
			fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));

			String src = this.fileService.getRealStorePath() + fileInfo.getPath();
			ImageUtil.imgThumb(src, src, widthHeight[0], widthHeight[1]);

		} else {
			fileInfo = this.storeRemote(request, response, file);
		}

		return fileInfo;
	}

	@Override
	public List<Thumbnail> storePicWithThumbnails(HttpServletRequest request, HttpServletResponse response, MultipartFile file, List<Thumbnail> thumbnailList) throws IOException {
		List<Thumbnail> thumbnails = new ArrayList<>();

		if (this.isLocalStore) {
			FileInfo fileInfo = this.fileService.storeAndSaveInfo(file, this.storeUrl,
					Long.parseLong(request.getHeader(Constants.CONTEXT_KEY_USER_ID)), IpUtils.getClientIp(request));

			String realPathPreFix = this.fileService.getRealStorePath();
			String relativePath = fileInfo.getPath();
			String src =  realPathPreFix + relativePath;
			BufferedImage image = ImageIO.read(new File(src));
			ImageUtil.imgThumb(src, src, image.getWidth(), image.getHeight());

			Thumbnail postPicture = new Thumbnail();
			postPicture.setWidth(image.getWidth());
			postPicture.setHeight(image.getHeight());
			postPicture.setPath(fileInfo.getPath());

			thumbnails.add(postPicture);

			String[] fInfo = relativePath.split("\\.");
			String noExtNamePath = fInfo[0];
			String extName = fInfo[1];

			for (Thumbnail tm : thumbnailList) {

				StringBuilder srcT = new StringBuilder(noExtNamePath).append("-").append(tm.getWidth()).append("x").append(tm.getHeight()).append(".").append(extName);
				String outPath = realPathPreFix + srcT.toString();
				ImageUtil.imgThumb(src, outPath, tm.getWidth(), tm.getHeight());

				BufferedImage imageT = ImageIO.read(new File(outPath));

				Thumbnail thumbnail = new Thumbnail();
				thumbnail.setWidth(imageT.getWidth());
				thumbnail.setHeight(imageT.getHeight());
				thumbnail.setPath(srcT.toString());
				thumbnail.setType(tm.getType());
				thumbnail.setMimeType(file.getContentType());

				thumbnails.add(thumbnail);
			}
		} else {
			FileInfo fileInfo = this.storeRemote(request, response, file);
		}

		return thumbnails;
	}

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

}
