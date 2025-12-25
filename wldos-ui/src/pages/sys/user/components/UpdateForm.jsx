import React from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip, Alert} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  UserOutlined,
  KeyOutlined,
  MailOutlined,
  PhoneOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  EditOutlined,
  LockOutlined
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
  } = props;

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      const value = {...values, ...fieldsValue};
      await handleUpdate(value);
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="用户配置"
          description="修改用户的基本信息、登录凭证和状态设置。用户是系统的主要使用者。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <UserOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="用户的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="nickname"
            label={
              <Space>
                昵称
                <Tooltip title="用户的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入昵称，不能为空，最多60个字！',
                max: 60,
              },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入昵称，最多60个字"
            />
          </FormItem>

          <FormItem
            name="loginName"
            label={
              <Space>
                登录账号
                <Tooltip title="用户的登录用户名">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入登录账号，最多60位！',
                max: 60,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入登录账号，最多60位"
            />
          </FormItem>
        </Card>

        {/* 用户配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              用户配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="用户的状态和备注信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="remark"
            label={
              <Space>
                备注信息
                <Tooltip title="用户的备注信息，用于管理员记录">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 200,
                message: '备注信息最多200个字符！',
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入备注信息，最多200个字符"
              style={{ resize: 'vertical' }}
            />
          </FormItem>

          <FormItem
            name="status"
            label={
              <Space>
                用户状态
                <Tooltip title="用户的启用状态">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择用户状态"
              style={{ width: '100%' }}
            >
              <Option value="normal">正常</Option>
              <Option value="locked">已锁定</Option>
              <Option value="cancelled">已注销</Option>
            </Select>
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" icon={<EditOutlined />} onClick={handleNext}>保存配置</Button>
      </>
    );
  };

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          用户配置
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
          nickname: props.values.nickname,
          remark: props.values.remark,
          loginName: props.values.loginName,
          status: props.values.status,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
