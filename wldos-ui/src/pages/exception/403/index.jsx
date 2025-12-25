import { Link, history } from 'umi';
import { Result, Button } from 'antd';
import React from 'react';

export default () => (
  <Result
    status="403"
    title="403"
    style={{
      background: 'none',
    }}
    subTitle="抱歉，您没有权限访问此页面"
    extra={[
      <Button type="primary" key="home" onClick={() => history.push('/')}>
        返回首页
      </Button>,
      <Button key="back" onClick={() => history.goBack()}>
        返回上页
      </Button>,
    ]}
  />
);
