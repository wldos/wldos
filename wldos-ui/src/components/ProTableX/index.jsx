/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import ProTable from '@ant-design/pro-table';
import isMobile from '@/hooks/isMobile';

/**
 * 增强版 ProTable - 自动适配移动端
 * 移动端：禁用全屏按钮、固定布局、横向滚动等特效
 * 桌面端：保留所有优化功能
 */
export default function ProTableX(props) {
  const mobile = isMobile();
  const {
    options = {},
    tableLayout,
    scroll,
    sticky,
    columns,
    ...rest
  } = props;

  // 处理列配置：若列已自带 render，则完全尊重原实现；否则仅追加样式
  const processedColumns = columns ? columns.map((col) => {
    if (typeof col.render === 'function') {
      return col; // 保持原样，完全尊重自定义渲染
    }
    return {
      ...col,
      onHeaderCell: () => ({ style: mobile ? {} : { whiteSpace: 'nowrap' } }),
    };
  }) : columns;

  return (
    <ProTable
      {...rest}
      columns={processedColumns}
      options={{
        ...options,
        fullScreen: !mobile && options.fullScreen !== false
      }}
      tableLayout={mobile ? undefined : (tableLayout ?? 'fixed')}
      scroll={mobile ? undefined : (scroll ?? { x: 1500 })}
      sticky={mobile ? false : (sticky ?? { offsetHeader: 96 })}
    />
  );
}
