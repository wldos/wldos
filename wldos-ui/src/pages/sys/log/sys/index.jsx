/**
 * 系统日志列表页（系统设置 → 安全审计 → 系统日志）。
 *
 * 数据维度：
 *   - 由业务侧通过 ISystemLogger / ApplicationEventPublisher 触发；
 *   - 涵盖配置变更（CONFIG_CHANGE）、调度任务（HOLIDAY_SYNC_*、TASK_*）、
 *     关键异常（EXCEPTION）等"运维事件"；
 *   - 一次写入不可改、不可删；详情含 metadata（自由 JSON）。
 *
 * 操作能力：
 *   - 按 来源模块 / 事件类型 / 严重级别 / 触发者 过滤；
 *   - 默认按 occur_at 倒序，行点击展开详情抽屉。
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

const SEVERITY_TAG = {
  INFO: { color: 'blue', text: '信息' },
  WARN: { color: 'orange', text: '警告' },
  ERROR: { color: 'red', text: '错误' },
  CRITICAL: { color: 'magenta', text: '严重' },
};

const SysLogList = () => {
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
      title: '事件类型',
      dataIndex: 'eventType',
      width: 200,
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
      title: '来源模块',
      dataIndex: 'sourceModule',
      width: 120,
      ellipsis: true,
    },
    {
      title: '级别',
      dataIndex: 'severity',
      width: 90,
      valueEnum: {
        INFO: { text: '信息', status: 'Default' },
        WARN: { text: '警告', status: 'Warning' },
        ERROR: { text: '错误', status: 'Error' },
        CRITICAL: { text: '严重', status: 'Error' },
      },
      render: (_, r) => {
        const v = r.severity;
        const cfg = SEVERITY_TAG[v];
        return cfg ? <Tag color={cfg.color}>{cfg.text}</Tag> : (v || '-');
      },
    },
    {
      title: '描述',
      dataIndex: 'message',
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: '触发者ID',
      dataIndex: 'operatorUserId',
      width: 180,
      ellipsis: true,
    },
    {
      title: '域ID',
      dataIndex: 'domainId',
      width: 120,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '扩展信息',
      dataIndex: 'metadata',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '入库时间',
      dataIndex: 'createdAt',
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
          headerTitle="系统日志"
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
        title={row ? `系统日志详情 - ${row.sourceModule || ''} / ${row.eventType || ''}` : ''}
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
                { title: '来源模块', dataIndex: 'sourceModule' },
                { title: '事件类型', dataIndex: 'eventType' },
                {
                  title: '级别',
                  dataIndex: 'severity',
                  render: (v) => {
                    const cfg = SEVERITY_TAG[v];
                    return cfg ? <Tag color={cfg.color}>{cfg.text}</Tag> : (v || '-');
                  },
                },
                { title: '描述', dataIndex: 'message' },
                { title: '触发者ID', dataIndex: 'operatorUserId' },
                { title: '域ID', dataIndex: 'domainId' },
                { title: '公司ID', dataIndex: 'comId' },
              ]}
            />
            <div style={{ marginTop: 16 }}>
              <Text strong>扩展信息（metadata，原始 JSON）：</Text>
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
                {row.metadata || '(空)'}
              </Paragraph>
            </div>
          </>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default SysLogList;
