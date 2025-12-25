import React, {useEffect, useState} from 'react';
import {history, connect, SelectLang } from 'umi';
import Avatar from './AvatarDropdown';
import HeaderSearch from '../HeaderSearch';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';
import {Button, Switch} from "antd";
import updateDarkTheme from "@/components/DarkTheme/UpdateTheme";

const GlobalHeaderRight = (props) => {
    const {theme, layout, currentUser = {
        avatar: '',
        nickname: '',
    }, route} = props;

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
                currentUser?.nickname ? (<NoticeIconView/>) : ('')
            }
            <Avatar menu/>
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
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
