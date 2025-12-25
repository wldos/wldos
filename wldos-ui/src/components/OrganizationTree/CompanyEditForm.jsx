import React from 'react';
import { Form, Input, InputNumber, Switch, Button, Space } from 'antd';

const { TextArea } = Input;

const CompanyEditForm = ({ company, onSubmit, loading }) => {
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
        comName: company.comName || company.name || '',
        comCode: company.comCode || company.code || '',
        comDesc: company.comDesc || company.description || '',
        displayOrder: company.displayOrder || 0,
        isValid: company.isValid === '1' || company.isValid === 1
      }}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="comName"
        label="公司名称"
        rules={[
          { required: true, message: '请输入公司名称' },
          { max: 100, message: '公司名称不能超过100个字符' }
        ]}
      >
        <Input placeholder="请输入公司名称" />
      </Form.Item>

      <Form.Item
        name="comCode"
        label="公司编码"
        rules={[
          { required: true, message: '请输入公司编码' },
          { max: 50, message: '公司编码不能超过50个字符' }
        ]}
      >
        <Input placeholder="请输入公司编码" />
      </Form.Item>

      <Form.Item
        name="comDesc"
        label="公司描述"
        rules={[
          { max: 500, message: '公司描述不能超过500个字符' }
        ]}
      >
        <TextArea 
          rows={4} 
          placeholder="请输入公司描述" 
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

export default CompanyEditForm;
