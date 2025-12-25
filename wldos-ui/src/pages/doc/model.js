import * as service from './service';

const DocModel = {
  namespace: 'doc',
  state: {
    doc: [],
    currentDoc: {
      chapter: [],
    },
    currentChapter: {},
  },
  effects: {
    * fetchDoc({payload, callback}, {call, put}) {
      const menus = yield call(service.queryDocList, payload);
      yield put({
        type: 'saveDoc',
        payload: menus?.data?.rows || [],
      })

      if (callback) {
        callback(menus || []);
      }
    },

    * fetchCurrentDoc({payload, callback}, {call, put}) {
      const response = yield call(service.queryCurrentDoc, payload);
      yield put({
        type: 'saveCurrentDoc',
        payload: response || {}
      });

      if (callback) {
        callback(response || {});
      }
    },

    * fetchCurrentChapter({payload}, {call, put}) {
      if (payload.chapterId === '') {
        yield put({
          type: 'saveCurrentChapter',
          payload: {},
        });
        return;
      }
      const response = yield call(service.queryCurrentChapter, payload);
      yield put({
        type: 'saveCurrentChapter',
        payload: response || {}
      });
    },
  },
  reducers: {
    saveDoc(state, action) {
      return {
        ...state,
        doc: action.payload || []
      };
    },

    saveCurrentDoc(state, { payload: {data = {}} }) {

      const {id, chapter} = data;
      const {doc} = state;
      const docs = [];
      for (let i = 0, len = doc.length; i < len; i += 1) {
        const b = doc[i];
        let newDoc = { ...b };
        if (b.id === id) {
          newDoc = {...newDoc, chapter}; // merge chapter
        }
        docs.push(newDoc);
      }

      return {...state, doc: docs, currentDoc: data}
    },

    saveCurrentChapter(state, { payload: {data = {}} }) {
      return {...state, currentChapter: data,};
    },

  },
};
export default DocModel;
