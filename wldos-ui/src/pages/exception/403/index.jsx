import { FormattedMessage, history } from 'umi';
import { Result, Button } from 'antd';
import React from 'react';

export default () => (
  <Result
    status="403"
    title="403"
    style={{
      background: 'none',
    }}
    subTitle={
      <FormattedMessage
        id="exceptionand403.description.403"
        defaultMessage="Sorry, you are not authorized to access this page."
      />
    }
    extra={[
      <Button type="primary" key="home" onClick={() => history.push('/')}>
        <FormattedMessage
          id="exceptionand403.exception.back"
          defaultMessage="Back to home"
        />
      </Button>,
      <Button key="back" onClick={() => history.goBack()}>
        <FormattedMessage
          id="exceptionand403.exception.backToPrevious"
          defaultMessage="Back to previous page"
        />
      </Button>,
    ]}
  />
);
