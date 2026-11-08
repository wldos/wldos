import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Button, Card, Form, message, Modal, Popconfirm, Select, Space, Tag, TreeSelect } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { connect, useIntl } from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import OrgTreeSelect from '@/components/TreeSelects/OrgTreeSelect';
import { queryPage as queryRolePage } from '@/pages/sys/role/service';
import useOperationalScope from '@/hooks/useOperationalScope';
import usePlatformOperatorMode from '@/hooks/usePlatformOperatorMode';
import { isValidScopeComId } from '@/utils/orgCreateUserDomain';
import { mapRoleTreeToTreeSelect } from '@/utils/tree';
import { bindSlot, querySlotBindings, querySlotCatalog, unbindSlot } from './service';

const TARGET_TYPE_TEXT = { org: '分群', role: '角色', role_set: '角色集' };

/**
 * 插槽绑定：业务流程消费的语义槽位 → 租户/站点自建分群或角色；未绑定按默认实现解析。
 * 列表按目标租户/目标站点级联查询，与主体管理同一管理范围口径。
 */
const SlotBindingPage = ({ isAdmin, currentUser }) => {
  const intl = useIntl();
  const { lock: platformOperatorLock } = usePlatformOperatorMode();

  const {
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    domainOptions,
    showDomainSelectInToolbar,
    listScopeReady,
  } = useOperationalScope({
    isAdmin,
    currentUser,
    intl,
    adminDefaultAxis: 'tenant-required',
    platformOperatorLock,
  });

  const catalogRef = useRef();
  const bindingRef = useRef();
  useDesktopSticky(catalogRef);
  const [bindVisible, setBindVisible] = useState(false);
  const [currentSlot, setCurrentSlot] = useState(null);
  const [roleTreeData, setRoleTreeData] = useState([]);
  const [roleTreeLoading, setRoleTreeLoading] = useState(false);
  const [form] = Form.useForm();
  const bindDomainId = Form.useWatch('domainId', form);

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    catalogRef.current?.reload?.();
    bindingRef.current?.reload?.();
  }, [listScopeReady, targetDomainId, effectiveComId]);

  const buildScopeParams = useCallback(() => {
    const params = {};
    if (isAdmin && isValidScopeComId(effectiveComId)) {
      params.comId = effectiveComId;
    }
    if (targetDomainId) {
      params.domainId = targetDomainId;
    }
    return params;
  }, [isAdmin, effectiveComId, targetDomainId]);

  // 生效站点变更后按当前租户/站点重新加载可绑定角色，并清空已选目标
  useEffect(() => {
    if (!bindVisible) {
      return undefined;
    }
    let cancelled = false;
    setRoleTreeLoading(true);
    const params = { current: 1, pageSize: 1000 };
    if (isAdmin && isValidScopeComId(effectiveComId)) {
      params.comId = effectiveComId;
    }
    if (bindDomainId) {
      params.domainId = bindDomainId;
    }
    queryRolePage(params).then((res) => {
      if (cancelled) {
        return;
      }
      setRoleTreeData(mapRoleTreeToTreeSelect(res?.data?.rows || []));
      setRoleTreeLoading(false);
    }).catch(() => {
      if (!cancelled) {
        setRoleTreeData([]);
        setRoleTreeLoading(false);
      }
    });
    return () => {
      cancelled = true;
    };
  }, [bindVisible, bindDomainId, isAdmin, effectiveComId]);

  const openBind = (slot) => {
    setCurrentSlot(slot);
    form.resetFields();
    if (targetDomainId) {
      form.setFieldsValue({ domainId: targetDomainId });
    }
    setBindVisible(true);
  };

  const handleBind = async () => {
    const values = await form.validateFields();
    const res = await bindSlot({
      slotCode: currentSlot.slotCode,
      comId: isAdmin && isValidScopeComId(effectiveComId) ? effectiveComId : undefined,
      domainId: values.domainId,
      targetId: values.targetId,
    });
    if (res?.code !== 200) {
      // 422 校验错误全局通知不弹，由调用方展示
      if (res?.code === 422) {
        message.error(res.message || '校验失败');
      }
      return;
    }
    message.success('绑定成功');
    setBindVisible(false);
    catalogRef.current?.reload?.();
    bindingRef.current?.reload?.();
  };

  const handleUnbind = async (record) => {
    const res = await unbindSlot({ id: record.id });
    if (res?.code !== 200) {
      return;
    }
    message.success('已解绑，恢复默认实现');
    catalogRef.current?.reload?.();
    bindingRef.current?.reload?.();
  };

  const catalogColumns = [
    { title: '插槽编码', dataIndex: 'slotCode', hideInSearch: true },
    { title: '说明', dataIndex: 'description', hideInSearch: true },
    {
      title: '目标类型',
      dataIndex: 'targetType',
      hideInSearch: true,
      render: (v) => TARGET_TYPE_TEXT[v] || v,
    },
    { title: '默认实现', dataIndex: 'defaultKind', hideInSearch: true, render: (v) => <Tag>{v}</Tag> },
    {
      title: '状态',
      dataIndex: 'bound',
      hideInSearch: true,
      render: (v) => (v ? <Tag color="blue">已绑定</Tag> : <Tag>默认实现</Tag>),
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) =>
        record.targetType === 'role_set' ? (
          <Tag>角色集暂不支持绑定</Tag>
        ) : (
          <a onClick={() => openBind(record)}>{record.bound ? '改绑' : '绑定'}</a>
        ),
    },
  ];

  const bindingColumns = [
    { title: '插槽编码', dataIndex: 'slotCode', hideInSearch: true },
    { title: '插槽说明', dataIndex: 'slotDescription', hideInSearch: true },
    {
      title: '目标类型',
      dataIndex: 'targetType',
      hideInSearch: true,
      render: (v) => TARGET_TYPE_TEXT[v] || v,
    },
    { title: '绑定目标', dataIndex: 'targetName', hideInSearch: true, render: (v) => v || '-' },
    {
      title: '生效范围',
      dataIndex: 'domainName',
      hideInSearch: true,
      hideInTable: !showDomainSelectInToolbar,
      render: (v, r) => (r.domainId ? v || r.domainId : <Tag>租户级</Tag>),
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Popconfirm
          title="确定解绑？解绑后按默认实现解析"
          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
          onConfirm={() => handleUnbind(record)}
        >
          <a>解绑</a>
        </Popconfirm>
      ),
    },
  ];

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
        <span>目标站点</span>
        <Select
          style={{ minWidth: 200 }}
          placeholder="选择站点"
          options={domainOptions}
          value={targetDomainId}
          onChange={(value) => setTargetDomainId(value || undefined)}
          showSearch
          optionFilterProp="label"
        />
      </Space>
    ) : null,
  ];

  const hasScopeSelector = (isAdmin && comListArray.length > 0) || showDomainSelectInToolbar;

  return (
    <PageContainer {...adminListPageContainerProps}>
      {hasScopeSelector ? (
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
          <Space size={16}>{scopeToolbar()}</Space>
        </div>
      ) : null}
      <ProTableX
        headerTitle="插槽注册表"
        actionRef={catalogRef}
        rowKey="slotCode"
        search={false}
        options={false}
        pagination={false}
        request={async () => {
          const res = await querySlotCatalog(buildScopeParams());
          const rows = res?.data || [];
          return { data: rows, total: rows.length, success: res?.code === 200 };
        }}
        columns={catalogColumns}
      />
      <Card size="small" style={{ marginTop: 16 }} bodyStyle={{ padding: 0 }}>
        <ProTableX
          headerTitle="绑定清单"
          actionRef={bindingRef}
          rowKey="id"
          search={false}
          options={false}
          pagination={false}
          request={async () => {
            const res = await querySlotBindings(buildScopeParams());
            const rows = res?.data || [];
            return { data: rows, total: rows.length, success: res?.code === 200 };
          }}
          columns={bindingColumns}
        />
      </Card>
      <Modal
        title={`${currentSlot?.bound ? '改绑' : '绑定'}插槽：${currentSlot?.description || ''}`}
        visible={bindVisible}
        onOk={handleBind}
        onCancel={() => setBindVisible(false)}
        destroyOnClose
      >
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item label="插槽编码">
            <Tag>{currentSlot?.slotCode}</Tag>
          </Form.Item>
          <Form.Item name="domainId" label="生效站点" tooltip="不选表示租户级绑定（本租户下全部站点）；选定站点后仅显示该站点下的分群/角色">
            <Select
              allowClear
              placeholder="租户级（全部站点）"
              options={domainOptions}
              onChange={() => form.setFieldsValue({ targetId: undefined })}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item
            name="targetId"
            label={currentSlot?.targetType === 'org' ? '目标分群' : '目标角色'}
            tooltip={
              currentSlot?.slotCode === 'guest.role'
                ? '改绑仅切换游客角色实例，不会迁移 wo_auth_role；目标角色须已有门户 menu 授权且挂载到生效站点'
                : undefined
            }
            rules={[{ required: true, message: '请选择绑定目标' }]}
          >
            {currentSlot?.targetType === 'org' ? (
              <OrgTreeSelect domainId={bindDomainId} category="segment" placeholder="请选择目标分群" style={{ width: '100%' }} />
            ) : (
              <TreeSelect
                placeholder="请选择目标角色"
                treeData={roleTreeData}
                loading={roleTreeLoading}
                showSearch
                treeNodeFilterProp="title"
                treeDefaultExpandAll
                treeLine
                allowClear
                dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                style={{ width: '100%' }}
              />
            )}
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
})(SlotBindingPage);
