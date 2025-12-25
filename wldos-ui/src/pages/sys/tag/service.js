import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/cms/tag`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/cms/category/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/cms/tag/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/cms/category/update`, {
    method: 'POST',
    data: params,
  });
}
export async function queryTagSelect() {
  return request(`${prefix}/tag/select`);
}
