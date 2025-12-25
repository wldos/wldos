import React, {useEffect, useState,} from 'react';
import {Card, Col, List, Row} from 'antd';
import {connect} from 'umi';
import styles from "./index.less";
import {genTdkCrumbs} from "@/utils/utils";
import {fetchSeoCrumbs} from "@/pages/home/service";
import {PageContainer} from "@ant-design/pro-layout";
import {queryFlatCategory} from "@/pages/sys/category/service";

/**
 * 分类信息类目模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const Category = (props) => {
  const {
    url,
    dispatch,
    slugTerm,
  } = props;

  const [items1, setItems1] = useState([]);
  const [items2, setItems2] = useState([]);
  const [items3, setItems3] = useState([]);
  const [items4, setItems4] = useState([]);
  useEffect(async () => {
    const res = await queryFlatCategory();
    if (res.data) {
      const temp1 = [];
      const temp2 = [];
      const temp3 = [];
      const temp4 = [];
      const {data} = res;
      for (let i = 0, len = data.length; i < len; i += 1) {
        if (i % 4 === 0)
          temp1.push(data[i]);
        else if (i % 4 === 1)
          temp2.push(data[i]);
        else if (i % 4 === 2)
          temp3.push(data[i]);
        else
          temp4.push(data[i]);
      }
      setItems1(temp1);
      setItems2(temp2);
      setItems3(temp3);
      setItems4(temp4);
    }
  }, [url]);

  useEffect(async () => {
    const res = await fetchSeoCrumbs({ slugTerm, tempType: 'category'});
    if (res && res.data) {
      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, '', '');
    }
  }, [url]);

  const rendItem = (item) => item.children && (
      <List
        className={styles.itemList}
        dataSource={item.children || []}
        grid={{ gutter: 16, xs: 3, sm: 3, md: 4, lg: 4, xl: 4, xxl: 4, }}
        renderItem={({slug, title}) => (
          <List.Item>
            <a href={`/info/category/${slug?? ''}`} rel="noopener"  target="_blank">
              {title}
            </a>
          </List.Item>
        )}
      />
  );
  const getKey = (id, index) => `${id}-${index}`;
  const body = (<div className={styles.coverCardList}>
    {(
      <Row
        className={styles.cardList}
        gutter={5}
        justify="center"
      >
        <Col key={1} xs={24} sm={24} md={24} lg={6} xl={6} xxl={6}>
        {items1.map((item, i) => (
          <Card key={getKey(item.id, i)} title={<a className={styles.header} href={`/info/category/${item.slug?? ''}`} target="_blank" rel="noopener">{item.title}</a>} >
            {rendItem(item)}
          </Card>))}
        </Col>
        <Col key={2} xs={24} sm={24} md={24} lg={6} xl={6} xxl={6}>
          {items2.map((item, i) => (
            <Card key={getKey(item.id, i)} title={<a className={styles.header} href={`/info/category/${item.slug?? ''}`} target="_blank" rel="noopener">{item.title}</a>} >
              {rendItem(item)}
            </Card>))}
        </Col>
        <Col key={3} xs={24} sm={24} md={24} lg={6} xl={6} xxl={6}>
          {items3.map((item, i) => (
            <Card key={getKey(item.id, i)} title={<a className={styles.header} href={`/info/category/${item.slug?? ''}`} target="_blank" rel="noopener">{item.title}</a>} >
              {rendItem(item)}
            </Card>))}
        </Col>
        <Col key={4} xs={24} sm={24} md={24} lg={6} xl={6} xxl={6}>
          {items4.map((item, i) => (
            <Card key={getKey(item.id, i)} title={<a className={styles.header} href={`/info/category/${item.slug?? ''}`} target="_blank" rel="noopener">{item.title}</a>}>
              {rendItem(item)}
            </Card>))}
        </Col>
      </Row>
    )}
  </div>);
  return (
    url === '/' ? body : (<PageContainer title={false} breadcrumb={false}>
      {body}
    </PageContainer>)
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Category);
