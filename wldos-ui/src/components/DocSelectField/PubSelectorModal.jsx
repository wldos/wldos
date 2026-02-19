import React, { useEffect, useState } from 'react';
import { Modal, Input, Table, message } from 'antd';
import { queryBookList } from '@/pages/home/service';

/**
 * 存档文档选择弹窗（/archives 分页查询，支持所有类型文档）
 * 供 DocSelectField 及协议、产品管理等页面复用。
 */
const PubSelectorModal = ({ visible, onSelect, onCancel, title = '选择文档' }) => {
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
      message.error('加载存档文档列表失败');
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
    { title: '标题', dataIndex: 'pubTitle', ellipsis: true },
    { title: '类型', dataIndex: 'pubType', width: 80 },
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
          placeholder="搜索标题"
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
          showTotal: (t) => `共 ${t} 条`,
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
