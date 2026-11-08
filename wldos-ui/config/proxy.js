/**
 * 在生产环境 代理是无法生效的，所以这里没有生产环境的配置，生产环境需要在nginx或apache虚拟主机配置中配置和这里对应的反向代理。
 * 开发环境门户为 SPA（:8000）；爬虫直出 HTML 在现网 Apache + cms-portal，本地验 HTML 用 Bot UA 访问经 Apache 或直连 :8088。
 * The agent cannot take effect in the production environment
 * so there is no configuration of the production environment
 * For details, please see https://pro.ant.design/docs/deploy
 */
const port = 8088
export default {
  dev: {
      // 注意 api、store、plugins、mock-api 是关键前缀；插件静态资源走 /api/plugin-assets/**，与 /api 共用代理
      // 后端统一 API 前缀：/api
      '/api/' : {
          target: `http://localhost:${port}/api`,
          changeOrigin: true,
          pathRewrite: {
              '^/api/' : '',
          },
      },

    // wldos文件服务前缀
    '/store/' : {
      target: `http://localhost:${port}/store`,
      changeOrigin: true,
      pathRewrite: {
        '^/store/' : '',
      },
    },

    // 插件类路由 API前缀
    '/plugins/' : {
      target: `http://localhost:${port}/plugins`,
      changeOrigin: true,
      pathRewrite: {
        '^/plugins/' : '',
      },
    },

    // mock 专用 api 前缀（与真实 /api 区分）
    '/mock-api/' : {
      target: `http://localhost:${port}/mock-api`,
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
          target: `http://localhost:${port}/api`,
          changeOrigin: true,
          pathRewrite: {
              '^/api': '',
          },
      },
  },
};
