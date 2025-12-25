import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function search(params) {
  return request(`${prefix}/cms/pub/search`, {
    params,
  });
}
