import {Button} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, {useRef, useState} from 'react';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import {querySelectPage} from "@/pages/sys/user/service";
import OrgTreeSelect from "@/components/TreeSelects/OrgTreeSelect";

const DomainAddAdmin = (props) => {
  const {
    values,
    onSubmit: bindAdmins,
    modalVisible,
    onCancel,
  } = props;
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const {domainId = '', comId} = values;

  const handleAdd = async (selectedRows) => {
    await bindAdmins({
      userIds: selectedRows.map((row) => row.id),
      domainId,
    });
  };

  const columns = [
    {
      title: '昵称',
      dataIndex: 'nickname',
    },
    {
      title: '用户ID',
      dataIndex: 'id',
      valueType: 'string',
    },
    {
      title: '账号',
      dataIndex: 'loginName',
      tip: '登录用户名',
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
      title: '所属组织',
      dataIndex: 'filterOrgId',
      hideInTable: true,
      renderFormItem: () => <OrgTreeSelect comId={comId || undefined} allowClear placeholder="按组织筛选" />,
    },
  ];
  return (
    <FullscreenModal
      width={"fit-content"}
      destroyOnClose
      title="添加站点管理员"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <ProTable
        headerTitle="用户清单"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        request={async (params, sorter, filter) => {
          const res = await querySelectPage({
            ...params,
            sorter,
            filter,
            domainId,
            scopeComId: comId || undefined,
            filterOrgId: params.filterOrgId || undefined,
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
          <Button
            onClick={async () => {
              await handleAdd(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            添加管理员
          </Button>
        </FooterToolbar>
      )}
    </FullscreenModal>
  );
};

export default DomainAddAdmin;
