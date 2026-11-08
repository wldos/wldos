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
import { canProvisionCompany } from '@/utils/orgCreateUserDomain';
import moment from 'moment';
import { EditOutlined, DeleteOutlined, PlusOutlined, UserAddOutlined } from '@ant-design/icons';
import ComUpdateForm from '@/pages/sys/com/components/UpdateForm';
import ComCreateForm from '@/pages/sys/com/components/CreateForm';
import ComCreateFormContent from '@/pages/sys/com/components/CreateFormContent';
import AddAdminForm from '@/pages/sys/com/components/AddAdminForm';
import ArchCreateForm from '@/pages/sys/arch/components/CreateForm';
import ArchCreateFormContent from '@/pages/sys/arch/components/CreateFormContent';
import { addComAdmin } from '@/pages/sys/com/service';
import {
  canDeleteCompanyRecord,
  companyDeleteBlockReason,
  VIRTUAL_PLATFORM_COM_ID,
} from '@/pages/sys/com/companyDeleteGuard';

const TOP_COM_ID = VIRTUAL_PLATFORM_COM_ID;

const CompanyDetail = ({
  company,
  onCompanyUpdate,
  onCompanyAdd,
  onSystemAdd,
  onCompanyDelete,
  comOptions = [],
  parentCompanyName,
  currentUser,
  isAdmin,
  isTenantAdmin,
}) => {
  const intl = useIntl();
  const canManageCompanyStructure = canProvisionCompany(isAdmin, isTenantAdmin);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [systemModalVisible, setSystemModalVisible] = useState(false);
  const [comCreateVisible, setComCreateVisible] = useState(false);
  const [comCreateParentId, setComCreateParentId] = useState('0');
  const [addAdminVisible, setAddAdminVisible] = useState(false);

  const isPlatformAdmin = String(currentUser?.tenantId ?? '') === TOP_COM_ID;
  const isTopLevelCompany = company.parentId == null || String(company.parentId) === TOP_COM_ID;
  const parentDisplay = isTopLevelCompany
    ? (isPlatformAdmin ? intl.formatMessage({ id: 'component.organizationTree.company.detail.platform' }) : '-')
    : (parentCompanyName || '-');

  const handleDeleteCompany = async () => {
    try {
      if (onCompanyDelete) {
        await onCompanyDelete(company.id);
        message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteOk' }));
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteTodo' }));
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.updateFail' }));
    }
  };

  const handleEditSubmit = async (values) => {
    if (onCompanyUpdate) {
      await onCompanyUpdate({ id: company.id, ...values });
    }
    setEditModalVisible(false);
  };

  const handleSystemSubmit = async (values) => {
    if (onSystemAdd) {
      await onSystemAdd(company.id, values);
    } else {
      message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.addTodo' }));
    }
    setSystemModalVisible(false);
  };

  const openComCreate = (parentId) => {
    setComCreateParentId(parentId);
    setComCreateVisible(true);
  };

  const handleComCreateSubmit = async (values) => {
    if (onCompanyAdd) {
      const ok = await onCompanyAdd({ ...values, parentId: comCreateParentId });
      if (ok !== false) {
        setComCreateVisible(false);
      }
    } else {
      message.info(intl.formatMessage({ id: 'component.organizationTree.company.msg.addTodo' }));
    }
  };

  const addTAdmin = async (value = { ids: [], userComId: '' }) => {
    if (!value || value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'component.organizationTree.company.msg.selectAdmin' }));
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'component.organizationTree.common.msg.submitting' }));
    try {
      const res = await addComAdmin({
        userIds: value.ids,
        userComId: value.userComId,
      });
      hide();
      if (res?.data !== '') {
        message.warn(res.data);
      } else {
        message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.adminOk' }));
      }
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.adminFail' }));
      return false;
    }
  };

  const siblingParentId = company.parentId == null || String(company.parentId) === TOP_COM_ID
    ? TOP_COM_ID
    : company.parentId;

  return (
    <div>
      <Descriptions bordered column={2}>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.name' })}>
          {company.comName || company.name || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.code' })}>
          {company.comCode || company.code || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.parent' })}>
          {parentDisplay}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.displayOrder' })}>
          {company.displayOrder || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.status' })}>
          <Tag color={company.isValid === '1' ? 'green' : 'red'}>
            {company.isValid === '1'
              ? intl.formatMessage({ id: 'component.organizationTree.common.status.valid' })
              : intl.formatMessage({ id: 'component.organizationTree.common.status.invalid' })}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.createTime' })}>
          {company.createTime ? moment(company.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.desc' })} span={2}>
          {company.comDesc || company.description || '-'}
        </Descriptions.Item>
      </Descriptions>

      <Space wrap style={{ marginTop: 16 }}>
        <Button type="primary" icon={<EditOutlined />} onClick={() => setEditModalVisible(true)}>
          {intl.formatMessage({ id: 'component.organizationTree.company.action.edit' })}
        </Button>
        {canManageCompanyStructure ? (
          <>
            <Button icon={<PlusOutlined />} onClick={() => openComCreate(company.id)}>
              {intl.formatMessage({ id: 'component.organizationTree.common.action.addChild' })}
            </Button>
            <Button icon={<PlusOutlined />} onClick={() => openComCreate(siblingParentId)}>
              {intl.formatMessage({ id: 'component.organizationTree.common.action.addSibling' })}
            </Button>
          </>
        ) : null}
        <Button icon={<PlusOutlined />} onClick={() => setSystemModalVisible(true)}>
          {intl.formatMessage({ id: 'component.organizationTree.system.action.add' })}
        </Button>
        <Button icon={<UserAddOutlined />} onClick={() => setAddAdminVisible(true)}>
          {intl.formatMessage({ id: 'component.organizationTree.company.action.setAdmin' })}
        </Button>
        {canManageCompanyStructure ? (
          canDeleteCompanyRecord(company) ? (
            <Popconfirm
              title={intl.formatMessage({ id: 'component.organizationTree.company.confirm.deleteTitle' })}
              description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
              onConfirm={handleDeleteCompany}
            >
              <Button danger icon={<DeleteOutlined />}>
                {intl.formatMessage({ id: 'component.organizationTree.company.action.delete' })}
              </Button>
            </Popconfirm>
          ) : (
            <Button danger icon={<DeleteOutlined />} disabled title={companyDeleteBlockReason(company)}>
              {intl.formatMessage({ id: 'component.organizationTree.company.action.delete' })}
            </Button>
          )
        ) : null}
      </Space>

      <ComUpdateForm
        onSubmit={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        updateModalVisible={editModalVisible}
        values={company}
        roles={comOptions}
      />

      <ComCreateForm
        modalVisible={comCreateVisible}
        onCancel={() => setComCreateVisible(false)}
      >
        <ComCreateFormContent
          key={`com-create-${comCreateParentId}`}
          onSubmit={handleComCreateSubmit}
          onCancel={() => setComCreateVisible(false)}
          roles={comOptions}
          defaultParentId={comCreateParentId}
        />
      </ComCreateForm>

      <AddAdminForm
        onCancel={() => setAddAdminVisible(false)}
        addAdminModalVisible={addAdminVisible}
        values={company}
        addTAdmin={addTAdmin}
      />

      <ArchCreateForm
        modalVisible={systemModalVisible}
        onCancel={() => setSystemModalVisible(false)}
      >
        <ArchCreateFormContent
          onSubmit={handleSystemSubmit}
          onCancel={() => setSystemModalVisible(false)}
          fixedComId={company.id}
        />
      </ArchCreateForm>
    </div>
  );
};

export default connect(({ user }) => ({
  currentUser: user.currentUser || {},
  ...buildAuthorityHelper(user, '/admin'),
}))(CompanyDetail);
