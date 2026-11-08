/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import {defineConfig} from 'umi';
import defaultSettings from './defaultSettings';
import proxy from './proxy';
import routes from './routes';
import path from 'path';
import { applyFlavorExtensionAliases } from './flavorAliases';

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
    // 浏览器语言由 src/app.js locale.getLocale + runtimeLocale 统一解析，避免匹配到项目内 pt-BR 等片段文件
    baseNavigator: false,
  },
  dynamicImport: {
    loading: '@/components/PageLoading/index',
  },
  routes,
  theme: {
    'primary-color': defaultSettings.primaryColor,
  },
  /*
   * umi dev（webpack-dev-server）默认对代理响应也开 gzip 压缩中间件；SSE（/api/ai/invoke/stream）
   * 走同一条 /api/ 代理时，压缩中间件按内部缓冲区攒够数据才 flush 给浏览器，会把"逐 token 到达"
   * 的流式响应重新攒批成"等一段时间后一次性吐一大块"，前端表现为打字机光标不动、只看到 loading
   * 转圈，等一整段攒够了才突然出现一堆文字——与生产 nginx 缓冲同类问题，只是发生在本地开发代理层。
   * 生产环境 AiInvokeController 已加 X-Accel-Buffering 响应头；webpack-dev-server 压缩中间件不认该头，
   * dev 须单独关 compress，本地才能看到流式效果。内网 dev 关压缩无实际代价。
   */
  devServer: {
    compress: false,
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
    // 开源 / 商业 flavor 扩展点别名（统一入口，详见 ./flavorAliases.js）：
    //   清单驱动；未显式 APP_FLAVOR 时按"商业目录是否存在"自动选择，社区源码下默认
    //   npm start/dev/build 能直接跑（自动 fallback 到 community stub + warn）。
    //   商业分支因 commercial 目录均在，命中真实路径，行为完全不变。
    applyFlavorExtensionAliases(config, { uiRoot: path.resolve(__dirname, '..') });

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
