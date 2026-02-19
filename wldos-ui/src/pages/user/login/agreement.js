import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 按类型获取当前生效协议（公开，登录勾选用）- 返回单个，可能为 null */
export async function getAgreementByType(type, pluginCode) {
  return request(`${prefix}/agreement/by-type/${type}`, {
    method: 'GET',
    params: pluginCode ? { pluginCode } : undefined,
  });
}

/** 按类型获取生效协议列表（公开，登录页用）- 返回数组，便于展示勾选 */
export async function getAgreementListByType(type, pluginCode) {
  try {
    const res = await request(`${prefix}/agreement/list`, {
      method: 'GET',
      params: { agreementType: type, ...(pluginCode ? { pluginCode } : {}) },
    });
    const data = res?.data?.data ?? res?.data;
    if (data?.rows) return data.rows;
    if (data?.list) return data.list;
    if (Array.isArray(data)) return data;
    return [];
  } catch (e) {
    return [];
  }
}

/** 协议详情（公开，弹窗展示） */
export async function getAgreementDetail(id) {
  return request(`${prefix}/agreement/${id}`, {
    method: 'GET',
  });
}
