import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
export async function verifyUser(params) {
  return request(`${prefix}/login/active`, {
    method: 'POST',
    data: params,
  });
}
