import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 管理端工单列表 */
export async function queryTicketAdminList(params) {
  return request(`${prefix}/admin/ticket/list`, {
    method: 'GET',
    params,
  });
}

/** 管理端工单详情（客服可查看任意工单） */
export async function getTicketAdminDetail(id) {
  return request(`${prefix}/admin/ticket/${id}`, {
    method: 'GET',
  });
}

/** 客服回复 */
export async function adminReplyTicket(id, content) {
  return request(`${prefix}/admin/ticket/${id}/reply`, {
    method: 'POST',
    data: { content },
  });
}
