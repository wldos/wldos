import React from 'react';
import Products from "@/pages/home/components/Product";

const ProductCategory = (props) => {
  const {
    match: {params: { slugCategory}, url}
  } = props;
  return (
    <Products
      url={url}
      slugTerm={slugCategory}
    />
  );
};

export default ProductCategory;