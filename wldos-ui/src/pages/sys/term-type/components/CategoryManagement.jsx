import React, { useRef, useState, useEffect, useCallback, useMemo } from 'react';
import { Card, message, Button, Space, Tag, Drawer, Divider, Popconfirm, Select, Tooltip } from 'antd';
import { useIntl } from 'umi';
import useOperationalScope from '@/hooks/useOperationalScope';
import {
  buildTermListQueryScope,
  isTermAdminListScopeReady,
  mergeToolbarWriteScope,
  isPlatformScopeComId,
} from '@/utils/orgCreateUserDomain';
import {
  resolveTermScope,
  TERM_SCOPE_COLOR,
} from '@/pages/sys/category';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import CreateForm from './CreateForm';
import UpdateForm from './UpdateForm';
import {
  queryLayerCategory,
  queryPluginCategory,
  queryCategoryTreeByType,
} from '../../category/service';
import {
  queryTermPage,
  addTerm,
  updateTerm,
  deleteTerm,
  deleteTerms,
} from '../../term-type/service';
import ProDescriptions from '@ant-design/pro-descriptions';
import { FooterToolbar } from '@ant-design/pro-layout';

const CategoryManagement = ({ termType, onRefresh, onRowClick, isAdmin, currentUser }) => {
  const intl = useIntl();
  const {
    effectiveComId,
    targetDomainId,
    setTargetDomainId,
    onTargetComChange,
    comListArray,
    domainOptions,
    domainLabelMap,
    showDomainSelectInToolbar,
    listScopeReady,
  } = useOperationalScope({
    isAdmin,
    currentUser,
    intl,
    adminDefaultAxis: 'platform',
    allowPlatformScopeInToolbar: isAdmin,
  });
  const termListScopeReady = useMemo(
    () => isTermAdminListScopeReady(isAdmin, listScopeReady, effectiveComId, targetDomainId),
    [isAdmin, listScopeReady, effectiveComId, targetDomainId],
  );
  const listScopeParams = useMemo(
    () => buildTermListQueryScope({ isAdmin, effectiveComId, targetDomainId }),
    [isAdmin, effectiveComId, targetDomainId],
  );

  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const [parentId, setParentId] = useState('0'); // 独立的 parentId 状态，用于 CreateForm
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [categoryTree, setCategoryTree] = useState([]);
  const [pageSize, setPageSize] = useState(15);
  const [isMobile, setIsMobile] = useState(
    typeof window !== 'undefined' ? (window.innerWidth || document.documentElement.clientWidth) < 768 : false
  );
  const tableWrapRef = useRef(null);
  const [containerWidth, setContainerWidth] = useState(0);

  useDesktopSticky(actionRef);

  // 加载分类树（用于父分类选择）
  const loadCategoryTree = useCallback(async () => {
    if (!termType || !termType.code) {
      setCategoryTree([]);
      return;
    }
    if (termType.structureType !== 'tree') {
      setCategoryTree([]);
      return;
    }
    if (isAdmin && !listScopeParams) {
      setCategoryTree([]);
      return;
    }
    const treeParams = listScopeParams || {};
    try {
      let res;
      if (termType.code === 'category') {
        res = await queryLayerCategory(treeParams);
      } else {
        res = await queryCategoryTreeByType(termType.code, treeParams);
      }
      if (res?.data) {
        setCategoryTree(res.data);
      }
    } catch (error) {
      console.error('加载分类树失败:', error);
    }
  }, [termType, isAdmin, listScopeParams]);

  useEffect(() => {
    if (!termType?.code || !termListScopeReady) {
      return;
    }
    loadCategoryTree();
    actionRef.current?.reload?.();
  }, [termType?.code, termListScopeReady, effectiveComId, targetDomainId, loadCategoryTree]);

  // 监听窗口大小变化
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 768);
      if (tableWrapRef.current) {
        setContainerWidth(tableWrapRef.current.offsetWidth);
      }
    };
    window.addEventListener('resize', handleResize);
    handleResize();
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const columns = [
    {
      title: '分类名称',
      dataIndex: 'name',
      fixed: isMobile ? undefined : 'left',
      width: 180,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '名称为必填项',
          },
          {
            max: 50,
            type: 'string',
            message: '最多50个字',
          },
        ],
      },
      render: (dom, entity) => {
        return (
          <a
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              if (onRowClick) {
                onRowClick(entity);
              }
            }}
            style={{ cursor: 'pointer' }}
          >
            {dom}
          </a>
        );
      },
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      hideInSearch: true,
      hideInForm: true,
      sorter: true,
      width: 120,
    },
    {
      title: '分类别名',
      dataIndex: 'slug',
      width: 220,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '分类别名为必填项',
          },
          {
            max: 200,
            type: 'string',
            message: '最大200个字符',
          },
        ],
      },
    },
    {
      title: '状态',
      dataIndex: 'isValid',
      width: 100,
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: '无效',
          status: 'invalid',
        },
        '1': {
          text: '有效',
          status: 'valid',
        },
      },
    },
    {
      title: '分类描述',
      dataIndex: 'description',
      valueType: 'textarea',
      width: 260,
      formItemProps: {
        rules: [
          {
            max: 200,
            type: 'string',
            message: '最多200个字',
          },
        ],
      },
    },
    {
      title: '关联内容数',
      dataIndex: 'count',
      width: 120,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '范围',
      dataIndex: 'domainId',
      width: 140,
      hideInForm: true,
      hideInSearch: true,
      render: (_, record) => {
        const scope = resolveTermScope(record, domainOptions, domainLabelMap);
        return <Tag color={TERM_SCOPE_COLOR[scope.level]}>{scope.text}</Tag>;
      },
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: isMobile ? undefined : 'right',
      width: termType.structureType === 'tree' ? 200 : 120,
      render: (_, record) => {
        const writable = record.writable !== false;
        const handleDelete = async () => {
          if (record.children && record.children.length > 0) {
            message.info('存在子节点，请先删除子节点');
            return;
          }
          const hide = message.loading('正在删除');
          try {
            await deleteTerm({
              id: record.id,
              termTypeId: record.termTypeId,
            });
            hide();
            message.success('删除成功，即将刷新');
            if (actionRef.current) {
              actionRef.current.reload();
            }
            loadCategoryTree();
            if (onRefresh) {
              onRefresh();
            }
          } catch (error) {
            hide();
            message.error('删除失败，请重试');
          }
        };

        const configLink = writable ? (
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            配置
          </a>
        ) : (
          <Tooltip title="平台/租户共享数据，仅归属层级可改，本站只读">
            <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>配置</span>
          </Tooltip>
        );
        const deleteLink = writable ? (
          <Popconfirm
            title={`您确定要删除[${record.count > 0 ? '分类下存在内容' : ''}]？`}
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={handleDelete}
          >
            <a>删除</a>
          </Popconfirm>
        ) : (
          <Tooltip title="平台/租户共享数据，仅归属层级可改，本站只读">
            <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>删除</span>
          </Tooltip>
        );

        if (termType.structureType === 'tree') {
          return (
            <>
              <a
                onClick={() => {
                  setParentId(record.id);
                  handleModalVisible(true);
                }}
              >
                子级
              </a>
              <Divider type="vertical" />
              <a
                onClick={() => {
                  setParentId(record.parentId || '0');
                  handleModalVisible(true);
                }}
              >
                同级
              </a>
              <Divider type="vertical" />
              {configLink}
              <Divider type="vertical" />
              {deleteLink}
            </>
          );
        }
        return (
          <>
            {configLink}
            <Divider type="vertical" />
            {deleteLink}
          </>
        );
      },
    },
  ];

  const hideReferenceCount = Boolean(termType?.hideReferenceCount);
  const tableColumns = hideReferenceCount
    ? columns.filter((col) => col.dataIndex !== 'count')
    : columns;

  // 计算列总宽度
  const totalColsWidth = tableColumns.reduce((sum, col) => sum + (col.width || 100), 0);
  const scrollX = isMobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  // 如果 termType 不存在，显示提示
  if (!termType || !termType.code) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          请选择一个分类类型
        </div>
      </Card>
    );
  }

  return (
    <Card
      title={
        <Space>
          <span>{termType.name} - 分类项管理</span>
          <Tag color={termType.structureType === 'tree' ? 'blue' : 'green'}>
            {termType.structureType === 'tree' ? '树形结构' : '扁平结构'}
          </Tag>
        </Space>
      }
      bodyStyle={{ padding: '16px' }}
    >
      <div ref={tableWrapRef}>
        <ProTableX
          headerTitle={`${termType.name}分类项列表`}
          actionRef={actionRef}
          rowKey="id"
          manualRequest={!termListScopeReady}
          toolBarRender={() => [
            isAdmin && comListArray.length > 0 ? (
              <Space key="com-select" size={8}>
                <span>目标租户</span>
                <Select
                  style={{ minWidth: 180 }}
                  allowClear
                  placeholder="选择租户"
                  options={comListArray}
                  value={effectiveComId}
                  onChange={onTargetComChange}
                  showSearch
                  optionFilterProp="label"
                />
              </Space>
            ) : null,
            isAdmin && !isPlatformScopeComId(effectiveComId) && (showDomainSelectInToolbar || domainOptions.length === 1) ? (
              <Space key="domain-select" size={8}>
                <span>目标站点</span>
                <Select
                  style={{ minWidth: 200 }}
                  placeholder="选择站点"
                  options={domainOptions}
                  value={targetDomainId}
                  onChange={(value) => setTargetDomainId(value || undefined)}
                  showSearch
                  optionFilterProp="label"
                  disabled={domainOptions.length <= 1}
                />
              </Space>
            ) : null,
            <Button
              type="primary"
              key="button"
              icon={<PlusOutlined />}
              disabled={!termListScopeReady}
              onClick={() => {
                setParentId('0');
                handleModalVisible(true);
              }}
            >
              新建
            </Button>,
          ]}
          pagination={{
            pageSize,
            showSizeChanger: true,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            onShowSizeChange: (_, size) => setPageSize(size),
          }}
          tableLayout={isMobile ? undefined : 'fixed'}
          scroll={isMobile ? undefined : { x: scrollX }}
          options={{ density: true, fullScreen: !isMobile, setting: true, reload: true }}
          search={{
            labelWidth: 120,
          }}
          request={async (params, sorter, filter) => {
            if (!termListScopeReady) {
              return { total: 0, data: [], success: true };
            }
            const scope = listScopeParams || {};
            const res = await queryTermPage({
              ...params,
              ...scope,
              classType: termType.code,
              sorter: { ...sorter, displayOrder: 'ascend' },
              filter,
            });
            return {
              total: res?.data?.total || 0,
              data: res?.data?.rows || [],
              success: true,
            };
          }}
          columns={tableColumns}
          rowSelection={{
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          }}
        />
      </div>

      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择 <a style={{ fontWeight: 600 }}>{selectedRowsState.length}</a> 项&nbsp;&nbsp;
            </div>
          }
        >
          <Button
            onClick={async () => {
              const hide = message.loading('正在删除');
              try {
                await deleteTerms(
                  selectedRowsState.map((row) => ({
                    id: row.id,
                    termTypeId: row.termTypeId,
                  }))
                );
                hide();
                message.success('删除成功，即将刷新');
                setSelectedRows([]);
                actionRef.current?.reload();
              } catch (error) {
                hide();
                message.error('删除失败，请重试');
              }
            }}
          >
            批量删除
          </Button>
        </FooterToolbar>
      )}

      {createModalVisible && (
        <CreateForm
          onSubmit={async (value) => {
            const hide = message.loading('正在添加');
            try {
              const res = await addTerm({
                ...mergeToolbarWriteScope(value, { effectiveComId, targetDomainId }),
                classType: termType.code,
              });
              hide();
              if (res?.data !== 'ok') {
                message.error(res.data || '添加失败');
                return;
              }
              message.success('添加成功');
              handleModalVisible(false);
              setParentId('0'); // 重置为根目录
              if (actionRef.current) {
                actionRef.current.reload();
              }
              loadCategoryTree();
              if (onRefresh) {
                onRefresh();
              }
            } catch (error) {
              hide();
              message.error('添加失败请重试！');
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setParentId('0'); // 重置为根目录
          }}
          modalVisible={createModalVisible}
          categoryList={categoryTree}
          parentId={parentId}
          structureType={termType.structureType}
          termType={termType}
        />
      )}

      {stepFormValues && Object.keys(stepFormValues).length ? (
        <UpdateForm
          onSubmit={async (value) => {
            const hide = message.loading('正在配置');
            try {
              const res = await updateTerm({
                name: value.name,
                displayOrder: value.displayOrder,
                slug: value.slug,
                id: value.id,
                parentId: value.parentId,
                termTypeId: value.termTypeId,
                isValid: value.isValid,
                description: value.description,
                infoFlag: value.infoFlag,
                classType: termType.code, // 确保传递类型编码
              });
              hide();
              if (res?.data !== 'ok') {
                message.error(res.data || '配置失败');
                return;
              }
              message.success('配置成功');
              handleUpdateModalVisible(false);
              setStepFormValues({});
              if (actionRef.current) {
                actionRef.current.reload();
              }
              loadCategoryTree();
              if (onRefresh) {
                onRefresh();
              }
            } catch (error) {
              hide();
              message.error('配置失败请重试！');
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setStepFormValues({});
          }}
          updateModalVisible={updateModalVisible}
          values={stepFormValues}
          categoryList={categoryTree}
          structureType={termType.structureType}
          termType={termType}
        />
      ) : null}

    </Card>
  );
};

// Drawer 需要在 Card 外面，使用 React Fragment 包裹
const CategoryManagementWithDrawer = ({ termType, onRefresh, isAdmin, currentUser }) => {
  const [row, setRow] = useState();
  const hideReferenceCount = Boolean(termType?.hideReferenceCount);

  return (
    <>
      <CategoryManagement
        termType={termType}
        onRefresh={onRefresh}
        onRowClick={setRow}
        isAdmin={isAdmin}
        currentUser={currentUser}
      />
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={true}
        placement="right"
      >
        {row?.name && (
          <ProDescriptions
            column={2}
            title={row?.name}
            request={async () => {
              return Promise.resolve({
                data: row || {},
              });
            }}
            params={{
              id: row?.name,
            }}
            columns={[
              {
                title: '分类名称',
                dataIndex: 'name',
              },
              {
                title: '展示顺序',
                dataIndex: 'displayOrder',
              },
              {
                title: '分类别名',
                dataIndex: 'slug',
              },
              {
                title: '状态',
                dataIndex: 'isValid',
                valueEnum: {
                  '0': { text: '无效', status: 'invalid' },
                  '1': { text: '有效', status: 'valid' },
                },
              },
              {
                title: '分类描述',
                dataIndex: 'description',
                span: 2,
              },
              ...(hideReferenceCount
                ? []
                : [
                    {
                      title: '关联内容数',
                      dataIndex: 'count',
                    },
                  ]),
            ]}
          />
        )}
      </Drawer>
    </>
  );
};

export default CategoryManagementWithDrawer;

