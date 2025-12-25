/**
 * 插件配置工具
 * 动态扫描插件目录，自动配置插件别名和路径
 */

const path = require('path');
const fs = require('fs');

const PLUGINS_DIR = path.resolve(__dirname, '../../wldos-plugins');
const UI_SUBDIR = 'src/main/ui';

/**
 * 扫描插件目录，获取所有插件信息
 * @returns {Array<{code: string, path: string, uiPath: string}>}
 */
function scanPlugins() {
  const plugins = [];

  if (!fs.existsSync(PLUGINS_DIR)) {
    console.warn(`[pluginConfig] 插件目录不存在: ${PLUGINS_DIR}`);
    return plugins;
  }

  const entries = fs.readdirSync(PLUGINS_DIR, { withFileTypes: true });

  for (const entry of entries) {
    if (!entry.isDirectory()) continue;

    const pluginCode = entry.name;
    const pluginPath = path.join(PLUGINS_DIR, pluginCode);
    const uiPath = path.join(pluginPath, UI_SUBDIR);

    // 检查是否存在 UI 目录
    if (fs.existsSync(uiPath) && fs.existsSync(path.join(uiPath, 'package.json'))) {
      plugins.push({
        code: pluginCode,
        path: pluginPath,
        uiPath: uiPath,
      });
    }
  }

  return plugins;
}

/**
 * 获取插件 webpack 别名配置
 * @returns {Object<string, string>}
 */
function getPluginAliases() {
  const plugins = scanPlugins();
  const aliases = {};

  for (const plugin of plugins) {
    aliases[`@plugin-${plugin.code}`] = plugin.uiPath;
  }

  return aliases;
}

/**
 * 获取插件列表（用于开发模式配置）
 * @returns {string[]}
 */
function getPluginCodes() {
  return scanPlugins().map(p => p.code);
}

module.exports = {
  scanPlugins,
  getPluginAliases,
  getPluginCodes,
  PLUGINS_DIR,
};

