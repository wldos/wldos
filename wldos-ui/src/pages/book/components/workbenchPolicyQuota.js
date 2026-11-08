const ACTION_WORK = 'cms.workbench.createWork';
const ACTION_CHAPTER = 'cms.workbench.createChapter';

export function pickPolicyLimit(items, action) {
  if (!Array.isArray(items)) {
    return null;
  }
  const row = items.find((i) => i && i.action === action);
  if (!row || row.max == null) {
    return null;
  }
  return { used: row.used != null ? row.used : 0, max: row.max };
}

export function formatQuotaLine(label, quota) {
  if (!quota || quota.max == null) {
    return null;
  }
  return `${label} ${quota.used}/${quota.max}`;
}

export { ACTION_WORK, ACTION_CHAPTER };
