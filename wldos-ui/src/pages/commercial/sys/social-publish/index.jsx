import React, { useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Alert,
  Badge,
  Button,
  Card,
  Divider,
  Form,
  Input,
  message,
  Radio,
  Select,
  Space,
  Steps,
  Tag,
  Tabs,
  Typography,
  Upload,
} from 'antd';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import {
  bindPublishAccount,
  queryAdminPlatforms,
  queryPlatformConfigSummary,
  queryPublishAccounts,
  queryPublishJobs,
  retryFailedJob,
  savePlatformConfig,
  wechatOAuthStart,
  wechatOAuthCallback,
  uploadWechatThumb,
} from './service';

const { Step } = Steps;

function parseAccountExtra(extraJson) {
  if (!extraJson || typeof extraJson !== 'string') {
    return {};
  }
  try {
    return JSON.parse(extraJson);
  } catch {
    return {};
  }
}

const platformOptions = [
  'WECHAT_MP',
  'BAIJIAHAO',
  'TOUTIAO',
  'ZHIHU',
  'SOHU',
  'XIAOHONGSHU',
  'BILIBILI',
];

export default function SocialPublishAdmin() {
  const actionRef = useRef();
  useDesktopSticky(actionRef);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [savingPlatformConfig, setSavingPlatformConfig] = useState(false);
  const [adminPlatforms, setAdminPlatforms] = useState([]);
  const [wechatSecretConfigured, setWechatSecretConfigured] = useState(false);
  const [form] = Form.useForm();
  const [platformForm] = Form.useForm();
  const [oauthLoadingId, setOauthLoadingId] = useState(null);
  const [callbackCode, setCallbackCode] = useState('');
  const [recordFilter, setRecordFilter] = useState('ALL');
  const [adminTab, setAdminTab] = useState('access');

  const loadAccounts = async () => {
    const res = await queryPublishAccounts();
    setAccounts(res?.data || []);
  };

  useEffect(() => {
    loadAccounts().catch(() => {});
  }, []);

  const loadPlatformConfig = async () => {
    const [platformRes, summaryRes] = await Promise.all([queryAdminPlatforms(), queryPlatformConfigSummary()]);
    const platforms = platformRes?.data || [];
    setAdminPlatforms(platforms);
    const summary = summaryRes?.data || {};
    const secretConfigured = !!summary?.wechatAppSecretConfigured;
    setWechatSecretConfigured(secretConfigured);
    platformForm.setFieldsValue({
      wechatAppId: summary?.wechatAppId || '',
      wechatAppSecret: '',
    });
  };

  useEffect(() => {
    loadPlatformConfig().catch(() => {});
  }, []);

  /** 微信公众号单平台闭环：应用凭证 → 运营账号 + OAuth → 可观测 */
  const wechatFlow = useMemo(() => {
    const wxPlat = adminPlatforms.find((p) => p.code === 'WECHAT_MP');
    const accessOk = wxPlat?.status === 'ACTIVE';
    const wxAccs = accounts.filter((a) => a.platform === 'WECHAT_MP');
    const oauthOk = wxAccs.some((a) => !!parseAccountExtra(a.extraJson).openid);
    let flowStep = 0;
    if (!accessOk) {
      flowStep = 0;
    } else if (!wxAccs.length || !oauthOk) {
      flowStep = 1;
    } else {
      flowStep = 2;
    }
    return { wxPlat, accessOk, wxAccs, oauthOk, flowStep };
  }, [adminPlatforms, accounts]);

  const handleBind = async () => {
    const values = await form.validateFields();
    setLoading(true);
    try {
      await bindPublishAccount(values);
      message.success(
        values.platform === 'WECHAT_MP' && values.authType === 'OAUTH2'
          ? '账号已保存，请使用下方「微信 OAuth」或提交回调 code 完成授权'
          : '账号已保存',
      );
      form.resetFields(['accountName', 'secret', 'extraJson']);
      await loadAccounts();
    } finally {
      setLoading(false);
    }
  };

  const saveWechatPlatformConfig = async () => {
    const values = await platformForm.validateFields();
    setSavingPlatformConfig(true);
    try {
      await savePlatformConfig({
        'wldos.socialpublish.wechat.app-id': values.wechatAppId,
        ...(values.wechatAppSecret ? { 'wldos.socialpublish.wechat.app-secret': values.wechatAppSecret } : {}),
      });
      message.success('平台接入配置已保存，状态标签将刷新为已接入');
      await loadPlatformConfig();
      await loadAccounts();
    } finally {
      setSavingPlatformConfig(false);
    }
  };

  return (
    <PageContainer
      title="内容发布管理"
      content="管理端：配置开放平台应用级凭证（AppID/AppSecret）、维护运营侧账号并查看全站发布任务；作者日常使用前台「自媒体发布」完成 OAuth 与发文。"
      style={{ padding: 0, margin: 0 }}
      bodyStyle={{ padding: '24px', margin: 0 }}
    >
      <Card size="small" style={{ marginBottom: 16 }}>
        <Tabs
          activeKey={adminTab}
          onChange={setAdminTab}
          items={[
            { key: 'access', label: '平台接入' },
            {
              key: 'accounts',
              label: (
                <span>
                  运营账号
                  {wechatFlow.accessOk && (!wechatFlow.wxAccs.length || !wechatFlow.oauthOk) ? (
                    <Badge dot offset={[6, 0]} />
                  ) : null}
                </span>
              ),
            },
            { key: 'jobs', label: '发布任务' },
          ]}
        />
      </Card>

      {adminTab === 'access' ? (
      <Card
        title="微信公众号 · 接入与闭环"
        style={{ marginBottom: 16 }}
        extra={
          wechatFlow.flowStep === 2 ? (
            <Typography.Text type="success">当前平台链路已就绪</Typography.Text>
          ) : (
            <Typography.Text type="secondary">按步骤完成即可对外发文</Typography.Text>
          )
        }
      >
        <Steps size="small" current={wechatFlow.flowStep} style={{ marginBottom: 16 }}>
          <Step
            title="应用凭证"
            description="在公众平台「开发 → 基本配置」取得 AppID、AppSecret，保存后下方「微信公众号」状态为 ACTIVE。"
          />
          <Step
            title="运营账号与授权"
            description="切换到「运营账号」新增 WECHAT_MP，点击「微信 OAuth」完成授权（或粘贴回调 code）。"
          />
          <Step
            title="验证与观测"
            description="作者在前台「自媒体发布」发文；在此查看「发布任务」与失败重试。"
          />
        </Steps>
        <Space wrap style={{ marginBottom: 16 }} align="start">
          {wechatFlow.flowStep === 0 && (
            <Typography.Text type="secondary">
              请先填写下方 AppID / AppSecret 并点击「保存接入配置」；状态变为 ACTIVE 后进入下一步。
            </Typography.Text>
          )}
          {wechatFlow.flowStep === 1 && (
            <>
              <Button type="primary" onClick={() => setAdminTab('accounts')}>
                前往运营账号与 OAuth
              </Button>
              <Typography.Text type="secondary">或继续在下方核对凭证与状态</Typography.Text>
            </>
          )}
          {wechatFlow.flowStep === 2 && (
            <>
              <Button type="primary" onClick={() => setAdminTab('jobs')}>
                查看发布任务
              </Button>
              <Button onClick={() => setAdminTab('accounts')}>运营账号</Button>
              <Typography.Text type="secondary">前台入口：自媒体发布</Typography.Text>
            </>
          )}
        </Space>
        <Divider plain style={{ margin: '8px 0 16px' }}>
          应用凭证表单
        </Divider>
        <Typography.Paragraph type="secondary" style={{ marginBottom: 12 }}>
          保存成功后，状态标签会从 UNCONFIGURED 变为 ACTIVE，并解锁前台该平台的账号绑定提示。
        </Typography.Paragraph>
        <Form form={platformForm} layout="inline">
          <Form.Item
            name="wechatAppId"
            label="微信公众号 AppID"
            rules={[
              { required: true, message: '请填写 AppID' },
              { pattern: /^[A-Za-z0-9_-]{8,64}$/, message: 'AppID 格式不正确' },
            ]}
          >
            <Input placeholder="公众平台开发者ID" style={{ width: 260 }} />
          </Form.Item>
          <Form.Item
            name="wechatAppSecret"
            label="AppSecret"
            rules={wechatSecretConfigured
              ? [{ pattern: /^$|^[A-Za-z0-9_-]{16,128}$/, message: 'AppSecret 格式不正确' }]
              : [
                { required: true, message: '请填写 AppSecret' },
                { pattern: /^[A-Za-z0-9_-]{16,128}$/, message: 'AppSecret 格式不正确' },
              ]}
            extra={wechatSecretConfigured ? '已配置（安全起见不回显）；留空表示不变更' : ''}
          >
            <Input.Password placeholder={wechatSecretConfigured ? '留空不修改' : '公众平台开发者密钥'} style={{ width: 320 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" loading={savingPlatformConfig} onClick={saveWechatPlatformConfig}>
              保存接入配置
            </Button>
          </Form.Item>
        </Form>
        <Space wrap style={{ marginTop: 12 }}>
          {adminPlatforms.map((p) => (
            <Tag key={p.code} color={p.status === 'ACTIVE' ? 'green' : p.status === 'UNCONFIGURED' ? 'orange' : 'blue'}>
              {`${p.name}：${p.status}`}
            </Tag>
          ))}
        </Space>
      </Card>
      ) : null}

      {adminTab === 'accounts' ? (
      <>
      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
        message="「运营账号」与产品目标一致：把具体自媒体账号接入系统以便代发。"
        description="此处面向运维/联调：可手动录入凭据或走微信 OAuth。普通作者在作品侧使用前台「自媒体发布 → 平台账号」完成绑定即可，无需打开本页。若作者在前台添加了「WEB 会话」渠道（不占开放平台 AppId），列表 Tag 会显示为 WEB会话，子任务走桌面委托。"
      />
      <Card
        title="运营账号"
        style={{ marginBottom: 16 }}
        extra={
          wechatFlow.accessOk ? (
            <Typography.Text type="secondary">微信公众号闭环：保存账号后使用下方 OAuth</Typography.Text>
          ) : (
            <Typography.Text type="warning">请先在「平台接入」保存 AppID/AppSecret</Typography.Text>
          )
        }
      >
        {wechatFlow.accessOk && wechatFlow.wxAccs.length === 0 ? (
          <Alert
            type="info"
            showIcon
            style={{ marginBottom: 12 }}
            message="尚未添加微信公众号运营账号"
            description="填写下方表单并保存后，即可使用「微信 OAuth」完成授权。"
          />
        ) : null}
        {wechatFlow.accessOk && wechatFlow.wxAccs.length > 0 && !wechatFlow.oauthOk ? (
          <Alert
            type="warning"
            showIcon
            style={{ marginBottom: 12 }}
            message="微信公众号尚未完成 OAuth"
            description="请点击下方「微信OAuth」在浏览器中授权，或将回调页 code 粘贴到输入框后提交。"
          />
        ) : null}
        {wechatFlow.accessOk && wechatFlow.oauthOk ? (
          <Alert
            type="success"
            showIcon
            style={{ marginBottom: 12 }}
            message="微信公众号已授权（已回填 openid）"
            description="可切换到「发布任务」查看同步结果，或在前台「自媒体发布」发起发文。"
          />
        ) : null}
        <Form
          form={form}
          layout="inline"
          initialValues={{ platform: 'WECHAT_MP', authType: 'OAUTH2' }}
        >
          <Form.Item name="platform" label="平台" rules={[{ required: true }]}>
            <Select style={{ width: 140 }} options={platformOptions.map((p) => ({ label: p, value: p }))} />
          </Form.Item>
          <Form.Item name="accountName" label="账号名" rules={[{ required: true }]}>
            <Input placeholder="如：公众号A" style={{ width: 160 }} />
          </Form.Item>
          <Form.Item name="authType" label="认证">
            <Select
              style={{ width: 120 }}
              options={[{ label: 'API_KEY', value: 'API_KEY' }, { label: 'OAUTH2', value: 'OAUTH2' }]}
            />
          </Form.Item>
          <Form.Item name="secret" label="凭据" rules={[{ required: true }]}>
            <Input.Password placeholder="token / appSecret" style={{ width: 200 }} />
          </Form.Item>
          <Form.Item name="extraJson" label="扩展">
            <Input placeholder='{"appId":"xxx"}' style={{ width: 220 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" loading={loading} onClick={handleBind}>
              保存
            </Button>
          </Form.Item>
        </Form>
        <Space wrap style={{ marginTop: 12 }}>
          {accounts.map((a) => {
            const isWx = a.platform === 'WECHAT_MP';
            const isWebSession = (a.bindChannel || 'OPENAPI').toUpperCase() === 'WEB_SESSION';
            const wxOk = isWx && !!parseAccountExtra(a.extraJson).openid;
            let color = 'blue';
            if (isWebSession) {
              color = 'geekblue';
            } else if (isWx) {
              color = wxOk ? 'green' : 'orange';
            }
            const prefix = isWebSession ? 'WEB会话 · ' : '';
            const suffix = isWebSession ? '' : (isWx ? (wxOk ? ' · 已 OAuth' : ' · 待 OAuth') : '');
            return (
              <Tag key={a.id} color={color}>{`${prefix}${a.platform}:${a.accountName}${suffix}`}</Tag>
            );
          })}
        </Space>
        <div style={{ marginTop: 12 }}>
          <Space wrap>
            {accounts.filter((a) => a.platform === 'WECHAT_MP').map((a) => (
              <Space key={`wx-${a.id}`}>
                <Button
                  size="small"
                  loading={oauthLoadingId === a.id}
                  onClick={async () => {
                    setOauthLoadingId(a.id);
                    try {
                      const redirectUri = `${window.location.origin}/admin/sys/social-publish`;
                      const res = await wechatOAuthStart(a.id, { redirectUri });
                      const data = res?.data;
                      if (data?.authUrl) {
                        window.open(data.authUrl, '_blank', 'noopener,noreferrer');
                      } else {
                        message.warning('未返回授权地址');
                      }
                    } finally {
                      setOauthLoadingId(null);
                    }
                  }}
                >
                  微信OAuth
                </Button>
                <Upload
                  showUploadList={false}
                  beforeUpload={(file) => {
                    uploadWechatThumb(a.id, file)
                      .then(() => message.success('封面素材已上传并回填'))
                      .catch((e) => message.error(e?.data?.message || '上传失败'));
                    return false;
                  }}
                >
                  <Button size="small">上传封面</Button>
                </Upload>
              </Space>
            ))}
          </Space>
          <Space style={{ marginTop: 8 }}>
            <Input
              placeholder="微信回调 code（可粘贴）"
              value={callbackCode}
              onChange={(e) => setCallbackCode(e.target.value)}
              style={{ width: 320 }}
            />
            <Button
              onClick={async () => {
                const target = accounts.find((a) => a.platform === 'WECHAT_MP');
                if (!target?.id) {
                  message.warning('请先绑定微信公众号账号');
                  return;
                }
                if (!callbackCode) {
                  message.warning('请先输入 code');
                  return;
                }
                await wechatOAuthCallback(target.id, { code: callbackCode });
                message.success('OAuth 回调成功，已回填 openid');
                setCallbackCode('');
                await loadAccounts();
              }}
            >
              提交回调code
            </Button>
          </Space>
        </div>
      </Card>
      </>
      ) : null}

      {adminTab === 'jobs' ? (
      <>
      <Card size="small" style={{ marginBottom: 12 }}>
        <Space>
          <span>账号状态快速筛选：</span>
          <Radio.Group
            size="small"
            value={recordFilter}
            onChange={(e) => {
              setRecordFilter(e.target.value);
              actionRef.current?.reload();
            }}
          >
            <Radio.Button value="ALL">全部任务</Radio.Button>
            <Radio.Button value="REAUTH">需授权</Radio.Button>
          </Radio.Group>
        </Space>
      </Card>

      <ProTableX
        actionRef={actionRef}
        rowKey="id"
        headerTitle="发布任务"
        search={{ labelWidth: 100 }}
        expandable={{
          expandedRowRender: (record) => (
            <div style={{ padding: '8px 0' }}>
              {(record.tasks || []).length ? (
                (record.tasks || []).map((task) => (
                  <Space key={task.id} style={{ display: 'flex', marginBottom: 8 }} wrap>
                    <Tag>{task.platform}</Tag>
                    {task.executionMode === 'DESKTOP_DELEGATE' ? <Tag color="purple">桌面委托</Tag> : <Tag>服务端API</Tag>}
                    <span>状态：{task.status}</span>
                    {task.retryCount != null ? <span>重试：{task.retryCount}</span> : null}
                    {task.errorCode ? <Tag color="orange">{task.errorCode}</Tag> : null}
                    {task.errorMsg ? <span style={{ color: '#999' }}>{task.errorMsg}</span> : null}
                  </Space>
                ))
              ) : (
                <Typography.Text type="secondary">暂无子任务</Typography.Text>
              )}
            </div>
          ),
        }}
        request={async (params) => {
          const res = await queryPublishJobs({
            current: params.current,
            pageSize: params.pageSize,
            condition: recordFilter === 'REAUTH' ? { requireReauth: true } : {},
          });
          const data = res?.data;
          return {
            data: data?.rows || [],
            total: data?.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={[
          { title: '任务ID', dataIndex: 'id', width: 90, hideInSearch: true },
          { title: '源类型', dataIndex: 'sourceType', hideInSearch: true },
          { title: '内容模式', dataIndex: 'contentMode', hideInSearch: true },
          { title: '状态', dataIndex: 'status', hideInSearch: true },
          { title: '创建时间', dataIndex: 'createTime', hideInSearch: true },
          {
            title: '操作',
            valueType: 'option',
            hideInSearch: true,
            render: (_, record) => [
              <a
                key="retry"
                onClick={async () => {
                  await retryFailedJob(record.id);
                  message.success('已触发重试');
                  actionRef.current?.reload();
                }}
              >
                重试失败项
              </a>,
            ],
          },
        ]}
      />
      </>
      ) : null}
    </PageContainer>
  );
}
