import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
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
 * @param selectedRows
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
/**
 * 角色授权
 * @param fields
 */
const handleAuth = async (fields = {roleIds: [], roleId: ''}, existRes = []) => {
  if (existRes?.length === fields.roleIds?.length && fields.roleIds.every(id => existRes.some(eid => eid === id))) {
    message.info('没有任何改变，不做操作！');
    return false;
  }

  const hide = message.loading('正在授权');

  try {
    await authRole({
      resIds: fields.resIds,
      roleId: fields.roleId,
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

const RoleList = () => {
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
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局 - 使用原版
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

  const queryExistRes = async (roleId) => {
    const res = await getExistRes({roleId});

    return res;
  };

  const columns = [
    {
      title: '角色名称',
      dataIndex: 'roleName',
      tip: '角色的显示名称，用于界面展示',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '角色名称为必填项',
          },
          {
            max: 25,
            type: 'string',
            message: '最多25个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      tip: '角色的唯一标识，用于系统识别',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '角色编码为必填项',
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
      title: '描述',
      dataIndex: 'roleDesc',
      valueType: 'textarea',
      tip: '角色的详细说明，帮助理解角色用途',
      width: '17%',
      formItemProps: {
        rules: [
          {
            max: 150,
            type: 'string',
            message: '最多150个字',
          },
        ],
      },
    },
    {
      title: '角色类型',
      dataIndex: 'roleType',
      tip: '角色的分类，影响权限范围',
      filters: true,
      onFilter: false,
      valueEnum: {
        'sys_role': {
          text: '系统角色',
        },
        'subject': {
          text: '社会主体',
        },
        'tal_role': {
          text: '租户角色',
        },
      },
    },
    {
      title: '父角色',
      dataIndex: 'parentId',
      tip: '角色的上级角色，用于角色层级管理',
      hideInTable: true,
      hideInForm: false,
      valueEnum: roleList,
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      tip: '角色在列表中的显示顺序，1-100',
      hideInSearch: true,
      sorter: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '展示顺序为必填项',
          },
          {
            pattern: '^([1-9]|[1-9]\\d|100)$',
            message: '请输入1-100之间的数字',
          },
        ],
      },
    },
    {
      title: '状态',
      dataIndex: 'isValid',
      tip: '角色的启用状态',
      hideInForm: false,
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
              // 子级：父角色=当前记录，角色类型=当前记录的角色类型
              setCurrentRecord({
                parentId: record.id,
                roleType: record.roleType
              });
              handleModalVisible(true);
            }}
          >
            子级
          </a>
          <Divider type="vertical"/>
          <a onClick={() => {
            // 同级：父角色=当前记录的父角色，角色类型=当前记录的角色类型
            setCurrentRecord({
              parentId: record.parentId,
              roleType: record.roleType
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
            授权
          </a>
          <Divider type="vertical"/>
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
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
          headerTitle="角色清单"
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
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
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