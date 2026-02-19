import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Tag, Input, Button, message, Spin } from 'antd';
import { Link, history, useParams } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { getTicketAdminDetail, adminReplyTicket } from './service';

const statusMap = {
  OPEN: { color: 'blue', text: '待处理' },
  PENDING: { color: 'orange', text: '处理中' },
  CLOSED: { color: 'default', text: '已关闭' },
};

const TicketAdminDetail = () => {
  const { id } = useParams();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [replyContent, setReplyContent] = useState('');
  const [replyLoading, setReplyLoading] = useState(false);

  const fetchDetail = () => {
    if (!id) return;
    setLoading(true);
    getTicketAdminDetail(id)
      .then((res) => {
        const data = res?.data?.data || res?.data;
        setDetail(data);
      })
      .catch(() => {
        message.error('工单不存在');
        setDetail(null);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchDetail();
  }, [id]);

  const handleReply = () => {
    if (!replyContent.trim()) {
      message.warning('请输入回复内容');
      return;
    }
    setReplyLoading(true);
    adminReplyTicket(id, replyContent)
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

  if (loading) {
    return (
      <PageContainer>
        <Spin tip="加载中..." />
      </PageContainer>
    );
  }

  if (!detail) {
    return (
      <PageContainer>
        <Card>工单不存在</Card>
      </PageContainer>
    );
  }

  const status = statusMap[detail.status] || { color: 'default', text: detail.status };

  return (
    <PageContainer
      title="工单详情"
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => history.push('/admin/sys/ticket')}>
          返回列表
        </Button>
      }
    >
      <Card>
        <Descriptions column={1} bordered>
          <Descriptions.Item label="工单号">{detail.ticketNo}</Descriptions.Item>
          <Descriptions.Item label="用户ID">{detail.userId}</Descriptions.Item>
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
                <div style={{ color: r.isStaff ? '#1890ff' : '#666' }}>
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
      </Card>
    </PageContainer>
  );
};

export default TicketAdminDetail;
