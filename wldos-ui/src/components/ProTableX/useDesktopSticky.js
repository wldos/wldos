/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect } from 'react';

function resolveWrap(containerRefOrEl) {
  let wrap = containerRefOrEl && containerRefOrEl.current
    ? containerRefOrEl.current
    : containerRefOrEl;
  if (!(wrap instanceof Element)) {
    wrap = document.querySelector('.ant-pro-table-list, .ant-pro-table');
  }
  return wrap instanceof Element ? wrap : null;
}

function clearLegacyStickyStyles(wrap) {
  if (!wrap) return;
  const selectors = [
    '.ant-pro-table-search',
    '.ant-pro-table-list-toolbar',
    '.ant-pro-table-toolbar',
    '.ant-table-header',
  ];
  selectors.forEach((sel) => {
    wrap.querySelectorAll(sel).forEach((el) => {
      el.style.position = '';
      el.style.top = '';
      el.style.zIndex = '';
      el.style.background = '';
      el.style.borderBottom = '';
      el.style.boxShadow = '';
    });
  });
  wrap.querySelectorAll('.ant-table-container').forEach((el) => {
    el.style.marginTop = '';
  });
  wrap.querySelectorAll('.ant-table-body').forEach((el) => {
    el.style.paddingTop = '';
    el.style.minHeight = '';
  });
}

/**
 * 历史兼容：列表吸顶已改由 ProTableX + useAdminListBodyScroll（表体内滚）统一处理。
 * 本 hook 仅清除旧版 inline sticky，避免与表体内滚叠加。
 */
export default function useDesktopSticky(containerRefOrEl) {
  useEffect(() => {
    const wrap = resolveWrap(containerRefOrEl);
    clearLegacyStickyStyles(wrap);
    return () => clearLegacyStickyStyles(wrap);
  }, [containerRefOrEl]);
}
