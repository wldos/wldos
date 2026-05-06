import React, { useMemo, useState } from 'react';
import { Drawer, Space, Button, Typography, Checkbox, Switch, DatePicker, message } from 'antd';

const PublishSettingsDrawer = ({
  open,
  onClose,
  accounts = [],
  defaultSelectedIds = [],
  onSubmit,
}) => {
  const [selectedIds, setSelectedIds] = useState(defaultSelectedIds || []);
  const [scheduled, setScheduled] = useState(false);
  const [scheduledAt, setScheduledAt] = useState(null);
  const [isOriginal, setIsOriginal] = useState(false);

  const options = useMemo(
    () =>
      (accounts || []).map((a) => ({
        label: `${a.accountName || a.platform} (${a.platform || '-'})`,
        value: a.id,
      })),
    [accounts],
  );

  const handleSubmit = async () => {
    if (!selectedIds.length) {
      message.warning('请先选择发布账号');
      return;
    }
    if (scheduled && !scheduledAt) {
      message.warning('请先选择定时发布时间');
      return;
    }
    if (onSubmit) {
      await onSubmit({
        accountIds: selectedIds,
        scheduled,
        scheduledAt,
        isOriginal,
      });
    }
  };

  return (
    <Drawer
      title="发布设置"
      width={520}
      open={open}
      onClose={onClose}
      destroyOnClose
      extra={
        <Space>
          <Button onClick={onClose}>取消</Button>
          <Button type="primary" onClick={handleSubmit}>
            确认发布
          </Button>
        </Space>
      }
    >
      <Space direction="vertical" style={{ width: '100%' }} size={16}>
        <div>
          <Typography.Text strong>选择账号</Typography.Text>
          <div style={{ marginTop: 8 }}>
            <Checkbox.Group
              style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
              options={options}
              value={selectedIds}
              onChange={setSelectedIds}
            />
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Typography.Text>原创声明</Typography.Text>
          <Switch checked={isOriginal} onChange={setIsOriginal} />
        </div>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Typography.Text>定时发布</Typography.Text>
            <Switch checked={scheduled} onChange={setScheduled} />
          </div>
          {scheduled ? (
            <DatePicker
              showTime={{ format: 'HH:mm' }}
              style={{ width: '100%', marginTop: 8 }}
              value={scheduledAt}
              onChange={setScheduledAt}
              format="YYYY年MM月DD日 HH:mm"
              placeholder="选择发布时间"
            />
          ) : null}
        </div>
      </Space>
    </Drawer>
  );
};

export default PublishSettingsDrawer;
