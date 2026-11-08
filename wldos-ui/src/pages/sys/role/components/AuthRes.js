import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {Button, Input, Modal, Space, Switch, Transfer, Tree} from 'antd';
import {useIntl} from 'umi';
import FullscreenModal from '@/components/FullscreenModal';
import {queryEnumMenuNodeKind, queryEnumResource} from '@/services/enum';
import {
  AuthResRowTitle,
  buildEnumLabelMap,
  mapTreeWithRowTitles,
  resolveAuthResTypeTag,
} from './authResTypeVisual';

const decorateAuthTree = (nodes, showResourcePath) => {
  if (!nodes || nodes.length === 0) {
    return [];
  }
  return nodes.map(({children, title, resourcePath, requestMethod, resourceCode, ...rest}) => {
    let displayTitle = title;
    if (showResourcePath) {
      if (resourcePath) {
        const methodPrefix = requestMethod ? `${requestMethod} ` : '';
        displayTitle = `${title} · ${methodPrefix}${resourcePath}`;
      } else if (resourceCode) {
        displayTitle = `${title} · ${resourceCode}`;
      }
    }
    return {
      ...rest,
      title: displayTitle,
      baseTitle: title,
      resourcePath,
      requestMethod,
      resourceCode,
      children: decorateAuthTree(children, showResourcePath),
    };
  });
};

const nodeSearchBlob = (node) => {
  const parts = [
    node.baseTitle,
    node.title,
    node.resourcePath,
    node.resourceCode,
    node.requestMethod,
    node.resourceType,
    node.menuNodeKind,
  ];
  return parts.filter(Boolean).join(' ').toLowerCase();
};

const findNodeByKey = (nodes, targetKey) => {
  for (const node of nodes) {
    if (node.key === targetKey) {
      return node;
    }
    if (node.children && node.children.length > 0) {
      const found = findNodeByKey(node.children, targetKey);
      if (found) {
        return found;
      }
    }
  }
  return null;
};

const collectDescendantKeys = (node, result = []) => {
  if (!node.children || node.children.length === 0) {
    return result;
  }
  node.children.forEach((child) => {
    if (!child.disabled) {
      result.push(child.key);
    }
    collectDescendantKeys(child, result);
  });
  return result;
};

const collectAncestorKeys = (node, treeData, result = []) => {
  if (!node || node.parentId === undefined || node.parentId === null) {
    return result;
  }
  const parent = findNodeByKey(treeData, node.parentId);
  if (!parent) {
    return result;
  }
  if (!parent.disabled) {
    result.push(parent.key);
  }
  return collectAncestorKeys(parent, treeData, result);
};

const normalizeWithAncestors = (keys, treeData) => {
  const newKeys = [...(keys || [])];
  (keys || []).forEach((key) => {
    const node = findNodeByKey(treeData, key);
    if (!node) {
      return;
    }
    collectAncestorKeys(node, treeData).forEach((ancestorKey) => {
      if (!newKeys.includes(ancestorKey)) {
        newKeys.push(ancestorKey);
      }
    });
  });
  return newKeys;
};

const getNodeChildren = (nodeKey, treeData) => {
  const node = findNodeByKey(treeData, nodeKey);
  if (!node) {
    return [];
  }
  const children = [];
  const collectChildren = (parentNode) => {
    if (!parentNode.children || parentNode.children.length === 0) {
      return;
    }
    parentNode.children.forEach((child) => {
      children.push(child);
      collectChildren(child);
    });
  };
  collectChildren(node);
  return children;
};

const areAllChildrenSelected = (nodeKey, checkedKeys, treeData) => {
  const children = getNodeChildren(nodeKey, treeData);
  if (children.length === 0) {
    return true;
  }
  return children.every((child) => checkedKeys.includes(child.key));
};

const hasSomeChildrenSelected = (nodeKey, checkedKeys, treeData) => {
  const children = getNodeChildren(nodeKey, treeData);
  if (children.length === 0) {
    return false;
  }
  return children.some((child) => checkedKeys.includes(child.key));
};

const computeHalfCheckedKeys = (checkedKeys, treeData) => {
  const halfChecked = new Set();

  const checkNode = (node) => {
    if (!node.children || node.children.length === 0) {
      return;
    }

    const selfSelected = checkedKeys.includes(node.key);
    const hasSome = hasSomeChildrenSelected(node.key, checkedKeys, treeData);
    const allSelected = areAllChildrenSelected(node.key, checkedKeys, treeData);

    if (selfSelected && hasSome && !allSelected) {
      halfChecked.add(node.key);
    } else if (!selfSelected && hasSome && !allSelected) {
      halfChecked.add(node.key);
    }

    node.children.forEach((child) => checkNode(child));
  };

  treeData.forEach((node) => checkNode(node));
  return Array.from(halfChecked);
};

const flattenTree = (nodes = [], result = [], ancestorTitles = [], rootKey = null, orderRef = { n: 0 }) => {
  nodes.forEach(({children, ...rest}) => {
    const currentRootKey = rootKey == null ? rest.key : rootKey;
    const pathTitles = [...ancestorTitles, rest.title];
    const treeOrder = orderRef.n;
    orderRef.n += 1;
    result.push({
      ...rest,
      fullTitle: pathTitles.join(' / '),
      treeOrder,
      rootKey: currentRootKey,
    });
    flattenTree(children, result, pathTitles, currentRootKey, orderRef);
  });
  return result;
};

const sortKeysByTreeOrder = (keys, flatData) => {
  const orderMap = new Map(flatData.map((item) => [item.key, item.treeOrder]));
  return [...keys].sort((a, b) => {
    const orderA = orderMap.has(a) ? orderMap.get(a) : Number.MAX_SAFE_INTEGER;
    const orderB = orderMap.has(b) ? orderMap.get(b) : Number.MAX_SAFE_INTEGER;
    return orderA - orderB;
  });
};

const markDisabled = (nodes = [], targetKeys = []) =>
  nodes.map(({children, ...node}) => ({
    ...node,
    disabled: node.disabled || targetKeys.includes(node.key),
    children: markDisabled(children, targetKeys),
  }));

const buildDirectGrantState = (resTree, roleResGrants) => {
  const tree = decorateAuthTree(resTree || [], false);
  const flat = flattenTree(tree);
  const visibleKeySet = new Set(flat.map((item) => String(item.key)));
  const directIds = (roleResGrants || []).filter((item) => !item.inherit).map((item) => String(item.id));
  const stubItems = directIds
    .filter((id) => !visibleKeySet.has(id))
    .map((id, idx) => {
      const grant = (roleResGrants || []).find((item) => String(item.id) === id);
      const title = grant?.resourceName || `资源 ${id}`;
      return {
        key: id,
        title,
        baseTitle: title,
        treeOrder: 1_000_000 + idx,
        fullTitle: title,
      };
    });
  const mergedFlat = [...flat, ...stubItems];
  const targetKeys = sortKeysByTreeOrder(normalizeWithAncestors(directIds, tree), mergedFlat);
  return { mergedFlat, targetKeys };
};

const targetKeysSignature = (keys) => [...(keys || [])].map(String).sort().join('\u0001');

const nodeMatchesKeyword = (node, keyword) => nodeSearchBlob(node).includes(keyword);

const filterTreeByKeyword = (nodes, keyword) => {
  if (!keyword) {
    return { tree: nodes, expandedKeys: [] };
  }
  const kw = keyword.trim().toLowerCase();
  if (!kw) {
    return { tree: nodes, expandedKeys: [] };
  }
  const expanded = new Set();

  const walk = (list) => {
    const out = [];
    (list || []).forEach((node) => {
      const childResult = walk(node.children);
      const selfMatch = nodeMatchesKeyword(node, kw);
      if (selfMatch || childResult.length > 0) {
        if (childResult.length > 0) {
          expanded.add(node.key);
        }
        out.push({
          ...node,
          children: selfMatch ? node.children : childResult,
        });
      }
    });
    return out;
  };

  return { tree: walk(nodes), expandedKeys: Array.from(expanded) };
};

const treeTransferStyle = (
  <style>
    {`.tree-transfer .ant-transfer-list-body-customize-wrapper {
      flex: auto;
      display: flex;
      flex-direction: column;
      min-height: 0;
      padding: 0;
      overflow: hidden;
    }
    .tree-transfer .ant-transfer-list:first-child .ant-transfer-list-body-search-wrapper {
      display: none;
    }
    .tree-transfer .ant-transfer-list {
      resize: both;
      overflow: auto;
      min-width: 280px;
      min-height: 280px;
      max-width: 100%;
      max-height: 85vh;
    }`}
  </style>
);

const AuthRes = (props) => {
  const intl = useIntl();
  const {
    onSubmit: handleAuth,
    onCancel: handleAuthModalVisible,
    authModalVisible,
    existRes,
    roleResGrants,
    res,
    values,
    targetDomainLabel,
  } = props;

  const grantStateInit = buildDirectGrantState(
    res,
    roleResGrants?.length ? roleResGrants : existRes?.map((id) => ({ id, inherit: false })),
  );
  const mergedFlatRef = useRef(grantStateInit.mergedFlat);

  const baselineSignatureRef = useRef(
    targetKeysSignature(grantStateInit.targetKeys),
  );

  const [targetKeys, setTargetKeys] = useState(() => grantStateInit.targetKeys);
  const [expandedKeys, setExpandedKeys] = useState(['0']);
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [leftSearch, setLeftSearch] = useState('');
  const [showResourcePath, setShowResourcePath] = useState(false);
  const [resourceTypeLabels, setResourceTypeLabels] = useState({});
  const [menuNodeKindLabels, setMenuNodeKindLabels] = useState({});

  useEffect(() => {
    let cancelled = false;
    Promise.all([queryEnumResource(), queryEnumMenuNodeKind()]).then(([resTypeRes, kindRes]) => {
      if (cancelled) {
        return;
      }
      setResourceTypeLabels(buildEnumLabelMap(resTypeRes?.data));
      setMenuNodeKindLabels(buildEnumLabelMap(kindRes?.data));
    });
    return () => {
      cancelled = true;
    };
  }, []);

  const renderResourceRow = useCallback(
    (node, textOverride) => {
      const text = textOverride != null ? textOverride : (node.title || node.baseTitle);
      const typeTag = resolveAuthResTypeTag(node, resourceTypeLabels, menuNodeKindLabels);
      return <AuthResRowTitle text={text} typeTag={typeTag} />;
    },
    [resourceTypeLabels, menuNodeKindLabels],
  );

  const authTree = useMemo(
    () => decorateAuthTree(res || [], showResourcePath),
    [res, showResourcePath],
  );

  const flatDataSource = useMemo(() => {
    const base = flattenTree(authTree);
    const keys = new Set(base.map((item) => String(item.key)));
    const stubs = mergedFlatRef.current.filter((item) => !keys.has(String(item.key)));
    return [...base, ...stubs];
  }, [authTree]);

  const listStyle = isFullscreen
    ? {width: 'min(560px, 42vw)', height: 'calc(100vh - 280px)'}
    : {width: 340, height: 420};

  const onExpand = (expandedKeysValue) => {
    setExpandedKeys(expandedKeysValue);
    setAutoExpandParent(false);
  };

  const isAuthDirty = useMemo(
    () => targetKeysSignature(targetKeys) !== baselineSignatureRef.current,
    [targetKeys],
  );

  const requestClose = useCallback(() => {
    if (!isAuthDirty) {
      handleAuthModalVisible();
      return;
    }
    Modal.confirm({
      title: intl.formatMessage({
        id: 'sys.role.auth.unsaved.title',
        defaultMessage: '未保存的授权变更',
      }),
      content: intl.formatMessage({
        id: 'sys.role.auth.unsaved.content',
        defaultMessage: '已调整候选或已授权列表，尚未点击「授权」保存。关闭将丢弃本次修改。',
      }),
      okText: intl.formatMessage({
        id: 'sys.role.auth.unsaved.discard',
        defaultMessage: '丢弃并关闭',
      }),
      cancelText: intl.formatMessage({
        id: 'sys.role.auth.unsaved.stay',
        defaultMessage: '继续编辑',
      }),
      okType: 'danger',
      onOk: () => handleAuthModalVisible(),
    });
  }, [handleAuthModalVisible, intl, isAuthDirty]);

  const handleAddRes = () => {
    handleAuth({resIds: targetKeys, roleId: values.id});
  };

  const renderFooter = () => (
    <>
      <Button onClick={requestClose}>取消</Button>
      <Button type="primary" onClick={() => handleAddRes()}>授权</Button>
    </>
  );

  const transferFilterOption = (input, item) => {
    const kw = (input || '').trim().toLowerCase();
    if (!kw) {
      return true;
    }
    const blob = [
      item.fullTitle,
      item.title,
      item.baseTitle,
      item.resourcePath,
      item.resourceCode,
      item.requestMethod,
      item.resourceType,
      item.menuNodeKind,
      resourceTypeLabels[item.resourceType],
      menuNodeKindLabels[item.menuNodeKind],
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase();
    return blob.includes(kw);
  };

  const leftTreeFilter = useMemo(
    () => filterTreeByKeyword(markDisabled(authTree, targetKeys), leftSearch),
    [authTree, targetKeys, leftSearch],
  );

  const leftTreeWithTypeTitles = useMemo(
    () => mapTreeWithRowTitles(leftTreeFilter.tree, (node) => renderResourceRow(node)),
    [leftTreeFilter.tree, renderResourceRow],
  );

  const leftExpandedKeys = leftSearch.trim()
    ? leftTreeFilter.expandedKeys
    : expandedKeys;

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={intl.formatMessage(
        {id: 'sys.role.auth.titleWithSite', defaultMessage: '角色授权 - {roleName}{siteSuffix}'},
        {
          roleName: values.roleName || '',
          siteSuffix: targetDomainLabel ? `（${targetDomainLabel}）` : '',
        },
      )}
      visible={authModalVisible}
      footer={renderFooter()}
      onCancel={requestClose}
      maskClosable={!isAuthDirty}
      onFullscreenChange={setIsFullscreen}
    >
      <div style={{ marginBottom: 12 }}>
        <Space>
          <Switch
            checked={showResourcePath}
            onChange={setShowResourcePath}
            size="small"
          />
          <span style={{ fontSize: 13 }}>
            {intl.formatMessage({
              id: 'sys.role.auth.showResourcePath',
              defaultMessage: '显示 API 路径',
            })}
          </span>
        </Space>
      </div>
      {treeTransferStyle}
      <Transfer
        targetKeys={targetKeys}
        onChange={(nextTargetKeys) => setTargetKeys(sortKeysByTreeOrder(nextTargetKeys, flatDataSource))}
        dataSource={flatDataSource}
        rowKey={(item) => item.key}
        className="tree-transfer"
        titles={['候选权限', '已授权权限']}
        showSelectAll={false}
        showSearch
        filterOption={transferFilterOption}
        listStyle={listStyle}
        render={(item) => renderResourceRow(item, item.fullTitle || item.baseTitle || item.title)}
      >
        {({direction, onItemSelect, onItemSelectAll, selectedKeys}) => {
          if (direction !== 'left') {
            return undefined;
          }
          const checkedKeys = [...selectedKeys, ...targetKeys];
          const halfCheckedKeys = computeHalfCheckedKeys(checkedKeys, authTree);

          const onCheck = (_checkedKeysValue, {node, checked}) => {
            if (checked) {
              const keysToAdd = [node.key, ...collectDescendantKeys(node), ...collectAncestorKeys(node, authTree)]
                .filter((key) => !checkedKeys.includes(key));
              if (keysToAdd.length) {
                onItemSelectAll(keysToAdd, true);
              }
            } else {
              onItemSelect(node.key, false);
            }
          };

          return (
            <div style={{flex: 'auto', minHeight: 0, display: 'flex', flexDirection: 'column'}}>
              <div style={{padding: '8px 12px 4px'}}>
                <Input.Search
                  allowClear
                  placeholder="搜索候选权限"
                  value={leftSearch}
                  onChange={(e) => setLeftSearch(e.target.value)}
                />
              </div>
              <div style={{flex: 'auto', minHeight: 0, overflowY: 'auto', padding: '0 4px 8px'}}>
                <Tree
                  blockNode
                  checkable
                  showLine
                  checkStrictly
                  onExpand={onExpand}
                  expandedKeys={leftExpandedKeys}
                  autoExpandParent={leftSearch.trim() ? true : autoExpandParent}
                  onCheck={onCheck}
                  checkedKeys={{checked: checkedKeys, halfChecked: halfCheckedKeys}}
                  treeData={leftTreeWithTypeTitles}
                />
              </div>
            </div>
          );
        }}
      </Transfer>
    </FullscreenModal>
  );
};

export default AuthRes;
