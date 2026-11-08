import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm, Select, Space, Tag, Tooltip} from 'antd';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useIntl, connect} from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import ProDescriptions from '@ant-design/pro-descriptions';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  addOrgUser,
  authRole, fetchOrgType,
  getExistRes,
  getOrgList,
  queryPage,
  removeEntity,
  removeEntitys,
  updateEntity
} from './service';
import AuthRes from "@/pages/sys/org/components/AuthRole";
import AddUserForm from "@/pages/sys/org/components/AddUserForm";
import UserCreateForm from '@/pages/sys/user/components/CreateForm';
import UserCreateFormContent from '@/pages/sys/user/components/CreateFormContent';
import { addEntity as registerUser4admin } from '@/pages/sys/user/service';
import {selectToEnum} from "@/utils/utils";
import useOperationalScope from '@/hooks/useOperationalScope';
import {
  needsTargetSiteSelection,
  resolveCreateUserDomainId,
  isPlatformScopeComId,
  isToolbarScopeComId,
  isValidScopeComId,
  canPickMemberScopeInModal,
  mergeToolbarWriteScope,
  comLabelText,
} from '@/utils/orgCreateUserDomain';
import { canDeleteOrgRecord, orgDeleteBlockReason } from './orgDeleteGuard';

const OrgList = ({ hasAuthority, currentUser, isAdmin, fixedCategory = 'staff', listTitle }) => {
  const intl = useIntl();
  const isSegmentCategory = fixedCategory === 'segment';

  const {
    defaultComId,
    targetComId,
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    comLabelEnum,
    domainOptions,
    allDomainRows,
    domainLabelMap,
    showDomainSelect,
    showDomainSelectInToolbar,
    listScopeReady,
  } = useOperationalScope({
    isAdmin,
    currentUser,
    intl,
    adminDefaultAxis: isSegmentCategory ? 'tenant-required' : 'platform',
    allowPlatformScopeInToolbar: isAdmin,
  });

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      await addEntity({...fields});
      hide();
      message.success(intl.formatMessage({ id: 'sys.org.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleUpdate = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.config', defaultMessage: '正在配置' }));

    try {
      await updateEntity({
        orgName: fields.orgName,
        orgCode: fields.orgCode,
        orgType: fields.orgType,
        comId: fields.comId,
        archId: fields.archId,
        parentId: fields.parentId,
        isValid: fields.isValid,
        displayOrder: fields.displayOrder,
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.org.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      await removeEntitys({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.org.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (!canDeleteOrgRecord(fields)) {
      message.info(orgDeleteBlockReason(fields) || intl.formatMessage({ id: 'sys.org.msg.cannotDelete', defaultMessage: '当前组织不可删除' }));
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.delete', defaultMessage: '正在删除' }));
    try {
      const res = await removeEntity({
        id: fields.id,
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.org.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleAuth = useCallback(async (fields = {roleIds: [], orgId: '', archId: '', comId: ''}, existRes = []) => {
    if (existRes?.length === fields.roleIds?.length &&
        fields.roleIds.every(id => existRes.some(eid => eid === id))) {
      message.info(intl.formatMessage({ id: 'sys.org.msg.authNoChange', defaultMessage: '没有任何改变，不做操作！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.auth', defaultMessage: '正在授权' }));

    try {
      await authRole({
        roleIds: fields.roleIds,
        orgId: fields.orgId,
        archId: fields.archId,
        comId: fields.comId,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.org.msg.authSuccess', defaultMessage: '授权成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.authFail', defaultMessage: '授权失败请重试！' }));
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
  const [parentId, setParentId] = useState(undefined);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [roleList, setRoleList] = useState({});
  const [roles, setRoles] = useState([]);
  // 上级组织下拉专用：仅当后端列表本身没有 id=0 的行时才补虚拟根节点。
  // 平台超管列表会注入 id=0、名称为「平台」的虚拟根节点
  // （代表顶级），此时 roles 已含 value='0' 的选项，不能再叠加，否则下拉里「根组织（顶级）」与「平台」重复；
  // 租户/站点管理员视角后端不注入该行，才需要这里补上，使 parentId='0' 有可读标签而非裸值或空。
  const hasBackendRootOption = useMemo(() => roles.some((item) => String(item.value) === '0'), [roles]);
  const parentOrgOptions = useMemo(() => (hasBackendRootOption ? roles : [{
    label: intl.formatMessage({ id: 'sys.org.parentId.top', defaultMessage: '根组织（顶级）' }),
    value: '0',
  }, ...roles]), [roles, intl, hasBackendRootOption]);
  const [resTree, setResTree] = useState([]);
  const [existRes, setAuthRes] = useState([]);
  const [authRoleValues, setAuthRoleValues] = useState({});
  const [orgTypeEnum, setOrgType] = useState({});
  const [orgTypes, setOrgTypeList] = useState([]);
  const [addUserModalVisible, handleAddUserModalVisible] = useState(false);
  const [addUserValues, setAddUserValues] = useState({});
  const [createUserVisible, setCreateUserVisible] = useState(false);
  const [createUserOrgRecord, setCreateUserOrgRecord] = useState(null);
  const createUserFormRef = useRef();
  const orgCategory = fixedCategory;
  const segmentWriteReady = !isSegmentCategory
    || (isValidScopeComId(effectiveComId) && !!targetDomainId);

  const addUser = useCallback(async (value={ids: [], orgId: '', archId: '', comId: ''}) => {
    if (!!value && value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'sys.org.msg.selectMembers', defaultMessage: '请选择要添加的成员！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const scoped = mergeToolbarWriteScope(
        { comId: value.comId, domainId: value.domainId },
        { effectiveComId, targetDomainId },
      );
      const res = await addOrgUser({
        userIds: value.ids,
        orgId: value.orgId,
        archId: value.archId,
        comId: scoped.comId != null ? scoped.comId : value.comId,
        userComId: value.userComId,
        domainId: scoped.domainId,
      });
      hide();
      if (res?.code !== 200) {
        message.error(res?.message || intl.formatMessage({ id: 'sys.org.msg.addMemberFail', defaultMessage: '添加失败请重试！' }));
        return false;
      }
      if (res?.data !== '')
        message.warn(res.data);
      else
        message.success(intl.formatMessage({ id: 'sys.org.msg.addMemberSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.org.msg.addMemberFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }

  }, [intl, effectiveComId, targetDomainId]);

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  const handleCreateUserInOrg = useCallback(async (fields) => {
    if (!createUserOrgRecord) {
      return false;
    }
    const domainId = resolveCreateUserDomainId(createUserOrgRecord, {
      targetDomainId,
      domainOptions,
    });
    if (needsTargetSiteSelection(createUserOrgRecord, {
      showDomainSelect,
      targetDomainId,
      domainOptions,
    })) {
      message.error(intl.formatMessage({
        id: 'sys.org.msg.selectTargetSite',
        defaultMessage: '请选择目标站点',
      }));
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.add', defaultMessage: '正在添加' }));
    try {
      const rowComId = createUserOrgRecord.comId;
      const useToolbarCom = rowComId == null || rowComId === '' || String(rowComId) === '0';
      const payload = mergeToolbarWriteScope({
        ...fields,
        orgId: createUserOrgRecord.id,
        archId: createUserOrgRecord.archId,
        comId: useToolbarCom && isValidScopeComId(effectiveComId)
          ? effectiveComId
          : createUserOrgRecord.comId,
        domainId: domainId || targetDomainId,
      }, { effectiveComId, targetDomainId });
      const res = await registerUser4admin(payload);
      if (res?.data) {
        const { status, news } = res.data;
        if (status === 'error') {
          hide();
          message.error(news);
          return false;
        }
      }
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.addSuccess', defaultMessage: '添加成功' }));
      actionRef.current?.reload?.();
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl, createUserOrgRecord, targetDomainId, domainOptions, showDomainSelect, effectiveComId]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const orgListParams = isAdmin && isToolbarScopeComId(effectiveComId) ? { comId: effectiveComId } : undefined;
      const [roleData, orgType] = await Promise.all([getOrgList(orgListParams), fetchOrgType()]);
      if (cancelled) {
        return;
      }

      let data = {
        0: { text: intl.formatMessage({ id: 'sys.org.parentId.top', defaultMessage: '根组织（顶级）' }) },
      };
      let temp = [];
      let arr = roleData?.data ?? [];
      if (defaultComId) {
        arr = arr.filter((item) => item.comId == null || String(item.comId) === String(defaultComId));
      } else if (isAdmin && isPlatformScopeComId(targetComId)) {
        arr = arr.filter((item) => item.comId == null || String(item.comId) === '0');
      } else if (isAdmin && isValidScopeComId(targetComId)) {
        arr = arr.filter((item) => item.comId == null || String(item.comId) === '0'
          || String(item.comId) === String(targetComId));
      }
      for (let i = 0, len = arr.length; i < len; i += 1) {
        const item = arr[i];
        data[item.id] = {
          text: item.orgName,
        };
        temp.push({ label: item.orgName, value: item.id });
      }
      setRoleList(data);
      setRoles(temp);

      data = {};
      arr = orgType?.data ?? [];
      for (let i = 0, len = arr.length; i < len; i += 1) {
        const item = arr[i];
        data[item.value] = {
          text: item.label,
        };
      }
      setOrgType(data);
      setOrgTypeList(orgType.data);
    })();
    return () => {
      cancelled = true;
    };
  }, [intl, isAdmin, defaultComId, targetComId, effectiveComId]);

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

  const queryExistRes = async (orgId, record) => {
    const params = { orgId };
    const domainId = resolveCreateUserDomainId(record, { targetDomainId, domainOptions });
    if (domainId) {
      params.domainId = domainId;
    }
    return getExistRes(params);
  };

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    actionRef.current?.reload?.();
  }, [listScopeReady, targetDomainId, effectiveComId, orgCategory]);

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.org.col.orgName', defaultMessage: isSegmentCategory ? '分群名称' : '组织名称' }),
      dataIndex: 'orgName',
      tip: intl.formatMessage({ id: 'sys.org.col.orgName.tip', defaultMessage: '组织是业务域运营的人员管理' }),
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.org.rule.orgNameRequired', defaultMessage: '组织名称为必填项' }),
          },
          {
            max: 60,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.org.rule.orgNameMax', defaultMessage: '最多60个字' }),
          },
        ],
      },
      render: (dom, entity) => (
        <span>
          <a onClick={() => setRow(entity)}>{dom}</a>
          {entity?.systemBuiltIn ? (
            <Tag color="blue" style={{ marginLeft: 8 }}>系统内置</Tag>
          ) : null}
          {entity?.kindTemplate ? (
            <Tag color="default" style={{ marginLeft: 8 }}>平台模板</Tag>
          ) : null}
        </span>
      ),
      width: '17%'
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.domainId', defaultMessage: '所属站点' }),
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
      title: intl.formatMessage({ id: 'sys.org.col.orgCode', defaultMessage: '组织编码' }),
      dataIndex: 'orgCode',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.org.rule.orgCodeRequired', defaultMessage: '组织编码为必填项' }),
          },
          {
            max: 32,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.org.rule.orgCodeMax', defaultMessage: '最多32位' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.orgType', defaultMessage: '组织类型' }),
      dataIndex: 'orgType',
      filters: isSegmentCategory ? false : true,
      onFilter: false,
      valueEnum: orgTypeEnum,
      render: (_, entity) => {
        if (entity?.systemBuiltIn || entity?.orgType === 'segment') {
          return intl.formatMessage({ id: 'sys.org.tab.segment', defaultMessage: '会员分群' });
        }
        return orgTypeEnum[entity?.orgType]?.text || entity?.orgType || '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.parentId', defaultMessage: '上级组织' }),
      dataIndex: 'parentId',
      hideInTable: true,
      hideInForm: true,
      valueEnum: roleList,
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.comId', defaultMessage: '归属公司' }),
      dataIndex: 'comId',
      filters: true,
      onFilter: false,
      hideInForm: true,
      valueEnum: comLabelEnum,
      width: '17%',
      render: (_, record) => {
        const label = comLabelText(comLabelEnum, record.comId);
        if (label) {
          return label;
        }
        if (record.comId == null || String(record.comId) === '0') {
          return '-';
        }
        return record.comId;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.displayOrder', defaultMessage: '展示顺序' }),
      dataIndex: 'displayOrder',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.status', defaultMessage: '状态' }),
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: intl.formatMessage({ id: 'sys.org.status.invalid', defaultMessage: '无效' }),
          status: 'invalid',
        },
        '1': {
          text: intl.formatMessage({ id: 'sys.org.status.valid', defaultMessage: '有效' }),
          status: 'valid',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.operation', defaultMessage: '操作' }),
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          {!isSegmentCategory && (
          <>
          <a
            onClick={() => {
              setCurrentRecord({
                parentId: record.id,
                orgType: record.orgType,
                comId: record.comId,
                archId: record.archId
              });
              handleModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.child', defaultMessage: '子级' })}
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            setCurrentRecord({
              parentId: record.parentId,
              orgType: record.orgType,
              comId: record.comId,
              archId: record.archId
            });
            handleModalVisible(true);
          }}>{intl.formatMessage({ id: 'sys.org.action.sibling', defaultMessage: '同级' })}</a>
          </>
          )}
          {hasAuthority('org:update') && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.config', defaultMessage: '配置' })}
          </a>
          </>
          )}
          {hasAuthority('org:auth') && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              if (needsTargetSiteSelection(record, {
                showDomainSelect: showDomainSelectInToolbar,
                targetDomainId,
                domainOptions,
              }) && !canPickMemberScopeInModal(record)) {
                message.error(intl.formatMessage({
                  id: 'sys.org.msg.selectTargetSite',
                  defaultMessage: '请选择目标站点',
                }));
                return;
              }
              queryExistRes(record.id, record).then(res => {
                if (res && res.data && res.data.authRes) {
                  const {orgRole = [], authRes = []} = res.data;
                  const temp = orgRole.map(item => item.id);
                  setAuthRes([...temp]);
                  setResTree(authRes);

                  setAuthRoleValues(record);
                  handleAuthModalVisible(true);
                }
              });
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.auth', defaultMessage: '授权' })}
          </a>
          </>
          )}
          {hasAuthority('org:addUser') && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              if (needsTargetSiteSelection(record, {
                showDomainSelect: showDomainSelectInToolbar,
                targetDomainId,
                domainOptions,
              }) && !canPickMemberScopeInModal(record)) {
                message.error(intl.formatMessage({
                  id: 'sys.org.msg.selectTargetSite',
                  defaultMessage: '请选择目标站点',
                }));
                return;
              }
              handleAddUserModalVisible(true);
              setAddUserValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.staff', defaultMessage: '人员' })}
          </a>
          </>
          )}
          {hasAuthority('user:create') && (
          <>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              if (needsTargetSiteSelection(record, {
                showDomainSelect: showDomainSelectInToolbar,
                targetDomainId,
                domainOptions,
              })) {
                message.error(intl.formatMessage({
                  id: 'sys.org.msg.selectTargetTenantSite',
                  defaultMessage: '请先选择目标租户与目标站点',
                }));
                return;
              }
              setCreateUserOrgRecord(record);
              setCreateUserVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.user.toolbar.new', defaultMessage: '新增用户' })}
          </a>
          </>
          )}
          {hasAuthority('org:delete') && (
          <>
          <Divider type="vertical"/>
          {canDeleteOrgRecord(record) ? (
            <Popconfirm title={intl.formatMessage({ id: 'sys.org.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
             onConfirm={async () => {
               const ok = await handleRemoveOne(record);
               if (ok) {
                 actionRef.current?.reloadAndRest?.();
               }
             }}
            >
              <a>{intl.formatMessage({ id: 'sys.org.action.delete', defaultMessage: '删除' })}</a>
            </Popconfirm>
          ) : (
            <Tooltip title={orgDeleteBlockReason(record)}>
              <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>{intl.formatMessage({ id: 'sys.org.action.delete', defaultMessage: '删除' })}</span>
            </Tooltip>
          )}
          </>
          )}
        </>
      ),
    },
  ], [intl, mobile, orgTypeEnum, roleList, comLabelEnum, domainLabelMap, handleRemoveOne, hasAuthority, isSegmentCategory, showDomainSelectInToolbar, targetDomainId, domainOptions]);

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle={listTitle || intl.formatMessage({ id: 'sys.org.headerTitle', defaultMessage: '组织清单' })}
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
                  allowClear
                  placeholder="选择站点"
                  options={domainOptions}
                  value={targetDomainId}
                  onChange={(value) => setTargetDomainId(value || undefined)}
                  showSearch
                  optionFilterProp="label"
                />
              </Space>
            ) : null,
            hasAuthority('org:create') && segmentWriteReady ? (
            <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.org.toolbar.new', defaultMessage: isSegmentCategory ? '新建分群' : '新建' })}
            </Button>
            ) : null,
          ]}
          request={async (params, sorter, filter) => {
            const queryParams = {
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
              filter,
              category: orgCategory,
            };
            if (targetDomainId) {
              queryParams.domainId = targetDomainId;
            }
            if (isAdmin && isToolbarScopeComId(effectiveComId)) {
              queryParams.comId = effectiveComId;
            }
            const res = await queryPage(queryParams);
            const rows = res?.data?.rows || [];

            return Promise.resolve({
              total: res?.data?.total || 0,
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
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              intl.formatMessage(
                { id: 'sys.org.pagination.range', defaultMessage: '第 {start}-{end} 条/总共 {total} 条' },
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
              {intl.formatMessage({ id: 'sys.org.footer.selected', defaultMessage: '已选择' })}{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              {intl.formatMessage({ id: 'sys.org.footer.items', defaultMessage: '项' })}&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title={intl.formatMessage({ id: 'sys.org.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>
              {intl.formatMessage({ id: 'sys.org.footer.batchDelete', defaultMessage: '批量删除' })}
            </Button>
          </Popconfirm>
          <Button type="primary">{intl.formatMessage({ id: 'sys.org.footer.batchExport', defaultMessage: '批量导出' })}</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)}
                  modalVisible={createModalVisible}
                  segmentMode={isSegmentCategory}>
        <CreateFormContent
          segmentMode={isSegmentCategory}
          onSubmit={async (value) => {
            const finalParentId = currentRecord?.parentId ?? parentId;
            const payload = { ...value };
            if (finalParentId != null && finalParentId !== '') {
              payload.parentId = finalParentId;
            }
            const success = await handleAdd(mergeToolbarWriteScope(payload, { effectiveComId, targetDomainId }));

            if (success) {
              handleModalVisible(false);
              setParentId(undefined);
              setCurrentRecord(null);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setParentId(undefined);
            setCurrentRecord(null);
          }}
          roles={parentOrgOptions}
          orgTypes={orgTypes}
          comList={comListArray}
          currentRecord={currentRecord}
          defaultComId={effectiveComId}
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
          roles={parentOrgOptions}
          orgTypes={orgTypes}
        />
      ) : null}
      {authRoleValues && Object.keys(authRoleValues).length ?
        <AuthRes
          onSubmit={async (value) => {
            const success = await handleAuth(value, existRes);

            if (success) {
              handleAuthModalVisible(false);
              setAuthRoleValues({});
              setResTree([]);
              setAuthRes([]);

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
          }}
          authModalVisible={authModalVisible}
          res={resTree}
          existRes={existRes}
          values={authRoleValues}
        />
        : null}
      {addUserValues && Object.keys(addUserValues).length ?
      <AddUserForm
        onCancel={() => {
          handleAddUserModalVisible(false);
          setAddUserValues({});
        }}
        addUserModalVisible={addUserModalVisible}
        values={addUserValues}
        addUser={addUser}
        comList={comListArray}
        domainOptions={domainOptions}
        allDomainRows={allDomainRows}
        showDomainSelect={showDomainSelect}
        effectiveComId={effectiveComId}
        listDomainId={resolveCreateUserDomainId(addUserValues, { targetDomainId, domainOptions })}
        listComId={effectiveComId}
      /> : null}

      <UserCreateForm
        modalVisible={createUserVisible}
        onCancel={() => {
          setCreateUserVisible(false);
          setCreateUserOrgRecord(null);
        }}
        onSubmit={() => createUserFormRef.current?.submit?.()}
      >
        <UserCreateFormContent
          ref={createUserFormRef}
          onSubmit={async (values) => {
            const success = await handleCreateUserInOrg(values);
            if (success) {
              setCreateUserVisible(false);
              setCreateUserOrgRecord(null);
            }
          }}
          onCancel={() => {
            setCreateUserVisible(false);
            setCreateUserOrgRecord(null);
          }}
        />
      </UserCreateForm>

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.orgName && (
          <ProDescriptions
            column={2}
            title={row?.orgName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.orgName,
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
})(OrgList);
