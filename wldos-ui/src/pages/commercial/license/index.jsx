import React, { useRef } from 'react';
import { Tag, Button, message } from 'antd';
import { Link } from 'umi';
import { CopyOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryLicenseList } from './service';

const statusMap = {
  ACTIVE: { color: 'green', text: '有效' },
  EXPIRED: { color: 'orange', text: '已过期' },
  REVOKED: { color: 'red', text: '已失效' },
};

const handleCopyKey = (record) => {
  const key = record.licenseKey;
  if (!key) return;
  if (navigator.clipboard?.writeText) {
    navigator.clipboard.writeText(key).then(() => {
      message.success('已复制到剪贴板');
    }).catch(() => fallbackCopy(key));
  } else {
    fallbackCopy(key);
  }
};

const fallbackCopy = (text) => {
  const ta = document.createElement('textarea');
  ta.value = text;
  document.body.appendChild(ta);
  ta.select();
  try {
    document.execCommand('copy');
    message.success('已复制到剪贴板');
  } catch (e) {
    message.error('复制失败');
  }
  document.body.removeChild(ta);
};

export default function LicenseList() {
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const columns = [
    { title: '插件', dataIndex: 'pluginCode', width: 120, hideInSearch: true },
    { title: '订单号', dataIndex: 'orderNo', width: 180, hideInSearch: true },
    {
      title: 'License Key',
      dataIndex: 'licenseKey',
      ellipsis: true,
      hideInSearch: true,
      render: (v, record) => (
        <span>
          {v ? `${v.substring(0, 20)}...` : '-'}
          <Button
            type="link"
            size="small"
            icon={<CopyOutlined />}
            onClick={() => handleCopyKey(record)}
          >
            复制
          </Button>
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      hideInSearch: true,
      render: (s) => {
        const m = statusMap[s] || { color: 'default', text: s };
        return <Tag color={m.color}>{m.text}</Tag>;
      },
    },
    { title: '过期时间', dataIndex: 'expireTime', width: 180, hideInSearch: true, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createTime', width: 180, hideInSearch: true },
    {
      title: '操作',
      key: 'action',
      width: 100,
      hideInSearch: true,
      render: (_, record) => <Link to={`/license/${record.id}`}>详情</Link>,
    },
  ];

  return (
    <PageContainer title="我的 License">
      <ProTableX
        headerTitle="License 列表"
        actionRef={actionRef}
        rowKey="id"
        search={false}
        request={async (params) => {
          const res = await queryLicenseList({
            current: params.current,
            pageSize: params.pageSize,
          });
          const data = res?.data?.data || res?.data || {};
          return {
            data: data.rows || data.list || [],
            total: data.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={columns}
      />
    </PageContainer>
  );
}
