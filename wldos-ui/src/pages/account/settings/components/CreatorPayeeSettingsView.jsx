import React, { useEffect, useState } from 'react';
import { Card, Form, Input, Button, message, Typography, Switch, Divider, Statistic, Row, Col, Spin } from 'antd';
import { BankOutlined, QrcodeOutlined } from '@ant-design/icons';
import {
  fetchCreatorPayee,
  saveCreatorPayee,
  fetchContentTipAuthorSummary,
} from '@/pages/commercial/cms-access/service';
import QrUploadField from '@/pages/commercial/cms-access/components/QrUploadField';

const { Paragraph } = Typography;

/**
 * 作者收款与结算（独立数据域：cms 创作者 payee，与基本资料无关）。
 */
const CreatorPayeeSettingsView = ({ showTipStats = true }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [tipStats, setTipStats] = useState(null);

  useEffect(() => {
    if (!showTipStats) {
      return undefined;
    }
    let cancelled = false;
    fetchContentTipAuthorSummary().then((res) => {
      if (!cancelled && res?.code === 200) {
        setTipStats(res.data);
      }
    });
    return () => {
      cancelled = true;
    };
  }, [showTipStats]);

  useEffect(() => {
    let cancelled = false;
    fetchCreatorPayee()
      .then((res) => {
        if (cancelled || res?.code !== 200 || !res.data) {
          return;
        }
        const d = res.data;
        form.setFieldsValue({
          ...d,
          tipPersonalQrOnly: d.tipPersonalQrOnly === '1' || d.tipPersonalQrOnly === true,
        });
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [form]);

  const onFinish = async (values) => {
    setSaving(true);
    try {
      const payload = {
        ...values,
        tipPersonalQrOnly: values.tipPersonalQrOnly ? '1' : '0',
      };
      const res = await saveCreatorPayee(payload);
      if (res?.code !== 200) {
        return;
      }
      message.success('已保存');
      if (res.data) {
        form.setFieldsValue({
          ...res.data,
          tipPersonalQrOnly: res.data.tipPersonalQrOnly === '1' || res.data.tipPersonalQrOnly === true,
        });
      }
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <Spin />;
  }

  return (
    <>
      {showTipStats && tipStats && (
        <Card size="small" style={{ marginBottom: 16 }}>
          <Row gutter={24}>
            <Col>
              <Statistic title="平台打赏笔数" value={tipStats.tipCount ?? 0} />
            </Col>
            <Col>
              <Statistic title="平台打赏累计（元）" value={tipStats.totalAmount ?? 0} precision={2} />
            </Col>
          </Row>
        </Card>
      )}
      <Card title={<><BankOutlined /> 我的收款账号</>} bordered={false}>
        <Paragraph type="secondary">
          付费阅读进入平台商户，此处银行/支付宝账号供平台线下结算。文末打赏可上传个人微信/支付宝收款码供读者扫码；也可继续使用平台在线打赏，或勾选「仅展示个人码」关闭在线打赏。
        </Paragraph>
        <Form form={form} layout="vertical" onFinish={onFinish} style={{ maxWidth: 520 }}>
          <Divider orientation="left"><QrcodeOutlined /> 打赏收款码</Divider>
          <Form.Item name="wechatQrPath" label="微信收款码">
            <QrUploadField label="微信收款码" />
          </Form.Item>
          <Form.Item name="alipayQrPath" label="支付宝收款码">
            <QrUploadField label="支付宝收款码" />
          </Form.Item>
          <Form.Item
            name="tipPersonalQrOnly"
            label="文末仅展示个人码"
            valuePropName="checked"
            extra="开启后读者只看到收款码，不展示平台在线打赏；关闭则个人码与在线打赏可同时展示。"
          >
            <Switch />
          </Form.Item>
          <Divider orientation="left"><BankOutlined /> 线下结算账号</Divider>
          <Form.Item name="accountName" label="收款户名">
            <Input placeholder="与支付宝/银行卡一致的姓名" maxLength={64} />
          </Form.Item>
          <Form.Item name="alipayAccount" label="支付宝账号">
            <Input placeholder="手机号或邮箱" maxLength={128} />
          </Form.Item>
          <Form.Item name="wechatAccount" label="微信收款说明">
            <Input placeholder="微信号或平台约定的收款说明" maxLength={128} />
          </Form.Item>
          <Form.Item name="bankName" label="开户行">
            <Input maxLength={128} />
          </Form.Item>
          <Form.Item name="bankAccount" label="银行卡号">
            <Input maxLength={64} />
          </Form.Item>
          <Form.Item name="remark" label="补充说明">
            <Input.TextArea rows={2} maxLength={256} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={saving}>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </>
  );
};

export default CreatorPayeeSettingsView;
