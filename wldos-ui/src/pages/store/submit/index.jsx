import React, { useState } from 'react';
import { 
  PageContainer 
} from '@ant-design/pro-layout';
import { 
  Card, 
  Upload, 
  Button, 
  message, 
  Steps, 
  Alert, 
  Typography, 
  Space,
  Divider,
  List,
  Tag,
  Progress
} from 'antd';
import { 
  UploadOutlined, 
  FileZipOutlined, 
  CheckCircleOutlined,
  InboxOutlined,
  InfoCircleOutlined,
  CloudUploadOutlined
} from '@ant-design/icons';
import { uploadPluginFile } from '@/pages/sys/store/service';
import './index.less';

const { Dragger } = Upload;
const { Title, Paragraph, Text } = Typography;
const { Step } = Steps;

const PluginSubmit = () => {
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [fileList, setFileList] = useState([]);
  const [submitHistory, setSubmitHistory] = useState([]);

  // 上传配置
  const uploadProps = {
    name: 'file',
    multiple: false,
    accept: '.zip',
    fileList,
    beforeUpload: (file) => {
      const isZip = file.type === 'application/zip' || file.name.endsWith('.zip');
      if (!isZip) {
        message.error('只能上传 ZIP 格式的插件包！');
        return false;
      }
      const isLt100M = file.size / 1024 / 1024 < 100;
      if (!isLt100M) {
        message.error('插件包大小不能超过 100MB！');
        return false;
      }
      setFileList([file]);
      return false; // 阻止自动上传
    },
    onRemove: () => {
      setFileList([]);
      return true;
    },
  };

  // 处理上传
  const handleUpload = async () => {
    if (fileList.length === 0) {
      message.error('请先选择插件包文件');
      return;
    }

    const file = fileList[0];
    setUploading(true);
    setUploadProgress(0);

    try {
      // 模拟上传进度
      const progressInterval = setInterval(() => {
        setUploadProgress((prev) => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 200);

      await uploadPluginFile(file);
      
      clearInterval(progressInterval);
      setUploadProgress(100);
      
      message.success('插件提交成功！等待审核中...');
      
      // 添加到提交历史
      const newHistory = {
        id: Date.now(),
        fileName: file.name,
        fileSize: (file.size / 1024 / 1024).toFixed(2) + ' MB',
        submitTime: new Date().toLocaleString('zh-CN'),
        status: 'pending',
        statusText: '待审核'
      };
      setSubmitHistory([newHistory, ...submitHistory]);
      
      // 清空文件列表
      setFileList([]);
      setUploadProgress(0);
    } catch (error) {
      console.error('上传失败:', error);
      message.error('插件提交失败: ' + (error.message || '未知错误'));
      setUploadProgress(0);
    } finally {
      setUploading(false);
    }
  };

  // 获取状态标签
  const getStatusTag = (status) => {
    const statusMap = {
      pending: { color: 'orange', text: '待审核' },
      approved: { color: 'green', text: '已通过' },
      rejected: { color: 'red', text: '已拒绝' },
    };
    const statusInfo = statusMap[status] || { color: 'default', text: '未知' };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  return (
    <PageContainer
      title="提交插件"
      content="将您的插件提交到 WLDOS 插件市场，审核通过后即可供其他用户安装使用"
    >
      <div className="plugin-submit-container">
        {/* 提交步骤说明 */}
        <Card 
          title={
            <Space>
              <InfoCircleOutlined />
              <span>提交流程</span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Steps current={fileList.length > 0 ? 1 : 0} size="small">
            <Step 
              title="选择插件包" 
              description="上传符合规范的 ZIP 格式插件包"
              icon={<FileZipOutlined />}
            />
            <Step 
              title="提交审核" 
              description="系统将自动解析插件信息并提交审核"
              icon={<CloudUploadOutlined />}
            />
            <Step 
              title="等待审核" 
              description="管理员审核通过后，插件将上架到市场"
              icon={<CheckCircleOutlined />}
            />
          </Steps>
        </Card>

        {/* 上传区域 */}
        <Card 
          title="上传插件包"
          extra={
            <Button
              type="primary"
              icon={<UploadOutlined />}
              onClick={handleUpload}
              loading={uploading}
              disabled={fileList.length === 0}
            >
              提交审核
            </Button>
          }
          style={{ marginBottom: 24 }}
        >
          <Dragger {...uploadProps} disabled={uploading}>
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">点击或拖拽插件包文件到此区域上传</p>
            <p className="ant-upload-hint">
              支持 ZIP 格式，文件大小不超过 100MB
            </p>
          </Dragger>

          {uploading && (
            <div style={{ marginTop: 16 }}>
              <Progress 
                percent={uploadProgress} 
                status={uploadProgress === 100 ? 'success' : 'active'}
                strokeColor={{
                  '0%': '#108ee9',
                  '100%': '#87d068',
                }}
              />
            </div>
          )}
        </Card>

        {/* 提交须知 */}
        <Card 
          title="提交须知"
          style={{ marginBottom: 24 }}
        >
          <Alert
            message="插件提交规范"
            description={
              <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
                <li>插件包必须是 ZIP 格式，包含完整的插件文件结构</li>
                <li>插件包必须包含有效的 <Text code>plugin.yml</Text> 配置文件</li>
                <li>插件代码必须符合 WLDOS 插件开发规范</li>
                <li>插件不得包含恶意代码或违反法律法规的内容</li>
                <li>提交后，插件将进入审核流程，审核通过后才会在市场上架</li>
                <li>审核时间通常为 1-3 个工作日</li>
              </ul>
            }
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />
          <Alert
            message="审核标准"
            description={
              <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
                <li>插件功能完整，能够正常运行</li>
                <li>插件描述准确，无虚假宣传</li>
                <li>插件代码质量良好，无明显安全漏洞</li>
                <li>插件符合 WLDOS 平台规范和政策要求</li>
              </ul>
            }
            type="warning"
            showIcon
          />
        </Card>

        {/* 提交历史 */}
        {submitHistory.length > 0 && (
          <Card title="我的提交记录">
            <List
              dataSource={submitHistory}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<FileZipOutlined style={{ fontSize: 24 }} />}
                    title={
                      <Space>
                        <Text strong>{item.fileName}</Text>
                        {getStatusTag(item.status)}
                      </Space>
                    }
                    description={
                      <Space split={<Divider type="vertical" />}>
                        <Text type="secondary">大小: {item.fileSize}</Text>
                        <Text type="secondary">提交时间: {item.submitTime}</Text>
                      </Space>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        )}
      </div>
    </PageContainer>
  );
};

export default PluginSubmit;

