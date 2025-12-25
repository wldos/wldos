/**
 * 插件同部署加载器
 * 支持从本地加载插件UI（ESM格式）
 */

/**
 * 注入CSS样式
 * @param {string} pluginCode 插件编码
 * @param {string} version 插件版本
 * @param {Array<string>} cssFiles CSS文件列表
 */
export function injectPluginStyles(pluginCode, version, cssFiles = []) {
  if (!cssFiles || cssFiles.length === 0) {
    return;
  }

  cssFiles.forEach(cssFile => {
    const linkId = `plugin-${pluginCode}-${version}-${cssFile}`;
    
    // 检查是否已经注入
    if (document.getElementById(linkId)) {
      return;
    }

    // 构建CSS文件URL：/plugin-assets/{code}/{version}/{cssFile}
    const cssUrl = `/plugin-assets/${pluginCode}/${version}/${cssFile}`;
    
    const link = document.createElement('link');
    link.id = linkId;
    link.rel = 'stylesheet';
    link.type = 'text/css';
    link.href = cssUrl;

    document.head.appendChild(link);
  });
}


