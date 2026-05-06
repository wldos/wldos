import React from 'react';
import { Modal, Table, Tag, Empty, Space, Button } from 'antd';

const columns = [
  { title: '记录ID', dataIndex: 'id', width: 90 },
  { title: '平台', dataIndex: 'platform', width: 120 },
  { title: '状态', dataIndex: 'status', render: (v) => <Tag>{v || '-'}</Tag>, width: 120 },
  { title: '提交时间', dataIndex: 'createTime' },
];

const PublishRecordModal = ({
  open,
  onClose,
  rows = [],
  loading = false,
  onRefresh,
}) => (
  <Modal
    title="发布记录"
    open={open}
    onCancel={onClose}
    footer={[
      <Button key="refresh" onClick={onRefresh}>刷新</Button>,
      <Button key="close" type="primary" onClick={onClose}>关闭</Button>,
    ]}
    width={860}
    destroyOnClose
  >
    {rows.length ? (
      <Table
        rowKey={(r) => String(r.id || Math.random())}
        columns={columns}
        dataSource={rows}
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    ) : (
      <Empty description="暂无发布记录" />
    )}
  </Modal>
);

export default PublishRecordModal;
