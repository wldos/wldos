/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */

package com.wldos.framework.support.storage;

import java.nio.file.Files;

import io.github.wldos.common.exception.BaseException;
import io.github.wldos.framework.support.storage.impl.FileStore;
import io.github.wldos.framework.support.storage.service.NoRepoFileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * {@link StoredFileDownloadSupport}：本地 {@link FileStore} 场景下写出响应头与正文。
 */
@ExtendWith(MockitoExtension.class)
class StoredFileDownloadSupportTest {

	private FileStore fileStore;

	@Mock
	private NoRepoFileService fileService;

	@BeforeEach
	void setUp() {
		this.fileStore = org.mockito.Mockito.spy(new FileStore(this.fileService));
		ReflectionTestUtils.setField(this.fileStore, "storeUrl", "http://test");
		ReflectionTestUtils.setField(this.fileStore, "isLocalStore", true);
	}

	@Test
	void writeToResponse_setsAttachmentHeadersAndBody(@TempDir java.nio.file.Path tmp) throws Exception {
		java.nio.file.Path f = tmp.resolve("sub").resolve("数据.bin");
		Files.createDirectories(f.getParent());
		byte[] data = new byte[] { 4, 5, 6 };
		Files.write(f, data);
		when(this.fileService.getOsPathname("/sub/数据.bin")).thenReturn(f.toString());

		MockHttpServletResponse resp = new MockHttpServletResponse();
		StoredFileDownloadSupport.writeToResponse(this.fileStore, resp, "/sub/数据.bin", "展示名.bin", false);

		assertEquals(200, resp.getStatus());
		assertArrayEquals(data, resp.getContentAsByteArray());
		assertEquals(data.length, resp.getContentLength());
		String cd = resp.getHeader("Content-Disposition");
		assertTrue(cd.startsWith("attachment;"), cd);
		assertTrue(cd.contains("filename"), cd);
	}

	@Test
	void writeToResponse_inlineDisposition(@TempDir java.nio.file.Path tmp) throws Exception {
		java.nio.file.Path f = tmp.resolve("x.txt");
		Files.write(f, "hi".getBytes(java.nio.charset.StandardCharsets.UTF_8));
		when(this.fileService.getOsPathname("/x.txt")).thenReturn(f.toString());

		MockHttpServletResponse resp = new MockHttpServletResponse();
		StoredFileDownloadSupport.writeToResponse(this.fileStore, resp, "/x.txt", null, true);

		assertTrue(resp.getHeader("Content-Disposition").startsWith("inline;"));
	}

	@Test
	void writeToResponse_nullStore_throws() {
		assertThrows(BaseException.class, () -> StoredFileDownloadSupport.writeToResponse(null, new MockHttpServletResponse(), "/a", "a", false));
	}

	@Test
	void writeToResponse_blankPath_throws() {
		assertThrows(BaseException.class, () -> StoredFileDownloadSupport.writeToResponse(this.fileStore, new MockHttpServletResponse(), "  ", "a", false));
	}
}
