/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.ffmpeg;


import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.FFmpegFrameFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 直播视频处理
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/17
 */
public class LiveVideoProcessor {

    public enum VideoQuality {
        HIGHEST("最高画质"), // 接近无损 文件很大 约是原来的5~6倍
        BALANCED("平衡画质"), // 平衡画质和文件大小
        LOWEST("最低画质");  // 优先文件大小

        private final String description;

        VideoQuality(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static void main(String[] args) {
        /*f (args.length < 3) {
            System.out.println("使用方法: java VideoProcessorApp <输入视频路径> <左边界位置> <比例(如9:16)>");
            return;
        }*/

        String inputPath = "C:/Users/30699/Videos/2025-02-16_21-19-45.mkv"; // 处理的视频
        int leftBoundary = 1042; // Integer.parseInt(args[1]); 画面左边矩
        String aspectRatio = "9:16"; // 画面宽高比
        VideoQuality setQuality = VideoQuality.BALANCED; // 画质要求
    
        
        processVideo(inputPath, leftBoundary, aspectRatio, setQuality);
    }

    public static void processVideo(String inputPath, int leftBoundary, String aspectRatio, VideoQuality quality) {
        FFmpegLogCallback.set();
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        FFmpegFrameFilter filter = null;
        
        try {
            grabber = new FFmpegFrameGrabber(inputPath);
            grabber.start();
            
            // 打印输入视频信息
            System.out.println("\n输入视频信息:");
            System.out.println("尺寸: " + grabber.getImageWidth() + "x" + grabber.getImageHeight());
            System.out.println("帧率: " + grabber.getFrameRate());
            System.out.println("时长: " + grabber.getLengthInTime() / 1000000.0 + "秒");
            System.out.println("视频编码: " + grabber.getVideoCodec());
            System.out.println("视频比特率: " + grabber.getVideoBitrate() + " bps");
            System.out.println("音频通道: " + grabber.getAudioChannels());
            System.out.println("采样率: " + grabber.getSampleRate());
            System.out.println("音频比特率: " + grabber.getAudioBitrate() + " bps");
            
            int sourceWidth = grabber.getImageWidth();
            int sourceHeight = grabber.getImageHeight();
            
            // 计算目标宽度
            String[] ratioParts = aspectRatio.split(":");
            double targetRatio = Double.parseDouble(ratioParts[0]) / Double.parseDouble(ratioParts[1]);
            int targetWidth = (int)Math.round(sourceHeight * targetRatio);
            if (targetWidth % 2 != 0) {
                targetWidth += 1;
            }
            
            // 确保裁剪区域不超出源视频
            if (leftBoundary + targetWidth > sourceWidth) {
                System.out.println("警告: 裁剪区域超出视频范围，将自动调整");
                leftBoundary = Math.max(0, sourceWidth - targetWidth);
            }
            
            // 创建输出文件
            String outputPath = inputPath.substring(0, inputPath.lastIndexOf('.')) + "_cropped.mp4";
            
            // 获取源视频比特率，如果为0则使用预设值
            int sourceBitrate = grabber.getVideoBitrate();
            if (sourceBitrate <= 0) {
                // 根据分辨率计算合适的比特率
                sourceBitrate = calculateBitrate(targetWidth, sourceHeight);
            }
            
            // 根据质量级别设置编码参数
            VideoEncodingSettings settings = getEncodingSettings(quality, targetWidth, sourceHeight);
            
            recorder = new FFmpegFrameRecorder(outputPath, targetWidth, sourceHeight);
            
            // 应用视频设置
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoBitrate(settings.bitrate);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            
            // 应用编码设置
            recorder.setVideoQuality(0);
            recorder.setVideoOption("crf", String.valueOf(settings.crf));
            recorder.setVideoOption("preset", settings.preset);
            recorder.setVideoOption("x264-params", settings.x264Params);
            recorder.setVideoOption("tune", "film");
            recorder.setVideoOption("profile", settings.profile);
            recorder.setVideoOption("level", settings.level);
            
            // 音频始终保持最高质量
            if (grabber.getAudioChannels() > 0) {
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.setAudioBitrate(320000);
                recorder.setAudioQuality(0);
            }
            
            recorder.start();
            
            // 创建裁剪过滤器，添加scale过滤器处理尺寸变化
            String filterStr = String.format("crop=%d:%d:%d:0,scale=%d:%d", 
                targetWidth, sourceHeight, leftBoundary, 
                targetWidth, sourceHeight);
            
            filter = new FFmpegFrameFilter(filterStr, sourceWidth, sourceHeight);
            filter.start();
            
            // 设置输入和输出格式
            filter.setPixelFormat(grabber.getPixelFormat());
            recorder.setPixelFormat(grabber.getPixelFormat());
            
            // 处理帧
            Frame frame;
            double frameRate = grabber.getFrameRate();
            double durationInSeconds = grabber.getLengthInTime() / 1000000.0;
            long totalFrames = (long)(frameRate * durationInSeconds);
            long processedFrames = 0;
            
            System.out.println("预计总帧数: " + totalFrames);
            
            while ((frame = grabber.grab()) != null) {
                if (frame.image != null) {
                    filter.push(frame);
                    Frame filteredFrame = filter.pull();
                    if (filteredFrame != null) {
                        recorder.record(filteredFrame);
                        processedFrames++;
                        
                        // 每秒更新一次进度，并确保最后一次更新显示100%
                        if (processedFrames % (int)frameRate == 0 || frame.keyFrame) {
                            double progress = Math.min((processedFrames * 100.0) / totalFrames, 100.0);
                            System.out.printf("\r处理进度: %.2f%% (%d/%d帧)", 
                                progress, processedFrames, totalFrames);
                            System.out.flush();
                        }
                    }
                } else if (frame.samples != null && grabber.getAudioChannels() > 0) {
                    recorder.recordSamples(frame.samples);
                }
            }
            
            // 确保显示最终进度
            System.out.printf("\r处理进度: 100.00%% (%d/%d帧)\n", processedFrames, processedFrames);
            System.out.println("实际处理帧数: " + processedFrames);
            System.out.println("处理完成:");
            System.out.println("输出文件: " + outputPath);
            System.out.println("目标尺寸: " + targetWidth + "x" + sourceHeight);
            System.out.println("裁剪位置: " + leftBoundary + " -> " + (leftBoundary + targetWidth));
            
        } catch (Exception e) {
            System.err.println("处理视频时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭所有资源
            try {
                if (filter != null) {
                    filter.close();
                }
                if (recorder != null) {
                    recorder.close();
                }
                if (grabber != null) {
                    grabber.close();
                }
            } catch (Exception e) {
                System.err.println("关闭资源时出错: " + e.getMessage());
            }
        }
    }

    // 根据分辨率计算合适的比特率
    private static int calculateBitrate(int width, int height) {
        int pixelCount = width * height;
        if (pixelCount >= 8294400) { // 4K (3840x2160)
            return 25000000; // 25 Mbps
        } else if (pixelCount >= 2073600) { // 1080p
            return 10000000; // 10 Mbps
        } else if (pixelCount >= 921600) { // 720p
            return 5000000; // 5 Mbps
        } else {
            return 2500000; // 2.5 Mbps
        }
    }
    private static double calculateEdgeDifference(BufferedImage image, int x) {
        if (x <= 1 || x >= image.getWidth() - 1) return 0;
        
        double totalDifference = 0;
        int height = image.getHeight();
        
        // 检查边界左右各1像素的差异
        for (int y = 0; y < height; y++) {
            // 获取边界左右的像素
            int leftPixel = image.getRGB(x - 1, y);
            int rightPixel = image.getRGB(x + 1, y);
            
            // 计算颜色差异
            double difference = getPixelDifference(leftPixel, rightPixel);
            totalDifference += difference;
        }
        
        // 返回平均差异值
        return totalDifference / height;
    }

    private static double getPixelDifference(int rgb1, int rgb2) {
        // 提取RGB分量
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        
        // 计算亮度差异
        double brightness1 = (r1 + g1 + b1) / 3.0;
        double brightness2 = (r2 + g2 + b2) / 3.0;
        
        // 计算颜色差异
        double colorDiff = Math.abs(brightness1 - brightness2);
        
        return colorDiff;
    }

    private static Frame scaleAndCropFrame(Frame inputFrame, CropParameters cropParams) {
        try {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage originalImage = converter.convert(inputFrame);
            
            if (originalImage == null) {
                return inputFrame;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 创建目标尺寸的图像
            BufferedImage processedImage = new BufferedImage(
                cropParams.width,
                cropParams.height,
                BufferedImage.TYPE_3BYTE_BGR
            );

            Graphics2D g2d = processedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            // 计算缩放后的尺寸，保持原始比例
            double originalRatio = (double) originalWidth / originalHeight;
            double targetRatio = (double) cropParams.width / cropParams.height;
            
            int scaledWidth, scaledHeight;
            int x = 0, y = 0;
            
            if (originalRatio > targetRatio) {
                // 原始画面较宽，以高度为基准进行缩放
                scaledHeight = cropParams.height;
                scaledWidth = (int)(originalWidth * ((double)cropParams.height / originalHeight));
                x = (cropParams.width - scaledWidth) / 2; // 水平居中
            } else {
                // 原始画面较窄，以宽度为基准进行缩放
                scaledWidth = cropParams.width;
                scaledHeight = (int)(originalHeight * ((double)cropParams.width / originalWidth));
                y = (cropParams.height - scaledHeight) / 2; // 垂直居中
            }

            // 填充黑色背景
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, cropParams.width, cropParams.height);
            
            // 绘制缩放后的图像
            g2d.drawImage(
                originalImage,
                x, y, x + scaledWidth, y + scaledHeight,  // 目标矩形
                0, 0, originalWidth, originalHeight,       // 源矩形
                null
            );
            
            g2d.dispose();
            return converter.convert(processedImage);
        } catch (Exception e) {
            System.err.println("处理帧时出错: " + e.getMessage());
            e.printStackTrace();
            return inputFrame;
        }
    }

    private static class CropParameters {
        final int width;
        final int height;
        final int x;
        final int y;

        CropParameters(int width, int height, int x, int y) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
    }

    private static class VideoEncodingSettings {
        final int bitrate;
        final int crf;
        final String preset;
        final String x264Params;
        final String profile;
        final String level;
        
        VideoEncodingSettings(int bitrate, int crf, String preset, String x264Params, 
                             String profile, String level) {
            this.bitrate = bitrate;
            this.crf = crf;
            this.preset = preset;
            this.x264Params = x264Params;
            this.profile = profile;
            this.level = level;
        }
    }

    private static VideoEncodingSettings getEncodingSettings(VideoQuality quality, int width, int height) {
        int bitrate = calculateBitrate(width, height);
        
        switch (quality) {
            case HIGHEST:
                return new VideoEncodingSettings(
                    bitrate * 2, // 加倍比特率
                    12,  // 接近无损的CRF值
                    "veryslow", // 最慢但最好的压缩
                    "ref=6:me=umh:subme=7:chroma-qp-offset=-2:bframes=8:b-adapt=2:aq-mode=3:aq-strength=0.8:psy-rd=1.0:deblock=1,1",
                    "high",
                    "5.1"
                );
                
            case BALANCED:
                return new VideoEncodingSettings(
                    bitrate,
                    18,  // 视觉无损的好选择
                    "slow", // 平衡速度和压缩
                    "ref=4:me=hex:subme=6:chroma-qp-offset=0:bframes=3:b-adapt=1:aq-mode=2:aq-strength=1.0:deblock=1,1",
                    "high",
                    "4.2"
                );
                
            case LOWEST:
                return new VideoEncodingSettings(
                    bitrate / 2, // 减半比特率
                    23,  // 可见压缩但文件小
                    "medium", // 更快的编码
                    "ref=2:me=hex:subme=4:chroma-qp-offset=1:bframes=2:b-adapt=1:aq-mode=1:aq-strength=1.2:deblock=1,1",
                    "main",
                    "4.0"
                );
                
            default:
                return getEncodingSettings(VideoQuality.BALANCED, width, height);
        }
    }
}