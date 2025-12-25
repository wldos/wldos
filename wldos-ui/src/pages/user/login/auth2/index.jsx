import React, {useEffect} from 'react';
import {connect, history} from 'umi';
import {message} from "antd";
import {queryCurrent} from "@/services/user";
import {redirect} from "@/utils/utils";

const Auth2Login = (props) => {
  const {userLogin = {}, submitting, match: {params = {},},
    location: {query: {code, state,}}, dispatch } = props;
  const {status,} = userLogin;

  useEffect(async () => {
    // 判断state中的domain，如何与当前domain相同则不跳转，否则跳转到state中的domain（三级跳）
    const index = state.indexOf("-");
    if (index > -1) {
      const newState = state.substr(0, index);
      const realDomain = state.substr(index + 1);

      window.location.href = `${realDomain}?code=${code}&state=${newState}`;
      return;
    }
    const query = await queryCurrent();
    if (query?.data?.userInfo?.id) {

      if (status === 'notActive') {
        message.warn(userLogin.news).then(
          () => {
            history.replace(`/user/active/verify=${query.data.userInfo.id}`);
          });
        return;
      }

      message.success('当前用户已登录！').then(() => redirect());
      return;
    }

    if (dispatch) {

      dispatch({
        type: 'login/loginOAuth',
        payload: {code, state, authType: params.authType},
      });
      
      if (submitting)
        message.loading('正在登录');
    }
  }, []);

  useEffect(() => { // 登录失败时
    if (status === 'error' && userLogin.news) {
      message.error(userLogin.news).then(() => {});
    }
  }, [submitting]);

  return (<></>);
};

export default connect(({login, loading}) => ({
  userLogin: login,
  submitting: loading.effects['login/loginOAuth'],
}))(Auth2Login);
