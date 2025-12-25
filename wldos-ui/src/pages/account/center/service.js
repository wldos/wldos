import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
export async function queryFakeList(params) {
  return request('/api/fake_list', {
    params,
  });
}

export async function saveTags(tags) {
  return request(`${prefix}/user/conf/tags`, {
    method: 'POST',
    data: tags,
  });
}
