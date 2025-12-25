import React from 'react';
import {Button, Form, Input, Select, TreeSelect, Card, Space, Tooltip, Alert, InputNumber} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {FolderOutlined, KeyOutlined, SettingOutlined, InfoCircleOutlined, EditOutlined} from '@ant-design/icons';

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
    categoryList,
    structureType = 'tree',
    termType // 分类类型信息（必传）
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const getDescription = () => {
    if (termType?.code === 'category') {
      return '编辑分类信息，修改分类的基本信息、层级关系和发布状态。';
    } else if (termType?.code === 'plugin') {
      return '编辑插件分类信息，修改分类的基本信息和层级关系。';
    } else if (termType?.code === 'tag') {
      return '编辑标签信息，修改标签的基本信息。';
    } else {
      return `编辑${termType?.name || '分类'}信息，修改${termType?.name || '分类'}的基本信息${termType?.structureType === 'tree' ? '和层级关系' : ''}。`;
    }
  };

  const renderContent = () => {
    return (
      <>
        {/* 页面说明 */}
        <Alert
          message={`编辑${termType?.name || '分类'}`}
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

          <FormItem
            name="displayOrder"
            label={
              <Space>
                展示顺序
                <Tooltip title="分类的显示顺序，数字越小越靠前">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入展示顺序，范围1-100！',
              },
            ]}
          >
            <InputNumber
              min={1}
              max={100}
              placeholder="请输入展示顺序，范围1-100"
              style={{ width: '100%' }}
            />
          </FormItem>

          <FormItem
            name="isValid"
            label={
              <Space>
                分类状态
                <Tooltip title="控制分类是否有效">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择分类状态"
              style={{ width: '100%' }}
            >
              <Option value="1">有效</Option>
              <Option value="0">无效</Option>
            </Select>
          </FormItem>

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
      </>
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
          {`编辑${termType?.name || '分类'}`}
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
          name: values?.name,
          displayOrder: values?.displayOrder,
          slug: values?.slug,
          isValid: values?.isValid,
          description: values?.description,
          parentId: values?.parentId || '0',
          infoFlag: values?.infoFlag,
          id: values?.id,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;

