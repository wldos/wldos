import React, {useEffect} from 'react';
import {queryCodeUrl} from "@/pages/user/login/authcode/service";

const AuthCode = (props) => {
  const {match: {params={}, }, } = props;

  useEffect(async () => {
    const query = await queryCodeUrl({authType: params?.authType?? '',
      redirectPrefix: encodeURIComponent(`${window.location.protocol}//${window.location.hostname}`)
    });

    if (query?.data?.url) {
      window.location.href=query.data.url;
    }
  }, []);

  return (<></>);
};

export default AuthCode;
