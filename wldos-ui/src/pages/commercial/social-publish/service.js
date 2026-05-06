import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryMyPublishAccounts() {
  return request(`${prefix}/cms/social-publish/accounts`, {
    method: 'GET',
  });
}

export async function querySupportedPlatforms() {
  return request(`${prefix}/cms/social-publish/platforms`, {
    method: 'GET',
  });
}

/** 融媒宝式桌面助手能力位（bindModeWebSession 等为后端布尔） */
export async function queryMediaAssistantCapability() {
  return request(`${prefix}/cms/social-publish/media-assistant/capability`, {
    method: 'GET',
  });
}

/** 桌面委托：待执行子任务（须登录） */
export async function queryPendingDelegateTasks() {
  return request(`${prefix}/cms/social-publish/delegate/tasks/pending`, {
    method: 'GET',
  });
}

/** 桌面委托：回写子任务结果（须登录） */
export async function completeDelegateTask(taskId, data) {
  return request(`${prefix}/cms/social-publish/delegate/tasks/${taskId}/complete`, {
    method: 'POST',
    data: data || {},
  });
}

export async function bindMyPublishAccount(data) {
  return request(`${prefix}/cms/social-publish/accounts`, {
    method: 'POST',
    data,
  });
}

/** 部分更新账号（备注、分组、extraJson 合并） */
export async function patchMyPublishAccount(id, data) {
  return request(`${prefix}/cms/social-publish/accounts/${id}`, {
    method: 'PATCH',
    data: data || {},
  });
}

export async function removeMyPublishAccount(id) {
  return request(`${prefix}/cms/social-publish/accounts/${id}`, {
    method: 'DELETE',
  });
}

export async function queryMyPublishPreference() {
  return request(`${prefix}/cms/social-publish/preference`, {
    method: 'GET',
  });
}

/** 发布页展示用：预读内容元信息（标题等），与作品编辑 preUpdate 同源 */
export async function queryPubMetaBrief(id) {
  return request(`${prefix}/info-${id}`, {
    method: 'GET',
  });
}

export async function saveMyPublishPreference(data) {
  return request(`${prefix}/cms/social-publish/preference`, {
    method: 'POST',
    data,
  });
}

export async function createPublishJob(data) {
  return request(`${prefix}/cms/social-publish/jobs`, {
    method: 'POST',
    data,
  });
}

export async function publishJobNow(id) {
  return request(`${prefix}/cms/social-publish/jobs/${id}/publish-now`, {
    method: 'POST',
  });
}

export async function retryJobFailed(id) {
  return request(`${prefix}/cms/social-publish/jobs/${id}/retry-failed`, {
    method: 'POST',
  });
}

export async function queryMyPublishJobs(params) {
  return request(`${prefix}/cms/social-publish/jobs/list`, {
    method: 'POST',
    data: params,
  });
}
