import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function addInfo(params) {
  return request(`${prefix}/info/add`, {
    method: 'POST',
    data: params,
  });
}
export async function fetchEnumMap() {
  return request(`${prefix}/info/enum/privacy`);
}
export async function preUpdate(params) {
  return request(`${prefix}/info-${params.id}`);
}
export async function updateInfo(params) {
  return request(`${prefix}/info/update`, {
    method: 'POST',
    data: params,
  });
}
export async function deleteInfo(params) {
  return request(`${prefix}/info/delete`, {
    method: 'DELETE',
    data: params,
  });
}