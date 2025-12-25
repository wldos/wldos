import request from '@/utils/request';
import config from '@/utils/config';
import { headerFix } from '@/utils/utils';

const { prefix } = config;

// 获取客户端插件列表
export async function queryPlugins(params) {
  return request(`${prefix}/admin/plugins`, {
    params,
  });
}

// 获取单个插件详情
export async function getPlugin(id) {
  return request(`${prefix}/admin/plugins/${id}`);
}

// 安装插件
export async function installPlugin(data) {
  const { file, ...otherData } = data;

  // 后端使用 @RequestParam 接收参数，需要使用 FormData 或 URL 参数
  // 即使没有文件，也使用 FormData 发送，确保参数能被正确接收
  const formData = new FormData();
  
  if (file) {
    formData.append('file', file);
  }

  // 添加其他参数（pluginCode、filePath 等）
  Object.keys(otherData).forEach(key => {
    if (otherData[key] != null) {
      formData.append(key, otherData[key]);
    }
  });

  return request(`${prefix}/admin/plugins/install`, {
    method: 'POST',
    data: formData,
    headers: {
      ...headerFix,
      'Content-Type': 'multipart/form-data',
    },
  });
}

// 卸载插件
export async function uninstallPlugin(id) {
  return request(`${prefix}/admin/plugins/uninstall/${id}`, {
    method: 'DELETE',
    data: { id },
  });
}

// 启用插件
export async function enablePlugin(id) {
  return request(`${prefix}/admin/plugins/enable/${id}`, {
    method: 'POST',
    data: { id },
  });
}

// 禁用插件
export async function disablePlugin(id) {
  return request(`${prefix}/admin/plugins/disable/${id}`, {
    method: 'POST',
    data: { id },
  });
}

// 更新插件
export async function updatePlugin(id) {
  return request(`${prefix}/admin/plugins/update/${id}`, {
    method: 'POST',
    data: { id },
  });
}

// 获取插件配置
export async function getPluginConfig(id) {
  return request(`${prefix}/admin/plugins/config`, {
    params: { id },
  });
}

// 更新插件配置
export async function updatePluginConfig(id, config) {
  return request(`${prefix}/admin/plugins/config/update`, {
    method: 'POST',
    data: { id, config },
  });
}

// 获取插件依赖信息
export async function getPluginDependencies(id) {
  return request(`${prefix}/admin/plugins/dependencies`, {
    params: { id },
  });
}

// 检查插件依赖
export async function checkPluginDependencies(id) {
  return request(`${prefix}/admin/plugins/dependencies/check`, {
    method: 'POST',
    data: { id },
  });
}



// 获取插件日志
export async function getPluginLogs(id, params) {
  return request(`${prefix}/admin/plugins/logs`, {
    method: 'GET',
    params: { id, ...params },
  });
}

// 清理插件日志
export async function clearPluginLogs(id) {
  return request(`${prefix}/admin/plugins/logs/clear`, {
    method: 'POST',
    data: { id },
  });
}

// 获取插件统计信息
export async function getPluginStats() {
  return request(`${prefix}/admin/plugins/stats`, {
    method: 'GET',
  });
}

// 批量操作插件
export async function batchOperatePlugins(operation, pluginIds) {
  return request(`${prefix}/admin/plugins/batch`, {
    method: 'POST',
    data: {
      operation,
      pluginIds,
    },
  });
}

// 获取插件市场列表
export async function getPluginMarket(params) {
  return request(`${prefix}/admin/plugins/market`, {
    method: 'GET',
    params,
  });
}

// 从市场安装插件
export async function installFromMarket(pluginCode, version) {
  return request(`${prefix}/admin/plugins/market/install`, {
    method: 'POST',
    data: {
      pluginCode,
      version,
    },
  });
}

// 检查插件更新
export async function checkPluginUpdates() {
  return request(`${prefix}/admin/plugins/updates/check`, {
    method: 'POST',
  });
}

// 更新插件到指定版本
export async function updatePluginVersion(id, version) {
  return request(`${prefix}/admin/plugins/update-version`, {
    method: 'POST',
    data: { id, version },
  });
}
