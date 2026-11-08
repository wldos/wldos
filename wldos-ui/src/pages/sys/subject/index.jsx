import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Button, Form, message, Modal, Popconfirm, Select, Space, Tabs, Tag, TreeSelect } from 'antd';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { connect, useIntl } from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import AddUserList from '@/pages/sys/user/components/add';
import { queryPage as queryRolePage } from '@/pages/sys/role/service';
import { mapRoleTreeToTreeSelect } from '@/utils/tree';
import useOperationalScope from '@/hooks/useOperationalScope';
import { isValidScopeComId } from '@/utils/orgCreateUserDomain';
import {
  addSubjectAuth,
  addSubjectGrant,
  querySubjectAuths,
  querySubjectGrants,
  querySubjectTypes,
  removeSubjectAuth,
  removeSubjectGrant,
} from './service';

/**
 * 主体管理：主体认证（用户↔主体类型）与主体授权（主体类型↔角色）。
 */
const SubjectPage = ({ isAdmin, currentUser }) => {
  const intl = useIntl();

  const {
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    domainOptions,
    showDomainSelectInToolbar,
    listScopeReady,
  } = useOperationalScope({ isAdmin, currentUser, intl, adminDefaultAxis: 'tenant-required' });

  const [tab, setTab] = useState('auth');
  const authRef = useRef();
  const grantRef = useRef();
  useDesktopSticky(authRef);
  useDesktopSticky(grantRef);

  const [typeOptions, setTypeOptions] = useState([]);
  const [grantRoleTreeData, setGrantRoleTreeData] = useState([]);
  const [grantRoleLoading, setGrantRoleLoading] = useState(false);

  const [authAddVisible, setAuthAddVisible] = useState(false);
  const [authTypeId, setAuthTypeId] = useState(undefined);
  const [grantAddVisible, setGrantAddVisible] = useState(false);
  const [grantForm] = Form.useForm();
  const grantFormDomainId = Form.useWatch('domainId', grantForm);

  const handleDomainChange = (value) => {
    setTargetDomainId(value || undefined);
    authRef.current?.reload?.();
    grantRef.current?.reload?.();
  };

  const buildListParams = useCallback(() => {
    const params = {};
    if (isAdmin && isValidScopeComId(effectiveComId)) {
      params.comId = effectiveComId;
    }
    if (targetDomainId) {
      params.domainId = targetDomainId;
    }
    return params;
  }, [isAdmin, effectiveComId, targetDomainId]);

  useEffect(() => {
    querySubjectTypes().then((res) => {
      const rows = res?.data || [];
      setTypeOptions(rows.map((t) => ({ label: t.subjectTypeName, value: t.id })));
    });
  }, []);

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    authRef.current?.reload?.();
    grantRef.current?.reload?.();
  }, [listScopeReady, targetDomainId, effectiveComId]);

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    authRef.current?.reload?.();
    grantRef.current?.reload?.();
  }, [tab]);

  useEffect(() => {
    if (!grantAddVisible) {
      return undefined;
    }
    let cancelled = false;
    setGrantRoleLoading(true);
    const params = { current: 1, pageSize: 1000 };
    if (isAdmin && isValidScopeComId(effectiveComId)) {
      params.comId = effectiveComId;
    }
    if (grantFormDomainId) {
      params.domainId = grantFormDomainId;
    }
    queryRolePage(params).then((res) => {
      if (cancelled) {
        return;
      }
      const rows = res?.data?.rows || [];
      setGrantRoleTreeData(mapRoleTreeToTreeSelect(rows));
      setGrantRoleLoading(false);
    }).catch(() => {
      if (!cancelled) {
        setGrantRoleTreeData([]);
        setGrantRoleLoading(false);
      }
    });
    return () => {
      cancelled = true;
    };
  }, [grantAddVisible, grantFormDomainId, isAdmin, effectiveComId]);

  const handleGrantFormDomainChange = () => {
    grantForm.setFieldsValue({ roleIds: [] });
  };

  const handleAuthAdd = async (val = {}) => {
    if (!authTypeId) {
      message.warning('请选择主体类型');
      return false;
    }
    if (!val.ids?.length) {
      message.info('请选择要添加的用户！');
      return false;
    }
    const payloadBase = {
      subjectTypeId: authTypeId,
    };
    if (isAdmin && effectiveComId) {
      payloadBase.comId = effectiveComId;
    }
    if (targetDomainId) {
      payloadBase.domainId = targetDomainId;
    }
    let ok = true;
    for (const userId of val.ids) {
      const res = await addSubjectAuth({ ...payloadBase, userId });
      if (res?.code !== 200) {
        if (res?.code === 422) {
          message.error(res.message || '校验失败');
        }
        ok = false;
        break;
      }
    }
    if (!ok) {
      return false;
    }
    message.success('添加成功');
    setAuthAddVisible(false);
    setAuthTypeId(undefined);
    authRef.current?.reload?.();
    return true;
  };

  const handleAuthRemove = async (record) => {
    const res = await removeSubjectAuth({ id: record.id });
    if (res?.code !== 200) {
      return;
    }
    message.success('已移除');
    authRef.current?.reload?.();
  };

  const handleGrantAdd = async () => {
    const values = await grantForm.validateFields();
    const payload = { ...values };
    if (isAdmin && effectiveComId) {
      payload.comId = effectiveComId;
    }
    const res = await addSubjectGrant(payload);
    if (res?.code !== 200) {
      if (res?.code === 422) {
        message.error(res.message || '校验失败');
      }
      return;
    }
    message.success('添加成功');
    setGrantAddVisible(false);
    grantForm.resetFields();
    grantRef.current?.reload?.();
  };

  const handleGrantRemove = async (record) => {
    const res = await removeSubjectGrant({ id: record.id });
    if (res?.code !== 200) {
      return;
    }
    message.success('已移除');
    grantRef.current?.reload?.();
  };

  const openGrantModal = () => {
    grantForm.resetFields();
    const initial = {};
    if (targetDomainId) {
      initial.domainId = targetDomainId;
    }
    grantForm.setFieldsValue(initial);
    setGrantAddVisible(true);
  };

  const scopeToolbar = () => [
    isAdmin && comListArray.length > 0 ? (
      <Space key="com-select" size={8}>
        <span>目标租户</span>
        <Select
          style={{ minWidth: 180 }}
          allowClear
          placeholder="选择租户"
          options={comListArray}
          value={effectiveComId}
          onChange={onTargetComChange}
          showSearch
          optionFilterProp="label"
        />
      </Space>
    ) : null,
    showDomainSelectInToolbar ? (
      <Space key="domain-select" size={8}>
        <span>{intl.formatMessage({ id: 'sys.role.domain.targetSite', defaultMessage: '目标站点' })}</span>
        <Select
          style={{ minWidth: 200 }}
          placeholder="选择站点"
          options={domainOptions}
          value={targetDomainId}
          onChange={handleDomainChange}
          showSearch
          optionFilterProp="label"
        />
      </Space>
    ) : null,
  ];

  const authColumns = useMemo(() => [
    { title: '主体类型', dataIndex: 'subjectTypeName', hideInSearch: true, render: (v) => v || '-' },
    { title: '用户ID', dataIndex: 'userId', hideInSearch: true },
    { title: '登录名', dataIndex: 'loginName', hideInSearch: true },
    { title: '昵称', dataIndex: 'nickname', hideInSearch: true, render: (v) => v || '-' },
    {
      title: '生效范围',
      dataIndex: 'domainName',
      hideInSearch: true,
      hideInTable: !showDomainSelectInToolbar,
      render: (v, r) => (r.domainId ? v || r.domainId : <Tag>租户级</Tag>),
    },
    { title: '创建时间', dataIndex: 'createTime', hideInSearch: true, valueType: 'dateTime' },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Popconfirm
          title="确定移除该主体认证？"
          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
          onConfirm={() => handleAuthRemove(record)}
        >
          <a>移除</a>
        </Popconfirm>
      ),
    },
  ], [showDomainSelectInToolbar]);

  const grantColumns = useMemo(() => [
    { title: '主体类型', dataIndex: 'subjectTypeName', hideInSearch: true, render: (v) => v || '-' },
    { title: '角色', dataIndex: 'roleName', hideInSearch: true, render: (v) => v || '-' },
    {
      title: '生效范围',
      dataIndex: 'domainName',
      hideInSearch: true,
      hideInTable: !showDomainSelectInToolbar,
      render: (v, r) => (r.domainId ? v || r.domainId : <Tag>租户级</Tag>),
    },
    { title: '创建时间', dataIndex: 'createTime', hideInSearch: true, valueType: 'dateTime' },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Popconfirm
          title="确定移除该主体授权？"
          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
          onConfirm={() => handleGrantRemove(record)}
        >
          <a>移除</a>
        </Popconfirm>
      ),
    },
  ], [showDomainSelectInToolbar]);

  const authPickerComId = effectiveComId && String(effectiveComId) !== '0' ? effectiveComId : '';

  const tenantLevelGrantAllowed = showDomainSelectInToolbar;

  return (
    <PageContainer {...adminListPageContainerProps}>
      <Tabs
        activeKey={tab}
        onChange={(key) => {
          setTab(key);
          authRef.current?.reload?.();
          grantRef.current?.reload?.();
        }}
        items={[
          { key: 'auth', label: '主体认证' },
          { key: 'grant', label: '主体授权' },
        ]}
      />
      {tab === 'auth' ? (
        <ProTableX
          headerTitle="主体认证清单"
          actionRef={authRef}
          manualRequest={!listScopeReady}
          rowKey="id"
          search={false}
          toolBarRender={() => [
            ...scopeToolbar(),
            <Button key="add" type="primary" onClick={() => { setAuthTypeId(undefined); setAuthAddVisible(true); }}>
              <PlusOutlined /> 添加认证
            </Button>,
          ]}
          request={async () => {
            const res = await querySubjectAuths(buildListParams());
            const rows = res?.data || [];
            return { data: rows, total: rows.length, success: res?.code === 200 };
          }}
          columns={authColumns}
          pagination={{
            defaultPageSize: 15,
            showSizeChanger: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条 / 总共 ${total} 条`,
          }}
        />
      ) : (
        <ProTableX
          headerTitle="主体授权清单"
          actionRef={grantRef}
          manualRequest={!listScopeReady}
          rowKey="id"
          search={false}
          toolBarRender={() => [
            ...scopeToolbar(),
            <Button key="add" type="primary" onClick={openGrantModal}>
              <PlusOutlined /> 添加授权
            </Button>,
          ]}
          request={async () => {
            const res = await querySubjectGrants(buildListParams());
            const rows = res?.data || [];
            return { data: rows, total: rows.length, success: res?.code === 200 };
          }}
          columns={grantColumns}
          pagination={{
            defaultPageSize: 15,
            showSizeChanger: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条 / 总共 ${total} 条`,
          }}
        />
      )}
      <AddUserList
        title="添加主体认证"
        submitText="添加认证"
        modalVisible={authAddVisible}
        onCancel={() => setAuthAddVisible(false)}
        onSubmit={handleAuthAdd}
        values={{ comId: authPickerComId }}
        extraContent={
          <div style={{ marginBottom: 16, minWidth: 360 }}>
            <Select
              style={{ minWidth: 240 }}
              placeholder="请选择主体类型"
              options={typeOptions}
              value={authTypeId}
              onChange={setAuthTypeId}
              showSearch
              optionFilterProp="label"
            />
          </div>
        }
      />
      <Modal
        title="添加主体授权"
        visible={grantAddVisible}
        onOk={handleGrantAdd}
        onCancel={() => setGrantAddVisible(false)}
        destroyOnClose
      >
        <Form form={grantForm} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item
            name="subjectTypeId"
            label="主体类型"
            rules={[{ required: true, message: '请选择主体类型' }]}
          >
            <Select placeholder="请选择主体类型" options={typeOptions} showSearch optionFilterProp="label" />
          </Form.Item>
          <Form.Item
            name="domainId"
            label="生效站点"
            tooltip={
              tenantLevelGrantAllowed
                ? '不选为租户级授权（本租户全部站点生效）；选定站点后角色下拉仅显示该站点可用角色'
                : '当前视图已锁定站点；站点管理员须指定生效站点'
            }
            rules={
              tenantLevelGrantAllowed
                ? []
                : [{ required: true, message: '请选择生效站点' }]
            }
          >
            <Select
              allowClear={tenantLevelGrantAllowed}
              disabled={!tenantLevelGrantAllowed && !!targetDomainId}
              placeholder={tenantLevelGrantAllowed ? '租户级（全部站点）' : '请选择生效站点'}
              options={domainOptions}
              onChange={handleGrantFormDomainChange}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item name="roleIds" label="角色" rules={[{ required: true, message: '请选择角色' }]}>
            <TreeSelect
              multiple
              treeData={grantRoleTreeData}
              loading={grantRoleLoading}
              placeholder="请选择角色（可多选，限当前租户或站点）"
              showSearch
              treeNodeFilterProp="title"
              treeDefaultExpandAll
              treeLine
              dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
              style={{ width: '100%' }}
            />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default connect(({ user }) => {
  const auth = buildAuthorityHelper(user, '/admin');
  return {
    ...auth,
    currentUser: user.currentUser,
  };
})(SubjectPage);
