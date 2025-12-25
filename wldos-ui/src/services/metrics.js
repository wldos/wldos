/**
 * 埋点上报服务
 * 用于将前端收集的UMD加载埋点数据上报到后端
 */
import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/**
 * 上报UMD加载埋点数据
 * @param {object} metricsData - 埋点数据
 * @param {Array} metricsData.uiInfoTime - UI信息获取耗时列表
 * @param {Array} metricsData.scriptTime - 脚本加载耗时列表
 * @param {Array} metricsData.parseErrors - 解析错误列表
 * @param {Array} metricsData.firstPaintTime - 首渲染耗时列表
 * @returns {Promise}
 */
export async function reportUmdMetrics(metricsData) {
  return request(`${prefix}/admin/plugins/metrics/umd`, {
    method: 'POST',
    data: metricsData,
  });
}

/**
 * 上报单个插件加载埋点（实时上报）
 * @param {string} code - 插件编码
 * @param {object} metric - 单个埋点数据
 * @returns {Promise}
 */
export async function reportPluginLoadMetric(code, metric) {
  return request(`${prefix}/admin/plugins/metrics/umd/plugin/${code}`, {
    method: 'POST',
    data: {
      code,
      ...metric,
    },
  });
}

