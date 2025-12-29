# 基于 wldos-ui 的前端集成指南

本文档说明如何在 **wldos-ui** 中集成 WLDOS 第三方 API。

## 🎯 核心要点

### wldos-ui 已内置统一响应格式处理

**wldos-ui** 的 `src/utils/request.js` 已经完整处理了 WLDOS 框架的统一响应格式，**无需额外处理**。

## 📋 响应格式说明

WLDOS 框架的统一响应格式：

```json
{
  "code": 200,           // 业务状态码：200=成功，非200=失败
  "message": "",         // 响应消息：成功时为空字符串，失败时为错误信息
  "data": { ... },       // 响应数据：业务数据
  "success": true        // 操作是否成功：true=成功，false=失败
}
```

## 🔧 wldos-ui 的处理方式

### 1. 自动错误处理

`wldos-ui/src/utils/request.js` 中的 `errorHandler` 自动处理：

```javascript
const errorHandler = (error) => {
  const {response} = error;
  
  if (response && response.data) {
    const { code, message } = response.data;
    
    // 401 未授权：自动清除权限并跳转登录页
    if (code === 401) {
      clearAuthority();
      history.replace({
        pathname: '/user/login',
        // ...
      });
    } 
    // 其他错误：自动显示错误提示
    else if (code !== 200) {
      notification.error({
        message: '请求异常',
        description: message || codeMessage[code] || '未知异常',
      });
    } 
  }
  return response;
};
```

### 2. 使用方式

在 Service 中调用 API：

```javascript
import request from '@/utils/request';

export async function getUserList() {
  // 直接调用，wldos-ui 会自动处理响应格式
  return request('/api/users/list');
}
```

在页面组件中使用：

```javascript
import { getUserList } from '@/services/integrationDemo';

const MyComponent = () => {
  const fetchUsers = async () => {
    try {
      const response = await getUserList();
      // response.data 就是业务数据（框架已处理）
      console.log('用户列表:', response.data);
    } catch (error) {
      // 错误已经被 request.js 的 errorHandler 处理了
      // 这里可以做额外的错误处理
      console.error('获取用户列表失败:', error);
    }
  };
  
  return <Button onClick={fetchUsers}>获取用户列表</Button>;
};
```

## 📝 完整示例

### Service 文件

**`wldos-ui/src/services/integrationDemo.js`**

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

export async function updateUser(id, params) {
  return request(`/api/users/${id}`, {
    method: 'PUT',
    data: params,
  });
}

export async function deleteUser(id) {
  return request(`/api/users/${id}`, {
    method: 'DELETE',
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

### 页面组件

**`wldos-ui/src/pages/integration-demo/index.jsx`**

```javascript
import React, { useState, useEffect } from 'react';
import { Table, Button, Form, Input, Modal, message, Card } from 'antd';
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
  const [productForm] = Form.useForm();
  const [createUserModalVisible, setCreateUserModalVisible] = useState(false);
  const [createProductModalVisible, setCreateProductModalVisible] = useState(false);

  // 获取用户列表
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await getUserList();
      // response.data 就是业务数据（wldos-ui 已处理）
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
      message.success('创建用户成功');
      form.resetFields();
      setCreateUserModalVisible(false);
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
      productForm.resetFields();
      setCreateProductModalVisible(false);
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
    <div style={{ padding: 24 }}>
      <h1>WLDOS 第三方集成 Demo</h1>
      
      {/* 用户管理示例 */}
      <Card title="用户管理（普通 Controller）" style={{ marginBottom: 24 }}>
        <Button 
          onClick={fetchUsers} 
          loading={loading}
          style={{ marginBottom: 16 }}
        >
          刷新用户列表
        </Button>
        <Button 
          type="primary"
          onClick={() => setCreateUserModalVisible(true)}
          style={{ marginLeft: 8 }}
        >
          创建用户
        </Button>
        <Table 
          dataSource={users} 
          columns={[
            { title: 'ID', dataIndex: 'id', width: 80 },
            { title: '用户名', dataIndex: 'username' },
            { title: '邮箱', dataIndex: 'email' },
            { title: '昵称', dataIndex: 'nickname' },
          ]}
          rowKey="id"
          loading={loading}
        />
      </Card>

      {/* 产品管理示例 */}
      <Card title="产品管理（EntityController）" style={{ marginBottom: 24 }}>
        <Button 
          onClick={fetchProducts} 
          loading={loading}
          style={{ marginBottom: 16 }}
        >
          刷新产品列表
        </Button>
        <Button 
          type="primary"
          onClick={() => setCreateProductModalVisible(true)}
          style={{ marginLeft: 8 }}
        >
          创建产品
        </Button>
        <Table 
          dataSource={products} 
          columns={[
            { title: 'ID', dataIndex: 'id', width: 80 },
            { title: '名称', dataIndex: 'name' },
            { title: '描述', dataIndex: 'description' },
            { title: '价格', dataIndex: 'price', render: (price) => `¥${price}` },
            { title: '库存', dataIndex: 'stock' },
          ]}
          rowKey="id"
          loading={loading}
        />
      </Card>

      {/* 订单管理示例 */}
      <Card title="订单管理（NonEntityController）">
        <Button 
          onClick={fetchOrders} 
          loading={loading}
          style={{ marginBottom: 16 }}
        >
          刷新订单列表
        </Button>
        <Table 
          dataSource={orders} 
          columns={[
            { title: '订单信息', dataIndex: 'orderInfo' },
          ]}
          rowKey="orderInfo"
          loading={loading}
        />
      </Card>

      {/* 创建用户弹窗 */}
      <Modal
        title="创建用户"
        visible={createUserModalVisible}
        onCancel={() => setCreateUserModalVisible(false)}
        onOk={() => form.submit()}
      >
        <Form form={form} onFinish={handleCreateUser}>
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ required: true, type: 'email', message: '请输入有效的邮箱' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="nickname"
            label="昵称"
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* 创建产品弹窗 */}
      <Modal
        title="创建产品"
        visible={createProductModalVisible}
        onCancel={() => setCreateProductModalVisible(false)}
        onOk={() => productForm.submit()}
      >
        <Form form={productForm} onFinish={handleAddProduct}>
          <Form.Item
            name="name"
            label="产品名称"
            rules={[{ required: true, message: '请输入产品名称' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="description"
            label="产品描述"
          >
            <Input.TextArea />
          </Form.Item>
          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <Input type="number" step="0.01" />
          </Form.Item>
          <Form.Item
            name="stock"
            label="库存"
            rules={[{ required: true, message: '请输入库存' }]}
          >
            <Input type="number" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default IntegrationDemo;
```

## 🔑 关键点总结

1. **无需处理响应格式**：wldos-ui 的 `request.js` 已经自动处理
2. **直接使用 `response.data`**：业务数据在 `response.data` 中
3. **错误自动处理**：错误会自动显示提示，401 会自动跳转登录
4. **Token 自动添加**：Token 会自动从 `authority` 中获取并添加到请求头

---

**版本**: 1.0.0  
**创建日期**: 2025-12-28

