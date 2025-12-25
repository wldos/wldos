import React from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, Alert, InputNumber } from 'antd';
import {
  TeamOutlined,
  KeyOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  NumberOutlined,
  CheckCircleOutlined,
  PlusOutlined,
  BankOutlined,
  ApartmentOutlined
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
    archs = [],
    orgTypes = [],
    comList = [],
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
          message="新建组织"
          description="创建新的组织机构，配置组织的基本信息、层级关系和状态设置。组织是业务域运营的人员管理单位。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <TeamOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="组织的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="orgName"
            label={
              <Space>
                组织名称
                <Tooltip title="组织的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入组织名称，不能为空，最多60个字！',
                max: 60,
              },
            ]}
          >
            <Input
              prefix={<TeamOutlined />}
              placeholder="请输入组织名称，最多60个字"
            />
          </FormItem>

          <FormItem
            name="orgCode"
            label={
              <Space>
                组织编码
                <Tooltip title="组织的唯一标识，用于系统识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入组织编码，不能为空，最多32位！',
                max: 32,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入组织编码，最多32位"
            />
          </FormItem>

          <FormItem
            name="orgType"
            label={
              <Space>
                组织类型
                <Tooltip title="组织的分类，影响权限范围">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请选择组织类型！',
              },
            ]}
          >
            <Select
              placeholder="请选择组织类型"
              style={{ width: '100%' }}
            >
              {orgTypes.map(type => (
                <Option key={type.value} value={type.value}>
                  {type.label}
                </Option>
              ))}
            </Select>
          </FormItem>
        </Card>

        {/* 组织配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              组织配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="组织的层级和状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="comId"
            label={
              <Space>
                归属公司
                <Tooltip title="组织所属的公司，用于公司层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请选择归属公司！',
              },
            ]}
          >
            <Select
              placeholder="请选择归属公司"
              style={{ width: '100%' }}
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={comList}
            />
          </FormItem>

          <FormItem
            name="archId"
            label={
              <Space>
                归属体系
                <Tooltip title="组织所属的体系，用于体系层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请选择归属体系！',
              },
            ]}
          >
            <Select
              placeholder="请选择归属体系"
              style={{ width: '100%' }}
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={archs}
            />
          </FormItem>

          <FormItem
            name="parentId"
            label={
              <Space>
                上级组织
                <Tooltip title="组织的上级组织，用于组织层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择上级组织"
              style={{ width: '100%' }}
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {roles && Array.isArray(roles) ? roles.map(item => (
                <Option key={item.value} value={item.value}>
                  {item.label}
                </Option>
              )) : Object.keys(roles || {}).map(key => (
                <Option key={key} value={key}>
                  {roles[key].text || roles[key].label}
                </Option>
              ))}
            </Select>
          </FormItem>

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="组织在列表中的显示顺序，1-100">
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
                <Tooltip title="组织的启用状态">
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
        // 根据当前记录设置初始值
        parentId: currentRecord?.parentId || '0',
        orgType: currentRecord?.orgType || undefined,
        comId: currentRecord?.comId || undefined,
        archId: currentRecord?.archId || undefined
      }}
    >
      {renderContent()}
    </Form>
  );
};

export default CreateFormContent;
