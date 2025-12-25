import {
  disable as darkreaderDisable,
  enable as darkreaderEnable,
  setFetchMethod as setFetch,
} from '@umijs/ssr-darkreader';

const updateDarkTheme = async (dark = false) => {
  if (typeof window === 'undefined') return;
  if (typeof window.MutationObserver === 'undefined') return;

  if (dark) {
    const defaultTheme = {
      brightness: 100,
      contrast: 90,
      sepia: 10,
    };

    const defaultFixes = {
      invert: [],
      css: `
        /* ProLayout logo样式 - 覆盖所有可能的logo位置 */
        .ant-pro-global-header-logo img,
        .ant-pro-top-nav-header-logo img,
        .ant-pro-sider-logo img,
        .ant-layout-sider .ant-pro-sider-logo img,
        .ant-pro-layout .ant-pro-sider-logo img {
          filter: invert(1) brightness(1.2) contrast(1.1) !important;
          background: transparent !important;
          border-radius: 8px !important;
          padding: 4px !important;
        }
        .ant-pro-global-header-logo img:hover,
        .ant-pro-top-nav-header-logo img:hover,
        .ant-pro-sider-logo img:hover,
        .ant-layout-sider .ant-pro-sider-logo img:hover,
        .ant-pro-layout .ant-pro-sider-logo img:hover {
          transform: scale(1.05) !important;
          filter: invert(1) brightness(1.4) contrast(1.2) drop-shadow(0 0 8px rgba(255, 255, 255, 0.3)) !important;
        }
        /* 通用logo选择器 - 匹配所有包含wldos的图片 */
        img[src*="wldos"],
        img[alt*="WLDOS"],
        img[alt*="logo"] {
          filter: invert(1) brightness(1.2) contrast(1.1) !important;
          background: transparent !important;
          border-radius: 8px !important;
          padding: 4px !important;
          transition: all 0.3s ease !important;
        }
        img[src*="wldos"]:hover,
        img[alt*="WLDOS"]:hover,
        img[alt*="logo"]:hover {
          transform: scale(1.05) !important;
          filter: invert(1) brightness(1.4) contrast(1.2) drop-shadow(0 0 8px rgba(255, 255, 255, 0.3)) !important;
        }
        /* 针对SVG的特殊处理 - 确保SVG在暗黑模式下变白 */
        img[src*="wldos.svg"],
        img[src*="logo.svg"] {
          filter: invert(1) brightness(1.5) contrast(1.2) !important;
          -webkit-filter: invert(1) brightness(1.5) contrast(1.2) !important;
        }
        img[src*="wldos.svg"]:hover,
        img[src*="logo.svg"]:hover {
          filter: invert(1) brightness(1.8) contrast(1.3) drop-shadow(0 0 12px rgba(255, 255, 255, 0.5)) !important;
          -webkit-filter: invert(1) brightness(1.8) contrast(1.3) drop-shadow(0 0 12px rgba(255, 255, 255, 0.5)) !important;
        }
        /* BookView页面中的WLDOS logo亮灯效果 */
        .flow img[alt="WLDOS"],
        .flow img[src*="wldos.svg"] {
          filter: invert(1) brightness(1.2) contrast(1.1) !important;
          background: transparent !important;
          border-radius: 8px !important;
          padding: 4px !important;
          transition: all 0.3s ease !important;
        }
        .flow img[alt="WLDOS"]:hover,
        .flow img[src*="wldos.svg"]:hover {
          transform: scale(1.05) !important;
          filter: invert(1) brightness(1.4) contrast(1.2) drop-shadow(0 0 8px rgba(255, 255, 255, 0.3)) !important;
        }
      `,
      ignoreInlineStyle: [
        '.react-switch-handle', 
        '.ant-pro-global-header-logo img', 
        '.ant-pro-top-nav-header-logo img', 
        '.ant-pro-sider-logo img',
        '.ant-layout-sider .ant-pro-sider-logo img',
        '.ant-pro-layout .ant-pro-sider-logo img',
        'img[src*="wldos"]',
        'img[alt*="WLDOS"]',
        'img[alt*="logo"]',
        'img[src*="wldos.svg"]',
        'img[src*="logo.svg"]',
        '.flow img[alt="WLDOS"]', 
        '.flow img[src*="wldos.svg"]'
      ],
      ignoreImageAnalysis: [],
      disableStyleSheetsProxy: true,
    };
    if (window.MutationObserver && window.fetch) {
      setFetch(window.fetch);
      darkreaderEnable(defaultTheme, defaultFixes);
    }
  } else {
    if (window.MutationObserver) darkreaderDisable();
  }
};
export default updateDarkTheme;