import React from 'react';
import { Form, Input, InputNumber, Switch, Button, Space, Select } from 'antd';

const { TextArea } = Input;
const { Option } = Select;

const OrganizationCreateForm = ({ systemId, companyId, onSubmit, loading }) => {
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    try {
      const submitData = {
        ...values,
        archId: systemId,
        comId: companyId
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
        orgName: '',
        orgCode: '',
        orgType: 'org',
        orgDesc: '',
        displayOrder: 0,
        isValid: true
      }}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="orgName"
        label="机构名称"
        rules={[
          { required: true, message: '请输入机构名称' },
          { max: 100, message: '机构名称不能超过100个字符' }
        ]}
      >
        <Input placeholder="请输入机构名称" />
      </Form.Item>

      <Form.Item
        name="orgCode"
        label="机构编码"
        rules={[
          { required: true, message: '请输入机构编码' },
          { max: 50, message: '机构编码不能超过50个字符' }
        ]}
      >
        <Input placeholder="请输入机构编码" />
      </Form.Item>

      <Form.Item
        name="orgType"
        label="组织类型"
        rules={[
          { required: true, message: '请选择组织类型' }
        ]}
      >
        <Select placeholder="请选择组织类型">
          <Option value="org">组织</Option>
          <Option value="dept">部门</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="orgDesc"
        label="机构描述"
        rules={[
          { max: 500, message: '机构描述不能超过500个字符' }
        ]}
      >
        <TextArea 
          rows={4} 
          placeholder="请输入机构描述" 
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
            创建
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default OrganizationCreateForm;
