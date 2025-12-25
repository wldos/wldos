import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/cms/comment`, {
    params,
  });
}
export async function publishEntity(params) {
  return request(`${prefix}/admin/cms/comment/audit`, {
    method: 'POST',
    data: params,
  });
}
export async function offlineEntity(params) {
  return request(`${prefix}/admin/cms/comment/reject`, {
    method: 'POST',
    data: params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/cms/comment/del`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/cms/comment/delBatch`, {
    method: 'DELETE',
    data: params,
  });
}
export async function spamComment(params) {
  return request(`${prefix}/admin/cms/comment/spam`, {
    method: 'POST',
    data: params,
  });
}
export async function trashComment(params) {
  return request(`${prefix}/admin/cms/comment/trash`, {
    method: 'POST',
    data: params,
  });
}