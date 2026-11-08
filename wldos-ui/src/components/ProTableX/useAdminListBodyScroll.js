/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect, useState } from 'react';

const MIN_BODY_Y = 160;
const VIEWPORT_BOTTOM_GAP = 12;

function readChromeTopPx() {
  if (typeof document === 'undefined') return 88;
  const raw = getComputedStyle(document.documentElement).getPropertyValue('--wldos-admin-chrome-top');
  const parsed = parseFloat(raw);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 88;
}

function measureBodyScrollY(wrap) {
  if (!wrap) return undefined;
  const tableWrapper = wrap.querySelector('.ant-table-wrapper');
  if (!tableWrapper) return undefined;

  const viewportH = window.innerHeight || document.documentElement.clientHeight;
  const tableTop = tableWrapper.getBoundingClientRect().top;
  const pagination = wrap.querySelector('.ant-pagination');
  const pagRect = pagination ? pagination.getBoundingClientRect() : null;
  const pagBlock = pagRect ? Math.max(pagRect.height, 32) + 8 : 56;
  const y = viewportH - tableTop - pagBlock - VIEWPORT_BOTTOM_GAP;
  return Math.max(MIN_BODY_Y, Math.floor(y));
}

function resetTableBodyScroll(wrap) {
  if (!wrap) return;
  wrap.querySelectorAll('.ant-table-body').forEach((el) => {
    el.scrollTop = 0;
  });
}

/**
 * 管理端列表：表体在视口剩余高度内滚动，查询区/工具栏/表头不参与整页滚动，避免多层 sticky 穿帮。
 */
export default function useAdminListBodyScroll(wrapRef, { enabled } = {}) {
  const [scrollY, setScrollY] = useState(undefined);

  useEffect(() => {
    if (!enabled) {
      setScrollY(undefined);
      return undefined;
    }

    const wrap = wrapRef.current;
    if (!wrap) return undefined;

    let rafId = 0;
    const apply = () => {
      cancelAnimationFrame(rafId);
      rafId = requestAnimationFrame(() => {
        const next = measureBodyScrollY(wrap);
        setScrollY((prev) => (prev === next ? prev : next));
      });
    };

    apply();
    resetTableBodyScroll(wrap);

    const ro = typeof ResizeObserver !== 'undefined' ? new ResizeObserver(apply) : null;
    if (ro) {
      ro.observe(wrap);
    }
    const mo = typeof MutationObserver !== 'undefined'
      ? new MutationObserver(apply)
      : null;
    if (mo) {
      mo.observe(wrap, { childList: true, subtree: true });
    }
    window.addEventListener('resize', apply);
    document.addEventListener('fullscreenchange', apply);

    return () => {
      cancelAnimationFrame(rafId);
      if (ro) ro.disconnect();
      if (mo) mo.disconnect();
      window.removeEventListener('resize', apply);
      document.removeEventListener('fullscreenchange', apply);
    };
  }, [enabled, wrapRef]);

  useEffect(() => {
    if (!enabled || !wrapRef.current) return;
    resetTableBodyScroll(wrapRef.current);
  }, [enabled, wrapRef]);

  return scrollY;
}

export { readChromeTopPx, measureBodyScrollY, resetTableBodyScroll };
