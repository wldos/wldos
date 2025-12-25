import React, {useEffect} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import { connect } from 'umi';
import {genTdkCrumbs} from "@/utils/utils";
import {fetchSeoCrumbs} from "@/pages/home/service";
import Chapters from "@/components/UserCenterList/Chapters";

/**
 * 个人中心-信息、作品列表模板
 *
 * @param props
 * @returns {JSX.Element}
 * @constructor
 */
const InfoList = (props) => {
  const {
    dispatch,
    url, // location url: ${match.url}
    slugTerm, // 分类别名：${match.slugCategory}|${match.slugTag}
    id,
    pubType,
    categoryList, tagData,
  } = props;
  const path = !id ? url : `/info-author/${id}.html`;

  useEffect(async () => {

    const res = await fetchSeoCrumbs({slugTerm, tempType: 'archives'});
    if (res && res.data) {
      
      await genTdkCrumbs({seoCrumbs: res.data}, dispatch, id ? undefined : '存档列表', '作者存档 - ');
    }
  }, [url, pubType]);

  const body = (<>{<Chapters key={pubType} title="存档列表" slugTerm={slugTerm} path={path} pubType={pubType} dispatch={dispatch}
                             categoryList={categoryList} tagData={tagData}/>}</>);

  return (
    url ? (<PageContainer title={false}>{body}</PageContainer>) : body
  );
};

export default connect(({user}) => ({
  tdk: user.tdk,
}))(InfoList);