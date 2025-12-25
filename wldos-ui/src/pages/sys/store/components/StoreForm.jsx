import React, { useEffect, useState } from 'react';
import { Form, Input, Select, Switch, Upload, Button, message, Card, Space, Tooltip, Alert, Row, Col, Divider, TreeSelect } from 'antd';
import { 
  UploadOutlined, 
  PlusOutlined, 
  EditOutlined, 
  FileTextOutlined, 
  UserOutlined, 
  SettingOutlined, 
  LinkOutlined, 
  StarOutlined, 
  LikeOutlined, 
  CommentOutlined, 
  CheckCircleOutlined, 
  InfoCircleOutlined,
  CodeOutlined,
  TagOutlined,
  PictureOutlined,
  GlobalOutlined,
  ToolOutlined,
  GithubOutlined
} from '@ant-design/icons';
import { connect } from 'umi';
import FullscreenModal from '@/components/FullscreenModal';
import { queryPluginCategory } from '@/pages/sys/category/service';
import config from '@/utils/config';
import { headerFix } from '@/utils/utils';
import './StoreForm.less';

const { TextArea } = Input;
const { Option } = Select;
const { SHOW_PARENT } = TreeSelect;
const { prefix } = config;

const StoreForm = ({ visible, onCancel, onSubmit, initialValues, dispatch }) => {
  const [form] = Form.useForm();
  const [categoryTree, setCategoryTree] = useState([]);
  const [loading, setLoading] = useState(false);

  // 加载插件分类树
  useEffect(() => {
    const loadCategoryTree = async () => {
      setLoading(true);
      try {
        const res = await queryPluginCategory();
        if (res?.data) {
          setCategoryTree(res.data);
        }
      } catch (error) {
        console.error('加载插件分类树失败:', error);
      } finally {
        setLoading(false);
      }
    };
    
    if (visible) {
      loadCategoryTree();
    }
  }, [visible]);

  useEffect(() => {
    if (visible) {
      if (initialValues) {
        // 如果有分类数据，需要转换为 TreeSelect 需要的格式
        let termTypeIds = [];
        if (initialValues.termTypeIds) {
          // 如果已经是数组格式，直接使用
          if (Array.isArray(initialValues.termTypeIds)) {
            termTypeIds = initialValues.termTypeIds.map(item => {
              if (typeof item === 'object' && item.value) {
                return item.value;
              }
              return item;
            });
          }
        }
        
        // 处理 tags 字段：可能是数组、JSON 字符串或普通字符串
        let tagsValue = [];
        if (initialValues.tags) {
          if (Array.isArray(initialValues.tags)) {
            tagsValue = initialValues.tags;
          } else if (typeof initialValues.tags === 'string') {
            try {
              tagsValue = JSON.parse(initialValues.tags);
              // 确保解析后是数组
              if (!Array.isArray(tagsValue)) {
                tagsValue = [];
              }
            } catch (e) {
              // 如果不是有效的 JSON，可能是单个标签字符串，转换为数组
              tagsValue = initialValues.tags.trim() ? [initialValues.tags] : [];
            }
          }
        }
        
        form.setFieldsValue({
          ...initialValues,
          tags: tagsValue,
          termTypeIds: termTypeIds.length > 0 ? termTypeIds : undefined,
        });
      } else {
        form.resetFields();
      }
    }
  }, [visible, initialValues, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      // 处理标签字段
      if (values.tags && Array.isArray(values.tags)) {
        values.tags = JSON.stringify(values.tags);
      }

      // termTypeIds 已经是数组格式，直接传递
      // TreeSelect 返回的是数组，格式为 [termTypeId1, termTypeId2, ...]

      onSubmit(values);
    } catch (error) {
      message.error('请检查表单填写是否正确');
    }
  };

  const categoryOptions = [
    { label: '开发工具', value: 'DEVELOPMENT' },
    { label: '系统工具', value: 'SYSTEM' },
    { label: '网络工具', value: 'NETWORK' },
    { label: '安全工具', value: 'SECURITY' },
    { label: '娱乐工具', value: 'ENTERTAINMENT' },
    { label: '其他', value: 'OTHER' },
  ];

  const reviewStatusOptions = [
    { label: '待审核', value: 'PENDING' },
    { label: '已通过', value: 'APPROVED' },
    { label: '已拒绝', value: 'REJECTED' },
    { label: '已下架', value: 'OFFLINE' },
  ];

  const footer = (
    <Space>
      <Button onClick={onCancel}>取消</Button>
      <Button type="primary" icon={initialValues ? <EditOutlined /> : <PlusOutlined />} onClick={handleSubmit}>
        {initialValues ? '保存修改' : '新增插件'}
      </Button>
    </Space>
  );

  return (
    <FullscreenModal
      title={
        <Space>
          {initialValues ? <EditOutlined style={{ color: '#1890ff' }} /> : <PlusOutlined style={{ color: '#1890ff' }} />}
          {initialValues ? '编辑插件' : '新增插件'}
        </Space>
      }
      visible={visible}
      onCancel={onCancel}
      width={900}
      bodyStyle={{
        padding: '24px'
      }}
      footer={footer}
      destroyOnClose
    >
      <div className="plugin-form">
        {/* 页面说明 */}
        <Alert
          message="插件信息配置"
          description="填写插件的详细信息，包括基本信息、技术参数、统计数据和审核状态等。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        <Form
          form={form}
          layout="vertical"
          size="large"
          initialValues={{
            autoStart: false,
            reviewStatus: 'PENDING',
            installSource: 'UPLOAD',
            autoUpdate: false,
          }}
        >
          {/* 基础信息分组 */}
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
              <Tooltip title="插件的基本信息配置">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="pluginCode"
                  label={
                    <Space>
                      插件编码
                      <Tooltip title="插件的唯一标识符，用于系统识别">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[
                    { required: true, message: '请输入插件ID' },
                    { pattern: /^[a-zA-Z0-9-_]+$/, message: '插件ID只能包含字母、数字、下划线和横线' },
                  ]}
                >
                  <Input 
                    prefix={<CodeOutlined />}
                    placeholder="请输入插件ID，如：example-plugin" 
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="name"
                  label={
                    <Space>
                      插件名称
                      <Tooltip title="插件的显示名称">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请输入插件名称' }]}
                >
                  <Input 
                    prefix={<FileTextOutlined />}
                    placeholder="请输入插件名称" 
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="version"
                  label={
                    <Space>
                      版本号
                      <Tooltip title="插件的版本信息">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请输入版本号' }]}
                >
                  <Input 
                    prefix={<TagOutlined />}
                    placeholder="请输入版本号，如：1.0.0" 
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="author"
                  label={
                    <Space>
                      作者
                      <Tooltip title="插件开发者">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请输入作者' }]}
                >
                  <Input 
                    prefix={<UserOutlined />}
                    placeholder="请输入作者" 
                  />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item
              name="description"
              label={
                <Space>
                  插件描述
                  <Tooltip title="插件的详细功能描述">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[{ required: true, message: '请输入插件描述' }]}
            >
              <TextArea
                rows={4}
                placeholder="请输入插件描述"
                maxLength={500}
                showCount
                style={{ resize: 'vertical' }}
              />
            </Form.Item>
          </Card>

          {/* 技术配置分组 */}
          <Card 
            title={
              <Space>
                <SettingOutlined style={{ color: '#52c41a' }} />
                技术配置
              </Space>
            }
            size="small"
            style={{ marginBottom: '16px' }}
            extra={
              <Tooltip title="插件的技术参数配置">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="mainClass"
                  label={
                    <Space>
                      主类
                      <Tooltip title="插件的主入口类">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请输入主类' }]}
                >
                  <Input 
                    prefix={<CodeOutlined />}
                    placeholder="请输入主类，如：com.example.ExamplePlugin" 
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="termTypeIds"
                  label={
                    <Space>
                      分类
                      <Tooltip title="插件的功能分类">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请选择分类' }]}
                >
                  <TreeSelect
                    showSearch
                    treeData={categoryTree}
                    treeDefaultExpandAll
                    allowClear
                    multiple
                    treeCheckable
                    showCheckedStrategy={SHOW_PARENT}
                    dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                    placeholder="请选择分类"
                    treeNodeFilterProp="title"
                    loading={loading}
                    style={{ width: '100%' }}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="tags"
                  label={
                    <Space>
                      标签
                      <Tooltip title="插件的功能标签，便于分类和搜索">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Select
                    mode="tags"
                    placeholder="请输入标签，按回车确认"
                    style={{ width: '100%' }}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="icon"
                  label={
                    <Space>
                      图标
                      <Tooltip title="上传插件图标，支持图片格式">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Upload
                    name="file"
                    listType="picture-card"
                    showUploadList={false}
                    action={`${prefix}/file/store`}
                    headers={headerFix}
                    accept=".jpg,.png,.gif,.jpeg,.bmp,.x-icon,.ico,.svg"
                    beforeUpload={(file) => {
                      const isImage = file.type.startsWith('image/');
                      if (!isImage) {
                        message.error('只能上传图片文件！');
                        return false;
                      }
                      const isLt2M = file.size / 1024 / 1024 < 2;
                      if (!isLt2M) {
                        message.error('图片大小不能超过 2MB！');
                        return false;
                      }
                      return true;
                    }}
                    onChange={(info) => {
                      if (info.file.status === 'done') {
                        // 上传成功，从响应中获取文件信息
                        const response = info.file.response;
                        console.log('图标上传响应:', response); // 调试日志
                        let fileInfo = null;
                        
                        // 处理不同的响应格式
                        if (response) {
                          // 格式1: { status: 200, message: 'ok', data: FileInfo }
                          if (response.data) {
                            fileInfo = response.data;
                          }
                          // 格式2: 直接返回 FileInfo 对象
                          else if (response.url || response.path) {
                            fileInfo = response;
                          }
                          // 格式3: 响应本身就是 FileInfo
                          else if (response.id || response.fileName) {
                            fileInfo = response;
                          }
                        }
                        
                        console.log('解析后的文件信息:', fileInfo); // 调试日志
                        
                        if (fileInfo) {
                          // FileInfo 包含 url 和 path 字段，优先使用 url
                          let iconUrl = fileInfo.url || fileInfo.path;
                          
                          // 如果 URL 是相对路径，需要确保以 / 开头
                          if (iconUrl) {
                            if (!iconUrl.startsWith('http://') && !iconUrl.startsWith('https://') && !iconUrl.startsWith('/')) {
                              iconUrl = '/' + iconUrl;
                            }
                            
                            console.log('设置图标 URL:', iconUrl); // 调试日志
                            form.setFieldsValue({ icon: iconUrl });
                            message.success('图标上传成功');
                          } else {
                            console.error('文件信息中没有 url 或 path:', fileInfo);
                            message.error('图标上传成功，但未获取到文件路径');
                          }
                        } else {
                          console.error('无法解析文件信息，响应:', response);
                          message.error('图标上传成功，但响应格式异常，请查看控制台日志');
                        }
                      } else if (info.file.status === 'error') {
                        console.error('图标上传失败:', info.file.error);
                        message.error('图标上传失败: ' + (info.file.error?.message || '未知错误'));
                      }
                    }}
                  >
                    <Form.Item shouldUpdate={(prevValues, currentValues) => prevValues.icon !== currentValues.icon} noStyle>
                      {({ getFieldValue }) => {
                        const iconUrl = getFieldValue('icon');
                        return iconUrl ? (
                          <div 
                            className="icon-preview-container"
                            style={{ 
                              position: 'relative', 
                              width: '100%', 
                              height: '100%',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              background: '#fafafa',
                              padding: '20px' // 匹配市场展示的 padding
                            }}
                          >
                            <img
                              src={iconUrl}
                              alt="icon"
                              style={{ 
                                maxWidth: '100%', 
                                maxHeight: '100%', 
                                width: 'auto',
                                height: 'auto',
                                objectFit: 'contain', // 使用 contain 匹配市场展示
                                display: 'block'
                              }}
                              onError={(e) => {
                                console.error('图标加载失败:', iconUrl, e);
                                e.target.style.display = 'none';
                                const errorDiv = e.target.parentElement?.querySelector('.icon-error-placeholder');
                                if (errorDiv) {
                                  errorDiv.style.display = 'flex';
                                }
                              }}
                            />
                            <div 
                              className="icon-error-placeholder"
                              style={{
                                display: 'none',
                                width: '100%',
                                height: '100%',
                                alignItems: 'center',
                                justifyContent: 'center',
                                color: '#999',
                                fontSize: '12px'
                              }}
                            >
                              图标加载失败
                            </div>
                            <div
                              style={{
                                position: 'absolute',
                                top: 0,
                                left: 0,
                                right: 0,
                                bottom: 0,
                                display: 'none',
                                alignItems: 'center',
                                justifyContent: 'center',
                                background: 'rgba(0,0,0,0.5)',
                                opacity: 0,
                                transition: 'opacity 0.3s',
                                cursor: 'pointer'
                              }}
                              onMouseEnter={(e) => e.currentTarget.style.opacity = '1'}
                              onMouseLeave={(e) => e.currentTarget.style.opacity = '0'}
                              onClick={(e) => {
                                e.stopPropagation();
                                form.setFieldsValue({ icon: '' });
                                message.success('已清除图标');
                              }}
                            >
                              <PictureOutlined style={{ color: '#fff', fontSize: '20px' }} />
                            </div>
                          </div>
                        ) : (
                          <div>
                            <PictureOutlined style={{ fontSize: '24px', color: '#999' }} />
                            <div style={{ marginTop: 8, color: '#999' }}>上传图标</div>
                          </div>
                        );
                      }}
                    </Form.Item>
                  </Upload>
                </Form.Item>
              </Col>
            </Row>
            <Form.Item
              name="downloadUrl"
              label={
                <Space>
                  下载地址
                  <Tooltip title="插件的下载链接">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              rules={[
                { type: 'url', message: '请输入有效的URL地址' },
              ]}
            >
              <Input 
                prefix={<LinkOutlined />}
                placeholder="请输入下载地址" 
              />
            </Form.Item>
          </Card>

          {/* 统计数据分组 */}
          <Card 
            title={
              <Space>
                <StarOutlined style={{ color: '#fa8c16' }} />
                统计数据
              </Space>
            }
            size="small"
            style={{ marginBottom: '16px' }}
            extra={
              <Tooltip title="插件的使用统计信息">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Row gutter={16}>
              <Col span={8}>
                <Form.Item
                  name="downloadCount"
                  label={
                    <Space>
                      下载量
                      <Tooltip title="插件下载次数">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Input
                    type="number"
                    placeholder="请输入下载量"
                    min={0}
                  />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  name="starCount"
                  label={
                    <Space>
                      点赞数
                      <Tooltip title="用户点赞数量">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Input
                    type="number"
                    placeholder="请输入点赞数"
                    min={0}
                  />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  name="likeCount"
                  label={
                    <Space>
                      收藏数
                      <Tooltip title="用户收藏数量">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Input
                    type="number"
                    placeholder="请输入收藏数"
                    min={0}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="reviewCount"
                  label={
                    <Space>
                      评论数
                      <Tooltip title="用户评论数量">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Input
                    type="number"
                    placeholder="请输入评论数"
                    min={0}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="autoStart"
                  label={
                    <Space>
                      自动启动
                      <Tooltip title="插件是否自动启动">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
              </Col>
            </Row>
          </Card>

          {/* Git 仓库信息分组（仅当安装来源为 GITHUB 或 GITEE 时显示） */}
          <Form.Item shouldUpdate={(prevValues, currentValues) => prevValues.installSource !== currentValues.installSource} noStyle>
            {({ getFieldValue }) => {
              const installSource = getFieldValue('installSource');
              const isGitSource = installSource === 'GITHUB' || installSource === 'GITEE';
              
              if (!isGitSource) return null;
              
              return (
                <Card 
                  title={
                    <Space>
                      <GithubOutlined style={{ color: '#1890ff' }} />
                      Git 仓库信息
                    </Space>
                  }
                  size="small"
                  style={{ marginBottom: '16px' }}
                  extra={
                    <Tooltip title="Git 仓库相关信息">
                      <InfoCircleOutlined />
                    </Tooltip>
                  }
                >
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="repoBranch"
                        label={
                          <Space>
                            Git 分支
                            <Tooltip title="Git 仓库分支名">
                              <InfoCircleOutlined style={{ color: '#999' }} />
                            </Tooltip>
                          </Space>
                        }
                      >
                        <Input placeholder="如：main" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="repoCommit"
                        label={
                          <Space>
                            Git 提交哈希
                            <Tooltip title="Git 提交哈希值">
                              <InfoCircleOutlined style={{ color: '#999' }} />
                            </Tooltip>
                          </Space>
                        }
                      >
                        <Input placeholder="如：abc123..." />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="repoLastUpdate"
                        label={
                          <Space>
                            Git 最后更新时间
                            <Tooltip title="Git 仓库最后更新时间">
                              <InfoCircleOutlined style={{ color: '#999' }} />
                            </Tooltip>
                          </Space>
                        }
                      >
                        <Input disabled placeholder="自动更新" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="autoUpdate"
                        label={
                          <Space>
                            自动更新
                            <Tooltip title="是否自动从 Git 仓库同步最新版本">
                              <InfoCircleOutlined style={{ color: '#999' }} />
                            </Tooltip>
                          </Space>
                        }
                        valuePropName="checked"
                      >
                        <Switch />
                      </Form.Item>
                    </Col>
                  </Row>
                </Card>
              );
            }}
          </Form.Item>

          {/* 审核状态分组 */}
          <Card 
            title={
              <Space>
                <CheckCircleOutlined style={{ color: '#722ed1' }} />
                审核状态
              </Space>
            }
            size="small"
            style={{ marginBottom: '16px' }}
            extra={
              <Tooltip title="插件的审核状态和相关信息">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="reviewStatus"
                  label={
                    <Space>
                      审核状态
                      <Tooltip title="插件的当前审核状态">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                  rules={[{ required: true, message: '请选择审核状态' }]}
                >
                  <Select placeholder="请选择审核状态">
                    {reviewStatusOptions.map(option => (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="installSource"
                  label={
                    <Space>
                      安装来源
                      <Tooltip title="插件的安装来源">
                        <InfoCircleOutlined style={{ color: '#999' }} />
                      </Tooltip>
                    </Space>
                  }
                >
                  <Select placeholder="请选择安装来源">
                    <Option value="UPLOAD">上传</Option>
                    <Option value="GITHUB">GitHub</Option>
                    <Option value="GITEE">Gitee</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            <Form.Item
              name="reviewMessage"
              label={
                <Space>
                  审核信息
                  <Tooltip title="审核相关的备注信息">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <TextArea
                rows={3}
                placeholder="请输入审核信息"
                maxLength={200}
                showCount
                style={{ resize: 'vertical' }}
              />
            </Form.Item>
          </Card>
        </Form>
      </div>
    </FullscreenModal>
  );
};

export default connect()(StoreForm);
