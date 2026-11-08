import React, { useEffect, useRef, useState } from 'react';
import { Modal, Input, Table, message, Button, Space, Select } from 'antd';
import { useIntl } from 'umi';
import { queryBookList } from '@/pages/home/service';
import { queryAdminPubPicker } from './cmsPickerService';
import { isChapterContainerPubType } from './cmsPickerUtils';
import PubPickerDetailDrawer from './PubPickerDetailDrawer';
import { usePubVisibilityScopeEnum } from '@/hooks/usePubVisibilityScopeEnum';
import { usePubTypeEnum } from '@/hooks/usePubTypeEnum';

/**
 * 文档选择弹窗。
 * - archives：前台 /archives（排除 page、INTERNAL_ONLY、UNLISTED）
 * - admin：管理端列表（含内部可见；排除 page 类型）
 */
const PubSelectorModal = ({
  visible,
  onSelect,
  onCancel,
  title,
  pickerSource = 'archives',
}) => {
  const intl = useIntl();
  const { labelOf: visibilityLabelOf } = usePubVisibilityScopeEnum();
  const { options: pubTypeOptions, labelOf: pubTypeLabelOf } = usePubTypeEnum({ excludeValues: ['page'] });
  const [keyword, setKeyword] = useState('');
  const [pubTypeFilter, setPubTypeFilter] = useState(undefined);
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [drawerPubId, setDrawerPubId] = useState(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  const filtersRef = useRef({ keyword: '', pubType: undefined, pageSize: 10 });
  filtersRef.current = { keyword, pubType: pubTypeFilter, pageSize };

  const fetchList = async (page, size, searchKeyword, typeFilter) => {
    setLoading(true);
    try {
      const kw = (searchKeyword ?? '').trim();
      const searchParams = kw
        ? pickerSource === 'admin'
          ? { keyword: kw }
          : { pubTitle: kw }
        : {};
      const typeParams = typeFilter ? { pubType: typeFilter } : {};
      const res =
        pickerSource === 'admin'
          ? await queryAdminPubPicker({
              current: page,
              pageSize: size,
              parentId: 0,
              ...searchParams,
              ...typeParams,
            })
          : await queryBookList({
              path: '/archives',
              current: page,
              pageSize: size,
              ...searchParams,
              ...typeParams,
            });
      const data = res?.data || res;
      let rows = data?.rows || data?.list || [];
      if (pickerSource === 'admin' && Array.isArray(rows)) {
        rows = rows.filter((row) => row.pubType !== 'page');
      }
      const totalCount = data?.total ?? 0;
      setDataSource(Array.isArray(rows) ? rows : []);
      setTotal(totalCount);
    } catch (e) {
      message.error(intl.formatMessage({ id: 'component.docSelect.msg.loadFail' }));
      setDataSource([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (visible) {
      setKeyword('');
      setPubTypeFilter(undefined);
      setCurrent(1);
      fetchList(1, pageSize, '', undefined);
    }
  }, [visible]);

  /** @param {{ keyword?: string, pubType?: string | undefined, pageSize?: number }} [patch] */
  const loadList = (page, patch = {}) => {
    const base = filtersRef.current;
    const nextKeyword = 'keyword' in patch ? patch.keyword : base.keyword;
    const nextPubType = 'pubType' in patch ? patch.pubType : base.pubType;
    const nextPageSize = 'pageSize' in patch ? patch.pageSize : base.pageSize;
    if ('keyword' in patch) {
      setKeyword(nextKeyword ?? '');
    }
    if ('pubType' in patch) {
      setPubTypeFilter(nextPubType);
    }
    if ('pageSize' in patch) {
      setPageSize(nextPageSize);
    }
    filtersRef.current = {
      keyword: nextKeyword ?? '',
      pubType: nextPubType,
      pageSize: nextPageSize,
    };
    setCurrent(page);
    fetchList(page, nextPageSize, nextKeyword ?? '', nextPubType);
  };

  const handleKeywordSearch = (value) => {
    loadList(1, { keyword: value ?? '' });
  };

  const handleKeywordChange = (e) => {
    const next = e.target.value;
    setKeyword(next);
    if (next === '') {
      loadList(1, { keyword: '' });
    }
  };

  const handlePubTypeChange = (value) => {
    loadList(1, { pubType: value == null || value === '' ? undefined : value });
  };

  const handleTableChange = (pagination) => {
    loadList(pagination.current, { pageSize: pagination.pageSize });
  };

  const handlePick = (record) => {
    onSelect?.(record.id, record.pubTitle || record.title, record);
    onCancel?.();
  };

  const openDetail = (record, e) => {
    e?.stopPropagation();
    setDrawerPubId(record.id);
    setDrawerOpen(true);
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 200,
      ellipsis: true,
      render: (id) => (id != null ? String(id) : '—'),
    },
    ...(pickerSource === 'admin'
      ? [
          {
            title: intl.formatMessage({ id: 'component.docSelect.col.created' }),
            dataIndex: 'createTime',
            width: 168,
            render: (t) => (t ? String(t).replace('T', ' ').slice(0, 19) : '—'),
          },
        ]
      : []),
    {
      title: intl.formatMessage({ id: 'component.docSelect.col.title' }),
      dataIndex: 'pubTitle',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'component.docSelect.col.type' }),
      dataIndex: 'pubType',
      width: 96,
      render: (v) => pubTypeLabelOf(v),
    },
    ...(pickerSource === 'admin'
      ? [
          {
            title: intl.formatMessage({ id: 'component.docSelect.col.visibility' }),
            dataIndex: 'visibilityScope',
            width: 96,
            render: (v) => visibilityLabelOf(v || 'PUBLIC_LISTED'),
          },
        ]
      : []),
    {
      title: intl.formatMessage({ id: 'component.docSelect.col.action' }),
      width: 140,
      render: (_, record) => (
        <Space size="small" onClick={(e) => e.stopPropagation()}>
          <Button type="link" size="small" onClick={(e) => openDetail(record, e)}>
            {intl.formatMessage({ id: 'component.docSelect.action.detail' })}
          </Button>
          {!isChapterContainerPubType(record.pubType) ? (
            <Button type="link" size="small" onClick={() => handlePick(record)}>
              {intl.formatMessage({ id: 'component.docSelect.action.pick' })}
            </Button>
          ) : null}
        </Space>
      ),
    },
  ];

  return (
    <>
      <Modal
        title={title}
        open={visible}
        onCancel={onCancel}
        footer={null}
        width={960}
        destroyOnClose
      >
        <form
          style={{ marginBottom: 16 }}
          onSubmit={(e) => {
            e.preventDefault();
            e.stopPropagation();
            loadList(1);
          }}
        >
          <Space wrap>
            <Input.Search
              placeholder={intl.formatMessage({ id: 'component.docSelect.search.placeholder' })}
              allowClear
              value={keyword}
              onChange={handleKeywordChange}
              onSearch={handleKeywordSearch}
              style={{ width: 360 }}
            />
            <Select
              allowClear
              placeholder={intl.formatMessage({ id: 'component.docSelect.filter.pubType' })}
              options={pubTypeOptions}
              value={pubTypeFilter}
              onChange={handlePubTypeChange}
              style={{ width: 160 }}
            />
          </Space>
        </form>
        <Table
          size="small"
          loading={loading}
          dataSource={dataSource}
          columns={columns}
          rowKey="id"
          pagination={{
            current,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (t) =>
              intl.formatMessage({ id: 'component.docSelect.pagination.total' }, { total: t }),
          }}
          onChange={handleTableChange}
          onRow={(record) => ({
            onClick: () => {
              if (isChapterContainerPubType(record.pubType)) {
                openDetail(record);
              } else {
                handlePick(record);
              }
            },
            style: { cursor: 'pointer' },
          })}
        />
      </Modal>
      <PubPickerDetailDrawer
        open={drawerOpen}
        pubId={drawerPubId}
        mode="picker"
        onClose={() => setDrawerOpen(false)}
        onPick={(id, pubTitle, meta) => {
          onSelect?.(id, pubTitle, meta);
          setDrawerOpen(false);
          onCancel?.();
        }}
      />
    </>
  );
};

export default PubSelectorModal;
