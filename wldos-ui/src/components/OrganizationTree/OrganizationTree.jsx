/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect, useRef } from 'react';
import { Tree, Spin, Badge, message } from 'antd';
import { useIntl } from 'umi';
import { fetchOrganizationData, fetchUsersByOrganization, loadSystemsByCompany, loadOrganizationsBySystem } from '@/services/organization';

function extractRows(res) {
  if (!res) return [];
  const data = res.data;
  if (Array.isArray(data)) return data;
  if (data?.rows && Array.isArray(data.rows)) return data.rows;
  return [];
}

function parseExpandedNodeId(key, typePrefix) {
  if (!key || !String(key).startsWith(`${typePrefix}-`)) return null;
  return String(key).slice(typePrefix.length + 1);
}

function mergeChildrenIntoTree(nodes, parentId, children, parentType) {
  return nodes.map((node) => {
    const typeMatch = !parentType || node.type === parentType;
    if (typeMatch && String(node.id) === String(parentId)) {
      return { ...node, children };
    }
    if (node.children?.length) {
      return { ...node, children: mergeChildrenIntoTree(node.children, parentId, children, parentType) };
    }
    return node;
  });
}

function findTreeNodeRaw(nodes, type, id) {
  if (!Array.isArray(nodes) || !nodes.length) {
    return null;
  }
  for (const node of nodes) {
    if (node.type === type && String(node.id) === String(id)) {
      return node;
    }
    const found = findTreeNodeRaw(node.children, type, id);
    if (found) {
      return found;
    }
  }
  return null;
}

const OrganizationTree = ({
  onSelect,
  selectedKey,
  showUsers = false,
  onTreeDataChange,
  reloadToken,
  focusTarget,
  onFocusApplied,
}) => {
  const intl = useIntl();
  const [treeData, setTreeData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [expandedKeys, setExpandedKeys] = useState([]);
  const expandedKeysRef = useRef(expandedKeys);
  const showUsersRef = useRef(showUsers);
  const selectedKeyRef = useRef(selectedKey);
  const focusAppliedRef = useRef(null);
  const focusTargetRef = useRef(focusTarget);

  expandedKeysRef.current = expandedKeys;
  showUsersRef.current = showUsers;
  selectedKeyRef.current = selectedKey;
  focusTargetRef.current = focusTarget;

  useEffect(() => {
    if (onTreeDataChange) {
      onTreeDataChange(treeData);
    }
  }, [treeData, onTreeDataChange]);

  const resolveBaseTreeForFocus = async (baseTree, target) => {
    if (!target?.type || target.type === 'company' || target.comId == null) {
      return baseTree;
    }
    if (findTreeNodeRaw(baseTree, 'company', target.comId)) {
      return baseTree;
    }
    return fetchOrganizationData();
  };

  const applyFocusPath = async (baseTree, target) => {
    if (!target?.type || target.id == null || target.id === '') {
      return { tree: baseTree, expandKeys: null, selectNode: null };
    }
    let tree = await resolveBaseTreeForFocus(baseTree, target);
    let expandKeys = null;
    let selectNode = null;
    if (target.type === 'organization' && target.comId != null && target.archId != null) {
      const systems = await loadSystemsByCompany(target.comId);
      tree = mergeChildrenIntoTree(tree, target.comId, systems, 'company');
      const organizations = await loadOrganizationsBySystem(target.archId);
      tree = mergeChildrenIntoTree(tree, target.archId, organizations, 'system');
      expandKeys = [`company-${target.comId}`, `system-${target.archId}`];
      selectNode = findTreeNodeRaw(tree, 'organization', target.id);
    } else if (target.type === 'company') {
      selectNode = findTreeNodeRaw(tree, 'company', target.id);
    } else if (target.type === 'system' && target.comId != null) {
      const systems = await loadSystemsByCompany(target.comId);
      tree = mergeChildrenIntoTree(tree, target.comId, systems, 'company');
      expandKeys = [`company-${target.comId}`];
      selectNode = findTreeNodeRaw(tree, 'system', target.id);
    }
    return { tree, expandKeys, selectNode };
  };

  const commitFocusSelection = (target, selectNode, expandKeys) => {
    if (expandKeys) {
      setExpandedKeys(expandKeys);
      expandedKeysRef.current = expandKeys;
    }
    if (selectNode && onSelect) {
      onSelect([`${selectNode.type}-${selectNode.id}`], { node: { node: selectNode } });
    } else if (target && !selectNode) {
      message.warning(intl.formatMessage({ id: 'component.organizationTree.msg.focusNotFound' }));
    }
    if (target && selectNode) {
      focusAppliedRef.current = `${target.type}-${target.id}`;
    }
    if (target && onFocusApplied) {
      onFocusApplied();
    }
  };

  const rehydrateExpandedChildren = async (baseTree, keys, loadUserNodes, activeSelectedKey) => {
    let tree = baseTree;
    const systemIdSet = new Set(
      keys
        .map((key) => parseExpandedNodeId(key, 'system'))
        .filter((id) => id != null),
    );
    const selectedSystemId = parseExpandedNodeId(activeSelectedKey, 'system');
    if (selectedSystemId) {
      systemIdSet.add(selectedSystemId);
    }
    for (const systemId of systemIdSet) {
      const organizations = await loadOrganizationsBySystem(systemId);
      tree = mergeChildrenIntoTree(tree, systemId, organizations, 'system');
    }
    const orgIdSet = new Set(
      keys
        .map((key) => parseExpandedNodeId(key, 'organization'))
        .filter((id) => id != null),
    );
    const selectedOrgId = parseExpandedNodeId(activeSelectedKey, 'organization');
    if (selectedOrgId) {
      orgIdSet.add(selectedOrgId);
    }
    if (loadUserNodes) {
      for (const orgId of orgIdSet) {
        const res = await fetchUsersByOrganization(orgId);
        const users = extractRows(res).map((user) => ({
          ...user,
          type: 'user',
          children: [],
        }));
        tree = mergeChildrenIntoTree(tree, orgId, users, 'organization');
      }
    }
    return tree;
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await fetchOrganizationData();
      const activeSelectedKey = selectedKeyRef.current;
      const selectedSystemId = parseExpandedNodeId(activeSelectedKey, 'system');
      let keysForRehydrate = expandedKeysRef.current;
      if (selectedSystemId) {
        const systemKey = `system-${selectedSystemId}`;
        if (!keysForRehydrate.includes(systemKey)) {
          keysForRehydrate = [...keysForRehydrate, systemKey];
        }
      }
      const hydrated = await rehydrateExpandedChildren(
        data,
        keysForRehydrate,
        showUsersRef.current,
        activeSelectedKey,
      );
      let tree = hydrated;
      const target = focusTargetRef.current;
      if (target) {
        const focused = await applyFocusPath(hydrated, target);
        tree = focused.tree;
        setTreeData(tree);
        commitFocusSelection(target, focused.selectNode, focused.expandKeys);
      } else {
        setTreeData(tree);
      }
      if (selectedSystemId && !target) {
        const systemKey = `system-${selectedSystemId}`;
        setExpandedKeys((prev) => (prev.includes(systemKey) ? prev : [...prev, systemKey]));
      }
      return tree;
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadTreeFail' }));
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // reloadToken 每次变化都从后端重新拉取完整树；父级增删改后自增该值即可触发刷新
  useEffect(() => {
    focusAppliedRef.current = null;
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [reloadToken]);

  useEffect(() => {
    if (!focusTarget?.type || focusTarget.id == null || focusTarget.id === '') {
      return undefined;
    }
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const baseTree = await fetchOrganizationData();
        if (cancelled) {
          return;
        }
        const focused = await applyFocusPath(baseTree, focusTarget);
        if (cancelled) {
          return;
        }
        setTreeData(focused.tree);
        commitFocusSelection(focusTarget, focused.selectNode, focused.expandKeys);
      } catch (error) {
        if (!cancelled) {
          message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadTreeFail' }));
        }
      } finally {
        setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [focusTarget]);

  const loadUsers = async (organizationId) => {
    try {
      const res = await fetchUsersByOrganization(organizationId);
      const users = extractRows(res).map((user) => ({
        ...user,
        type: 'user',
        children: [],
      }));
      setTreeData((prev) => mergeChildrenIntoTree(prev, organizationId, users, 'organization'));
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadUsersFail' }));
    }
  };

  const getNodeIcon = (type) => {
    const iconMap = {
      company: '🏢',
      system: '🏭',
      organization: '🏛️',
      user: '👤',
    };
    return iconMap[type] || '📁';
  };

  const renderTreeNode = (node) => {
    const icon = getNodeIcon(node.type);

    const getNodeName = (nodeItem) => {
      switch (nodeItem.type) {
        case 'company':
          return nodeItem.comName || intl.formatMessage({ id: 'component.organizationTree.unknown.company' });
        case 'system':
          return nodeItem.archName || intl.formatMessage({ id: 'component.organizationTree.unknown.system' });
        case 'organization':
          return nodeItem.orgName || intl.formatMessage({ id: 'component.organizationTree.unknown.organization' });
        case 'user':
          return nodeItem.nickname || nodeItem.account || nodeItem.name || intl.formatMessage({ id: 'component.organizationTree.unknown.user' });
        default:
          return nodeItem.name || intl.formatMessage({ id: 'component.organizationTree.unknown.default' });
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
      node,
      isLeaf: node.type === 'organization' && !showUsers,
      children: node.children ? node.children.map(renderTreeNode) : undefined,
    };
  };

  const handleExpand = async (nextExpandedKeys, { node }) => {
    setExpandedKeys(nextExpandedKeys);

    const nodeData = node.node || node;

    if (nodeData.type === 'company' && !nodeData.children?.length) {
      await loadSystemsForCompany(nodeData.id);
    } else if (nodeData.type === 'system' && !nodeData.children?.length) {
      await loadOrganizationsForSystem(nodeData.id);
    } else if (nodeData.type === 'organization' && showUsers && !nodeData.children?.length) {
      await loadUsers(nodeData.id);
    }
  };

  const loadSystemsForCompany = async (companyId) => {
    try {
      const systems = await loadSystemsByCompany(companyId);
      setTreeData((prev) => mergeChildrenIntoTree(prev, companyId, systems, 'company'));
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadSystemsFail' }));
    }
  };

  const loadOrganizationsForSystem = async (systemId) => {
    try {
      const organizations = await loadOrganizationsBySystem(systemId);
      setTreeData((prev) => mergeChildrenIntoTree(prev, systemId, organizations, 'system'));
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.msg.loadOrganizationsFail' }));
    }
  };

  const treeDataForRender = treeData.map(renderTreeNode);

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
        defaultExpandAll={false}
      />
    </Spin>
  );
};

export default OrganizationTree;
