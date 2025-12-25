import {Card, Col, Pagination, Row} from 'antd';
import React, {useEffect, useState} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import { connect, history, Link } from 'umi';
import styles from './style.less';
import {queryBookList, queryListSideCar} from "@/pages/home/service";
import SideCar from "@/pages/home/components/SideCar";
import {genTdkCrumbs} from "@/utils/utils";
import Chapters from "@/components/ArchiveList/Chapters";
import {fetchSeoCrumbs} from "@/pages/home/service";
import {starObject} from "@/components/ArchiveList/Chapters/service";

/**
 * 存档列表模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const Archives = (props) => {
  const {
    dispatch,
    count, // 初始页内记录数: 16
    url, // location url: ${match.url}
    page, // 当前页号
    pageUrl, // 带分页号的url
    pageName, // 侧边栏定位页名称: 'product'|'archive'|'tag'...
    slugTerm, // 分类别名：${match.slugCategory}|${match.slugTag}
    termType
  } = props;

  let path;
  if (slugTerm) {
    path = `/archives/${termType === 'tag' ? 'tag' : 'category'}/${slugTerm}`;
  } else {
    path = url;
  }

  const [cur, setCur] = useState(page ? parseInt(page, 10) : 1);
  const [listSide, setList] = useState(undefined);
  const [data, setData] = useState({list: [], total: 0, pageSize: count, current: 1,});
  const [loading, setLoading] = useState(false);

  const onPageChange = (pNum) => {
    setCur(pNum);
    if (pNum === 1)
      history.replace(url);
    else
      history.replace(url === '/' ? `/page/${pNum}` : `${url.endsWith('/') ? url : `${url}/`}page/${pNum}`);
  };

  useEffect(async () => {
    const res = await queryListSideCar({
      pageName,
    });

    if (res?.data) {
      setList(res.data.rows);
    }
  }, []);

  useEffect(async () => {
    setLoading(true);

    const res = await queryBookList({
      pageSize: data.pageSize,
      current: page || 1,

      listStyle: 'archive',
      sorter: {"createTime":"descend"},
      path
    });

    if (res && res.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      setData({list: rows, total, current, pageSize,});
    }
    setLoading(false);
  }, [url, pageUrl]);

  const hasMore = (curNum) => {
    return curNum <= 2;
  };

  const fetchMore = async (num) => {
    setLoading(true);
    const totalPageNum = Math.floor( ( parseInt(data.total, 10) - 1 ) / parseInt(data.pageSize, 10) ) + 1;
    if (num > totalPageNum || !hasMore(num)) {
      setLoading(false);
      return;
    }
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: num,
      listStyle: 'archive',
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
      setCur(num);
    }
  };

  useEffect(async () => {

    const res = await fetchSeoCrumbs({ slugTerm, tempType: 'archives'});
    if (res && res.data) {

      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, '存档列表', '');
    }
  }, [url]);

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

  const itemRender = (current, type, originalElement) => {
    const target = url === '/' || url.endsWith('/') ? url : `${url  }/`;
    if (type === 'page')
      return <Link to={`${target}page/${current}`}>{current}</Link>
    if (type === 'prev')
      return <Link to={`${target}page/${current}`}>&lt;</Link>
    if (type === 'next')
      return <Link to={`${target}page/${current}`}>&gt;</Link>
    return originalElement;
  };

  const body = (<GridContent className={styles.listWidth}>
    <Row gutter={24}>
      <Col lg={6} md={24}>
        <Card title="热门排行推荐" className={styles.hotRec} bordered={false} style={{ marginBottom: 24, }} >
          {!listSide ? null : <SideCar listSide={listSide}/>}
        </Card>
      </Col>
      <Col lg={18} md={24}>
        <Card className={styles.tabsCard} bordered={true} >
          <>
            <Chapters
              fetchMore={fetchMore}
              list={data.list}
              pageSize={data.pageSize}
              loading={loading}
              total={data.total}
              cur={cur}
              star={star}
            />
            <Pagination
              current={cur}
              onChange={(p) => onPageChange(p)}
              itemRender={itemRender}
              total={data.total}
              pageSize={data.pageSize}
              showQuickJumper
              showSizeChanger={false}
            />
          </>
        </Card>
      </Col>
    </Row>
  </GridContent>);

  return (
    url === '/' ? body : (<PageContainer title={false} breadcrumb={false}>
      {body}
    </PageContainer>)
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Archives);
