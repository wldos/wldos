/**
 * 登录日志列表页（系统设置 → 安全审计 → 登录日志）。
 *
 * 数据维度：
 *   - 表中记录所有"登录类事件"（账号密码登录、手机登录、注册、密码重置等），
 *     每条事件不可改、不可删，仅作展示与分页查询；
 *   - 失败事件的 userId 通常为 null（账号尚未通过验证），通过 loginAccount 关联。
 *
 * 操作能力：
 *   - 按 登录账号 / 用户ID / IP / 事件类型 / 结果 过滤；
 *   - 默认按 occur_at 倒序，支持点击列头排序；
 *   - 行点击展开详情（UA 等长字段），不暴露写操作。
 */
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Drawer, Tag, Typography } from 'antd';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import { queryPage } from './service';

const { Text } = Typography;

const EVENT_TYPE_ENUM = {
  ACCOUNT: { text: '账号密码登录' },
  MOBILE: { text: '手机验证码登录' },
  OAUTH: { text: '第三方登录' },
  MFA: { text: 'MFA二次校验' },
  LOGOUT: { text: '退出登录' },
  REGISTER: { text: '注册' },
  ACTIVE: { text: '账号激活' },
  RESET: { text: '密码重置' },
  PASSWD_CHANGE: { text: '修改密码' },
  MOBILE_CHANGE: { text: '修改密保手机' },
  SEC_QUEST_CHANGE: { text: '修改密保问题' },
  BAK_EMAIL_CHANGE: { text: '修改备用邮箱' },
  MFA_CHANGE: { text: '修改MFA设备' },
};

const RESULT_ENUM = {
  SUCCESS: { text: '成功', status: 'Success' },
  FAIL: { text: '失败', status: 'Error' },
  LOCKED: { text: '已锁定', status: 'Warning' },
  EXPIRED: { text: '已过期', status: 'Default' },
  MFA_REQUIRED: { text: '需要MFA', status: 'Processing' },
};

const RESULT_TAG_COLOR = {
  SUCCESS: 'green',
  FAIL: 'red',
  LOCKED: 'orange',
  EXPIRED: 'default',
  MFA_REQUIRED: 'blue',
};

const LoginLogList = () => {
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
      title: '登录账号',
      dataIndex: 'loginAccount',
      width: 200,
      ellipsis: true,
      fixed: mobile ? undefined : 'left',
      render: (dom, record) => <a onClick={() => setRow(record)}>{dom}</a>,
    },
    {
      title: '事件时间',
      dataIndex: 'occurAt',
      width: 180,
      valueType: 'dateTime',
      sorter: true,
      defaultSortOrder: 'descend',
      hideInSearch: true,
    },
    {
      title: '事件类型',
      dataIndex: 'eventType',
      width: 140,
      valueEnum: EVENT_TYPE_ENUM,
    },
    {
      title: '结果',
      dataIndex: 'result',
      width: 100,
      valueEnum: RESULT_ENUM,
      render: (_, record) => {
        const v = record.result;
        if (!v) return '-';
        const color = RESULT_TAG_COLOR[v] || 'default';
        const text = RESULT_ENUM[v]?.text || v;
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      width: 180,
      ellipsis: true,
      render: (v) => v || '-',
    },
    {
      title: 'IP',
      dataIndex: 'ip',
      width: 140,
      ellipsis: true,
    },
    {
      title: '失败原因',
      dataIndex: 'failReason',
      ellipsis: true,
      hideInSearch: true,
      render: (v) => (v ? <Text type="danger">{v}</Text> : '-'),
    },
    {
      title: '浏览器UA',
      dataIndex: 'userAgent',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: 'OAuth Provider',
      dataIndex: 'oauthProvider',
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
          headerTitle="登录日志"
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
        width={600}
        visible={!!row}
        onClose={() => setRow(null)}
        closable={false}
        title={row ? `登录日志详情 - ${row.loginAccount || ''}` : ''}
      >
        {row && (
          <ProDescriptions
            column={1}
            dataSource={row}
            columns={[
              { title: 'ID', dataIndex: 'id' },
              { title: '事件时间', dataIndex: 'occurAt', valueType: 'dateTime' },
              { title: '入库时间', dataIndex: 'createdAt', valueType: 'dateTime' },
              {
                title: '事件类型',
                dataIndex: 'eventType',
                valueEnum: EVENT_TYPE_ENUM,
              },
              {
                title: '结果',
                dataIndex: 'result',
                valueEnum: RESULT_ENUM,
              },
              { title: '登录账号', dataIndex: 'loginAccount' },
              { title: '用户ID', dataIndex: 'userId' },
              { title: 'OAuth Provider', dataIndex: 'oauthProvider' },
              { title: '失败原因', dataIndex: 'failReason' },
              { title: 'IP', dataIndex: 'ip' },
              { title: '浏览器UA', dataIndex: 'userAgent' },
              { title: '域ID', dataIndex: 'domainId' },
              { title: '公司ID', dataIndex: 'comId' },
            ]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default LoginLogList;
