import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/log/sys`, {
    method: 'GET',
    params,
  });
}

export async function queryDetail(id) {
  return request(`${prefix}/admin/sys/log/sys/${id}`, {
    method: 'GET',
  });
}
