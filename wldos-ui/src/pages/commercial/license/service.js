import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 我的 License 列表（需登录），POST + PageQuery body */
export async function queryLicenseList(params = {}) {
  const { current = 1, pageSize = 10 } = params;
  return request(`${prefix}/license/list`, {
    method: 'POST',
    data: { current, pageSize, condition: {} },
  });
}

/** License 详情（需登录） */
export async function getLicenseDetail(id) {
  return request(`${prefix}/license/${id}`, {
    method: 'GET',
  });
}
