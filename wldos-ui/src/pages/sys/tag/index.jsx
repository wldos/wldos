import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm, Select, Space, Tag, Tooltip} from 'antd';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {connect, useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import useOperationalScope from '@/hooks/useOperationalScope';
import {
  addEntity,
  queryPage,
} from './service';
import {
  handleRemove,
  handleRemoveOne,
  handleUpdate,
  resolveTermScope,
  TERM_SCOPE_COLOR,
} from "@/pages/sys/category";
import {
  mergeToolbarWriteScope,
  buildTermListQueryScope,
  isTermAdminListScopeReady,
  isPlatformScopeComId,
} from '@/utils/orgCreateUserDomain';

/**
 * 新增标签。不可复用 sys/category 的 handleAdd——那个函数内部固定调用
 * category/service.js 的 addEntity（POST /admin/cms/category/add），
 * 后端该接口会强制把 classType 落成 category，导致标签被存成分类，标签列表永远看不到。
 * @param fields
 */
const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');

  try {
    const res = await addEntity({...fields});
    hide();
    if (res?.code !== 200 || (res?.data && res.data !== 'ok')) {
      message.error(res?.message || res?.data || '添加失败');
      return false;
    }
    message.success('添加成功');
    return true;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }
};

const TagList = ({ isAdmin, currentUser }) => {
  const intl = useIntl();
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [parentId, setParentId] = useState('0');
  const [isMobile, setIsMobile] = useState(typeof window !== 'undefined' ? (window.innerWidth || document.documentElement.clientWidth) < 768 : false);
  const tableWrapRef = useRef(null);
  const [containerWidth, setContainerWidth] = useState(0);

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

  useEffect(() => {
    if (!termListScopeReady) {
      return;
    }
    actionRef.current?.reload?.();
  }, [termListScopeReady, effectiveComId, targetDomainId]);

  useDesktopSticky(actionRef);

  const columns = [
    {
      title: '标签名称',
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
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '标签别名',
      dataIndex: 'slug',
      width: 220,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '标签别名为必填项',
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
      title: '标签描述',
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
      }
    },
    {
      title: '关联内容数',
      dataIndex: 'count',
      width: 120,
      hideInForm: true,
      hideInSearch: true
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
      width: 200,
      render: (_, record) => {
        const writable = record.writable !== false;
        return (
        <>
          {writable ? (
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
          )}
          <Divider type="vertical"/>
          {writable ? (
          <Popconfirm title={`您确定要删除[${record.count > 0 ? '标签下存在内容' : ''}]？`} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemoveOne(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>删除</a>
          </Popconfirm>
          ) : (
          <Tooltip title="平台/租户共享数据，仅归属层级可改，本站只读">
            <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>删除</span>
          </Tooltip>
          )}
        </>
        );
      },
    },
  ];

  // 监听容器宽度，仅在总列宽 > 容器宽时横向滚动
  React.useEffect(() => {
    if (isMobile) return;
    const el = tableWrapRef.current;
    if (!el || typeof ResizeObserver === 'undefined') return;
    const ro = new ResizeObserver((entries) => {
      const w = entries?.[0]?.contentRect?.width || el.clientWidth || 0;
      setContainerWidth(w);
    });
    ro.observe(el);
    return () => ro.disconnect();
  }, [isMobile]);
  const totalColsWidth = columns.reduce((sum, col) => sum + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = isMobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={tableWrapRef}>
      <ProTableX
        headerTitle="标签列表"
        actionRef={actionRef}
        rowKey="id"
        manualRequest={!termListScopeReady}
        tableLayout={isMobile ? undefined : 'fixed'}
        scroll={isMobile ? undefined : { x: scrollX }}
        options={{ density: true, fullScreen: !isMobile, setting: true, reload: true }}
        pagination={{
          defaultPageSize: 15,
          pageSizeOptions: ['10', '15', '20', '30', '50', '100'],
          showSizeChanger: true,
        }}
        search={{
          labelWidth: 120,
        }}
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
          <Button key={0} type="primary" disabled={!termListScopeReady} onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> 新建
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          if (!termListScopeReady) {
            return { total: 0, data: [], success: true };
          }
          const scope = listScopeParams || {};
          const res = await queryPage({
            ...params,
            sorter: { ...sorter, displayOrder: 'ascend' },
            filter,
            ...scope,
          });
          return Promise.resolve({
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: true,
          });
        }
        }
        columns={(isMobile ? columns : columns.map(c => ({
          ...c,
          onHeaderCell: () => ({ style: { whiteSpace: 'nowrap' } })
        })))}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
        }}
      />
      </div>
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项&nbsp;&nbsp;
              <span>
                管理内容项总计 {selectedRowsState.reduce((pre, item) => pre + item.count, 0)} 条
              </span>
            </div>
          }
        >
          <Popconfirm title="您确定要删除[已关联内容的标签将被释放]？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      {createModalVisible && <CreateForm
        onSubmit={async (value) => {
          const success = await handleAdd(mergeToolbarWriteScope(value, { effectiveComId, targetDomainId }));

          if (success) {
            handleModalVisible(false);
            setParentId('0');
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleModalVisible(false);
          setParentId('0');
        }}
        modalVisible={createModalVisible}
        parentId={parentId}
      />}
      {stepFormValues && Object.keys(stepFormValues).length ? (
        <UpdateForm
          onSubmit={async (value) => {
            const success = await handleUpdate(value);

            if (success) {
              handleUpdateModalVisible(false);
              setStepFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setStepFormValues({});
          }}
          updateModalVisible={updateModalVisible}
          values={stepFormValues}
        />
      ) : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions
            column={2}
            title={row?.name}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.name,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default connect(({ user }) => ({
  isAdmin: !!user.sessionFlags?.isAdmin,
  currentUser: user.currentUser,
}))(TagList);
