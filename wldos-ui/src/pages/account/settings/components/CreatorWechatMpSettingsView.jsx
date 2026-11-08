import React, { useEffect, useState } from 'react';
import { Card, Form, Input, Button, message, Typography, Spin, Switch } from 'antd';
import { WechatOutlined } from '@ant-design/icons';
import GuideQrFileUploadField from '@/pages/commercial/cms-access/components/GuideQrFileUploadField';
import {
  fetchCreatorWechatMp,
  saveCreatorWechatMp,
} from '@/pages/commercial/cms-access/service';

const { Paragraph } = Typography;

const CreatorWechatMpSettingsView = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const mpQrFileId = Form.useWatch('mpQrFileId', form);
  const mpQrPreviewUrl = Form.useWatch('mpQrPreviewUrl', form);

  useEffect(() => {
    let cancelled = false;
    fetchCreatorWechatMp()
      .then((res) => {
        if (cancelled || res?.code !== 200) {
          return;
        }
        const d = res.data || {};
        form.setFieldsValue({
          ...d,
          mpQrFileId: d.mpQrFileId != null ? String(d.mpQrFileId) : null,
          mpQrPreviewUrl: d.mpQrUrl || null,
          enabled: d.enabled !== '0',
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
        mpQrFileId: values.mpQrFileId != null ? String(values.mpQrFileId).trim() : undefined,
        leadAnchorPubId:
          values.leadAnchorPubId != null && String(values.leadAnchorPubId).trim() !== ''
            ? String(values.leadAnchorPubId).trim()
            : undefined,
        enabled: values.enabled ? '1' : '0',
      };
      delete payload.mpQrPreviewUrl;
      const res = await saveCreatorWechatMp(payload);
      if (res?.code !== 200) {
        return;
      }
      message.success('已保存');
      if (res.data) {
        form.setFieldsValue({
          ...res.data,
          mpQrFileId: res.data.mpQrFileId != null ? String(res.data.mpQrFileId) : null,
          mpQrPreviewUrl: res.data.mpQrUrl || null,
          enabled: res.data.enabled !== '0',
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
    <Card title={<><WechatOutlined /> 我的公众号（查看码引流）</>} bordered={false}>
      <Paragraph type="secondary">
        与「创作与收款」同级：读者端展示本页二维码；微信服务器 URL 填本站
        {' '}
        <Typography.Text code>/api/wechat/mp/callback</Typography.Text>
        ，Token 与下方一致。开启「自动回复领码」后按需生成查看码（非预生成码池）；各篇章的解锁规则与有效期在作品/章节的「访问与解锁」里配置。
      </Paragraph>
      <Form form={form} layout="vertical" onFinish={onFinish} style={{ maxWidth: 560 }}>
        <Form.Item name="accountName" label="名称">
          <Input placeholder="便于自己识别" maxLength={64} />
        </Form.Item>
        <Form.Item
          name="callbackToken"
          label="公众平台服务器 Token"
          rules={[{ required: true, message: '必填' }]}
        >
          <Input placeholder="与微信公众平台配置一致" maxLength={64} />
        </Form.Item>
        <Form.Item name="appId" label="AppId（可选）">
          <Input maxLength={32} />
        </Form.Item>
        <Form.Item name="claimKeywords" label="触发关键词">
          <Input placeholder="解锁,查看码,阅读" />
        </Form.Item>
        <Form.Item
          name="leadAnchorPubId"
          label="发码默认篇章 pubId（可选）"
          extra="填某一章/篇的 pubId，公众号回复的码只解锁该篇章；不填则自动选你最近更新且启用「查看码解锁」的篇章。"
        >
          <Input placeholder="章节 pub_id，字符串勿 Number" />
        </Form.Item>
        <Form.Item label="公众号二维码" required>
          <GuideQrFileUploadField
            fileId={mpQrFileId}
            previewUrl={mpQrPreviewUrl}
            onChange={({ fileId, previewUrl }) => {
              form.setFieldsValue({ mpQrFileId: fileId, mpQrPreviewUrl: previewUrl });
            }}
          />
        </Form.Item>
        <Form.Item name="mpQrFileId" hidden>
          <Input />
        </Form.Item>
        <Form.Item name="mpQrPreviewUrl" hidden>
          <Input />
        </Form.Item>
        <Form.Item name="enabled" label="启用自动回复领码" valuePropName="checked">
          <Switch />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={saving}>
            保存
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default CreatorWechatMpSettingsView;
