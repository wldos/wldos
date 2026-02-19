import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function getRobotsTxt() {
  return request(`${prefix}/admin/sys/options/robots`, { method: 'GET' });
}

export async function saveRobotsTxt(content) {
  return request(`${prefix}/admin/sys/options/robots`, {
    method: 'POST',
    data: { content },
    requestType: 'json',
  });
}
