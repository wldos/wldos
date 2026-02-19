/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, {useEffect, useState} from 'react';
import {history, connect, SelectLang } from 'umi';
import Avatar from './AvatarDropdown';
import HeaderSearch from '../HeaderSearch';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';
import {Button, Switch} from "antd";
import updateDarkTheme from "@/components/DarkTheme/UpdateTheme";

/** 从菜单树中收集 menuRegion===nav_avatar 的项，保留子菜单结构 */
const collectAvatarMenuItems = (menus) => {
  if (!menus || !Array.isArray(menus)) return [];
  const result = [];
  const walk = (items) => {
    (items || []).forEach((item) => {
      if (item.menuRegion === 'nav_avatar' && item.path) {
        const node = { path: item.path, name: item.name, icon: item.icon, displayOrder: item.displayOrder };
        if (item.children && item.children.length) {
          node.children = item.children
            .filter((c) => c.path)
            .map((c) => ({ path: c.path, name: c.name, icon: c.icon, displayOrder: c.displayOrder }))
            .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        }
        result.push(node);
      }
      if (item.children && item.children.length) walk(item.children);
    });
  };
  walk(menus);
  return result.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
};

const GlobalHeaderRight = (props) => {
    const {theme, layout, currentUser = {
        avatar: '',
        nickname: '',
    }, route, menuData} = props;

    const {module} = route && route['/'] || '';
    const [darkMode, setDarkMode] = useState(localStorage.getItem("darkMode") === "1");

    const switchDarkMode = () => {
      localStorage.setItem("darkMode", darkMode ? "0" : "1");
      let temp = !darkMode;
      setDarkMode(temp);
      updateDarkTheme(temp).then();
    };

    useEffect(() => {
     if (darkMode) {
       updateDarkTheme(true).then();
     }
    });

    let className = styles.right;

    if (theme === 'dark' &&  layout === 'top') {
        className = `${styles.right}  ${styles.dark}`;
    }
  const search = (pathname = '/search', value) => {
    const data = {wd: value};
    const path = {
      pathname,
      state: data,
    };
    history.push(path);
    }

    return (
        <div className={className}>
            <HeaderSearch
                className={`${styles.action} ${styles.search}`}
                placeholder="站内搜索"
                defaultValue="云平台"
                options={[
                    {
                        label: <a onClick={() => search('/search', '云平台')} href="#">云平台</a>,
                        value: '云平台',
                    },
                    {
                        label: <a onClick={() => search('/search', '技术')}
                                  href="#">技术</a>,
                        value: '技术',
                    },
                    {
                        label: <a onClick={() => search('/search', '信息')}
                                  href="#">信息</a>,
                        value: '信息',
                    },
                ]}
                onSearch={value => search(`/search`, value)}
            />
            {
                currentUser?.nickname ? (<NoticeIconView disablePolling />) : ('')
            }
            <Avatar menu avatarMenu={collectAvatarMenuItems(menuData)}/>
          {/* <SelectLang className={styles.action} /> */}
            <span className={styles.action}>
              <Switch checkedChildren="🌙" unCheckedChildren="☀" onClick={switchDarkMode} defaultChecked={darkMode} size={"small"} />
            </span>
          {module === 'category' && <span className={styles.action}>
            <Button
                type="primary"
                shape="round"
                size="small"
                href="/info/pub/create"
            >发布信息</Button>
            </span>}
        </div>
    )
}

export default connect(({ user, settings }) => ({
  currentUser: user.currentUser,
  route: user.route,
  menuData: user.menuData || [],
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
