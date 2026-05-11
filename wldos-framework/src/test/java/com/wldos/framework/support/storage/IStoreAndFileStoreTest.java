/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */

package com.wldos.framework.support.storage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.wldos.common.Constants;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.framework.support.storage.impl.FileStore;
import io.github.wldos.framework.support.storage.service.NoRepoFileService;
import io.github.wldos.framework.support.storage.vo.FileInfo;
import io.github.wldos.framework.support.storage.vo.UploadResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IStoreAndFileStoreTest {

	private FileStore fileStore;

	@Mock
	private NoRepoFileService fileService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@BeforeEach
	void setUp() {
		this.fileStore = org.mockito.Mockito.spy(new FileStore(this.fileService));
		ReflectionTestUtils.setField(this.fileStore, "storeUrl", "http://test");
		ReflectionTestUtils.setField(this.fileStore, "isLocalStore", true);
	}

	@Test
	void storeFileWithDigest_returnsCorrectMd5AndSize() throws Exception {
		byte[] data = new byte[] { 1, 2, 3 };
		MockMultipartFile file = new MockMultipartFile("f", "a.bin", "application/octet-stream", data);
		FileInfo info = new FileInfo(99L, "a.bin", "/2020/01/x.bin", "/store/2020/01/x.bin", "application/octet-stream");
		NoRepoFileService.SaveWithDigest swd = new NoRepoFileService.SaveWithDigest(info, "5289df737df57326fcdd22597afb1fac", 3L);
		when(this.fileService.storeAndSaveInfoWithDigest(eq(file), eq("http://test"), anyLong(), anyString(), eq(true)))
				.thenReturn(swd);
		when(this.request.getHeader(Constants.CONTEXT_KEY_USER_ID)).thenReturn("1");

		UploadResult r = this.fileStore.storeFileWithDigest(this.request, this.response, file);

		assertEquals(99L, r.getFileId());
		assertEquals("/2020/01/x.bin", r.getPath());
		assertEquals(3L, r.getSize());
		assertEquals("5289df737df57326fcdd22597afb1fac", r.getMd5());
	}

	@Test
	void storeFileWithDigest_empty_throwsBaseException() throws Exception {
		MockMultipartFile empty = new MockMultipartFile("f", "a.bin", "application/octet-stream", new byte[0]);
		assertThrows(BaseException.class, () -> this.fileStore.storeFileWithDigest(this.request, this.response, empty));
	}

	@Test
	void getPublicUrl_returnsDefaultWhenPathBlank() {
		io.github.wldos.framework.support.storage.IStore store = org.mockito.Mockito.mock(
				io.github.wldos.framework.support.storage.IStore.class,
				org.mockito.Mockito.withSettings().defaultAnswer(org.mockito.Answers.CALLS_REAL_METHODS));
		assertEquals("http://d", store.getPublicUrl("", "http://d"));
		assertEquals("http://d", store.getPublicUrl("  ", "http://d"));
	}

	@Test
	void getPublicUrl_addsLeadingSlashWhenMissing() {
		io.github.wldos.framework.support.storage.IStore store = org.mockito.Mockito.mock(
				io.github.wldos.framework.support.storage.IStore.class,
				org.mockito.Mockito.withSettings().defaultAnswer(org.mockito.Answers.CALLS_REAL_METHODS));
		when(store.getFileUrl(eq("/a/b.png"), isNull())).thenReturn("/store/a/b.png");

		assertEquals("/store/a/b.png", store.getPublicUrl("a/b.png"));
		verify(store).getFileUrl("/a/b.png", null);
	}

	@Test
	void getPublicUrl_byFileId_fallsBackToPath_whenWoFileMissing() {
		io.github.wldos.framework.support.storage.IStore store = org.mockito.Mockito.mock(
				io.github.wldos.framework.support.storage.IStore.class,
				org.mockito.Mockito.withSettings().defaultAnswer(org.mockito.Answers.CALLS_REAL_METHODS));
		when(store.getFileUrl(eq(1L), isNull())).thenReturn("");
		when(store.getFileUrl(eq("/fallback.png"), isNull())).thenReturn("/store/fallback.png");

		assertEquals("/store/fallback.png", store.getPublicUrl(1L, "fallback.png"));
	}

	@Test
	void openStoredFile_throwsFileNotFound_whenMissing(@TempDir java.nio.file.Path tmpDir) {
		when(this.fileService.getOsPathname("/missing.bin"))
				.thenReturn(tmpDir.resolve("really-missing.bin").toString());

		assertThrows(FileNotFoundException.class, () -> {
			try (InputStream ignored = this.fileStore.openStoredFile("/missing.bin")) {
				// not reached
			}
		});
	}

	@Test
	void copyStoredFileTo_streamsWithoutLoadingWholeFileInHeap(@TempDir java.nio.file.Path tmp) throws Exception {
		java.nio.file.Path f = tmp.resolve("a.bin");
		Files.write(f, new byte[] { 1, 2, 3 });
		when(this.fileService.getOsPathname("/a.bin")).thenReturn(f.toString());

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		this.fileStore.copyStoredFileTo("/a.bin", bos);

		assertArrayEquals(new byte[] { 1, 2, 3 }, bos.toByteArray());
		assertEquals(3L, this.fileStore.storedFileSize("/a.bin"));
	}

	@Test
	void storedFileExists_returnsFalseForBlankPath() {
		assertFalse(this.fileStore.storedFileExists(""));
		assertFalse(this.fileStore.storedFileExists(null));
	}
}
