/**
 * 插件同部署加载器
 * 支持从本地加载插件UI（ESM格式）
 */

/**
 * 解析 ESM 入口 URL（支持 manifest 中 assetBase / esmEntry）
 * @param {string} code 插件编码
 * @param {object} pluginInfo manifest 中该插件条目
 * @returns {string}
 */
export function resolvePluginEsmUrl(code, pluginInfo) {
  if (!pluginInfo || !pluginInfo.version) {
    return `/plugin-assets/${code}/1.0.0/esm/index.js`;
  }
  if (pluginInfo.esmEntry) {
    const e = pluginInfo.esmEntry;
    if (e.startsWith('http://') || e.startsWith('https://') || e.startsWith('/')) {
      return e;
    }
    return `/plugin-assets/${code}/${pluginInfo.version}/${e}`;
  }
  const base = pluginInfo.assetBase
    ? (pluginInfo.assetBase.endsWith('/') ? pluginInfo.assetBase : `${pluginInfo.assetBase}/`)
    : `/plugin-assets/${code}/${pluginInfo.version}/`;
  return `${base}esm/index.js`;
}

/**
 * 注入CSS样式
 * @param {string} pluginCode 插件编码
 * @param {string} version 插件版本
 * @param {Array<string>} cssFiles CSS文件列表
 * @param {object} [pluginInfo] manifest 中插件条目（可选，用于 assetBase）
 */
export function injectPluginStyles(pluginCode, version, cssFiles = [], pluginInfo = null) {
  if (!cssFiles || cssFiles.length === 0) {
    return;
  }

  const base = pluginInfo?.assetBase
    ? (pluginInfo.assetBase.endsWith('/') ? pluginInfo.assetBase : `${pluginInfo.assetBase}/`)
    : `/plugin-assets/${pluginCode}/${version}/`;

  cssFiles.forEach((cssFile) => {
    const linkId = `plugin-${pluginCode}-${version}-${cssFile}`;

    if (document.getElementById(linkId)) {
      return;
    }

    const cssUrl =
      cssFile.startsWith('http://') ||
      cssFile.startsWith('https://') ||
      (cssFile.startsWith('/') && cssFile.includes('plugin-assets'))
        ? cssFile
        : `${base}${cssFile}`;

    const link = document.createElement('link');
    link.id = linkId;
    link.rel = 'stylesheet';
    link.type = 'text/css';
    link.href = cssUrl;

    document.head.appendChild(link);
  });
}
