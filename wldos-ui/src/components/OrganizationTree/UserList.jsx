/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Table, Spin, Button, Space, Tag, Avatar, Input, Select, Row, Col } from 'antd';
import { EditOutlined, DeleteOutlined, UserOutlined, EyeOutlined } from '@ant-design/icons';
import { useIntl } from 'umi';
import moment from 'moment';
import UserPermissionView from '@/components/UserPermissionView';

const { Search } = Input;
const { Option } = Select;

const UserList = ({ users, loading, organizationId }) => {
  const intl = useIntl();
  const [permissionModalVisible, setPermissionModalVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  // 删除重复的添加用户功能，因为上方已有添加用户按钮

  // 过滤用户数据
  const filteredUsers = users.filter(user => {
    const matchesSearch = !searchText ||
      user.nickname?.toLowerCase().includes(searchText.toLowerCase()) ||
      user.login_name?.toLowerCase().includes(searchText.toLowerCase()) ||
      user.email?.toLowerCase().includes(searchText.toLowerCase());

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
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.email' }),
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.status' }),
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === '正常' || status === 'normal' ? 'green' : 'red'}>
          {status === '正常' || status === 'normal'
            ? intl.formatMessage({ id: 'component.organizationTree.userList.status.normal' })
            : status === 'notActive'
              ? intl.formatMessage({ id: 'component.organizationTree.userList.status.notActive' })
              : status}
        </Tag>
      ),
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.createTime' }),
      dataIndex: 'createTime',
      key: 'createTime',
      render: (createTime) => {
        if (!createTime) return '-';
        // 处理时间戳（毫秒）
        const timestamp = typeof createTime === 'string' ? parseInt(createTime) : createTime;
        return moment(timestamp).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: intl.formatMessage({ id: 'component.organizationTree.userList.col.action' }),
      key: 'action',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            size="small"
          >
            {intl.formatMessage({ id: 'component.organizationTree.userList.action.edit' })}
          </Button>
          <Button
            type="link"
            icon={<EyeOutlined />}
            size="small"
            onClick={() => handleViewPermission(record)}
          >
            {intl.formatMessage({ id: 'component.organizationTree.userList.action.permissionView' })}
          </Button>
          <Button
            type="link"
            danger
            icon={<DeleteOutlined />}
            size="small"
          >
            {intl.formatMessage({ id: 'component.organizationTree.userList.action.delete' })}
          </Button>
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

      {/* 查询条件 */}
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
              <Option value="正常">{intl.formatMessage({ id: 'component.organizationTree.userList.status.normal' })}</Option>
              <Option value="notActive">{intl.formatMessage({ id: 'component.organizationTree.userList.status.notActive' })}</Option>
            </Select>
          </Col>
        </Row>
      </div>

      <Spin spinning={loading}>
        <Table
          columns={columns}
          dataSource={filteredUsers}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => intl.formatMessage({ id: 'component.organizationTree.userList.pagination.total' }, { total }),
          }}
          size="small"
        />
      </Spin>

      {/* 权限查看弹窗 */}
      <UserPermissionView
        visible={permissionModalVisible}
        onCancel={handleClosePermissionModal}
        userId={selectedUser?.id}
        userName={selectedUser?.nickname || selectedUser?.login_name || selectedUser?.username}
      />
    </div>
  );
};

export default UserList;
