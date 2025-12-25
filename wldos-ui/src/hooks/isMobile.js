import { useEffect, useState } from 'react';

/**
 * 移动端检测 Hook
 * 统一判断是否为移动端设备（宽度 < 768px）
 * @returns {boolean} isMobile - 是否为移动端
 */
export default function isMobile() {
  const get = () => {
    if (typeof window === 'undefined') return false;
    const width = window.innerWidth || document.documentElement.clientWidth;
    const match = window.matchMedia && window.matchMedia('(max-width: 767.98px)').matches;
    return match || width < 768;
  };

  const [isMobile, setIsMobile] = useState(get);

  useEffect(() => {
    let timeoutId = null;
    const debouncedResize = () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      timeoutId = setTimeout(() => {
        const newIsMobile = get();
        setIsMobile(prev => prev !== newIsMobile ? newIsMobile : prev);
      }, 100); // 防抖延迟100ms
    };
    
    window.addEventListener('resize', debouncedResize);
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      window.removeEventListener('resize', debouncedResize);
    };
  }, []);

  return isMobile;
}
