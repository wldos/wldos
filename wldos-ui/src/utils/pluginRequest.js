import request from '@/utils/request';

/**
 * 创建插件专用的 request 方法
 * 自动为 URL 添加 /plugins/{pluginCode} 前缀
 * 
 * @param {string} pluginCode - 插件编码
 * @returns {function} 插件专用的 request 方法
 */
export function createPluginRequest(pluginCode) {
  if (!pluginCode) {
    throw new Error('pluginCode is required');
  }

  const apiPrefix = `/plugins/${pluginCode}`;

  /**
   * 插件专用的 request 方法
   * 使用方式与 @/utils/request 完全一致
   * 
   * @param {string} url - API 路径（不需要包含 /plugins/{pluginCode} 前缀）
   * @param {object} options - request 选项（与 @/utils/request 的参数格式一致）
   * @returns {Promise} request 结果
   */
  return function pluginRequest(url, options = {}) {
    // 如果 URL 已经包含 /plugins/ 前缀，不再添加
    if (url.startsWith('/plugins/')) {
      return request(url, options);
    }

    // 确保 url 以 / 开头
    const normalizedUrl = url.startsWith('/') ? url : '/' + url;
    // 拼接完整路径：/plugins/{pluginCode}/xxx
    const fullUrl = apiPrefix + normalizedUrl;

    // 调用原有的 request 方法
    return request(fullUrl, options);
  };
}

/**
 * 插件专用的 request 方法（支持 pluginCode 参数）
 * 暴露到 window.pluginRequest，供插件使用
 * 
 * @param {string} url - API 路径（不需要包含 /plugins/{pluginCode} 前缀）
 * @param {object} params - 请求参数（与 @/utils/request 的参数格式一致）
 * @param {string} pluginCode - 插件编码（必需）
 * @returns {Promise} request 结果
 */
export function pluginRequest(url, params, pluginCode) {
  if (!pluginCode) {
    throw new Error('pluginCode is required for pluginRequest');
  }

  // 如果 URL 已经包含 /plugins/ 前缀，不再添加
  if (url.startsWith('/plugins/')) {
    return request(url, params);
  }

  // 确保 url 以 / 开头
  const normalizedUrl = url.startsWith('/') ? url : '/' + url;
  // 拼接完整路径：/plugins/{pluginCode}/xxx
  const fullUrl = `/plugins/${pluginCode}${normalizedUrl}`;

  // 调用原有的 request 方法
  return request(fullUrl, params);
}

// 暴露到全局，供插件使用
if (typeof window !== 'undefined') {
  window.pluginRequest = pluginRequest;
}

