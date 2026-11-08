import request from '@/utils/request';
import config from '@/utils/config';
import { getLocale } from 'umi';

const { prefix } = config;

const silentOpts = { skipErrorHandler: true };

/** 表单校验用：不触发全局 notification */
export async function checkEmailForForget(data) {
  return request(`${prefix}/authcode/checkEmail`, {
    method: 'POST',
    data,
    ...silentOpts,
  });
}

export async function checkCaptchaForForget(data) {
  return request(`${prefix}/authcode/check`, {
    method: 'POST',
    data,
    ...silentOpts,
  });
}

export async function fetchForgetResetProfile() {
  return request(`${prefix}/authcode/forgetResetProfile`, {
    ...silentOpts,
  });
}

export async function fetchClickChallengeForForget() {
  return request(`${prefix}/authcode/clickChallenge`, {
    params: { locale: getLocale() },
    ...silentOpts,
  });
}

export async function verifyClickChallengeForForget(data) {
  return request(`${prefix}/authcode/clickVerify`, {
    method: 'POST',
    data,
    ...silentOpts,
  });
}

export async function forgetEmailAdmissionForForget(data) {
  return request(`${prefix}/authcode/forgetEmailAdmission`, {
    method: 'POST',
    data,
    ...silentOpts,
  });
}

export async function queryCaptchaEmailForForget(params) {
  return request(`${prefix}/authcode/code4email`, {
    params: {
      scene: 'forget',
      ...params,
    },
    ...silentOpts,
  });
}

export async function resetPasswordForForget(data) {
  return request(`${prefix}/login/reset`, {
    method: 'POST',
    data,
  });
}
