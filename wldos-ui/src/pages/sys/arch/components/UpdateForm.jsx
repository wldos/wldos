import React from 'react';
import {Button, Form, Input, Modal, Select, Card, Space, Tooltip, Alert} from 'antd';
import CompanyTreeSelect from '@/components/TreeSelects/CompanyTreeSelect';
import ArchTreeSelect from '@/components/TreeSelects/ArchTreeSelect';
import FullscreenModal from '@/components/FullscreenModal';
import {
  EditOutlined,
  ApartmentOutlined,
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
          message="体系配置"
          description="配置体系的基本信息、层级关系和状态设置。体系是组织架构的重要组成部分。"
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
                message: '请输入名称，不能为空，最多12个字！',
                max: 12,
              },
            ]}
          >
            <Input 
              prefix={<ApartmentOutlined />}
              placeholder="请输入体系名称，最多12个字"
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
                message: '请输入体系编码，最多50位！',
                max: 50,
              },
            ]}
          >
            <Input 
              prefix={<KeyOutlined />}
              placeholder="请输入体系编码，最多50位"
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
                message: '最多120个字！',
                max: 120,
              },
            ]}
          >
            <TextArea 
              rows={3}
              placeholder="请输入体系描述，最多120个字"
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
          style={{ marginBottom: '16px' }}
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
              usePaged={true}
            />
          </FormItem>

          <FormItem 
            name="parentId" 
            label={
              <Space>
                上级体系
                <Tooltip title="体系的上级体系，用于层级管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <ArchTreeSelect
              placeholder="请选择上级体系"
              includeTop
              companyId={props.values.comId}
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
          体系配置
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
          archName: props.values.archName,
          archCode: props.values.archCode,
          comId: props.values.comId,
          parentId: props.values.parentId,
          archDesc: props.values.archDesc,
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