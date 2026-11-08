import React, { useEffect, useState } from 'react';
import { Spin } from 'antd';
import { archiveSlug } from '@/pages/book/article/service';
import { Redirect } from 'react-router';
import Page from '@/pages/book/page/Page';
import { typeUrl } from '@/utils/utils';
import DynamicRouter from '@/pages/dynamicrouter';

/**
 * 别名路由：优先 CMS pubName（含 page 类型），未命中再交动态菜单路由。
 * 静态路由 /:slug 在 routes.js 中位于 /* 之前，不会被 Umi 直接交给 dynamicrouter。
 */
const Slug = (props) => {
  const { match, dispatch } = props;
  const { params = {} } = match;
  const [resolveState, setResolveState] = useState('loading');
  const [idType, setIdType] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const slug = params?.slug ?? '';
    if (!slug) {
      setResolveState('miss');
      setIdType(null);
      return undefined;
    }
    setResolveState('loading');
    setIdType(null);
    archiveSlug({ contAlias: slug })
      .then((res) => {
        if (cancelled) return;
        const data = res?.data ?? res;
        if (data && (data.id != null || data.pubType)) {
          setIdType(data);
          setResolveState('ok');
        } else {
          setIdType(null);
          setResolveState('miss');
        }
      })
      .catch(() => {
        if (!cancelled) {
          setIdType(null);
          setResolveState('miss');
        }
      });
    return () => {
      cancelled = true;
    };
  }, [params?.slug]);

  if (resolveState === 'loading') {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: 120 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (resolveState === 'miss') {
    return <DynamicRouter key={typeof window !== 'undefined' ? window.location.pathname : params?.slug} />;
  }

  if (idType?.pubType === 'page') {
    return <Page key={idType.id} data={idType} match={match} dispatch={dispatch} />;
  }

  if (idType?.id) {
    return <Redirect to={typeUrl({ pubType: idType.pubType, id: idType.id })} />;
  }

  return <DynamicRouter key={typeof window !== 'undefined' ? window.location.pathname : params?.slug} />;
};

export default Slug;
