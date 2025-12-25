/**
 * 插件 API 工具
 * 
 * 使用插件自己的 request 工具，完全独立，不依赖主应用
 */
import request from './request';

// API基础路径（插件引擎会自动添加 /plugins/{pluginCode} 前缀）
const API_BASE = '';

// API调用函数 - 直接使用框架的request工具
const apiCall = (url, options = {}) => {
  return request(`${API_BASE}${url}`, options);
};

// 演示数据相关API
export const demoAPI = {
  // 获取所有演示数据
  getDemos: () => apiCall('/list'),
  
  // 根据ID获取演示数据
  getDemoById: (id) => apiCall(`/${id}`),
  
  // 创建演示数据
  createDemo: (demo) => apiCall('/create', {
    method: 'POST',
    data: demo
  }),
  
  // 更新演示数据
  updateDemo: (id, demo) => apiCall(`/${id}`, {
    method: 'PUT',
    data: demo
  }),
  
  // 删除演示数据
  deleteDemo: (id) => apiCall(`/${id}`, {
    method: 'DELETE'
  }),
  
  // 初始化示例数据
  initDemoData: () => apiCall('/init', {
    method: 'POST'
  }),
  
  // 健康检查
  healthCheck: () => apiCall('/health')
};

