import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useLocation } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { Alert, Button, Card, Checkbox, Col, DatePicker, Empty, Form, Input, Menu, message, Modal, Radio, Row, Select, Space, Spin, Typography, Tabs, Tag, Tooltip } from 'antd';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import {
  bindMyPublishAccount,
  removeMyPublishAccount,
  createPublishJob,
  publishJobNow,
  retryJobFailed,
  queryMyPublishAccounts,
  queryMyPublishJobs,
  queryMyPublishPreference,
  queryPubMetaBrief,
  queryMediaAssistantCapability,
  querySupportedPlatforms,
  saveMyPublishPreference,
  completeDelegateTask,
} from './service';
import {
  buildDesktopExternalChannel,
  isDesktopEmbedded,
  openDesktopExternal,
  purgeDesktopExternalAccount,
  showDesktopExternal,
} from '@/utils/desktopEmbeddedBridge';

const VALID_TABS = ['defaults', 'publish', 'accounts', 'records'];
const DEFAULT_SCHEDULE_POLICY = { minLeadMinutes: 60, maxLeadMinutes: 14 * 24 * 60 };

const normalizeTab = (tab) => {
  if (tab === 'settings') return 'accounts';
  return VALID_TABS.includes(tab) ? tab : 'publish';
};

const readTabFromQuery = () => {
  if (typeof window === 'undefined') {
    return 'publish';
  }
  const query = new URLSearchParams(window.location.search || '');
  const raw = query.get('tab');
  if (!raw) return 'publish';
  return normalizeTab(raw);
};

/** bind_channel=WEB_SESSION 时展示轻量标记 */
const bindChannelTag = (channel) => {
  const c = (channel || '').toUpperCase();
  if (c === 'WEB_SESSION') {
    return <Tag color="geekblue">网页同步</Tag>;
  }
  return null;
};

export default function SocialPublishPage() {
  const location = useLocation();
  const actionRef = useRef();
  useDesktopSticky(actionRef);
  const [accounts, setAccounts] = useState([]);
  /** 接口已仅返回 WEB_SESSION；useMemo 防御旧缓存 */
  const assistantAccounts = useMemo(
    () => (accounts || []).filter((a) => (a.bindChannel || '').toUpperCase() === 'WEB_SESSION'),
    [accounts],
  );
  const [submitting, setSubmitting] = useState(false);
  const [defaultAccountIds, setDefaultAccountIds] = useState([]);
  const [defaultIncludeWldos, setDefaultIncludeWldos] = useState(true);
  const [jobCondition, setJobCondition] = useState({});
  const [recordFilter, setRecordFilter] = useState('ALL');
  const [activeTab, setActiveTab] = useState(readTabFromQuery);
  const [selectedAccountId, setSelectedAccountId] = useState(null);
  const [form] = Form.useForm();
  const [webSessionForm] = Form.useForm();
  const [recordRows, setRecordRows] = useState([]);
  const [autoContentMode, setAutoContentMode] = useState('MARKDOWN');
  const [publishLaunchMode, setPublishLaunchMode] = useState('');
  const [schedulePanelOpen, setSchedulePanelOpen] = useState(false);
  /** 内容发布 Tab：展示标题（ID 仅只读展示，不可手改） */
  const [sourcePubSummary, setSourcePubSummary] = useState({
    loading: false,
    title: '',
    id: '',
    error: null,
  });
  /** 融媒宝式 WEB_SESSION 是否已接入（后端 capability） */
  const [mediaAssistantCap, setMediaAssistantCap] = useState(null);
  /** 添加账号时可选平台 */
  const [assistantPlatforms, setAssistantPlatforms] = useState([]);
  const [webSessionModalOpen, setWebSessionModalOpen] = useState(false);
  const [webSessionSubmitting, setWebSessionSubmitting] = useState(false);
  /** 内容发布 Tab：当前勾选的账号 ID（用于 WEB_SESSION 提示，随表单与 accounts 刷新） */
  const [publishAccountIds, setPublishAccountIds] = useState([]);
  const [scheduleRule, setScheduleRule] = useState({
    minLeadMinutes: DEFAULT_SCHEDULE_POLICY.minLeadMinutes,
    maxLeadMinutes: DEFAULT_SCHEDULE_POLICY.maxLeadMinutes,
    platformCodes: [],
  });
  /** 桌面委托子任务：在「发布记录」展开行内确认结果时，可选填平台链接 */
  const [delegateUrlByTaskId, setDelegateUrlByTaskId] = useState({});
  const desktopEmbedded = isDesktopEmbedded();

  const defaultAccountIdsForUi = useMemo(
    () => defaultAccountIds.filter((id) => assistantAccounts.some((a) => String(a.id) === String(id))),
    [defaultAccountIds, assistantAccounts],
  );

  const toDate = useCallback((v) => {
    if (!v) return null;
    if (v instanceof Date) return v;
    if (typeof v.toDate === 'function') return v.toDate();
    return null;
  }, []);

  const platformPolicyMap = useMemo(() => {
    const map = {};
    (assistantPlatforms || []).forEach((p) => {
      const code = String(p?.code || '').toUpperCase();
      const minLeadMinutes = Number(p?.scheduleMinLeadMinutes);
      const maxLeadMinutes = Number(p?.scheduleMaxLeadMinutes);
      if (code && Number.isFinite(minLeadMinutes) && Number.isFinite(maxLeadMinutes) && minLeadMinutes > 0 && maxLeadMinutes > minLeadMinutes) {
        map[code] = { minLeadMinutes, maxLeadMinutes };
      }
    });
    return map;
  }, [assistantPlatforms]);

  const calcScheduleRule = useCallback((selectedIds) => {
    const ids = Array.isArray(selectedIds) ? selectedIds : [];
    const selectedAccounts = assistantAccounts.filter((a) => ids.some((id) => String(id) === String(a.id)));
    if (!selectedAccounts.length) {
      return {
        minLeadMinutes: DEFAULT_SCHEDULE_POLICY.minLeadMinutes,
        maxLeadMinutes: DEFAULT_SCHEDULE_POLICY.maxLeadMinutes,
        platformCodes: [],
      };
    }
    let minLeadMinutes = 0;
    let maxLeadMinutes = Number.MAX_SAFE_INTEGER;
    const platformSet = new Set();
    selectedAccounts.forEach((a) => {
      const code = String(a?.platform || '').toUpperCase();
      platformSet.add(code || 'UNKNOWN');
      const policy = platformPolicyMap[code] || DEFAULT_SCHEDULE_POLICY;
      minLeadMinutes = Math.max(minLeadMinutes, Number(policy.minLeadMinutes) || DEFAULT_SCHEDULE_POLICY.minLeadMinutes);
      maxLeadMinutes = Math.min(maxLeadMinutes, Number(policy.maxLeadMinutes) || DEFAULT_SCHEDULE_POLICY.maxLeadMinutes);
    });
    if (!Number.isFinite(maxLeadMinutes) || maxLeadMinutes <= 0) {
      maxLeadMinutes = DEFAULT_SCHEDULE_POLICY.maxLeadMinutes;
    }
    return { minLeadMinutes, maxLeadMinutes, platformCodes: Array.from(platformSet) };
  }, [assistantAccounts, platformPolicyMap]);

  const scheduleBounds = useMemo(() => {
    const now = new Date();
    return {
      minAt: new Date(now.getTime() + scheduleRule.minLeadMinutes * 60 * 1000),
      maxAt: new Date(now.getTime() + scheduleRule.maxLeadMinutes * 60 * 1000),
    };
  }, [scheduleRule]);

  const disabledScheduleDate = useCallback((current) => {
    const d = toDate(current);
    if (!d) return false;
    return d.getTime() < scheduleBounds.minAt.getTime() || d.getTime() > scheduleBounds.maxAt.getTime();
  }, [scheduleBounds, toDate]);

  const loadAccounts = async () => {
    const res = await queryMyPublishAccounts();
    
    setAccounts(res?.data || []);
  };

  const updateTabInUrl = (tabKey, extra = {}) => {
    const query = new URLSearchParams(window.location.search || '');
    query.set('tab', normalizeTab(tabKey));
    query.delete('launchMode');
    if (extra.launchMode) {
      query.set('launchMode', extra.launchMode);
    }
    const next = `${window.location.pathname}?${query.toString()}`;
    window.history.replaceState({}, document.title, next);
  };

  /** 与地址栏同步：tab / 内容格式 / 发布记录筛选；sourcePubId 表单值由下方专用 effect 写入 */
  useEffect(() => {
    const query = new URLSearchParams(location.search || '');
    const rawTab = query.get('tab');
    const tab = normalizeTab(rawTab || 'publish');
    const sourcePubId = query.get('sourcePubId');
    const mode = (query.get('contentMode') || '').toUpperCase();
    const launchMode = (query.get('launchMode') || '').toLowerCase();

    const prefill = {};
    const cond = {};
    if (sourcePubId) {
      cond.sourcePubId = sourcePubId;
    }
    if (mode === 'MARKDOWN' || mode === 'HTML') {
      prefill.contentMode = mode;
      setAutoContentMode(mode);
    }
    setPublishLaunchMode(launchMode === 'scheduled' ? 'scheduled' : '');
    if (Object.keys(prefill).length) {
      form.setFieldsValue(prefill);
    }
    if (Object.keys(cond).length) {
      setJobCondition(cond);
      setTimeout(() => actionRef.current?.reload(), 0);
    }
    setActiveTab(tab);
  }, [form, location.search]);

  useEffect(() => {
    queryMyPublishPreference().then((res) => {
      const data = res?.data || {};
      setDefaultAccountIds(data?.accountIds || []);
      setDefaultIncludeWldos(data?.includeWldosSelf !== false);
    }).catch(() => {});
  }, []);

  useEffect(() => {
    loadAccounts().catch(() => {});
  }, []);

  useEffect(() => {
    if (activeTab === 'publish' && publishLaunchMode === 'scheduled') {
      const t = window.setTimeout(() => setSchedulePanelOpen(true), 120);
      return () => window.clearTimeout(t);
    }
    setSchedulePanelOpen(false);
    return undefined;
  }, [activeTab, publishLaunchMode]);

  useEffect(() => {
    if (!assistantAccounts.length) {
      setSelectedAccountId(null);
      return;
    }
    if (selectedAccountId && !assistantAccounts.some((a) => String(a.id) === String(selectedAccountId))) {
      setSelectedAccountId(null);
    }
  }, [assistantAccounts, selectedAccountId]);

  useEffect(() => {
    if (!webSessionModalOpen && assistantPlatforms.length > 0) {
      return undefined;
    }
    let cancelled = false;
    querySupportedPlatforms()
      .then((res) => {
        if (cancelled) return;
        setAssistantPlatforms(res?.data || []);
      })
      .catch(() => {
        if (!cancelled) setAssistantPlatforms([]);
      });
    return () => {
      cancelled = true;
    };
  }, [webSessionModalOpen, assistantPlatforms.length]);

  useEffect(() => {
    let cancelled = false;
    queryMediaAssistantCapability()
      .then((res) => {
        if (cancelled) return;
        setMediaAssistantCap(res?.data ?? null);
      })
      .catch(() => {
        if (!cancelled) setMediaAssistantCap(null);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (activeTab !== 'publish') {
      return undefined;
    }
    const ids = form.getFieldValue('accountIds') || [];
    setPublishAccountIds(ids);
    setScheduleRule(calcScheduleRule(ids.length ? ids : defaultAccountIdsForUi));
    return undefined;
  }, [activeTab, accounts, form, calcScheduleRule, defaultAccountIdsForUi]);

  /** URL 中 sourcePubId 变化时：同步表单隐藏字段并拉取标题（创作页发布中心跳转） */
  useEffect(() => {
    const q = new URLSearchParams(location.search || '');
    const sid = q.get('sourcePubId');
    if (!sid) {
      setSourcePubSummary({ loading: false, title: '', id: '', error: null });
      form.setFieldsValue({ sourcePubId: undefined });
      return undefined;
    }
    let cancelled = false;
    form.setFieldsValue({ sourcePubId: sid });
    setSourcePubSummary({ loading: true, title: '', id: sid, error: null });
    queryPubMetaBrief(sid)
      .then((res) => {
        const data = res?.data;
        if (cancelled) return;
        setSourcePubSummary({
          loading: false,
          title: data?.pubTitle || '',
          id: sid,
          error: null,
        });
      })
      .catch(() => {
        if (cancelled) return;
        setSourcePubSummary({
          loading: false,
          title: '',
          id: sid,
          error: '无法加载内容信息，请确认该内容为您本人创建',
        });
      });
    return () => {
      cancelled = true;
    };
  }, [location.search, form]);

  const handleCreate = async (opts = {}) => {
    let values;
    try {
      values = await form.validateFields();
    } catch (e) {
      message.warning('请先关联要发布的作品：在作品编辑页保存后，用「发布中心」打开本页。');
      return;
    }
    const wantSchedule = publishLaunchMode === 'scheduled' && !opts.immediate;
    if (wantSchedule && !values.scheduledTime) {
      message.warning('请先选择定时发布时间');
      return;
    }
    const finalAccountIds = Array.isArray(values.accountIds) && values.accountIds.length
      ? values.accountIds
      : (Array.isArray(defaultAccountIdsForUi) ? defaultAccountIdsForUi : []);
    if (!finalAccountIds.length) {
      message.warning('请先绑定并选择至少一个发布账号');
      return;
    }
    const scheduledTime = wantSchedule && values.scheduledTime && typeof values.scheduledTime.format === 'function'
      ? values.scheduledTime.format('YYYY-MM-DD HH:mm:ss')
      : undefined;
    if (wantSchedule && values.scheduledTime) {
      const dt = toDate(values.scheduledTime);
      if (!dt || dt.getTime() < scheduleBounds.minAt.getTime() || dt.getTime() > scheduleBounds.maxAt.getTime()) {
        const minHour = Math.ceil(scheduleRule.minLeadMinutes / 60);
        const maxDay = Math.floor(scheduleRule.maxLeadMinutes / (24 * 60));
        message.warning(`定时发布需在 ${minHour} 小时到 ${maxDay} 天范围内`);
        return;
      }
    }
    const payload = {
      sourceType: 'PUB_SINGLE',
      sourcePubId: values.sourcePubId,
      contentMode: autoContentMode,
      accountIds: finalAccountIds,
      scheduledTime,
    };
    setSubmitting(true);
    try {
      await createPublishJob(payload);
      form.setFieldsValue({ accountIds: finalAccountIds });
      message.success(scheduledTime ? '已设置定时发布' : '已提交发布');
      actionRef.current?.reload();
    } finally {
      setSubmitting(false);
    }
  };

  const openAddAccountModal = () => {
    const first = (assistantPlatforms || []).find((p) => (p.status || '').toUpperCase() === 'ACTIVE');
    webSessionForm.setFieldsValue({
      platform: first?.code || 'WECHAT_MP',
      remark: '',
      platformHomeUrl: undefined,
    });
    setWebSessionModalOpen(true);
  };

  const handleWebSessionBind = async () => {
    const values = await webSessionForm.validateFields();
    setWebSessionSubmitting(true);
    try {
      const bindRes = await bindMyPublishAccount({
        platform: values.platform || 'WECHAT_MP',
        bindChannel: 'WEB_SESSION',
        accountName: values.remark || undefined,
        ...(values.platformHomeUrl && String(values.platformHomeUrl).trim()
          ? { platformHomeUrl: String(values.platformHomeUrl).trim() }
          : {}),
      });
      setWebSessionModalOpen(false);
      webSessionForm.resetFields();
      await loadAccounts();
    } catch (e) {
      message.error(e?.message || '添加失败');
    } finally {
      setWebSessionSubmitting(false);
    }
  };

  const removeAssistantAccount = (account) => {
    if (!account?.id) {
      return;
    }
    Modal.confirm({
      title: '删除账号',
      content: `确认删除账号「${account.accountName || account.platform}」吗？`,
      okText: '删除',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        await removeMyPublishAccount(account.id);
        if (isDesktopEmbedded()) {
          purgeDesktopExternalAccount(account.id).catch(() => {});
        }
        message.success('已删除账号');
        if (String(selectedAccountId) === String(account.id)) {
          setSelectedAccountId(null);
        }
        await loadAccounts();
      },
    });
  };

  const goPublishWithAccount = (account) => {
    if (!account?.id) return;
    form.setFieldsValue({ accountIds: [account.id] });
    const next = 'publish';
    setActiveTab(next);
    updateTabInUrl(next);
    message.success('已切换到「内容发布」，并勾选该账号');
  };

  const selectedAccount = assistantAccounts.find((a) => String(a.id) === String(selectedAccountId));
  const selectedExternalChannel = useMemo(
    () => buildDesktopExternalChannel({
      biz: 'social',
      module: 'account',
      scene: String(selectedAccount?.platform || 'unknown').toLowerCase(),
      entity: selectedAccount?.id,
    }),
    [selectedAccount],
  );
  /** 内容发布 Tab 已勾选且为 WEB_SESSION 的账号（走桌面委托子任务） */
  const selectedWebSessionForPublish = useMemo(() => {
    const ids = Array.isArray(publishAccountIds) ? publishAccountIds.map(String) : [];
    return assistantAccounts.filter(
      (a) => ids.includes(String(a.id)) && (a.bindChannel || '').toUpperCase() === 'WEB_SESSION',
    );
  }, [assistantAccounts, publishAccountIds]);
  const isAssistantAccountReady = (account) =>
    !!account && (account.status || '').toUpperCase() === 'ACTIVE';

  const openSelectedAccountInDesktop = useCallback(() => {
    if (!selectedAccount) return;
    const target = String(selectedAccount.platformHomeUrl || '').trim();
    if (!target) {
      message.warning('当前账号未配置平台后台地址');
      return;
    }
    const ok = openDesktopExternal(selectedExternalChannel, target);
    if (!ok) {
      message.warning('当前不是桌面嵌入环境，无法在壳内打开');
    }
  }, [selectedAccount, selectedExternalChannel]);

  const doPublishNow = async (jobId) => {
    await publishJobNow(jobId);
    message.success('已触发立即发布');
    actionRef.current?.reload();
  };

  const doRetryFailed = async (jobId) => {
    await retryJobFailed(jobId);
    message.success('已发起重试');
    actionRef.current?.reload();
  };

  const clearContextFilter = () => {
    setJobCondition({});
    const query = new URLSearchParams(window.location.search || '');
    query.delete('sourcePubId');
    const next = `${window.location.pathname}?${query.toString()}`;
    window.history.replaceState({}, document.title, next);
    actionRef.current?.reload();
  };

  const hasContextFilter = Object.keys(jobCondition || {}).length > 0;
  const runningCount = (recordRows || []).filter((r) => r.status === 'RUNNING').length;
  const successCount = (recordRows || []).filter((r) => r.status === 'SUCCESS').length;
  const failedCount = (recordRows || []).filter((r) => ['FAILED', 'PARTIAL_OK'].includes(r.status)).length;
  const reauthCount = (recordRows || []).filter((r) => (r.tasks || []).some((t) => t.errorCode === 'REAUTH_REQUIRED')).length;
  const tabTips = {
    defaults: {
      title: '发布设置',
      desc: '保存创作页一键发布时默认使用的自媒体账号。',
    },
    publish: {
      title: '内容发布',
      desc: '从作品编辑页「发布中心」进入；选择要发布的账号，由桌面端在各平台完成发布。',
    },
    accounts: {
      title: '添加账号',
      desc: '先添加要同步的自媒体账号，再使用发布助手；各平台网页内登录在桌面端完成。',
    },
    records: {
      title: '发布记录',
      desc: '查看发布进度、各账号状态与失败原因。',
    },
  };
  const statCards = [
    { title: '进行中', value: runningCount, color: '#1677ff' },
    { title: '已成功', value: successCount, color: '#52c41a' },
    { title: '失败/部分成功', value: failedCount, color: '#fa8c16' },
    { title: '需授权', value: reauthCount, color: '#eb2f96' },
  ];
  const authorizedCount = (assistantAccounts || []).filter((a) => isAssistantAccountReady(a)).length;
  const runPublishNowOnPage = () => {
    setActiveTab('publish');
    setPublishLaunchMode('');
    updateTabInUrl('publish');
    const cur = form.getFieldValue('accountIds');
    const fallback = defaultAccountIdsForUi.length ? defaultAccountIdsForUi : [];
    form.setFieldsValue({
      scheduledTime: null,
      accountIds: (cur && cur.length) ? cur : fallback,
    });
    setScheduleRule(calcScheduleRule((cur && cur.length) ? cur : fallback));
    window.setTimeout(() => {
      handleCreate({ immediate: true }).catch(() => {});
    }, 0);
  };

  const enterScheduledPublishOnPage = () => {
    setActiveTab('publish');
    setPublishLaunchMode('scheduled');
    updateTabInUrl('publish', { launchMode: 'scheduled' });
    const cur = form.getFieldValue('accountIds');
    const fallback = defaultAccountIdsForUi.length ? defaultAccountIdsForUi : [];
    setScheduleRule(calcScheduleRule((cur && cur.length) ? cur : fallback));
    message.info('请选择定时发布时间，然后点击「确认发布」');
  };

  const doDelegateComplete = async (taskId, success) => {
    const key = String(taskId);
    const url = (delegateUrlByTaskId[key] || '').trim();
    try {
      await completeDelegateTask(taskId, {
        success,
        platformUrl: success && url ? url : undefined,
        errorCode: success ? undefined : 'DELEGATE_USER_ABORT',
        errorMsg: success ? undefined : '用户在网页端标记未发布成功',
      });
      message.success(success ? '已记录为已发布' : '已记录为未发布');
      setDelegateUrlByTaskId((prev) => {
        const next = { ...prev };
        delete next[key];
        return next;
      });
      actionRef.current?.reload?.();
    } catch (e) {
      message.error(e?.message || '提交失败');
    }
  };

  return (
    <PageContainer
      title="自媒体发布"
      content={
        <div>
          <div>在此<strong>添加账号</strong>、<strong>发布内容</strong>到各平台并查看进度；各平台侧登录与发文在<strong>桌面端</strong>完成。</div>
          <Typography.Text type="secondary" style={{ display: 'block', marginTop: 8, fontSize: 13 }}>
            作品编辑页「发布中心」可打开本页各 Tab；创作仍在作品编辑页完成。
          </Typography.Text>
        </div>
      }
    >
      <Card size="small" style={{ marginBottom: 16, background: '#fafcff', borderColor: '#e6f0ff' }}>
        <Space direction="vertical" size={2}>
          <Typography.Text strong>发布中心（竞品式单流程）</Typography.Text>
          <Typography.Text type="secondary">
            操作顺序：添加账号（图标选平台） → 内容发布（选择账号） → 发布记录（查看进度/失败重试）。
          </Typography.Text>
        </Space>
      </Card>
      {(
      <Card
        title="步骤 1：发布到自媒体"
        style={{ marginBottom: 16 }}
        extra={<Typography.Text type="secondary">请先添加账号并安装桌面端</Typography.Text>}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ contentMode: 'MARKDOWN' }}
          onValuesChange={(changed, all) => {
            if (Object.prototype.hasOwnProperty.call(changed, 'accountIds')) {
              const ids = Array.isArray(all.accountIds) ? all.accountIds : [];
              setPublishAccountIds(ids);
              setScheduleRule(calcScheduleRule(ids.length ? ids : defaultAccountIdsForUi));
            }
          }}
        >
          {selectedWebSessionForPublish.length > 0 && mediaAssistantCap?.delegateEnabled !== false ? (
            <Alert
              type="info"
              showIcon
              style={{ marginBottom: 12 }}
              message="桌面委托发布"
              description="已选账号为「网页同步」渠道：助手将按各平台流程为您同步发稿，进度与结果见「发布记录」。发布进行中请保持助手运行。"
            />
          ) : null}
          <Space wrap style={{ marginBottom: 16 }}>
            <Button type="primary" onClick={runPublishNowOnPage}>
              立即发布
            </Button>
            <Button onClick={enterScheduledPublishOnPage}>
              定时发布
            </Button>
          </Space>
          <Row gutter={16}>
            <Col xs={24} lg={14}>
              <Card size="small" title="内容参数" style={{ marginBottom: 12 }}>
                <Row gutter={12}>
                  <Col xs={24} md={12}>
                    <Form.Item
                      label="内容"
                      required
                      extra="由作品编辑页「发布中心」跳转时自动关联；请勿手动修改内容 ID，以免错发。"
                    >
                      {sourcePubSummary.loading ? (
                        <Spin size="small" />
                      ) : sourcePubSummary.title ? (
                        <Space direction="vertical" size={2} style={{ width: '100%' }}>
                          <Typography.Text strong style={{ fontSize: 15 }}>{sourcePubSummary.title}</Typography.Text>
                          <Typography.Text type="secondary" copyable={{ text: String(sourcePubSummary.id) }}>
                            内容 ID：{sourcePubSummary.id}
                          </Typography.Text>
                        </Space>
                      ) : sourcePubSummary.error && sourcePubSummary.id ? (
                        <Typography.Text type="danger">{sourcePubSummary.error}</Typography.Text>
                      ) : (
                        <Typography.Text type="secondary">
                          未关联作品。请从作品编辑页使用「发布中心」打开本页，将自动带出要发布的内容与标题。
                        </Typography.Text>
                      )}
                    </Form.Item>
                    <Form.Item
                      name="sourcePubId"
                      rules={[{ required: true, message: '请从作品页进入以关联发布内容' }]}
                      style={{ display: 'none' }}
                    >
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col xs={24} md={12}>
                    <Form.Item label="发布格式">
                      <Input
                        value={autoContentMode === 'HTML' ? 'HTML（由内容实际格式自动确定）' : 'Markdown（由内容实际格式自动确定）'}
                        disabled
                      />
                    </Form.Item>
                  </Col>
                  <Col xs={24}>
                    <Form.Item
                      name="scheduledTime"
                      label="定时发布"
                      extra={publishLaunchMode === 'scheduled'
                        ? (() => {
                          const minHour = Math.ceil(scheduleRule.minLeadMinutes / 60);
                          const maxDay = Math.floor(scheduleRule.maxLeadMinutes / (24 * 60));
                          const platforms = scheduleRule.platformCodes.length ? scheduleRule.platformCodes.join(' / ') : '默认账号策略';
                          return `已按本次账号（${platforms}）计算定时交集：${minHour} 小时 ~ ${maxDay} 天。定时指令由客户端提交到平台官方机制。`;
                        })()
                        : '点击上方「定时发布」按钮后再选择时间'}
                    >
                      <DatePicker
                        showTime
                        disabled={publishLaunchMode !== 'scheduled'}
                        {...(publishLaunchMode === 'scheduled'
                          ? { open: schedulePanelOpen, onOpenChange: (o) => setSchedulePanelOpen(o) }
                          : {})}
                        placeholder={publishLaunchMode === 'scheduled' ? '请选择定时发布时间' : '请先点击「定时发布」'}
                        style={{ width: '100%' }}
                        format="YYYY-MM-DD HH:mm:ss"
                        disabledDate={disabledScheduleDate}
                      />
                    </Form.Item>
                  </Col>
                </Row>
              </Card>
            </Col>
            <Col xs={24} lg={10}>
              <Card
                size="small"
                title="账号选择"
                extra={<Tag color={authorizedCount > 0 ? 'green' : 'orange'}>{`可用 ${authorizedCount}/${assistantAccounts.length}`}</Tag>}
                style={{ marginBottom: 12 }}
              >
                <Form.Item name="accountIds" rules={[{ required: true, message: '至少选择一个账号' }]} style={{ marginBottom: 8 }}>
                  <Checkbox.Group
                    options={assistantAccounts.map((a) => ({
                      label: (
                        <span>
                          {bindChannelTag(a.bindChannel)}
                          <Tag style={{ marginLeft: 4 }}>{a.platform}</Tag>
                          {a.accountName}
                          {isAssistantAccountReady(a) ? '' : '（不可用）'}
                        </span>
                      ),
                      value: a.id,
                    }))}
                  />
                </Form.Item>
                <Space>
                  <Button
                    size="small"
                    onClick={() => {
                      form.setFieldsValue({ accountIds: defaultAccountIdsForUi });
                      message.success(defaultAccountIdsForUi.length ? '已应用默认发布账号' : '暂未配置默认发布账号');
                    }}
                  >
                    使用默认账号
                  </Button>
                  <Button size="small" onClick={() => { setActiveTab('accounts'); updateTabInUrl('accounts'); }}>
                    去添加账号
                  </Button>
                </Space>
              </Card>
            </Col>
          </Row>
          <Space>
            <Button type="primary" loading={submitting} onClick={() => handleCreate()}>
              {publishLaunchMode === 'scheduled' ? '确认定时发布' : '确认发布'}
            </Button>
            <Typography.Text type="secondary">进度请到「发布记录」查看</Typography.Text>
          </Space>
        </Form>
      </Card>
      )}

      <>
        {mediaAssistantCap && mediaAssistantCap.bindModeWebSession === false ? (
          <Alert
            type="warning"
            showIcon
            style={{ marginBottom: 16 }}
            message="暂无法添加账号"
            description={(
              <span>
                请管理员在服务端开启网页同步发布（
                <Typography.Text code>wldos.socialpublish.media-assistant.web-session-enabled=true</Typography.Text>
                ）。
              </span>
            )}
          />
        ) : null}
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col xs={24} md={7} lg={6}>
            <Card
              title="步骤 2：添加账号"
              size="small"
              extra={(
                <Space size="small">
                  <Tooltip
                    title={
                      mediaAssistantCap && !mediaAssistantCap.bindModeWebSession
                        ? '网页同步发布未开启，请联系管理员'
                        : undefined
                    }
                  >
                    <Button
                      type="primary"
                      size="small"
                      onClick={openAddAccountModal}
                      disabled={mediaAssistantCap !== null && !mediaAssistantCap.bindModeWebSession}
                    >
                      添加账号
                    </Button>
                  </Tooltip>
                  <Button
                    type="link"
                    size="small"
                    onClick={() => loadAccounts().then(() => message.success('已刷新')).catch(() => message.error('刷新失败'))}
                  >
                    刷新
                  </Button>
                </Space>
              )}
            >
              {!assistantAccounts.length ? (
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无账号">
                  <Button
                    type="primary"
                    onClick={openAddAccountModal}
                    disabled={mediaAssistantCap !== null && !mediaAssistantCap.bindModeWebSession}
                  >
                    添加账号
                  </Button>
                </Empty>
              ) : (
                <Menu
                  mode="inline"
                  selectedKeys={selectedAccountId ? [String(selectedAccountId)] : []}
                  onClick={({ key }) => setSelectedAccountId(key)}
                >
                  {assistantAccounts.map((a) => (
                    <Menu.Item key={String(a.id)}>
                      {bindChannelTag(a.bindChannel)}
                      <Tag style={{ marginRight: 6, marginLeft: 6 }}>{a.platform}</Tag>
                      <Typography.Text>{a.accountName}</Typography.Text>
                      <Tag
                        color={isAssistantAccountReady(a) ? 'green' : 'orange'}
                        style={{ marginLeft: 8 }}
                      >
                        {isAssistantAccountReady(a) ? '可用' : '不可用'}
                      </Tag>
                    </Menu.Item>
                  ))}
                </Menu>
              )}
            </Card>
          </Col>
          <Col xs={24} md={17} lg={18}>
            {selectedAccount ? (
              <Card title="当前账号" size="small" style={{ minHeight: 180 }}>
                <Space direction="vertical" style={{ width: '100%' }} size="small">
                  <div>
                    <Space wrap>
                      <Typography.Text>列表显示名：{selectedAccount.accountName}</Typography.Text>
                      {bindChannelTag(selectedAccount.bindChannel)}
                    </Space>
                  </div>
                  {selectedAccount.platformHomeUrl ? (
                    <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                      平台入口：
                      <a href={selectedAccount.platformHomeUrl} target="_blank" rel="noopener noreferrer">
                        {selectedAccount.platformHomeUrl}
                      </a>
                    </Typography.Text>
                  ) : null}
                  <Typography.Text type="secondary">
                    添加账号后，请在助手中打开对应平台官网完成登录。发布进行中请勿退出助手，以免中断。进度与结果可在发布记录查看。
                  </Typography.Text>
                  <Space wrap>
                    <Button type="primary" onClick={() => goPublishWithAccount(selectedAccount)}>去发布</Button>
                    {desktopEmbedded ? (
                      <>
                        <Button onClick={openSelectedAccountInDesktop}>壳内打开平台</Button>
                        <Button onClick={() => showDesktopExternal(selectedExternalChannel)}>切换到平台会话</Button>
                      </>
                    ) : null}
                    <Button danger onClick={() => removeAssistantAccount(selectedAccount)}>删除账号</Button>
                  </Space>
                </Space>
              </Card>
            ) : (
              <Card title="当前账号" size="small" style={{ minHeight: 180 }}>
                <Empty
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                  description={assistantAccounts.length ? '请从左侧选择账号查看详情' : '请先添加账号'}
                />
              </Card>
            )}
          </Col>
        </Row>
        <Modal
          title="添加账号"
          open={webSessionModalOpen}
          onCancel={() => { setWebSessionModalOpen(false); webSessionForm.resetFields(); }}
          onOk={handleWebSessionBind}
          confirmLoading={webSessionSubmitting}
          okText="确定"
          destroyOnClose
          width={480}
        >
          <Typography.Paragraph type="secondary" style={{ marginBottom: 12 }}>
            身份以当前站点登录为准。各平台网页内登录在桌面端完成。
          </Typography.Paragraph>
          <Form form={webSessionForm} layout="vertical">
            <Form.Item name="platform" label="平台" rules={[{ required: true, message: '请选择平台' }]}>
              <Radio.Group style={{ width: '100%' }}>
                <Row gutter={[8, 8]}>
                  {(assistantPlatforms || []).map((p) => {
                    const active = (p.status || '').toUpperCase() === 'ACTIVE';
                    return (
                      <Col span={12} key={p.code}>
                        <Radio.Button
                          value={p.code}
                          disabled={!active}
                          style={{ width: '100%', textAlign: 'center' }}
                        >
                          {active ? (p.name || p.code) : `${p.name || p.code}（即将支持）`}
                        </Radio.Button>
                      </Col>
                    );
                  })}
                </Row>
              </Radio.Group>
            </Form.Item>
            <Form.Item name="remark" label="列表显示名" rules={[{ required: true, message: '请填写列表显示名' }]}>
              <Input placeholder="例如：公众号主号" />
            </Form.Item>
            <Form.Item
              name="platformHomeUrl"
              label="平台后台地址"
              extra="桌面端打开此地址进入该平台后台（不填则按平台默认，如微信公众号 mp.weixin.qq.com）"
            >
              <Input placeholder="https://mp.weixin.qq.com" allowClear />
            </Form.Item>
            <Typography.Text type="secondary" style={{ fontSize: 12 }}>
              说明：此处只登记账号条目；平台实际登录在桌面端助手中完成。
            </Typography.Text>
          </Form>
        </Modal>
      </>

      {(
      <>
      <Row gutter={12} style={{ marginBottom: 12 }}>
        {statCards.map((it) => (
          <Col xs={12} md={6} key={it.title}>
            <Card size="small" bodyStyle={{ padding: '10px 12px' }}>
              <Space direction="vertical" size={0}>
                <Typography.Text type="secondary" style={{ fontSize: 12 }}>{it.title}</Typography.Text>
                <Typography.Text strong style={{ fontSize: 20, color: it.color }}>{it.value}</Typography.Text>
              </Space>
            </Card>
          </Col>
        ))}
      </Row>
      {hasContextFilter ? (
        <Card size="small" style={{ marginBottom: 12 }}>
          <Space>
            <Tag color="blue">当前已按来源作品筛选</Tag>
            <Button size="small" onClick={clearContextFilter}>清除筛选</Button>
          </Space>
        </Card>
      ) : null}
      <Card size="small" style={{ marginBottom: 12 }}>
        <Space>
          <span>账号状态快速筛选：</span>
          <Radio.Group
            size="small"
            value={recordFilter}
            onChange={(e) => {
              setRecordFilter(e.target.value);
              actionRef.current?.reloadAndRest?.();
              actionRef.current?.reload?.();
            }}
          >
            <Radio.Button value="ALL">全部</Radio.Button>
            <Radio.Button value="REAUTH">需授权</Radio.Button>
          </Radio.Group>
        </Space>
      </Card>
      {!recordRows.length ? (
        <Card style={{ marginBottom: 12 }}>
          <Empty description={recordFilter === 'REAUTH' ? '暂无需要重新授权的发布' : '暂无发布记录'}>
            <Space>
              <Button type="primary" onClick={() => { setActiveTab('publish'); updateTabInUrl('publish'); }}>
                去发布
              </Button>
              <Button onClick={() => { setActiveTab('accounts'); updateTabInUrl('accounts'); }}>
                去添加账号
              </Button>
            </Space>
          </Empty>
        </Card>
      ) : null}
      <ProTableX
        actionRef={actionRef}
        rowKey="id"
        headerTitle="我的发布记录"
        search={{ labelWidth: 100 }}
        expandable={{
          expandedRowRender: (record) => (
            <div style={{ padding: '8px 0' }}>
              {(record.tasks || []).length ? (
                (record.tasks || []).map((task) => {
                  const taskKey = task.id != null ? String(task.id) : '';
                  const isDesktopPending =
                    task.executionMode === 'DESKTOP_DELEGATE' && task.status === 'PENDING_DELEGATE';
                  return (
                    <div key={task.id} style={{ marginBottom: 12 }}>
                      <Space style={{ display: 'flex', marginBottom: isDesktopPending ? 6 : 0 }} wrap align="center">
                        <Tag>{task.platform}</Tag>
                        {task.executionMode === 'DESKTOP_DELEGATE' ? <Tag color="purple">桌面端</Tag> : null}
                        <span>状态：{task.status}</span>
                        {task.retryCount != null ? <span>重试：{task.retryCount}</span> : null}
                        {task.errorCode ? <Tag color="orange">{task.errorCode}</Tag> : null}
                        {task.errorMsg ? <span style={{ color: '#999' }}>{task.errorMsg}</span> : null}
                        {task.platformUrl ? (
                          <a href={task.platformUrl} target="_blank" rel="noopener noreferrer">
                            查看平台链接
                          </a>
                        ) : null}
                      </Space>
                      {isDesktopPending ? (
                        <Space wrap align="center" style={{ paddingLeft: 0 }}>
                          <Input
                            size="small"
                            placeholder="平台文章链接（可选）"
                            style={{ width: 240 }}
                            value={delegateUrlByTaskId[taskKey] || ''}
                            onChange={(e) =>
                              setDelegateUrlByTaskId((prev) => ({
                                ...prev,
                                [taskKey]: e.target.value,
                              }))
                            }
                          />
                          <Button size="small" type="primary" onClick={() => doDelegateComplete(task.id, true)}>
                            已发布
                          </Button>
                          <Button size="small" danger onClick={() => doDelegateComplete(task.id, false)}>
                            未发布
                          </Button>
                        </Space>
                      ) : null}
                    </div>
                  );
                })
              ) : (
                <span style={{ color: '#999' }}>暂无子任务明细</span>
              )}
            </div>
          ),
        }}
        request={async (params) => {
          const condition = {
            ...jobCondition,
            ...(recordFilter === 'REAUTH' ? { requireReauth: true } : {}),
          };
          const res = await queryMyPublishJobs({
            current: params.current,
            pageSize: params.pageSize,
            condition,
          });
          const data = res?.data;
          setRecordRows(data?.rows || []);
          return {
            data: data?.rows || [],
            total: data?.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={[
          { title: '记录ID', dataIndex: 'id', hideInSearch: true, width: 90 },
          {
            title: '内容',
            dataIndex: 'sourcePubTitle',
            hideInSearch: true,
            width: 220,
            ellipsis: true,
            render: (_, record) => (
              <Space direction="vertical" size={0}>
                <Typography.Text strong ellipsis={{ tooltip: record.sourcePubTitle }}>
                  {record.sourcePubTitle || '—'}
                </Typography.Text>
                <Typography.Text type="secondary" copyable={record.sourcePubId ? { text: String(record.sourcePubId) } : false} style={{ fontSize: 12 }}>
                  ID {record.sourcePubId ?? '—'}
                </Typography.Text>
              </Space>
            ),
          },
          { title: '模式', dataIndex: 'contentMode', hideInSearch: true, width: 96 },
          {
            title: '执行方式',
            dataIndex: 'scheduledTime',
            hideInSearch: true,
            width: 128,
            render: (_, record) => {
              if (!record.scheduledTime) {
                return <Tag>立即</Tag>;
              }
              const raw = record.scheduledTime;
              const text = typeof raw === 'string' ? raw.replace('T', ' ').slice(0, 19) : String(raw);
              return (
                <Space direction="vertical" size={0}>
                  <Tag color="blue">定时</Tag>
                  <Typography.Text type="secondary" style={{ fontSize: 12 }}>{text}</Typography.Text>
                </Space>
              );
            },
          },
          { title: '状态', dataIndex: 'status', hideInSearch: true },
          { title: '提交时间', dataIndex: 'createTime', hideInSearch: true },
          {
            title: '操作',
            dataIndex: 'option',
            hideInSearch: true,
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => doPublishNow(record.id)} disabled={record.status === 'RUNNING' || record.status === 'SUCCESS'}>
                  立即发布
                </Button>
                <Button size="small" onClick={() => doRetryFailed(record.id)} disabled={!['FAILED', 'PARTIAL_OK'].includes(record.status)}>
                  失败重试
                </Button>
              </Space>
            ),
          },
        ]}
      />
      </>
      )}
    </PageContainer>
  );
}
