/**
 * 门户静态首页（home/static）扩展配置解析。
 * 与后台域资源 extraProps / DynSet.extraProps 一致，schemaType=portalStatic。
 * 文档：wldos-ui/docs/CMS官网静态首页可配置方案.md
 * 注入样式须与默认页完全、确定一致，仅允许改 portalStaticInject.less + 模板 HTML；勿动 index.less/index.jsx。类名 portalStatic_{区块key}_*
 */

export const PORTAL_STATIC_SECTION_KEYS = [
  'hero',
  'coreCapabilities',
  'coreValue',
  'useCases',
  'architecture',
  'capabilities',
  'techStack',
  'techPhilosophy',
  'cloudIot',
];

/**
 * 区块 HTML：整段字符串，或字符串数组（多行，按行拼接，便于手写/审阅 JSON）
 * @param {unknown} v
 * @returns {string}
 */
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

/**
 * @param {string} [extraPropsStr] DynSet.extraProps JSON 字符串
 * @returns {Record<string, string>} 有 HTML 的区块 key -> 拼接后的 HTML
 */
/** @returns {Record<string, string>} 各区块 HTML，无则空串 */
export function parsePortalStaticToSectionMap(extraPropsStr) {
  const out = {};
  PORTAL_STATIC_SECTION_KEYS.forEach((k) => {
    out[k] = '';
  });
  if (!extraPropsStr || typeof extraPropsStr !== 'string' || !extraPropsStr.trim()) {
    return out;
  }
  try {
    const o = JSON.parse(extraPropsStr);
    if (!o || typeof o !== 'object') return out;
    if (o.schemaType && o.schemaType !== 'portalStatic') return out;
    const sec = o.sections;
    if (!sec || typeof sec !== 'object') return out;
    PORTAL_STATIC_SECTION_KEYS.forEach((k) => {
      out[k] = normalizeSectionHtml(sec[k]);
    });
  } catch {
    return out;
  }
  return out;
}

/** 是否可视为门户静态模板 JSON（空串视为是） */
export function isPortalStaticExtraProps(str) {
  if (str == null || !String(str).trim()) return true;
  try {
    const o = JSON.parse(str);
    if (!o || typeof o !== 'object') return false;
    return o.schemaType === 'portalStatic';
  } catch {
    return false;
  }
}

/** 由区块 map 生成 extraProps 存库字符串；全空返回 '' */
export function buildPortalStaticExtraProps(sections) {
  const trimmed = {};
  PORTAL_STATIC_SECTION_KEYS.forEach((k) => {
    const v = (sections[k] || '').trim();
    if (v) trimmed[k] = v;
  });
  if (Object.keys(trimmed).length === 0) return '';
  return JSON.stringify({
    schemaType: 'portalStatic',
    schemaVersion: 1,
    sections: trimmed,
  });
}

export function parsePortalStaticSections(extraPropsStr) {
  const map = parsePortalStaticToSectionMap(extraPropsStr);
  const out = {};
  PORTAL_STATIC_SECTION_KEYS.forEach((k) => {
    if (map[k]) out[k] = map[k];
  });
  return out;
}
