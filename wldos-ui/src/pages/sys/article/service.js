import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/cms/pub/chapter`, {
    params,
  });
}
export async function publishEntity(params) {
  return request(`${prefix}/admin/cms/pub/publish`, {
    method: 'POST',
    data: params,
  });
}
export async function offlineEntity(params) {
  return request(`${prefix}/admin/cms/pub/offline`, {
    method: 'POST',
    data: params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/cms/pub/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/cms/pub/deletes`, {
    method: 'DELETE',
    data: params,
  });
}