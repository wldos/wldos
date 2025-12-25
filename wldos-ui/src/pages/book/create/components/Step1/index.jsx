import React, {useEffect, useState, useCallback} from 'react';
import {Divider, List, Popover, Card, Button, Space, Typography, Skeleton} from 'antd';
import {connect} from 'umi';
import styles from './index.less';

const {Title, Text} = Typography;

const Step1 = ({dispatch, bookInfo: {list = [],}, loading}) => {
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'bookInfo/fetchCategory',
      });
    }
    
    // 检测移动端
    const checkMobile = () => {
      setIsMobile(window.innerWidth <= 768);
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    
    return () => window.removeEventListener('resize', checkMobile);
  }, [dispatch]);

  const finishCategory = useCallback((termTypeId, termName) => {
    if (dispatch) {
      dispatch({
        type: 'bookInfo/saveStepFormData',
        payload: {termTypeIds: [{label: termName, value: termTypeId}]},
      });
      dispatch({
        type: 'bookInfo/saveCurrentStep',
        payload: 'info',
      });
    }
  }, [dispatch]);

  const rendItem = useCallback((item) => {
    if (!item.children) return "";
    
    const len = Math.min(item.children.length, 4);
    const mLen = Math.min(item.children.length, 3);

    return item.children && (
      <div className={styles.subCategoryContainer}>
        <List
          className={styles.itemList}
          dataSource={item.children || []}
          grid={{ 
            gutter: [12, 12], 
            xs: 1, 
            sm: 2, 
            md: len, 
            lg: len, 
            xl: len, 
            xxl: len 
          }}
          renderItem={(it) => (
            <List.Item>
              <Button 
                type="text" 
                block 
                className={styles.subCategoryBtn}
                onClick={() => finishCategory(it.key, it.title)}
              >
                {it.title}
              </Button>
            </List.Item>
          )}
        />
      </div>
    );
  }, [finishCategory]);

  const cardList = list && (
    <List
      rowKey="id"
      loading={loading}
      grid={{
        gutter: [16, 16],
        xs: 1,
        sm: 2,
        md: 3,
        lg: 4,
        xl: 4,
        xxl: 4
      }}
      dataSource={list}
      renderItem={(item) => (
        <List.Item>
          <Card
            className={styles.categoryCard}
            hoverable
            bodyStyle={{ padding: '16px', textAlign: 'center' }}
          >
            {isMobile ? (
              <Popover 
                placement="bottom" 
                title={item.title} 
                content={rendItem(item)} 
                trigger="click"
                overlayClassName={styles.mobilePopover}
              >
                <Button type="primary" block className={styles.categoryBtn}>
                  {item.title}
                </Button>
              </Popover>
            ) : (
              <Popover 
                placement="right" 
                title={item.title} 
                content={rendItem(item)} 
                trigger="hover"
                overlayClassName={styles.desktopPopover}
              >
                <Button type="primary" block className={styles.categoryBtn}>
                  {item.title}
                </Button>
              </Popover>
            )}
          </Card>
        </List.Item>
      )}
    />
  );

  // 骨架屏
  const skeletonList = (
    <List
      grid={{
        gutter: [16, 16],
        xs: 1,
        sm: 2,
        md: 3,
        lg: 4,
        xl: 4,
        xxl: 4
      }}
      dataSource={Array.from({ length: 8 })}
      renderItem={() => (
        <List.Item>
          <Card>
            <Skeleton active paragraph={{ rows: 2 }} />
          </Card>
        </List.Item>
      )}
    />
  );

  return (
    <div className={styles.categoryList}>
      <div className={styles.header}>
        <Title level={3} className={styles.title}>选择信息分类</Title>
        <Text type="secondary" className={styles.subtitle}>
          请选择您要发布的信息所属的分类，然后选择具体的子分类
        </Text>
      </div>
      
      <div className={styles.content}>
        {loading ? skeletonList : cardList}
      </div>
      
      <Divider style={{ margin: '40px 0 24px' }} />
      
      <div className={styles.desc}>
        <Title level={4}>使用说明</Title>
        <Space direction="vertical" size="small" style={{ width: '100%' }}>
          <div>
            <Text strong>选择分类：</Text>
            <Text>
              {isMobile 
                ? '点击分类卡片，在弹出的选项中选择具体的子分类' 
                : '将鼠标悬停在分类卡片上，在弹出的选项中选择具体的子分类'
              }
            </Text>
          </div>
          <div>
            <Text strong>填写信息：</Text>
            <Text>选择分类后，将进入信息填写页面，需要上传封面图和主图</Text>
          </div>
        </Space>
      </div>
    </div>
  );
};

export default connect(({bookInfo, loading}) => ({
  bookInfo,
  loading: loading.models.category,
}))(Step1);
