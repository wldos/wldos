import { DefaultFooter } from '@ant-design/pro-layout';
import { GithubOutlined } from '@ant-design/icons';
import React from 'react';

const wldosFooterDom = (version) => (
  <DefaultFooter
    copyright={`${new Date().getFullYear()} WLDOS 云管端解决方案 ${version?? 'V1.9.1'}`}
    links={[
      {
        key: 'wldos',
        title: '开源社区',
        href: 'https://gitee.com/wldos/wldos',
        blankTarget: true,
      },
      {
        key: 'github',
        title: <GithubOutlined />,
        href: 'https://github.com/wldos/wldos',
        blankTarget: true,
      },
      {
        key: 'xiupu',
        title: '关于我们',
        href: 'https://gitee.com/wldos/wldos/wikis/%E5%85%B3%E4%BA%8E%E6%88%91%E4%BB%AC',
        blankTarget: true,
      },
    ]}
  />
);

export default wldosFooterDom;
