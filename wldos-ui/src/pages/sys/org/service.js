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
export async function getOrgList(params) {
  return request(`${prefix}/admin/sys/org/all`, { params });
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

/** 身份登记册：按类别查询成员（admin/tadmin/dadmin/can_trust） */
export async function queryIdentityRegistry(params) {
  return request(`${prefix}/admin/sys/identity/registry`, {
    params,
  });
}

/** 身份登记册：添加成员（tadmin 须传 userComId） */
export async function addIdentityMember(params) {
  return request(`${prefix}/admin/sys/identity/add`, {
    method: 'POST',
    data: params,
  });
}

/** 身份登记册：移除成员 */
export async function removeIdentityMember(params) {
  return request(`${prefix}/admin/sys/identity/remove`, {
    method: 'POST',
    data: params,
  });
}