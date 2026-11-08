# wldos.org 门户静态首页（home/static-org）

- 前端组件：`src/pages/home/static-org/index.jsx`
- 扩展配置：`schemaType: portalStaticOrg`
- 区块 key：`hero`、`pillars`、`getStarted`、`contribute`、`techStack`

后台在 **域资源** 中设置：动态模板 = 绑定组件，指定 url = `home/static-org`，即可使用分区块 HTML 编辑器。

**定位**：开源站独立叙事，首页 **不** 引导至商业域；外链以 GitHub / Gitee / 本站文档为主。

## 国际化

- 文案键前缀：`portalOrg.*`，定义在 `src/locales/zh-CN|zh-TW|en-US/portalOrg.js`。
- 语言解析：`src/utils/runtimeLocale.js`（浏览器 locale → 简中/繁中/英文）。
- **关于、支持等子页** 为 CMS **page** 内容，需在创作工作台按语言维护，不在本模板内。
