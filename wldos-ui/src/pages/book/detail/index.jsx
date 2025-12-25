import {Button, Card, Col, Divider, List, Row, Typography, message, Tooltip} from 'antd';
import React, {useEffect, useState} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {Link, connect} from 'umi';
import styles from './style.less';
import moment from "moment";
import {previewProduct, queryProduct} from "@/pages/book/detail/service";
import {queryBookList, queryListSideCar} from "@/pages/home/service";
import Comments from "@/pages/book/detail/components/Comments";
import Share from "@/components/share";
import AvatarList from "@/components/AvatarList";
import {
  bodyContentBook,
  genAvatars,
  genTdkCrumbs, typeUrl,
} from "@/utils/utils";
import TelCode from "@/pages/book/detail/components/TelCode";

const getKey = (id, index) => `${id}-${index}`;

const operTabList = (cNum) => [
  {
    key: 'info',
    tab: (
      <span> 详情{' '}
        <span style={{ fontSize: 14, }} > </span>
      </span>
    ),
  },
  {
    key: 'comments',
    tab: (
      <span> 评论{' '}
        <span
          style={{
            fontSize: 14,
            fontStyle: 'italic',
            color: 'red',
          }}
        >
          {cNum === '0' ? '抢沙发!' : cNum}
        </span>
      </span>
    ),
  },
];

const Product = (props) => {
  const {match: {params={}, url}, dispatch} = props;
  const [product, setProduct] = useState({});
  const [mainPic, setMainPic] = useState(undefined);
  const [list, setList] = useState(undefined);
  const [listRight, setListRight] = useState(undefined);
  const [tabKey, setTabKey] = useState('info');
  const [listRel, setListRel] = useState(undefined);
  const [visible, setVisible] = useState(false);

  useEffect(async () => {
    setMainPic(undefined);
    let res = !params.preview ? await queryProduct({id: params.bookId}) : await previewProduct({id: params.bookId, preview: params.preview});
    let temp;
    if (res && res.data) {
      await genTdkCrumbs((temp = res.data), dispatch, '详情',  '');
      setProduct(res.data);
    }

    const { termTypeIds } = temp || {};

    res = await queryListSideCar({
      pageName: 'product',
    });

    if (res?.data) {
      setList(res.data.rows);
    }

    res = await queryListSideCar({
      pageName: 'productRight',
    });

    if (res?.data) {
      setListRight(res.data.rows);
    }

    res = await queryBookList({
      pageSize: 8,
      current: 1,
      pubType: 'book',
      sorter: {"createTime":"descend"},
      filter: termTypeIds ? {termTypeId: termTypeIds} : {},
      path: '/'
    });

    if (res?.data) {
      setListRel(res.data.rows);
    }
  }, [url]);

  const proDesc = product.pubTitle && (
    <>
      <ProDescriptions
        title={product.pubTitle}
        className={styles.proDesc}
        column={4}
        dataSource={{
          ...product,
          address: `${product.prov}/${product.city}`,
        } || {}}
        columns={[
          {
            title: '价格',
            key: 'ornPrice',
            dataIndex: 'ornPrice',
            valueType: 'money',
            span: 3,
            contentStyle: {color: "red"},
          },
          {
            title: '热度',
            key: 'views',
            dataIndex: 'views',
            valueType: 'number',
            style: {textAlignLast: "right"}
          },
          {
            title: '卖点',
            key: 'subTitle',
            dataIndex: 'subTitle',
            span: 4,
          },
          {
            title: '联系电话',
            key: 'telephone',
            dataIndex: 'telephone',
            valueType: 'phone',
            ellipsis: true,
            span: 4,
            render: (dom, entity) => {
              return (<>
                {dom}
                <a className={styles.contact} onClick={() => setVisible(true)}>点击查看完整号码</a>
                <a className={styles.contactMobile} href={`tel:${entity.realNo}`}>拨打电话</a>
                </>);
            },
          },
          {
            title: '联系人',
            key: 'contact',
            dataIndex: 'contact',
            span: 4,
          },
          {
            title: '地区',
            key: 'address',
            dataIndex: 'address',
            span: 4,
          },
          {
            title: '发布时间',
            key: 'createTime',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            span: 4,
          },
        ]}
      />
      <div className={styles.proButton}>
        <Tooltip title="推广期 限时全系列免费试读！" color="orange">
          <Button size="small" type="primary" >
          <Link to={`/book-${product.id}.html` /* 购买后，变为阅读，可读内容由后端决定 */} target="_blank" rel="noopener">试读</Link>
        </Button>
        </Tooltip>
        <Tooltip title="推广期 免费试读，无需购买！" color="orange">
          <Button size="small" type="primary"
                  onClick={async () => {message.success('推广期免费全文试读，无需购买！');
                  /* @todo 付费设置检查，如果免费，价格一栏显示免费，查看直接打开阅读页，如果收费，查看时需要付款，付款成功打开阅读页；如果打赏可见，显示打赏按钮 */
                  }}>
            购买
          </Button>
        </Tooltip>
        {product.reward && <Button size="small" type="primary">打赏</Button>}
      </div>
      <Divider
        style={{margin: '40px 0 24px',}}/>
      <Share title={product.pubTitle || '无标题'} src={mainPic || '@/assets/log.svg'}/>
    </>);

  const cardList = list && (
    <List
      rowKey="id"
      grid={{
        gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1,
      }}
      dataSource={list}
      renderItem={({cover, id, pubType, members, pubTitle, createTime}) => (
        <List.Item>
            <Card className={styles.card} hoverable onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
                  cover={<img alt={pubTitle} src={cover}/>}>
              <Card.Meta
                title={<Link to={typeUrl({pubType, id})} target="_blank" rel="noopener">{pubTitle}</Link>}
              />
              <div className={styles.cardItemContent}>
                <span>{moment(createTime).fromNow()}</span>
                <div className={styles.avatarList}>
                  <AvatarList size="small">
                    {genAvatars(id, members)}
                  </AvatarList>
                </div>
              </div>
            </Card>
        </List.Item>
      )}
    />
  );

  const cardListRight = listRight && (
    <List
      rowKey="id"
      grid={{gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1,}}
      dataSource={listRight}
      renderItem={(item) => (
        <List.Item>
            <Card className={styles.card} hoverable size="small" onClick={() => window.open(`/product-${item.id}.html`, '_blank')}
                  cover={<img alt={item.pubTitle} src={item.cover}/>}>
              <Card.Meta title={<Link to={`/product-${item.id}.html`} target="_blank" rel="noopener">{item.pubTitle}</Link>}/>
            </Card>
        </List.Item>
      )}
    />
  );

  const cardListMiddle = listRel && (
    <List
      rowKey="id"
      grid={{
        gutter: 24, xs: 1, sm: 1, md: 4, lg: 4, xl: 4, xxl: 4,
      }}
      dataSource={listRel}
      renderItem={(item) => (
        <List.Item>
            <Card className={styles.card} hoverable size="small" onClick={() => window.open(`/product-${item.id}.html`, '_blank')}
                  cover={<img alt={item.pubTitle} src={item.cover}/>}>
              <Card.Meta title={<Link to={`/product-${item.id}.html`} target="_blank" rel="noopener">{item.pubTitle}</Link>}/>
            </Card>
        </List.Item>
      )}
    />
  );

  const rowBody = product.pubTitle && (
    <Row gutter={24}>
      <Col lg={6} md={24}>
        {product.mainPic?.length > 0 && (
          <Card bordered={false} style={{ marginBottom: 24, }} >
          <div>
            <div className={styles.avatarHolder}>
              {product.mainPic[0] && <img alt={product.pubTitle} src={mainPic?? product.mainPic[0].url}/>}
            </div>
            <div className={styles.team}>
              <Row gutter={2}>
                {product.mainPic.map((item, i) => (
                  <Col key={getKey(item.key, i)} xs={6} sm={6} md={6} lg={6} xl={6}>
                    <a onMouseOver={() => setMainPic(item.url)}>
                      <img alt={item.key} src={item.url}/>
                    </a>
                  </Col>
                ))}
              </Row>
            </div>
          </div>
          </Card>
        )}
        <Card title="热门排行推荐" className={styles.hotRec} bordered={false} style={{ marginBottom: 24, }} >
          {cardList}
        </Card>
      </Col>
      <Col lg={18} md={24}>
        <Row gutter={24}>
          <Col lg={18} md={24}>
            <Card bordered={true} style={{ marginBottom: 24, }} >
              {proDesc}
            </Card>
          </Col>
          <Col lg={6} md={24} >
            <Card className={styles.cardListRight} bordered={false}>
              {cardListRight}
            </Card>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col xs={24} sm={24} md={24} lg={24} xl={24}>
            <Card className={styles.tabsCard}
                  bordered={true}
                  tabList={operTabList(product?.commentCount)}
                  activeTabKey={tabKey}
                  onTabChange={(a) => setTabKey(a)}
            >
              {tabKey === 'info' ? (<>{bodyContentBook(product?.pubContent, product?.id) || '没有内容'}<Divider style={{ margin: '40px 0 24px', }} /></>) : <Comments id={product?.id}/>}
            </Card>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col xs={24} sm={24} md={24} lg={24} xl={24}>
            <Card title="相关推荐" bordered={false} style={{ marginBottom: 24, }} >
              {cardListMiddle}
            </Card>
          </Col>
        </Row>
      </Col>
    </Row>
  );

  return (
    <PageContainer title={false} breadcrumb={false}>
      <GridContent>
        {rowBody}
        {product && Object.keys(product).length ? (
          <TelCode onCancel={() => setVisible(false)} visible={visible} url={document.location.href} realNo={product.realNo}/>) : null }
      </GridContent>
    </PageContainer>
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Product);