import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/org`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/org/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntitys(params) {
  return request(`${prefix}/admin/sys/org/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/org/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/org/update`, {
    method: 'POST',
    data: params,
  });
}
export async function getExistRes(params) {
  return request(`${prefix}/admin/sys/org/role`, {
    params,
  });
}
export async function authRole(params) {
  return request(`${prefix}/admin/sys/org/auth`, {
    method: 'POST',
    data: params,
  });
}
export async function getOrgList() {
  return request(`${prefix}/admin/sys/org/all`);
}
export async function addOrgUser(params) {
  return request(`${prefix}/admin/sys/org/user`, {
    method: 'POST',
    data: params,
  });
}
export async function fetchOrgType() {
  return request(`${prefix}/admin/sys/org/type`);
}