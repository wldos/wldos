import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
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
const handleRemove = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeEntitys({
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
 *  删除节点
 */
const handleRemoveOne = async (fields) => {
  if (!fields) return true;

  if (fields.children) {
    message.info("存在子节点，请先删除子节点");
    return true;
  }
  const hide = message.loading('正在删除');
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
const handleAuth = async (fields = {roleIds: [], orgId: '', archId: '', comId: ''}, existRes = []) => {
  if (existRes?.length === fields.roleIds?.length &&
      fields.roleIds.every(id => existRes.some(eid => eid === id))) {
    message.info('没有任何改变，不做操作！');
    return false;
  }

  const hide = message.loading('正在授权');

  try {
    await authRole({
      roleIds: fields.roleIds,
      orgId: fields.orgId,
      archId: fields.archId,
      comId: fields.comId,
    });
    hide();
    message.success('授权成功');
    return true;
  } catch (error) {
    hide();
    message.error('授权失败请重试！');
    return false;
  }
};
const addUser = async (value={ids: [], orgId: '', archId: '', comId: ''}) => {
  if (!!value && value.ids?.length === 0) {
    message.info('请选择要添加的成员！');
    return false;
  }

  const hide = message.loading('正在添加');

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
      message.success('添加成功');
    return true;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }

};

const OrgList = () => {
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [authModalVisible, handleAuthModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [parentId, setParentId] = useState('0');
  const [currentRecord, setCurrentRecord] = useState(null); // 存储当前记录信息
  const [roleList, setRoleList] = useState({});
  const [roles, setRoles] = useState([]);
  const [resTree, setResTree] = useState([]);
  const [existRes, setAuthRes] = useState([]);
  const [authRoleValues, setAuthRoleValues] = useState({});
  const [comList, setComList] = useState({});
  const [comListArray, setComListArray] = useState([]); // 用于表单的数组格式
  const [orgTypeEnum, setOrgType] = useState({});
  const [orgTypes, setOrgTypeList] = useState([]); // 同名不能正常显示
  const [archList, setArchList] = useState({});
  const [archs, setArchs] = useState([]);
  const [addUserModalVisible, handleAddUserModalVisible] = useState(false);
  const [addUserValues, setAddUserValues] = useState({});

  // 移动端检测
  const mobile = isMobile();

  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  // 使用桌面端粘性布局 - 重新启用，已移除推挤逻辑
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
    setComList(selectToEnum(arr)); // 用于表格的枚举值
    setComListArray(arr); // 用于表单的数组格式

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

  // 监听容器宽度变化
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

  const columns = [
    {
      title: '组织名称',
      dataIndex: 'orgName',
      tip: '组织是业务域运营的人员管理',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '组织名称为必填项',
          },
          {
            max: 60,
            type: 'string',
            message: '最多60个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
      width: '17%'
    },
    {
      title: '组织编码',
      dataIndex: 'orgCode',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '组织编码为必填项',
          },
          {
            max: 32,
            type: 'string',
            message: '最多32位',
          },
        ],
      },
    },
    {
      title: '组织类型',
      dataIndex: 'orgType',
      filters: true,
      onFilter: false,
      valueEnum: orgTypeEnum,
    },
    {
      title: '上级组织',
      dataIndex: 'parentId',
      hideInTable: true,
      hideInForm: true,
      valueEnum: roleList,
    },
    {
      title: '归属公司',
      dataIndex: 'comId',
      filters: true,
      onFilter: false,
      hideInForm: true,
      valueEnum: comList,
      width: '17%'
    },
    {
      title: '归属体系',
      dataIndex: 'archId',
      filters: true,
      onFilter: false,
      valueEnum: archList,
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '状态',
      dataIndex: 'isValid',
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
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          <a
            onClick={() => {
              // 子级：父组织=当前记录，组织类型=当前记录的组织类型，归属公司=当前记录的归属公司，归属体系=当前记录的归属体系
              setCurrentRecord({
                parentId: record.id,
                orgType: record.orgType,
                comId: record.comId,
                archId: record.archId
              });
              handleModalVisible(true);
            }}
          >
            子级
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            // 同级：父组织=当前记录的父组织，组织类型=当前记录的组织类型，归属公司=当前记录的归属公司，归属体系=当前记录的归属体系
            setCurrentRecord({
              parentId: record.parentId,
              orgType: record.orgType,
              comId: record.comId,
              archId: record.archId
            });
            handleModalVisible(true);
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
            授权
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleAddUserModalVisible(true);
              setAddUserValues(record);
            }}
          >
            人员
          </a>
          <Divider type="vertical"/>
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
           onConfirm={async () => {
             await handleRemoveOne(record);
             actionRef.current?.reloadAndRest?.();
           }}
          >
            <a>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];

  // 计算列总宽度 - 动态计算
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
          headerTitle="组织清单"
          actionRef={actionRef}
          rowKey="id"
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
          columns={columns}
          rowSelection={{
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          }}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
          }}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
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
            </div>
          }
        >
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>
              批量删除
            </Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)}
                  modalVisible={createModalVisible}>
        <CreateFormContent
          onSubmit={async (value) => {
            // 使用 currentRecord 中的 parentId，如果没有则使用默认的 parentId
            const finalParentId = currentRecord?.parentId || parentId;
            const success = await handleAdd({...value, parentId: finalParentId});

            if (success) {
              handleModalVisible(false);
              setParentId('0');
              setCurrentRecord(null); // 清空当前记录

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setParentId('0');
            setCurrentRecord(null); // 清空当前记录
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
