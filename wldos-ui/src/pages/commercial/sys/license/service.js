import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 管理端 License 列表，POST + PageQuery body */
export async function queryLicenseAdminList(params = {}) {
  const { current = 1, pageSize = 10, userId, pluginCode, status } = params;
  const condition = {};
  if (userId != null && userId !== '') condition.userId = userId;
  if (pluginCode != null && pluginCode !== '') condition.pluginCode = pluginCode;
  if (status != null && status !== '') condition.status = status;
  return request(`${prefix}/admin/license/list`, {
    method: 'POST',
    data: { current, pageSize, condition },
  });
}

/** 补发 License（管理员） */
export async function reissueLicense(licenseId) {
  return request(`${prefix}/admin/license/reissue`, {
    method: 'POST',
    params: { licenseId },
  });
}

/** 失效 License（管理员） */
export async function revokeLicense(licenseId) {
  return request(`${prefix}/admin/license/revoke`, {
    method: 'POST',
    params: { licenseId },
  });
}
