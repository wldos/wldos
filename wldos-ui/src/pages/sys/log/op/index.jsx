/**
 * 操作日志列表页（系统设置 → 安全审计 → 操作日志）。
 *
 * 数据维度：
 *   - 由 @OpLog 注解 + AOP 切面自动采集，覆盖所有标注的 controller 方法；
 *   - 一次写入不可改、不可删；详情含入参摘要（脱敏 + 截断）和耗时；
 *   - 资源名称 / 操作者名称由后端 IOpLogContextResolver SPI 在查询时回填，
 *     展示侧字段：resourceName / userName。
 *
 * 操作能力：
 *   - 按 模块 / 动作 / 资源类型 / 操作者 / IP 过滤；
 *   - 默认按 occur_at 倒序，支持点击列头排序；
 *   - 行点击展开详情抽屉（入参 / 错误 / UA 等长字段）。
 */
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Drawer, Tag, Typography } from 'antd';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import { queryPage } from './service';

const { Text, Paragraph } = Typography;

const RESULT_TAG_COLOR = (code) => {
  if (!code) return 'default';
  const c = String(code);
  if (c.startsWith('2')) return 'green';
  if (c.startsWith('4')) return 'orange';
  if (c.startsWith('5')) return 'red';
  return 'default';
};

const OpLogList = () => {
  const actionRef = useRef();
  const [row, setRow] = useState(null);

  const mobile = isMobile();
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(() => {
    if (!containerRef.current) return undefined;
    const updateWidth = () => {
      if (containerRef.current) {
        setContainerWidth(containerRef.current.offsetWidth);
      }
    };
    updateWidth();
    const resizeObserver = new ResizeObserver(updateWidth);
    resizeObserver.observe(containerRef.current);
    return () => resizeObserver.disconnect();
  }, []);

  const columns = useMemo(() => [
    {
      title: '动作',
      dataIndex: 'action',
      width: 180,
      ellipsis: true,
      fixed: mobile ? undefined : 'left',
      render: (dom, record) => <a onClick={() => setRow(record)}>{dom}</a>,
    },
    {
      title: '事件时间',
      dataIndex: 'occurAt',
      width: 170,
      valueType: 'dateTime',
      sorter: true,
      defaultSortOrder: 'descend',
      hideInSearch: true,
    },
    {
      title: '模块',
      dataIndex: 'module',
      width: 120,
      ellipsis: true,
    },
    {
      title: '资源类型',
      dataIndex: 'resourceTypeLabel',
      width: 110,
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        const label = record.resourceTypeLabel;
        const type = record.resourceType;
        if (label) return <span title={type ? `${label}（${type}）` : label}>{label}</span>;
        if (type) return <span style={{ color: '#999' }} title={type}>{type}</span>;
        return '-';
      },
    },
    {
      title: '资源类型',
      dataIndex: 'resourceType',
      hideInTable: true,
    },
    {
      title: '资源',
      dataIndex: 'resourceName',
      width: 180,
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => {
        const name = record.resourceName;
        const id = record.resourceId;
        if (name) return <span title={id ? `${name} (${id})` : name}>{name}</span>;
        if (id) return <span style={{ color: '#999' }}>{id}</span>;
        return '-';
      },
    },
    {
      title: '资源ID',
      dataIndex: 'resourceId',
      width: 140,
      ellipsis: true,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '操作者',
      dataIndex: 'userName',
      width: 160,
      ellipsis: true,
      render: (_, record) => {
        const name = record.userName;
        const uid = record.userId;
        if (name) return name;
        if (uid != null) return <span style={{ color: '#999' }}>{uid}</span>;
        return '-';
      },
    },
    {
      title: '操作者ID',
      dataIndex: 'userId',
      width: 180,
      ellipsis: true,
      hideInTable: true,
    },
    {
      title: 'HTTP',
      dataIndex: 'httpMethod',
      width: 80,
      hideInSearch: true,
    },
    {
      title: '路径',
      dataIndex: 'requestPath',
      width: 220,
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: '结果',
      dataIndex: 'resultCode',
      width: 90,
      hideInSearch: true,
      render: (v) => (v ? <Tag color={RESULT_TAG_COLOR(v)}>{v}</Tag> : '-'),
    },
    {
      title: '耗时(ms)',
      dataIndex: 'durationMs',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: 'IP',
      dataIndex: 'ip',
      width: 130,
      ellipsis: true,
    },
    {
      title: '错误',
      dataIndex: 'errorMessage',
      ellipsis: true,
      hideInSearch: true,
      render: (v) => (v ? <Text type="danger">{v}</Text> : '-'),
    },
    {
      title: '入参摘要',
      dataIndex: 'paramsSummary',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '浏览器UA',
      dataIndex: 'userAgent',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '入库时间',
      dataIndex: 'createdAt',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '域ID',
      dataIndex: 'domainId',
      hideInTable: true,
      hideInSearch: true,
    },
  ], [mobile]);

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer
      style={{ padding: '0', margin: '0' }}
      bodyStyle={{ padding: '24px', margin: '0' }}
    >
      <div ref={containerRef}>
        <ProTableX
          headerTitle="操作日志"
          actionRef={actionRef}
          rowKey="id"
          search={{ labelWidth: 120 }}
          request={async (params, sorter, filter) => {
            const res = await queryPage({
              ...params,
              sorter,
              filter,
            });
            return {
              data: res?.data?.rows || [],
              total: res?.data?.total || 0,
              success: res?.code === 200,
            };
          }}
          columns={columns}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
          }}
        />
      </div>
      <Drawer
        width={680}
        visible={!!row}
        onClose={() => setRow(null)}
        closable={false}
        title={row ? `操作日志详情 - ${row.module || ''}/${row.action || ''}` : ''}
      >
        {row && (
          <>
            <ProDescriptions
              column={1}
              dataSource={row}
              columns={[
                { title: 'ID', dataIndex: 'id' },
                { title: '事件时间', dataIndex: 'occurAt', valueType: 'dateTime' },
                { title: '入库时间', dataIndex: 'createdAt', valueType: 'dateTime' },
                { title: '模块', dataIndex: 'module' },
                { title: '动作', dataIndex: 'action' },
                {
                  title: '资源类型',
                  dataIndex: 'resourceTypeLabel',
                  render: (_, record) => {
                    const label = record.resourceTypeLabel;
                    const type = record.resourceType;
                    if (label && type) return `${label}（${type}）`;
                    if (label) return label;
                    if (type) return type;
                    return '-';
                  },
                },
                {
                  title: '资源',
                  dataIndex: 'resourceName',
                  render: (_, record) => {
                    const name = record.resourceName;
                    const id = record.resourceId;
                    if (name && id) return `${name} (${id})`;
                    if (name) return name;
                    if (id) return id;
                    return '-';
                  },
                },
                { title: '资源ID', dataIndex: 'resourceId' },
                {
                  title: '操作者',
                  dataIndex: 'userName',
                  render: (_, record) => {
                    const name = record.userName;
                    const uid = record.userId;
                    if (name && uid != null) return `${name} (${uid})`;
                    if (name) return name;
                    if (uid != null) return uid;
                    return '-';
                  },
                },
                { title: '操作者ID', dataIndex: 'userId' },
                { title: '虚拟身份', dataIndex: 'virtualUserId' },
                { title: '域ID', dataIndex: 'domainId' },
                { title: '公司ID', dataIndex: 'comId' },
                { title: 'HTTP', dataIndex: 'httpMethod' },
                { title: '路径', dataIndex: 'requestPath' },
                { title: '结果码', dataIndex: 'resultCode' },
                { title: '耗时(ms)', dataIndex: 'durationMs' },
                { title: 'IP', dataIndex: 'ip' },
                { title: '浏览器UA', dataIndex: 'userAgent' },
                { title: '错误信息', dataIndex: 'errorMessage' },
              ]}
            />
            <div style={{ marginTop: 16 }}>
              <Text strong>入参摘要（脱敏 + 截断 4096B）：</Text>
              <Paragraph
                copyable
                style={{
                  background: '#f5f5f5',
                  padding: 12,
                  marginTop: 8,
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-all',
                  fontFamily: 'monospace',
                  fontSize: 12,
                  maxHeight: 320,
                  overflow: 'auto',
                }}
              >
                {row.paramsSummary || '(空)'}
              </Paragraph>
            </div>
          </>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default OpLogList;
