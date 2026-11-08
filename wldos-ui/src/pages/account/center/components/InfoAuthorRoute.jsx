import React from 'react';
import Info from '@/pages/home/components/Info';

/**
 * 作者供求信息公开列表（/info-author/:userId.html 及 /page/:page）
 */
const InfoAuthorRoute = (props) => {
  const {
    match: { url, params: { userId, page } },
  } = props;

  const paginationRoot = `/info-author/${userId}`;

  return (
    <Info
      count={8}
      id={userId}
      page={page}
      pageUrl={url}
      url={url.indexOf('/page/') > -1 ? url.substring(0, url.indexOf('/page/')) : paginationRoot}
      pageOneUrl={`${paginationRoot}.html`}
      paginationRoot={paginationRoot}
      pubType="info"
    />
  );
};

export default InfoAuthorRoute;
