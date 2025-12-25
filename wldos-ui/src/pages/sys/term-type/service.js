import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;
const TERM_TYPE_META_API = `${prefix}/admin/system/term-type-meta`;
const TERM_API = `${prefix}/admin/term-type/term`;

// 分类类型元数据管理 API
/**
 * 查询所有分类类型
 */
export async function queryTermTypes() {
  return request(`${TERM_TYPE_META_API}`, {
    method: 'GET',
  });
}

/**
 * 根据编码查询分类类型
 */
export async function queryTermTypeByCode(code) {
  return request(`${TERM_TYPE_META_API}/code/${code}`, {
    method: 'GET',
  });
}

/**
 * 保存分类类型（新增或更新）
 */
export async function saveTermType(data) {
  return request(`${TERM_TYPE_META_API}`, {
    method: 'POST',
    data,
  });
}

/**
 * 删除分类类型
 */
export async function deleteTermType(code) {
  return request(`${TERM_TYPE_META_API}/code/${code}`, {
    method: 'DELETE',
  });
}

// 通用分类项管理 API（支持所有类型）
/**
 * 查询分类项列表（分页）
 * @param {Object} params - 查询参数，必须包含 classType
 */
export async function queryTermPage(params) {
  return request(`${TERM_API}`, {
    method: 'GET',
    params,
  });
}

/**
 * 新增分类项
 * @param {Object} data - 分类项数据，必须包含 classType
 */
export async function addTerm(data) {
  return request(`${TERM_API}/add`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新分类项
 * @param {Object} data - 分类项数据
 */
export async function updateTerm(data) {
  return request(`${TERM_API}/update`, {
    method: 'POST',
    data,
  });
}

/**
 * 删除分类项
 * @param {Object} data - 分类项数据，包含 id 和 termTypeId
 */
export async function deleteTerm(data) {
  return request(`${TERM_API}/delete`, {
    method: 'DELETE',
    data,
  });
}

/**
 * 批量删除分类项
 * @param {Array} data - 分类项数组，每个元素包含 id 和 termTypeId
 */
export async function deleteTerms(data) {
  return request(`${TERM_API}/deletes`, {
    method: 'DELETE',
    data,
  });
}
