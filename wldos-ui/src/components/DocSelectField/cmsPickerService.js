import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/**
 * 业务模块「关联 CMS 文档」选择器（专用接口，与 /admin/cms/pub/book 内容管理列表隔离）。
 */
export async function queryAdminPubPicker(params) {
  return request(`${prefix}/admin/cms/pub/association-picker`, {
    method: 'GET',
    params,
  });
}

export async function getAdminPub(id) {
  return request(`${prefix}/admin/cms/pub/get`, {
    method: 'GET',
    params: { id },
  });
}

export async function getSpaceBook(bookId, params) {
  return request(`${prefix}/space/book/${bookId}`, {
    method: 'GET',
    params,
  });
}

export async function getSpaceBookChapter(bookId, chapterId) {
  return request(`${prefix}/space/book/${bookId}/chapter/${chapterId}`, {
    method: 'GET',
  });
}

export async function updatePubVisibilityScope(id, visibilityScope) {
  return request(`${prefix}/admin/cms/pub/${id}/visibility-scope`, {
    method: 'PUT',
    data: { visibilityScope },
  });
}
