/**
 * 插件manifest服务
 * 负责加载和管理插件UI清单文件
 */

/**
 * 加载插件manifest.json
 * @returns {Promise<Object>} manifest对象
 */
export async function loadPluginManifest() {
  try {
    const response = await fetch('/plugin-assets/manifest.json', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Cache-Control': 'no-cache',
      },
    });

    if (!response.ok) {
      if (response.status === 404) {
        // manifest文件不存在，返回空对象
        console.warn('[PluginManifest] manifest.json 不存在，可能没有安装插件');
        return { plugins: {} };
      }
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const manifest = await response.json();
    return manifest || { plugins: {} };
  } catch (error) {
    console.error('[PluginManifest] 加载manifest失败:', error);
    // 返回空对象，避免阻塞应用启动
    return { plugins: {} };
  }
}

/**
 * 将manifest中的路由转换为菜单格式
 * @param {Object} manifest manifest对象
 * @returns {Array} 菜单数组
 */
export function convertManifestToRoutes(manifest) {
  if (!manifest || !manifest.plugins) {
    return [];
  }

  const routes = [];
  const plugins = manifest.plugins;

  Object.keys(plugins).forEach(pluginCode => {
    const plugin = plugins[pluginCode];
    if (!plugin || !plugin.routes || !Array.isArray(plugin.routes)) {
      return;
    }

    // 为每个路由创建菜单项
    plugin.routes.forEach(route => {
      if (!route.path || !route.component) {
        return;
      }

      // 判断是管理侧还是用户侧路由
      const isAdminRoute = route.path.startsWith('/admin/');
      const resourceType = isAdminRoute ? 'admin_plugin_menu' : 'plugin_menu';

      routes.push({
        path: route.path,
        component: route.component,
        name: route.name || plugin.name || pluginCode,
        icon: route.icon,
        sort: route.sort || 0,
        type: resourceType,
        // 保存插件信息，供组件加载器使用
        pluginCode: pluginCode,
        version: plugin.version,
        moduleFormat: plugin.moduleFormat || 'esm',
        entry: plugin.entry || 'index.js',
        assets: plugin.assets || {},
      });
    });
  });

  return routes;
}

/**
 * 合并manifest路由到现有菜单
 * @param {Array} existingRoutes 现有菜单路由
 * @param {Array} manifestRoutes manifest路由
 * @returns {Array} 合并后的路由
 */
export function mergeRoutes(existingRoutes, manifestRoutes) {
  if (!manifestRoutes || manifestRoutes.length === 0) {
    return existingRoutes || [];
  }

  // 创建路径映射，避免重复
  const pathMap = new Map();
  
  // 先添加现有路由
  (existingRoutes || []).forEach(route => {
    if (route.path) {
      pathMap.set(route.path, route);
    }
  });

  // 再添加manifest路由（如果路径不存在）
  manifestRoutes.forEach(route => {
    if (route.path && !pathMap.has(route.path)) {
      pathMap.set(route.path, route);
    }
  });

  return Array.from(pathMap.values());
}

