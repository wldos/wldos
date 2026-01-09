/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import {LikeOutlined, MessageFilled, StarTwoTone} from '@ant-design/icons';
import {List, Tag} from 'antd';
import React, {useCallback, useMemo} from 'react';
import {Link} from 'umi';
import ChapterListContent from '../ChapterListContent';
import styles from './index.less';
import InfiniteScroll from "react-infinite-scroller";
import {typeUrl} from "@/utils/utils";

const Chapters = (props) => { // isBook=product表示是作品列表，否则是章节列表，如果为空是独立文章作品列表
  const {list = [], loading, fetchMore, pageSize, total, cur, star, isBook} = props;
  const dataSource = Array.isArray(list) ? list.filter(Boolean) : [];

  const IconText = ({icon, text, params}) => (
    <span>
      <a onClick={() => star(params)}>{icon}</a> {text}
    </span>
  );

  const enumTags = (tags = []) => {
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Tag key={`${tag.id}-${i}`}><Link
          to={`/archives/tag/${tag.slug}`} target="_blank" rel="noopener">{tag.name}</Link></Tag>));
    }
    return temp;
  };

  // 中文“图片加载失败”的轻量 SVG 兜底
  const svgPlaceholder = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(`<?xml version="1.0" encoding="UTF-8"?><svg xmlns="http://www.w3.org/2000/svg" width="160" height="120"><rect width="100%" height="100%" rx="12" ry="12" fill="#f5f5f5"/><text x="50%" y="55%" font-size="14" fill="#999" text-anchor="middle">图片加载失败</text></svg>`)));

  const onImgError = (e) => {
    if (e && e.target) {
      if (e.target.src !== svgPlaceholder) {
        e.target.onerror = null; // 防止循环触发
        e.target.src = svgPlaceholder;
      }
    }
  };

  // 稳定化 loadMore 函数，避免每次渲染都创建新函数
  const handleLoadMore = useCallback(() => {
    if (fetchMore && typeof fetchMore === 'function') {
      fetchMore(cur + 1);
    }
  }, [fetchMore, cur]);

  // 稳定化 hasMore 计算
  const hasMoreData = useMemo(() => {
    return total > (cur - 1) * pageSize;
  }, [total, cur, pageSize]);

  return (
    <InfiniteScroll
      initialLoad={false}
      pageStart={0}
      loadMore={handleLoadMore}
      hasMore={hasMoreData}
    >
      <List
        size="large"
        className={styles.articleList}
        loading={loading}
        itemLayout="vertical"
        rowKey="id"
        dataSource={dataSource}
        renderItem={(item, i) => {
          if (!item) return null;
          return !isBook ? (
            <List.Item
              actions={[
                <IconText key="star" icon={<StarTwoTone/>} text={item.starCount}
                          params={{path: '/cms/star', id: item.id}}/>,
                <IconText key="like" icon={<LikeOutlined/>} text={item.likeCount}
                          params={{path: '/cms/like', id: item.id}}/>,
                <IconText key="message" icon={<MessageFilled/>} text={item.commentCount}/>,
              ]}
              extra={
                <Link to={typeUrl(item)} rel="nofollow " target="_blank">
                  <img
                    alt="cover"
                    loading="lazy"
                    decoding="async"
                    referrerPolicy="no-referrer"
                    src={item.cover || svgPlaceholder}
                    onError={onImgError}
                    className="article-cover-image"
                    style={{
                      width: 160,
                      height: 120, // 改回4:3矩形，更适合文章封面
                      display: 'block',
                      objectFit: 'cover',
                      background: '#f5f5f5',
                      borderRadius: 6,
                    }}
                  />
                </Link>
              }
            >
              <List.Item.Meta
                title={
                  <Link className={styles.listItemMetaTitle} // 此处需要根据发布类型解耦类型模板，暂定信息、产品和存档三种模板
                        /* eslint-disable-next-line no-nested-ternary */
                        to={typeUrl(item)}
                  target="_blank" rel="noopener">
                    {item.pubTitle}
                  </Link>
                }
                description={
                  <span>
                  {item.tags && enumTags(item.tags)}
                  </span>
                }
              />
              {item && <ChapterListContent data={item} />}
            </List.Item>
          ) : (
            <List.Item>
              {
                isBook === 'product' ?
                  (<>
                    <List.Item.Meta
                      title={
                        <Link className={styles.listItemMetaTitle} to={`/product-${item.id}.html`} target="_blank" rel="noopener">
                          {item.pubTitle}
                        </Link>
                      }
                  />
                  {item && <ChapterListContent data={item}  isBook={isBook}/>}
                    </>
                  )
                  : (
                    <Link className={styles.listItemMetaTitle} to={`/archives-${item.id}.html`} target="_blank" rel="noopener">
                      {`${total - i} ${item.pubTitle}`}
                    </Link>
                  )
              }
            </List.Item>
          );
        }}
      />
    </InfiniteScroll>
  );
};

export default Chapters;
