import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Progress, Tag, Badge, Button, Space, Tooltip } from 'antd';
import {
  MonitorOutlined,
  DatabaseOutlined,
  CloudOutlined,
  SecurityScanOutlined,
  UserOutlined,
  FileTextOutlined,
  SettingOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined
} from '@ant-design/icons';
import styles from "@/pages/sys/monitor/style.less";

const Monitor = () => {
  const [imageSrc, setImageSrc] = useState('');
  const [loadError, setLoadError] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [systemStats, setSystemStats] = useState({
    cpu: 45,
    memory: 68,
    disk: 32,
    network: 85,
    uptime: '15天8小时',
    users: 1247,
    requests: 89456,
    errors: 0
  });

  // 多级容错图片源列表
  const imageSources = [
    'http://www.gitee.com/wldos/wldos/store/wldos.svg',
    '/store/logo-wldos.svg',  // 通过代理访问后端文件服务
    '/assets/images/wldos-logo.svg',
    '/assets/images/default-logo.png',
    'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjMwIiBoZWlnaHQ9IjIzMCIgdmlld0JveD0iMCAwIDIzMCAyMzAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMzAiIGhlaWdodD0iMjMwIiByeD0iMTE1IiBmaWxsPSIjMDA3Q0ZGIi8+Cjx0ZXh0IHg9IjExNSIgeT0iMTIwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSJ3aGl0ZSIgZm9udC1zaXplPSIyNCIgZm9udC1mYW1pbHk9IkFyaWFsIj5XTERPUzwvdGV4dD4KPC9zdmc+'
  ];

  // 尝试加载下一个图片源
  const tryNextImage = (index = 0) => {
    if (index >= imageSources.length) {
      setLoadError(true);
      return;
    }

    const img = new Image();
    img.onload = () => {
      setImageSrc(imageSources[index]);
      setLoadError(false);
    };
    img.onerror = () => {
      console.warn(`图片加载失败: ${imageSources[index]}`);
      tryNextImage(index + 1);
    };
    img.src = imageSources[index];
  };

  useEffect(() => {
    tryNextImage();
  }, []);

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

  // 模拟系统数据更新
  useEffect(() => {
    const interval = setInterval(() => {
      setSystemStats(prev => ({
        ...prev,
        cpu: Math.max(10, Math.min(90, prev.cpu + (Math.random() - 0.5) * 10)),
        memory: Math.max(20, Math.min(95, prev.memory + (Math.random() - 0.5) * 8)),
        network: Math.max(30, Math.min(100, prev.network + (Math.random() - 0.5) * 15)),
        requests: prev.requests + Math.floor(Math.random() * 10),
        errors: 0 // 始终保持为0
      }));
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{
      background: 'linear-gradient(135deg, #88ADC7 0%, #f0f2f5 100%)',
      minHeight: 'calc(100vh - 48px)', // 减去标签栏高度
      padding: '16px',
      position: 'relative'
    }}>
      {/* 背景装饰 */}
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        background: 'url("data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.1\'%3E%3Ccircle cx=\'30\' cy=\'30\' r=\'4\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")',
        opacity: 0.3
      }} />

      {/* 顶部控制栏 */}
      <div style={{
        position: 'relative',
        zIndex: 10,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '16px',
        background: 'rgba(255,255,255,0.1)',
        backdropFilter: 'blur(10px)',
        borderRadius: '12px',
        padding: '12px 20px'
      }}>
        <div style={{ color: 'white', fontSize: '20px', fontWeight: 'bold' }}>
          <MonitorOutlined style={{ marginRight: '8px' }} />
          系统监控中心
        </div>
        <Space>
          <Tooltip title="刷新数据">
            <Button
              type="text"
              icon={<ReloadOutlined />}
              style={{ color: 'white' }}
              onClick={() => window.location.reload()}
            />
          </Tooltip>
          <Button
            type="primary"
            icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
            onClick={toggleFullscreen}
          >
            {isFullscreen ? '退出全屏' : '全屏视图'}
          </Button>
        </Space>
      </div>

      {/* 主要内容区域 */}
      <div style={{ position: 'relative', zIndex: 10 }}>
        <Row gutter={[16, 16]} style={{ height: 'calc(100vh - 200px)' }}>
          {/* 系统概览 */}
          <Col xs={24} lg={8} style={{ height: '100%' }}>
            <Card
              title={
                <Space>
                  <MonitorOutlined style={{ color: '#1890ff' }} />
                  系统概览
                </Space>
              }
              style={{
                background: 'rgba(255,255,255,0.95)',
                backdropFilter: 'blur(10px)',
                borderRadius: '12px',
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
                height: '100%'
              }}
              bodyStyle={{ height: 'calc(100% - 57px)', overflow: 'auto' }}
            >
              <Space direction="vertical" size="large" style={{ width: '100%' }}>
                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>CPU使用率</div>
                  <Progress
                    percent={Math.round(systemStats.cpu)}
                    strokeColor={{
                      '0%': '#108ee9',
                      '100%': '#87d068',
                    }}
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    {Math.round(systemStats.cpu)}%
                  </div>
                </div>

                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>内存使用率</div>
                  <Progress
                    percent={Math.round(systemStats.memory)}
                    strokeColor={{
                      '0%': '#87d068',
                      '100%': '#ff7875',
                    }}
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    {Math.round(systemStats.memory)}%
                  </div>
                </div>

                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>磁盘使用率</div>
                  <Progress
                    percent={systemStats.disk}
                    strokeColor="#52c41a"
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    {systemStats.disk}%
                  </div>
                </div>

                {/* 添加更多内容填充空间 */}
                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>系统负载</div>
                  <Progress
                    percent={Math.round((systemStats.cpu + systemStats.memory) / 2)}
                    strokeColor="#722ed1"
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    {Math.round((systemStats.cpu + systemStats.memory) / 2)}%
                  </div>
                </div>
              </Space>
            </Card>
          </Col>

          {/* 网络状态 */}
          <Col xs={24} lg={8} style={{ height: '100%' }}>
            <Card
              title={
                <Space>
                  <CloudOutlined style={{ color: '#52c41a' }} />
                  网络状态
                </Space>
              }
              style={{
                background: 'rgba(255,255,255,0.95)',
                backdropFilter: 'blur(10px)',
                borderRadius: '12px',
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
                height: '100%'
              }}
              bodyStyle={{ height: 'calc(100% - 57px)', overflow: 'auto' }}
            >
              <Space direction="vertical" size="large" style={{ width: '100%' }}>
                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>网络负载</div>
                  <Progress
                    percent={Math.round(systemStats.network)}
                    strokeColor={{
                      '0%': '#52c41a',
                      '100%': '#ff4d4f',
                    }}
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    {Math.round(systemStats.network)}%
                  </div>
                </div>

                <Row gutter={16}>
                  <Col span={12}>
                    <Statistic
                      title="在线用户"
                      value={systemStats.users}
                      valueStyle={{ color: '#1890ff' }}
                    />
                  </Col>
                  <Col span={12}>
                    <Statistic
                      title="今日请求"
                      value={systemStats.requests}
                      valueStyle={{ color: '#52c41a' }}
                    />
                  </Col>
                </Row>

                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>系统运行时间</div>
                  <div style={{
                    background: '#f0f0f0',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    textAlign: 'center',
                    fontSize: '16px',
                    fontWeight: 'bold',
                    color: '#1890ff'
                  }}>
                    {systemStats.uptime}
                  </div>
                </div>

                {/* 添加网络统计 */}
                <Row gutter={16}>
                  <Col span={12}>
                    <Statistic
                      title="带宽使用"
                      value="45%"
                      valueStyle={{ color: '#fa8c16' }}
                    />
                  </Col>
                  <Col span={12}>
                    <Statistic
                      title="连接数"
                      value="1,234"
                      valueStyle={{ color: '#13c2c2' }}
                    />
                  </Col>
                </Row>
              </Space>
            </Card>
          </Col>

          {/* 安全状态 */}
          <Col xs={24} lg={8} style={{ height: '100%' }}>
            <Card
              title={
                <Space>
                  <SecurityScanOutlined style={{ color: '#fa8c16' }} />
                  安全状态
                </Space>
              }
              style={{
                background: 'rgba(255,255,255,0.95)',
                backdropFilter: 'blur(10px)',
                borderRadius: '12px',
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
                height: '100%'
              }}
              bodyStyle={{ height: 'calc(100% - 57px)', overflow: 'auto' }}
            >
              <Space direction="vertical" size="large" style={{ width: '100%' }}>
                <div>
                  <div style={{ marginBottom: '12px', color: '#666' }}>安全等级</div>
                  <Tag color="green" style={{ fontSize: '14px', padding: '4px 12px' }}>
                    <CheckCircleOutlined style={{ marginRight: '4px' }} />
                    安全
                  </Tag>
                </div>

                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>错误日志</div>
                  <div style={{
                    background: '#f0f0f0',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    textAlign: 'center',
                    fontSize: '14px',
                    color: systemStats.errors > 0 ? '#ff4d4f' : '#52c41a',
                    fontWeight: 'bold'
                  }}>
                    {systemStats.errors > 0 ? `系统错误 (${systemStats.errors})` : '无错误'}
                  </div>
                </div>

                <Row gutter={16}>
                  <Col span={12}>
                    <Statistic
                      title="数据库连接"
                      value="正常"
                      valueStyle={{ color: '#52c41a', fontSize: '16px' }}
                    />
                  </Col>
                  <Col span={12}>
                    <Statistic
                      title="API状态"
                      value="正常"
                      valueStyle={{ color: '#52c41a', fontSize: '16px' }}
                    />
                  </Col>
                </Row>

                {/* 添加安全统计 */}
                <div>
                  <div style={{ marginBottom: '8px', color: '#666' }}>安全扫描</div>
                  <Progress
                    percent={100}
                    strokeColor="#52c41a"
                    showInfo={false}
                  />
                  <div style={{ textAlign: 'right', color: '#666', fontSize: '12px' }}>
                    已完成
                  </div>
                </div>

                <Row gutter={16}>
                  <Col span={12}>
                    <Statistic
                      title="防火墙"
                      value="启用"
                      valueStyle={{ color: '#52c41a', fontSize: '16px' }}
                    />
                  </Col>
                  <Col span={12}>
                    <Statistic
                      title="SSL证书"
                      value="有效"
                      valueStyle={{ color: '#52c41a', fontSize: '16px' }}
                    />
                  </Col>
                </Row>
              </Space>
            </Card>
          </Col>
        </Row>
      </div>
      </div>
  );
}

export default Monitor;
