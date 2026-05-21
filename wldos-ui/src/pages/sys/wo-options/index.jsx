import {
  PlusOutlined,
  QuestionCircleOutlined,
  EditOutlined,
  KeyOutlined,
  FileTextOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { Alert, Button, Card, Divider, Form, Input, message, Popconfirm, Select, Space } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { FooterToolbar, PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import FullscreenModal from '@/components/FullscreenModal';
import { fetchEnumWoOptionAppCode, fetchEnumWoOptionType } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';
import { addEntity, queryPage, removeEntities, removeEntity, updateEntity } from './service';

const WO_OPTIONS_FORM_LAYOUT = { labelCol: { span: 6 }, wrapperCol: { span: 16 } };

/** 本页表单与列表展示文案当前为中文直出；后续可接入 umi useIntl / locale 做统一国际化。 */



const WoOptionsAdmin = () => {

  const actionRef = useRef();

  const [form] = Form.useForm();

  const [modalVisible, setModalVisible] = useState(false);

  const [editing, setEditing] = useState(null);

  const [selectedRows, setSelectedRows] = useState([]);

  const [optionTypeValueEnum, setOptionTypeValueEnum] = useState({});

  const [appCodeValueEnum, setAppCodeValueEnum] = useState({});

  const [optionTypeSelectOptions, setOptionTypeSelectOptions] = useState([]);

  const [optionTypeModalOptions, setOptionTypeModalOptions] = useState([]);

  const [appCodeSelectOptions, setAppCodeSelectOptions] = useState([]);

  const [appCodeModalOptions, setAppCodeModalOptions] = useState([]);

  const mobile = isMobile();

  useDesktopSticky(actionRef);



  useEffect(async () => {

    try {

      const tRes = await fetchEnumWoOptionType();

      if (tRes?.data) {

        setOptionTypeValueEnum(selectToEnum(tRes.data));

        setOptionTypeSelectOptions(tRes.data.map((i) => ({ label: i.label, value: i.value })));

      }

      const aRes = await fetchEnumWoOptionAppCode();

      if (aRes?.data) {

        setAppCodeValueEnum(selectToEnum(aRes.data));

        setAppCodeSelectOptions(aRes.data.map((i) => ({ label: i.label, value: i.value })));

      }

    } catch (error) {

      message.error('加载枚举失败');

    }

  }, []);



  const openCreate = () => {

    setEditing(null);

    form.resetFields();

    setAppCodeModalOptions(appCodeSelectOptions);

    setOptionTypeModalOptions(optionTypeSelectOptions);

    form.setFieldsValue({

      optionType: 'auto_reload',

      appCode: 'sys_option',

    });

    setModalVisible(true);

  };



  const openEdit = (record) => {

    setEditing(record);

    const c = record.appCode;

    let modalAppOpts = appCodeSelectOptions;

    if (c != null && c !== '' && !appCodeSelectOptions.some((o) => o.value === c)) {

      modalAppOpts = [{ label: `${c}（当前值）`, value: c }, ...appCodeSelectOptions];

    }

    setAppCodeModalOptions(modalAppOpts);

    const ot = record.optionType;

    let modalTypeOpts = optionTypeSelectOptions;

    if (ot != null && ot !== '' && !optionTypeSelectOptions.some((o) => o.value === ot)) {

      modalTypeOpts = [{ label: `${ot}（当前值）`, value: ot }, ...optionTypeSelectOptions];

    }

    setOptionTypeModalOptions(modalTypeOpts);

    form.setFieldsValue({

      ...record,

      appCode: record.appCode == null || record.appCode === '' ? '' : record.appCode,

    });

    setModalVisible(true);

  };



  const validateOptionValue = (value) => {

    const v = (value || '').trim();

    if (!v) return true;

    if (v.startsWith('[') || v.startsWith('{')) {

      try {

        JSON.parse(v);

        return true;

      } catch {

        message.error('参数值若以 [、{ 开头须为合法的结构化格式');

        return false;

      }

    }

    return true;

  };



  const submitModal = async () => {

    try {

      const values = await form.validateFields();

      if (!validateOptionValue(values.optionValue)) return;

      const payload = { ...values };

      if (payload.appCode === '' || payload.appCode === undefined) {

        payload.appCode = null;

      }

      const hide = message.loading(editing ? '正在保存' : '正在新增');

      try {

        if (editing) {

          await updateEntity({ ...editing, ...payload });

        } else {

          await addEntity(payload);

        }

        hide();

        message.success(editing ? '保存成功' : '新增成功');

        setModalVisible(false);

        actionRef.current?.reload?.();

      } catch (err) {

        hide();

        message.error(editing ? '保存失败' : '新增失败');

      }

    } catch {

      // 校验失败

    }

  };



  const closeModal = () => setModalVisible(false);

  const renderModalFooter = () => (
    <Space>
      <Button onClick={closeModal}>取消</Button>
      <Button type="primary" onClick={submitModal}>
        {editing ? '保存' : '确定'}
      </Button>
    </Space>
  );



  const handleDeleteOne = async (record) => {

    const hide = message.loading('正在删除');

    try {

      await removeEntity({ id: record.id });

      hide();

      message.success('删除成功');

      actionRef.current?.reload?.();

    } catch {

      hide();

      message.error('删除失败');

    }

  };



  const handleBatchDelete = async () => {

    if (!selectedRows?.length) return;

    const hide = message.loading('正在删除');

    try {

      await removeEntities({ ids: selectedRows.map((r) => r.id) });

      hide();

      message.success('删除成功');

      setSelectedRows([]);

      actionRef.current?.reload?.();

    } catch {

      hide();

      message.error('删除失败');

    }

  };



  const columns = [

    {

      title: '配置标识',

      dataIndex: 'optionKey',

      ellipsis: true,

      width: 220,

    },

    {

      title: '名称',

      dataIndex: 'optionName',

      ellipsis: true,

      width: 160,

    },

    {

      title: '参数值',

      dataIndex: 'optionValue',

      ellipsis: true,

      hideInSearch: true,

      width: 260,

    },

    {

      title: '生效方式',

      dataIndex: 'optionType',

      width: 120,

      valueEnum: Object.keys(optionTypeValueEnum).length ? optionTypeValueEnum : undefined,

      render: (_, r) => optionTypeValueEnum[r.optionType]?.text ?? r.optionType,

    },

    {

      title: '所属应用',

      dataIndex: 'appCode',

      width: 130,

      valueEnum: Object.keys(appCodeValueEnum).length ? appCodeValueEnum : undefined,

      render: (_, r) => {

        const k = r.appCode == null ? '' : r.appCode;

        return appCodeValueEnum[k]?.text ?? (r.appCode == null || r.appCode === '' ? '（未指定）' : r.appCode);

      },

    },

    {

      title: '说明',

      dataIndex: 'description',

      ellipsis: true,

      hideInSearch: true,

      width: 180,

    },

    {

      title: '操作',

      valueType: 'option',

      width: 160,

      fixed: mobile ? undefined : 'right',

      render: (_, record) => (

        <>

          <a onClick={() => openEdit(record)}>编辑</a>

          <Divider type="vertical" />

          <Popconfirm

            title="删除可能影响运行中的系统行为，确定删除？"

            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}

            onConfirm={() => handleDeleteOne(record)}

          >

            <a>删除</a>

          </Popconfirm>

        </>

      ),

    },

  ];



  return (

    <PageContainer style={{ padding: 0, margin: 0 }} bodyStyle={{ padding: '24px', margin: 0 }}>

      <Alert

        type="warning"

        showIcon

        style={{ marginBottom: 16 }}

        message="全平台通用默认参数"

        description="此处配置对所有站点生效；生效节奏由各项的「生效方式」决定。请在确认含义后再修改；仅个别站点需要差异化时，请使用「站点系统参数」覆盖。"

      />

      <ProTableX

        headerTitle="全局系统选项"

        actionRef={actionRef}

        rowKey="id"

        search={{ labelWidth: 100 }}

        pagination={{

          defaultPageSize: 15,

          pageSizeOptions: ['10', '15', '20', '30', '50', '100'],

          showSizeChanger: true,

        }}

        toolBarRender={() => [

          <Button type="primary" key="add" onClick={openCreate}>

            <PlusOutlined /> 新建

          </Button>,

        ]}

        request={async (params) => {

          const res = await queryPage(params);

          return {

            data: res?.data?.rows ?? [],

            total: res?.data?.total ?? 0,

            success: true,

          };

        }}

        columns={columns}

        rowSelection={{

          onChange: (_, rows) => setSelectedRows(rows),

        }}

      />

      {selectedRows?.length > 0 && (

        <FooterToolbar>

          <Popconfirm

            title={`确定批量删除 ${selectedRows.length} 条？可能影响系统运行`}

            onConfirm={handleBatchDelete}

          >

            <Button danger>批量删除</Button>

          </Popconfirm>

        </FooterToolbar>

      )}

      <FullscreenModal
        width={880}
        bodyStyle={{ padding: '24px' }}
        destroyOnClose
        visible={modalVisible}
        onCancel={closeModal}
        footer={renderModalFooter()}
        title={
          <Space>
            {editing ? (
              <EditOutlined style={{ color: '#1890ff' }} />
            ) : (
              <PlusOutlined style={{ color: '#52c41a' }} />
            )}
            {editing ? '编辑全局系统选项' : '新建全局系统选项'}
          </Space>
        }
      >
        <Form form={form} preserve={false} layout="horizontal" {...WO_OPTIONS_FORM_LAYOUT}>
          <Card
            size="small"
            style={{ marginBottom: 16 }}
            title={
              <Space>
                <KeyOutlined style={{ color: '#1890ff' }} />
                基础信息
              </Space>
            }
          >
            <Form.Item
              name="optionKey"
              label="配置标识"
              extra="全平台内唯一，保存后不可修改"
              rules={[{ required: true, message: '请填写配置标识' }]}
            >
              <Input disabled={!!editing} placeholder="例如：portal_search_hints" />
            </Form.Item>
            <Form.Item name="optionName" label="显示名称" tooltip="列表中展示用，可选填">
              <Input placeholder="简短名称，便于识别" />
            </Form.Item>
          </Card>
          <Card
            size="small"
            style={{ marginBottom: 16 }}
            title={
              <Space>
                <FileTextOutlined style={{ color: '#1890ff' }} />
                参数内容
              </Space>
            }
          >
            <Form.Item
              name="optionValue"
              label="参数值"
              rules={[{ required: true, message: '请填写参数值' }]}
            >
              <Input.TextArea
                rows={12}
                placeholder="可为普通文本；若以 [ 或 { 开头，将按列表/对象格式做校验"
              />
            </Form.Item>
            <Form.Item name="description" label="说明" tooltip="用途、注意事项等">
              <Input.TextArea rows={2} placeholder="可选填" />
            </Form.Item>
          </Card>
          <Card
            size="small"
            title={
              <Space>
                <SettingOutlined style={{ color: '#1890ff' }} />
                生效与归属
              </Space>
            }
          >
            <Form.Item
              name="optionType"
              label="生效方式"
              rules={[{ required: true, message: '请选择生效方式' }]}
            >
              <Select
                placeholder="请选择"
                showSearch
                getPopupContainer={() => document.body}
                filterOption={(input, option) =>
                  String(option?.children ?? '')
                    .toLowerCase()
                    .includes(input.toLowerCase())
                }
              >
                {optionTypeModalOptions.map((o) => (
                  <Select.Option key={o.value === '' ? '_option_type_empty' : o.value} value={o.value}>
                    {o.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="appCode" label="所属应用">
              <Select
                allowClear
                placeholder="请选择所属应用（可留空）"
                showSearch
                getPopupContainer={() => document.body}
                filterOption={(input, option) =>
                  String(option?.children ?? '')
                    .toLowerCase()
                    .includes(input.toLowerCase())
                }
              >
                {appCodeModalOptions.map((o) => (
                  <Select.Option key={o.value === '' ? '_app_code_empty' : o.value} value={o.value}>
                    {o.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Card>
        </Form>
      </FullscreenModal>

    </PageContainer>

  );

};



export default WoOptionsAdmin;

