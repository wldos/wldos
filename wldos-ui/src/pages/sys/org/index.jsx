import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useIntl} from 'umi';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
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
import {getComSelectOption} from "@/pages/sys/com/service";
import {getArchList} from "@/pages/sys/arch/service";
import {selectToEnum} from "@/utils/utils";

const OrgList = () => {
  const intl = useIntl();

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

    if (fields.children) {
      message.info(intl.formatMessage({ id: 'sys.org.msg.hasChildren', defaultMessage: '存在子节点，请先删除子节点' }));
      return true;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.delete', defaultMessage: '正在删除' }));
    try {
      await removeEntity({
        id: fields.id,
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

  const addUser = useCallback(async (value={ids: [], orgId: '', archId: '', comId: ''}) => {
    if (!!value && value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'sys.org.msg.selectMembers', defaultMessage: '请选择要添加的成员！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.org.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await addOrgUser({
        userIds: value.ids,
        orgId: value.orgId,
        archId: value.archId,
        comId: value.comId
      });
      hide();
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
  const [comList, setComList] = useState({});
  const [comListArray, setComListArray] = useState([]);
  const [orgTypeEnum, setOrgType] = useState({});
  const [orgTypes, setOrgTypeList] = useState([]);
  const [archList, setArchList] = useState({});
  const [archs, setArchs] = useState([]);
  const [addUserModalVisible, handleAddUserModalVisible] = useState(false);
  const [addUserValues, setAddUserValues] = useState({});

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(async () => {
    const roleData = await getOrgList();

    let data = {};
    let temp = [];
    let arr = roleData?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.orgName,
      }
      temp.push({label: item.orgName, value: item.id});
    }
    setRoleList(data);
    setRoles(temp);

    const comData = await getComSelectOption();
    arr = comData?.data?? [];
    setComList(selectToEnum(arr));
    setComListArray(arr);

    const orgType = await fetchOrgType();
    data = {};
    arr = orgType?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.value] = {
        text: item.label,
      }
    }
    setOrgType(data);
    setOrgTypeList(orgType.data);

    const archData = await getArchList();
    data = {};
    temp = [];
    arr = archData?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.archName,
      }
      temp.push({label: item.archName, value: item.id});
    }
    setArchList(data);
    setArchs(temp);
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

  const queryExistRes = async (orgId) => {
    return getExistRes({orgId});
  };

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.org.col.orgName', defaultMessage: '组织名称' }),
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
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
      width: '17%'
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
      filters: true,
      onFilter: false,
      valueEnum: orgTypeEnum,
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
      valueEnum: comList,
      width: '17%'
    },
    {
      title: intl.formatMessage({ id: 'sys.org.col.archId', defaultMessage: '归属体系' }),
      dataIndex: 'archId',
      filters: true,
      onFilter: false,
      valueEnum: archList,
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
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.config', defaultMessage: '配置' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              queryExistRes(record.id).then(res => {
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
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleAddUserModalVisible(true);
              setAddUserValues(record);
            }}
          >
            {intl.formatMessage({ id: 'sys.org.action.staff', defaultMessage: '人员' })}
          </a>
          <Divider type="vertical"/>
          <Popconfirm title={intl.formatMessage({ id: 'sys.org.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
           onConfirm={async () => {
             await handleRemoveOne(record);
             actionRef.current?.reloadAndRest?.();
           }}
          >
            <a>{intl.formatMessage({ id: 'sys.org.action.delete', defaultMessage: '删除' })}</a>
          </Popconfirm>
        </>
      ),
    },
  ], [intl, mobile, orgTypeEnum, roleList, comList, archList, handleRemoveOne]);

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
          headerTitle={intl.formatMessage({ id: 'sys.org.headerTitle', defaultMessage: '组织清单' })}
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.org.toolbar.new', defaultMessage: '新建' })}
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
          archs={archs}
          orgTypes={orgTypes}
          comList={comListArray}
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
          archs={archs}
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
      /> : null}

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

export default OrgList;
