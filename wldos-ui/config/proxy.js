/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * 在生产环境 代理是无法生效的，所以这里没有生产环境的配置，生产环境需要在nginx或apache虚拟主机配置中配置和这里对应的反向代理
 * The agent cannot take effect in the production environment
 * so there is no configuration of the production environment
 * For details, please see https://pro.ant.design/docs/deploy
 */

export default {
  dev: {
      // 注意 api、store、plugins、mock-api 是关键前缀，业务 api 不要包含这些，可能会导致 404
      // 后端统一 API 前缀：/api
      '/api/' : {
          target: 'http://localhost:8088/api',
          changeOrigin: true,
          pathRewrite: {
              '^/api/' : '',
          },
      },
    // wldos文件服务前缀
    '/store/' : {
      target: 'http://localhost:8088/store',
      changeOrigin: true,
      pathRewrite: {
        '^/store/' : '',
      },
    },
    // 插件类路由 API前缀
    '/plugins/' : {
      target: 'http://localhost:8088/plugins',
      changeOrigin: true,
      pathRewrite: {
        '^/plugins/' : '',
      },
    },
    // mock 专用 api 前缀（与真实 /api 区分）
    '/mock-api/' : {
      target: 'http://localhost:8088/mock-api',
      changeOrigin: true,
      pathRewrite: {
        '^/mock-api/' : '',
      },
    },
  },
  test: {
    '/test/': {
      target: 'http://localhost:8000',
      changeOrigin: true,
      pathRewrite: {
        '^': '',
      },
    },
  },
  pre: {
      '/api': {
          target: 'http://localhost:8088/api',
          changeOrigin: true,
          pathRewrite: {
              '^/api': '',
          },
      },
  },
};
