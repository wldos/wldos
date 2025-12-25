import React from 'react';
import { Descriptions, Tag, Space, Badge, Divider, Button, Card, Alert, Typography, Tooltip } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import moment from 'moment';
import { 
  InfoCircleOutlined, 
  ReloadOutlined, 
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  UserOutlined,
  CodeOutlined,
  FileTextOutlined,
  SettingOutlined,
  DatabaseOutlined
} from '@ant-design/icons';

const PluginDetail = ({ visible, onCancel, plugin }) => {
  if (!plugin) return null;

  const statusMap = {
    'INSTALLED': { text: '已安装', color: 'blue' },
    'ENABLED': { text: '已启用', color: 'green' },
    'DISABLED': { text: '已禁用', color: 'orange' },
    'ERROR': { text: '错误', color: 'red' },
  };

  const metadata = plugin.metadata ? (typeof plugin.metadata === 'string' ? JSON.parse(plugin.metadata) : plugin.metadata) : {};

  return (
    <FullscreenModal
      title={
        <Space>
          <InfoCircleOutlined style={{ color: '#1890ff' }} />
          插件详情 - {plugin.pluginName}
        </Space>
      }
      visible={visible}
      onCancel={onCancel}
      footer={[
        <Button key="back" onClick={onCancel}>
          关闭
        </Button>,
        <Button key="reload" icon={<ReloadOutlined />}>
          重新加载
        </Button>,
      ]}
      width={900}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <div>
        {/* 页面说明 */}
        <Alert
          message="插件信息"
          description={`查看插件 "${plugin.pluginName}" 的详细信息和运行状态。`}
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <FileTextOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="插件的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Descriptions column={2} bordered>
            <Descriptions.Item label="插件名称">
              <Space>
                <FileTextOutlined />
                {plugin.pluginName}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="版本">
              <Space>
                <CodeOutlined />
                {plugin.version}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="作者">
              <Space>
                <UserOutlined />
                {plugin.author}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="状态">
              <Badge
                status={statusMap[plugin.pluginStatus]?.color || 'default'}
                text={statusMap[plugin.pluginStatus]?.text || '未知'}
              />
            </Descriptions.Item>

            <Descriptions.Item label="描述" span={2}>
              <Typography.Paragraph style={{ margin: 0 }}>
                {plugin.description}
              </Typography.Paragraph>
            </Descriptions.Item>

            <Descriptions.Item label="自动启动">
              <Badge
                status={plugin.autoStart === 'true' ? 'success' : 'default'}
                text={plugin.autoStart === 'true' ? '是' : '否'}
              />
            </Descriptions.Item>

            <Descriptions.Item label="依赖">
              {plugin.depends ? (
                <Space size={[0, 4]} wrap>
                  {plugin.depends.split(',').map((dep, index) => (
                    <Tag key={index} size="small" color="blue">{dep.trim()}</Tag>
                  ))}
                </Space>
              ) : (
                <span style={{ color: '#999' }}>无依赖</span>
              )}
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* 错误信息 */}
        {plugin.errorMessage && (
          <Card
            title={
              <Space>
                <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />
                错误信息
              </Space>
            }
            size="small"
            style={{ 
              marginBottom: '16px',
              background: '#fff2f0',
              border: '1px solid #ffccc7'
            }}
          >
            <Alert
              message="插件初始化失败"
              description={plugin.errorMessage}
              type="error"
              showIcon
            />
          </Card>
        )}

        {/* 元数据 */}
        <Card
          title={
            <Space>
              <DatabaseOutlined style={{ color: '#52c41a' }} />
              元数据
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="插件的元数据信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          {Object.keys(metadata).length > 0 ? (
            <Descriptions column={2} bordered>
              {Object.entries(metadata).map(([key, value]) => (
                <Descriptions.Item key={key} label={key}>
                  <Typography.Text code>
                    {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                  </Typography.Text>
                </Descriptions.Item>
              ))}
            </Descriptions>
          ) : (
            <div style={{ 
              textAlign: 'center', 
              padding: '20px', 
              color: '#999',
              background: '#fafafa',
              borderRadius: '6px'
            }}>
              <DatabaseOutlined style={{ fontSize: '24px', marginBottom: '8px' }} />
              <div>暂无元数据</div>
            </div>
          )}
        </Card>

        {/* 其他信息 */}
        <Card
          title={
            <Space>
              <ClockCircleOutlined style={{ color: '#722ed1' }} />
              其他信息
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="插件的创建和更新信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Descriptions column={2} bordered>
            <Descriptions.Item label="创建时间">
              <Space>
                <ClockCircleOutlined />
                {plugin.createTime ? moment(plugin.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="更新时间">
              <Space>
                <ClockCircleOutlined />
                {plugin.updateTime ? moment(plugin.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="创建人">
              <Space>
                <UserOutlined />
                {plugin.createBy}
              </Space>
            </Descriptions.Item>

            <Descriptions.Item label="更新人">
              <Space>
                <UserOutlined />
                {plugin.updateBy}
              </Space>
            </Descriptions.Item>
          </Descriptions>
        </Card>
      </div>
    </FullscreenModal>
  );
};

export default PluginDetail;
