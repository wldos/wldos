import React, { useEffect, useRef, useState } from 'react';
import { connect } from 'umi';
import { Button, message, Popconfirm, Select, Space, Tabs, Tag } from 'antd';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import AddUserList from '@/pages/sys/user/components/add';
import { queryPage as queryDomainPage } from '@/pages/sys/domain/service';
import { addIdentityMember, queryIdentityRegistry, removeIdentityMember } from '../service';

const KINDS = [
  { key: 'admin', label: '超级管理员' },
  { key: 'tadmin', label: '租户管理员' },
  { key: 'dadmin', label: '站点管理员' },
  { key: 'can_trust', label: '可信会员' },
];

/**
 * 身份登记册：平台内置身份（超管/租户管/站点管/可信会员）清单，仅超管。
 * tadmin 按目标租户下拉筛选（默认可查全部）；dadmin 按租户→站点级联；admin/can_trust 平台级平铺。
 * 添加/移除走登记册专用 API（服务端双写身份组成员投影）。
 */
const IdentityRegistryPanel = ({ comList = [], currentUser }) => {
  const [kind, setKind] = useState('admin');
  const [addVisible, setAddVisible] = useState(false);
  const [tenantId, setTenantId] = useState(undefined);
  const [filterComId, setFilterComId] = useState(undefined);
  const [filterTadminComId, setFilterTadminComId] = useState(undefined);
  const [filterDomainId, setFilterDomainId] = useState(undefined);
  const [domainOptions, setDomainOptions] = useState([]);
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const isTenant = kind === 'tadmin';
  const isDomain = kind === 'dadmin';
  const currentUserId = currentUser?.id != null ? String(currentUser.id) : undefined;

  // dadmin：先选租户再拉该租户下站点（与站点管理页 query.comId 一致）
  useEffect(() => {
    if (!isDomain) {
      return;
    }
    if (filterComId == null || filterComId === '') {
      setDomainOptions([]);
      return;
    }
    const params = {
      current: 1,
      pageSize: 500,
      comId: filterComId,
      sorter: { displayOrder: 'ascend' },
    };
    queryDomainPage(params).then((res) => {
      const rows = res?.data?.rows || res?.rows || [];
      const scoped = rows.filter((d) => String(d.comId) === String(filterComId));
      setDomainOptions(
        scoped.map((d) => ({
          label: d.siteName || d.siteDomain || d.id,
          value: d.id,
        })),
      );
    });
  }, [isDomain, filterComId]);

  const handleTabChange = (k) => {
    setKind(k);
    setTenantId(undefined);
    setFilterComId(undefined);
    setFilterTadminComId(undefined);
    setFilterDomainId(undefined);
    actionRef.current?.reload?.();
  };

  const handleAdd = async (val = {}) => {
    if (!val.ids?.length) {
      message.info('请选择要添加的成员！');
      return false;
    }
    if (isTenant && !tenantId) {
      message.warning('请选择受托租户（该用户将成为其租户管理员）');
      return false;
    }
    const res = await addIdentityMember({
      kind,
      userIds: val.ids,
      userComId: isTenant ? tenantId : undefined,
    });
    if (res?.code !== 200) {
      if (res?.code === 422) {
        message.error(res.message || '校验失败');
      }
      return false;
    }
    if (res?.data) {
      message.warn(res.data);
    } else {
      message.success('添加成功');
    }
    actionRef.current?.reload?.();
    return true;
  };

  const handleRemove = async (record) => {
    const res = await removeIdentityMember({ kind, userIds: [String(record.userId)] });
    if (res?.code !== 200) {
      return;
    }
    message.success('已移除');
    actionRef.current?.reload?.();
  };

  const columns = [
    { title: '用户ID', dataIndex: 'userId', hideInSearch: true },
    { title: '登录名', dataIndex: 'loginName', hideInSearch: true },
    { title: '昵称', dataIndex: 'nickname', hideInSearch: true, render: (v) => v || '-' },
    ...(isTenant
      ? [{ title: '受托租户', dataIndex: 'comName', hideInSearch: true, render: (_, r) => r.comName || r.comId || '-' }]
      : []),
    ...(isDomain
      ? [{ title: '绑定站点', dataIndex: 'domainName', hideInSearch: true, render: (_, r) => r.domainName || r.domainId || '-' }]
      : []),
    { title: '登记时间', dataIndex: 'createTime', hideInSearch: true, valueType: 'dateTime' },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        // dadmin 只读：维护入口在站点管理
        if (isDomain) {
          return <Tag>站点管理中维护</Tag>;
        }
        // 禁止移除当前登录管理员自身
        if (currentUserId && String(record.userId) === currentUserId) {
          return <Tag>当前登录</Tag>;
        }
        return (
          <Popconfirm
            title="确定移除该身份？（同步移除身份组成员）"
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={() => handleRemove(record)}
          >
            <a>移除</a>
          </Popconfirm>
        );
      },
    },
  ];

  // 工具栏：tadmin 加租户检索；dadmin 加租户→站点级联
  const renderToolbar = () => [
    isTenant && (
      <Select
        key="filterTadminCom"
        allowClear
        showSearch
        optionFilterProp="label"
        placeholder="目标租户（空=全部）"
        style={{ minWidth: 220 }}
        options={comList}
        value={filterTadminComId}
        onChange={(v) => {
          setFilterTadminComId(v);
          actionRef.current?.reload?.();
        }}
      />
    ),
    isDomain && (
      <Select
        key="filterCom"
        allowClear
        showSearch
        optionFilterProp="label"
        placeholder="按租户筛选"
        style={{ minWidth: 200 }}
        options={comList}
        value={filterComId}
        onChange={(v) => {
          setFilterComId(v);
          setFilterDomainId(undefined);
          actionRef.current?.reload?.();
        }}
      />
    ),
    isDomain && (
      <Select
        key="filterDomain"
        allowClear
        showSearch
        optionFilterProp="label"
        placeholder={filterComId ? '选择目标站点' : '请先选择租户'}
        disabled={!filterComId}
        style={{ minWidth: 200 }}
        options={domainOptions}
        value={filterDomainId}
        onChange={(v) => {
          setFilterDomainId(v);
          actionRef.current?.reload?.();
        }}
      />
    ),
    !isDomain && (
      <Button key="add" type="primary" onClick={() => setAddVisible(true)}>
        <PlusOutlined /> 添加
      </Button>
    ),
  ];

  return (
    <>
      <Tabs
        activeKey={kind}
        onChange={handleTabChange}
        items={KINDS.map((k) => ({ key: k.key, label: k.label }))}
      />
      <ProTableX
        headerTitle={false}
        actionRef={actionRef}
        rowKey={(r) => `${r.id}-${r.userId}`}
        search={false}
        toolBarRender={renderToolbar}
        request={async () => {
          const res = await queryIdentityRegistry({
            kind,
            comId: isTenant ? filterTadminComId : isDomain ? filterComId : undefined,
            domainId: isDomain ? filterDomainId : undefined,
          });
          const rows = res?.data || [];
          return { data: rows, total: rows.length, success: res?.code === 200 };
        }}
        columns={columns}
        pagination={false}
        options={false}
      />
      <AddUserList
        key={`${kind}-${tenantId || ''}`}
        title={`添加${KINDS.find((k) => k.key === kind)?.label || ''}`}
        modalVisible={addVisible}
        onCancel={() => setAddVisible(false)}
        onSubmit={handleAdd}
        values={isTenant && tenantId ? { comId: tenantId } : {}}
        extraContent={
          isTenant ? (
            <div style={{ marginBottom: 16, minWidth: 360 }}>
              <Space size={8}>
                <span>受托租户</span>
                <Select
                  style={{ minWidth: 240 }}
                  placeholder="请选择受托租户（该用户将成为其租户管理员）"
                  options={comList}
                  value={tenantId}
                  onChange={setTenantId}
                  showSearch
                  optionFilterProp="label"
                />
              </Space>
            </div>
          ) : null
        }
      />
    </>
  );
};

export default connect(({ user }) => ({
  currentUser: user.currentUser,
}))(IdentityRegistryPanel);
