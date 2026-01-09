/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, Modal, message, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined, UserOutlined, SettingOutlined } from '@ant-design/icons';
import OrganizationEditForm from './OrganizationEditForm';
import UserCreateForm from './UserCreateForm';
import PermissionSettings from './PermissionSettings';

const OrganizationDetailContent = ({ organization, onAddUser, companyName, systemName, userCount, showUsers, usersLoaded, onOrganizationUpdate, onUserAdd, onPermissionSettings, onOrganizationDelete }) => {
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
        message.success('机构删除成功');
      } else {
        message.info('删除机构功能待实现');
      }
    } catch (error) {
      message.error('删除机构失败');
    }
  };

  const handleEditSubmit = async (values) => {
    setEditLoading(true);
    try {
      if (onOrganizationUpdate) {
        await onOrganizationUpdate(organization.id, values);
        message.success('机构信息更新成功');
        setEditModalVisible(false);
      } else {
        message.info('更新机构信息功能待实现');
        setEditModalVisible(false);
      }
    } catch (error) {
      message.error('更新机构信息失败');
    } finally {
      setEditLoading(false);
    }
  };

  const handleUserSubmit = async (values) => {
    setUserLoading(true);
    try {
      if (onUserAdd) {
        await onUserAdd(organization.id, values);
        message.success('用户添加成功');
        setUserModalVisible(false);
      } else {
        message.info('添加用户功能待实现');
        setUserModalVisible(false);
      }
    } catch (error) {
      message.error('添加用户失败');
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
      <Descriptions title="机构信息" bordered column={2}>
        <Descriptions.Item label="机构名称">{organization.orgName || '-'}</Descriptions.Item>
        <Descriptions.Item label="机构编码">{organization.orgCode || '-'}</Descriptions.Item>
        <Descriptions.Item label="组织类型">
          {organization.orgType === 'org' ? '组织' :
           organization.orgType === 'dept' ? '部门' :
           organization.orgType || organization.type || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="归属公司">{companyName || '-'}</Descriptions.Item>
        <Descriptions.Item label="归属体系">{systemName || '-'}</Descriptions.Item>
        <Descriptions.Item label="展示顺序">{organization.displayOrder || '-'}</Descriptions.Item>
        <Descriptions.Item label="状态">
          <Tag color={organization.isValid === '1' || organization.isValid === 1 ? 'green' : 'red'}>
            {organization.isValid === '1' || organization.isValid === 1 ? '有效' : '无效'}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label="用户数量">
          {showUsers && usersLoaded ? (
            <Tag color="blue">
              <UserOutlined /> {userCount || 0} 人
            </Tag>
          ) : showUsers && !usersLoaded ? (
            <Tag color="default">
              <UserOutlined /> 加载中...
            </Tag>
          ) : (
            <Tag color="default" style={{ cursor: 'pointer' }} onClick={handleShowUsers}>
              <UserOutlined /> 点击显示用户查看
            </Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditOrganization}>
            编辑机构
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddUser}>
            添加用户
          </Button>
          <Button icon={<SettingOutlined />} onClick={handlePermissionSettings}>
            权限设置
          </Button>
          <Popconfirm
            title="确定要删除这个机构吗？"
            description="删除后将无法恢复，请谨慎操作。"
            onConfirm={handleDeleteOrganization}
            okText="确定"
            cancelText="取消"
          >
            <Button danger icon={<DeleteOutlined />}>
              删除机构
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑机构模态框 */}
      <Modal
        title="编辑机构"
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
        title="添加用户"
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
        title="权限设置"
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
