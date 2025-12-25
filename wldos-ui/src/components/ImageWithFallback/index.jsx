import React, { useCallback } from 'react';

/**
 * 统一的图片组件，支持懒加载和错误兜底
 * @param {Object} props - 组件属性
 * @param {string} props.src - 图片源地址
 * @param {string} props.alt - 图片alt文本
 * @param {string|number} props.width - 图片宽度
 * @param {string|number} props.height - 图片高度
 * @param {string} props.className - CSS类名
 * @param {Object} props.style - 内联样式
 * @param {boolean} props.loading - 是否懒加载，默认true
 * @param {string} props.fallbackText - 兜底文字，默认"图片加载失败"
 * @param {string} props.fallbackSize - 兜底图片尺寸，默认"medium" (small/medium/large)
 * @param {Function} props.onError - 自定义错误处理
 * @param {Function} props.onLoad - 自定义加载完成处理
 * @param {Object} props.rest - 其他img属性
 */
const ImageWithFallback = ({
  src,
  alt = '',
  width,
  height,
  className = '',
  style = {},
  loading = true,
  fallbackText = '图片加载失败',
  fallbackSize = 'medium',
  onError,
  onLoad,
  ...rest
}) => {
  // 根据尺寸预设生成兜底SVG
  const getFallbackSvg = useCallback((size, text) => {
    const sizeMap = {
      small: { width: '80', height: '60', fontSize: '10' },
      medium: { width: '160', height: '120', fontSize: '14' },
      large: { width: '240', height: '180', fontSize: '16' },
      custom: { width: width || '100%', height: height || '120', fontSize: '14' }
    };
    
    const config = sizeMap[size] || sizeMap.medium;
    
    return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(`
      <svg xmlns="http://www.w3.org/2000/svg" width="${config.width}" height="${config.height}">
        <defs>
          <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stop-color="#f5f5f5"/>
            <stop offset="1" stop-color="#e9e9e9"/>
          </linearGradient>
        </defs>
        <rect width="100%" height="100%" fill="url(#g)"/>
        <text x="50%" y="50%" text-anchor="middle" fill="#999" font-size="${config.fontSize}">${text}</text>
      </svg>
    `)}`;
  }, [width, height]);

  // 统一的错误处理
  const handleError = useCallback((e) => {
    if (e && e.target) {
      e.target.onerror = null;
      e.target.src = getFallbackSvg(fallbackSize, fallbackText);
    }
    onError?.(e);
  }, [getFallbackSvg, fallbackSize, fallbackText, onError]);

  // 合并样式
  const mergedStyle = {
    objectFit: 'cover',
    ...(width && { width: typeof width === 'number' ? `${width}px` : width }),
    ...(height && { height: typeof height === 'number' ? `${height}px` : height }),
    ...style
  };

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      style={mergedStyle}
      loading={loading ? 'lazy' : undefined}
      onError={handleError}
      onLoad={onLoad}
      {...rest}
    />
  );
};

export default ImageWithFallback;
