/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import {extend} from 'umi-request';
import {notification} from 'antd';
import {PLUGIN_CODE} from '../config/plugin';

/**
 * request 网络请求工具
 * 
 * 运行时优先使用主应用的 request 实例（如果存在），确保插件和主应用使用同一个 application
 * 开发时使用插件自己的 request 工具，保持完全独立
 */

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '权限不足，访问被禁止。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

const errorHandler = (error) => {
  const {response} = error;

  if (response && response.status) {
    const {status} = response;
    if (status === 401) {
      notification.warn({
        message: '未授权',
        description: '请重新登录',
      });
    } else if (status === 403) {
      notification.warn({
        message: '未授权操作',
      });
    }
  } else if (!response) {
    notification.warn({
      message: 'hold on! 您的请求未及响应！',
    });
  }
  return response;
};

const req = extend({errorHandler});

// 响应拦截器（处理新的统一响应格式：code, message, data, success）
req.interceptors.response.use(
  (response) => {
    // 处理业务响应（新格式：code, message, data, success）
    // 注意：如果返回的是String（JSON字符串），需要先解析
    let res = response.data;
    if (typeof res === 'string') {
      try {
        res = JSON.parse(res);
      } catch (e) {
        // 如果不是JSON字符串，直接返回原始数据
        return response;
      }
    }
    
    // 检查是否是新的响应格式
    if (res && (res.code !== undefined || res.success !== undefined)) {
      const code = res.code ?? 200;
      const message = res.message || '操作成功';
      const success = res.success ?? (code === 200);
      const data = res.data;
      
      // 业务成功
      if (success && code === 200) {
        // 直接返回data，便于前端使用
        response.data = data;
        return response;
      }
      
      // 业务失败
      const errorMessage = codeMessage[code] || message;
      
      // 401/403 特殊处理
      if (code === 401) {
        notification.warn({
          message: '未授权',
          description: '请重新登录',
        });
      } else if (code === 403) {
        notification.warn({
          message: '权限不足',
          description: errorMessage,
        });
      } else {
        notification.error({
          message: '操作失败',
          description: errorMessage,
        });
      }
      
      // 抛出错误
      const error = new Error(errorMessage);
      error.response = response;
      error.code = code;
      return Promise.reject(error);
    }
    
    // 如果不是新格式，直接返回（兼容处理）
    return response;
  },
  (error) => {
    // HTTP错误处理（网络错误、超时等）由errorHandler处理
    return Promise.reject(error);
  }
);

const pluginRequestLocal = (url, params) => {
  const {method = 'GET'} = params || {};
  const options = {
    method,
    headers: {
      Accept: '*/*',
    },
  };
  if (method === 'GET') {
    Object.assign(options, params);
  } else {
    Object.assign(options, {
      data: params.data,
    });
  }

  return req(url, options);
};

/**
 * 插件 request 方法
 * 运行时必须使用主应用暴露的 pluginRequest（自动添加 /plugins/{pluginCode} 前缀）
 * 开发时使用插件自己的 request 工具
 * 
 * 注意：每个插件的 request.js 都直接导入自己的 PLUGIN_CODE，避免多个插件同时运行时冲突
 * 
 * @param {string} url - API 路径（不需要包含 /plugins/{pluginCode} 前缀）
 * @param {object} params - 请求参数
 * @returns {Promise} request 结果
 */
const request = (url, params) => {
  // 运行时：必须使用主应用暴露的 pluginRequest
  if (typeof window !== 'undefined' && window.pluginRequest) {
    // 直接使用导入的 PLUGIN_CODE，避免多个插件同时运行时全局变量冲突
    if (!PLUGIN_CODE) {
      throw new Error('Plugin code is not configured. Please ensure PLUGIN_CODE is set in src/config/plugin.js');
    }
    // 调用主应用的 pluginRequest，自动添加 /plugins/{pluginCode} 前缀
    return window.pluginRequest(url, params, PLUGIN_CODE);
  }
  
  // 开发时：使用插件自己的 request 工具
  return pluginRequestLocal(url, params);
};

export default request;

