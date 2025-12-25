import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

// 获取应用市场插件列表
export async function queryStorePlugins(params) {
  try {
    const response = await request(`${prefix}/_store/plugins`, {
      params,
    });

    // 添加模拟插件数据用于测试配置功能
    const mockPlugin = {
      id: 'mock-store-plugin-001',
      pluginCode: 'test-config-plugin',
      name: '测试配置插件',
      version: '1.0.0',
      author: 'WLDOS团队',
      mainClass: 'com.wldos.plugin.TestConfigPlugin',
      category: '测试工具',
      tags: JSON.stringify(['配置', '测试', '开发']),
      downloadCount: 1250,
      starCount: 89,
      likeCount: 156,
      reviewStatus: 'APPROVED',
      description: '这是一个用于测试插件配置功能的模拟插件，包含各种配置项类型。',
      icon: '/store/logo-wldos.svg',
      downloadUrl: 'https://github.com/wldos/test-config-plugin/releases/latest',
      createTime: '2024-01-15 10:30:00',
      updateTime: '2024-01-20 14:25:00',
      createBy: 'admin',
      updateBy: 'admin'
    };

    // 将模拟插件添加到响应数据中
    if (response?.data?.rows) {
      response.data.rows = [mockPlugin, ...response.data.rows];
      response.data.total = (response.data.total || 0) + 1;
    } else {
      response.data = {
        rows: [mockPlugin],
        total: 1
      };
    }

    return response;
  } catch (error) {
    // 如果API调用失败，返回模拟数据
    const mockPlugin = {
      id: 'mock-store-plugin-001',
      pluginCode: 'test-config-plugin',
      name: '测试配置插件',
      version: '1.0.0',
      author: 'WLDOS团队',
      mainClass: 'com.wldos.plugin.TestConfigPlugin',
      category: '测试工具',
      tags: JSON.stringify(['配置', '测试', '开发']),
      downloadCount: 1250,
      starCount: 89,
      likeCount: 156,
      reviewStatus: 'APPROVED',
      description: '这是一个用于测试插件配置功能的模拟插件，包含各种配置项类型。',
      icon: '/store/logo-wldos.svg',
      downloadUrl: 'https://github.com/wldos/test-config-plugin/releases/latest',
      createTime: '2024-01-15 10:30:00',
      updateTime: '2024-01-20 14:25:00',
      createBy: 'admin',
      updateBy: 'admin'
    };

    return {
      data: {
        rows: [mockPlugin],
        total: 1
      },
      success: true
    };
  }
}

// 获取单个插件详情
export async function getStorePlugin(id) {
  return request(`${prefix}/_store/plugins/${id}`, {
    method: 'GET',
  });
}

// 获取应用商店插件列表
export async function queryAppStorePlugins(params) {
  try {
    const response = await request(`${prefix}/appstore/plugins`, {
      params,
    });

    // 添加模拟插件数据用于测试配置功能
    const mockPlugin = {
      id: 'mock-store-plugin-001',
      pluginCode: 'test-config-plugin',
      name: '测试配置插件',
      version: '1.0.0',
      author: 'WLDOS团队',
      mainClass: 'com.wldos.plugin.TestConfigPlugin',
      category: '测试工具',
      tags: JSON.stringify(['配置', '测试', '开发']),
      downloadCount: 1250,
      starCount: 89,
      likeCount: 156,
      reviewStatus: 'APPROVED',
      description: '这是一个用于测试插件配置功能的模拟插件，包含各种配置项类型。',
      icon: '/store/logo-wldos.svg',
      downloadUrl: 'https://github.com/wldos/test-config-plugin/releases/latest',
      createTime: '2024-01-15 10:30:00',
      updateTime: '2024-01-20 14:25:00',
      createBy: 'admin',
      updateBy: 'admin'
    };

    // 将模拟插件添加到响应数据中
    if (response?.data?.rows) {
      response.data.rows = [mockPlugin, ...response.data.rows];
      response.data.total = (response.data.total || 0) + 1;
    } else {
      response.data = {
        rows: [mockPlugin],
        total: 1
      };
    }

    return response;
  } catch (error) {
    // 如果API调用失败，返回模拟数据
    const mockPlugin = {
      id: 'mock-store-plugin-001',
      pluginCode: 'test-config-plugin',
      name: '测试配置插件',
      version: '1.0.0',
      author: 'WLDOS团队',
      mainClass: 'com.wldos.plugin.TestConfigPlugin',
      category: '测试工具',
      tags: JSON.stringify(['配置', '测试', '开发']),
      downloadCount: 1250,
      starCount: 89,
      likeCount: 156,
      reviewStatus: 'APPROVED',
      description: '这是一个用于测试插件配置功能的模拟插件，包含各种配置项类型。',
      icon: '/store/logo-wldos.svg',
      downloadUrl: 'https://github.com/wldos/test-config-plugin/releases/latest',
      createTime: '2024-01-15 10:30:00',
      updateTime: '2024-01-20 14:25:00',
      createBy: 'admin',
      updateBy: 'admin'
    };

    return {
      data: {
        rows: [mockPlugin],
        total: 1
      },
      success: true
    };
  }
}

// 新增插件
export async function addStorePlugin(data) {
  return request(`${prefix}/appstore/plugins/addWithCategories`, {
    method: 'POST',
    data,
  });
}

// 更新插件
export async function updateStorePlugin(id, data) {
  return request(`${prefix}/appstore/plugins/updateWithCategories`, {
    method: 'POST',
    data,
  });
}

// 删除插件
export async function deleteStorePlugin(id) {
  return request(`${prefix}/appstore/plugins/delete`, {
    method: 'DELETE',
    data: { id },
  });
}

// 审核插件
export async function reviewStorePlugin(pluginCode, reviewData) {
  return request(`${prefix}/appstore/plugins/${pluginCode}/review`, {
    method: 'POST',
    data: {
      approved: reviewData.approved,
      comments: reviewData.comments || undefined
    },
  });
}

// 下架插件
export async function offlineStorePlugin(pluginCode) {
  return request(`${prefix}/appstore/plugins/${pluginCode}/offline`, {
    method: 'POST',
  });
}

// 安装反馈（下载数+1）
export async function installFeedback(pluginCode) {
  return request(`${prefix}/_store/plugins/feedback/${pluginCode}`, {
    method: 'POST',
  });
}

// 获取应用商店插件分类列表（用于下拉列表）
export async function getPluginCategories() {
  return request(`${prefix}/appstore/plugins/categories`, {
    method: 'GET',
  });
}

// 获取应用市场插件分类列表（用于下拉列表）
export async function getStorePluginCategories() {
  return request(`${prefix}/_wldostore/plugins/categories`, {
    method: 'GET',
  });
}

// 获取插件统计信息
export async function getPluginStats() {
  return request(`${prefix}/_store/stats`, {
    method: 'GET',
  });
}

// 上传插件文件
export async function uploadPluginFile(file) {
  const formData = new FormData();
  formData.append('file', file);

  return request(`${prefix}/appstore/plugins/upload`, {
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

// 下载插件
export async function downloadPlugin(id) {
  return request(`${prefix}/appstore/plugins/download`, {
    method: 'GET',
    params: { id },
    responseType: 'blob',
  });
}

// 获取插件依赖信息
export async function getPluginDependencies(id) {
  return request(`${prefix}/appstore/plugins/dependencies`, {
    method: 'GET',
    params: { id },
  });
}

// 检查插件兼容性
export async function checkPluginCompatibility(pluginCode, version) {
  return request(`${prefix}/admin/appstore/plugins/compatibility`, {
    method: 'POST',
    data: { pluginCode, version },
  });
}

// 从 GitHub/Gitee 同步插件
export async function syncFromGitHub(data) {
  return request(`${prefix}/appstore/plugins/sync-from-github`, {
    method: 'POST',
    data,
  });
}
