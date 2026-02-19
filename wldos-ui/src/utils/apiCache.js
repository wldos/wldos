/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * API 缓存工具
 *
 * 用于减少高频 API 的重复请求，按评估文档实现：
 * - route（isAuth）：按 pathname 内存缓存，会话内复用
 * - 登出或 token 失效时需清空
 *
 * @see wldos-agent/docs/网站模板管理与API缓存方案评估.md
 */

/** pathname -> 200|401|403 的缓存，会话内有效 */
const routeAuthCache = new Map();

/**
 * 获取 pathname 对应的路由权限缓存
 * @param {string} pathname 路由路径
 * @returns {string|undefined} '200'|'401'|'403' 或 undefined（未缓存）
 */
export function getRouteAuthCache(pathname) {
  if (!pathname) return undefined;
  return routeAuthCache.get(pathname);
}

/**
 * 设置 pathname 的路由权限缓存
 * @param {string} pathname 路由路径
 * @param {string} code '200'|'401'|'403'
 */
export function setRouteAuthCache(pathname, code) {
  if (!pathname || !code) return;
  routeAuthCache.set(pathname, String(code));
}

/**
 * 清空路由权限缓存（登出、401 时调用）
 */
export function clearRouteAuthCache() {
  routeAuthCache.clear();
}
