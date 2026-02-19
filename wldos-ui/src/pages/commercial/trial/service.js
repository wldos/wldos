import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 申请试用 Key（方案 4.3） */
export async function applyTrial(data) {
  return request(`${prefix}/product/trial/apply`, {
    method: 'POST',
    data,
  });
}
