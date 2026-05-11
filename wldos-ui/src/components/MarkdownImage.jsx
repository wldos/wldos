/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';

/** 仅透传可出现在 img 元素上的合法属性，避免 react-markdown / rehype 传入 node、mode 等污染 DOM */
function sanitizeImgDomProps(raw) {
  const allow = [
    'className',
    'id',
    'loading',
    'decoding',
    'sizes',
    'srcSet',
    'crossOrigin',
    'referrerPolicy',
    'draggable',
    'fetchPriority',
  ];
  const out = {};
  for (const k of allow) {
    const v = raw[k];
    if (v != null && typeof v !== 'object') {
      out[k] = v;
    }
  }
  return out;
}

/**
 * 共享的 Markdown 图片组件
 * 支持解析 HTML 注释中的尺寸信息
 * 基于文档模块的正常实现
 */
const MarkdownImage = ({ src, alt, title, content, ...props }) => {
  // 检查是否有HTML注释中的尺寸信息
  const markdownContent = content || '';

  // 更宽松的正则表达式，支持多种格式
  const patterns = [
    // 格式1: ![alt](url "title") <!-- 尺寸: 300x408 -->
    new RegExp(`!\\[([^\\]]*)\\]\\(${src.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\)(?: "([^"]*)")?(?:\\s*<!--\\s*尺寸:\\s*(\\d+)x(\\d+)\\s*-->)?`),
    // 格式2: ![alt](url) <!-- 尺寸: 300x408 -->
    new RegExp(`!\\[([^\\]]*)\\]\\(${src.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\)(?:\\s*<!--\\s*尺寸:\\s*(\\d+)x(\\d+)\\s*-->)?`),
    // 格式3: 查找包含该URL的所有行，然后检查是否有尺寸注释
    new RegExp(`.*${src.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}.*<!--\\s*尺寸:\\s*(\\d+)x(\\d+)\\s*-->.*`, 'g')
  ];

  let match = null;
  let extractedTitle = title;
  let width = null;
  let height = null;

  // 尝试匹配各种格式
  for (const pattern of patterns) {
    match = markdownContent.match(pattern);
    if (match) {
      break;
    }
  }

  // 如果没匹配到，尝试更简单的方法：查找包含该URL的行
  if (!match) {
    const lines = markdownContent.split('\n');
    for (const line of lines) {
      if (line.includes(src)) {
        const sizeMatch = line.match(/<!--\s*尺寸:\s*(\d+)x(\d+)\s*-->/);
        if (sizeMatch) {
          width = sizeMatch[1];
          height = sizeMatch[2];
          break;
        }
      }
    }
  } else {
    if (match.length >= 4) {
      // Array(5) 情况：完整的markdown格式匹配
      extractedTitle = match[2] || title;
      width = match[3] || match[1];
      height = match[4] || match[2];
    } else if (match.length === 3) {
      // Array(3) 情况：match[0]是完整匹配，match[1]和match[2]是宽高
      width = match[1];
      height = match[2];
    } else if (match.length === 1) {
      // Array(1) 情况：只匹配到整个字符串，需要重新提取尺寸
      const sizeMatch = match[0].match(/<!--\s*尺寸:\s*(\d+)x(\d+)\s*-->/);
      if (sizeMatch) {
        width = sizeMatch[1];
        height = sizeMatch[2];
      }
    }
  }

  const shadow = '0 2px 8px rgba(0,0,0,0.1)';
  const imgStyle = {
    maxWidth: '100%',
    height: 'auto',
    width: 'auto',
    display: 'block',
    margin: '0 auto',
    boxShadow: shadow,
  };

  if (width && height) {
    const w = Number(width);
    const h = Number(height);
    if (w > 0 && h > 0) {
      imgStyle.maxWidth = `min(100%, ${w}px)`;
      imgStyle.aspectRatio = `${w} / ${h}`;
    }
  }

  const domExtra = sanitizeImgDomProps(props);

  return (
    <div style={{ textAlign: 'center', margin: '16px 0' }}>
      <img
        src={src}
        alt={alt || ''}
        title={extractedTitle}
        style={imgStyle}
        onError={(e) => {
          e.target.style.display = 'none';
        }}
        {...domExtra}
      />
    </div>
  );
};

export default MarkdownImage;
