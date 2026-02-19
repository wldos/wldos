import React, { useEffect } from 'react';
import { Form, Input, Select, InputNumber, Button, Space, Card } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import DocSelectField from '@/components/DocSelectField';

const { Option } = Select;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

const UpdateForm = ({ modalVisible, values, onSubmit, onCancel }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (modalVisible && values) {
      form.setFieldsValue({
        agreementCode: values.agreementCode,
        agreementType: values.agreementType,
        title: values.title,
        pubId: values.pubId,
        pluginCode: values.pluginCode || undefined,
        displayOrder: values.displayOrder ?? 1,
        isActive: values.isActive || '1',
      });
    }
  }, [modalVisible, values, form]);

  const handleOk = async () => {
    try {
      const fields = await form.validateFields();
      await onSubmit(fields);
    } catch (e) {
      // 校验失败
    }
  };

  return (
    <>
      <FullscreenModal
        width={640}
        bodyStyle={{ padding: '24px' }}
        destroyOnClose
        title={
          <Space>
            <EditOutlined style={{ color: '#1890ff' }} />
            编辑协议
          </Space>
        }
        visible={modalVisible}
        footer={
          <Space>
            <Button onClick={onCancel}>取消</Button>
            <Button type="primary" onClick={handleOk}>
              确定
            </Button>
          </Space>
        }
        onCancel={onCancel}
      >
        <Form form={form} {...formLayout}>
          <Card title="协议信息" size="small" style={{ marginBottom: 0 }}>
            <Form.Item
              name="agreementCode"
              label="协议编码"
              rules={[{ required: true, message: '请输入协议编码' }, { max: 50, message: '最多50字符' }]}
            >
              <Input placeholder="如 login_terms、order_terms、legal_notice" />
            </Form.Item>
            <Form.Item
              name="agreementType"
              label="协议类型"
              rules={[{ required: true, message: '请选择协议类型' }]}
            >
              <Select placeholder="请选择">
                <Option value="LOGIN">登录协议</Option>
                <Option value="ORDER">下单协议</Option>
                <Option value="LEGAL">法律声明</Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="title"
              label="协议标题"
              rules={[{ required: true, message: '请输入标题' }, { max: 256, message: '最多256字符' }]}
            >
              <Input placeholder="协议标题" />
            </Form.Item>
            <DocSelectField
              name="pubId"
              label="CMS 文档"
              rules={[{ required: true, message: '请选择 CMS 文档' }]}
              extra="在 CMS 中创建 agreement 类型页面后，通过下方按钮选择对应文档"
              placeholder="点击右侧按钮选择 CMS 文档"
              buttonText="选择文档"
              modalTitle="选择 CMS 文档（协议）"
            />
            <Form.Item
              name="pluginCode"
              label="插件编码"
              extra="插件专属条款时填写，为空表示全局协议"
            >
              <Input placeholder="可选" />
            </Form.Item>
            <Form.Item name="displayOrder" label="展示顺序" extra="数值越小越靠前，从 1 开始">
              <InputNumber min={1} style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="isActive" label="是否生效">
              <Select>
                <Option value="1">生效</Option>
                <Option value="0">停用</Option>
              </Select>
            </Form.Item>
          </Card>
        </Form>
      </FullscreenModal>
    </>
  );
};

export default UpdateForm;
