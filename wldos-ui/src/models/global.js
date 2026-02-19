/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import {
  queryNotices,
  queryNoticesCount,
  queryAdminTicketNoticesCount,
  queryAdminTicketNotices,
} from '@/services/user';

const GlobalModel = {
  namespace: 'global',
  state: {
    collapsed: false,
    notices: [],
    /** 'user' | 'adminTicket'：管理端布局下为 adminTicket 时轮询工单待处理数并展示 */
    noticeMode: 'user',
  },
  effects: {
    /** 轻量轮询：只更新角标，有新增未读时可选桌面提醒；管理端为工单待处理数 */
    *fetchNoticesCount(_, { call, put, select }) {
      const noticeMode = yield select((state) => state.global?.noticeMode || 'user');
      const prevUnread = yield select((state) => state.user?.currentUser?.unreadCount ?? 0);
      const api = noticeMode === 'adminTicket' ? queryAdminTicketNoticesCount : queryNoticesCount;
      const { totalCount = 0, unreadCount = 0 } = yield call(api) || {};
      yield put({
        type: 'user/changeNotifyCount',
        payload: { totalCount, unreadCount },
      });
      if (
        typeof Notification !== 'undefined' &&
        Notification.permission === 'granted' &&
        unreadCount > prevUnread &&
        prevUnread >= 0
      ) {
        try {
          const n = unreadCount - prevUnread;
          const title = n > 1 ? `您有 ${n} 条新通知` : (noticeMode === 'adminTicket' ? '新工单待处理' : '新工单提醒');
          new Notification(title, { body: noticeMode === 'adminTicket' ? '请到工单管理查看' : '请及时处理', icon: '/favicon.ico' });
        } catch (e) {}
      }
    },

    *fetchNotices(_, { call, put, select }) {
      const noticeMode = yield select((state) => state.global?.noticeMode || 'user');
      const prevNotices = yield select((state) => state.global.notices || []);
      const prevLen = Array.isArray(prevNotices) ? prevNotices.length : 0;
      const api = noticeMode === 'adminTicket' ? () => queryAdminTicketNotices(20) : queryNotices;
      const data = yield call(api) || [];
      yield put({
        type: 'saveNotices',
        payload: data,
      });
      const unreadCount = yield select(
        (state) => state.global.notices.filter((item) => !item.read).length,
      );
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: data.length,
          unreadCount,
        },
      });
      // 新工单/新通知：浏览器桌面通知（需用户已授权）
      if (
        typeof Notification !== 'undefined' &&
        Notification.permission === 'granted' &&
        Array.isArray(data) &&
        data.length > prevLen &&
        prevLen >= 0
      ) {
        try {
          const n = data.length - prevLen;
          const title = n > 1 ? `您有 ${n} 条新通知` : (noticeMode === 'adminTicket' ? '新工单待处理' : '新工单提醒');
          const body = data[0] && data[0].title ? data[0].title : (noticeMode === 'adminTicket' ? '请到工单管理查看' : '请及时处理');
          new Notification(title, { body, icon: '/favicon.png' });
        } catch (e) {
          // ignore
        }
      }
    },

    *clearNotices({ payload }, { put, select }) {
      yield put({
        type: 'saveClearedNotices',
        payload,
      });
      const count = yield select((state) => state.global.notices.length);
      const unreadCount = yield select(
        (state) => state.global.notices.filter((item) => !item.read).length,
      );
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: count,
          unreadCount,
        },
      });
    },

    *changeNoticeReadState({ payload }, { put, select }) {
      const notices = yield select((state) =>
        state.global.notices.map((item) => {
          const notice = { ...item };

          if (notice.id === payload) {
            notice.read = true;
          }

          return notice;
        }),
      );
      yield put({
        type: 'saveNotices',
        payload: notices,
      });
      yield put({
        type: 'user/changeNotifyCount',
        payload: {
          totalCount: notices.length,
          unreadCount: notices.filter((item) => !item.read).length,
        },
      });
    },
  },
  reducers: {
    changeLayoutCollapsed(state = { notices: [], collapsed: true }, { payload }) {
      return { ...state, collapsed: payload };
    },

    setNoticeMode(state, { payload }) {
      return { ...state, noticeMode: payload === 'adminTicket' ? 'adminTicket' : 'user' };
    },

    saveNotices(state, { payload }) {
      return {
        collapsed: false,
        ...state,
        notices: payload,
      };
    },

    saveClearedNotices(
      state = {
        notices: [],
        collapsed: true,
      },
      { payload },
    ) {
      return {
        ...state,
        collapsed: false,
        notices: state.notices.filter((item) => item.type !== payload),
      };
    },
  },
};
export default GlobalModel;
