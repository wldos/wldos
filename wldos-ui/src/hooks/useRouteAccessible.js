import { useEffect, useState } from 'react';
import { isAuth } from '@/services/user';
import { getRouteAuthCache } from '@/utils/apiCache';

/** 组织架构树工作台路由（与 config/routes.js、菜单 resourcePath 一致） */
export const ORGAN_ARCHITECTURE_ROUTE = '/admin/organ/architecture';

/**
 * 当前用户是否可访问指定管理端路由（与 app.js isAuth / GET /user/route 同语义）。
 */
export default function useRouteAccessible(routePath) {
  const [accessible, setAccessible] = useState(() => {
    const cached = getRouteAuthCache(routePath);
    return cached === '200';
  });

  useEffect(() => {
    if (!routePath) {
      setAccessible(false);
      return undefined;
    }
    const cached = getRouteAuthCache(routePath);
    if (cached !== undefined) {
      setAccessible(cached === '200');
      return undefined;
    }
    let cancelled = false;
    isAuth({ route: routePath })
      .then((res) => {
        if (!cancelled) {
          setAccessible(res?.data === '200');
        }
      })
      .catch(() => {
        if (!cancelled) {
          setAccessible(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [routePath]);

  return accessible;
}
