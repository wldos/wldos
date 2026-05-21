/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {history, connect, useIntl } from 'umi';
import Avatar from './AvatarDropdown';
import HeaderSearch from '../HeaderSearch';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';
import {Button, Switch} from "antd";
import updateDarkTheme from "@/components/DarkTheme/UpdateTheme";
import LanguageSwitcher from "@/components/LanguageSwitcher";
import { querySearchHints } from '@/services/user';

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
    const intl = useIntl();

    const {module} = route && route['/'] || '';
    const [darkMode, setDarkMode] = useState(localStorage.getItem("darkMode") === "1");
    const [hintItems, setHintItems] = useState([]);

    useEffect(() => {
      let cancelled = false;
      (async () => {
        try {
          const res = await querySearchHints();
          const raw = res?.data?.data ?? res?.data;
          const list = Array.isArray(raw)
            ? raw.map((x) => (typeof x === 'string' ? x.trim() : '')).filter(Boolean)
            : [];
          if (!cancelled && list.length) {
            setHintItems(list);
          }
        } catch (e) {
          /* 接口不可用或已含 JWT 场景下失败时，保留下方国际化默认提示 */
        }
      })();
      return () => { cancelled = true; };
    }, []);

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
    const search = useCallback((pathname = '/search', value) => {
    const data = {wd: value};
    const path = {
      pathname,
      state: data,
    };
    history.push(path);
    }, []);

    const defaultHints = useMemo(
      () => [
        intl.formatMessage({ id: 'component.globalHeader.search.example1' }),
        intl.formatMessage({ id: 'component.globalHeader.search.example2' }),
        intl.formatMessage({ id: 'component.globalHeader.search.example3' }),
      ],
      [intl],
    );

    const hints = hintItems.length ? hintItems : defaultHints;

    const searchOptions = useMemo(
      () =>
        hints.map((text) => ({
          label: (
            <a onClick={() => search('/search', text)} href="#" rel="nofollow">
              {text}
            </a>
          ),
          value: text,
        })),
      [hints, search],
    );

    return (
        <div className={className}>
            <HeaderSearch
                key={hintItems.length ? `hints-${hintItems.join('\u001f')}` : 'hints-i18n'}
                className={`${styles.action} ${styles.search}`}
                placeholder={intl.formatMessage({ id: 'component.globalHeader.search' })}
                defaultValue={hints[0]}
                options={searchOptions}
                onSearch={value => search(`/search`, value)}
            />
            {
                currentUser?.nickname ? (<NoticeIconView disablePolling />) : ('')
            }
            <Avatar menu avatarMenu={collectAvatarMenuItems(menuData)}/>
            <LanguageSwitcher className={styles.action} />
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
