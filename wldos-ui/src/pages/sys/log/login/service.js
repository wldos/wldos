/**
 * 登录日志 管理端 API。
 *
 * 后端：modules/wldos-platform → com.wldos.platform.audit.controller.LoginLogAdminController
 *      网关前缀 /api，类级 @RequestMapping("/admin/sys/log/login")。
 *
 * 注：GET 请求 utils/request.js 会透传 params，无需手工拼 query string。
 */
import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/**
 * 分页查询登录日志。
 *
 * @param {Object} params - 包含 current/pageSize/sorter/filter 以及业务过滤字段
 *                          （loginAccount / userId / ip / eventType / result）。
 */
export async function queryPage(params) {
  return request(`${prefix}/admin/sys/log/login`, {
    method: 'GET',
    params,
  });
}

/** 按 ID 查询单条详情。 */
export async function queryDetail(id) {
  return request(`${prefix}/admin/sys/log/login/${encodeURIComponent(id)}`, {
    method: 'GET',
  });
}
