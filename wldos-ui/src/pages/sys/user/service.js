import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/user`, {
    params,
  });
}
export async function queryOrgPage(params) {
  return request(`${prefix}/admin/sys/user/org`, {
    params,
  });
}
export async function queryComPage(params) {
  return request(`${prefix}/admin/sys/user/com`, {
    params,
  });
}
export async function querySelectPage(params) {
  return request(`${prefix}/admin/sys/user/select`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/user/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/sys/user/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeOrgStaff(params) {
  return request(`${prefix}/admin/sys/org/staffDel`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/user/register4admin`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/user/update`, {
    method: 'POST',
    data: params,
  });
}
export async function updatePasswd4admin(params) {
  return request(`${prefix}/admin/sys/user/passwd4admin`, {
    method: 'POST',
    data: params,
  });
}
