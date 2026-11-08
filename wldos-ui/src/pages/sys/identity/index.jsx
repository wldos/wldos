import React, { useEffect, useState } from 'react';
import { connect } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { Result, Spin } from 'antd';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import IdentityRegistryPanel from '@/pages/sys/org/components/IdentityRegistryPanel';
import { getComSelectOption } from '@/pages/sys/com/service';

/** 身份登记册：平台内置身份清单，仅超管可见。 */
const IdentityRegistryPage = ({ isAdmin, sessionPending }) => {
  const [comList, setComList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    getComSelectOption().then((res) => {
      if (!cancelled) {
        setComList(res?.data ?? []);
        setLoading(false);
      }
    });
    return () => {
      cancelled = true;
    };
  }, []);

  if (sessionPending) {
    return (
      <PageContainer {...adminListPageContainerProps}>
        <Spin size="large" style={{ display: 'block', margin: '120px auto' }} />
      </PageContainer>
    );
  }

  if (!isAdmin) {
    return (
      <PageContainer {...adminListPageContainerProps}>
        <Result status="403" title="无权限" subTitle="身份登记册仅平台超管可用" />
      </PageContainer>
    );
  }

  return (
    <PageContainer {...adminListPageContainerProps} title="身份登记册">
      {loading ? (
        <Spin size="large" style={{ display: 'block', margin: '120px auto' }} />
      ) : (
        <IdentityRegistryPanel comList={comList} />
      )}
    </PageContainer>
  );
};

export default connect(({ user, loading }) => {
  const fetchSettled = loading.effects['user/fetchCurrent'] === false;
  return {
    ...buildAuthorityHelper(user, '/admin'),
    sessionPending: !fetchSettled,
  };
})(IdentityRegistryPage);
