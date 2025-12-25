import React, {useEffect, useState} from 'react';
import ProList from '@ant-design/pro-list';
import {PageContainer} from "@ant-design/pro-layout";
import {Link} from "umi";
import {search} from "@/pages/search/service";

const getUrl = ({pubType, id}) => {
  if (pubType === 'info')
    return `/info-${id}.html`;
  if (pubType === 'book')
    return `/product-${id}.html`;
  if (pubType === 'doc')
    return `/doc/book/${id}.html`;

  return `/archives-${id}.html`;
};

const Search = (props) => {
  const {loading, ...restProps} = props;
  const {location: {state = {wd: undefined,}}} = restProps;
  const [kd, setWord] = useState({keyword: ''});
  const [fs, setFang] = useState(undefined);
  const temp = JSON.parse(sessionStorage.getItem('wo_search'))?? {};
  if (state?.wd && fs === 1 && state.wd !== temp.keyword) {
    const { wd } = state;
    sessionStorage.setItem('wo_search', JSON.stringify(kd));
    setWord({ keyword: wd });
  }
  useEffect(() => {
    setFang(1);
  }, [state?.wd]);

  return (
    <PageContainer
      title={false}
      header={
        {
          breadcrumb: {
            routes: [
              {
                path: '/',
                breadcrumbName: '首页',
              },
              {
                path: '',
                breadcrumbName: kd.keyword ? `关键字 "${kd.keyword}" 的搜索结果` : '输入关键字查询'
              }
            ]
          }
        }
      }
    >
      <ProList
      search={{}} rowKey="id"
      headerTitle="查询结果"
      loading={loading}
      params={kd}
      onSubmit={() => setFang(2)}
      request={async (params, sorter, filter) => {
        const {wd, keyword, ...other} = params;
        let wc = keyword;
        if (fs === 2) {
          sessionStorage.setItem('wo_search', JSON.stringify({keyword: wd}));
          wc = wd;
          setWord({ keyword: wd });
        }
        const res = await search({
          wd: wc,
          ...other,
          sorter,
          filter});
        return Promise.resolve({
          total: res?.data?.total || 0,
          data: res?.data?.rows || [],
          success: true,
        });
      }}
      pagination={{pageSize: 15,}}
      metas={{
        title: {
          dataIndex: 'pubTitle',
          title: '标题',
          search: false,
          render: (dom, entity) => {
            return <Link to={getUrl(entity)} target="_blank" rel="noopener noreferrer">{dom}</Link>;
          }
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

export default Search;
