import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, Popconfirm} from 'antd';
import React, {useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import {
  queryPage,
} from './service';
import {
  handleAdd,
  handleInfoFlag,
  handleRemove,
  handleRemoveOne,
  handleUpdate
} from "@/pages/sys/category";

const CategoryList = () => {
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
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            配置
          </a>
          <Divider type="vertical"/>
          <Popconfirm title={`您确定要删除[${record.count > 0 ? '标签下存在内容' : ''}]？`} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
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
        headerTitle="标签列表"
        actionRef={actionRef}
        rowKey="id"
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
          <Popconfirm title="您确定要删除[已关联内容的标签将被释放]？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
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

export default CategoryList;
