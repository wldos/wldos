import React from 'react';
import { Descriptions, Tag, Space, Badge, Divider, Button } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import { DownloadOutlined, StarOutlined, LikeOutlined, MessageOutlined, CheckCircleOutlined, GithubOutlined, SyncOutlined } from '@ant-design/icons';
import moment from 'moment';

const StoreDetail = ({ visible, onCancel, plugin, onInstall, installed = false, showMainClass = false, showAdminInfo = false }) => {
  if (!plugin) return null;

  const tagList = plugin.tags ? (typeof plugin.tags === 'string' ? JSON.parse(plugin.tags) : plugin.tags) : [];

  const statusMap = {
    'PENDING': { text: '待审核', color: 'orange' },
    'APPROVED': { text: '已通过', color: 'green' },
    'REJECTED': { text: '已拒绝', color: 'red' },
    'OFFLINE': { text: '已下架', color: 'default' },
  };

  const categoryMap = {
    'DEVELOPMENT': '开发工具',
    'SYSTEM': '系统工具',
    'NETWORK': '网络工具',
    'SECURITY': '安全工具',
    'ENTERTAINMENT': '娱乐工具',
    'OTHER': '其他',
  };

  const footer = (
    <Space>
      <Button onClick={onCancel}>关闭</Button>
      {installed ? (
        <Button type="default" icon={<CheckCircleOutlined />} disabled>
          已安装
        </Button>
      ) : (
        <Button 
          type="primary" 
          icon={<DownloadOutlined />} 
          onClick={onInstall}
        >
          安装插件
        </Button>
      )}
    </Space>
  );

  return (
    <FullscreenModal
      title="插件详情"
      visible={visible}
      onCancel={onCancel}
      footer={footer}
      width={800}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <Descriptions column={2} bordered>
        {showAdminInfo && (
          <Descriptions.Item label="插件编码" span={2}>
            {plugin.pluginCode}
          </Descriptions.Item>
        )}

        <Descriptions.Item label="插件名称">
          {plugin.pluginName || plugin.name || plugin.pluginCode}
        </Descriptions.Item>

        <Descriptions.Item label="版本">
          {plugin.version}
        </Descriptions.Item>

        <Descriptions.Item label="作者">
          {plugin.author}
        </Descriptions.Item>

        <Descriptions.Item label="分类">
          <Tag color="blue">{categoryMap[plugin.category] || plugin.category}</Tag>
        </Descriptions.Item>

        <Descriptions.Item label="标签" span={2}>
          <Space size={[0, 4]} wrap>
            {tagList.map((tag, index) => (
              <Tag key={index} size="small">{tag}</Tag>
            ))}
            {tagList.length === 0 && <span style={{ color: '#999' }}>暂无标签</span>}
          </Space>
        </Descriptions.Item>

        {showMainClass && (
          <Descriptions.Item label="主类" span={2}>
            {plugin.mainClass}
          </Descriptions.Item>
        )}

        <Descriptions.Item label="描述" span={2}>
          {plugin.description}
        </Descriptions.Item>

        <Descriptions.Item label="图标">
          {plugin.icon ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <img
                src={plugin.icon}
                alt="插件图标"
                style={{ 
                  width: 48, 
                  height: 48, 
                  objectFit: 'contain',
                  border: '1px solid #d9d9d9',
                  borderRadius: '4px',
                  padding: '4px',
                  backgroundColor: '#fafafa'
                }}
                onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'inline';
                }}
              />
              <span style={{ display: 'none', color: '#999' }}>图标加载失败</span>
            </div>
          ) : (
            <div style={{ 
              width: 48, 
              height: 48, 
              border: '1px dashed #d9d9d9', 
              borderRadius: '4px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              backgroundColor: '#fafafa',
              color: '#999'
            }}>
              暂无图标
            </div>
          )}
        </Descriptions.Item>

        <Descriptions.Item label="自动启动">
          <Badge
            status={plugin.autoStart ? 'success' : 'default'}
            text={plugin.autoStart ? '是' : '否'}
          />
        </Descriptions.Item>
      </Descriptions>

      <Divider orientation="left">统计信息</Divider>

      <Descriptions column={4} bordered>
        <Descriptions.Item label="下载量">
          <Space>
            <DownloadOutlined />
            {plugin.downloadCount || 0}
          </Space>
        </Descriptions.Item>

        <Descriptions.Item label="点赞数">
          <Space>
            <StarOutlined />
            {plugin.starCount || 0}
          </Space>
        </Descriptions.Item>

        <Descriptions.Item label="收藏数">
          <Space>
            <LikeOutlined />
            {plugin.likeCount || 0}
          </Space>
        </Descriptions.Item>

        <Descriptions.Item label="评论数">
          <Space>
            <MessageOutlined />
            {plugin.reviewCount || 0}
          </Space>
        </Descriptions.Item>
      </Descriptions>

      {showAdminInfo && (
        <>
          <Divider orientation="left">审核信息</Divider>

          <Descriptions column={2} bordered>
            <Descriptions.Item label="审核状态">
              <Badge
                status={statusMap[plugin.reviewStatus]?.color || 'default'}
                text={statusMap[plugin.reviewStatus]?.text || '未知'}
              />
            </Descriptions.Item>

            <Descriptions.Item label="审核人">
              {plugin.reviewBy || '-'}
            </Descriptions.Item>

            <Descriptions.Item label="审核时间">
              {plugin.reviewTime ? moment(plugin.reviewTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>

            <Descriptions.Item label="更新时间">
              {plugin.updateTime ? moment(plugin.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>

            <Descriptions.Item label="审核信息" span={2}>
              {plugin.reviewMessage || '-'}
            </Descriptions.Item>
          </Descriptions>
        </>
      )}

      {/* Git 仓库信息（仅当安装来源为 GITHUB 或 GITEE 时显示） */}
      {(plugin.installSource === 'GITHUB' || plugin.installSource === 'GITEE') && (
        <>
          <Divider orientation="left">
            <Space>
              <GithubOutlined />
              <span>Git 仓库信息</span>
            </Space>
          </Divider>

          <Descriptions column={2} bordered>
            <Descriptions.Item label="安装来源">
              <Tag color={plugin.installSource === 'GITHUB' ? 'blue' : 'green'}>
                {plugin.installSource === 'GITHUB' ? 'GitHub' : 'Gitee'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="自动更新">
              <Badge
                status={plugin.autoUpdate ? 'success' : 'default'}
                text={plugin.autoUpdate ? '是' : '否'}
              />
            </Descriptions.Item>

            <Descriptions.Item label="Git 分支">
              {plugin.repoBranch || '-'}
            </Descriptions.Item>

            <Descriptions.Item label="Git 提交">
              {plugin.repoCommit ? (
                <span style={{ fontFamily: 'monospace', fontSize: '12px' }}>
                  {plugin.repoCommit.substring(0, 8)}...
                </span>
              ) : (
                '-'
              )}
            </Descriptions.Item>

            <Descriptions.Item label="Git 最后更新时间" span={2}>
              {plugin.repoLastUpdate ? (
                moment(plugin.repoLastUpdate).format('YYYY-MM-DD HH:mm:ss')
              ) : (
                '-'
              )}
            </Descriptions.Item>

            <Descriptions.Item label="仓库地址" span={2}>
              {plugin.downloadUrl || plugin.pluginUrl ? (
                <a 
                  href={plugin.downloadUrl || plugin.pluginUrl} 
                  target="_blank" 
                  rel="noopener noreferrer"
                >
                  {plugin.downloadUrl || plugin.pluginUrl}
                </a>
              ) : (
                <span style={{ color: '#999' }}>暂无仓库地址</span>
              )}
            </Descriptions.Item>
          </Descriptions>
        </>
      )}

      {showAdminInfo && (
        <>
          <Divider orientation="left">其他信息</Divider>

          <Descriptions column={2} bordered>
            <Descriptions.Item label="下载地址">
              {plugin.downloadUrl ? (
                <a href={plugin.downloadUrl} target="_blank" rel="noopener noreferrer">
                  {plugin.downloadUrl}
                </a>
              ) : (
                <span style={{ color: '#999' }}>暂无下载地址</span>
              )}
            </Descriptions.Item>

            <Descriptions.Item label="安装来源">
              {plugin.installSource ? (
                <Tag color={
                  plugin.installSource === 'UPLOAD' ? 'default' :
                  plugin.installSource === 'GITHUB' ? 'blue' : 'green'
                }>
                  {plugin.installSource === 'UPLOAD' ? '上传' :
                   plugin.installSource === 'GITHUB' ? 'GitHub' : 'Gitee'}
                </Tag>
              ) : (
                <Tag color="default">上传</Tag>
              )}
            </Descriptions.Item>

            <Descriptions.Item label="创建时间">
              {plugin.createTime ? moment(plugin.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>

            <Descriptions.Item label="更新时间">
              {plugin.updateTime ? moment(plugin.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>

            <Descriptions.Item label="创建人">
              {plugin.createBy}
            </Descriptions.Item>

            <Descriptions.Item label="更新人">
              {plugin.updateBy}
            </Descriptions.Item>
          </Descriptions>
        </>
      )}
    </FullscreenModal>
  );
};

export default StoreDetail;
