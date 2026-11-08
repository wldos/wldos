import React from 'react';
import ReactMarkdown from 'react-markdown';
import { bodyContent } from '@/utils/utils';
import { resolveCmsBodyMimeType } from '@/pages/commercial/_shared/cmsContentMime';
import '@/styles/markdown.css';

/** 选择器预览：仅正文，Markdown 与 HTML 分轨渲染 */
export default function CmsPreviewBody({ content, mimeType }) {
  const text = content != null ? String(content).trim() : '';
  if (!text) {
    return null;
  }
  const effectiveMime = resolveCmsBodyMimeType(text, mimeType);
  if (effectiveMime === 'text/markdown') {
    return <ReactMarkdown className="markdown-body">{text}</ReactMarkdown>;
  }
  return bodyContent(text);
}
