import {Button, Form, Input, Card, Space, Tooltip, Divider, Typography } from 'antd';
import {connect, FormattedMessage} from 'umi';
import React, {Component} from 'react';
import {
  WechatOutlined,
  QqOutlined,
  WeiboOutlined,
  KeyOutlined,
  LinkOutlined,
  UserOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import styles from './config.less';

const { Title, Text } = Typography;

class OAuth2Config extends Component {
  view = undefined;

  componentDidMount() {
    const {oauthType, dispatch} = this.props;
    dispatch({
      type: 'oauth2/fetchConfig',
      payload: {oauthType}
    });
  }

  updateCurrent = () => {
    const {dispatch, oauthType} = this.props;
    dispatch({
      type: 'oauth2/fetchConfig',
      payload: {oauthType},
    });
  };

  getViewDom = (ref) => {
    this.view = ref;
  };

  handleFinish = (values) => {
    const {dispatch} = this.props;
    dispatch({
      type: 'oauth2/saveOAuthConfig',
      payload: values,
      callback: (res) => {
        if (res && res.data) {
          if (res.data === 'ok')
            this.updateCurrent();
        }
      }
    });
  };

  getPlatformInfo = (oauthType) => {
    const platformInfo = {
      wechat: {
        name: '微信登录',
        icon: <WechatOutlined style={{ color: '#07c160' }} />,
        color: '#07c160',
        description: '配置微信开放平台OAuth2.0登录',
        helpUrl: 'https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html'
      },
      qq: {
        name: 'QQ登录',
        icon: <QqOutlined style={{ color: '#12b7f5' }} />,
        color: '#12b7f5',
        description: '配置QQ互联OAuth2.0登录',
        helpUrl: 'https://wiki.connect.qq.com/'
      },
      weibo: {
        name: '微博登录',
        icon: <WeiboOutlined style={{ color: '#e6162d' }} />,
        color: '#e6162d',
        description: '配置微博开放平台OAuth2.0登录',
        helpUrl: 'https://open.weibo.com/wiki/Connect/login'
      }
    };
    return platformInfo[oauthType] || platformInfo.wechat;
  };

  render() {
    const {wechat, qq, weibo, oauthType, configLoading} = this.props;
    const platformInfo = this.getPlatformInfo(oauthType);

    let initValues;
    if (oauthType === 'wechat') {
      initValues = wechat;
    } else {
      initValues = oauthType === 'qq' ? qq : weibo;
    }
    
    return (!configLoading &&
      <div style={{ padding: '24px' }} ref={this.getViewDom}>
        {/* 平台信息头部 */}
        <Card style={{ marginBottom: '24px' }}>
          <Space align="center" size="large">
            {platformInfo.icon}
            <div>
              <Title level={4} style={{ margin: 0, color: platformInfo.color }}>
                {platformInfo.name}
              </Title>
              <Text type="secondary">{platformInfo.description}</Text>
            </div>
          </Space>
        </Card>
        <Form
          layout="vertical"
          onFinish={this.handleFinish}
          initialValues={{...initValues, oauthType}}
          size="large"
        >
          <Form.Item name="oauthType" noStyle>
            <Input type="hidden" />
          </Form.Item>

          {/* 基础配置分组 */}
          <Card 
            title={
              <Space>
                <KeyOutlined style={{ color: '#1890ff' }} />
                基础配置
              </Space>
            }
            style={{ marginBottom: '24px' }}
            extra={
              <Tooltip title="应用的基础认证信息">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Form.Item
              name="appId"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.appId" defaultMessage="AppID/AppKey" />
                  <Tooltip title="在第三方平台申请应用后获得的唯一标识">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: true,
                  message: <FormattedMessage id="oauth.config.appId-message"
                                             defaultMessage="请输入AppID/AppKey!"/>
                },
              ]}
            >
              <Input 
                placeholder="请输入AppID或AppKey"
                prefix="🔑"
              />
            </Form.Item>
            
            <Form.Item
              name="appSecret"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.appSecret" defaultMessage="AppSecret" />
                  <Tooltip title="应用密钥，请妥善保管，不要泄露">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: true,
                  message: <FormattedMessage
                    id="oauth.config.appSecret-message"
                    defaultMessage="请输入AppSecret!"/>
                },
              ]}
            >
              <Input.Password 
                placeholder="请输入AppSecret"
                prefix="🔒"
              />
            </Form.Item>
          </Card>
          {/* 回调配置分组 */}
          <Card 
            title={
              <Space>
                <LinkOutlined style={{ color: '#52c41a' }} />
                回调配置
              </Space>
            }
            style={{ marginBottom: '24px' }}
            extra={
              <Tooltip title="用户授权后的回调地址和权限范围">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Form.Item
              name="redirectUri"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.redirectUri" defaultMessage="回调地址" />
                  <Tooltip title="用户授权后跳转的地址，需要在第三方平台配置">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="oauth.config.redirectUri-message"
                    defaultMessage="请输入回调地址!"/>
                },
              ]}
              extra="格式：http(s)://yourdomain.com/callback"
            >
              <Input 
                placeholder="请输入回调地址"
                prefix="🔗"
              />
            </Form.Item>
            
            <Form.Item
              name="scope"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.scope" defaultMessage="授权域" />
                  <Tooltip title="应用请求的权限范围，多个用逗号分隔">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="oauth.config.scope-message"
                    defaultMessage="请输入授权域!"/>
                },
              ]}
              extra="例如：snsapi_login,user_info"
            >
              <Input 
                placeholder="请输入授权域，多个用逗号分隔"
                prefix="🎯"
              />
            </Form.Item>
          </Card>
          {/* API接口配置分组 */}
          <Card 
            title={
              <Space>
                <SettingOutlined style={{ color: '#fa8c16' }} />
                API接口配置
              </Space>
            }
            style={{ marginBottom: '24px' }}
            extra={
              <Tooltip title="OAuth2.0流程中的API接口地址">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Form.Item
              name="codeUri"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.codeUri" defaultMessage="授权码获取地址" />
                  <Tooltip title="用户授权后获取授权码的接口地址">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: true,
                  message: <FormattedMessage
                    id="oauth.config.codeUri-message"
                    defaultMessage="请输入授权码获取地址!"/>
                },
              ]}
              extra="用户点击授权按钮后跳转的地址"
            >
              <Input 
                placeholder="请输入授权码获取地址"
                prefix="🔐"
              />
            </Form.Item>
            
            <Form.Item
              name="accessTokenUri"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.accessTokenUri" defaultMessage="访问令牌获取地址" />
                  <Tooltip title="通过授权码换取访问令牌的接口地址">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="oauth.config.accessTokenUri-message"
                    defaultMessage="请输入访问令牌获取地址!"/>
                },
              ]}
              extra="用于获取访问令牌的API接口"
            >
              <Input 
                placeholder="请输入访问令牌获取地址"
                prefix="🎫"
              />
            </Form.Item>
            
            <Form.Item
              name="refreshTokenUri"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.refreshTokenUri" defaultMessage="刷新令牌获取地址" />
                  <Tooltip title="刷新访问令牌的接口地址（可选）">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="oauth.config.refreshTokenUri-message"
                    defaultMessage="请输入刷新令牌获取地址!"/>
                },
              ]}
              extra="用于刷新访问令牌的API接口（可选）"
            >
              <Input 
                placeholder="请输入刷新令牌获取地址"
                prefix="🔄"
              />
            </Form.Item>
            
            <Form.Item
              name="userInfoUri"
              label={
                <Space>
                  <FormattedMessage id="oauth.config.userInfoUri" defaultMessage="用户信息获取地址" />
                  <Tooltip title="获取用户基本信息的接口地址">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage id="oauth.config.userInfoUri-message"
                                             defaultMessage="请输入用户信息获取地址!"/>
                },
              ]}
              extra="用于获取用户基本信息的API接口"
            >
              <Input 
                placeholder="请输入用户信息获取地址"
                prefix="👤"
              />
            </Form.Item>
          </Card>

          {/* 操作按钮 */}
          <Card style={{ textAlign: 'center' }}>
            <Space size="large">
              <Button 
                type="primary" 
                size="large"
                htmlType="submit"
                icon={<CheckCircleOutlined />}
                style={{ minWidth: '120px' }}
              >
                <FormattedMessage
                  id="oauth.config.update"
                  defaultMessage="保存配置"/>
              </Button>
              <Button 
                size="large"
                onClick={() => window.location.reload()}
                style={{ minWidth: '120px' }}
              >
                重置配置
              </Button>
            </Space>
          </Card>
        </Form>
      </div>
    );
  }
}

export default connect(({loading, oauth2}) => ({
  wechat: oauth2.wechat,
  qq: oauth2.qq,
  weibo: oauth2.weibo,
  configLoading: loading.effects['oauth2/fetchConfig'],
}))(OAuth2Config);
