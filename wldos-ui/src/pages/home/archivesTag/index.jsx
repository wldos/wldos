import React from 'react';
import Archives from "@/pages/home/components/Archives";

const ArchivesTag = (props) => {
  const {
    match: {url, params: { slugTag, page}}
  } = props;

  return (
    
    <Archives
      count={8}
      url={url.indexOf("/page/") > -1 ? url.substring(0, url.indexOf("/page/")) : url}
      pageUrl={url}
      page={page}
      pageName="product"
      termType = "tag"
      slugTerm={slugTag}
    />
  );
};

export default ArchivesTag;