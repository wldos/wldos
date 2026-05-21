import {extend} from 'umi-request';
import {notification} from 'antd';
import {clearAuthority, setAuthority} from "@/utils/authority";
import {getPageQuery, headerFix, guest} from "@/utils/utils";
import {notifyDesktopEmbeddedSession} from '@/utils/desktopEmbeddedBridge';
import {history} from "umi";
import {stringify} from "querystring";

/** 与 {@link com.wldos.framework.support.auth.AccountNotActivatedException#CODE} 一致 */
const ACCOUNT_NOT_ACTIVATED_CODE = 460;

/**
 * 业务 HTTP 状态码默认说明（优先展示服务端 message，缺省用此处文案）。
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
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  [ACCOUNT_NOT_ACTIVATED_CODE]: '请先完成邮箱激活后再使用个人中心等功能。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

/**
 * 对特定业务码覆盖全局通知样式（缺省：{@code error} + 标题「请求异常」）。
 * 新增业务码时只改此处与 {@link codeMessage}，不必改 errorHandler。
 */
const codeNotifyByCode = {
  [ACCOUNT_NOT_ACTIVATED_CODE]: { level: 'warning', message: '账号未激活' },
};

const notifyApiByLevel = {
  warning: (config) => notification.warning(config),
  error: (config) => notification.error(config),
};

/**
 * 业务 code ≠ 200 时的统一提示（422 由调用方处理，不弹全局通知）。
 */
function notifyByBusinessCode(code, serverMessage) {
  if (code === 422) {
    return;
  }
  const description = serverMessage || codeMessage[code] || '未知异常';
  const spec = codeNotifyByCode[code];
  const level = spec && notifyApiByLevel[spec.level] ? spec.level : 'error';
  const title = (spec && spec.message) || '请求异常';
  notifyApiByLevel[level]({ message: title, description });
}

/**
 * 异常处理程序
 */
const errorHandler = (error) => {
  const {response} = error;

  if (response && response.code) {
    const { code, message, status } = response;
    console.log('response====', response, code);
    if (code === 401 || status === 401) {
      clearAuthority();
      const {redirect} = getPageQuery();
      if (typeof window !== 'undefined' && window.location.pathname !== '/user/login' && !redirect) {
        history.replace({
          pathname: '/user/login',
          search: stringify({redirect: window.location.href}),
        });
      }
    } else if (code !== 200) {
      notifyByBusinessCode(code, message);
    }
  } else if (!response) {
    notification.error({
      message: '系统异常',
      description: 'hold on! 您的请求未及响应！',
    });
  }

  return response;
};

/**
 * 配置request请求时的默认参数
 */
const req = extend({errorHandler});
// 拦截请求后响应
req.interceptors.response.use(async (res) => {
  const { headers } = res;
  if (headers.get("token") && headers.get("refresh")) {
    setAuthority(
      {token: {
      accessToken: headers.get('token'),
      refresh: headers.get('refresh')
      },
    });
  }

  // 检查业务状态码（优化性能：只在 JSON 响应时解析）
  // 注意：umi-request 的响应拦截器接收的是原始 Response 对象
  const contentType = headers.get('content-type') || '';
  const isJsonResponse = contentType.includes('application/json');
  
  // 只在 JSON 响应时检查业务状态码，避免不必要的解析
  if (isJsonResponse) {
    try {
      // 克隆响应以便多次读取（避免消耗原始响应流）
      const clonedRes = res.clone();
      const data = await clonedRes.json();
      
      // 检查业务状态码，如果不是 200 则触发错误处理
      if (data && data.code !== 200) {
        // 抛出错误，让 errorHandler 也能处理
        return Promise.reject({
          response: data,
          message: data.message || codeMessage[data.code] || '未知异常'
        });
      }
    } catch (e) {
      // 如果解析失败（非 JSON 响应或解析错误），忽略错误，继续返回原始响应
      // 不打印警告，因为可能是正常的非 JSON 响应（如文件下载）
    }
  }

  notifyDesktopEmbeddedSession();

  return res;
}, (error) => {
  // 错误拦截器：处理 HTTP 错误和业务错误
  return Promise.reject(error);
});

const request = (url, params) => {

  const {method = 'GET'} = params || {};
  const options = {
    method,
    headers: {
      ...headerFix(),
      Accept: '*/*',
    },
  };
  if (method === 'GET') {
    Object.assign(options,
      // method为get，umi-request默认为get
      params, // get方法数据域schema为params，表示请求参数，完整写法是：params: {key: value,...}
    );
  } else {
    Object.assign(options, {
      // method可以为：POST、PATCH、PUT、DELETE
      data: params.data, // post方法数据域schema为data，表示要提交的数据，完整写法是：data: {key: value,...}
    });
  }

  return req(url, options);
};

const postFile = (url, params) => {
  const formData = new FormData();
  formData.append('file', params);
  return request(url, {method: 'post', data: formData});
};
export {postFile, guest};
export default request;
