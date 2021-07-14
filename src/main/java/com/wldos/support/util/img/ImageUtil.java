/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util.img;

import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图像处理。
 *
 * @Title ImageUtil
 * @Package com.wldos.support.util.img
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/7/3
 * @Version 1.0
 */
public class ImageUtil {
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
