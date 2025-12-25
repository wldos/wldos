import React, { useState, forwardRef, useImperativeHandle } from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, Alert, InputNumber } from 'antd';
import CompanyTreeSelect from '@/components/TreeSelects/CompanyTreeSelect';
import ArchTreeSelect from '@/components/TreeSelects/ArchTreeSelect';
import {
  ApartmentOutlined,
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

const CreateFormContent = forwardRef((props, ref) => {
  const [form] = Form.useForm();
  const [selectedCompanyId, setSelectedCompanyId] = useState(null);
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel,
    onFormSubmit,
  } = props;

  // 处理公司选择变化
  const handleCompanyChange = (value) => {
    setSelectedCompanyId(value);
    // 清空父体系选择
    form.setFieldsValue({ parentId: undefined });
  };

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      if (onFormSubmit) {
        onFormSubmit(fieldsValue);
      } else {
        await handleAdd(fieldsValue);
      }
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  // 暴露submit方法给父组件
  useImperativeHandle(ref, () => ({
    submit: handleNext
  }));

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="新建体系"
          description="创建新的体系架构，配置体系的基本信息、层级关系和状态设置。体系是组织架构的基础单位。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <ApartmentOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="体系的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="archName"
            label={
              <Space>
                体系名称
                <Tooltip title="体系的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入体系名称，不能为空，最多25个字！',
                max: 25,
              },
            ]}
          >
            <Input
              prefix={<ApartmentOutlined />}
              placeholder="请输入体系名称，最多25个字"
            />
          </FormItem>

          <FormItem
            name="archCode"
            label={
              <Space>
                体系编码
                <Tooltip title="体系的唯一标识，用于系统识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入体系编码，不能为空，最多32位！',
                max: 32,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入体系编码，最多32位"
            />
          </FormItem>

          <FormItem
            name="archDesc"
            label={
              <Space>
                体系描述
                <Tooltip title="体系的详细说明，帮助理解体系用途">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: false,
                message: '最多150个字！',
                max: 150,
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入体系描述，最多150个字"
              style={{ resize: 'vertical' }}
            />
          </FormItem>
        </Card>

        {/* 体系配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              体系配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="体系的层级和状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="comId"
            label={
              <Space>
                归属公司
                <Tooltip title="选择体系归属的公司">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[{ required: true, message: '请选择归属公司！' }]}
          >
            <CompanyTreeSelect 
              placeholder="请选择归属公司" 
              onChange={handleCompanyChange}
              usePaged={true}
            />
          </FormItem>

          <FormItem
            name="parentId"
            label={
              <Space>
                父体系
                <Tooltip title="体系的上级体系，用于体系层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <ArchTreeSelect
              placeholder="请选择父体系"
              includeTop
              companyId={selectedCompanyId}
              usePaged={true}
            />
          </FormItem>

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="体系在列表中的显示顺序，1-100">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入展示顺序，1-100！',
              },
              {
                type: 'number',
                min: 1,
                max: 100,
                message: '请输入1到100之间的数字！',
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
                <Tooltip title="体系的启用状态">
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

  return (
    <Form
      {...formLayout}
      form={form}
      initialValues={{
        isValid: '1',
        displayOrder: 1,
        parentId: 0
      }}
    >
      {renderContent()}
    </Form>
  );
});

export default CreateFormContent;
