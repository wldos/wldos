import React, {useState, useEffect} from 'react';
import {Button, Form, Input, Select, TreeSelect, Space, Typography, Row, Col, Card, Divider, Tooltip, Alert, Radio} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import {
  SettingOutlined,
  AppstoreOutlined,
  FileTextOutlined,
  ConfigOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  GlobalOutlined,
  PlusOutlined,
  LinkOutlined,
  MenuOutlined
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

const CreateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleModalVisible,
    modalVisible,
    apps,
    menus,
    resTypeOptions,
    parentId
  } = props;

  const [termValue, setTermValue] = useState(undefined);
  const [iconPickerVisible, setIconPickerVisible] = useState(false);
  const [currentIcon, setCurrentIcon] = useState(null);
  const [iconType, setIconType] = useState('antd');
  const [selectedAntdName, setSelectedAntdName] = useState('');
  const [customIcon, setCustomIcon] = useState('');
  const [customUrl, setCustomUrl] = useState('');

  const typeProps = {
    showSearch: true,
    treeData: menus?? [],
    value: termValue,
    onChange: setTermValue,
    treeDefaultExpandAll: true,
    treeLine: true,
    placeholder: '请选择',
    treeNodeFilterProp: 'title',
    dropdownStyle: { maxHeight: 400, overflow: 'auto'},
  };

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    // 处理图标字段为字符串
    let iconString = '';
    if (iconType === 'antd') {
      iconString = selectedAntdName || fieldsValue.iconName || fieldsValue.icon?.name || '';
    } else if (iconType === 'custom') {
      iconString = customIcon || fieldsValue.customIcon || '';
    } else if (iconType === 'url') {
      iconString = customUrl || fieldsValue.customUrl || '';
    }

    const processedFields = { ...fieldsValue, icon: iconString };

    handleAdd(processedFields);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 基础信息分组 */}
        <Card
          title={
            <Space>
              <AppstoreOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="资源的基本标识信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="resourceName"
                label={
                  <Space>
                    资源名称
                    <Tooltip title="资源的显示名称，用于识别">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '资源名称为必填项',
                  },
                  {
                    max: 25,
                    type: 'string',
                    message: '最多25个字',
                  },
                ]}
              >
                <Input
                  placeholder="请输入资源名称，最多25个字"
                  prefix="📝"
                />
              </FormItem>

              <FormItem
                name="resourceCode"
                label={
                  <Space>
                    资源编码
                    <Tooltip title="资源的唯一标识码">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '资源编码为必填项',
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: '最多50个字符',
                  },
                ]}
              >
                <Input
                  placeholder="请输入英文编码，最多50个字符"
                  prefix="🔑"
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="resourcePath"
                label={
                  <Space>
                    资源路径
                    <Tooltip title="资源的访问路径">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '资源路径为必填项',
                  },
                  {
                    max: 250,
                    type: 'string',
                    message: '最多250个字符',
                  },
                ]}
              >
                <Input
                  placeholder="请输入英文或符号字符，最多250个字符"
                  prefix="🔗"
                />
              </FormItem>

              <FormItem
                name="componentPath"
                label={
                  <Space>
                    组件路径
                    <Tooltip title="组件文件路径，相对于src/pages/目录">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: false,
                    message: '组件路径格式：pathX/xxx/xxx，相对于src/pages/目录',
                    pattern: /^[a-zA-Z0-9\/\-_]+$/,
                  },
                ]}
              >
                <Input
                  placeholder="请输入组件路径，如：test，注意无需加/index"
                  prefix="📁"
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
            <Tooltip title="菜单相关的配置信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="resourceType"
                label={
                  <Space>
                    资源类型
                    <Tooltip title="资源的类型，影响显示方式">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '请选择资源类型',
                  },
                ]}
              >
                <Select
                  placeholder="请选择资源类型"
                  style={{ width: '100%' }}
                  filterOption={(input, option) =>
                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                  options={resTypeOptions}
                />
              </FormItem>

              <FormItem
                label={
                  <Space>
                    菜单图标
                    <Tooltip title="菜单显示的图标">
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

            <Col span={12}>
              <FormItem
                name="parentId"
                label={
                  <Space>
                    上级菜单
                    <Tooltip title="菜单的父级菜单">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: false,
                    message: '请选择上级菜单',
                  },
                ]}
              >
                <TreeSelect {...typeProps} />
              </FormItem>

              <FormItem
                name="displayOrder"
                label={
                  <Space>
                    展示顺序
                    <Tooltip title="菜单的显示顺序，数字越小越靠前">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '展示顺序为必填项',
                  },
                  {
                    pattern: /^([1-9]|[1-9]\d|100)$/,
                    message: '请输入1-100之间的数字',
                  },
                ]}
              >
                <Input
                  placeholder="请输入数字，1-100"
                  prefix="🔢"
                />
              </FormItem>
            </Col>
          </Row>
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
            <Tooltip title="资源的技术配置信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="requestMethod"
                label={
                  <Space>
                    请求方法
                    <Tooltip title="HTTP请求方法">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
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
                    <Tooltip title="链接的打开方式">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
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
              <GlobalOutlined style={{ color: '#722ed1' }} />
              应用配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="资源的应用归属和状态配置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="appId"
                label={
                  <Space>
                    归属应用
                    <Tooltip title="资源所属的应用">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  placeholder="请选择归属应用"
                  style={{ width: '100%' }}
                  filterOption={(input, option) =>
                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                  options={apps}
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="isValid"
                label={
                  <Space>
                    应用状态
                    <Tooltip title="资源是否启用">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  placeholder="请选择应用状态"
                  style={{ width: '100%' }}
                >
                  <Option value="1">有效</Option>
                  <Option value="0">无效</Option>
                </Select>
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

        {/* 辅助字段，不直接提交 */}
        <FormItem name="iconName" hidden><Input /></FormItem>
        <FormItem name="customIcon" hidden><Input /></FormItem>
        <FormItem name="customUrl" hidden><Input /></FormItem>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <Space>
        <Button onClick={() => handleModalVisible()}>
          取消
        </Button>
        <Button type="primary" onClick={() => handleNext()}>
          创建资源
        </Button>
      </Space>
    );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#52c41a' }} />
          新建资源
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
          resourceType: 'menu',
          requestMethod: 'GET',
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
        value={currentIcon || {}}
      />
    </FullscreenModal>
  );
};

export default CreateForm;
