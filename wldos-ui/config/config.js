/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

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
  // 移除 IE11 支持可减少约 15% 体积，如需支持 IE11 请取消注释
  // targets: { ie: 11 },
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
  // ========== 生产环境优化配置 ==========
  // 启用 terser 压缩，移除 console 和注释
  terserOptions: {
    compress: {
      drop_console: true,      // 移除 console
      drop_debugger: true,     // 移除 debugger
    },
    output: {
      comments: false,         // 移除注释
    },
  },
  // 配置 webpack
  chainWebpack(config, { env }) {
    // ========== 插件配置 ==========
    // 允许动态导入（插件路径是运行时才过来的，webpack 无法在构建时打包）
    config.module
      .set('exprContextCritical', false)
      .set('unknownContextCritical', false)
      .set('wrappedContextCritical', false);

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
