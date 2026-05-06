/**
 * 节假日日历管理页（系统配置 → 节假日日历）。
 *
 * 数据维度：
 *   - 表中**仅记录**与默认周末日历不同的日子（OFF=放假 / WORK=调休补班）；
 *   - 平日工作 / 周末休息属于默认行为，不入表，由调用方按 weekday 兜底。
 *
 * 操作能力：
 *   - 切换年份查看；
 *   - 单条新增 / 编辑 / 删除（手工维护，优先级最高，外部同步不会覆盖 MANUAL 条目）；
 *   - 一键同步外部数据源（HolidayExternalSource 默认实现为 noop，站点可覆盖）；
 *   - 一键导入内置兜底（首次开荒或站点离线场景）。
 */
import React, { useEffect, useMemo, useState } from 'react';
import {
  PageContainer,
} from '@ant-design/pro-layout';
import {
  Card,
  Button,
  Space,
  Table,
  Select,
  Modal,
  Form,
  Input,
  DatePicker,
  Tag,
  Popconfirm,
  message,
  Tooltip,
  Alert,
} from 'antd';
import {
  PlusOutlined,
  ReloadOutlined,
  CloudSyncOutlined,
  DownloadOutlined,
  EditOutlined,
  DeleteOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
import moment from 'moment';
import * as service from './service';

const SOURCE_COLOR = {
  MANUAL: 'green',
  SYNC: 'blue',
  IMPORT: 'gold',
};

const Calendar = () => {
  const thisYear = new Date().getFullYear();
  const [year, setYear] = useState(thisYear);
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState([]);
  const [enums, setEnums] = useState({ dayKind: [], source: [] });
  const [builtinYearList, setBuiltinYearList] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const yearOptions = useMemo(() => {
    const arr = [];
    for (let y = thisYear - 2; y <= thisYear + 3; y += 1) arr.push(y);
    return arr;
  }, [thisYear]);

  const reload = async (y = year) => {
    setLoading(true);
    try {
      const res = await service.queryByYear(y);
      setList(res?.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    (async () => {
      const [er, by] = await Promise.all([
        service.fetchEnums(),
        service.builtinYears(),
      ]);
      if (er?.data) setEnums(er.data);
      if (by?.data) setBuiltinYearList(by.data);
    })();
  }, []);

  useEffect(() => {
    reload(year);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [year]);

  const openCreate = () => {
    setEditing({});
    form.resetFields();
    form.setFieldsValue({
      date: moment(`${year}-01-01`),
      dayKind: 'OFF',
    });
  };

  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({
      ...record,
      date: record.date ? moment(record.date) : null,
    });
  };

  const closeForm = () => {
    setEditing(null);
    form.resetFields();
  };

  const submitForm = async () => {
    const values = await form.validateFields();
    const data = {
      ...values,
      date: values.date ? values.date.format('YYYY-MM-DD') : null,
      id: editing?.id,
    };
    const res = await service.saveItem(data);
    if (res?.code === 200) {
      message.success('保存成功');
      closeForm();
      reload();
    }
  };

  const onDelete = async (record) => {
    const res = await service.removeItem(record.id);
    if (res?.code === 200) {
      message.success('删除成功');
      reload();
    }
  };

  const onSync = async () => {
    const res = await service.syncYear(year, false);
    if (res?.code === 200) {
      message.success(`同步完成，写入 ${res?.data || 0} 条（MANUAL 条目受保护未覆盖）`);
      reload();
    }
  };

  const onImportBuiltin = async () => {
    const res = await service.importBuiltin(year);
    if (res?.code === 200) {
      message.success(`导入兜底完成，写入 ${res?.data || 0} 条`);
      reload();
    }
  };

  const columns = [
    {
      title: '日期',
      dataIndex: 'date',
      width: 130,
      sorter: (a, b) => (a.date || '').localeCompare(b.date || ''),
      defaultSortOrder: 'ascend',
    },
    {
      title: '类型',
      dataIndex: 'dayKind',
      width: 110,
      render: (v) => {
        const opt = enums.dayKind.find((it) => it.value === v);
        return v === 'OFF' ? (
          <Tag color="red">{opt ? opt.label : '放假'}</Tag>
        ) : (
          <Tag color="orange">{opt ? opt.label : '调休补班'}</Tag>
        );
      },
    },
    { title: '节日名', dataIndex: 'name', width: 160, ellipsis: true },
    {
      title: '来源',
      dataIndex: 'source',
      width: 100,
      render: (v) => {
        const opt = enums.source.find((it) => it.value === v);
        return <Tag color={SOURCE_COLOR[v] || 'default'}>{opt ? opt.label : v}</Tag>;
      },
    },
    { title: '备注', dataIndex: 'remark', ellipsis: true },
    {
      title: '操作',
      width: 160,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该条目？"
            okText="确认"
            cancelText="取消"
            onConfirm={() => onDelete(record)}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer
      title="节假日日历"
      extra={
        <Space>
          <span>年份：</span>
          <Select
            value={year}
            onChange={setYear}
            style={{ width: 110 }}
            options={yearOptions.map((y) => ({ value: y, label: `${y} 年` }))}
          />
          <Tooltip title="重新读取该年节假日条目">
            <Button icon={<ReloadOutlined />} onClick={() => reload()} />
          </Tooltip>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
            新增
          </Button>
          <Tooltip title="调用 HolidayExternalSource 同步外部源；MANUAL 条目受保护不被覆盖。默认实现为 noop（不发起网络），需要时由站点提供实现 Bean 覆盖。">
            <Button icon={<CloudSyncOutlined />} onClick={onSync}>
              同步外部
            </Button>
          </Tooltip>
          <Tooltip
            title={
              builtinYearList.includes(year)
                ? '把内置兜底数据写入当前年份；MANUAL 条目受保护不被覆盖。'
                : '当前年份无内置兜底，可先切到支持的年份，或用同步/手工维护。'
            }
          >
            <Button
              icon={<DownloadOutlined />}
              onClick={onImportBuiltin}
              disabled={!builtinYearList.includes(year)}
            >
              导入兜底
            </Button>
          </Tooltip>
        </Space>
      }
      style={{
        padding: '0',
        margin: '0'
      }}
      bodyStyle={{
        padding: '24px',
        margin: '0'
      }}
    >
      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 12 }}
        message={'日历仅记录“与默认周末日历不同的日子”。普通工作日/周末由调用方按 weekday 兜底，无需入表。'}
        description={
          <span>
            手工维护（MANUAL）优先级最高，外部同步不会覆盖。客户端通过独立接口
            <code> GET /api/svc/sync/calendar?year=YYYY </code>
            按年拉取本表数据，与守护策略解耦。
          </span>
        }
      />

      <Card bordered={false} bodyStyle={{ padding: 16 }}>
        <Table
          rowKey="id"
          loading={loading}
          dataSource={list}
          columns={columns}
          pagination={false}
          size="middle"
          scroll={{ x: 800 }}
        />
      </Card>

      <Modal
        visible={!!editing}
        title={editing?.id ? '编辑节假日' : '新增节假日'}
        onOk={submitForm}
        onCancel={closeForm}
        okText="保存"
        cancelText="取消"
        destroyOnClose
        width={520}
      >
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
          <Form.Item
            label={
              <Space>
                日期
                <Tooltip title="该日的具体日期，业务唯一键。同一日期重复保存会覆盖现有条目。">
                  <InfoCircleOutlined />
                </Tooltip>
              </Space>
            }
            name="date"
            rules={[{ required: true, message: '请选择日期' }]}
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label={
              <Space>
                类型
                <Tooltip title="OFF=法定放假；WORK=调休补班。仅这两种值有意义。">
                  <InfoCircleOutlined />
                </Tooltip>
              </Space>
            }
            name="dayKind"
            rules={[{ required: true, message: '请选择类型' }]}
          >
            <Select
              options={enums.dayKind.map((it) => ({ value: it.value, label: it.label }))}
            />
          </Form.Item>
          <Form.Item label="节日名" name="name">
            <Input placeholder="如：春节 / 国庆节 / 调休补班" maxLength={64} allowClear />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={2} maxLength={200} showCount allowClear />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default Calendar;
