import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 管理端佣金分页列表 */
export async function queryCommissionList(params) {
  return request(`${prefix}/admin/marketing/commission/list`, {
    method: 'GET',
    params,
  });
}

/** 管理端佣金汇总 */
export async function queryCommissionSummary() {
  return request(`${prefix}/admin/marketing/commission/summary`, {
    method: 'GET',
  });
}

/** 更新佣金状态与备注 */
export async function updateCommission(id, data) {
  return request(`${prefix}/admin/marketing/commission/${id}`, {
    method: 'PUT',
    data,
  });
}
