// ESM 包装器：将全局 ReactDOM 暴露为 ESM 模块
// 供插件使用 import ReactDOM from "ReactDOM" 时使用

// 从全局对象获取 ReactDOM
// 注意：这个文件会在主应用加载后执行，确保 ReactDOM 已可用
const ReactDOM = window.ReactDOM || window.__REACT_DOM__;

// 如果全局对象不存在，抛出错误
if (!ReactDOM) {
  throw new Error('ReactDOM 未找到，请确保主应用已加载');
}

// 导出默认值（兼容 import ReactDOM from "ReactDOM"）
export default ReactDOM;
// 也导出命名导出（兼容 import { ReactDOM } from "ReactDOM"）
export { ReactDOM };

