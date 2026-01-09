/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import { Form, Input, InputNumber, Switch, Button, Space } from 'antd';

const { TextArea } = Input;

const SystemEditForm = ({ system, onSubmit, loading }) => {
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        isValid: values.isValid ? 1 : 0,
      };
      await onSubmit(payload);
    } catch (error) {
      console.error('提交失败:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={{
        archName: system.archName || '',
        archCode: system.archCode || '',
        archDesc: system.archDesc || system.description || '',
        displayOrder: system.displayOrder || 0,
        isValid: system.isValid === '1' || system.isValid === 1
      }}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="archName"
        label="体系名称"
        rules={[
          { required: true, message: '请输入体系名称' },
          { max: 100, message: '体系名称不能超过100个字符' }
        ]}
      >
        <Input placeholder="请输入体系名称" />
      </Form.Item>

      <Form.Item
        name="archCode"
        label="体系编码"
        rules={[
          { required: true, message: '请输入体系编码' },
          { max: 50, message: '体系编码不能超过50个字符' }
        ]}
      >
        <Input placeholder="请输入体系编码" />
      </Form.Item>

      <Form.Item
        name="archDesc"
        label="体系描述"
        rules={[
          { max: 500, message: '体系描述不能超过500个字符' }
        ]}
      >
        <TextArea
          rows={4}
          placeholder="请输入体系描述"
        />
      </Form.Item>

      <Form.Item
        name="displayOrder"
        label="展示顺序"
        rules={[
          { required: true, message: '请输入展示顺序' }
        ]}
      >
        <InputNumber
          min={0}
          max={9999}
          placeholder="请输入展示顺序"
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item
        name="isValid"
        label="状态"
        valuePropName="checked"
      >
        <Switch
          checkedChildren="有效"
          unCheckedChildren="无效"
        />
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={() => form.resetFields()}>
            重置
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            保存
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default SystemEditForm;
