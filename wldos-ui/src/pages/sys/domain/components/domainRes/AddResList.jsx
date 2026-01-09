import {Button, message} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, {useRef, useState, useMemo, useCallback} from 'react';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import {querySelectPage} from "@/pages/sys/res/service";


const AddResList = (props) => {
  const {
    values,
    onSubmit: addRes,
    modalVisible,
    onCancel,
  } = props;
  const actionRef = useRef();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [allData, setAllData] = useState([]);
  const lastSelectTimeRef = useRef(0); // 记录最后一次 onSelect 的时间
  const {domainId = ''} = values;

  const handleAdd = async (selectedRows) => {
    await addRes({
      ids: selectedRows.map((row) => row.id),
      domainId,
    });
  };

  // 获取所有子节点的ID（递归）
  const getAllChildrenIds = useCallback((node, allNodes) => {
    const childrenIds = [];
    if (node.children && node.children.length > 0) {
      node.children.forEach(child => {
        childrenIds.push(child.id);
        // 递归获取子节点的子节点
        const childNode = allNodes.find(n => n.id === child.id);
        if (childNode) {
          childrenIds.push(...getAllChildrenIds(childNode, allNodes));
        }
      });
    }
    return childrenIds;
  }, []);

  // 扁平化树形数据，方便查找
  const flattenTree = useCallback((nodes, result = []) => {
    nodes.forEach(node => {
      result.push(node);
      if (node.children && node.children.length > 0) {
        flattenTree(node.children, result);
      }
    });
    return result;
  }, []);

  // 获取节点的所有子节点（包括嵌套）- 用于计算 indeterminate 状态
  const getNodeChildren = useCallback((nodeId, allNodes) => {
    const children = [];

    // 递归查找节点
    const findNode = (nodes, targetId) => {
      for (const node of nodes) {
        if (node.id === targetId) {
          return node;
        }
        if (node.children && node.children.length > 0) {
          const found = findNode(node.children, targetId);
          if (found) return found;
        }
      }
      return null;
    };

    const node = findNode(allNodes, nodeId);
    if (!node) return [];

    // 递归收集所有子节点
    const collectChildren = (parentNode) => {
      if (parentNode.children && parentNode.children.length > 0) {
        parentNode.children.forEach(child => {
          children.push(child);
          collectChildren(child);
        });
      }
    };

    collectChildren(node);
    return children;
  }, []);

  // 获取节点的父节点
  const getParentNode = useCallback((nodeId, allNodes) => {
    for (const node of allNodes) {
      if (node.children && node.children.some(child => child.id === nodeId)) {
        return node;
      }
      // 递归查找
      if (node.children) {
        const found = getParentNode(nodeId, node.children);
        if (found) return found;
      }
    }
    return null;
  }, []);

  // 检查节点的所有子节点是否都被选中
  const areAllChildrenSelected = useCallback((nodeId, selectedKeys, allNodes) => {
    const children = getNodeChildren(nodeId, allNodes);
    if (children.length === 0) return true;
    return children.every(child => selectedKeys.includes(child.id));
  }, [getNodeChildren]);

  // 检查节点是否有部分子节点被选中
  const hasSomeChildrenSelected = useCallback((nodeId, selectedKeys, allNodes) => {
    const children = getNodeChildren(nodeId, allNodes);
    if (children.length === 0) return false;
    return children.some(child => selectedKeys.includes(child.id));
  }, [getNodeChildren]);

  // 递归收集节点的所有子节点ID
  const collectAllChildrenIds = useCallback((node, result = []) => {
    if (node.children && node.children.length > 0) {
      node.children.forEach(child => {
        if (child.selected !== true) {
          result.push(child.id);
        }
        // 递归收集子节点的子节点
        collectAllChildrenIds(child, result);
      });
    }
    return result;
  }, []);

  // 处理节点选择
  const handleSelect = useCallback((record, selected) => {
    // 记录选择时间，用于在 onChange 中判断是否忽略
    lastSelectTimeRef.current = Date.now();

    setSelectedRowKeys(prevKeys => {
      let newKeys = [...prevKeys];

      if (selected) {
        // 选中节点：添加当前节点及其所有子节点
        if (!newKeys.includes(record.id)) {
          newKeys.push(record.id);
        }
        // 优先从 record.children 获取子节点，如果没有则从 allData 中查找
        let childrenIds = [];
        if (record.children && record.children.length > 0) {
          childrenIds = collectAllChildrenIds(record);
        } else {
          // 如果 record 没有 children，从 allData 中查找节点及其子节点
          const children = getNodeChildren(record.id, allData);
          childrenIds = children.map(child => child.id);
        }

        childrenIds.forEach(childId => {
          if (!newKeys.includes(childId)) {
            newKeys.push(childId);
          }
        });
      } else {
        // 取消选中节点：只移除当前节点，不移除子节点
        newKeys = newKeys.filter(key => key !== record.id);
      }

      return newKeys;
    });
  }, [collectAllChildrenIds, getNodeChildren, allData]);

  // 计算 indeterminate 状态（部分选中状态）
  const getIndeterminateKeys = useMemo(() => {
    const indeterminateKeys = new Set();

    // 递归检查所有节点
    const checkNode = (node) => {
      if (!node.children || node.children.length === 0) {
        return;
      }

      const selfSelected = selectedRowKeys.includes(node.id);
      const hasSome = hasSomeChildrenSelected(node.id, selectedRowKeys, allData);
      const allSelected = areAllChildrenSelected(node.id, selectedRowKeys, allData);

      // 部分选中状态的条件：
      // 1. 节点本身已选中，但不是所有子节点都被选中（这是关键：取消子节点后父节点仍选中但显示部分选中）
      // 2. 节点本身未选中，但有部分子节点被选中
      if (selfSelected && hasSome && !allSelected) {
        indeterminateKeys.add(node.id);
      } else if (!selfSelected && hasSome && !allSelected) {
        indeterminateKeys.add(node.id);
      }

      // 递归检查子节点
      if (node.children) {
        node.children.forEach(child => {
          checkNode(child);
        });
      }
    };

    // 递归遍历所有节点
    allData.forEach(node => {
      checkNode(node);
    });

    return Array.from(indeterminateKeys);
  }, [allData, selectedRowKeys, hasSomeChildrenSelected, areAllChildrenSelected]);

  // 根据 selectedRowKeys 获取选中的行数据（包括嵌套节点）
  const selectedRowsState = useMemo(() => {
    // 扁平化树形数据，然后过滤出选中的节点
    const flatData = flattenTree(allData);
    return flatData.filter(item => selectedRowKeys.includes(item.id));
  }, [allData, selectedRowKeys, flattenTree]);

  const columns = [
    {
      title: '资源名称',
      dataIndex: 'resourceName',
    },
    {
      title: '资源描述',
      dataIndex: 'remark',
      valueType: 'textarea',
      width: '50%'
    },
    {
      title: '资源状态',
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        0: {
          text: '无效',
          status: 'invalid',
        },
        1: {
          text: '有效',
          status: 'valid',
        },
      },
    },
  ];
  return (
    <FullscreenModal
      width={"fit-content"}
      destroyOnClose
      title="添加资源"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <ProTable
        headerTitle="资源清单"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        expandable={{
          defaultExpandAllRows: true, // 默认展开所有行，确保子节点可见
        }}
        request={async (params, sorter, filter) => {
          const res = await querySelectPage({
            ...params,
            domainId,
            sorter,
            filter
          });
          if (!res?.data?.total)
            message.warn('无资源可添加，请确认是否已定制应用');
          const data = res?.data?.rows || [];
          // 保存所有数据用于树形选择逻辑
          setAllData(data);
          return Promise.resolve({
            total: res?.data?.total || 0,
            data: data,
            success: true,
          });
        }
        }
        columns={columns}
        rowSelection={{
          selectedRowKeys: selectedRowKeys,
          onChange: (selectedKeys, selectedRows) => {
            // 如果刚刚执行了 onSelect（100ms 内），忽略 onChange，避免覆盖 onSelect 的修改
            const timeSinceLastSelect = Date.now() - lastSelectTimeRef.current;
            if (timeSinceLastSelect < 100) {
              return; // 忽略，onSelect 已经处理了
            }

            // 检查是否是全选/取消全选操作（变化数量大）
            const prevCount = selectedRowKeys.length;
            const newCount = selectedKeys.length;
            const diff = Math.abs(newCount - prevCount);

            // 如果是全选/取消全选（变化超过1个），或者是初始化，才更新
            if (diff > 1 || prevCount === 0 || newCount === 0) {
              setSelectedRowKeys(selectedKeys);
            }
          },
          onSelect: (record, selected, selectedRows, nativeEvent) => {
            // 自定义选择逻辑：选中父节点时自动选中所有子节点
            // 取消子节点时，父节点保持选中状态（显示为部分选中）
            handleSelect(record, selected);
          },
          getCheckboxProps: (record) => ({
            disabled: record.selected === true, // 只有已添加到域的资源才禁用
            indeterminate: getIndeterminateKeys.includes(record.id), // 部分选中状态
          }),
          // 使用 checkStrictly: true 禁用自动关联，通过自定义逻辑控制父子节点关系
          checkStrictly: true,
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项&nbsp;&nbsp;
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleAdd(selectedRowsState);
              setSelectedRowKeys([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            添加资源
          </Button>
        </FooterToolbar>
      )}
    </FullscreenModal>
  );
};

export default AddResList;
