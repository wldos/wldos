import React from 'react';
import ReactDOM from 'react-dom';
import { isAuth} from "@/services/user";
import {history} from 'umi';
import {getAuthority} from "@/utils/authority";
import {autoLoginManager} from "@/utils/autoLogin";
// 导入 pluginRequest 以确保 window.pluginRequest 被暴露
import '@/utils/pluginRequest';

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
      // 用户已登录，重定向到首页
      history.push('/');
      return;
    }
    return;
  }
  
  if (pathname === '404') {
    return;
  }
  
  // 权限验证
  isAuth({route: pathname}).then((res) => {
    const code = res?.data;
    if (code === '401') {
      // console.log('onRouteChange: 401 location=', pathname);
      history.push('/user/login');
    }else if (code === '403') {
      // console.log('onRouteChange: 403 location=', pathname);
      history.push('404');
    }
  });
}