import React from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip, Alert} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {TagOutlined, KeyOutlined, SettingOutlined, InfoCircleOutlined, PlusOutlined} from '@ant-design/icons';

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

const CreateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleModalVisible,
    modalVisible,
    parentId
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    handleAdd(fieldsValue);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="新建标签"
          description="创建新的内容标签，配置标签的基本信息和发布状态。标签用于内容标记和分类。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

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

        {/* 标签配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              标签配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="标签的状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="infoFlag"
            label={
              <Space>
                信息发布状态
                <Tooltip title="控制标签下的内容是否可以发布">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择信息发布状态"
              style={{ width: '100%' }}
            >
              <Option value="1">开启</Option>
              <Option value="0">关闭</Option>
            </Select>
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleModalVisible()}>取消</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleNext()}>创建标签</Button>
      </>
    );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        maxHeight: '70vh',
        overflowY: 'auto',
        padding: '24px'
      }}
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#1890ff' }} />
          新建标签
        </Space>
      }
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          infoFlag: '1',
          parentId
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default CreateForm;
