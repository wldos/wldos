/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import * as service from '@/services/user';
import { setAuthority} from "@/utils/authority";
import { loadPluginManifest } from '@/services/pluginManifest';

const UserModel = {
  namespace: 'user',
  state: {
    currentUser: {},
    sessionFlags: { isAdmin: false, canTrust: false, tenantAdmin: false },
    menuData: [],
    adminMenu: [],
    adminAuthorities: [],
    route: {}, // 首页动态模板，不是动态路由
    site: {},
    tdk: {},
    pluginManifest: null, // 插件manifest数据
  },
  effects: {
    * fetch(_, {call, put}) {
      const response = yield call(service.queryUsers);
      yield put({
        type: 'save',
        payload: {
          userInfo: response.userInfo,
          currentAuthority: response.currentAuthority
        },
      });
    },

    * fetchSite(_, {call, put, select}) {
        const hostname = typeof window !== 'undefined' ? window.location.hostname : '';
        const state = yield select(s => s.user);
        const cachedHost = state.site?._resolvedHost;
        if (state.site && (state.site.siteDomain || state.site.siteTitle) && cachedHost === hostname) {
          return;
        }
        const res = yield call(service.querySiteSeo);
        if (res && res.data) {
          yield put({
            type: 'saveSite',
            payload: { ...res.data, _resolvedHost: hostname },
          });
        }
    },

    * fetchCurrent(_, {call, put, select}) {
      const response = yield call(service.queryCurrent);

      if (response && response.data) {
        yield put({
          type: 'saveCurrentUser',
          payload: response.data,
        });
      } else {
        yield put({
          type: 'saveCurrentUser',
          payload: {
            userInfo: {},
            menu: [],
            currentAuthority: ['guest'],
          },
        });
      }

      const stateNow = yield select(s => s.user);
      if (!stateNow.pluginManifest) {
        // 同 namespace 异步触发，不阻塞 fetchCurrent（fork 不接受字符串 effect 名）
        yield put({ type: 'fetchPluginManifest' });
      }
    },

    /** 插件 UI manifest：后台异步拉取，不阻塞 currentUser / 菜单 */
    * fetchPluginManifest(_, {call, put, select}) {
      const state = yield select(s => s.user);
      if (state.pluginManifest) {
        return state.pluginManifest;
      }
      try {
        const manifest = yield call(loadPluginManifest);
        yield put({
          type: 'savePluginManifest',
          payload: manifest,
        });
        return manifest;
      } catch (error) {
        console.warn('[UserModel] 加载 pluginManifest 失败:', error);
        return null;
      }
    },

    * fetchAdminMenu({payload}, {call, put}) {
      const menus = yield call(service.queryMenu, payload);
      yield put({
        type: 'saveMenu',
        payload: menus.data || [],
      });
    },

    * fetchAdminAuthorities(_, { call, put }) {
      try {
        const res = yield call(service.queryAdminAuthorities);
        const list = Array.isArray(res?.data) ? res.data : [];
        yield put({
          type: 'saveAdminAuthorities',
          payload: list,
        });
        return list;
      } catch (error) {
        console.warn('[UserModel] 加载 adminAuthorities 失败:', error);
        yield put({
          type: 'saveAdminAuthorities',
          payload: [],
        });
        return [];
      }
    },
    * getDomainRoute({callback}, { select }) {
      const res = yield select((state) => state.user);
      if (callback) {
       callback(res.route);
      }
    },

    /**
     * 刷新插件 manifest
     * 在插件安装、升级、卸载等操作后调用，更新插件清单
     */
    * refreshPluginManifest(_, {call, put}) {
      try {
        const manifest = yield call(loadPluginManifest);
        yield put({
          type: 'savePluginManifest',
          payload: manifest,
        });
        return manifest;
      } catch (error) {
        console.warn('[UserModel] 刷新 pluginManifest 失败:', error);
        // 刷新失败不影响其他操作，返回 null
        return null;
      }
    },
  },
  reducers: {
    saveCurrentUser(state, {payload}) {
      if (payload)
        setAuthority(payload);
      const userInfo = payload?.userInfo || {};
      return {
        ...state, currentUser: userInfo,
        sessionFlags: {
          // Jackson 对 boolean isAdmin 可能序列化为 admin，兼容两种字段名
          isAdmin: !!(payload?.isAdmin ?? payload?.admin),
          canTrust: !!payload?.canTrust,
          tenantAdmin: !!payload?.tenantAdmin,
          operatorComId: userInfo?.operatorComId,
          workComId: userInfo?.workComId,
        },
        menuData: payload?.menu || [],
        route: payload?.route || state.route || {},
      };
    },

    saveMenu(state, action) {
      return {
        ...state,
        adminMenu: action.payload || []
      };
    },

    saveAdminAuthorities(state, action) {
      return {
        ...state,
        adminAuthorities: action.payload || [],
      };
    },

    saveSite(state, action) {
      return {...state,
        site: action.payload,
      };
    },

    saveTdk(state, action) {
      return {
        ...state,
        tdk: action.payload,
      };
    },

    savePluginManifest(state, action) {
      return {
        ...state,
        pluginManifest: action.payload,
      };
    },

    changeNotifyCount(
      state = {
        currentUser: {},
      },
      action,
    ) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload.totalCount,
          unreadCount: action.payload.unreadCount,
        },
      };
    },
  },
};
export default UserModel;
