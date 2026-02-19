# WLDOS 第三方集成示例项目

这是一个完整的 WLDOS 第三方集成示例项目，演示如何使用 `wldos-framework` 集成到第三方应用中。

## 📋 项目说明

本项目演示了 WLDOS 框架的以下核心功能：

1. **统一响应格式**：自动将 Controller 返回值包装为统一的 `Result` 格式
2. **统一异常处理**：自动捕获异常并返回统一格式
3. **参数校验**：使用 Bean Validation 进行参数校验
4. **Swagger API 文档**：自动生成 API 文档
5. **分页标准用法**：Map + PageQuery + PageData，支持动态筛选、排序，适配 ProTable 等前端

### 📦 Demo 示例类型

本项目包含三种不同类型的 Controller 示例：

1. **EntityController 示例**（`ProductController`）
   - 演示使用 `EntityController` 基类进行实体 CRUD 操作
   - 自动提供：add、update、delete、get、all、page 等标准接口
   - 支持 Hook 机制：preAdd、postAdd、preUpdate、postUpdate 等
   - 支持多租户隔离、域隔离等高级功能

2. **NonEntityController 示例**（`OrderController`）
   - 演示使用 `NonEntityController` 基类进行非实体业务操作
   - 提供用户信息获取：getUserId、getToken、getUserIp 等
   - 提供租户信息获取：getTenantId、isMultiTenancy 等
   - 提供域名信息获取：getDomain、getDomainId 等
   - 提供权限判断：isAdmin、isCanTrust 等

3. **普通 Controller 示例**（`UserController`）
   - 演示普通 Controller 的使用方式（不继承框架基类）
   - 适用于简单的业务场景
   - 仍然享受框架的统一响应格式、异常处理等功能

## 🚀 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+

### 2. 运行项目

```bash
# 进入项目目录
cd wldos-integration-demo

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 3. 访问应用

启动成功后，访问以下地址：

- **Swagger API 文档**：http://localhost:8080/doc.html
- **前端 Demo**：打开 `frontend-demo/index.html` 文件（或使用本地服务器）
- **用户列表**（普通Controller）：http://localhost:8080/api/users/list
- **用户分页**：http://localhost:8080/api/users/page-list?current=1&pageSize=10
- **产品列表**（EntityController）：http://localhost:8080/api/products/all
- **文章分页**：http://localhost:8080/api/articles/admin-list?current=1&pageSize=10
- **订单列表**（NonEntityController）：http://localhost:8080/api/orders/list
- **订单分页**：http://localhost:8080/api/orders/admin-list?current=1&pageSize=10

### 4. 前端 Demo 使用

前端 Demo 提供了完整的交互界面，演示如何调用不同类型的 API：

```bash
# 方法1：直接打开 HTML 文件
# 在浏览器中打开 frontend-demo/index.html

# 方法2：使用本地服务器（推荐）
cd frontend-demo
python -m http.server 8000
# 或
npx http-server -p 8000

# 然后访问：http://localhost:8000
```

详细说明请参考：
- [前端 Demo 文档](frontend-demo/README.md) - 包含独立 HTML Demo 和基于 wldos-ui 的集成指南
- [基于 wldos-ui 的集成指南](frontend-demo/wldos-ui-integration.md) - **推荐**：详细说明如何在 wldos-ui 中集成

## 📁 项目结构

```
wldos-integration-demo/
├── pom.xml                          # Maven 配置文件
├── README.md                        # 项目说明文档
├── frontend-demo/                   # 前端 Demo
│   ├── index.html                   # 前端演示页面
│   ├── request.js                   # 统一响应格式处理
│   ├── demo.js                      # Demo 业务逻辑
│   └── README.md                    # 前端 Demo 说明
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── myapp/
        │               ├── MyAppApplication.java      # 主应用类
        │               ├── controller/
        │               │   ├── UserController.java    # 用户管理（普通Controller示例）
        │               │   ├── ProductController.java # 产品管理（EntityController示例）
        │               │   └── OrderController.java   # 订单管理（NonEntityController示例）
        │               ├── entity/
        │               │   └── Product.java            # 产品实体类
        │               ├── dao/
        │               │   └── ProductDao.java        # 产品数据访问层接口
        │               ├── dto/
        │               │   └── UserCreateDTO.java     # 用户创建 DTO
        │               ├── vo/
        │               │   └── UserVO.java            # 用户视图对象
        │               └── service/
        │                   ├── UserService.java       # 用户服务类
        │                   ├── ProductService.java    # 产品服务类（EntityService示例）
        │                   └── OrderService.java      # 订单服务类（NonEntityService示例）
        └── resources/
            └── application.yml      # 应用配置文件
```

## 🔧 配置说明

### application.yml

```yaml
wldos:
  framework:
    # 配置第三方应用的基础包路径
    base-package: com.example.myapp
    enabled: true
    swagger:
      enabled: true  # 默认启用 Swagger API 文档
```

**重要说明**：
- `base-package` 配置用于指定第三方应用的包路径
- WLDOS 框架会根据这个配置识别哪些是第三方应用的代码
- 框架会自动处理配置包路径下的 Controller、异常和 Swagger 文档

## 📝 API 示例

### EntityController 示例（ProductController）

#### 1. 新增产品
**请求**：
```http
POST /api/products/add
Content-Type: application/json

{
  "name": "iPhone 15",
  "description": "苹果最新款手机",
  "price": 5999.00,
  "stock": 100
}
```

#### 2. 查询所有产品
**请求**：
```http
GET /api/products/all
```

#### 3. 根据ID查询产品
**请求**：
```http
GET /api/products/get?id=1
```

#### 4. 更新产品
**请求**：
```http
POST /api/products/update
Content-Type: application/json

{
  "id": 1,
  "name": "iPhone 15 Pro",
  "price": 6999.00
}
```

#### 5. 删除产品
**请求**：
```http
DELETE /api/products/delete
Content-Type: application/json

{
  "id": 1
}
```

### NonEntityController 示例（OrderController）

#### 1. 创建订单
**请求**：
```http
POST /api/orders/create?productId=1&quantity=2
```

#### 2. 查询订单列表
**请求**：
```http
GET /api/orders/list
```

#### 3. 获取当前用户信息
**请求**：
```http
GET /api/orders/user-info
```

**响应**：
```json
{
  "code": 200,
  "message": "",
  "data": {
    "userId": 1,
    "token": "xxx",
    "userIp": "127.0.0.1",
    "isMultiTenancy": false,
    "tenantId": null,
    "isMultiDomain": false,
    "domain": "localhost",
    "domainId": null,
    "platDomain": "gitee.com/wldos/wldos",
    "tokenExpTime": 3600000
  },
  "success": true
}
```

### 普通 Controller 示例（UserController）

#### 1. 获取用户列表

**请求**：
```http
GET /api/users/list
```

**响应**：
```json
{
  "code": 200,
  "message": "",
  "data": [
    {
      "id": 1,
      "username": "zhangsan",
      "email": "zhangsan@example.com",
      "nickname": "张三"
    },
    {
      "id": 2,
      "username": "lisi",
      "email": "lisi@example.com",
      "nickname": "李四"
    }
  ],
  "success": true
}
```

### 2. 获取用户详情

**请求**：
```http
GET /api/users/1
```

**响应**：
```json
{
  "code": 200,
  "message": "",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "nickname": "张三"
  },
  "success": true
}
```

### 3. 创建用户

**请求**：
```http
POST /api/users
Content-Type: application/json

{
  "username": "test",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

**成功响应**：
```json
{
  "code": 200,
  "message": "",
  "data": {
    "id": 4,
    "username": "test",
    "email": "test@example.com",
    "nickname": "测试用户"
  },
  "success": true
}
```

**参数校验失败响应**：
```json
{
  "code": 422,
  "message": "username: 用户名不能为空; email: 邮箱格式不正确",
  "data": null,
  "success": false
}
```

### 4. 异常处理示例

**请求**：
```http
GET /api/users/exception
```

**响应**：
```json
{
  "code": 400,
  "message": "这是一个业务异常示例",
  "data": null,
  "success": false
}
```

## 🎯 核心特性演示

### 1. 统一响应格式

WLDOS 框架会自动将 Controller 返回值包装为统一的 `Result` 格式：

```java
@GetMapping("/list")
public List<UserVO> getUserList() {
    // 直接返回业务对象，框架会自动包装为 Result.ok(data)
    return userService.getUserList();
}
```

### 2. 异常处理

框架会自动捕获异常并返回统一格式：

```java
@GetMapping("/exception")
public UserVO exceptionExample() {
    // 如果抛出异常，框架会自动捕获并返回 Result.error(...)
    throw new BaseException(400, "这是一个业务异常示例");
}
```

### 3. 参数校验

使用 Bean Validation 进行参数校验：

```java
@PostMapping
public Result createUser(@RequestBody @Valid UserCreateDTO dto) {
    // 如果校验失败，框架会自动返回 Result.error(422, errors)
    UserVO user = userService.createUser(dto);
    return Result.ok(user);
}
```

### 4. Swagger API 文档

框架会自动生成 Swagger API 文档，访问 `http://localhost:8080/doc.html` 即可查看。

### 5. 分页标准用法（PageQuery 自动解析）

框架提供 `PageQueryMethodArgumentResolver`，Controller 可直接声明 `PageQuery` 参数，无需显式 `new PageQuery(params)`：

```java
@GetMapping("/admin-list")
public Result<PageData<Article>> adminList(PageQuery pageQuery) {
    return Result.ok(service.pageList(pageQuery));
}
```

- **PageQuery 自动解析**：框架从请求参数（GET query 或 POST form）构建 PageQuery（current、pageSize、sorter、filter、condition）
- **勿加 @RequestBody**：POST 才有 body，GET 用 requestParam（query）；若写成 `@RequestBody PageQuery`，GET 会报 "Required request body is missing"。仅写 `PageQuery pageQuery` 时由框架解析器从请求参数绑定
- **@ApiImplicitParams**：可为 Swagger 补充参数文档
- **PageData**：统一分页结构（total、current、pageSize、rows）

## 📚 更多文档

- [WLDOS 第三方集成指南](../wldos-agent/docs/第三方开发/第三方集成指南.md)
- [WLDOS 官方文档](https://github.com/wldos/wldos-docs)

## 🤝 技术支持

如有问题，请联系：
- 邮箱：306991142@qq.com
- 文档：https://github.com/wldos/wldos-docs

---

**项目版本**: 1.0.0  
**WLDOS 版本**: 2.3.7.1  
**创建日期**: 2025-12-28

