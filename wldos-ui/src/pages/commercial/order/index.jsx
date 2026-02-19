import React, { useRef } from 'react';
import { Tag, message } from 'antd';
import { Link } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryOrderList, cancelOrder } from './service';

const statusMap = {
  PENDING: { color: 'orange', text: '待支付' },
  PAID: { color: 'blue', text: '已支付' },
  COMPLETED: { color: 'green', text: '已完成' },
  CLOSED: { color: 'default', text: '已关闭' },
  REFUNDED: { color: 'purple', text: '已退款' },
  ABNORMAL: { color: 'red', text: '异常' },
};

export default function OrderList() {
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const handleCancel = async (orderNo) => {
    try {
      await cancelOrder(orderNo);
      message.success('已取消');
      actionRef.current?.reload();
    } catch (e) {
      message.error(e?.data?.message || '取消失败');
    }
  };

  const columns = [
    {
      title: '范围',
      dataIndex: 'referrerOnly',
      valueType: 'select',
      valueEnum: { '': { text: '全部订单' }, true: { text: '我推荐的订单' } },
      hideInTable: true,
      search: { transform: (v) => (v === '' ? undefined : { referrerOnly: v }) },
    },
    { title: '订单号', dataIndex: 'orderNo', width: 180, hideInSearch: true },
    { title: '商品', dataIndex: 'productCode', width: 120, hideInSearch: true },
    {
      title: '金额',
      dataIndex: 'totalAmount',
      width: 100,
      hideInSearch: true,
      render: (v) => (v != null ? `¥${v}` : '-'),
    },
    {
      title: '优惠/推荐',
      key: 'referral',
      width: 100,
      hideInSearch: true,
      render: (_, r) =>
        r.discountAmount > 0 ? (
          <span title={r.referralCode ? `推荐码: ${r.referralCode}` : ''}>¥{r.discountAmount} 抵扣</span>
        ) : r.referralCode ? (
          <Tag color="blue">推荐</Tag>
        ) : (
          '-'
        ),
    },
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
      width: 180,
      fixed: 'right',
      hideInSearch: true,
      render: (_, record) => (
        <>
          <Link to={`/order/${record.orderNo}`}>详情</Link>
          {record.status === 'PENDING' && (
            <>
              <span style={{ margin: '0 8px' }}>|</span>
              <a onClick={() => handleCancel(record.orderNo)}>取消</a>
              {record.payUrl && (
                <>
                  <span style={{ margin: '0 8px' }}>|</span>
                  <a href={record.payUrl} target="_blank" rel="noopener noreferrer">
                    支付
                  </a>
                </>
              )}
            </>
          )}
        </>
      ),
    },
  ];

  return (
    <PageContainer title="我的订单">
      <ProTableX
        headerTitle="订单列表"
        actionRef={actionRef}
        rowKey="orderNo"
        search={{ labelWidth: 100 }}
        request={async (params) => {
          const res = await queryOrderList({
            current: params.current,
            pageSize: params.pageSize,
            referrerOnly: params.referrerOnly === true || params.referrerOnly === 'true',
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
