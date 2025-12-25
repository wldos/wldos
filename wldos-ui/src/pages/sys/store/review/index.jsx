import React, { useState, useEffect, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import {
  Button,
  Tag,
  Space,
  Modal,
  message,
  Tooltip,
  Badge,
  Input,
  Select,
  Popconfirm,
  Form,
  Switch
} from 'antd';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {
  EyeOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  UploadOutlined,
  ReloadOutlined,
  DownloadOutlined,
  EditOutlined,
  StopOutlined,
  GithubOutlined,
  SyncOutlined
} from '@ant-design/icons';
import { connect } from 'umi';
import moment from 'moment';
import StoreForm from '../components/StoreForm';
import StoreDetail from '../components/StoreDetail';
import {
  queryAppStorePlugins,
  addStorePlugin,
  updateStorePlugin,
  deleteStorePlugin,
  reviewStorePlugin,
  offlineStorePlugin,
  uploadPluginFile,
  getPluginCategories,
  syncFromGitHub
} from '../service';
import { installPlugin, queryPlugins } from '@/pages/sys/plugins/service';

const { TextArea } = Input;
const { Option } = Select;

const StoreReview = ({ dispatch }) => {
  const [formVisible, setFormVisible] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [reviewVisible, setReviewVisible] = useState(false);
  const [currentPlugin, setCurrentPlugin] = useState(null);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewStatus, setReviewStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [categoryOptions, setCategoryOptions] = useState([]); // 分类选项列表
  const [syncVisible, setSyncVisible] = useState(false);
  const [syncing, setSyncing] = useState(false);
  const [syncForm] = Form.useForm();
  const [installedPlugins, setInstalledPlugins] = useState([]); // 已安装插件列表

  const mobile = isMobile();
  const containerRef = useRef();
  const actionRef = useRef();

  // 获取已安装的插件列表
  useEffect(() => {
    const fetchInstalledPlugins = async () => {
      try {
        const res = await queryPlugins({ pageSize: 1000 });
        if (res?.data?.rows) {
          const installedCodes = res.data.rows.map(p => p.pluginCode);
          setInstalledPlugins(installedCodes);
        }
      } catch (error) {
        console.error('获取已安装插件失败:', error);
      }
    };
    fetchInstalledPlugins();
  }, []);

  // 判断插件是否已安装
  const isInstalled = (pluginCode) => {
    return installedPlugins.includes(pluginCode);
  };

  // 获取插件分类列表
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await getPluginCategories();
        if (res?.data) {
          // 转换树形结构为扁平列表（用于下拉选择）
          const flattenCategories = (items, result = []) => {
            items.forEach(item => {
              // 排除根分类（value = "0" 或 id = "0" 或 id = 0）
              const itemId = item.id != null ? String(item.id) : item.value;
              const itemValue = item.value || itemId;
              if (itemValue && itemValue !== '0') {
                result.push({
                  label: item.label || item.name,
                  value: itemValue,
                });
              }
              if (item.children && item.children.length > 0) {
                flattenCategories(item.children, result);
              }
            });
            return result;
          };
          const flatList = flattenCategories(Array.isArray(res.data) ? res.data : []);
          setCategoryOptions(flatList);
        }
      } catch (error) {
        console.error('获取插件分类列表失败:', error);
      }
    };
    fetchCategories();
  }, []);

  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

  // 监听容器宽度变化
  useEffect(() => {
    if (!containerRef.current) return;

    const updateWidth = () => {
      if (containerRef.current) {
        // 宽度监听逻辑
      }
    };

    updateWidth();
    const resizeObserver = new ResizeObserver(updateWidth);
    resizeObserver.observe(containerRef.current);

    return () => resizeObserver.disconnect();
  }, []);

  // 表格列配置
  const columns = [
    {
      title: '插件编码',
      dataIndex: 'pluginCode',
      key: 'pluginCode',
      width: 150,
      ellipsis: true,
      fixed: mobile ? undefined : 'left',
    },
    {
      title: '插件名称',
      dataIndex: 'pluginName',
      key: 'pluginName',
      width: 180,
      ellipsis: true,
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 100,
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 120,
    },
    {
      title: '分类',
      dataIndex: 'termTypeId',  // 查询条件使用 termTypeId（单个分类ID）
      key: 'termTypeId',
      width: 120,
      valueType: 'select',
      valueEnum: categoryOptions.length > 0 ? categoryOptions.reduce((acc, item) => {
        acc[item.value] = { text: item.label };
        return acc;
      }, {}) : {},
      render: (_, record) => {
        // 显示使用 categoryNames 字段，确保是数组
        const categoryNames = record.categoryNames;
        if (!categoryNames) return '-';
        // 处理可能是字符串的情况（兼容性处理）
        const categoryList = Array.isArray(categoryNames)
          ? categoryNames
          : (typeof categoryNames === 'string' ? JSON.parse(categoryNames || '[]') : []);
        if (!categoryList || categoryList.length === 0) {
          return '-';
        }
        return (
          <Space size={[0, 4]} wrap>
            {categoryList.map((name, index) => (
              <Tag key={index} color="blue">{name}</Tag>
            ))}
          </Space>
        );
      },
    },
    {
      title: '标签',
      dataIndex: 'tags',
      key: 'tags',
      width: 200,
      render: (tags) => {
        if (!tags) return '-';
        const tagList = typeof tags === 'string' ? JSON.parse(tags) : tags;
        return (
          <Space size={[0, 4]} wrap>
            {tagList.slice(0, 3).map((tag, index) => (
              <Tag key={index} size="small">{tag}</Tag>
            ))}
            {tagList.length > 3 && <Tag size="small">+{tagList.length - 3}</Tag>}
          </Space>
        );
      },
    },
    {
      title: '审核状态',
      dataIndex: 'reviewStatus',
      key: 'reviewStatus',
      width: 120,
      filters: [
        { text: '待审核', value: 'PENDING' },
        { text: '已通过', value: 'APPROVED' },
        { text: '已拒绝', value: 'REJECTED' },
        { text: '已下架', value: 'OFFLINE' },
      ],
      render: (status) => {
        const statusMap = {
          'PENDING': { text: '待审核', color: 'orange' },
          'APPROVED': { text: '已通过', color: 'green' },
          'REJECTED': { text: '已拒绝', color: 'red' },
          'OFFLINE': { text: '已下架', color: 'default' },
        };
        const config = statusMap[status] || { text: '未知', color: 'default' };
        return <Badge status={config.color} text={config.text} />;
      },
    },
    {
      title: '安装来源',
      dataIndex: 'installSource',
      key: 'installSource',
      width: 120,
      filters: [
        { text: '上传', value: 'UPLOAD' },
        { text: 'GitHub', value: 'GITHUB' },
        { text: 'Gitee', value: 'GITEE' },
      ],
      render: (source) => {
        const sourceMap = {
          'UPLOAD': { text: '上传', color: 'default' },
          'GITHUB': { text: 'GitHub', color: 'blue' },
          'GITEE': { text: 'Gitee', color: 'green' },
        };
        const config = sourceMap[source] || { text: '未知', color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '审核人',
      dataIndex: 'reviewerName',
      key: 'reviewerName',
      width: 120,
      render: (text) => text || '-',
    },
    {
      title: '审核时间',
      dataIndex: 'reviewTime',
      key: 'reviewTime',
      width: 180,
      valueType: 'dateTime',
      render: (text, record) => {
        // 如果审核状态不是待审核，显示审核时间（后端已设置为 updateTime）
        if (record.reviewStatus && record.reviewStatus !== 'PENDING') {
          const timeValue = record.reviewTime || record.updateTime;
          if (!timeValue) {
            return '-';
          }

          // 格式化时间：处理时间戳（毫秒）和日期字符串
          try {
            // 如果是数字（时间戳），转换为日期
            if (typeof timeValue === 'number' || (typeof timeValue === 'string' && /^\d+$/.test(timeValue))) {
              const timestamp = typeof timeValue === 'string' ? parseInt(timeValue, 10) : timeValue;
              // 判断是秒还是毫秒时间戳（13位是毫秒，10位是秒）
              const date = timestamp > 9999999999 ? moment(timestamp) : moment(timestamp * 1000);
              return date.format('YYYY-MM-DD HH:mm:ss');
            }
            // 如果是日期字符串，直接格式化
            return moment(timeValue).format('YYYY-MM-DD HH:mm:ss');
          } catch (e) {
            // 如果格式化失败，返回原始值
            return timeValue;
          }
        }
        return '-';
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: mobile ? undefined : 'right',
      render: (_, record) => {
        // 未上架发布的插件状态：待审核、已拒绝、已下架
        const isNotPublished = ['PENDING', 'REJECTED', 'OFFLINE'].includes(record.reviewStatus);
        // 已上架发布的插件状态：已通过
        const isPublished = record.reviewStatus === 'APPROVED';

        return (
          <Space size="small">
            <Tooltip title="查看详情">
              <Button
                type="link"
                size="small"
                icon={<EyeOutlined />}
                onClick={() => handleView(record)}
              />
            </Tooltip>
            {isNotPublished && (
              <Tooltip title="编辑">
                <Button
                  type="link"
                  size="small"
                  icon={<EditOutlined />}
                  onClick={() => handleEdit(record)}
                />
              </Tooltip>
            )}
            {record.reviewStatus === 'PENDING' && (
              <>
                <Tooltip title="通过审核">
                  <Button
                    type="link"
                    size="small"
                    icon={<CheckCircleOutlined />}
                    style={{ color: '#52c41a' }}
                    onClick={() => handleReview(record, true)}
                  />
                </Tooltip>
                <Tooltip title="拒绝审核">
                  <Button
                    type="link"
                    size="small"
                    danger
                    icon={<CloseCircleOutlined />}
                    onClick={() => handleReview(record, false)}
                  />
                </Tooltip>
              </>
            )}
            {isPublished && (
              <>
                {!isInstalled(record.pluginCode) && (
                  <Tooltip title="测试安装">
                    <Popconfirm
                      title="确认测试安装此插件？"
                      onConfirm={() => handleTestInstall(record)}
                      okText="确认"
                      cancelText="取消"
                    >
                      <Button
                        type="link"
                        size="small"
                        icon={<DownloadOutlined />}
                        style={{ color: '#1890ff' }}
                      />
                    </Popconfirm>
                  </Tooltip>
                )}
                {(record.installSource === 'GITHUB' || record.installSource === 'GITEE') && (
                  <Tooltip title="同步最新版本">
                    <Button
                      type="link"
                      size="small"
                      icon={<SyncOutlined />}
                      style={{ color: '#722ed1' }}
                      onClick={() => handleSyncLatest(record)}
                      loading={syncing}
                    />
                  </Tooltip>
                )}
                <Tooltip title="下架">
                  <Popconfirm
                    title="确认下架此插件？"
                    onConfirm={() => handleOffline(record)}
                    okText="确认"
                    cancelText="取消"
                  >
                    <Button
                      type="link"
                      size="small"
                      icon={<StopOutlined />}
                      style={{ color: '#ff4d4f' }}
                    />
                  </Popconfirm>
                </Tooltip>
              </>
)}
          </Space>
        );
      },
    },
  ];

  // 查看详情
  const handleView = (record) => {
    setCurrentPlugin(record);
    setDetailVisible(true);
  };

  // 编辑插件
  const handleEdit = (record) => {
    setCurrentPlugin(record);
    setFormVisible(true);
  };

  // 删除插件
  const handleDelete = (record) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除插件 "${record.pluginName || record.pluginCode}" 吗？`,
      onOk: async () => {
        try {
          await deleteStorePlugin(record.id);
          message.success('删除成功');
          if (actionRef.current) {
            actionRef.current.reload();
          }
        } catch (error) {
          message.error('删除失败');
        }
      },
    });
  };

  // 新增插件
  const handleAdd = () => {
    setCurrentPlugin(null);
    setFormVisible(true);
  };

  // 表单提交
  const handleFormSubmit = async (values) => {
    try {
      // 字段映射：前端字段名 -> 后端字段名
      const mappedValues = {
        ...values,
        pluginName: values.name,  // name -> pluginName
        downCount: values.downloadCount,  // downloadCount -> downCount
        pluginUrl: values.downloadUrl,  // downloadUrl -> pluginUrl
      };
      // 删除前端字段名
      delete mappedValues.name;
      delete mappedValues.downloadCount;
      delete mappedValues.downloadUrl;

      if (currentPlugin) {
        // 更新时需要包含 id
        await updateStorePlugin(currentPlugin.id, {
          ...mappedValues,
          id: currentPlugin.id
        });
        message.success('更新成功');
      } else {
        await addStorePlugin(mappedValues);
        message.success('添加成功');
      }
      setFormVisible(false);
      if (actionRef.current) {
        actionRef.current.reload();
      }
    } catch (error) {
      message.error('操作失败: ' + (error.message || '未知错误'));
    }
  };

  // 审核插件
  const handleReview = (record, approved) => {
    setCurrentPlugin(record);
    setReviewStatus(approved);
    setReviewComment('');
    setReviewVisible(true);
  };

  // 提交审核
  const handleReviewSubmit = async () => {
    if (!currentPlugin) return;

    setLoading(true);
    try {
      await reviewStorePlugin(currentPlugin.pluginCode, {
        approved: reviewStatus,
        comments: reviewComment
      });

      message.success(`插件${reviewStatus ? '审核通过' : '审核拒绝'}成功`);
      setReviewVisible(false);
      if (actionRef.current) {
        actionRef.current.reload();
      }
    } catch (error) {
      message.error('审核失败: ' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 测试安装插件
  const handleTestInstall = async (record) => {
    try {
      const response = await installPlugin({ pluginCode: record.pluginCode });

      if (response?.data === 'ok') {
        message.success(`插件 "${record.pluginName || record.pluginCode}" 测试安装成功`);
        // 更新已安装插件列表
        setInstalledPlugins(prev => [...prev, record.pluginCode]);
        // 刷新插件清单
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
      } else {
        message.error('测试安装失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      console.error('测试安装失败:', error);
      message.error('测试安装失败: ' + (error.message || '网络错误'));
    }
  };

  // 详情页安装插件
  const handleDetailInstall = async () => {
    if (!currentPlugin) return;

    try {
      const response = await installPlugin({ pluginCode: currentPlugin.pluginCode });

      if (response?.data === 'ok') {
        message.success(`插件 "${currentPlugin.pluginName || currentPlugin.pluginCode}" 安装成功`);
        // 更新已安装插件列表
        setInstalledPlugins(prev => [...prev, currentPlugin.pluginCode]);
        // 刷新插件清单
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        // 关闭详情弹窗
        setDetailVisible(false);
      } else {
        message.error('安装失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      console.error('安装失败:', error);
      message.error('安装失败: ' + (error.message || '网络错误'));
    }
  };

  // 上传插件
  const handleUpload = async (file) => {
    setUploading(true);
    try {
      const response = await uploadPluginFile(file);
      // WLDOS 统一返回格式：{ status: 200, message: "ok" 或 "错误消息", data: "..." }
      // 如果 message 不是 "ok"，说明有错误消息
      if (response?.message && response.message !== 'ok') {
        message.error(response.message);
      } else {
        message.success('插件上传成功，等待审核');
        if (actionRef.current) {
          actionRef.current.reload();
        }
      }
    } catch (error) {
      // 从响应中提取错误消息
      const errorMessage = error?.response?.message || error?.message || '上传失败';
      message.error(errorMessage);
    } finally {
      setUploading(false);
    }
  };

  // 下架插件
  const handleOffline = async (record) => {
    try {
      await offlineStorePlugin(record.pluginCode);
      message.success('插件下架成功');
      if (actionRef.current) {
        actionRef.current.reload();
      }
    } catch (error) {
      message.error('下架失败: ' + (error.message || '未知错误'));
    }
  };

  // 打开同步对话框
  const handleOpenSync = () => {
    syncForm.resetFields();
    setSyncVisible(true);
  };

  // 提交同步
  const handleSyncSubmit = async () => {
    try {
      const values = await syncForm.validateFields();
      setSyncing(true);

      const syncData = {
        repoUrl: values.repoUrl,
        branch: values.branch || 'main',
        commitHash: values.commitHash || undefined,
        buildFromSource: values.buildFromSource || false,
        buildTimeoutSeconds: values.buildTimeoutSeconds || 600,
      };

      await syncFromGitHub(syncData);
      message.success('插件同步成功');
      setSyncVisible(false);
      if (actionRef.current) {
        actionRef.current.reload();
      }
    } catch (error) {
      message.error('同步失败: ' + (error.message || '未知错误'));
    } finally {
      setSyncing(false);
    }
  };

  // 同步最新版本（针对已存在的 Git 插件）
  const handleSyncLatest = async (record) => {
    if (!record.pluginUrl) {
      message.warning('该插件没有 Git 仓库地址');
      return;
    }

    Modal.confirm({
      title: '确认同步最新版本',
      content: `确定要从 Git 仓库同步插件 "${record.pluginName || record.pluginCode}" 的最新版本吗？`,
      onOk: async () => {
        try {
          setSyncing(true);
          const syncData = {
            repoUrl: record.pluginUrl,
            branch: record.repoBranch || 'main',
            commitHash: undefined, // 使用最新提交
            buildFromSource: false,
            buildTimeoutSeconds: 600,
          };
          await syncFromGitHub(syncData);
          message.success('同步成功');
          if (actionRef.current) {
            actionRef.current.reload();
          }
        } catch (error) {
          message.error('同步失败: ' + (error.message || '未知错误'));
        } finally {
          setSyncing(false);
        }
      },
    });
  };

  return (
    <PageContainer
      title="插件审核管理"
      extra={
        <Space>
          <Button
            icon={<GithubOutlined />}
            onClick={handleOpenSync}
          >
            从 Git 同步
          </Button>
          <Button
            icon={<UploadOutlined />}
            onClick={() => {
              // 触发文件上传
              const input = document.createElement('input');
              input.type = 'file';
              input.accept = '.zip';
              input.onchange = (e) => {
                const file = e.target.files[0];
                if (file) {
                  handleUpload(file);
                }
              };
              input.click();
            }}
            loading={uploading}
          >
            上传插件
          </Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={() => {
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }}
          >
            刷新
          </Button>
        </Space>
      }
      style={{
        padding: '0',
        margin: '0'
      }}
      bodyStyle={{
        padding: '24px',
        margin: '0'
      }}
    >
      <div ref={containerRef}>
        <ProTableX
          headerTitle="插件审核列表"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button
              type="primary"
              key="add"
              icon={<UploadOutlined />}
              onClick={handleAdd}
            >
              新增插件
            </Button>,
          ]}
          request={async (params, sorter, filter) => {
            const res = await queryAppStorePlugins({
              ...params,
              sorter,
              filter
            });
            // 转换后端字段名到前端字段名，用于表单回显
            const rows = (res?.data?.rows || []).map(row => {
              // 处理 tags 字段：后端返回的是数组，确保是数组格式
              let tagsValue = [];
              if (row.tags) {
                if (Array.isArray(row.tags)) {
                  tagsValue = row.tags;
                } else if (typeof row.tags === 'string') {
                  try {
                    tagsValue = JSON.parse(row.tags);
                    if (!Array.isArray(tagsValue)) {
                      tagsValue = [];
                    }
                  } catch (e) {
                    tagsValue = [];
                  }
                }
              }

              return {
                ...row,
                name: row.pluginName,  // pluginName -> name (用于表单)
                downloadCount: row.downCount,  // downCount -> downloadCount (用于表单)
                downloadUrl: row.pluginUrl,  // pluginUrl -> downloadUrl (用于表单)
                // termTypeIds 如果后端返回了分类信息，需要转换为 TreeSelect 需要的格式
                // 格式: [termTypeId1, termTypeId2, ...] 或 [{label: '', value: ''}, ...]
                termTypeIds: Array.isArray(row.termTypeIds) ? row.termTypeIds : [],
                // categoryNames 后端已返回分类名称列表，确保是数组
                categoryNames: Array.isArray(row.categoryNames)
                  ? row.categoryNames
                  : (typeof row.categoryNames === 'string' ? JSON.parse(row.categoryNames || '[]') : []),
                // tags 确保是数组格式
                tags: tagsValue,
                // 审核时间和审核人（后端已处理）
                reviewTime: row.reviewTime,
                reviewerName: row.reviewerName,
                // Git 相关字段
                installSource: row.installSource || 'UPLOAD',
                repoBranch: row.repoBranch,
                repoCommit: row.repoCommit,
                repoLastUpdate: row.repoLastUpdate,
                autoUpdate: row.autoUpdate || false,
              };
            });
            return Promise.resolve({
              total: res?.data?.total || 0,
              data: rows,
              success: true,
            });
          }}
          columns={columns}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
          }}
        />
      </div>

      {/* 表单弹窗 */}
      <StoreForm
        visible={formVisible}
        onCancel={() => setFormVisible(false)}
        onSubmit={handleFormSubmit}
        initialValues={currentPlugin}
      />

      {/* 详情弹窗 */}
      <StoreDetail
        visible={detailVisible}
        onCancel={() => setDetailVisible(false)}
        plugin={currentPlugin}
        showMainClass={true}
        showAdminInfo={true}
        installed={currentPlugin ? isInstalled(currentPlugin.pluginCode) : false}
        onInstall={handleDetailInstall}
      />

      {/* 审核弹窗 */}
      <Modal
        title={reviewStatus ? '通过审核' : '拒绝审核'}
        visible={reviewVisible}
        onOk={handleReviewSubmit}
        onCancel={() => setReviewVisible(false)}
        confirmLoading={loading}
        okText="确认"
        cancelText="取消"
        okButtonProps={{
          danger: !reviewStatus,
          type: reviewStatus ? 'primary' : 'default'
        }}
        width={600}
      >
        <div style={{ marginBottom: '16px' }}>
          <p>
            插件：<strong>{currentPlugin?.pluginName || currentPlugin?.pluginCode}</strong>
          </p>
          <p>
            版本：<strong>{currentPlugin?.version}</strong>
          </p>
        </div>
        <div>
          <p style={{ marginBottom: '8px' }}>审核意见：</p>
          <TextArea
            rows={4}
            placeholder={reviewStatus ? '请输入审核通过的意见（可选）' : '请输入拒绝审核的原因'}
            value={reviewComment}
            onChange={(e) => setReviewComment(e.target.value)}
            maxLength={500}
            showCount
          />
        </div>
      </Modal>

      {/* Git 同步弹窗 */}
      <Modal
        title={
          <Space>
            <GithubOutlined />
            <span>从 Git 仓库同步插件</span>
          </Space>
        }
        visible={syncVisible}
        onOk={handleSyncSubmit}
        onCancel={() => {
          setSyncVisible(false);
          syncForm.resetFields();
        }}
        confirmLoading={syncing}
        okText="开始同步"
        cancelText="取消"
        width={600}
      >
        <Form
          form={syncForm}
          layout="vertical"
          initialValues={{
            branch: 'main',
            buildFromSource: false,
            buildTimeoutSeconds: 600,
          }}
        >
          <Form.Item
            name="repoUrl"
            label="Git 仓库地址"
            rules={[
              { required: true, message: '请输入 Git 仓库地址' },
              { type: 'url', message: '请输入有效的 URL 地址' },
            ]}
            tooltip="支持 GitHub 和 Gitee，例如：https://github.com/owner/repo.git 或 https://gitee.com/owner/repo.git"
          >
            <Input
              placeholder="https://github.com/owner/repo.git"
              prefix={<GithubOutlined />}
            />
          </Form.Item>

          <Form.Item
            name="branch"
            label="分支名"
            tooltip="要同步的分支，默认为 main"
          >
            <Input placeholder="main" />
          </Form.Item>

          <Form.Item
            name="commitHash"
            label="提交哈希（可选）"
            tooltip="如果指定提交哈希，将使用该提交而不是分支"
          >
            <Input placeholder="可选，留空则使用分支最新提交" />
          </Form.Item>

          <Form.Item
            name="buildFromSource"
            label="从源码构建"
            valuePropName="checked"
            tooltip="如果启用，将从源码编译构建插件；否则直接下载 ZIP 包"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="buildTimeoutSeconds"
            label="构建超时时间（秒）"
            tooltip="从源码构建时的超时时间，默认 600 秒"
          >
            <Input type="number" min={60} max={3600} />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default connect()(StoreReview);

