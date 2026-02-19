import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 获取默认推荐营销规则 */
export async function getMarketingRule() {
  return request(`${prefix}/admin/marketing/rule`, { method: 'GET' });
}

/** 保存默认推荐营销规则 */
export async function saveMarketingRule(data) {
  return request(`${prefix}/admin/marketing/rule`, {
    method: 'PUT',
    data,
  });
}
