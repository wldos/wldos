# Airdrop Plugin API 文档

## 📋 概述

本文档描述了 Airdrop Plugin 的所有 API 端点。所有 API 都遵循 RESTful 设计规范。

**基础路径**: `/plugins/airdrop` (由插件引擎自动添加)

---

## 🔗 API 端点列表

### 1. 任务管理 (TaskController)

**基础路径**: `/tasks`

| 方法 | 路径 | 描述 | Controller |
|------|------|------|------------|
| GET | `/tasks` | 获取任务列表（支持搜索和筛选） | SearchController |
| GET | `/tasks/{id}` | 获取任务详情 | TaskController |
| POST | `/tasks` | 创建任务 | TaskController |
| PUT | `/tasks/{id}` | 更新任务 | TaskController |
| DELETE | `/tasks/{id}` | 删除任务 | TaskController |
| POST | `/tasks/{id}/execute` | 执行任务 | TaskController |
| POST | `/tasks/{id}/stop` | 停止任务 | TaskController |
| POST | `/tasks/search` | 高级搜索任务 | SearchController |
| POST | `/tasks/filters/save` | 保存筛选条件 | SearchController |
| GET | `/tasks/filters` | 获取保存的筛选条件 | SearchController |

### 2. 批量操作 (BatchController)

**基础路径**: `/tasks/batch`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/tasks/batch/create` | 批量创建任务 |
| PUT | `/tasks/batch/update` | 批量更新任务 |
| DELETE | `/tasks/batch/delete` | 批量删除任务 |
| POST | `/tasks/batch/execute` | 批量执行任务 |
| POST | `/tasks/batch/stop` | 批量停止任务 |

### 3. 模板管理 (TemplateController)

**基础路径**: `/templates`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/templates` | 获取模板列表 |
| GET | `/templates/{id}` | 获取模板详情 |
| POST | `/templates` | 创建模板 |
| PUT | `/templates/{id}` | 更新模板 |
| DELETE | `/templates/{id}` | 删除模板 |

### 4. 执行记录 (ExecutionController)

**基础路径**: `/execution`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/execution` | 获取执行记录列表 |
| GET | `/execution/{id}` | 获取执行记录详情 |

### 5. Cron任务调度 (CronTaskController)

**基础路径**: `/cron/tasks`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/cron/tasks` | 获取所有Cron任务 |
| POST | `/cron/tasks` | 创建Cron任务 |
| PUT | `/cron/tasks/{taskId}/pause` | 暂停Cron任务 |
| PUT | `/cron/tasks/{taskId}/resume` | 恢复Cron任务 |
| DELETE | `/cron/tasks/{taskId}` | 删除Cron任务 |
| PUT | `/cron/tasks/{taskId}/update-cron` | 更新Cron表达式 |
| GET | `/cron/tasks/statistics` | 获取Cron任务统计 |

### 6. 任务依赖 (DependencyController)

**基础路径**: `/dependencies`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/dependencies` | 添加任务依赖关系 |
| DELETE | `/dependencies` | 移除任务依赖关系 |
| GET | `/dependencies/{taskId}` | 获取任务依赖 |
| POST | `/dependencies/execute/{rootTaskId}` | 执行任务链 |
| GET | `/dependencies/graph/{rootTaskId}` | 获取依赖图 |
| POST | `/dependencies/check-cycle` | 检查循环依赖 |
| GET | `/dependencies/status/{taskId}` | 获取任务状态 |
| GET | `/dependencies/history/{taskId}` | 获取任务执行历史 |

### 7. 脚本调试 (DebugController)

**基础路径**: `/debug`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/debug/sessions` | 创建调试会话 |
| GET | `/debug/sessions/{sessionId}` | 获取调试会话信息 |
| POST | `/debug/sessions/{sessionId}/breakpoints` | 设置断点 |
| DELETE | `/debug/sessions/{sessionId}/breakpoints/{lineNumber}` | 移除断点 |
| GET | `/debug/sessions/{sessionId}/breakpoints` | 获取断点列表 |
| POST | `/debug/sessions/{sessionId}/start` | 开始调试 |
| POST | `/debug/sessions/{sessionId}/continue` | 继续执行 |
| POST | `/debug/sessions/{sessionId}/step-over` | 单步执行（Step Over） |
| POST | `/debug/sessions/{sessionId}/step-into` | 单步进入（Step Into） |
| POST | `/debug/sessions/{sessionId}/step-out` | 单步跳出（Step Out） |
| POST | `/debug/sessions/{sessionId}/pause` | 暂停执行 |
| POST | `/debug/sessions/{sessionId}/stop` | 停止调试 |
| GET | `/debug/sessions/{sessionId}/variables` | 获取变量 |
| PUT | `/debug/sessions/{sessionId}/variables/{variableName}` | 设置变量 |
| GET | `/debug/sessions/{sessionId}/call-stack` | 获取调用栈 |

### 8. 脚本测试 (TestingController)

**基础路径**: `/tests`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/tests` | 创建测试用例 |
| GET | `/tests/{testId}` | 获取测试用例 |
| POST | `/tests/{testId}/execute` | 执行测试用例 |
| GET | `/tests/{testId}/result` | 获取测试结果 |
| POST | `/tests/batch/execute` | 批量执行测试 |
| POST | `/tests/report` | 生成测试报告 |

### 9. 性能分析 (PerformanceController)

**基础路径**: `/performance`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/performance/{taskId}/metrics` | 记录性能指标 |
| GET | `/performance/{taskId}/analyze` | 分析性能 |
| GET | `/performance/{taskId}/report` | 生成性能报告 |

### 10. 版本管理 (VersionController)

**基础路径**: `/tasks/{taskId}/versions`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/tasks/{taskId}/versions` | 创建脚本版本 |
| GET | `/tasks/{taskId}/versions` | 获取版本历史 |
| GET | `/tasks/{taskId}/versions/current` | 获取当前版本 |
| GET | `/tasks/{taskId}/versions/{versionNumber}` | 获取指定版本 |
| PUT | `/tasks/{taskId}/versions/{versionNumber}/set-current` | 设置当前版本 |
| GET | `/tasks/{taskId}/versions/compare` | 版本对比 |
| POST | `/tasks/{taskId}/versions/{versionNumber}/rollback` | 版本回滚 |
| DELETE | `/tasks/{taskId}/versions/{versionNumber}` | 删除版本 |

### 11. 脚本协作 (CollaborationController)

**基础路径**: `/collaboration/{taskId}`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/collaboration/{taskId}/session` | 创建协作会话 |
| POST | `/collaboration/{taskId}/join` | 加入协作会话 |
| POST | `/collaboration/{taskId}/leave` | 离开协作会话 |
| POST | `/collaboration/{taskId}/operation` | 应用操作 |
| POST | `/collaboration/{taskId}/lock` | 锁定行 |
| POST | `/collaboration/{taskId}/unlock` | 解锁行 |
| GET | `/collaboration/{taskId}/content` | 获取协作内容 |
| GET | `/collaboration/{taskId}/history` | 获取协作历史 |

### 12. 数据导出 (ExportController)

**基础路径**: `/export`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/export/tasks` | 导出任务列表（支持CSV/Excel/PDF） |
| GET | `/export/execution` | 导出执行记录 |

### 13. 邮件通知 (NotificationController)

**基础路径**: `/tasks/{taskId}/notification`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/tasks/{taskId}/notification/configure` | 配置任务通知规则 |
| GET | `/tasks/{taskId}/notification/rule` | 获取通知规则 |
| GET | `/tasks/{taskId}/notification/history` | 获取通知历史 |
| POST | `/tasks/{taskId}/notification/test` | 发送测试邮件 |

### 14. 任务统计 (StatisticsController)

**基础路径**: `/statistics`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/statistics` | 获取任务执行统计 |
| GET | `/statistics/charts` | 获取图表数据（用于ECharts） |

### 15. 任务进度 (ProgressController)

**基础路径**: `/tasks/{taskId}/progress`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/tasks/{taskId}/progress` | 获取任务进度 |
| PUT | `/tasks/{taskId}/progress` | 更新任务进度 |
| POST | `/tasks/{taskId}/progress/complete` | 完成任务进度 |

### 16. 模板市场 (MarketController)

**基础路径**: `/market`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/market/templates` | 上传模板到市场 |
| GET | `/market/templates/search` | 搜索模板 |
| GET | `/market/templates/{templateId}` | 获取模板详情 |
| POST | `/market/templates/{templateId}/download` | 下载模板 |
| POST | `/market/templates/{templateId}/rate` | 评分模板 |
| GET | `/market/categories` | 获取模板分类 |

### 17. 结果可视化 (VisualizationController)

**基础路径**: `/visualization`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/visualization/parse` | 解析并可视化结果 |

### 18. 自动更新 (AutoUpdateController)

**基础路径**: `/update`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/update/check` | 手动触发更新检查 |
| GET | `/update/history` | 获取更新历史 |
| GET | `/update/backups` | 获取更新备份列表 |
| POST | `/update/rollback` | 回滚更新 |

### 19. 配置管理 (ConfigController)

**基础路径**: `/configs`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/configs` | 获取所有配置 |
| GET | `/configs/type/{configType}` | 根据类型获取配置 |
| GET | `/configs/{configType}/{configKey}` | 获取配置值 |
| POST | `/configs` | 设置配置值 |
| GET | `/configs/bitbrowser` | 获取比特浏览器配置 |
| GET | `/configs/queue` | 获取队列配置 |
| POST | `/configs/init` | 初始化默认配置 |

### 20. 脚本向导 (ScriptWizardController)

**基础路径**: `/wizard`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/wizard/generate` | 通过向导配置生成脚本 |
| POST | `/wizard/create-task` | 通过向导配置生成脚本并创建任务 |
| POST | `/wizard/export` | 导出脚本 |
| GET | `/wizard/template` | 获取脚本向导配置模板 |

### 21. 权限管理 (PermissionController)

**基础路径**: `/permissions`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/permissions` | 获取所有权限列表 |
| POST | `/permissions/check` | 检查权限 |

### 22. 国际化 (I18nController)

**基础路径**: `/i18n`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/i18n/message` | 获取消息 |
| GET | `/i18n/locales` | 获取支持的语言列表 |

### 23. 健康检查 (HealthController)

**基础路径**: `/health`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/health` | 健康检查 |

---

## 📝 请求和响应格式

### 请求格式

所有 POST/PUT 请求的请求体应为 JSON 格式：

```json
{
  "field1": "value1",
  "field2": "value2"
}
```

### 响应格式

#### 成功响应

```json
{
  "success": true,
  "data": { ... },
  "message": "操作成功"
}
```

#### 错误响应

```json
{
  "success": false,
  "error": "错误信息",
  "message": "操作失败"
}
```

---

## 🔐 认证

所有 API 请求都需要通过插件引擎的认证机制。认证信息会自动添加到请求头中。

---

## 📊 状态码

| 状态码 | 描述 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 503 | 服务不可用（可选服务未启用） |

---

## 🚀 使用示例

### 创建任务

```javascript
const response = await fetch('/plugins/airdrop/tasks', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    taskName: '示例任务',
    scriptType: 'javascript',
    scriptContent: 'console.log("Hello World");'
  })
});

const result = await response.json();
```

### 执行任务

```javascript
const response = await fetch('/plugins/airdrop/tasks/123/execute', {
  method: 'POST'
});

const result = await response.json();
```

### 获取任务列表

```javascript
const response = await fetch('/plugins/airdrop/tasks?status=running&page=1&pageSize=10');
const result = await response.json();
```

---

## 📚 相关文档

- [Controller拆分完成总结](./Controller拆分完成总结.md)
- [Controller拆分计划](./Controller拆分计划.md)

---

**文档版本**: v1.0  
**最后更新**: 2025-12-28  
**维护者**: WLDOS Team

