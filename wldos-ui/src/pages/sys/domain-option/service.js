import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 分页查询全局 wo_options，用于域级表单拉取「同名键」对照 */
export async function fetchGlobalOptionByKey(optionKey) {
  if (!optionKey || !String(optionKey).trim()) {
    return null;
  }
  const res = await request(`${prefix}/admin/sys/options/query`, {
    params: { current: 1, pageSize: 5, optionKey: String(optionKey).trim() },
  });
  const rows = res?.data?.rows ?? [];
  return rows[0] ?? null;
}

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/domainOption`, {
    params,
  });
}

export async function addEntity(params) {
  return request(`${prefix}/admin/sys/domainOption/add`, {
    method: 'POST',
    data: params,
  });
}

export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/domainOption/update`, {
    method: 'POST',
    data: params,
  });
}

export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/domainOption/delete`, {
    method: 'DELETE',
    data: params,
  });
}

export async function removeEntities(params) {
  return request(`${prefix}/admin/sys/domainOption/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
