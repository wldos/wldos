import React, { useRef } from 'react';
import { Tag, Button } from 'antd';
import { Link } from 'umi';
import { PlusOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryTicketList } from './service';

const statusMap = {
  OPEN: { color: 'blue', text: '待处理' },
  PENDING: { color: 'orange', text: '处理中' },
  CLOSED: { color: 'default', text: '已关闭' },
};

export default function TicketList() {
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const columns = [
    { title: '工单号', dataIndex: 'ticketNo', width: 200, hideInSearch: true },
    { title: '主题', dataIndex: 'subject', ellipsis: true, hideInSearch: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      hideInSearch: true,
      render: (s) => {
        const m = statusMap[s] || { color: 'default', text: s };
        return <Tag color={m.color}>{m.text}</Tag>;
      },
    },
    { title: '创建时间', dataIndex: 'createTime', width: 180, hideInSearch: true },
    {
      title: '操作',
      key: 'action',
      width: 100,
      hideInSearch: true,
      render: (_, record) => <Link to={`/ticket/${record.id}`}>详情</Link>,
    },
  ];

  return (
    <PageContainer
      title="我的工单"
      extra={
        <Link to="/ticket/create">
          <Button type="primary" icon={<PlusOutlined />}>提交工单</Button>
        </Link>
      }
    >
      <ProTableX
        headerTitle="工单列表"
        actionRef={actionRef}
        rowKey="id"
        search={false}
        request={async (params) => {
          const res = await queryTicketList({
            current: params.current,
            pageSize: params.pageSize,
          });
          const data = res?.data?.data || res?.data || {};
          return {
            data: data.rows || data.list || [],
            total: data.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={columns}
      />
    </PageContainer>
  );
}
