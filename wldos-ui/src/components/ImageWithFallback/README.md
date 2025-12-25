# ImageWithFallback 统一图片组件

## 功能特性

- ✅ 统一的图片错误兜底处理
- ✅ 懒加载支持
- ✅ 多种预设尺寸
- ✅ 自定义兜底文字
- ✅ 响应式设计
- ✅ TypeScript 支持

## 基本用法

```jsx
import ImageWithFallback from '@/components/ImageWithFallback';

// 基本使用
<ImageWithFallback
  src="/path/to/image.jpg"
  alt="图片描述"
  width="200px"
  height="150px"
/>

// 使用预设尺寸
<ImageWithFallback
  src="/path/to/image.jpg"
  alt="图片描述"
  fallbackSize="medium" // small/medium/large/full
/>

// 自定义兜底文字
<ImageWithFallback
  src="/path/to/image.jpg"
  alt="图片描述"
  fallbackText="自定义错误提示"
  fallbackSize="large"
/>
```

## 预设尺寸

| 尺寸 | 宽度 | 高度 | 字体大小 | 适用场景 |
|------|------|------|----------|----------|
| small | 80px | 60px | 10px | 右上角推荐、小图标 |
| medium | 160px | 120px | 14px | 列表项、卡片封面 |
| large | 240px | 180px | 16px | 主要内容展示 |
| full | 100% | 100% | 16px | 主图展示、全屏显示 |

## 高级用法

```jsx
import { createImageErrorHandler, getFallbackImage } from '@/utils/imageUtils';

// 自定义错误处理
<ImageWithFallback
  src="/path/to/image.jpg"
  alt="图片描述"
  onError={createImageErrorHandler({
    width: '200',
    height: '150',
    fontSize: '14',
    text: '加载失败'
  })}
/>

// 获取兜底图片URL
const fallbackUrl = getFallbackImage('medium', '图片加载失败');
```

## 在现有项目中的迁移

### 替换前
```jsx
<img 
  src={cover}
  alt={title}
  loading="lazy"
  onError={(e) => {
    if (e && e.target) {
      e.target.onerror = null;
      e.target.src = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(`<svg>...</svg>`)}`;
    }
  }}
  style={{objectFit: 'cover', width: '100%', height: '120px'}}
/>
```

### 替换后
```jsx
<ImageWithFallback
  src={cover}
  alt={title}
  width="100%"
  height="120px"
  fallbackSize="medium"
/>
```

## 样式定制

组件会自动应用以下样式：
- `object-fit: cover` - 保持图片比例
- `transition: opacity 0.3s ease` - 平滑过渡
- 响应式尺寸调整

## 注意事项

1. 组件会自动处理图片加载失败的情况
2. 支持懒加载，提升页面性能
3. 兜底图片使用 SVG 格式，体积小且清晰
4. 所有预设都经过测试，确保在不同设备上显示正常
