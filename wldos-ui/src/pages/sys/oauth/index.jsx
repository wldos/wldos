import { Card } from 'antd';
import React, { Component } from 'react';
import { GridContent } from '@ant-design/pro-layout';
import styles from './index.less';
import OAuth2Config from "@/pages/sys/oauth/components/OAuth2Config";

const operationTabList = [
  {
    key: 'wechat',
    tab: (
      <span>
        微信{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
  {
    key: 'qq',
    tab: (
      <span>
        QQ{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
  {
    key: 'weibo',
    tab: (
      <span>
        微博{' '}
        <span
          style={{
            fontSize: 14,
          }}
        >
        </span>
      </span>
    ),
  },
];

class OAuth extends Component {
  state = {
    oauthType: 'wechat',
  };

  onTabChange = (key) => {
    this.setState({
      oauthType: key,
    });
  };

  renderChildrenByTabKey = (oauthType) => {

    return <OAuth2Config oauthType={oauthType} key={oauthType}/>;
  };

  render() {
    const { oauthType } = this.state;
    return (
      <Card
        className={styles.tabsCard}
        bordered={false}
        tabList={operationTabList}
        activeTabKey={oauthType}
        onTabChange={this.onTabChange}
      >
        {this.renderChildrenByTabKey(oauthType)}
      </Card>
    );
  }
}

export default OAuth;
