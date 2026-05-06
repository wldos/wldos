import React, { useMemo, useRef, useState } from 'react';
import { Modal, Space, Button, Select, message, Typography, Input } from 'antd';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import {
  patchMyPublishAccount,
  removeMyPublishAccount,
} from '@/pages/commercial/social-publish/service';
import { isDesktopEmbedded, purgeDesktopExternalAccount } from '@/utils/desktopEmbeddedBridge';

function parseExtra(raw) {
  if (!raw) return {};
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : raw;
  } catch (e) {
    return {};
  }
}

/** 从账号列表归纳可选分组名 */
function collectGroupNames(accounts) {
  const set = new Set(['默认分组']);
  (accounts || []).forEach((a) => {
    const g = parseExtra(a?.extraJson)?.groupName;
    if (g) set.add(String(g));
  });
  return Array.from(set);
}

/**
 * 齿轮入口：表格维护备注、删除、迁移分组（PATCH extraJson）。
 */
const AccountGroupManageModal = ({ open, onClose, accounts = [], onRefresh }) => {
  const actionRef = useRef();
  useDesktopSticky(actionRef);
  const [migrateRow, setMigrateRow] = useState(null);
  const [targetGroup, setTargetGroup] = useState('');
  const [remarkDraft, setRemarkDraft] = useState({});
  const groupOptions = useMemo(() => collectGroupNames(accounts).map((g) => ({ label: g, value: g })), [accounts]);

  const handleDelete = (row) => {
    if (!row?.id) return;
    Modal.confirm({
      title: '删除账号',
      content: `确认删除「${row.accountName || row.platform}」吗？`,
      okText: '删除',
      okButtonProps: { danger: true },
      onOk: async () => {
        await removeMyPublishAccount(row.id);
        if (isDesktopEmbedded()) {
          purgeDesktopExternalAccount(row.id).catch(() => {});
        }
        message.success('已删除');
        if (onRefresh) await onRefresh();
      },
    });
  };

  const handleSaveRemark = async (row) => {
    const id = row?.id;
    if (!id) return;
    const remark = remarkDraft[String(id)];
    if (remark === undefined) {
      message.info('未修改备注');
      return;
    }
    await patchMyPublishAccount(id, { remark });
    message.success('备注已保存');
    setRemarkDraft((prev) => {
      const next = { ...prev };
      delete next[String(id)];
      return next;
    });
    if (onRefresh) await onRefresh();
  };

  const submitMigrate = async () => {
    const row = migrateRow;
    if (!row?.id || !targetGroup) {
      message.warning('请选择目标分组');
      return;
    }
    await patchMyPublishAccount(row.id, { groupName: targetGroup });
    message.success('已迁移分组');
    setMigrateRow(null);
    setTargetGroup('');
    if (onRefresh) await onRefresh();
  };

  const columns = [
    {
      title: '账号名',
      dataIndex: 'accountName',
      ellipsis: true,
      width: 160,
    },
    {
      title: '平台',
      dataIndex: 'platform',
      width: 110,
    },
    {
      title: '分组',
      width: 120,
      render: (_, row) => parseExtra(row.extraJson).groupName || '默认分组',
    },
    {
      title: '备注',
      width: 220,
      render: (_, row) => {
        const id = String(row.id);
        const extra = parseExtra(row.extraJson);
        const initial = extra.remark != null ? String(extra.remark) : '';
        const val = remarkDraft[id] !== undefined ? remarkDraft[id] : initial;
        return (
          <Space size={4} wrap>
            <Input
              size="small"
              style={{ width: 140 }}
              placeholder="备注"
              value={val}
              onChange={(e) => setRemarkDraft((prev) => ({ ...prev, [id]: e.target.value }))}
            />
            <Button size="small" type="link" onClick={() => handleSaveRemark(row)}>
              保存
            </Button>
          </Space>
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 88,
      render: (v) => v || '-',
    },
    {
      title: '操作',
      valueType: 'option',
      width: 200,
      fixed: 'right',
      render: (_, row) => (
        <Space size={4}>
          <Button type="link" size="small" onClick={() => {
            setMigrateRow(row);
            setTargetGroup(parseExtra(row.extraJson).groupName || '默认分组');
          }}
          >
            迁移分组
          </Button>
          <Button type="link" size="small" danger onClick={() => handleDelete(row)}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <>
      <Modal
        title="分组与账号管理"
        open={open}
        onCancel={onClose}
        footer={null}
        width={1000}
        destroyOnClose
        bodyStyle={{ paddingTop: 12 }}
      >
        <Typography.Paragraph type="secondary" style={{ marginBottom: 12 }}>
          在此批量维护备注、迁移分组或删除账号；左侧折叠分组与下列表数据来源一致。
        </Typography.Paragraph>
        <ProTableX
          actionRef={actionRef}
          rowKey={(r) => String(r.id)}
          columns={columns}
          dataSource={accounts || []}
          search={false}
          pagination={{ pageSize: 10 }}
          scroll={{ x: 900 }}
          toolBarRender={false}
          options={false}
        />
      </Modal>

      <Modal
        title="迁移到其他分组"
        open={!!migrateRow}
        onCancel={() => {
          setMigrateRow(null);
          setTargetGroup('');
        }}
        onOk={submitMigrate}
        okText="确定"
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <Typography.Text type="secondary">
            账号：{migrateRow?.accountName || migrateRow?.platform || '-'}
          </Typography.Text>
          <Select
            style={{ width: '100%' }}
            showSearch
            placeholder="目标分组"
            options={groupOptions}
            value={targetGroup || undefined}
            onChange={setTargetGroup}
            dropdownRender={(menu) => (
              <>
                {menu}
                <div style={{ padding: '8px 12px', borderTop: '1px solid #f0f0f0' }}>
                  <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                    新分组请先在左侧分组列表中「添加新分组」，再刷新本表。
                  </Typography.Text>
                </div>
              </>
            )}
          />
        </Space>
      </Modal>
    </>
  );
};

export default AccountGroupManageModal;
