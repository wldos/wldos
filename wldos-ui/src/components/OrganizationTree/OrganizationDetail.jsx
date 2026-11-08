/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect } from 'react';
import { Button, message, Descriptions, Tag, Typography, Space } from 'antd';
import { useIntl } from 'umi';
import { fetchUsersByOrganization } from '@/services/organization';
import CompanyDetail from './CompanyDetail';
import SystemDetail from './SystemDetail';
import OrganizationDetailContent from './OrganizationDetailContent';
import UserList from './UserList';

const { Title } = Typography;

const OrganizationDetail = ({
  selectedNode,
  treeData,
  onCompanyUpdate,
  onCompanyAdd,
  onSystemAdd,
  onCompanyDelete,
  onSystemUpdate,
  onOrganizationAdd,
  onSystemDelete,
  onOrganizationUpdate,
  onMembersChanged,
  onOrganizationDelete,
  comOptions,
  orgTypeOptions,
  archOptionsForOrg,
  orgOptionsForParent,
  hasAuthority = () => true,
  targetDomainId,
  domainOptions = [],
  showDomainSelect = false,
  onTargetDomainChange,
  allDomainRows = [],
}) => {
  const intl = useIntl();
  const [showUsers, setShowUsers] = useState(false);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [usersLoaded, setUsersLoaded] = useState(false);
  useEffect(() => {
    setShowUsers(false);
    setUsers([]);
    setUsersLoaded(false);
  }, [selectedNode?.type, selectedNode?.id]);

  useEffect(() => {
    if (selectedNode && selectedNode.type === 'organization' && showUsers) {
      loadUsers();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedNode, showUsers]);

  if (!selectedNode) {
    return (
      <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
        {intl.formatMessage({ id: 'component.organizationTree.detail.selectNode' })}
      </div>
    );
  }

  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await fetchUsersByOrganization(selectedNode.id);
      let userData = [];
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          userData = response.data;
        } else if (response.data.rows && Array.isArray(response.data.rows)) {
          userData = response.data.rows;
        }
      }
      setUsers(userData);
      setUsersLoaded(true);
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadUsersFail' }));
      setUsersLoaded(false);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleShowUsers = () => {
    setUsersLoaded(false);
    setShowUsers(true);
  };

  const handleMembersChanged = () => {
    if (onMembersChanged) onMembersChanged();
    if (showUsers) {
      setUsersLoaded(false);
      loadUsers();
    }
  };

  const findCompanyName = (comId) => {
    if (!treeData || !comId) return '-';
    for (const company of treeData) {
      if (company.id === comId) {
        return company.comName || company.name || '-';
      }
    }
    return '-';
  };

  const findSystemName = (archId) => {
    if (!treeData || !archId) return '-';
    for (const company of treeData) {
      if (company.children) {
        for (const system of company.children) {
          if (system.id === archId) {
            return system.archName || system.name || '-';
          }
        }
      }
    }
    return '-';
  };

  const findOrgName = (orgId) => {
    if (!treeData || !orgId) return '-';
    for (const company of treeData) {
      if (!company.children) continue;
      for (const system of company.children) {
        if (!system.children) continue;
        for (const org of system.children) {
          if (org.id === orgId) {
            return org.orgName || org.name || '-';
          }
        }
      }
    }
    return '-';
  };

  const getNodeDisplayName = (node) => {
    switch (node.type) {
      case 'company':
        return node.comName || intl.formatMessage({ id: 'component.organizationTree.unknown.company' });
      case 'system':
        return node.archName || intl.formatMessage({ id: 'component.organizationTree.unknown.system' });
      case 'organization':
        return node.orgName || intl.formatMessage({ id: 'component.organizationTree.unknown.organization' });
      case 'user':
        return node.nickname || intl.formatMessage({ id: 'component.organizationTree.unknown.user' });
      default:
        return node.name || intl.formatMessage({ id: 'component.organizationTree.unknown.default' });
    }
  };

  const getNodeTypeLabel = (type) => {
    const keyMap = {
      company: 'component.organizationTree.nodeType.company',
      system: 'component.organizationTree.nodeType.system',
      organization: 'component.organizationTree.nodeType.organization',
      user: 'component.organizationTree.nodeType.user',
    };
    return intl.formatMessage({ id: keyMap[type] || 'component.organizationTree.unknown.default' });
  };

  const renderDetail = () => {
    switch (selectedNode.type) {
      case 'company':
        return (
          <CompanyDetail
            company={selectedNode}
            onCompanyUpdate={onCompanyUpdate}
            onCompanyAdd={onCompanyAdd}
            onSystemAdd={onSystemAdd}
            onCompanyDelete={onCompanyDelete}
            comOptions={comOptions}
            parentCompanyName={findCompanyName(selectedNode.parentId)}
          />
        );
      case 'system': {
        const systemCompanyName = findCompanyName(selectedNode.comId);
        const isTopSystem = selectedNode.parentId == null || String(selectedNode.parentId) === '0';
        const parentSystemName = isTopSystem
          ? intl.formatMessage({ id: 'component.organizationTree.system.detail.parent.top' })
          : findSystemName(selectedNode.parentId);
        return (
          <SystemDetail
            system={selectedNode}
            companyName={systemCompanyName}
            parentSystemName={parentSystemName}
            onSystemUpdate={onSystemUpdate}
            onSystemAdd={onSystemAdd}
            onOrganizationAdd={onOrganizationAdd}
            onSystemDelete={onSystemDelete}
            comOptions={comOptions}
            orgTypeOptions={orgTypeOptions}
            archOptionsForOrg={archOptionsForOrg}
            orgOptionsForParent={orgOptionsForParent}
          />
        );
      }
      case 'organization': {
        const companyName = findCompanyName(selectedNode.comId);
        const systemName = findSystemName(selectedNode.archId);
        const isTopOrg = selectedNode.parentId == null || String(selectedNode.parentId) === '0';
        const parentOrgName = isTopOrg
          ? intl.formatMessage({ id: 'component.organizationTree.org.detail.parent.top' })
          : findOrgName(selectedNode.parentId);
        return (
          <>
            <OrganizationDetailContent
              organization={selectedNode}
              onToggleShowUsers={handleToggleShowUsers}
              companyName={companyName}
              systemName={systemName}
              parentOrgName={parentOrgName}
              userCount={users.length}
              showUsers={showUsers}
              usersLoaded={usersLoaded}
              onOrganizationUpdate={onOrganizationUpdate}
              onOrganizationAdd={onOrganizationAdd}
              onMembersChanged={handleMembersChanged}
              onOrganizationDelete={onOrganizationDelete}
              orgTypeOptions={orgTypeOptions}
              archOptionsForOrg={archOptionsForOrg}
              orgOptionsForParent={orgOptionsForParent}
              comOptions={comOptions}
              hasAuthority={hasAuthority}
              targetDomainId={targetDomainId}
              domainOptions={domainOptions}
              showDomainSelect={showDomainSelect}
              onTargetDomainChange={onTargetDomainChange}
              allDomainRows={allDomainRows}
            />
            {showUsers && (
              <div style={{ marginTop: 16 }}>
                <UserList
                  users={users}
                  loading={loading}
                  organizationId={selectedNode.id}
                  onMembersChanged={handleMembersChanged}
                  hasAuthority={hasAuthority}
                />
              </div>
            )}
          </>
        );
      }
      case 'user':
        return (
          <Descriptions bordered column={2}>
            <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.userList.col.nickname' })}>
              {selectedNode.nickname || selectedNode.account || '-'}
            </Descriptions.Item>
            <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.userList.col.loginName' })}>
              {selectedNode.login_name || selectedNode.account || '-'}
            </Descriptions.Item>
            <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.userList.col.email' })}>
              {selectedNode.email || '-'}
            </Descriptions.Item>
            <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.userList.col.status' })}>
              <Tag color={selectedNode.status === '正常' || selectedNode.status === 'normal' ? 'green' : 'red'}>
                {selectedNode.status || '-'}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        );
      default:
        return <div>{intl.formatMessage({ id: 'component.organizationTree.unknown.type' })}</div>;
    }
  };

  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          flexWrap: 'wrap',
          gap: 12,
          marginBottom: 16,
        }}
      >
        <Space align="center" wrap>
          <Title level={5} style={{ margin: 0 }}>
            {getNodeDisplayName(selectedNode)}
          </Title>
          <Tag>{getNodeTypeLabel(selectedNode.type)}</Tag>
        </Space>
        {selectedNode.type === 'organization' && (
          <Button type="primary" onClick={() => setShowUsers(!showUsers)}>
            {showUsers
              ? intl.formatMessage({ id: 'component.organizationTree.detail.hideUsers' })
              : intl.formatMessage({ id: 'component.organizationTree.detail.showUsers' })}
          </Button>
        )}
      </div>
      {renderDetail()}
    </div>
  );
};

export default OrganizationDetail;
