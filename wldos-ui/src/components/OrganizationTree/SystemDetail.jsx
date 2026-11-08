/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, message, Popconfirm } from 'antd';
import { useIntl, connect } from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { resolveTenantScopeComId } from '@/utils/orgCreateUserDomain';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import ArchUpdateForm from '@/pages/sys/arch/components/UpdateForm';
import ArchCreateForm from '@/pages/sys/arch/components/CreateForm';
import ArchCreateFormContent from '@/pages/sys/arch/components/CreateFormContent';
import OrgCreateForm from '@/pages/sys/org/components/CreateForm';
import OrgCreateFormContent from '@/pages/sys/org/components/CreateFormContent';

const SystemDetail = ({
  system,
  companyName,
  parentSystemName,
  onSystemUpdate,
  onSystemAdd,
  onOrganizationAdd,
  onSystemDelete,
  comOptions = [],
  orgTypeOptions = [],
  archOptionsForOrg = [],
  orgOptionsForParent = [],
  isTenantAdmin,
  currentUser,
}) => {
  const intl = useIntl();
  const tenantFixedComId = isTenantAdmin ? resolveTenantScopeComId(currentUser) : undefined;
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [orgModalVisible, setOrgModalVisible] = useState(false);
  const [archCreateVisible, setArchCreateVisible] = useState(false);
  const [archCreateParentId, setArchCreateParentId] = useState('0');

  const handleDeleteSystem = async () => {
    try {
      if (onSystemDelete) {
        await onSystemDelete(system.id);
        message.success(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteOk' }));
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteTodo' }));
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteFail' }));
    }
  };

  const handleEditSubmit = async (values) => {
    if (onSystemUpdate) {
      await onSystemUpdate(system.id, values);
    }
    setEditModalVisible(false);
  };

  const handleOrgSubmit = async (values) => {
    if (onOrganizationAdd) {
      await onOrganizationAdd(system.id, values);
    } else {
      message.info(intl.formatMessage({ id: 'component.organizationTree.org.msg.addTodo' }));
    }
    setOrgModalVisible(false);
  };

  const openArchCreate = (parentId) => {
    setArchCreateParentId(parentId);
    setArchCreateVisible(true);
  };

  const handleArchCreateSubmit = async (values) => {
    if (onSystemAdd) {
      await onSystemAdd(system.comId, { ...values, parentId: archCreateParentId });
    } else {
      message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.addTodo' }));
    }
    setArchCreateVisible(false);
  };

  const siblingParentId = system.parentId == null || String(system.parentId) === '0'
    ? '0'
    : system.parentId;

  const parentOrgOptions = [
    { label: intl.formatMessage({ id: 'component.organizationTree.org.detail.parent.top', defaultMessage: '顶级组织' }), value: '0' },
    ...(orgOptionsForParent || []).filter(
      (item) => String(item.archId) === String(system.archId ?? system.id)
        && String(item.comId) === String(system.comId),
    ),
  ];

  return (
    <div>
      <Descriptions bordered column={2}>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.name' })}>
          {system.archName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.code' })}>
          {system.archCode || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.company' })}>
          {companyName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.parent' })}>
          {parentSystemName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.displayOrder' })}>
          {system.displayOrder || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.status' })}>
          <Tag color={system.isValid === '1' ? 'green' : 'red'}>
            {system.isValid === '1'
              ? intl.formatMessage({ id: 'component.organizationTree.common.status.valid' })
              : intl.formatMessage({ id: 'component.organizationTree.common.status.invalid' })}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.desc' })} span={2}>
          {system.archDesc || system.description || '-'}
        </Descriptions.Item>
      </Descriptions>

      <Space wrap style={{ marginTop: 16 }}>
        <Button type="primary" icon={<EditOutlined />} onClick={() => setEditModalVisible(true)}>
          {intl.formatMessage({ id: 'component.organizationTree.system.action.edit' })}
        </Button>
        <Button icon={<PlusOutlined />} onClick={() => openArchCreate(system.id)}>
          {intl.formatMessage({ id: 'component.organizationTree.common.action.addChild' })}
        </Button>
        <Button icon={<PlusOutlined />} onClick={() => openArchCreate(siblingParentId)}>
          {intl.formatMessage({ id: 'component.organizationTree.common.action.addSibling' })}
        </Button>
        <Button icon={<PlusOutlined />} onClick={() => setOrgModalVisible(true)}>
          {intl.formatMessage({ id: 'component.organizationTree.org.action.add' })}
        </Button>
        <Popconfirm
          title={intl.formatMessage({ id: 'component.organizationTree.system.confirm.deleteTitle' })}
          description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
          onConfirm={handleDeleteSystem}
        >
          <Button danger icon={<DeleteOutlined />}>
            {intl.formatMessage({ id: 'component.organizationTree.system.action.delete' })}
          </Button>
        </Popconfirm>
      </Space>

      <ArchUpdateForm
        onSubmit={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        updateModalVisible={editModalVisible}
        values={system}
        lockComId={tenantFixedComId}
      />

      <ArchCreateForm
        modalVisible={archCreateVisible}
        onCancel={() => setArchCreateVisible(false)}
      >
        <ArchCreateFormContent
          key={`arch-create-${archCreateParentId}`}
          onSubmit={handleArchCreateSubmit}
          onCancel={() => setArchCreateVisible(false)}
          fixedComId={system.comId}
          defaultParentId={archCreateParentId}
        />
      </ArchCreateForm>

      <OrgCreateForm
        modalVisible={orgModalVisible}
        onCancel={() => setOrgModalVisible(false)}
      >
        <OrgCreateFormContent
          onSubmit={handleOrgSubmit}
          onCancel={() => setOrgModalVisible(false)}
          orgTypes={orgTypeOptions}
          comList={comOptions}
          archs={archOptionsForOrg}
          roles={parentOrgOptions}
          currentRecord={{ comId: system.comId, archId: system.id, parentId: '0' }}
        />
      </OrgCreateForm>
    </div>
  );
};

export default connect(({ user }) => ({
  currentUser: user.currentUser || {},
  ...buildAuthorityHelper(user, '/admin'),
}))(SystemDetail);
