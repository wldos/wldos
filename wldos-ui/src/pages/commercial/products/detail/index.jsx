/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Button, Spin, Tag, Tabs, Input, Alert } from 'antd';
import { DownloadOutlined, ShoppingCartOutlined, ArrowLeftOutlined, AppstoreOutlined, SafetyOutlined, GiftOutlined } from '@ant-design/icons';
import { history, useParams, Link, connect } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import config from '@/utils/config';
import { getStoreProductDetail } from '../service';
import { validateReferralCode } from '../../checkout/service';
import RecommendDiscountModal from '../../components/RecommendDiscountModal';
import styles from '../detail.less';

const { TabPane } = Tabs;

const STORE_BASE = '/store';
const { prefix } = config;

const PluginDetail = (props) => {
  const { dispatch, currentUser } = props;
  const { id } = useParams();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedLicense, setSelectedLicense] = useState('TIME_BASED'); // TIME_BASED | LIFETIME
  const [referralCode, setReferralCode] = useState('');
  const [referralValid, setReferralValid] = useState(null); // null | true | false
  const [recommendModalVisible, setRecommendModalVisible] = useState(false);

  useEffect(() => {
    if (detail && (detail.originalPriceLifetime != null && Number(detail.originalPriceLifetime) > 0) && !(detail.originalPriceTimeBased != null && Number(detail.originalPriceTimeBased) > 0)) {
      setSelectedLicense('LIFETIME');
    } else if (detail) {
      setSelectedLicense('TIME_BASED');
    }
  }, [detail]);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getStoreProductDetail(id)
      .then((res) => {
        const data = res?.data?.data != null ? res.data.data : res?.data;
        setDetail(data);
        if (data && dispatch) {
          dispatch({
            type: 'user/saveTdk',
            payload: {
              title: (data.displayTitle || data.pluginName || '产品') + ' - 产品中心',
              keywords: data.tags || data.displayTitle || data.pluginName || '',
              description: data.displaySummary || data.displayTitle || data.pluginName || '',
              crumbs: [],
            },
          });
        }
      })
      .catch(() => setDetail(null))
      .finally(() => setLoading(false));
  }, [id, dispatch]);

  const getCoverUrl = (coverImage) => {
    if (!coverImage) return null;
    if (coverImage.startsWith('http')) return coverImage;
    const path = coverImage.startsWith('/') ? coverImage : `/${coverImage}`;
    return `${STORE_BASE}${path}`;
  };

  const defaultCoverPlaceholder = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="280" height="200"><rect width="100%" height="100%" fill="#f0f0f0"/><text x="50%" y="50%" fill="#999" font-size="14" text-anchor="middle" dy=".3em">暂无封面</text></svg>'
  )));

  const handleCoverError = (e) => {
    if (e?.target && e.target.src !== defaultCoverPlaceholder) {
      e.target.onerror = null;
      e.target.src = defaultCoverPlaceholder;
    }
  };

  const handleBuy = () => {
    if (!detail) return;
    const productId = detail.pluginId || detail.id;
    const productCode = detail.pluginCode || '';
    let amount = detail.price || 0;
    if (hasDualLicense && selectedLicense === 'LIFETIME' && detail.originalPriceLifetime != null) {
      amount = Number(detail.originalPriceLifetime);
    } else if (hasDualLicense && selectedLicense === 'TIME_BASED' && detail.originalPriceTimeBased != null) {
      amount = Number(detail.originalPriceTimeBased);
    }
    let q = `productId=${productId}&productCode=${productCode}&amount=${amount}&displayId=${detail.id}`;
    if (referralCode && referralCode.trim()) q += `&referralCode=${encodeURIComponent(referralCode.trim())}`;
    history.push(`/checkout?${q}`);
  };

  const commercialTypeText = {
    OPEN_SOURCE: '开源',
    FREE_COMMERCIAL: '免费商业',
    PAID_COMMERCIAL: '付费',
  };

  if (loading) {
    return (
      <PageContainer>
        <div style={{ textAlign: 'center', padding: 80 }}>
          <Spin size="large" />
        </div>
      </PageContainer>
    );
  }

  if (!detail) {
    return (
      <PageContainer>
        <Card>产品不存在或已下架</Card>
      </PageContainer>
    );
  }

  const title = detail.displayTitle || detail.pluginName || '未命名';
  const cover = getCoverUrl(detail.coverImage) || getCoverUrl(detail.previewPic);
  const priceNum = detail.price != null ? Number(detail.price) : 0;
  const hasTimeBased = detail.originalPriceTimeBased != null && Number(detail.originalPriceTimeBased) > 0;
  const hasLifetime = detail.originalPriceLifetime != null && Number(detail.originalPriceLifetime) > 0;
  const hasDualLicense = hasTimeBased || hasLifetime;
  const isFree = priceNum <= 0 && !hasTimeBased && !hasLifetime;
  const priceText = priceNum > 0 ? `¥${detail.price}` : (hasTimeBased || hasLifetime ? '见下方许可' : '免费');
  const hasRichContent = detail.content || detail.contentHtml;
  const summaryText = detail.displaySummary;
  const tagList = detail.tags ? String(detail.tags).split(/[,，、\s]+/).filter(Boolean) : [];
  const supportRecommend = detail.supportRecommendDiscount === '1' || detail.supportRecommendDiscount === true;

  // 下载链接：优先用后端返回的 downloadUrl，否则用 pluginCode 走下载接口
  const rawUrl = detail.downloadUrl;
  const downloadHref =
    rawUrl && (rawUrl.startsWith('http://') || rawUrl.startsWith('https://'))
      ? rawUrl
      : rawUrl && rawUrl.startsWith('/')
        ? `${typeof window !== 'undefined' ? window.location.origin : ''}${rawUrl}`
        : detail.pluginCode
          ? `${prefix}/_store/plugins/download/${detail.pluginCode}/latest`
          : null;
  const canDownload = !!downloadHref;

  const buyAmount = hasDualLicense && selectedLicense === 'LIFETIME' && detail.originalPriceLifetime != null
    ? Number(detail.originalPriceLifetime)
    : hasDualLicense && selectedLicense === 'TIME_BASED' && detail.originalPriceTimeBased != null
      ? Number(detail.originalPriceTimeBased)
      : detail.price != null ? Number(detail.price) : 0;
  const buyPriceText = buyAmount > 0 ? `¥${buyAmount}` : '免费';

  return (
    <PageContainer
      title={title}
      onBack={() => history.push('/product')}
      backIcon={<ArrowLeftOutlined />}
    >
      <Alert
        message="购买须知"
        description={
          <ul style={{ margin: 0, paddingLeft: 18 }}>
            <li>购买即视为同意<Link to="/agreement">《wldos 服务协议》</Link>、《推荐营销规则》及对应产品专项条款。</li>
            <li>wldos 不提供付费在线服务，仅离线软件许可付费；离线软件支持本地激活与在线检测。</li>
            <li>推荐码优惠仅适用于新用户，通过推荐链接或推荐码下单享优惠；未输入则不享受。<a onClick={() => setRecommendModalVisible(true)} style={{ marginLeft: 6 }}><GiftOutlined /> 推荐优惠说明</a></li>
          </ul>
        }
        type="info"
        showIcon
        icon={<SafetyOutlined />}
        className={styles.complianceAlert}
      />
      <div className={styles.detailLayout}>
        <div className={styles.left}>
          <Card>
            {cover ? (
              <img src={cover} alt={title} className={styles.coverImg} onError={handleCoverError} />
            ) : (
              <img src={defaultCoverPlaceholder} alt={title} className={styles.coverImg} />
            )}
            {hasDualLicense && (
              <div className={styles.licenseSwitch}>
                <span className={styles.licenseSwitchLabel}>许可类型：</span>
                <Button.Group>
                  {hasTimeBased && (
                    <Button type={selectedLicense === 'TIME_BASED' ? 'primary' : 'default'} size="small" onClick={() => setSelectedLicense('TIME_BASED')}>
                      按时间 ¥{detail.originalPriceTimeBased}
                    </Button>
                  )}
                  {hasLifetime && (
                    <Button type={selectedLicense === 'LIFETIME' ? 'primary' : 'default'} size="small" onClick={() => setSelectedLicense('LIFETIME')}>
                      终身制 ¥{detail.originalPriceLifetime}
                    </Button>
                  )}
                </Button.Group>
              </div>
            )}
            {supportRecommend && (
              <div className={styles.referralInput}>
                <Input
                  placeholder="输入推荐码享优惠，未输入则不享受"
                  value={referralCode}
                  onChange={(e) => { setReferralCode(e.target.value); setReferralValid(null); }}
                  onBlur={async () => {
                    if (!referralCode || !referralCode.trim()) { setReferralValid(null); return; }
                    try {
                      const res = await validateReferralCode(referralCode.trim());
                      const data = res?.data?.data != null ? res.data.data : res?.data;
                      setReferralValid(data?.valid === true);
                    } catch (e) { setReferralValid(false); }
                  }}
                  allowClear
                />
                {referralValid === true && <span style={{ color: '#52c41a', fontSize: 12 }}>推荐码有效，下单可享优惠</span>}
                {referralValid === false && <span style={{ color: '#ff4d4f', fontSize: 12 }}>推荐码无效或已失效</span>}
              </div>
            )}
            <div className={styles.actions}>
              {isFree && canDownload && (
                <a href={downloadHref} download target="_blank" rel="noopener noreferrer" style={{ display: 'block', marginBottom: 8 }}>
                  <Button type="primary" size="large" icon={<DownloadOutlined />} block>下载</Button>
                </a>
              )}
              {(detail.supportTrial === '1' || detail.supportTrial === true) && (
                <Link to={`/product/trial?productId=${detail.id}`} style={{ display: 'block', marginBottom: 8 }}>
                  <Button size="large" block>免费试用</Button>
                </Link>
              )}
              {(!isFree || hasDualLicense) && (
                <Button type="primary" size="large" icon={<ShoppingCartOutlined />} block onClick={handleBuy} style={{ marginBottom: 8 }}>
                  购买 {buyPriceText}
                </Button>
              )}
              {isFree && !hasDualLicense && !canDownload && (
                <Button size="large" block disabled style={{ marginBottom: 8 }}>暂无下载</Button>
              )}
            </div>
            <div className={styles.leftOverview}>
              <div className={styles.leftOverviewRow}>
                <span>版本</span>
                <span>{detail.version || '-'}</span>
              </div>
              <div className={styles.leftOverviewRow}>
                <span>下载</span>
                <span>{detail.downCount ?? 0}</span>
              </div>
              <div className={styles.leftOverviewRow}>
                <span>星级</span>
                <span>{detail.starCount ?? 0}</span>
              </div>
              {detail.categoryName && (
                <div className={styles.leftOverviewRow}>
                  <span>分类</span>
                  <span>{detail.categoryName}</span>
                </div>
              )}
              {tagList.length > 0 && (
                <div className={styles.leftTags}>
                  {tagList.map((t) => (
                    <Tag key={t} className={styles.leftTag}>{t}</Tag>
                  ))}
                </div>
              )}
            </div>
          </Card>
        </div>
        <div className={styles.right}>
          <Tabs defaultActiveKey="intro" className={styles.detailTabs}>
            <TabPane tab="功能介绍" key="intro">
              <Card title="产品信息" className={styles.infoCard}>
                <Descriptions column={2} bordered size="small">
                  <Descriptions.Item label="产品名称" span={2}>{title}</Descriptions.Item>
                  <Descriptions.Item label="版本">{detail.version || '-'}</Descriptions.Item>
                  <Descriptions.Item label="价格">{priceText}</Descriptions.Item>
                  <Descriptions.Item label="类型">{commercialTypeText[detail.commercialType] || detail.commercialType}</Descriptions.Item>
                  <Descriptions.Item label="分类">{detail.categoryName || '-'}</Descriptions.Item>
                  <Descriptions.Item label="下载量">{detail.downCount ?? 0}</Descriptions.Item>
                  <Descriptions.Item label="星级">{detail.starCount ?? 0}</Descriptions.Item>
                  <Descriptions.Item label="简介" span={2}>{detail.displaySummary || '-'}</Descriptions.Item>
                </Descriptions>
              </Card>
              <Card title="产品详情" className={styles.detailCard}>
                {hasRichContent ? (
                  <div
                    className={styles.content}
                    dangerouslySetInnerHTML={{
                      __html: detail.contentHtml || detail.content || '',
                    }}
                  />
                ) : (
                  <div className={styles.detailBody}>
                    <p className={summaryText ? styles.detailSummary : styles.detailNoMore}>
                      {summaryText || '暂无更多介绍'}
                    </p>
                  </div>
                )}
              </Card>
            </TabPane>
            <TabPane tab="授权规则" key="license">
              <Card className={styles.tabCard}>
                <p>本产品为<strong>离线软件许可</strong>，仅支持本地部署与使用。</p>
                <p>许可类型说明：</p>
                <ul>
                  <li><strong>按时间购买</strong>：在有效期内使用，到期后可续费或重新购买。</li>
                  <li><strong>终身制</strong>：一次购买，永久使用（以服务协议为准）。</li>
                </ul>
                <p>具体授权范围与限制以《wldos 服务协议》及产品专项条款为准。</p>
              </Card>
            </TabPane>
            <TabPane tab="更新日志" key="changelog">
              <Card className={styles.tabCard}>
                {detail.versionHistory ? (
                  <pre className={styles.preBlock}>{detail.versionHistory}</pre>
                ) : (
                  <p className={styles.detailNoMore}>暂无更新日志</p>
                )}
              </Card>
            </TabPane>
            <TabPane tab="FAQ" key="faq">
              <Card className={styles.tabCard}>
                <p className={styles.detailNoMore}>常见问题内容可配置或从 CMS 关联，当前占位。</p>
              </Card>
            </TabPane>
            <TabPane tab="服务协议" key="agreement">
              <Card className={styles.tabCard}>
                <p>请阅读并同意以下协议后再购买：</p>
                <p><Link to="/agreement" target="_blank" rel="noopener noreferrer">《wldos 服务协议》</Link></p>
                <p>购买即视为您已阅读并同意上述协议及本产品相关条款。</p>
              </Card>
            </TabPane>
            <TabPane tab="推荐优惠" key="referral">
              <Card className={styles.tabCard}>
                <p><strong>推荐码优惠</strong>：新用户通过推荐链接或推荐码下单可享优惠，推荐人可获得佣金。</p>
                <p>优惠规则、佣金比例及发放方式以平台《推荐营销规则》为准。注册登录后可在个人中心获取您的推荐码与推荐链接。</p>
                <p><a onClick={() => setRecommendModalVisible(true)}><GiftOutlined /> 推荐优惠说明</a> · <Link to="/agreement" target="_blank" rel="noopener noreferrer">查看推荐营销规则</Link></p>
              </Card>
            </TabPane>
          </Tabs>
          <div className={styles.bottomCta}>
            <Link to="/product">
              <Button icon={<AppstoreOutlined />}>返回产品中心</Button>
            </Link>
            <Link to="/agreement" target="_blank" rel="noopener noreferrer" style={{ marginLeft: 8 }}>服务协议</Link>
            <span className={styles.bottomCtaTip}>浏览更多插件与产品</span>
          </div>
        </div>
      </div>
      <RecommendDiscountModal
        visible={recommendModalVisible}
        onClose={() => setRecommendModalVisible(false)}
        onGetReferral={() => {
          setRecommendModalVisible(false);
          if (currentUser && (currentUser.id || currentUser.userId)) {
            history.push('/account/referral');
          } else {
            history.push(`/user/login?redirect=${encodeURIComponent('/account/referral')}`);
          }
        }}
      />
    </PageContainer>
  );
};

export default connect(({ user }) => ({ currentUser: user.currentUser || {} }))(PluginDetail);
