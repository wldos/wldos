import React from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip, InputNumber} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  TeamOutlined,
  KeyOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  NumberOutlined,
  CheckCircleOutlined,
  EditOutlined,
  BankOutlined,
  ApartmentOutlined
} from '@ant-design/icons';

const FormItem = Form.Item;
const {Option} = Select;

/** 会员分群在库内 org_type 仍为 platform，界面上展示为「会员分群」 */
const MEMBER_SEGMENT_TYPE_LABEL = '会员分群';

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
    roles,
    orgTypes,
  } = props;

  // 系统内置分群与自建分群都按分群维护：类型只读、不挂上级
  const isSegmentRecord = values?.systemBuiltIn || values?.orgType === 'segment';
  const nameLabel = isSegmentRecord ? '分群名称' : '组织名称';
  const codeLabel = isSegmentRecord ? '分群编码' : '组织编码';
  // 收窄后可选类型；若当前行的历史 orgType 不在收窄列表里（如遗留测试数据），补回一项，
  // 避免编辑时下拉找不到当前值而显示空白——仅这一条是兜底，不作为新的可选入口
  const narrowedOrgTypes = (orgTypes || []).filter((t) => t.value !== 'segment');
  const hasCurrentType = narrowedOrgTypes.some((t) => t.value === values?.orgType);
  const selectableOrgTypes = !values?.orgType || hasCurrentType
    ? narrowedOrgTypes
    : [...narrowedOrgTypes, { label: values.orgType, value: values.orgType }];
  const showOrgTypeSelect = !isSegmentRecord && selectableOrgTypes.length > 1;
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

          {isSegmentRecord ? (
            <>
              <FormItem
                label={
                  <Space>
                    组织类型
                    <Tooltip title="系统内置会员分群，类型不可改">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Input disabled value={MEMBER_SEGMENT_TYPE_LABEL} />
              </FormItem>
              <FormItem name="orgType" hidden>
                <Input />
              </FormItem>
            </>
          ) : !showOrgTypeSelect ? (
            <FormItem name="orgType" hidden>
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
            >
              <Select
                placeholder="请选择组织类型"
                style={{ width: '100%' }}
                showSearch
                filterOption={(input, option) =>
                  String(option?.label ?? option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                }
                options={selectableOrgTypes}
              />
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
          {!isSegmentRecord && (
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
              options={roles}
            />
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

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" icon={<EditOutlined />} onClick={handleNext}>保存配置</Button>
      </>
    );

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
          {isSegmentRecord ? '分群配置' : '组织配置'}
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
          orgName: props.values.orgName,
          orgCode: props.values.orgCode,
          orgType: props.values.orgType,
          parentId: props.values.parentId || '0',
          archId: props.values.archId,
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
