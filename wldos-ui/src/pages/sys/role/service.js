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
export async function getRoleList(params) {
  return request(`${prefix}/admin/sys/role/all`, { params });
}
export async function fetchScopeDimensions(params) {
  return request(`${prefix}/admin/sys/scope/dimensions/grantable`, { params });
}
export async function fetchPickableDictTypes(params) {
  return request(`${prefix}/admin/sys/scope/dimensions/pickable`, { params });
}
/** @deprecated 使用 fetchPickableDictTypes */
export async function fetchPickableDimensions(params) {
  return fetchPickableDictTypes(params);
}
export async function fetchDirectAdoptedDimensions(params) {
  return request(`${prefix}/admin/sys/role/scopes/adopted`, { params });
}
export async function fetchRoleScopes(params) {
  return request(`${prefix}/admin/sys/role/scopes`, { params });
}
export async function saveRoleScopeBundle({ roleId, scopes, adoptedDictCodes, adoptedDimensionCodes }) {
  return request(`${prefix}/admin/sys/role/scopes?roleId=${roleId}`, {
    method: 'POST',
    data: { scopes, adoptedDictCodes, adoptedDimensionCodes },
  });
}
/** @deprecated 使用 saveRoleScopeBundle */
export async function saveRoleScopes({ roleId, scopes }) {
  return saveRoleScopeBundle({ roleId, scopes, adoptedDictCodes: [] });
}
export async function copyRoleScopes(data) {
  return request(`${prefix}/admin/sys/role/scopes/copy`, {
    method: 'POST',
    data,
  });
}
export async function batchApplyRoleScopes(data) {
  return request(`${prefix}/admin/sys/role/scopes/batch`, {
    method: 'POST',
    data,
  });
}
export async function previewAuthBatch(data) {
  return request(`${prefix}/admin/sys/role/auth/batch/preview`, {
    method: 'POST',
    data,
  });
}
export async function applyAuthBatch(data) {
  return request(`${prefix}/admin/sys/role/auth/batch`, {
    method: 'POST',
    data,
  });
}
export async function rollbackAuthBatch(data) {
  return request(`${prefix}/admin/sys/role/auth/batch/rollback`, {
    method: 'POST',
    data,
  });
}
export async function fetchAuthDrift(params) {
  return request(`${prefix}/admin/sys/role/auth/drift`, { params });
}
export async function fetchDimensionOptions(dimensionCode) {
  return request(`${prefix}/admin/sys/scope/dimensions/${dimensionCode}/options`);
}
export async function fetchDictOptions(dictCode) {
  return request(`${prefix}/admin/sys/scope/dict/${dictCode}/options`);
}
export async function fetchDictTreeOptions(dictCode) {
  return request(`${prefix}/admin/sys/scope/dict/${dictCode}/tree-options`);
}
export async function fetchRegionChildren(params) {
  return request(`${prefix}/admin/sys/scope/regions/children`, { params });
}
export async function fetchRegionPaths(params) {
  return request(`${prefix}/admin/sys/scope/regions/paths`, { params });
}
