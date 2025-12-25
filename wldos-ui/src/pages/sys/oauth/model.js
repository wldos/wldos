import {fetchCurConfig, saveConfig} from './service';
import {message} from "antd";

const Model = {
  namespace: 'oauth2',
  state: {
    wechat: {
      appId: '',
      appSecret: '',
      redirectUri: '',
      userInfoUri: '',
      scope: '',
      codeUri: '',
      accessTokenUri: '',
      refreshTokenUri: '',
    },
    qq: {
      appId: '',
      appSecret: '',
      redirectUri: '',
      userInfoUri: '',
      scope: '',
      codeUri: '',
      accessTokenUri: '',
      refreshTokenUri: '',
    },
    weibo: {
      appId: '',
      appSecret: '',
      redirectUri: '',
      userInfoUri: '',
      scope: '',
      codeUri: '',
      accessTokenUri: '',
      refreshTokenUri: '',
    },
    isLoading: false,
  },
  effects: {
    *fetchConfig({ payload }, { call, put }) {
      const {oauthType} = payload;
      const response = yield call(fetchCurConfig, oauthType);
      const config = response?.data?? {};

      if (oauthType === 'wechat') {
        yield put({
          type: 'saveConfigWechat',
          payload: config,
        });
      } else if (oauthType === 'qq'){
        yield put({
          type: 'saveConfigQQ',
          payload:  config,
        });
      } else {
        yield put({type: 'saveConfigWeibo',
         payload: config,
        });
      }
    },

    *saveOAuthConfig({ payload, callback }, { call }) {

      const response = yield call(saveConfig, payload);
      if (response?.data && response.data === 'ok')
        message.success("设置成功！");
      else
        message.error("保存信息失败！");

      if (callback)
        callback(response || {});
    }
  },
  reducers: {
    saveConfigWechat(state, action) {
      return { ...state, wechat: action.payload || {} };
    },

    saveConfigQQ(state, action) {
      return { ...state, qq: action.payload || {} };
    },

    saveConfigWeibo(state, action) {
      return { ...state, weibo: action.payload || {} };
    },

    changeLoading(state, action) {
      return { ...state, isLoading: action.payload };
    },
  },
};
export default Model;
