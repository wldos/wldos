import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/com`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/com/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntitys(params) {
  return request(`${prefix}/admin/sys/com/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/com/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/com/update`, {
    method: 'POST',
    data: params,
  });
}
export async function getComSelectOption() {
  return request(`${prefix}/admin/sys/com/select`);
}
export async function removeComAdmin(params) {
  return request(`${prefix}/admin/sys/com/rmAdmin`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addComAdmin(params) {
  return request(`${prefix}/admin/sys/com/admin`, {
    method: 'POST',
    data: params,
  });
}