/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.storage.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.UUIDUtils;
import com.wldos.common.enums.FileAccessPolicyEnum;
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

	/** 静态资源区，公开访问, 物理存储根路径下再创建一级，方便权限控制等 */
	public static final String PUBLIC_AREA = "/" + FileAccessPolicyEnum.PUBLIC;
	/** 私密文件区，必须登陆，必须检查属主 */
	public static final String PRIVATE_AREA = "/" + FileAccessPolicyEnum.PRIVATE;

	/** 日期格式化对象 */
	public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("/yyyy/MM/ddHHmmss");

	/**
	 * 获取日期格式化+随机码的文件名，带扩展名，用于物理存储的记录
	 *
	 * @param ext 文件的真实扩展名（不含点）
	 * @return 格式化的文件名
	 */
	public static String genFilename(String ext) {
		return MONTH_FORMAT.format(new Date()) + UUIDUtils.generateShortUuid() + "." + ext;
	}

	/**
	 * 通过路径和扩展名获取日期格式化+随机码的文件名，带扩展名，用于物理存储的记录
	 *
	 * @param path 路径，绝对路径或相对路径由调用方确定
	 * @param ext 不带点的扩展名
	 * @return 带路径文件名
	 */
	public static String genFilename(String path, String ext) {
		return path + MONTH_FORMAT.format(new Date()) + UUIDUtils.generateShortUuid() + "." + ext;
	}

	/** 非法路径名正则表达式 */
	protected static final Pattern ILLEGAL_CURRENT_FOLDER_PATTERN = Pattern
			.compile("^[^/]|[^/]$|/\\.{1,2}|\\\\|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}");

	/**
	 * 文件名称消毒，去除非法、潜在威胁符号
	 *
	 * @param filename 文件名
	 * @return 安全文件名
	 */
	public static String sanitizeFileName(final String filename) {

		if (ObjectUtils.isBlank(filename))
			return filename;

		String name = forceSingleExtension(filename);

		// Remove \ / | : ? * " < > 'Control Chars' with _
		return name.replaceAll("\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	/**
	 * 文件夹名称消毒，去除非法、潜在威胁符号
	 *
	 * @param folderName 文件名
	 * @return 安全文件名
	 */
	public static String sanitizeFolderName(final String folderName) {

		if (ObjectUtils.isBlank(folderName))
			return folderName;

		// Remove . \ / | : ? * " < > 'Control Chars' with _
		return folderName.replaceAll(
				"\\.|\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	/**
	 * 是否有效路径
	 *
	 * @param path 路径
	 * @return 是否有效
	 */
	public static boolean isValidPath(final String path) {
		if (ObjectUtils.isBlank(path))
			return false;

		return !ILLEGAL_CURRENT_FOLDER_PATTERN.matcher(path).find();
	}

	/**
	 * 将文件名中的所有点替换为下划线，保留最后一个
	 *
	 * @param filename 要清理的文件名
	 * @return 仅带一个点的字符串
	 */
	public static String forceSingleExtension(final String filename) {
		return filename.replaceAll("\\.(?![^.]+$)", "_");
	}

	/**
	 * 检查文件名是否包含多个点
	 *
	 * @param filename 要检查的文件名
	 * @return <code>true</code> 如果文件名包含多个点, 否则
	 *         <code>false</code>
	 */
	public static boolean isSingleExtension(final String filename) {
		return filename.matches("[^\\.]+\\.[^\\.]+");
	}

	/**
	 * 检查目录是否存在，如果不存在，则创建该目录
	 *
	 * @param dir 要检查的目录
	 */
	public static void checkDirAndCreate(File dir) {
		if (!dir.exists())
			dir.mkdirs();
	}

	/**
	 * 迭代文件目录，并找到第一个不存在的路径。<br />
	 * 提取文件不包含扩展名的路径名, 迭代路径名直到找到首个
	 * 实际不存在但出现在 <code>路径名(n).扩展</code>. 在这里n是一个
	 * 从1开始的正整数。
	 *
	 * @param file 基础文件
	 * @return 首个不存在的文件目录
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

	/**
	 * 是否以oss方式私密访问
	 *
	 * @param accessPolicy 访问策略，见<code>FileAccessPolicyEnum</code>
	 * @return 是否
	 */
	public static boolean isOss(String accessPolicy) {
		return FileAccessPolicyEnum.PRIVATE.toString().equals(accessPolicy);
	}

	/**
	 * 获取安全文件名，去除多余的点，去除不安全符号
	 *
	 * @param origName 初始文件名
	 * @return 处理过的文件名
	 */
	public static String getSecurityFileName(String origName) {
		String fileName = sanitizeFileName(origName);
		if (isSingleExtension(origName)) {
			return forceSingleExtension(fileName);
		}
		return fileName;
	}
}