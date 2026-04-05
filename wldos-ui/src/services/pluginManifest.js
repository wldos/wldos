/**
 * 插件 manifest 服务：优先从引擎 API 拉取，失败时回退静态 /plugin-assets/manifest.json
 */
import config from '@/utils/config';
import { headerFix } from '@/utils/utils';

const { prefix } = config;

/**
 * 加载插件 manifest（聚合 JSON）
 * @returns {Promise<Object>} manifest 对象，结构 { plugins: { ... } }
 */
export async function loadPluginManifest() {
  const tryApi = async () => {
    const response = await fetch(`${prefix}/plugin-ui/manifest`, {
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Cache-Control': 'no-cache',
        ...headerFix(),
      },
      credentials: 'same-origin',
    });
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    const manifest = await response.json();
    return manifest || { plugins: {} };
  };

  const tryStatic = async () => {
    const response = await fetch('/plugin-assets/manifest.json', {
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Cache-Control': 'no-cache',
      },
    });
    if (!response.ok) {
      if (response.status === 404) {
        return { plugins: {} };
      }
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    const manifest = await response.json();
    return manifest || { plugins: {} };
  };

  try {
    return await tryApi();
  } catch (e) {
    console.warn('[PluginManifest] API 拉取失败，回退静态 manifest.json:', e?.message || e);
    try {
      return await tryStatic();
    } catch (err2) {
      console.error('[PluginManifest] 加载 manifest 失败:', err2);
      return { plugins: {} };
    }
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

  Object.keys(plugins).forEach((pluginCode) => {
    const plugin = plugins[pluginCode];
    if (!plugin || !plugin.routes || !Array.isArray(plugin.routes)) {
      return;
    }

    plugin.routes.forEach((route) => {
      if (!route.path || !route.component) {
        return;
      }

      const isAdminRoute = route.path.startsWith('/admin/');
      const resourceType = isAdminRoute ? 'admin_plugin_menu' : 'plugin_menu';

      routes.push({
        path: route.path,
        component: route.component,
        name: route.name || plugin.name || pluginCode,
        icon: route.icon,
        sort: route.sort || 0,
        type: resourceType,
        pluginCode,
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

  const pathMap = new Map();

  (existingRoutes || []).forEach((route) => {
    if (route.path) {
      pathMap.set(route.path, route);
    }
  });

  manifestRoutes.forEach((route) => {
    if (route.path && !pathMap.has(route.path)) {
      pathMap.set(route.path, route);
    }
  });

  return Array.from(pathMap.values());
}
