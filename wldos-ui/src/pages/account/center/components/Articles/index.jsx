import React, {useEffect, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import { connect } from 'umi';
import {queryBookList} from "@/pages/home/service";
import {genTdkCrumbs} from "@/utils/utils";
import Chapters from "@/components/ArchiveList/Chapters";
import {fetchSeoCrumbs} from "@/pages/home/service";
import {starObject} from "@/components/ArchiveList/Chapters/service";

/**
 * 个人中心-关注、收藏列表模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const Articles = (props) => {
  const {
    dispatch,
    count = 16, // 初始页内记录数: 16
    url, // location url: ${match.url}
    slugTerm, // 分类别名：${match.slugCategory}|${match.slugTag}
    id,
    isBook, // isBook=product表示是合集列表，否则是合集的章节列表，如果为空是独立文章作品列表
    match
  } = props;

  let path;
  if (id) {
    path = `/archives-author/${id}.html`;
  } else {
    path = url?? (match?.url?? '');
  }

  const [cur, setCur] = useState(1);
  const [data, setData] = useState({list: [], total: 0, pageSize: count, current: 1,});
  const [loading, setLoading] = useState(false);

  useEffect(async () => {
    setLoading(true);
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: 1,
      sorter: {"createTime":"descend"},
      path
    });

    if (res && res.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      setData({list: rows, total, current, pageSize,});
    }
    setLoading(false);
  }, [url, isBook]);

  const fetchMore = async (num) => {
    setLoading(true);
    const totalPageNum = Math.floor( ( parseInt(data.total, 10) - 1 ) / parseInt(data.pageSize, 10) ) + 1;
    if (num > totalPageNum) {
      setLoading(false);
      return;
    }
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: num,
      sorter: {"createTime":"descend"},
      path
    });
    setLoading(false);
    if (res && res.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      if (total === 0) {
        return;
      }

      const state = {
        ...data,
        list: data.list.concat(rows), total, current, pageSize, hasMore: true,
      };

      setData(state);
      setCur(num+1);
    }
  };

  useEffect(async () => {

    const res = await fetchSeoCrumbs({ slugTerm, tempType: 'archives'});
    if (res && res.data) {
      
      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, id ? undefined : '存档列表', '作者存档 - ');
    }
  }, [url, isBook]);

  const star = async (p) => {
    if (!p)
      return;
    const res = await starObject(p);
    if (res && res.data) {
      const listData = [];
      const {list} = data;

      for (let i = 0, len = list.length; i < len; i += 1) {
        let pub = list[i];
        if (pub.id === p.id) {
          if (p.path === '/cms/star') {
            const c = pub.starCount + res.data;
            pub = {...pub, starCount: c + 1 === 0 ? 0 : c};
          } else {
            const c = pub.likeCount + res.data;
            pub = {...pub, likeCount: c + 1 === 0 ? 0 : c};
          }
        }
        listData.push(pub);
      }
      setData({...data, list: listData});
    }
  };

  const body = (<>{data.list && <Chapters fetchMore={fetchMore} list={data.list} pageSize={data.pageSize}
                                          loading={loading} total={data.total} cur={cur} 
                                          star={star} isBook={isBook}/>}</>);

  return (
    match?.url ? (<PageContainer title={false} breadcrumb={false}>{body}</PageContainer>) : body
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Articles);