import React, { useEffect, useMemo, useState } from 'react';
import ProList from '@ant-design/pro-list';
import { PageContainer } from '@ant-design/pro-layout';
import { Link, connect } from 'umi';
import { Space, Switch, Typography } from 'antd';
import { search } from '@/pages/search/service';

const getUrl = ({ pubType, id }) => {
  if (pubType === 'info') return `/info-${id}.html`;
  if (pubType === 'book') return `/content-${id}.html`;
  if (pubType === 'doc') return `/doc/book/${id}.html`;
  return `/archives-${id}.html`;
};

const readStoredWd = () => {
  try {
    const raw = sessionStorage.getItem('wo_search');
    if (!raw) return '';
    const parsed = JSON.parse(raw);
    return String(parsed?.wd ?? parsed?.keyword ?? '').trim();
  } catch (e) {
    return '';
  }
};

const readStoredInternal = () => {
  try {
    const raw = sessionStorage.getItem('wo_search');
    if (!raw) return false;
    const parsed = JSON.parse(raw);
    return parsed?.includeInternal === true;
  } catch (e) {
    return false;
  }
};

const Search = (props) => {
  const { loading, currentUser, ...restProps } = props;
  const navWd = String(restProps?.location?.state?.wd ?? '').trim();
  const loggedIn = Boolean(currentUser?.id);
  const [searchWd, setSearchWd] = useState(() => navWd || readStoredWd());
  const [includeInternal, setIncludeInternal] = useState(() => readStoredInternal());

  useEffect(() => {
    if (navWd) {
      setSearchWd(navWd);
      sessionStorage.setItem(
        'wo_search',
        JSON.stringify({ wd: navWd, includeInternal: loggedIn ? includeInternal : false }),
      );
    }
  }, [navWd, includeInternal, loggedIn]);

  useEffect(() => {
    if (!loggedIn && includeInternal) {
      setIncludeInternal(false);
    }
  }, [loggedIn, includeInternal]);

  const listParams = useMemo(
    () => ({
      wd: searchWd,
      scope: loggedIn && includeInternal ? 'internal' : 'public',
    }),
    [searchWd, includeInternal, loggedIn],
  );

  return (
    <PageContainer
      title={false}
      header={{
        breadcrumb: {
          routes: [
            { path: '/', breadcrumbName: '首页' },
            {
              path: '',
              breadcrumbName: searchWd
                ? `关键字 "${searchWd}" 的搜索结果`
                : '输入关键字查询',
            },
          ],
        },
      }}
    >
      <ProList
        key={`${searchWd || '__empty__'}_${listParams.scope}`}
        search={{}}
        rowKey="id"
        headerTitle="查询结果"
        loading={loading}
        params={listParams}
        form={{ initialValues: { wd: searchWd } }}
        request={async (params, sorter, filter) => {
          const wc = String(params?.wd ?? '').trim();
          if (wc) {
            sessionStorage.setItem(
              'wo_search',
              JSON.stringify({
                wd: wc,
                includeInternal: loggedIn && (params?.scope === 'internal' || includeInternal),
              }),
            );
            setSearchWd(wc);
          }
          const res = await search({
            wd: wc,
            scope: params?.scope,
            ...params,
            sorter,
            filter,
          });
          return {
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: true,
          };
        }}
        pagination={{ pageSize: 15 }}
        metas={{
          title: {
            dataIndex: 'pubTitle',
            title: '标题',
            search: false,
            render: (dom, entity) => (
              <Link to={getUrl(entity)} target="_blank" rel="noopener noreferrer">
                {dom}
              </Link>
            ),
          },
          wd: {
            title: '关键字',
            valueType: 'input',
          },
        }}
      />
    </PageContainer>
  );
};

export default connect(({ user }) => ({ currentUser: user.currentUser || {} }))(Search);
