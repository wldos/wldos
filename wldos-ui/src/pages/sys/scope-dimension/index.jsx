import { PlusOutlined, InfoCircleOutlined, AppstoreOutlined } from '@ant-design/icons';

import { Button, message, Tag, Tooltip, Space, Popconfirm } from 'antd';

import React, { useCallback, useEffect, useRef, useState } from 'react';

import { PageContainer } from '@ant-design/pro-layout';

import ProTableX from '@/components/ProTableX';

import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';

import FullscreenModal from '@/components/FullscreenModal';

import { Form, Input, InputNumber, Select } from 'antd';

import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';

import {

  addEntity,

  batchRegister,

  fetchDictSources,

  queryPage,

  removeEntity,

  updateEntity,

} from './service';



const BUILTIN_DIMENSION_CODES = ['com_id', 'domain_id', 'org_id', 'prov_code', 'city_code', 'county_code', 'biz_code'];

const isDictRegisteredDimension = (record) => {
  const vs = record?.valueSource || '';
  return vs.startsWith('DICT:') || vs.startsWith('TERM_CLASS:');
};

const canDeleteDimension = (record) =>
  isDictRegisteredDimension(record) && !BUILTIN_DIMENSION_CODES.includes(record.dimensionCode);

const SCOPE_TIER_OPTIONS = [
  { label: '全局字段（全员可见）', value: 'global' },
  { label: '定制字段（角色按需选用）', value: 'extension' },
];

const SCOPE_TIER_ENUM = {
  global: { text: '全局' },
  extension: { text: '定制' },
};

const formLayout = { labelCol: { span: 6 }, wrapperCol: { span: 16 } };



const BUILTIN_SOURCES = [

  { label: '组织树 ORG_TREE', value: 'ORG_TREE' },

  { label: '行政区划 · 省 TABLE:wo_region:1', value: 'TABLE:wo_region:1' },

  { label: '行政区划 · 市 TABLE:wo_region:2', value: 'TABLE:wo_region:2' },

  { label: '行政区划 · 区县 TABLE:wo_region:3', value: 'TABLE:wo_region:3' },

];



const parseValueSource = (valueSource) => {

  const source = valueSource || '';

  if (source.startsWith('DICT:')) {

    const code = source.substring('DICT:'.length);

    return { sourceKind: 'DICT', dictCode: code };

  }

  if (source.startsWith('TERM_CLASS:')) {

    const code = source.substring('TERM_CLASS:'.length);

    return { sourceKind: 'DICT', dictCode: code };

  }

  const builtin = BUILTIN_SOURCES.find((item) => item.value === source);

  if (builtin) {

    return { sourceKind: 'BUILTIN', builtinSource: source };

  }

  return { sourceKind: 'BUILTIN', builtinSource: 'ORG_TREE' };

};



const buildValueSource = (values) => {

  if (values.sourceKind === 'DICT') {

    const raw = values.dictCode;

    const code = Array.isArray(raw) ? raw[0] : raw;

    return `DICT:${String(code || '').trim()}`;

  }

  return values.builtinSource;

};



const ScopeDimensionAdmin = () => {

  const actionRef = useRef();

  const [form] = Form.useForm();

  const [batchForm] = Form.useForm();

  const [visible, setVisible] = useState(false);

  const [batchVisible, setBatchVisible] = useState(false);

  const [editing, setEditing] = useState(null);

  const [dictSources, setDictSources] = useState([]);

  const sourceKind = Form.useWatch('sourceKind', form);

  useDesktopSticky(actionRef);



  const loadDictSources = useCallback(() => {

    fetchDictSources().then((res) => {

      if (res?.code === 200 && Array.isArray(res.data)) {

        setDictSources(res.data);

      }

    });

  }, []);



  useEffect(() => {

    loadDictSources();

  }, [loadDictSources]);



  const dictOptions = dictSources.map((item) => ({

    label: item.registered

      ? `${item.name || item.dictCode} (${item.dictCode}) · 已登记`

      : `${item.name || item.dictCode} (${item.dictCode})`,

    value: item.dictCode,

    disabled: item.registered === true,

  }));



  const batchDictOptions = dictSources

    .filter((item) => !item.registered)

    .map((item) => ({

      label: `${item.name || item.dictCode} (${item.dictCode})`,

      value: item.dictCode,

    }));



  const openModal = (record) => {

    setEditing(record || null);

    form.resetFields();

    if (record) {

      form.setFieldsValue({

        ...record,

        ...parseValueSource(record.valueSource),

      });

    } else {

      form.setFieldsValue({

        sourceKind: 'DICT',

        matchMode: 'IN',

        enabled: '1',

        scopeUiTier: 'global',

        sortOrder: 10,

      });

    }

    setVisible(true);

  };



  const openBatchModal = () => {

    batchForm.resetFields();

    batchForm.setFieldsValue({

      codeSuffix: '_code',

      enabled: '1',

      scopeUiTier: 'global',

    });

    setBatchVisible(true);

  };



  const handleSubmit = async () => {

    const values = await form.validateFields();

    const valueSource = buildValueSource(values);

    const { sourceKind: _sk, builtinSource: _bs, dictCode: _dc, ...rest } = values;

    const payload = editing

      ? { ...editing, ...rest, valueSource }

      : { ...rest, valueSource };

    const fn = editing ? updateEntity : addEntity;

    const res = await fn(payload);

    if (res?.code !== 200) {

      return;

    }

    message.success(editing ? '已更新' : '已创建');

    setVisible(false);

    loadDictSources();

    actionRef.current?.reload();

  };



  const handleBatchSubmit = async () => {

    const values = await batchForm.validateFields();

    const res = await batchRegister({

      dictCodes: values.dictCodes,

      codeSuffix: values.codeSuffix,

      enabled: values.enabled,

      scopeUiTier: values.scopeUiTier,

    });

    if (res?.code !== 200) {

      return;

    }

    const result = res.data || {};

    const created = result.createdCount || 0;

    const skipped = result.skippedCount || 0;

    if (created > 0) {

      message.success(`已登记 ${created} 种字典类型${skipped > 0 ? `，跳过 ${skipped} 项` : ''}`);

    } else if (skipped > 0) {

      message.warning(`未新建，${skipped} 项已跳过`);

    } else {

      message.info('未登记任何业务字段');

    }

    setBatchVisible(false);

    loadDictSources();

    actionRef.current?.reload();

  };



  const handleDelete = async (record) => {

    const res = await removeEntity({ id: record.id });

    if (res?.code !== 200) {

      return;

    }

    message.success('已删除');

    loadDictSources();

    actionRef.current?.reload();

  };



  const columns = [

    {

      title: '编码',

      dataIndex: 'dimensionCode',

      width: 140,

      hideInSearch: false,

    },

    {

      title: '名称',

      dataIndex: 'dimensionName',

      width: 120,

      hideInSearch: false,

    },

    {

      title: '值来源',

      dataIndex: 'valueSource',

      ellipsis: true,

      hideInSearch: true,

    },

    {

      title: '匹配模式',

      dataIndex: 'matchMode',

      width: 100,

      hideInSearch: true,

    },

    {

      title: '字段范围',

      dataIndex: 'scopeUiTier',

      width: 100,

      hideInSearch: true,

      valueEnum: SCOPE_TIER_ENUM,

      render: (_, record) => {

        const tier = record.scopeUiTier || 'extension';

        return tier === 'global' ? <Tag color="blue">全局</Tag> : <Tag>定制</Tag>;

      },

    },

    {

      title: '启用',

      dataIndex: 'enabled',

      width: 90,

      valueType: 'select',

      valueEnum: {

        '1': { text: '已启用' },

        '0': { text: '未启用' },

      },

      render: (_, record) => (

        record.enabled === '1' ? <Tag color="green">已启用</Tag> : <Tag>未启用</Tag>

      ),

    },

    {

      title: '排序',

      dataIndex: 'sortOrder',

      width: 80,

      sorter: true,

      hideInSearch: true,

    },

    {

      title: '操作',

      width: 120,

      hideInSearch: true,

      render: (_, record) => (

        <Space>

          <a onClick={() => openModal(record)}>编辑</a>

          {canDeleteDimension(record) ? (

            <Popconfirm

              title="确定删除？若角色已配置或已绑定实体列，请先在对应页面清理"

              onConfirm={() => handleDelete(record)}

            >

              <a>删除</a>

            </Popconfirm>

          ) : null}

        </Space>

      ),

    },

  ];



  return (

    <PageContainer {...adminListPageContainerProps}>

      <ProTableX

        headerTitle={(

          <Space>

            业务字段

            <Tooltip title="每种枚举类型字典登记为一行；可同时登记多种，角色管理范围按维独立配置">

              <InfoCircleOutlined style={{ color: '#999', fontSize: 14 }} />

            </Tooltip>

          </Space>

        )}

        actionRef={actionRef}

        rowKey="id"

        search={{ labelWidth: 100 }}

        toolBarRender={() => [

          <Button key="batch" icon={<AppstoreOutlined />} onClick={openBatchModal}>

            批量登记

          </Button>,

          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => openModal(null)}>

            登记业务字段

          </Button>,

        ]}

        request={async (params, sorter) => {

          const res = await queryPage({

            current: params.current,

            pageSize: params.pageSize,

            dimensionCode: params.dimensionCode,

            dimensionName: params.dimensionName,

            enabled: params.enabled,

            sorter: Object.keys(sorter || {}).length

              ? sorter

              : { sortOrder: 'ascend', id: 'ascend' },

          });

          return {

            data: res?.data?.rows || [],

            total: res?.data?.total || 0,

            success: res?.code === 200,

          };

        }}

        columns={columns}

      />

      <FullscreenModal

        title={editing ? '编辑业务字段' : '登记业务字段'}

        visible={visible}

        onCancel={() => setVisible(false)}

        onOk={handleSubmit}

        width={720}

        bodyStyle={{ padding: 24 }}

      >

        <Form form={form} {...formLayout}>

          <Form.Item name="dimensionCode" label="编码" rules={[{ required: true }]}>

            <Input disabled={!!editing} placeholder="brand_code" />

          </Form.Item>

          <Form.Item name="dimensionName" label="名称" rules={[{ required: true }]}>

            <Input placeholder="品牌" />

          </Form.Item>

          <Form.Item name="sourceKind" label="选项类型" rules={[{ required: true }]}>

            <Select

              options={[

                { label: '字典 / Term 分类（DICT）', value: 'DICT' },

                { label: '内置（组织 / 行政区划）', value: 'BUILTIN' },

              ]}

            />

          </Form.Item>

          {sourceKind === 'DICT' ? (

            <Form.Item

              name="dictCode"

              label="字典源"

              rules={[{ required: true, message: '请选择字典类型' }]}

              extra="一种字典类型对应一行业务字段；已登记类型不可重复选择"

            >

              <Select

                showSearch

                optionFilterProp="label"

                options={dictOptions}

                placeholder="选择枚举类型字典"

              />

            </Form.Item>

          ) : (

            <Form.Item name="builtinSource" label="内置来源" rules={[{ required: true }]}>

              <Select options={BUILTIN_SOURCES} />

            </Form.Item>

          )}

          <Form.Item name="matchMode" label="匹配模式" initialValue="IN">

            <Select

              options={[

                { label: 'EQ', value: 'EQ' },

                { label: 'IN', value: 'IN' },

                { label: 'SUBTREE', value: 'SUBTREE' },

                { label: 'PREFIX', value: 'PREFIX' },

              ]}

            />

          </Form.Item>

          <Form.Item name="enabled" label="启用" initialValue="1">

            <Select

              options={[

                { label: '已启用（参与角色配置）', value: '1' },

                { label: '未启用', value: '0' },

              ]}

            />

          </Form.Item>

          {sourceKind === 'DICT' ? (

            <Form.Item

              name="scopeUiTier"

              label="字段范围"

              initialValue="global"

              extra="全局：所有角色管理范围可见；定制：角色按需选用后配置"

            >

              <Select options={SCOPE_TIER_OPTIONS} />

            </Form.Item>

          ) : null}

          <Form.Item

            name="sortOrder"

            label="排序"

            initialValue={10}

            extra="控制角色「管理范围」展示顺序；建议从 10 起、步长 10（0 也会排在最前）"

          >

            <InputNumber min={0} style={{ width: '100%' }} />

          </Form.Item>

        </Form>

      </FullscreenModal>

      <FullscreenModal

        title="批量登记业务字段"

        visible={batchVisible}

        onCancel={() => setBatchVisible(false)}

        onOk={handleBatchSubmit}

        width={720}

        bodyStyle={{ padding: 24 }}

      >

        <Form form={batchForm} {...formLayout}>

          <Form.Item

            name="dictCodes"

            label="字典类型"

            rules={[{ required: true, message: '请至少选择一种字典类型' }]}

            extra="每种类型生成一行业务字段（dimension_code = 类型编码 + 后缀）"

          >

            <Select

              mode="multiple"

              showSearch

              optionFilterProp="label"

              options={batchDictOptions}

              placeholder="多选枚举类型，如 brand、product_line"

            />

          </Form.Item>

          <Form.Item name="codeSuffix" label="编码后缀" initialValue="_code">

            <Input placeholder="_code" />

          </Form.Item>

          <Form.Item name="enabled" label="启用" initialValue="1">

            <Select

              options={[

                { label: '已启用', value: '1' },

                { label: '未启用', value: '0' },

              ]}

            />

          </Form.Item>

          <Form.Item name="scopeUiTier" label="字段范围" initialValue="global">

            <Select options={SCOPE_TIER_OPTIONS} />

          </Form.Item>

        </Form>

      </FullscreenModal>

    </PageContainer>

  );

};



export default ScopeDimensionAdmin;

