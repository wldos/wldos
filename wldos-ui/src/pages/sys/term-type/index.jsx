import React, { useEffect, useState, useMemo } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Card, List, Typography, Space, Tag, Empty, message, Button, Modal, Form, Input, Select, Pagination } from 'antd';
import {
  FolderOutlined,
  TagOutlined,
  AppstoreOutlined,
  SettingOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { queryTermTypes, saveTermType, deleteTermType } from './service';
import CategoryManagement from './components/CategoryManagement';
import styles from './index.less';
import { useIntl } from 'umi';

const { Title, Text } = Typography;

// 图标映射
const iconMap = {
  FolderOutlined: FolderOutlined,
  TagOutlined: TagOutlined,
  AppstoreOutlined: AppstoreOutlined,
  SettingOutlined: SettingOutlined,
};

const TermTypeManagement = () => {
  const [termTypes, setTermTypes] = useState([]);
  const [selectedType, setSelectedType] = useState(null);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingType, setEditingType] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const intl = useIntl();

  // 加载分类类型列表
  const loadTermTypes = async () => {
    setLoading(true);
    try {
      const res = await queryTermTypes();
      if (res?.data) {
        setTermTypes(res.data);
        // 默认选中第一个
        if (res.data.length > 0 && !selectedType) {
          setSelectedType(res.data[0]);
        }
        return res.data;
      }
      return [];
    } catch (error) {
      message.error(intl.formatMessage({ id: 'sys.termType.loadFailed', defaultMessage: '加载分类类型失败' }));
      return [];
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTermTypes();
  }, []);

  // 获取图标组件
  const getIcon = (iconName) => {
    const IconComponent = iconMap[iconName] || SettingOutlined;
    return <IconComponent />;
  };

  // 获取结构类型标签
  const getStructureTypeTag = (structureType) => {
    if (structureType === 'tree') {
      return <Tag color="blue">{intl.formatMessage({ id: 'sys.termType.structureTypeTree', defaultMessage: '树形' })}</Tag>;
    } else if (structureType === 'flat') {
      return <Tag color="green">{intl.formatMessage({ id: 'sys.termType.structureTypeFlat', defaultMessage: '扁平' })}</Tag>;
    }
    return null;
  };

  // 过滤和分页处理
  const filteredAndPaginatedTypes = useMemo(() => {
    let filtered = termTypes;
    
    // 搜索过滤
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.trim().toLowerCase();
      filtered = termTypes.filter(item => 
        item.name?.toLowerCase().includes(keyword) ||
        item.code?.toLowerCase().includes(keyword) ||
        item.description?.toLowerCase().includes(keyword)
      );
    }
    
    // 分页
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    return {
      data: filtered.slice(start, end),
      total: filtered.length,
    };
  }, [termTypes, searchKeyword, currentPage, pageSize]);

  // 搜索关键词变化时重置到第一页
  useEffect(() => {
    setCurrentPage(1);
  }, [searchKeyword]);

  return (
    <PageContainer
      style={{
        padding: '0',
        margin: '0',
      }}
      bodyStyle={{
        padding: '24px',
        margin: '0',
      }}
    >
      <div className={styles.container}>
        {/* 左侧：类型列表 */}
        <div className={styles.typeList}>
          <Card
            title={intl.formatMessage({ id: 'sys.termType.typeListTitle', defaultMessage: '分类类型' })}
            size="small"
            extra={
              <Space size={8} style={{ flexWrap: 'nowrap' }}>
                <Input
                  size="small"
                  placeholder={intl.formatMessage({ id: 'sys.termType.searchPlaceholder', defaultMessage: '搜索类型名称、编码或描述' })}
                  prefix={<SearchOutlined />}
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  allowClear
                  style={{ width: 150, flexShrink: 1 }}
                />
                <Button
                  type="primary"
                  size="small"
                  onClick={() => {
                    setEditingType(null);
                    form.resetFields();
                    setModalVisible(true);
                  }}
                >
                  {intl.formatMessage({ id: 'sys.termType.add', defaultMessage: '新增' })}
                </Button>
                <Button
                  size="small"
                  icon={<ReloadOutlined />}
                  onClick={() => {
                    loadTermTypes();
                  }}
                >
                  {intl.formatMessage({ id: 'sys.termType.refresh', defaultMessage: '刷新' })}
                </Button>
              </Space>
            }
          >
            <List
              loading={loading}
              dataSource={filteredAndPaginatedTypes.data}
              style={{ minHeight: 'calc(100vh - 400px)' }}
              renderItem={(item) => {
                const isSelected = selectedType?.code === item.code;
                return (
                  <List.Item
                    className={styles.typeItem}
                    style={{
                      backgroundColor: isSelected ? '#e6f7ff' : 'transparent',
                      cursor: 'pointer',
                      paddingLeft: '12px',
                    }}
                    onClick={() => setSelectedType(item)}
                    actions={[
                      !item.isSystem && (
                        <Button
                          type="link"
                          size="small"
                          onClick={(e) => {
                            e.stopPropagation();
                            setEditingType(item);
                            form.setFieldsValue({
                              code: item.code,
                              name: item.name,
                              description: item.description,
                              structureType: item.structureType,
                              icon: item.icon,
                            });
                            setModalVisible(true);
                          }}
                        >
                          {intl.formatMessage({ id: 'sys.termType.edit', defaultMessage: '编辑' })}
                        </Button>
                      ),
                      !item.isSystem && (
                        <Button
                          type="link"
                          size="small"
                          danger
                          onClick={async (e) => {
                            e.stopPropagation();
                            Modal.confirm({
                              title: intl.formatMessage({ id: 'sys.termType.confirmDeleteTitle', defaultMessage: '确认删除' }),
                              content: intl.formatMessage(
                                { id: 'sys.termType.confirmDeleteContent', defaultMessage: '确定要删除分类类型"{name}"吗？' },
                                { name: item.name },
                              ),
                              onOk: async () => {
                                try {
                                  const res = await deleteTermType(item.code);
                                  if (res?.data === 'ok') {
                                    message.success(intl.formatMessage({ id: 'sys.termType.deleteSuccess', defaultMessage: '删除成功' }));
                                    loadTermTypes();
                                    if (selectedType?.code === item.code) {
                                      setSelectedType(null);
                                    }
                                  } else {
                                    message.error(res?.message || intl.formatMessage({ id: 'sys.termType.deleteFailed', defaultMessage: '删除失败' }));
                                  }
                                } catch (error) {
                                  message.error(
                                    intl.formatMessage(
                                      { id: 'sys.termType.deleteFailedWithReason', defaultMessage: '删除失败：{reason}' },
                                      {
                                        reason:
                                          error.message ||
                                          intl.formatMessage({ id: 'sys.termType.unknownError', defaultMessage: '未知错误' }),
                                      },
                                    ),
                                  );
                                }
                              },
                            });
                          }}
                        >
                          {intl.formatMessage({ id: 'sys.termType.delete', defaultMessage: '删除' })}
                        </Button>
                      ),
                    ].filter(Boolean)}
                  >
                    <List.Item.Meta
                      avatar={
                        <div style={{ fontSize: 20, color: isSelected ? '#1890ff' : '#666' }}>
                          {getIcon(item.icon)}
                        </div>
                      }
                      title={
                        <Space>
                          <Text strong={isSelected}>{item.name}</Text>
                          {item.isSystem && <Tag color="red">{intl.formatMessage({ id: 'sys.termType.system', defaultMessage: '系统' })}</Tag>}
                        </Space>
                      }
                      description={
                        <Space direction="vertical" size={4} style={{ width: '100%' }}>
                          <Text 
                            type="secondary" 
                            style={{ 
                              fontSize: 12,
                              wordBreak: 'break-word',
                              overflowWrap: 'break-word',
                            }}
                            ellipsis={{ tooltip: item.description || intl.formatMessage({ id: 'sys.termType.noDescription', defaultMessage: '无描述' }) }}
                          >
                            {item.description || intl.formatMessage({ id: 'sys.termType.noDescription', defaultMessage: '无描述' })}
                          </Text>
                          <Space size={8} wrap style={{ alignItems: 'center' }}>
                            <Text type="secondary" style={{ fontSize: 12, whiteSpace: 'nowrap' }}>
                              {intl.formatMessage({ id: 'sys.termType.codePrefix', defaultMessage: '编码' })}: {item.code}
                            </Text>
                            {getStructureTypeTag(item.structureType)}
                            {item.existsInDb && (
                              <Tag color="success" style={{ fontSize: 11, margin: 0 }}>
                                {intl.formatMessage({ id: 'sys.termType.used', defaultMessage: '已使用' })}
                              </Tag>
                            )}
                          </Space>
                        </Space>
                      }
                    />
                  </List.Item>
                );
              }}
            />
            {filteredAndPaginatedTypes.total > 0 && (
              <div style={{ padding: '12px 0', textAlign: 'center', borderTop: '1px solid #f0f0f0' }}>
                <Pagination
                  size="small"
                  current={currentPage}
                  pageSize={pageSize}
                  total={filteredAndPaginatedTypes.total}
                  showSizeChanger
                  showQuickJumper
                  showTotal={(total) =>
                    intl.formatMessage(
                      { id: 'sys.termType.paginationTotal', defaultMessage: '共 {total} 条' },
                      { total },
                    )
                  }
                  pageSizeOptions={['5', '10', '15', '20']}
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
            )}
          </Card>
        </div>

        {/* 右侧：分类项管理 */}
        <div className={styles.categoryManagement}>
          {selectedType ? (
            <CategoryManagement
              key={selectedType.code}
              termType={selectedType}
              onRefresh={loadTermTypes}
            />
          ) : (
            <Card>
              <Empty description={intl.formatMessage({ id: 'sys.termType.chooseCategoryType', defaultMessage: '请选择一个分类类型' })} />
            </Card>
          )}
        </div>
      </div>

      {/* 新增/编辑分类类型弹窗 */}
      <Modal
        title={
          editingType
            ? intl.formatMessage({ id: 'sys.termType.modalTitleEdit', defaultMessage: '编辑分类类型' })
            : intl.formatMessage({ id: 'sys.termType.modalTitleCreate', defaultMessage: '新增分类类型' })
        }
        visible={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditingType(null);
          form.resetFields();
        }}
        onOk={async () => {
          try {
            const values = await form.validateFields();
            const res = await saveTermType(values);
            if (res?.data === 'ok') {
              message.success(
                editingType
                  ? intl.formatMessage({ id: 'sys.termType.saveSuccessEdit', defaultMessage: '编辑成功' })
                  : intl.formatMessage({ id: 'sys.termType.saveSuccessCreate', defaultMessage: '新增成功' }),
              );
              setModalVisible(false);
              const editingCode = editingType?.code;
              setEditingType(null);
              form.resetFields();
              // 刷新列表
              const updatedTypes = await loadTermTypes();
              // 如果编辑的是当前选中的类型，更新选中状态
              if (editingCode && selectedType?.code === editingCode) {
                const updatedType = updatedTypes.find(item => item.code === editingCode);
                if (updatedType) {
                  setSelectedType(updatedType);
                }
              }
            } else {
              message.error(res?.message || intl.formatMessage({ id: 'sys.termType.saveFailed', defaultMessage: '操作失败' }));
            }
          } catch (error) {
            console.error('保存失败:', error);
            message.error(
              intl.formatMessage(
                { id: 'sys.termType.saveFailedWithReason', defaultMessage: '保存失败：{reason}' },
                {
                  reason:
                    error.message ||
                    intl.formatMessage({ id: 'sys.termType.unknownError', defaultMessage: '未知错误' }),
                },
              ),
            );
          }
        }}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            structureType: 'tree',
            supportSort: '1',
            supportHierarchy: '1',
          }}
        >
          <Form.Item
            name="code"
            label={intl.formatMessage({ id: 'sys.termType.form.code', defaultMessage: '编码' })}
            rules={[
              { required: true, message: intl.formatMessage({ id: 'sys.termType.form.codeRequired', defaultMessage: '请输入编码' }) },
              { pattern: /^[a-z_]+$/, message: intl.formatMessage({ id: 'sys.termType.form.codePattern', defaultMessage: '编码只能包含小写字母和下划线' }) },
            ]}
          >
            <Input
              placeholder={intl.formatMessage({ id: 'sys.termType.form.codePlaceholder', defaultMessage: '请输入编码，如：custom_type' })}
              disabled={!!editingType}
            />
          </Form.Item>
          <Form.Item
            name="name"
            label={intl.formatMessage({ id: 'sys.termType.form.name', defaultMessage: '名称' })}
            rules={[{ required: true, message: intl.formatMessage({ id: 'sys.termType.form.nameRequired', defaultMessage: '请输入名称' }) }]}
          >
            <Input placeholder={intl.formatMessage({ id: 'sys.termType.form.namePlaceholder', defaultMessage: '请输入名称' })} />
          </Form.Item>
          <Form.Item
            name="description"
            label={intl.formatMessage({ id: 'sys.termType.form.description', defaultMessage: '描述' })}
          >
            <Input.TextArea
              rows={3}
              placeholder={intl.formatMessage({ id: 'sys.termType.form.descriptionPlaceholder', defaultMessage: '请输入描述' })}
            />
          </Form.Item>
          <Form.Item
            name="structureType"
            label={intl.formatMessage({ id: 'sys.termType.form.structureType', defaultMessage: '结构类型' })}
            rules={[{ required: true, message: intl.formatMessage({ id: 'sys.termType.form.structureTypeRequired', defaultMessage: '请选择结构类型' }) }]}
          >
            <Select>
              <Select.Option value="tree">{intl.formatMessage({ id: 'sys.termType.form.structureTypeTree', defaultMessage: '树形' })}</Select.Option>
              <Select.Option value="flat">{intl.formatMessage({ id: 'sys.termType.form.structureTypeFlat', defaultMessage: '扁平' })}</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="icon"
            label={intl.formatMessage({ id: 'sys.termType.form.icon', defaultMessage: '图标' })}
          >
            <Input placeholder={intl.formatMessage({ id: 'sys.termType.form.iconPlaceholder', defaultMessage: '请输入图标名称，如：FolderOutlined' })} />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default TermTypeManagement;

