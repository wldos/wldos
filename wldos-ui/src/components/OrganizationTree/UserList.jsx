/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Space, Tag, Avatar, Input, Select, Row, Col, Popconfirm, message, Divider } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import ProTableX from '@/components/ProTableX';
import { useIntl } from 'umi';
import UserPermissionView from '@/components/UserPermissionView';
import UpdateForm from '@/pages/sys/user/components/UpdateForm';
import { removeOrgStaff, updateEntity } from '@/pages/sys/user/service';

const { Search } = Input;
const { Option } = Select;

function normalizeUserRecord(record) {
  if (!record) {
    return record;
  }
  return {
    ...record,
    loginName: record.loginName || record.login_name || record.account,
    login_name: record.login_name || record.loginName || record.account,
  };
}

const UserList = ({ users, loading, organizationId, onMembersChanged, hasAuthority = () => true }) => {
  const intl = useIntl();
  const [permissionModalVisible, setPermissionModalVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [updateModalVisible, setUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});

  const filteredUsers = users.filter((user) => {
    const loginName = user.loginName || user.login_name || user.account || '';
    const matchesSearch = !searchText
      || user.nickname?.toLowerCase().includes(searchText.toLowerCase())
      || loginName.toLowerCase().includes(searchText.toLowerCase())
      || user.email?.toLowerCase().includes(searchText.toLowerCase());

    const matchesStatus = statusFilter === 'all' || user.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const handleViewPermission = (record) => {
    setSelectedUser(record);
    setPermissionModalVisible(true);
  };

  const handleClosePermissionModal = () => {
    setPermissionModalVisible(false);
    setSelectedUser(null);
  };

  const handleRemoveMember = async (record) => {
    try {
      await removeOrgStaff({ ids: [record.id], orgId: organizationId });
      message.success(intl.formatMessage({ id: 'component.organizationTree.userList.msg.removeOk' }));
      if (onMembersChanged) onMembersChanged();
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.userList.msg.removeFail' }));
    }
  };

  const handleUpdate = async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.config', defaultMessage: '正在配置' }));
    try {
      await updateEntity({
        nickname: fields.nickname,
        remark: fields.remark,
        loginName: fields.loginName,
        email: fields.email,
        mobile: fields.mobile,
        passwd: fields.passwd,
        status: fields.status,
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  };

  const renderStatusTag = (status) => {
    const isNormal = status === '正常' || status === 'normal';
    let label = status;
    if (status === 'normal') {
      label = intl.formatMessage({ id: 'component.organizationTree.userList.status.normal' });
    } else if (status === 'notActive') {
      label = intl.formatMessage({ id: 'component.organizationTree.userList.status.notActive' });
    } else if (status === 'locked') {
      label = intl.formatMessage({ id: 'sys.user.status.locked', defaultMessage: '已锁定' });
    }
    return (
      <Tag color={isNormal ? 'green' : 'red'}>
        {label}
      </Tag>
    );
  };

  const columns = [
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.avatar' }),
      dataIndex: 'avatar',
      key: 'avatar',
      width: 60,
      render: (avatar) => (
        <Avatar
          src={avatar}
          icon={<UserOutlined />}
          size="small"
        />
      ),
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.nickname' }),
      dataIndex: 'nickname',
      key: 'nickname',
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.loginName' }),
      dataIndex: 'login_name',
      key: 'login_name',
      render: (_, record) => record.loginName || record.login_name || record.account || '-',
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.email' }),
      dataIndex: 'email',
      key: 'email',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.status' }),
      dataIndex: 'status',
      key: 'status',
      render: (status) => renderStatusTag(status),
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.createTime' }),
      dataIndex: 'createTime',
      key: 'createTime',
      valueType: 'dateTime',
      width: 168,
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.action' }),
      key: 'action',
      valueType: 'option',
      fixed: 'right',
      width: 200,
      render: (_, record) => (
        <Space size={0} split={<Divider type="vertical" />}>
          {hasAuthority('user:update') && (
            <a
              onClick={() => {
                setStepFormValues(normalizeUserRecord(record));
                setUpdateModalVisible(true);
              }}
            >
              {intl.formatMessage({ id: 'sys.user.action.config', defaultMessage: '配置' })}
            </a>
          )}
          {hasAuthority('user:update') && (
            <a onClick={() => handleViewPermission(record)}>
              {intl.formatMessage({ id: 'component.organizationTree.userList.action.permissionView' })}
            </a>
          )}
          <Popconfirm
            title={intl.formatMessage({ id: 'component.organizationTree.userList.confirm.removeTitle' })}
            onConfirm={() => handleRemoveMember(record)}
          >
            <a>{intl.formatMessage({ id: 'component.organizationTree.userList.action.delete' })}</a>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ marginTop: 16 }}>
      <div style={{ marginBottom: 16 }}>
        <h3 style={{ margin: 0, color: '#333' }}>
          <UserOutlined /> {intl.formatMessage({ id: 'component.organizationTree.userList.title' })}
        </h3>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={12}>
            <Search
              placeholder={intl.formatMessage({ id: 'component.organizationTree.userList.searchPlaceholder' })}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onSearch={setSearchText}
              style={{ width: '100%' }}
              allowClear
            />
          </Col>
          <Col span={6}>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: '100%' }}
              placeholder={intl.formatMessage({ id: 'component.organizationTree.userList.filter.statusPlaceholder' })}
            >
              <Option value="all">{intl.formatMessage({ id: 'component.organizationTree.userList.filter.allStatus' })}</Option>
              <Option value="normal">{intl.formatMessage({ id: 'component.organizationTree.userList.status.normal' })}</Option>
              <Option value="notActive">{intl.formatMessage({ id: 'component.organizationTree.userList.status.notActive' })}</Option>
              <Option value="locked">{intl.formatMessage({ id: 'sys.user.status.locked', defaultMessage: '已锁定' })}</Option>
            </Select>
          </Col>
        </Row>
      </div>

      <ProTableX
        listLayout={false}
        search={false}
        options={false}
        sticky={false}
        scroll={{ x: 960 }}
        columns={columns}
        dataSource={filteredUsers}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => intl.formatMessage({ id: 'component.organizationTree.userList.pagination.total' }, { total }),
        }}
        size="small"
      />

      <UserPermissionView
        visible={permissionModalVisible}
        onCancel={handleClosePermissionModal}
        userId={selectedUser?.id}
        userName={selectedUser?.nickname || selectedUser?.login_name || selectedUser?.username}
      />

      <UpdateForm
        onCancel={() => setUpdateModalVisible(false)}
        updateModalVisible={updateModalVisible}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            setUpdateModalVisible(false);
            if (onMembersChanged) onMembersChanged();
          }
        }}
        values={stepFormValues}
      />
    </div>
  );
};

export default UserList;
