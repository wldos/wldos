import React, {useEffect, useState, useCallback, useMemo} from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import {Card, List, Typography} from 'antd';
import {connect, Link} from 'umi';
import moment from "moment";
import styles from "./index.less";
import AvatarList from "@/components/AvatarList";
import {genAvatars, genTdkCrumbs, typeUrl} from "@/utils/utils";
import {fetchSeoCrumbs} from "@/pages/home/service";
import {PageContainer} from "@ant-design/pro-layout";

const {Paragraph} = Typography;

const Products = (props) => {
  const {
    dispatch,
    list = [],
    total,
    pageSize,
    loading,
    url,
    slugTerm,
  } = props;

  const [cur, setCur] = useState(1);
  useEffect(() => {
    dispatch({
      type: 'workBooks/fetch',
      payload: {
        pageSize,
        current: 1,
        sorter: {"createTime":"descend"},
        path: url
      },
    });
    setCur(cur+1);
  }, [url]);

  const fetchMore = useCallback((num) => {
    dispatch({
      type: 'workBooks/appendFetch',
      payload: {
        pageSize,
        current: num,
        sorter: {"createTime":"descend"},
        path: url
      },
    });
    setCur(num+1);
  }, [dispatch, pageSize, url]);

  useEffect(async () => {
    const res = await fetchSeoCrumbs({ slugTerm, tempType: 'product'});
    if (res && res.data) {
      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, '', '');
    }
  }, [url]);

  // 图片错误处理 -> 使用显示“图片加载失败”的轻量 SVG
  const onImgError = useCallback((e) => {
    const svgPlaceholder = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(`<?xml version="1.0" encoding="UTF-8"?><svg xmlns="http://www.w3.org/2000/svg" width="240" height="180"><rect width="100%" height="100%" fill="#f5f5f5"/><text x="50%" y="55%" font-size="14" fill="#999" text-anchor="middle">图片加载失败</text></svg>`)));
    if (e && e.target) {
      if (e.target.src !== svgPlaceholder) {
        e.target.onerror = null;
        e.target.src = svgPlaceholder;
      }
    }
  }, []);

  // 优化的网格配置
  const gridConfig = useMemo(() => ({
    gutter: [24, 24],
    xs: 2,
    sm: 2,
    md: 3,
    lg: 3,
    xl: 4,
    xxl: 4,
  }), []);

  // 保留全部有效项（仅过滤空值），避免丢数据
  const safeList = useMemo(() => (
    Array.isArray(list) ? list.filter(Boolean) : []
  ), [list]);

  const cardList = (
    <InfiniteScroll
      initialLoad={false}
      pageStart={0}
      loadMore={() => fetchMore(cur)}
      hasMore={total > (cur - 1) * pageSize}
      threshold={250}
      useWindow={false}
    >
      <List
        loading={loading}
        grid={gridConfig}
        dataSource={safeList}
        renderItem={(item, index) => {
          if (!item) return null;
          const { pubType, id, pubTitle, cover, createTime, members } = item;
          return (
          <List.Item key={`${id}-${index}`}>
            <Card 
              className={styles.card} 
              hoverable 
              onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
              cover={
                <div className={styles.cardCover}>
                  {cover ? (
                    <img 
                      alt={pubTitle} 
                      src={cover}
                      loading="lazy"
                      onError={onImgError}
                    />
                  ) : (
                    <div className={styles.placeholderImage}>
                      <span>📷</span>
                    </div>
                  )}
                </div>
              }
            >
              <Card.Meta
                title={
                  <Link to={typeUrl({pubType, id})} target="_blank" rel="noopener">
                    {pubTitle}
                  </Link>
                }
              />
              <div className={styles.cardItemContent}>
                <span>{moment(createTime).fromNow()}</span>
                <div className={styles.avatarList}>
                  {members && (
                    <AvatarList size="small">
                      {genAvatars(`${id}-product-`, members)}
                    </AvatarList>
                  )}
                </div>
              </div>
            </Card>
          </List.Item>);
        }}
      />
    </InfiniteScroll>
  );

  const body = (
    <div className={styles.coverCardList}>
      <div className={styles.cardList}>
        {cardList}
      </div>
    </div>
  );
  
  return (url === '/' ? body : (<PageContainer title={false} breadcrumb={false}>{body}</PageContainer>));
};

export default connect(({workBooks, loading, user}) => ({
  list: workBooks.list,
  total: workBooks.total,
  pageSize: workBooks.pageSize,
  current: workBooks.current,
  loading: loading.models.workBooks,
  tdk: user.tdk,
}))(Products);
