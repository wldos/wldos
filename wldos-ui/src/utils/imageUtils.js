/**
 * 图片处理工具函数
 */

/**
 * 生成兜底图片的SVG数据URL
 * @param {Object} options - 配置选项
 * @param {string|number} options.width - 宽度
 * @param {string|number} options.height - 高度
 * @param {string} options.text - 显示文字，默认"图片加载失败"
 * @param {string} options.fontSize - 字体大小，默认"14"
 * @param {string} options.bgColor - 背景色，默认渐变灰色
 * @returns {string} SVG数据URL
 */
export const generateFallbackImage = ({
  width = '100%',
  height = '120',
  text = '图片加载失败',
  fontSize = '14',
  bgColor = 'linear-gradient'
} = {}) => {
  const svgContent = bgColor === 'linear-gradient' 
    ? `
      <svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}">
        <defs>
          <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stop-color="#f5f5f5"/>
            <stop offset="1" stop-color="#e9e9e9"/>
          </linearGradient>
        </defs>
        <rect width="100%" height="100%" fill="url(#g)"/>
        <text x="50%" y="50%" text-anchor="middle" fill="#999" font-size="${fontSize}">${text}</text>
      </svg>
    `
    : `
      <svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}">
        <rect width="100%" height="100%" fill="${bgColor}"/>
        <text x="50%" y="50%" text-anchor="middle" fill="#999" font-size="${fontSize}">${text}</text>
      </svg>
    `;
  
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svgContent)}`;
};

/**
 * 预设的兜底图片配置
 */
export const FALLBACK_PRESETS = {
  // 小尺寸 - 用于右上角推荐
  small: {
    width: '80',
    height: '60',
    fontSize: '10'
  },
  // 中等尺寸 - 用于列表项
  medium: {
    width: '160',
    height: '120',
    fontSize: '14'
  },
  // 大尺寸 - 用于主要展示
  large: {
    width: '240',
    height: '180',
    fontSize: '16'
  },
  // 全尺寸 - 用于主图展示
  full: {
    width: '100%',
    height: '100%',
    fontSize: '16'
  }
};

/**
 * 获取预设的兜底图片
 * @param {string} preset - 预设名称
 * @param {string} text - 自定义文字
 * @returns {string} SVG数据URL
 */
export const getFallbackImage = (preset = 'medium', text = '图片加载失败') => {
  const config = FALLBACK_PRESETS[preset] || FALLBACK_PRESETS.medium;
  return generateFallbackImage({
    ...config,
    text
  });
};

/**
 * 创建图片错误处理函数
 * @param {Object} options - 配置选项
 * @returns {Function} 错误处理函数
 */
export const createImageErrorHandler = (options = {}) => {
  return (e) => {
    if (e && e.target) {
      e.target.onerror = null;
      e.target.src = generateFallbackImage(options);
    }
  };
};

// 预设的 onError 方法，便于直接复用
export const onErrorSmall = createImageErrorHandler({ width: '80', height: '60', fontSize: '10' });
export const onErrorMedium = createImageErrorHandler({ width: '160', height: '120', fontSize: '14' });
export const onErrorLarge = createImageErrorHandler({ width: '240', height: '180', fontSize: '16' });
export const onErrorFull = createImageErrorHandler({ width: '100%', height: '100%', fontSize: '16' });
