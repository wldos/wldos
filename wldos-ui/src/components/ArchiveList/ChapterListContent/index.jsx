/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import '@/styles/markdown.css';
import {Link} from 'umi';
import moment from 'moment';
import styles from './index.less';
import {Avatar} from "antd";

const ChapterListContent = ({
  data: { id, pubExcerpt, pubType, pubMimeType, createBy, createTime, member },
}) => {
  const detail = (() => {
    if (pubType === 'book') return { href: `/content-${id}.html`, text: '>>详情' };
    if (pubType === 'info') return { href: `/info-${id}.html`, text: '>>详情' };
    if (pubType === 'doc') return { href: `/doc/book/${id}.html`, text: '>>全文' };
    return { href: `/archives-${id}.html`, text: '>>全文' };
  })();

  const looksLikeHtml = (text) => {
    const t = (text || '').trim();
    if (!t) return false;
    // 粗略判断：是否包含明显的 HTML 标签
    return /<\/?[a-z][\s\S]*>/i.test(t);
  };

  const looksLikeMarkdown = (text) => {
    const t = (text || '').trim();
    if (!t) return false;
    // 如果包含明显 HTML 标签，优先按富文本处理
    if (looksLikeHtml(t)) return false;
    return (
      /(^#{1,6}\s)|(^\s*[-*+]\s+)|```|(\[[^\]]+\]\([^)]+\))|(\*\*[^*]+\*\*)/m.test(t)
    );
  };

  const isMarkdownLike =
    pubMimeType === 'text/markdown' ||
    (pubMimeType == null && !looksLikeHtml(pubExcerpt) && looksLikeMarkdown(pubExcerpt));

  const escapeHtml = (s) => {
    const str = String(s ?? '');
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  };

  // 截断后的简介 markdown 不能交给 react-markdown 解析，否则会出现半截语法残符。
  // 这里把 markdown“符号”清除掉，只保留可读文本和链接（[text](url)）。
  const markdownExcerptToHtml = (md) => {
    let t = String(md || '');
    if (!t.trim()) return '';

    // 删除代码围栏和行内代码标记（简介截断时最容易破坏解析）
    t = t.replace(/```[\s\S]*?```/g, '');
    t = t.replace(/```/g, '');
    t = t.replace(/`/g, '');

    // 按行清理 markdown 结构前缀
    t = t.replace(/(^|\n)\s*#{1,6}\s*/g, '$1'); // 标题
    t = t.replace(/(^|\n)\s*>\s?/g, '$1'); // 引用
    t = t.replace(/(^|\n)\s*([-*+]\s+)/g, '$1'); // 无序列表
    t = t.replace(/(^|\n)\s*\d+\.\s+/g, '$1'); // 有序列表

    // 清理常见强调/删除线符号（简介不追求严格 markdown 样式）
    t = t.replace(/(\*\*|__)([^]*?)\1/g, '$2');
    t = t.replace(/~~([^~]+?)~~/g, '$1');
    t = t.replace(/[*_~]/g, '');

    // 截断 markdown 常见问题：括号没闭合，导致 '[' 或 '(' 残留。
    // 如果检测到未配对，则直接移除多出来的符号类别，避免“半截符号”渲染。
    const openSquare = (t.match(/\[/g) || []).length;
    const closeSquare = (t.match(/]/g) || []).length;
    if (openSquare > closeSquare) t = t.replace(/\[/g, '');
    if (closeSquare > openSquare) t = t.replace(/]/g, '');

    const openParen = (t.match(/\(/g) || []).length;
    const closeParen = (t.match(/\)/g) || []).length;
    if (openParen > closeParen) t = t.replace(/\(/g, '');
    if (closeParen > openParen) t = t.replace(/\)/g, '');

    // 兜底：如果结尾停在明显的 markdown 未闭合字符上，截掉尾部
    t = t.replace(/[\[\(]\s*$/, '');
    t = t.replace(/[`*_#>~+\-]\s*$/, '');

    // 先 escape，避免 markdown 中夹带 HTML 造成注入
    const escaped = escapeHtml(t);

    // markdown 链接转 a：只处理完整形态 [text](url)
    // 这里 url 在 escaped 中已被转义成 &amp; 等，直接放入 href 可正常还原
    const withLinks = escaped.replace(/\[([^\]]+)\]\(([^)]+)\)/g, (_, text, url) => {
      const href = String(url || '').trim();
      if (!href) return text;
      return `<a href="${href}" target="_blank" rel="noopener noreferrer">${text}</a>`;
    });

    return withLinks.replace(/\n/g, '<br/>');
  };

  const excerptHtml = (() => {
    if (!pubExcerpt) return '';
    if (isMarkdownLike) {
      return markdownExcerptToHtml(pubExcerpt);
    }
    // 非 markdown：假设后端已提供 HTML
    return pubExcerpt;
  })();

  return (
    <div className={styles.listContent}>
      <div className={styles.description}>
        <span
          // eslint-disable-next-line react/no-danger
          dangerouslySetInnerHTML={{
            __html: `${excerptHtml} ... <a href="${detail.href}" target="_blank" rel="noopener noreferrer">${detail.text}</a>`,
          }}
        />
      </div>
      {member && (
        <div className={styles.extra}>
          <Avatar src={member.avatar} tips={member.nickname} />
          <Link to={`/archives-author/${createBy}.html`} target="_blank" rel="noopener">
            {member.nickname.length > 2
              ? `${member.nickname.substring(0, 2)}...`
              : `${member.nickname}`}
          </Link>{' '}
          发布于
          <em>{moment(createTime).format('YYYY-MM-DD HH:mm')}</em>
        </div>
      )}
    </div>
  );
};

export default ChapterListContent;
