import React, {useEffect, useState} from 'react';
import {archiveSlug} from "@/pages/book/article/service";
import {Redirect} from "react-router";

const Slug = (props) => {
  const {match: {params={}, }, modelType = 'archives'} = props;
  const [id, setId] = useState("");

  // eslint-disable-next-line consistent-return
  useEffect(async () => {
    await archiveSlug({contAlias: params?.slug?? '',
      // redirectPrefix: encodeURIComponent(`${window.location.protocol}//${window.location.hostname}${(window.location.port === '80' || window.location.port === '443') ? '' : `:${window.location.port}`}`)
    }).then(res => setId(res?.data));
  }, []);

  return id && (<Redirect to={`/${modelType}-${id}.html`} />);
};

export default Slug;
