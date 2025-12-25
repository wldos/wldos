import { message } from 'antd';
import * as service from './service';

const Model = {
  namespace: 'sysOptions',
  state: {
    options: {}
  },
  effects: {
    * fetchConfig(_, { call, put }) {
      const response = yield call(service.fetchSysOptions());
      const config = response?.data?? {};

      yield put({
        type: 'updateConfig',
        payload: config,
      });
    },

    * saveConfig({ payload, callback }, { call }) {
      const response = yield call(service.configOptions, payload);
      if (response?.data && response.data === 'ok')
        message.success("配置成功！");
      else
        message.error("配置失败！");

      if (callback)
        callback(response || {});
    },
  },
  reducers: {
    updateConfig(state, action) {
      return { ...state, options: action.payload || {} };
    },
  }
};
export default Model;
