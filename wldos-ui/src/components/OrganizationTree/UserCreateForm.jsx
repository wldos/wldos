import React from 'react';
import { Form, Input, Button, Space, Select } from 'antd';

const { Option } = Select;

const UserCreateForm = ({ organizationId, onSubmit, loading }) => {
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    try {
      const submitData = {
        ...values,
        organizationId: organizationId
      };
      await onSubmit(submitData);
    } catch (error) {
      console.error('提交失败:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={{
        userName: '',
        nickName: '',
        email: '',
        phone: '',
        role: 'user'
      }}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="userName"
        label="用户名"
        rules={[
          { required: true, message: '请输入用户名' },
          { max: 50, message: '用户名不能超过50个字符' }
        ]}
      >
        <Input placeholder="请输入用户名" />
      </Form.Item>

      <Form.Item
        name="nickName"
        label="昵称"
        rules={[
          { required: true, message: '请输入昵称' },
          { max: 50, message: '昵称不能超过50个字符' }
        ]}
      >
        <Input placeholder="请输入昵称" />
      </Form.Item>

      <Form.Item
        name="email"
        label="邮箱"
        rules={[
          { required: true, message: '请输入邮箱' },
          { type: 'email', message: '请输入有效的邮箱地址' }
        ]}
      >
        <Input placeholder="请输入邮箱" />
      </Form.Item>

      <Form.Item
        name="phone"
        label="手机号"
        rules={[
          { required: true, message: '请输入手机号' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
        ]}
      >
        <Input placeholder="请输入手机号" />
      </Form.Item>

      <Form.Item
        name="role"
        label="角色"
        rules={[
          { required: true, message: '请选择角色' }
        ]}
      >
        <Select placeholder="请选择角色">
          <Option value="admin">管理员</Option>
          <Option value="user">普通用户</Option>
          <Option value="guest">访客</Option>
        </Select>
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={() => form.resetFields()}>
            重置
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            创建
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default UserCreateForm;
