import React, {useMemo, useState} from 'react';
import {Button, Input, Transfer, Tree} from 'antd';
import {useIntl} from 'umi';
import FullscreenModal from '@/components/FullscreenModal';

const flattenTree = (nodes = [], result = [], ancestorTitles = [], orderRef = { n: 0 }) => {
  nodes.forEach(({children, ...rest}) => {
    const pathTitles = [...ancestorTitles, rest.title];
    const treeOrder = orderRef.n;
    orderRef.n += 1;
    result.push({
      ...rest,
      fullTitle: pathTitles.join(' / '),
      treeOrder,
    });
    flattenTree(children, result, pathTitles, orderRef);
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

const nodeMatchesKeyword = (node, keyword) => {
  const label = node.title || '';
  return label.toLowerCase().includes(keyword);
};

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

const AuthRole = (props) => {
  const intl = useIntl();
  const {
    onSubmit: handleAuth,
    onCancel: handleAuthModalVisible,
    authModalVisible,
    existRes,
    res,
    values,
  } = props;

  const flatDataSource = useMemo(() => flattenTree(res || []), [res]);

  // existRes（已授权角色 id，来自 wo_role_org 现有数据）可能混有历史遗留、按当前授权范围规则已不再合法的
  // 角色 id（比如授权规则收紧后的旧数据，或角色本身归属轴已变更）。这类 id 不在候选树 res 里，
  // 树上没有对应节点、也就没有勾选框可取消——如果照单全收放进 targetKeys，每次保存都会原样带着这个
  // 过期 id 一起重新提交，被后端按当前规则拒绝，且管理员在这个弹窗里既看不到它、也摘不掉它。
  // 这里按候选树过滤一次，未出现在候选树里的 id 不带入这次提交，避免每次保存都被这条脏数据卡住。
  const [targetKeys, setTargetKeys] = useState(() => {
    const flat = flattenTree(res || []);
    const validKeys = new Set(flat.map((item) => item.key));
    return sortKeysByTreeOrder((existRes || []).filter((key) => validKeys.has(key)), flat);
  });
  const [expandedKeys, setExpandedKeys] = useState([]);
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [leftSearch, setLeftSearch] = useState('');

  const listStyle = isFullscreen
    ? {width: 'min(560px, 42vw)', height: 'calc(100vh - 280px)'}
    : {width: 340, height: 420};

  const handleAddRes = () => {
    handleAuth({roleIds: targetKeys, orgId: values.id, archId: values.archId, comId: values.comId});
  };

  const renderFooter = () => (
    <>
      <Button onClick={() => handleAuthModalVisible(false, values)}>取消</Button>
      <Button type="primary" onClick={() => handleAddRes()}>授权</Button>
    </>
  );

  const transferFilterOption = (input, item) => {
    const text = (item.fullTitle || item.title || '').toLowerCase();
    return text.includes((input || '').trim().toLowerCase());
  };

  const leftTreeFilter = useMemo(
    () => filterTreeByKeyword(markDisabled(res || [], targetKeys), leftSearch),
    [res, targetKeys, leftSearch],
  );

  const leftExpandedKeys = leftSearch.trim() ? leftTreeFilter.expandedKeys : expandedKeys;

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={intl.formatMessage(
        {id: 'sys.org.auth.title', defaultMessage: '组织授权 - {orgName}'},
        {orgName: values.orgName || ''},
      )}
      visible={authModalVisible}
      footer={renderFooter()}
      onCancel={() => handleAuthModalVisible()}
      onFullscreenChange={setIsFullscreen}
    >
      {treeTransferStyle}
      <Transfer
        targetKeys={targetKeys}
        onChange={(nextTargetKeys) => setTargetKeys(sortKeysByTreeOrder(nextTargetKeys, flatDataSource))}
        dataSource={flatDataSource}
        rowKey={(item) => item.key}
        className="tree-transfer"
        titles={['候选角色', '已授权角色']}
        showSelectAll={false}
        showSearch
        filterOption={transferFilterOption}
        listStyle={listStyle}
        render={(item) => item.fullTitle || item.title}
      >
        {({direction, onItemSelect, selectedKeys}) => {
          if (direction !== 'left') {
            return undefined;
          }
          const checkedKeys = [...selectedKeys, ...targetKeys];

          return (
            <div style={{flex: 'auto', minHeight: 0, display: 'flex', flexDirection: 'column'}}>
              <div style={{padding: '8px 12px 4px'}}>
                <Input.Search
                  allowClear
                  placeholder="搜索候选角色"
                  value={leftSearch}
                  onChange={(e) => setLeftSearch(e.target.value)}
                />
              </div>
              <div style={{flex: 'auto', minHeight: 0, overflowY: 'auto', padding: '0 4px 8px'}}>
                <Tree
                  blockNode
                  checkable
                  checkStrictly
                  defaultExpandAll={!leftSearch.trim()}
                  onExpand={(keys) => {
                    setExpandedKeys(keys);
                    setAutoExpandParent(false);
                  }}
                  expandedKeys={leftExpandedKeys}
                  autoExpandParent={leftSearch.trim() ? true : autoExpandParent}
                  checkedKeys={checkedKeys}
                  treeData={leftTreeFilter.tree}
                  onCheck={(_, {node: {key}}) => {
                    onItemSelect(key, !checkedKeys.includes(key));
                  }}
                  onSelect={(_, {node: {key}}) => {
                    onItemSelect(key, !checkedKeys.includes(key));
                  }}
                />
              </div>
            </div>
          );
        }}
      </Transfer>
    </FullscreenModal>
  );
};

export default AuthRole;
