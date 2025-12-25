import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryArticle(params) {
  return request(`${prefix}/archives-id/${params.id}`);
}