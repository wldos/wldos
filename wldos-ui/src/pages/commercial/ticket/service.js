import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 提交工单（需登录） */
export async function createTicket(data) {
  return request(`${prefix}/ticket`, {
    method: 'POST',
    data,
  });
}

/** 我的工单列表（需登录），GET，params: current、pageSize */
export async function queryTicketList(params = {}) {
  const { current = 1, pageSize = 10 } = params;
  return request(`${prefix}/ticket/list`, {
    method: 'GET',
    params: { current, pageSize },
  });
}

/** 工单详情（需登录） */
export async function getTicketDetail(id) {
  return request(`${prefix}/ticket/${id}`, {
    method: 'GET',
  });
}

/** 回复工单（需登录） */
export async function replyTicket(id, content) {
  return request(`${prefix}/ticket/${id}/reply`, {
    method: 'POST',
    data: { content },
  });
}
