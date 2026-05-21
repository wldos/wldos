/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.framework.support.storage.IStore;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.StringUtils;

/**
 * 将 {@link IStore} 中已存文件按逻辑路径写出到 HTTP 响应：统一 Content-Type、Content-Disposition（UTF-8 文件名）、可选 Content-Length，
 * 引导业务使用 {@code void} + 流式写出，避免误返回可 JSON 包装类型导致下载异常。
 *
 * @author Yuanxi Universe
 */
public final class StoredFileDownloadSupport {

	private StoredFileDownloadSupport() {
	}

	/**
	 * @param store            非空
	 * @param response         Servlet 响应
	 * @param logicalPath      与 {@link IStore#copyStoredFileTo} 一致
	 * @param downloadFileName 下载展示名；空则从 {@code logicalPath} 取末段
	 * @param inline           {@code true} 时为 {@code inline}，否则 {@code attachment}
	 */
	public static void writeToResponse(IStore store, HttpServletResponse response, String logicalPath, String downloadFileName,
			boolean inline) throws IOException {
		if (store == null) {
			throw new BaseException("文件存储不可用：IStore 未注入");
		}
		if (!StringUtils.hasText(logicalPath)) {
			throw new BaseException("文件路径不能为空");
		}
		String path = logicalPath.trim();
		String filename = resolveDownloadFilename(downloadFileName, path);

		MediaType mediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);
		response.setContentType(mediaType.toString());

		ContentDisposition disposition = ContentDisposition.builder(inline ? "inline" : "attachment")
				.filename(filename, StandardCharsets.UTF_8)
				.build();
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());

		long size = resolveSize(store, path);
		if (size >= 0) {
			response.setContentLengthLong(size);
		}

		store.copyStoredFileTo(path, response.getOutputStream());
		response.flushBuffer();
	}

	private static long resolveSize(IStore store, String logicalPath) throws IOException {
		try {
			return store.storedFileSize(logicalPath);
		}
		catch (IllegalStateException | UnsupportedOperationException ex) {
			// 远程存储在无当前请求上下文等场景无法取长度：省略 Content-Length，chunked 仍可下载
			return -1;
		}
	}

	private static String resolveDownloadFilename(String downloadFileName, String logicalPath) {
		String raw = StringUtils.hasText(downloadFileName) ? downloadFileName.trim() : null;
		if (!StringUtils.hasText(raw)) {
			raw = extractLastSegment(logicalPath);
		}
		if (!StringUtils.hasText(raw)) {
			return "download";
		}
		int s = Math.max(raw.lastIndexOf('/'), raw.lastIndexOf('\\'));
		if (s >= 0 && s < raw.length() - 1) {
			raw = raw.substring(s + 1);
		}
		return StringUtils.hasText(raw) ? raw : "download";
	}

	private static String extractLastSegment(String path) {
		int s = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
		if (s < 0) {
			return path;
		}
		return s < path.length() - 1 ? path.substring(s + 1) : path;
	}
}
