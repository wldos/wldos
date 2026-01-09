/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect } from 'react';
import { Card, Button, Switch, Spin, message } from 'antd';
import { fetchUsersByOrganization } from '@/services/organization';
import CompanyDetail from './CompanyDetail';
import SystemDetail from './SystemDetail';
import OrganizationDetailContent from './OrganizationDetailContent';
import UserList from './UserList';

const OrganizationDetail = ({
  selectedNode,
  treeData,
  onCompanyUpdate,
  onSystemAdd,
  onCompanyDelete,
  onSystemUpdate,
  onOrganizationAdd,
  onSystemDelete,
  onOrganizationUpdate,
  onUserAdd,
  onPermissionSettings,
  onOrganizationDelete
}) => {
  const [showUsers, setShowUsers] = useState(false);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [usersLoaded, setUsersLoaded] = useState(false);

  // 当选中机构节点时，加载用户数据
  useEffect(() => {
    if (selectedNode && selectedNode.type === 'organization' && showUsers) {
      loadUsers();
    }
  }, [selectedNode, showUsers]);

  if (!selectedNode) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
          请选择组织节点
        </div>
      </Card>
    );
  }

  const loadUsers = async () => {
    setLoading(true);
    try {
      console.log('开始加载用户数据，组织ID:', selectedNode.id, '组织名称:', selectedNode.orgName || selectedNode.name);
      const response = await fetchUsersByOrganization(selectedNode.id);
      console.log('用户数据响应:', response);

      // 确保数据是数组格式
      let userData = [];
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          userData = response.data;
        } else if (response.data.rows && Array.isArray(response.data.rows)) {
          userData = response.data.rows;
        }
      }

      console.log('处理后的用户数据:', userData);
      console.log('用户数据数量:', userData.length);

      // 检查用户数据是否属于当前组织
      if (userData.length > 0) {
        console.log('第一个用户的组织信息:', {
          userId: userData[0].id,
          userName: userData[0].userName,
          organizationId: userData[0].organizationId,
          currentOrgId: selectedNode.id
        });
      }

      setUsers(userData);
      setUsersLoaded(true);
    } catch (error) {
      console.error('加载用户数据失败:', error);
      message.error('加载用户数据失败');
      setUsersLoaded(false);
    } finally {
      setLoading(false);
    }
  };

  const handleAddUser = (data) => {
    console.log('添加用户功能被调用:', data);
    // TODO: 实现添加用户的具体逻辑
    // 这里可以打开添加用户的模态框或跳转到用户管理页面
    message.info('添加用户功能待实现');
  };

  const handleShowUsers = () => {
    console.log('显示用户按钮被点击');
    setUsersLoaded(false); // 重置用户数据加载状态
    setShowUsers(true);
  };

  // 根据ID查找公司名称
  const findCompanyName = (comId) => {
    if (!treeData || !comId) return '-';

    for (const company of treeData) {
      if (company.id === comId) {
        return company.comName || company.name || '-';
      }
    }
    return '-';
  };

  // 根据ID查找体系名称
  const findSystemName = (archId) => {
    console.log('查找体系名称，archId:', archId, 'treeData:', treeData);
    if (!treeData || !archId) return '-';

    for (const company of treeData) {
      console.log('检查公司:', company.comName, 'children:', company.children);
      if (company.children) {
        for (const system of company.children) {
          console.log('检查体系:', system.archName, 'id:', system.id, '匹配archId:', archId);
          if (system.id === archId) {
            console.log('找到匹配的体系:', system.archName);
            return system.archName || system.name || '-';
          }
        }
      }
    }
    console.log('未找到匹配的体系');
    return '-';
  };

  const renderDetail = () => {
    switch (selectedNode.type) {
      case 'company':
        return (
          <CompanyDetail
            company={selectedNode}
            onCompanyUpdate={onCompanyUpdate}
            onSystemAdd={onSystemAdd}
            onCompanyDelete={onCompanyDelete}
          />
        );
      case 'system':
        console.log('渲染体系详情，selectedNode:', selectedNode);
        console.log('comId:', selectedNode.comId);
        const systemCompanyName = findCompanyName(selectedNode.comId);
        console.log('体系解析结果 - 公司名称:', systemCompanyName);
        return (
          <SystemDetail
            system={selectedNode}
            companyName={systemCompanyName}
            onSystemUpdate={onSystemUpdate}
            onOrganizationAdd={onOrganizationAdd}
            onSystemDelete={onSystemDelete}
          />
        );
      case 'organization':
        console.log('渲染机构详情，selectedNode:', selectedNode);
        console.log('comId:', selectedNode.comId, 'archId:', selectedNode.archId);
        const companyName = findCompanyName(selectedNode.comId);
        const systemName = findSystemName(selectedNode.archId);
        console.log('解析结果 - 公司名称:', companyName, '体系名称:', systemName);
        return (
          <div>
            <OrganizationDetailContent
              organization={selectedNode}
              onAddUser={handleShowUsers}
              companyName={companyName}
              systemName={systemName}
              userCount={users.length}
              showUsers={showUsers}
              usersLoaded={usersLoaded}
              onOrganizationUpdate={onOrganizationUpdate}
              onUserAdd={onUserAdd}
              onPermissionSettings={onPermissionSettings}
              onOrganizationDelete={onOrganizationDelete}
            />
            {showUsers && (
              <div style={{ marginTop: 16 }}>
                <UserList
                  users={users}
                  loading={loading}
                  organizationId={selectedNode.id}
                />
              </div>
            )}
          </div>
        );
      case 'user':
        return <UserDetail user={selectedNode} />;
      default:
        return <div>未知类型</div>;
    }
  };

  // 获取节点显示名称
  const getNodeDisplayName = (node) => {
    switch (node.type) {
      case 'company':
        return node.comName || '未知公司';
      case 'system':
        return node.archName || '未知体系';
      case 'organization':
        return node.orgName || '未知机构';
      case 'user':
        return node.nickname || '未知用户';
      default:
        return node.name || '未知';
    }
  };

  return (
    <Card
      title={`${getNodeDisplayName(selectedNode)} - 详情管理`}
      extra={
        selectedNode.type === 'organization' && (
          <Button
            type="primary"
            onClick={() => setShowUsers(!showUsers)}
          >
            {showUsers ? '隐藏用户' : '显示用户'}
          </Button>
        )
      }
    >
      {renderDetail()}
    </Card>
  );
};

export default OrganizationDetail;
