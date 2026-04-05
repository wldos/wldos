import React, { useState, useEffect } from 'react';
import { Card, Typography, Space, Button, Upload, Modal, message } from 'antd';
import { CloudOutlined, UploadOutlined } from '@ant-design/icons';
import { Link } from 'umi';
import { getServerInfo, uploadLicense } from './service';

const { Title, Text, Paragraph } = Typography;

/**
 * 用户实例许可证页：与业界一致——购买与发放在官网/客户门户完成，本页仅提供本机机器码（供官网绑定）与安装已获得的 license。
 */
export default function LicenseApplyPage() {
  const [serverInfo, setServerInfo] = useState(null);
  const [serverInfoLoading, setServerInfoLoading] = useState(false);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    fetchServerInfo();
  }, []);

  useEffect(() => {
    document.title = '许可证 - 管理后台';
  }, []);

  const fetchServerInfo = async () => {
    setServerInfoLoading(true);
    try {
      const res = await getServerInfo();
      const d = res?.data?.data != null ? res.data.data : res?.data;
      if (d) setServerInfo(d);
      else message.error(res?.message || '获取本机信息失败');
    } catch (e) {
      message.error(e?.data?.message || e?.message || '获取本机信息失败');
    } finally {
      setServerInfoLoading(false);
    }
  };

  const handleUploadLicense = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    setUploading(true);
    try {
      const res = await uploadLicense(formData);
      if (res?.code === 200) {
        message.success('License 已上传并生效');
        setUploadModalVisible(false);
      } else {
        message.error(res?.message || '上传失败');
      }
    } catch (e) {
      message.error(e?.data?.message || e?.message || '上传失败');
    } finally {
      setUploading(false);
    }
  };

  /** 官网购买许可入口（可配置：defaultSettings 或 REACT_APP_LICENSE_PURCHASE_URL） */
  const licensePurchaseUrl = typeof window !== 'undefined' && window.g_defaultSettings?.licensePurchaseUrl
    ? window.g_defaultSettings.licensePurchaseUrl
    : (process.env.REACT_APP_LICENSE_PURCHASE_URL || '');

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: 24 }}>
        <Space align="center">
          <Link to="/admin/sys/reg">← 版本信息</Link>
          <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
            <CloudOutlined /> 许可证
          </Title>
        </Space>
        <Text type="secondary">
          {licensePurchaseUrl ? (
            <>购买、绑定本机与获取 license 均在<a href={licensePurchaseUrl} target="_blank" rel="noopener noreferrer">官网</a>完成；本页仅用于查看本机机器码并安装已获得的 license 文件。</>
          ) : (
            '购买、绑定本机与获取 license 均在官网完成；本页仅用于查看本机机器码并安装已获得的 license 文件。'
          )}
        </Text>
      </div>

      <Card title={<Space><CloudOutlined style={{ color: '#1890ff' }} />许可证</Space>}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <div>
            <Text strong>机器码（唯一申请串）</Text>
            <Text type="secondary" style={{ fontSize: 11 }}>
              申请许可证时仅需提交此机器码。
            </Text>
            {!serverInfoLoading && serverInfo?.machineCode ? (
              <div style={{ marginTop: 12 }}>
                <Paragraph
                  copyable={{ text: serverInfo.machineCode }}
                  style={{ marginBottom: 4, marginTop: 8, fontFamily: 'monospace', fontSize: 12, wordBreak: 'break-all' }}
                >
                  {serverInfo.machineCode}
                </Paragraph>
              </div>
            ) : <div style={{ marginTop: 8, fontSize: 12, color: '#666' }}>{serverInfoLoading ? '正在获取机器码…' : '-'}</div>}
          </div>
          <div>
            <Text strong>证书与许可证（覆盖本实例）</Text>
            <div style={{ marginTop: 8 }}>
              <Button type="primary" icon={<UploadOutlined />} onClick={() => setUploadModalVisible(true)}>上传 License</Button>
            </div>
          </div>
        </Space>
      </Card>

      <Modal title="上传 License" open={uploadModalVisible} onCancel={() => setUploadModalVisible(false)} footer={null} destroyOnClose>
        <Upload accept=".lic" maxCount={1} beforeUpload={(file) => { handleUploadLicense(file); return false; }} showUploadList={!uploading}>
          <Button icon={<UploadOutlined />} loading={uploading}>选择许可证文件</Button>
        </Upload>
        <p style={{ marginTop: 12, color: '#666', fontSize: 12 }}>上传后替换本实例许可证文件并生效。</p>
      </Modal>
    </div>
  );
}
