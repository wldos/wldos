import React, {useState, useEffect} from 'react';
import {Button, Form, Input, Select, TreeSelect, Space, Typography, Row, Col, Card, Divider, Tooltip, Alert, Radio} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  SettingOutlined,
  PlusOutlined,
  FileTextOutlined,
  AppstoreOutlined,
  GlobalOutlined,
  MenuOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  KeyOutlined,
  FolderOutlined,
  TagOutlined
} from '@ant-design/icons';
import IconPickerModal from '@/components/IconPickerModal';
import { renderIcon } from '@/utils/iconLibrary';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;
const {Text} = Typography;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 18,
  },
};

const SimpleForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleSimpleAdd,
    onCancel: handleModalVisible,
    modalVisible,
    apps,
    templates,
    categories,
    menus,
    parentId
  } = props;

  const [termValue, setTermValue] = useState(undefined);
  const [descriptionLength, setDescriptionLength] = useState(0);

  const [iconType, setIconType] = useState('antd');
  const [selectedAntdName, setSelectedAntdName] = useState('');
  const [customIcon, setCustomIcon] = useState('');
  const [customUrl, setCustomUrl] = useState('');

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();

    // 处理图标字段为字符串
    let iconString = '';
    if (iconType === 'antd') {
      iconString = selectedAntdName || fieldsValue.icon?.name || '';
    } else if (iconType === 'custom') {
      iconString = customIcon || '';
    } else if (iconType === 'url') {
      iconString = customUrl || '';
    }

    const processedFields = { ...fieldsValue, icon: iconString };

    handleSimpleAdd(processedFields);
  };

  const typeProps = {
    showSearch: true,
    treeData: categories?? [],
    value: termValue,
    onChange: setTermValue,
    treeDefaultExpandAll: true,
    treeLine: true,
    placeholder: '请选择',
    treeNodeFilterProp: 'title',
    dropdownStyle: { maxHeight: 400, overflow: 'auto'},
  };

  const [menuId, setMenuId] = useState(undefined);
  const [iconPickerVisible, setIconPickerVisible] = useState(false);
  const [currentIcon, setCurrentIcon] = useState(null);

  // 监听表单图标字段变化，同步到 currentIcon 状态
  useEffect(() => {
    const iconValue = form.getFieldValue('icon');
    if (iconValue && !currentIcon) {
      setCurrentIcon(iconValue);
    }
  }, [form, currentIcon]);
  const menuProps = {
    showSearch: true,
    treeData: menus?? [],
    value: menuId,
    onChange: setMenuId,
    treeDefaultExpandAll: true,
    treeLine: true,
    placeholder: '请选择',
    treeNodeFilterProp: 'title',
    dropdownStyle: { maxHeight: 400, overflow: 'auto'},
  };

  const renderContent = () => {
    return (
      <div>
        {/* 页面说明 */}
        <Alert
          message="模板菜单创建"
          description="通过预定义模板快速创建菜单，系统将根据模板自动配置相关参数，简化菜单创建流程。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息分组 */}
        <Card
          title={
            <Space>
              <FileTextOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="菜单的基本信息配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="resName"
                label={
                  <Space>
                    资源名称
                    <Tooltip title="菜单显示名称，用于导航和权限控制">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '请输入资源名称'
                  },
                  {
                    max: 25,
                    type: 'string',
                    message: '最多25个字'
                  }
                ]}
              >
                <Input
                  prefix={<FileTextOutlined />}
                  placeholder="请输入，不能为空，最多25个字！"
                />
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                name="tempType"
                label={
                  <Space>
                    模板
                    <Tooltip title="选择预定义的菜单模板，系统将自动配置相关参数">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '请设置模板'
                  }
                ]}
              >
                <Select
                  prefix={<TagOutlined />}
                  placeholder="请选择模板类型"
                  style={{ width: '100%' }}
                  filterOption={(input, option) =>
                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                  options={templates}
                />
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* 菜单配置分组 */}
        <Card
          title={
            <Space>
              <MenuOutlined style={{ color: '#52c41a' }} />
              菜单配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="菜单的显示和导航配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="termTypeId"
                label={
                  <Space>
                    分类目录
                    <Tooltip title="选择菜单所属的分类目录，用于组织管理">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <TreeSelect
                  {...typeProps}
                  prefix={<FolderOutlined />}
                  placeholder="请选择分类目录"
                />
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                label={
                  <Space>
                    菜单图标
                    <Tooltip title="选择菜单图标，用于导航显示">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Radio.Group value={iconType} onChange={(e) => setIconType(e.target.value)}>
                    <Radio value="antd">内置</Radio>
                    <Radio value="custom">自定义</Radio>
                    <Radio value="url">链接</Radio>
                  </Radio.Group>
                  {iconType === 'antd' && (
                    <Space>
                      <div style={{ width: 200, height: 32, border: '1px solid #d9d9d9', borderRadius: '6px', display: 'flex', alignItems: 'center', padding: '0 11px', backgroundColor: '#fafafa' }}>
                        {selectedAntdName ? renderIcon({ type: 'antd', name: selectedAntdName }) : <span style={{ color: '#bfbfbf' }}>请选择图标</span>}
                      </div>
                      <Button icon={<SettingOutlined />} onClick={() => setIconPickerVisible(true)} title="配置图标" />
                    </Space>
                  )}
                  {iconType === 'custom' && (
                    <Input placeholder="输入自定义图标名称" value={customIcon} onChange={(e) => setCustomIcon(e.target.value)} />
                  )}
                  {iconType === 'url' && (
                    <Input placeholder="输入图标URL" value={customUrl} onChange={(e) => setCustomUrl(e.target.value)} />
                  )}
                </Space>
              </FormItem>
            </Col>
          </Row>

          {/* 辅助字段 */}
          <FormItem name="icon" hidden><Input /></FormItem>
        </Card>

        {/* 技术配置分组 */}
        <Card
          title={
            <Space>
              <KeyOutlined style={{ color: '#fa8c16' }} />
              技术配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="菜单的技术参数配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="reqMethod"
                label={
                  <Space>
                    请求方法
                    <Tooltip title="HTTP请求方法，用于API接口">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  prefix={<KeyOutlined />}
                  placeholder="请选择请求方法"
                  style={{ width: '100%' }}
                >
                  <Option value="GET">GET</Option>
                  <Option value="POST">POST</Option>
                  <Option value="PUT">PUT</Option>
                  <Option value="DELETE">DELETE</Option>
                </Select>
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                name="target"
                label={
                  <Space>
                    打开方式
                    <Tooltip title="菜单链接的打开方式">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  prefix={<LinkOutlined />}
                  placeholder="请选择打开方式"
                  style={{ width: '100%' }}
                >
                  <Option value="_self">self</Option>
                  <Option value="_blank">blank</Option>
                  <Option value="_parent">parent</Option>
                  <Option value="_top">top</Option>
                </Select>
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* 应用配置分组 */}
        <Card
          title={
            <Space>
              <AppstoreOutlined style={{ color: '#722ed1' }} />
              应用配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="菜单的应用归属和层级关系">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="parentId"
                label={
                  <Space>
                    上级菜单
                    <Tooltip title="选择父级菜单，建立菜单层级关系">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <TreeSelect
                  {...menuProps}
                  prefix={<MenuOutlined />}
                  placeholder="请选择上级菜单"
                />
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem
                name="appId"
                label={
                  <Space>
                    归属应用
                    <Tooltip title="选择菜单所属的应用系统">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  prefix={<AppstoreOutlined />}
                  placeholder="请选择归属应用"
                  style={{ width: '100%' }}
                  filterOption={(input, option) =>
                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                  options={apps}
                />
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* 描述信息分组 - 全宽显示 */}
        <Card
          title={
            <Space>
              <FileTextOutlined style={{ color: '#13c2c2' }} />
              描述信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="资源的详细描述信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="remark"
            label={
              <Space>
                资源描述
                <Tooltip title="资源的详细描述信息">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 50,
                type: 'string',
                message: '最多50个字',
              },
            ]}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <TextArea
              rows={3}
              placeholder="请输入资源描述，例如：系统核心功能模块、用户管理相关页面等"
              showCount
              maxLength={50}
              style={{
                resize: 'vertical',
                minHeight: '80px'
              }}
            />
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <Space>
        <Button onClick={() => handleModalVisible()}>取消</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleNext()}>
          创建菜单
        </Button>
      </Space>
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
          <PlusOutlined style={{ color: '#1890ff' }} />
          新增菜单
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
          reqMethod: 'GET',
          target: '_self',
          parentId,
        }}
      >
        {renderContent()}
      </Form>

      <IconPickerModal
        visible={iconPickerVisible}
        onCancel={() => setIconPickerVisible(false)}
        onOk={(iconData) => {
          setCurrentIcon(iconData);
          setSelectedAntdName(iconData?.name || iconData?.value || '');
          form.setFieldsValue({
            icon: iconData,
            iconName: iconData?.name || iconData?.value || ''
          });
          setIconPickerVisible(false);
        }}
        value={currentIcon || form.getFieldValue('icon') || {}}
      />
    </FullscreenModal>
  );
};

export default SimpleForm;
