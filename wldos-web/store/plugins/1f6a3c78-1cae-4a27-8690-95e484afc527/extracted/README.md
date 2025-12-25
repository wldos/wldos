# WLDOS 插件开发演示 (Plugin Demo)

## 概述

这是一个 WLDOS 插件开发演示项目，作为插件开发者的基础框架和参考示例。本项目展示了如何开发一个完整的 WLDOS 插件，包括后端 Java 代码、前端 React 组件、菜单配置、Hook 机制等。

## 功能特性

### 🎯 核心功能
- **完整的插件结构**：包含所有必要的文件和目录结构
- **后端示例**：Controller、Service、Entity 完整示例
- **前端示例**：React 组件、API 调用、表格管理等
- **菜单配置**：演示如何配置插件菜单和权限
- **Hook 机制**：演示如何使用 WLDOS Hook 机制
- **构建配置**：完整的 Maven 和 Rollup 构建配置

### 📁 目录结构

```
plugin-demo/
├── src/
│   ├── main/
│   │   ├── java/                    # Java源代码
│   │   │   └── com/wldos/plugin/demo/
│   │   │       ├── DemoPlugin.java          # 主插件类
│   │   │       ├── controller/              # 控制器
│   │   │       │   └── DemoController.java
│   │   │       ├── service/                  # 服务层
│   │   │       │   └── DemoService.java
│   │   │       └── entity/                  # 实体类
│   │   │           └── DemoEntity.java
│   │   ├── resources/
│   │   │   └── application.yml              # 应用配置
│   │   └── ui/                               # 前端UI
│   │       ├── src/
│   │       │   ├── pages/                    # 页面组件
│   │       │   │   ├── home/                 # 首页
│   │       │   │   └── example/              # 示例页面
│   │       │   └── utils/                    # 工具类
│   │       │       ├── api.js                 # API调用
│   │       │       └── request.js            # 请求工具
│   │       ├── rollup.config.js              # Rollup配置
│   │       └── package.json                  # 前端依赖
│   └── assembly/
│       └── package.xml                        # 打包配置
├── plugin.yml                                # 插件配置
├── pom.xml                                   # Maven配置
└── README.md                                 # 说明文档
```

## 快速开始

### 1. 复制插件模板

将此插件目录复制为你的新插件：

```bash
cp -r plugin-demo your-plugin-name
cd your-plugin-name
```

### 2. 修改插件信息

编辑 `plugin.yml` 文件，修改插件的基本信息：

```yaml
code: your-plugin-code
name: 你的插件名称
version: 1.0.0
description: 你的插件描述
author: 你的名字
mainClass: com.wldos.plugin.yourplugin.YourPlugin
```

### 3. 修改 Java 代码

- 修改包名：将 `com.wldos.plugin.demo` 替换为你的包名
- 修改类名：将 `DemoPlugin`、`DemoController` 等替换为你的类名
- 实现业务逻辑：根据你的需求修改 Service 和 Controller

### 4. 修改前端代码

- 修改页面组件：在 `src/main/ui/src/pages/` 下创建你的页面
- 修改 API 调用：在 `src/main/ui/src/utils/api.js` 中定义你的 API
- 修改路由配置：在 `plugin.yml` 中配置你的菜单和路由

### 5. 构建插件

```bash
# 构建UI（自动执行，无需手动）
mvn clean package
```

构建完成后，会在 `wldos-web/plugins/` 目录下生成 `plugin-demo.zip` 文件。

### 6. 安装插件

将生成的 ZIP 文件上传到 WLDOS 系统的插件管理页面进行安装。

## 开发指南

### 后端开发

#### 1. 插件主类

`DemoPlugin.java` 是插件的主类，需要继承 `AbstractPlugin` 并实现必要的接口：

```java
public class DemoPlugin extends AbstractPlugin implements Handler, Invoker {
    // 实现插件生命周期方法
    @Override
    public void init() throws Exception { }
    
    @Override
    public void start() throws Exception { }
    
    @Override
    public void stop() throws Exception { }
}
```

#### 2. Controller

`DemoController.java` 是 REST API 控制器，使用 Spring 的 `@RestController` 注解：

```java
@RestController
@RequestMapping("/plugins/demo")
public class DemoController {
    // 定义你的 API 端点
}
```

#### 3. Service

`DemoService.java` 是业务逻辑层，使用 Spring 的 `@Service` 注解：

```java
@Service
public class DemoService {
    // 实现你的业务逻辑
}
```

#### 4. Entity

`DemoEntity.java` 是实体类，用于数据模型定义：

```java
@Data
public class DemoEntity {
    // 定义你的数据字段
}
```

### 前端开发

#### 1. 页面组件

在 `src/main/ui/src/pages/` 下创建你的页面组件：

```jsx
import React from 'react';
import { Card } from 'antd';

const YourPage = () => {
  return (
    <Card>
      {/* 你的页面内容 */}
    </Card>
  );
};

export default YourPage;
```

#### 2. API 调用

在 `src/main/ui/src/utils/api.js` 中定义你的 API：

```javascript
export const yourAPI = {
  getData: () => apiCall('/your-endpoint'),
  createData: (data) => apiCall('/your-endpoint', {
    method: 'POST',
    data: data
  })
};
```

#### 3. 路由配置

在 `plugin.yml` 中配置菜单和路由：

```yaml
menus:
  - code: your-menu
    name: 你的菜单
    path: /admin/your-plugin
    component: /your-page
```

### Hook 机制

WLDOS 提供了强大的 Hook 机制，允许插件扩展系统功能。

#### 1. 定义 Hook

在 `plugin.yml` 中定义 Hook：

```yaml
hooks:
  - extName: your.hook.name
    type: HANDLER  # 或 INVOKE
    priority: 10
    description: Hook描述
    method: yourMethod
```

#### 2. 实现 Hook

在插件主类中实现 Hook 方法：

```java
@WLDOSHook(extName = "your.hook.name", type = HookType.HANDLER, priority = 10)
public Object yourMethod(Object... args) {
    // 处理逻辑
    return result;
}
```

## 配置说明

### plugin.yml

插件配置文件，定义插件的基本信息和菜单：

- `code`: 插件编码（唯一）
- `name`: 插件名称
- `version`: 插件版本
- `mainClass`: 主类全限定名
- `menus`: 菜单配置
- `hooks`: Hook 配置

### pom.xml

Maven 配置文件，定义依赖和构建过程：

- 依赖 `wldos-framework` 和 `wldos-platform`
- 使用 `frontend-maven-plugin` 构建前端
- 使用 `maven-assembly-plugin` 打包插件

### rollup.config.js

Rollup 配置文件，用于构建前端 ESM 格式：

- 多入口构建
- 外部依赖处理
- Babel 转译

## 注意事项

1. **插件编码唯一性**：确保 `plugin.yml` 中的 `code` 在整个系统中唯一
2. **包名规范**：Java 包名应遵循 `com.wldos.plugin.{plugin-code}` 格式
3. **路由路径**：前端路由路径应与 `plugin.yml` 中的 `path` 配置一致
4. **API 路径**：后端 API 路径应遵循 `/plugins/{plugin-code}/...` 格式
5. **UI 构建**：UI 构建会在 Maven 打包时自动执行，无需手动构建
6. **外部依赖**：前端依赖 React、Ant Design 等由主系统提供，无需打包

## 示例功能

本演示插件包含以下示例功能：

1. **首页**：展示插件信息和快速开始指南
2. **示例页面**：演示数据的增删改查功能
3. **API 调用**：演示前后端数据交互
4. **Hook 机制**：演示插件 Hook 的使用

## 参考资源

- [WLDOS 插件开发文档](../README.md)
- [Airdrop 插件示例](../airdrop/README.md)
- [Ant Design Pro 文档](https://pro.ant.design/)

## 许可证

Apache License 2.0

## 作者

WLDOS Team

