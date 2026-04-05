/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import { Form, Input, Button, Space, Select } from 'antd';
import { useIntl } from 'umi';

const { Option } = Select;

const UserCreateForm = ({ organizationId, onSubmit, loading }) => {
  const intl = useIntl();
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
        label={intl.formatMessage({ id: 'component.organizationTree.user.form.userName' })}
        rules={[
          { required: true, message: intl.formatMessage({ id: 'component.organizationTree.user.form.userName.required' }) },
          { max: 50, message: intl.formatMessage({ id: 'component.organizationTree.user.form.userName.max' }) },
        ]}
      >
        <Input placeholder={intl.formatMessage({ id: 'component.organizationTree.user.form.userName.placeholder' })} />
      </Form.Item>

      <Form.Item
        name="nickName"
        label={intl.formatMessage({ id: 'component.organizationTree.user.form.nickName' })}
        rules={[
          { required: true, message: intl.formatMessage({ id: 'component.organizationTree.user.form.nickName.required' }) },
          { max: 50, message: intl.formatMessage({ id: 'component.organizationTree.user.form.nickName.max' }) },
        ]}
      >
        <Input placeholder={intl.formatMessage({ id: 'component.organizationTree.user.form.nickName.placeholder' })} />
      </Form.Item>

      <Form.Item
        name="email"
        label={intl.formatMessage({ id: 'component.organizationTree.user.form.email' })}
        rules={[
          { required: true, message: intl.formatMessage({ id: 'component.organizationTree.user.form.email.required' }) },
          { type: 'email', message: intl.formatMessage({ id: 'component.organizationTree.user.form.email.invalid' }) },
        ]}
      >
        <Input placeholder={intl.formatMessage({ id: 'component.organizationTree.user.form.email.placeholder' })} />
      </Form.Item>

      <Form.Item
        name="phone"
        label={intl.formatMessage({ id: 'component.organizationTree.user.form.phone' })}
        rules={[
          { required: true, message: intl.formatMessage({ id: 'component.organizationTree.user.form.phone.required' }) },
          { pattern: /^1[3-9]\d{9}$/, message: intl.formatMessage({ id: 'component.organizationTree.user.form.phone.invalid' }) },
        ]}
      >
        <Input placeholder={intl.formatMessage({ id: 'component.organizationTree.user.form.phone.placeholder' })} />
      </Form.Item>

      <Form.Item
        name="role"
        label={intl.formatMessage({ id: 'component.organizationTree.user.form.role' })}
        rules={[
          { required: true, message: intl.formatMessage({ id: 'component.organizationTree.user.form.role.required' }) },
        ]}
      >
        <Select placeholder={intl.formatMessage({ id: 'component.organizationTree.user.form.role.placeholder' })}>
          <Option value="admin">{intl.formatMessage({ id: 'component.organizationTree.user.form.role.admin' })}</Option>
          <Option value="user">{intl.formatMessage({ id: 'component.organizationTree.user.form.role.user' })}</Option>
          <Option value="guest">{intl.formatMessage({ id: 'component.organizationTree.user.form.role.guest' })}</Option>
        </Select>
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={() => form.resetFields()}>
            {intl.formatMessage({ id: 'component.organizationTree.user.form.reset' })}
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            {intl.formatMessage({ id: 'component.organizationTree.user.form.create' })}
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default UserCreateForm;
