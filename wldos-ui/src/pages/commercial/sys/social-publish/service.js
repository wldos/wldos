import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function bindPublishAccount(data) {
  return request(`${prefix}/admin/cms/social-publish/accounts`, {
    method: 'POST',
    data,
  });
}

export async function queryAdminPlatforms() {
  return request(`${prefix}/admin/cms/social-publish/platforms`, {
    method: 'GET',
  });
}

export async function queryPlatformConfigSummary() {
  return request(`${prefix}/admin/cms/social-publish/platforms/config-summary`, {
    method: 'GET',
  });
}

export async function savePlatformConfig(data) {
  return request(`${prefix}/admin/cms/social-publish/platforms/config`, {
    method: 'POST',
    data,
  });
}

export async function queryPublishAccounts() {
  return request(`${prefix}/admin/cms/social-publish/accounts/list`, {
    method: 'POST',
    data: {},
  });
}

export async function queryPublishJobs(params) {
  return request(`${prefix}/admin/cms/social-publish/jobs/list`, {
    method: 'POST',
    data: params,
  });
}

export async function retryFailedJob(jobId) {
  return request(`${prefix}/admin/cms/social-publish/jobs/${jobId}/retry-failed`, {
    method: 'POST',
  });
}

export async function wechatOAuthStart(id, params) {
  return request(`${prefix}/admin/cms/social-publish/accounts/${id}/wechat/oauth/start`, {
    method: 'GET',
    params,
  });
}

export async function wechatOAuthCallback(id, params) {
  return request(`${prefix}/admin/cms/social-publish/accounts/${id}/wechat/oauth/callback`, {
    method: 'POST',
    params,
  });
}

export async function uploadWechatThumb(id, file) {
  const formData = new FormData();
  formData.append('file', file);
  return request(`${prefix}/admin/cms/social-publish/accounts/${id}/wechat/material/thumb`, {
    method: 'POST',
    data: formData,
    requestType: 'form',
  });
}
