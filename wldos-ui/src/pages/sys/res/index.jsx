import {PlusOutlined, QuestionCircleOutlined, ReloadOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, notification, Popconfirm, Space, Tag, Tooltip, Switch} from 'antd';
import React, {useEffect, useRef, useState, useMemo, useCallback} from 'react';
import {useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import BatchSettingModal from './components/BatchSettingModal';
import CheckApiCoverage from './components/CheckApiCoverage';
import {
  addEntity,
  getAppList, getMenuTree,
  getEntity,
  getResList,
  queryPage,
  removeEntity,
  removeEntitys,
  updateEntity,
  probeResourceConfig,
  refreshAuthCache,
} from './service';
import {queryEnumResource, queryEnumResourceTerminal, queryEnumResourceMenuRegion, queryEnumMenuNodeKind, queryEnumScopeLevel} from "@/services/enum";
import {renderIcon} from "@/utils/iconLibrary";
import {selectToEnum} from '@/utils/utils';
import {isSiteOwnedNavDir, filterTreeHideSiteNavDir} from './siteNavDirUtils';

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
      await updateEntity(fields);
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
    if (selectedRows.some((row) => isSiteOwnedNavDir(row))) {
      hide();
      message.warning('所选资源含站点自建菜单分组，请在「站点管理 → 菜单布局」中维护');
      return false;
    }
    try {
      await removeEntitys({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.deleteSuccessRefresh', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(error?.response?.message || error?.message || intl.formatMessage({ id: 'sys.res.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (isSiteOwnedNavDir(fields)) {
      message.warning('站点自建菜单分组请在「站点管理 → 菜单布局」中维护');
      return false;
    }

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
      message.error(error?.response?.message || error?.message || intl.formatMessage({ id: 'sys.res.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
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
  const [scopeLevels, setScopeLevels] = useState({});
  const [scopeLevelOptions, setScopeLevelOptions] = useState([]);
  const [terminalTypes, setTerminalTypes] = useState({});
  const [terminalOptions, setTerminalOptions] = useState([]);
  const [menuRegionTypes, setMenuRegionTypes] = useState({});
  const [menuRegionOptions, setMenuRegionOptions] = useState([]);
  const [menuNodeKindOptions, setMenuNodeKindOptions] = useState([]);
  const [menuNodeKindTypes, setMenuNodeKindTypes] = useState({});
  const [hideSiteNavDir, setHideSiteNavDir] = useState(true);
  const [batchSettingVisible, setBatchSettingVisible] = useState(false);
  const [checkModalVisible, setCheckModalVisible] = useState(false);
  const [createPresetValues, setCreatePresetValues] = useState(null);
  const coverageRef = useRef(null);
  const pendingCoverageItemRef = useRef(null);

  const handleCoverageQuickCreate = useCallback(async (item) => {
    pendingCoverageItemRef.current = item;
    try {
      const probeRes = await probeResourceConfig({
        resourceType: item.suggestedResourceType,
        resourcePath: item.apiPath,
        requestMethod: item.apiMethod,
        appId: item.appId,
      });
      const probeMsg = probeRes?.data;
      if (probeMsg && probeMsg !== true && typeof probeMsg === 'string' && !probeMsg.startsWith('提示：')) {
        notification.warning({
          message: '资源配置校验未通过',
          description: probeMsg,
          duration: 6,
        });
      }
    } catch (e) {
      // probe 失败不阻断，保存时仍会校验
    }
    setParentId(item.sourceMenuId);
    setSourceRecord({
      id: item.sourceMenuId,
      appId: item.appId,
      resourceType: item.sourceMenuResourceType,
    });
    setAddMode('child');
    setCreatePresetValues({
      parentId: item.sourceMenuId,
      appId: item.appId,
      resourceType: item.suggestedResourceType,
      resourcePath: item.apiPath,
      requestMethod: item.apiMethod,
      resourceName: item.apiName || item.suggestedResourceName,
      resourceCode: item.suggestedResourceCode,
      isValid: '1',
      scopeLevel: 'all',
      displayOrder: 1,
    });
    handleModalVisible(true);
  }, []);

  const handleBatchSetting = useCallback(async (patch) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.res.msg.loadingBatchSetting', defaultMessage: '正在批量设置' }));
    try {
      await Promise.all(
        selectedRowsState.map(({ children, ...row }) => updateEntity({ ...row, ...patch })),
      );
      hide();
      message.success(intl.formatMessage({ id: 'sys.res.msg.batchSettingSuccess', defaultMessage: '批量设置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.res.msg.batchSettingFail', defaultMessage: '批量设置失败，请重试' }));
      return false;
    }
  }, [intl, selectedRowsState]);

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

    const scopeRes = await queryEnumScopeLevel();
    data = {};
    arr = scopeRes?.data ?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.value] = {
        text: item.label,
        value: item.value,
      };
    }
    setScopeLevels(data);
    setScopeLevelOptions(arr);

    const terminalRes = await queryEnumResourceTerminal();
    data = {};
    arr = terminalRes?.data ?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.value] = {
        text: item.label,
        value: item.value,
      };
    }
    setTerminalTypes(data);
    setTerminalOptions(arr);

    const menuRegionRes = await queryEnumResourceMenuRegion();
    data = {
      '': {
        text: intl.formatMessage({ id: 'sys.res.menuRegion.default', defaultMessage: '默认主导航' }),
        value: '',
      },
    };
    arr = menuRegionRes?.data ?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.value] = {
        text: item.label,
        value: item.value,
      };
    }
    setMenuRegionTypes(data);
    setMenuRegionOptions(arr);

    const menuNodeKindRes = await queryEnumMenuNodeKind();
    const menuNodeKindData = menuNodeKindRes?.data ?? [];
    setMenuNodeKindOptions(menuNodeKindData);
    setMenuNodeKindTypes(selectToEnum(menuNodeKindData));

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
  const totalColsWidth = 1750;
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
        return (
          <Space size={4}>
            <a onClick={() => setRow(entity)}>{dom}</a>
            {isSiteOwnedNavDir(entity) ? (
              <Tag color="purple">站点分组</Tag>
            ) : null}
          </Space>
        );
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
      title: '节点形态',
      dataIndex: 'menuNodeKind',
      hideInSearch: true,
      filters: true,
      onFilter: false,
      valueEnum: menuNodeKindTypes,
      render: (text) => menuNodeKindTypes[text]?.text || text || '-',
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.resourceType', defaultMessage: '资源类型' }),
      dataIndex: 'resourceType',
      filters: true,
      onFilter: false,
      valueEnum: resTypes,
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.menuRegion', defaultMessage: '展示区域' }),
      dataIndex: 'menuRegion',
      hideInSearch: true,
      filters: true,
      onFilter: false,
      valueEnum: menuRegionTypes,
      render: (text) => {
        if (!text) {
          return menuRegionTypes['']?.text || menuRegionTypes.nav_main?.text || '-';
        }
        return menuRegionTypes[text]?.text || text || '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.terminals', defaultMessage: '可见终端' }),
      dataIndex: 'terminals',
      filters: true,
      onFilter: false,
      valueEnum: terminalTypes,
      render: (_, record) => {
        const raw = record.terminals;
        if (!raw) {
          return terminalTypes['']?.text || intl.formatMessage({ id: 'sys.res.terminals.all', defaultMessage: '全部终端' });
        }
        return raw.split(',').map((item) => {
          const key = item.trim();
          return terminalTypes[key]?.text || key;
        }).join('、');
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.res.col.scopeLevel', defaultMessage: '授权层级' }),
      dataIndex: 'scopeLevel',
      filters: true,
      onFilter: false,
      valueEnum: scopeLevels,
      render: (text) => scopeLevels[text || 'all']?.text || text || scopeLevels.all?.text || '-',
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
            onClick={async () => {
              const hide = message.loading('加载配置…');
              try {
                const detailRes = await getEntity({ id: record.id });
                hide();
                if (detailRes?.code === 200 && detailRes?.data) {
                  setStepFormValues(detailRes.data);
                } else {
                  setStepFormValues(record);
                }
              } catch (e) {
                hide();
                setStepFormValues(record);
              }
              handleUpdateModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.res.action.config', defaultMessage: '配置' })}
          </a>
          <Divider type="vertical"/>
          {isSiteOwnedNavDir(record) ? (
            <Tooltip title="站点自建菜单分组请在「站点管理 → 菜单布局」中维护">
              <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>{intl.formatMessage({ id: 'sys.res.action.delete', defaultMessage: '删除' })}</span>
            </Tooltip>
          ) : (
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
          )}
        </>
      ),
    },
  ], [intl, mobile, resTypes, terminalTypes, menuRegionTypes, scopeLevels, appList, resList, handleRemoveOne, menuNodeKindTypes]);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle={intl.formatMessage({ id: 'sys.res.list.title', defaultMessage: '资源清单' })}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          expandable={{
            defaultExpandAllRows: false,
            childrenColumnName: 'children',
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
            <Button key={1} onClick={() => setCheckModalVisible(true)}>
              {intl.formatMessage({ id: 'sys.res.toolbar.checkApi', defaultMessage: '检查遗漏 API' })}
            </Button>,
            <Popconfirm
              key="refreshAuth"
              title={intl.formatMessage({
                id: 'sys.res.confirm.refreshAuth',
                defaultMessage: '清双层鉴权缓存？下一次请求将按最新授权重新编译',
              })}
              onConfirm={async () => {
                const res = await refreshAuthCache();
                if (res?.code === 200) {
                  message.success(intl.formatMessage({
                    id: 'sys.res.msg.refreshAuthOk',
                    defaultMessage: '鉴权缓存已刷新',
                  }));
                }
              }}
            >
              <Button icon={<ReloadOutlined />}>
                {intl.formatMessage({ id: 'sys.res.toolbar.refreshAuth', defaultMessage: '刷新鉴权缓存' })}
              </Button>
            </Popconfirm>,
            <span key={2} style={{ marginLeft: 8 }}>
              <Switch
                checked={hideSiteNavDir}
                onChange={(checked) => {
                  setHideSiteNavDir(checked);
                  actionRef.current?.reload();
                }}
              />
              <span style={{ marginLeft: 8 }}>隐藏站点分组</span>
            </span>,
          ]}
          request={async (params, sorter, filter) => {
            const res = await queryPage({
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
              filter
            });
            let rows = res?.data?.rows || [];
            if (hideSiteNavDir) {
              rows = filterTreeHideSiteNavDir(rows);
            }
            return Promise.resolve({
              total: hideSiteNavDir ? rows.length : (res?.data?.total || 0),
              data: rows,
              success: true,
            });
          }
          }
          columns={columns}
          rowSelection={{
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          }}
          pagination={{
            defaultPageSize: 50,
            pageSizeOptions: ['10', '15', '20', '30', '50', '100', '150', '200'],
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
              {intl.formatMessage({ id: 'sys.res.footer.items', defaultMessage: '项' })}
            </div>
          }
        >
          <Button onClick={() => setBatchSettingVisible(true)}>
            {intl.formatMessage({ id: 'sys.res.batch.setting', defaultMessage: '批量设置' })}
          </Button>
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
            setCreatePresetValues(null);
            const pending = pendingCoverageItemRef.current;
            if (pending) {
              coverageRef.current?.resolveItem(pending.apiMethod, pending.apiPath);
              pendingCoverageItemRef.current = null;
            }
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
          setCreatePresetValues(null);
          pendingCoverageItemRef.current = null;
        }}
        modalVisible={createModalVisible}
        apps={apps}
        menus={menus}
        resTypeOptions={resTypeOptions}
        scopeLevelOptions={scopeLevelOptions}
        terminalOptions={terminalOptions}
        menuRegionOptions={menuRegionOptions}
        menuNodeKindOptions={menuNodeKindOptions}
        parentId={parentId}
        sourceRecord={sourceRecord}
        addMode={addMode}
        presetValues={createPresetValues}
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
        scopeLevelOptions={scopeLevelOptions}
        terminalOptions={terminalOptions}
        menuRegionOptions={menuRegionOptions}
        menuNodeKindOptions={menuNodeKindOptions}
        />
      ) : null}

      <BatchSettingModal
        visible={batchSettingVisible}
        onCancel={() => setBatchSettingVisible(false)}
        onSubmit={async (patch) => {
          const success = await handleBatchSetting(patch);
          if (success) {
            setBatchSettingVisible(false);
            setSelectedRows([]);
            actionRef.current?.reloadAndRest?.();
          }
        }}
        menuRegionOptions={menuRegionOptions}
        menuNodeKindOptions={menuNodeKindOptions}
        terminalOptions={terminalOptions}
        scopeLevelOptions={scopeLevelOptions}
        apps={apps}
        menus={menus}
        selectedCount={selectedRowsState.length}
      />

      <CheckApiCoverage
        ref={coverageRef}
        modalVisible={checkModalVisible}
        onCancel={() => setCheckModalVisible(false)}
        onQuickCreate={handleCoverageQuickCreate}
      />

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
