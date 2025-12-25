import React from 'react';
import Info from "@/pages/home/components/Info";

const InfoCategory = (props) => {
  const {
    match: {url, params: { slugCategory, page}}
  } = props;

  return (
    <Info
      count={8}
      url={url.indexOf("/page/") > -1 ? url.substring(0, url.indexOf("/page/")) : url}
      pageUrl={url}
      page={page}
      pageName="product"
      slugTerm={slugCategory?? '0'}
    />
  );
};

export default InfoCategory;