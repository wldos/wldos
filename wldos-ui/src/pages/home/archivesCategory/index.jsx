import React from 'react';
import Archives from "@/pages/home/components/Archives";

const ArchivesCategory = (props) => {
  const {
    match: {url, params: { slugCategory, page}}
  } = props;

  return (
    <Archives
      count={8}
      url={url.indexOf("/page/") > -1 ? url.substring(0, url.indexOf("/page/")) : url}
      pageUrl={url}
      page={page}
      pageName="all"
      slugTerm={slugCategory}
      termType = "category"
    />
  );
};

export default ArchivesCategory;