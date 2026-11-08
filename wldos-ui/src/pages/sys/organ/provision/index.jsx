import React from 'react';
import { connect, history } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { Result, Spin } from 'antd';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import TenantProvisionWizard from './components/TenantProvisionWizard';

const TenantProvisionPage = ({ isAdmin, sessionPending }) => {
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
        <Result
          status="403"
          title="无权限"
          subTitle="租户入驻仅平台超管可用"
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer
      {...adminListPageContainerProps}
      title="租户入驻"
      subTitle="一次性开通新租户：公司、主站、体系及可选租户管理员"
    >
      <TenantProvisionWizard
        embedded
        onSuccess={(data) => {
          const comId = data?.comId;
          history.push(
            comId
              ? `/admin/organ/architecture?focusType=company&focusId=${comId}`
              : '/admin/organ/architecture',
          );
        }}
      />
    </PageContainer>
  );
};

export default connect(({ user, loading }) => {
  const fetchSettled = loading.effects['user/fetchCurrent'] === false;
  return {
    ...buildAuthorityHelper(user, '/admin'),
    sessionPending: !fetchSettled,
  };
})(TenantProvisionPage);
