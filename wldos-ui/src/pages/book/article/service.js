import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryArticle(params) {
  return request(`${prefix}/archives-${params.id}.html`);
}
export async function previewArticle(params) {
  return request(`${prefix}/archives-${params.id}/${params.preview}`);
}
export async function archiveSlug(params) {
  return request(`${prefix}/archive-${params.contAlias}`);
}