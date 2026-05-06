/**
 * 内容发布助手（自媒体多渠道）——书本集成侧纯逻辑。
 * CMS 本站审核流（pubStatus → in_review）见 pages/book/utils/cmsReviewPublish.js，勿混在此处。
 */
import {
  createPublishJob as createSocialPublishJob,
  queryMyPublishJobs as querySocialPublishJobs,
  queryMyPublishAccounts as queryAssistantPublishAccounts,
  querySupportedPlatforms as queryAssistantPlatforms,
  queryMyPublishPreference as queryMySocialPublishPreference,
} from '@/pages/commercial/social-publish/service';

export const DEFAULT_SCHEDULE_POLICY = { minLeadMinutes: 60, maxLeadMinutes: 14 * 24 * 60 };

export const PLATFORM_SCHEDULE_POLICY = {
  WECHAT_MP: { minLeadMinutes: 60, maxLeadMinutes: 14 * 24 * 60 },
  XIAOHONGSHU: { minLeadMinutes: 120, maxLeadMinutes: 7 * 24 * 60 },
  TOUTIAO: { minLeadMinutes: 60, maxLeadMinutes: 7 * 24 * 60 },
  ZHIHU: { minLeadMinutes: 180, maxLeadMinutes: 3 * 24 * 60 },
  BAIJIAHAO: { minLeadMinutes: 60, maxLeadMinutes: 7 * 24 * 60 },
  DOUYIN: { minLeadMinutes: 30, maxLeadMinutes: 3 * 24 * 60 },
  BILIBILI: { minLeadMinutes: 120, maxLeadMinutes: 15 * 24 * 60 },
};

export function buildSchedulePolicyMap(platforms) {
  const map = {};
  (platforms || []).forEach((p) => {
    const code = String(p?.code || '').toUpperCase();
    if (!code) return;
    const minLeadMinutes = Number(p?.scheduleMinLeadMinutes);
    const maxLeadMinutes = Number(p?.scheduleMaxLeadMinutes);
    if (Number.isFinite(minLeadMinutes) && Number.isFinite(maxLeadMinutes) && minLeadMinutes > 0 && maxLeadMinutes > minLeadMinutes) {
      map[code] = { minLeadMinutes, maxLeadMinutes };
    }
  });
  return map;
}

const norm = (v) => String(v == null ? '' : v).trim();

function parseExtraGroups(a) {
  if (!a?.extraJson || typeof a.extraJson !== 'string') return [];
  try {
    const extra = JSON.parse(a.extraJson);
    const fromList = extra?.groupIds || [];
    const fromOne = extra?.groupId ? [extra.groupId] : [];
    return [...fromList, ...fromOne].map((x) => norm(x)).filter(Boolean);
  } catch (e) {
    return [];
  }
}

function accountGroups(a) {
  const directList = a?.groupIds || [];
  const directSingle = [a?.groupId, a?.groupCode, a?.groupName];
  return [...directList, ...directSingle, ...parseExtraGroups(a)].map((x) => norm(x)).filter(Boolean);
}

/**
 * 用户默认发布偏好 + 已选账号解析（供书本「立即发布」与定时规则等使用）。
 */
export async function loadDefaultPublishContext() {
  const [prefRes, accRes, platformsRes] = await Promise.all([
    queryMySocialPublishPreference(),
    queryAssistantPublishAccounts(),
    queryAssistantPlatforms(),
  ]);
  const pref = prefRes?.data || {};
  const assistants = accRes?.data || [];
  const platforms = platformsRes?.data || [];
  const rawIds = pref?.accountIds || [];
  const rawGroupIds = pref?.groupIds || (pref?.groupId ? [pref.groupId] : []);
  const idSet = new Set(rawIds.map((id) => norm(id)).filter(Boolean));
  const groupSet = new Set(rawGroupIds.map((id) => norm(id)).filter(Boolean));
  const selectedAccounts = assistants.filter((a) => {
    const idMatched = idSet.has(norm(a.id));
    const groups = accountGroups(a);
    const groupMatched = groups.some((g) => groupSet.has(g));
    return idMatched || groupMatched;
  });
  const accountIds = selectedAccounts.map((a) => a.id);
  return {
    includeWldosSelf: pref?.includeWldosSelf !== false,
    rawIds,
    rawGroupIds,
    accountIds,
    selectedAccounts,
    schedulePolicyMap: buildSchedulePolicyMap(platforms),
  };
}

export function calcScheduleRule(selectedAccounts, platformPolicies) {
  const selected = selectedAccounts || [];
  if (!selected.length) {
    return {
      minLeadMinutes: DEFAULT_SCHEDULE_POLICY.minLeadMinutes,
      maxLeadMinutes: DEFAULT_SCHEDULE_POLICY.maxLeadMinutes,
      platformCodes: [],
    };
  }
  let minLeadMinutes = 0;
  let maxLeadMinutes = Number.MAX_SAFE_INTEGER;
  const platformCodeSet = new Set();
  selected.forEach((a) => {
    const code = String(a?.platform || '').toUpperCase();
    platformCodeSet.add(code || 'UNKNOWN');
    const policy = (platformPolicies && platformPolicies[code]) || PLATFORM_SCHEDULE_POLICY[code] || DEFAULT_SCHEDULE_POLICY;
    minLeadMinutes = Math.max(minLeadMinutes, Number(policy.minLeadMinutes) || DEFAULT_SCHEDULE_POLICY.minLeadMinutes);
    maxLeadMinutes = Math.min(maxLeadMinutes, Number(policy.maxLeadMinutes) || DEFAULT_SCHEDULE_POLICY.maxLeadMinutes);
  });
  if (!Number.isFinite(maxLeadMinutes) || maxLeadMinutes <= 0) {
    maxLeadMinutes = DEFAULT_SCHEDULE_POLICY.maxLeadMinutes;
  }
  return { minLeadMinutes, maxLeadMinutes, platformCodes: Array.from(platformCodeSet) };
}

/**
 * 按书本来源创建第三方渠道发布任务（助手 API）。
 */
export async function createAssistantPublishJobs({ sourcePayload, contentMode, accountIds, scheduledTime }) {
  if (!accountIds?.length) {
    return null;
  }
  if (!sourcePayload?.sourcePubId) {
    throw new Error('请先保存内容');
  }
  return createSocialPublishJob({
    ...sourcePayload,
    contentMode,
    accountIds,
    ...(scheduledTime ? { scheduledTime } : {}),
  });
}

export async function fetchAssistantJobsForSource(sourcePubId) {
  if (!sourcePubId) {
    return [];
  }
  const res = await querySocialPublishJobs({
    current: 1,
    pageSize: 20,
    condition: { sourcePubId },
  });
  return res?.data?.rows || [];
}
