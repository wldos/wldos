import React from 'react';
import {Button, Form, Input, Select, Card, Space, Divider, Typography, Tooltip, Alert} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  AppstoreOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  GlobalOutlined,
  EditOutlined,
} from '@ant-design/icons';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    appTypeList
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 基础信息分组 */}
        <Card 
          title={
            <Space>
              <AppstoreOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="应用的基本标识信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="appName"
            label={
              <Space>
                应用名称
                <Tooltip title="应用的显示名称，用于识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '应用名称为必填项',
              },
              {
                max: 12,
                type: 'string',
                message: '最多12个字',
              },
            ]}
          >
            <Input 
              placeholder="请输入应用名称，最多12个字"
              prefix="📱"
            />
          </FormItem>
          
          <FormItem
            name="appDesc"
            label={
              <Space>
                应用描述
                <Tooltip title="应用的详细描述信息">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 50,
                type: 'string',
                message: '最多50个字',
              },
            ]}
          >
            <TextArea 
              rows={3}
              placeholder="请输入应用描述，最多50个字"
            />
          </FormItem>
        </Card>

        {/* 应用配置分组 */}
        <Card 
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              应用配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="应用的类型配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="appType"
            label={
              <Space>
                管理类型
                <Tooltip title="应用的管理类型，影响权限和功能">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请选择管理类型',
              },
            ]}
          >
            <Select
              placeholder="请选择管理类型"
              style={{ width: '100%' }}
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={appTypeList}
            />
          </FormItem>
        </Card>

        {/* 技术配置分组 */}
        <Card 
          title={
            <Space>
              <KeyOutlined style={{ color: '#fa8c16' }} />
              技术配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="应用的技术标识和安全配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Alert
            message="重要提示"
            description="应用编码全局唯一，应用暴露的资源必须以此编码为URL前缀：/appCode/resName/subResName"
            type="warning"
            showIcon
            style={{ marginBottom: '16px' }}
          />
          
          <FormItem
            name="appCode"
            label={
              <Space>
                应用编码
                <Tooltip title="应用的唯一标识码，用于URL前缀">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '应用编码为必填项',
              },
              {
                max: 5,
                type: 'string',
                message: '最多5个字符',
              },
              {
                pattern: /^[a-zA-Z]+$/,
                message: '只能包含英文字母',
              },
            ]}
          >
            <Input 
              placeholder="请输入5位以内英文编码"
              prefix="🔑"
            />
          </FormItem>
          
          <FormItem
            name="appSecret"
            label={
              <Space>
                应用密钥
                <Tooltip title="应用的安全密钥，用于API认证">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '应用密钥为必填项',
              },
              {
                max: 50,
                type: 'string',
                message: '最多50个字符',
              },
            ]}
          >
            <Input.Password 
              placeholder="请输入应用密钥，最多50个字符"
              prefix="🔐"
            />
          </FormItem>
        </Card>

        {/* 状态设置分组 */}
        <Card 
          title={
            <Space>
              <GlobalOutlined style={{ color: '#722ed1' }} />
              状态设置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="应用的运行状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem 
            name="isValid" 
            label={
              <Space>
                应用状态
                <Tooltip title="应用是否启用">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              style={{ width: '100%' }}
              placeholder="请选择应用状态"
            >
              <Option value="1">有效</Option>
              <Option value="0">无效</Option>
            </Select>
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () => {
    return (
      <Space>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>
          取消
        </Button>
        <Button type="primary" onClick={() => handleNext()}>
          保存配置
        </Button>
      </Space>
    );
  };

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          应用配置
        </Space>
      }
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          appName: props.values.appName,
          appDesc: props.values.appDesc,
          appType: props.values.appType,
          appCode: props.values.appCode,
          appSecret: props.values.appSecret,
          isValid: props.values.isValid,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
