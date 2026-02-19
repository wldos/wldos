/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils.img;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

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

	/**
	 * 转换为 WebP 格式
	 * 
	 * @param source 源图片路径
	 * @param output WebP 输出路径
	 * @param width 目标宽度（可选，null 则保持原尺寸）
	 * @param height 目标高度（可选，null 则保持原尺寸）
	 * @throws IOException io异常
	 */
	public static void convertToWebP(String source, String output, Integer width, Integer height) throws IOException {
		Thumbnails.Builder<?> builder = Thumbnails.of(source);
		
		if (width != null && height != null) {
			builder.size(width, height);
		} else if (width != null) {
			// 只指定宽度，按比例缩放
			BufferedImage img = ImageIO.read(new File(source));
			double scale = (double) width / img.getWidth();
			builder.scale(scale);
		} else {
			// 保持原尺寸
			builder.scale(1.0);
		}
		
		// 输出为 WebP 格式
		builder.outputFormat("webp")
		       .toFile(output);
	}
}