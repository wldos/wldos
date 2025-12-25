import {Card, Col, Divider, List, Row, Typography} from 'antd';
import React, {useEffect, useState} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {Link, connect} from 'umi';
import styles from '../style.less';
import moment from "moment";
import {previewInfo, queryInfo} from "@/pages/book/detail/service";
import {queryBookList, queryListSideCar} from "@/pages/home/service";
import Comments from "@/pages/book/detail/components/Comments";
import Share from "@/components/share";
import AvatarList from "@/components/AvatarList";
import {bodyContent, genAvatars, genTdkCrumbs, typeUrl} from "@/utils/utils";
import TelCode from "@/pages/book/detail/components/TelCode";
import {createImageErrorHandler} from "@/utils/imageUtils";

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

const Info = (props) => {
  const {match: {params={}, url}, dispatch} = props;
  const [product, setProduct] = useState({});
  const [mainPic, setMainPic] = useState(undefined);
  const [list, setList] = useState(undefined);
  const [listRight, setListRight] = useState(undefined);
  const [tabKey, setTabKey] = useState('info');
  const [listRel, setListRel] = useState(undefined);
  const [visible, setVisible] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  // 图片滚动控制函数
  const goToPreviousImage = () => {
    if (product.mainPic?.length > 0) {
      const newIndex = currentImageIndex > 0 ? currentImageIndex - 1 : product.mainPic.length - 1;
      setCurrentImageIndex(newIndex);
      setMainPic(product.mainPic[newIndex].url);
    }
  };

  const goToNextImage = () => {
    if (product.mainPic?.length > 0) {
      const newIndex = currentImageIndex < product.mainPic.length - 1 ? currentImageIndex + 1 : 0;
      setCurrentImageIndex(newIndex);
      setMainPic(product.mainPic[newIndex].url);
    }
  };

  // 触屏滚动支持
  const handleTouchStart = (e) => {
    const touch = e.touches[0];
    setTouchStartX(touch.clientX);
  };

  const handleTouchEnd = (e) => {
    const touch = e.changedTouches[0];
    const touchEndX = touch.clientX;
    const diff = touchStartX - touchEndX;
    
    if (Math.abs(diff) > 50) { // 滑动距离超过50px才切换
      if (diff > 0) {
        goToNextImage(); // 向左滑动，显示下一张
      } else {
        goToPreviousImage(); // 向右滑动，显示上一张
      }
    }
  };

  const [touchStartX, setTouchStartX] = useState(0);

  useEffect(async () => {
    setMainPic(undefined);
    setCurrentImageIndex(0);
    let res = !params.preview ? await queryInfo({id: params.infoId}) : await previewInfo({id: params.infoId, preview: params.preview});
    let temp;
    if (res && res.data) {   
      temp = res.data;
      await genTdkCrumbs(temp, dispatch, '详情',  '');
      setProduct(res.data);
    }

    const { termTypeIds } = temp || {};

    res = await queryListSideCar({ pageName: 'info', });

    if (res?.data) {
      setList(res.data.rows);
    }

    res = await queryListSideCar({
      pageName: 'infoRight',
    });

    if (res?.data) {
      setListRight(res.data.rows);
    }

    res = await queryBookList({
      pageSize: 8,
      current: 1,
      pubType: 'info',
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
      <div className={styles.proButton} />
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
      renderItem={({cover, id, pubType, members, pubTitle, subTitle, createTime}) => (
        <List.Item>
            <Card className={styles.card} hoverable onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
                  cover={
                    <img 
                      alt={pubTitle} 
                      src={cover} 
                      loading="lazy"
                      onError={createImageErrorHandler({ width: '100%', height: '100%', fontSize: '14' })}
                    />
                  }>
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
            <Card className={styles.card} hoverable size="small" onClick={() => window.open(`/info-${item.id}.html`, '_blank')}
                  cover={
                    <img 
                      alt={item.pubTitle} 
                      src={item.cover} 
                      loading="lazy"
                      onError={createImageErrorHandler({ width: '100%', height: '100%', fontSize: '10' })}
                    />
                  }>
              <Card.Meta title={<Link to={`/info-${item.id}.html`} target="_blank" rel="noopener">{item.pubTitle}</Link>}/>
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
            <Card className={styles.card} hoverable size="small" onClick={() => window.open(`/info-${item.id}.html`, '_blank')}
                  cover={
                    <img 
                      alt={item.pubTitle} 
                      src={item.cover} 
                      loading="lazy"
                      onError={createImageErrorHandler({ width: '100%', height: '100%', fontSize: '12' })}
                    />
                  }>
              <Card.Meta title={<Link to={`/info-${item.id}.html`} target="_blank" rel="noopener">{item.pubTitle}</Link>}/>
            </Card>
        </List.Item>
      )}
    />
  );

  const rowBody = product.pubTitle && (
    <Row gutter={24}>
      <Col lg={6} md={24}>
        {product.mainPic?.length > 0 && (<Card bordered={false} style={{ marginBottom: 24, }} >
            <div className={styles.imagePreviewContainer}>
              <div 
                className={styles.avatarHolder}
                onTouchStart={handleTouchStart}
                onTouchEnd={handleTouchEnd}
              >
                {product.mainPic[currentImageIndex] && (
                  <img 
                    alt={product.pubTitle} 
                    src={mainPic ?? product.mainPic[currentImageIndex].url} 
                    loading="lazy" 
                    onError={createImageErrorHandler({ width: '100%', height: '100%', fontSize: '16' })} 
                    style={{objectFit:'contain', width:'100%', height:'100%'}}
                  />
                )}
                
                {/* 滚动按钮 - 桌面端显示 */}
                {product.mainPic.length > 1 && (
                  <>
                    <button 
                      className={styles.scrollButton} 
                      onClick={goToPreviousImage}
                      style={{ left: '10px' }}
                    >
                      ‹
                    </button>
                    <button 
                      className={styles.scrollButton} 
                      onClick={goToNextImage}
                      style={{ right: '10px' }}
                    >
                      ›
                    </button>
                  </>
                )}
                
                {/* 图片指示器 */}
                {product.mainPic.length > 1 && (
                  <div className={styles.imageIndicator}>
                    {product.mainPic.map((_, index) => (
                      <span 
                        key={index}
                        className={`${styles.indicatorDot} ${index === currentImageIndex ? styles.active : ''}`}
                        onClick={() => {
                          setCurrentImageIndex(index);
                          setMainPic(product.mainPic[index].url);
                        }}
                      />
                    ))}
                  </div>
                )}
              </div>
            </div>
        </Card>)}
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
              {tabKey === 'info' ? (<>{bodyContent(product?.pubContent) || '没有内容'}<Divider style={{ margin: '40px 0 24px', }} /></>) : <Comments id={product?.id}/>}
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
}))(Info);