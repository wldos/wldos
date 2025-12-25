import React from 'react';
import {defineConfig} from 'umi';
import defaultSettings from './defaultSettings';
import proxy from './proxy';
import routes from './routes';

const {REACT_APP_ENV} = process.env;

export default defineConfig({
 // mfsu: {},
  hash: true,
  antd: {},
  dva: {
    hmr: true,
    immer: { enableES5: true }
  },
  targets: {
    ie: 11,
  },
  history: {
    type: 'browser',
  },
  locale: {
    default: 'zh-CN',
    antd: true,
    baseNavigator: true,
  },
  dynamicImport: {
    loading: '@/components/PageLoading/index',
  },
  routes,
  theme: {
    'primary-color': defaultSettings.primaryColor,
  },
  title: false,
  ignoreMomentLocale: true,
  proxy: proxy[REACT_APP_ENV || 'dev'],
  manifest: {
    basePath: '/',
  },
  // 配置 webpack，让插件路径能够像本地组件一样被处理
  chainWebpack(config, { env }) {
    // 允许动态导入（插件路径是运行时才过来的，webpack 无法在构建时打包）
    // 这样插件 UI 和主应用构建的 chunk 可以使用完全相同的加载逻辑
    config.module
      .set('exprContextCritical', false)
      .set('unknownContextCritical', false)
      .set('wrappedContextCritical', false);
    
    // 注意：对于动态变量路径，webpack 无法在构建时静态分析
    // - 本地组件路径 `@/pages/xxx/index`：webpack 可以静态分析（@/ 是别名），构建时打包
    // - 插件路径 `/plugins/...`：webpack 无法静态分析，运行时通过浏览器原生 import() 加载
    // 两者都使用相同的代码，webpack 会根据路径类型自动处理
    
    // 开发模式：动态配置插件别名（生产环境完全规避）
    if (env === 'development') {
      try {
        const pluginConfig = require('./pluginConfig');
        const aliases = pluginConfig.getPluginAliases();
        
        // 动态添加插件别名
        for (const [alias, pluginPath] of Object.entries(aliases)) {
          config.resolve.alias.set(alias, pluginPath);
        }
        
        // 添加插件源码目录到模块解析路径
        config.resolve.modules
          .add(require('path').resolve(__dirname, '../../../wldos-plugins'));
      } catch (error) {
        // 开发环境配置失败不影响构建，只打印警告
        console.warn('[config] 插件开发模式配置失败:', error.message);
      }
    }
  },
});
