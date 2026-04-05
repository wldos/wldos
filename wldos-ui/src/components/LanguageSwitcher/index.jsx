import React from 'react';
import { Dropdown, Menu } from 'antd';
import { TranslationOutlined } from '@ant-design/icons';
import { useIntl, setLocale } from 'umi';

const LanguageSwitcher = ({ className }) => {
  const intl = useIntl();

  const langMenu = (
    <Menu
      selectedKeys={[intl.locale]}
      onClick={({ key }) => setLocale(key, false)}
      items={[
        {
          key: 'en-US',
          label: (
            <span>
              <span style={{ marginRight: 8 }}>US</span>
              English
            </span>
          ),
        },
        {
          key: 'zh-CN',
          label: (
            <span>
              <span style={{ marginRight: 8 }}>CN</span>
              简体中文
            </span>
          ),
        },
        {
          key: 'zh-TW',
          label: (
            <span>
              <span style={{ marginRight: 8 }}>HK</span>
              繁體中文
            </span>
          ),
        },
      ]}
    />
  );

  return (
    <Dropdown overlay={langMenu} placement="bottomRight" trigger={['hover', 'click']}>
      <span className={className}>
        <TranslationOutlined />
      </span>
    </Dropdown>
  );
};

export default LanguageSwitcher;

