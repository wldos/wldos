import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function configOptions(params) {
  return request(`${prefix}/system/options/config`, {
    method: 'POST',
    data: params,
  });
}
export async function getOrgList() {
  return request(`${prefix}/admin/sys/org/platOrg`);
}
export async function fetchSysOptions() {
  return request(`${prefix}/system/options`);
}

