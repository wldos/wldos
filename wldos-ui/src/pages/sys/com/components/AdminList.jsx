import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Drawer, message, Popconfirm} from 'antd';
import React, {useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import AddAdminList from "@/pages/sys/com/components/AddAdmin";
import {queryComPage} from "@/pages/sys/user/service";
import {removeComAdmin} from "@/pages/sys/com/service";

const cancelComAdmin = async (selectedRows, userComId) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeComAdmin({
      ids: selectedRows.map((row) => row.id),
      userComId
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

const AdminList = (props) => {
  const {userComId = '', addTAdmin} = props;
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [addModalVisible, handleAddModalVisible] = useState(false);

  const columns = [
    {
      title: '昵称',
      dataIndex: 'nickname',
      tip: '用户指注册会员，可以是自然人或者法人，不同身份可以认证',
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
      tip: '登录用户名，请使用邮箱',
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
    },
    {
      title: '用户ID',
      dataIndex: 'id',
      hideInForm: true,
      valueType: 'string',
    },
  ];
  return (
    <PageContainer>
      <ProTable
        headerTitle="租户管理员"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button key={1} type="primary" onClick={() => handleAddModalVisible(true)}>
            <PlusOutlined/> 添加
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const res = await queryComPage({ // 查询租户管理员
            ...params,
            userComId,
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
      />
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
                        await cancelComAdmin(selectedRowsState, userComId);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      {userComId !== '' ? (
        <AddAdminList
          onCancel={() => handleAddModalVisible(false)}
          modalVisible={addModalVisible}
          onSubmit={async (value) => {
            const success = await addTAdmin(value);

            if (success) {
              handleAddModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          addModalVisible={addModalVisible}
          values={{userComId}}
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

export default AdminList;
