/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { TreeSelect } from 'antd';
import { queryPage as queryOrgPage } from '@/pages/sys/org/service';
import { normalizeOrgNode } from '@/utils/tree';

const { SHOW_PARENT } = TreeSelect;

function treeContainsValue(nodes, target) {
  if (target == null || !nodes?.length) {
    return false;
  }
  const targetStr = String(target);
  for (const node of nodes) {
    if (String(node.value) === targetStr) {
      return true;
    }
    if (node.children?.length && treeContainsValue(node.children, target)) {
      return true;
    }
  }
  return false;
}

export default function OrgTreeSelect({
  value,
  onChange,
  comId,
  domainId,
  category,
  seedOption,
  multiple = false,
  placeholder = '请选择组织',
  style,
  allowClear = true,
  treeCheckable = false,
  treeDefaultExpandAll = true,
}) {
  const [treeData, setTreeData] = useState([]);
  const [loading, setLoading] = useState(false);

  const mapTree = useCallback((nodes = []) => {
    return nodes.map((n) => ({
      ...normalizeOrgNode(n),
      children: Array.isArray(n.children) ? mapTree(n.children) : undefined,
    }));
  }, []);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const params = { current: 1, pageSize: 1000 };
      if (comId != null) {
        params.comId = comId;
      }
      if (domainId != null) {
        params.domainId = domainId;
      }
      if (category != null) {
        params.category = category;
      }
      const res = await queryOrgPage(params);
      const list = res?.data?.rows || [];
      setTreeData(mapTree(list));
    } finally {
      setLoading(false);
    }
  }, [comId, domainId, category, mapTree]);

  useEffect(() => {
    loadAll();
  }, [loadAll]);

  const displayTreeData = useMemo(() => {
    if (!seedOption?.value || !seedOption?.label) {
      return treeData;
    }
    if (treeContainsValue(treeData, seedOption.value)) {
      return treeData;
    }
    return [
      {
        title: seedOption.label,
        value: seedOption.value,
        key: seedOption.value,
        disabled: false,
        isLeaf: true,
      },
      ...treeData,
    ];
  }, [treeData, seedOption]);

  return (
    <TreeSelect
      value={value}
      onChange={onChange}
      treeData={displayTreeData}
      multiple={multiple}
      treeCheckable={treeCheckable}
      showCheckedStrategy={SHOW_PARENT}
      placeholder={placeholder}
      style={style}
      allowClear={allowClear}
      treeDefaultExpandAll={treeDefaultExpandAll}
      loading={loading}
      showSearch
      treeNodeFilterProp="title"
      dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
    />
  );
}
