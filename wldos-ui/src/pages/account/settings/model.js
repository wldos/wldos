import {queryCity, queryProvince, queryRegionInfo, saveBaseInfo} from './service';
import {queryCurAccount} from "@/services/user";
import {message} from "antd";

const titles = [
  'Alipay',
  'Angular',
  'Ant Design',
  'Ant Design Pro',
  'Bootstrap',
  'React',
  'Vue',
  'Webpack',
];
const avatars = [ // 修改为系统群组头像
  'https://gw.alipayobjects.com/zos/rmsportal/WdGqmHpayyMjiEhcKoVE.png', // Alipay
  'https://gw.alipayobjects.com/zos/rmsportal/zOsKZmFRdUtvpqCImOVY.png', // Angular
  'https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png', // Ant Design
  'https://gw.alipayobjects.com/zos/rmsportal/sfjbOqnsXXJgNCjCzDBL.png', // Ant Design Pro
  'https://gw.alipayobjects.com/zos/rmsportal/siCrBXXhmvTQGWPNLBow.png', // Bootstrap
  'https://gw.alipayobjects.com/zos/rmsportal/kZzEzemZyKLKFsojXItE.png', // React
  'https://gw.alipayobjects.com/zos/rmsportal/ComBAopevLwENQdKWiIn.png', // Vue
  'https://gw.alipayobjects.com/zos/rmsportal/nxkuOJlFJuAUhzlMTCEe.png', // Webpack
];

const Model = {
  namespace: 'account',
  state: {
    currentUser: {},
    province: [],
    city: [],
    isLoading: false,
  },
  effects: {
    *fetchCurrent(_, { call, put }) {
      const response = yield call(queryCurAccount);
      const {province, city, ...other} = response?.data;
      const cityInfo = !city ? [{}] : yield call(queryRegionInfo, city);

      const provName = cityInfo?.data?.provName?? '';
      const cityName = cityInfo?.data?.name?? '';

      yield put({
        type: 'saveCurrentUser',
        payload:
          { ...other,
            notice: [
              {
                id: 'xxx1',
                title: titles[0],
                logo: avatars[0],
                description: '那是一种内在的东西，他们到达不了，也无法触及的',
                updatedAt: new Date(),
                member: '科学搬砖组',
                href: '',
                memberLink: '',
              },
              {
                id: 'xxx2',
                title: titles[1],
                logo: avatars[1],
                description: '希望是一个好东西，也许是最好的，好东西是不会消亡的',
                updatedAt: new Date('2017-07-24'),
                member: '全组都是吴彦祖',
                href: '',
                memberLink: '',
              },
            ],
            notifyCount: 2,
            unreadCount: 2,
            geographic: {
              province: {
                label: provName,
                key: province || 0,
              },
              city: {
                label: cityName,
                key: city || 0,
              },
            },
          },
      });
    },

    *fetchProvince(_, { call, put }) {
      yield put({
        type: 'changeLoading',
        payload: true,
      });
      const response = yield call(queryProvince);
      yield put({
        type: 'setProvince',
        payload: response?.data || {}
      });
    },

    *fetchCity({ payload }, { call, put }) {
      const response = yield call(queryCity, payload);
      yield put({
        type: 'setCity',
        payload: response?.data || {},
      });
    },

    *saveBase({ payload, callback }, { call }) {

      const response = yield call(saveBaseInfo, payload);
      if (response?.data && response.data === 'ok')
        message.success("设置成功！");
      else
        message.error("保存信息失败！");

      if (callback)
        callback(response || {});
    }
  },
  reducers: {
    saveCurrentUser(state, action) {
      return { ...state, currentUser: action.payload || {} };
    },

    changeNotifyCount(state = {}, action) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload.totalCount,
          unreadCount: action.payload.unreadCount,
        },
      };
    },

    setProvince(state, action) {
      return { ...state, province: action.payload };
    },

    setCity(state, action) {
      return { ...state, city: action.payload };
    },

    changeLoading(state, action) {
      return { ...state, isLoading: action.payload };
    },
  },
};
export default Model;
