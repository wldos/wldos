/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.utils;

import java.io.FileInputStream;

/**
 * 文件类型工具类。
 *
 * @author 元悉宇宙
 * @date 2022/10/27
 * @version 1.0
 */
public class FileTypeUtils {
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (byte b : src) {
			int v = b & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}


	/**
	 常用文件的文件头如下：(以前六位为准)
	 JPEG (jpg)，文件头：FFD8FF
	 PNG (png)，文件头：89504E47
	 GIF (gif)，文件头：47494638
	 TIFF (tif)，文件头：49492A00
	 Windows Bitmap (bmp)，文件头：424D
	 CAD (dwg)，文件头：41433130
	 Adobe Photoshop (psd)，文件头：38425053
	 Rich Text Format (rtf)，文件头：7B5C727466
	 XML (xml)，文件头：3C3F786D6C
	 HTML (html)，文件头：68746D6C3E
	 Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
	 Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
	 Outlook (pst)，文件头：2142444E
	 MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
	 MS Access (mdb)，文件头：5374616E64617264204A
	 WordPerfect (wpd)，文件头：FF575043
	 Postscript (eps.or.ps)，文件头：252150532D41646F6265
	 Adobe Acrobat (pdf)，文件头：255044462D312E
	 Quicken (qdf)，文件头：AC9EBD8F
	 Windows Password (pwl)，文件头：E3828596
	 ZIP Archive (zip)，文件头：504B0304
	 RAR Archive (rar)，文件头：52617221
	 Wave (wav)，文件头：57415645
	 AVI (avi)，文件头：41564920
	 Real Audio (ram)，文件头：2E7261FD
	 Real Media (rm)，文件头：2E524D46
	 MPEG (mpg)，文件头：000001BA
	 MPEG (mpg)，文件头：000001B3
	 Quicktime (mov)，文件头：6D6F6F76
	 Windows Media (asf)，文件头：3026B2758E66CF11
	 MIDI (mid)，文件头：4D546864
	 */
	public static String checkType(String headBytes) {

		switch (headBytes) {
			case "FFD8FF":
				return "jpg";
			case "89504E":
				return "png";
			case "474946":
				return "gif";

			default:
				return "0000";
		}
	}

	/**
	 * 常用图片类型检查
	 *
	 * @param headByte 头字节
	 * @return 类型扩展：jpg、png等
	 */
	public static String checkPicType(String headByte) {

		switch (headByte) {
			case "FFD8FF":
				return "jpg";
			case "89504E":
				return "png";
			case "474946":
				return "gif";
			case "49492A":
				return "tif";

			default:
				return "0000";
		}
	}

	public static void main1(String[] args) throws Exception {
		FileInputStream is = new FileInputStream("F:\\相册\\微信图片\\03.jpg");
		byte[] b = new byte[3];
		is.read(b, 0, b.length);
		String xxx = bytesToHexString(b);
		xxx = xxx.toUpperCase();
		System.out.println("头文件是：" + xxx);
		String ooo = checkType(xxx);
		System.out.println("后缀名是：" + ooo);

	}
}