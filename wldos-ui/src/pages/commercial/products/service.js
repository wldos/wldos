import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 产品列表（公开） */
export async function queryStoreProductList(params) {
  return request(`${prefix}/product/list`, {
    method: 'GET',
    params,
  });
}

/** 产品详情（公开） */
export async function getStoreProductDetail(id) {
  return request(`${prefix}/product/${id}`, {
    method: 'GET',
  });
}

/** 产品分类类型枚举（与 wldos 类型管理一致） */
export async function getProductCategoryType() {
  try {
    const res = await request(`${prefix}/product/category-type`, { method: 'GET' });
    const data = res?.data ?? res?.data?.data ?? res;
    return data || null;
  } catch (e) {
    return null;
  }
}

/** 分类树（公开） */
export async function getStoreProductCategories() {
  return request(`${prefix}/product/categories`, {
    method: 'GET',
  });
}

/** 产品中心筛选项（分类+类型/适用场景/许可方式，与类型管理同一模式） */
export async function getProductOptions() {
  return request(`${prefix}/product/options`, {
    method: 'GET',
  });
}
