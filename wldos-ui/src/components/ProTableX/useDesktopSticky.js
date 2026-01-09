/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect } from 'react';
import isMobile from '@/hooks/isMobile';

/**
 * 桌面端粘性工具：在桌面端为查询区、工具栏添加粘性，
 * 并在内容不足一屏时为表格容器增加 marginTop，避免首行被表头遮挡。
 * 小屏设备下不做任何处理，保持 antd 默认自然展示。
 */
export default function useDesktopSticky(containerRefOrEl, topBase = 88) {
  const mobile = isMobile();

  useEffect(() => {
    // 兼容传入 ref 或 DOM 节点；actionRef 并非 DOM，需要兜底查询
    let wrap = containerRefOrEl && containerRefOrEl.current
      ? containerRefOrEl.current
      : containerRefOrEl;

    if (!(wrap instanceof Element)) {
      // 兜底：尝试页面内查找当前第一个 ProTable 容器
      wrap = document.querySelector('.ant-pro-table-list, .ant-pro-table');
    }

    if (!(wrap instanceof Element)) return;

    const apply = () => {
      if (mobile) return; // 小屏不处理

      const isFullscreen = !!document.fullscreenElement;
      const viewportH = window.innerHeight || document.documentElement.clientHeight;

      const searchEl = wrap.querySelector('.ant-pro-table-search');
      const toolbarEl = wrap.querySelector('.ant-pro-table-list-toolbar, .ant-pro-table-toolbar');
      const headerEl = wrap.querySelector('.ant-table-header');
      const bodyEl = wrap.querySelector('.ant-table-body');
      const containerEl = wrap.querySelector('.ant-table-container');

      // 计算各元素高度
      const searchH = searchEl ? searchEl.offsetHeight : 0;
      const toolbarH = toolbarEl ? toolbarEl.offsetHeight : 0;
      const headerH = headerEl?.offsetHeight || 32;

      // 全屏状态下的特殊处理 - 全屏时不锁定任何元素，避免低分辨率屏幕问题
      if (isFullscreen) {
        // 全屏时，清理所有粘性样式，让所有元素自然滚动
        if (searchEl) {
          Object.assign(searchEl.style, {
            position: 'static',
            top: 'auto',
            zIndex: 'auto',
            background: 'transparent',
            borderBottom: 'none'
          });
        }

        if (toolbarEl) {
          Object.assign(toolbarEl.style, {
            position: 'static',
            top: 'auto',
            zIndex: 'auto',
            background: 'transparent',
            borderBottom: 'none'
          });
        }

        if (headerEl) {
          Object.assign(headerEl.style, {
            position: 'static',
            top: 'auto',
            zIndex: 'auto',
            background: 'transparent',
            borderBottom: 'none',
            boxShadow: 'none'
          });
        }

        // 清理容器的所有间距
        if (containerEl) {
          containerEl.style.marginTop = '0px';
        }
        if (bodyEl) {
          bodyEl.style.paddingTop = '0px';
          bodyEl.style.minHeight = '';
        }

        // 全屏时：保持所有元素自然状态，不干预滚动条
      } else {
        // 非全屏状态，使用原有逻辑
        if (searchEl) {
          Object.assign(searchEl.style, {
            position: 'sticky',
            top: `${topBase}px`,
            zIndex: 9,
            background: '#fff'
          });
        }

        if (toolbarEl) {
          Object.assign(toolbarEl.style, {
            position: 'sticky',
            top: `${topBase + searchH}px`,
            zIndex: 9,
            background: '#fff'
          });
        }

        const offset = topBase + searchH + toolbarH;
        // 移除 push-down 逻辑，避免首行空白与抖动
        // 不再动态修改 container 的 marginTop 与 body 的 paddingTop

        if (headerEl) {
          Object.assign(headerEl.style, {
            position: 'sticky',
            top: `${offset}px`,
            zIndex: 8,
            background: '#fff'
          });
        }
        // 退出全屏：无需恢复滚动策略（未修改过）
      }
    };

    // 防抖处理，减少频繁调用
    let timeoutId = null;
    const debouncedApply = () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      timeoutId = setTimeout(apply, 50); // 防抖延迟50ms
    };

    // 首次应用
    apply();

    // 使用防抖的ResizeObserver和事件监听
    const ro = typeof ResizeObserver !== 'undefined' ? new ResizeObserver(debouncedApply) : null;
    if (ro && wrap) ro.observe(wrap);
    window.addEventListener('resize', debouncedApply);
    document.addEventListener('fullscreenchange', debouncedApply);
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      if (ro) ro.disconnect();
      window.removeEventListener('resize', debouncedApply);
      document.removeEventListener('fullscreenchange', debouncedApply);
    };
  }, [mobile, containerRefOrEl, topBase]);
}


