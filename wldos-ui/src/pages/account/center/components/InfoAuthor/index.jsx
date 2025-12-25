import React, {useEffect} from 'react';
import InfoList from "@/pages/account/center/components/InfoList";
import {connect} from "umi";

const InfoAuthor = (props) => {
  const { match: {url, params}, dispatch, categoryList, tagData } = props;

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'bookSpace/fetchCategory',
      });
    }
  }, []);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'bookSpace/fetchTag',
      });
    }
  }, []);

  return (
    <InfoList
      url={url}
      pubType={'info'}
      id={params.id}
      categoryList={categoryList}
      tagData={tagData}
    />
  );
};

export default connect(({bookSpace}) => ({
  categoryList: bookSpace.categories,
  tagData: bookSpace.tagData,
}))(InfoAuthor);