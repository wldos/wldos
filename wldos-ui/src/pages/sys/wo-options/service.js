import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
const BASE = `${prefix}/admin/sys/options`;

export async function queryPage(params) {
  return request(`${BASE}/query`, {
    params,
  });
}

export async function addEntity(params) {
  return request(`${BASE}/add`, {
    method: 'POST',
    data: params,
  });
}

export async function updateEntity(params) {
  return request(`${BASE}/update`, {
    method: 'POST',
    data: params,
  });
}

export async function removeEntity(params) {
  return request(`${BASE}/delete`, {
    method: 'DELETE',
    data: params,
  });
}

export async function removeEntities(params) {
  return request(`${BASE}/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
