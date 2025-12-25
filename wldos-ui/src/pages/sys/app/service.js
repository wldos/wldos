import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/app`, {
    params,
  });
}
export async function queryDomainPage(params) {
  return request(`${prefix}/admin/sys/app/domain`, {
    params,
  });
}
export async function querySelectPage(params) {
  return request(`${prefix}/admin/sys/app/select`, {
    params,
  });
}
export async function fetchAppType() {
  return request(`${prefix}/admin/sys/app/type`);
}
export async function fetchAppOrigins() {
  return request(`${prefix}/admin/sys/app/origins`);
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/app/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/sys/app/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeDomainApp(params) {
  return request(`${prefix}/admin/sys/domain/appDel`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/app/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/app/update`, {
    method: 'POST',
    data: params,
  });
}
export async function installPlugin(params) {
  return request(`${prefix}/admin/plugin/install`, {
    method: 'POST',
    data: params,
  });
}
