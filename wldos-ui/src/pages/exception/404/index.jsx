import { Link, FormattedMessage } from 'umi';
import { Result, Button } from 'antd';
import React from 'react';
export default () => (
  <Result
    status="404"
    title="404"
    style={{
      background: 'none',
    }}
    subTitle={
      <FormattedMessage
        id="exceptionand404.description.404"
        defaultMessage="Sorry, the page you visited does not exist."
      />
    }
    extra={
      <Link to="/">
        <Button type="primary">
          <FormattedMessage
            id="exceptionand404.exception.back"
            defaultMessage="Back to home"
          />
        </Button>
      </Link>
    }
  />
);
