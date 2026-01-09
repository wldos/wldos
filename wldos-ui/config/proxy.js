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
      // 注意wldos、store、api是关键前缀，业务api不要包含这些，可能会导致404
      // 后端api前缀：wldos mock专用api前缀
      '/wldos/' : {
          target: 'http://localhost:8088/wldos',
          changeOrigin: true,
          pathRewrite: {
              '^/wldos/' : '',
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
    // mock专用api前缀
    '/api/' : {
      target: 'http://localhost:8088/api',
      changeOrigin: true,
      pathRewrite: {
        '^/api/' : '',
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
      '/wldos': {
          target: 'http://localhost:8088/wldos',
          changeOrigin: true,
          pathRewrite: {
              '^/wldos': '',
          },
      },
  },
};
