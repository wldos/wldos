import React, { useEffect, useMemo, useState } from 'react';
import { Button, Card, Col, Row, Select, Space, Switch, Tooltip, TreeSelect } from 'antd';
import { FilterOutlined, InfoCircleOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { queryPage as queryOrgPage } from '@/pages/sys/org/service';
import { fetchDictOptions, fetchDictTreeOptions } from '../service';
import RegionCascadeSelect from './RegionCascadeSelect';

const buildOrgTree = (rows = []) => {
  const map = {};
  rows.forEach((item) => {
    map[item.id] = {
      title: item.orgName,
      value: String(item.id),
      key: String(item.id),
      parentId: item.parentId,
      children: [],
    };
  });
  const roots = [];
  Object.values(map).forEach((node) => {
    const pid = node.parentId;
    const parent = pid != null && pid !== 0 && map[pid] ? map[pid] : null;
    if (parent) {
      parent.children.push(node);
    } else {
      roots.push(node);
    }
  });
  const sortNodes = (nodes) => {
    nodes.sort((a, b) => String(a.title).localeCompare(String(b.title), 'zh-CN'));
    nodes.forEach((n) => {
      if (n.children?.length) {
        sortNodes(n.children);
      }
    });
  };
  sortNodes(roots);
  return roots;
};

const isRegionSource = (source) => (source || '').startsWith('TABLE:wo_region');

const isOrgSource = (source) => source === 'ORG_TREE';

const isDictDimension = (dim) => {
  const source = dim?.valueSource || '';
  return source.startsWith('DICT:') || source.startsWith('TERM_CLASS:');
};

const isTreeDictSource = (source) => (source || '').startsWith('TERM_CLASS:');

const mapApiTreeToAntd = (nodes = []) => nodes.map((node) => ({
  title: node.title,
  value: String(node.value),
  key: String(node.key || node.value),
  children: node.children?.length ? mapApiTreeToAntd(node.children) : undefined,
}));

const isStringDimension = (dim) => {
  const matchMode = (dim.matchMode || '').toUpperCase();
  const source = dim.valueSource || '';
  return (
    matchMode === 'IN'
    || matchMode === 'PREFIX'
    || isRegionSource(source)
    || source.startsWith('DICT:')
    || source.startsWith('TERM_CLASS:')
    || source.startsWith('TERM_TYPE:')
  );
};

const modeOptionsFor = (dim) => {
  const matchMode = (dim.matchMode || '').toUpperCase();
  const opts = [{ label: '全部（不收窄）', value: 'ALL' }];
  if (matchMode === 'SUBTREE') {
    opts.push({ label: '指定范围及下级', value: 'SUBTREE' });
  } else if (matchMode === 'IN' || matchMode === 'PREFIX') {
    opts.push({ label: '指定值', value: 'IN' });
  } else {
    opts.push({ label: '指定值', value: 'VALUES' });
  }
  return opts;
};

const activeModeOptions = (dim) => modeOptionsFor(dim).filter((o) => o.value !== 'ALL');

const isRowActivated = (row) => {
  if (!row) {
    return false;
  }
  const mode = (row.scopeMode || 'ALL').toUpperCase();
  return mode !== 'ALL';
};

const SCOPE_HELP = '全局字段开关关闭表示不限制（ALL）。定制字段按行添加类型并勾选取值；未选用的类型不参与该角色配置。';

const buildDictDim = (dictCode, dimensionCode) => ({
  dimensionCode: dimensionCode || dictCode,
  valueSource: `DICT:${dictCode}`,
  matchMode: 'IN',
});

const ScopeDimensionField = ({
  globalDimensions = [],
  customDimensions = [],
  pickableDictTypes = [],
  directAdoptedDictCodes = [],
  adoptedDictCodes = [],
  onAdoptedChange,
  value = [],
  onChange,
}) => {
  const [orgTree, setOrgTree] = useState([]);
  const [optionMap, setOptionMap] = useState({});
  const [treeOptionMap, setTreeOptionMap] = useState({});
  const [extensionRows, setExtensionRows] = useState([]);

  const sortedGlobalDimensions = useMemo(
    () => [...globalDimensions].sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0)),
    [globalDimensions],
  );

  const pickableByDictCode = useMemo(() => {
    const map = {};
    (pickableDictTypes || []).forEach((item) => {
      if (item?.dictCode) {
        map[item.dictCode] = item;
      }
    });
    return map;
  }, [pickableDictTypes]);

  const extensionByDictCode = useMemo(() => {
    const map = {};
    (customDimensions || []).forEach((item) => {
      if (item?.dictCode) {
        map[item.dictCode] = item;
      }
    });
    return map;
  }, [customDimensions]);

  const resolveDimensionCode = (dictCode) => {
    if (!dictCode) {
      return null;
    }
    const ext = extensionByDictCode[dictCode];
    if (ext?.dimensionCode) {
      return ext.dimensionCode;
    }
    const pick = pickableByDictCode[dictCode];
    if (pick?.registeredDimensionCode) {
      return pick.registeredDimensionCode;
    }
    return dictCode;
  };

  const inheritedDictCodes = useMemo(() => {
    const direct = new Set(directAdoptedDictCodes || []);
    return (customDimensions || [])
      .filter((item) => item.inherited && item.dictCode && !direct.has(item.dictCode))
      .map((item) => item.dictCode);
  }, [customDimensions, directAdoptedDictCodes]);

  const hasOrgDim = useMemo(
    () => sortedGlobalDimensions.some((d) => isOrgSource(d.valueSource)),
    [sortedGlobalDimensions],
  );

  const loadedOptionDictCodes = useMemo(() => {
    const codes = new Set();
    extensionRows.forEach((row) => {
      if (row.dictCode) {
        codes.add(row.dictCode);
      }
    });
    sortedGlobalDimensions.forEach((dim) => {
      const source = dim?.valueSource || '';
      if (source.startsWith('DICT:')) {
        codes.add(source.substring(5));
      } else if (source.startsWith('TERM_CLASS:')) {
        codes.add(source.substring('TERM_CLASS:'.length));
      }
    });
    return [...codes].join(',');
  }, [extensionRows, sortedGlobalDimensions]);

  useEffect(() => {
    const rows = (customDimensions || []).map((item) => ({
      key: item.dictCode,
      dictCode: item.dictCode,
      dimensionCode: item.dimensionCode,
      inherited: !!item.inherited,
    }));
    setExtensionRows(rows);
  }, [customDimensions]);

  useEffect(() => {
    if (!hasOrgDim) {
      return;
    }
    queryOrgPage({ current: 1, pageSize: 5000 }).then((res) => {
      const rows = res?.data?.rows || [];
      setOrgTree(buildOrgTree(rows));
    });
  }, [hasOrgDim]);

  useEffect(() => {
    if (!loadedOptionDictCodes) {
      return;
    }
    loadedOptionDictCodes.split(',').filter(Boolean).forEach((dictCode) => {
      const ext = extensionByDictCode[dictCode];
      const globalDim = sortedGlobalDimensions.find((dim) => {
        const source = dim?.valueSource || '';
        return source === `DICT:${dictCode}` || source === `TERM_CLASS:${dictCode}`;
      });
      const source = ext?.valueSource || globalDim?.valueSource || `DICT:${dictCode}`;
      const useTree = isTreeDictSource(source);
      if (useTree) {
        if (treeOptionMap[dictCode]) {
          return;
        }
        fetchDictTreeOptions(dictCode).then((res) => {
          if (res?.data?.length) {
            setTreeOptionMap((prev) => ({ ...prev, [dictCode]: mapApiTreeToAntd(res.data) }));
          }
        });
        return;
      }
      if (optionMap[dictCode]) {
        return;
      }
      fetchDictOptions(dictCode).then((res) => {
        if (res?.data) {
          setOptionMap((prev) => ({ ...prev, [dictCode]: res.data }));
          if (!isTreeDictSource(source)) {
            fetchDictTreeOptions(dictCode).then((treeRes) => {
              if (treeRes?.data?.length) {
                setTreeOptionMap((prev) => ({ ...prev, [dictCode]: mapApiTreeToAntd(treeRes.data) }));
              }
            });
          }
        }
      });
    });
  }, [loadedOptionDictCodes, optionMap, treeOptionMap, extensionByDictCode, sortedGlobalDimensions]);

  const valueMap = useMemo(() => {
    const map = {};
    (value || []).forEach((row) => {
      map[row.dimensionCode] = row;
    });
    return map;
  }, [value]);

  const emitChange = (dimensionCode, patch) => {
    const next = [...(value || [])];
    const idx = next.findIndex((item) => item.dimensionCode === dimensionCode);
    const row = {
      dimensionCode,
      scopeMode: patch.scopeMode || 'ALL',
      scopeValues: patch.scopeValues || '[]',
    };
    if (idx >= 0) {
      next[idx] = { ...next[idx], ...row };
    } else {
      next.push(row);
    }
    onChange?.(next.filter((item) => isRowActivated(item)));
  };

  const toggleActivation = (dim, checked) => {
    const code = dim.dimensionCode;
    if (!checked) {
      onChange?.((value || []).filter((row) => row.dimensionCode !== code));
      return;
    }
    const defaultMode = activeModeOptions(dim)[0]?.value || 'IN';
    emitChange(code, { scopeMode: defaultMode, scopeValues: '[]' });
  };

  const usedExtensionDictCodes = useMemo(
    () => extensionRows.map((r) => r.dictCode).filter(Boolean),
    [extensionRows],
  );

  const typeSelectOptions = (currentDictCode) => {
    const pool = [...pickableDictTypes, ...customDimensions];
    const seen = new Set();
    return pool
      .filter((item) => {
        const dictCode = item?.dictCode;
        if (!dictCode || seen.has(dictCode)) {
          return false;
        }
        seen.add(dictCode);
        if (dictCode === currentDictCode) {
          return true;
        }
        return !usedExtensionDictCodes.includes(dictCode);
      })
      .map((item) => ({
        label: item.name || item.dictName || item.dictCode,
        value: item.dictCode,
      }));
  };

  const handleAddExtensionRow = () => {
    setExtensionRows((prev) => [...prev, { key: `new-${Date.now()}`, dictCode: null }]);
  };

  const handleRemoveExtensionRow = (row) => {
    const { key, dictCode, dimensionCode } = row;
    if (dictCode && (adoptedDictCodes || []).includes(dictCode)) {
      onAdoptedChange?.((adoptedDictCodes || []).filter((c) => c !== dictCode));
    }
    setExtensionRows((prev) => prev.filter((r) => r.key !== key));
    const dimCode = dimensionCode || resolveDimensionCode(dictCode);
    if (dimCode) {
      onChange?.((value || []).filter((s) => s.dimensionCode !== dimCode));
    }
  };

  const handleExtensionTypeChange = (rowKey, nextDictCode) => {
    const prevRow = extensionRows.find((r) => r.key === rowKey);
    if (prevRow?.dictCode && (adoptedDictCodes || []).includes(prevRow.dictCode)) {
      onAdoptedChange?.((adoptedDictCodes || []).filter((c) => c !== prevRow.dictCode));
    }
    const prevDimCode = prevRow?.dimensionCode || resolveDimensionCode(prevRow?.dictCode);
    if (prevDimCode) {
      onChange?.((value || []).filter((s) => s.dimensionCode !== prevDimCode));
    }
    const nextDimCode = resolveDimensionCode(nextDictCode);
    setExtensionRows((prev) => prev.map((r) => (
      r.key === rowKey
        ? { ...r, dictCode: nextDictCode, dimensionCode: nextDimCode, inherited: false }
        : r
    )));
    if (nextDictCode && !inheritedDictCodes.includes(nextDictCode)) {
      const nextAdopts = [...(adoptedDictCodes || [])];
      if (!nextAdopts.includes(nextDictCode)) {
        nextAdopts.push(nextDictCode);
      }
      onAdoptedChange?.(nextAdopts);
    }
    if (nextDictCode && nextDimCode) {
      const dim = buildDictDim(nextDictCode, nextDimCode);
      const defaultMode = activeModeOptions(dim)[0]?.value || 'IN';
      emitChange(nextDimCode, { scopeMode: defaultMode, scopeValues: '[]' });
    }
  };

  const renderValuePicker = (dim, current, dictCode) => {
    const code = dim.dimensionCode;
    const source = dim.valueSource || '';
    const stringDim = isStringDimension(dim);
    const modes = activeModeOptions(dim);
    const defaultMode = modes[0]?.value || 'IN';
    const mode = current.scopeMode || defaultMode;
    const showModeSelect = modes.length > 1;
    let selectedValues = [];
    try {
      selectedValues = JSON.parse(current.scopeValues || '[]').map(String);
    } catch (e) {
      selectedValues = [];
    }
    const showPicker = mode === 'SUBTREE' || mode === 'IN' || mode === 'VALUES';
    const dictCodeFromSource = source.startsWith('DICT:')
      ? source.substring(5)
      : (source.startsWith('TERM_CLASS:') ? source.substring('TERM_CLASS:'.length) : null);
    const optionKey = dictCode || dictCodeFromSource || code;

    return (
      <Space direction="vertical" style={{ width: '100%' }}>
        {showModeSelect ? (
          <Select
            value={mode}
            onChange={(nextMode) => {
              emitChange(code, { scopeMode: nextMode, scopeValues: current.scopeValues || '[]' });
            }}
            options={modes}
          />
        ) : null}
        {showPicker && isOrgSource(source) && (
          <TreeSelect
            treeData={orgTree}
            treeCheckable
            showCheckedStrategy={TreeSelect.SHOW_PARENT}
            treeDefaultExpandAll
            showLine={{ showLeafIcon: false }}
            treeNodeFilterProp="title"
            placeholder="选择组织根节点"
            value={selectedValues}
            onChange={(keys) => {
              const ids = (keys || []).map((k) => Number(k));
              emitChange(code, { scopeMode: mode, scopeValues: JSON.stringify(ids) });
            }}
            style={{ width: '100%' }}
          />
        )}
        {showPicker && isRegionSource(source) && (
          <RegionCascadeSelect
            dimensionCode={code}
            value={selectedValues}
            onChange={(keys) => {
              emitChange(code, { scopeMode: mode, scopeValues: JSON.stringify(keys || []) });
            }}
          />
        )}
        {showPicker && !isOrgSource(source) && !isRegionSource(source) && stringDim && treeOptionMap[optionKey]?.length > 0 && (
          <TreeSelect
            treeData={treeOptionMap[optionKey]}
            treeCheckable
            showCheckedStrategy={TreeSelect.SHOW_PARENT}
            treeDefaultExpandAll
            showLine={{ showLeafIcon: false }}
            treeNodeFilterProp="title"
            placeholder="选择范围值"
            value={selectedValues}
            onChange={(keys) => {
              emitChange(code, { scopeMode: mode, scopeValues: JSON.stringify(keys || []) });
            }}
            style={{ width: '100%' }}
          />
        )}
        {showPicker && !isOrgSource(source) && !isRegionSource(source) && stringDim
          && !(treeOptionMap[optionKey]?.length > 0) && (
          <Select
            mode="multiple"
            placeholder="选择范围值"
            options={optionMap[optionKey] || []}
            fieldNames={{ label: 'label', value: 'value' }}
            value={selectedValues}
            onChange={(keys) => {
              emitChange(code, { scopeMode: mode, scopeValues: JSON.stringify(keys || []) });
            }}
            style={{ width: '100%' }}
            notFoundContent={optionMap[optionKey] ? undefined : '加载中…'}
          />
        )}
      </Space>
    );
  };

  const renderBuiltinDimension = (dim) => {
    const code = dim.dimensionCode;
    const current = valueMap[code] || {};
    const activated = isRowActivated(current);

    return (
      <Row key={code} gutter={16} style={{ marginBottom: 16 }} align="top">
        <Col span={6} style={{ textAlign: 'right', paddingTop: 5 }}>
          <Space>
            <span>{dim.dimensionName || code}</span>
            <Switch
              size="small"
              checked={activated}
              onChange={(checked) => toggleActivation(dim, checked)}
            />
          </Space>
        </Col>
        <Col span={18}>
          {activated ? renderValuePicker(dim, current) : null}
        </Col>
      </Row>
    );
  };

  const renderExtensionRow = (row, index) => {
    const dimCode = row.dimensionCode || resolveDimensionCode(row.dictCode);
    const dim = row.dictCode ? buildDictDim(row.dictCode, dimCode) : null;
    const current = dimCode ? (valueMap[dimCode] || {}) : {};
    const isInherited = row.dictCode && inheritedDictCodes.includes(row.dictCode);
    const canRemove = !isInherited;

    return (
      <Row key={row.key} gutter={16} style={{ marginBottom: 16 }} align="top">
        <Col span={6}>
          <Select
            placeholder="选择枚举类型"
            value={row.dictCode || undefined}
            options={typeSelectOptions(row.dictCode)}
            onChange={(code) => handleExtensionTypeChange(row.key, code)}
            style={{ width: '100%' }}
            disabled={isInherited}
          />
        </Col>
        <Col span={16}>
          {dim ? renderValuePicker(dim, {
            ...current,
            scopeMode: current.scopeMode || activeModeOptions(dim)[0]?.value || 'IN',
          }, row.dictCode) : null}
        </Col>
        <Col span={2} style={{ paddingTop: 5 }}>
          <Space>
            {canRemove ? (
              <Tooltip title="移除此扩展维">
                <Button
                  type="text"
                  size="small"
                  danger
                  icon={<DeleteOutlined />}
                  onClick={() => handleRemoveExtensionRow(row)}
                />
              </Tooltip>
            ) : (
              <Tooltip title="继承自父角色，不可移除">
                <InfoCircleOutlined style={{ color: '#999' }} />
              </Tooltip>
            )}
            {index === extensionRows.length - 1 ? (
              <Tooltip title="添加定制字段">
                <Button type="text" size="small" icon={<PlusOutlined />} onClick={handleAddExtensionRow} />
              </Tooltip>
            ) : null}
          </Space>
        </Col>
      </Row>
    );
  };

  if (!sortedGlobalDimensions.length && !pickableDictTypes.length && !customDimensions.length) {
    return null;
  }

  return (
    <Card
      title={(
        <Space>
          <FilterOutlined style={{ color: '#722ed1' }} />
          定制过滤
        </Space>
      )}
      size="small"
      style={{ marginBottom: 16 }}
      extra={(
        <Tooltip title={SCOPE_HELP}>
          <InfoCircleOutlined />
        </Tooltip>
      )}
    >
      {sortedGlobalDimensions.length ? (
        <>
          {sortedGlobalDimensions.map(renderBuiltinDimension)}
        </>
      ) : null}
      <div style={{ marginTop: sortedGlobalDimensions.length ? 8 : 0 }}>
        <Row gutter={16} style={{ marginBottom: 12 }}>
          <Col span={6}>
            <Space>
              <span style={{ fontWeight: 500 }}>定制字段</span>
            </Space>
          </Col>
          <Col span={18}>
            {extensionRows.length === 0 ? (
              <Button type="dashed" icon={<PlusOutlined />} onClick={handleAddExtensionRow}>
                添加定制字段
              </Button>
            ) : null}
          </Col>
        </Row>
        {extensionRows.map(renderExtensionRow)}
      </div>
    </Card>
  );
};

export default ScopeDimensionField;

/** 保存前去掉已激活但未选值的行 */
export function filterPersistableScopes(scopes) {
  if (!scopes?.length) {
    return [];
  }
  return scopes.filter((row) => {
    if (!isRowActivated(row)) {
      return false;
    }
    try {
      const parsed = JSON.parse(row.scopeValues || '[]');
      return Array.isArray(parsed) && parsed.length > 0;
    } catch (e) {
      return false;
    }
  });
}
