import React, {useEffect} from 'react';
import {queryCodeUrl} from "@/pages/user/login/authcode/service";
import { POST_LOGIN_RETURN_KEY } from '@/pages/commercial/cms-access/utils/accessCodeLoginRedirect';

const AuthCode = (props) => {
  const { match: { params = {} }, location } = props;

  useEffect(async () => {
    const returnPath = location?.query?.redirect;
    if (returnPath && typeof window !== 'undefined') {
      sessionStorage.setItem(POST_LOGIN_RETURN_KEY, returnPath);
    }
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
