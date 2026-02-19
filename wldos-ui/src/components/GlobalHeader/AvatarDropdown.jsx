/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons';
import {Avatar, Menu} from 'antd';
import React from 'react';
import {connect, history} from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import {getPageQuery, redirectReq} from "@/utils/utils";
import {stringify} from "querystring";
import { renderIcon } from '@/utils/iconLibrary';

class AvatarDropdown extends React.Component {
    onMenuClick = (event) => {
        const {key} = event;

        if (key === 'logout') {
            const {dispatch} = this.props;

            if (dispatch) {
                dispatch({
                    type: 'login/logout',
                    payload: {
                        isManageSide: this.props.isManageSide === 0,
                    }
                });
            }

            return;
        }

        // key 可能是 path（如 /order/list）或 account 子路径（如 center）
        if (key.startsWith('/')) {
            history.push(key);
        } else {
            history.push(`/account/${key}`);
        }
    };

    login = () => {
        const {redirect} = getPageQuery();

        if (window.location.pathname !== '/user/login' && !redirect) {
            history.replace({
                pathname: '/user/login',
                search: stringify({
                    redirect: window.location.href,
                }),
            });
        }
    }

    render() {
        const {
            currentUser = {
                avatar: '',
                nickname: '',
                isManageSide: 0,
            },
            menu,
            avatarMenu = [],
        } = this.props;
        const hasAvatarItems = avatarMenu && avatarMenu.length > 0;
        const renderAvatarMenuItem = (item) => {
            if (item.children && item.children.length > 0) {
                return (
                    <Menu.SubMenu key={item.path} title={<span>{item.icon ? renderIcon(item.icon) : <UserOutlined />}{item.name || item.path}</span>}>
                        {item.children.map((child) => (
                            <Menu.Item key={child.path}>
                                {child.icon ? renderIcon(child.icon) : null}
                                {child.name || child.path}
                            </Menu.Item>
                        ))}
                    </Menu.SubMenu>
                );
            }
            return (
                <Menu.Item key={item.path}>
                    {item.icon ? renderIcon(item.icon) : <UserOutlined />}
                    {item.name || item.path}
                </Menu.Item>
            );
        };
        const menuHeaderDropdown = (
            <Menu className={styles.menu} selectedKeys={[]} onClick={this.onMenuClick}>
                {hasAvatarItems && avatarMenu.map((item) => renderAvatarMenuItem(item))}
                {/* 个人中心、个人设置始终保留（原写死项） */}
                <Menu.Item key="center">
                    <UserOutlined/>
                    个人中心
                </Menu.Item>
                <Menu.Item key="settings">
                    <SettingOutlined/>
                    个人设置
                </Menu.Item>
                <Menu.Divider/>

                <Menu.Item key="logout">
                    <LogoutOutlined/>
                    退出登录
                </Menu.Item>
            </Menu>
        );
        return currentUser && currentUser.nickname ? (
          <HeaderDropdown overlay={menuHeaderDropdown}>
            <span className={`${styles.action} ${styles.account}`}>
                <Avatar size="small" className={styles.avatar} src={currentUser.avatar} alt="avatar"/>
            {/* <span className={`${styles.name} anticon`}>{currentUser.nickname}</span> */}
            </span>
          </HeaderDropdown>
        ) : (
            <span className={`${styles.action} ${styles.account} ${styles.hide}`}>
                <span className={` anticon`}><a onClick={() => redirectReq('/user/login')}>登录</a></span>
      </span>
        );
    };
}

export default connect(({user}) => ({
    currentUser: user.currentUser,
}))(AvatarDropdown);
