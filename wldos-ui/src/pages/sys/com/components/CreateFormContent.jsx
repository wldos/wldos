import React from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, Alert, InputNumber } from 'antd';
import {
  BankOutlined,
  KeyOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  NumberOutlined,
  CheckCircleOutlined,
  PlusOutlined
} from '@ant-design/icons';

const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

const CreateFormContent = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel,
    roles = []
  } = props;

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      await handleAdd(fieldsValue);
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="新建公司"
          description="创建新的公司实体，配置公司的基本信息、层级关系和状态设置。公司是平台上的独立运营单位。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <BankOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="公司的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="comName"
            label={
              <Space>
                公司名称
                <Tooltip title="公司的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入公司名称，不能为空，最多50个字！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<BankOutlined />}
              placeholder="请输入公司名称，最多50个字"
            />
          </FormItem>

          <FormItem
            name="comCode"
            label={
              <Space>
                公司编码
                <Tooltip title="公司的统一社会信用代码，用于系统识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入统一社会信用代码，最多50位！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入统一社会信用代码，最多50位"
            />
          </FormItem>

          <FormItem
            name="comDesc"
            label={
              <Space>
                公司描述
                <Tooltip title="公司的详细说明，帮助理解公司业务">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: false,
                message: '最多120个字！',
                max: 120,
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入公司描述，最多120个字"
              style={{ resize: 'vertical' }}
            />
          </FormItem>
        </Card>

        {/* 公司配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              公司配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="公司的层级和状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="parentId"
            label={
              <Space>
                父公司
                <Tooltip title="公司的上级公司，用于公司层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择父公司"
              style={{ width: '100%' }}
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={roles}
            />
          </FormItem>

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="公司在列表中的显示顺序，1-100">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入展示顺序！',
              },
            ]}
          >
            <InputNumber
              prefix={<NumberOutlined />}
              placeholder="请输入数字，1-100"
              min={1}
              max={100}
              style={{ width: '100%' }}
            />
          </FormItem>

          <FormItem
            name="isValid"
            label={
              <Space>
                状态
                <Tooltip title="公司的启用状态">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择状态"
              style={{ width: '100%' }}
            >
              <Option value="1">有效</Option>
              <Option value="0">无效</Option>
            </Select>
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <>
        <Button onClick={handleCancel}>取消</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleNext}>创建公司</Button>
      </>
    );

  return (
    <Form
      {...formLayout}
      form={form}
      initialValues={{
        isValid: '1',
        displayOrder: 1
      }}
    >
      {renderContent()}
      <div style={{ textAlign: 'right', marginTop: '24px' }}>
        {renderFooter()}
      </div>
    </Form>
  );
};

export default CreateFormContent;
