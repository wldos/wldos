import React, { Suspense, useState, useEffect, useMemo } from 'react';
import {connect, history} from 'umi';
import { Spin } from 'antd';
import Product from "@/pages/home/components/Product";
import Archives from "@/pages/home/components/Archives";
import Category from "@/pages/home/components/Category";
import Admin from "@/pages/sys/monitor";

const Index = (props) => {
  const {match, route,} = props;
  const [HomeComponent, setHomeComponent] = useState(null);

  // 安全地解构 match 和 params
  const params = match?.params || {};
  const {slugCategory, page} = params;
  const pageUrl = match?.url || '/';

  // 稳定化 route 数据，避免每次都是新对象导致 useEffect 重复执行
  const routeData = useMemo(() => {
    if (!route || !route['/']) return {};
    const homeRoute = route['/'];
    return {
      category: homeRoute.category,
      module: homeRoute.module,
      url: homeRoute.url,
    };
  }, [route?.['/']?.category, route?.['/']?.module, route?.['/']?.url]);

  const {category, module, url} = routeData;
  const state = {slugTerm: category?? slugCategory, url: '/', pageUrl, page};

  // 稳定化 url 和 module 值，避免字符串引用变化导致 useEffect 重复执行
  const stableUrl = useMemo(() => url, [url]);
  const stableModule = useMemo(() => module, [module]);

  // 根据 module 类型判断处理方式
  // module === 'url'：按外部链接处理
  if (stableModule === 'url' && stableUrl) {
    if (stableUrl.startsWith('http://') || stableUrl.startsWith('https://')) {
      window.location.href = stableUrl;
      return null;
    }
  }

  // module === 'component'：按组件动态加载处理
  useEffect(() => {
    const loadHomeComponent = async () => {
      if (stableModule === 'component' && stableUrl) {
        try {
          // url 作为组件路径，动态加载
          const module = await import(/* webpackChunkName: "[request]" */ `@/pages/${stableUrl}/index`);
          const LoadedComponent = module.default || module;
          setHomeComponent(() => LoadedComponent);
        } catch (err) {
          console.error('加载首页组件失败:', err);
          setHomeComponent(null); // 加载失败，回退到旧逻辑
        }
      } else {
        setHomeComponent(null); // 不是 component 类型，使用旧逻辑
      }
    };

    loadHomeComponent();
  }, [stableModule, stableUrl]);

  // 新逻辑：如果加载了新组件，使用新组件
  if (HomeComponent) {
    return (
      <Suspense fallback={<Spin size="large" />}>
        <HomeComponent
          {...props}
          {...state}
        />
      </Suspense>
    );
  }

  // 旧逻辑：兼容原有 module 映射（module 不是 'url' 也不是 'component' 时）
  const moduleMap = {
    'product': <Product {...state} />,
    'category': <Category {...state} />,
    'archives': <Archives {...state} count={8} pageName="all" />,
    'admin': <Admin />,
  };

  if (stableModule === 'admin') {
    history.push('/admin');
    return (<></>);
  }

  const body = stableModule && moduleMap[stableModule];
  return (<>{body || moduleMap['archives']}</>);
};

export default connect(({ user, loading }) => ({
  route: user.route,
  loading: loading.effects['user/fetchCurrent'],
}))(Index);
