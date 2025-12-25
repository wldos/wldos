import {List} from 'antd';
import React from 'react';
import {Link} from 'umi';
import styles from './index.less';
import InfiniteScroll from "react-infinite-scroller";

const ElementList = (props) => {
  const {list = [], loading, fetchMore, pageSize, total, cur} = props;
  return (
    <InfiniteScroll
      initialLoad={false}
      pageStart={0}
      loadMore={() => fetchMore(cur+1)}
      hasMore={(total > (cur - 1) * pageSize)}
    >
      <List
        size="large"
        className={styles.articleList}
        loading={loading}
        itemLayout="vertical"
        dataSource={list}
        renderItem={(item, i) =>
          (
            <List.Item key={`${item.id}-${i}`} >
              <Link className={styles.listItemMetaTitle} target="_blank" to={`/element-${item.id}.html`} rel="noopener">
                {`${i+1} ${item.pubTitle}`}
              </Link>
            </List.Item>
          )
        }
      />
    </InfiniteScroll>
  );
};

export default ElementList;
