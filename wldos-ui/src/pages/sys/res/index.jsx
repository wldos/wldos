import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState, useMemo, useCallback} from 'react';
import {useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  getAppList, getMenuTree,
  getResList,
  queryPage,
  removeEntity,
  removeEntitys,
  updateEntity
} from './service';
import {queryEnumResource} from "@/services/enum";
import {renderIcon} from "@/utils/iconLibrary";

const ResourceList = () => {
  const intl = useIntl();

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.res.msg.loadingAdd', defaultMessage: '正在添加' }));

    try {
      await addEntity({...fields});
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.res.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleUpdate = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.res.msg.loadingConfig', defaultMessage: '正在配置' }));

    try {
      await updateEntity({
        resourceName: fields.resourceName,
        resourcePath: fields.resourcePath,
        resourceCode: fields.resourceCode,
        resourceType: fields.resourceType,
        componentPath: fields.componentPath,
        icon: fields.icon,
        requestMethod: fields.requestMethod,
        target: fields.target,
        appId: fields.appId,
        isValid: fields.isValid,
        id: fields.id,
        parentId: fields.parentId,
        remark: fields.remark,
        displayOrder: fields.displayOrder,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.res.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.res.msg.loadingDelete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      await removeEntitys({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.deleteSuccessRefresh', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.res.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (fields.children) {
      message.info(intl.formatMessage({ id: 'sys.res.msg.hasChildren', defaultMessage: '存在子节点，请先删除子节点' }));
      return true;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.res.msg.loadingDelete', defaultMessage: '正在删除' }));
    try {
      await removeEntity({
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.deleteSuccessRefresh', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.res.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [parentId, setParentId] = useState('0');
  // 新建上下文：来源记录与新增模式（child 子级 / sibling 同级 / null 顶部新建）
  // 用于在 CreateForm 中按"选定资源"派生默认值，提升配置效率
  const [sourceRecord, setSourceRecord] = useState(null);
  const [addMode, setAddMode] = useState(null);
  const [resList, setResList] = useState({});
  const [menus, setMenus] = useState([]);
  const [appList, setAppList] = useState({});
  const [apps, setApps] = useState([]);
  const [resTypes, setResTypes] = useState({});
  const [resTypeOptions, setResTypeOptions] = useState([]);

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(async () => {
    const res = await getAppList();

    let data = {};
    const temp = [];
    let arr = res?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.appName,
        value: item.id,
      }
      temp.push({label: item.appName, value: item.id});
    }
    setAppList(data);
    setApps(temp);

    const resData = await getResList();

    data = {};
    arr = resData?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.resourceName,
        value: item.id,
      }
    }
    setResList(data);

    data = {};
    const arrRes = await queryEnumResource();
    arr = arrRes?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.value] = {
        text: item.label,
        value: item.value,
      };
    }
    setResTypes(data);
    setResTypeOptions(arr);

    const menuRes = await getMenuTree();
    if (menuRes?.data)
      setMenus(menuRes.data);
  }, []);

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
  const totalColsWidth = 1500;
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.res.col.resourceName', defaultMessage: '资源名称' }),
      dataIndex: 'resourceName',
      tip: intl.formatMessage({
        id: 'sys.res.tip.resourceName',
        defaultMessage: '资源包括菜单、组件、api或静态资源，特点是需要用确定的方式请求(URI+HTTP METHOD)',
      }),
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.res.rule.resourceNameRequired', defaultMessage: '资源名称为必填项' }),
          },
          {
            max: 25,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.res.rule.max25', defaultMessage: '最多25个字' }),
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.displayOrder', defaultMessage: '展示顺序' }),
      dataIndex: 'displayOrder',
      hideInSearch: true,
      hideInForm: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.resourceCode', defaultMessage: '资源编码' }),
      dataIndex: 'resourceCode',
      tip: intl.formatMessage({
        id: 'sys.res.tip.resourceCode',
        defaultMessage:
          '菜单和路由保持一致：资源编码在同一应用下唯一，资源编码原则上取urlPattern的叶子节点：/appCode/resName，子资源名称在父资源名称之后。',
      }),
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.res.rule.resourceCodeRequired', defaultMessage: '资源编码为必填项' }),
          },
          {
            max: 50,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.res.rule.max50', defaultMessage: '最大50个字符' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.resourcePath', defaultMessage: '资源路径' }),
      dataIndex: 'resourcePath',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.res.rule.resourcePathRequired', defaultMessage: '资源路径为必填项' }),
          },
          {
            max: 250,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.res.rule.max250', defaultMessage: '最大250个字符' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.componentPath', defaultMessage: '组件路径' }),
      dataIndex: 'componentPath',
      hideInSearch: true,
      formItemProps: {
        rules: [
          {
            required: false,
            message: intl.formatMessage({
              id: 'sys.res.rule.componentPathPattern',
              defaultMessage: '组件路径格式：pathX/xxx/xxx，相对于src/pages/目录',
            }),
            pattern: /^[a-zA-Z0-9\/\-_]+$/,
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.icon', defaultMessage: 'icon图标' }),
      dataIndex: 'icon',
      render: (text) => {
        if (!text) return '-';
        const val = typeof text === 'object' ? (text.name || text.value || '') : text;
        return renderIcon(val);
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.resourceType', defaultMessage: '资源类型' }),
      dataIndex: 'resourceType',
      filters: true,
      onFilter: false,
      valueEnum: resTypes,
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.requestMethod', defaultMessage: '请求方法' }),
      dataIndex: 'requestMethod',
      filters: true,
      onFilter: false,
      valueEnum: {
        GET: {
          text: 'GET',
        },
        POST: {
          text: 'POST',
        },
        PUT: {
          text: 'PUT',
        },
        DELETE: {
          text: 'DELETE',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.target', defaultMessage: '打开方式' }),
      dataIndex: 'target',
      valueEnum: {
        '_self': {
          text: '_self',
        },
        '_blank': {
          text: '_blank',
        },
        '_parent': {
          text: '_parent',
        },
        '_top': {
          text: '_top',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.parentResource', defaultMessage: '上级资源' }),
      dataIndex: 'parentId',
      hideInTable: true,
      hideInForm: true,
      valueEnum: resList,
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.app', defaultMessage: '归属应用' }),
      dataIndex: 'appId',
      filters: true,
      onFilter: false,
      valueEnum: appList,
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.remark', defaultMessage: '资源描述' }),
      dataIndex: 'remark',
      valueType: 'textarea',
      width: '16%'
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.status', defaultMessage: '资源状态' }),
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: intl.formatMessage({ id: 'sys.res.status.invalid', defaultMessage: '无效' }),
          status: 'invalid',
        },
        '1': {
          text: intl.formatMessage({ id: 'sys.res.status.valid', defaultMessage: '有效' }),
          status: 'valid',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.action', defaultMessage: '操作' }),
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          <a
            onClick={() => {
              setParentId(record.id);
              setSourceRecord(record);
              setAddMode('child');
              handleModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.res.action.child', defaultMessage: '子级' })}
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            setParentId(record.parentId);
            setSourceRecord(record);
            setAddMode('sibling');
            handleModalVisible(true);
          }}>{intl.formatMessage({ id: 'sys.res.action.sibling', defaultMessage: '同级' })}</a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.res.action.config', defaultMessage: '配置' })}
          </a>
          <Divider type="vertical"/>
          <Popconfirm
            title={intl.formatMessage({ id: 'sys.res.confirm.delete', defaultMessage: '您确定要删除？' })}
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
              await handleRemoveOne(record);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            <a>{intl.formatMessage({ id: 'sys.res.action.delete', defaultMessage: '删除' })}</a>
          </Popconfirm>
        </>
      ),
    },
  ], [intl, mobile, resTypes, appList, resList, handleRemoveOne]);

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
          headerTitle={intl.formatMessage({ id: 'sys.res.list.title', defaultMessage: '资源清单' })}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={0} type="primary" onClick={() => {
              setParentId('0');
              setSourceRecord(null);
              setAddMode(null);
              handleModalVisible(true);
            }}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.res.toolbar.new', defaultMessage: '新建' })}
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
          columns={columns}
          rowSelection={{
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          }}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              intl.formatMessage(
                { id: 'sys.res.pagination.range', defaultMessage: '第 {start}-{end} 条/总共 {total} 条' },
                { start: range[0], end: range[1], total },
              ),
          }}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
        />
      </div>
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              {intl.formatMessage({ id: 'sys.res.footer.selected', defaultMessage: '已选择' })}{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              {intl.formatMessage({ id: 'sys.res.footer.items', defaultMessage: '项' })}&nbsp;&nbsp;
              <span>
                {intl.formatMessage(
                  { id: 'sys.res.footer.callTotal', defaultMessage: '应用调用次数总计 {n} 万' },
                  { n: selectedRowsState.reduce((pre, item) => pre + item.callNo, 0) },
                )}
              </span>
            </div>
          }
        >
          <Popconfirm
            title={intl.formatMessage({ id: 'sys.res.confirm.delete', defaultMessage: '您确定要删除？' })}
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
              await handleRemove(selectedRowsState);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            <Button>{intl.formatMessage({ id: 'sys.res.batch.delete', defaultMessage: '批量删除' })}</Button>
          </Popconfirm>
          <Button type="primary">{intl.formatMessage({ id: 'sys.res.batch.export', defaultMessage: '批量导出' })}</Button>
        </FooterToolbar>
      )}
      {createModalVisible && <CreateForm
        onSubmit={async (value) => {
          const success = await handleAdd(value);

          if (success) {
            handleModalVisible(false);
            setParentId('0');
            setSourceRecord(null);
            setAddMode(null);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleModalVisible(false);
          setParentId('0');
          setSourceRecord(null);
          setAddMode(null);
        }}
        modalVisible={createModalVisible}
        apps={apps}
        menus={menus}
        resTypeOptions={resTypeOptions}
        parentId={parentId}
        sourceRecord={sourceRecord}
        addMode={addMode}
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
          apps={apps}
          menus={menus}
          resTypeOptions={resTypeOptions}
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
        {row?.resourceName && (
          <ProDescriptions
            column={2}
            title={row?.resourceName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.resourceName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default ResourceList;
