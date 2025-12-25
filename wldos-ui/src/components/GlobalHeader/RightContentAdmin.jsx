import {Tooltip, Button} from 'antd';
import {QuestionCircleOutlined, FullscreenOutlined, FullscreenExitOutlined} from '@ant-design/icons';
import React, {useState, useEffect} from 'react';
import {connect, SelectLang} from 'umi';
import Avatar from './AvatarDropdown';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';

const GlobalHeaderRightAdmin = (props) => {
    const {theme, layout, currentUser = {
        avatar: '',
        nickname: '',
    },} = props;

    const [isFullscreen, setIsFullscreen] = useState(false);
    const [isMobile, setIsMobile] = useState(typeof window !== 'undefined' ? (window.innerWidth || document.documentElement.clientWidth) < 768 : false);

    let className = styles.right;

    if (theme === 'dark' &&  layout === 'top') {
        className = `${styles.right}  ${styles.dark}`;
    }

    // 全屏切换功能
    const toggleFullscreen = () => {
        if (!document.fullscreenElement) {
            // 进入全屏
            if (document.documentElement.requestFullscreen) {
                document.documentElement.requestFullscreen();
            } else if (document.documentElement.webkitRequestFullscreen) {
                document.documentElement.webkitRequestFullscreen();
            } else if (document.documentElement.mozRequestFullScreen) {
                document.documentElement.mozRequestFullScreen();
            } else if (document.documentElement.msRequestFullscreen) {
                document.documentElement.msRequestFullscreen();
            }
        } else {
            // 退出全屏
            if (document.exitFullscreen) {
                document.exitFullscreen();
            } else if (document.webkitExitFullscreen) {
                document.webkitExitFullscreen();
            } else if (document.mozCancelFullScreen) {
                document.mozCancelFullScreen();
            } else if (document.msExitFullscreen) {
                document.msExitFullscreen();
            }
        }
    };

    // 监听全屏状态变化
    useEffect(() => {
        const width = window.innerWidth || document.documentElement.clientWidth;
        const mobile = width < 768;
        setIsMobile(mobile);

        if (mobile) return;

        const handleFullscreenChange = () => {
            setIsFullscreen(!!document.fullscreenElement);
        };

        document.addEventListener('fullscreenchange', handleFullscreenChange);
        document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
        document.addEventListener('mozfullscreenchange', handleFullscreenChange);
        document.addEventListener('MSFullscreenChange', handleFullscreenChange);

        return () => {
            document.removeEventListener('fullscreenchange', handleFullscreenChange);
            document.removeEventListener('webkitfullscreenchange', handleFullscreenChange);
            document.removeEventListener('mozfullscreenchange', handleFullscreenChange);
            document.removeEventListener('MSFullscreenChange', handleFullscreenChange);
        };
    }, []);

    return (
        <div className={className}>
            { <Tooltip title="使用文档">
                <a
                    style={{
                        color: 'inherit',
                    }}
                    target="_blank"
                    href="http://www.wldos.com/doc"
                    rel="noopener noreferrer"
                    className={styles.action}
                >
                    <QuestionCircleOutlined/>
                </a>
            </Tooltip> }

            {/* 全屏控件 - 仅桌面端显示 */}
            {!isMobile && (
                <Tooltip title={isFullscreen ? "退出全屏" : "进入全屏"}>
                    <Button
                        className={`${styles.action} fullscreen-toggle`}
                        type="text"
                        icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                        onClick={toggleFullscreen}
                        style={{
                            color: 'inherit',
                            border: 'none',
                            background: 'transparent',
                            boxShadow: 'none'
                        }}
                    />
                </Tooltip>
            )}

            {
                currentUser && currentUser.nickname ? (<NoticeIconView/>) : ('')
                // @TODO isManageSide从后端取
            }
            <Avatar menu isManageSide={0}/>
            <SelectLang className={styles.action}/>
        </div>
    )
}

export default connect(({ user, settings }) => ({
  currentUser: user.currentUser,
theme: settings.navTheme,
layout: settings.layout,
}))(GlobalHeaderRightAdmin);
