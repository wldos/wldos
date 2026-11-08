import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Modal, Popconfirm, Select, Space, Table, Tabs, Tag, Tooltip, Typography} from 'antd';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useIntl, connect} from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { DISPLAY_ORDER_PATTERN_STRING } from '@/utils/displayOrder';
import {
  isPlatformScopeComId,
  isValidScopeComId,
  mergeToolbarWriteScope,
  comLabelText,
} from '@/utils/orgCreateUserDomain';
import useOperationalScope from '@/hooks/useOperationalScope';
import usePlatformOperatorMode from '@/hooks/usePlatformOperatorMode';
import isMobile from '@/hooks/isMobile';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  authRole,
  getExistRes,
  getRoleList,
  queryPage,
  removeEntity,
  removeEntitys,
  updateEntity,
  saveRoleScopeBundle,
  batchApplyRoleScopes,
  previewAuthBatch,
  applyAuthBatch,
  rollbackAuthBatch,
  fetchAuthDrift,
} from './service';
import AuthRes from "@/pages/sys/role/components/AuthRes";
import { canDeleteRoleRecord, roleDeleteBlockReason } from './roleDeleteGuard';

const ROLE_BATCH_SNAPSHOT_STORAGE_KEY = 'wldos.roleAuthBatch.pendingSnapshot';

const loadPersistedBatchSnapshot = () => {
  if (typeof sessionStorage === 'undefined') {
    return null;
  }
  try {
    const raw = sessionStorage.getItem(ROLE_BATCH_SNAPSHOT_STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch (e) {
    return null;
  }
};

const persistBatchSnapshot = (meta) => {
  if (typeof sessionStorage === 'undefined' || !meta?.snapshotId) {
    return;
  }
  sessionStorage.setItem(ROLE_BATCH_SNAPSHOT_STORAGE_KEY, JSON.stringify(meta));
};

const clearPersistedBatchSnapshot = () => {
  if (typeof sessionStorage === 'undefined') {
    return;
  }
  sessionStorage.removeItem(ROLE_BATCH_SNAPSHOT_STORAGE_KEY);
};

const resolveBatchDomainDisplay = (row, domainLabelMap) => {
  const idKey = row?.domainId != null ? String(row.domainId) : '';
  if (idKey && domainLabelMap?.[idKey]) {
    return domainLabelMap[idKey];
  }
  if (row?.domainName) {
    return row.domainName;
  }
  return idKey || '—';
};

const filterRolesByComId = (rows, comId) => {
  if (!comId || !rows?.length) {
    return rows ?? [];
  }
  if (String(comId) === '0') {
    return rows.filter((item) => item.comId == null || String(item.comId) === '0');
  }
  return rows.filter((item) =>
    item.comId == null || String(item.comId) === '0' || String(item.comId) === String(comId),
  );
};

const RoleList = ({ hasAuthority, isAdmin, isTenantAdmin, currentUser }) => {
  const intl = useIntl();
  const { lock: platformOperatorLock } = usePlatformOperatorMode();

  const {
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    comLabelEnum,
    domainOptions,
    allDomainRows,
    domainLabelMap,
    showDomainSelectInToolbar,
    listScopeReady,
  } = useOperationalScope({
    isAdmin,
    currentUser,
    intl,
    adminDefaultAxis: 'platform',
    allowPlatformScopeInToolbar: isAdmin,
    platformOperatorLock,
    domainToolbarEnabled: isAdmin || isTenantAdmin,
  });

  const targetDomainLabel = useMemo(() => {
    if (isPlatformScopeComId(effectiveComId) || !targetDomainId) {
      return '';
    }
    const matched = domainOptions.find((item) => item.value === targetDomainId);
    return matched?.label || domainLabelMap[targetDomainId] || '';
  }, [effectiveComId, targetDomainId, domainOptions, domainLabelMap]);

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await addEntity({...fields});
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.role.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl]);

  const handleUpdate = useCallback(async (fields, scopeBundle) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.config', defaultMessage: '正在配置' }));

    try {
      const res = await updateEntity({
        roleName: fields.roleName,
        roleCode: fields.roleCode,
        roleType: fields.roleType,
        dataPolicy: fields.dataPolicy,
        parentId: fields.parentId,
        roleDesc: fields.roleDesc,
        isValid: fields.isValid,
        displayOrder: fields.displayOrder,
        comId: fields.comId,
        id: fields.id,
      });
      if (res?.code !== 200) {
        hide();
        return false;
      }
      if (scopeBundle && fields.id) {
        const scopeRes = await saveRoleScopeBundle({
          roleId: fields.id,
          scopes: scopeBundle.scopes,
          adoptedDictCodes: scopeBundle.adoptedDictCodes,
        });
        if (scopeRes?.code !== 200) {
          hide();
          return false;
        }
      }
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      const res = await removeEntitys({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.role.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (!canDeleteRoleRecord(fields)) {
      message.info(roleDeleteBlockReason(fields) || intl.formatMessage({ id: 'sys.role.msg.cannotDelete', defaultMessage: '当前角色不可删除' }));
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.delete', defaultMessage: '正在删除' }));
    try {
      const res = await removeEntity({
        id: fields.id,
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.role.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl]);

  const handleAuth = useCallback(async (fields = {roleIds: [], roleId: ''}, existRes = [], domainId) => {
    if (existRes?.length === fields.roleIds?.length && fields.roleIds.every(id => existRes.some(eid => eid === id))) {
      message.info(intl.formatMessage({ id: 'sys.role.msg.authNoChange', defaultMessage: '没有任何改变，不做操作！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.auth', defaultMessage: '正在授权' }));

    try {
      const payload = {
        resIds: fields.resIds,
        roleId: fields.roleId,
      };
      if (domainId) {
        payload.targetDomainId = domainId;
      }
      const res = await authRole(payload);
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.role.msg.authSuccess', defaultMessage: '授权成功' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl]);

  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [authModalVisible, handleAuthModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [batchScopeSourceId, setBatchScopeSourceId] = useState(undefined);
  const [parentId, setParentId] = useState('0');
  const [currentRecord, setCurrentRecord] = useState(null);
  const [roleList, setRoleList] = useState({});
  const [roles, setRoles] = useState([]);
  // 父角色下拉专用：补虚拟根节点，使 parentId='0'（顶级）能显示为可读标签而非裸值 0
  const parentRoleOptions = useMemo(() => [{ label: '根角色（顶级）', value: '0' }, ...roles], [roles]);
  const [resTree, setResTree] = useState([]);
  const [existRes, setAuthRes] = useState([]);
  const [roleResGrants, setRoleResGrants] = useState([]);
  const [authRoleValues, setAuthRoleValues] = useState({});
  const [batchAuthVisible, setBatchAuthVisible] = useState(false);
  const [batchAuthSourceRoleId, setBatchAuthSourceRoleId] = useState(undefined);
  const [batchAuthTargetDomains, setBatchAuthTargetDomains] = useState([]);
  const [batchAuthMode, setBatchAuthMode] = useState('addMissing');
  const [batchAuthPreview, setBatchAuthPreview] = useState(null);
  const [batchAuthLoading, setBatchAuthLoading] = useState(false);
  const [driftRoleId, setDriftRoleId] = useState(undefined);
  const [driftBaselineDomainId, setDriftBaselineDomainId] = useState(undefined);
  const [driftItems, setDriftItems] = useState([]);
  const [driftLoading, setDriftLoading] = useState(false);
  const [batchAuthTab, setBatchAuthTab] = useState('push');
  const [batchAuthPreviewToken, setBatchAuthPreviewToken] = useState(null);
  const [batchAuthRollbackMeta, setBatchAuthRollbackMeta] = useState(() => loadPersistedBatchSnapshot());

  useEffect(() => {
    setBatchAuthPreviewToken(null);
    setBatchAuthPreview(null);
  }, [batchAuthSourceRoleId, batchAuthTargetDomains, batchAuthMode]);

  const mobile = isMobile();
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  useDesktopSticky(actionRef);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const roleListParams = isAdmin && isValidScopeComId(effectiveComId) ? { comId: effectiveComId } : undefined;
      const roleData = await getRoleList(roleListParams);
      if (cancelled) {
        return;
      }
      const data = {};
      const temp = [];
      let arr = filterRolesByComId(roleData?.data ?? [], effectiveComId);
      for (let i = 0, len = arr.length; i < len; i += 1) {
        const item = arr[i];
        data[item.id] = {
          text: item.roleName,
          roleCode: item.roleCode,
        };
        temp.push({ label: item.roleName, value: item.id });
      }
      setRoleList(data);
      setRoles(temp);
    })();
    return () => {
      cancelled = true;
    };
  }, [intl, isAdmin, effectiveComId]);

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

  const resolveAuthTargetDomainId = useCallback(() => (
    isPlatformScopeComId(effectiveComId) ? undefined : targetDomainId
  ), [effectiveComId, targetDomainId]);

  const queryExistRes = async (roleId, domainId) => {
    const params = { roleId };
    if (domainId) {
      params.targetDomainId = domainId;
    }
    const res = await getExistRes(params);
    return res;
  };

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    actionRef.current?.reload?.();
  }, [listScopeReady, targetDomainId, effectiveComId]);

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.role.col.roleName', defaultMessage: '角色名称' }),
      dataIndex: 'roleName',
      tip: intl.formatMessage({ id: 'sys.role.col.roleName.tip', defaultMessage: '角色的显示名称，用于界面展示' }),
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.role.rule.roleNameRequired', defaultMessage: '角色名称为必填项' }),
          },
          {
            max: 25,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.role.rule.roleNameMax', defaultMessage: '最多25个字' }),
          },
        ],
      },
      render: (dom, entity) => {
        return (
          <span>
            <a onClick={() => setRow(entity)}>{dom}</a>
            {entity?.systemBuiltIn ? (
              <Tag color="blue" style={{ marginLeft: 8 }}>系统内置</Tag>
            ) : null}
            {entity?.kindTemplate ? (
              <Tag color="default" style={{ marginLeft: 8 }}>平台模板</Tag>
            ) : null}
          </span>
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.domainId', defaultMessage: '所属站点' }),
      dataIndex: 'domainId',
      hideInSearch: true,
      hideInTable: !isAdmin || !showDomainSelectInToolbar,
      render: (_, entity) => {
        if (entity?.domainId == null || entity.domainId === '' || Number(entity.domainId) <= 0) {
          return '全局';
        }
        return domainLabelMap[String(entity.domainId)] || String(entity.domainId);
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.comId', defaultMessage: '归属公司' }),
      dataIndex: 'comId',
      hideInSearch: true,
      hideInForm: true,
      hideInTable: !isAdmin,
      valueEnum: comLabelEnum,
      render: (_, record) => {
        const label = comLabelText(comLabelEnum, record.comId);
        if (label) {
          return label;
        }
        if (record.comId == null || String(record.comId) === '0') {
          return intl.formatMessage({ id: 'sys.scope.platform', defaultMessage: '平台' });
        }
        return record.comId;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.roleCode', defaultMessage: '角色编码' }),
      dataIndex: 'roleCode',
      tip: intl.formatMessage({ id: 'sys.role.col.roleCode.tip', defaultMessage: '角色的唯一标识，用于系统识别' }),
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.role.rule.roleCodeRequired', defaultMessage: '角色编码为必填项' }),
          },
          {
            max: 32,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.role.rule.roleCodeMax', defaultMessage: '最多32位' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.roleDesc', defaultMessage: '描述' }),
      dataIndex: 'roleDesc',
      valueType: 'textarea',
      tip: intl.formatMessage({ id: 'sys.role.col.roleDesc.tip', defaultMessage: '角色的详细说明，帮助理解角色用途' }),
      width: '17%',
      formItemProps: {
        rules: [
          {
            max: 150,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.role.rule.roleDescMax', defaultMessage: '最多150个字' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.roleType', defaultMessage: '角色类型' }),
      dataIndex: 'roleType',
      tip: intl.formatMessage({ id: 'sys.role.col.roleType.tip', defaultMessage: '角色的分类，影响权限范围' }),
      filters: true,
      onFilter: false,
      valueEnum: {
        'sys_role': {
          text: intl.formatMessage({ id: 'sys.role.roleType.sys_role', defaultMessage: '系统角色' }),
        },
        'subject': {
          text: intl.formatMessage({ id: 'sys.role.roleType.subject', defaultMessage: '社会主体' }),
        },
        'tal_role': {
          text: intl.formatMessage({ id: 'sys.role.roleType.tal_role', defaultMessage: '租户角色' }),
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.parentId', defaultMessage: '父角色' }),
      dataIndex: 'parentId',
      tip: intl.formatMessage({ id: 'sys.role.col.parentId.tip', defaultMessage: '角色的上级角色，用于角色层级管理' }),
      hideInTable: true,
      hideInForm: false,
      valueEnum: roleList,
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.displayOrder', defaultMessage: '展示顺序' }),
      dataIndex: 'displayOrder',
      tip: intl.formatMessage({ id: 'sys.role.col.displayOrder.tip', defaultMessage: '角色在列表中的显示顺序，1-9999' }),
      hideInSearch: true,
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.role.rule.displayOrderRequired', defaultMessage: '展示顺序为必填项' }),
          },
          {
            pattern: DISPLAY_ORDER_PATTERN_STRING,
            message: intl.formatMessage({ id: 'sys.role.rule.displayOrderRange', defaultMessage: '请输入1-9999之间的数字' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.status', defaultMessage: '状态' }),
      dataIndex: 'isValid',
      tip: intl.formatMessage({ id: 'sys.role.col.status.tip', defaultMessage: '角色的启用状态' }),
      hideInForm: false,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: intl.formatMessage({ id: 'sys.role.status.invalid', defaultMessage: '无效' }),
          status: 'invalid',
        },
        '1': {
          text: intl.formatMessage({ id: 'sys.role.status.valid', defaultMessage: '有效' }),
          status: 'valid',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.role.col.operation', defaultMessage: '操作' }),
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => {
        const canManage = record.managed !== false;
        const canAssign = record.assignable !== false;
        return (
        <>
          <a
            onClick={() => {
              setCurrentRecord({
                parentId: record.id,
                roleType: record.roleType
              });
              handleModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.role.action.child', defaultMessage: '子级' })}
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            setCurrentRecord({
              parentId: record.parentId,
              roleType: record.roleType
            });
            handleModalVisible(true);
          }}>{intl.formatMessage({ id: 'sys.role.action.sibling', defaultMessage: '同级' })}</a>
          {hasAuthority('role:update') && canManage && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.role.action.config', defaultMessage: '配置' })}
          </a>
          </>
          )}
          {hasAuthority('role:auth') && canAssign && !canManage && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            数据与范围
          </a>
          </>
          )}
          {hasAuthority('role:auth') && canAssign && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              queryExistRes(record.id, resolveAuthTargetDomainId()).then(res => {
                if (res && res.data && res.data.authRes) {
                  const {roleRes = [], authRes = []} = res.data;
                  setRoleResGrants(roleRes);
                  const temp = roleRes.filter((item) => !item.inherit).map((item) => String(item.id));
                  setAuthRes([...temp]);
                  setResTree(authRes);

                  setAuthRoleValues(record);
                  handleAuthModalVisible(true);
                }
              });
            }}
          >
            {intl.formatMessage({ id: 'sys.role.action.auth', defaultMessage: '授权' })}
          </a>
          </>
          )}
          {hasAuthority('role:auth') && canAssign && (isAdmin || isTenantAdmin) && (record.kindTemplate || record.systemBuiltIn) && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setBatchAuthSourceRoleId(record.id);
              setDriftRoleId(record.id);
              setBatchAuthTargetDomains([]);
              setBatchAuthPreview(null);
              setBatchAuthPreviewToken(null);
              setBatchAuthVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.role.action.authSync', defaultMessage: '站点同步' })}
          </a>
          </>
          )}
          {hasAuthority('role:delete') && canManage && (
          <>
          <Divider type="vertical"/>
          {canDeleteRoleRecord(record) ? (
            <Popconfirm title={intl.formatMessage({ id: 'sys.role.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                        onConfirm={async () => {
                          const success = await handleRemoveOne(record);
                          if (success) {
                            actionRef.current?.reloadAndRest?.();
                          }
                        }}>
              <a>{intl.formatMessage({ id: 'sys.role.action.delete', defaultMessage: '删除' })}</a>
            </Popconfirm>
          ) : (
            <Tooltip title={roleDeleteBlockReason(record)}>
              <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>{intl.formatMessage({ id: 'sys.role.action.delete', defaultMessage: '删除' })}</span>
            </Tooltip>
          )}
          </>
          )}
        </>
      );},
    },
  ], [intl, mobile, roleList, domainLabelMap, comLabelEnum, handleRemoveOne, hasAuthority, isAdmin, isTenantAdmin, targetDomainId, showDomainSelectInToolbar]);

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle={intl.formatMessage({ id: 'sys.role.headerTitle', defaultMessage: '角色清单' })}
          actionRef={actionRef}
          manualRequest={!listScopeReady}
          rowKey="id"
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
            showDomainSelectInToolbar ? (
              <Space key="domain-select" size={8}>
                <span>{intl.formatMessage({ id: 'sys.role.domain.targetSite', defaultMessage: '目标站点' })}</span>
                <Select
                  style={{ minWidth: 200 }}
                  placeholder="选择站点"
                  options={domainOptions}
                  value={targetDomainId}
                  onChange={(value) => setTargetDomainId(value || undefined)}
                  showSearch
                  optionFilterProp="label"
                />
              </Space>
            ) : null,
            (isAdmin || isTenantAdmin) && hasAuthority('role:auth') ? (
              <Button key="batch-auth" onClick={() => {
                setBatchAuthSourceRoleId(undefined);
                setBatchAuthTargetDomains([]);
                setBatchAuthPreview(null);
                setBatchAuthMode('addMissing');
                setBatchAuthVisible(true);
              }}>
                批量推送授权
              </Button>
            ) : null,
            hasAuthority('role:create') ? (
            <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.role.toolbar.new', defaultMessage: '新建' })}
            </Button>
            ) : null,
          ]}
          request={async (params, sorter, filter) => {
            const queryParams = {
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
              filter
            };
            if (!isPlatformScopeComId(effectiveComId) && targetDomainId) {
              queryParams.domainId = targetDomainId;
            }
            if (isAdmin && effectiveComId) {
              queryParams.comId = effectiveComId;
            }
            const res = await queryPage(queryParams);
            const rows = res?.data?.rows || [];

            return Promise.resolve({
              total: res?.data?.total || 0,
              data: rows,
              success: true,
            });
          }}
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
                { id: 'sys.role.pagination.range', defaultMessage: '第 {start}-{end} 条/总共 {total} 条' },
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
              {intl.formatMessage({ id: 'sys.role.footer.selected', defaultMessage: '已选择' })}{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              {intl.formatMessage({ id: 'sys.role.footer.items', defaultMessage: '项' })}&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title={intl.formatMessage({ id: 'sys.role.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        const success = await handleRemove(selectedRowsState);
                        if (success) {
                          actionRef.current?.reloadAndRest?.();
                        }
                      }}>
            <Button>{intl.formatMessage({ id: 'sys.role.footer.batchDelete', defaultMessage: '批量删除' })}</Button>
          </Popconfirm>
          {hasAuthority('role:auth') ? (
            <>
              <Select
                allowClear
                placeholder="从角色复制定制过滤"
                style={{ width: 220 }}
                value={batchScopeSourceId}
                onChange={setBatchScopeSourceId}
                options={roles.filter((r) => !selectedRowsState.some((row) => String(row.id) === String(r.value)))}
              />
              <Popconfirm
                title="将源角色的定制过滤应用到已选角色？"
                disabled={!batchScopeSourceId}
                onConfirm={async () => {
                  const res = await batchApplyRoleScopes({
                    sourceRoleId: batchScopeSourceId,
                    targetRoleIds: selectedRowsState.map((row) => row.id),
                  });
                  if (res?.code === 200) {
                    message.success('批量应用定制过滤成功');
                    setBatchScopeSourceId(undefined);
                    actionRef.current?.reloadAndRest?.();
                  }
                }}
              >
                <Button disabled={!batchScopeSourceId}>批量应用定制过滤</Button>
              </Popconfirm>
            </>
          ) : null}
          <Button type="primary">{intl.formatMessage({ id: 'sys.role.footer.batchExport', defaultMessage: '批量导出' })}</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)}
                  modalVisible={createModalVisible}>
        <CreateFormContent
          isPlatformAdmin={isAdmin}
          defaultComId={effectiveComId}
          onSubmit={async (value) => {
            const finalParentId = currentRecord?.parentId || parentId;
            const success = await handleAdd(mergeToolbarWriteScope({
              ...value,
              parentId: finalParentId,
            }, { effectiveComId, targetDomainId }));

            if (success) {
              handleModalVisible(false);
              setParentId('0');
              setCurrentRecord(null);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setParentId('0');
            setCurrentRecord(null);
          }}
          roles={parentRoleOptions}
          comList={comListArray}
          currentRecord={currentRecord}
        />
      </CreateForm>
      {stepFormValues && Object.keys(stepFormValues).length ? (
        <UpdateForm
          isPlatformAdmin={isAdmin}
          onSubmit={async (value, scopeBundle) => {
            const success = await handleUpdate(value, scopeBundle);

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
          roles={parentRoleOptions}
          comList={comListArray}
          assignable={stepFormValues.assignable !== false}
          managed={stepFormValues.managed !== false}
        />
      ) : null}
      {authRoleValues && Object.keys(authRoleValues).length ?
        <AuthRes
          onSubmit={async (value) => {
            const success = await handleAuth(value, existRes, resolveAuthTargetDomainId());

            if (success) {
              handleAuthModalVisible(false);
              setAuthRoleValues({});
              setResTree([]);
              setAuthRes([]);
              setRoleResGrants([]);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleAuthModalVisible(false);
            setAuthRoleValues({});
            setResTree([]);
            setAuthRes([]);
            setRoleResGrants([]);
          }}
          authModalVisible={authModalVisible}
          res={resTree}
          existRes={existRes}
          roleResGrants={roleResGrants}
          values={authRoleValues}
          targetDomainLabel={targetDomainLabel}
        />
        : null}

      <Modal
        title="会员角色授权：多站同步"
        visible={batchAuthVisible}
        width={720}
        destroyOnClose
        onCancel={() => setBatchAuthVisible(false)}
        footer={[
          batchAuthRollbackMeta?.snapshotId ? (
            <span key="rollback-wrap" style={{ float: 'left', maxWidth: '55%' }}>
              <Button
                key="rollback"
                danger
                loading={batchAuthLoading}
                onClick={() => {
                  const siteLabel = (batchAuthRollbackMeta.domainNames || []).join('、') || '（站点名未知）';
                  Modal.confirm({
                    title: '确认回滚上次推送',
                    content: `将以下站点该角色授权恢复为推送前快照，仅能回滚一次（7 天内有效）：${siteLabel}`,
                    okText: '确认回滚',
                    cancelText: '取消',
                    onOk: async () => {
                      setBatchAuthLoading(true);
                      const res = await rollbackAuthBatch({ snapshotId: batchAuthRollbackMeta.snapshotId });
                      setBatchAuthLoading(false);
                      if (res?.code !== 200) {
                        if (res?.code === 422) {
                          message.error(res.message || '校验失败');
                          const msg = res.message || '';
                          if (msg.includes('快照')) {
                            setBatchAuthRollbackMeta(null);
                            clearPersistedBatchSnapshot();
                          }
                        }
                        return;
                      }
                      message.success('已回滚至推送前授权');
                      setBatchAuthRollbackMeta(null);
                      clearPersistedBatchSnapshot();
                      setBatchAuthPreviewToken(null);
                      setBatchAuthPreview(null);
                      actionRef.current?.reload?.();
                    },
                  });
                }}
              >
                回滚上次推送
              </Button>
              <Typography.Text type="secondary" style={{ display: 'block', marginTop: 4, fontSize: 12 }}>
                快照覆盖站点：{(batchAuthRollbackMeta.domainNames || []).join('、') || '—'}
              </Typography.Text>
            </span>
          ) : null,
          <Button key="cancel" onClick={() => setBatchAuthVisible(false)}>取消</Button>,
          <Button
            key="push"
            type="primary"
            loading={batchAuthLoading}
            disabled={batchAuthTab !== 'push'}
            onClick={() => {
              if (batchAuthTab !== 'push') {
                return;
              }
              if (!batchAuthSourceRoleId || !batchAuthTargetDomains.length) {
                message.warning('请选择源角色与目标站点');
                return;
              }
              if (!batchAuthPreviewToken) {
                message.warning('请先预览 diff（修改源/目标/模式后须重新预览）');
                return;
              }
              if (batchAuthPreview?.items?.some((row) => row.instanceReady === false)) {
                message.warning('存在未就绪的目标站，请调整目标后重新预览');
                return;
              }
              const runApply = async () => {
                setBatchAuthLoading(true);
                const res = await applyAuthBatch({
                  sourceRoleId: batchAuthSourceRoleId,
                  targetDomainIds: batchAuthTargetDomains.map(Number),
                  mode: batchAuthMode,
                  confirmReplace: batchAuthMode === 'replace',
                  previewToken: batchAuthPreviewToken,
                });
                setBatchAuthLoading(false);
                if (res?.code !== 200) {
                  if (res?.code === 422) {
                    message.error(res.message || '校验失败');
                  }
                  return;
                }
                const applyData = res.data || {};
                const snapshotId = applyData.snapshotId != null ? String(applyData.snapshotId) : null;
                const domainNames = applyData.snapshotDomainNames || [];
                if (snapshotId) {
                  const meta = { snapshotId, domainNames, pushedAt: Date.now() };
                  setBatchAuthRollbackMeta(meta);
                  persistBatchSnapshot(meta);
                }
                setBatchAuthPreviewToken(null);
                message.success(
                  snapshotId
                    ? `推送完成，7 天内可回滚一次（站点：${domainNames.join('、') || '—'}）`
                    : '批量推送完成',
                );
                setBatchAuthVisible(false);
                actionRef.current?.reload?.();
              };
              if (batchAuthMode === 'replace') {
                Modal.confirm({
                  title: '确认整表替换',
                  content: '整表替换会用源角色授权覆盖目标站该角色的默认授权，目标站多配的项会被移除。请先预览差异再确认。',
                  okText: '确认推送',
                  cancelText: '取消',
                  onOk: runApply,
                });
                return;
              }
              runApply();
            }}
          >
            执行推送
          </Button>,
        ]}
      >
      <Typography.Paragraph type="secondary" style={{ marginBottom: 16 }}>
        会员角色在每个站点各有一份，授权挂在该站点实例上。同步前请先预览差异；推送前会保存快照，7 天内可回滚一次（快照在本机进程内，重启后不可回滚）。
        整表替换会删掉目标站多配的授权，请谨慎。「差异对比」只读，不会改数据。
      </Typography.Paragraph>
      {batchAuthRollbackMeta?.snapshotId ? (
        <Typography.Paragraph style={{ marginBottom: 16 }}>
          <Tag color="orange">待回滚快照</Tag>
          覆盖站点：{(batchAuthRollbackMeta.domainNames || []).join('、') || '—'}
        </Typography.Paragraph>
      ) : null}
      <Tabs defaultActiveKey="push" onChange={setBatchAuthTab}>
      <Tabs.TabPane tab="同步推送" key="push">
        <Space direction="vertical" style={{ width: '100%' }} size={12}>
          <div>
            <div style={{ marginBottom: 4 }}>源角色</div>
            <Select
              style={{ width: '100%' }}
              placeholder="选择会员角色（平台模板或当前站点实例）"
              value={batchAuthSourceRoleId}
              onChange={setBatchAuthSourceRoleId}
              options={roles}
              showSearch
              optionFilterProp="label"
            />
          </div>
          <div>
            <div style={{ marginBottom: 4 }}>目标站点</div>
            <Select
              mode="multiple"
              style={{ width: '100%' }}
              placeholder="多选目标站点"
              value={batchAuthTargetDomains}
              onChange={setBatchAuthTargetDomains}
              options={domainOptions}
              showSearch
              optionFilterProp="label"
            />
          </div>
          <div>
            <div style={{ marginBottom: 4 }}>模式</div>
            <Select
              style={{ width: '100%' }}
              value={batchAuthMode}
              onChange={setBatchAuthMode}
              options={[
                { label: 'addMissing — 仅补缺失', value: 'addMissing' },
                { label: 'merge — 并集不删', value: 'merge' },
                { label: 'replace — 整表替换', value: 'replace' },
              ]}
            />
          </div>
          <Button
            onClick={async () => {
              if (!batchAuthSourceRoleId || !batchAuthTargetDomains.length) {
                message.warning('请选择源角色与目标站点');
                return;
              }
              setBatchAuthLoading(true);
              setBatchAuthPreview(null);
              const res = await previewAuthBatch({
                sourceRoleId: batchAuthSourceRoleId,
                targetDomainIds: batchAuthTargetDomains.map(Number),
                mode: batchAuthMode,
              });
              setBatchAuthLoading(false);
              if (res?.code !== 200) {
                if (res?.code === 422) {
                  message.error(res.message || '校验失败');
                }
                return;
              }
              setBatchAuthPreview(res.data);
              setBatchAuthPreviewToken(res.data?.previewToken || null);
              if (!res.data?.previewToken) {
                message.warning('存在未就绪目标站，请调整目标后重新预览');
              }
            }}
          >
            预览 diff
          </Button>
          {batchAuthPreview?.items?.length ? (
            <Table
              size="small"
              pagination={false}
              rowKey="domainId"
              dataSource={batchAuthPreview.items}
              columns={[
                {
                  title: '站点',
                  dataIndex: 'domainName',
                  ellipsis: true,
                  render: (_, row) => resolveBatchDomainDisplay(row, domainLabelMap),
                },
                {
                  title: '状态',
                  dataIndex: 'instanceReady',
                  width: 120,
                  render: (ready, row) => (ready === false
                    ? <Tag color="warning">未就绪</Tag>
                    : <Tag color="success">可推送</Tag>),
                },
                {
                  title: '说明',
                  dataIndex: 'statusNote',
                  ellipsis: true,
                  render: (v, row) => (row.instanceReady === false ? (v || '本站无角色实例') : '—'),
                },
                { title: '当前', dataIndex: 'currentCount', width: 64 },
                { title: '新增', dataIndex: 'toAdd', width: 64 },
                { title: '移除', dataIndex: 'toRemove', width: 64 },
                { title: '推送后', dataIndex: 'afterCount', width: 72 },
              ]}
            />
          ) : null}
        </Space>
      </Tabs.TabPane>
      <Tabs.TabPane tab="差异对比" key="drift">
        <Space direction="vertical" style={{ width: '100%' }} size={12}>
          <div>
            <div style={{ marginBottom: 4 }}>会员角色</div>
            <Select
              style={{ width: '100%' }}
              placeholder="选择角色"
              value={driftRoleId}
              onChange={setDriftRoleId}
              options={roles}
              showSearch
              optionFilterProp="label"
            />
          </div>
          <div>
            <div style={{ marginBottom: 4 }}>基准站点（空 = 平台模板）</div>
            <Select
              style={{ width: '100%' }}
              placeholder="空 = 平台模板"
              value={driftBaselineDomainId}
              onChange={setDriftBaselineDomainId}
              options={domainOptions}
              allowClear
              showSearch
              optionFilterProp="label"
            />
          </div>
          <Button
            loading={driftLoading}
            onClick={async () => {
              const roleCode = roleList?.[driftRoleId]?.roleCode;
              if (!roleCode) {
                message.warning('请选择角色');
                return;
              }
              setDriftLoading(true);
              setDriftItems([]);
              const res = await fetchAuthDrift({
                roleCode,
                baselineDomainId: driftBaselineDomainId ? Number(driftBaselineDomainId) : undefined,
              });
              setDriftLoading(false);
              if (res?.code !== 200) {
                if (res?.code === 422) {
                  message.error(res.message || '校验失败');
                }
                return;
              }
              const rows = res.data || [];
              setDriftItems(rows);
              if (!rows.length) {
                message.info('当前权限范围内没有可对比的站点');
              }
            }}
          >
            查询差异
          </Button>
          {driftItems.length ? (
            <Table
              size="small"
              pagination={false}
              rowKey="domainId"
              dataSource={driftItems}
              columns={[
                {
                  title: '站点',
                  dataIndex: 'domainName',
                  ellipsis: true,
                  render: (_, row) => resolveBatchDomainDisplay(row, domainLabelMap),
                },
                {
                  title: '状态',
                  dataIndex: 'instanceReady',
                  width: 120,
                  render: (ready) => (ready === false
                    ? <Tag color="warning">未就绪</Tag>
                    : <Tag color="success">已对比</Tag>),
                },
                {
                  title: '说明',
                  dataIndex: 'statusNote',
                  ellipsis: true,
                  render: (v, row) => (row.instanceReady === false ? (v || '本站无角色实例') : '—'),
                },
                { title: '实例授权数', dataIndex: 'instanceCount', width: 90 },
                { title: '比基准少', dataIndex: 'missingFromBaseline', width: 96 },
                { title: '比基准多', dataIndex: 'extraVsBaseline', width: 96 },
              ]}
            />
          ) : null}
        </Space>
      </Tabs.TabPane>
      </Tabs>
      </Modal>

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.roleName && (
          <ProDescriptions
            column={2}
            title={row?.roleName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.roleName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default connect(({ user, loading }) => {
  const auth = buildAuthorityHelper(user, '/admin');
  return {
    ...auth,
    currentUser: user.currentUser,
    loading: loading.effects['user/fetchAdminAuthorities'],
  };
})(RoleList);
