import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/scope/object-bind`, { params });
}

export async function addEntity(data) {
  return request(`${prefix}/admin/sys/scope/object-bind/add`, { method: 'POST', data });
}

export async function updateEntity(data) {
  return request(`${prefix}/admin/sys/scope/object-bind/update`, { method: 'POST', data });
}

export async function fetchDimensionCatalog(params) {
  return request(`${prefix}/admin/sys/scope/dimension`, { params });
}
