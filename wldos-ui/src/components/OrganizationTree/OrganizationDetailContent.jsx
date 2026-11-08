/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useRef } from 'react';
import { Descriptions, Tag, Button, Space, message, Popconfirm, Select } from 'antd';
import { useIntl } from 'umi';
import { EditOutlined, DeleteOutlined, PlusOutlined, UserOutlined, SettingOutlined, UserAddOutlined } from '@ant-design/icons';
import OrgUpdateForm from '@/pages/sys/org/components/UpdateForm';
import OrgCreateForm from '@/pages/sys/org/components/CreateForm';
import OrgCreateFormContent from '@/pages/sys/org/components/CreateFormContent';
import AddUserForm from '@/pages/sys/org/components/AddUserForm';
import AuthRole from '@/pages/sys/org/components/AuthRole';
import CreateForm from '@/pages/sys/user/components/CreateForm';
import CreateFormContent from '@/pages/sys/user/components/CreateFormContent';
import { addEntity } from '@/pages/sys/user/service';
import { addOrgUser, authRole, getExistRes } from '@/pages/sys/org/service';
import {
  needsTargetSiteSelection,
  resolveCreateUserDomainId,
  isValidScopeComId,
  canPickMemberScopeInModal,
} from '@/utils/orgCreateUserDomain';

const OrganizationDetailContent = ({
  organization,
  onToggleShowUsers,
  companyName,
  systemName,
  parentOrgName,
  userCount,
  showUsers,
  usersLoaded,
  onOrganizationUpdate,
  onOrganizationAdd,
  onMembersChanged,
  onOrganizationDelete,
  orgTypeOptions = [],
  archOptionsForOrg = [],
  orgOptionsForParent = [],
  comOptions = [],
  hasAuthority = () => true,
  targetDomainId,
  domainOptions = [],
  showDomainSelect = false,
  onTargetDomainChange,
  allDomainRows = [],
}) => {
  const intl = useIntl();
  const createFormRef = useRef();
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [orgCreateVisible, setOrgCreateVisible] = useState(false);
  const [orgCreateRecord, setOrgCreateRecord] = useState(null);
  const [addUserModalVisible, setAddUserModalVisible] = useState(false);
  const [createUserVisible, setCreateUserVisible] = useState(false);
  const [authModalVisible, setAuthModalVisible] = useState(false);
  const [resTree, setResTree] = useState([]);
  const [existRes, setExistRes] = useState([]);

  const parentOrgOptions = [
    { label: intl.formatMessage({ id: 'component.organizationTree.org.detail.parent.top', defaultMessage: '顶级组织' }), value: '0' },
    ...(orgOptionsForParent || []).filter(
      (item) => String(item.archId) === String(organization.archId)
        && String(item.comId) === String(organization.comId),
    ),
  ];

  const handleDeleteOrganization = async () => {
    try {
      if (onOrganizationDelete) {
        await onOrganizationDelete(organization.id);
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteOk' }));
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteTodo' }));
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteFail' }));
    }
  };

  const handleEditSubmit = async (values) => {
    if (onOrganizationUpdate) {
      await onOrganizationUpdate(organization.id, values);
    }
    setEditModalVisible(false);
  };

  const openOrgCreate = (parentId) => {
    setOrgCreateRecord({
      parentId,
      orgType: organization.orgType,
      comId: organization.comId,
      archId: organization.archId,
    });
    setOrgCreateVisible(true);
  };

  const handleOrgCreateSubmit = async (values) => {
    if (onOrganizationAdd) {
      await onOrganizationAdd(organization.archId, values);
    } else {
      message.info(intl.formatMessage({ id: 'component.organizationTree.org.msg.addTodo' }));
    }
    setOrgCreateVisible(false);
  };

  const handleAddUser = async (value = { ids: [], orgId: '', archId: '', comId: '' }) => {
    if (value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'component.organizationTree.user.msg.selectMembers' }));
      return false;
    }
    const domainId = value.domainId || resolveCreateUserDomainId(organization, { targetDomainId, domainOptions });
    try {
      const res = await addOrgUser({
        userIds: value.ids,
        orgId: value.orgId,
        archId: value.archId,
        comId: isValidScopeComId(value.comId) ? value.comId : undefined,
        ...(domainId ? { domainId } : {}),
      });
      if (res?.code !== 200) {
        message.error(res?.message || intl.formatMessage({ id: 'component.organizationTree.user.msg.addFail' }));
        return false;
      }
      if (res?.data !== '') {
        message.warn(res.data);
      } else {
        message.success(intl.formatMessage({ id: 'component.organizationTree.user.msg.addOk' }));
      }
      if (onMembersChanged) onMembersChanged();
      return true;
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.user.msg.addFail' }));
      return false;
    }
  };

  const handleCreateUser = async (fields) => {
    const domainId = resolveCreateUserDomainId(organization, {
      targetDomainId,
      domainOptions,
    });
    if (needsTargetSiteSelection(organization, {
      showDomainSelect,
      targetDomainId,
      domainOptions,
    })) {
      message.error(intl.formatMessage({
        id: 'sys.org.msg.selectTargetSite',
        defaultMessage: '请选择目标站点',
      }));
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.add', defaultMessage: '正在添加' }));
    try {
      const payload = {
        ...fields,
        orgId: organization.id,
        archId: organization.archId,
        comId: organization.comId,
      };
      if (domainId) {
        payload.domainId = domainId;
      }
      const res = await addEntity(payload);
      if (res?.data) {
        const { status, news } = res.data;
        if (status === 'error') {
          hide();
          message.error(news);
          return false;
        }
      }
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.addSuccess', defaultMessage: '添加成功' }));
      if (onMembersChanged) {
        onMembersChanged();
      }
      return true;
    } catch (error) {
      hide();
      const desc = error?.response?.message;
      const applied = createFormRef.current?.applyServerValidationErrors?.(desc);
      if (!applied) {
        message.error(intl.formatMessage({ id: 'sys.user.msg.addFail', defaultMessage: '添加失败请重试！' }));
      }
      return false;
    }
  };

  const handleOpenAuth = async () => {
    if (needsTargetSiteSelection(organization, { showDomainSelect, targetDomainId, domainOptions })) {
      message.error(intl.formatMessage({
        id: 'sys.org.msg.selectTargetSite',
        defaultMessage: '请选择目标站点',
      }));
      return;
    }
    const params = { orgId: organization.id };
    const domainId = resolveCreateUserDomainId(organization, { targetDomainId, domainOptions });
    if (domainId) {
      params.domainId = domainId;
    }
    const res = await getExistRes(params);
    if (res && res.data && res.data.authRes) {
      const { orgRole = [], authRes = [] } = res.data;
      setExistRes(orgRole.map((item) => item.id));
      setResTree(authRes);
      setAuthModalVisible(true);
    } else {
      message.error(intl.formatMessage({ id: 'component.organizationTree.permission.msg.loadFail' }));
    }
  };

  const handleAuthSubmit = async (fields = { roleIds: [], orgId: '', archId: '', comId: '' }) => {
    if (
      existRes.length === fields.roleIds.length &&
      fields.roleIds.every((id) => existRes.includes(id))
    ) {
      message.info(intl.formatMessage({ id: 'component.organizationTree.permission.msg.noChange' }));
      return;
    }
    try {
      await authRole({
        roleIds: fields.roleIds,
        orgId: fields.orgId,
        archId: fields.archId,
        comId: fields.comId,
      });
      message.success(intl.formatMessage({ id: 'component.organizationTree.permission.msg.authOk' }));
      setAuthModalVisible(false);
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.permission.msg.authFail' }));
    }
  };

  const siblingParentId = organization.parentId == null || String(organization.parentId) === '0'
    ? '0'
    : organization.parentId;

  return (
    <div>
      <Descriptions bordered column={2}>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.name' })}>
          {organization.orgName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.code' })}>
          {organization.orgCode || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.type' })}>
          {organization.orgType === 'org'
            ? intl.formatMessage({ id: 'component.organizationTree.org.detail.type.org' })
            : organization.orgType === 'dept'
              ? intl.formatMessage({ id: 'component.organizationTree.org.detail.type.dept' })
              : organization.orgType || organization.type || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.company' })}>
          {companyName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.system' })}>
          {systemName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.parent' })}>
          {parentOrgName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.displayOrder' })}>
          {organization.displayOrder || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.status' })}>
          <Tag color={organization.isValid === '1' || organization.isValid === 1 ? 'green' : 'red'}>
            {organization.isValid === '1' || organization.isValid === 1
              ? intl.formatMessage({ id: 'component.organizationTree.common.status.valid' })
              : intl.formatMessage({ id: 'component.organizationTree.common.status.invalid' })}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount' })} span={2}>
          {showUsers && usersLoaded ? (
            <Tag color="blue">
              <UserOutlined /> {userCount || 0} {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.unit' })}
            </Tag>
          ) : showUsers && !usersLoaded ? (
            <Tag color="default">
              <UserOutlined /> {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.loading' })}
            </Tag>
          ) : (
            <Tag color="default" style={{ cursor: 'pointer' }} onClick={onToggleShowUsers}>
              <UserOutlined /> {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.show' })}
            </Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      {showDomainSelect ? (
        <Space style={{ marginTop: 12 }}>
          <span>{intl.formatMessage({ id: 'sys.role.domain.targetSite', defaultMessage: '目标站点' })}</span>
          <Select
            style={{ minWidth: 220 }}
            placeholder={intl.formatMessage({
              id: 'sys.org.msg.selectTargetSite',
              defaultMessage: '请选择目标站点',
            })}
            options={domainOptions}
            value={targetDomainId}
            onChange={(value) => onTargetDomainChange?.(value || undefined)}
            showSearch
            optionFilterProp="label"
          />
        </Space>
      ) : null}

      <Space wrap style={{ marginTop: 16 }}>
        {hasAuthority('org:update') && (
          <Button type="primary" icon={<EditOutlined />} onClick={() => setEditModalVisible(true)}>
            {intl.formatMessage({ id: 'component.organizationTree.org.action.edit' })}
          </Button>
        )}
        {hasAuthority('org:create') && (
          <>
            <Button icon={<PlusOutlined />} onClick={() => openOrgCreate(organization.id)}>
              {intl.formatMessage({ id: 'component.organizationTree.common.action.addChild' })}
            </Button>
            <Button icon={<PlusOutlined />} onClick={() => openOrgCreate(siblingParentId)}>
              {intl.formatMessage({ id: 'component.organizationTree.common.action.addSibling' })}
            </Button>
          </>
        )}
        {hasAuthority('org:addUser') && (
          <Button
            icon={<PlusOutlined />}
            onClick={() => {
              if (needsTargetSiteSelection(organization, {
                showDomainSelect,
                targetDomainId,
                domainOptions,
              }) && !canPickMemberScopeInModal(organization)) {
                message.error(intl.formatMessage({
                  id: 'sys.org.msg.selectTargetSite',
                  defaultMessage: '请选择目标站点',
                }));
                return;
              }
              setAddUserModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'component.organizationTree.user.action.add' })}
          </Button>
        )}
        {hasAuthority('user:create') && (
          <Button
            icon={<UserAddOutlined />}
            onClick={() => {
              if (needsTargetSiteSelection(organization, {
                showDomainSelect,
                targetDomainId,
                domainOptions,
              })) {
                message.error(intl.formatMessage({
                  id: 'sys.org.msg.selectTargetSite',
                  defaultMessage: '请选择目标站点',
                }));
                return;
              }
              setCreateUserVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.user.toolbar.new', defaultMessage: '新增用户' })}
          </Button>
        )}
        {hasAuthority('org:auth') && (
          <Button icon={<SettingOutlined />} onClick={handleOpenAuth}>
            {intl.formatMessage({ id: 'component.organizationTree.permission.action.settings' })}
          </Button>
        )}
        {hasAuthority('org:delete') && (
          <Popconfirm
            title={intl.formatMessage({ id: 'component.organizationTree.org.confirm.deleteTitle' })}
            description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
            onConfirm={handleDeleteOrganization}
          >
            <Button danger icon={<DeleteOutlined />}>
              {intl.formatMessage({ id: 'component.organizationTree.org.action.delete' })}
            </Button>
          </Popconfirm>
        )}
      </Space>

      <OrgUpdateForm
        onSubmit={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        updateModalVisible={editModalVisible}
        values={organization}
        orgTypes={orgTypeOptions}
        archs={archOptionsForOrg}
        roles={orgOptionsForParent}
      />

      <OrgCreateForm
        modalVisible={orgCreateVisible}
        onCancel={() => setOrgCreateVisible(false)}
      >
        <OrgCreateFormContent
          key={orgCreateRecord ? `org-create-${orgCreateRecord.parentId}` : 'org-create'}
          onSubmit={handleOrgCreateSubmit}
          onCancel={() => setOrgCreateVisible(false)}
          orgTypes={orgTypeOptions}
          comList={comOptions}
          archs={archOptionsForOrg}
          roles={parentOrgOptions}
          currentRecord={orgCreateRecord}
        />
      </OrgCreateForm>

      <AddUserForm
        addUserModalVisible={addUserModalVisible}
        onCancel={() => setAddUserModalVisible(false)}
        values={{
          id: organization.id,
          orgName: organization.orgName,
          orgCode: organization.orgCode,
          archId: organization.archId,
          comId: organization.comId,
          domainId: organization.domainId,
          kindTemplate: organization.kindTemplate,
          systemBuiltIn: organization.systemBuiltIn,
        }}
        addUser={handleAddUser}
        comList={comOptions}
        domainOptions={domainOptions}
        allDomainRows={allDomainRows}
        showDomainSelect={showDomainSelect}
        effectiveComId={isValidScopeComId(organization.comId) ? organization.comId : undefined}
        listDomainId={resolveCreateUserDomainId(organization, { targetDomainId, domainOptions })}
        listComId={isValidScopeComId(organization.comId) ? organization.comId : undefined}
      />

      <CreateForm
        modalVisible={createUserVisible}
        onCancel={() => setCreateUserVisible(false)}
        onSubmit={() => createFormRef.current?.submit?.()}
      >
        <CreateFormContent
          ref={createFormRef}
          onSubmit={async (values) => {
            const success = await handleCreateUser(values);
            if (success) {
              setCreateUserVisible(false);
            }
          }}
          onCancel={() => setCreateUserVisible(false)}
        />
      </CreateForm>

      <AuthRole
        onSubmit={handleAuthSubmit}
        onCancel={() => setAuthModalVisible(false)}
        authModalVisible={authModalVisible}
        res={resTree}
        existRes={existRes}
        values={{
          id: organization.id,
          orgName: organization.orgName,
          archId: organization.archId,
          comId: organization.comId,
        }}
      />
    </div>
  );
};

export default OrganizationDetailContent;
