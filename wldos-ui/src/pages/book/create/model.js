import {addInfo, updateInfo} from './service';
import {message} from "antd";
import {queryInfoCategory} from "@/pages/sys/category/service";

const Model = {
  namespace: 'bookInfo',
  state: {
    current: 'category',
    step: {},
    privacyEnum: [],
    geographic: {},
    list: []
  },
  effects: {
    *submitStepForm({ payload }, { call, put }) {
      const res = yield call(addInfo, payload);
      const { id, error }= res?.data || {};

      if (!id) {
        message.error(error?? '信息保存异常').then();
        return;
      }

      yield put({
        type: 'saveStepFormData',
        payload: {
          ...payload,
          id
        },
      });
      yield put({
        type: 'saveCurrentStep',
        payload: 'result',
      });
    },

    *fetchCategory(_, { call, put }) {
      const res = yield call(queryInfoCategory);
      yield put({
        type: 'saveCategory',
        payload: res?.data || []
      });
    }
  },
  reducers: {
    saveCurrentStep(state, { payload }) {
      return { ...state, current: payload };
    },

    saveStepFormData(state, { payload }) {
      return { ...state, step: { ...state.step, ...payload } };
    },

    savePrivacyEnum(state, { payload }) {
      return {...state, ...payload};
    },

    saveGeographic(state, { payload }) {
      return {...state, geographic: payload};
    },

    clearAndSaveCurStep(state, { payload }) {
      return {...state,
        current: payload,
        step: {},
        privacyEnum: [],
        geographic: {},
      };
    },

    saveCategory(state, action) {
      return {...state, list: action.payload,};
    },
  },
};
export default Model;
