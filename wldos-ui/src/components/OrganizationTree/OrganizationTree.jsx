/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect } from 'react';
import { Tree, Spin, Badge, message } from 'antd';
import { fetchOrganizationData, fetchUsersByOrganization, loadSystemsByCompany, loadOrganizationsBySystem } from '@/services/organization';

const OrganizationTree = ({ onSelect, selectedKey, showUsers = false, onTreeDataChange, externalTreeData }) => {
  const [treeData, setTreeData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [expandedKeys, setExpandedKeys] = useState([]);

  useEffect(() => {
    loadData();
  }, []);

  // 监听外部数据变化
  useEffect(() => {
    if (externalTreeData) {
      if (externalTreeData.length === 0) {
        // 当外部数据为空数组时，重新加载数据
        const currentExpandedKeys = expandedKeys;
        loadData().then(() => {
          // 重新加载后恢复展开状态
          setExpandedKeys(currentExpandedKeys);
        });
      } else {
        // 当外部数据不为空时，直接使用外部数据
        setTreeData(externalTreeData);
      }
    }
  }, [externalTreeData]);

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await fetchOrganizationData();
      setTreeData(data);
      // 通知父组件树形数据已更新
      if (onTreeDataChange) {
        onTreeDataChange(data);
      }
      return data;
    } catch (error) {
      message.error('加载组织架构数据失败');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // 更新树形数据并通知父组件
  const updateTreeData = (newData) => {
    setTreeData(newData);
    if (onTreeDataChange) {
      onTreeDataChange(newData);
    }
  };

  // 按需加载用户数据
  const loadUsers = async (organizationId) => {
    try {
      const users = await fetchUsersByOrganization(organizationId);
      updateTreeWithUsers(organizationId, users);
    } catch (error) {
      message.error('加载用户数据失败');
    }
  };

  // 更新树形数据，添加用户节点
  const updateTreeWithUsers = (organizationId, users) => {
    const updateNode = (nodes) => {
      return nodes.map(node => {
        if (node.type === 'organization' && node.id === organizationId) {
          return {
            ...node,
            children: users.map(user => ({
              ...user,
              type: 'user',
              children: []
            }))
          };
        }
        if (node.children) {
          return {
            ...node,
            children: updateNode(node.children)
          };
        }
        return node;
      });
    };

    updateTreeData(updateNode(treeData));
  };

  const getNodeIcon = (type) => {
    const iconMap = {
      company: '🏢', // 公司 - 独立主体机构/系统租户
      system: '🏭', // 业务体系 - 不同业务专业（市场部、项目部等）
      organization: '🏛️', // 组织机构 - 部门组织树
      user: '👤' // 员工
    };
    return iconMap[type] || '📁';
  };

  const renderTreeNode = (node) => {
    const icon = getNodeIcon(node.type);

    // 根据节点类型获取正确的名称字段
    const getNodeName = (node) => {
      switch (node.type) {
        case 'company':
          return node.comName || '未知公司';
        case 'system':
          return node.archName || '未知体系';
        case 'organization':
          return node.orgName || '未知机构';
        case 'user':
          return node.nickname || node.account || node.name || '未知用户';
        default:
          return node.name || '未知';
      }
    };

    const title = (
      <span>
        {icon} {getNodeName(node)}
        {node.type === 'organization' && node.userCount > 0 && (
          <Badge count={node.userCount} style={{ marginLeft: 8 }} />
        )}
      </span>
    );

    return {
      title,
      key: `${node.type}-${node.id}`,
      node: node,
      isLeaf: node.type === 'organization' && !showUsers,
      children: node.children ? node.children.map(renderTreeNode) : undefined,
    };
  };

  const handleExpand = async (expandedKeys, { node }) => {
    setExpandedKeys(expandedKeys);

    console.log('展开节点:', node);

    // 获取实际的节点数据
    const nodeData = node.node || node;
    console.log('节点数据:', nodeData);

    // 按需加载子级数据
    if (nodeData.type === 'company' && !nodeData.children?.length) {
      console.log('加载公司的体系数据:', nodeData.id);
      // 加载公司的体系数据
      await loadSystemsForCompany(nodeData.id);
    } else if (nodeData.type === 'system' && !nodeData.children?.length) {
      console.log('加载体系的机构数据:', nodeData.id);
      // 加载体系的机构数据
      await loadOrganizationsForSystem(nodeData.id);
    } else if (nodeData.type === 'organization' && showUsers && !nodeData.children?.length) {
      console.log('加载机构的用户数据:', nodeData.id);
      // 加载机构的用户数据
      await loadUsers(nodeData.id);
    }
  };

  // 按需加载体系数据
  const loadSystemsForCompany = async (companyId) => {
    try {
      console.log('开始加载体系数据，公司ID:', companyId);
      const systems = await loadSystemsByCompany(companyId);
      console.log('获取到的体系数据:', systems);
      updateTreeWithChildren(companyId, systems);
    } catch (error) {
      console.error('加载体系数据失败:', error);
      message.error('加载体系数据失败');
    }
  };

  // 按需加载机构数据
  const loadOrganizationsForSystem = async (systemId) => {
    try {
      console.log('开始加载机构数据，体系ID:', systemId);
      const organizations = await loadOrganizationsBySystem(systemId);
      console.log('获取到的机构数据:', organizations);
      updateTreeWithChildren(systemId, organizations);
    } catch (error) {
      console.error('加载机构数据失败:', error);
      message.error('加载机构数据失败');
    }
  };

  // 更新树形数据，添加子级节点
  const updateTreeWithChildren = (parentId, children) => {
    console.log('更新树形数据，父节点ID:', parentId, '子节点数据:', children);
    console.log('当前树形数据:', treeData);

    const updateNode = (nodes) => {
      return nodes.map(node => {
        console.log('检查节点:', node.id, '是否匹配父节点ID:', parentId);
        if (node.id === parentId) {
          console.log('找到匹配的父节点，添加子节点');
          return {
            ...node,
            children: children
          };
        }
        if (node.children) {
          return {
            ...node,
            children: updateNode(node.children)
          };
        }
        return node;
      });
    };

    const newTreeData = updateNode(treeData);
    console.log('更新后的树形数据:', newTreeData);
    console.log('更新后的树形数据长度:', newTreeData.length);
    console.log('第一个节点的子节点:', newTreeData[1]?.children);
    updateTreeData([...newTreeData]); // 强制创建新数组引用
  };

  // 调试：检查树形数据
  const treeDataForRender = treeData.map(renderTreeNode);
  console.log('渲染前的树形数据:', treeData);
  console.log('渲染用的树形数据:', treeDataForRender);
  console.log('展开的键:', expandedKeys);

  return (
    <Spin spinning={loading}>
      <Tree
        treeData={treeDataForRender}
        onSelect={onSelect}
        onExpand={handleExpand}
        selectedKeys={selectedKey ? [selectedKey] : []}
        expandedKeys={expandedKeys}
        showLine
        showIcon
        defaultExpandAll={false} // 默认不展开，避免一次性加载过多数据
      />
    </Spin>
  );
};

export default OrganizationTree;
