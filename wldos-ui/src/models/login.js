/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import {stringify} from 'querystring';
import {history} from 'umi';
import * as service from '@/services/login';
import {clearAuthority, setAuthority} from '@/utils/authority';
import {getHome, getPageQuery, redirect} from '@/utils/utils';
import {message} from 'antd';
import {auth2Login} from "@/pages/user/login/auth2/service";

const Model = {
  namespace: 'login',
  state: {
    status: undefined,
  },
  effects: {
    * login({payload}, {call, put}) {
      const response = yield call(service.accountLogin, payload);
      if (response?.data?.status === 'ok') {
        const news = response.data.news || '🎉 🎉 🎉  登录成功！';
        yield put({
          type: 'changeLoginStatus',
          payload: {
            ...response.data,
            news
          },
        });
        message.success(news).then(
          () => redirect());
      } else if (response?.data?.status === 'notActive') {
        yield put({
          type: 'changeLoginStatus',
          payload: {
            ...response.data,
            news: '待激活用户，请先到注册邮箱激活邮件激活完整功能'
          },
        });
        message.warn('待激活用户，请先到注册邮箱激活邮件激活完整功能').then(() => redirect());
      }
      else {
        yield put({
          type: 'changeLoginStatus',
          payload: {
            status: 'error',
            type: response?.data?.type,
            currentAuthority: 'guest',
            news: response?.message?? response?.data?.news?? '未知原因登录失败，请重试！'
          },
        });
      }
    },

    * loginOAuth({payload}, {call, put}) {
      const response = yield call(auth2Login, payload);
      if (response?.success) {
        const news = response.data.news || '🎉 🎉 🎉  登录成功！';
        yield put({
          type: 'changeLoginStatus',
          payload: {
            ...response.data,
            news
          },
        });
        message.success(news).then(
          () => redirect());

      } else {
        yield put({
          type: 'changeLoginStatus',
          payload: {
            status: 'error',
            type: response?.data?.type,
            currentAuthority: 'guest',
            news: response?.message || response?.data?.news || '未知原因登录失败，请重试！'
          },
        });
      }
    },

    * logout({payload}, {call, put}) {
      const res = yield call(service.logout, payload);

      // 清除本地缓存
      yield call(clearAuthority);

      yield put({
        type: 'user/saveCurrentUser',
        payload: res.data || {},
      });

      const {isManageSide = false} = payload;

      if (!isManageSide) { // 前端注销回主页
        history.replace({
          pathname: getHome(),
        });
        window.location.reload();
      } else { // 后端注销回登录页
        const {redirect} = getPageQuery();

        if (window.location.pathname !== '/user/login' && !redirect) {
          history.replace({
            pathname: '/user/login',
            search: stringify({
              redirect: window.location.href,
            }),
          });
          window.location.reload();
        }
      }
    },
  },
  reducers: {
    changeLoginStatus(state, {payload}) {
      if (payload.currentAuthority)
        setAuthority(payload);
      return {...state, status: payload.status, type: payload.type, news: payload.news};
    },
  },
};
export default Model;
