import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/domain/resList`, {
    params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/domain/resConf`, {
    method: 'POST',
    data: params,
  });
}
export async function getAppList() {
  return request(`${prefix}/admin/sys/app/all`);
}
export async function getResList() {
  return request(`${prefix}/admin/sys/res/all`);
}
export async function removeDomainRes(params) {
  return request(`${prefix}/admin/sys/domain/resDel`, {
    method: 'DELETE',
    data: params,
  });
}
export async function querySelectPage(params) {
  return request(`${prefix}/admin/sys/res/select`, {
    params,
  });
}
