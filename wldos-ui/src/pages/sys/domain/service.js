import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/domain`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/domain/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/sys/domain/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/domain/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/domain/update`, {
    method: 'POST',
    data: params,
  });
}
export async function addDomainApp(params) {
  return request(`${prefix}/admin/sys/domain/app`, {
    method: 'POST',
    data: params,
  });
}
export async function addDomainRes(params) {
  return request(`${prefix}/admin/sys/domain/res`, {
    method: 'POST',
    data: params,
  });
}
export async function getPlatDomain() {
  return request(`${prefix}/admin/sys/domain/root`);
}
