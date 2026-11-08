/**
 * 由域名、协议、端口组装站点访问 URL（与 TenantProvisionService.resolveSiteUrl 规则一致）。
 */
export function resolveProvisionSiteUrl({ siteDomain, siteScheme, sitePort, siteUrl }) {
  if (siteUrl != null && String(siteUrl).trim() !== '') {
    return String(siteUrl).trim();
  }
  const domain = (siteDomain || '').trim().toLowerCase();
  if (!domain) {
    return '';
  }
  const port = sitePort != null && sitePort !== '' ? Number(sitePort) : null;
  let scheme = (siteScheme || 'http').trim().toLowerCase();
  if (port === 443) {
    scheme = 'https';
  } else if (scheme !== 'https') {
    scheme = 'http';
  }
  if (port == null || port === 80) {
    return `${scheme}://${domain}`;
  }
  if (port === 443) {
    return `https://${domain}`;
  }
  return `${scheme}://${domain}:${port}`;
}

export function defaultProvisionSitePort() {
  if (typeof window === 'undefined') {
    return 8088;
  }
  const p = window.location.port;
  if (p) {
    const n = Number(p);
    return Number.isFinite(n) && n > 0 ? n : 8088;
  }
  return window.location.protocol === 'https:' ? 443 : 8088;
}
