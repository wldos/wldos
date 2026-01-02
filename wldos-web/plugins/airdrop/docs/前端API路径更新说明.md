# 前端API路径更新说明

## 📋 更新概述

由于后端Controller已按功能拆分，部分前端API路径需要更新以匹配新的Controller路径。

## ✅ 已更新的API路径

### 1. 脚本调试API (debugAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/debug/start` | `/debug/sessions` | 创建调试会话 |
| `/debug/{sessionId}/*` | `/debug/sessions/{sessionId}/*` | 所有调试操作 |

**更新内容**:
- `createDebugSession`: `/debug/start` → `/debug/sessions`
- `getDebugSession`: `/debug/{sessionId}` → `/debug/sessions/{sessionId}`
- 所有调试操作路径都添加了 `/sessions` 前缀

### 2. 脚本测试API (testAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/tests/test-cases` | `/tests` | 测试用例操作 |
| `/tests/execute-batch` | `/tests/batch/execute` | 批量执行 |
| `/tests/reports` | `/tests/report` | 测试报告（POST方法） |

**更新内容**:
- `createTestCase`: `/tests/test-cases` → `/tests`
- `getTestCase`: `/tests/{testId}` (新增)
- `executeTestCase`: `/tests/test-cases/{testId}/execute` → `/tests/{testId}/execute`
- `executeBatch`: `/tests/execute-batch` → `/tests/batch/execute`
- `generateTestReport`: `/tests/report` (新增POST方法)
- 保留了兼容方法 `getTestCases` 和 `getTestReports`

### 3. 性能分析API (performanceAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/performance/{taskId}/reports` | `/performance/{taskId}/report` | 性能报告 |

**更新内容**:
- `getPerformanceReport`: `/performance/{taskId}/reports` → `/performance/{taskId}/report`
- `analyzePerformance`: 移除了POST方法，改为GET方法

### 4. 脚本协作API (collaborationAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/collaboration/{taskId}/apply-change` | `/collaboration/{taskId}/operation` | 应用操作 |

**更新内容**:
- `createSession`: 添加了 `userId` 和 `userName` 参数
- `applyChange`: `/collaboration/{taskId}/apply-change` → `/collaboration/{taskId}/operation`
- 新增了 `lockLine` 和 `unlockLine` 方法

### 5. 版本管理API (versionAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/tasks/{taskId}/versions/{version1}/diff` | `/tasks/{taskId}/versions/compare` | 版本对比 |

**更新内容**:
- `compareVersions`: 改为使用查询参数 `?version1=...&version2=...`
- `createVersion`: 更新了参数结构，需要 `scriptContent` 等字段

### 6. 模板市场API (marketAPI)

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `/market/templates` | `/market/templates/search` | 搜索模板 |

**更新内容**:
- `getTemplates`: `/market/templates` → `/market/templates/search`
- `rateTemplate`: 添加了 `userId` 参数
- 新增了 `getCategories` 方法

## 📝 兼容性说明

为了保持向后兼容，部分API保留了旧的方法名，但内部会调用新的路径。建议逐步迁移到新的API调用方式。

## 🔄 迁移建议

1. **立即更新**: 调试、测试、性能分析、协作、版本管理、模板市场相关的API调用
2. **逐步迁移**: 其他API可以保持现状，逐步迁移
3. **测试验证**: 更新后请测试所有相关功能，确保正常工作

## 📚 相关文档

- [API文档](./API文档.md) - 完整的API端点列表
- [Controller拆分完成总结](./Controller拆分完成总结.md) - Controller拆分详情

---

**更新日期**: 2025-12-28  
**状态**: ✅ 前端API路径已更新

