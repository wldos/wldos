import { Button } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { FooterToolbar } from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import { fetchAppOrigins, fetchAppType, querySelectPage } from '@/pages/sys/app/service';
import { selectToEnum } from '@/utils/utils';

const AddAppList = (props) => {
  const { values, onSubmit: addApp, modalVisible, onCancel } = props;
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const { domainId = '', comId = '' } = values;
  const [appTypeEnum, setAppTypeEnum] = useState({});
  const [appOriginsEnum, setAppOriginsEnum] = useState({});

  useEffect(() => {
    if (!modalVisible) {
      return;
    }
    let cancelled = false;
    (async () => {
      const [appType, appOrigins] = await Promise.all([fetchAppType(), fetchAppOrigins()]);
      if (cancelled) {
        return;
      }
      setAppTypeEnum(selectToEnum(appType?.data || []));
      setAppOriginsEnum(selectToEnum(appOrigins?.data || []));
    })();
    return () => {
      cancelled = true;
    };
  }, [modalVisible]);

  const handleAdd = async (selectedRows) => {
    await addApp({
      ids: selectedRows.map((row) => row.id),
      domainId,
      comId,
    });
  };

  const columns = useMemo(
    () => [
      {
        title: '应用名称',
        dataIndex: 'appName',
      },
      {
        title: '应用编码',
        dataIndex: 'appCode',
        hideInSearch: true,
      },
      {
        title: '应用描述',
        dataIndex: 'appDesc',
        valueType: 'textarea',
        hideInSearch: true,
      },
      {
        title: '站点挂载',
        dataIndex: 'domainMounted',
        hideInTable: true,
        valueType: 'select',
        valueEnum: {
          1: { text: '已添加' },
          0: { text: '未添加' },
        },
      },
      {
        title: '挂载状态',
        dataIndex: 'selected',
        hideInSearch: true,
        valueEnum: {
          true: { text: '已添加', status: 'Success' },
          false: { text: '未添加', status: 'Default' },
        },
      },
      {
        title: '管理类型',
        dataIndex: 'appType',
        valueType: 'select',
        valueEnum: appTypeEnum,
      },
      {
        title: '应用来源',
        dataIndex: 'appOrigin',
        valueType: 'select',
        valueEnum: appOriginsEnum,
      },
      {
        title: '应用状态',
        dataIndex: 'isValid',
        hideInForm: true,
        valueType: 'select',
        valueEnum: {
          0: {
            text: '无效',
            status: 'Error',
          },
          1: {
            text: '有效',
            status: 'Success',
          },
        },
      },
    ],
    [appTypeEnum, appOriginsEnum],
  );

  return (
    <FullscreenModal
      width="fit-content"
      destroyOnClose
      title="添加应用"
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      bodyStyle={{
        padding: '24px',
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
            domainId,
            sorter,
            filter,
          });
          return {
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: res?.code === 200,
          };
        }}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          getCheckboxProps: (record) => ({
            disabled: record.selected === true,
          }),
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
