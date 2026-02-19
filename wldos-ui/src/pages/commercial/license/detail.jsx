import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Button, message, Spin } from 'antd';
import { history, useParams } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { CopyOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { getLicenseDetail } from './service';

const statusMap = {
  ACTIVE: { color: 'green', text: '有效' },
  EXPIRED: { color: 'orange', text: '已过期' },
  REVOKED: { color: 'red', text: '已失效' },
};

const LicenseDetail = () => {
  const { id } = useParams();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getLicenseDetail(id)
      .then((res) => {
        const data = res?.data?.data || res?.data;
        setDetail(data);
      })
      .catch(() => {
        message.error('License 不存在或无权限');
        setDetail(null);
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleCopyKey = () => {
    const key = detail?.licenseKey;
    if (!key) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(key).then(() => {
        message.success('已复制到剪贴板');
      }).catch(() => {
        message.error('复制失败');
      });
    } else {
      const ta = document.createElement('textarea');
      ta.value = key;
      document.body.appendChild(ta);
      ta.select();
      try {
        document.execCommand('copy');
        message.success('已复制到剪贴板');
      } catch (e) {
        message.error('复制失败');
      }
      document.body.removeChild(ta);
    }
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
        <Card>License 不存在或无权限查看</Card>
      </PageContainer>
    );
  }

  const status = statusMap[detail.status] || { color: 'default', text: detail.status };

  return (
    <PageContainer
      title="License 详情"
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => history.push('/license/list')}>
          返回列表
        </Button>
      }
    >
      <Card>
        <Descriptions column={1} bordered>
          <Descriptions.Item label="插件编码">{detail.pluginCode}</Descriptions.Item>
          <Descriptions.Item label="订单号">{detail.orderNo}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <span style={{ color: status.color }}>{status.text}</span>
          </Descriptions.Item>
          <Descriptions.Item label="License Key">
            <span style={{ wordBreak: 'break-all' }}>{detail.licenseKey}</span>
            <Button
              type="link"
              icon={<CopyOutlined />}
              onClick={handleCopyKey}
              style={{ marginLeft: 8 }}
            >
              复制
            </Button>
          </Descriptions.Item>
          <Descriptions.Item label="过期时间">{detail.expireTime || '-'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{detail.createTime}</Descriptions.Item>
        </Descriptions>
      </Card>
    </PageContainer>
  );
};

export default LicenseDetail;
