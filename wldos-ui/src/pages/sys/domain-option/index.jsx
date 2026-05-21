import {
  PlusOutlined,
  QuestionCircleOutlined,
  EditOutlined,
  KeyOutlined,
  CloudOutlined,
  FileTextOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { Alert, Button, Card, Divider, Form, Input, message, Popconfirm, Select, Spin, Space } from 'antd';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FooterToolbar, PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import FullscreenModal from '@/components/FullscreenModal';
import { fetchEnumWoOptionAppCode, fetchEnumWoOptionType } from '@/services/enum';
import { queryPage as queryDomainPage } from '@/pages/sys/domain/service';
import { addEntity, fetchGlobalOptionByKey, queryPage, removeEntities, removeEntity, updateEntity } from './service';

const PORTAL_TABS_KEY = 'portal_account_center_tabs';

const DOMAIN_OPTION_FORM_LAYOUT = { labelCol: { span: 6 }, wrapperCol: { span: 16 } };

/** 表单分组与标题样式对齐应用管理等页；文案为中文直出，后续可抽 locale。 */

const DomainOptionAdmin = () => {
  const actionRef = useRef();
  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selectedRows, setSelectedRows] = useState([]);
  const [domainOptions, setDomainOptions] = useState([]);
  const [optionTypeSelectOptions, setOptionTypeSelectOptions] = useState([]);
  const [optionTypeModalOptions, setOptionTypeModalOptions] = useState([]);
  const [appCodeSelectOptions, setAppCodeSelectOptions] = useState([]);
  const [appCodeModalOptions, setAppCodeModalOptions] = useState([]);
  const [globalBaseline, setGlobalBaseline] = useState(null);
  const [globalBaselineLoading, setGlobalBaselineLoading] = useState(false);
  const mobile = isMobile();
  useDesktopSticky(actionRef);

  const loadDomains = useCallback(async () => {
    try {
      const res = await queryDomainPage({ current: 1, pageSize: 500 });
      const rows = res?.data?.rows ?? [];
      setDomainOptions(
        rows.map((d) => ({
          label: `${d.siteName || d.id} (${d.siteDomain || '-'})`,
          value: d.id,
        })),
      );
    } catch (e) {
      message.error('加载站点列表失败');
    }
  }, []);

  useEffect(() => {
    loadDomains();
  }, [loadDomains]);

  useEffect(async () => {
    try {
      const tRes = await fetchEnumWoOptionType();
      if (tRes?.data) {
        setOptionTypeSelectOptions(tRes.data.map((i) => ({ label: i.label, value: i.value })));
      }
      const aRes = await fetchEnumWoOptionAppCode();
      if (aRes?.data) {
        setAppCodeSelectOptions(aRes.data.map((i) => ({ label: i.label, value: i.value })));
      }
    } catch (error) {
      message.error('加载枚举失败');
    }
  }, []);

  const loadGlobalForKey = useCallback(async (rawKey) => {
    const key = (rawKey || '').trim();
    if (!key) {
      setGlobalBaseline(null);
      return;
    }
    setGlobalBaselineLoading(true);
    try {
      const row = await fetchGlobalOptionByKey(key);
      setGlobalBaseline(row);
    } catch {
      setGlobalBaseline(null);
    } finally {
      setGlobalBaselineLoading(false);
    }
  }, []);

  const domainLabelMap = useMemo(() => {
    const m = {};
    domainOptions.forEach((o) => {
      m[o.value] = o.label;
    });
    return m;
  }, [domainOptions]);

  const domainValueEnum = useMemo(() => {
    const e = {};
    domainOptions.forEach((o) => {
      e[o.value] = { text: o.label };
    });
    return e;
  }, [domainOptions]);

  const labelForOptionType = useCallback(
    (v) => {
      if (v == null || v === '') return '—';
      const hit = optionTypeSelectOptions.find((o) => o.value === v);
      return hit?.label ?? v;
    },
    [optionTypeSelectOptions],
  );

  const labelForAppCode = useCallback(
    (v) => {
      if (v == null || v === '') return '（未指定）';
      const hit = appCodeSelectOptions.find((o) => o.value === v);
      return hit?.label ?? v;
    },
    [appCodeSelectOptions],
  );

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    setAppCodeModalOptions(appCodeSelectOptions);
    setOptionTypeModalOptions(optionTypeSelectOptions);
    form.setFieldsValue({
      optionType: 'auto_reload',
      appCode: 'sys_option',
      optionKey: PORTAL_TABS_KEY,
    });
    setModalVisible(true);
    loadGlobalForKey(PORTAL_TABS_KEY);
  };

  const openEdit = (record) => {
    setEditing(record);
    const c = record.appCode;
    let modalAppOpts = appCodeSelectOptions;
    if (c != null && c !== '' && !appCodeSelectOptions.some((o) => o.value === c)) {
      modalAppOpts = [{ label: `${c}（当前值）`, value: c }, ...appCodeSelectOptions];
    }
    setAppCodeModalOptions(modalAppOpts);
    const ot = record.optionType;
    let modalTypeOpts = optionTypeSelectOptions;
    if (ot != null && ot !== '' && !optionTypeSelectOptions.some((o) => o.value === ot)) {
      modalTypeOpts = [{ label: `${ot}（当前值）`, value: ot }, ...optionTypeSelectOptions];
    }
    setOptionTypeModalOptions(modalTypeOpts);
    form.setFieldsValue({
      ...record,
      domainId: record.domainId,
      appCode: record.appCode == null || record.appCode === '' ? '' : record.appCode,
    });
    setModalVisible(true);
    loadGlobalForKey(record.optionKey);
  };

  const validateOptionValueJson = (key, value) => {
    const v = (value || '').trim();
    if (!v) return true;
    if (key === PORTAL_TABS_KEY || v.startsWith('[') || v.startsWith('{')) {
      try {
        JSON.parse(v);
        return true;
      } catch {
        message.error('填写内容须为合法格式（个人中心页签等为列表/对象，可按示例书写）');
        return false;
      }
    }
    return true;
  };

  const submitModal = async () => {
    try {
      const values = await form.validateFields();
      if (!validateOptionValueJson(values.optionKey, values.optionValue)) return;
      const payload = { ...values };
      if (payload.appCode === '' || payload.appCode === undefined) {
        payload.appCode = null;
      }
      const hide = message.loading(editing ? '正在保存' : '正在新增');
      try {
        if (editing) {
          await updateEntity({ ...editing, ...payload });
        } else {
          await addEntity(payload);
        }
        hide();
        message.success(editing ? '保存成功' : '新增成功');
        setModalVisible(false);
        setGlobalBaseline(null);
        actionRef.current?.reload?.();
      } catch (err) {
        hide();
        message.error(editing ? '保存失败' : '新增失败');
      }
    } catch {
      // 表单校验失败
    }
  };

  const closeOptionModal = () => {
    setModalVisible(false);
    setGlobalBaseline(null);
  };

  const renderOptionModalFooter = () => (
    <Space>
      <Button onClick={closeOptionModal}>取消</Button>
      <Button type="primary" onClick={submitModal}>
        {editing ? '保存' : '确定'}
      </Button>
    </Space>
  );

  const handleDeleteOne = async (record) => {
    const hide = message.loading('正在删除');
    try {
      await removeEntity({ id: record.id });
      hide();
      message.success('删除成功');
      actionRef.current?.reload?.();
    } catch {
      hide();
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (!selectedRows?.length) return;
    const hide = message.loading('正在删除');
    try {
      await removeEntities({ ids: selectedRows.map((r) => r.id) });
      hide();
      message.success('删除成功');
      setSelectedRows([]);
      actionRef.current?.reload?.();
    } catch {
      hide();
      message.error('删除失败');
    }
  };

  const columns = [
    {
      title: '站点',
      dataIndex: 'domainId',
      valueType: 'select',
      valueEnum: domainValueEnum,
      width: 220,
      render: (_, row) => domainLabelMap[row.domainId] || row.domainId,
    },
    {
      title: '配置标识',
      dataIndex: 'optionKey',
      ellipsis: true,
      width: 200,
    },
    {
      title: '名称',
      dataIndex: 'optionName',
      ellipsis: true,
      hideInSearch: true,
      width: 140,
    },
    {
      title: '参数值',
      dataIndex: 'optionValue',
      ellipsis: true,
      hideInSearch: true,
      width: 280,
    },
    {
      title: '说明',
      dataIndex: 'description',
      ellipsis: true,
      hideInSearch: true,
      width: 160,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 160,
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          <a onClick={() => openEdit(record)}>编辑</a>
          <Divider type="vertical" />
          <Popconfirm
            title="确定删除该条站点配置？"
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={() => handleDeleteOne(record)}
          >
            <a>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <PageContainer
      style={{ padding: 0, margin: 0 }}
      bodyStyle={{ padding: '24px', margin: 0 }}
    >
      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
        message="站点配置与全平台默认的关系"
        description={
          <>
            <div style={{ marginBottom: 8 }}>
              <strong>分工：</strong>「全局系统选项」里维护的是<strong>全平台通用默认值</strong>；本页用于为<strong>某个站点</strong>单独填写覆盖值。系统使用时，会<strong>优先采用本站点上的设置</strong>；若本站点未配置，则自动回落到全平台默认。
            </div>
            <div>
              <strong>仅本站生效：</strong>即便全平台暂时没有同名项，您仍可在本站点先行保存，只对该站生效。若希望先定全平台统一基线，请先到「全局系统选项」新增同名配置，再回到此处按需覆盖。
            </div>
          </>
        }
      />
      <ProTableX
        headerTitle="域级系统参数"
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        pagination={{
          defaultPageSize: 15,
          pageSizeOptions: ['10', '15', '20', '30', '50', '100'],
          showSizeChanger: true,
        }}
        toolBarRender={() => [
          <Button type="primary" key="add" onClick={openCreate}>
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params) => {
          const res = await queryPage(params);
          return {
            data: res?.data?.rows ?? [],
            total: res?.data?.total ?? 0,
            success: true,
          };
        }}
        columns={columns}
        rowSelection={{
          onChange: (_, rows) => setSelectedRows(rows),
        }}
      />
      {selectedRows?.length > 0 && (
        <FooterToolbar>
          <Popconfirm title={`确定批量删除 ${selectedRows.length} 条？`} onConfirm={handleBatchDelete}>
            <Button danger>批量删除</Button>
          </Popconfirm>
        </FooterToolbar>
      )}
      <FullscreenModal
        width={880}
        bodyStyle={{ padding: '24px' }}
        destroyOnClose
        visible={modalVisible}
        onCancel={closeOptionModal}
        footer={renderOptionModalFooter()}
        title={
          <Space>
            {editing ? <EditOutlined style={{ color: '#1890ff' }} /> : <PlusOutlined style={{ color: '#52c41a' }} />}
            {editing ? '编辑站点系统参数' : '新建站点系统参数'}
          </Space>
        }
      >
        <Form
          form={form}
          preserve={false}
          layout="horizontal"
          {...DOMAIN_OPTION_FORM_LAYOUT}
          onValuesChange={(changed, all) => {
            if (Object.prototype.hasOwnProperty.call(changed, 'optionKey')) {
              loadGlobalForKey(all.optionKey);
            }
          }}
        >
          <Card
            size="small"
            style={{ marginBottom: 16 }}
            title={
              <Space>
                <KeyOutlined style={{ color: '#1890ff' }} />
                站点与配置标识
              </Space>
            }
          >
            <Form.Item
              name="domainId"
              label="站点"
              rules={[{ required: true, message: '请选择站点' }]}
            >
              <Select
                showSearch
                optionFilterProp="label"
                disabled={!!editing}
                placeholder="选择要覆盖配置的站点"
                options={domainOptions}
              />
            </Form.Item>
            <Form.Item
              name="optionKey"
              label="配置标识"
              extra="须与「全局系统选项」中为同一配置项保留的标识完全一致。"
              rules={[{ required: true, message: '请填写配置标识' }]}
            >
              <Input disabled={!!editing} placeholder={`示例：${PORTAL_TABS_KEY}`} />
            </Form.Item>
          </Card>
          <Card
            size="small"
            style={{ marginBottom: 16 }}
            title={
              <Space>
                <CloudOutlined style={{ color: '#1890ff' }} />
                全平台默认对照
              </Space>
            }
          >
            {globalBaselineLoading ? (
              <div style={{ textAlign: 'center', padding: '24px 0' }}>
                <Spin tip="正在加载全平台默认…" />
              </div>
            ) : globalBaseline ? (
              <Alert
                type="info"
                showIcon
                message="全平台默认（只读对照）"
                description={
                  <div>
                    <div style={{ marginBottom: 8, color: 'rgba(0,0,0,0.65)' }}>
                      显示名称：{globalBaseline.optionName || '—'}；生效方式：{labelForOptionType(globalBaseline.optionType)}
                      ；所属应用：{labelForAppCode(globalBaseline.appCode)}
                    </div>
                    <div style={{ fontSize: 12, marginBottom: 4, color: 'rgba(0,0,0,0.45)' }}>
                      全平台默认内容
                    </div>
                    <Input.TextArea
                      readOnly
                      rows={6}
                      value={globalBaseline.optionValue || ''}
                      style={{ fontFamily: 'monospace', fontSize: 12 }}
                    />
                  </div>
                }
              />
            ) : (
              <Alert
                type="warning"
                showIcon
                message="全平台尚无可对照的同名配置"
                description="保存后仅在本站点生效。若希望先定统一基线，请先到「全局系统选项」新增同名配置，再回到此处填写本站点覆盖值。"
              />
            )}
          </Card>
          <Card
            size="small"
            style={{ marginBottom: 16 }}
            title={
              <Space>
                <FileTextOutlined style={{ color: '#1890ff' }} />
                内容与说明
              </Space>
            }
          >
            <Form.Item name="optionName" label="显示名称" tooltip="可选">
              <Input placeholder="如：门户个人中心页签" />
            </Form.Item>
            <Form.Item
              name="optionValue"
              label="参数值"
              rules={[{ required: true, message: '请填写参数值' }]}
            >
              <Input.TextArea rows={10} placeholder='个人中心页签示例：[{"type":"info","title":"资料"}]' />
            </Form.Item>
            <Form.Item name="description" label="说明" tooltip="可选">
              <Input.TextArea rows={2} />
            </Form.Item>
          </Card>
          <Card
            size="small"
            title={
              <Space>
                <SettingOutlined style={{ color: '#1890ff' }} />
                生效与归属
              </Space>
            }
          >
            <Form.Item
              name="optionType"
              label="生效方式"
              rules={[{ required: true, message: '请选择生效方式' }]}
            >
              <Select
                placeholder="请选择"
                showSearch
                style={{ width: '100%' }}
                getPopupContainer={() => document.body}
                filterOption={(input, option) =>
                  String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                }
              >
                {optionTypeModalOptions.map((o) => (
                  <Select.Option key={o.value === '' ? '_domain_option_type_empty' : o.value} value={o.value}>
                    {o.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="appCode" label="所属应用">
              <Select
                allowClear
                placeholder="请选择所属应用（可留空）"
                showSearch
                style={{ width: '100%' }}
                getPopupContainer={() => document.body}
                filterOption={(input, option) =>
                  String(option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                }
              >
                {appCodeModalOptions.map((o) => (
                  <Select.Option key={o.value === '' ? '_domain_app_code_empty' : o.value} value={o.value}>
                    {o.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Card>
        </Form>
      </FullscreenModal>
    </PageContainer>
  );
};

export default DomainOptionAdmin;
