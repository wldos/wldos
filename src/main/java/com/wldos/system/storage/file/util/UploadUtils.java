/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.file.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.UUIDUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 文件上传工具类。
 *
 * @author 树悉猿
 * @date 2021/6/1
 * @version 1.0
 */
public class UploadUtils {

	/** 日期格式化对象 */
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

	/**
	 * 文件名称消毒，去除非法、潜在威胁符号
	 *
	 * @param filename
	 * @return
	 */
	public static String sanitizeFileName(final String filename) {

		if (ObjectUtil.isBlank(filename))
			return filename;

		String name = forceSingleExtension(filename);

		// Remove \ / | : ? * " < > 'Control Chars' with _
		return name.replaceAll("\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	/**
	 * 文件夹名称消毒，去除非法、潜在威胁符号
	 *
	 * @param folderName
	 * @return
	 */
	public static String sanitizeFolderName(final String folderName) {

		if (ObjectUtil.isBlank(folderName))
			return folderName;

		// Remove . \ / | : ? * " < > 'Control Chars' with _
		return folderName.replaceAll(
				"\\.|\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	/**
	 * 是否有效路径
	 *
	 * @param path
	 * @return
	 */
	public static boolean isValidPath(final String path) {
		if (ObjectUtil.isBlank(path))
			return false;

		if (ILLEGAL_CURRENT_FOLDER_PATTERN.matcher(path).find())
			return false;

		return true;
	}

	/**
	 * Replaces all dots in a filename with underscores except the last one.
	 *
	 * @param filename
	 *            filename to sanitize
	 * @return string with a single dot only
	 */
	public static String forceSingleExtension(final String filename) {
		return filename.replaceAll("\\.(?![^.]+$)", "_");
	}

	/**
	 * Checks if a filename contains more than one dot.
	 *
	 * @param filename
	 *            filename to check
	 * @return <code>true</code> if filename contains severals dots, else
	 *         <code>false</code>
	 */
	public static boolean isSingleExtension(final String filename) {
		return filename.matches("[^\\.]+\\.[^\\.]+");
	}

	/**
	 * Checks a directory for existence and creates it if non-existent.
	 *
	 * @param dir
	 *            directory to check/create
	 */
	public static void checkDirAndCreate(File dir) {
		if (!dir.exists())
			dir.mkdirs();
	}

	/**
	 * Iterates over a base name and returns the first non-existent file.<br />
	 * This method extracts a file's base name, iterates over it until the first
	 * non-existent appearance with <code>basename(n).ext</code>. Where n is a
	 * positive integer starting from one.
	 *
	 * @param file
	 *            base file
	 * @return first non-existent file
	 */
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

//	public static void main(String[] args) {
//		System.out.println(generateFilename("/base", "gif"));
//	}

}

