import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 插槽注册表（含当前租户/站点下是否已绑定） */
export async function querySlotCatalog(params) {
  return request(`${prefix}/admin/sys/slot/catalog`, { params });
}

/** 当前租户/站点下的插槽绑定清单 */
export async function querySlotBindings(params) {
  return request(`${prefix}/admin/sys/slot/bindings`, { params });
}

/** 绑定插槽到目标分群/角色 */
export async function bindSlot(params) {
  return request(`${prefix}/admin/sys/slot/bind`, {
    method: 'POST',
    data: params,
  });
}

/** 解绑插槽 */
export async function unbindSlot(params) {
  return request(`${prefix}/admin/sys/slot/unbind`, {
    method: 'POST',
    data: params,
  });
}
