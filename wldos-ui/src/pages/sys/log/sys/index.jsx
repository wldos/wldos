import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect, useLocation } from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { PageContainer } from '@ant-design/pro-layout';
import { auditComDomainFilterColumns } from '@/pages/sys/log/_shared/auditLogFilterColumns';
import { useAuditLogListMeta } from '@/pages/sys/log/_shared/auditLogListMeta';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import { Drawer, Tag, Typography } from 'antd';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import { fetchEnumSysLogActorKind } from '@/services/enum';
import { selectToEnum } from '@/utils/utils';
import { queryPage } from './service';

const { Text, Paragraph } = Typography;

const SEVERITY_TAG = {
  INFO: { color: 'blue', text: '信息' },
  WARN: { color: 'orange', text: '警告' },
  ERROR: { color: 'red', text: '错误' },
  CRITICAL: { color: 'magenta', text: '严重' },
};

const emptyDash = (v) => (v === 0 || v === '0' ? v : v || '-');

const URL_SEARCH_KEYS = [
  'sourceModule', 'eventType', 'severity', 'ip', 'requestUri', 'requestMethod', 'actorKind', 'operatorUserId',
];

function parseSearchFromLocation(search) {
  const raw = search && search.startsWith('?') ? search.slice(1) : (search || '');
  if (!raw) return {};
  const params = new URLSearchParams(raw);
  const initial = {};
  URL_SEARCH_KEYS.forEach((key) => {
    const v = params.get(key);
    if (v != null && v !== '') {
      initial[key] = v;
    }
  });
  return initial;
}

const SysLogList = ({ isAdmin, isTenantAdmin, currentUser }) => {
  const location = useLocation();
  const actionRef = useRef();
  const listMeta = useAuditLogListMeta({ isAdmin, isTenantAdmin, currentUser });
  const { domainLabelMap, showDomainColumn } = listMeta;
  const formRef = useRef();
  const [row, setRow] = useState(null);
  const [actorKindEnum, setActorKindEnum] = useState({});
  const initialSearch = useMemo(
    () => parseSearchFromLocation(location.search),
    [location.search],
  );

  const mobile = isMobile();
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  useEffect(() => {
    fetchEnumSysLogActorKind().then((res) => {
      if (res?.data) {
        setActorKindEnum(selectToEnum(res.data));
      }
    });
  }, []);

  useEffect(() => {
    if (!formRef.current || !Object.keys(initialSearch).length) {
      return;
    }
    formRef.current.setFieldsValue(initialSearch);
    actionRef.current?.reload();
  }, [initialSearch]);

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
    ...auditComDomainFilterColumns(listMeta),
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
      title: 'IP',
      dataIndex: 'ip',
      width: 130,
      ellipsis: true,
      hideInSearch: true,
      render: (_, r) => emptyDash(r.ip),
    },
    {
      title: 'IP',
      dataIndex: 'ip',
      hideInTable: true,
    },
    {
      title: '请求路径',
      dataIndex: 'requestUri',
      width: 280,
      ellipsis: true,
      hideInSearch: true,
      render: (_, r) => emptyDash(r.requestUri),
    },
    {
      title: 'Method',
      dataIndex: 'requestMethod',
      width: 80,
      hideInSearch: true,
      render: (_, r) => emptyDash(r.requestMethod),
    },
    {
      title: '来源分类',
      dataIndex: 'actorKind',
      width: 120,
      ellipsis: true,
      valueType: 'select',
      valueEnum: actorKindEnum,
      render: (_, r) => {
        const v = r.actorKind;
        if (!v) return '-';
        return actorKindEnum[v]?.text || v;
      },
    },
    {
      title: '触发者ID',
      dataIndex: 'operatorUserId',
      width: 180,
      ellipsis: true,
      render: (_, r) => emptyDash(r.operatorUserId),
    },
    {
      title: '站点',
      dataIndex: 'domainId',
      width: 140,
      ellipsis: true,
      hideInSearch: true,
      hideInTable: !showDomainColumn,
      render: (_, record) => domainLabelMap[String(record.domainId)] || record.domainId || '-',
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
  ], [mobile, actorKindEnum, domainLabelMap, showDomainColumn, listMeta]);

  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle="系统日志"
          actionRef={actionRef}
          formRef={formRef}
          form={{ initialValues: initialSearch }}
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
                { title: 'IP', dataIndex: 'ip', render: (v) => emptyDash(v) },
                { title: '请求路径', dataIndex: 'requestUri', render: (v) => emptyDash(v) },
                { title: 'Method', dataIndex: 'requestMethod', render: (v) => emptyDash(v) },
                { title: 'Host', dataIndex: 'requestHost', render: (v) => emptyDash(v) },
                {
                  title: '来源分类',
                  dataIndex: 'actorKind',
                  render: (v) => (v ? (actorKindEnum[v]?.text || v) : '-'),
                },
                { title: '聚合次数', dataIndex: 'aggregateCount', render: (v) => emptyDash(v) },
                { title: '触发者ID', dataIndex: 'operatorUserId', render: (v) => emptyDash(v) },
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

export default connect(({ user }) => ({
  currentUser: user.currentUser || {},
  ...buildAuthorityHelper(user, '/admin'),
}))(SysLogList);
