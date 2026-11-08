/**
 * 个人设置左侧页签注册表（当前为前端写死）。
 * 数据与基本资料分离：创作收款走 /content/creator/payee，不写入 account 基本资料接口。
 *
 * 后续若要做「活的」展示/隐藏，可在此改为请求 wo_options / 门户配置，
 * 或与 queryAccountCenterTabDefs 同类接口按角色裁剪后合并本表。
 */

/** @typedef {'all' | 'commercial'} SettingsTabFlavor */

/** @type {Array<{ key: string, i18nId: string, flavor: SettingsTabFlavor }>} */
export const ACCOUNT_SETTINGS_TAB_DEFS = [
  { key: 'base', i18nId: 'app.settings.menuMap.basic', flavor: 'all' },
  { key: 'security', i18nId: 'app.settings.menuMap.security', flavor: 'all' },
  { key: 'binding', i18nId: 'app.settings.menuMap.binding', flavor: 'all' },
  { key: 'notification', i18nId: 'app.settings.menuMap.notification', flavor: 'all' },
  { key: 'creatorPayee', i18nId: 'app.settings.menuMap.creatorPayee', flavor: 'commercial' },
  { key: 'creatorWechatMp', i18nId: 'app.settings.menuMap.creatorWechatMp', flavor: 'commercial' },
];

const FLAVOR = process.env.APP_FLAVOR || 'commercial';

/**
 * @param {object} [options]
 * @param {boolean} [options.cmsCreatorCapabilities] 插槽 cms.creator_capabilities 成员，默认 false
 */
export function resolveVisibleSettingsTabs(options = {}) {
  const cmsCreatorCapabilities = options.cmsCreatorCapabilities === true;
  return ACCOUNT_SETTINGS_TAB_DEFS.filter((tab) => {
    if (tab.flavor === 'commercial' && FLAVOR === 'community') {
      return false;
    }
    if ((tab.key === 'creatorPayee' || tab.key === 'creatorWechatMp') && !cmsCreatorCapabilities) {
      return false;
    }
    return true;
  });
}

/** URL 查询参数 tab 与 selectKey 映射（短链 alias） */
export const SETTINGS_TAB_QUERY_ALIASES = {
  'creator-payee': 'creatorPayee',
  'creator-wechat-mp': 'creatorWechatMp',
};

/** 页签 key → URL query.tab */
export function settingsTabToQuery(tabKey) {
  if (!tabKey || tabKey === 'base') {
    return null;
  }
  if (tabKey === 'creatorPayee') {
    return 'creator-payee';
  }
  if (tabKey === 'creatorWechatMp') {
    return 'creator-wechat-mp';
  }
  return tabKey;
}

/** URL query.tab → 页签 key；无 tab 参数时返回 null（勿当作 base，避免覆盖当前选中） */
export function settingsTabFromQuery(raw) {
  if (typeof raw !== 'string' || !raw.trim()) {
    return null;
  }
  const key = SETTINGS_TAB_QUERY_ALIASES[raw] || raw;
  const visible = resolveVisibleSettingsTabs().some((t) => t.key === key);
  return visible ? key : null;
}
