import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/collector/info/list`, {
    params,
  });
}
export async function fetchSrcCache(id) {
  return request(`${prefix}/admin/collector/info/srcCache/${id}`);
}