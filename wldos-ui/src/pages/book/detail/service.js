import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryProduct(params) {
  return request(`${prefix}/product-${params.id}.html`);
}
export async function previewProduct(params) {
  return request(`${prefix}/product-${params.id}/${params.preview}`);
}
export async function queryInfo(params) {
  return request(`${prefix}/info-${params.id}.html`);
}
export async function previewInfo(params) {
  return request(`${prefix}/info-${params.id}/${params.preview}`);
}
export async function queryComment(params) {
  return request(`${prefix}/cms/comment/${params.id}`);
}
export async function doComment(params) {
  return request(`${prefix}/cms/comment/commit`, {
    method: 'POST',
    data: params,
  });
}
export async function delComment(params) {
  return request(`${prefix}/cms/comment/del`, {
    method: 'DELETE',
    data: params,
  });
}