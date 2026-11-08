# 创作工作台策略（Hook 模型）

业务契约在 **本模块**，不在 `wldos-sdk-api`。商业会员限额由 `wldos-store-entitlements` 注册 Hook 增强。

## 调用链

```
SpaceController / KCMSService 写路径
  → WorkbenchPolicySupport
  → WorkbenchPolicyOrchestrator
       1. applyHandler(cms.workbench.contributeLimit, WorkbenchPolicyContext)
       2. 用量+1 与 effectiveMax 比较 → 403
       3. doInvoke(wldos.policy.enforce)   // agent 锚点
       4. doInvoke(cms.beforeCreateWork | cms.beforeCreateChapter)
```

前台限额：`GET` 空间接口内 `resolveWorkbenchLimits` → 同上 Hook 聚合，不触发 enforce/before。

## 常量与类型

| 类 | 职责 |
|----|------|
| `com.wldos.cms.policy.WorkbenchPolicyHooks` | action 与 extName 字符串 |
| `com.wldos.cms.policy.WorkbenchPolicyContext` | Hook 入参 `args[0]`；Handler 调用 `mergeLimit` / `deny` |
| `com.wldos.cms.policy.WorkbenchPolicyOrchestrator` | 聚合与断言 |
| `com.wldos.cms.policy.hook.ConfigWorkbenchLimitHookHandler` | 社区默认上限（无 entitlements JAR） |
| `com.wldos.cms.policy.hook.WorkbenchPolicyHookRegistrar` | 注册社区 Handler |

## 配置

```properties
wldos.cms.workbench.max-works=50
wldos.cms.workbench.max-chapters=500
```

旗舰运营实例以会员套餐权益为准（商业 Handler 覆盖配置语义）。

## 二开

1. 对 `cms.workbench.contributeLimit` 注册 `Handler`，priority 介于 100～900 之间按产品约定；在 Handler 内对 `WorkbenchPolicyContext` 做 `mergeLimit` 或 `deny`。
2. 对 `cms.beforeCreateWork` / `cms.beforeCreateChapter` 注册副作用 Hook（审计、扩展校验等）。

登记表：`docs/0_wldos动态sdk契约落地方案/action-extname-登记.md`。
