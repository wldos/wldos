import React, { Suspense, useState, useEffect, useMemo } from 'react';
import { history } from 'umi';
import { Alert, Spin } from 'antd';
import Monitor from '@/pages/sys/monitor';
import { queryAdminHome } from '@/services/user';

const BuiltinMonitor = Monitor;

/**
 * 管理端 /admin「首页」Tab：按当前访问域解析 admin_home_config，动态加载组件或回退内置仪表盘。
 */
const AdminHomeDispatcher = (props) => {
  const [config, setConfig] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(null);
  const [HomeComponent, setHomeComponent] = useState(null);
  const [componentLoading, setComponentLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      setLoading(true);
      setLoadError(null);
      try {
        const res = await queryAdminHome();
        if (cancelled) return;
        if (res?.code !== 200) {
          setConfig({ module: 'builtin', url: 'sys/monitor', source: 'builtin' });
          return;
        }
        setConfig(res.data || { module: 'builtin', url: 'sys/monitor', source: 'builtin' });
      } catch (e) {
        if (!cancelled) {
          setLoadError(e);
          setConfig({ module: 'builtin', url: 'sys/monitor', source: 'builtin' });
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, []);

  const module = config?.module || 'builtin';
  const url = config?.url || '';
  const extraPropsStr = config?.extraProps || '';

  const extraProps = useMemo(() => {
    if (!extraPropsStr || typeof extraPropsStr !== 'string') return {};
    try {
      return JSON.parse(extraPropsStr);
    } catch {
      return {};
    }
  }, [extraPropsStr]);

  useEffect(() => {
    if (!config || loading) return;
    if (module === 'redirect' && url) {
      const target = (url || '').replace(/\/$/, '') || '/admin';
      if (!target.startsWith('/admin')) {
        setConfig({ module: 'builtin', url: 'sys/monitor', source: 'builtin' });
        return;
      }
      window.sessionStorage.setItem('wldos.adminHomePath', target);
      history.replace({
        pathname: target,
        state: { fromAdminHome: true },
      });
      return;
    }
    window.sessionStorage.setItem('wldos.adminHomePath', '/admin');
    if (module !== 'component' || !url) {
      setHomeComponent(null);
      setComponentLoading(false);
      return;
    }
    let cancelled = false;
    const loadComponent = async () => {
      setComponentLoading(true);
      try {
        const mod = await import(/* webpackChunkName: "admin-home-[request]" */ `@/pages/${url}/index`);
        if (!cancelled) {
          setHomeComponent(() => mod.default || mod);
        }
      } catch (err) {
        console.error('管理端首页组件加载失败:', url, err);
        if (!cancelled) setHomeComponent(null);
      } finally {
        if (!cancelled) setComponentLoading(false);
      }
    };
    loadComponent();
    return () => { cancelled = true; };
  }, [config, loading, module, url]);

  if (loading) {
    return (
      <div style={{ padding: 100, textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  const inheritedHint = config?.source === 'rootDomain' ? (
    <Alert
      type="info"
      showIcon
      style={{ marginBottom: 16 }}
      message="当前首页继承自根域名主站配置"
    />
  ) : null;

  if (module === 'redirect' && url) {
    return (
      <div style={{ padding: 100, textAlign: 'center' }}>
        <Spin size="large" tip="正在跳转…" />
      </div>
    );
  }

  if (module === 'component' && url) {
    if (componentLoading && !HomeComponent) {
      return (
        <div style={{ padding: 100, textAlign: 'center' }}>
          <Spin size="large" />
        </div>
      );
    }
    if (HomeComponent) {
      return (
        <>
          {inheritedHint}
          <Suspense fallback={<Spin size="large" />}>
            <HomeComponent {...props} adminHomeConfig={config} extraProps={extraProps} />
          </Suspense>
        </>
      );
    }
    return (
      <>
        {inheritedHint}
        <Alert
          type="warning"
          showIcon
          message="首页组件加载失败，已回退内置仪表盘"
          description={loadError ? String(loadError.message || loadError) : `路径：${url}`}
          style={{ marginBottom: 16 }}
        />
        <BuiltinMonitor {...props} />
      </>
    );
  }

  return (
    <>
      {inheritedHint}
      <BuiltinMonitor {...props} adminHomeConfig={config} />
    </>
  );
};

export default AdminHomeDispatcher;
