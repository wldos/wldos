import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Popover, Space } from 'antd';
import { FilterOutlined } from '@ant-design/icons';
import { fetchEnumPubType } from '@/services/enum';
import styles from '../style.less';

const WorkbenchInlineTypeFilter = ({ value, onApply, onPopoverOpenChange }) => {
  const applied = Array.isArray(value) ? value : [];
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const [draft, setDraft] = useState([]);

  useEffect(() => {
    let cancelled = false;
    fetchEnumPubType().then((res) => {
      if (cancelled || res?.code !== 200 || !res?.data) {
        return;
      }
      setOptions(res.data.map((item) => ({ label: item.label, value: item.value })));
    });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (open) {
      setDraft(applied);
    }
  }, [open, applied]);

  const handleOpenChange = (next) => {
    setOpen(next);
    if (onPopoverOpenChange) {
      onPopoverOpenChange(next);
    }
  };

  const confirm = () => {
    onApply(draft);
    setOpen(false);
    if (onPopoverOpenChange) {
      onPopoverOpenChange(false);
    }
  };

  const reset = () => {
    setDraft([]);
    onApply([]);
    setOpen(false);
    if (onPopoverOpenChange) {
      onPopoverOpenChange(false);
    }
  };

  const content = (
    <div className={styles.typeFilterPanel}>
      <div className={styles.typeFilterOptions}>
        <Checkbox.Group
          value={draft}
          onChange={(next) => setDraft(next)}
        >
          <Space direction="vertical" size={4}>
            {options.map((opt) => (
              <Checkbox key={opt.value} value={opt.value}>
                {opt.label}
              </Checkbox>
            ))}
          </Space>
        </Checkbox.Group>
      </div>
      <div className={styles.typeFilterActions}>
        <Button size="small" type="link" onClick={reset}>
          重置
        </Button>
        <Button size="small" type="primary" onClick={confirm}>
          确定
        </Button>
      </div>
    </div>
  );

  return (
    <Popover
      trigger="click"
      placement="bottomLeft"
      open={open}
      onOpenChange={handleOpenChange}
      content={content}
      overlayClassName={styles.typeFilterPopover}
    >
      <Button
        size="small"
        type={applied.length ? 'primary' : 'default'}
        icon={<FilterOutlined />}
        className={styles.topIconBtn}
        aria-label="类型筛选"
        title="类型筛选"
      />
    </Popover>
  );
};

export default WorkbenchInlineTypeFilter;
