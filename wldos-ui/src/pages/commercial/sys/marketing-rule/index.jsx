/*
 * 推荐营销规则配置（9.3.2）
 * 佣金比例、优惠比例/上限、规则启用；与产品 supportRecommendDiscount 共同控制推荐入口
 */
import React, { useEffect, useState } from 'react';
import { Card, Form, InputNumber, Radio, Button, message } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { getMarketingRule, saveMarketingRule } from './service';

const MarketingRuleAdmin = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [hasRule, setHasRule] = useState(false);

  useEffect(() => {
    setLoading(true);
    getMarketingRule()
      .then((res) => {
        const d = res?.data?.data != null ? res.data.data : res?.data;
        if (d && (d.id || d.ruleCode)) {
          setHasRule(true);
          form.setFieldsValue({
            id: d.id,
            commissionRate: d.commissionRate != null ? Number(d.commissionRate) : 0.05,
            discountRate: d.discountRate != null ? Number(d.discountRate) : 0.1,
            discountAmountMax: d.discountAmountMax != null ? Number(d.discountAmountMax) : 50,
            isEnabled: d.isEnabled === '0' ? '0' : '1',
            remark: d.remark || '',
          });
        } else {
          form.setFieldsValue({
            commissionRate: 0.05,
            discountRate: 0.1,
            discountAmountMax: 50,
            isEnabled: '1',
            remark: '',
          });
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [form]);

  const onFinish = (values) => {
    setSaving(true);
    const payload = {
      id: values.id,
      ruleCode: 'DEFAULT',
      commissionRate: values.commissionRate,
      discountRate: values.discountRate,
      discountAmountMax: values.discountAmountMax,
      isEnabled: values.isEnabled,
      remark: values.remark || null,
    };
    saveMarketingRule(payload)
      .then(() => {
        message.success('保存成功');
        setHasRule(true);
        if (payload.id) form.setFieldsValue({ id: payload.id });
      })
      .catch(() => message.error('保存失败'))
      .finally(() => setSaving(false));
  };

  return (
    <PageContainer title="推荐营销规则" content="全局默认规则：佣金比例、新用户优惠比例与上限；产品是否展示推荐入口由产品管理中的「推荐优惠」控制">
      <Card loading={loading}>
        <Form form={form} layout="vertical" onFinish={onFinish} style={{ maxWidth: 480 }}>
          <Form.Item name="id" hidden>
            <input type="hidden" />
          </Form.Item>
          <Form.Item
            name="commissionRate"
            label="佣金比例"
            rules={[{ required: true }]}
            extra="如 0.05 表示 5%，推荐人获得订单实付金额的该比例作为佣金"
          >
            <InputNumber min={0} max={1} step={0.01} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="discountRate"
            label="新用户优惠比例"
            extra="如 0.1 表示 10%，新用户使用推荐码可享订单金额的该比例抵扣（需校验接口与结算页配合）"
          >
            <InputNumber min={0} max={1} step={0.01} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="discountAmountMax"
            label="单笔优惠金额上限（元）"
            extra="新用户单笔订单推荐抵扣不超过该金额"
          >
            <InputNumber min={0} precision={2} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="isEnabled" label="规则启用" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value="1">启用</Radio>
              <Radio value="0">停用</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} placeholder="规则说明、备注" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={saving}>保存</Button>
          </Form.Item>
        </Form>
      </Card>
    </PageContainer>
  );
};

export default MarketingRuleAdmin;
