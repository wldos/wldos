import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryElement(params) {
  return request(`${prefix}/element-${params.id}.html`);
}
export async function previewElement(params) {
  return request(`${prefix}/element-${params.id}/preview`);
}