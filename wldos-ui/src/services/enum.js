import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryEnumTemplate() {
  return request(`${prefix}/enum/select/template`);
}
export async function queryEnumResource() {
  return request(`${prefix}/enum/select/resource`);
}
export async function fetchEnumPubStatus() {
  return request(`${prefix}/enum/select/pubStatus`);
}
export async function fetchEnumIsCollect() {
  return request(`${prefix}/admin/collector/info/enum/select/collect`);
}
export async function fetchEnumPubType() {
  return request(`${prefix}/enum/select/pubType`);
}

// 插件相关枚举
export async function fetchEnumPluginStatus() {
  return request(`${prefix}/enum/select/pluginStatus`);
}
export async function fetchEnumAutoStart() {
  return request(`${prefix}/enum/select/autoStart`);
}