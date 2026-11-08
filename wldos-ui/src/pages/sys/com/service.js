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
export async function getComSelectOption(params) {
  return request(`${prefix}/admin/sys/com/select`, { params: params || undefined });
}

export async function getComEntityById(id) {
  return request(`${prefix}/admin/sys/com/get`, {
    params: { id },
  });
}

/** 租户/站点管理员：按会话 comId 拉单条公司，供列表「归属公司」展示与筛选 */
export async function getSessionComSelectOption(comId) {
  if (comId == null || comId === '' || String(comId) === '0') {
    return [];
  }
  const res = await getComEntityById(comId);
  const entity = res?.data;
  if (!entity?.id) {
    return [];
  }
  return [{ label: entity.comName, value: String(entity.id) }];
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