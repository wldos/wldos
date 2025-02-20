/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.ffmpeg;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * png 转 gif动图
 */
public class GifCreator {

    public static void main(String[] args) throws Exception {
         //File video = new File("D:/simple/src.mp4");
        String pngDir = "D:/simple/png";

        // 1.视频切片
        //VideoScreenshot.extractFramesFromVideoInterval(video, pngDir, 5);

        // 2.切片组装 生成gif
        String outputPath = "D:/simple/gif/gf_001.gif";
        int frameDelay = 350;
        createGifFromFrames(pngDir, outputPath, frameDelay);
    }

    /**
     * gif动图生成
     *
     * @param inputDir   静态图片的目录
     * @param outputPath 输出gif的目录
     * @param frameDelay gif动图动画间隔，每帧延迟间隔，毫秒
     */
    private static void createGifFromFrames(String inputDir, String outputPath, int frameDelay) throws Exception {
        File directory = new File(inputDir);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No PNG files found in directory: " + inputDir);
        }

        // 按文件名排序
        Arrays.sort(files, (f1, f2) -> {
            String num1 = f1.getName().replaceAll("\\D+", "");
            String num2 = f2.getName().replaceAll("\\D+", "");
            return Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
        });

        try (ImageOutputStream output = new FileImageOutputStream(new File(outputPath))) {
            // 读取第一帧并调整大小到240p
            BufferedImage firstImage = resizeTo240p(ImageIO.read(files[0]));

            // 创建GIF写入器
            try (GifSequenceWriter writer = new GifSequenceWriter(output,
                    firstImage.getType(),
                    frameDelay,
                    true)) {

                System.out.println("Starting GIF creation with " + files.length + " frames");
                int processedFrames = 0;

                // 写入所有帧
                for (File file : files) {
                    try {
                        BufferedImage originalImage = ImageIO.read(file);
                        // 调整图片大小到240p
                        BufferedImage resizedImage = resizeTo240p(originalImage);
                        writer.writeToSequence(resizedImage);
                        processedFrames++;

                        // 打印进度
                        if (processedFrames % 10 == 0 || processedFrames == files.length) {
                            System.out.printf("Progress: %d/%d frames processed (%.1f%%)\n",
                                    processedFrames, files.length,
                                    (processedFrames * 100.0) / files.length);
                        }

                        // 释放资源
                        originalImage.flush();
                        resizedImage.flush();
                    } catch (IOException e) {
                        System.err.println("Error processing frame: " + file.getName());
                        e.printStackTrace();
                    }
                }

                System.out.println("GIF creation completed");
            }
        }
    }

    private static BufferedImage resizeTo240p(BufferedImage originalImage) {
        // 计算240p的尺寸，保持宽高比
        int targetHeight = 240;
        int targetWidth = (int) Math.round(
                (double) originalImage.getWidth() / originalImage.getHeight() * targetHeight
        );

        // 创建新的缩放后的图像，使用ARGB格式
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight,
                BufferedImage.TYPE_INT_ARGB);

        // 使用高质量缩放
        Graphics2D g2d = resizedImage.createGraphics();
        try {
            // 设置白色背景（如果需要的话）
            g2d.setBackground(Color.WHITE);
            g2d.clearRect(0, 0, targetWidth, targetHeight);

            // 设置高质量缩放参数
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制缩放后的图像
            g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        } finally {
            g2d.dispose();
        }

        return resizedImage;
    }
}

// 优化后的GifSequenceWriter类
class GifSequenceWriter implements AutoCloseable {
    protected ImageWriter writer;
    protected ImageWriteParam params;
    protected IIOMetadata metadata;

    public GifSequenceWriter(ImageOutputStream out, int imageType, int frameDelay, boolean loop)
            throws IOException {
        // 获取GIF ImageWriter
        writer = ImageIO.getImageWritersBySuffix("gif").next();
        params = writer.getDefaultWriteParam();

        // GIF不支持显式压缩设置，移除压缩相关代码
        // if (params.canWriteCompressed()) {
        //     params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        //     params.setCompressionQuality(0.7f);
        // }

        // 设置图像类型
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params);

        configureGifMetadata(frameDelay, loop);

        writer.setOutput(out);
        writer.prepareWriteSequence(null);
    }

    private void configureGifMetadata(int frameDelay, boolean loop) throws IIOInvalidTreeException {
        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(frameDelay / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by: Java");

        IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loopContinuously = loop ? 0 : 1;
        byte[] loopData = new byte[]{0x1, (byte) (loopContinuously & 0xFF),
                (byte) ((loopContinuously >> 8) & 0xFF)};
        child.setUserObject(loopData);
        appExtensionsNode.appendChild(child);

        metadata.setFromTree(metaFormatName, root);
    }

    public void writeToSequence(BufferedImage img) throws IOException {
        writer.writeToSequence(new IIOImage(img, null, metadata), params);
    }

    // 实现AutoCloseable接口的close方法
    @Override
    public void close() throws IOException {
        writer.endWriteSequence();
        writer.dispose();
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }
}