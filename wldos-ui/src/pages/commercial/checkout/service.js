import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 按类型获取当前生效协议（结算页勾选协议用） */
export async function getAgreementByType(type, pluginCode) {
  return request(`${prefix}/agreement/by-type/${type}`, {
    method: 'GET',
    params: pluginCode ? { pluginCode } : undefined,
  });
}

/** 创建订单（需登录） */
export async function createOrder(data) {
  return request(`${prefix}/order/create`, {
    method: 'POST',
    data,
  });
}

/** 产品详情（结算页展示用） */
export async function getStoreProductDetail(id) {
  return request(`${prefix}/product/${id}`, {
    method: 'GET',
  });
}

/** 推荐码校验（下单前） */
export async function validateReferralCode(code) {
  return request(`${prefix}/user/referral/validate`, {
    method: 'GET',
    params: { code },
  });
}
