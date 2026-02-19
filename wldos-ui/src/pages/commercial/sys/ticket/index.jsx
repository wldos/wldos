import React, { useRef, useState, useEffect } from 'react';
import { Tag, Button, Drawer, Descriptions, Card, Input, message, Spin } from 'antd';
import { Link } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryTicketAdminList, getTicketAdminDetail, adminReplyTicket } from './service';

const statusMap = {
  OPEN: { color: 'blue', text: '待处理' },
  PENDING: { color: 'orange', text: '处理中' },
  CLOSED: { color: 'default', text: '已关闭' },
};

const TicketAdmin = () => {
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const [viewRecordId, setViewRecordId] = useState(null);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [replyContent, setReplyContent] = useState('');
  const [replyLoading, setReplyLoading] = useState(false);

  const fetchDetail = (id) => {
    if (!id) return;
    setDetailLoading(true);
    setDetail(null);
    getTicketAdminDetail(id)
      .then((res) => {
        const data = res?.data?.data || res?.data;
        setDetail(data);
      })
      .catch(() => {
        message.error('工单不存在');
        setDetail(null);
      })
      .finally(() => setDetailLoading(false));
  };

  useEffect(() => {
    if (viewRecordId) {
      fetchDetail(viewRecordId);
      setReplyContent('');
    } else {
      setDetail(null);
    }
  }, [viewRecordId]);

  const handleReply = () => {
    if (!viewRecordId || !replyContent.trim()) {
      message.warning('请输入回复内容');
      return;
    }
    setReplyLoading(true);
    adminReplyTicket(viewRecordId, replyContent)
      .then((res) => {
        const data = res?.data?.data || res?.data;
        setDetail(data);
        setReplyContent('');
        message.success('回复成功');
      })
      .catch((e) => {
        message.error(e?.data?.message || '回复失败');
      })
      .finally(() => setReplyLoading(false));
  };

  const columns = [
    { title: '工单号', dataIndex: 'ticketNo', width: 200 },
    { title: '用户ID', dataIndex: 'userId', width: 100, hideInSearch: true },
    { title: '用户账号', dataIndex: 'loginName', width: 160 },
    { title: '用户昵称', dataIndex: 'nickname', width: 160 },
    {
      title: '关联订单',
      dataIndex: 'orderNo',
      width: 180,
      hideInSearch: true,
      render: (orderNo) =>
        orderNo ? (
          <Link to={`/admin/sys/order?orderNo=${encodeURIComponent(orderNo)}`} target="_blank">
            {orderNo}
          </Link>
        ) : (
          '-'
        ),
    },
    { title: '主题', dataIndex: 'subject', ellipsis: true, hideInSearch: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
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
      render: (_, record) => (
        <Button type="link" size="small" onClick={() => setViewRecordId(record.id)}>
          查看
        </Button>
      ),
    },
  ];

  const status = detail ? (statusMap[detail.status] || { color: 'default', text: detail.status }) : null;

  return (
    <PageContainer
      title="工单管理"
      style={{ padding: '0', margin: '0' }}
      bodyStyle={{ padding: '24px', margin: '0' }}
    >
      <ProTableX
        headerTitle="工单列表"
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        request={async (params) => {
          const res = await queryTicketAdminList({
            current: params.current,
            pageSize: params.pageSize,
            userId: params.userId,
            loginName: params.loginName,
            nickname: params.nickname,
            status: params.status,
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

      <Drawer
        title={detail ? `工单详情 · ${detail.ticketNo}` : '工单详情'}
        width={560}
        open={!!viewRecordId}
        onClose={() => setViewRecordId(null)}
        destroyOnClose
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>
            <Spin tip="加载中..." />
          </div>
        ) : !detail ? (
          <div style={{ color: '#999' }}>工单不存在</div>
        ) : (
          <>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="工单号">{detail.ticketNo}</Descriptions.Item>
              <Descriptions.Item label="用户ID">{detail.userId}</Descriptions.Item>
              <Descriptions.Item label="用户账号">{detail.loginName || '-'}</Descriptions.Item>
              <Descriptions.Item label="用户昵称">{detail.nickname || '-'}</Descriptions.Item>
              {detail.orderNo && (
                <Descriptions.Item label="关联订单">
                  <Link to={`/admin/sys/order?orderNo=${encodeURIComponent(detail.orderNo)}`} target="_blank">
                    {detail.orderNo}
                  </Link>
                </Descriptions.Item>
              )}
              <Descriptions.Item label="主题">{detail.subject}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={status.color}>{status.text}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="创建时间">{detail.createTime}</Descriptions.Item>
            </Descriptions>

            <div style={{ marginTop: 24 }}>
              <h4>回复记录</h4>
              {detail.replies && detail.replies.length > 0 ? (
                detail.replies.map((r) => (
                  <Card key={r.id} size="small" style={{ marginBottom: 8 }}>
                    <div style={{ color: r.isStaff ? '#1890ff' : '#666', fontSize: 12 }}>
                      {r.isStaff ? '客服' : '用户'} · {r.createTime}
                    </div>
                    <div style={{ marginTop: 8 }}>{r.content}</div>
                  </Card>
                ))
              ) : (
                <div style={{ color: '#999' }}>暂无回复</div>
              )}
            </div>

            {detail.status !== 'CLOSED' && (
              <div style={{ marginTop: 24 }}>
                <Input.TextArea
                  rows={3}
                  placeholder="输入回复内容（客服回复）"
                  value={replyContent}
                  onChange={(e) => setReplyContent(e.target.value)}
                />
                <Button
                  type="primary"
                  style={{ marginTop: 8 }}
                  onClick={handleReply}
                  loading={replyLoading}
                >
                  客服回复
                </Button>
              </div>
            )}
          </>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TicketAdmin;
