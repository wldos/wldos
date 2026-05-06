/**
 * 系统日志 管理端 API。
 *
 * 后端：modules/wldos-platform → com.wldos.platform.audit.controller.SysLogAdminController
 *      网关前缀 /api，类级 @RequestMapping("/admin/sys/log/sys")。
 */
import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/**
 * 分页查询系统日志。
 *
 * @param {Object} params - 包含 current/pageSize/sorter/filter 以及业务过滤字段
 *                          （sourceModule / eventType / severity / operatorUserId）。
 */
export async function queryPage(params) {
  return request(`${prefix}/admin/sys/log/sys`, {
    method: 'GET',
    params,
  });
}

/** 按 ID 查询单条详情。 */
export async function queryDetail(id) {
  return request(`${prefix}/admin/sys/log/sys/${encodeURIComponent(id)}`, {
    method: 'GET',
  });
}
