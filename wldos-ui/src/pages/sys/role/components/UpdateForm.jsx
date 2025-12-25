import React from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip, Alert, Divider} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  EditOutlined,
  UserOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  FileTextOutlined,
  NumberOutlined,
  CheckCircleOutlined
} from '@ant-design/icons';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    roles,
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="角色配置"
          description="配置角色的基本信息、类型和权限设置。角色是权限管理的基础单位。"
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
                message: '展示顺序1~100！',
                pattern: '^([1-9]|[1-9]\\d|100)$',
                max: 3,
              },
            ]}
          >
            <Input 
              prefix={<NumberOutlined />}
              placeholder="请输入数字，1-100"
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
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" icon={<EditOutlined />} onClick={() => handleNext()}>保存配置</Button>
      </>
    );

  return (
    <FullscreenModal
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          角色配置
        </Space>
      }
      width={800}
      bodyStyle={{
        maxHeight: '70vh',
        overflowY: 'auto',
        padding: '24px'
      }}
      destroyOnClose
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          roleName: props.values.roleName,
          roleCode: props.values.roleCode,
          roleType: props.values.roleType,
          parentId: props.values.parentId,
          roleDesc: props.values.roleDesc,
          isValid: props.values.isValid,
          displayOrder: props.values.displayOrder,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
