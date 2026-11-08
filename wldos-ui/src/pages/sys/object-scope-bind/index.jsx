import { PlusOutlined, InfoCircleOutlined } from '@ant-design/icons';
import { Button, message, Tag, Tooltip, Space } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import FullscreenModal from '@/components/FullscreenModal';
import { Form, Input, Select } from 'antd';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import { addEntity, fetchDimensionCatalog, queryPage, updateEntity } from './service';

const formLayout = { labelCol: { span: 6 }, wrapperCol: { span: 16 } };

const ObjectScopeBindAdmin = () => {
  const actionRef = useRef();
  const [form] = Form.useForm();
  const [visible, setVisible] = useState(false);
  const [editing, setEditing] = useState(null);
  const [dimensionOptions, setDimensionOptions] = useState([]);
  useDesktopSticky(actionRef);

  useEffect(() => {
    fetchDimensionCatalog({ current: 1, pageSize: 200 }).then((res) => {
      const rows = res?.data?.rows || [];
      setDimensionOptions(
        rows
          .filter((item) => item.enabled === '1')
          .map((item) => ({
            label: `${item.dimensionName || item.dimensionCode} (${item.dimensionCode})`,
            value: item.dimensionCode,
          })),
      );
    });
  }, []);

  const openModal = (record) => {
    setEditing(record || null);
    form.resetFields();
    if (record) {
      form.setFieldsValue(record);
    }
    setVisible(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const payload = editing ? { ...editing, ...values } : values;
    const fn = editing ? updateEntity : addEntity;
    const res = await fn(payload);
    if (res?.code !== 200) {
      return;
    }
    message.success(editing ? '已更新' : '已登记');
    setVisible(false);
    actionRef.current?.reload();
  };

  const columns = [
    {
      title: '实体',
      dataIndex: 'entityKey',
      width: 160,
    },
    {
      title: '业务字段',
      dataIndex: 'dimensionCode',
      width: 140,
      valueType: 'select',
      fieldProps: { options: dimensionOptions, showSearch: true },
    },
    {
      title: '物理列',
      dataIndex: 'columnName',
      width: 140,
      hideInSearch: true,
    },
    {
      title: 'join',
      dataIndex: 'joinTemplate',
      ellipsis: true,
      hideInSearch: true,
      render: (v) => v || '—',
    },
    {
      title: '状态',
      dataIndex: 'isValid',
      width: 90,
      hideInSearch: true,
      render: (v) => (v === '1' ? <Tag color="green">有效</Tag> : <Tag>无效</Tag>),
    },
    {
      title: '操作',
      width: 100,
      hideInSearch: true,
      render: (_, record) => <a onClick={() => openModal(record)}>编辑</a>,
    },
  ];

  return (
    <PageContainer {...adminListPageContainerProps}>
      <ProTableX
        headerTitle={(
          <Space>
            实体字段绑定
            <Tooltip title="指定业务表哪些列参与字段过滤；未绑定的表自动跳过">
              <InfoCircleOutlined style={{ color: '#999', fontSize: 14 }} />
            </Tooltip>
          </Space>
        )}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => openModal(null)}>
            登记绑定
          </Button>,
        ]}
        request={async (params) => {
          const res = await queryPage({
            current: params.current,
            pageSize: params.pageSize,
            entityKey: params.entityKey,
            dimensionCode: params.dimensionCode,
          });
          return {
            data: res?.data?.rows || [],
            total: res?.data?.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={columns}
      />
      <FullscreenModal
        title={editing ? '编辑实体字段绑定' : '登记实体字段绑定'}
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={handleSubmit}
        width={720}
        bodyStyle={{ padding: 24 }}
      >
        <Form form={form} {...formLayout}>
          <Form.Item
            name="entityKey"
            label="实体"
            rules={[{ required: true }]}
            extra="Java 实体类名，如 StoreOrder、WoUser"
          >
            <Input placeholder="StoreOrder" disabled={!!editing} />
          </Form.Item>
          <Form.Item
            name="dimensionCode"
            label="业务字段"
            rules={[{ required: true }]}
          >
            <Select
              showSearch
              optionFilterProp="label"
              options={dimensionOptions}
              disabled={!!editing}
              placeholder="选择已启用的业务字段"
            />
          </Form.Item>
          <Form.Item
            name="columnName"
            label="物理列"
            rules={[{ required: true }]}
            extra="通常为 snake_case，与 dimension_code 同名即可"
          >
            <Input placeholder="biz_code" />
          </Form.Item>
          <Form.Item
            name="joinTemplate"
            label="join 模板"
            extra="可选；复杂关联时填写，留空则直接用主表列"
          >
            <Input placeholder="留空" />
          </Form.Item>
          <Form.Item name="isValid" label="状态" initialValue="1">
            <Select
              options={[
                { label: '有效', value: '1' },
                { label: '无效', value: '0' },
              ]}
            />
          </Form.Item>
        </Form>
      </FullscreenModal>
    </PageContainer>
  );
};

export default ObjectScopeBindAdmin;
