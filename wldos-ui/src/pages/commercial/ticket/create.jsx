import React, { useState } from 'react';
import { Card, Form, Input, Button, message } from 'antd';
import { history, useLocation } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { createTicket } from './service';

const TicketCreate = () => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const location = useLocation();
  const orderNoFromUrl = typeof window !== 'undefined' ? new URLSearchParams(location?.search || '').get('orderNo') : null;

  const handleSubmit = () => {
    form.validateFields().then((values) => {
      const payload = { ...values };
      if (orderNoFromUrl) payload.orderNo = orderNoFromUrl;
      setLoading(true);
      createTicket(payload)
        .then(() => {
          message.success('工单提交成功');
          history.push('/ticket/list');
        })
        .catch((e) => {
          message.error(e?.data?.message || '提交失败');
        })
        .finally(() => setLoading(false));
    });
  };

  return (
    <PageContainer title="提交工单">
      <Card>
        {orderNoFromUrl && (
          <p style={{ marginBottom: 16, color: 'rgba(0,0,0,0.65)' }}>
            本工单将关联订单：<strong>{orderNoFromUrl}</strong>
          </p>
        )}
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            name="subject"
            label="主题"
            rules={[{ required: true, message: '请输入主题' }]}
          >
            <Input placeholder="请简要描述您的问题" maxLength={256} showCount />
          </Form.Item>
          <Form.Item
            name="content"
            label="详细描述"
            rules={[{ required: true, message: '请输入详细描述' }]}
          >
            <Input.TextArea rows={6} placeholder="请详细描述您遇到的问题" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              提交
            </Button>
            <Button style={{ marginLeft: 8 }} onClick={() => history.push('/ticket/list')}>
              返回
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer>
  );
};

export default TicketCreate;
