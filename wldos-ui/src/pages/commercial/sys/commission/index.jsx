/*
 * 管理端佣金统计（9.4.2）、佣金管控（9.2.4）
 */
import React, { useRef, useEffect, useState } from 'react';
import { Card, Tag, Row, Col, Statistic, Button, Modal, Form, Input, Select, message } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryCommissionList, queryCommissionSummary, updateCommission } from './service';

const statusMap = {
  PENDING: { color: 'orange', text: '待结算' },
  SETTLED: { color: 'green', text: '已结算' },
  DEDUCTED: { color: 'red', text: '已扣除' },
  CANCELLED: { color: 'default', text: '已取消' },
};

export default function CommissionAdmin() {
  const actionRef = useRef();
  useDesktopSticky(actionRef);
  const [summary, setSummary] = useState(null);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [editForm] = Form.useForm();
  const [editLoading, setEditLoading] = useState(false);

  useEffect(() => {
    queryCommissionSummary()
      .then((res) => {
        const d = res?.data?.data != null ? res.data.data : res?.data;
        if (d && typeof d === 'object') setSummary(d);
      })
      .catch(() => {});
  }, []);

  const handleEdit = (record) => {
    setEditingRecord(record);
    editForm.setFieldsValue({ status: record.status || 'PENDING', remark: record.remark || '' });
    setEditModalVisible(true);
  };

  const handleEditOk = () => {
    editForm.validateFields().then((values) => {
      if (!editingRecord?.id) return;
      setEditLoading(true);
      updateCommission(editingRecord.id, { status: values.status, remark: values.remark })
        .then(() => {
          message.success('已更新');
          setEditModalVisible(false);
          setEditingRecord(null);
          actionRef.current?.reload();
          queryCommissionSummary().then((res) => {
            const d = res?.data?.data != null ? res.data.data : res?.data;
            if (d && typeof d === 'object') setSummary(d);
          });
        })
        .catch(() => message.error('更新失败'))
        .finally(() => setEditLoading(false));
    });
  };

  const columns = [
    {
      title: '推荐人ID',
      dataIndex: 'referrerUserId',
      valueType: 'digit',
      width: 110,
      hideInTable: true,
      search: { transform: (v) => (v == null || v === '' ? undefined : { referrerUserId: v }) },
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueType: 'select',
      valueEnum: {
        PENDING: { text: '待结算' },
        SETTLED: { text: '已结算' },
        DEDUCTED: { text: '已扣除' },
      },
      width: 100,
      hideInTable: true,
      search: { transform: (v) => (v == null || v === '' ? undefined : { status: v }) },
    },
    { title: 'ID', dataIndex: 'id', width: 100, ellipsis: true, hideInSearch: true },
    { title: '订单ID', dataIndex: 'orderId', width: 100, hideInSearch: true },
    { title: '订单号', dataIndex: 'orderNo', width: 180, hideInSearch: true },
    { title: '推荐人ID', dataIndex: 'referrerUserId', width: 100, hideInSearch: true },
    { title: '佣金金额', dataIndex: 'amount', width: 100, hideInSearch: true, render: (v) => (v != null ? `¥${v}` : '-') },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      hideInSearch: true,
      render: (s) => {
        const m = statusMap[s] || { color: 'default', text: s || '-' };
        return <Tag color={m.color}>{m.text}</Tag>;
      },
    },
    { title: '结算时间', dataIndex: 'settleTime', width: 160, hideInSearch: true },
    { title: '扣除原因', dataIndex: 'deductReason', ellipsis: true, hideInSearch: true },
    { title: '备注', dataIndex: 'remark', ellipsis: true, hideInSearch: true },
    { title: '创建时间', dataIndex: 'createTime', width: 160, hideInSearch: true },
    {
      title: '操作',
      key: 'action',
      width: 80,
      fixed: 'right',
      hideInSearch: true,
      render: (_, record) => (
        <Button type="link" size="small" onClick={() => handleEdit(record)}>编辑</Button>
      ),
    },
  ];

  return (
    <PageContainer title="佣金统计" content="推荐订单产生的佣金记录与汇总">
      {summary && (
        <Row gutter={24} style={{ marginBottom: 24 }}>
          <Col span={8}>
            <Card>
              <Statistic title="待结算" value={summary.totalPending != null ? Number(summary.totalPending) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}> 共 {summary.countPending || 0} 笔</span>
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="已结算" value={summary.totalSettled != null ? Number(summary.totalSettled) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}> 共 {summary.countSettled || 0} 笔</span>
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="已扣除" value={summary.totalDeducted != null ? Number(summary.totalDeducted) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}> 共 {summary.countDeducted || 0} 笔</span>
            </Card>
          </Col>
        </Row>
      )}
      <ProTableX
        headerTitle="佣金记录"
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        request={async (params) => {
          const res = await queryCommissionList({
            current: params.current,
            pageSize: params.pageSize,
            referrerUserId: params.referrerUserId,
            status: params.status,
          });
          const d = res?.data?.data != null ? res.data.data : res?.data;
          const list = d?.rows || d?.list || [];
          const total = d?.total ?? 0;
          return { data: list, total, success: res?.code === 200 };
        }}
        columns={columns}
      />
      <Modal
        title="编辑佣金"
        open={editModalVisible}
        onOk={handleEditOk}
        onCancel={() => { setEditModalVisible(false); setEditingRecord(null); }}
        confirmLoading={editLoading}
        destroyOnClose
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="status" label="状态" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="PENDING">待结算</Select.Option>
              <Select.Option value="SETTLED">已结算</Select.Option>
              <Select.Option value="DEDUCTED">已扣除</Select.Option>
              <Select.Option value="CANCELLED">已取消</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="调整原因、备注" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
