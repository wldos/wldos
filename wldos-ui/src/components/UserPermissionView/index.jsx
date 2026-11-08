/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect } from 'react';
import { Modal, Table, Tabs, Card, Tag, Space, Spin, Typography, Divider, Button, Popconfirm, Select, message } from 'antd';
import { EyeOutlined, UserOutlined, SettingOutlined, TeamOutlined, PlusOutlined, LinkOutlined } from '@ant-design/icons';
import { queryUserMenu, queryUserAdminMenu, queryUserRoleSources, addOrgStaff, removeOrgStaff, bindUserRole, unbindUserRole } from '@/services/user';
import { getRoleList } from '@/pages/sys/role/service';
import OrgTreeSelect from '@/components/TreeSelects/OrgTreeSelect';
import { renderIcon } from '@/utils/iconLibrary';

const { TabPane } = Tabs;
const { Text } = Typography;

const UserPermissionView = ({ visible, onCancel, userId, userName, writable = true }) => {
  const [loading, setLoading] = useState(false);
  const [userPermissions, setUserPermissions] = useState({});
  const [adminPermissions, setAdminPermissions] = useState([]);
  const [orgRoles, setOrgRoles] = useState([]);
  const [directRoles, setDirectRoles] = useState([]);
  const [subjectRoles, setSubjectRoles] = useState([]);
  const [identityRoles, setIdentityRoles] = useState([]);
  const [addOrgVisible, setAddOrgVisible] = useState(false);
  const [selectedOrgId, setSelectedOrgId] = useState(null);
  const [orgActionLoading, setOrgActionLoading] = useState(false);
  const [bindRoleVisible, setBindRoleVisible] = useState(false);
  const [selectedRoleId, setSelectedRoleId] = useState(null);
  const [roleOptions, setRoleOptions] = useState([]);
  const [roleActionLoading, setRoleActionLoading] = useState(false);

  useEffect(() => {
    if (visible && userId) {
      fetchUserPermissions();
    }
  }, [visible, userId]);

  const fetchUserPermissions = async () => {
    if (!userId) {
      return;
    }

    setLoading(true);
    try {
      const userResponse = await queryUserMenu(userId);

      if (userResponse?.code !== 200) {
        return;
      }
      setUserPermissions(userResponse.data || {});

      const adminResponse = await queryUserAdminMenu(userId);
      setAdminPermissions(adminResponse?.data || []);

      const sourceResponse = await queryUserRoleSources(userId);
      const sources = sourceResponse?.data || {};
      setOrgRoles(sources.orgRoles || []);
      setDirectRoles(sources.directRoles || []);
      setSubjectRoles(sources.subjectRoles || []);
      setIdentityRoles(sources.identityRoles || []);
    } catch (error) {
      console.error('获取用户权限失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const openBindRoleModal = async () => {
    setBindRoleVisible(true);
    setSelectedRoleId(null);
    const res = await getRoleList();
    const rows = res?.data || [];
    setRoleOptions(rows.map((r) => ({ label: r.roleName || r.roleCode, value: r.id })));
  };

  const handleBindRole = async () => {
    if (!selectedRoleId) {
      message.warning('请选择角色');
      return;
    }
    setRoleActionLoading(true);
    try {
      const res = await bindUserRole({ userId: String(userId), roleId: String(selectedRoleId) });
      if (res?.code !== 200) {
        return;
      }
      message.success('已直挂角色');
      setBindRoleVisible(false);
      fetchUserPermissions();
    } finally {
      setRoleActionLoading(false);
    }
  };

  const handleUnbindRole = async (record) => {
    setRoleActionLoading(true);
    try {
      const res = await unbindUserRole({ userId: String(userId), roleId: String(record.roleId) });
      if (res?.code !== 200) {
        return;
      }
      message.success('已解绑');
      fetchUserPermissions();
    } finally {
      setRoleActionLoading(false);
    }
  };

  const handleAddOrg = async () => {
    if (selectedOrgId == null || selectedOrgId === '') {
      message.warning('请选择组织');
      return;
    }
    setOrgActionLoading(true);
    try {
      const res = await addOrgStaff({
        orgId: selectedOrgId,
        userIds: [String(userId)],
      });
      if (res?.code !== 200) {
        return;
      }
      message.success('已加入组织');
      setAddOrgVisible(false);
      setSelectedOrgId(null);
      fetchUserPermissions();
    } finally {
      setOrgActionLoading(false);
    }
  };

  const handleRemoveOrg = async (record) => {
    setOrgActionLoading(true);
    try {
      const res = await removeOrgStaff({ ids: [String(userId)], orgId: record.orgId });
      if (res?.code !== 200) {
        return;
      }
      message.success('已移出组织');
      fetchUserPermissions();
    } finally {
      setOrgActionLoading(false);
    }
  };

  const renderMenuPermissions = (menus, emptyHint) => {
    const menuList = menus || [];
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
            menu: { text: '菜单', color: 'blue' },
            admin_menu: { text: '管理菜单', color: 'green' },
            admin_plugin_menu: { text: '插件菜单', color: 'purple' },
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
            _self: { text: '当前窗口', color: 'blue' },
            _blank: { text: '新窗口', color: 'green' },
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
      <>
        {menuList.length === 0 && emptyHint && (
          <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
            {emptyHint}
          </Text>
        )}
        <Table
          columns={columns}
          dataSource={menuList}
          rowKey="id"
          pagination={false}
          size="small"
          expandable={{
            childrenColumnName: 'children',
            defaultExpandAllRows: true,
          }}
        />
      </>
    );
  };

  const renderAuthContext = () => {
    const domainName = userPermissions.authDomainName;
    const domainId = userPermissions.authDomainId;
    const comId = userPermissions.authComId;
    const comName = userPermissions.authComName;
    if (domainId == null && domainName == null && comId == null && comName == null) {
      return null;
    }
    const formatDomain = () => {
      if (domainId == null) {
        return '-';
      }
      const name = domainName && String(domainName).trim();
      return name ? `${name} (${domainId})` : `站点 ID ${domainId}`;
    };
    const formatCompany = () => {
      if (comId === 0 || comId === '0') {
        return comName || '平台门户';
      }
      const name = comName && String(comName).trim();
      if (name) {
        return `${name} (${comId})`;
      }
      if (comId != null) {
        return `公司 ID ${comId}`;
      }
      return '-';
    };
    return (
      <Card size="small" title="有效权限上下文" style={{ marginBottom: 16 }}>
        <Space split={<Divider type="vertical" />} wrap>
          <span>
            <strong>站点：</strong>
            {formatDomain()}
          </span>
          <span>
            <strong>公司：</strong>
            {formatCompany()}
          </span>
        </Space>
      </Card>
    );
  };

  const renderUserInfo = (userInfo) => {
    if (!userInfo) return null;
    const loginName = userInfo.loginName || userInfo.login_name || userInfo.account;
    const domainId = userInfo.domainId ?? userInfo.domain_id;
    const isReal = userInfo.isReal ?? userInfo.is_real;

    return (
      <Card size="small" title="用户信息">
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <div>
            <strong>登录名：</strong>{loginName || '-'}
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
              {userInfo.status === 'normal' ? '正常'
                : userInfo.status === 'notActive' ? '待激活'
                  : userInfo.status === 'locked' ? '已锁定'
                    : userInfo.status === 'cancelled' ? '已注销'
                      : userInfo.status || '未知'}
            </Tag>
          </div>
          <div>
            <strong>实名认证：</strong>
            <Tag color={isReal === '1' || isReal === 1 ? 'green' : 'orange'}>
              {isReal === '1' || isReal === 1 ? '已认证' : '未认证'}
            </Tag>
          </div>
          <div>
            <strong>域ID：</strong>{domainId || '无'}
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

  const orgRoleColumns = [
    {
      title: '组织',
      dataIndex: 'orgName',
      key: 'orgName',
      render: (text, record) => (
        <Space>
          <TeamOutlined />
          {text || '-'}
          {record.orgCode ? <Text type="secondary" style={{ fontSize: 12 }}>({record.orgCode})</Text> : null}
        </Space>
      ),
    },
    {
      title: '授予的角色',
      dataIndex: 'roles',
      key: 'roles',
      render: (roles) => {
        if (!roles || roles.length === 0) {
          return <Text type="secondary">无角色</Text>;
        }
        return (
          <Space wrap>
            {roles.map((role) => (
              <Tag key={role.roleId} color="blue">{role.roleName || role.roleCode}</Tag>
            ))}
          </Space>
        );
      },
    },
    ...(writable ? [{
      title: '操作',
      key: 'action',
      width: 90,
      render: (_, record) => (
        <Popconfirm title={`确定把该用户移出「${record.orgName}」？其经该组织获得的角色将一并失效`} onConfirm={() => handleRemoveOrg(record)}>
          <a>移出</a>
        </Popconfirm>
      ),
    }] : []),
  ];

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
            {renderAuthContext()}
            {renderUserInfo(userPermissions.userInfo)}

            {userPermissions.currentAuthority && userPermissions.currentAuthority.length > 0 && (
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
                {renderMenuPermissions(
                  userPermissions.menu || [],
                  '无用户端菜单。常见原因：未挂站点会员组（FREEUSER）且所属机构未授权；或管理员改状态为正常但未迁入会员组（请重新保存状态或走邮箱激活）。',
                )}
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
              {renderMenuPermissions(
                adminPermissions,
                '暂无管理端菜单。',
              )}
            </Card>
          </TabPane>

          <TabPane
            tab={
              <Space>
                <TeamOutlined />
                归属与角色
              </Space>
            }
            key="orgRoles"
          >
            <Card
              size="small"
              title="归属组织与角色来源"
              extra={writable ? (
                <Button
                  size="small"
                  type="primary"
                  icon={<PlusOutlined />}
                  onClick={() => {
                    setSelectedOrgId(null);
                    setAddOrgVisible(true);
                  }}
                >
                  加入组织
                </Button>
              ) : null}
            >
              <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
                角色通过组织挂接到人：加入某组织即获得该组织授予的全部角色，移出即失效。
              </Text>
              <Table
                columns={orgRoleColumns}
                dataSource={orgRoles}
                rowKey="orgId"
                pagination={false}
                size="small"
                loading={orgActionLoading}
                locale={{ emptyText: '未归属任何组织' }}
              />
            </Card>

            <Card size="small" title="主体角色" style={{ marginTop: 16 }}>
              <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
                经主体认证（用户 ↔ 主体类型）与主体授权（主体类型 ↔ 角色）获得的角色，在主体管理中维护。
              </Text>
              {subjectRoles.length === 0 ? (
                <Text type="secondary">无主体角色</Text>
              ) : (
                <Space wrap>
                  {subjectRoles.map((role) => (
                    <Tag key={role.roleId} color="purple">{role.roleName || role.roleCode}</Tag>
                  ))}
                </Space>
              )}
            </Card>

            <Card size="small" title="身份角色" style={{ marginTop: 16 }}>
              <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
                经身份登记册所属身份组（超管/租户管/站点管/守信人）挂接的角色，在身份登记册与站点管理中维护。
              </Text>
              {identityRoles.length === 0 ? (
                <Text type="secondary">无身份角色</Text>
              ) : (
                <Space wrap>
                  {identityRoles.map((role) => (
                    <Tag key={role.roleId} color="gold">{role.roleName || role.roleCode}</Tag>
                  ))}
                </Space>
              )}
            </Card>
          </TabPane>

          <TabPane
            tab={
              <Space>
                <LinkOutlined />
                直接授权
              </Space>
            }
            key="directRoles"
          >
            <Card
              size="small"
              title="直挂角色"
              extra={writable ? (
                <Button size="small" type="primary" icon={<PlusOutlined />} onClick={openBindRoleModal}>
                  直挂角色
                </Button>
              ) : null}
            >
              <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
                直挂角色不经组织直接挂到人，立即生效；解绑立即回收。
              </Text>
              <Table
                columns={[
                  {
                    title: '角色',
                    dataIndex: 'roleName',
                    key: 'roleName',
                    render: (text, record) => (
                      <Space>
                        {text || '-'}
                        {record.roleCode ? <Text type="secondary" style={{ fontSize: 12 }}>({record.roleCode})</Text> : null}
                      </Space>
                    ),
                  },
                  ...(writable ? [{
                    title: '操作',
                    key: 'action',
                    width: 90,
                    render: (_, record) => (
                      <Popconfirm title={`确定解绑直挂角色「${record.roleName || record.roleCode}」？`} onConfirm={() => handleUnbindRole(record)}>
                        <a>解绑</a>
                      </Popconfirm>
                    ),
                  }] : []),
                ]}
                dataSource={directRoles}
                rowKey="roleId"
                pagination={false}
                size="small"
                loading={roleActionLoading}
                locale={{ emptyText: '无直挂角色' }}
              />
            </Card>
          </TabPane>
        </Tabs>
      </Spin>

      <Modal
        title="加入组织"
        open={addOrgVisible}
        onOk={handleAddOrg}
        onCancel={() => {
          setAddOrgVisible(false);
          setSelectedOrgId(null);
        }}
        confirmLoading={orgActionLoading}
        destroyOnClose
      >
        <div style={{ marginBottom: 8 }}>
          <Text type="secondary">选择要加入的组织，该用户将获得此组织授予的全部角色。</Text>
        </div>
        <OrgTreeSelect
          placeholder="请选择组织"
          value={selectedOrgId}
          onChange={setSelectedOrgId}
          style={{ width: '100%' }}
        />
      </Modal>

      <Modal
        title="直挂角色"
        open={bindRoleVisible}
        onOk={handleBindRole}
        onCancel={() => {
          setBindRoleVisible(false);
          setSelectedRoleId(null);
        }}
        confirmLoading={roleActionLoading}
        destroyOnClose
      >
        <div style={{ marginBottom: 8 }}>
          <Text type="secondary">选择要直挂的角色，该用户立即获得此角色的全部授权。</Text>
        </div>
        <Select
          placeholder="请选择角色"
          options={roleOptions}
          value={selectedRoleId}
          onChange={setSelectedRoleId}
          showSearch
          optionFilterProp="label"
          style={{ width: '100%' }}
        />
      </Modal>
    </Modal>
  );
};

export default UserPermissionView;
