import request from '@/utils/request';
import config from '@/utils/config';
import { queryStorePlugins } from '@/pages/sys/store/service';

const { prefix } = config;

/** 获取插件列表（用于产品选择插件，含 icon 作封面默认值） */
export async function getPluginListForSelect() {
  try {
    const res = await queryStorePlugins({ pageSize: 500 });
    const rows = res?.data?.rows || res?.data?.list || [];
    return rows.map((p) => ({
      id: p.id,
      pluginCode: p.pluginCode || p.code,
      name: p.name || p.pluginCode || p.code || String(p.id),
      icon: p.icon || p.iconUrl || p.coverImage || '',
    }));
  } catch (e) {
    return [];
  }
}

/** 管理端产品分页列表（POST body） */
export async function queryStoreProductAdminList(data) {
  return request(`${prefix}/admin/product/list`, {
    method: 'POST',
    data,
  });
}

/** 管理端产品详情 */
export async function getStoreProductAdminDetail(id) {
  return request(`${prefix}/admin/product/${id}`, {
    method: 'GET',
  });
}

/** 新增产品 */
export async function addStoreProduct(data) {
  return request(`${prefix}/admin/product/add`, {
    method: 'POST',
    data,
  });
}

/** 更新产品 */
export async function updateStoreProduct(id, data) {
  return request(`${prefix}/admin/product/update/${id}`, {
    method: 'PUT',
    data,
  });
}

/** 删除产品 */
export async function deleteStoreProduct(id) {
  return request(`${prefix}/admin/product/delete/${id}`, {
    method: 'DELETE',
  });
}

/** 产品分类类型枚举（与 wldos 类型管理一致，用于表单展示/校验） */
export async function getProductCategoryType() {
  try {
    const res = await request(`${prefix}/product/category-type`, { method: 'GET' });
    const data = res?.data ?? res?.data?.data ?? res;
    return data || null;
  } catch (e) {
    return null;
  }
}

/** 分类树接口原始请求（管理端/表单用） */
export async function getStoreProductCategoriesRequest() {
  return request(`${prefix}/product/categories`, {
    method: 'GET',
  });
}

/** 分类树（返回解析后的数组，便于 TreeSelect 使用） */
export async function getStoreProductCategories() {
  try {
    const res = await getStoreProductCategoriesRequest();
    const raw = res?.data ?? res;
    if (Array.isArray(raw)) return raw;
    if (raw && Array.isArray(raw.data)) return raw.data;
    return [];
  } catch (e) {
    return [];
  }
}

/** 查询 CMS 文档列表（用于产品详情关联选择，k_pubs） */
export async function queryCmsPubList(params) {
  return request(`${prefix}/admin/cms/pub/info`, {
    method: 'GET',
    params,
  });
}
