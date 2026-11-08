/**
 * 站点（域名）工具：定位当前登录站点、格式化站点下拉选项。
 * 主体管理、插槽绑定等带「目标站点」级联的页面共用。
 */

const normalizeHost = (host) => (host || '').trim().toLowerCase().replace(/^www\./, '');

/**
 * 从站点列表中解析当前登录站点 id：优先站点信息 id，其次按域名（主域/二级域/cname）匹配，最后按站点名匹配。
 *
 * @param {Array} domainRows 站点列表
 * @param {Object} siteInfo 当前站点信息（querySiteSeo 返回）
 * @returns {string|undefined} 当前站点 id（字符串），解析不到返回 undefined
 */
export const resolveLoginDomainId = (domainRows, siteInfo) => {
  if (!domainRows?.length) {
    return undefined;
  }
  const siteId = siteInfo?.id != null && siteInfo.id !== '' ? String(siteInfo.id) : undefined;
  if (siteId && domainRows.some((item) => String(item.id) === siteId)) {
    return siteId;
  }
  const hostCandidates = new Set();
  if (siteInfo?.siteDomain) {
    hostCandidates.add(normalizeHost(siteInfo.siteDomain));
  }
  if (typeof window !== 'undefined' && window.location?.hostname) {
    hostCandidates.add(normalizeHost(window.location.hostname));
  }
  for (const host of hostCandidates) {
    if (!host) {
      continue;
    }
    const byHost = domainRows.find((item) => {
      if (normalizeHost(item.siteDomain) === host || normalizeHost(item.secondDomain) === host) {
        return true;
      }
      const cnames = (item.cnameDomain || '')
        .split(',')
        .map((part) => normalizeHost(part))
        .filter(Boolean);
      return cnames.includes(host);
    });
    if (byHost?.id != null) {
      return String(byHost.id);
    }
  }
  const siteName = siteInfo?.siteName?.trim?.();
  if (siteName) {
    const byName = domainRows.find((item) => item.siteName === siteName);
    if (byName?.id != null) {
      return String(byName.id);
    }
  }
  return undefined;
};

/**
 * 站点下拉选项文案：当前站标注「（当前站）」，其余带域名后缀，无域名仅站点名。
 *
 * @param {Object} item 站点行
 * @param {string} currentDomainId 当前站点 id
 * @param {Function} formatMessage intl.formatMessage
 * @returns {string} 选项文案
 */
export const formatDomainOptionLabel = (item, currentDomainId, formatMessage) => {
  const baseName = item.siteName || item.siteDomain || String(item.id);
  const domainTag = (item.siteDomain || item.secondDomain || '').trim();
  const isCurrent = currentDomainId && String(item.id) === String(currentDomainId);
  if (isCurrent) {
    return formatMessage(
      { id: 'sys.role.domainOption.current', defaultMessage: '{name}（当前站）' },
      { name: baseName },
    );
  }
  if (domainTag) {
    return formatMessage(
      { id: 'sys.role.domainOption.withDomain', defaultMessage: '{name}（{domain}）' },
      { name: baseName, domain: domainTag },
    );
  }
  return baseName;
};
