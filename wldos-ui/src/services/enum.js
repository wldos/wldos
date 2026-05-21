import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryEnumTemplate() {
  return request(`${prefix}/enum/select/template`);
}
export async function queryEnumResource() {
  return request(`${prefix}/enum/select/resource`);
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

// 插件相关枚举
export async function fetchEnumPluginStatus() {
  return request(`${prefix}/enum/select/pluginStatus`);
}
export async function fetchEnumAutoStart() {
  return request(`${prefix}/enum/select/autoStart`);
}

// 注：商业模块枚举（订单状态 / 佣金状态等）放在 `pages/commercial/_shared/enums.js`，
// 与开源核心枚举隔离（详见 .cursor/rules/wldos-knowledge-base.mdc § 5.3.0.3 末段「商业模块隔离」）。