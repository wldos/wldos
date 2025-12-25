import React, { useState, useEffect, useRef } from 'react';
import { Modal, Button, Space } from 'antd';
import { FullscreenOutlined, FullscreenExitOutlined } from '@ant-design/icons';

const FullscreenModal = ({
  children,
  fullscreen = false,
  onFullscreenChange,
  ...modalProps
}) => {
  const [isFullscreen, setIsFullscreen] = useState(fullscreen);
  const [contentHeight, setContentHeight] = useState('auto');
  const contentRef = useRef(null);

  // 计算内容高度
  const calculateHeight = () => {
    if (isFullscreen && contentRef.current) {
      const contentHeight = contentRef.current.scrollHeight;
      const viewportHeight = window.innerHeight - 200; // 减去header、footer和padding的高度
      
      // 在全屏模式下，如果内容高度超过视窗高度，则设置固定高度并允许滚动
      if (contentHeight > viewportHeight) {
        setContentHeight(`${viewportHeight}px`);
      } else {
        setContentHeight('auto');
      }
    } else {
      setContentHeight('auto');
    }
  };

  useEffect(() => {
    if (isFullscreen && contentRef.current) {
      // 使用setTimeout确保DOM完全渲染后再计算
      const timer = setTimeout(calculateHeight, 100);
      
      // 监听窗口大小变化
      const handleResize = () => {
        if (isFullscreen) {
          calculateHeight();
        }
      };
      
      window.addEventListener('resize', handleResize);
      
      return () => {
        clearTimeout(timer);
        window.removeEventListener('resize', handleResize);
      };
    } else {
      setContentHeight('auto');
    }
  }, [isFullscreen, children]);

  // 全屏状态变化时重新计算高度
  useEffect(() => {
    if (isFullscreen) {
      const timer = setTimeout(calculateHeight, 200);
      return () => clearTimeout(timer);
    }
  }, [isFullscreen]);

  // Modal独立全屏切换功能
  const toggleFullscreen = () => {
    const newFullscreenState = !isFullscreen;
    setIsFullscreen(newFullscreenState);
    
    // 通知父组件全屏状态变化
    if (onFullscreenChange) {
      onFullscreenChange(newFullscreenState);
    }
  };

  // 动态计算Modal配置
  const modalConfig = {
    ...modalProps,
    width: isFullscreen ? '100vw' : modalProps.width || 800,
    style: {
      ...modalProps.style,
      ...(isFullscreen && {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        margin: 0,
        maxWidth: '100vw',
        maxHeight: '100vh',
        zIndex: 9999,
      }),
    },
    bodyStyle: {
      ...modalProps.bodyStyle,
      ...(isFullscreen && {
        height: 'calc(100vh - 120px)',
        maxHeight: 'calc(100vh - 120px)',
        overflowY: 'hidden',
        padding: '24px',
        display: 'flex',
        flexDirection: 'column',
        position: 'relative',
      }),
    },
    title: (
      <Space>
        {modalProps.title}
        <Button
          type="text"
          size="small"
          icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
          onClick={toggleFullscreen}
          title={isFullscreen ? '退出全屏' : '全屏显示'}
        />
      </Space>
    ),
  };

  return (
    <Modal {...modalConfig}>
      <div ref={contentRef} style={{ 
        display: 'flex', 
        flexDirection: 'column', 
        height: isFullscreen ? '100%' : 'auto',
        minHeight: isFullscreen ? 'calc(100vh - 200px)' : 'auto',
        position: 'relative'
      }}>
        <div style={{ 
          flex: 1, 
          overflowY: isFullscreen ? 'auto' : 'visible',
          minHeight: 0, // 确保flex子元素能够正确收缩
          paddingBottom: isFullscreen ? '20px' : '0' // 给底部留出空间
        }}>
          {children}
        </div>
      </div>
    </Modal>
  );
};

export default FullscreenModal;
