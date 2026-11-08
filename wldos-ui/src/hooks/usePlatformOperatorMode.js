import { useEffect, useMemo, useState } from 'react';
import { useLocation } from 'umi';
import { fetchPlatformOperatorContext } from '@/pages/sys/platform-operator/service';

/**
 * URL 带 platformOps=1 时锁定为平台运营主体 scope（读 options 指针）。
 */
export function usePlatformOperatorMode() {
  const location = useLocation();
  const enabled = location?.query?.platformOps === '1';
  const [ctx, setCtx] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!enabled) {
      setCtx(null);
      setLoading(false);
      return undefined;
    }
    let cancelled = false;
    setLoading(true);
    fetchPlatformOperatorContext()
      .then((res) => {
        if (cancelled) {
          return;
        }
        if (res?.code === 200 && res?.data) {
          setCtx(res.data);
        } else {
          setCtx({ ready: false });
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [enabled]);

  const lock = useMemo(() => {
    if (!enabled || !ctx?.ready) {
      return null;
    }
    return {
      comId: String(ctx.operatorComId),
      domainId: String(ctx.primaryDomainId),
      comName: ctx.comName,
      siteLabel: ctx.siteName || ctx.siteDomain,
    };
  }, [enabled, ctx]);

  return {
    enabled,
    loading,
    ctx,
    lock,
    ready: !enabled || (ctx?.ready === true),
  };
}

export default usePlatformOperatorMode;
