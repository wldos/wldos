import {Button} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, {useRef, useState} from 'react';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import {querySelectPage} from "@/pages/sys/user/service";
import OrgTreeSelect from "@/components/TreeSelects/OrgTreeSelect";

const AddUserList = (props) => {
  const {
    values,
    onSubmit: addUser,
    modalVisible,
    onCancel,
    title = '添加成员',
    submitText = '添加成员',
    extraContent,
  } = props;
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const { orgId = '', archId = '', comId = '', domainId, scopeComId: scopeComIdProp } = values;
  const candidateScopeComId = scopeComIdProp || comId;

  const handleAdd = async (selectedRows) => {
    return addUser({
      ids: selectedRows.map((row) => row.id),
      orgId,
      archId,
      comId,
      ...(domainId ? { domainId } : {}),
    });
  };

  const columns = [
    {
      title: '昵称',
      dataIndex: 'nickname',
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
    },
    {
      title: '用户ID',
      dataIndex: 'id',
      hideInForm: true,
      valueType: 'string',
    },
    {
      title: '账号',
      dataIndex: 'loginName',
      tip: '登录用户名',
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
      title={title}
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px'
      }}
    >
      {extraContent}
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
            scopeComId: candidateScopeComId || undefined,
            ...(domainId ? { domainId } : {}),
            filterOrgId: params.filterOrgId || undefined,
            ...(orgId ? { candidateProfile: 'orgMember', excludeOrgId: orgId } : {}),
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
              const ok = await handleAdd(selectedRowsState);
              if (ok === false) {
                return;
              }
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
              onCancel?.();
            }}
          >
            {submitText}
          </Button>
        </FooterToolbar>
      )}
    </FullscreenModal>
  );
};

export default AddUserList;
