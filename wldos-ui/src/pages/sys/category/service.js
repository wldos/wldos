import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/cms/category`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/cms/category/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/cms/category/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/cms/category/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/cms/category/update`, {
    method: 'POST',
    data: params,
  });
}
export async function queryLayerCategory() {
  return request(`${prefix}/category/treeSelect`);
}
export async function queryFlatCategory() {
  return request(`${prefix}/category/flatTree`);
}
// 仅供信息发布模块使用
export async function queryInfoCategory() {
  return request(`${prefix}/info/flatTree`);
}
export async function queryCategoryFromPid(params) {
  return request(`${prefix}/category/fromPid/${params.termTypeId}`);
}
export async function queryCategoryFromPlug(params) {
  return request(`${prefix}/category/fromPlug/${params.slugTerm}`);
}
export async function queryTopCategory() {
  return request(`${prefix}/category/select`);
}
export async function queryPluginCategory() {
  return request(`${prefix}/plugin/category/treeSelect`);
}
export async function queryCategoryTreeByType(classType) {
  return request(`${prefix}/term/category/treeSelect`, {
    params: { classType },
  });
}
export async function infoFlag(params) {
  return request(`${prefix}/admin/cms/term/infoFlags`, {
    method: 'POST',
    data: params,
  });
}