import { Link, FormattedMessage } from 'umi';
import { Result, Button } from 'antd';
import React from 'react';
export default () => (
  <Result
    status="500"
    title="500"
    style={{
      background: 'none',
    }}
    subTitle={
      <FormattedMessage
        id="exceptionand500.description.500"
        defaultMessage="Sorry, the server is reporting an error."
      />
    }
    extra={
      <Link to="/">
        <Button type="primary">
          <FormattedMessage
            id="exceptionand500.exception.back"
            defaultMessage="Back to home"
          />
        </Button>
      </Link>
    }
  />
);
