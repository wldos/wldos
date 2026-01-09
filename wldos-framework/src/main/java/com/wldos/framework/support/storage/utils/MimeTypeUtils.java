/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 多媒体类型工具类。
 *
 * @author 元悉宇宙
 * @date 2022/10/27
 * @version 1.0
 */
public class MimeTypeUtils {

	private static boolean isBMP(byte[] buf) {
		byte[] markBuf = "BM".getBytes();  //BMP图片文件的前两个字节
		return compare(buf, markBuf);
	}

	private static boolean isICON(byte[] buf) {
		byte[] markBuf = { 0, 0, 1, 0, 1, 0, 32, 32 };
		return compare(buf, markBuf);
	}

	private static boolean isWEBP(byte[] buf) {
		byte[] markBuf = "RIFF".getBytes(); //WebP图片识别符
		return compare(buf, markBuf);
	}

	private static boolean isGIF(byte[] buf) {

		byte[] markBuf = "GIF89a".getBytes(); //GIF识别符
		if (compare(buf, markBuf)) {
			return true;
		}
		markBuf = "GIF87a".getBytes(); //GIF识别符
		return compare(buf, markBuf);
	}


	private static boolean isPNG(byte[] buf) {

		byte[] markBuf = { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A }; //PNG识别符
		return compare(buf, markBuf);
	}

	private static boolean isJPEG(byte[] buf) {
		String headString = Objects.requireNonNull(FileTypeUtils.bytesToHexString(buf)).toUpperCase();

		return headString.equals("FFD8FF");
	}

	private static boolean isJPEGHeader(byte[] buf) {
		byte[] markBuf = { (byte) 0xff, (byte) 0xd8 }; //JPEG开始符

		return compare(buf, markBuf);
	}

	private static boolean isJPEGFooter(byte[] buf)//JPEG结束符
	{
		byte[] markBuf = { (byte) 0xff, (byte) 0xd9 };
		return compare(buf, markBuf);
	}


	/**
	 * 获取文件的mimeType
	 * @param filename 文件名
	 * @return MIME类型
	 */
	private static String getMimeType(String filename) throws IOException {
		String mimeType = readType(filename);
		return String.format("image/%s", mimeType);
	}

	// 判断依据的最大字节长度
	private static final int MAX_BYTE_LENGTH = 8;

	/**
	 * 读取文件类型
	 * @param filename 图片文件名
	 * @return MIME类型
	 * @throws IOException io异常
	 */
	public static String readType(String filename) throws IOException {

		FileInputStream fis = null;
		try {
			File f = new File(filename);
			if (!f.exists() || f.isDirectory() || f.length() < MAX_BYTE_LENGTH) {
				throw new IOException("the file [" + f.getAbsolutePath() + "] is not image !");
			}

			fis = new FileInputStream(f);
			byte[] bufHeaders = readInputStreamAt(fis, 0, MAX_BYTE_LENGTH * 3);
			if (isJPEG(bufHeaders)) {
				return "jpeg";
			}
			if (isPNG(bufHeaders)) {
				return "png";
			}
			if (isGIF(bufHeaders)) {
				return "gif";
			}
			if (isWEBP(bufHeaders)) {
				return "webp";
			}
			if (isBMP(bufHeaders)) {
				return "bmp";
			}
			if (isICON(bufHeaders)) {
				return "ico";
			}
			throw new IOException("the image's format is unknown!");
		}
		finally {
			try {
				if (fis != null) {
					fis.close();
				}
			}
			catch (Exception ignored) {
			}
		}
	}

	public static String readType(InputStream in) throws IOException {

		if (in == null) {
			throw new IOException("文件流无效!");
		}

		byte[] bufHeaders = readInputStreamAt(in, 0, MAX_BYTE_LENGTH * 3);
		if (isJPEG(bufHeaders)) {
			return "jpeg";
		}
		if (isPNG(bufHeaders)) {
			return "png";
		}
		if (isGIF(bufHeaders)) {
			return "gif";
		}
		if (isWEBP(bufHeaders)) {
			return "webp";
		}
		if (isBMP(bufHeaders)) {
			return "bmp";
		}
		if (isICON(bufHeaders)) {
			return "ico";
		}
		throw new IOException("the image's format is unknown!");
	}

	/**
	 * 标示一致性比较
	 * @param buf  待检测标示
	 * @param markBuf 标识符字节数组
	 * @return 返回false标示标示不匹配
	 */
	private static boolean compare(byte[] buf, byte[] markBuf) {
		for (int i = 0; i < markBuf.length; i++) {
			byte b = markBuf[i];
			byte a = buf[i];

			if (a != b) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param fis 输入流对象
	 * @param skip_length 跳过位置长度
	 * @param length 要读取的长度
	 * @return 字节数组
	 * @throws IOException io
	 */
	private static byte[] readInputStreamAt(InputStream fis, long skip_length, int length) throws IOException {
		byte[] buf = new byte[length];
		fis.skip(skip_length);
		fis.read(buf, 0, length);
		return buf;
	}
}