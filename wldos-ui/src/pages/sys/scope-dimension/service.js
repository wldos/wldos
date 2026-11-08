import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/scope/dimension`, { params });
}

export async function addEntity(data) {
  return request(`${prefix}/admin/sys/scope/dimension/add`, { method: 'POST', data });
}

export async function updateEntity(data) {
  return request(`${prefix}/admin/sys/scope/dimension/update`, { method: 'POST', data });
}

export async function removeEntity(data) {
  return request(`${prefix}/admin/sys/scope/dimension/delete`, { method: 'DELETE', data });
}

export async function fetchDictSources() {
  return request(`${prefix}/admin/sys/scope/dict-sources`);
}

export async function batchRegister(data) {
  return request(`${prefix}/admin/sys/scope/dimension/batch`, { method: 'POST', data });
}
