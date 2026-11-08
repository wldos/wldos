import { useEffect, useState } from 'react';
import { fetchCmsCommentEnabled } from '@/pages/book/detail/service';

/** 全站评论开关（wo_options wldos_cms_comment_enabled），默认 true 与现网一致 */
export default function useCmsCommentEnabled() {
  const [enabled, setEnabled] = useState(true);

  useEffect(() => {
    let cancelled = false;
    fetchCmsCommentEnabled().then((res) => {
      if (cancelled) {
        return;
      }
      const raw = res?.data;
      if (raw === false || raw === 'false' || raw === '0' || raw === 0) {
        setEnabled(false);
      } else if (raw === true || raw === 'true' || raw === '1' || raw === 1) {
        setEnabled(true);
      }
    });
    return () => {
      cancelled = true;
    };
  }, []);

  return enabled;
}
