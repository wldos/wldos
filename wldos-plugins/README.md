本模块对应wldos平台的插件模块，官方插件默认在此模块下，第三方插件也可以参考demo独立创建项目，保证发行时插件的zip包放在wldos发行包的plugins目录下。

## 📋 快速导航

- [插件API网关与前后端交互规范](#插件api网关与前后端交互规范) - 了解插件API路径映射和调用规范
- [插件配置约定](#插件配置约定) - 插件配置文件和权限设置
- [UI目录约定](#ui目录约定) - 前端UI开发和构建规范
- [数据库脚本管理](#数据库脚本管理) - 数据库脚本编写和管理
- [前端插件配置 (pluginConfig)](#前端插件配置-pluginconfig---2025-10-28-更新-) ⭐ - pluginConfig 和 providedDependencies 用法（2025-10-28 新增）
- [插件注册路径说明](#插件注册路径说明-) ⭐ - 新路径注册和旧路径兼容性（2025-10-28 新增）
- [最佳实践](#最佳实践) - 开发建议和最佳实践

### 🔗 核心规范快速链接
- [前端API调用规范](#前端调用规范) - 基于统一路由前缀的API定义
- [权限配置格式](#权限配置) - API权限的配置方法
- [完整开发示例](#完整开发示例) - 端到端开发示例
- [插件注册路径说明](#插件注册路径说明-) ⭐ - 新路径 vs 旧路径（2025-10-28 新增）  
```plaintext
wldos主应用发行包结构
wldos.zip
├── wldos.war
├── plugins/
│   ├── xxx-plugin.zip
│   └── ...
└── store/
└── ...
```
插件结构：

```plaintext
# 构建前的插件模块结构（开发时）
demo-plugin/
├── plugin.yml                       # 插件配置文件（与pom.xml平行）
├── pom.xml                          # Maven配置
├── build-ui.sh（或者build-ui.bat）   # 构建脚本
├── README.md                        # 说明文档
└── src/
    ├── main/
    │   ├── java/                    # Java源代码
    │   ├── resources/               # 资源文件目录
    │   │   ├── application.yml      # 应用配置（可选）
    │   │   ├── i18n/                # 国际化资源（可选）
    │   │   │   ├── messages_zh_CN.properties
    │   │   │   └── messages_en_US.properties
    │   │   ├── icons/             # 图标文件（可选）
    │   │   │   ├── images/          # 展示图片（可选）
    │   │   │   └── docs/            # 文档文件（可选）
    │   ├── database/                # 数据库脚本目录
    │   │   ├── create-tables.sql    # 创建表脚本
    │   │   ├── drop-tables.sql      # 删除表脚本
    │   │   └── upgrade-*.sql        # 升级脚本（可选）
    │   └── ui/                      # UI源码目录
    │       ├── config/              # UmiJS 配置文件
    │       │   ├── routes.js        # 路由配置（必需）
    │       │   ├── config.js        # UmiJS 主配置
    │       │   └── defaultSettings.js
    │       ├── src/                 # 前端源码
    │       │   ├── components/      # 组件目录（首字母大写）
    │       │   ├── pages/          # 页面目录（小写）
    │       │   └── utils/          # 工具函数
    │       ├── package.json         # 依赖管理
    │       ├── rollup.config.js    # Rollup 构建配置（实际使用）
    │       ├── README.md           # UI说明文档
    │       └── dist/               # 构建输出目录
    │           └── esm/            # ESM 格式输出
    │               └── *.js        # 各页面的 ESM 模块文件
    └── assembly/
        └── package.xml              # 打包配置

# 构建后的插件包结构（安装时）
demo-plugin.zip
├── plugin.yml                       # 插件配置文件
├── demo-plugin.jar                  # 插件主程序包
├── ui/                              # 插件UI目录（构建后的UMD文件）
│   └── index.js                     # UMD格式的UI模块文件
├── icons/                          # 图标目录
│   ├── demo-16x16.png             # 小图标
│   ├── demo-32x32.png             # 中图标
│   └── demo-64x64.png             # 大图标
├── images/                         # 展示图片目录
│   ├── screenshot1.png            # 功能截图1
│   ├── screenshot2.png            # 功能截图2
│   └── banner.png                 # 横幅图片
├── database/                      # 数据库脚本目录
│   ├── create-tables.sql         # 创建表脚本
│   ├── drop-tables.sql           # 删除表脚本
│   └── upgrade-*.sql             # 升级脚本（可选）
└── docs/                          # 文档目录
    ├── index.md                   # 主文档
    └── CHANGELOG.md               # 变更日志
```

docs/index.md:
```markdown
# WLDOS演示插件

## 简介
这是一个WLDOS框架的演示插件，用于展示插件的基本功能和使用方法。

## 功能特性
- 功能1：XXX
- 功能2：XXX
- 功能3：XXX

## 使用说明
1. 安装插件
2. 配置参数
3. 启用插件

## API文档
...
```

docs/CHANGELOG.md:
```markdown
# 更新日志

## [1.0.0] - 2024-03-21
### 新增
- 初始版本发布
- 基础功能实现
- 完整文档支持

### 修复
- 无

### 变更
- 无
```

图片资源建议：
1. icons/
   - demo-16x16.png: 浅色系简洁图标，适合菜单显示
   - demo-32x32.png: 中等大小，用于工具栏
   - demo-64x64.png: 大图标，用于插件市场展示

2. images/
   - screenshot1.png: 主要功能界面截图 (建议 1280x720)
   - screenshot2.png: 配置界面截图 (建议 1280x720)
   - banner.png: 市场展示横幅 (建议 1920x320)

## UI目录约定

### 目录结构说明

#### 构建前的UI源码目录（开发时）
插件的UI源码位于 `src/main/ui/` 目录下，包含所有开发相关的文件：

#### 必需文件
- `config/routes.js`: 路由配置文件，定义页面路由（必需）
- `package.json`: 依赖管理文件（必需）
- `rollup.config.js`: Rollup 构建配置文件（实际使用）
- `src/pages/`: 页面组件目录（必需）

#### 构建后的UI目录（安装时）
构建完成后，UI文件会被打包到插件的 `ui/` 目录下，包含 ESM 格式的模块文件。

### UI构建方式

实际插件使用 Rollup 构建 ESM 格式的模块文件：

#### ESM模块构建（实际使用）
- **构建输出**: 多个ESM模块文件（`dist/esm/*.js`），每个页面一个文件
- **文件格式**: ESM (ES Module)
- **依赖管理**: 外部依赖注入（React、ReactDOM、Ant Design）
- **优势**: 
  - 按需加载，只加载当前页面需要的模块
  - 文件小、加载快
  - 依赖统一管理
  - 支持代码分割和懒加载

### Rollup配置示例

#### ESM模块构建配置（实际使用）
实际插件使用 Rollup 构建 ESM 格式的模块文件，支持多入口构建：

```javascript
// rollup.config.js
import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import babel from '@rollup/plugin-babel';
import globals from './rollup-plugin-globals.js';
import routesConfig from './config/routes.js';

// 从路由配置生成多入口
const inputs = {};
routesConfig.forEach(route => {
  if (route.component) {
    const componentPath = route.component.replace(/^\.\//, '');
    inputs[componentPath] = `src/pages/${componentPath}/index.jsx`;
  }
});

export default {
  input: inputs,
  output: {
    dir: 'dist/esm',
    format: 'es',
    entryFileNames: '[name].js',
    globals: {
      'react': 'React',
      'react-dom': 'ReactDOM',
      'antd': 'antd'
    }
  },
  external: ['react', 'react-dom', 'antd'],
  plugins: [
    resolve(),
    commonjs(),
    babel({ babelHelpers: 'bundled' }),
    globals()
  ]
};
```

**配置说明**：
- 使用 Rollup 构建 ESM 格式的模块文件
- 支持多入口构建，为每个页面生成独立的 ESM 文件
- 输出到 `dist/esm/` 目录，文件名为 `[name].js`（如 `scheduler.js`、`tasks.js`）
- 外部依赖（React、ReactDOM、antd）由主应用提供
- 路由配置在 `config/routes.js` 中定义

### 路由配置格式
`config/routes.js` 文件定义插件的页面路由，与 `plugin.yml` 中的 `menus` 配置对应：

```javascript
// config/routes.js
const routes = [
  {
    path: '/admin/your/home',      // 对应 plugin.yml 中的 menus[].path
    component: './home',            // 对应 src/pages/home/index.jsx
  },
  {
    path: '/admin/your/list',
    component: './list',            // 对应 src/pages/list/index.jsx
  },
];

export default routes;
```

**说明**：
- `path` 必须与 `plugin.yml` 中 `menus[].path` 一致
- `component` 使用相对路径 `./` 表示 `src/pages/` 目录
- Rollup 会根据路由配置生成对应的 ESM 文件

### 构建输出

#### 开发时的构建输出
- **ESM模块构建**: 构建后的文件输出到 `src/main/ui/dist/esm/` 目录下，每个页面生成独立的 ESM 文件（如 `scheduler.js`、`tasks.js`）

#### 安装时的UI目录
构建完成后，Maven会将 `dist/esm/` 目录下的文件复制到插件的 `ui/esm/` 目录中：
- 插件包中的 `ui/esm/` 目录包含所有页面的 ESM 模块文件
- 系统会根据路由配置动态加载对应的 ESM 模块

### 权限配置
插件扩展支持权限控制，权限标识必须与后端权限系统保持一致。

### 开发建议

#### Rollup ESM 构建的优势
1. **按需加载**: 只加载当前页面需要的模块，提升性能
2. **统一依赖管理**: 外部依赖由主应用提供，避免版本冲突
3. **更好的缓存策略**: 每个页面独立文件，便于缓存管理
4. **代码分割**: 自动代码分割，减少初始加载时间
5. **开发体验**: 与主应用使用相同的 UmiJS 框架，开发体验一致

#### 构建脚本示例
```bash
#!/bin/bash
# build-ui.sh

cd src/main/ui
yarn install
yarn build

echo "UI构建完成！"
echo "构建输出: src/main/ui/dist/esm/*.js"
```

#### 插件打包配置
```xml
<!-- src/assembly/package.xml -->
<fileSet>
    <directory>${basedir}/src/main/ui/dist</directory>
    <outputDirectory>ui</outputDirectory>
    <includes>
        <include>**/*</include>
    </includes>
    <useDefaultExcludes>false</useDefaultExcludes>
    <fileMode>0644</fileMode>
    <directoryMode>0755</directoryMode>
</fileSet>
```

## 构建说明

- **构建工具**: Rollup（实际使用）
- **输出格式**: ESM (ES Module)
- **多入口构建**: 为每个页面生成独立的 ESM 文件
- **路由配置**: 在 `config/routes.js` 中定义，与 `plugin.yml` 中的菜单路径对应
- **页面组件**: 位于 `src/pages/{pageName}/index.jsx`
- **构建输出**: `dist/esm/{pageName}.js`

### 最佳实践

#### 1. 项目结构
```
demo_plugin
├── plugin.yml          # 插件配置（包含 menus 和 hooks）
├── pom.xml             # Maven配置
├── build-ui.sh（或build-ui.bat）   # 构建脚本
└── src/main/
    ├── java/           # Java源代码
    ├── resources/      # 资源文件
    │   ├── application.yml
    │   └── i18n/       # 国际化资源（可选）
    ├── database/       # 数据库脚本目录
    │   ├── create-tables.sql
    │   ├── drop-tables.sql
    │   └── upgrade-*.sql
    └── ui/             # UI源码目录
        ├── config/     # UmiJS 配置
        │   └── routes.js  # 路由配置（必需）
        ├── src/        # 前端源码
        │   ├── components/  # 组件目录（首字母大写）
        │   ├── pages/      # 页面目录（小写）
        │   └── utils/      # 工具函数
        ├── package.json    # 依赖配置
        ├── rollup.config.js # Rollup 构建配置
        └── dist/       # 构建输出目录
            └── esm/    # ESM 格式输出
                └── *.js  # 各页面的 ESM 模块文件
```

#### 2. 页面组件导出
每个页面组件在 `src/pages/{pageName}/index.jsx` 中定义：

```javascript
// src/pages/home/index.jsx
import React from 'react';
import { Card, Button } from 'antd';

export const HomePage = () => {
  return (
    <Card>
      <h1>首页</h1>
      <Button type="primary">操作</Button>
    </Card>
  );
};

// 默认导出（Rollup 会将其构建为 ESM 模块）
export default HomePage;
```

**说明**：
- 每个页面组件独立文件，位于 `src/pages/{pageName}/index.jsx`
- 使用 `export default` 导出组件
- Rollup 会为每个页面生成独立的 ESM 文件（`dist/esm/{pageName}.js`）
- 系统会根据路由配置动态加载对应的 ESM 模块

#### 3. 路由配置
路由配置在 `config/routes.js` 中定义，与 `plugin.yml` 中的 `menus` 配置对应：

```javascript
// config/routes.js
const routes = [
  {
    path: '/admin/demo/home',      // 对应 plugin.yml 中的 menus[].path
    component: './home',            // 对应 src/pages/home/index.jsx
  },
  {
    path: '/admin/demo/list',
    component: './list',            // 对应 src/pages/list/index.jsx
  },
];

export default routes;
```

#### 4. 开发流程
1. 在 `plugin.yml` 中配置菜单（`menus`）和钩子（`hooks`）
2. 在 `config/routes.js` 中配置路由，对应 `plugin.yml` 中的菜单路径
3. 在 `src/pages/` 中开发页面组件
4. 运行 `build-ui.sh` 或 `build-ui.bat` 构建UI
5. 运行 `mvn package` 打包插件
6. 安装测试

### 故障排除

#### 常见问题
1. **模块加载失败**: 检查 ESM 格式和路由配置
2. **页面未找到**: 确保路由配置与 `plugin.yml` 中的菜单路径一致
3. **依赖冲突**: 使用外部依赖配置，避免打包核心库
4. **构建失败**: 检查Node.js版本和依赖安装
5. **路由不匹配**: 确保 `config/routes.js` 中的 `path` 与 `plugin.yml` 中的 `menus[].path` 完全一致

#### 调试技巧
- 使用浏览器开发者工具检查网络请求
- 查看控制台错误信息
- 验证构建输出文件格式（ESM 模块）
- 检查路由配置是否与菜单配置一致
- 测试 ESM 模块加载和页面渲染

## 插件API网关与前后端交互规范

### API路径映射机制

WLDOS插件系统采用统一的API网关机制，实现插件前后端的透明交互。所有插件API请求都会通过主应用的网关进行路由和转发。

#### 路径映射规则

**外部访问路径** → **插件内部路径**
```
/plugins/{pluginCode}/{endpoint} → /plugins/{pluginCode}/{endpoint}
```

**具体示例**：
```
/plugins/airdrop/configs     → /plugins/airdrop/configs
/plugins/airdrop/tasks       → /plugins/airdrop/tasks
/plugins/test/status         → /plugins/test/status
```

#### 插件Controller规范

插件Controller必须遵循以下规范：

```java
@RestController
@RequestMapping("/plugins/airdrop")  // 插件内部路径，必须以/plugins/{pluginCode}开头
public class AirdropController {
    
    @GetMapping("/configs")
    public ResponseEntity<List<AirdropConfig>> getAllConfigs() {
        // 插件API实现
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<AirdropTask> createTask(@RequestBody CreateTaskRequest request) {
        // 插件API实现
    }
}
```

#### 前端调用规范

前端调用插件API时，必须使用外部访问路径。插件UI模块的API定义基于约定的路由前缀：

**1. API基础路径定义**：
```javascript
// 插件UI模块中的API基础路径定义
const API_BASE = '/plugins/airdrop';
```

**2. 统一API调用函数**：
```javascript
// 使用qi框架的request工具（推荐方式）
const request = window.request;

if (!request) {
  throw new Error('request工具未找到，请确保框架已正确加载');
}

// API调用函数 - 直接使用框架的request工具
const apiCall = (url, options = {}) => {
  return request(`${API_BASE}${url}`, options);
};

// 或者使用原生fetch方式（备选方案）
const apiCallWithFetch = async (url, options = {}) => {
  try {
    const response = await fetch(`${API_BASE}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    });
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('API调用失败:', error);
    throw error;
  }
};
```

**3. 模块化API定义**：
```javascript
// 任务相关API
const taskAPI = {
  // 获取任务列表
  getTasks: () => apiCall('/tasks'),
  
  // 创建任务
  createTask: (task) => apiCall('/tasks', {
    method: 'POST',
    data: task  // 使用data而不是body，框架会自动处理JSON序列化
  }),
  
  // 更新任务
  updateTask: (id, task) => apiCall(`/tasks/${id}`, {
    method: 'PUT',
    data: task
  }),
  
  // 删除任务
  deleteTask: (id) => apiCall(`/tasks/${id}`, {
    method: 'DELETE'
  }),
  
  // 执行任务
  executeTask: (id) => apiCall(`/tasks/${id}/execute`, {
    method: 'POST'
  })
};

// 模板相关API
const templateAPI = {
  getTemplates: () => apiCall('/templates'),
  createTemplate: (template) => apiCall('/templates', {
    method: 'POST',
    data: template
  }),
  updateTemplate: (id, template) => apiCall(`/templates/${id}`, {
    method: 'PUT',
    data: template
  }),
  deleteTemplate: (id) => apiCall(`/templates/${id}`, {
    method: 'DELETE'
  })
};
```

**4. 组件中的API使用**：
```javascript
// React组件中使用API
export const AirdropTasks = () => {
  const [tasks, setTasks] = React.useState([]);
  const [loading, setLoading] = React.useState(false);

  // 加载任务列表
  const loadTasks = async () => {
    try {
      setLoading(true);
      const data = await taskAPI.getTasks();
      setTasks(data);
    } catch (error) {
      message.error('加载任务列表失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 删除任务
  const handleDeleteTask = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除任务 "${record.taskName}" 吗？`,
      async onOk() {
        try {
          await taskAPI.deleteTask(record.id);
          message.success('任务删除成功');
          loadTasks(); // 重新加载列表
        } catch (error) {
          message.error('删除任务失败: ' + error.message);
        }
      },
    });
  };

  // 组件渲染...
};
```

**5. 完整的API路径映射**：
```
前端调用路径                    → 后端处理路径
/plugins/airdrop/tasks     → /plugins/airdrop/tasks
/plugins/airdrop/templates → /plugins/airdrop/templates
/plugins/airdrop/execution → /plugins/airdrop/execution
```

#### 权限验证

插件API请求会经过以下权限验证：
1. **JWT令牌验证**：验证用户身份和令牌有效性
2. **域名验证**：验证请求来源域名
3. **插件API权限验证**：通过`AuthService.verifyReqAuth`验证具体权限

#### 错误处理

插件API统一错误响应格式：

```json
{
    "status": 400,
    "message": "错误描述信息",
    "data": null,
    "success": false
}
```

常见错误码：
- `400`：请求参数错误
- `401`：未授权（JWT无效）
- `403`：权限不足
- `404`：插件或API不存在
- `500`：服务器内部错误

#### 开发注意事项

1. **路径约定**：
   - 插件内部使用 `/plugins/{pluginCode}/...` 格式
   - 外部访问使用 `/plugins/{pluginCode}/...` 格式
   - 网关会自动进行路径转换

2. **请求头要求**：
   - `Authorization: Bearer <jwt-token>`：必需
   - `X-Wldos-Domain: <domain>`：必需
   - `Content-Type: application/json`：POST/PUT请求必需

3. **响应格式**：
   - 使用 `ResponseEntity<T>` 包装响应
   - 统一使用JSON格式
   - 包含适当的HTTP状态码

4. **权限配置**：
   - 在 `plugin.yml` 中配置API权限
   - 权限代码格式：`{pluginCode}:api:{endpoint}`
   - 示例：`airdrop:api:configs`、`airdrop:api:tasks`

#### 完整开发示例

以下是一个完整的插件API开发示例：

**1. 插件Controller实现**：
```java
@RestController
@RequestMapping("/plugins/airdrop")
@Api(tags = "脚本插件API")
@Slf4j
public class AirdropController {
    
    @Autowired
    private AirdropService airdropService;
    
    @GetMapping("/configs")
    @ApiOperation("获取所有配置")
    public ResponseEntity<List<AirdropConfig>> getAllConfigs() {
        try {
            List<AirdropConfig> configs = airdropService.getAllConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/tasks")
    @ApiOperation("创建脚本任务")
    public ResponseEntity<AirdropTask> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        try {
            AirdropTask task = airdropService.createTask(request);
            return ResponseEntity.ok(task);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("创建任务失败", e);
            return ResponseEntity.status(500).build();
        }
    }
}
```

**2. 权限配置**：
```yaml
permissions:
  - code: airdrop:api:configs
    name: 访问配置API
    description: 允许访问脚本插件配置相关API
  
  - code: airdrop:api:tasks
    name: 访问任务API
    description: 允许访问脚本插件任务相关API
```

**3. 前端UI模块实现**：
```javascript
// src/pages/tasks/index.jsx
import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Space, Tag, Modal, message } from 'antd';
import { taskAPI } from '../../utils/api';

export const AirdropTasks = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);

  // 加载任务列表
  const loadTasks = async () => {
    try {
      setLoading(true);
      const data = await taskAPI.getTasks();
      setTasks(data);
    } catch (error) {
      message.error('加载任务列表失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 删除任务
  const handleDeleteTask = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除任务 "${record.taskName}" 吗？`,
      async onOk() {
        try {
          await taskAPI.deleteTask(record.id);
          message.success('任务删除成功');
          loadTasks();
        } catch (error) {
          message.error('删除任务失败: ' + error.message);
        }
      },
    });
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const columns = [
    { title: '任务名称', dataIndex: 'taskName', key: 'taskName' },
    { title: '任务类型', dataIndex: 'taskType', key: 'taskType' },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (status) => {
        const statusMap = {
          'pending': { color: 'orange', text: '待执行' },
          'running': { color: 'blue', text: '运行中' },
          'completed': { color: 'green', text: '已完成' },
          'failed': { color: 'red', text: '失败' }
        };
        const statusInfo = statusMap[status] || { color: 'default', text: status };
        return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
      }
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
    { title: '操作', key: 'action',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => handleViewTask(record)}>查看</Button>
          <Button type="link" size="small" onClick={() => handleEditTask(record)}>编辑</Button>
          <Button type="link" size="small" danger onClick={() => handleDeleteTask(record)}>删除</Button>
        </Space>
      )
    },
  ];

  return (
    <Card>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={handleCreateTask}>新建任务</Button>
        <Button style={{ marginLeft: 8 }} onClick={loadTasks}>刷新</Button>
      </div>
      <Table 
        columns={columns} 
        dataSource={tasks} 
        rowKey="id" 
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条记录`
        }}
      />
    </Card>
  );
};

// 默认导出（Rollup 会将其构建为 ESM 模块）
export default AirdropTasks;
```

**4. API访问路径**：
- 外部访问：`GET /plugins/airdrop/configs`
- 内部处理：`GET /plugins/airdrop/configs`
- 权限验证：`airdrop:api:configs`

### 插件配置文件

插件配置文件必须遵循严格的格式规范，系统会在安装时进行校验。详细模板请参考：[plugin-config-template.yml](./plugin-config-template.yml)

**重要提示：**
- 字段名必须严格按照模板使用，常见错误如 `pluginCode`、`pluginName` 等会被系统检测并报错
- 所有必需字段不能为空
- 字段格式必须符合规范（如ID格式、版本号格式等）

#### plugin.yml 配置模板
```yaml
# 插件基本信息
code: demo-plugin                         # 插件编码（必须唯一）
name: 演示插件                          # 插件名称
version: 1.0.0                          # 插件版本
description: 这是一个演示插件           # 插件描述
author: WLDOS Team                      # 作者信息
mainClass: com.wldos.plugin.demo.DemoPlugin  # 插件主类

# 数据库配置
database:
  tables:
    - name: demo_user                    # 表名（会自动添加 plugin_ 前缀）
      description: 用户表               # 表描述
      version: 1.0.0                    # 表版本
    - name: demo_config                  # 配置表
      description: 配置表
      version: 1.0.0

# 权限配置
permissions:
  # 查看权限 - 分配给所有角色
  - code: demo:view
    name: 查看演示模块
    description: 允许查看演示模块主界面
  
  # 创建权限 - 分配给注册用户及以上角色
  - code: demo:create
    name: 创建数据
    description: 允许创建新的数据记录
  
  # 编辑权限 - 分配给管理员及以上角色
  - code: demo:edit
    name: 编辑数据
    description: 允许编辑现有数据记录
  
  # 删除权限 - 分配给租户管理员及以上角色
  - code: demo:delete
    name: 删除数据
    description: 允许删除数据记录
  
  # API权限 - 用于控制插件API访问
  # 权限代码格式：{pluginCode}:api:{endpoint}
  # 示例：demo:api:configs 表示允许访问 /plugins/demo/configs 接口
  - code: demo:api:configs
    name: 访问配置API
    description: 允许访问插件配置相关API
  
  - code: demo:api:tasks
    name: 访问任务API
    description: 允许访问插件任务相关API
  
  - code: demo:api:status
    name: 访问状态API
    description: 允许访问插件状态相关API

# 菜单配置
# 插件菜单路径约定：
# - 用户侧菜单：/pluginCode/xxx
# - 子菜单：/pluginCode/xxx/.../
# - 管理侧菜单：/admin/pluginCode/xxx
# - 管理侧子菜单：/admin/pluginCode/xxx/.../
#
# 菜单配置规范（2025-11-01 更新）：
# 1. 顶级菜单：仅作为模块占用和角色授权资源占位，不可点击（点击用于展开/折叠子菜单）
#    - 如果有子菜单：可以不配置 component（作为占位菜单）
#    - 如果没有子菜单：必须配置 component（作为首页菜单）
# 2. 第一个子菜单：作为首页，路径指向顶级菜单的路径，使用首页组件
#    - 必须配置 component 字段（除非是按钮类型）
# 3. 菜单层级：支持最多3级（包含顶级菜单），即：顶级菜单 -> 子菜单 -> 子子菜单
# 4. 子菜单可以有子菜单，用于构建更细粒度的功能模块
# 5. component 字段验证规则：
#    - 顶级菜单有子菜单：可以不配置 component
#    - 顶级菜单无子菜单：必须配置 component
#    - 子菜单：必须配置 component（按钮类型除外）
menus:
  - code: demo-root
    name: 演示模块
    path: /demo
    icon: ExperimentOutlined
    sort: 100
    resourceType: admin_plugin_menu
    # 注意：顶级菜单不配置 component，仅作占位和权限控制
    children:
      # 第一个子菜单作为首页，路径指向顶级菜单路径
      - code: demo-home
        name: 首页
        path: /demo  # 指向顶级菜单路径
        component: DemoHome  # 使用首页组件
        sort: 0
        resourceType: admin_plugin_menu
      
      - code: demo-list
        name: 数据列表
        path: /demo/list
        component: DemoList
        sort: 1
        resourceType: admin_plugin_menu
        # 子菜单可以有子菜单（最多3级）
        children:
          - code: demo-list:view
            name: 查看列表
            path: /demo/list/view
            sort: 1
            resourceType: admin_plugin_button
            method: GET
      
      - code: demo-config
        name: 配置管理
        path: /demo/config
        component: DemoConfig
        sort: 2
        resourceType: admin_plugin_menu
```

#### plugin.properties 配置模板（替代 plugin.yml）
```properties
# 插件基本信息
id=demo-plugin
name=演示插件
version=1.0.0
description=这是一个演示插件
author=WLDOS Team
mainClass=com.wldos.plugin.demo.DemoPlugin

# 数据库配置
database.tables=demo_user,demo_config
database.table.demo_user.description=用户表
database.table.demo_user.version=1.0.0
database.table.demo_config.description=配置表
database.table.demo_config.version=1.0.0

# 权限配置
permissions=demo:view,demo:create,demo:edit,demo:delete
permission.demo:view.name=查看演示模块
permission.demo:view.description=允许查看演示模块主界面
permission.demo:create.name=创建数据
permission.demo:create.description=允许创建新的数据记录
permission.demo:edit.name=编辑数据
permission.demo:edit.description=允许编辑现有数据记录
permission.demo:delete.name=删除数据
permission.demo:delete.description=允许删除数据记录

# 菜单配置
# 插件菜单路径约定：
# - 用户侧菜单：/pluginCode/xxx
# - 子菜单：/pluginCode/xxx/.../
# - 管理侧菜单：/admin/pluginCode/xxx
# - 管理侧子菜单：/admin/pluginCode/xxx/.../
menus=demo-root,demo-list,demo-config
menu.demo-root.name=演示模块
menu.demo-root.path=/demo
menu.demo-root.icon=ExperimentOutlined
menu.demo-root.sort=100
menu.demo-root.permissions=demo:view
menu.demo-list.name=数据列表
menu.demo-list.path=/demo/list
menu.demo-list.icon=UnorderedListOutlined
menu.demo-list.parentId=demo-root
menu.demo-list.sort=1
menu.demo-list.permissions=demo:view
menu.demo-config.name=配置管理
menu.demo-config.path=/demo/config
menu.demo-config.icon=SettingOutlined
menu.demo-config.parentId=demo-root
menu.demo-config.sort=2
menu.demo-config.permissions=demo:edit
```

### Maven 配置模板

#### pom.xml 配置模板
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.wldos.plugin</groupId>
    <artifactId>demo-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>WLDOS演示插件</name>
    <description>WLDOS平台演示插件</description>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <wldos.version>1.0.0</wldos.version>
    </properties>

    <dependencies>
        <!-- WLDOS框架依赖 -->
        <dependency>
            <groupId>com.wldos</groupId>
            <artifactId>wldos-framework</artifactId>
            <version>${wldos.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- WLDOS平台依赖 -->
        <dependency>
            <groupId>com.wldos</groupId>
            <artifactId>wldos-platform</artifactId>
            <version>${wldos.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- 数据库相关 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- 工具类 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- 资源文件处理 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- 打包插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <descriptors>
                        <descriptor>src/assembly/package.xml</descriptor>
                    </descriptors>
                    <finalName>${project.artifactId}-${project.version}</finalName>
                    <appendAssemblyId>false</appendAssemblyId>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Assembly 打包配置模板

#### src/assembly/package.xml 配置模板
```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.1.0 
          http://maven.apache.org/xsd/assembly-2.1.0.xsd">
    
    <id>plugin</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>

    <fileSets>
                 <!-- 插件配置文件 -->
         <fileSet>
             <directory>${basedir}</directory>
             <outputDirectory>/</outputDirectory>
             <includes>
                 <include>plugin.yml</include>
                 <include>plugin.properties</include>
             </includes>
             <useDefaultExcludes>false</useDefaultExcludes>
             <fileMode>0644</fileMode>
         </fileSet>
         
         <!-- 注意：Hook配置在 plugin.yml 的 hooks 节中，不需要单独的 hooks.json 文件 -->

        <!-- 插件JAR包 -->
        <fileSet>
            <directory>${project.build.directory}</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>${project.artifactId}-${project.version}.jar</include>
            </includes>
            <useDefaultExcludes>false</useDefaultExcludes>
            <fileMode>0644</fileMode>
        </fileSet>

        <!-- UI构建文件 -->
        <fileSet>
            <directory>${basedir}/src/main/ui/dist</directory>
            <outputDirectory>ui</outputDirectory>
            <includes>
                <include>**/*</include>
            </includes>
            <useDefaultExcludes>false</useDefaultExcludes>
            <fileMode>0644</fileMode>
            <directoryMode>0755</directoryMode>
        </fileSet>
        <!-- 注意：实际构建输出为 dist/esm/*.js，会被复制到 ui/esm/ 目录 -->

        <!-- 图标文件 -->
        <fileSet>
            <directory>${basedir}/src/main/resources/icons</directory>
            <outputDirectory>icons</outputDirectory>
            <includes>
                <include>**/*</include>
            </includes>
            <useDefaultExcludes>false</useDefaultExcludes>
            <fileMode>0644</fileMode>
            <directoryMode>0755</directoryMode>
        </fileSet>

        <!-- 展示图片 -->
        <fileSet>
            <directory>${basedir}/src/main/resources/images</directory>
            <outputDirectory>images</outputDirectory>
            <includes>
                <include>**/*</include>
            </includes>
            <useDefaultExcludes>false</useDefaultExcludes>
            <fileMode>0644</fileMode>
            <directoryMode>0755</directoryMode>
        </fileSet>

        <!-- 文档文件 -->
        <fileSet>
            <directory>${basedir}/src/main/resources/docs</directory>
            <outputDirectory>docs</outputDirectory>
            <includes>
                <include>**/*</include>
            </includes>
            <useDefaultExcludes>false</useDefaultExcludes>
            <fileMode>0644</fileMode>
            <directoryMode>0755</directoryMode>
        </fileSet>

                 <!-- 数据库脚本 -->
         <fileSet>
             <directory>${basedir}/src/main/database</directory>
             <outputDirectory>database</outputDirectory>
             <includes>
                 <include>**/*.sql</include>
             </includes>
             <useDefaultExcludes>false</useDefaultExcludes>
             <fileMode>0644</fileMode>
             <directoryMode>0755</directoryMode>
         </fileSet>
    </fileSets>
</assembly>
```

# 数据库脚本管理

## 脚本目录约定

插件的数据库脚本必须放在 `src/main/database/` 目录下，系统会自动检测并执行相应的脚本。

### 脚本文件命名约定

- **`create-tables.sql`** - 创建表脚本（必需，如果插件需要数据库）
- **`drop-tables.sql`** - 删除表脚本（必需，如果插件需要数据库）
- **`init-data.sql`** - 初始化数据脚本（可选）
- **`upgrade-{version}.sql`** - 升级脚本（可选，如 `upgrade-1.1.0.sql`）
- **`uninstall.sql`** - 卸载脚本（可选）

### 脚本执行时机

- **安装时**：执行 `create-tables.sql` 和 `init-data.sql`
- **升级时**：执行对应的 `upgrade-{version}.sql`
- **卸载时**：先执行 `drop-tables.sql`（如果存在），然后自动清理所有相关表

### 脚本编写规范

- 使用 `CREATE TABLE IF NOT EXISTS` 和 `DROP TABLE IF EXISTS`
- 表名会自动添加 `plugin_{pluginCode}_` 前缀
- 支持 `{t:logicName}` 通配符进行动态表名解析
- 使用 `/* */` 注释格式，避免 `--` 单行注释

### 示例脚本

#### create-tables.sql
```sql
/* 插件演示表创建脚本 */
/* 表名会自动添加 plugin_demo-plugin_ 前缀 */

CREATE TABLE IF NOT EXISTS plugin_demo_plugin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演示用户表';
```

#### drop-tables.sql
```sql
/* 插件演示表删除脚本 */
/* 按照依赖关系顺序删除表 */

DROP TABLE IF EXISTS plugin_demo_plugin_config;
DROP TABLE IF EXISTS plugin_demo_plugin_user;
```

#### upgrade-1.1.0.sql
```sql
/* 插件演示升级脚本 v1.0.0 -> v1.1.0 */

/* 为用户表添加新字段 */
ALTER TABLE plugin_demo_plugin_user 
ADD COLUMN phone VARCHAR(20) COMMENT '手机号' AFTER email,
ADD COLUMN avatar VARCHAR(255) COMMENT '头像URL' AFTER phone;

/* 添加索引 */
ALTER TABLE plugin_demo_plugin_user 
ADD INDEX idx_phone (phone);
```

## 重要说明

1. **配置文件不再涉及数据库**：插件的 `plugin.yml` 配置文件中不再需要 `database` 配置节，系统会自动检测 `database/` 目录
2. **自动表名前缀**：所有表名会自动添加 `plugin_{pluginCode}_` 前缀，防止与系统表冲突
3. **智能脚本检测**：系统会自动检测脚本文件的存在性，不存在时跳过相关操作
4. **事务保证**：所有数据库操作都在事务中执行，确保数据一致性
5. **兜底清理**：即使卸载脚本失败，系统也会自动清理所有相关表

### 配置约定说明

#### 1. 插件菜单路径约定
插件菜单路径必须遵循以下约定，系统会根据路径自动识别插件编码和菜单类型：

**用户侧菜单路径约定：**
- 主菜单：`/pluginCode/xxx`
- 子菜单：`/pluginCode/xxx/.../`
- 示例：`/airdrop/tasks`、`/airdrop/templates`、`/airdrop/scheduler`

**管理侧菜单路径约定：**
- 主菜单：`/admin/pluginCode/xxx`
- 子菜单：`/admin/pluginCode/xxx/.../`
- 示例：`/admin/airdrop/settings`、`/admin/airdrop/users`

**路径规则说明：**
- `pluginCode`：插件编码，必须与插件配置文件中的 `code` 字段一致
- `xxx`：具体的功能模块名称
- `...`：多级子菜单路径
- 系统会根据路径的第一段（用户侧）或第二段（管理侧）自动提取插件编码

**菜单层级规范（2025-11-01 更新）：**
- **层级限制**：支持最多3级菜单（包含顶级菜单），即：顶级菜单 -> 子菜单 -> 子子菜单
- **顶级菜单**：仅作为模块占用和角色授权资源占位，不配置 `component`，不可点击（点击用于展开/折叠子菜单）
  - **如果有子菜单**：可以不配置 `component`（作为占位菜单）
  - **如果没有子菜单**：必须配置 `component`（作为首页菜单）
- **第一个子菜单**：作为首页，路径指向顶级菜单的路径，使用首页组件，**必须配置 `component` 字段**（除非是按钮类型）
- **子菜单嵌套**：子菜单可以有子菜单，用于构建更细粒度的功能模块
- **component 字段验证**：系统会根据菜单层级自动验证：
  - 顶级菜单有子菜单：可以不配置 `component`
  - 顶级菜单无子菜单：必须配置 `component`
  - 子菜单：必须配置 `component`（按钮类型除外）

**路径示例（2025-11-01 更新）：**
```yaml
# 用户侧菜单示例（遵循新规范）
menus:
  - code: airdrop-main
    name: 脚本管理
    path: /airdrop                    # 顶级菜单路径（仅占位，比子级菜单少一级目录，相当于前缀）
    icon: RocketOutlined
    sort: 100
    resourceType: plugin_menu
    # 注意：顶级菜单不配置 component，仅作占位和权限控制
    children:
      # 第一个子菜单作为首页，路径指向顶级菜单路径
      - code: airdrop-home
        name: 首页
        path: /airdrop/home                # 指向顶级菜单路径
        component: AirdropLayout      # 使用首页组件
        sort: 0
        resourceType: plugin_menu
      
      - code: airdrop-tasks
        name: 任务管理
        path: /airdrop/tasks          # 子菜单
        component: AirdropTasks
        sort: 1
        resourceType: plugin_menu
        # 子菜单可以有子菜单（最多3级）
        children:
          - code: airdrop-tasks:view
            name: 查看任务
            path: /airdrop/tasks/view
            sort: 1
            resourceType: plugin_button
            method: GET
      
      - code: airdrop-templates
        name: 模板管理
        path: /airdrop/templates      # 子菜单
        component: AirdropTemplates
        sort: 2
        resourceType: plugin_menu

# 管理侧菜单示例（遵循新规范）
menus:
  - code: airdrop-admin-main
    name: 脚本设置
    path: /admin/airdrop/settings     # 顶级菜单路径（仅占位）
    icon: SettingOutlined
    sort: 100
    resourceType: admin_plugin_menu
    # 注意：顶级菜单不配置 component，仅作占位和权限控制
    children:
      # 第一个子菜单作为首页，路径指向顶级菜单路径
      - code: airdrop-admin-home
        name: 首页
        path: /admin/airdrop/settings # 指向顶级菜单路径
        component: AirdropSettings    # 使用首页组件
        sort: 0
        resourceType: admin_plugin_menu
      
      - code: airdrop-admin-users
        name: 用户管理
        path: /admin/airdrop/users    # 子菜单
        component: AirdropUsers
        sort: 1
        resourceType: admin_plugin_menu
```

**前端识别机制：**
- 用户侧：前端 `DynamicRouter` 组件会根据 `resourceType === 'plugin_menu'` 识别用户侧插件菜单
- 管理侧：前端 `AdminDynamicRouter` 组件会根据 `resourceType === 'admin_plugin_menu'` 识别管理侧插件菜单
- 从路径中提取插件编码：
  - 用户侧：`/pluginCode/xxx` → `pluginCode`
  - 管理侧：`/admin/pluginCode/xxx` → `pluginCode`
- 动态加载对应的插件UI组件

#### 2. 插件配置文件
- **plugin.yml**: 推荐使用YAML格式，结构清晰，易于维护
- **plugin.properties**: 替代方案，适合简单配置
- **配置验证**: 系统会自动验证配置文件的完整性和正确性

#### 2. 数据库脚本管理
- **脚本位置**: 数据库脚本必须放在 `src/main/database/` 目录下
- **脚本打包**: 脚本会被打包到插件的 `database/` 目录中，系统会从插件目录读取
- **脚本命名**: 
  - `create-tables.sql`: 创建表脚本（必需）
  - `drop-tables.sql`: 删除表脚本（必需）
  - `upgrade-{version}.sql`: 升级脚本（可选，如 `upgrade-1.1.0.sql`）
- **脚本执行**: 系统会在插件安装、升级、卸载时自动执行相应脚本
- **安全验证**: 系统会验证脚本安全性，确保只操作插件自己的表

#### 3. 数据库表命名
- **前缀规则**: 所有表名会自动添加 `plugin_{id}_` 前缀
- **示例**: 配置中的 `demo_user` 实际表名为 `plugin_demo-plugin_user`
- **安全机制**: 防止插件表与系统表冲突

#### 4. 权限配置
- **权限代码**: 使用 `插件ID:操作` 格式，如 `demo:view`
- **权限层级**: 按角色分配不同级别的权限
- **权限验证**: 系统会自动验证权限配置的有效性

#### 5. 菜单配置

**基本规范：**
- **菜单代码**: 使用有意义的代码，如 `demo-root`，遵循 `插件编码:功能` 格式
- **菜单层级**: 支持最多3级（包含顶级菜单），通过 `children` 建立菜单层级关系
- **顶级菜单**: 仅作为模块占用和角色授权资源占位，不配置 `component`，不可点击
- **第一个子菜单**: 作为首页，路径指向顶级菜单的路径，使用首页组件
- **图标支持**: 支持Ant Design图标库图标
- **资源类型**: 
  - `admin_plugin_menu`: 管理侧插件菜单
  - `plugin_menu`: 用户侧插件菜单
  - `admin_plugin_button`: 管理侧插件按钮权限
  - `plugin_button`: 用户侧插件按钮权限

**component 字段验证规则（2025-11-01 更新）：**
系统会根据菜单层级和类型自动验证 `component` 字段：

1. **顶级菜单（第一层菜单）**：
   - 如果有子菜单：**可以不配置 `component`**（作为占位菜单，仅用于模块占用和权限控制）
   - 如果没有子菜单：**必须配置 `component`**（作为首页菜单，直接显示组件）

2. **子菜单（第二层及以下）**：
   - **必须配置 `component`**（除非是按钮类型资源）
   - 按钮类型资源（`resourceType` 包含 `button`）：不需要 `component` 字段

3. **验证规则总结**：
   ```yaml
   # ✅ 正确示例1：顶级菜单有子菜单，可以不配置 component
   - code: plugin-root
     name: 插件模块
     path: /admin/plugin
     resourceType: admin_plugin_menu
     # 注意：顶级菜单有子菜单时，可以不配置 component
     children:
       - code: plugin-home
         name: 首页
         path: /admin/plugin  # 指向顶级菜单路径
         component: PluginHome  # 子菜单必须配置 component
         resourceType: admin_plugin_menu
   
   # ✅ 正确示例2：顶级菜单没有子菜单，必须配置 component
   - code: simple-menu
     name: 简单菜单
     path: /admin/simple
     component: SimplePage  # 顶级菜单没有子菜单时，必须配置 component
     resourceType: admin_plugin_menu
   
   # ❌ 错误示例：子菜单缺少 component
   - code: parent-menu
     name: 父菜单
     path: /admin/parent
     resourceType: admin_plugin_menu
     children:
       - code: child-menu
         name: 子菜单
         path: /admin/parent/child
         # 错误：子菜单必须配置 component
         resourceType: admin_plugin_menu
   ```

**验证错误提示：**
- 如果顶级菜单没有子菜单且缺少 `component`，系统会提示：`菜单配置[X]缺少 'component' 字段（顶级菜单如果没有子菜单，必须配置 component 作为首页组件）`
- 如果子菜单缺少 `component`（且不是按钮类型），系统会提示：`菜单配置[X]缺少 'component' 字段（子菜单必须配置 component，除非是按钮类型）`

#### 6. 打包配置
- **文件组织**: 按功能模块组织文件结构
- **权限设置**: 设置合适的文件权限
- **依赖管理**: 确保所有必要文件都包含在插件包中

### 最佳实践

#### 1. 配置管理
- 使用版本控制管理配置文件
- 提供配置模板和示例
- 添加详细的配置说明文档

#### 2. 依赖管理
- 最小化依赖，只包含必要的库
- 使用 `provided` 作用域避免重复打包
- 定期更新依赖版本

#### 3. 打包优化
- 只打包必要的文件
- 优化文件大小
- 确保文件结构清晰

#### 4. 数据库脚本最佳实践
- **目录结构**: 将 `database/` 目录放在 `src/main/` 下，避免打包到JAR中
- **脚本编写**: 使用 `CREATE TABLE IF NOT EXISTS` 和 `DROP TABLE IF EXISTS`
- **版本管理**: 每个版本升级都要提供对应的升级脚本
- **回滚支持**: 升级脚本要考虑回滚的可能性
- **数据迁移**: 升级时注意数据迁移和兼容性
- **测试验证**: 在测试环境验证脚本执行效果

#### 5. API开发最佳实践
- **统一响应格式**：使用 `ResponseEntity<T>` 包装所有API响应
- **错误处理**：提供详细的错误信息和适当的HTTP状态码
- **参数验证**：使用 `@Valid` 注解验证请求参数
- **权限控制**：为每个API端点配置相应的权限
- **文档注释**：使用 `@ApiOperation` 等注解提供API文档
- **测试覆盖**：编写单元测试和集成测试

```java
@RestController
@RequestMapping("/plugins/airdrop")
@Api(tags = "脚本插件API")
public class AirdropController {
    
    @GetMapping("/configs")
    @ApiOperation("获取所有配置")
    @PreAuthorize("hasPermission('airdrop:api:configs')")
    public ResponseEntity<List<AirdropConfig>> getAllConfigs() {
        try {
            List<AirdropConfig> configs = configService.findAll();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Collections.emptyList());
        }
    }
    
    @PostMapping("/tasks")
    @ApiOperation("创建脚本任务")
    @PreAuthorize("hasPermission('airdrop:api:tasks')")
    public ResponseEntity<AirdropTask> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        try {
            AirdropTask task = taskService.createTask(request);
            return ResponseEntity.ok(task);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

#### 6. 前端API调用最佳实践

**基于约定路由前缀的API定义规范**：

插件UI模块的API定义必须遵循以下规范：

1. **API基础路径约定**：
   ```javascript
   // 插件UI模块中的API基础路径定义
   const API_BASE = '/plugins/{pluginCode}';
   ```

2. **统一API调用函数**：
   ```javascript
   // 使用框架的request工具（推荐方式）
   const request = window.request;
   
   if (!request) {
     throw new Error('request工具未找到，请确保框架已正确加载');
   }
   
   // API调用函数 - 直接使用框架的request工具
   const apiCall = (url, options = {}) => {
     return request(`${API_BASE}${url}`, options);
   };
   
   // 或者使用原生fetch方式（备选方案）
   const apiCallWithFetch = async (url, options = {}) => {
     try {
       const response = await fetch(`${API_BASE}${url}`, {
         headers: {
           'Content-Type': 'application/json',
           ...options.headers
         },
         ...options
       });
       
       if (!response.ok) {
         throw new Error(`HTTP ${response.status}: ${response.statusText}`);
       }
       
       return await response.json();
     } catch (error) {
       console.error('API调用失败:', error);
       throw error;
     }
   };
   ```

3. **模块化API定义**：
   ```javascript
   // 按功能模块组织API
   const taskAPI = {
     getTasks: () => apiCall('/tasks'),
     createTask: (task) => apiCall('/tasks', {
       method: 'POST',
       data: task  // 使用data，框架会自动处理JSON序列化
     }),
     updateTask: (id, task) => apiCall(`/tasks/${id}`, {
       method: 'PUT',
       data: task
     }),
     deleteTask: (id) => apiCall(`/tasks/${id}`, {
       method: 'DELETE'
     })
   };
   
   const templateAPI = {
     getTemplates: () => apiCall('/templates'),
     createTemplate: (template) => apiCall('/templates', {
       method: 'POST',
       data: template
     })
   };
   ```

4. **组件中的API使用**：
   ```javascript
   // React组件中使用API
   export const AirdropTasks = () => {
     const [tasks, setTasks] = React.useState([]);
     const [loading, setLoading] = React.useState(false);

     const loadTasks = async () => {
       try {
         setLoading(true);
         const data = await taskAPI.getTasks();
         setTasks(data);
       } catch (error) {
         message.error('加载任务列表失败: ' + error.message);
       } finally {
         setLoading(false);
       }
     };

     // 组件挂载时加载数据
     React.useEffect(() => {
       loadTasks();
     }, []);

     // 其他业务逻辑...
   };
   ```

5. **错误处理和加载状态**：
   ```javascript
   // 统一的错误处理
   const handleApiError = (error, defaultMessage = '操作失败') => {
     console.error('API错误:', error);
     message.error(error.message || defaultMessage);
   };

   // 带加载状态的API调用
   const callWithLoading = async (apiCall, setLoading) => {
     try {
       setLoading(true);
       return await apiCall();
     } catch (error) {
       handleApiError(error);
       throw error;
     } finally {
       setLoading(false);
     }
   };
   ```

6. **API路径映射表**：
   ```
   前端调用路径                    → 后端处理路径
   /plugins/airdrop/tasks     → /plugins/airdrop/tasks
   /plugins/airdrop/templates → /plugins/airdrop/templates
   /plugins/airdrop/execution → /plugins/airdrop/execution
   ```

**开发建议**：
- **使用框架工具**：优先使用框架提供的`window.request`工具，自动处理认证、错误等
- **统一封装**：使用统一的API调用函数，避免重复代码
- **模块化组织**：按功能模块组织API，便于维护
- **数据格式**：使用`data`字段传递数据，框架会自动处理JSON序列化
- **错误处理**：框架request工具已内置错误处理，无需手动处理HTTP状态码
- **加载状态**：显示API请求的加载状态，提升用户体验
- **类型安全**：使用TypeScript时，为API响应定义类型接口

**框架request工具优势**：
- 自动添加认证头（Authorization、X-Wldos-Domain等）
- 统一错误处理和响应解析
- 支持请求拦截器和响应拦截器
- 自动处理JSON序列化和反序列化
- 内置重试机制和超时处理

## 前端插件配置 (pluginConfig) - 2025-10-28 更新 ⭐

> **重要更新**: 本文档于 2025-10-28 新增了 `pluginConfig.providedDependencies` 的详细说明，用于处理插件自身提供的依赖。

### pluginConfig 基本结构

`pluginConfig` 是插件的元数据配置对象，包含插件的基本信息、组件声明和依赖声明。

```javascript
const pluginConfig = {
  // 基础信息
  name: '插件名称',
  version: '1.0.0',
  description: '插件描述',
  author: '作者名称',
  
  // 组件声明
  components: {
    Layout: LayoutComponent,
    Page: PageComponent,
  },
  
  // 插件提供的依赖声明（2025-10-28 新增）
  providedDependencies: {
    'lodash': {
      value: _,
      version: '4.17.21'  // 推荐：提供版本信息用于冲突检测
    }
  }
};
```

### providedDependencies 用法 ⭐ 2025-10-28 新增

#### 使用场景

当插件打包了自己的依赖（**未通过 webpack externals 排除**）时，需要在 `pluginConfig.providedDependencies` 中声明。

**适用场景**：
- 插件打包了第三方库（如 lodash、moment、echarts）
- 插件打包了特定版本的依赖
- 插件希望向其他插件或主应用提供共享的依赖

#### 声明格式

支持两种格式：

**格式 1：详细格式（推荐）**

```javascript
import _ from 'lodash';
import moment from 'moment';

const pluginConfig = {
  name: 'MyPlugin',
  version: '1.0.0',
  providedDependencies: {
    'lodash': {
      value: _,
      version: '4.17.21'  // 提供版本信息，用于冲突检测
    },
    'moment': {
      value: moment,
      version: '2.29.4'
    }
  },
  Components: { /* ... */ }
};
```

**格式 2：简单格式（如果不需要版本信息）**

```javascript
import _ from 'lodash';
import moment from 'moment';

const pluginConfig = {
  name: 'MyPlugin',
  version: '1.0.0',
  providedDependencies: {
    'lodash': _,      // 简单格式，不提供版本信息
    'moment': moment
  },
  Components: { /* ... */ }
};
```

#### 自动注册机制

插件加载后，系统会**自动发现并注册** `pluginConfig.providedDependencies` 中声明的依赖，无需手动调用 API。

**工作流程**：
1. 插件代码执行完成后，系统检查 `pluginConfig.providedDependencies`
2. 自动调用 `runtime.registerProvidedDependency()` 注册每个依赖
3. 如果依赖已存在，进行冲突检测和共享处理

**优势**：
- ✅ 零配置：只需在 `pluginConfig` 中声明，自动发现并注册
- ✅ 代码内聚：依赖声明与代码在一起，易于维护
- ✅ 版本控制：支持版本信息，便于冲突检测
- ✅ 前端自主：不依赖后端改动

#### 依赖冲突处理 ⭐ 2025-10-28 新增

当多个插件提供了相同的依赖时，系统会进行智能处理：

**场景 1：版本相同 - 依赖共享**
```javascript
// 插件 A
providedDependencies: {
  'lodash': { value: _, version: '4.17.21' }
}

// 插件 B
providedDependencies: {
  'lodash': { value: _, version: '4.17.21' }  // 相同版本
}

// 结果：插件 B 会共享插件 A 的 lodash，避免重复注册
// 控制台输出：依赖共享: lodash 已由插件 A 提供，插件 B 将复用该依赖
```

**场景 2：版本不同 - 冲突警告**
```javascript
// 插件 A
providedDependencies: {
  'lodash': { value: _, version: '4.17.21' }
}

// 插件 B
providedDependencies: {
  'lodash': { value: _, version: '5.0.0' }  // 不同版本
}

// 结果：检测到版本冲突，发出警告，但继续使用第一个注册的版本
// 控制台警告：依赖冲突: lodash 已由插件 A (v4.17.21) 提供，插件 B 尝试注册 v5.0.0
```

#### 完整示例

```javascript
// src/pages/home/index.jsx
import React, { useState, useEffect } from 'react';
import { Card, Table, Button, message } from 'antd';
import { homeAPI } from '../../utils/api';

// 页面组件
const HomePage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadData = async () => {
    try {
      setLoading(true);
      const res = await homeAPI.getData();
      setData(res?.data || []);
    } catch (error) {
      message.error('加载数据失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <Card>
      <h1>首页</h1>
      <Table 
        dataSource={data} 
        loading={loading}
        rowKey="id"
      />
    </Card>
  );
};

// 默认导出（Rollup 会将其构建为 ESM 模块）
export default HomePage;
```

**说明**：
- 每个页面组件独立文件，位于 `src/pages/{pageName}/index.jsx`
- 使用 `export default` 导出组件
- Rollup 会为每个页面生成独立的 ESM 文件（`dist/esm/{pageName}.js`）
- 系统会根据路由配置动态加载对应的 ESM 模块
- 路由配置在 `config/routes.js` 中，必须与 `plugin.yml` 中的菜单路径对应

### 插件注册路径说明 ⭐ 2025-10-28 新增

#### 新路径 vs 旧路径

WLDOS 插件系统支持两种插件注册路径，系统会自动兼容：

**新路径（推荐）**：`window.WLDOSPlugins.register(pluginCode, module)`
- ✅ 统一管理：通过插件注册表统一管理所有插件
- ✅ 易于查询：可以通过 `window.WLDOSPlugins.get(code)` 获取插件
- ✅ 支持卸载：可以通过 `window.WLDOSPlugins.unload(code)` 卸载插件
- ✅ 版本管理：便于版本管理和冲突检测

**旧路径（兼容）**：`window[pluginCode]` 或 `window['plugin_' + pluginCode]`
- ✅ 向后兼容：保持对旧插件的兼容性
- ✅ 自动迁移：系统会自动检测并注册到注册表
- ⚠️ 逐步迁移：新插件推荐使用新路径

#### 组件发现优先级

系统按以下优先级发现组件：

1. **注册表路径（优先）**：`window.WLDOSPlugins.get(code)`
2. **旧 window 路径（回退）**：`window[code]` → `window['plugin_' + code]` → `window[code + 'Plugin']` → `window[code.toUpperCase()]`

#### 自动兼容机制

系统会自动将旧路径的插件注册到注册表：

```javascript
// 系统自动执行（无需插件代码）
if (pluginModule && window.WLDOSPlugins && !window.WLDOSPlugins.has(code)) {
  window.WLDOSPlugins.register(code, pluginModule);
}
```

#### 推荐实践 ⭐ 2025-10-28 新增

**新插件开发**：
1. 在 `plugin.yml` 中配置菜单（`menus`）和钩子（`hooks`）
2. 在 `config/routes.js` 中配置路由，对应菜单路径
3. 在 `src/pages/` 中开发页面组件
4. 使用 Rollup 构建 ESM 格式的模块文件

**旧插件迁移**：
1. 无需立即修改，系统会自动兼容
2. 逐步迁移到新路径，获得更好的管理能力

### pluginConfig 最佳实践 ⭐ 2025-10-28 新增

1. **推荐使用详细格式**：提供版本信息，便于冲突检测
   ```javascript
   providedDependencies: {
     'lodash': { value: _, version: '4.17.21' }
   }
   ```

2. **统一在 pluginConfig 中声明**：不要手动调用 `runtime.registerProvidedDependency()`

3. **保持依赖版本一致**：如果多个插件需要同一依赖，尽量使用相同版本，避免冲突

4. **通过 externals 排除常用依赖**：对于 React、antd 等主应用提供的依赖，通过 webpack externals 排除，不要打包到插件中

**rollup.config.js 示例**：
```javascript
export default {
  // ... 其他配置
  external: ['react', 'react-dom', 'antd'],
  // 使用主应用的React和antd，避免版本冲突和Context问题
};
```

#### 7. 测试验证
- 验证配置文件格式
- 测试数据库脚本
- 验证权限和菜单配置
- 测试API路径映射和权限验证
- 验证前后端API交互

