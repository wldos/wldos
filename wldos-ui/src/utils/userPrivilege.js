/** 是否可配置作品「发现范围」（管理员或可信者用户组）。 */
export function canManageVisibilityScope(userState) {
  const flags = userState?.sessionFlags;
  return !!(flags?.isAdmin || flags?.canTrust);
}
