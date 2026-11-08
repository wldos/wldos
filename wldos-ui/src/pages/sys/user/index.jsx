import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm, Select, Space, Tag, Tooltip} from 'antd';
import React, {useCallback, useRef, useState, useEffect, useMemo} from 'react';
import {useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {connect} from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  queryPage,
  queryOrgPage,
  removeEntity,
  removeEntities,
  removeOrgStaff,
  updateEntity,
  updatePasswd4admin,
  setUserPrimaryOrg,
  setUserPrimaryCompany
} from './service';
import AddUserList from "@/pages/sys/user/components/add";
import Passwd from "@/pages/account/settings/components/Passwd";
import UserPermissionView from '@/components/UserPermissionView';
import { parseLoginActionResult } from '@/utils/loginResult';
import OrgTreeSelect from "@/components/TreeSelects/OrgTreeSelect";
import useOperationalScope from '@/hooks/useOperationalScope';
import usePlatformOperatorMode from '@/hooks/usePlatformOperatorMode';
import {
  comLabelText,
  isPlatformScopeComId,
  isToolbarScopeComId,
  isValidScopeComId,
  mergeToolbarWriteScope,
} from '@/utils/orgCreateUserDomain';
import { fetchEnumUserStatus } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';

const UserList = (props) => {
  const intl = useIntl();
  const {orgId = '', archId = '', comId = '', addUser, hasAuthority, isAdmin, currentUser, listDomainId, listComId} = props;

  const scopeToolbarEnabled = isAdmin && orgId === '';
  const { lock: platformOperatorLock, ready: platformOpsReady } = usePlatformOperatorMode();

  const {
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    comLabelEnum,
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
    toolbarEnabled: scopeToolbarEnabled,
    platformOperatorLock,
  });

  const memberCreateScopeReady = useMemo(() => {
    if (!scopeToolbarEnabled) {
      return true;
    }
    if (platformOperatorLock) {
      return platformOpsReady && targetDomainId != null && targetDomainId !== '';
    }
    if (isPlatformScopeComId(effectiveComId)) {
      return true;
    }
    return isValidScopeComId(effectiveComId) && targetDomainId != null && targetDomainId !== '';
  }, [scopeToolbarEnabled, effectiveComId, targetDomainId, platformOperatorLock, platformOpsReady]);

  const createFormRef = useRef();

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      if (scopeToolbarEnabled && !isToolbarScopeComId(effectiveComId)) {
        message.error(intl.formatMessage({
          id: 'sys.user.msg.platformNoMemberCreate',
          defaultMessage: '请先选择目标租户及站点',
        }));
        return false;
      }
      if (scopeToolbarEnabled && isValidScopeComId(effectiveComId) && !targetDomainId) {
        message.error(intl.formatMessage({
          id: 'sys.user.msg.targetDomainRequired',
          defaultMessage: '请先选择目标站点',
        }));
        return false;
      }
      // 顶层用户管理的「新增」不带组织选择器，新用户落在哪个站点由工具栏「目标站点」决定，
      // 否则后端会退回管理员自己登录时所在的站点，与工具栏选择的目标租户/站点无关。
      const payload = { ...fields };
      if (scopeToolbarEnabled && targetDomainId) {
        payload.domainId = targetDomainId;
      }
      const res = await addEntity(payload);
      if (res && res.data) {
        const {status, news} = res.data;
        if (status === 'error'){
          message.error(news);
          return false;
        }
      }
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      const desc = error?.response?.message;
      const applied = createFormRef.current?.applyServerValidationErrors?.(desc);
      if (!applied) {
        message.error(intl.formatMessage({ id: 'sys.user.msg.addFail', defaultMessage: '添加失败请重试！' }));
      }
      return false;
    }
  }, [intl, scopeToolbarEnabled, targetDomainId, effectiveComId]);

  const handleUpdate = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.config', defaultMessage: '正在配置' }));

    try {
      await updateEntity({
        nickname: fields.nickname,
        remark: fields.remark,
        loginName: fields.loginName,
        email: fields.email,
        mobile: fields.mobile,
        passwd: fields.passwd,
        status: fields.status,
        id: fields.id,
      });
      if (orgId === '' && fields.orgId != null && fields.orgId !== stepFormValues.orgId) {
        await setUserPrimaryOrg({ userId: fields.id, orgId: fields.orgId });
      }
      if (orgId === '' && fields.comId != null && fields.comId !== stepFormValues.comId) {
        await setUserPrimaryCompany({ userId: fields.id, comId: fields.comId });
      }
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  }, [intl, orgId, stepFormValues]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      await removeEntities({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const cancelOrgStaff = useCallback(async (selectedRows, orgIdParam) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      await removeOrgStaff({
        ids: selectedRows.map((row) => row.id),
        orgId: orgIdParam,
        ...(listDomainId ? { domainId: listDomainId } : {}),
        ...(listComId && String(listComId) !== '0' ? { comId: listComId } : {}),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl, listDomainId, listComId]);

  const handleRemoveOne = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.user.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!fields) return true;

    try {
      await removeEntity({
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.user.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.user.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const changePasswd = useCallback(async (values) => {
    const {nickname, id, password, confirm} = values;

    try {
      const res = await updatePasswd4admin({ id, password, confirm });
      const outcome = parseLoginActionResult(res);
      if (!outcome.ok) {
        if (outcome.news) {
          message.error(outcome.news);
        }
        return false;
      }
      message.info(intl.formatMessage(
        { id: 'sys.user.msg.passwdSuccess', defaultMessage: '修改用户{nickname}密码成功！' },
        { nickname },
      ));
      return true;
    } catch (error) {
      message.error(intl.formatMessage({ id: 'sys.user.msg.passwdFail', defaultMessage: '修改失败请重试！' }));
      return false;
    }
  }, [intl]);

  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [addModalVisible, handleAddModalVisible] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [modalVisible, setModalVisible] = useState(false);
  const [statusEnum, setStatusEnum] = useState({});
  const [permissionModalVisible, setPermissionModalVisible] = useState(false);
  const [permissionUser, setPermissionUser] = useState(null);

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(() => {
    if (!listScopeReady || orgId !== '') {
      return;
    }
    actionRef.current?.reload?.();
  }, [listScopeReady, targetDomainId, effectiveComId, orgId]);

  useEffect(() => {
    fetchEnumUserStatus().then((res) => {
      if (res?.data) {
        setStatusEnum(selectToEnum(res.data));
      }
    });
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

  const totalColsWidth = 1000;
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const domainFilterEnum = useMemo(() => {
    const enumMap = {};
    Object.entries(domainLabelMap || {}).forEach(([value, label]) => {
      if (value != null && value !== '') {
        enumMap[String(value)] = { text: label || value };
      }
    });
    return enumMap;
  }, [domainLabelMap]);

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.user.col.nickname', defaultMessage: '昵称' }),
      dataIndex: 'nickname',
      tip: intl.formatMessage({ id: 'sys.user.col.nickname.tip', defaultMessage: '用户指注册会员，可以是自然人或者法人，不同身份可以认证' }),
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.user.rule.nicknameRequired', defaultMessage: '昵称为必填项' }),
          },
          {
            max: 60,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.user.rule.nicknameMax', defaultMessage: '最多60个字' }),
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.loginName', defaultMessage: '账号' }),
      dataIndex: 'loginName',
      tip: intl.formatMessage({ id: 'sys.user.col.loginName.tip', defaultMessage: '登录用户名，建议使用邮箱' }),
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.user.rule.loginNameRequired', defaultMessage: '账号为必填项' }),
          },
          {
            max: 30,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.user.rule.loginNameMax', defaultMessage: '最大30个字符' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.passwd', defaultMessage: '密码' }),
      dataIndex: 'passwd',
      hideInSearch: true,
      hideInTable: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.user.rule.passwdRequired', defaultMessage: '密码为必填项,最多120位字符' }),
            max: 120,
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.email', defaultMessage: '邮箱' }),
      dataIndex: 'email',
      tip: intl.formatMessage({ id: 'sys.user.col.email.tip', defaultMessage: '用于找回密码' }),
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.user.rule.emailRequired', defaultMessage: '邮箱为必填项' }),
          },
          {
            max: 50,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.user.rule.emailMax', defaultMessage: '最大50个字符' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.status', defaultMessage: '状态' }),
      dataIndex: 'status',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: statusEnum,
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.remark', defaultMessage: '简介' }),
      dataIndex: 'remark',
      hideInForm: true,
      valueType: 'textarea',
      width: '16%'
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.domain', defaultMessage: '站点' }),
      dataIndex: 'domainId',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: orgId !== '' || !showDomainSelectInToolbar,
      filters: orgId === '' && showDomainSelectInToolbar,
      onFilter: false,
      valueEnum: domainFilterEnum,
      render: (_, record) => {
        const id = record.domainId;
        if (id == null || id === '') {
          return '-';
        }
        const mapped = domainLabelMap[String(id)];
        if (mapped) {
          return mapped;
        }
        const name = record.domainName && String(record.domainName).trim();
        return name ? `${name} (${id})` : `站点 ID ${id}`;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.comName', defaultMessage: '公司' }),
      dataIndex: 'mainComId',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: orgId !== '',
      filters: orgId === '',
      onFilter: false,
      valueEnum: comLabelEnum,
      render: (_, record) => {
        const cid = record.mainComId ?? record.comId;
        const label = comLabelText(comLabelEnum, cid);
        if (label) {
          return label;
        }
        if (record.comName) {
          return record.comName;
        }
        if (cid === 0 || cid === '0') {
          return intl.formatMessage({ id: 'sys.scope.platform', defaultMessage: '平台' });
        }
        if (cid != null && cid !== '') {
          return `公司 ID ${cid}`;
        }
        return '-';
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.orgName', defaultMessage: '组织' }),
      dataIndex: 'orgName',
      hideInForm: true,
      hideInTable: orgId !== '',
      renderFormItem: () => <OrgTreeSelect allowClear placeholder="按组织筛选" />,
      render: (_, record) => {
        const names = (record.orgNames || record.orgName || '').split('、').filter(Boolean);
        if (names.length <= 1) {
          return record.orgName || '-';
        }
        return (
          <>
            {names[0]}
            <Tooltip title={names.join('、')}>
              <Tag style={{ marginLeft: 4 }}>+{names.length - 1}</Tag>
            </Tooltip>
          </>
        );
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.createTime', defaultMessage: '创建时间' }),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: orgId !== '',
      hideInTable: orgId !== '',
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.id', defaultMessage: '用户ID' }),
      dataIndex: 'id',
      hideInForm: true,
      valueType: 'string',
    },
    {
      title: intl.formatMessage({ id: 'sys.user.col.operation', defaultMessage: '操作' }),
      dataIndex: 'option',
      valueType: 'option',
      hideInTable: orgId !== '',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => {
        const writable = record.writable !== false;
        return (
        <>
          {hasAuthority('user:update') && (
          <>
          <a
            onClick={() => {
              setPermissionUser(record);
              setPermissionModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.user.action.viewAuth', defaultMessage: '权限查看' })}
          </a>
          <Divider type="vertical"/>
          </>
          )}
          {hasAuthority('user:update') && writable && (
            <>
              <a
                onClick={() => {
                  handleUpdateModalVisible(true);
                  setStepFormValues(record);
                }}
              >
                {intl.formatMessage({ id: 'sys.user.action.config', defaultMessage: '配置' })}
              </a>
              <Divider type="vertical"/>
            </>
          )}
          {hasAuthority('user:passwd') && writable && (
            <>
              <a
                onClick={() => {
                  setModalVisible(true);
                  setFormValues(record);
                }}
              >
                {intl.formatMessage({ id: 'sys.user.action.updatePasswd', defaultMessage: '更新密码' })}
              </a>
              <Divider type="vertical"/>
            </>
          )}
          {hasAuthority('user:delete') && writable && (
          <Popconfirm title={intl.formatMessage({ id: 'sys.user.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemoveOne(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>{intl.formatMessage({ id: 'sys.user.action.delete', defaultMessage: '删除' })}</a>
          </Popconfirm>
          )}
          {!writable && (
            <Tooltip title={intl.formatMessage({ id: 'sys.user.action.notWritable.tip', defaultMessage: '平级管理员账号，仅可查看' })}>
              <span style={{color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed'}}>
                {intl.formatMessage({ id: 'sys.user.action.readonly', defaultMessage: '仅查看' })}
              </span>
            </Tooltip>
          )}
        </>
        );
      },
    },
  ], [intl, mobile, orgId, handleRemoveOne, hasAuthority, statusEnum, comLabelEnum, domainFilterEnum, domainLabelMap, showDomainSelectInToolbar]);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle={intl.formatMessage({ id: 'sys.user.headerTitle', defaultMessage: '用户清单' })}
          actionRef={actionRef}
          manualRequest={scopeToolbarEnabled && !listScopeReady}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            isAdmin && orgId === '' && comListArray.length > 0 ? (
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
            scopeToolbarEnabled && showDomainSelectInToolbar ? (
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
            hasAuthority('user:create') ? (
            <Button key={1} type="primary" hidden={orgId === ''}  onClick={() => handleAddModalVisible(true)}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.user.toolbar.add', defaultMessage: '添加' })}
            </Button>
            ) : null,
            hasAuthority('user:create') ? (
            <Tooltip
              title={
                scopeToolbarEnabled && !memberCreateScopeReady
                  ? intl.formatMessage({
                    id: 'sys.user.toolbar.new.disabledHint',
                    defaultMessage: '请先在工具栏选择目标租户及站点',
                  })
                  : undefined
              }
            >
              <Button
                key={0}
                type="primary"
                hidden={orgId !== ''}
                disabled={scopeToolbarEnabled && !memberCreateScopeReady}
                onClick={() => handleModalVisible(true)}
              >
                <PlusOutlined/> {intl.formatMessage({ id: 'sys.user.toolbar.new', defaultMessage: '新增' })}
              </Button>
            </Tooltip>
            ) : null,
          ]}
          request={async (params, sorter, filter) => {
            const res = orgId ? await queryOrgPage({
              ...params,
              orgId,
              sorter,
              filter,
              ...(listDomainId ? { domainId: listDomainId } : {}),
              ...(listComId && String(listComId) !== '0' ? { comId: listComId } : {}),
            }) : await queryPage({
              ...params,
              sorter,
              filter,
              filterOrgId: params.orgName || undefined,
              ...(isAdmin && isToolbarScopeComId(effectiveComId) ? { comId: effectiveComId } : {}),
              ...(isAdmin && targetDomainId ? { domainId: targetDomainId } : {}),
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
                { id: 'sys.user.pagination.range', defaultMessage: '第 {start}-{end} 条/总共 {total} 条' },
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
              {intl.formatMessage({ id: 'sys.user.footer.selected', defaultMessage: '已选择' })}{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              {intl.formatMessage({ id: 'sys.user.footer.items', defaultMessage: '项' })}&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title={intl.formatMessage({ id: 'sys.user.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        if (orgId === '') {
                          await handleRemove(selectedRowsState);
                        } else {
                          await cancelOrgStaff(selectedRowsState, orgId);
                        }
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>{intl.formatMessage({ id: 'sys.user.footer.batchDelete', defaultMessage: '批量删除' })}</Button>
          </Popconfirm>
          <Button type="primary">{intl.formatMessage({ id: 'sys.user.footer.batchExport', defaultMessage: '批量导出' })}</Button>
        </FooterToolbar>
      )}
      <CreateForm
        onCancel={() => handleModalVisible(false)}
        modalVisible={createModalVisible}
        onSubmit={() => createFormRef.current?.submit?.()}
      >
        <CreateFormContent
          ref={createFormRef}
          onSubmit={async (value) => {
            const success = await handleAdd(mergeToolbarWriteScope(value, { effectiveComId, targetDomainId }));

            if (success) {
              handleModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => handleModalVisible(false)}
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
          comList={comListArray}
        />
      ) : null}
      {formValues && Object.keys(formValues).length ? (
        <Passwd
          onSubmit={async (value) => {
            const success = await changePasswd(value);

            if (success) {
              setModalVisible(false);
              setFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            setModalVisible(false);
            setFormValues({});
          }}
          modalVisible={modalVisible}
          values={formValues}
          hideOld
        />
      ) : null}
      {orgId !== '' ? (
        <AddUserList
          onCancel={() => handleAddModalVisible(false)}
          modalVisible={addModalVisible}
          onSubmit={async (value) => {
            const success = await addUser(value);

            if (success) {
              handleAddModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
            return success;
          }}
          addModalVisible={addModalVisible}
          values={{orgId, archId, comId, domainId: listDomainId}}
        />
      ) : null}

      <UserPermissionView
        visible={permissionModalVisible}
        onCancel={() => {
          setPermissionModalVisible(false);
          setPermissionUser(null);
        }}
        userId={permissionUser?.id}
        userName={permissionUser?.nickname || permissionUser?.loginName || permissionUser?.login_name}
        writable={permissionUser?.writable !== false}
      />

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.nickname && (
          <ProDescriptions
            column={2}
            title={row?.nickname}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.nickname,
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
})(UserList);
