/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React from 'react';
import ProLayout, { PageLoading } from '@ant-design/pro-layout';
import { Redirect, connect } from 'umi';
import { stringify } from 'querystring';
import {wldosHeader} from "@/utils/utils";

class SecurityLayout extends React.Component {
  state = {
    isReady: false,
  };

  componentDidMount() {
    this.setState({
      isReady: true,
    });

    const { dispatch } = this.props;

    if (dispatch) {
      dispatch({
        type: 'user/fetchCurrent',
      });
      dispatch({
        type: 'user/fetchSite',
      });
    }
  }

  render() {
    const { isReady } = this.state;
    const { children, loading, site, settings, currentUser } = this.props;
    // 你可以把它替换成你自己的登录认证规则（比如判断 token 是否存在）
    const isLogin = currentUser && currentUser.id;
    const queryString = stringify({
      redirect: window.location.href,
    });

    if ((!isLogin && loading) || !isReady) {
      return <PageLoading />;
    }

    if (!isLogin && window.location.pathname !== '/user/login') {
      return <Redirect to={`/user/login?${queryString}`} />;
    }

    return (
      <>
        {wldosHeader(site)}
        <ProLayout
          title={`${site.siteTitle}_${site.siteDescription || ''}` || ''}
          {...settings}
          pure={true}
        >
          {children}
        </ProLayout>
      </>
    );
  }
}

export default connect(({global, settings, user, loading }) => ({
  currentUser: user.currentUser,
  site: user.site,
  loading: loading.models.user,
  settings: {
    ...settings,
    collapsed: global.collapsed,
  }
}))(SecurityLayout);
