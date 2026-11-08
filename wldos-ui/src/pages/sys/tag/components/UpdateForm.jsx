import React from 'react';
import {Button, Form, Input, Card, Space, Tooltip} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {TagOutlined, KeyOutlined, InfoCircleOutlined, EditOutlined} from '@ant-design/icons';

const FormItem = Form.Item;
const {TextArea} = Input;

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
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <TagOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="标签的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="name"
            label={
              <Space>
                标签名称
                <Tooltip title="标签的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入标签名称，不能为空，最多50个字！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<TagOutlined />}
              placeholder="请输入标签名称，最多50个字"
            />
          </FormItem>

          <FormItem
            name="slug"
            label={
              <Space>
                标签别名
                <Tooltip title="标签的URL别名，用于SEO和友好链接">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入标签别名，不能为空，最多200个字符！',
                max: 200,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入标签别名，最多200个字符"
            />
          </FormItem>

          <FormItem
            name="description"
            label={
              <Space>
                标签描述
                <Tooltip title="标签的详细描述，用于说明标签用途">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 200,
                message: '标签描述最多200个字符！',
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入标签描述，最多200个字符"
              style={{ resize: 'vertical' }}
            />
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
      width={800}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          标签配置
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
          name: props.values.name,
          slug: props.values.slug,
          description: props.values.description,
          id: props.values.id,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
