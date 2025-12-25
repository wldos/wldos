import React, { useState, useEffect, useRef } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import {Button, Tag, Space, Modal, message, Tooltip, Badge, Popconfirm, Drawer} from 'antd';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {
  PlusOutlined,
  EyeOutlined,
  DeleteOutlined,
  DownloadOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  ReloadOutlined,
  UploadOutlined
} from '@ant-design/icons';
import { connect } from 'umi';
import PluginDetail from './components/PluginDetail';
import UploadPlugin from './components/UploadPlugin';
import { queryPlugins, installPlugin, uninstallPlugin, enablePlugin, disablePlugin, updatePlugin } from './service';
import { fetchEnumPluginStatus, fetchEnumAutoStart } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';
import ProDescriptions from "@ant-design/pro-descriptions";

const PluginList = ({ dispatch }) => {
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentPlugin, setCurrentPlugin] = useState(null);
  const [row, setRow] = useState();
  const [loading, setLoading] = useState(false);
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [pluginStatusEnum, setPluginStatusEnum] = useState({});
  const [autoStartEnum, setAutoStartEnum] = useState({});
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

  // 获取枚举数据
  useEffect(async () => {
    try {
      const [statusRes, autoStartRes] = await Promise.all([
        fetchEnumPluginStatus(),
        fetchEnumAutoStart()
      ]);

      if (statusRes?.data) {
        setPluginStatusEnum(selectToEnum(statusRes.data));
      }

      if (autoStartRes?.data) {
        setAutoStartEnum(selectToEnum(autoStartRes.data));
      }
    } catch (error) {
      console.error('获取枚举数据失败:', error);
    }
  }, []);

  // 监听容器宽度变化
  useEffect(() => {
    if (!containerRef.current) return;
    
    const updateWidth = () => {
      if (containerRef.current) {
        setContainerWidth(containerRef.current.offsetWidth);
      }
    };
    
    updateWidth();
    const resizeObserver = new ResizeObserver(updateWidth);
    resizeObserver.observe(containerRef.current);
    
    return () => resizeObserver.disconnect();
  }, []);

  // 计算列总宽度
  const totalColsWidth = 1200; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  // 表格列配置
  const columns = [
    {
      title: '插件编码',
      dataIndex: 'pluginCode',
      key: 'pluginCode',
      width: 120,
      ellipsis: true,
      fixed: mobile ? undefined : 'left',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '插件名称',
      dataIndex: 'pluginName',
      key: 'pluginName',
      width: 150,
      ellipsis: true,
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 80,
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 100,
    },
    {
      title: '主类',
      dataIndex: 'mainClass',
      key: 'mainClass',
      width: 200,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'pluginStatus',
      width: 100,
      filters: true,
      onFilter: false,
      valueEnum: pluginStatusEnum,
      render: (dom, entity) => {
        const statusMap = {
          'INSTALLED': { text: '已安装', color: 'blue' },
          'ENABLED': { text: '已启用', color: 'green' },
          'DISABLED': { text: '已禁用', color: 'orange' },
          'ERROR': { text: '错误', color: 'red' },
        };
        const config = statusMap[entity.pluginStatus] || { text: '未知', color: 'default' };
        return <Badge status={config.color} text={config.text} />;
      },
    },
    {
      title: '自动启动',
      dataIndex: 'autoStart',
      width: 100,
      filters: true,
      onFilter: false,
      valueEnum: autoStartEnum,
      render: (entity) => (
        <Badge
          status={entity.autoStart === 'true' ? 'success' : 'default'}
          text={entity.autoStart === 'true' ? '是' : '否'}
        />
      ),
    },
    {
      title: '错误信息',
      dataIndex: 'errorMessage',
      key: 'errorMessage',
      width: 150,
      ellipsis: true,
      render: (_, record) => {
        // 直接使用 record 中的 errorMessage
        const actualErrorMessage = record?.errorMessage;
        // 简单直接的判断：如果 errorMessage 为空、null、undefined 或空字符串，就显示 '-'
        if (!actualErrorMessage || actualErrorMessage === '') {
          return '-';
        }
        // 如果是字符串类型的 'null' 或 'undefined'，也显示 '-'
        if (typeof actualErrorMessage === 'string' &&
            (actualErrorMessage === 'null' || actualErrorMessage === 'undefined' || actualErrorMessage.trim() === '')) {
          return '-';
        }

        return (
            <Tooltip title={actualErrorMessage} color="dark">
              <span style={{
                color: '#ff4d4f'
              }}>有错误</span>
            </Tooltip>
        );
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 150,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      key: 'action',
      width: 280,
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="查看详情">
            <Button
              type="link"
              size="small"
              icon={<EyeOutlined />}
              onClick={() => handleView(record)}
            />
          </Tooltip>

          {record.pluginStatus === 'ENABLED' ? (
            <Popconfirm
              title="确定要禁用这个插件吗？"
              description="禁用后插件将停止运行，但不会卸载。"
              onConfirm={() => handleDisable(record)}
              okText="确定"
              cancelText="取消"
            >
              <Tooltip title="禁用插件">
                <Button
                  type="link"
                  size="small"
                  icon={<PauseCircleOutlined />}
                />
              </Tooltip>
            </Popconfirm>
          ) : (
            <Popconfirm
              title="确定要启用这个插件吗？"
              description="启用后插件将开始运行。"
              onConfirm={() => handleEnable(record)}
              okText="确定"
              cancelText="取消"
            >
              <Tooltip title="启用插件">
                <Button
                  type="link"
                  size="small"
                  icon={<PlayCircleOutlined />}
                />
              </Tooltip>
            </Popconfirm>
          )}

          {/* 只在检测到更新时显示更新按钮 */}
          {record.hasUpdate && (
            <Popconfirm
              title="确定要更新这个插件吗？"
              description="更新过程中插件将暂时不可用，请确认是否继续？"
              onConfirm={() => handleUpdate(record)}
              okText="确定"
              cancelText="取消"
            >
              <Tooltip title="更新插件">
                <Button
                  type="link"
                  size="small"
                  icon={<ReloadOutlined />}
                  style={{ color: '#1890ff' }}
                />
              </Tooltip>
            </Popconfirm>
          )}

          <Popconfirm
            title={
              <div>
                <div style={{ marginBottom: 8, fontWeight: 'bold' }}>确定要卸载这个插件吗？</div>
                <div style={{ fontSize: 12, color: '#666' }}>
                  {record.pluginStatus === 'ENABLED'
                    ? '该插件当前已启用，卸载时会自动停止插件并删除所有相关数据。此操作不可恢复，请谨慎操作。'
                    : '卸载后将删除插件的所有相关数据。此操作不可恢复，请谨慎操作。'}
                </div>
              </div>
            }
            onConfirm={() => handleUninstall(record)}
            okText="确定"
            cancelText="取消"
          >
            <Tooltip title="卸载插件">
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 查看详情
  const handleView = (record) => {

    setCurrentPlugin(record);
    setDetailVisible(true);
  };

  // 启用插件
  const handleEnable = async (record) => {
    try {
      const response = await enablePlugin(record.pluginCode);
      if (response?.data === 'ok') {
        message.success('插件启用成功');
        // 刷新插件清单（启用/禁用不影响 manifest，但为了保持数据一致性，也刷新）
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        if (actionRef.current) {
          actionRef.current.reload();
        }
      } else {
        message.error('插件启用失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      message.error('插件启用失败: ' + (error.message || '网络错误'));
    }
  };

  // 禁用插件
  const handleDisable = async (record) => {
    try {
      const response = await disablePlugin(record.pluginCode);
      if (response?.data === 'ok') {
        message.success('插件禁用成功');
        // 刷新插件清单（启用/禁用不影响 manifest，但为了保持数据一致性，也刷新）
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        if (actionRef.current) {
          actionRef.current.reload();
        }
      } else {
        message.error('插件禁用失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      message.error('插件禁用失败: ' + (error.message || '网络错误'));
    }
  };

  // 更新插件
  const handleUpdate = async (record) => {
    try {
      const response = await updatePlugin(record.pluginCode);
      if (response?.data === 'ok') {
        message.success('插件更新成功');
        // 刷新插件清单（更新会修改 manifest）
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        if (actionRef.current) {
          actionRef.current.reload();
        }
      } else {
        message.error('插件更新失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      message.error('插件更新失败: ' + (error.message || '网络错误'));
    }
  };

  // 卸载插件
  const handleUninstall = async (record) => {
    try {
      const response = await uninstallPlugin(record.pluginCode);
      if (response?.data === 'ok') {
        message.success('插件卸载成功');
        // 刷新插件清单（卸载会修改 manifest）
        if (dispatch) {
          dispatch({ type: 'user/refreshPluginManifest' });
        }
        if (actionRef.current) {
          actionRef.current.reload();
        }
      } else {
        message.error('插件卸载失败: ' + (response?.message || '未知错误'));
      }
    } catch (error) {
      message.error('插件卸载失败: ' + (error.message || '网络错误'));
    }
  };

  const actionRef = React.useRef();

  return (
    <PageContainer
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
          headerTitle="插件管理"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button
              type="primary"
              key="upload"
              icon={<UploadOutlined />}
              onClick={() => setUploadModalVisible(true)}
            >
              安装插件
            </Button>,
          ]}
          request={async (params, sorter, filter) => {
            const res = await queryPlugins({
              ...params,
              sorter,
              filter
            });

            if (res?.data?.rows && res.data.rows.length > 0) {
              res.data.rows.forEach((plugin, index) => {
                console.log(`插件${index + 1}:`, {
                  pluginCode: plugin.pluginCode,
                  pluginName: plugin.pluginName,
                  errorMessage: plugin.errorMessage,
                  errorMessageType: typeof plugin.errorMessage,
                  pluginStatus: plugin.pluginStatus
                });
              });
            }

            return Promise.resolve({
              total: res?.data?.total || 0,
              data: res?.data?.rows || [],
              success: true,
            });
          }
          }
          columns={columns}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
          }}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
        />
      </div>

      {/* 详情弹窗 */}
      <PluginDetail
        visible={detailVisible}
        onCancel={() => setDetailVisible(false)}
        plugin={currentPlugin}
      />

      {/* 上传插件弹窗 */}
      <UploadPlugin
        visible={uploadModalVisible}
        onCancel={() => setUploadModalVisible(false)}
        onSuccess={() => {
          setUploadModalVisible(false);
          // 刷新插件清单（安装会修改 manifest）
          if (dispatch) {
            dispatch({ type: 'user/refreshPluginManifest' });
          }
          if (actionRef.current) {
            actionRef.current.reload();
          }
        }}
      />

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.pluginName && (
          <ProDescriptions
            column={2}
            title={row?.pluginName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.pluginName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default connect()(PluginList);
