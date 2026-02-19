import React, { useEffect, useMemo, useState } from 'react';
import { Card, List, Input, Select, TreeSelect, Pagination, Spin, Empty, Space, Alert, Checkbox, Button } from 'antd';
import { SearchOutlined, DownloadOutlined, StarOutlined, GiftOutlined, UndoOutlined } from '@ant-design/icons';
import { Link, history, connect } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { queryStoreProductList, getProductOptions } from './service';
import RecommendDiscountModal from '../components/RecommendDiscountModal';
import styles from './index.less';

/** 封面等静态资源走 /store 路径，与 API prefix 无关 */
const STORE_BASE = '/store';
const { SHOW_PARENT } = TreeSelect;

const PluginList = (props) => {
  const { currentUser } = props;
  const [list, setList] = useState([]);
  const [recommendModalVisible, setRecommendModalVisible] = useState(false);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [size] = useState(12);
  const [keyword, setKeyword] = useState('');
  const [categoryTermTypeIds, setCategoryTermTypeIds] = useState([]);
  const [productType, setProductType] = useState(undefined);
  const [priceFilter, setPriceFilter] = useState(undefined);
  const [scene, setScene] = useState(undefined);
  const [licenseType, setLicenseType] = useState(undefined);
  const [supportRecommendDiscount, setSupportRecommendDiscount] = useState(false);
  const [orderBy, setOrderBy] = useState('default');
  const [options, setOptions] = useState({ categories: [], productType: [], scene: [], licenseType: [] });

  const categoryTreeData = useMemo(() => {
    const toTree = (nodes) => {
      if (!Array.isArray(nodes)) return [];
      return nodes
        .filter(Boolean)
        .map((n) => {
          const val = n.id != null ? String(n.id) : n.categoryCode != null ? String(n.categoryCode) : undefined;
          const isRoot =
            (n.categoryCode != null && String(n.categoryCode) === '0') ||
            (n.categoryName === '根分类' && (n.parentId === null || n.parentId === undefined));
          return {
            title: n.categoryName || n.name || '-',
            value: val,
            key: val,
            // 根分类仅作为展示与展开节点，不参与勾选和筛选，避免选中根节点时误传 0
            disableCheckbox: isRoot,
            selectable: !isRoot,
            children: toTree(n.children),
          };
        })
        .filter((n) => n.value != null);
    };
    return toTree(options.categories || []);
  }, [options.categories]);

  const fetchList = async (p = 1) => {
    setLoading(true);
    try {
      const res = await queryStoreProductList({
        current: p,
        pageSize: size,
        keyword: keyword || undefined,
        // 支持多选：后端兼容单值 / 逗号分隔 / 数组
        categoryTermTypeId: categoryTermTypeIds && categoryTermTypeIds.length ? categoryTermTypeIds.join(',') : undefined,
        productType: productType || undefined,
        priceFilter: priceFilter || undefined,
        scene: scene || undefined,
        licenseType: licenseType || undefined,
        supportRecommendDiscount: supportRecommendDiscount ? '1' : undefined,
        orderBy: orderBy !== 'default' ? orderBy : undefined,
      });
      const inner = res?.data?.data || res?.data || {};
      setList(inner.list || inner.rows || []);
      setTotal(inner.total ?? 0);
    } catch (e) {
      setList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const fetchOptions = async () => {
    try {
      const res = await getProductOptions();
      const data = res?.data?.data != null ? res.data.data : res?.data || {};
      setOptions({
        categories: Array.isArray(data.categories) ? data.categories : [],
        productType: Array.isArray(data.productType) ? data.productType : [],
        scene: Array.isArray(data.scene) ? data.scene : [],
        licenseType: Array.isArray(data.licenseType) ? data.licenseType : [],
      });
    } catch (e) {
      setOptions({ categories: [], productType: [], scene: [], licenseType: [] });
    }
  };

  useEffect(() => {
    fetchList(page);
  }, [page, categoryTermTypeIds, productType, priceFilter, scene, licenseType, supportRecommendDiscount, orderBy]);

  useEffect(() => {
    fetchOptions();
  }, []);

  const handleSearch = () => {
    setPage(1);
    fetchList(1);
  };

  const handleReset = () => {
    setKeyword('');
    setCategoryTermTypeIds([]);
    setProductType(undefined);
    setPriceFilter(undefined);
    setScene(undefined);
    setLicenseType(undefined);
    setSupportRecommendDiscount(false);
    setOrderBy('default');
    setPage(1);
  };

  const getCoverUrl = (coverImage) => {
    if (!coverImage) return null;
    if (coverImage.startsWith('http')) return coverImage;
    const path = coverImage.startsWith('/') ? coverImage : `/${coverImage}`;
    return `${STORE_BASE}${path}`;
  };

  const defaultCoverPlaceholder = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="240" height="180"><rect width="100%" height="100%" fill="#f0f0f0"/><text x="50%" y="50%" fill="#999" font-size="14" text-anchor="middle" dy=".3em">暂无封面</text></svg>'
  )));

  const handleCoverError = (e) => {
    if (e?.target && e.target.src !== defaultCoverPlaceholder) {
      e.target.onerror = null;
      e.target.src = defaultCoverPlaceholder;
    }
  };

  const commercialTypeText = {
    OPEN_SOURCE: '开源',
    FREE_COMMERCIAL: '免费商业',
    PAID_COMMERCIAL: '付费',
  };

  const productTypeText = options.productType?.length
    ? Object.fromEntries(options.productType.map((o) => [o.value, o.label]))
    : { PLUGIN: '插件', SCENARIO: '平台' };
  const sceneText = options.scene?.length
    ? Object.fromEntries(options.scene.map((o) => [o.value, o.label]))
    : { PERSONAL: '个人', ENTERPRISE: '企业', IOT: '云物互联' };
  const licenseTypeText = options.licenseType?.length
    ? Object.fromEntries(options.licenseType.map((o) => [o.value, o.label]))
    : { TIME_BASED: '按时间购买', LIFETIME: '终身制' };

  const parseSceneTags = (sceneVal) => {
    if (!sceneVal || typeof sceneVal !== 'string') return [];
    return sceneVal.split(/[,，]/).map((s) => s.trim()).filter(Boolean);
  };

  const applyQuickFilter = (type) => {
    if (type === 'free') {
      setPriceFilter('free');
      setScene(undefined);
      setSupportRecommendDiscount(false);
    } else if (type === 'enterprise') {
      setPriceFilter(undefined);
      setScene('ENTERPRISE');
      setSupportRecommendDiscount(false);
    } else if (type === 'recommend') {
      setPriceFilter(undefined);
      setScene(undefined);
      setSupportRecommendDiscount(true);
    }
    setPage(1);
  };

  return (
    <PageContainer title="产品中心" content="WLDOS 插件与产品展示">
      <Alert
        message={null}
        description={
          <ul className={styles.tipList}>
            <li>需阅读服务协议；购买前请阅读<Link to="/agreement">《服务协议》</Link>。</li>
            <li>仅离线软件许可付费，在线服务免费；许可可选：按时间购买 / 终身制。</li>
            <li>推荐码优惠：新用户通过推荐链接下单享优惠，推荐人得佣金。<a onClick={() => setRecommendModalVisible(true)} style={{ marginLeft: 6 }}><GiftOutlined /> 推荐优惠说明</a></li>
          </ul>
        }
        type="info"
        showIcon
        className={styles.tipBlock}
      />
      <div className={styles.quickFilters}>
        <span className={styles.quickFiltersLabel}>快捷筛选：</span>
        <button type="button" className="ant-btn ant-btn-sm" onClick={() => applyQuickFilter('free')}>免费产品</button>
        <button type="button" className="ant-btn ant-btn-sm" onClick={() => applyQuickFilter('enterprise')}>企业版产品</button>
        <button type="button" className="ant-btn ant-btn-sm" onClick={() => applyQuickFilter('recommend')}>支持推荐优惠</button>
      </div>
      <div className={styles.filterBar}>
        <Space wrap size="middle">
          <Input
            placeholder="搜索产品"
            prefix={<SearchOutlined />}
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
            style={{ width: 220 }}
            allowClear
          />
          <TreeSelect
            placeholder="分类"
            value={categoryTermTypeIds}
            onChange={(v) => setCategoryTermTypeIds(Array.isArray(v) ? v : v ? [v] : [])}
            style={{ width: 220 }}
            allowClear
            showSearch
            treeData={categoryTreeData}
            treeDefaultExpandAll
            multiple
            treeCheckable
            showCheckedStrategy={SHOW_PARENT}
            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
            treeNodeFilterProp="title"
          />
          <Select
            placeholder="类型"
            value={productType}
            onChange={setProductType}
            style={{ width: 100 }}
            allowClear
          >
            {(options.productType || []).map((o) => (
              <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="价格"
            value={priceFilter}
            onChange={setPriceFilter}
            style={{ width: 90 }}
            allowClear
          >
            <Select.Option value="free">免费</Select.Option>
            <Select.Option value="paid">付费</Select.Option>
          </Select>
          <Select
            placeholder="适用场景"
            value={scene}
            onChange={setScene}
            style={{ width: 120 }}
            allowClear
          >
            {(options.scene || []).map((o) => (
              <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="许可方式"
            value={licenseType}
            onChange={setLicenseType}
            style={{ width: 120 }}
            allowClear
          >
            {(options.licenseType || []).map((o) => (
              <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
            ))}
          </Select>
          <Checkbox
            checked={supportRecommendDiscount}
            onChange={(e) => setSupportRecommendDiscount(e.target.checked)}
          >
            支持推荐优惠
          </Checkbox>
          <Select
            value={orderBy}
            onChange={setOrderBy}
            style={{ width: 120 }}
          >
            <Select.Option value="default">默认排序</Select.Option>
            <Select.Option value="newest">最新</Select.Option>
            <Select.Option value="download">下载量</Select.Option>
            <Select.Option value="star">星级</Select.Option>
            <Select.Option value="price">价格</Select.Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
          <Button icon={<UndoOutlined />} onClick={handleReset}>
            重置
          </Button>
        </Space>
      </div>

      <Spin spinning={loading}>
        {list.length === 0 && !loading ? (
          <div className={styles.emptyState}>
            <Empty description="暂无产品" />
          </div>
        ) : (
          <>
            <List
              grid={{ gutter: 24, xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 4 }}
              dataSource={list}
              renderItem={(item) => {
                const title = item.displayTitle || item.pluginName || '未命名';
                const cover = getCoverUrl(item.coverImage);
                const priceNum = item.price != null ? Number(item.price) : 0;
                const hasTimeBased = item.originalPriceTimeBased != null && Number(item.originalPriceTimeBased) > 0;
                const hasLifetime = item.originalPriceLifetime != null && Number(item.originalPriceLifetime) > 0;
                const hasDualPrice = hasTimeBased || hasLifetime;
                const priceText = priceNum > 0 ? `¥${item.price}` : '免费';
                const typeTag = item.productType ? (productTypeText[item.productType] || item.productType) : null;
                const summary = item.displaySummary || item.pluginName || '-';
                const summaryShort = summary.length > 60 ? summary.slice(0, 60) + '…' : summary;
                const sceneTags = parseSceneTags(item.scene);
                const showRecommendBadge = item.supportRecommendDiscount === '1' || item.supportRecommendDiscount === true;
                return (
                  <List.Item>
                    <Link to={`/product-${item.id}.html`} target="_blank" style={{ display: 'block' }}>
                      <Card
                        hoverable
                        cover={
                          <div className={styles.cardCover}>
                            {showRecommendBadge && <span className={styles.recommendBadge}>推荐立减</span>}
                            {cover ? (
                              <img src={cover} alt={title} onError={handleCoverError} />
                            ) : (
                              <img src={defaultCoverPlaceholder} alt={title} />
                            )}
                          </div>
                        }
                      >
                        <Card.Meta
                          title={
                            <span>
                              {title}
                              {typeTag && <span className={styles.cardTypeTag}>{typeTag}</span>}
                            </span>
                          }
                          description={
                            <div className={styles.cardMeta}>
                              {sceneTags.length > 0 && (
                                <div className={styles.sceneTags}>
                                  {sceneTags.map((s) => (
                                    <span key={s} className={styles.sceneTag}>
                                      {sceneText[s] || s}
                                    </span>
                                  ))}
                                </div>
                              )}
                              <span className={styles.cardSummary}>{summaryShort}</span>
                              <div className={styles.cardFooter}>
                                {hasDualPrice ? (
                                  <span className={styles.priceBlock}>
                                    {hasTimeBased && <span className={styles.priceItem}><span className={styles.licenseLabel}>按时间</span> ¥{item.originalPriceTimeBased}</span>}
                                    {hasLifetime && <span className={styles.priceItem}><span className={styles.licenseLabel}>终身制</span> ¥{item.originalPriceLifetime}</span>}
                                    {showRecommendBadge && <span className={styles.recommendLabel}>推荐优惠</span>}
                                  </span>
                                ) : (
                                  <span className={styles.price}>{priceText}</span>
                                )}
                                {!hasDualPrice && <span>{commercialTypeText[item.commercialType] || item.commercialType}</span>}
                                {item.version && <span>v{item.version}</span>}
                                <span><DownloadOutlined /> {item.downCount || 0}</span>
                                <span><StarOutlined /> {item.starCount || 0}</span>
                              </div>
                            </div>
                          }
                        />
                      </Card>
                    </Link>
                  </List.Item>
                );
              }}
            />
            {total > size && (
              <div className={styles.pagination}>
                <Pagination
                  current={page}
                  pageSize={size}
                  total={total}
                  onChange={setPage}
                  showSizeChanger={false}
                />
              </div>
            )}
          </>
        )}
      </Spin>
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

export default connect(({ user }) => ({ currentUser: user.currentUser || {} }))(PluginList);
