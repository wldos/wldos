import React from 'react';
import {defineConfig} from 'umi';
import path from 'path';
import defaultSettings from './defaultSettings';
import proxy from './proxy';
import routes from './routes';

const {REACT_APP_ENV} = process.env;

// 插件信息（从环境变量或配置文件获取）
const PLUGIN_CODE = 'plugin-demo'; // 插件编码
const PLUGIN_VERSION = process.env.PLUGIN_VERSION || '1.0.0'; // 插件版本

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
    // 插件不生成自己的 webpack runtime（不生成 umi.js）
    // 只生成 chunk 文件，使用主应用的 webpack runtime
    config.optimization.runtimeChunk(false);
    
    // 关键：配置模块 ID 生成策略为 hashed（哈希）
    // 在 webpack 4 中，hashed 可以确保相同路径的模块总是得到相同的 ID
    // 如果不配置，webpack 默认使用 natural（自然数），每次构建可能不同
    // 注意：webpack 5 支持 'deterministic'，但 UmiJS 使用的是 webpack 4
    config.optimization.set('moduleIds', 'hashed');
    
    // 配置 externals，让插件的依赖使用主应用的模块
    // 这样插件的 chunk 中不会包含这些依赖的代码，而是直接引用主应用的模块
    // 主应用的 webpack runtime 可以直接解析这些依赖
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
      // 其他依赖也可以配置 externals，使用主应用的模块
      // 但需要注意：主应用必须已经暴露了这些模块到全局变量
    });
  },
});

