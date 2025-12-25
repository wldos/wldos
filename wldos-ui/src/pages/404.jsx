import { Button, Result } from 'antd';
import React, { useEffect } from 'react';
import { history } from 'umi';
import { connect } from 'umi';

const NoFoundPage = ({ dispatch }) => {
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'user/saveTdk',
        payload: {
          pubType: 'page',
          title: '页面不存在',
          keywords: '',
          description: ''
        }
      });
    }
  }, [dispatch]);

  return (
    <Result
      status="404"
      title="404"
      subTitle="Sorry, the page you visited does not exist."
      extra={
        <Button type="primary" onClick={() => history.push('/')}>
          Back Home
        </Button>
      }
    />
  );
};

export default connect()(NoFoundPage);
