import React, { forwardRef, useImperativeHandle } from 'react';
import { Form, Input, Select, Button, Card, Space, Tooltip, InputNumber } from 'antd';
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

const CreateFormContent = forwardRef((props, ref) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel,
    roles = [],
    orgTypes = [],
    comList = [],
    currentRecord = null,
    defaultComId,
    segmentMode = false,
  } = props;

  // 分群页固定按分群类型创建；编制树页不提供分群类型，避免新建后只出现在分群列表
  const effectiveOrgTypes = segmentMode ? orgTypes : orgTypes.filter((t) => t.value !== 'segment');
  // 收窄后可选类型只剩「组织机构」一项时（租户/站点管理员均如此，仅平台超管额外可见「系统用户组」），
  // 下拉本身不再传达任何选择意义，直接隐藏并落默认值，避免用户误以为还需要做选择
  const showOrgTypeSelect = !segmentMode && effectiveOrgTypes.length > 1;
  const singleOrgType = effectiveOrgTypes.length === 1 ? effectiveOrgTypes[0].value : 'org';
  const nameLabel = segmentMode ? '分群名称' : '组织名称';
  const codeLabel = segmentMode ? '分群编码' : '组织编码';

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      await handleAdd(fieldsValue);
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  // 暴露submit方法给父组件（CreateForm 的弹窗底部按钮通过 ref 触发校验+提交）
  useImperativeHandle(ref, () => ({
    submit: handleNext
  }));

  const renderContent = () => {
    return (
      <div>
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
                {nameLabel}
                <Tooltip title="组织的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: `请输入${nameLabel}，不能为空，最多60个字！`,
                max: 60,
              },
            ]}
          >
            <Input
              prefix={<TeamOutlined />}
              placeholder={`请输入${nameLabel}，最多60个字`}
            />
          </FormItem>

          <FormItem
            name="orgCode"
            label={
              <Space>
                {codeLabel}
                <Tooltip title="组织的唯一标识，用于系统识别">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: `请输入${codeLabel}，不能为空，最多32位！`,
                max: 32,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder={`请输入${codeLabel}，最多32位`}
            />
          </FormItem>

          {segmentMode ? (
            <FormItem name="orgType" hidden initialValue="segment">
              <Input />
            </FormItem>
          ) : !showOrgTypeSelect ? (
            <FormItem name="orgType" hidden initialValue={singleOrgType}>
              <Input />
            </FormItem>
          ) : (
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
              showSearch
              filterOption={(input, option) =>
                String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
              }
            >
              {effectiveOrgTypes.map(type => (
                <Option key={type.value} value={type.value}>
                  {type.label}
                </Option>
              ))}
            </Select>
          </FormItem>
          )}
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
          {!segmentMode && comList.length > 1 && (
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
              showSearch
              filterOption={(input, option) =>
                String(option?.label ?? option?.children ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={comList}
            />
          </FormItem>
          )}

          {!segmentMode && (
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
              showSearch
              filterOption={(input, option) =>
                String(option?.label ?? option?.children ?? '').toLowerCase().includes(input.toLowerCase())
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
          )}

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="组织在列表中的显示顺序，1-9999">
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
              max={9999}
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
        parentId: currentRecord?.parentId || '0',
        orgType: segmentMode ? 'segment' : (currentRecord?.orgType || (effectiveOrgTypes.find((t) => t.value === 'org') ? 'org' : undefined)),
        comId: currentRecord?.comId ?? defaultComId,
      }}
    >
      {renderContent()}
    </Form>
  );
});

export default CreateFormContent;
