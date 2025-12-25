import React from 'react';
import Category from "@/pages/home/components/Category";

const Categories = (props) => {
  const {
    match: {params: { slugCategory}, url}
  } = props;

  return (
    <Category
      url={url}
      slugTerm={slugCategory}
    />
  );
};

export default Categories;