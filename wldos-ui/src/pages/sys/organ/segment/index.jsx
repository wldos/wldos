import React from 'react';
import { useIntl } from 'umi';
import OrgList from '@/pages/sys/org';

/** 会员分群管理页：组织清单的分群分区视图（与编制树分菜单呈现）。 */
const SegmentList = () => {
  const intl = useIntl();
  return (
    <OrgList
      fixedCategory="segment"
      listTitle={intl.formatMessage({ id: 'sys.org.tab.segment', defaultMessage: '会员分群' })}
    />
  );
};

export default SegmentList;
