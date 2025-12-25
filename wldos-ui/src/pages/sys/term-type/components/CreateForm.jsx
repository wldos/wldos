import React, { useEffect } from 'react';
import {Button, Form, Input, Select, TreeSelect, Card, Space, Tooltip, Alert} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  FolderOutlined,
  KeyOutlined,
  SettingOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

const CreateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleModalVisible,
    modalVisible,
    categoryList,
    parentId,
    structureType = 'tree',
    termType // 分类类型信息（必传）
  } = props;

  // 当 modalVisible 或 categoryList 变化时，更新表单初始值
  useEffect(() => {
    if (modalVisible) {
      form.setFieldsValue({
        ...(termType?.code === 'category' ? { infoFlag: '1' } : {}),
        parentId: parentId || '0'
      });
    }
  }, [modalVisible, categoryList, parentId, termType, form]);

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      await handleAdd(fieldsValue);
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  const getDescription = () => {
    if (termType?.code === 'category') {
      return '创建新的内容分类，配置分类的基本信息、层级关系和发布状态。分类用于内容组织和导航。';
    } else if (termType?.code === 'plugin') {
      return '创建新的插件分类，配置分类的基本信息和层级关系。插件分类用于组织和管理插件。';
    } else if (termType?.code === 'tag') {
      return '创建新的标签，配置标签的基本信息。标签用于内容的标记和检索。';
    } else {
      return `创建新的${termType?.name || '分类'}，配置${termType?.name || '分类'}的基本信息${termType?.structureType === 'tree' ? '和层级关系' : ''}。`;
    }
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message={`新建${termType?.name || '分类'}`}
          description={getDescription()}
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <FolderOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="分类的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="name"
            label={
              <Space>
                分类名称
                <Tooltip title="分类的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入分类名称，不能为空，最多50个字！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<FolderOutlined />}
              placeholder="请输入分类名称，最多50个字"
            />
          </FormItem>

          <FormItem
            name="slug"
            label={
              <Space>
                分类别名
                <Tooltip title="分类的URL别名，用于SEO和友好链接">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入分类别名，不能为空，最多200个字符！',
                max: 200,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入分类别名，最多200个字符"
            />
          </FormItem>

          <FormItem
            name="description"
            label={
              <Space>
                分类描述
                <Tooltip title="分类的详细描述，用于说明分类用途">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 200,
                message: '分类描述最多200个字符！',
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入分类描述，最多200个字符"
              style={{ resize: 'vertical' }}
            />
          </FormItem>
        </Card>

        {/* 分类配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              分类配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="分类的层级和状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          {structureType === 'tree' && (
            <FormItem
              name="parentId"
              label={
                <Space>
                  父级分类
                  <Tooltip title="分类的上级分类，用于建立分类层级关系">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <TreeSelect
                showSearch
                treeData={categoryList || []}
                treeDefaultExpandAll
                allowClear
                dropdownStyle={{ maxHeight: 400, overflow: 'auto'}}
                placeholder="请选择父级分类"
                treeNodeFilterProp="title"
                style={{ width: '100%' }}
              />
            </FormItem>
          )}

          {/* 信息发布状态：仅在内容分类时显示 */}
          {termType?.code === 'category' && (
            <FormItem
              name="infoFlag"
              label={
                <Space>
                  信息发布状态
                  <Tooltip title="控制分类下的内容是否可以发布">
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
          )}
        </Card>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleModalVisible()}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );

  return (
    <FullscreenModal
      width={640}
      bodyStyle={{
        padding: '32px 40px 48px',
      }}
      destroyOnClose
      title={`新建${termType?.name || '分类'}`}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          ...(termType?.code === 'category' ? { infoFlag: '1' } : {}),
          parentId: parentId || '0'
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default CreateForm;

