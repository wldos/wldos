# WLDOS 第三方集成前端 Demo

这是一个完整的前端 Demo，演示如何基于 **wldos-ui** 调用 WLDOS 框架提供的不同类型 Controller API，并处理统一响应格式。

## 📋 功能说明

本 Demo 包含以下功能：

1. **普通 Controller 示例**（UserController）
   - 获取用户列表
   - 获取用户详情
   - 创建用户

2. **EntityController 示例**（ProductController）
   - 查询所有产品
   - 根据ID查询产品
   - 新增产品

3. **NonEntityController 示例**（OrderController）
   - 创建订单
   - 查询订单列表
   - 获取当前用户信息

## 🎯 基于 wldos-ui 的集成方式

### wldos-ui 已内置统一响应格式处理

**wldos-ui** 的 `src/utils/request.js` 已经处理了 WLDOS 框架的统一响应格式：

```javascript
// wldos-ui/src/utils/request.js
const errorHandler = (error) => {
  const {response} = error;
  
  if (response && response.data) {
    const { code, message } = response.data;
    if (code === 401) {
      // 处理未授权
      clearAuthority();
      // ...
    } else if (code !== 200) {
      // 处理业务错误
      notification.error({
        message: '请求异常',
        description: message || codeMessage[code] || '未知异常',
      });
    } 
  }
  return response;
};
```

**重要说明**：
- wldos-ui 的 `request.js` 已经自动处理了统一响应格式
- 使用 `code !== 200` 判断业务失败
- 自动显示错误提示（notification.error）
- 自动处理 401 未授权（跳转登录页）

### 在 wldos-ui 中创建 Service

在 `wldos-ui/src/services/` 目录下创建 Service 文件：

**示例：`wldos-ui/src/services/integrationDemo.js`**

```javascript
import request from '@/utils/request';

/**
 * 用户管理 Service（普通 Controller 示例）
 */
export async function getUserList() {
  return request('/api/users/list');
}

export async function getUserById(id) {
  return request(`/api/users/${id}`);
}

export async function createUser(params) {
  return request('/api/users', {
    method: 'POST',
    data: params,
  });
}

/**
 * 产品管理 Service（EntityController 示例）
 */
export async function getAllProducts() {
  return request('/api/products/all');
}

export async function getProductById(id) {
  return request(`/api/products/get?id=${id}`);
}

export async function addProduct(params) {
  return request('/api/products/add', {
    method: 'POST',
    data: params,
  });
}

export async function updateProduct(params) {
  return request('/api/products/update', {
    method: 'POST',
    data: params,
  });
}

export async function deleteProduct(params) {
  return request('/api/products/delete', {
    method: 'DELETE',
    data: params,
  });
}

/**
 * 订单管理 Service（NonEntityController 示例）
 */
export async function createOrder(params) {
  return request(`/api/orders/create?productId=${params.productId}&quantity=${params.quantity}`);
}

export async function getOrderList() {
  return request('/api/orders/list');
}

export async function getUserInfo() {
  return request('/api/orders/user-info');
}
```

### 在 wldos-ui 中创建页面组件

在 `wldos-ui/src/pages/` 目录下创建页面组件：

**示例：`wldos-ui/src/pages/integration-demo/index.jsx`**

```javascript
import React, { useState, useEffect } from 'react';
import { Table, Button, Form, Input, Modal, message } from 'antd';
import { 
  getUserList, 
  createUser,
  getAllProducts,
  addProduct,
  createOrder,
  getOrderList 
} from '@/services/integrationDemo';

const IntegrationDemo = () => {
  const [users, setUsers] = useState([]);
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  // 获取用户列表
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await getUserList();
      // wldos-ui 的 request 已经处理了响应格式
      // response.data 就是业务数据
      setUsers(response.data || []);
    } catch (error) {
      // 错误已经被 request.js 的 errorHandler 处理了
      console.error('获取用户列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 创建用户
  const handleCreateUser = async (values) => {
    try {
      const response = await createUser(values);
      // response.data 就是业务数据
      message.success('创建用户成功');
      form.resetFields();
      fetchUsers(); // 刷新列表
    } catch (error) {
      // 错误已经被 request.js 的 errorHandler 处理了
      console.error('创建用户失败:', error);
    }
  };

  // 获取产品列表
  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await getAllProducts();
      setProducts(response.data || []);
    } catch (error) {
      console.error('获取产品列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 创建产品
  const handleAddProduct = async (values) => {
    try {
      const response = await addProduct(values);
      message.success('创建产品成功');
      fetchProducts(); // 刷新列表
    } catch (error) {
      console.error('创建产品失败:', error);
    }
  };

  // 创建订单
  const handleCreateOrder = async (productId, quantity) => {
    try {
      const response = await createOrder({ productId, quantity });
      message.success('创建订单成功');
      fetchOrders(); // 刷新列表
    } catch (error) {
      console.error('创建订单失败:', error);
    }
  };

  // 获取订单列表
  const fetchOrders = async () => {
    setLoading(true);
    try {
      const response = await getOrderList();
      setOrders(response.data || []);
    } catch (error) {
      console.error('获取订单列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
    fetchProducts();
    fetchOrders();
  }, []);

  return (
    <div>
      <h2>WLDOS 第三方集成 Demo</h2>
      
      {/* 用户管理示例 */}
      <div style={{ marginBottom: 24 }}>
        <h3>用户管理（普通 Controller）</h3>
        <Button onClick={fetchUsers} loading={loading}>刷新用户列表</Button>
        <Table 
          dataSource={users} 
          columns={[
            { title: 'ID', dataIndex: 'id' },
            { title: '用户名', dataIndex: 'username' },
            { title: '邮箱', dataIndex: 'email' },
            { title: '昵称', dataIndex: 'nickname' },
          ]}
          rowKey="id"
        />
      </div>

      {/* 产品管理示例 */}
      <div style={{ marginBottom: 24 }}>
        <h3>产品管理（EntityController）</h3>
        <Button onClick={fetchProducts} loading={loading}>刷新产品列表</Button>
        <Table 
          dataSource={products} 
          columns={[
            { title: 'ID', dataIndex: 'id' },
            { title: '名称', dataIndex: 'name' },
            { title: '描述', dataIndex: 'description' },
            { title: '价格', dataIndex: 'price' },
            { title: '库存', dataIndex: 'stock' },
          ]}
          rowKey="id"
        />
      </div>

      {/* 订单管理示例 */}
      <div>
        <h3>订单管理（NonEntityController）</h3>
        <Button onClick={fetchOrders} loading={loading}>刷新订单列表</Button>
        <Table 
          dataSource={orders} 
          columns={[
            { title: '订单信息', dataIndex: 'orderInfo' },
          ]}
          rowKey="orderInfo"
        />
      </div>
    </div>
  );
};

export default IntegrationDemo;
```

## 🚀 使用方法

### 1. 在 wldos-ui 中集成

#### 步骤1：创建 Service 文件

在 `wldos-ui/src/services/` 目录下创建 `integrationDemo.js`：

```bash
# 复制示例代码到 wldos-ui/src/services/integrationDemo.js
```

#### 步骤2：创建页面组件

在 `wldos-ui/src/pages/` 目录下创建页面：

```bash
mkdir -p wldos-ui/src/pages/integration-demo
# 创建 index.jsx 文件，复制示例代码
```

#### 步骤3：配置路由

在 `wldos-ui/config/routes.js` 中添加路由：

```javascript
{
  path: '/integration-demo',
  name: 'integration-demo',
  component: './integration-demo',
}
```

### 2. 启动项目

```bash
# 启动后端服务
cd wldos-integration-demo
mvn spring-boot:run

# 启动前端服务（在另一个终端）
cd wldos-ui
npm start
# 或
yarn start
```

### 3. 访问页面

访问：`http://localhost:8000/integration-demo`

## 📝 核心要点

### 1. 统一响应格式处理

**wldos-ui 的 `request.js` 已经自动处理了统一响应格式**：

```javascript
// 响应格式：{ code: 200, message: "", data: {...}, success: true }
// wldos-ui 自动处理：
// - code !== 200 时自动显示错误提示
// - code === 401 时自动跳转登录页
// - 返回的 response.data 就是业务数据
```

### 2. Service 调用方式

```javascript
import request from '@/utils/request';

// GET 请求
export async function getUserList() {
  return request('/api/users/list');
}

// POST 请求
export async function createUser(params) {
  return request('/api/users', {
    method: 'POST',
    data: params,
  });
}
```

### 3. 页面中使用

```javascript
// 调用 Service
const response = await getUserList();

// response.data 就是业务数据（框架已处理）
setUsers(response.data || []);

// 错误处理：wldos-ui 的 request.js 已经自动处理了错误提示
// 如果需要在页面中额外处理，可以使用 try-catch
try {
  const response = await createUser(values);
  message.success('创建成功');
} catch (error) {
  // 错误已经被 request.js 处理了，这里可以做额外处理
  console.error('创建失败:', error);
}
```

## 🔧 配置说明

### 修改 API 地址

如果需要修改后端 API 地址，编辑 `wldos-ui/config/config.js`：

```javascript
export default {
  // ...
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
};
```

### 添加 Token 认证

wldos-ui 的 `request.js` 已经自动处理了 Token 认证：

```javascript
// wldos-ui/src/utils/request.js
// 自动从 authority 中获取 token 并添加到请求头
headers: {
  ...headerFix,  // headerFix 中包含了 token
  Accept: '*/*',
}
```

## 📚 更多文档

- [WLDOS 第三方集成指南](../README.md)
- [wldos-ui 文档](../../wldos-ui/README.md)
- [WLDOS 官方文档](https://github.com/wldos/wldos-docs)

## 📌 注意事项

1. **响应格式**：wldos-ui 的 `request.js` 已经处理了统一响应格式，直接使用 `response.data` 即可
2. **错误处理**：错误已经被 `errorHandler` 自动处理，会自动显示错误提示
3. **Token 认证**：Token 会自动从 `authority` 中获取并添加到请求头
4. **401 处理**：401 错误会自动跳转到登录页

---

**版本**: 1.0.0  
**创建日期**: 2025-12-28  
**基于**: wldos-ui
