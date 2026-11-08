import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Drawer, message, Popconfirm} from 'antd';
import React, {useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import DomainAddAdmin from "@/pages/sys/domain/components/DomainAddAdmin";
import {queryDomainAdmins, unbindDomainAdmins} from "@/pages/sys/domain/service";

const cancelDomainAdmin = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await unbindDomainAdmins({
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

const DomainAdminList = (props) => {
  const {domainId = '', comId, bindAdmins} = props;
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [addModalVisible, handleAddModalVisible] = useState(false);

  const columns = [
    {
      title: '昵称',
      dataIndex: 'nickname',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '账号',
      dataIndex: 'loginName',
      tip: '登录用户名',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      tip: '用于找回密码',
    },
    {
      title: '状态',
      dataIndex: 'status',
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
      valueType: 'textarea',
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      hideInSearch: true,
      valueType: 'string',
    },
  ];
  return (
    <PageContainer>
      <ProTable
        headerTitle="站点管理员"
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
          const res = await queryDomainAdmins({
            ...params,
            domainId,
            sorter,
            filter
          });
          const page = res?.data?.rows != null ? res.data : (res?.data?.data || {});
          return Promise.resolve({
            total: page?.total || 0,
            data: page?.rows || [],
            success: res?.code === 200,
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
                        await cancelDomainAdmin(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      {domainId !== '' ? (
        <DomainAddAdmin
          onCancel={() => handleAddModalVisible(false)}
          modalVisible={addModalVisible}
          onSubmit={async (value) => {
            const success = await bindAdmins(value);

            if (success) {
              handleAddModalVisible(false);

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          values={{domainId, comId}}
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

export default DomainAdminList;
