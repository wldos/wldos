/**
 * 开源门户静态首页（home/static-org）扩展配置。
 * schemaType=portalStaticOrg
 * 文档：docs/wldos官网软件产品商业化闭环/wldos.com与wldos.org双站定位与独立运营规划.md
 */

export const PORTAL_STATIC_ORG_SECTION_KEYS = [
  'hero',
  'pillars',
  'getStarted',
  'contribute',
  'techStack',
];

export function normalizeSectionHtml(v) {
  if (v == null) return '';
  if (Array.isArray(v)) {
    return v
      .map((line) => (line == null ? '' : String(line)))
      .join('\n')
      .trim();
  }
  return String(v).trim();
}

export function parsePortalStaticOrgToSectionMap(extraPropsStr) {
  const out = {};
  PORTAL_STATIC_ORG_SECTION_KEYS.forEach((k) => {
    out[k] = '';
  });
  if (!extraPropsStr || typeof extraPropsStr !== 'string' || !extraPropsStr.trim()) {
    return out;
  }
  try {
    const o = JSON.parse(extraPropsStr);
    if (!o || typeof o !== 'object') return out;
    if (o.schemaType && o.schemaType !== 'portalStaticOrg') return out;
    const sec = o.sections;
    if (!sec || typeof sec !== 'object') return out;
    PORTAL_STATIC_ORG_SECTION_KEYS.forEach((k) => {
      out[k] = normalizeSectionHtml(sec[k]);
    });
  } catch {
    return out;
  }
  return out;
}

export function isPortalStaticOrgExtraProps(str) {
  if (str == null || !String(str).trim()) return true;
  try {
    const o = JSON.parse(str);
    if (!o || typeof o !== 'object') return false;
    return o.schemaType === 'portalStaticOrg';
  } catch {
    return false;
  }
}

export function buildPortalStaticOrgExtraProps(sections) {
  const trimmed = {};
  PORTAL_STATIC_ORG_SECTION_KEYS.forEach((k) => {
    const v = (sections[k] || '').trim();
    if (v) trimmed[k] = v;
  });
  if (Object.keys(trimmed).length === 0) return '';
  return JSON.stringify({
    schemaType: 'portalStaticOrg',
    schemaVersion: 1,
    sections: trimmed,
  });
}

export function parsePortalStaticOrgSections(extraPropsStr) {
  const map = parsePortalStaticOrgToSectionMap(extraPropsStr);
  const out = {};
  PORTAL_STATIC_ORG_SECTION_KEYS.forEach((k) => {
    if (map[k]) out[k] = map[k];
  });
  return out;
}
