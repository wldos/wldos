import React, {useEffect, useState} from 'react';
import {
  Card,
  Row,
  Col,
  Typography,
  Space,
  Tag,
  Button,
  Divider,
  Alert,
  Progress,
  Statistic,
  Timeline,
  Tooltip,
  Badge,
} from 'antd';
import {
  InfoCircleOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  CloudOutlined,
  GithubOutlined,
  QuestionCircleOutlined,
  ReloadOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import styles from './style.less';
import {queryIssueVersion, queryLicense} from "@/pages/sys/reg/service";

const { Title, Text, Paragraph } = Typography;

const License = () => {
  const [licInfo, setLicInfo] = useState({orgName: '', prodName: '', edition: '', version: ''});
  const [licEnum, setLicEnum] = useState([]);
  const [systemInfo, setSystemInfo] = useState({
    uptime: '7天12小时',
    memoryUsage: 65,
    diskUsage: 42,
    lastUpdate: '2025-01-15',
    status: 'running'
  });

  useEffect(async () => {
    const res = await queryLicense();
    if (res?.data) {
      setLicInfo(res.data);
    }
  }, []);

  useEffect(async () => {
    const res = await queryIssueVersion();
    if (res?.data) {

      setLicEnum(res.data);
    }
  }, []);

  const editionLabel = (en, e) => {
    const curLabel = en.map(item => {
      if (item.value === e) {
        return item.label;
      }
      return '';
    });
    return curLabel?? '';
  };

  const getEditionColor = (edition) => {
    const colorMap = {
      'Free': 'default',
      'Community': 'blue',
      'Professional': 'green',
      'Enterprise': 'purple',
    };
    return colorMap[edition] || 'default';
  };

  const getStatusColor = (status) => {
    return status === 'running' ? 'success' : 'error';
  };

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      {/* 页面标题 */}
      <div style={{ marginBottom: '24px' }}>
        <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
          <Space>
            <InfoCircleOutlined />
            版本信息
          </Space>
        </Title>
        <Text type="secondary">系统版本详情和运行状态</Text>
      </div>

      <Row gutter={[24, 24]}>
        {/* 产品信息卡片 */}
        <Col xs={24} lg={12}>
          <Card 
            title={
              <Space>
                <CloudOutlined style={{ color: '#1890ff' }} />
                产品信息
              </Space>
            }
            extra={
              <Tag color={getEditionColor(licInfo.edition)}>
                {editionLabel(licEnum, licInfo.edition)}
              </Tag>
            }
          >
            <Space direction="vertical" size="large" style={{ width: '100%' }}>
              <div>
                <Text strong>产品名称</Text>
                <div style={{ marginTop: '8px' }}>
                  <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
                    {licInfo.prodName || 'WLDOS云物互联应用支撑平台'}
                  </Title>
                </div>
              </div>
              
              <div>
                <Text strong>授权组织</Text>
                <div style={{ marginTop: '8px' }}>
                  <Text>{licInfo.orgName || '开源用户'}</Text>
                </div>
              </div>
              
              <div>
                <Text strong>版本号</Text>
                <div style={{ marginTop: '8px' }}>
                  <Tag color="blue" style={{ fontSize: '16px', padding: '4px 12px' }}>
                    {licInfo.version || 'V2.0.1'}
                  </Tag>
                </div>
              </div>
              
              {licInfo.edition && licInfo.edition !== 'Free' && (
                <div>
                  <Text strong>到期时间</Text>
                  <div style={{ marginTop: '8px' }}>
                    <Text type="warning">{licInfo.expiryTime}</Text>
                  </div>
                </div>
              )}
            </Space>
          </Card>
        </Col>

        {/* 系统状态卡片 */}
        <Col xs={24} lg={12}>
          <Card 
            title={
              <Space>
                <CheckCircleOutlined style={{ color: '#52c41a' }} />
                系统状态
              </Space>
            }
            extra={
              <Badge 
                status={getStatusColor(systemInfo.status)} 
                text={systemInfo.status === 'running' ? '运行中' : '异常'}
              />
            }
          >
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Statistic
                  title="运行时间"
                  value={systemInfo.uptime}
                  prefix={<ClockCircleOutlined />}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="最后更新"
                  value={systemInfo.lastUpdate}
                  prefix={<DownloadOutlined />}
                />
              </Col>
              <Col span={24}>
                <div style={{ marginTop: '16px' }}>
                  <Text strong>内存使用率</Text>
                  <Progress 
                    percent={systemInfo.memoryUsage} 
                    status={systemInfo.memoryUsage > 80 ? 'exception' : 'active'}
                    style={{ marginTop: '8px' }}
                  />
                </div>
              </Col>
              <Col span={24}>
                <div style={{ marginTop: '16px' }}>
                  <Text strong>磁盘使用率</Text>
                  <Progress 
                    percent={systemInfo.diskUsage} 
                    status={systemInfo.diskUsage > 90 ? 'exception' : 'active'}
                    style={{ marginTop: '8px' }}
                  />
                </div>
              </Col>
            </Row>
          </Card>
        </Col>

        {/* 版本历史卡片 */}
        <Col xs={24} lg={12}>
          <Card 
            title={
              <Space>
                <DatabaseOutlined style={{ color: '#fa8c16' }} />
                版本历史
              </Space>
            }
          >
            <Timeline>
              <Timeline.Item color="green">
                <Text strong>V2.0.0</Text>
                <div style={{ marginTop: '4px' }}>
                  <Text type="secondary">2025-01-15</Text>
                  <div>重大版本更新，新增多租户支持</div>
                </div>
              </Timeline.Item>
              <Timeline.Item color="blue">
                <Text strong>V1.9.1</Text>
                <div style={{ marginTop: '4px' }}>
                  <Text type="secondary">2024-12-20</Text>
                  <div>性能优化和bug修复</div>
                </div>
              </Timeline.Item>
              <Timeline.Item color="gray">
                <Text strong>V1.9.0</Text>
                <div style={{ marginTop: '4px' }}>
                  <Text type="secondary">2024-11-15</Text>
                  <div>新增插件系统</div>
                </div>
              </Timeline.Item>
            </Timeline>
          </Card>
        </Col>

        {/* 操作和链接卡片 */}
        <Col xs={24} lg={12}>
          <Card 
            title={
              <Space>
                <QuestionCircleOutlined style={{ color: '#722ed1' }} />
                帮助与支持
              </Space>
            }
          >
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              <div>
                <Text strong>快速操作</Text>
                <div style={{ marginTop: '12px' }}>
                  <Space wrap>
                    <Button type="primary" icon={<ReloadOutlined />}>
                      检查更新
                    </Button>
                    <Button icon={<DownloadOutlined />}>
                      下载更新
                    </Button>
                    <Button icon={<InfoCircleOutlined />}>
                      系统诊断
                    </Button>
                  </Space>
                </div>
              </div>
              
              <Divider />
              
              <div>
                <Text strong>相关链接</Text>
                <div style={{ marginTop: '12px' }}>
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Button 
                      type="link" 
                      icon={<GithubOutlined />}
                      href="https://github.com/wldos/wldos"
                      target="_blank"
                      style={{ padding: 0, height: 'auto' }}
                    >
                      GitHub 开源社区
                    </Button>
                    <Button 
                      type="link" 
                      icon={<CloudOutlined />}
                      href="https://gitee.com/wldos/wldos"
                      target="_blank"
                      style={{ padding: 0, height: 'auto' }}
                    >
                      Gitee 开源社区
                    </Button>
                    <Button 
                      type="link" 
                      icon={<QuestionCircleOutlined />}
                      href="https://gitee.com/wldos/wldos/wikis/关于我们"
                      target="_blank"
                      style={{ padding: 0, height: 'auto' }}
                    >
                      关于我们
                    </Button>
                  </Space>
                </div>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>

      {/* 底部信息 */}
      <div style={{ marginTop: '24px', textAlign: 'center' }}>
        <Alert
          message="WLDOS 云管端解决方案"
          description={
            <div>
              <Text type="secondary">
                © 2025 WLDOS 云管端解决方案 {licInfo.version || 'V2.0.1'} | 
                开源社区 | 
                <a href="https://github.com/wldos/wldos" target="_blank" rel="noopener noreferrer">
                  GitHub
                </a> | 
                <a href="https://gitee.com/wldos/wldos" target="_blank" rel="noopener noreferrer">
                  Gitee
                </a>
              </Text>
            </div>
          }
          type="info"
          showIcon
          style={{ background: '#f6ffed', border: '1px solid #b7eb8f' }}
        />
      </div>
    </div>
  );
};

export default License;
