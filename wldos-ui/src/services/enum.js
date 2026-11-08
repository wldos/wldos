import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryEnumTemplate() {
  return request(`${prefix}/enum/select/template`);
}
export async function queryEnumResource() {
  return request(`${prefix}/enum/select/resource`);
}
export async function queryEnumScopeLevel() {
  return request(`${prefix}/enum/select/scopeLevel`);
}
export async function queryEnumResourceTerminal() {
  return request(`${prefix}/enum/select/resourceTerminal`);
}
export async function queryEnumResourceMenuRegion() {
  return request(`${prefix}/enum/select/resourceMenuRegion`);
}
export async function queryEnumMenuNodeKind() {
  return request(`${prefix}/enum/select/menuNodeKind`);
}
/** wo_options.option_type */
export async function fetchEnumWoOptionType() {
  return request(`${prefix}/enum/select/optionType`);
}
/** wo_options.app_code */
export async function fetchEnumWoOptionAppCode() {
  return request(`${prefix}/enum/select/optionAppCode`);
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

/** k_pubs.visibility_scope（CMS 可发现性） */
export async function fetchEnumPubVisibilityScope() {
  return request(`${prefix}/enum/select/pubVisibilityScope`);
}

// 插件相关枚举
export async function fetchEnumPluginStatus() {
  return request(`${prefix}/enum/select/pluginStatus`);
}
export async function fetchEnumAutoStart() {
  return request(`${prefix}/enum/select/autoStart`);
}

export async function fetchEnumUserStatus() {
  return request(`${prefix}/enum/select/userStatus`);
}

/** wo_sys_log metadata.actorKind 中文（列表展示；平台 EnumController，与是否安装网关商业模块无关） */
export async function fetchEnumSysLogActorKind() {
  return request(`${prefix}/enum/select/sysLogActorKind`);
}
