import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 管理端分页列表 */
export async function queryAgreementList(params) {
  return request(`${prefix}/admin/agreement/list`, {
    method: 'GET',
    params,
  });
}

/** 管理端详情 */
export async function getAgreementAdminDetail(id) {
  return request(`${prefix}/admin/agreement/${id}`, {
    method: 'GET',
  });
}

/** 新增协议 */
export async function addAgreement(data) {
  return request(`${prefix}/admin/agreement`, {
    method: 'POST',
    data,
  });
}

/** 更新协议 */
export async function updateAgreement(data) {
  return request(`${prefix}/admin/agreement/update`, {
    method: 'POST',
    data,
  });
}

/** 删除协议 */
export async function deleteAgreement(id) {
  return request(`${prefix}/admin/agreement/${id}`, {
    method: 'DELETE',
  });
}

/** 启用/停用 */
export async function toggleAgreementActive(id, active) {
  return request(`${prefix}/admin/agreement/${id}/active`, {
    method: 'POST',
    params: { active },
  });
}
