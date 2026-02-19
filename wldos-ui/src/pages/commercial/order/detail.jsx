import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Button, Tag, message, Tabs, List } from 'antd';
import { Link, history, useParams } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { getOrderDetail, cancelOrder } from './service';
import { getAgreementListByType } from '@/pages/user/login/agreement';

const statusMap = {
  PENDING: { color: 'orange', text: '待支付' },
  PAID: { color: 'blue', text: '已支付' },
  COMPLETED: { color: 'green', text: '已完成' },
  CLOSED: { color: 'default', text: '已关闭' },
  REFUNDED: { color: 'purple', text: '已退款' },
  ABNORMAL: { color: 'red', text: '异常' },
};

const OrderDetail = () => {
  const { orderNo } = useParams();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [orderAgreements, setOrderAgreements] = useState([]);

  useEffect(() => {
    if (orderNo) {
      getOrderDetail(orderNo)
        .then((res) => setDetail(res?.data))
        .catch(() => setDetail(null))
        .finally(() => setLoading(false));
    }
  }, [orderNo]);

  useEffect(() => {
    getAgreementListByType('ORDER').then((rows) => setOrderAgreements(Array.isArray(rows) ? rows : []));
  }, []);

  const handleCancel = async () => {
    try {
      await cancelOrder(orderNo);
      message.success('已取消');
      setDetail((d) => (d ? { ...d, status: 'CLOSED' } : null));
    } catch (e) {
      message.error(e?.data?.message || '取消失败');
    }
  };

  if (loading) return <PageContainer><Card loading /></PageContainer>;
  if (!detail) return <PageContainer><Card>订单不存在</Card></PageContainer>;

  const status = statusMap[detail.status] || { color: 'default', text: detail.status };

  const tabItems = [
    {
      key: 'info',
      label: '订单信息',
      children: (
        <>
          <Descriptions column={1} bordered>
            <Descriptions.Item label="订单号">{detail.orderNo}</Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={status.color}>{status.text}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="商品">{detail.productCode}</Descriptions.Item>
            <Descriptions.Item label="金额">¥{detail.totalAmount}</Descriptions.Item>
            {detail.discountAmount > 0 && (
              <Descriptions.Item label="推荐抵扣">¥{detail.discountAmount}</Descriptions.Item>
            )}
            {(detail.referralCode || detail.referrerUserId) && (
              <Descriptions.Item label="推荐">推荐码: {detail.referralCode || '-'}</Descriptions.Item>
            )}
            {(detail.commissionStatus || detail.commissionAmount != null) && (
              <Descriptions.Item label="佣金">
                {detail.commissionAmount != null ? `¥${detail.commissionAmount}` : '-'}（{detail.commissionStatus || '-'}）
              </Descriptions.Item>
            )}
            <Descriptions.Item label="支付渠道">{detail.payChannel || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{detail.createTime}</Descriptions.Item>
            <Descriptions.Item label="支付时间">{detail.payTime || '-'}</Descriptions.Item>
          </Descriptions>
          <div style={{ marginTop: 16 }}>
            {detail.status === 'PENDING' && (
              <>
                <Button type="primary" href={detail.payUrl} target="_blank" rel="noopener noreferrer">
                  去支付
                </Button>
                <Button danger style={{ marginLeft: 8 }} onClick={handleCancel}>
                  取消订单
                </Button>
              </>
            )}
            <Button style={{ marginLeft: 8 }} onClick={() => history.push('/order/list')}>
              返回列表
            </Button>
          </div>
        </>
      ),
    },
    {
      key: 'agreement',
      label: '服务协议',
      children: (
        <div style={{ padding: '8px 0' }}>
          <p style={{ color: 'rgba(0,0,0,0.65)', marginBottom: 16 }}>
            本订单下单时需同意以下下单协议（与结算页一致），您可在此查看当前生效的协议：
          </p>
          {orderAgreements.length > 0 ? (
            <List
              size="small"
              dataSource={orderAgreements}
              renderItem={(a) => (
                <List.Item>
                  <Link to="/agreement" target="_blank" rel="noopener noreferrer">
                    {a.title || a.agreementCode || '协议'}
                  </Link>
                </List.Item>
              )}
            />
          ) : (
            <p style={{ color: 'rgba(0,0,0,0.45)' }}>
              <Link to="/agreement" target="_blank" rel="noopener noreferrer">查看《wldos 服务协议》《支付条款》《退款政策》《推荐营销规则》等</Link>
            </p>
          )}
        </div>
      ),
    },
  ];

  return (
    <PageContainer title={`订单 ${orderNo}`} onBack={() => history.push('/order/list')}>
      <Card>
        <Tabs items={tabItems} />
      </Card>
    </PageContainer>
  );
};

export default OrderDetail;
