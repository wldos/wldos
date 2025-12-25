import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
export async function auth2Login(params) {
  return request(`${prefix}/oauth/login`, {
    method: 'POST',
    data: params,
  });
}
