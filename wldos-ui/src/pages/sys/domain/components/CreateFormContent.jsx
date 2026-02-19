import React, {useEffect, useState} from 'react';
import {Button, Form, Input, message, Select, Card, Space, Divider, Typography, Tooltip, Row, Col} from 'antd';
import {
  GlobalOutlined,
  PictureOutlined,
  SearchOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  FileTextOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import {UploadView, upParams} from "@/components/FileUpload";

const { Title, Text } = Typography;
const FormItem = Form.Item;
const {Option} = Select;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 18,
  },
};

const CreateFormContent = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleAdd,
    onCancel: handleCancel,
    platDomain,
    ossUrl
  } = props;

  const [logoUrl, setLogoUrl] = useState('');
  const [iconUrl, setIconUrl] = useState('');

  const beforeUp = (file) => {
    const isGt50K = file.size / 1024 > 100;
    if (isGt50K) {
      return message.error('logo大小不能超过100k').then(() => false);
    }
    return true;
  };

  const handleChange = (info, type) => {
    const {file: {status, response}} = info;

    if (status === 'done') {
      message.success(`上传成功！`, 1).then(() => {
        // 框架包装为 Result<T>，response.data 为 FileInfo
        const path = response?.data?.path ?? response?.path;
        const url = response?.data?.url ?? response?.url;
        if (type === 'logo') {
          setLogoUrl(url ?? undefined);
          if (path)
            form.setFieldsValue({siteLogo: path ?? ''});
        } else {
          setIconUrl(url ?? undefined);
          if (path)
            form.setFieldsValue({favicon: path ?? ''});
        }
      });
    } else if (status === 'error') {
      message.error(`上传失败！`, 2).then(()=>{});
    }
  };

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    handleAdd(fieldsValue);
  };

  const renderContent = () => {
    return (
      <div>
        {/* 基础信息分组 */}
        <Card 
          title={
            <Space>
              <GlobalOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="网站的基本标识信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="siteName"
                label={
                  <Space>
                    网站名称
                    <Tooltip title="网站显示名称，用于识别">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '网站名称为必填项',
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: '最多50个字',
                  },
                ]}
              >
                <Input 
                  placeholder="请输入网站名称，最多50个字"
                  prefix="🌐"
                />
              </FormItem>
            </Col>
            
            <Col span={12}>
              <FormItem
                name="siteDomain"
                label={
                  <Space>
                    网站域名
                    <Tooltip title="主域名地址，如：example.com">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '域名为必填项',
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: '最多50位',
                  },
                ]}
              >
                <Input 
                  placeholder="请输入主域名，如：example.com"
                  prefix="🔗"
                />
              </FormItem>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item 
                label={
                  <Space>
                    个性域名
                    <Tooltip title="用于生成个性访问地址">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
            <Input.Group compact>
              <span>
                <Form.Item
                  noStyle
                  name="secondDomain"
                  rules={[
                    {
                      required: true,
                      message: '个性域名为必填项',
                    },
                    {
                      max: 10,
                      type: 'string',
                      message: '最多10位',
                    },
                    {
                      type: 'string',
                      pattern: '^[a-z]+$',
                      message: '只能是小写字母'
                    }
                  ]}
                >
                  <Input
                    style={{
                      width: 'calc(100% - 100px)',
                    }}
                    placeholder="请输入个性域名"
                    prefix="🎯"
                  />
                </Form.Item>
                <span style={{ padding: '0 8px', color: '#999' }}>.{platDomain}</span>
              </span>
            </Input.Group>
          </Form.Item>
            </Col>
            
            <Col span={12}>
              <FormItem
                name="siteUrl"
                label={
                  <Space>
                    主页地址
                    <Tooltip title="网站首页完整URL地址">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '主页地址为必填项',
                  },
                  {
                    max: 200,
                    type: 'string',
                    message: '最多200位',
                  },
                ]}
              >
                <Input 
                  placeholder="请输入完整的主页地址，如：http://www.example.com"
                  prefix="🏠"
                />
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* 品牌设置分组 */}
        <Card 
          title={
            <Space>
              <PictureOutlined style={{ color: '#52c41a' }} />
              品牌设置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="网站的品牌标识和视觉元素">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem 
                label={
                  <Space>
                    Logo
                    <Tooltip title="网站Logo图片，建议尺寸：200x60px">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Input.Group compact>
                  <span>
                    <Form.Item
                      style={{
                        width: 'calc(100% - 100px)',
                      }}
                      name="siteLogo"
                    >
                      <Input hidden/>
                    </Form.Item>
                    <UploadView 
                      buttonTitle="上传Logo" 
                      src={logoUrl} 
                      params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.svg+xml'}}
                      beforeUp={(file) => beforeUp(file)}
                      onChange={(info) => handleChange(info, 'logo')} 
                    />
                  </span>
                </Input.Group>
              </FormItem>
            </Col>
            
            <Col span={12}>
              <FormItem 
                label={
                  <Space>
                    Favicon
                    <Tooltip title="网站图标，建议尺寸：32x32px">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
            <Input.Group compact>
              <span>
                <Form.Item
                  style={{
                    width: 'calc(100% - 100px)',
                  }}
                  name="favicon"
                >
                  <Input hidden/>
                </Form.Item>
                <UploadView 
                  buttonTitle="上传图标" 
                  src={iconUrl} 
                  params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.x-icon,.ico,.svg+xml'}}
                  beforeUp={(file) => beforeUp(file)}
                  onChange={(info) => handleChange(info, 'icon')} 
                />
              </span>
            </Input.Group>
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* SEO配置分组 */}
        <Card 
          title={
            <Space>
              <SearchOutlined style={{ color: '#fa8c16' }} />
              SEO配置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="搜索引擎优化相关设置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="siteTitle"
                label={
                  <Space>
                    网站标题
                    <Tooltip title="浏览器标签页显示的标题">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: '网站标题为必填项',
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: '最多50个字',
                  },
                ]}
              >
                <Input 
                  placeholder="请输入网站标题，最多50个字"
                  prefix="📝"
                />
              </FormItem>
            </Col>
            
            <Col span={12}>
              <FormItem
                name="siteKeyword"
                label={
                  <Space>
                    关键词
                    <Tooltip title="网站关键词，用逗号分隔">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
              {
                required: true,
                message: '关键词为必填项',
              },
              {
                max: 125,
                type: 'string',
                message: '最多125个字',
              },
            ]}
          >
                <Input 
                  placeholder="请输入关键词，用逗号分隔"
                  prefix="🔍"
                />
              </FormItem>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="slogan"
                label={
                  <Space>
                    网站口号
                    <Tooltip title="网站宣传口号或标语">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    max: 25,
                    type: 'string',
                    message: '最多25个字',
                  },
                ]}
              >
                <Input 
                  placeholder="请输入网站口号，最多25个字"
                  prefix="💬"
                />
              </FormItem>
            </Col>
            
            <Col span={12}>
              <FormItem
                name="cnameDomain"
                label={
                  <Space>
                    别名域名
                    <Tooltip title="多个域名别名，用逗号分隔">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: false,
                    message: '别名域名为必填项',
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: '最多50个字符',
                  },
                ]}
              >
                <Input 
                  placeholder="多个别名，以半角逗号间隔"
                  prefix="🔗"
                />
              </FormItem>
            </Col>
          </Row>
          
          <FormItem
            name="siteDescription"
            label={
              <Space>
                网站描述
                <Tooltip title="网站描述，用于SEO和搜索结果展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '网站描述为必填项',
              },
              {
                max: 125,
                type: 'string',
                message: '最多125个字',
              },
            ]}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea 
              placeholder="请输入网站描述，最多125个字"
              rows={3}
            />
          </FormItem>
        </Card>

        {/* 高级设置分组 */}
        <Card 
          title={
            <Space>
              <SettingOutlined style={{ color: '#722ed1' }} />
              高级设置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="网站的高级配置选项">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                name="displayOrder"
                label={
                  <Space>
                    展示顺序
                    <Tooltip title="数字越小排序越靠前，范围1-100">
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
                  placeholder="请输入展示顺序，1-100"
                  prefix="🔢"
                />
              </FormItem>
            </Col>
            
            <Col span={12}>
              <FormItem 
                name="isValid" 
                label={
                  <Space>
                    状态
                    <Tooltip title="网站是否启用">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
            <Select
              style={{
                width: '100%',
              }}
              placeholder="请选择状态"
              defaultValue="1"
            >
              <Option value="1">有效</Option>
              <Option value="0">无效</Option>
            </Select>
              </FormItem>
            </Col>
          </Row>
        </Card>

        {/* 内容设置分组 */}
        <Card 
          title={
            <Space>
              <FileTextOutlined style={{ color: '#13c2c2' }} />
              内容设置
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="网站页面内容相关设置">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="foot"
            label={
              <Space>
                底部栏目
                <Tooltip title="网站底部栏目内容，支持HTML">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea 
              rows={4} 
              placeholder="请输入底部栏目内容，支持HTML格式"
            />
          </FormItem>
          
          <FormItem
            name="flink"
            label={
              <Space>
                友情链接
                <Tooltip title="友情链接列表，每行一个链接">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea 
              rows={4} 
              placeholder="请输入友情链接，每行一个链接"
            />
          </FormItem>
          
          <FormItem
            name="copy"
            label={
              <Space>
                版权信息
                <Tooltip title="网站版权信息，支持HTML">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea 
              rows={4} 
              placeholder="请输入版权信息，支持HTML格式"
            />
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
        displayOrder: '1'
      }}
    >
      {renderContent()}
    </Form>
  );
};

export default CreateFormContent;
