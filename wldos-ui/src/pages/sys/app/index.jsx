import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  fetchAppType,
  fetchAppOrigins,
  queryPage,
  queryDomainPage,
  removeEntity,
  removeEntities,
  removeDomainApp,
  updateEntity,
  installPlugin
} from './service';
import AddAppList from "@/pages/sys/app/components/AddApp";
import {getComSelectOption} from "@/pages/sys/com/service";
import {selectToEnum} from "@/utils/utils";

/**
 * 添加节点
 * @param fields
 */
const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');

  try {
    await addEntity({...fields});
    hide();
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
const handleUpdate = async (fields) => {
  const hide = message.loading('正在配置');

  try {
    await updateEntity({
      appName: fields.appName,
      appDesc: fields.appDesc,
      appType: fields.appType,
      appCode: fields.appCode,
      appSecret: fields.appSecret,
      isValid: fields.isValid,
      id: fields.id,
    });
    hide();
    message.success('配置成功');
    return true;
  } catch (error) {
    hide();
    message.error('配置失败请重试！');
    return false;
  }
};

/**
 * 安装插件
 * @param fields
 */
const handlePluginInstall = async (fields) => {
  const hide = message.loading('正在安装');

  try {
    await installPlugin({
      appName: fields.appName,
      appDesc: fields.appDesc,
      appType: fields.appType,
      appCode: fields.appCode,
      appSecret: fields.appSecret,
      isValid: fields.isValid,
      id: fields.id, // 两个来源：1.本地上传插件包；2.远程下载插件安装（需要应用商店或者开源社区的支持）。
    });
    hide();
    message.success('安装成功');
    return true;
  } catch (error) {
    hide();
    message.error('安装失败请重试！');
    return false;
  }
};

/**
 *  批量删除
 * @param selectedRows
 */
const handleRemove = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeEntities({
      ids: selectedRows.map((row) => row.id),
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
 *  取消域应用关联
 */
const cancelDomainApp = async (selectedRows, domainId) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeDomainApp({
      ids: selectedRows.map((row) => row.id),
      domainId,
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
 *  删除节点
 */
const handleRemoveOne = async (fields) => {
  const hide = message.loading('正在删除');
  if (!fields) return true;

  try {
    await removeEntity({
      id: fields.id,
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

const AppList = (props) => {
  const {domainId = '', comId = '', addApp} = props;
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [appTypeEnum, setAppType] = useState({});
  const [appOriginsEnum, setAppOrigins] = useState({});
  const [appTypeList, setAppTypeList] = useState([]);
  const [appOriginsList, setAppOriginsList] = useState([]);
  const [comList, setComList] = useState({});
  const [comListArray, setComListArray] = useState([]);
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [headerOffset, setHeaderOffset] = useState(88);
  const [scrollY, setScrollY] = useState(undefined);
  const [isMobile, setIsMobile] = useState(typeof window !== 'undefined' ? (window.innerWidth || document.documentElement.clientWidth) < 768 : false); 
  const [addModalVisible, handleAddModalVisible] = useState(false);
  const tableWrapRef = useRef(null);
  const [containerWidth, setContainerWidth] = useState(0);

  useEffect(async () => {
    const appType = await fetchAppType();
    const appData = appType?.data?? [];
    setAppType(selectToEnum(appData));
    setAppTypeList(appType.data);

    const appOrigins = await fetchAppOrigins();
    const appOriginsData = appOrigins?.data?? [];
    setAppOrigins(selectToEnum(appOriginsData));
    setAppOriginsList(appOriginsData);

    const comData = await getComSelectOption();

    const resData = comData?.data?? [];
    setComList(selectToEnum(resData));
    setComListArray(resData);
  }, []);

  let columns = [
    {
      title: '应用名称',
      dataIndex: 'appName',
      fixed: isMobile ? undefined : 'left',
      width: 150,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '应用名称为必填项',
          },
          {
            max: 12,
            type: 'string',
            message: '最多12个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '应用描述',
      dataIndex: 'appDesc',
      valueType: 'textarea',
      width: '17%'
    },
    {
      title: '管理类型',
      dataIndex: 'appType',
      // 保持默认宽度由表格自行分配
      filters: true,
      onFilter: false,
      valueEnum: appTypeEnum,
    },
    {
      title: '应用来源',
      dataIndex: 'appOrigin',
      // 保持默认宽度由表格自行分配
      filters: true,
      onFilter: false,
      valueEnum: appOriginsEnum,
    },
    {
      title: '归属公司',
      dataIndex: 'comId',
      // 保持默认宽度由表格自行分配
      filters: true,
      onFilter: false,
      valueEnum: comList,
    },
    {
      title: '应用编码',
      dataIndex: 'appCode',
      // 保持默认宽度由表格自行分配
      tip: '菜单和路由保持一致：应用编码全局唯一，应用暴露的资源必须以此编码为url前缀：/appCode/resName/subResName',
      hideInSearch: domainId !== '',
      hideInTable: domainId !== '',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '应用编码为必填项',
          },
          {
            max: 5,
            type: 'string',
            message: '最大5个字符',
          },
        ],
      },
    },
    {
      title: '应用密钥',
      dataIndex: 'appSecret',
      // 保持默认宽度由表格自行分配
      hideInSearch: domainId !== '',
      hideInTable: domainId !== '',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '应用密钥为必填项',
          },
        ],
      },
    },
    {
      title: '应用状态',
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        0: {
          text: '无效',
          status: 'invalid',
        },
        1: {
          text: '有效',
          status: 'valid',
        },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInTable: domainId !== '',
      width: 180,
      render: (text) => isMobile ? text : <span style={{ whiteSpace: 'nowrap' }}>{text}</span>,
    },
    {
      title: '应用ID',
      dataIndex: 'id',
      valueType: 'string',
      hideInForm: true,
      align: 'right',
      width: 200,
      render: (text) => isMobile ? text : <span style={{ whiteSpace: 'nowrap' }}>{text}</span>,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: isMobile ? undefined : 'right',
      hideInTable: domainId !== '',
      width: 200,
      render: (_, record) => {
        const isPlugin = record.appOrigin === 'plugin';
        
        return (
          <div style={isMobile ? undefined : { whiteSpace: 'nowrap' }}>
            <a
              onClick={() => {
                handleUpdateModalVisible(true);
                setStepFormValues(record);
              }}
            >
              配置
            </a>
            <Divider type="vertical"/>
            {isPlugin && (
              <>
                <a
                  onClick={() => {
                    // 跳转到插件管理页面
                    window.open('/admin/plugins', '_parent');
                  }}
                >
                  扩展维护
                </a>
                <Divider type="vertical"/>
              </>
            )}
            <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                        onConfirm={async () => {
                          await handleRemoveOne(record);
                          actionRef.current?.reloadAndRest?.();
                        }}>
              <a>删除</a>
            </Popconfirm>
          </div>
        );
      },
    },
  ];
  // 使用 ProTableX 内置样式处理，无需在页面级追加 onHeaderCell

  // 使用 ProTableX 内置桌面粘性处理
  useDesktopSticky(actionRef);
  // 监听容器宽度，仅在总列宽 > 容器宽时开启横向滚动
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

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
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
        headerTitle="应用清单"
        actionRef={actionRef}
        rowKey="id"
        bordered
        pagination={{
          defaultPageSize: 15,
          pageSizeOptions: ['10', '15', '20', '30', '50'],
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
        }}
        options={{ density: true, fullScreen: !isMobile, setting: true, reload: true }}
        tableLayout={isMobile ? undefined : 'fixed'}
        scroll={isMobile ? undefined : { x: scrollX }}
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button key={1} type="primary" hidden={domainId === ''}  onClick={() => handleAddModalVisible(true)}>
            <PlusOutlined/> 添加
          </Button>,
          <Button key={0} type="primary" hidden={domainId !== ''} onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> 新建
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const res = domainId ? await queryDomainPage({
            ...params,
            domainId,
            sorter,
            filter
          }) : await queryPage({...params,
            sorter,
            filter});
          return Promise.resolve({
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: true,
          });
        }
        }
        columns={(isMobile ? columns : columns.map((c) => ({
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
              </span>
            </div>
          }
        >
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        // eslint-disable-next-line no-unused-expressions
                        const success = domainId === '' ? await handleRemove(selectedRowsState) : await cancelDomainApp(selectedRowsState, domainId);
                        if (success) {
                          setSelectedRows([]);
                          actionRef.current?.reloadAndRest?.();
                        }
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      <CreateForm 
        onCancel={() => handleModalVisible(false)}
        modalVisible={createModalVisible}
      >
        <CreateFormContent
          onSubmit={async (value) => {
            const success = await handleAdd(value);

            if (success) {
              handleModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => handleModalVisible(false)}
          appTypeList={appTypeList}
          appOriginsList={appOriginsList}
          comList={comListArray}
        />
      </CreateForm>
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
          appTypeList={appTypeList}
        />
      ) : null}
      {domainId !== '' ? (
        <AddAppList
          onCancel={() => handleAddModalVisible(false)}
          modalVisible={addModalVisible}
          onSubmit={async (value) => {
            const success = await addApp(value);

            if (success) {
              handleAddModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          addModalVisible={addModalVisible}
          values={{domainId, comId}}
        />) : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.appName && (
          <ProDescriptions
            column={2}
            title={row?.appName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.appName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default AppList;
