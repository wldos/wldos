import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryProvince() {
  return request(`${prefix}/region/prov`);
}
export async function queryCity(province) {
  return request(`${prefix}/region/city/${province}`);
}
export async function queryRegionInfo(city) {
  return request(`${prefix}/region/info/${city}`);
}
export async function saveBaseInfo(params) {
  return request(`${prefix}/user/conf`, {
    method: 'POST',
    data: params,
  });
}
export async function updatePasswd(params) {
  return request(`${prefix}/login/passwd`, {
    method: 'POST',
    data: params,
  });
}
export async function updateMobile(params) {
  return request(`${prefix}/login/mobile`, {
    method: 'POST',
    data: params,
  });
}
export async function updateSecQuest(params) {
  return request(`${prefix}/login/secQuest`, {
    method: 'POST',
    data: params,
  });
}
export async function updateBakEmail(params) {
  return request(`${prefix}/login/bakEmail`, {
    method: 'POST',
    data: params,
  });
}
export async function updateMFA(params) {
  return request(`${prefix}/login/mfa`, {
    method: 'POST',
    data: params,
  });
}
