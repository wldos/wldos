/**
 * 获取组件路径
 *
 * 功能：统一处理本地组件和插件组件的路径
 * 唯一区别：本地组件是在主应用构建时打包的，插件是运行时补丁过来的
 */

/**
 * 从路由匹配信息中获取组件路径
 *
 * @param {Object} match - 路由匹配对象
 * @param {Object} manifest - manifest 对象（可选）
 * @returns {string} 组件路径
 */
export function getComponentPath(match, manifest = null) {
  if (!match || !match.component) {
    return null;
  }

  // 统一规范化 component 路径（插件和本地组件使用相同的处理方式）
  // 后端返回的格式可能是 "sys/plugins" 或 "./tasks"，统一处理
  // 返回格式：sys/plugins 或 tasks（和主应用本地组件一样）
  const normalizedPath = match.component.replace(/^\.\//, '').replace(/^\//, '');
  
  // 返回规范化路径，供 AdminDynamicRouter 根据类型拼装
  // 本地组件：@/pages/sys/plugins/index
  // 插件组件：/plugin-assets/{pluginCode}/{version}/dynamic-{path}-index.js
  return normalizedPath;
}

