/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */

import { touchMyShortcut } from '@/services/user';

let portalMenuTree = [];

const lastTouch = { path: '', t: 0 };

/** ProLayout postMenuData 注入当前门户菜单树，用于路径匹配菜单名称 */
export function setPortalMenuTree(tree) {
  portalMenuTree = tree && Array.isArray(tree) ? tree : [];
}

export function normalizePath(p) {
  if (!p) return '';
  let s = p.endsWith('/') && p.length > 1 ? p.slice(0, -1) : p;
  if (!s.startsWith('/')) s = `/${s}`;
  return s;
}

/**
 * 在门户菜单树中找出与当前 pathname 最匹配的菜单项：优先精确匹配 path，否则取**路径最长**的前缀匹配（避免落到「系统」等顶层分组名）。
 */
export function findMenuForPathname(pathname) {
  const normPath = normalizePath(pathname);
  const candidates = [];

  const walk = (nodes) => {
    if (!nodes || !nodes.length) return;
    for (const n of nodes) {
      if (n.path) {
        const p = normalizePath(n.path);
        if (!p || p === '/') {
          if (n.children) walk(n.children);
          continue;
        }
        if (normPath === p) {
          candidates.push({ node: n, pathLen: p.length, exact: true });
        } else if (normPath.startsWith(`${p}/`)) {
          candidates.push({ node: n, pathLen: p.length, exact: false });
        }
      }
      if (n.children) walk(n.children);
    }
  };

  walk(portalMenuTree);
  if (candidates.length === 0) return null;
  candidates.sort((a, b) => {
    if (a.exact !== b.exact) return a.exact ? -1 : 1;
    return b.pathLen - a.pathLen;
  });
  return candidates[0].node;
}

/** 顶层分组名等非叶子标题，打点时应回退为路径友好文案，避免「我的常用」全是「系统」 */
const GENERIC_MENU_TITLES = new Set(['系统', '系统管理', '管理', '菜单', '导航']);

export function resolveShortcutRecordTitle(pathname, menuNode) {
  const normPath = normalizePath(pathname);
  const raw = menuNode && menuNode.name != null ? String(menuNode.name).trim() : '';
  if (raw && !GENERIC_MENU_TITLES.has(raw)) {
    return raw.slice(0, 128);
  }
  const PATH_TITLE = {
    '/': '首页',
    '/account/center': '个人中心',
    '/account/settings': '个人设置',
  };
  if (PATH_TITLE[normPath]) return PATH_TITLE[normPath];
  const parts = normPath.split('/').filter(Boolean);
  if (parts.length === 0) return '页面';
  return String(parts[parts.length - 1]).slice(0, 128);
}

const SKIP_PREFIXES = [
  '/user/login',
  '/user/register',
  '/404',
  '/403',
];

function shouldSkipPath(pathname) {
  if (!pathname) return true;
  const p = normalizePath(pathname);
  if (p === '/' || p === '/user' || p === '/user/') return true;
  return SKIP_PREFIXES.some((pre) => p === pre || p.startsWith(`${pre}/`));
}

/**
 * 路由切换时调用：向服务端累加「我的常用」访问记录（静默失败）。
 * {@code /admin} 下不传门户菜单 id，由服务端按管理菜单解析标题与资源 id。
 */
export async function tryRecordPortalMenuVisit(pathname) {
  if (typeof pathname !== 'string' || shouldSkipPath(pathname)) return;

  const now = Date.now();
  if (pathname === lastTouch.path && now - lastTouch.t < 1800) return;
  lastTouch.path = pathname;
  lastTouch.t = now;

  const normPath = normalizePath(pathname);
  if (normPath.startsWith('/admin')) {
    try {
      await touchMyShortcut({
        path: normPath,
        title: '',
      });
    } catch (e) {
      /* 静默：未登录、无权限打点接口或路由校验失败等 */
    }
    return;
  }

  const hit = findMenuForPathname(pathname);
  if (!hit) return;

  try {
    await touchMyShortcut({
      path: normPath,
      title: resolveShortcutRecordTitle(pathname, hit),
      resourceId: hit.id,
    });
  } catch (e) {
    /* 静默：未登录、无权限打点接口或路由校验失败等 */
  }
}
