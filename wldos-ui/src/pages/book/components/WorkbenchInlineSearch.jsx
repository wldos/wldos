import React from 'react';
import { Button, Input } from 'antd';
import { CloseOutlined, SearchOutlined } from '@ant-design/icons';
import styles from '../style.less';

const WorkbenchInlineSearch = ({
  open,
  onOpenChange,
  value,
  onChange,
  onSubmit,
  placeholder,
  compact,
}) => {
  if (!open) {
    return (
      <Button
        size="small"
        type="default"
        icon={<SearchOutlined />}
        className={styles.topIconBtn}
        aria-label="搜索"
        onClick={() => onOpenChange(true)}
      />
    );
  }
  return (
    <div className={compact ? styles.inlineSearchCompact : styles.inlineSearch}>
      <Input.Search
        size="small"
        allowClear
        autoFocus
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        onSearch={(text) => onSubmit(text != null ? text : value)}
        onPressEnter={() => onSubmit(value)}
      />
      <Button
        type="text"
        size="small"
        icon={<CloseOutlined />}
        aria-label="关闭搜索"
        onClick={() => {
          onChange('');
          onSubmit('');
          onOpenChange(false);
        }}
      />
    </div>
  );
};

export default WorkbenchInlineSearch;
