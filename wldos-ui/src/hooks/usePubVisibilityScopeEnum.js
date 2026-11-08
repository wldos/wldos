import { useEffect, useMemo, useState } from 'react';
import { fetchEnumPubVisibilityScope } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';

/** 与 {@link com.wldos.cms.enums.PubVisibilityScopeEnum} 一致，接口不可用时的兜底 */
export const PUB_VISIBILITY_SCOPE_FALLBACK = [
  { label: '前台可发现', value: 'PUBLIC_LISTED' },
  { label: '仅链接访问（不参与列表/搜索）', value: 'UNLISTED' },
  { label: '仅内部可见', value: 'INTERNAL_ONLY' },
];

function normalizeSelectList(res) {
  const raw = res?.data;
  if (Array.isArray(raw)) {
    return raw;
  }
  if (Array.isArray(raw?.rows)) {
    return raw.rows;
  }
  return [];
}

/**
 * CMS k_pubs.visibility_scope：GET /enum/select/pubVisibilityScope
 */
export function usePubVisibilityScopeEnum() {
  const [options, setOptions] = useState(PUB_VISIBILITY_SCOPE_FALLBACK);
  const [valueEnum, setValueEnum] = useState(() => selectToEnum(PUB_VISIBILITY_SCOPE_FALLBACK));

  useEffect(() => {
    fetchEnumPubVisibilityScope()
      .then((res) => {
        const list = normalizeSelectList(res);
        if (list.length > 0) {
          setOptions(list);
          setValueEnum(selectToEnum(list));
        }
      })
      .catch(() => {
        /* 保留 fallback */
      });
  }, []);

  const labelOf = useMemo(
    () => (code) => (code != null && valueEnum[code]?.text ? valueEnum[code].text : code || ''),
    [valueEnum],
  );

  return { options, valueEnum, labelOf };
}

/**
 * Step2 formContent 第 5 参：统一带上发现范围 options，避免各入口漏传。
 */
export function useBookFormPrivacyExtra(showVisibilityScope) {
  const { options: visibilityScopeOptions } = usePubVisibilityScopeEnum();
  return {
    showVisibilityScope,
    visibilityScopeOptions,
  };
}
