import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
export async function queryCodeUrl(params={authType : '', redirectPrefix : ''}) {
  return request(`${prefix}/oauth/code/${params.authType}`, {
    params
  });
}
