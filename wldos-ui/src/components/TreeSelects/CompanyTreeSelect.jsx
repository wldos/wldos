import React, { useEffect, useState, useCallback } from 'react';
import { TreeSelect } from 'antd';
import { getComSelectOption, queryPage as queryComPage } from '@/pages/sys/com/service';
import { normalizeCompanyNode } from '@/utils/tree';

const { SHOW_PARENT } = TreeSelect;

export default function CompanyTreeSelect({
  value,
  onChange,
  multiple = false,
  placeholder = '请选择公司',
  style,
  allowClear = true,
  treeCheckable = false,
  treeDefaultExpandAll = false,
  usePaged = false, // true: 使用分页懒加载
}) {
  const [treeData, setTreeData] = useState([]);
  const [loading, setLoading] = useState(false);

  const mapTree = useCallback((nodes = []) => {
    return nodes.map((n) => ({
      ...normalizeCompanyNode(n),
      children: Array.isArray(n.children) ? mapTree(n.children) : undefined,
    }));
  }, []);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const res = await queryComPage({ current: 1, pageSize: 1000 });
      const list = res?.data?.rows || [];
      
      // 添加顶级平台公司选项
      const platformCompany = {
        id: '0',
        parentId: '0',
        comName: '平台公司',
        comCode: 'platform',
        comDesc: '平台顶级公司',
        isValid: '1',
        children: null
      };
      
      const dataWithPlatform = [platformCompany, ...list];
      setTreeData(mapTree(dataWithPlatform));
    } finally {
      setLoading(false);
    }
  }, [mapTree]);

  // 懒加载子节点（分页/异步场景占位，当前接口已返回children，可直接返回已存在children）
  const onLoadData = async (node) => {
    if (!usePaged) return Promise.resolve();
    const hasChildren = Array.isArray(node.children) && node.children.length > 0;
    if (hasChildren) return Promise.resolve();
    // 示例：按需请求下一层（后端若支持 parentId 分页）
    const res = await queryComPage({ parentId: node.value, current: 1, pageSize: 1000 });
    const rows = res?.data?.rows || [];
    node.children = rows.map((r) => normalizeCompanyNode(r));
    setTreeData((prev) => [...prev]);
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


