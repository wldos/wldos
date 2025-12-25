import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import {
  addEntity, infoFlag,
  queryLayerCategory,
  queryPage,
  removeEntity,
  removeEntities,
  updateEntity
} from './service';

/**
 * 添加节点
 * @param fields
 */
export const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');

  try {
    const res = await addEntity({...fields});
    hide();
    if (res?.data !== 'ok'){
      message.error(res.data);
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
    if (res?.data !== 'ok'){
      message.error(res.data);
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

const CategoryList = () => {
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

  useEffect(async () => {

    const resData = await queryLayerCategory();
    if (resData?.data)
      setCategoryList(resData.data);
  }, []);

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
      render: (_, record) => (
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
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            配置
          </a>
          <Divider type="vertical"/>
          <Popconfirm title={`您确定要删除[${record.count > 0 ? '分类下存在内容' : ''}]？`} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemoveOne(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>删除</a>
          </Popconfirm>
        </>
      ),
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
      <div ref={tableWrapRef}>
      <ProTableX
        headerTitle="分类目录"
        actionRef={actionRef}
        rowKey="id"
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
          <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> 新建
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const res = await queryPage({
            ...params,
            sorter: {...sorter, 'displayOrder': 'ascend',},
            filter
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
          const success = await handleAdd(value);

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

export default CategoryList;
