import React, {useEffect, useState} from 'react';
import {verifyUser} from "@/pages/user/active/service";
import {message} from "antd";
import {redirect} from "@/utils/utils";

const ActiveUser = (props) => {
  const {
    match: { params }
  } = props;

  const [actStatus, setStatus] = useState(undefined);

  useEffect(async () => {
    if (params?.verify) {
      const res = await verifyUser({verify: params.verify});
      const {status, news} = res?.data?? {};
      setStatus(status);

      if (status === 'error') {
        message.warn(news);
        return;
      }

      message.success(news).then(() => redirect());
    }
  }, []);

  return (
    <div align='center'>{actStatus && actStatus === 'ok' ? '激活成功，自动跳转中……' : '激活失败或失效，尝试跳转……'}<br/>
    <a href="#" onClick={() => redirect()}>手动跳转</a>
    </div>
  );
};

export default ActiveUser;
