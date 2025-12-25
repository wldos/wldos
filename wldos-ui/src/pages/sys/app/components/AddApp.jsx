import {Button} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, {useRef, useState} from 'react';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import {querySelectPage} from "@/pages/sys/app/service";

const AddAppList = (props) => {
  const {
    values,
    onSubmit: addApp,
    modalVisible,
    onCancel,
  } = props;
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const {domainId = '', comId= ''} = values;

  const handleAdd = async (selectedRows) => {
    await addApp({
      ids: selectedRows.map((row) => row.id),
      domainId,
      comId
    });
  };

  const columns = [
    {
      title: '应用名称',
      dataIndex: 'appName',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '应用名称为必填项',
          },
          {
            max: 12,
            type: 'string',
            message: '最多12个字',
          },
        ],
      },
    },
    {
      title: '应用描述',
      dataIndex: 'appDesc',
      valueType: 'textarea',
    },
    {
      title: '应用状态',
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        0: {
          text: '无效',
          status: 'invalid',
        },
        1: {
          text: '有效',
          status: 'valid',
        },
      },
    },
  ];
  return (
    <FullscreenModal
      width={"fit-content"}
      destroyOnClose
      title="添加应用"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <ProTable
        headerTitle="应用清单"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        request={async (params, sorter, filter) => {
          const res = await querySelectPage({
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
            添加应用
          </Button>
        </FooterToolbar>
      )}
    </FullscreenModal>
  );
};

export default AddAppList;
