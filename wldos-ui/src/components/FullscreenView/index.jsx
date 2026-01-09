/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState, useEffect, useRef } from 'react';
import { Button, Space, Tooltip, Modal, Card, Row, Col, Statistic, Progress, Tag, Badge } from 'antd';
import {
  FullscreenOutlined,
  FullscreenExitOutlined,
  DashboardOutlined,
  MonitorOutlined,
  DatabaseOutlined,
  CloudOutlined,
  SecurityScanOutlined,
  UserOutlined,
  FileTextOutlined,
  SettingOutlined,
  ReloadOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';

const FullscreenView = () => {
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [systemStats, setSystemStats] = useState({
    cpu: 45,
    memory: 68,
    disk: 32,
    network: 85,
    uptime: '15天8小时',
    users: 1247,
    requests: 89456,
    errors: 12
  });

  const fullscreenRef = useRef(null);

  // 全屏切换功能
  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      if (fullscreenRef.current?.requestFullscreen) {
        fullscreenRef.current.requestFullscreen();
      } else if (fullscreenRef.current?.webkitRequestFullscreen) {
        fullscreenRef.current.webkitRequestFullscreen();
      } else if (fullscreenRef.current?.mozRequestFullScreen) {
        fullscreenRef.current.mozRequestFullScreen();
      } else if (fullscreenRef.current?.msRequestFullscreen) {
        fullscreenRef.current.msRequestFullscreen();
      }
    } else {
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
        errors: Math.max(0, prev.errors + (Math.random() > 0.9 ? 1 : 0))
      }));
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  // 360视图内容
  const render360View = () => (
    <div style={{
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      minHeight: '100vh',
      padding: '24px',
      position: 'relative',
      overflow: 'hidden'
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
        marginBottom: '24px',
        background: 'rgba(255,255,255,0.1)',
        backdropFilter: 'blur(10px)',
        borderRadius: '12px',
        padding: '16px 24px'
      }}>
        <div style={{ color: 'white', fontSize: '20px', fontWeight: 'bold' }}>
          <DashboardOutlined style={{ marginRight: '8px' }} />
          系统360视图
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
        <Row gutter={[24, 24]}>
          {/* 系统概览 */}
          <Col xs={24} lg={8}>
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
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
              }}
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
              </Space>
            </Card>
          </Col>

          {/* 网络状态 */}
          <Col xs={24} lg={8}>
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
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
              }}
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
              </Space>
            </Card>
          </Col>

          {/* 安全状态 */}
          <Col xs={24} lg={8}>
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
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
              }}
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
                  <Badge
                    count={systemStats.errors}
                    style={{ backgroundColor: systemStats.errors > 10 ? '#ff4d4f' : '#52c41a' }}
                  >
                    <div style={{
                      background: '#f0f0f0',
                      padding: '8px 12px',
                      borderRadius: '6px',
                      textAlign: 'center',
                      fontSize: '14px'
                    }}>
                      系统错误
                    </div>
                  </Badge>
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
              </Space>
            </Card>
          </Col>

          {/* 快速操作 */}
          <Col xs={24}>
            <Card
              title={
                <Space>
                  <SettingOutlined style={{ color: '#722ed1' }} />
                  快速操作
                </Space>
              }
              style={{
                background: 'rgba(255,255,255,0.95)',
                backdropFilter: 'blur(10px)',
                borderRadius: '12px',
                boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
              }}
            >
              <Row gutter={[16, 16]}>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    type="primary"
                    icon={<DatabaseOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    数据库管理
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    icon={<UserOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    用户管理
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    icon={<FileTextOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    内容管理
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    icon={<SecurityScanOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    安全设置
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    icon={<MonitorOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    系统监控
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    icon={<SettingOutlined />}
                    block
                    style={{ height: '60px', fontSize: '16px' }}
                  >
                    系统设置
                  </Button>
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );

  return (
    <div ref={fullscreenRef}>
      {render360View()}
    </div>
  );
};

export default FullscreenView;
