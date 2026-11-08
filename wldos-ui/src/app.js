import React from 'react';
import ReactDOM from 'react-dom';
import { isAuth} from "@/services/user";
import {history} from 'umi';
import {getAuthority} from "@/utils/authority";
import {autoLoginManager} from "@/utils/autoLogin";
// 导入 pluginRequest 以确保 window.pluginRequest 被暴露
import '@/utils/pluginRequest';
import { resolveRuntimeLocale, getMomentLocaleTag } from '@/utils/runtimeLocale';
import { bootstrapDesktopEmbeddedApp, isDesktopEmbedded, getDesktopShellBranding, DESKTOP_DEFAULT_ENTRY_PATH, consumeDesktopShellBootRoute } from '@/utils/desktopEmbeddedBridge';
import DarkThemeBootstrap from '@/components/DarkTheme/DarkThemeBootstrap';

/** 统一语言解析：仅 zh-CN / zh-TW / en-US，避免匹配到 pt-BR 等片段 locale */
export const locale = {
  getLocale() {
    return resolveRuntimeLocale();
  },
};

/** 保证 moment 与 Umi 语言一致（ignoreMomentLocale 时仍需按需加载 locale 数据） */
export async function render(oldRender) {
  const m = (await import('moment')).default;
  await import('moment/locale/zh-cn');
  await import('moment/locale/zh-tw');
  const tag = getMomentLocaleTag(resolveRuntimeLocale());
  m.locale(tag);
  bootstrapDesktopEmbeddedApp(history);
  if (typeof window !== 'undefined') {
    const { readDarkMode, syncDarkTheme } = await import('@/components/DarkTheme/darkMode');
    syncDarkTheme(readDarkMode());
  }
  oldRender();
}

export function rootContainer(container) {
  return (
    <>
      <DarkThemeBootstrap />
      {container}
    </>
  );
}

// 主应用暴露 React 到全局，供插件运行时复用（类似 Java 类加载器机制）
// 插件代码可以直接 import React from 'react'，webpack externals 会转换为从 window.React 获取
// 运行时复用主应用的 React 实例，不会重复打包，避免多实例问题
if (typeof window !== 'undefined') {
  window.React = React;
  window.ReactDOM = ReactDOM;
}

// 应用启动时的自动登录检测
export async function onRouteChange({ location }) {
  const {pathname} = location;

  // 如果是登录页，检查是否已有有效登录状态
  if (pathname === '/user/login') {
    const isLoggedIn = await autoLoginManager.checkLoginStatus();
    if (isLoggedIn) {
      const home = isDesktopEmbedded()
        ? (getDesktopShellBranding()?.entryPath || DESKTOP_DEFAULT_ENTRY_PATH)
        : '/';
      history.push(home);
      return;
    }
    return;
  }
  
  if (isDesktopEmbedded()) {
    const entry =
      consumeDesktopShellBootRoute()
      || getDesktopShellBranding()?.entryPath
      || DESKTOP_DEFAULT_ENTRY_PATH;
    if (pathname === '/' || pathname === '/index.html') {
      history.replace(entry);
      return;
    }
  }
  
  if (pathname === '/404') {
    return;
  }

  const authRes = await isAuth({ route: pathname });
  const code = authRes?.data;
  if (code === '401') {
    history.push('/user/login');
  } else if (code === '403') {
    history.push('/404');
  } else if (authRes?.code === 200) {
    import('@/utils/portalMenuVisit').then((m) => m.tryRecordPortalMenuVisit(pathname));
  }
}