import React, { useRef } from 'react';
import { Tag, Button, message, Popconfirm } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import { queryLicenseAdminList, reissueLicense, revokeLicense } from './service';

const statusMap = {
  ACTIVE: { color: 'green', text: '有效' },
  EXPIRED: { color: 'orange', text: '已过期' },
  REVOKED: { color: 'red', text: '已失效' },
};

const LicenseAdmin = () => {
  const actionRef = useRef();
  useDesktopSticky(actionRef);

  const handleReissue = async (licenseId) => {
    try {
      await reissueLicense(licenseId);
      message.success('补发成功');
      actionRef.current?.reload();
    } catch (e) {
      message.error(e?.data?.message || '补发失败');
    }
  };

  const handleRevoke = async (licenseId) => {
    try {
      await revokeLicense(licenseId);
      message.success('已失效');
      actionRef.current?.reload();
    } catch (e) {
      message.error(e?.data?.message || '操作失败');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80, hideInSearch: true },
    { title: '订单号', dataIndex: 'orderNo', width: 180 },
    { title: '用户ID', dataIndex: 'userId', width: 100 },
    { title: '插件', dataIndex: 'pluginCode', width: 120 },
    {
      title: 'License Key',
      dataIndex: 'licenseKey',
      ellipsis: true,
      hideInSearch: true,
      render: (v) => (v ? `${v.substring(0, 24)}...` : '-'),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      render: (s) => {
        const m = statusMap[s] || { color: 'default', text: s };
        return <Tag color={m.color}>{m.text}</Tag>;
      },
    },
    { title: '过期时间', dataIndex: 'expireTime', width: 180, hideInSearch: true },
    { title: '创建时间', dataIndex: 'createTime', width: 180, hideInSearch: true },
    {
      title: '操作',
      key: 'action',
      width: 160,
      fixed: 'right',
      hideInSearch: true,
      render: (_, record) => (
        <>
          {record.status === 'ACTIVE' && (
            <>
              <Popconfirm
                title="确定补发 License？将生成新的 License Key，原 Key 仍有效。"
                onConfirm={() => handleReissue(record.id)}
              >
                <Button type="link" size="small">补发</Button>
              </Popconfirm>
              <Popconfirm
                title="确定失效此 License？失效后不可恢复。"
                onConfirm={() => handleRevoke(record.id)}
              >
                <Button type="link" size="small" danger>失效</Button>
              </Popconfirm>
            </>
          )}
        </>
      ),
    },
  ];

  return (
    <PageContainer
      title="License 管理"
      style={{ padding: '0', margin: '0' }}
      bodyStyle={{ padding: '24px', margin: '0' }}
    >
      <ProTableX
        headerTitle="License 列表"
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        request={async (params) => {
          const res = await queryLicenseAdminList({
            current: params.current,
            pageSize: params.pageSize,
            userId: params.userId,
            pluginCode: params.pluginCode,
            status: params.status,
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
};

export default LicenseAdmin;
