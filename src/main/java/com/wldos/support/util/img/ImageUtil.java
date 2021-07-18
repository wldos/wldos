/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.util.img;

import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图像处理。
 *
 * @author 树悉猿
 * @date 2021/7/3
 * @version 1.0
 */
public class ImageUtil {

	private ImageUtil() {
		throw new IllegalStateException("Utility class");
	}
	/**
	 * 按尺寸创建缩略图
	 *
	 * @param source 输入源
	 * @param output 输出源
	 * @param width 像素宽度
	 * @param height 像素高度
	 * @throws IOException
	 */
	public static void imgThumb(String source, String output, int width, int height) throws IOException {
		Thumbnails.of(source).size(width, height).toFile(output);
	}

	/**
	 * 按比例生成缩略图
	 *
	 * @param source 输入源
	 * @param output 输出源
	 * @param scale  宽高比
	 * @throws IOException
	 */
	public static void imgScale(String source, String output, double scale) throws IOException {
		Thumbnails.of(source).scale(scale).toFile(output);
	}
}
