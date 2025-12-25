import { Card, Col, List, Row, Typography} from 'antd';
import React, {useEffect, useState} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import {Link, connect} from 'umi';
import styles from './style.less';
import moment from "moment";
import {queryProduct} from "@/pages/book/detail/service";
import {genAvatars, genTdkCrumbs, typeUrl} from "@/utils/utils";
import {queryBookList, queryListSideCar} from "@/pages/home/service";
import AvatarList from "@/components/AvatarList";
import ElementList from "@/pages/book/read/list";

/**
 * 作品集篇章列表模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const count = 16;
const Read = (props) => {
  const { dispatch, match, } = props;
  const { params = {} } = match;
  const path = `/book-${params.bookId}.html`;

  const [cur, setCur] = useState(1);
  const [listSide, setList] = useState(undefined);
  const [data, setData] = useState({list: [], total: 0, pageSize: count, current: 1,});
  const [loading, setLoading] = useState(false);

  useEffect(async () => {
    const res = await queryListSideCar({
      pageName: 'product',
    });

    if (res?.data) {
      setList(res.data.rows);
    }
  }, []);

  useEffect(async () => {
    setLoading(true);
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: 1,
      pubType: 'chapter',
      sorter: {"createTime":"ascend"},
      path
    });
    setLoading(false);
    if (res?.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      setData({list: rows, total, current, pageSize,});
    }
  }, []);

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
      pubType: 'chapter',
      sorter: {"createTime":"ascend"},
      path
    });
    setLoading(false);
    if (res?.data) {
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
    const res = await queryProduct({id: params.bookId});
    if (res && res.data) {
      await genTdkCrumbs(res.data, dispatch, '章节列表', '章节列表 - ');
    }
  }, []);

  const cardList = listSide && (
    <List
      rowKey="id"
      grid={{
        gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1,
      }}
      dataSource={listSide}
      renderItem={({cover, id, members, pubTitle, pubType, createTime}) => (
        <List.Item>
            <Card className={styles.card} hoverable onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
                  cover={<img alt={pubTitle} src={cover}/>}>
              <Card.Meta
                title={<Link to={typeUrl({pubType, id})} target="_blank" rel="noopener">{pubTitle}</Link>}
              />
              />
              <div className={styles.cardItemContent}>
                <span>{moment(createTime).fromNow()}</span>
                <div className={styles.avatarList}>
                  <AvatarList size="small">
                    {genAvatars(`${id}-read-`, members)}
                  </AvatarList>
                </div>
              </div>
            </Card>
        </List.Item>
      )}
    />
  );

  return (
    <PageContainer title={false} breadcrumb={false}>
      <GridContent>
        <Row gutter={24}>
          <Col lg={6} md={24}>
            <Card title="热门排行推荐" className={styles.hotRec} bordered={false} style={{ marginBottom: 24, }} >
              {cardList}
            </Card>
          </Col>
          <Col lg={18} md={24}>
            <Card className={styles.tabsCard} bordered={true} >
              {data.list && <ElementList fetchMore={fetchMore} list={data.list} pageSize={data.pageSize} loading={loading} total={data.total} cur={cur} />}
            </Card>
          </Col>
        </Row>
      </GridContent>
    </PageContainer>
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Read);