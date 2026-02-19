import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 管理端分页列表 */
export async function queryOrderAdminList(params) {
  return request(`${prefix}/admin/order/list`, {
    method: 'GET',
    params,
  });
}

/** 管理端导出 */
export async function exportOrderAdmin(params) {
  return request(`${prefix}/admin/order/export`, {
    method: 'GET',
    params,
    responseType: 'blob',
  });
}

/** 管理端退款（10.4.4）：仅 PAID/COMPLETED 可退 */
export async function refundOrderAdmin(orderNo, reason) {
  return request(`${prefix}/admin/order/${orderNo}/refund`, {
    method: 'POST',
    data: reason != null ? { reason } : {},
  });
}
