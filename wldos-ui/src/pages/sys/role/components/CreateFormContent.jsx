import React from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, InputNumber } from 'antd';
import { useIntl } from 'umi';
import { DISPLAY_ORDER_MAX } from '@/utils/displayOrder';
import { DATA_POLICY_TIP_ID, DATA_POLICY_TIP_PROPS } from '../_shared/dataPolicyTip';
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
  const intl = useIntl();
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel,
    roles = [],
    comList = [],
    currentRecord = null,
    isPlatformAdmin = false,
    defaultComId,
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
              {isPlatformAdmin ? <Option value="sys_role">系统角色</Option> : null}
              <Option value="subject">社会主体</Option>
              <Option value="tal_role">租户角色</Option>
            </Select>
          </FormItem>

          <FormItem
            name="dataPolicy"
            label={
              <Space>
                数据策略
                <Tooltip
                  title={intl.formatMessage({ id: DATA_POLICY_TIP_ID })}
                  {...DATA_POLICY_TIP_PROPS}
                >
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select allowClear placeholder="默认按身份推断" style={{ width: '100%' }}>
              <Option value="SELF">仅本人</Option>
              <Option value="ORG">本组织</Option>
              <Option value="ORG_SUBTREE">组织及下级</Option>
              <Option value="TENANT">本租户</Option>
              <Option value="TENANT_DOMAIN">本租户本站点</Option>
              <Option value="DOMAIN">本站点</Option>
              <Option value="ALL">全部</Option>
            </Select>
          </FormItem>

          <FormItem
            name="comId"
            label={
              <Space>
                归属公司
                <Tooltip title="角色所属的公司；不选择时超管创建落平台，租户管理员创建按自身所属租户">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择归属公司（不选默认平台/自身租户）"
              style={{ width: '100%' }}
              allowClear
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={comList}
            />
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
              showSearch
              optionFilterProp="label"
              options={roles}
            />
          </FormItem>

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="角色在列表中的显示顺序，1-9999">
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
              placeholder="请输入数字，1-9999"
              min={1}
              max={DISPLAY_ORDER_MAX}
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
        parentId: currentRecord?.parentId || '0',
        roleType: currentRecord?.roleType || undefined,
        comId: currentRecord?.comId ?? defaultComId,
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
