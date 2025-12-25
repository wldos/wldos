import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryUsers() {
  return request(`${prefix}/user/users`);
}
export async function queryCurrent() {
  return request(`${prefix}/user/currentUser`);
}
export async function queryCurAccount() {
  return request(`${prefix}/user/curAccount`);
}
export async function queryMenu() {
  return request(`${prefix}/admin/sys/user/adminMenu`);
}
export async function queryUserMenu(userId) {
  return request(`${prefix}/user/menu/${userId}`);
}
export async function queryUserAdminMenu(userId) {
  return request(`${prefix}/admin/sys/user/adminMenu/${userId}`);
}
export async function querySiteSeo() {
  return request(`${prefix}/user/curDomain`);
}
export async function querySiteSlogan() {
  return request(`${prefix}/user/slogan`);
}
export async function queryNotices() { // @Todo 消息提醒下一步实现
  return [];
}
export async function fetchRoutes() {
  return request(`${prefix}/user/routes`);
}
export async function isAuth(params) {
  return request(`${prefix}/user/route`, {
    params
  });
}
export async function fetchDynRoutes() { // 获取后端设置的首页动态模板，不是动态路由
  return request(`${prefix}/user/dynamite`);
}