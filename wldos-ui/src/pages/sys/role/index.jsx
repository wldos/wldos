import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
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
  updateEntity
} from './service';
import AuthRes from "@/pages/sys/role/components/AuthRes";

const RoleList = () => {
  const intl = useIntl();

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      await addEntity({...fields});
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.role.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleUpdate = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.config', defaultMessage: '正在配置' }));

    try {
      await updateEntity({
        roleName: fields.roleName,
        roleCode: fields.roleCode,
        roleType: fields.roleType,
        parentId: fields.parentId,
        roleDesc: fields.roleDesc,
        isValid: fields.isValid,
        displayOrder: fields.displayOrder,
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.role.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    try {
      await removeEntitys({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.role.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (fields.children) {
      message.info(intl.formatMessage({ id: 'sys.role.msg.hasChildren', defaultMessage: '存在子节点，请先删除子节点' }));
      return true;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.delete', defaultMessage: '正在删除' }));
    try {
      await removeEntity({
        id: fields.id,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.role.msg.deleteFail', defaultMessage: '删除失败，请重试' }));
      return false;
    }
  }, [intl]);

  const handleAuth = useCallback(async (fields = {roleIds: [], roleId: ''}, existRes = []) => {
    if (existRes?.length === fields.roleIds?.length && fields.roleIds.every(id => existRes.some(eid => eid === id))) {
      message.info(intl.formatMessage({ id: 'sys.role.msg.authNoChange', defaultMessage: '没有任何改变，不做操作！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.role.msg.loading.auth', defaultMessage: '正在授权' }));

    try {
      await authRole({
        resIds: fields.resIds,
        roleId: fields.roleId,
      });
      hide();
      message.success(intl.formatMessage({ id: 'sys.role.msg.authSuccess', defaultMessage: '授权成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.role.msg.authFail', defaultMessage: '授权失败请重试！' }));
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
  const [parentId, setParentId] = useState('0');
  const [currentRecord, setCurrentRecord] = useState(null);
  const [roleList, setRoleList] = useState({});
  const [roles, setRoles] = useState([]);
  const [resTree, setResTree] = useState([]);
  const [existRes, setAuthRes] = useState([]);
  const [authRoleValues, setAuthRoleValues] = useState({});

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(async () => {
    const roleData = await getRoleList();
    const data = {};
    const temp = [];
    const arr = roleData?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.roleName,
      }
      temp.push({label: item.roleName, value: item.id});
    }
    setRoleList(data);
    setRoles(temp);
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

  const queryExistRes = async (roleId) => {
    const res = await getExistRes({roleId});

    return res;
  };

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
        return <a onClick={() => setRow(entity)}>{dom}</a>;
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
      tip: intl.formatMessage({ id: 'sys.role.col.displayOrder.tip', defaultMessage: '角色在列表中的显示顺序，1-100' }),
      hideInSearch: true,
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.role.rule.displayOrderRequired', defaultMessage: '展示顺序为必填项' }),
          },
          {
            pattern: '^([1-9]|[1-9]\\d|100)$',
            message: intl.formatMessage({ id: 'sys.role.rule.displayOrderRange', defaultMessage: '请输入1-100之间的数字' }),
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
      render: (_, record) => (
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
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.role.action.config', defaultMessage: '配置' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              queryExistRes(record.id).then(res => {
                if (res && res.data && res.data.authRes) {
                  const {roleRes = [], authRes = []} = res.data;
                  const temp = roleRes.map(item => item.id);
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
          <Divider type="vertical"/>
          <Popconfirm title={intl.formatMessage({ id: 'sys.role.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemoveOne(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>{intl.formatMessage({ id: 'sys.role.action.delete', defaultMessage: '删除' })}</a>
          </Popconfirm>
        </>
      ),
    },
  ], [intl, mobile, roleList, handleRemoveOne]);

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

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
          headerTitle={intl.formatMessage({ id: 'sys.role.headerTitle', defaultMessage: '角色清单' })}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.role.toolbar.new', defaultMessage: '新建' })}
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
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>{intl.formatMessage({ id: 'sys.role.footer.batchDelete', defaultMessage: '批量删除' })}</Button>
          </Popconfirm>
          <Button type="primary">{intl.formatMessage({ id: 'sys.role.footer.batchExport', defaultMessage: '批量导出' })}</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)}
                  modalVisible={createModalVisible}>
        <CreateFormContent
          onSubmit={async (value) => {
            const finalParentId = currentRecord?.parentId || parentId;
            const success = await handleAdd({...value, parentId: finalParentId});

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
          roles={roles}
          currentRecord={currentRecord}
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
          roles={roles}
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

export default RoleList;
