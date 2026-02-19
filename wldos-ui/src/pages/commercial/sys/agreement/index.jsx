import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Divider, message, Popconfirm, Select, Switch } from 'antd';
import React, { useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';
import {
  queryAgreementList,
  addAgreement,
  updateAgreement,
  deleteAgreement,
  toggleAgreementActive,
} from './service';

const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');
  try {
    const res = await addAgreement(fields);
    hide();
    if (res?.code === 200) {
      message.success('添加成功');
      return true;
    }
    message.error(res?.message || '添加失败');
    return false;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }
};

const handleUpdate = async (fields) => {
  const hide = message.loading('正在更新');
  try {
    const res = await updateAgreement(fields);
    hide();
    if (res?.code === 200) {
      message.success('更新成功');
      return true;
    }
    message.error(res?.message || '更新失败');
    return false;
  } catch (error) {
    hide();
    message.error('更新失败请重试！');
    return false;
  }
};

const handleRemove = async (record) => {
  const hide = message.loading('正在删除');
  try {
    await deleteAgreement(record.id);
    hide();
    message.success('删除成功');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败请重试！');
    return false;
  }
};

const handleToggleActive = async (record, active) => {
  const hide = message.loading('正在更新');
  try {
    await toggleAgreementActive(record.id, active);
    hide();
    message.success(active ? '已启用' : '已停用');
    return true;
  } catch (error) {
    hide();
    message.error('操作失败请重试！');
    return false;
  }
};

const AgreementTypeOptions = [
  { value: 'LOGIN', label: '登录协议' },
  { value: 'ORDER', label: '下单协议' },
  { value: 'LEGAL', label: '法律声明' },
];

const AgreementList = () => {
  const [createModalVisible, handleCreateModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();

  useDesktopSticky(actionRef);

  const columns = [
    {
      title: '协议编码',
      dataIndex: 'agreementCode',
      width: 160,
      formItemProps: {
        rules: [{ required: true, message: '请输入协议编码' }],
      },
    },
    {
      title: '协议类型',
      dataIndex: 'agreementType',
      width: 120,
      valueEnum: {
        LOGIN: { text: '登录协议' },
        ORDER: { text: '下单协议' },
        LEGAL: { text: '法律声明' },
      },
      formItemProps: {
        rules: [{ required: true, message: '请选择协议类型' }],
      },
    },
    {
      title: '标题',
      dataIndex: 'title',
      ellipsis: true,
      formItemProps: {
        rules: [{ required: true, message: '请输入标题' }],
      },
    },
    {
      title: '版本',
      dataIndex: 'version',
      width: 80,
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '状态',
      dataIndex: 'isActive',
      width: 100,
      hideInForm: true,
      render: (_, record) => (
        <Switch
          checked={record.isActive === '1'}
          onChange={(checked) => {
            handleToggleActive(record, checked).then(() => {
              actionRef.current?.reload?.();
            });
          }}
        />
      ),
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      width: 100,
      hideInSearch: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 180,
      render: (_, record) => (
        <>
          <a
            onClick={() => {
              setStepFormValues(record);
              handleUpdateModalVisible(true);
            }}
          >
            编辑
          </a>
          <Divider type="vertical" />
          <Popconfirm
            title="确定删除该协议？"
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
              const ok = await handleRemove(record);
              if (ok) actionRef.current?.reload?.();
            }}
          >
            <a style={{ color: '#ff4d4f' }}>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <PageContainer
      style={{
        padding: '0',
        margin: '0',
      }}
      bodyStyle={{
        padding: '24px',
        margin: '0',
      }}
    >
      <ProTableX
        headerTitle="服务协议"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Button
            key="add"
            type="primary"
            onClick={() => {
              setStepFormValues({});
              handleCreateModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sorter) => {
          const res = await queryAgreementList({
            current: params.current,
            pageSize: params.pageSize,
            agreementType: params.agreementType,
            isActive: params.isActive,
            sorter: sorter && Object.keys(sorter).length ? JSON.stringify(sorter) : undefined,
          });
          const data = res?.data || {};
          return {
            data: data.rows || [],
            total: data.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={columns}
      />
      <CreateForm
        modalVisible={createModalVisible}
        onSubmit={async (values) => {
          const success = await handleAdd(values);
          if (success) {
            handleCreateModalVisible(false);
            actionRef.current?.reload?.();
          }
        }}
        onCancel={() => handleCreateModalVisible(false)}
      />
      <UpdateForm
        modalVisible={updateModalVisible}
        values={stepFormValues}
        onSubmit={async (values) => {
          const success = await handleUpdate({ ...values, id: stepFormValues.id });
          if (success) {
            handleUpdateModalVisible(false);
            setStepFormValues({});
            actionRef.current?.reload?.();
          }
        }}
        onCancel={() => {
          handleUpdateModalVisible(false);
          setStepFormValues({});
        }}
      />
    </PageContainer>
  );
};

export default AgreementList;
