import React from 'react';
import Articles from './Articles';

/**
 * 作者作品公开列表（/archives-author/:userId.html 及 /page/:page）
 */
const ArchivesAuthorRoute = (props) => {
  const {
    match: { url, params: { userId, page } },
  } = props;

  return (
    <Articles
      count={8}
      authorUserId={userId}
      page={page}
      pageUrl={url}
      match={props.match}
    />
  );
};

export default ArchivesAuthorRoute;
