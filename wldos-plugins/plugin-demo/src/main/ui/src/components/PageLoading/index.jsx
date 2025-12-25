import React from 'react';

/**
 * 页面加载组件
 * 用于动态导入时的 loading 状态
 */
const PageLoading = () => {
  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '200px',
      }}
    >
      <div>加载中...</div>
    </div>
  );
};

export default PageLoading;

