/*
 * 个人推荐中心（方案 5.2）
 * 展示我的推荐码、推荐链接、简要规则；与阶段 5 会员营销联动后可扩展
 */
import React, { useState, useEffect } from 'react';
import { Card, Input, Button, Alert, Spin, message, Statistic, Row, Col } from 'antd';
import { CopyOutlined, LinkOutlined, GiftOutlined, OrderedListOutlined, DollarOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { Link, connect } from 'umi';
import { getReferralInfo, getReferralSummary } from './service';
import styles from './index.less';

const ReferralCenter = (props) => {
  const { currentUser } = props;
  const [loading, setLoading] = useState(true);
  const [referralCode, setReferralCode] = useState('');
  const [referralUrl, setReferralUrl] = useState('');
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    getReferralInfo()
      .then((res) => {
        const data = res?.data?.data != null ? res.data.data : res?.data;
        if (data) {
          setReferralCode(data.referralCode || '');
          setReferralUrl(data.referralUrl || '');
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
    getReferralSummary()
      .then((res) => {
        const d = res?.data?.data != null ? res.data.data : res?.data;
        if (d && typeof d === 'object') setSummary(d);
      })
      .catch(() => {});
  }, []);

  const baseUrl = typeof window !== 'undefined' ? window.location.origin : '';
  const displayUrl = referralUrl || (referralCode ? `${baseUrl}/product?ref=${encodeURIComponent(referralCode)}` : '');

  const copyToClipboard = (text) => {
    if (!text) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(() => message.success('已复制'));
    } else {
      const ta = document.createElement('textarea');
      ta.value = text;
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
      message.success('已复制');
    }
  };

  if (loading) {
    return (
      <PageContainer title="个人推荐中心">
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer title="个人推荐中心" content="获取您的推荐码与推荐链接，分享给新用户下单享优惠，您可获得佣金">
      <Alert
        message="推荐规则说明"
        description={
          <ul style={{ margin: 0, paddingLeft: 18 }}>
            <li>将下方推荐链接或推荐码分享给新用户，新用户通过链接访问产品并下单时填写您的推荐码即可享优惠。</li>
            <li>推荐人可获得相应佣金，具体以《推荐营销规则》为准。</li>
          </ul>
        }
        type="info"
        showIcon
        style={{ marginBottom: 24 }}
      />
      <Card title="我的推荐码" className={styles.card}>
        <Input
          readOnly
          value={referralCode || '（加载中或未开通）'}
          addonAfter={
            referralCode ? (
              <Button type="link" size="small" icon={<CopyOutlined />} onClick={() => copyToClipboard(referralCode)}>
                复制
              </Button>
            ) : null
          }
          style={{ maxWidth: 360 }}
        />
      </Card>
      <Card title="推荐链接" className={styles.card}>
        <Input
          readOnly
          value={displayUrl || '（请先获取推荐码）'}
          addonAfter={
            displayUrl ? (
              <Button type="link" size="small" icon={<CopyOutlined />} onClick={() => copyToClipboard(displayUrl)}>
                复制
              </Button>
            ) : null
          }
          style={{ maxWidth: 560 }}
        />
        <p style={{ marginTop: 12, color: '#666', fontSize: 12 }}>新用户通过此链接访问产品中心并下单时，可填写您的推荐码享优惠。</p>
      </Card>
      {summary && (summary.totalPending != null || summary.totalSettled != null || summary.countPending > 0) && (
        <Card title="佣金汇总" className={styles.card} style={{ marginTop: 24 }}>
          <Row gutter={24}>
            <Col span={8}>
              <Statistic title="待结算" value={summary.totalPending != null ? Number(summary.totalPending) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}>共 {summary.countPending || 0} 笔</span>
            </Col>
            <Col span={8}>
              <Statistic title="已结算" value={summary.totalSettled != null ? Number(summary.totalSettled) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}>共 {summary.countSettled || 0} 笔</span>
            </Col>
            <Col span={8}>
              <Statistic title="已扣除" value={summary.totalDeducted != null ? Number(summary.totalDeducted) : 0} prefix="¥" />
              <span style={{ fontSize: 12, color: '#999' }}>共 {summary.countDeducted || 0} 笔</span>
            </Col>
          </Row>
        </Card>
      )}
      <div style={{ marginTop: 24 }}>
        <Link to="/order/list"><Button icon={<OrderedListOutlined />}>我的订单</Button></Link>
        <Link to="/order/list?referrerOnly=true" style={{ marginLeft: 8 }}><Button icon={<OrderedListOutlined />}>我推荐的订单</Button></Link>
        <Link to="/product" style={{ marginLeft: 8 }}><Button icon={<LinkOutlined />}>前往产品中心</Button></Link>
      </div>
    </PageContainer>
  );
};

export default connect(({ user }) => ({ currentUser: user.currentUser || {} }))(ReferralCenter);
