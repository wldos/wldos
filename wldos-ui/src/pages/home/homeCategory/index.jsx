import React from 'react';
import Category from "@/pages/home/components/Category";

const Index = (props) => {
  // 直接从 props 中读取 home/index.jsx 传递的参数
  const {slugTerm, url, pageUrl, page} = props;

  const state = {slugTerm, url, pageUrl, page};

  return (<Category {...state} />);
};

export default Index;