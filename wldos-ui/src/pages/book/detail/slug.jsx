import React from 'react';
import Slug from "@/pages/book/components/Slug";

const SlugArchive = (props) => {
  const { match } = props;

  return (<Slug modelType="product" match={match}/>);
};

export default SlugArchive;
