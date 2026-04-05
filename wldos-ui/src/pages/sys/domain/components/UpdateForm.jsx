import React, {useEffect, useState} from 'react';
import {Button, Form, Input, message, Select, Card, Space, Tooltip, Row, Col} from 'antd';
import {useIntl} from 'umi';
import FullscreenModal from '@/components/FullscreenModal';
import {
  GlobalOutlined,
  PictureOutlined,
  SearchOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  FileTextOutlined,
  CrownOutlined,
} from '@ant-design/icons';
import {UploadView, upParams} from "@/components/FileUpload";

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

const UpdateForm = (props) => {
  const intl = useIntl();
  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    platDomain,
    ossUrl
  } = props;

  const [logoUrl, setLogoUrl] = useState(values?.siteLogo);
  const [iconUrl, setIconUrl] = useState(values?.favicon);

  useEffect(async () => {
    const {siteLogo, favicon} = values;
    if (!siteLogo)
      setLogoUrl(siteLogo);
    if (!favicon)
      setIconUrl(favicon);
  }, []);

  const beforeUp = (file) => {
    const isGt50K = file.size / 1024 > 100;
    if (isGt50K) {
      return message.error(intl.formatMessage({ id: 'sys.domain.msg.logoMax', defaultMessage: 'logo大小不能超过100k' })).then(() => false);
    }

    return true;
  };

  const handleChange = (info, type) => {
    const {file: {status, response}} = info;

    if (status === 'done') {
      message.success(intl.formatMessage({ id: 'sys.domain.msg.uploadSuccess', defaultMessage: '上传成功！' }), 1).then(() => {
        const {data: {url, path}} = response;
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
      message.error(intl.formatMessage({ id: 'sys.domain.msg.uploadFail', defaultMessage: '上传失败！' }), 2).then(()=>{});
    }
  };

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <div>
        <Card
          title={
            <Space>
              <GlobalOutlined style={{ color: '#1890ff' }} />
              {intl.formatMessage({ id: 'sys.domain.update.card.basic', defaultMessage: '基础信息' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.card.basic.tip', defaultMessage: '网站的基本标识信息' })}>
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteName', defaultMessage: '网站名称' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteName.tip', defaultMessage: '网站显示名称，用于识别' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.rule.siteNameRequired', defaultMessage: '网站名称为必填项' }),
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max50chars', defaultMessage: '最多50个字' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteName', defaultMessage: '请输入网站名称，最多50个字' })}
                  prefix="🌐"
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="siteDomain"
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteDomain', defaultMessage: '网站域名' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteDomain.tip', defaultMessage: '主域名地址，如：example.com' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.rule.siteDomainRequired', defaultMessage: '域名为必填项' }),
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max50', defaultMessage: '最多50位' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteDomain', defaultMessage: '请输入主域名，如：example.com' })}
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.secondDomain', defaultMessage: '个性域名' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.secondDomain.tip', defaultMessage: '用于生成个性访问地址' })}>
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
                          message: intl.formatMessage({ id: 'sys.domain.rule.secondDomainRequired', defaultMessage: '个性域名为必填项' }),
                        },
                        {
                          max: 10,
                          type: 'string',
                          message: intl.formatMessage({ id: 'sys.domain.rule.max10', defaultMessage: '最多10位' }),
                        },
                        {
                          type: 'string',
                          pattern: '^[a-z]+$',
                          message: intl.formatMessage({ id: 'sys.domain.rule.lowercaseOnly', defaultMessage: '只能是小写字母' })
                        }
                      ]}
                    >
                      <Input
                        style={{
                          width: 'calc(100% - 100px)',
                        }}
                        placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.secondDomain', defaultMessage: '请输入个性域名' })}
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteUrl', defaultMessage: '主页地址' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteUrl.tip', defaultMessage: '网站首页完整URL地址' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.rule.siteUrlRequired', defaultMessage: '主页地址为必填项' }),
                  },
                  {
                    max: 200,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max200', defaultMessage: '最多200位' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteUrl', defaultMessage: '请输入完整的主页地址，如：http://www.example.com' })}
                  prefix="🏠"
                />
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card
          title={
            <Space>
              <PictureOutlined style={{ color: '#52c41a' }} />
              {intl.formatMessage({ id: 'sys.domain.update.card.brand', defaultMessage: '品牌设置' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.card.brand.tip', defaultMessage: '网站的品牌标识和视觉元素' })}>
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <FormItem
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteLogo', defaultMessage: 'Logo' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteLogo.tip', defaultMessage: '网站Logo图片，建议尺寸：200x60px' })}>
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
                      buttonTitle={intl.formatMessage({ id: 'sys.domain.update.upload.logo', defaultMessage: '上传Logo' })}
                      src={logoUrl}
                      params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.svg+xml', data: { allowWebp: false }}}
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.favicon', defaultMessage: 'Favicon' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.favicon.tip', defaultMessage: '网站图标，建议尺寸：32x32px' })}>
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
                      buttonTitle={intl.formatMessage({ id: 'sys.domain.update.upload.icon', defaultMessage: '上传图标' })}
                      src={iconUrl}
                      params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.x-icon,.ico,.svg+xml', data: { allowWebp: false }}}
                      beforeUp={(file) => beforeUp(file)}
                      onChange={(info) => handleChange(info, 'icon')}
                    />
                  </span>
                </Input.Group>
              </FormItem>
            </Col>
          </Row>
        </Card>
        <Card
          title={
            <Space>
              <SearchOutlined style={{ color: '#fa8c16' }} />
              {intl.formatMessage({ id: 'sys.domain.update.card.seo', defaultMessage: 'SEO配置' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.card.seo.tip', defaultMessage: '搜索引擎优化相关设置' })}>
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteTitle', defaultMessage: '网站标题' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteTitle.tip', defaultMessage: '浏览器标签页显示的标题' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.rule.siteTitleRequired', defaultMessage: '网站标题为必填项' }),
                  },
                  {
                    max: 50,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max50chars', defaultMessage: '最多50个字' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteTitle', defaultMessage: '请输入网站标题，最多50个字' })}
                  prefix="📝"
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="siteKeyword"
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.domain.update.field.siteKeyword', defaultMessage: '关键词' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteKeyword.tip', defaultMessage: '网站关键词，用逗号分隔' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.rule.keywordRequired', defaultMessage: '关键词为必填项' }),
                  },
                  {
                    max: 125,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max125', defaultMessage: '最多125个字' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteKeyword', defaultMessage: '请输入关键词，用逗号分隔' })}
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.slogan', defaultMessage: '网站口号' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.slogan.tip', defaultMessage: '网站宣传口号或标语' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    max: 25,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.rule.max25', defaultMessage: '最多25个字' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.slogan', defaultMessage: '请输入网站口号，最多25个字' })}
                  prefix="💬"
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="cnameDomain"
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.domain.update.field.cnameDomain', defaultMessage: '别名域名' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.cnameDomain.tip', defaultMessage: '多个域名别名，用逗号分隔' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    max: 50,
                    type: 'string',
                    message: intl.formatMessage({ id: 'sys.domain.update.rule.cnameMax', defaultMessage: '最多50个字符' }),
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.cnameDomain', defaultMessage: '多个别名，以半角逗号间隔' })}
                  prefix="🔗"
                />
              </FormItem>
            </Col>
          </Row>

          <FormItem
            name="siteDescription"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.domain.update.field.siteDescription', defaultMessage: '网站描述' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.siteDescription.tip', defaultMessage: '网站描述，用于SEO和搜索结果展示' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'sys.domain.rule.descRequired', defaultMessage: '描述为必填项' }),
              },
              {
                max: 125,
                type: 'string',
                message: intl.formatMessage({ id: 'sys.domain.rule.max125', defaultMessage: '最多125个字' }),
              },
            ]}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea
              placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.siteDescription', defaultMessage: '请输入网站描述，最多125个字' })}
              rows={3}
            />
          </FormItem>
        </Card>
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#722ed1' }} />
              {intl.formatMessage({ id: 'sys.domain.update.card.advanced', defaultMessage: '高级设置' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.card.advanced.tip', defaultMessage: '网站的高级配置选项' })}>
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
                    {intl.formatMessage({ id: 'sys.domain.update.field.displayOrder', defaultMessage: '展示顺序' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.displayOrder.tip', defaultMessage: '数字越小排序越靠前，范围1-100' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.domain.update.rule.displayOrder', defaultMessage: '展示顺序1~100！' }),
                    pattern: '^([1-9]|[1-9]\\d|100)$',
                    max: 3,
                  },
                ]}
              >
                <Input
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.displayOrder', defaultMessage: '请输入展示顺序，1-100' })}
                  prefix="🔢"
                />
              </FormItem>
            </Col>

            <Col span={12}>
              <FormItem
                name="isValid"
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.domain.update.field.isValid', defaultMessage: '状态' })}
                    <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.isValid.tip', defaultMessage: '网站是否启用' })}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  style={{
                    width: '100%',
                  }}
                  placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.isValid', defaultMessage: '请选择状态' })}
                >
                  <Option value="1">{intl.formatMessage({ id: 'sys.domain.status.valid', defaultMessage: '有效' })}</Option>
                  <Option value="0">{intl.formatMessage({ id: 'sys.domain.status.invalid', defaultMessage: '无效' })}</Option>
                </Select>
              </FormItem>
            </Col>
          </Row>
        </Card>

        <Card
          title={
            <Space>
              <FileTextOutlined style={{ color: '#13c2c2' }} />
              {intl.formatMessage({ id: 'sys.domain.update.card.content', defaultMessage: '内容设置' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.card.content.tip', defaultMessage: '网站页面内容相关设置' })}>
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="foot"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.domain.update.field.foot', defaultMessage: '底部栏目' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.foot.tip', defaultMessage: '网站底部栏目内容，支持HTML' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea
              rows={4}
              placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.foot', defaultMessage: '请输入底部栏目内容，支持HTML格式' })}
            />
          </FormItem>

          <FormItem
            name="flink"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.domain.update.field.flink', defaultMessage: '友情链接' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.flink.tip', defaultMessage: '友情链接列表，每行一个链接' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea
              rows={4}
              placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.flink', defaultMessage: '请输入友情链接，每行一个链接' })}
            />
          </FormItem>

          <FormItem
            name="copy"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.domain.update.field.copy', defaultMessage: '版权信息' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.domain.update.field.copy.tip', defaultMessage: '网站版权信息，支持HTML' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input.TextArea
              rows={4}
              placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.copy', defaultMessage: '请输入版权信息，支持HTML格式' })}
            />
          </FormItem>
        </Card>
      </div>
    );
  };

  const renderFooter = () =>
    (
      <Space>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>
          {intl.formatMessage({ id: 'sys.domain.update.cancel', defaultMessage: '取消' })}
        </Button>
        <Button type="primary" onClick={() => handleNext()}>
          {intl.formatMessage({ id: 'sys.domain.update.save', defaultMessage: '保存配置' })}
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
          <CrownOutlined style={{ color: '#1890ff' }} />
          {intl.formatMessage({ id: 'sys.domain.update.title', defaultMessage: '域名配置' })}
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
          siteName: values.siteName,
          siteDomain: values.siteDomain,
          siteLogo: values.siteLogo?.indexOf(ossUrl) > -1 ? values.siteLogo.substring(ossUrl.length, values.siteLogo.length) : '',
          favicon: values.favicon?.indexOf(ossUrl) > -1 ? values.favicon.substring(ossUrl.length, values.favicon.length) : '',
          secondDomain: values.secondDomain,
          siteUrl: values.siteUrl,
          siteTitle: values.siteTitle,
          siteKeyword: values.siteKeyword,
          siteDescription: values.siteDescription,
          slogan: values.slogan,
          isValid: values.isValid,
          displayOrder: values.displayOrder,
          cnameDomain: values.cnameDomain,
          foot: values.foot,
          flink: values.flink,
          copy: values.copy
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
