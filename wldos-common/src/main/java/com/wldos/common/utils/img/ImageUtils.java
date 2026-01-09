/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.img;

import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图像处理。
 *
 * @author 元悉宇宙
 * @date 2021/7/3
 * @version 1.0
 */
public class ImageUtils {

	private ImageUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 按尺寸创建缩略图
	 *
	 * @param source 输入源
	 * @param output 输出源
	 * @param width 像素宽度
	 * @param height 像素高度
	 * @throws IOException io
	 */
	public static void imgThumb(String source, String output, int width, int height) throws IOException {
		Thumbnails.of(source).size(width, height).toFile(output);
	}

	/**
	 * 按比例缩放生成原图宽高比锁定的缩略图
	 *
	 * @param source 输入源
	 * @param output 输出源
	 * @param scale  原始宽高比的缩放比例
	 * @throws IOException io
	 */
	public static void imgScale(String source, String output, double scale) throws IOException {
		Thumbnails.of(source).scale(scale).toFile(output);
	}
}