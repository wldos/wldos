import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryLicense() {
  return request(`${prefix}/admin/sys/license`);
}
export async function queryIssueVersion() {
  return request(`${prefix}/admin/sys/license/versionEnum`);
}
