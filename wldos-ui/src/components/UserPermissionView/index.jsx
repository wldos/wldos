import React, { useState, useEffect } from 'react';
import { Modal, Table, Tabs, Card, Tag, Space, Spin, message } from 'antd';
import { EyeOutlined, UserOutlined, SettingOutlined } from '@ant-design/icons';
import { queryUserMenu, queryUserAdminMenu } from '@/services/user';
import { renderIcon } from '@/utils/iconLibrary';

const { TabPane } = Tabs;

const UserPermissionView = ({ visible, onCancel, userId, userName }) => {
  const [loading, setLoading] = useState(false);
  const [userPermissions, setUserPermissions] = useState({});
  const [adminPermissions, setAdminPermissions] = useState([]);

  useEffect(() => {
    if (visible && userId) {
      fetchUserPermissions();
    }
  }, [visible, userId]);

  const fetchUserPermissions = async () => {
    if (!userId) {
      message.error('用户ID不能为空');
      return;
    }

    setLoading(true);
    try {
      // 获取指定用户的用户端权限
      const userResponse = await queryUserMenu(userId);
      // 获取指定用户的管理端权限
      const adminResponse = await queryUserAdminMenu(userId);
      
      console.log('用户端权限数据:', userResponse);
      console.log('管理端权限数据:', adminResponse);
      
      setUserPermissions(userResponse.data || {});
      setAdminPermissions(adminResponse.data || []);
    } catch (error) {
      message.error('获取用户权限失败');
      console.error('获取用户权限失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 渲染菜单权限
  const renderMenuPermissions = (menus) => {
    const columns = [
      {
        title: '菜单名称',
        dataIndex: 'name',
        key: 'name',
        render: (text, record) => (
          <Space>
            {record.icon && renderIcon(record.icon)}
            {text}
          </Space>
        ),
      },
      {
        title: '路径',
        dataIndex: 'path',
        key: 'path',
        render: (path) => (
          <code style={{ fontSize: '12px', background: '#f5f5f5', padding: '2px 4px', borderRadius: '2px' }}>
            {path}
          </code>
        ),
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
        render: (type) => {
          const typeMap = {
            'menu': { text: '菜单', color: 'blue' },
            'admin_menu': { text: '管理菜单', color: 'green' },
            'admin_plugin_menu': { text: '插件菜单', color: 'purple' },
          };
          const config = typeMap[type] || { text: type, color: 'default' };
          return <Tag color={config.color}>{config.text}</Tag>;
        },
      },
      {
        title: '打开方式',
        dataIndex: 'target',
        key: 'target',
        render: (target) => {
          const targetMap = {
            '_self': { text: '当前窗口', color: 'blue' },
            '_blank': { text: '新窗口', color: 'green' },
          };
          const config = targetMap[target] || { text: target, color: 'default' };
          return <Tag color={config.color}>{config.text}</Tag>;
        },
      },
      {
        title: '排序',
        dataIndex: 'displayOrder',
        key: 'displayOrder',
        width: 80,
        render: (order) => <span style={{ color: '#666' }}>{order}</span>,
      },
    ];

    return (
      <Table
        columns={columns}
        dataSource={menus}
        rowKey="id"
        pagination={false}
        size="small"
        expandable={{
          childrenColumnName: 'children',
          defaultExpandAllRows: true,
        }}
      />
    );
  };

  // 渲染用户信息
  const renderUserInfo = (userInfo) => {
    if (!userInfo) return null;

    return (
      <Card size="small" title="用户信息">
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <div>
            <strong>登录名：</strong>{userInfo.login_name || '-'}
          </div>
          <div>
            <strong>昵称：</strong>{userInfo.nickname || '-'}
          </div>
          <div>
            <strong>真实姓名：</strong>{userInfo.username || '-'}
          </div>
          <div>
            <strong>手机号：</strong>{userInfo.mobile || '-'}
          </div>
          <div>
            <strong>邮箱：</strong>{userInfo.email || '-'}
          </div>
          <div>
            <strong>头像：</strong>
            {userInfo.avatar ? (
              <img 
                src={userInfo.avatar} 
                alt="头像" 
                style={{ width: 24, height: 24, borderRadius: '50%', verticalAlign: 'middle' }}
              />
            ) : '-'}
          </div>
          <div>
            <strong>状态：</strong>
            <Tag color={userInfo.status === 'normal' ? 'green' : 'red'}>
              {userInfo.status === 'normal' ? '正常' : 
               userInfo.status === 'notActive' ? '待激活' :
               userInfo.status === 'locked' ? '已锁定' :
               userInfo.status === 'cancelled' ? '已注销' :
               userInfo.status || '未知'}
            </Tag>
          </div>
          <div>
            <strong>实名认证：</strong>
            <Tag color={userInfo.is_real === '1' ? 'green' : 'orange'}>
              {userInfo.is_real === '1' ? '已认证' : '未认证'}
            </Tag>
          </div>
          <div>
            <strong>域ID：</strong>{userInfo.domain_id || '无'}
          </div>
          <div>
            <strong>公司：</strong>{userInfo.company || '-'}
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <strong>备注：</strong>{userInfo.remark || '-'}
          </div>
        </div>
      </Card>
    );
  };

  return (
    <Modal
      title={
        <Space>
          <EyeOutlined />
          用户权限查看 - {userName || '未知用户'}
        </Space>
      }
      open={visible}
      onCancel={onCancel}
      width={1000}
      footer={null}
      destroyOnClose
    >
      <Spin spinning={loading}>
        <Tabs defaultActiveKey="user" type="card">
          <TabPane
            tab={
              <Space>
                <UserOutlined />
                用户端权限
              </Space>
            }
            key="user"
          >
            {renderUserInfo(userPermissions.userInfo)}
            
            {/* 权限级别展示 */}
            {userPermissions.currentAuthority && (
              <div style={{ marginTop: 16 }}>
                <Card size="small" title="权限级别">
                  <Space wrap>
                    {userPermissions.currentAuthority.map((authority, index) => (
                      <Tag key={index} color="blue">{authority}</Tag>
                    ))}
                  </Space>
                </Card>
              </div>
            )}
            
            <div style={{ marginTop: 16 }}>
              <Card size="small" title="用户端菜单权限">
                {renderMenuPermissions(userPermissions.menu || [])}
              </Card>
            </div>
          </TabPane>
          
          <TabPane
            tab={
              <Space>
                <SettingOutlined />
                管理端权限
              </Space>
            }
            key="admin"
          >
            <Card size="small" title="管理端菜单权限">
              {renderMenuPermissions(adminPermissions)}
            </Card>
          </TabPane>
        </Tabs>
      </Spin>
    </Modal>
  );
};

export default UserPermissionView;
