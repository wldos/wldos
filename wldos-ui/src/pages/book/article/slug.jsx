import React from 'react';
import Slug from "@/pages/book/components/Slug";

const SlugArchive = (props) => {
  const { match } = props;

  return (<Slug modelType="archives" match={match}/>);
};

export default SlugArchive;
