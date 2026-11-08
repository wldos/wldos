import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function fetchPlatformOperatorContext() {
  return request(`${prefix}/admin/sys/platform-operator/context`);
}
