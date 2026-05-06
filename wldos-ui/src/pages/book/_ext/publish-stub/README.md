# BookView 发布扩展点：开源 / 商业解耦边界

BookView 是开源（Apache 2.0）组件，但需要可选地接入"自媒体发布助手"商业能力。本目录是这两层之间的 **stub 实现**，与 commercial 端 `pages/commercial/social-publish/book-integration/` **同名 export 镜像**，由 webpack alias `@book-publish-ext` 在两侧切换。

## alias 切换点

`wldos-ui/config/config.js` 的 `chainWebpack`：

```js
config.resolve.alias.set(
  '@book-publish-ext',
  APP_FLAVOR === 'community'
    ? path.resolve(__dirname, '../src/pages/book/_ext/publish-stub')
    : path.resolve(__dirname, '../src/pages/commercial/social-publish/book-integration'),
);
```

## 接口契约

社区分支删除 `pages/commercial/` 整个目录后，BookView 的 11 个 import 仍能解析到本目录，构建/运行均不报错。

| BookView 引用 | commercial 真实实现 | 本 stub 兜底 |
|---|---|---|
| `@book-publish-ext/service` `{bindMyPublishAccount, queryMyPublishAccounts, querySupportedPlatforms}` | `book-integration/service.js`（facade） → `social-publish/service.js` | `service.js` 三个 noop |
| `@book-publish-ext/assistantPublishCore` `{DEFAULT_SCHEDULE_POLICY, loadDefaultPublishContext, calcScheduleRule, createAssistantPublishJobs, fetchAssistantJobsForSource}` | `book-integration/assistantPublishCore.js` | `assistantPublishCore.js` 同名 noop，常量同值 |
| `@book-publish-ext/components/PublishSettingsDrawer` | `book-integration/components/PublishSettingsDrawer.jsx` | `components/PublishSettingsDrawer.jsx` 返回 null |
| `@book-publish-ext/components/AccountManageModal` | `book-integration/components/AccountManageModal.jsx` | `components/AccountManageModal.jsx` 返回 null |
| `@book-publish-ext/components/PublishRecordModal` | `book-integration/components/PublishRecordModal.jsx` | `components/PublishRecordModal.jsx` 返回 null |

## 运行时双因子守护

stub 只是"打不进去也得能编译过"的兜底——真正阻止社区版用户碰到发布助手入口的，是 BookView 内的运行时守护：

```js
const PUBLISH_ASSISTANT_ENABLED = process.env.APP_FLAVOR !== 'community';
const assistantDesktopCommercial = useMemo(
  () => PUBLISH_ASSISTANT_ENABLED && isDesktopEmbedded(),
  [],
);
```

只有 `assistantDesktopCommercial=true` 时 BookView 才渲染发布按钮 / 发布抽屉等入口，因此 stub 的 noop 大概率永远不会被真正调用。stub 主要价值在于"webpack resolve 阶段不挂"，避免社区分支 `npm run build:community` 因 `Module not found` 失败。

## 维护规则

- 当 commercial 端在 `book-integration/` 增加新的 export，BookView 引用了它 —— **必须在本目录补对应同名 stub**，否则 community 构建会报 `export not found`
- stub 形参 / 返回值 shape 必须保持与 commercial 一致，避免 BookView 解构访问出错
- 不要让 stub 抛出意料之外的 error；功能不可用要么静默 noop（component 返回 null），要么 reject `'publish_assistant_unavailable'` 让上层 message.error 提示
- `wldos-ui/scripts/verify-flavor-alignment.js` 在 `community` 模式下会扫描 BookView 等开源页面，禁止再出现 `from '@/pages/commercial/'` 直引——CI 防回归
