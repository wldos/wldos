import React, {useEffect, useState} from 'react';
import {
  Form,
  Input,
  InputNumber,
  Button,
  Select,
  Switch,
  Card,
  message,
  TreeSelect,
  Divider,
  Space,
  Alert,
  Tooltip,
} from 'antd';
import {
  SettingOutlined,
  MailOutlined,
  FileTextOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';
import styles from "@/pages/sys/oauth/index.less";
import * as service from "@/pages/sys/config/service";
import {queryLayerCategory} from "@/pages/sys/category/service";

const SysOptions = () => {
  const [form] = Form.useForm();
  const [tenantStatus, setTenantStatus] = useState(false);
  const [domainStatus, setDomainStatus] = useState(false);
  const [commentStatus, setCommentStatus] = useState(false);
  const [emailStatus, setEmailStatus] = useState(false);
  const [treeData, setTreeData] = useState([]);

  const reload = async () => {
    const res = await service.fetchSysOptions();
    if (res?.data) {
      form.setFieldsValue(res.data);
      // eslint-disable-next-line camelcase
      const {wldos_system_multitenancy_switch = 'false', wldos_system_multidomain_switch = 'false', wldos_platform_user_register_emailaction = 'false'} = res.data;
      setTenantStatus(wldos_system_multitenancy_switch === 'true');
      setDomainStatus(wldos_system_multidomain_switch === 'true');
      setEmailStatus(wldos_platform_user_register_emailaction === 'true');
    }
  }

  useEffect(async () => {
    await reload();

    const res = await queryLayerCategory();
    if (res?.data) {
      setTreeData(res.data);
    }
  }, []);

  const handleFinish = async () => {
    const values = await form.validateFields();

    const hide = message.loading('正在配置');

    try {
      await service.configOptions(values);
      hide();
      message.success('配置成功').then(() => reload());
      return true;
    } catch (error) {
      hide();
      message.error('配置失败请重试！');
      return false;
    }
  };

  const validateNonRoot = async (rule, value = 0) => {
    const {required = false, root = 0, message: mes = '请输入合适的值'} = rule;
    if (required && value === undefined) {
      return Promise.reject(new Error(mes));
    }
    if (root === value) {
      return Promise.reject(new Error(mes));
    }
    return Promise.resolve();
  };

  const validateNum = async (rule, value = 0) => {
    const {required = false, min = 0, max = 0, message: mes = '请输入合适的值'} = rule;
    if (required && value === undefined) {
      return Promise.reject(new Error(mes));
    }
    if (value > max || value < min) {
      return Promise.reject(new Error(mes));
    }
    return Promise.resolve();
  };

  const tProps = {
    treeData,
    treeDefaultExpandAll: true,
    allowClear: true,
    treeLine: true,
    placeholder: '请选择',
    style: {
      width: '100%',
    },
    showSearch: true,
    treeNodeFilterProp: 'title',
    dropdownStyle: { maxHeight: 400, overflow: 'auto'},
  };

  return (
    <div style={{ padding: '24px' }}>
      <Alert
        message="系统参数配置"
        description="配置系统的基础参数，包括平台设置、邮件配置和内容管理参数。修改后请及时保存配置。"
        type="info"
        showIcon
        style={{ marginBottom: '24px' }}
      />
      
      <Form
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 16 }}
        layout="horizontal"
        form={form}
        size="large"
      >
        {/* 基础配置分组 */}
        <Card 
          title={
            <Space>
              <SettingOutlined style={{ color: '#1890ff' }} />
              基础配置
            </Space>
          }
          style={{ marginBottom: '24px' }}
          extra={
            <Tooltip title="配置系统的基础运行参数">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Form.Item
            name="wldos_platform_domain"
            label="平台根域名"
            rules={[
              {
                required: true,
                message: '请输入平台根域名，不能为空，最多120个字符！',
                min: 1,
                max: 120,
              },
            ]}
          >
            <Input 
              placeholder="不带www的顶级域名，如：example.com"
              prefix="🌐"
            />
          </Form.Item>
          
          <Form.Item
            name="wldos_req_protocol"
            label="平台请求协议"
            rules={[
              {
                required: true,
                message: '请输入平台请求协议！',
              },
            ]}
          >
            <Select placeholder="请设置请求协议，推荐使用https">
              <Select.Option value="http">HTTP</Select.Option>
              <Select.Option value="https">HTTPS（推荐）</Select.Option>
            </Select>
          </Form.Item>
          
          <Form.Item 
            name="wldos_system_multitenancy_switch" 
            label={
              <Space>
                多租户模式
                <Tooltip title="开启后支持多租户隔离，适合SaaS平台">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Switch 
              checkedChildren="开启" 
              unCheckedChildren="关闭" 
              checked={tenantStatus} 
              onChange={(e) => setTenantStatus(e)}
            />
          </Form.Item>
          
          <Form.Item 
            name="wldos_system_multidomain_switch" 
            label={
              <Space>
                多站点模式
                <Tooltip title="开启后支持多域名站点管理">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Switch 
              checkedChildren="开启" 
              unCheckedChildren="关闭" 
              checked={domainStatus} 
              onChange={(e) => setDomainStatus(e)}
            />
          </Form.Item>
          
          <Form.Item 
            name="wldos_file_store_path" 
            label="文件存储路径"
            extra="留空则使用默认路径：根目录/store"
          >
            <Input placeholder="自定义文件存储路径，留空使用默认路径" />
          </Form.Item>
          
          <Form.Item
            name="wldos_platform_adminEmail"
            label="管理员邮箱"
            rules={[
              {
                required: false,
                message: '管理员邮箱，最多120个字符！',
                max: 120,
              },
              {
                type: 'email',
                message: '请输入正确格式邮箱',
              }
            ]}
          >
            <Input 
              placeholder="系统管理员邮箱地址"
              prefix="📧"
            />
          </Form.Item>
        </Card>
        {/* 内容配置分组 */}
        <Card 
          title={
            <Space>
              <FileTextOutlined style={{ color: '#52c41a' }} />
              内容管理配置
            </Space>
          }
          style={{ marginBottom: '24px' }}
          extra={
            <Tooltip title="配置内容发布和管理相关参数">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Form.Item
            name="wldos_cms_defaultTermTypeId"
            label="内容默认分类"
            rules={[
              {
                required: true,
                message: '请选择默认分类！',
              },
              {
                required: true,
                root: 0,
                message: '不能设置为根分类',
                validator: async (rule, value) => validateNonRoot(rule, value),
              }
            ]}
          >
            <TreeSelect {...tProps} />
          </Form.Item>
          
          <Form.Item
            name="wldos_cms_content_maxLength"
            label="单篇内容最大长度"
            rules={[
              {
                required: true,
                message: '请设置单篇发布最长篇幅！',
              },
              {
                required: true,
                min: 2000,
                max: 53610,
                message: '单个发布字符数上限最多53610，最少2000',
                validator: async (rule, value) => validateNum(rule, value),
              },
            ]}
            extra="建议设置：2000-53610字符"
          >
            <InputNumber 
              style={{ width: '100%' }}
              placeholder="设置单篇内容最大字符数"
              min={2000}
              max={53610}
            />
          </Form.Item>
          
          <Form.Item
            name="wldos_cms_tag_maxTagNum"
            label="单个发布最多标签数"
            rules={[
              {
                required: true,
                message: '请设置单个发布允许添加的标签个数！',
              },
              {
                required: true,
                min: 1,
                max: 5,
                message: '单个发布最多5个标签',
                validator: async (rule, value) => validateNum(rule, value),
              },
            ]}
            extra="建议设置：1-5个标签"
          >
            <InputNumber 
              style={{ width: '100%' }}
              placeholder="设置单个发布最多标签数"
              min={1}
              max={5}
            />
          </Form.Item>
          
          <Form.Item
            name="wldos_cms_tag_tagLength"
            label="标签最长字符数"
            rules={[
              {
                required: true,
                message: '请设置标签最长字符数（每3个字符代表一个汉字）！',
              },
              {
                required: true,
                min: 3,
                max: 30,
                message: '标签字符数3到30位',
                validator: async (rule, value) => validateNum(rule, value),
              },
            ]}
            extra="建议设置：3-30个字符（1个汉字=3个字符）"
          >
            <InputNumber 
              style={{ width: '100%' }}
              placeholder="设置标签最长字符数"
              min={3}
              max={30}
            />
          </Form.Item>
          
          <Form.Item 
            name="wldos_cms_comment_audit" 
            label={
              <Space>
                评论审核
                <Tooltip title="开启后用户评论需要审核才能显示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Switch 
              checkedChildren="开启" 
              unCheckedChildren="关闭" 
              checked={commentStatus} 
              onChange={(e) => setCommentStatus(e)}
            />
          </Form.Item>
        </Card>
        {/* 邮件配置分组 */}
        <Card 
          title={
            <Space>
              <MailOutlined style={{ color: '#fa8c16' }} />
              邮件配置
            </Space>
          }
          style={{ marginBottom: '24px' }}
          extra={
            <Tooltip title="配置系统邮件发送相关参数">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Form.Item 
            name="wldos_platform_user_register_emailaction" 
            label={
              <Space>
                注册邮箱激活
                <Tooltip title="开启后用户注册需要通过邮箱验证">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Switch 
              checkedChildren="开启" 
              unCheckedChildren="关闭" 
              checked={emailStatus} 
              onChange={(e) => setEmailStatus(e)}
            />
          </Form.Item>
          
          {emailStatus && (
            <>
              <Divider orientation="left" plain>
                <Space>
                  <MailOutlined />
                  SMTP服务器配置
                </Space>
              </Divider>
              
              <Form.Item
                name="spring_mail_host"
                label="SMTP服务器"
                rules={[
                  {
                    required: true,
                    message: 'SMTP服务器地址，最多60个字符！',
                    max: 60,
                  },
                ]}
                extra="例如：smtp.qq.com、smtp.163.com"
              >
                <Input 
                  placeholder="请输入SMTP服务器地址"
                  prefix="🔗"
                />
              </Form.Item>
              
              <Form.Item
                name="spring_mail_username"
                label="SMTP用户名"
                rules={[
                  {
                    required: true,
                    message: 'SMTP用户名，最多50个字符！',
                    max: 50,
                  },
                ]}
                extra="通常是完整的邮箱地址"
              >
                <Input 
                  placeholder="请输入SMTP认证用户名"
                  prefix="👤"
                />
              </Form.Item>
              
              <Form.Item
                name="spring_mail_password"
                label="SMTP密码"
                rules={[
                  {
                    required: true,
                    message: 'SMTP密码，最多120个字符！',
                    max: 120,
                  },
                ]}
                extra="QQ邮箱需要使用授权码，不是登录密码"
              >
                <Input.Password 
                  placeholder="请输入SMTP认证密码"
                  prefix="🔒"
                />
              </Form.Item>
              
              <Form.Item
                name="wldos_mail_fromMail_addr"
                label="发件邮箱地址"
                rules={[
                  {
                    required: true,
                    message: '发件邮箱地址，最多120个字符！',
                    max: 120,
                  },
                  {
                    type: 'email',
                    message: '请输入正确格式邮箱',
                  }
                ]}
                extra="系统发送邮件的发件人地址"
              >
                <Input 
                  placeholder="请输入发件邮箱地址"
                  prefix="📧"
                />
              </Form.Item>
              
              <Alert
                message="邮件配置说明"
                description="请确保SMTP服务器支持您使用的邮箱服务商。QQ邮箱需要开启SMTP服务并获取授权码。"
                type="info"
                showIcon
                style={{ marginTop: '16px' }}
              />
            </>
          )}
        </Card>
        {/* 操作按钮 */}
        <Card style={{ textAlign: 'center', marginTop: '24px' }}>
          <Space size="large">
            <Button 
              type="primary" 
              size="large"
              icon={<CheckCircleOutlined />}
              onClick={() => handleFinish()}
              style={{ minWidth: '120px' }}
            >
              保存配置
            </Button>
            <Button 
              size="large"
              onClick={() => form.resetFields()}
              style={{ minWidth: '120px' }}
            >
              重置配置
            </Button>
          </Space>
        </Card>
      </Form>
    </div>
  );
};

export default SysOptions;

