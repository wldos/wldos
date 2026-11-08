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
  addEntity, infoFlag,
  queryLayerCategory,
  queryPage,
  removeEntity,
  removeEntities,
  updateEntity
} from './service';
import {
  mergeToolbarWriteScope,
  buildTermListQueryScope,
  isTermAdminListScopeReady,
  isPlatformScopeComId,
} from '@/utils/orgCreateUserDomain';

/**
 * 归属范围文案：与后端 AxisLayerGuard.resolveAxisLayer 同一判断顺序——domainId 落到具体站点才算「站点专属」，
 * 否则再看 comId 是否为平台（0）。分类、标签的列表都是平台共享/租户共享/站点专属三层的并集展示，
 * 不看这一列，光看名称分不出哪条是共享哪条是本站自己建的。
 * @param record 行数据（需带 comId、domainId）
 * @param domainOptions 当前已加载的站点选项（用于把 domainId 换成可读站名，找不到时退化为不带站名的泛称）
 * @param domainLabelMap 全量站点 id→名称（与工具栏筛选无关）
 */
export const resolveTermScope = (record, domainOptions, domainLabelMap) => {
  const domainId = record?.domainId;
  const hasDomain = domainId != null && String(domainId) !== '0';
  if (hasDomain) {
    const matched = domainOptions?.find((item) => String(item.value) === String(domainId));
    const label = matched?.label || domainLabelMap?.[String(domainId)];
    return { level: 'domain', text: label ? `站点专属：${label}` : '站点专属' };
  }
  const comId = record?.comId;
  if (comId == null || String(comId) === '0') {
    return { level: 'platform', text: '平台共享' };
  }
  return { level: 'tenant', text: '租户共享（全站）' };
};

export const TERM_SCOPE_COLOR = {
  platform: 'gold',
  tenant: 'blue',
  domain: 'default',
};

/**
 * 添加节点
 * @param fields
 */
export const handleAdd = async (fields) => {
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

/**
 * 更新节点
 * @param fields
 */
export const handleUpdate = async (fields) => {
  const hide = message.loading('正在配置');

  try {
    const res = await updateEntity({
      name: fields.name,
      displayOrder: fields.displayOrder,
      slug: fields.slug,
      id: fields.id,
      parentId: fields.parentId,
      termTypeId: fields.termTypeId,
      isValid: fields.isValid,
      description: fields.description,
      infoFlag: fields.infoFlag
    });
    hide();
    if (res?.code !== 200 || (res?.data && res.data !== 'ok')) {
      message.error(res?.message || res?.data || '配置失败');
      return false;
    }
    message.success('配置成功');
    return true;
  } catch (error) {
    hide();
    message.error('配置失败请重试！');
    return false;
  }
};

/**
 *  批量删除
 * @param selectedRows
 */
export const handleRemove = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeEntities(
      selectedRows.map((row) => {
        return {id: row.id, termTypeId: row.termTypeId,};
      }),
    );
    hide();
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};
/**
 *  删除节点
 */
export const handleRemoveOne = async (fields) => {
  if (!fields) return true;

  if (fields.children) {
    message.info("存在子节点，请先删除子节点");
    return true;
  }
  const hide = message.loading('正在删除');
  try {
    await removeEntity({
      id: fields.id,
      termTypeId: fields.termTypeId,
    });
    hide();
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};
/**
 *  批量取反信息发布状态
 * @param selectedRows
 */
export const handleInfoFlag = async (selectedRows) => {
  const hide = message.loading('正在取反');
  if (!selectedRows) return true;
  try {
    await infoFlag(
      selectedRows.map((row) => {
        return row.id;
      }),
    );
    hide();
    message.success('取反成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('取反失败，请重试');
    return false;
  }
};

const CategoryList = ({ isAdmin, currentUser }) => {
  const intl = useIntl();
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [parentId, setParentId] = useState('0'); 
  const [categoryList, setCategoryList] = useState([]);
  const [pageSize, setPageSize] = useState(15);
  // 移动端判定与容器宽度监测
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
    const treeParams = listScopeParams || {};
    queryLayerCategory(treeParams).then((resData) => {
      if (resData?.data) {
        setCategoryList(resData.data);
      }
    });
  }, [termListScopeReady, listScopeParams]);

  useEffect(() => {
    if (!termListScopeReady) {
      return;
    }
    actionRef.current?.reload?.();
  }, [termListScopeReady, effectiveComId, targetDomainId]);

  useDesktopSticky(actionRef);
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
        return <a onClick={() => setRow(entity)}>{dom}</a>;
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
      title: '信息发布状态',
      dataIndex: 'infoFlag',
      width: 120,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: '关',
          status: 'invalid',
        },
        '1': {
          text: '开',
          status: 'valid',
        },
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
          <a
            onClick={() => {
              handleModalVisible(true);
              setParentId(record.id);
            }}
          >
            子级
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            handleModalVisible(true);
            setParentId(record.parentId);
          }}>同级</a>
          <Divider type="vertical"/>
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
          <Popconfirm title={`您确定要删除[${record.count > 0 ? '分类下存在内容' : ''}]？`} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
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

  // 监听容器宽度：仅当总列宽 > 容器宽时横向滚动
  useEffect(() => {
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
        headerTitle="分类目录"
        actionRef={actionRef}
        rowKey="id"
        manualRequest={!termListScopeReady}
        pagination={{
          pageSize,
          showSizeChanger: true,
          pageSizeOptions: ['10','15','20','30','50'],
          onShowSizeChange: (_, size) => setPageSize(size),
        }}
        tableLayout={isMobile ? undefined : 'fixed'}
        scroll={isMobile ? undefined : { x: scrollX }}
        options={{ density: true, fullScreen: !isMobile, setting: true, reload: true }}
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
          <Popconfirm title="您确定要删除[包含内容的分类将被释放]？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Popconfirm title="您确定取反信息发布状态[开、关状态互换]？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleInfoFlag(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量取反信息发布状态</Button>
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
        categoryList={categoryList}
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
          categoryList={categoryList}
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
}))(CategoryList);
