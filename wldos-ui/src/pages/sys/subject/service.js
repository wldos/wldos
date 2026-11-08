import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 主体类型字典 */
export async function querySubjectTypes() {
  return request(`${prefix}/admin/sys/subject/types`);
}

/** 主体认证清单 */
export async function querySubjectAuths(params) {
  return request(`${prefix}/admin/sys/subject/auth-list`, { params });
}

/** 添加主体认证（用户挂主体类型） */
export async function addSubjectAuth(params) {
  return request(`${prefix}/admin/sys/subject/auth-add`, {
    method: 'POST',
    data: params,
  });
}

/** 移除主体认证 */
export async function removeSubjectAuth(params) {
  return request(`${prefix}/admin/sys/subject/auth-remove`, {
    method: 'POST',
    data: params,
  });
}

/** 主体授权清单 */
export async function querySubjectGrants(params) {
  return request(`${prefix}/admin/sys/subject/grant-list`, { params });
}

/** 添加主体授权（主体类型挂角色） */
export async function addSubjectGrant(params) {
  return request(`${prefix}/admin/sys/subject/grant-add`, {
    method: 'POST',
    data: params,
  });
}

/** 移除主体授权 */
export async function removeSubjectGrant(params) {
  return request(`${prefix}/admin/sys/subject/grant-remove`, {
    method: 'POST',
    data: params,
  });
}
