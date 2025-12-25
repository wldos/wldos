import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/role`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/role/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntitys(params) {
  return request(`${prefix}/admin/sys/role/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/role/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/role/update`, {
    method: 'POST',
    data: params,
  });
}
export async function getResList() {
  return request(`${prefix}/admin/sys/res/tree`);
}
export async function getExistRes(params) {
  return request(`${prefix}/admin/sys/role/res`, {
    params,
  });
}
export async function authRole(params) {
  return request(`${prefix}/admin/sys/role/auth`, {
    method: 'POST',
    data: params,
  });
}
export async function getRoleList() {
  return request(`${prefix}/admin/sys/role/all`);
}
