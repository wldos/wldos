import React from 'react';
import Info from "@/pages/home/components/Info";

const ProductTag = (props) => {
  const {
    match: {params: { slugTag}}
  } = props;

  return (
    <Info
      url={`/info/tag/${slugTag}`}
      slugTerm={slugTag}
      pubType="book"
    />
  );
};

export default ProductTag;