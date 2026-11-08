import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Button, Card, Collapse, Form, Input, InputNumber, message, notification, Select, Space, Tooltip, Typography } from 'antd';
import {
  AppstoreOutlined,
  BankOutlined,
  GlobalOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  LockOutlined,
  MailOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import { provisionTenant, searchUsers, fetchProvisionScenarioSelect, fetchProvisionScenarios } from '../service';
import { defaultProvisionSitePort, resolveProvisionSiteUrl } from '../siteUrl';

const { Text } = Typography;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const accountFieldNames = ['adminLoginName', 'adminEmail', 'adminNickname', 'adminPasswd', 'adminConfirm'];

const requiredSiteFieldNames = ['comCode', 'comName', 'siteDomain'];

const hasAnyAccountInput = (values) =>
  accountFieldNames.some((key) => {
    const v = values?.[key];
    return v != null && String(v).trim() !== '';
  });

const syncInputValueOnBlur = (form, fieldName) => (event) => {
  const value = event?.target?.value;
  if (value != null && value !== '') {
    form.setFieldsValue({ [fieldName]: value });
  }
};

const SiteUrlPreview = ({ form }) => {
  const siteDomain = Form.useWatch('siteDomain', form);
  const siteScheme = Form.useWatch('siteScheme', form);
  const sitePort = Form.useWatch('sitePort', form);
  const siteUrl = Form.useWatch('siteUrl', form);
  const preview = resolveProvisionSiteUrl({ siteDomain, siteScheme, sitePort, siteUrl });
  if (!preview) {
    return null;
  }
  return (
    <Form.Item label="访问地址预览">
      <Text copyable={{ text: preview }}>{preview}</Text>
    </Form.Item>
  );
};

const syncAutofillPasswordFields = (form) => {
  const patch = {};
  ['adminPasswd', 'adminConfirm'].forEach((fieldName) => {
    if (form.getFieldValue(fieldName)) {
      return;
    }
    const fieldRef = form.getFieldInstance(fieldName);
    const domValue = fieldRef?.input?.value;
    if (domValue) {
      patch[fieldName] = domValue;
    }
  });
  if (Object.keys(patch).length > 0) {
    form.setFieldsValue(patch);
  }
};

const TenantProvisionForm = ({
  form,
  footer,
  userOptions,
  userSearchLoading,
  onUserSearch,
  scenarioOptions,
  activeScenario,
}) => (
  <>
    <Form
      form={form}
      {...formLayout}
      initialValues={{
        siteScheme: 'http',
        sitePort: defaultProvisionSitePort(),
        siteUrl: '',
      }}
    >
      <Card
        title={<Space><BankOutlined style={{ color: '#1890ff' }} />入驻场景</Space>}
        size="small"
        style={{ marginBottom: 16 }}
        extra={<Tooltip title="从平台登记的模板站复制资源与应用挂载"><InfoCircleOutlined /></Tooltip>}
      >
        <Form.Item
          name="provisionScenarioCode"
          label="场景"
          rules={[{ required: true, message: '请选择入驻场景' }]}
        >
          <Select options={scenarioOptions} placeholder="选择入驻场景" />
        </Form.Item>
        {activeScenario ? (
          <Form.Item label="模板状态" colon={false}>
            <Text type={activeScenario.ready ? undefined : 'danger'}>
              模板 {activeScenario.templateSiteDomain || activeScenario.templateDomainId}
              {' · '}资源 {activeScenario.resourceCount ?? 0} / 应用 {activeScenario.appCount ?? 0}
              {activeScenario.cloneSummary ? ` · ${activeScenario.cloneSummary}` : ''}
              {!activeScenario.ready ? ' · 模板站挂载未就绪，请先在站点管理配置' : ''}
            </Text>
          </Form.Item>
        ) : null}
      </Card>
      <Card
        title={<Space><AppstoreOutlined style={{ color: '#1890ff' }} />公司</Space>}
        size="small"
        style={{ marginBottom: 16 }}
        extra={<Tooltip title="新租户公司信息"><InfoCircleOutlined /></Tooltip>}
      >
        <Form.Item name="comCode" label="公司编码" rules={[{ required: true, message: '请输入公司编码' }]}>
          <Input placeholder="如 tenant-b" />
        </Form.Item>
        <Form.Item name="comName" label="公司名称" rules={[{ required: true, message: '请输入公司名称' }]}>
          <Input placeholder="如 租户 B" />
        </Form.Item>
        <Form.Item name="comDesc" label="公司描述">
          <Input.TextArea rows={2} placeholder="可选" />
        </Form.Item>
      </Card>
      <Card
        title={<Space><GlobalOutlined style={{ color: '#1890ff' }} />主站（强制）</Space>}
        size="small"
        style={{ marginBottom: 16 }}
        extra={<Tooltip title="每个租户必须有一个主站域名"><InfoCircleOutlined /></Tooltip>}
      >
        <Form.Item name="siteDomain" label="主站域名" rules={[{ required: true, message: '请输入主站域名' }]}>
          <Input placeholder="如 b.example.com" />
        </Form.Item>
        <Form.Item name="siteName" label="主站名称">
          <Input placeholder="默认同公司名称" />
        </Form.Item>
        <Form.Item
          name="sitePort"
          label={
            <Space>
              访问端口
              <Tooltip title="本地 dev 常用 8088；生产 80/443 可填对应端口（URL 中自动省略）">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            </Space>
          }
        >
          <InputNumber min={1} max={65535} precision={0} style={{ width: '100%' }} placeholder="如 8088" />
        </Form.Item>
        <Form.Item name="siteScheme" label="访问协议">
          <Select
            options={[
              { value: 'http', label: 'http' },
              { value: 'https', label: 'https' },
            ]}
            onChange={(value) => {
              if (value === 'https' && !form.getFieldValue('sitePort')) {
                form.setFieldsValue({ sitePort: 443 });
              }
            }}
          />
        </Form.Item>
        <SiteUrlPreview form={form} />
        <Collapse ghost style={{ marginBottom: 8 }}>
          <Collapse.Panel header="高级：完整访问 URL" key="siteUrl">
            <Form.Item
              name="siteUrl"
              label="访问 URL"
              extra="填写后优先于上方端口/协议自动组装"
            >
              <Input placeholder="如 https://b.example.com:8443" />
            </Form.Item>
          </Collapse.Panel>
        </Collapse>
        <Form.Item name="archCode" label="体系编码">
          <Input placeholder="默认同公司编码-arch" />
        </Form.Item>
        <Form.Item name="archName" label="体系名称">
          <Input placeholder="默认同公司名称+体系" />
        </Form.Item>
      </Card>
      <Card
        title={<Space><TeamOutlined style={{ color: '#1890ff' }} />租户管理员</Space>}
        size="small"
        extra={<Tooltip title="可选；默认创建首管，也可折叠指定已有用户"><InfoCircleOutlined /></Tooltip>}
      >
        <Form.Item
          name="adminLoginName"
          label={
            <Space>
              登录账号
              <Tooltip title="首管登录用户名，可与邮箱不同">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            </Space>
          }
          rules={[
            { required: true, message: '请输入登录账号' },
            { max: 30, message: '登录账号最多 30 个字符' },
          ]}
        >
          <Input prefix={<KeyOutlined />} placeholder="如 tenant_b_admin，最多 30 字符" />
        </Form.Item>
        <Form.Item
          name="adminEmail"
          label={
            <Space>
              邮箱
              <Tooltip title="用于接收通知，可与登录账号不同">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            </Space>
          }
          rules={[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '邮箱格式不正确' },
          ]}
        >
          <Input prefix={<MailOutlined />} placeholder="admin@tenant-b.example.com" />
        </Form.Item>
        <Form.Item
          name="adminNickname"
          label={
            <Space>
              昵称
              <Tooltip title="首管在平台显示的名称">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            </Space>
          }
          rules={[
            { required: true, message: '请输入昵称' },
            { max: 50, message: '昵称最多 50 个字符' },
          ]}
        >
          <Input prefix={<UserOutlined />} placeholder="租户管理员" />
        </Form.Item>
        <Form.Item
          name="adminPasswd"
          label={
            <Space>
              登录密码
              <Tooltip title="6～100 个字符">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            </Space>
          }
          rules={[
            { required: true, message: '请输入登录密码' },
            { min: 6, max: 100, message: '密码长度须在 6-100 之间' },
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="6～100 个字符"
            autoComplete="new-password"
            onBlur={syncInputValueOnBlur(form, 'adminPasswd')}
          />
        </Form.Item>
        <Form.Item
          name="adminConfirm"
          label="确认密码"
          dependencies={['adminPasswd']}
          rules={[
            { required: true, message: '请再次输入密码' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('adminPasswd') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入的密码不一致'));
              },
            }),
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="再次输入密码"
            autoComplete="new-password"
            onBlur={syncInputValueOnBlur(form, 'adminConfirm')}
          />
        </Form.Item>
        <Collapse ghost>
          <Collapse.Panel header="指定已有用户（高级）" key="existing">
            <Form.Item name="tenantAdminUserIds" label="已有用户">
              <Select
                mode="multiple"
                allowClear
                showSearch
                filterOption={false}
                placeholder="输入昵称或账号搜索"
                options={userOptions}
                loading={userSearchLoading}
                onSearch={onUserSearch}
                notFoundContent={userSearchLoading ? '搜索中…' : '暂无数据，请输入关键词搜索'}
              />
            </Form.Item>
          </Collapse.Panel>
        </Collapse>
      </Card>
    </Form>
    {footer}
  </>
);

const TenantProvisionWizard = ({ visible, onCancel, onSuccess, embedded = false }) => {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [userOptions, setUserOptions] = useState([]);
  const [userSearchLoading, setUserSearchLoading] = useState(false);
  const [scenarioOptions, setScenarioOptions] = useState([]);
  const [scenarioViews, setScenarioViews] = useState([]);
  const searchTimerRef = useRef(null);
  const provisionScenarioCode = Form.useWatch('provisionScenarioCode', form);

  const activeScenario = useMemo(
    () => scenarioViews.find((item) => item.code === provisionScenarioCode),
    [scenarioViews, provisionScenarioCode],
  );

  const loadScenarios = useCallback(async () => {
    const [selectRes, listRes] = await Promise.all([
      fetchProvisionScenarioSelect(),
      fetchProvisionScenarios(),
    ]);
    const options = selectRes?.data || [];
    const views = listRes?.data || [];
    setScenarioOptions(options);
    setScenarioViews(views);
    const defaultCode = views.find((item) => item.defaultScenario)?.code || options[0]?.value;
    if (defaultCode && !form.getFieldValue('provisionScenarioCode')) {
      form.setFieldsValue({ provisionScenarioCode: defaultCode });
    }
  }, [form]);

  useEffect(() => {
    if (embedded || visible) {
      loadScenarios();
    }
  }, [embedded, visible, loadScenarios]);

  const doUserSearch = useCallback(async (keyword) => {
    const q = (keyword || '').trim();
    if (!q) {
      setUserOptions([]);
      return;
    }
    setUserSearchLoading(true);
    try {
      const res = await searchUsers({ current: 1, pageSize: 20, nickname: q, loginName: q });
      const rows = res?.data?.rows || [];
      setUserOptions(
        rows.map((u) => ({
          value: String(u.id),
          label: `${u.nickname || u.loginName || u.id} (${u.loginName || u.id})`,
        })),
      );
    } finally {
      setUserSearchLoading(false);
    }
  }, []);

  const onUserSearch = useMemo(
    () => (keyword) => {
      if (searchTimerRef.current) {
        clearTimeout(searchTimerRef.current);
      }
      searchTimerRef.current = setTimeout(() => {
        doUserSearch(keyword);
      }, 300);
    },
    [doUserSearch],
  );

  const normalizeSitePayload = (payload) => {
    const next = { ...payload };
    if (!next.siteUrl?.trim()) {
      delete next.siteUrl;
    } else {
      next.siteUrl = next.siteUrl.trim();
    }
    if (next.sitePort == null || next.sitePort === '') {
      delete next.sitePort;
    }
    return next;
  };

  const buildPayload = (values) => {
    const {
      adminLoginName,
      adminEmail,
      adminNickname,
      adminPasswd,
      adminConfirm,
      tenantAdminUserIds,
      ...rest
    } = values;
    const hasCreate = hasAnyAccountInput(values);
    const hasExisting = Array.isArray(tenantAdminUserIds) && tenantAdminUserIds.length > 0;

    if (hasCreate && hasExisting) {
      message.error('创建首管与指定已有用户不能同时填写');
      return null;
    }
    if (hasCreate) {
      if (!adminLoginName?.trim() || !adminEmail?.trim() || !adminNickname?.trim() || !adminPasswd || !adminConfirm) {
        message.error('创建首管需填写登录账号、邮箱、昵称、密码与确认密码');
        return null;
      }
      if (adminLoginName.trim().length > 30) {
        message.error('登录账号最多 30 个字符');
        return null;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(adminEmail.trim())) {
        message.error('邮箱格式不正确');
        return null;
      }
      if (adminPasswd !== adminConfirm) {
        message.error('两次输入的密码不一致');
        return null;
      }
      if (adminPasswd.length < 6 || adminPasswd.length > 100) {
        message.error('密码长度须在 6-100 之间');
        return null;
      }
      return normalizeSitePayload({
        ...rest,
        tenantAdminAccount: {
          loginName: adminLoginName.trim(),
          email: adminEmail.trim(),
          nickname: adminNickname.trim(),
          passwd: adminPasswd,
          confirm: adminConfirm,
        },
      });
    }
    if (hasExisting) {
      return normalizeSitePayload({
        ...rest,
        tenantAdminUserIds,
      });
    }
    return normalizeSitePayload({ ...rest });
  };

  const handleSubmit = async () => {
    try {
      await form.validateFields([...requiredSiteFieldNames, 'provisionScenarioCode']);
      if (activeScenario && !activeScenario.ready) {
        message.warning('当前入驻场景模板站挂载未就绪，请先在站点管理配置应用与资源');
        return;
      }
      const snapshot = form.getFieldsValue(true);
      const hasExisting = Array.isArray(snapshot.tenantAdminUserIds) && snapshot.tenantAdminUserIds.length > 0;
      const hasCreate = hasAnyAccountInput(snapshot);
      if (hasCreate && hasExisting) {
        message.error('创建首管与指定已有用户不能同时填写');
        return;
      }
      if (hasCreate) {
        syncAutofillPasswordFields(form);
        await form.validateFields(accountFieldNames);
      }
      const values = form.getFieldsValue(true);
      const payload = buildPayload(values);
      if (!payload) {
        return;
      }
      setSubmitting(true);
      const res = await provisionTenant(payload);
      if (res?.code !== 200) {
        return;
      }
      const data = res?.data || {};
      if (data.message) {
        message.warning(data.message);
      }
      const adminHint = data.tenantAdminLoginName ? `首管账号：${data.tenantAdminLoginName}` : '';
      if (data.adminUrl) {
        notification.success({
          message: '入驻成功',
          description: (
            <span>
              管理入口：
              <a href={data.adminUrl} target="_blank" rel="noopener noreferrer">
                {data.adminUrl}
              </a>
              {adminHint ? `；${adminHint}` : ''}
            </span>
          ),
          duration: 8,
        });
      } else {
        message.success(`入驻成功${adminHint ? `，${adminHint}` : ''}`);
      }
      form.resetFields();
      form.setFieldsValue({
        siteScheme: 'http',
        sitePort: defaultProvisionSitePort(),
        siteUrl: '',
      });
      setUserOptions([]);
      onSuccess?.(data);
      if (!embedded) {
        onCancel?.();
      }
    } catch (error) {
      if (error?.errorFields) {
        return;
      }
      const errMsg = error?.response?.message || error?.message;
      if (errMsg) {
        notification.error({ message: '入驻失败', description: errMsg });
      }
    } finally {
      setSubmitting(false);
    }
  };

  const footer = embedded ? (
    <div style={{ marginTop: 24, textAlign: 'right' }}>
      <Space>
        <Button type="primary" loading={submitting} onClick={handleSubmit}>
          确认入驻
        </Button>
      </Space>
    </div>
  ) : null;

  const formNode = (
    <TenantProvisionForm
      form={form}
      footer={footer}
      userOptions={userOptions}
      userSearchLoading={userSearchLoading}
      onUserSearch={onUserSearch}
      scenarioOptions={scenarioOptions}
      activeScenario={activeScenario}
    />
  );

  if (embedded) {
    return formNode;
  }

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{ padding: 24 }}
      destroyOnClose
      title={
        <Space>
          <BankOutlined style={{ color: '#1890ff' }} />
          租户入驻
        </Space>
      }
      visible={visible}
      onCancel={onCancel}
      footer={
        <Space>
          <Button onClick={onCancel}>取消</Button>
          <Button type="primary" loading={submitting} onClick={handleSubmit}>
            确认入驻
          </Button>
        </Space>
      }
    >
      {formNode}
    </FullscreenModal>
  );
};

export default TenantProvisionWizard;
