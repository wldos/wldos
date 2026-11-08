/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useRef } from 'react';
import ProTable from '@ant-design/pro-table';
import classNames from 'classnames';
import isMobile from '@/hooks/isMobile';
import { mergeAdminListSearch } from '@/pages/commercial/_shared/pageContainerStyle';
import useAdminListBodyScroll from './useAdminListBodyScroll';
import useAdminListStickyChrome from './useAdminListStickyChrome';
import useAdminTableWheelPassthrough from './useAdminTableWheelPassthrough';

/**
 * 增强版 ProTable - 自动适配移动端
 *
 * listLayout:
 * - compact：查询区与表格左对齐、无额外顶内边距（配合 adminListPageContainerProps）
 * - classic：原 ProTable 查询区样式（配合 getAdminPageContainerProps({ showTitle: true })）
 * - 不传：不改动 search / 样式，完全透传
 *
 * 桌面端 compact/classic 默认整页竖滚 + 查询/工具栏 sticky + 表头 ProTable sticky（单一滚动条体验）。
 * 仅当 viewportBodyScroll={true} 且未显式 scroll.y 时启用表体内滚（特殊页 opt-in）。
 */
export default function ProTableX(props) {
  const wrapRef = useRef(null);
  const mobile = isMobile();
  const {
    options = {},
    tableLayout,
    scroll,
    sticky,
    columns,
    pagination,
    className,
    search,
    listLayout = 'compact',
    viewportBodyScroll,
    ...rest
  } = props;

  const layoutDisabled = listLayout === false || listLayout === 'none';
  const layoutMode = layoutDisabled ? null : (listLayout || 'compact');

  const processedColumns = columns ? columns.map((col) => {
    if (typeof col.render === 'function') {
      return col;
    }
    return {
      ...col,
      onHeaderCell: () => ({ style: mobile ? {} : { whiteSpace: 'nowrap' } }),
    };
  }) : columns;

  const mergedPagination =
    pagination === false
      ? false
      : pagination;

  const resolvedSearch = layoutMode
    ? mergeAdminListSearch(search, { compact: layoutMode === 'compact' })
    : search;

  const mergedClassName = classNames(
    className,
    layoutMode === 'compact' && 'adminListProTable',
    layoutMode === 'classic' && 'adminListProTableClassic',
  );

  const mergedOptions =
    options === false
      ? false
      : {
          ...(options || {}),
          fullScreen: !mobile && options?.fullScreen !== false,
        };

  const adminChrome = !mobile && !!layoutMode;

  const offsetHeader = useAdminListStickyChrome(wrapRef, {
    enabled: adminChrome && sticky !== false,
    mobile,
  });

  const useInnerBodyScroll =
    viewportBodyScroll === true
    && !mobile
    && layoutMode
    && scroll?.y === undefined;

  const bodyScrollY = useAdminListBodyScroll(wrapRef, { enabled: useInnerBodyScroll });

  useAdminTableWheelPassthrough(wrapRef, {
    enabled: adminChrome && !useInnerBodyScroll,
  });

  const mergedScroll = mobile
    ? undefined
    : {
        ...(scroll || {}),
        ...(bodyScrollY != null ? { y: bodyScrollY } : {}),
      };

  let mergedSticky = false;
  if (!mobile) {
    if (sticky !== undefined) {
      mergedSticky = sticky;
    } else if (adminChrome) {
      mergedSticky = { offsetHeader };
    }
  }

  return (
    <div ref={wrapRef} className="wldos-pro-table-x-wrap">
      <ProTable
        {...rest}
        className={mergedClassName || undefined}
        search={resolvedSearch}
        columns={processedColumns}
        pagination={mergedPagination}
        options={mergedOptions}
        tableLayout={mobile ? undefined : (tableLayout ?? 'fixed')}
        scroll={mergedScroll}
        sticky={mergedSticky}
      />
    </div>
  );
}
