import React, { useRef, useState } from 'react';
import { Tag, Button, Modal, Form, Input, message } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { useLocation } from 'umi';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryOrderAdminList, refundOrderAdmin } from './service';

const statusMap = {
  PENDING: { color: 'orange', text: '待支付' },
  PAID: { color: 'blue', text: '已支付' },
  COMPLETED: { color: 'green', text: '已完成' },
  CLOSED: { color: 'default', text: '已关闭' },
  REFUNDED: { color: 'purple', text: '已退款' },
  ABNORMAL: { color: 'red', text: '异常' },
};

const REFUNDABLE_STATUS = ['PAID', 'COMPLETED'];

const OrderAdmin = () => {
  const actionRef = useRef();
  const location = useLocation();
  const orderNoFromUrl = typeof window !== 'undefined' ? new URLSearchParams(location?.search || '').get('orderNo') : null;
  const [refundModalVisible, setRefundModalVisible] = useState(false);
  const [refundOrderNo, setRefundOrderNo] = useState('');
  const [refundSubmitting, setRefundSubmitting] = useState(false);
  const [refundForm] = Form.useForm();
  useDesktopSticky(actionRef);

  const handleRefundOk = async () => {
    try {
      const values = await refundForm.validateFields();
      setRefundSubmitting(true);
      await refundOrderAdmin(refundOrderNo, values?.reason);
      message.success('退款成功');
      setRefundModalVisible(false);
      refundForm.resetFields();
      setRefundOrderNo('');
      actionRef.current?.reload();
    } catch (e) {
      if (e?.errorFields) return;
      message.error(e?.data?.message || e?.message || '退款失败');
    } finally {
      setRefundSubmitting(false);
    }
  };

  const openRefundModal = (orderNo) => {
    setRefundOrderNo(orderNo);
    refundForm.resetFields();
    setRefundModalVisible(true);
  };

  const columns = [
    { title: '订单号', dataIndex: 'orderNo', width: 180 },
    { title: '用户ID', dataIndex: 'userId', width: 100, hideInSearch: true },
    { title: '商品', dataIndex: 'productCode', width: 120 },
    { title: '金额', dataIndex: 'totalAmount', width: 100, render: (v) => `¥${v}` },
    {
      title: '推荐人ID',
      dataIndex: 'referrerUserId',
      width: 100,
      hideInSearch: false,
    },
    {
      title: '优惠/佣金',
      key: 'referral',
      width: 120,
      hideInSearch: true,
      render: (_, r) => (
        <span>
          {r.discountAmount > 0 && `¥${r.discountAmount} `}
          {r.commissionStatus && <Tag>{r.commissionStatus}</Tag>}
          {!r.discountAmount && !r.commissionStatus && '-'}
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (s) => {
        const m = statusMap[s] || { color: 'default', text: s };
        return <Tag color={m.color}>{m.text}</Tag>;
      },
    },
    { title: '支付渠道', dataIndex: 'payChannel', width: 100 },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      hideInSearch: true,
      render: (_, record) => {
        const canRefund = REFUNDABLE_STATUS.includes(record.status);
        return (
          <>
            {canRefund && (
              <Button type="link" size="small" danger onClick={() => openRefundModal(record.orderNo)}>
                退款
              </Button>
            )}
          </>
        );
      },
    },
  ];

  return (
    <PageContainer
      title="订单管理"
      style={{ padding: '0', margin: '0' }}
      bodyStyle={{ padding: '24px', margin: '0' }}
    >
      <ProTableX
        headerTitle="订单列表"
        actionRef={actionRef}
        rowKey="orderNo"
        search={{ labelWidth: 100 }}
        request={async (params) => {
          const orderNo = params.orderNo || orderNoFromUrl;
          const res = await queryOrderAdminList({
            current: params.current,
            pageSize: params.pageSize,
            status: params.status,
            referrerUserId: params.referrerUserId,
            orderNo: orderNo || undefined,
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
      <Modal
        title="确认退款"
        open={refundModalVisible}
        onOk={handleRefundOk}
        onCancel={() => { setRefundModalVisible(false); refundForm.resetFields(); setRefundOrderNo(''); }}
        confirmLoading={refundSubmitting}
        okText="确认退款"
        destroyOnClose
      >
        <p style={{ marginBottom: 16 }}>
          订单号：<strong>{refundOrderNo}</strong>，退款后订单状态将变为「已退款」，若存在推荐佣金将自动扣回。
        </p>
        <Form form={refundForm} layout="vertical">
          <Form.Item name="reason" label="退款原因（选填）">
            <Input.TextArea rows={3} placeholder="如：用户申请、误购等" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default OrderAdmin;
