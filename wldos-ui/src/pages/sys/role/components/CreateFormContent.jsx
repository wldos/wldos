import React from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, Alert, InputNumber } from 'antd';
import {
  UserOutlined,
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
    roles = [],
    currentRecord = null // 接收当前记录信息
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
          message="新建角色"
          description="创建新的角色，配置角色的基本信息、类型和权限设置。角色是权限管理的基础单位。"
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
            <Tooltip title="角色的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="roleName"
            label={
              <Space>
                角色名称
                <Tooltip title="角色的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入名称，不能为空，最多25个字！',
                max: 25,
              },
            ]}
          >
            <Input 
              prefix={<UserOutlined />}
              placeholder="请输入角色名称，最多25个字"
            />
          </FormItem>

          <FormItem
            name="roleCode"
            label={
              <Space>
                角色编码
                <Tooltip title="角色的唯一标识，用于系统识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入编码，不能为空，最多32位！',
                max: 32,
              },
            ]}
          >
            <Input 
              prefix={<KeyOutlined />}
              placeholder="请输入角色编码，最多32位"
            />
          </FormItem>

          <FormItem
            name="roleDesc"
            label={
              <Space>
                角色描述
                <Tooltip title="角色的详细说明，帮助理解角色用途">
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
              placeholder="请输入角色描述，最多150个字"
              style={{ resize: 'vertical' }}
            />
          </FormItem>
        </Card>

        {/* 角色配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              角色配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="角色的类型和层级配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem 
            name="roleType" 
            label={
              <Space>
                角色类型
                <Tooltip title="角色的分类，影响权限范围">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择角色类型"
              style={{ width: '100%' }}
            >
              <Option value="sys_role">系统角色</Option>
              <Option value="subject">社会主体</Option>
              <Option value="tal_role">租户角色</Option>
            </Select>
          </FormItem>

          <FormItem 
            name="parentId" 
            label={
              <Space>
                父角色
                <Tooltip title="角色的上级角色，用于角色层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择父角色"
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
                <Tooltip title="角色在列表中的显示顺序，1-100">
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
                <Tooltip title="角色的启用状态">
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
        <Button type="primary" icon={<PlusOutlined />} onClick={handleNext}>创建角色</Button>
      </>
    );

  return (
    <Form
      {...formLayout}
      form={form}
      initialValues={{
        isValid: '1',
        displayOrder: 1,
        // 根据当前记录设置初始值
        parentId: currentRecord?.parentId || '0',
        roleType: currentRecord?.roleType || undefined
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
