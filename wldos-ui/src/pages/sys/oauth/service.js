import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function saveConfig({oauthType, ...params}) {
  return request(`${prefix}/oauth/config/${oauthType}`, {
    method: 'POST',
    data: params,
  });
}
export async function fetchCurConfig(oauthType) {
  return request(`${prefix}/oauth/fetch/${oauthType}`);
}
