import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useRef, useState, useEffect} from 'react';
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
  queryPage,
  queryOrgPage,
  removeEntity,
  removeEntities,
  removeOrgStaff,
  updateEntity,
  updatePasswd4admin
} from './service';
import AddUserList from "@/pages/sys/user/components/add";
import Passwd from "@/pages/account/settings/components/Passwd";

/**
 * 添加节点
 * @param fields
 */
const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');

  try {
    const res = await addEntity({...fields});
    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
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
      nickname: fields.nickname,
      remark: fields.remark,
      loginName: fields.loginName,
      passwd: fields.passwd,
      status: fields.status,
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

const cancelOrgStaff = async (selectedRows, orgId) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeOrgStaff({
      ids: selectedRows.map((row) => row.id),
      orgId
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
 * @param handleRemoveOne
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

const changePasswd = async (values) => {
  const {nickname, id, password, confirm} = values;

  try {
    const res = await updatePasswd4admin({id, password, confirm});

    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
    message.info(`修改用户${nickname}密码成功！`);
    return true;
  } catch (error) {
    message.error('修改失败请重试！');
    return false;
  }
};

const UserList = (props) => {
  const {orgId = '', archId = '', comId = '', addUser} = props;
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [addModalVisible, handleAddModalVisible] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [modalVisible, setModalVisible] = useState(false);
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

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

  // 计算列总宽度
  const totalColsWidth = 1000; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '昵称',
      dataIndex: 'nickname',
      tip: '用户指注册会员，可以是自然人或者法人，不同身份可以认证',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '昵称为必填项',
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
    },
    {
      title: '账号',
      dataIndex: 'loginName',
      tip: '登录用户名，建议使用邮箱',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '账号为必填项',
          },
          {
            max: 30,
            type: 'string',
            message: '最大30个字符',
          },
        ],
      },
    },
    {
      title: '密码',
      dataIndex: 'passwd',
      hideInSearch: true,
      hideInTable: true,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '密码为必填项,最多120位字符',
            max: 120,
          },
        ],
      },
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      tip: '用于找回密码',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '邮箱为必填项',
          },
          {
            max: 50,
            type: 'string',
            message: '最大50个字符',
          },
        ],
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        locked: {
          text: '已锁定',
        },
        cancelled: {
          text: '已注销',
        },
        normal: {
          text: '正常',
        }
      },
    },
    {
      title: '简介',
      dataIndex: 'remark',
      hideInForm: true,
      valueType: 'textarea',
      width: '16%'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: orgId !== '',
      hideInTable: orgId !== '',
    },
    {
      title: '用户ID',
      dataIndex: 'id',
      hideInForm: true,
      valueType: 'string',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      hideInTable: orgId !== '',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
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
              setModalVisible(true);
              setFormValues(record);
            }}
          >
            更新密码
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
          headerTitle="用户清单"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={1} type="primary" hidden={orgId === ''}  onClick={() => handleAddModalVisible(true)}>
              <PlusOutlined/> 添加
            </Button>,
            <Button key={0} type="primary" hidden={orgId !== ''} onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> 新增
            </Button>,
          ]}
          request={async (params, sorter, filter) => {
            const res = (orgId && archId && comId) ? await queryOrgPage({
              ...params,
              orgId, archId, comId,
              sorter,
              filter
            }) : await queryPage({
              ...params,
              sorter,
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
                        if (orgId === '') {
                          await handleRemove(selectedRowsState);
                        } else {
                          await cancelOrgStaff(selectedRowsState, orgId);
                        }
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
            const success = await handleAdd(value);

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
          }}
          addModalVisible={addModalVisible}
          values={{orgId, archId, comId}}
        />) : null}

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

export default UserList;
