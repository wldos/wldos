import React, { useState, useEffect } from 'react';
import { Form, Input, InputNumber, Select, Button, Space } from 'antd';
import { loadSystemsByCompany } from '@/services/organization';

const { TextArea } = Input;
const { Option } = Select;

const SystemCreateForm = ({ companyId, onSubmit, loading, parentSystems = [] }) => {
  const [form] = Form.useForm();
  const [companySystems, setCompanySystems] = useState([]);
  const [loadingSystems, setLoadingSystems] = useState(false);

  // 加载同公司的体系列表
  useEffect(() => {
    const loadCompanySystems = async () => {
      if (!companyId) return;
      
      setLoadingSystems(true);
      try {
        const systems = await loadSystemsByCompany(companyId);
        setCompanySystems(systems);
      } catch (error) {
        console.error('加载公司体系失败:', error);
      } finally {
        setLoadingSystems(false);
      }
    };

    loadCompanySystems();
  }, [companyId]);

  const handleSubmit = async (values) => {
    try {
      const submitData = {
        ...values,
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
        archName: '',
        archCode: '',
        archDesc: '',
        parentId: 0,
        displayOrder: 1,
        isValid: '1'
      }}
      onFinish={handleSubmit}
    >
      <Form.Item
        name="archName"
        label="体系名称"
        rules={[
          { required: true, message: '请输入体系名称，不能为空，最多25个字！', max: 25 }
        ]}
      >
        <Input placeholder="请输入体系名称，最多25个字" />
      </Form.Item>

      <Form.Item
        name="archCode"
        label="体系编码"
        rules={[
          { required: true, message: '请输入体系编码，不能为空，最多32位！', max: 32 }
        ]}
      >
        <Input placeholder="请输入体系编码，最多32位" />
      </Form.Item>

      <Form.Item
        name="archDesc"
        label="体系描述"
        rules={[
          { max: 150, message: '最多150个字！' }
        ]}
      >
        <TextArea 
          rows={3} 
          placeholder="请输入体系描述，最多150个字" 
          style={{ resize: 'vertical' }}
        />
      </Form.Item>


      <Form.Item
        name="parentId"
        label="父体系"
        rules={[
          { required: true, message: '请选择父体系' }
        ]}
      >
        <Select 
          placeholder="请选择父体系" 
          loading={loadingSystems}
        >          
          {companySystems.map(system => (
            <Option key={system.id} value={system.id}>
              {system.archName}
            </Option>
          ))}
        </Select>
      </Form.Item>

      <Form.Item
        name="displayOrder"
        label="展示顺序"
        rules={[
          { required: true, message: '请输入展示顺序，1-100！' },
          { type: 'number', min: 1, max: 100, message: '请输入1到100之间的数字！' }
        ]}
      >
        <InputNumber 
          min={1} 
          max={100} 
          placeholder="请输入数字，1-100" 
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item
        name="isValid"
        label="状态"
        rules={[
          { required: true, message: '请选择状态' }
        ]}
      >
        <Select placeholder="请选择状态">
          <Option value="1">有效</Option>
          <Option value="0">无效</Option>
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

export default SystemCreateForm;
