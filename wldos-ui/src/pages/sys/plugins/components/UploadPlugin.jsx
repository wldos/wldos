import React, { useState } from 'react';
import { Button, Upload, message, Card, Space, Alert, Divider, Typography, Tooltip } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import { 
  UploadOutlined, 
  FileZipOutlined, 
  InfoCircleOutlined, 
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  CloudUploadOutlined
} from '@ant-design/icons';
import { installPlugin } from '../service';

const UploadPlugin = ({ visible, onCancel, onSuccess }) => {
  const [uploading, setUploading] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);

  // 文件上传处理
  const handleFileUpload = async () => {
    if (!selectedFile) {
      message.error('请选择插件文件');
      return;
    }

    setUploading(true);

    try {
      // 直接调用安装接口，传入文件参数
      const response = await installPlugin({
        file: selectedFile
      });

      // 检查响应状态，后端返回格式为 {status: 200, message: "ok", data: "插件安装成功"}
      if (response?.data === 'ok') {
        message.success('插件安装成功');

        // 关闭上传界面
        handleCancel();

        // 通知父组件刷新列表
        if (onSuccess) {
          onSuccess();
        }
      } else {
        message.error('插件安装失败: ' + (response.message || '未知错误'));
      }
    } catch (error) {
      console.error('安装失败:', error);
      message.error('操作失败: ' + (error.message || '网络错误'));
    } finally {
      setUploading(false);
    }
  };

  const handleCancel = () => {
    setSelectedFile(null);
    setUploading(false);
    onCancel();
  };

  const uploadProps = {
    beforeUpload: (file) => {

      const isZip = file.type === 'application/zip' || file.name.endsWith('.zip');
      if (!isZip) {
        message.error('只能上传 ZIP 文件!');
        return false;
      }
      const isLt50M = file.size / 1024 / 1024 < 50;
      if (!isLt50M) {
        message.error('文件大小不能超过 50MB!');
        return false;
      }
      setSelectedFile(file);
      return false; // 阻止自动上传
    },
    showUploadList: false,
  };

  return (
    <FullscreenModal
      title={
        <Space>
          <CloudUploadOutlined style={{ color: '#1890ff' }} />
          安装插件
        </Space>
      }
      visible={visible}
      onCancel={handleCancel}
      width={600}
      bodyStyle={{
        padding: '24px'
      }}
      footer={[
        <Button key="cancel" onClick={handleCancel}>
          取消
        </Button>,
        <Button
          key="upload"
          type="primary"
          loading={uploading}
          disabled={!selectedFile || uploading}
          onClick={handleFileUpload}
          icon={<UploadOutlined />}
        >
          安装插件
        </Button>
      ]}
    >
      <div>
        {/* 页面说明 */}
        <Alert
          message="插件安装"
          description="上传插件包文件，系统将自动解析并安装插件。请确保插件包格式正确且符合系统要求。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        {/* 文件上传区域 */}
        <Card
          title={
            <Space>
              <FileZipOutlined style={{ color: '#52c41a' }} />
              选择插件包
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="支持ZIP格式的插件包文件">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <Space direction="vertical" style={{ width: '100%' }}>
            <Upload {...uploadProps}>
              <Button 
                icon={<UploadOutlined />} 
                size="large"
                style={{ width: '100%', height: '60px' }}
              >
                点击选择插件包文件
              </Button>
            </Upload>

            {/* 文件要求说明 */}
            <div style={{ 
              padding: '12px', 
              background: '#f6ffed', 
              border: '1px solid #b7eb8f', 
              borderRadius: '6px',
              fontSize: '12px',
              color: '#666'
            }}>
              <Space direction="vertical" size="small" style={{ width: '100%' }}>
                <div>
                  <CheckCircleOutlined style={{ color: '#52c41a', marginRight: '4px' }} />
                  仅支持 ZIP 格式的插件包
                </div>
                <div>
                  <CheckCircleOutlined style={{ color: '#52c41a', marginRight: '4px' }} />
                  文件大小不能超过 50MB
                </div>
                <div>
                  <ExclamationCircleOutlined style={{ color: '#faad14', marginRight: '4px' }} />
                  选择文件后将直接安装插件
                </div>
              </Space>
            </div>
          </Space>
        </Card>

        {/* 已选择文件信息 */}
        {selectedFile && (
          <Card
            title={
              <Space>
                <CheckCircleOutlined style={{ color: '#52c41a' }} />
                已选择文件
              </Space>
            }
            size="small"
            style={{ 
              background: '#f6ffed', 
              border: '1px solid #b7eb8f'
            }}
          >
            <Space direction="vertical" style={{ width: '100%' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <FileZipOutlined style={{ color: '#52c41a' }} />
                <Typography.Text strong style={{ color: '#52c41a' }}>
                  {selectedFile.name}
                </Typography.Text>
              </div>
              
              <Divider style={{ margin: '8px 0' }} />
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: '#666' }}>
                  文件大小: {(() => {
                    if (!selectedFile.size) return '0.00 MB';
                    const sizeInMB = selectedFile.size / 1024 / 1024;
                    return sizeInMB < 1
                      ? `${(selectedFile.size / 1024).toFixed(2)} KB`
                      : `${sizeInMB.toFixed(2)} MB`;
                  })()}
                </span>
                <span style={{ fontSize: '11px', color: '#999' }}>
                  {selectedFile.size?.toLocaleString()} 字节
                </span>
              </div>
            </Space>
          </Card>
        )}
      </div>
    </FullscreenModal>
  );
};

export default UploadPlugin;
