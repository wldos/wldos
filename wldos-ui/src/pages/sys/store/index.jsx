import React, { useState, useEffect, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Card,
  Input,
  TreeSelect,
  Button,
  Tag,
  Space,
  Modal,
  message,
  Tooltip,
  Badge,
  Row,
  Col,
  Empty,
  Spin,
  Typography,
  Divider,
  Pagination
} from 'antd';
import {
  SearchOutlined,
  EyeOutlined,
  DownloadOutlined,
  StarOutlined,
  LikeOutlined,
  CheckCircleOutlined,
  AppstoreOutlined,
  FilterOutlined,
  LoadingOutlined
} from '@ant-design/icons';
import { connect } from 'umi';
import StoreDetail from './components/StoreDetail';
import { queryStorePlugins, installFeedback, getStorePluginCategories } from './service';
import { queryPlugins, installPlugin } from '@/pages/sys/plugins/service';
import isMobile from '@/hooks/isMobile';
import './index.less';

const { Search } = Input;
const { Text, Paragraph } = Typography;

const StoreList = ({ dispatch }) => {
  const [plugins, setPlugins] = useState([]);
  const [installedPlugins, setInstalledPlugins] = useState([]);
  const [loading, setLoading] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentPlugin, setCurrentPlugin] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(undefined);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(12);
  const [total, setTotal] = useState(0);
  const [installing, setInstalling] = useState({});
  const [categoryTree, setCategoryTree] = useState([]);
  const [categoriesLoading, setCategoriesLoading] = useState(false);

  const mobile = isMobile();

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

  // 获取插件分类树
  useEffect(() => {
    const fetchCategories = async () => {
      setCategoriesLoading(true);
      try {
        const res = await getStorePluginCategories();
        if (res?.data) {
          // 将 API 返回的分类数据转换为 TreeSelect 需要的格式
          // API 返回格式: [{ label: '分类名称', value: 'termTypeId', id: termTypeId, children: [...] }]
          const convertToTreeData = (items) => {
            return items.map(item => {
              const treeNode = {
                title: item.label || item.name,
                value: String(item.id || item.value),
                key: String(item.id || item.value),
              };
              if (item.children && item.children.length > 0) {
                treeNode.children = convertToTreeData(item.children);
              }
              return treeNode;
            });
          };

          const treeData = convertToTreeData(Array.isArray(res.data) ? res.data : []);
          setCategoryTree(treeData);
        }
      } catch (error) {
        console.error('获取插件分类失败:', error);
        message.warning('获取插件分类失败');
        setCategoryTree([]);
      } finally {
        setCategoriesLoading(false);
      }
    };
    fetchCategories();
  }, []);

  // 获取插件市场列表
  const fetchPlugins = async (params = {}) => {
    setLoading(true);
    try {
      const res = await queryStorePlugins({
        current: currentPage,
        pageSize: pageSize,
        keyword: searchKeyword,
        termTypeId: selectedCategory ? selectedCategory : undefined,
        ...params
      });

      if (res?.data) {
        // 映射后端返回的数据，统一字段名
        const mappedPlugins = (res.data.rows || []).map(plugin => ({
          ...plugin,
          name: plugin.pluginName || plugin.name, // 统一使用 name 字段
        }));
        setPlugins(mappedPlugins);
        setTotal(res.data.total || 0);
      }
    } catch (error) {
      console.error('获取插件列表失败:', error);
      message.error('获取插件列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlugins();
  }, [currentPage, pageSize, searchKeyword, selectedCategory]);

  // 搜索
  const handleSearch = (value) => {
    setSearchKeyword(value);
    setCurrentPage(1);
  };

  // 分类筛选
  const handleCategoryChange = (value) => {
    // TreeSelect 可能返回数组或单个值，统一处理为字符串或 undefined
    const categoryValue = Array.isArray(value)
      ? (value.length > 0 ? value[0] : undefined)
      : value;
    setSelectedCategory(categoryValue);
    setCurrentPage(1);
  };

  // 查看详情
  const handleView = (plugin) => {
    setCurrentPlugin(plugin);
    setDetailVisible(true);
  };

  // 安装插件
  const handleInstall = async (plugin) => {
    if (installing[plugin.pluginCode]) return;

    setInstalling(prev => ({ ...prev, [plugin.pluginCode]: true }));

    try {
      const response = await installPlugin({ pluginCode: plugin.pluginCode });

      if (response?.success) {
        message.success(`插件 "${plugin.name || plugin.pluginCode}" 安装成功`);
        // 更新已安装列表
        setInstalledPlugins(prev => [...prev, plugin.pluginCode]);
        // 刷新插件清单
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        // 反馈到市场（下载数+1）
        try {
          await installFeedback(plugin.pluginCode);
        } catch (error) {
          console.warn('市场反馈失败:', error);
        }
      } else {
        message.error('安装失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      console.error('安装失败:', error);
      message.error('安装失败: ' + (error.message || '网络错误'));
    } finally {
      setInstalling(prev => ({ ...prev, [plugin.pluginCode]: false }));
    }
  };

  // 判断是否已安装
  const isInstalled = (pluginCode) => {
    return installedPlugins.includes(pluginCode);
  };

  // 解析标签
  const parseTags = (tags) => {
    if (!tags) return [];
    try {
      return typeof tags === 'string' ? JSON.parse(tags) : tags;
    } catch {
      return [];
    }
  };

  return (
    <PageContainer
      title="插件市场"
      extra={
        <Space>
          <Badge count={installedPlugins.length} showZero>
            <Button icon={<AppstoreOutlined />} type="text">
              已安装 {installedPlugins.length} 个
            </Button>
          </Badge>
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
      {/* 搜索和筛选栏 */}
      <Card
        size="small"
        style={{ marginBottom: '24px' }}
        bodyStyle={{ padding: '16px' }}
      >
        <Row gutter={16} align="middle">
          <Col xs={24} sm={12} md={10}>
            <Search
              placeholder="搜索插件名称、描述..."
              allowClear
              enterButton={<SearchOutlined />}
              size="large"
              onSearch={handleSearch}
              onChange={(e) => !e.target.value && handleSearch('')}
            />
          </Col>
          <Col xs={24} sm={12} md={6}>
            <TreeSelect
              placeholder="选择分类"
              size="large"
              style={{ width: '100%' }}
              value={selectedCategory}
              onChange={handleCategoryChange}
              treeData={categoryTree}
              treeDefaultExpandAll
              allowClear
              showSearch
              treeNodeFilterProp="title"
              dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
              loading={categoriesLoading}
            />
          </Col>
          <Col xs={24} sm={24} md={8} style={{ textAlign: mobile ? 'left' : 'right' }}>
            <Text type="secondary">
              共找到 <Text strong>{total}</Text> 个插件
            </Text>
          </Col>
        </Row>
      </Card>

      {/* 插件卡片列表 */}
      <Spin spinning={loading}>
        {plugins.length === 0 ? (
          <Empty
            description="暂无插件"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            style={{ margin: '60px 0' }}
          />
        ) : (
          <>
            <Row gutter={[16, 16]}>
              {plugins.map(plugin => {
                const installed = isInstalled(plugin.pluginCode);
                const installingPlugin = installing[plugin.pluginCode];
                const tags = parseTags(plugin.tags);

                return (
                  <Col
                    key={plugin.id || plugin.pluginCode}
                    xs={24}
                    sm={12}
                    md={8}
                    lg={6}
                  >
                    <Card
                      hoverable
                      className="plugin-card"
                      cover={
                        <div className="plugin-card-cover">
                          {plugin.icon ? (
                            <img
                              src={plugin.icon}
                              alt={plugin.name}
                              onError={(e) => {
                                e.target.style.display = 'none';
                              }}
                            />
                          ) : (
                            <div className="plugin-card-placeholder">
                              <AppstoreOutlined style={{ fontSize: '48px', color: '#d9d9d9' }} />
                            </div>
                          )}
                          {installed && (
                            <div className="plugin-card-badge">
                              <Badge
                                status="success"
                                text="已安装"
                                style={{
                                  backgroundColor: 'rgba(82, 196, 26, 0.1)',
                                  padding: '4px 8px',
                                  borderRadius: '4px'
                                }}
                              />
                            </div>
                          )}
                        </div>
                      }
                      actions={[
                        <Tooltip title="查看详情">
                          <EyeOutlined
                            key="view"
                            onClick={() => handleView(plugin)}
                            style={{ fontSize: '18px' }}
                          />
                        </Tooltip>,
                        <Tooltip title={installed ? '已安装' : '安装插件'}>
                          {installed ? (
                            <CheckCircleOutlined
                              key="installed"
                              style={{ fontSize: '18px', color: '#52c41a' }}
                            />
                          ) : (
                            <DownloadOutlined
                              key="install"
                              onClick={() => handleInstall(plugin)}
                              style={{
                                fontSize: '18px',
                                color: installingPlugin ? '#1890ff' : undefined
                              }}
                            />
                          )}
                        </Tooltip>
                      ]}
                    >
                      <Card.Meta
                        title={
                          <div>
                            <Text strong ellipsis style={{ fontSize: '16px', display: 'block' }}>
                              {plugin.name || plugin.pluginCode}
                            </Text>
                            <Text type="secondary" style={{ fontSize: '12px' }}>
                              v{plugin.version}
                            </Text>
                          </div>
                        }
                        description={
                          <div>
                            <Paragraph
                              ellipsis={{ rows: 2 }}
                              style={{
                                marginBottom: '8px',
                                minHeight: '40px',
                                fontSize: '13px',
                                color: '#666'
                              }}
                            >
                              {plugin.description || '暂无描述'}
                            </Paragraph>
                            <div style={{ marginBottom: '8px' }}>
                              <Space size={[4, 4]} wrap>
                                {tags.slice(0, 3).map((tag, index) => (
                                  <Tag key={index} size="small">{tag}</Tag>
                                ))}
                                {tags.length > 3 && (
                                  <Tag size="small">+{tags.length - 3}</Tag>
                                )}
                              </Space>
                            </div>
                            <Divider style={{ margin: '8px 0' }} />
                            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px' }}>
                              <Space>
                                <DownloadOutlined />
                                <Text type="secondary">{plugin.downloadCount || 0}</Text>
                              </Space>
                              <Space>
                                <StarOutlined />
                                <Text type="secondary">{plugin.starCount || 0}</Text>
                              </Space>
                              <Space>
                                <LikeOutlined />
                                <Text type="secondary">{plugin.likeCount || 0}</Text>
                              </Space>
                            </div>
                            <div style={{ marginTop: '8px' }}>
                              <Text type="secondary" style={{ fontSize: '12px' }}>
                                作者: {plugin.author || '未知'}
                              </Text>
                            </div>
                          </div>
                        }
                      />
                      {installingPlugin && (
                        <div style={{
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          right: 0,
                          bottom: 0,
                          backgroundColor: 'rgba(255, 255, 255, 0.8)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          borderRadius: '8px',
                          zIndex: 10
                        }}>
                          <Spin indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />} />
                        </div>
                      )}
                    </Card>
                  </Col>
                );
              })}
            </Row>

            {/* 分页 */}
            <div style={{ marginTop: '24px', textAlign: 'right' }}>
              <Pagination
                current={currentPage}
                pageSize={pageSize}
                total={total}
                showSizeChanger
                showQuickJumper
                showTotal={(total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`}
                pageSizeOptions={['12', '24', '48', '96']}
                onChange={(page, size) => {
                  setCurrentPage(page);
                  setPageSize(size);
                }}
                onShowSizeChange={(current, size) => {
                  setCurrentPage(1);
                  setPageSize(size);
                }}
              />
            </div>
          </>
        )}
      </Spin>

      {/* 详情弹窗 */}
      <StoreDetail
        visible={detailVisible}
        onCancel={() => setDetailVisible(false)}
        plugin={currentPlugin}
        onInstall={() => {
          if (currentPlugin) {
            handleInstall(currentPlugin);
            setDetailVisible(false);
          }
        }}
        installed={currentPlugin ? isInstalled(currentPlugin.pluginCode) : false}
      />
    </PageContainer>
  );
};

export default connect()(StoreList);
