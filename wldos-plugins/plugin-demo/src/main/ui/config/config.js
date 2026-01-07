import React from 'react';
import {defineConfig} from 'umi';
import path from 'path';
import defaultSettings from './defaultSettings';
import proxy from './proxy';
import routes from './routes';
// 从自动生成的配置文件读取插件信息（单一数据源：plugin.yml）
// 注意：使用相对路径，因为 config.js 在配置阶段路径别名可能未初始化
import { PLUGIN_CODE, PLUGIN_VERSION } from '../src/config/plugin';

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
  // 设置 publicPath 为子目录，这样插件的 chunk 会使用子目录路径
  publicPath: REACT_APP_ENV === 'production' 
    ? `/plugin-assets/${PLUGIN_CODE}/${PLUGIN_VERSION}/`
    : '/',
  
  // 关键：让插件使用主应用的 webpack runtime，而不是自己的
  // 方案：插件构建时不生成 webpack runtime（不生成 umi.js），只生成 chunk 文件
  // 插件的依赖（React、antd等）通过 externals 配置，使用主应用的模块
  // 这样插件的 chunk 中的依赖 ID 就是主应用的模块 ID，主应用的 webpack runtime 可以直接解析
  chainWebpack(config) {
    // 只在生产构建时配置 externals（作为插件加载时）
    // 开发模式下（独立运行）不配置 externals，让 webpack 打包所有依赖
    const isProduction = process.env.NODE_ENV === 'production';
    const isPluginBuild = process.env.BUILD_AS_PLUGIN === 'true';
    
    if (isProduction && isPluginBuild) {
      // 生产构建且作为插件：不生成自己的 webpack runtime，使用主应用的
      config.optimization.runtimeChunk(false);
      
      // 配置模块 ID 生成策略为 hashed（哈希）
      config.optimization.set('moduleIds', 'hashed');
      
      // 配置 externals，让插件的依赖使用主应用的模块
      config.externals({
        'react': {
          root: 'React',
          commonjs: 'react',
          commonjs2: 'react',
          amd: 'react',
        },
        'react-dom': {
          root: 'ReactDOM',
          commonjs: 'react-dom',
          commonjs2: 'react-dom',
          amd: 'react-dom',
        },
        'antd': {
          root: 'antd',
          commonjs: 'antd',
          commonjs2: 'antd',
          amd: 'antd',
        },
      });
    } else {
      // 开发模式或独立构建：正常打包所有依赖，不配置 externals
      // 这样插件可以独立运行，不依赖主应用
    }
  },
});

