import { useEffect, useMemo, useState } from 'react';
import { fetchEnumPubType } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';

/** 与 {@link com.wldos.platform.core.enums.PubTypeEnum} 主类型一致，接口不可用时的兜底 */
export const PUB_TYPE_FALLBACK = [
  { label: '文集', value: 'book' },
  { label: '文档', value: 'doc' },
  { label: '文章', value: 'post' },
  { label: '信息', value: 'info' },
  { label: '服务协议', value: 'agreement' },
  { label: '常见问题', value: 'faq' },
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
 * CMS 主作品发布类型：GET /enum/select/pubType
 *
 * @param {{ excludeValues?: string[] }} [options]
 */
export function usePubTypeEnum({ excludeValues = [] } = {}) {
  const [options, setOptions] = useState(PUB_TYPE_FALLBACK);
  const [valueEnum, setValueEnum] = useState(() => selectToEnum(PUB_TYPE_FALLBACK));

  useEffect(() => {
    fetchEnumPubType()
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

  const pickerOptions = useMemo(() => {
    const excluded = new Set(excludeValues);
    return options.filter((o) => !excluded.has(o.value));
  }, [options, excludeValues]);

  const labelOf = useMemo(
    () => (code) => (code != null && valueEnum[code]?.text ? valueEnum[code].text : code || '—'),
    [valueEnum],
  );

  return { options: pickerOptions, valueEnum, labelOf };
}
