import {queryBookList} from './service';

const count = 16

const Model = {
  namespace: 'workBooks',
  state: {
    list: [],
    total: 0,
    current: 1,
    pageSize: count,
  },
  effects: {
    * fetch({payload}, {call, put}) {
      const response = yield call(queryBookList, payload);
      const {rows = [], total = 0, current = 1, pageSize = count} = response?.data || {};
      yield put({
        type: 'queryList',
        payload: {
          list: rows,
          total, current, pageSize,
        },
      });
    },
    * appendFetch({payload}, {call, put, select}) {
      const res = yield select((state) => state.workBooks);
      const totalPageNum = Math.floor( ( parseInt(res.total, 10) - 1 ) / parseInt(res.pageSize, 10) ) + 1;
      if (payload.current > totalPageNum) {
        return;
      }
      const response = yield call(queryBookList, payload);
      const {rows = [], total = 0, current = 1, pageSize = count} = response?.data || {};
      if (total === 0) {
        return;
      }
      yield put({
        type: 'appendList',
        payload: {
          list: rows,
          pageData: {
            total, current, pageSize,
            hasMore: true,
          }
        },
      });
    },
  },
  reducers: {
    queryList(state, {payload}) {
      return {...state, ...payload};
    },
    appendList(state, action) {
      return {
        ...state,
        list: state.list.concat(action.payload.list), ...action.payload.pageData
      };
    },
  },
};
export default Model;
