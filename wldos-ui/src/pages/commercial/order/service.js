import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 创建订单（需登录） */
export async function createOrder(data) {
  return request(`${prefix}/order/create`, {
    method: 'POST',
    data,
  });
}

/** 订单详情（需登录） */
export async function getOrderDetail(orderNo) {
  return request(`${prefix}/order/${orderNo}`, {
    method: 'GET',
  });
}

/** 我的订单列表（需登录） */
export async function queryOrderList(params) {
  return request(`${prefix}/order/list`, {
    method: 'GET',
    params,
  });
}

/** 是否可享推荐优惠（新用户） */
export async function getReferralEligible() {
  return request(`${prefix}/order/referral/eligible`, { method: 'GET' });
}

/** 取消订单（需登录） */
export async function cancelOrder(orderNo) {
  return request(`${prefix}/order/${orderNo}/cancel`, {
    method: 'POST',
  });
}

/** 订单说明内容（结算页展示） */
export async function getOrderPolicyContent(type) {
  return request(`${prefix}/order/policy/content`, {
    method: 'GET',
    params: { type },
  });
}
