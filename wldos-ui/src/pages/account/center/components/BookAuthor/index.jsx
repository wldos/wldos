import React from 'react';
import Articles from "@/pages/account/center/components/Articles";

const BookAuthor = (props) => {
  const { match: {url, params} } = props;

  return (
    <Articles
      url={url}
      id={params.id}
    />
  );
};

export default BookAuthor;