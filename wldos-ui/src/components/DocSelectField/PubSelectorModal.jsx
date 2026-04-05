import React, { useEffect, useState } from 'react';
import { Modal, Input, Table, message } from 'antd';
import { useIntl } from 'umi';
import { queryBookList } from '@/pages/home/service';

/**
 * 存档文档选择弹窗（/archives 分页查询，支持所有类型文档）
 * 供 DocSelectField 及协议、产品管理等页面复用。
 */
const PubSelectorModal = ({ visible, onSelect, onCancel, title }) => {
  const intl = useIntl();
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const fetchList = async (page = 1, size = pageSize) => {
    setLoading(true);
    try {
      const res = await queryBookList({
        path: '/archives',
        current: page,
        pageSize: size,
        ...(keyword ? { pubTitle: keyword } : {}),
      });
      const data = res?.data || res;
      const rows = data?.rows || data?.list || [];
      const totalCount = data?.total || 0;
      setDataSource(Array.isArray(rows) ? rows : []);
      setTotal(totalCount || 0);
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
      setCurrent(1);
      fetchList(1, pageSize);
    }
  }, [visible]);

  const handleSearch = () => {
    setCurrent(1);
    fetchList(1, pageSize);
  };

  const handleTableChange = (pagination) => {
    setCurrent(pagination.current);
    setPageSize(pagination.pageSize);
    fetchList(pagination.current, pagination.pageSize);
  };

  const handleRowClick = (record) => {
    onSelect?.(record.id, record.pubTitle || record.title);
    onCancel?.();
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    {
      title: intl.formatMessage({ id: 'component.docSelect.col.title' }),
      dataIndex: 'pubTitle',
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'component.docSelect.col.type' }),
      dataIndex: 'pubType',
      width: 80,
    },
  ];

  return (
    <Modal
      title={title}
      open={visible}
      onCancel={onCancel}
      footer={null}
      width={640}
      destroyOnClose
    >
      <div style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder={intl.formatMessage({ id: 'component.docSelect.search.placeholder' })}
          allowClear
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onSearch={handleSearch}
          style={{ width: 300 }}
        />
      </div>
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
          onClick: () => handleRowClick(record),
          style: { cursor: 'pointer' },
        })}
      />
    </Modal>
  );
};

export default PubSelectorModal;
