import React, { useCallback, useEffect, useState } from 'react';
import { Cascader } from 'antd';
import {
  fetchRegionChildren,
  fetchRegionPaths,
} from '../service';

const REGION_LEVEL_BY_DIM = {
  prov_code: 1,
  city_code: 2,
  county_code: 3,
};

const toCascadeOption = (node) => ({
  value: node.value,
  label: node.label,
  isLeaf: node.leaf === true,
});

const pathToValue = (path) => (path && path.length ? path[path.length - 1] : null);

const pathsToCascaderValue = (paths) => (paths || []).map((path) => path.map((node) => node.value));

const findRootOption = (options, value) => (options || []).find((item) => item.value === value);

const isValidSelectionPath = (path, selectLevel, options) => {
  if (!path?.length) {
    return false;
  }
  if (selectLevel === 1) {
    return path.length === 1;
  }
  if (selectLevel === 2) {
    if (path.length === 2) {
      return true;
    }
    if (path.length === 1) {
      const root = findRootOption(options, path[0]);
      return root?.isLeaf === true;
    }
    return false;
  }
  if (selectLevel === 3) {
    return path.length === 3 || path.length === 2;
  }
  return false;
};

const injectChildren = (nodes, targetValue, children) => (nodes || []).map((node) => {
  if (node.value === targetValue) {
    return {
      ...node,
      loading: false,
      children,
      isLeaf: false,
    };
  }
  if (node.children?.length) {
    return { ...node, children: injectChildren(node.children, targetValue, children) };
  }
  return node;
});

const setNodeLoading = (nodes, targetValue, loading) => (nodes || []).map((node) => {
  if (node.value === targetValue) {
    return { ...node, loading };
  }
  if (node.children?.length) {
    return { ...node, children: setNodeLoading(node.children, targetValue, loading) };
  }
  return node;
});

const applyPathsToTree = (tree, apiPaths) => {
  const roots = [...(tree || [])];
  (apiPaths || []).forEach((path) => {
    let list = roots;
    path.forEach((node, idx) => {
      const isLast = idx === path.length - 1;
      let existing = list.find((item) => item.value === node.value);
      if (!existing) {
        existing = {
          value: node.value,
          label: node.label,
          isLeaf: node.leaf === true || isLast,
        };
        list.push(existing);
      }
      if (!isLast) {
        if (!existing.children) {
          existing.children = [];
        }
        list = existing.children;
      }
    });
  });
  return roots;
};

const RegionCascadeSelect = ({ dimensionCode, value = [], onChange, disabled }) => {
  const selectLevel = REGION_LEVEL_BY_DIM[dimensionCode] || 3;
  const [options, setOptions] = useState([]);
  const [cascaderValue, setCascaderValue] = useState([]);

  const loadRoot = useCallback(() => {
    fetchRegionChildren({ parentId: 0, selectLevel }).then((res) => {
      if (res?.data) {
        setOptions(res.data.map(toCascadeOption));
      }
    });
  }, [selectLevel]);

  useEffect(() => {
    loadRoot();
  }, [loadRoot]);

  useEffect(() => {
    const codes = (value || []).map(String).filter(Boolean);
    if (!codes.length) {
      setCascaderValue([]);
      return;
    }
    fetchRegionPaths({ codes: codes.join(','), selectLevel }).then((res) => {
      if (!res?.data?.length) {
        setCascaderValue([]);
        return;
      }
      setCascaderValue(pathsToCascaderValue(res.data));
      setOptions((prev) => applyPathsToTree(prev, res.data));
    });
  }, [value, selectLevel]);

  const loadData = (selectedOptions) => {
    const target = selectedOptions[selectedOptions.length - 1];
    const targetValue = target.value;
    setOptions((prev) => setNodeLoading(prev, targetValue, true));
    fetchRegionChildren({ parentId: targetValue, selectLevel })
      .then((res) => {
        const children = (res?.data || []).map(toCascadeOption);
        setOptions((prev) => injectChildren(prev, targetValue, children));
      })
      .catch(() => {
        setOptions((prev) => setNodeLoading(prev, targetValue, false));
      });
  };

  const handleChange = (nextPaths) => {
    const normalized = (nextPaths || []).filter((path) => isValidSelectionPath(path, selectLevel, options));
    setCascaderValue(normalized);
    const codes = normalized.map(pathToValue).filter(Boolean);
    onChange?.(codes);
  };

  return (
    <Cascader
      options={options}
      loadData={loadData}
      changeOnSelect={false}
      multiple
      showSearch={{
        filter: (inputValue, path) => path.some(
          (option) => (option.label || '').toLowerCase().includes(inputValue.toLowerCase()),
        ),
      }}
      placeholder="级联选择行政区划"
      value={cascaderValue}
      onChange={handleChange}
      disabled={disabled}
      style={{ width: '100%' }}
      displayRender={(labels) => labels.join(' / ')}
    />
  );
};

export default RegionCascadeSelect;
