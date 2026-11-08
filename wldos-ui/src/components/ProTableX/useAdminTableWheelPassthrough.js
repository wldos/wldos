/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect } from 'react';

function findVerticalScrollParent(start) {
  let node = start instanceof Element ? start : null;
  while (node && node !== document.body) {
    const style = getComputedStyle(node);
    const oy = style.overflowY;
    if ((oy === 'auto' || oy === 'scroll' || oy === 'overlay')
      && node.scrollHeight > node.clientHeight + 1) {
      return node;
    }
    node = node.parentElement;
  }
  return document.documentElement;
}

/**
 * 整页滚动模式下，antd 表体横向滚动容器会吞掉滚轮；表体无竖向溢出时将滚轮交给外层滚动容器。
 */
export default function useAdminTableWheelPassthrough(wrapRef, { enabled }) {
  useEffect(() => {
    if (!enabled) return undefined;
    const wrap = wrapRef.current;
    if (!wrap) return undefined;

    const onWheel = (e) => {
      if (!wrap.contains(e.target)) return;
      const inTable = e.target.closest('.ant-table-container, .ant-table-body, .ant-table-content, .ant-table');
      if (!inTable || !wrap.contains(inTable)) return;

      const body = wrap.querySelector('.ant-table-body');
      if (body && body.scrollHeight > body.clientHeight + 1) {
        return;
      }

      const scroller = findVerticalScrollParent(wrap);
      if (scroller.scrollHeight <= scroller.clientHeight + 1) return;

      scroller.scrollBy({ top: e.deltaY, left: 0, behavior: 'auto' });
      e.preventDefault();
    };

    wrap.addEventListener('wheel', onWheel, { passive: false, capture: true });
    return () => wrap.removeEventListener('wheel', onWheel, { capture: true });
  }, [enabled, wrapRef]);
}
