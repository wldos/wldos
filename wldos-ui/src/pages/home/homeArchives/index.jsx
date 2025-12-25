import React from 'react';
import Archives from "@/pages/home/components/Archives";

const Index = (props) => {
  // 直接从 props 中读取 home/index.jsx 传递的参数
  const {slugTerm, url, pageUrl, page} = props;

  const state = {slugTerm, url, pageUrl, page};

  return (<Archives {...state} count={8} pageName="all" />);
};

export default Index;
