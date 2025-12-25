import React, { useState } from 'react';
import { Table, Spin, Button, Space, Tag, Avatar, Input, Select, Row, Col } from 'antd';
import { EditOutlined, DeleteOutlined, UserOutlined, EyeOutlined, SearchOutlined } from '@ant-design/icons';
import moment from 'moment';
import UserPermissionView from '@/components/UserPermissionView';

const { Search } = Input;
const { Option } = Select;

const UserList = ({ users, loading, organizationId }) => {
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
      title: '头像',
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
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
    },
    {
      title: '登录名',
      dataIndex: 'login_name',
      key: 'login_name',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === '正常' ? 'green' : 'red'}>
          {status}
        </Tag>
      ),
    },
    {
      title: '创建时间',
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
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="small">
          <Button 
            type="link" 
            icon={<EditOutlined />}
            size="small"
          >
            编辑
          </Button>
          <Button 
            type="link" 
            icon={<EyeOutlined />}
            size="small"
            onClick={() => handleViewPermission(record)}
          >
            权限查看
          </Button>
          <Button 
            type="link" 
            danger 
            icon={<DeleteOutlined />}
            size="small"
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ marginTop: 16 }}>
      <div style={{ marginBottom: 16 }}>
        <h3 style={{ margin: 0, color: '#333' }}>
          <UserOutlined /> 组织成员
        </h3>
      </div>
      
      {/* 查询条件 */}
      <div style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={12}>
            <Search
              placeholder="搜索昵称、登录名或邮箱"
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
              placeholder="选择状态"
            >
              <Option value="all">全部状态</Option>
              <Option value="正常">正常</Option>
              <Option value="notActive">未激活</Option>
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
            showTotal: (total) => `共 ${total} 条记录`,
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
