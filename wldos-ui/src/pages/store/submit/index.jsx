import React, { useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
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
  Progress,
} from 'antd';
import {
  UploadOutlined,
  FileZipOutlined,
  CheckCircleOutlined,
  InboxOutlined,
  InfoCircleOutlined,
  CloudUploadOutlined,
} from '@ant-design/icons';
import { FormattedMessage } from 'umi';
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

  const uploadProps = {
    name: 'file',
    multiple: false,
    accept: '.zip',
    fileList,
    beforeUpload: (file) => {
      const isZip = file.type === 'application/zip' || file.name.endsWith('.zip');
      if (!isZip) {
        message.error(
          <FormattedMessage
            id="store.submit.upload.onlyZip"
            defaultMessage="Only ZIP plugin packages are allowed."
          />,
        );
        return false;
      }
      const isLt100M = file.size / 1024 / 1024 < 100;
      if (!isLt100M) {
        message.error(
          <FormattedMessage
            id="store.submit.upload.maxSize"
            defaultMessage="Plugin package size must not exceed 100MB."
          />,
        );
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

  const handleUpload = async () => {
    if (fileList.length === 0) {
      message.error(
        <FormattedMessage
          id="store.submit.upload.selectFileFirst"
          defaultMessage="Please select a plugin package file first."
        />,
      );
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

      message.success(
        <FormattedMessage
          id="store.submit.upload.success"
          defaultMessage="Plugin submitted successfully! Waiting for review..."
        />,
      );

      // 添加到提交历史
      const newHistory = {
        id: Date.now(),
        fileName: file.name,
        fileSize: `${(file.size / 1024 / 1024).toFixed(2)} MB`,
        submitTime: new Date().toISOString(),
        status: 'pending',
      };
      setSubmitHistory([newHistory, ...submitHistory]);
      
      // 清空文件列表
      setFileList([]);
      setUploadProgress(0);
    } catch (error) {
      console.error('upload plugin failed:', error);
      message.error(
        `${window.g_app?._store?.getState()?.intl?.messages?.['store.submit.upload.failPrefix'] ||
          'Plugin submission failed: '}${
          error && error.message
            ? error.message
            : window.g_app?._store?.getState()?.intl?.messages?.['store.submit.upload.unknownError'] ||
              'Unknown error'
        }`,
      );
      setUploadProgress(0);
    } finally {
      setUploading(false);
    }
  };

  const getStatusTag = (status) => {
    const statusMap = {
      pending: {
        color: 'orange',
        text: (
          <FormattedMessage
            id="store.submit.status.pending"
            defaultMessage="Pending"
          />
        ),
      },
      approved: {
        color: 'green',
        text: (
          <FormattedMessage
            id="store.submit.status.approved"
            defaultMessage="Approved"
          />
        ),
      },
      rejected: {
        color: 'red',
        text: (
          <FormattedMessage
            id="store.submit.status.rejected"
            defaultMessage="Rejected"
          />
        ),
      },
    };
    const statusInfo =
      statusMap[status] || {
        color: 'default',
        text: (
          <FormattedMessage
            id="store.submit.status.unknown"
            defaultMessage="Unknown"
          />
        ),
      };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  return (
    <PageContainer
      title={
        <FormattedMessage
          id="store.submit.title"
          defaultMessage="Submit Plugin"
        />
      }
      content={
        <FormattedMessage
          id="store.submit.content"
          defaultMessage="Submit your plugin to the WLDOS plugin marketplace. Once approved, it will be available for other users to install."
        />
      }
    >
      <div className="plugin-submit-container">
        <Card
          title={
            <Space>
              <InfoCircleOutlined />
            <span>
              <FormattedMessage
                id="store.submit.steps.title"
                defaultMessage="Submission process"
              />
            </span>
            </Space>
          }
          style={{ marginBottom: 24 }}
        >
          <Steps current={fileList.length > 0 ? 1 : 0} size="small">
            <Step
              title={
                <FormattedMessage
                  id="store.submit.steps.step1.title"
                  defaultMessage="Select plugin package"
                />
              }
              description={
                <FormattedMessage
                  id="store.submit.steps.step1.desc"
                  defaultMessage="Upload a compliant ZIP plugin package."
                />
              }
              icon={<FileZipOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.submit.steps.step2.title"
                  defaultMessage="Submit for review"
                />
              }
              description={
                <FormattedMessage
                  id="store.submit.steps.step2.desc"
                  defaultMessage="The system will automatically parse plugin info and submit it for review."
                />
              }
              icon={<CloudUploadOutlined />}
            />
            <Step
              title={
                <FormattedMessage
                  id="store.submit.steps.step3.title"
                  defaultMessage="Wait for review"
                />
              }
              description={
                <FormattedMessage
                  id="store.submit.steps.step3.desc"
                  defaultMessage="After administrator approval, the plugin will be listed in the marketplace."
                />
              }
              icon={<CheckCircleOutlined />}
            />
          </Steps>
        </Card>

        <Card
          title={
            <FormattedMessage
              id="store.submit.card.upload.title"
              defaultMessage="Upload plugin package"
            />
          }
          extra={
            <Button
              type="primary"
              icon={<UploadOutlined />}
              onClick={handleUpload}
              loading={uploading}
              disabled={fileList.length === 0}
            >
              <FormattedMessage
                id="store.submit.card.upload.button"
                defaultMessage="Submit for review"
              />
            </Button>
          }
          style={{ marginBottom: 24 }}
        >
          <Dragger {...uploadProps} disabled={uploading}>
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">
              <FormattedMessage
                id="store.submit.card.upload.dragText"
                defaultMessage="Click or drag plugin package file to this area to upload"
              />
            </p>
            <p className="ant-upload-hint">
              <FormattedMessage
                id="store.submit.card.upload.hint"
                defaultMessage="ZIP format only, file size must not exceed 100MB."
              />
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

        <Card
          title={
            <FormattedMessage
              id="store.submit.notice.title"
              defaultMessage="Submission notes"
            />
          }
          style={{ marginBottom: 24 }}
        >
          <Alert
            message={
              <FormattedMessage
                id="store.submit.notice.specTitle"
                defaultMessage="Plugin submission guidelines"
              />
            }
            description={
              <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item1"
                    defaultMessage="The plugin package must be in ZIP format and contain the complete plugin file structure."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item2"
                    defaultMessage="The plugin package must contain a valid configuration file {file}."
                    values={{
                      file: <Text code>plugin.yml</Text>,
                    }}
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item3"
                    defaultMessage="Plugin code must comply with the WLDOS plugin development guidelines."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item4"
                    defaultMessage="Plugins must not contain malicious code or content that violates laws or regulations."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item5"
                    defaultMessage="After submission, the plugin will enter the review process, and will be listed in the marketplace only after approval."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.spec.item6"
                    defaultMessage="The review time is usually 1-3 working days."
                  />
                </li>
              </ul>
            }
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />
          <Alert
            message={
              <FormattedMessage
                id="store.submit.notice.reviewTitle"
                defaultMessage="Review standards"
              />
            }
            description={
              <ul style={{ marginBottom: 0, paddingLeft: 20 }}>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.review.item1"
                    defaultMessage="Plugin functionality is complete and works properly."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.review.item2"
                    defaultMessage="Plugin description is accurate and not misleading."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.review.item3"
                    defaultMessage="Plugin code quality is good and has no obvious security vulnerabilities."
                  />
                </li>
                <li>
                  <FormattedMessage
                    id="store.submit.notice.review.item4"
                    defaultMessage="Plugin complies with WLDOS platform specifications and policies."
                  />
                </li>
              </ul>
            }
            type="warning"
            showIcon
          />
        </Card>

        {submitHistory.length > 0 && (
          <Card
            title={
              <FormattedMessage
                id="store.submit.history.title"
                defaultMessage="My submission records"
              />
            }
          >
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
                        <Text type="secondary">
                          <FormattedMessage
                            id="store.submit.history.size"
                            defaultMessage="Size: {size}"
                            values={{ size: item.fileSize }}
                          />
                        </Text>
                        <Text type="secondary">
                          <FormattedMessage
                            id="store.submit.history.time"
                            defaultMessage="Submitted at: {time}"
                            values={{ time: item.submitTime }}
                          />
                        </Text>
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

