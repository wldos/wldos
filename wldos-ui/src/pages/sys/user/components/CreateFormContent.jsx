import React, { forwardRef, useCallback, useImperativeHandle } from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, Alert } from 'antd';
import {
  UserOutlined,
  KeyOutlined,
  MailOutlined,
  PhoneOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  PlusOutlined,
  LockOutlined
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
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel
  } = props;

  const applyServerValidationErrors = useCallback(
    (msg) => {
      if (!msg || typeof msg !== 'string') return false;
      const fieldErrors = [];
      for (const segment of msg.split(';')) {
        const part = segment.trim();
        if (!part) continue;
        const idx = part.indexOf(':');
        if (idx === -1) continue;
        const name = part.slice(0, idx).trim();
        const text = part.slice(idx + 1).trim();
        if (name && text) {
          fieldErrors.push({ name, errors: [text] });
        }
      }
      if (fieldErrors.length) {
        form.setFields(fieldErrors);
        return true;
      }
      return false;
    },
    [form],
  );

  const handleNext = useCallback(async () => {
    let fieldsValue;
    try {
      fieldsValue = await form.validateFields();
    } catch {
      return;
    }
    const payload = { ...fieldsValue };
    if (!payload.mobile || String(payload.mobile).trim() === '') {
      delete payload.mobile;
    }
    await handleAdd(payload);
  }, [form, handleAdd]);

  useImperativeHandle(
    ref,
    () => ({
      submit: handleNext,
      applyServerValidationErrors,
    }),
    [handleNext, applyServerValidationErrors],
  );

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="新建用户"
          description="创建新的用户账户，配置用户的基本信息、登录凭证和状态设置。用户是系统的主要使用者。"
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
                message: '请输入昵称，不能为空，最多50个字！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入昵称，最多50个字"
            />
          </FormItem>

          <FormItem
            name="loginName"
            label={
              <Space>
                登录账号
                <Tooltip title="用户的登录用户名，建议使用邮箱">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入登录账号，不能为空，最多30个字符！',
                max: 30,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入登录账号，最多30个字符"
            />
          </FormItem>

          <FormItem
            name="passwd"
            label={
              <Space>
                登录密码
                <Tooltip title="用户的登录密码，长度 6～100 个字符">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入登录密码！',
              },
              {
                min: 6,
                max: 100,
                message: '密码长度必须在6-100之间',
              },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入登录密码，6～100 个字符"
            />
          </FormItem>

          <FormItem
            name="confirm"
            label={
              <Space>
                确认密码
                <Tooltip title="请再次输入登录密码">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            dependencies={['passwd']}
            rules={[
              {
                required: true,
                message: '确认密码不能为空',
              },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('passwd') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请再次输入登录密码"
            />
          </FormItem>

          <FormItem
            name="email"
            label={
              <Space>
                邮箱地址
                <Tooltip title="用户的邮箱地址，用于接收通知">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '邮箱不能为空',
              },
              {
                type: 'email',
                message: '邮箱格式不正确',
              },
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="请输入邮箱地址"
            />
          </FormItem>

          <FormItem
            name="mobile"
            label={
              <Space>
                手机号码
                <Tooltip title="用户的手机号码，用于接收短信通知（选填，须 11 位）">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                validator(_, value) {
                  const v = value == null ? '' : String(value).trim();
                  if (!v) return Promise.resolve();
                  if (/^1[3-9]\d{9}$/.test(v)) return Promise.resolve();
                  return Promise.reject(new Error('手机号必须是11位数字'));
                },
              },
            ]}
          >
            <Input
              prefix={<PhoneOutlined />}
              placeholder="请输入手机号码"
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
              <Option value="1">正常</Option>
              <Option value="0">禁用</Option>
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
        status: '1'
      }}
    >
      {renderContent()}
    </Form>
  );
});

CreateFormContent.displayName = 'CreateFormContent';

export default CreateFormContent;
