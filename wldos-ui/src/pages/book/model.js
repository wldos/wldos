import * as service from './service';
import {queryTagSelect} from "@/pages/sys/tag/service";
import {queryLayerCategory} from "@/pages/sys/category/service";
import {history} from "umi";

const Model = {
  namespace: 'bookSpace',
  state: {
    book: [],
    currentBook: {
      chapter: [],
    },
    currentChapter: {},
    categories: [],
    tagData: []
  },
  effects: {
    * fetchBook({payload, callback}, {call, put}) {
      const response = yield call(service.queryBook, payload);
      yield put({
        type: 'saveBook',
        payload: response || {}
      });
      if (callback) {
        callback(response || {});
      }
    },

    * fetchCurrentBook({payload, callback}, {call, put}) {
      const response = yield call(service.queryCurrentBook, payload);
      yield put({
        type: 'saveCurrentBook',
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

    * addChapter({payload}, {call, put}) {
      const response = yield call(service.addChapter, payload);
      yield put({
        type: 'addCurrentChapter',
        payload: response || {}
      });
    },

    * updateChapter({ payload }, { put }) {
      if (payload)
        yield put({
          type: 'updateCurChapter',
          payload,
        });
    },

    * fetchCategory({_}, {call, put}) {
      const res = yield call(queryLayerCategory, _);
      if (res?.data)
        yield put({
          type: 'saveCategory',
          payload: res.data
        });
    },

    * fetchTag({_}, {call, put}) {
      const res = yield call(queryTagSelect, _);
      if (res?.data)
        yield put({
          type: 'saveTag',
          payload: res.data
        });
    }
  },
  reducers: {
    saveBook(state, { payload }) {
      const {data: {rows = [], ...pageData}} = payload;
      return {...state, book: rows, ...pageData};
    },

    addCurrentBook(state, { payload: {data = {}} }) {
      if (!data)
        return state;
      state.book.push(data);
      return {...state, currentBook: data};
    },

    saveCurrentBook(state, { payload: {data = {}} }) {
      return {...state, currentBook: data };
    },

    updateCurBook(state, { payload: {data = {}}}) {
      const {pubTitle, id} = data;

      const {book} = state;
      const books = [];
      for (let i = 0, len = book.length; i < len; i += 1) {
        const b = book[i];
        let newBook = { ...b };
        if (b.id === id) {
          newBook = {...newBook, pubTitle};
        }
        books.push(newBook);
      }

      return {...state, book: books, currentBook: {...state.currentBook, pubTitle}}
    },

    delCurBook(state, { payload: {data = {}}}) {
      const {id} = data;

      const {book} = state;
      const books = [];
      for (let i = 0, len = book.length; i < len; i += 1) {
        const b = book[i];
        if (b.id !== id) {
          books.push(b);
        }
      }

      return {...state, book: books, }
    },

    addCurrentChapter(state, { payload: {data = {}} }) {
      if (!data)
        return state;

      return {...state, currentBook: {...state.currentBook, chapter: [...state.currentBook.chapter, data]}, currentChapter: data};
    },

    updateCurChapter(state, { payload: {data = {}} }) {
      const {chapter} = state.currentBook;
      const chapters = [];
      for (let i = 0, len = chapter.length; i < len; i += 1) {
        const c = chapter[i];
        let newChapter = { ...c };
        if (c.id === data.id) {
          newChapter = data;
        }
        chapters.push(newChapter);
      }

      return {...state, /* currentChapter: data, */ currentBook: {...state.currentBook, chapter: chapters}};
    },

    delCurChapter(state, { payload: {data = {}} }) {
      const {chapter} = state.currentBook;
      const chapters = [];
      for (let i = 0, len = chapter.length; i < len; i += 1) {
        const c = chapter[i];
        if (c.id !== data.id) {
          chapters.push(c);
        }
      }

      return {...state, currentBook: {...state.currentBook, chapter: chapters}, currentChapter: chapters.length > 0 ? chapters[0] : {}};
    },

    saveCurrentChapter(state, { payload: {data = {}} }) {
      return {...state, currentChapter: data,};
    },

    saveCategory(state, action) {
      return {...state, categories: action.payload};
    },

    saveTag(state, action) {
      return {...state, tagData: action.payload};
    }
  },
};
export default Model;
