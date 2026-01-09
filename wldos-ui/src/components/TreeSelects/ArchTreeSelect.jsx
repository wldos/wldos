/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { TreeSelect } from 'antd';
import { queryPage as queryArchPage } from '@/pages/sys/arch/service';
import { normalizeArchNode } from '@/utils/tree';

const { SHOW_PARENT } = TreeSelect;

export default function ArchTreeSelect({
  value,
  onChange,
  companyId, // 可选：传入后按公司过滤
  includeTop = true, // 是否包含 顶级体系(0)
  multiple = false,
  placeholder = '请选择体系',
  style,
  allowClear = true,
  treeCheckable = false,
  treeDefaultExpandAll = false,
  usePaged = false,
}) {
  const [rawTree, setRawTree] = useState([]);
  const [loading, setLoading] = useState(false);

  const mapTree = useCallback((nodes = []) => {
    return nodes.map((n) => ({
      ...normalizeArchNode(n),
      children: Array.isArray(n.children) ? mapTree(n.children) : undefined,
    }));
  }, []);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      // 使用分页查询API获取所有体系数据
      const res = await queryArchPage({ current: 1, pageSize: 1000 });
      const list = res?.data?.rows || [];
      setRawTree(list);
    } finally {
      setLoading(false);
    }
  }, []);

  const treeData = useMemo(() => {
    let src = rawTree;

    // 如果指定了公司ID，过滤出属于同一公司的体系树
    if (companyId) {
      const filterByCom = (nodes) => {
        const result = [];
        for (const n of nodes) {
          const matchSelf = String(n.comId || '') === String(companyId);
          const children = Array.isArray(n.children) ? filterByCom(n.children) : undefined;
          if (matchSelf || (children && children.length > 0)) {
            result.push({ ...n, children });
          }
        }
        return result;
      };
      src = filterByCom(rawTree);
    }

    const mapped = mapTree(src);
    if (includeTop) {
      return [{ title: '顶级体系', value: 0, key: 0, isLeaf: true }, ...mapped];
    }
    return mapped;
  }, [rawTree, companyId, includeTop, mapTree]);

  const onLoadData = async (node) => {
    if (!usePaged) return Promise.resolve();
    const hasChildren = Array.isArray(node.children) && node.children.length > 0;
    if (hasChildren) return Promise.resolve();
    const res = await queryArchPage({ parentId: node.value, current: 1, pageSize: 1000 });
    const rows = res?.data?.rows || [];
    node.children = rows.map((r) => normalizeArchNode(r));
    return Promise.resolve();
  };

  useEffect(() => {
    loadAll();
  }, [loadAll]);

  return (
    <TreeSelect
      value={value}
      onChange={onChange}
      treeData={treeData}
      multiple={multiple}
      treeCheckable={treeCheckable}
      showCheckedStrategy={SHOW_PARENT}
      placeholder={placeholder}
      style={style}
      allowClear={allowClear}
      treeDefaultExpandAll={treeDefaultExpandAll}
      loadData={usePaged ? onLoadData : undefined}
      loading={loading}
      dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
    />
  );
}


