import React, {useEffect, useState} from 'react';
import {archiveSlug} from "@/pages/book/article/service";
import {Redirect} from "react-router";
import Page from "@/pages/book/page/Page";
import {typeUrl} from "@/utils/utils";
import {GridContent} from "@ant-design/pro-layout";
import {Card} from "antd";
import styles from "@/pages/book/page/style.less";
import DynamicRouter from "@/pages/DynamicRouter";

const Slug = (props) => {
  const { match, dispatch} = props;
  const [idType, setId] = useState({id: '', pubType: ''});

  const { params={} } = match;

  // eslint-disable-next-line consistent-return
  useEffect(async () => {
    await archiveSlug({contAlias: params?.slug?? ''}).then(res => setId(!res?.data ? undefined : res.data));
  }, [params?.slug]);

  if (!idType)
    return <DynamicRouter key={location.pathname}/>; // 直接渲染DynamicRouter

  if (idType.pubType === 'page')
    return (<Page key={idType.id} data={idType} match={match} dispatch={dispatch} />);
  else
    return idType.id && (<Redirect to={ typeUrl({pubType: idType.pubType, id: idType.id}) } />);
};

export default Slug;
