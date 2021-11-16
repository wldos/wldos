/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.UUIDUtils;
import com.wldos.system.storage.enums.FileAccessPolicyEnum;
import org.apache.commons.io.FilenameUtils;

/**
 * 文件上传工具类。
 *
 * @author 树悉猿
 * @date 2021/6/1
 * @version 1.0
 */
public class StoreUtils {

	public static final String fileSecurityPolicyKey = "isOss";

	public static final String PUBLIC_AREA = "/" + FileAccessPolicyEnum.PUBLIC;

	public static final String PRIVATE_AREA = "/" + FileAccessPolicyEnum.PRIVATE;

	public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("/yyyyMM/ddHHmmss");

	public static String genFilename(String ext) {
		return MONTH_FORMAT.format(new Date()) + UUIDUtils.generateShortUuid() + "." + ext;
	}

	public static String genFilename(String path, String ext) {
		return path + MONTH_FORMAT.format(new Date()) + UUIDUtils.generateShortUuid() + "." + ext;
	}

	public static String genByFilename(String path, String fileName, String ext) {
		return path + fileName + "." + ext;
	}

	protected static final Pattern ILLEGAL_CURRENT_FOLDER_PATTERN = Pattern
			.compile("^[^/]|[^/]$|/\\.{1,2}|\\\\|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}");

	public static String sanitizeFileName(final String filename) {

		if (ObjectUtil.isBlank(filename))
			return filename;

		String name = forceSingleExtension(filename);

		return name.replaceAll("\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	public static String sanitizeFolderName(final String folderName) {

		if (ObjectUtil.isBlank(folderName))
			return folderName;

		return folderName.replaceAll(
				"\\.|\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	public static boolean isValidPath(final String path) {
		if (ObjectUtil.isBlank(path))
			return false;

		if (ILLEGAL_CURRENT_FOLDER_PATTERN.matcher(path).find())
			return false;

		return true;
	}

	public static String forceSingleExtension(final String filename) {
		return filename.replaceAll("\\.(?![^.]+$)", "_");
	}

	public static boolean isSingleExtension(final String filename) {
		return filename.matches("[^\\.]+\\.[^\\.]+");
	}

	public static void checkDirAndCreate(File dir) {
		if (!dir.exists())
			dir.mkdirs();
	}

	public static File getUniqueFile(final File file) {
		if (!file.exists())
			return file;

		File tmpFile = new File(file.getAbsolutePath());
		File parentDir = tmpFile.getParentFile();
		int count = 1;
		String extension = FilenameUtils.getExtension(tmpFile.getName());
		String baseName = FilenameUtils.getBaseName(tmpFile.getName());
		do {
			tmpFile = new File(parentDir, baseName + "(" + count++ + ")."
					+ extension);
		} while (tmpFile.exists());
		return tmpFile;
	}

	public static boolean isOss(String accessPolicy) {
		return FileAccessPolicyEnum.PRIVATE.toString().equals(accessPolicy);
	}

	public static String getSecurityFileName(String origName) {
		String fileName = sanitizeFileName(origName);
		if (isSingleExtension(origName)) {
			return forceSingleExtension(fileName);
		}
		return fileName;
	}
}

