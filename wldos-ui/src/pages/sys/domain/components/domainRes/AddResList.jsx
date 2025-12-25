import {Button, message} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, {useRef, useState} from 'react';
import {FooterToolbar} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import {querySelectPage} from "@/pages/sys/res/service";


const AddResList = (props) => {
  const {
    values,
    onSubmit: addRes,
    modalVisible,
    onCancel,
  } = props;
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const {domainId = ''} = values;
  const handleAdd = async (selectedRows) => {
    await addRes({
      ids: selectedRows.map((row) => row.id),
      domainId,
    });
  };

  const columns = [
    {
      title: '资源名称',
      dataIndex: 'resourceName',
    },
    {
      title: '资源描述',
      dataIndex: 'remark',
      valueType: 'textarea',
      width: '50%'
    },
    {
      title: '资源状态',
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
      title="添加资源"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px'
      }}
    >
      <ProTable
        headerTitle="资源清单"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        request={async (params, sorter, filter) => {
          const res = await querySelectPage({
            ...params,
            domainId,
            sorter,
            filter
          });
          if (!res?.data?.total)
            message.warn('无资源可添加，请确认是否已定制应用');
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
          getCheckboxProps: (record) => ({
            disabled: record.selected === true, // 只有已添加到域的资源才禁用，父节点选中不影响子节点
          }),
          // 对于树形结构，父子节点独立选择
          checkStrictly: true, // 父子节点不关联，可以独立选择
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
            添加资源
          </Button>
        </FooterToolbar>
      )}
    </FullscreenModal>
  );
};

export default AddResList;
