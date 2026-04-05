/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, Modal, message, Popconfirm } from 'antd';
import { useIntl } from 'umi';
import { EditOutlined, DeleteOutlined, PlusOutlined, UserOutlined, SettingOutlined } from '@ant-design/icons';
import OrganizationEditForm from './OrganizationEditForm';
import UserCreateForm from './UserCreateForm';
import PermissionSettings from './PermissionSettings';

const OrganizationDetailContent = ({
  organization,
  onAddUser,
  companyName,
  systemName,
  userCount,
  showUsers,
  usersLoaded,
  onOrganizationUpdate,
  onUserAdd,
  onPermissionSettings,
  onOrganizationDelete,
}) => {
  const intl = useIntl();
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [userModalVisible, setUserModalVisible] = useState(false);
  const [permissionModalVisible, setPermissionModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [userLoading, setUserLoading] = useState(false);

  const handleEditOrganization = () => {
    setEditModalVisible(true);
  };

  const handleAddUser = () => {
    setUserModalVisible(true);
  };

  const handlePermissionSettings = () => {
    setPermissionModalVisible(true);
  };

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
    setEditLoading(true);
    try {
      if (onOrganizationUpdate) {
        await onOrganizationUpdate(organization.id, values);
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.updateOk' }));
        setEditModalVisible(false);
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.org.msg.updateTodo' }));
        setEditModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.updateFail' }));
    } finally {
      setEditLoading(false);
    }
  };

  const handleUserSubmit = async (values) => {
    setUserLoading(true);
    try {
      if (onUserAdd) {
        await onUserAdd(organization.id, values);
        message.success(intl.formatMessage({ id: 'component.organizationTree.user.msg.addOk' }));
        setUserModalVisible(false);
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.user.msg.addTodo' }));
        setUserModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.user.msg.addFail' }));
    } finally {
      setUserLoading(false);
    }
  };

  const handleShowUsers = () => {
    console.log('点击显示用户查看');
    if (onAddUser) {
      onAddUser();
    }
  };

  return (
    <div>
      <Descriptions title={intl.formatMessage({ id: 'component.organizationTree.org.detail.title' })} bordered column={2}>
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
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount' })}>
          {showUsers && usersLoaded ? (
            <Tag color="blue">
              <UserOutlined /> {userCount || 0} {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.unit' })}
            </Tag>
          ) : showUsers && !usersLoaded ? (
            <Tag color="default">
              <UserOutlined /> {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.loading' })}
            </Tag>
          ) : (
            <Tag color="default" style={{ cursor: 'pointer' }} onClick={handleShowUsers}>
              <UserOutlined /> {intl.formatMessage({ id: 'component.organizationTree.org.detail.userCount.show' })}
            </Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditOrganization}>
            {intl.formatMessage({ id: 'component.organizationTree.org.action.edit' })}
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddUser}>
            {intl.formatMessage({ id: 'component.organizationTree.user.action.add' })}
          </Button>
          <Button icon={<SettingOutlined />} onClick={handlePermissionSettings}>
            {intl.formatMessage({ id: 'component.organizationTree.permission.action.settings' })}
          </Button>
          <Popconfirm
            title={intl.formatMessage({ id: 'component.organizationTree.org.confirm.deleteTitle' })}
            description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
            onConfirm={handleDeleteOrganization}
          >
            <Button danger icon={<DeleteOutlined />}>
              {intl.formatMessage({ id: 'component.organizationTree.org.action.delete' })}
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑机构模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.org.modal.editTitle' })}
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        footer={null}
        width={600}
      >
        <OrganizationEditForm
          organization={organization}
          onSubmit={handleEditSubmit}
          loading={editLoading}
        />
      </Modal>

      {/* 添加用户模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.user.modal.addTitle' })}
        open={userModalVisible}
        onCancel={() => setUserModalVisible(false)}
        footer={null}
        width={600}
      >
        <UserCreateForm
          organizationId={organization.id}
          onSubmit={handleUserSubmit}
          loading={userLoading}
        />
      </Modal>

      {/* 权限设置模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.permission.modal.title' })}
        open={permissionModalVisible}
        onCancel={() => setPermissionModalVisible(false)}
        footer={null}
        width={800}
      >
        <PermissionSettings
          organization={organization}
          onSave={onPermissionSettings}
        />
      </Modal>
    </div>
  );
};

export default OrganizationDetailContent;
