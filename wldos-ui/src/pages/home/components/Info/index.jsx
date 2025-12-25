import React, {useEffect, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import {connect} from 'umi';
import {genTdkCrumbs} from "@/utils/utils";
import Chapters from "@/components/InfoList/Chapters";
import {fetchSeoCrumbs} from "@/pages/home/service";

/**
 * 供求信息列表模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const Info = (props) => {
  const {
    dispatch,
    url, // location url: ${match.url}
    page, // 当前页号
    pageUrl, // 带分页号的url
    count,    
    slugTerm, // 分类别名：${match.slugCategory}|${match.slugTag}
    id,
    pubType,
    match
  } = props;

  let path;
  if (id) {
    path = `/info-author/${id}.html`;
  } else {
    path = url?? (match?.url?? '');
  } 

  const [title, setTitle] = useState('信息列表');

  useEffect(async () => {

    const res = await fetchSeoCrumbs({ slugTerm, tempType: pubType === 'book' ? 'product' : 'info'});
    if (res?.data) {
      setTitle(res.data.title);
      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, id ? undefined : '信息列表', '');
    }
  }, [url]);

  const body = (<Chapters title={title} slugTerm={slugTerm}  path={path} pubType={pubType} url={url} count={count} page={page} pageUrl={pageUrl}/>);

  return (
    url === '/' ? body : (<PageContainer title={false} breadcrumb={false}>{body}</PageContainer>)
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(Info);