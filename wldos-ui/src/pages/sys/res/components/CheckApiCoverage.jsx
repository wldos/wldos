import React, {forwardRef, useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState} from 'react';
import {Button, Empty, Input, Select, Space, Table, Tag} from 'antd';
import {PlusOutlined, SearchOutlined} from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import {checkApiCoverage} from '../service';

const APP_CODE_OPTIONS = [
  {label: '管理端(admin)', value: 'admin'},
  {label: '门户(portal)', value: 'portal'},
];

const METHOD_OPTIONS = [
  {label: '全部方法', value: ''},
  {label: 'GET', value: 'GET'},
  {label: 'POST', value: 'POST'},
  {label: 'PUT', value: 'PUT'},
  {label: 'DELETE', value: 'DELETE'},
  {label: 'PATCH', value: 'PATCH'},
];

const PAGINATION_HEIGHT = 56;
const TABLE_HEADER_HEIGHT = 39;
const NON_FULLSCREEN_MAX_SCROLL = 480;

export const buildApiKey = (method, path) => `${method || 'GET'}:${path || ''}`;

const formatCheckedAt = (ts) => {
  if (!ts) return '';
  const d = new Date(ts);
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
};

const CheckApiCoverage = forwardRef((props, ref) => {
  const {modalVisible, onCancel, onQuickCreate} = props;
  const [appCode, setAppCode] = useState('admin');
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [methodFilter, setMethodFilter] = useState('');
  const [sourceMenuFilter, setSourceMenuFilter] = useState('');
  const [displayItems, setDisplayItems] = useState([]);
  const [checkedAt, setCheckedAt] = useState(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [tableScrollY, setTableScrollY] = useState(NON_FULLSCREEN_MAX_SCROLL);

  const cacheRef = useRef(new Map());
  const checkedAtRef = useRef(new Map());
  const tableHostRef = useRef(null);

  const loadCacheForApp = (code) => {
    const cached = cacheRef.current.get(code);
    setDisplayItems(cached || []);
    setCheckedAt(checkedAtRef.current.get(code) || null);
  };

  const updateTableScrollY = useCallback(() => {
    const host = tableHostRef.current;
    if (!host) {
      return;
    }
    const hostHeight = host.clientHeight;
    if (hostHeight <= 0) {
      return;
    }
    const bodyHeight = hostHeight - TABLE_HEADER_HEIGHT - PAGINATION_HEIGHT;
    if (isFullscreen) {
      setTableScrollY(Math.max(120, bodyHeight));
    } else {
      setTableScrollY(Math.max(120, Math.min(NON_FULLSCREEN_MAX_SCROLL, bodyHeight)));
    }
  }, [isFullscreen]);

  useEffect(() => {
    if (modalVisible) {
      loadCacheForApp(appCode);
    }
  }, [modalVisible, appCode]);

  useEffect(() => {
    if (!modalVisible) {
      return undefined;
    }
    updateTableScrollY();
    const host = tableHostRef.current;
    if (!host || typeof ResizeObserver === 'undefined') {
      window.addEventListener('resize', updateTableScrollY);
      return () => window.removeEventListener('resize', updateTableScrollY);
    }
    const observer = new ResizeObserver(() => updateTableScrollY());
    observer.observe(host);
    window.addEventListener('resize', updateTableScrollY);
    return () => {
      observer.disconnect();
      window.removeEventListener('resize', updateTableScrollY);
    };
  }, [modalVisible, isFullscreen, checkedAt, keyword, methodFilter, sourceMenuFilter, updateTableScrollY]);

  useImperativeHandle(ref, () => ({
    resolveItem(apiMethod, apiPath) {
      const key = buildApiKey(apiMethod, apiPath);
      setDisplayItems((prev) => prev.filter((row) => buildApiKey(row.apiMethod, row.apiPath) !== key));
      const cached = cacheRef.current.get(appCode) || [];
      cacheRef.current.set(
        appCode,
        cached.filter((row) => buildApiKey(row.apiMethod, row.apiPath) !== key),
      );
    },
  }));

  const handleCheck = async () => {
    setLoading(true);
    try {
      const res = await checkApiCoverage({appCode});
      const items = res?.data || [];
      const now = Date.now();
      cacheRef.current.set(appCode, items);
      checkedAtRef.current.set(appCode, now);
      setDisplayItems(items);
      setCheckedAt(now);
    } finally {
      setLoading(false);
    }
  };

  const handleAppChange = (code) => {
    setAppCode(code);
    setKeyword('');
    setMethodFilter('');
    setSourceMenuFilter('');
    loadCacheForApp(code);
  };

  const handleClose = () => {
    onCancel();
  };

  const sourceMenuOptions = useMemo(() => {
    const names = [...new Set(displayItems.map((i) => i.sourceMenuName).filter(Boolean))];
    return [{label: '全部来源页面', value: ''}, ...names.map((n) => ({label: n, value: n}))];
  }, [displayItems]);

  const filteredItems = useMemo(() => {
    const kw = keyword.trim().toLowerCase();
    return displayItems.filter((row) => {
      if (methodFilter && (row.apiMethod || 'GET') !== methodFilter) {
        return false;
      }
      if (sourceMenuFilter && row.sourceMenuName !== sourceMenuFilter) {
        return false;
      }
      if (!kw) {
        return true;
      }
      const haystack = [
        row.sourceMenuName,
        row.sourceMenuPath,
        row.apiName,
        row.suggestedResourceName,
        row.apiPath,
        `${row.apiMethod || 'GET'} ${row.apiPath}`,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();
      return haystack.includes(kw);
    });
  }, [displayItems, keyword, methodFilter, sourceMenuFilter]);

  const hasChecked = checkedAt != null;
  const columns = [
    {title: '来源页面', dataIndex: 'sourceMenuName', width: 120, ellipsis: true},
    {title: '页面路径', dataIndex: 'sourceMenuPath', width: 160, ellipsis: true},
    {
      title: 'API 名称',
      dataIndex: 'apiName',
      width: 140,
      ellipsis: true,
      render: (_, record) => record.apiName || record.suggestedResourceName || '—',
    },
    {
      title: '遗漏 API',
      width: 200,
      ellipsis: true,
      render: (_, record) => `${record.apiMethod || 'GET'} ${record.apiPath}`,
    },
    {
      title: '操作',
      width: 80,
      fixed: 'right',
      render: (_, record) => (
        <Button type="link" size="small" icon={<PlusOutlined />} onClick={() => onQuickCreate?.(record)}>
          创建
        </Button>
      ),
    },
  ];

  const rootStyle = isFullscreen
    ? {height: '100%', display: 'flex', flexDirection: 'column', minHeight: 0}
    : {display: 'flex', flexDirection: 'column'};

  const tableHostStyle = isFullscreen
    ? {flex: 1, minHeight: 0, overflow: 'hidden', display: 'flex', flexDirection: 'column'}
    : {maxHeight: NON_FULLSCREEN_MAX_SCROLL + TABLE_HEADER_HEIGHT + PAGINATION_HEIGHT + 8};

  return (
    <FullscreenModal
      width={960}
      bodyStyle={{padding: 24, ...(isFullscreen ? {display: 'flex', flexDirection: 'column'} : {})}}
      title="检查遗漏 API"
      visible={modalVisible}
      onCancel={handleClose}
      onFullscreenChange={setIsFullscreen}
      footer={<Button onClick={handleClose}>关闭</Button>}
    >
      <div style={rootStyle}>
        <Space style={{marginBottom: 12, width: '100%', justifyContent: 'space-between', flexShrink: 0}} wrap>
          <Space wrap>
            <Select style={{width: 160}} options={APP_CODE_OPTIONS} value={appCode} onChange={handleAppChange} />
            <Button type="primary" icon={<SearchOutlined />} loading={loading} onClick={handleCheck}>
              {hasChecked ? '重新检查' : '检查'}
            </Button>
          </Space>
          {hasChecked && (
            <Space wrap>
              <span style={{color: '#999'}}>上次检查：{formatCheckedAt(checkedAt)}</span>
              <Tag color="warning">共 {displayItems.length} 条遗漏</Tag>
              {filteredItems.length !== displayItems.length && (
                <Tag>筛选后 {filteredItems.length} 条</Tag>
              )}
            </Space>
          )}
        </Space>

        {hasChecked && (
          <Space style={{marginBottom: 12, flexShrink: 0}} wrap>
            <Input
              allowClear
              style={{width: 220}}
              placeholder="关键词：页面/API 名称/路径"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <Select
              style={{width: 120}}
              options={METHOD_OPTIONS}
              value={methodFilter}
              onChange={setMethodFilter}
            />
            <Select
              style={{width: 180}}
              options={sourceMenuOptions}
              value={sourceMenuFilter}
              onChange={setSourceMenuFilter}
              showSearch
              optionFilterProp="label"
            />
          </Space>
        )}

        {hasChecked ? (
          filteredItems.length > 0 ? (
            <>
              <div ref={tableHostRef} style={tableHostStyle}>
                <Table
                  rowKey={(row) => `${row.sourceMenuPath}-${row.apiMethod}-${row.apiPath}`}
                  size="small"
                  columns={columns}
                  dataSource={filteredItems}
                  scroll={{x: 860, y: tableScrollY}}
                  pagination={{
                    defaultPageSize: 20,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    showTotal: (total) => `共 ${total} 条`,
                  }}
                />
              </div>
              <div style={{marginTop: 12, color: '#999', flexShrink: 0}}>
                点击「创建」将打开预填表单；保存后资源清单自动刷新，本列表移除对应项，无需重新检查。
              </div>
            </>
          ) : (
            <Empty description={displayItems.length > 0 ? '筛选无结果' : '未发现遗漏的 API 资源声明'} />
          )
        ) : (
          <Empty description="选择应用后点击「检查」" />
        )}
      </div>
    </FullscreenModal>
  );
});

CheckApiCoverage.displayName = 'CheckApiCoverage';

export default CheckApiCoverage;
