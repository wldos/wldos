/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * 验证码操作类。
 *
 * @author 树悉猿
 * @date 2021/4/30
 * @version 1.0
 */
public class VerifyCode {
	/** 验证码图片长 */
	private int width = 120;

	/** 验证码图片宽 */
	private int height = 40;

	/** 验证码图片色位1~8 */
	private int colorBit = 4;

	/** 验证码图片 */
	private BufferedImage image;

	/** 文本内容 */
	private String text;

	private static final Random r = new Random();

	//private String[] fontNames = {"宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312"};
	//字体数组
	private static final String[] fontNames = { "Georgia" };

	//验证码数组
	private static final String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

	private VerifyCode(int width, int height, int colorBit) {
		this.width = width;
		this.height = height;
		this.colorBit = colorBit;
		this.image = createImage();
		Graphics2D g = (Graphics2D) image.getGraphics();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			String s = codes.charAt(r.nextInt(codes.length())) + "";
			sb.append(s);
			float x = i * 1.0F * this.width / 4;
			g.setFont(randomFont());
			g.setColor(randomColor());
			g.drawString(s, x, height - 5);
		}
		this.text = sb.toString();
		drawLine(image);
	}

	/**
	 * 生成验证码
	 *
	 * @param width
	 * @param height
	 * @param colorBit
	 * @return
	 */
	public static VerifyCode genCaptcha(int width, int height, int colorBit) {
		return new VerifyCode(width, height, colorBit);
	}

	public void out(OutputStream out) {
		try {
			ImageIO.write(this.image, "JPEG", out);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String outBase64() {
		int size = 50000; // 指定50k的内存足够开辟空间，防止jdk8盲目膨胀
		ByteArrayOutputStream stream = new ByteArrayOutputStream(size);
		// 输出图片流
		this.out(stream);
		return Base64.encodeBase64String(stream.toByteArray());
	}

	/**
	 * 获取随机的颜色
	 *
	 * @return
	 */
	private Color randomColor() {
		int r = this.r.nextInt(225);
		int g = this.r.nextInt(225);
		int b = this.r.nextInt(225);
		return new Color(r, g, b);
	}

	/**
	 * 获取随机字体
	 *
	 * @return
	 */
	private Font randomFont() {
		int index = r.nextInt(fontNames.length);
		String fontName = fontNames[index];
		//随机获取字体的样式，0是无样式，1是加粗，2是斜体，3是加粗加斜体
		int style = r.nextInt(4);
		int size = r.nextInt(10) + 24;
		return new Font(fontName, style, size);
	}

	/**
	 * 画干扰线，验证码干扰线用来防止计算机解析图片
	 *
	 * @param image 图片
	 */
	private void drawLine(BufferedImage image) {
		int num = r.nextInt(10); //定义干扰线的数量
		Graphics2D g = (Graphics2D) image.getGraphics();
		for (int i = 0; i < num; i++) {
			int x1 = r.nextInt(this.width);
			int y1 = r.nextInt(this.height);
			int x2 = r.nextInt(this.width);
			int y2 = r.nextInt(this.height);
			g.setColor(randomColor());
			g.drawLine(x1, y1, x2, y2);
		}
	}

	/**
	 * 创建图片的方法
	 *
	 * @return 图像
	 */
	private BufferedImage createImage() {
		//创建图片缓冲区
		BufferedImage image = new BufferedImage(this.width, this.height, this.colorBit);
		//获取画笔
		Graphics2D g = (Graphics2D) image.getGraphics();
		//设置背景色随机
		g.setColor(new Color(255, 255, r.nextInt(245) + 10));
		g.fillRect(0, 0, this.width, this.height);
		//返回一个图片
		return image;
	}

	/**
	 * 获取验证码图片的方法
	 *
	 * @return 图像
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * 获取验证码文本的方法
	 *
	 * @return 文本
	 */
	public String getText() {
		return text;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getColorBit() {
		return colorBit;
	}

	public void setColorBit(int colorBit) {
		this.colorBit = colorBit;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setText(String text) {
		this.text = text;
	}
}
