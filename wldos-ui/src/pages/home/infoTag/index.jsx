import React from 'react';
import Info from "@/pages/home/components/Info";

const InfoTag = (props) => {
  const {
    match: {url, params: { slugTag, page}}
  } = props;

  return (
    <Info
      count={8}
      url={url.indexOf("/page/") > -1 ? url.substring(0, url.indexOf("/page/")) : url}
      pageUrl={url}
      page={page}
      pageName="info"
      slugTerm={slugTag}
    />
  );
};

export default InfoTag;