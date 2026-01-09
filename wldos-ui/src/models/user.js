/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
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
    menuData: [],
    adminMenu: [],
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

    * fetchSite(_, {call, put}) {
        const res = yield call(service.querySiteSeo);

        if (res && res.data) {
          yield put({
            type: 'saveSite',
            payload: res.data,
          });
        }
    },

    * fetchCurrent(_, {call, put, select}) {
      const response = yield call(service.queryCurrent);
      const dyn = yield call(service.fetchDynRoutes);

      // 同时加载插件 manifest（用户端和管理端共享）
      // 检查是否已加载，避免重复加载
      const state = yield select(state => state.user);
      if (!state.pluginManifest) {
        try {
          const manifest = yield call(loadPluginManifest);
          yield put({
            type: 'savePluginManifest',
            payload: manifest,
          });
        } catch (error) {
          // manifest 加载失败不影响用户信息加载
          console.warn('[UserModel] 加载 pluginManifest 失败:', error);
        }
      }

      if (response && response.data) {
        yield put({
          type: 'saveCurrentUser',
          payload: {...response.data, route: dyn?.data},
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
    },
    * fetchAdminMenu({payload}, {call, put}) {
      const menus = yield call(service.queryMenu, payload);
      yield put({
        type: 'saveMenu',
        payload: menus.data || [],
      });
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
      return {
        ...state, currentUser: payload.userInfo,
        menuData: payload.menu || [],
        route: payload.route || state.route || {},
      };
    },

    saveMenu(state, action) {
      return {
        ...state,
        adminMenu: action.payload || []
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
