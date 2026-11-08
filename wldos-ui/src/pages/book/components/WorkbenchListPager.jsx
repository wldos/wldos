import React from 'react';
import { Pagination } from 'antd';
import styles from '../style.less';

const PAGE_SIZE_OPTIONS = ['10', '20', '30', '50'];

const WorkbenchListPager = ({ current, pageSize, total, onChange, compact }) => {
  const totalCount = total != null ? Number(total) : 0;

  if (totalCount === 0) {
    return null;
  }

  const handleChange = (page, size) => {
    if (onChange) {
      onChange(page, size || pageSize);
    }
  };

  return (
    <div className={compact ? styles.listPagerCompact : styles.listPager}>
      <Pagination
        size="small"
        current={current}
        pageSize={pageSize}
        total={totalCount}
        showSizeChanger
        pageSizeOptions={PAGE_SIZE_OPTIONS}
        showLessItems
        showTotal={false}
        onChange={handleChange}
        onShowSizeChange={handleChange}
      />
    </div>
  );
};

export default WorkbenchListPager;
