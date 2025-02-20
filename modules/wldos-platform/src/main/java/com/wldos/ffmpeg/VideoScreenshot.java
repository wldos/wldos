/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.ffmpeg;

import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 视频切图，一键生成切图
 */
public class VideoScreenshot {

    /**
     * 视频切帧（跳跃截取，每隔2秒取一张图）
     *
     * @param videoFile 视频文件
     * @param outputDir 输出目录
     * @param maxFrames 最大不重复切帧数量
     */
    public static void extractFramesFromVideoInterval(File videoFile, String outputDir, int maxFrames) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile);
        try {
            frameGrabber.start();

            // 设置视频流索引
            frameGrabber.setVideoStream(0);

            System.out.println("视频时长: " + frameGrabber.getLengthInFrames() + " frames");
            System.out.println("视频码率: " + frameGrabber.getFrameRate());
            System.out.println("视频格式: " + frameGrabber.getFormat());
            System.out.println("视频编码: " + frameGrabber.getVideoCodec());

            Java2DFrameConverter converter = new Java2DFrameConverter();
            int frameNumber = 0;
            int extractedFrames = 0;

            // 每隔几帧取一帧
            int frameInterval = Math.max(1, (int) (frameGrabber.getFrameRate() / 2)); // 每秒取约2帧

            Frame frame;
            // 使用 grabFrame() 替代 grabImage()
            while ((frame = frameGrabber.grabFrame(true, true, true, false)) != null) {
                if (extractedFrames >= maxFrames) {
                    break;
                }

                // 确保是视频帧
                if (frame.image == null) {
                    continue;
                }

                // 按间隔取帧
                if (frameNumber % frameInterval != 0) {
                    frameNumber++;
                    continue;
                }

                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                    File outputfile = new File(outputDir + File.separator +
                            String.format("frame_%06d.png", frameNumber));
                    ImageIO.write(bufferedImage, "png", outputfile);
                    extractedFrames++;
                    System.out.println("抽取帧" + frameNumber +
                            ", 有效帧号: " + extractedFrames);
                }
                frameNumber++;
            }

            System.out.println("切帧完成。 帧处理总量: " +
                    frameNumber + ", 有效帧总量: " + extractedFrames);
        } catch (Exception e) {
            System.err.println("抽取帧时报错: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            try {
                frameGrabber.stop();
                frameGrabber.release();
            } catch (Exception e) {
                System.err.println("关闭抓取器时报错: " + e.getMessage());
            }
        }
    }

    /**
     * 视频截图（图片去重）
     *
     * @param videoFile 视频文件
     * @param outputDir 输出目录
     * @param maxFrames 最大有效帧数量
     */
    public static void extractFramesFromVideo(File videoFile, String outputDir, int maxFrames) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile);
        try {
            frameGrabber.start();
            frameGrabber.setVideoStream(0);

            System.out.println("视频时长: " + frameGrabber.getLengthInFrames() + " frames");
            System.out.println("视频码率: " + frameGrabber.getFrameRate());

            Java2DFrameConverter converter = new Java2DFrameConverter();
            int frameNumber = 0;
            int extractedFrames = 0;
            int[] previousHistogram = null;

            // 计算帧间隔（每秒取2帧）
            int frameInterval = Math.max(1, (int) (frameGrabber.getFrameRate() / 2));

            Frame frame;
            // 1.逐帧处理
            while ((frame = frameGrabber.grabFrame(true, true, true, false)) != null) {
                if (extractedFrames >= maxFrames) break;

                // 按间隔采样帧
                if (frameNumber % frameInterval != 0) {
                    frameNumber++;
                    continue;
                }

                if (frame.image == null) {
                    frameNumber++;
                    continue;
                }

                // 转换为BufferedImage
                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                    // 2.快速计算当前帧的特征：直方图特征
                    int[] currentHistogram = getFastHistogram(bufferedImage);

                    // 3.检查与前一帧的相似度
                    if (previousHistogram == null || !isSimilar(previousHistogram, currentHistogram, 0.75)) {
                        File outputfile = new File(outputDir + File.separator +
                                String.format("frame_%06d.png", frameNumber));
                        // 保存不重复的帧
                        ImageIO.write(bufferedImage, "png", outputfile);

                        previousHistogram = currentHistogram;
                        extractedFrames++;
                        System.out.println("Extracted frame " + frameNumber +
                                ", total extracted: " + extractedFrames);
                    }
                    bufferedImage.flush(); // 释放图像资源
                }
                frameNumber++;
            }

            System.out.println("Finished extracting frames. Total processed: " +
                    frameNumber + ", Total extracted: " + extractedFrames);

        } finally {
            frameGrabber.stop();
            frameGrabber.release();
        }
    }
    // 图像特征值提取
    private static int[] getFastHistogram(BufferedImage image) {
        int[] histogram = new int[64]; // 64级灰度值
        int width = image.getWidth();
        int height = image.getHeight();

        // 大幅增加采样步长
        int sampleStepX = width / 20;  // 只取宽度的1/20的点
        int sampleStepY = height / 20; // 只取高度的1/20的点

        sampleStepX = Math.max(1, sampleStepX);
        sampleStepY = Math.max(1, sampleStepY);

        for (int y = 0; y < height; y += sampleStepY) {
            for (int x = 0; x < width; x += sampleStepX) {
                int rgb = image.getRGB(x, y);
                // 简化的灰度计算，只使用绿色通道
                int gray = (rgb >> 8) & 0xff;
                // 将256级别的灰度压缩到64级别
                histogram[gray >> 2]++;
            }
        }
        return histogram;
    }

    // 相似度比较
    private static boolean isSimilar(int[] hist1, int[] hist2, double threshold) {
        double diff = 0;
        int total = 0;

        // 简化的相似度计算
        for (int i = 0; i < hist1.length; i++) {
            diff += Math.abs(hist1[i] - hist2[i]);
            total += hist1[i] + hist2[i];
        }

        // 归一化差异值
        double similarity = 1.0 - (diff / total);
        return similarity >= threshold;
    }

    public static void main(String[] args) throws Exception {
        String videoPath = "C:/Users/30699/Videos/2月16日(4).mp4";
        String outputPath = "D:/simple";
        //int frameNumber = 100; // 截取第 300 帧
        //captureScreenshot(videoPath, outputPath, frameNumber);
        extractFramesFromVideo(new File(videoPath), outputPath, 200);
    }
}
