/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect, useRef, useState } from 'react';
import { flushSync } from 'react-dom';
import { readChromeTopPx } from './useAdminListBodyScroll';

const DEFAULT_OFFSET = 88;

function resolveStickyTopBase(wrap) {
  if (document.fullscreenElement) {
    return 0;
  }
  let node = wrap;
  while (node && node !== document.body) {
    if (node.classList) {
      const names = Array.from(node.classList);
      if (names.some((c) => /fullscreen/i.test(c))) {
        return 0;
      }
    }
    node = node.parentElement;
  }
  return readChromeTopPx();
}

function clearInlineSticky(el) {
  if (!el) return;
  el.style.position = '';
  el.style.top = '';
  el.style.zIndex = '';
  el.style.background = '';
  el.style.borderBottom = '';
  el.style.boxShadow = '';
}

function syncStickyHolderTop(wrap, offsetPx) {
  wrap.querySelectorAll('.ant-table-sticky-holder').forEach((el) => {
    el.style.top = `${offsetPx}px`;
  });
}

/**
 * 管理端列表：整页滚动；查询区与工具栏 sticky；表头由 ProTable sticky + 动态 offsetHeader 处理。
 */
export default function useAdminListStickyChrome(wrapRef, { enabled, mobile }) {
  const [offsetHeader, setOffsetHeader] = useState(DEFAULT_OFFSET);
  const offsetRef = useRef(DEFAULT_OFFSET);

  useEffect(() => {
    if (!enabled || mobile) {
      setOffsetHeader(DEFAULT_OFFSET);
      return undefined;
    }

    const wrap = wrapRef.current;
    if (!wrap) return undefined;

    let rafId = 0;
    const apply = () => {
      const topBase = resolveStickyTopBase(wrap);
      const searchEl = wrap.querySelector('.ant-pro-table-search');
      const toolbarEl = wrap.querySelector('.ant-pro-table-list-toolbar, .ant-pro-table-toolbar');
      const headerEl = wrap.querySelector('.ant-table-header');

      clearInlineSticky(headerEl);

      const searchH = searchEl ? searchEl.offsetHeight : 0;
      const toolbarH = toolbarEl ? toolbarEl.offsetHeight : 0;
      const nextOffset = topBase + searchH + toolbarH;

      if (searchEl) {
        Object.assign(searchEl.style, {
          position: 'sticky',
          top: `${topBase}px`,
          zIndex: '9',
          background: '#fff',
        });
      }
      if (toolbarEl) {
        Object.assign(toolbarEl.style, {
          position: 'sticky',
          top: `${topBase + searchH}px`,
          zIndex: '9',
          background: '#fff',
        });
      }

      syncStickyHolderTop(wrap, nextOffset);
      const prevOffset = offsetRef.current;
      offsetRef.current = nextOffset;
      if (prevOffset !== nextOffset) {
        flushSync(() => {
          setOffsetHeader(nextOffset);
        });
      }
    };

    const scheduleApply = () => {
      cancelAnimationFrame(rafId);
      rafId = requestAnimationFrame(apply);
    };

    const burstApply = () => {
      apply();
      scheduleApply();
      rafId = requestAnimationFrame(() => {
        rafId = requestAnimationFrame(apply);
      });
    };

    apply();

    const ro = typeof ResizeObserver !== 'undefined' ? new ResizeObserver(scheduleApply) : null;
    if (ro) {
      ro.observe(wrap);
      const searchEl = wrap.querySelector('.ant-pro-table-search');
      const tableRoot = wrap.querySelector('.ant-pro-table');
      if (searchEl) ro.observe(searchEl);
      if (tableRoot) ro.observe(tableRoot);
    }

    const mo = typeof MutationObserver !== 'undefined'
      ? new MutationObserver(scheduleApply)
      : null;
    if (mo) {
      mo.observe(wrap, { childList: true, subtree: true, attributes: true, attributeFilter: ['class', 'style'] });
    }

    const onCollapseInteraction = (e) => {
      if (e.target.closest('.ant-pro-form-collapse-button')) {
        burstApply();
      }
    };
    wrap.addEventListener('click', onCollapseInteraction, true);

    const searchRoot = wrap.querySelector('.ant-pro-table-search');
    const onTransitionEnd = (e) => {
      if (e.propertyName === 'height' || e.propertyName === 'max-height' || e.propertyName === 'padding') {
        apply();
      }
    };
    if (searchRoot) {
      searchRoot.addEventListener('transitionend', onTransitionEnd);
    }

    const onResize = () => scheduleApply();
    window.addEventListener('resize', onResize);
    document.addEventListener('fullscreenchange', burstApply);

    return () => {
      cancelAnimationFrame(rafId);
      if (ro) ro.disconnect();
      if (mo) mo.disconnect();
      wrap.removeEventListener('click', onCollapseInteraction, true);
      if (searchRoot) searchRoot.removeEventListener('transitionend', onTransitionEnd);
      window.removeEventListener('resize', onResize);
      document.removeEventListener('fullscreenchange', burstApply);
      clearInlineSticky(wrap.querySelector('.ant-pro-table-search'));
      clearInlineSticky(wrap.querySelector('.ant-pro-table-list-toolbar, .ant-pro-table-toolbar'));
      clearInlineSticky(wrap.querySelector('.ant-table-header'));
      wrap.querySelectorAll('.ant-table-sticky-holder').forEach((el) => {
        el.style.top = '';
      });
    };
  }, [enabled, mobile, wrapRef]);

  return offsetHeader;
}
