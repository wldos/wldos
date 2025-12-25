// ESM 包装器：将全局 React 暴露为 ESM 模块
// 供插件使用 import React from "React" 时使用

// 从全局对象获取 React
// 注意：这个文件会在主应用加载后执行，确保 React 已可用
const React = window.React || window.__REACT__;

// 如果全局对象不存在，抛出错误
if (!React) {
  throw new Error('React 未找到，请确保主应用已加载');
}

// 导出默认值（兼容 import React from "React"）
export default React;
// 也导出命名导出（兼容 import { React } from "React"）
export { React };
