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

