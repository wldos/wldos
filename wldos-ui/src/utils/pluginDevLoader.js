/**
 * 插件开发模式加载器
 * 开发模式下直接从源码加载插件，用于快速预览插件界面
 * 注意：同部署机制的验证需要通过真正的插件包安装流程来测试
 */

/**
 * 加载开发模式的插件组件（从源码直接加载）
 * @param {string} pluginCode - 插件代码
 * @param {string} componentName - 组件名称
 * @returns {Promise<React.Component>}
 */
export async function loadDevPluginComponent(pluginCode, componentName) {
  if (process.env.NODE_ENV !== 'development') {
    throw new Error('开发模式加载器仅在开发环境下可用');
  }

  try {
    // 使用 webpack alias 路径（在 config.js 中动态配置）
    const pluginAlias = `@plugin-${pluginCode}`;
    const pluginPath = `${pluginAlias}/index.js`;
    
    // 动态导入插件模块
    const pluginModule = await import(/* webpackChunkName: "plugin-dev-[request]" */ pluginPath);
    
    // 从模块中提取组件
    const component = pluginModule[componentName] 
      || pluginModule.default?.[componentName] 
      || pluginModule.default;
    
    if (!component) {
      throw new Error(`组件 ${componentName} 在插件 ${pluginCode} 中不存在`);
    }
    
    return component;
  } catch (error) {
    console.error(`[pluginDevLoader] 从源码加载开发插件失败: ${pluginCode}`, error);
    throw error;
  }
}

/**
 * 检查插件是否启用开发模式
 * @param {string} pluginCode - 插件代码
 * @returns {boolean}
 */
export function isPluginInDevMode(pluginCode) {
  if (process.env.NODE_ENV !== 'development') {
    return false;
  }
  
  // 环境变量格式：PLUGIN_CODE 或 *
  // 例如：REACT_APP_DEV_PLUGINS=airdrop,umd-demo
  // 或：REACT_APP_DEV_PLUGINS=*（启用所有插件）
  // 注意：umi 会自动注入 REACT_APP_ 开头的环境变量到 process.env
  const devPluginsEnv = process.env.REACT_APP_DEV_PLUGINS;
  
  // 开发环境调试日志（仅在首次调用时打印）
  if (!isPluginInDevMode._logged) {
    console.log('[pluginDevLoader] 环境变量 REACT_APP_DEV_PLUGINS:', devPluginsEnv || '(未设置)');
    isPluginInDevMode._logged = true;
  }
  
  if (!devPluginsEnv) {
    return false;
  }
  
  const devPluginsConfig = devPluginsEnv.split(',').map(s => s.trim()).filter(Boolean);
  
  // 检查是否匹配
  return devPluginsConfig.some(config => config === '*' || config === pluginCode);
}

