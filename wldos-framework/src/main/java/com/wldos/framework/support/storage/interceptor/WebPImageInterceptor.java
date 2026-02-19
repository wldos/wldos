/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.storage.interceptor;

import com.wldos.common.utils.img.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * WebP 图片格式自动转换拦截器
 * 
 * 功能：
 * 1. 拦截 /store/** 图片请求
 * 2. 检查浏览器是否支持 WebP（Accept 头）
 * 3. 如果支持且原图不是 WebP，动态转换为 WebP
 * 4. 转换结果缓存，避免重复转换
 * 
 * @author 元悉宇宙
 * @date 2026-01-27
 */
@Slf4j
@Component
public class WebPImageInterceptor implements HandlerInterceptor {
    
    @Value("${wldos_file_store_path:}")
    private String storePath;
    
    // 支持的图片格式
    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        
        // 只处理 /store/** 路径的图片请求
        if (!requestPath.startsWith("/store/")) {
            return true; // 继续处理
        }
        
        // 检查是否为图片文件
        if (!isImageFile(requestPath)) {
            return true; // 继续处理
        }
        
        // 检查浏览器是否支持 WebP
        String acceptHeader = request.getHeader("Accept");
        boolean supportsWebP = acceptHeader != null && acceptHeader.contains("image/webp");
        
        if (!supportsWebP) {
            return true; // 浏览器不支持 WebP，继续使用原图
        }
        
        // 如果原图已经是 WebP，直接返回
        if (requestPath.toLowerCase().endsWith(".webp")) {
            return true;
        }
        
        // 尝试返回 WebP 版本
        try {
            String originalPath = getOriginalFilePath(requestPath);
            String webpPath = getWebPPath(originalPath);
            
            File originalFile = new File(originalPath);
            File webpFile = new File(webpPath);
            
            // 如果原图不存在，继续处理（可能返回 404）
            if (!originalFile.exists()) {
                return true;
            }
            
            // 如果 WebP 已存在，直接返回
            if (webpFile.exists()) {
                serveWebPFile(webpFile, response);
                return false; // 已处理，不再继续
            }
            
            // 动态转换为 WebP
            ImageUtils.convertToWebP(originalPath, webpPath, null, null);
            
            // 返回转换后的 WebP
            if (webpFile.exists()) {
                serveWebPFile(webpFile, response);
                return false; // 已处理
            }
        } catch (Exception e) {
            log.warn("WebP 转换失败，降级到原图: {}", requestPath, e);
            // 转换失败，继续使用原图
        }
        
        return true; // 继续处理原图
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String path) {
        String lowerPath = path.toLowerCase();
        for (String ext : IMAGE_EXTENSIONS) {
            if (lowerPath.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取原始文件路径
     */
    private String getOriginalFilePath(String requestPath) {
        // 移除 /store/ 前缀，获取相对路径
        String relativePath = requestPath.substring(7); // "/store/".length()
        return storePath + File.separator + "store" + File.separator + relativePath.replace("/", File.separator);
    }
    
    /**
     * 获取 WebP 文件路径
     */
    private String getWebPPath(String originalPath) {
        // 替换扩展名为 .webp
        int lastDot = originalPath.lastIndexOf('.');
        if (lastDot > 0) {
            return originalPath.substring(0, lastDot) + ".webp";
        }
        return originalPath + ".webp";
    }
    
    /**
     * 返回 WebP 文件
     */
    private void serveWebPFile(File webpFile, HttpServletResponse response) throws IOException {
        response.setContentType("image/webp");
        response.setContentLengthLong(webpFile.length());
        response.setHeader("Cache-Control", "public, max-age=31536000"); // 缓存 1 年
        
        try (FileInputStream fis = new FileInputStream(webpFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
