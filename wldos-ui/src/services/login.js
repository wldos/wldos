import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function accountLogin(params) {
  return request(`${prefix}/login/account`, {
    method: 'POST',
    data: params,
  });
}

export async function logout() {
    return request(`${prefix}/login/logout`, {
        method: 'DELETE'
    });
}

export async function getFakeCaptcha(mobile) {
  return request(`${prefix}/login/captcha?mobile=${mobile}`);
}

export async function queryCaptcha() {
    return request(`${prefix}/authcode/code`);
}

export async function checkCaptcha(params) {
    return request(`${prefix}/authcode/check`, {
        method: 'POST',
        data: params,
    });
}

export async function queryCaptchaMobile(params) {
  return request(`${prefix}/authcode/code4mobile`, {
    params
  });
}

export async function queryCaptchaEmail(params) {
  return request(`${prefix}/authcode/code4email`, {
    params
  });
}

export async function checkEmail(params) {
  return request(`${prefix}/authcode/checkEmail`, {
    method: 'POST',
    data: params,
  });
}

export async function resetPassword(params) {
  return request(`${prefix}/login/reset`, {
    method: 'POST',
    data: params,
  });
}

export async function refreshTokenService() {
  return request(`${prefix}/login/refresh`);
}
export async function fetchEncryptKey() {
  return request(`${prefix}/login/encrypt`);
}


