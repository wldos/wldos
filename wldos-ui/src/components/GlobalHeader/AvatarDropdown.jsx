import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons';
import {Avatar, Menu} from 'antd';
import React from 'react';
import {connect, history} from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import {getPageQuery, redirectReq} from "@/utils/utils";
import {stringify} from "querystring";

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

        history.push(`/account/${key}`);
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
        } = this.props;
        const menuHeaderDropdown = (
            <Menu className={styles.menu} selectedKeys={[]} onClick={this.onMenuClick}>
                {menu && (
                    <Menu.Item key="center">
                        <UserOutlined/>
                        个人中心
                    </Menu.Item>
                )}
                {menu && (
                    <Menu.Item key="settings">
                        <SettingOutlined/>
                        个人设置
                    </Menu.Item>
                )}
                {menu && <Menu.Divider/>}

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
