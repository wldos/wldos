/**
 * 门户静态首页模板种类（域资源 component + url）
 */
export const PORTAL_STATIC_HOME_URL_COM = 'home/static';
export const PORTAL_STATIC_HOME_URL_ORG = 'home/static-org';

/** @returns {'com'|'org'|null} */
export function resolvePortalStaticHomeKind(moduleName, url) {
  if (moduleName !== 'component') return null;
  const u = String(url || '').trim();
  if (u === PORTAL_STATIC_HOME_URL_COM) return 'com';
  if (u === PORTAL_STATIC_HOME_URL_ORG) return 'org';
  return null;
}

export function isPortalStaticHomeResource(moduleName, url) {
  return resolvePortalStaticHomeKind(moduleName, url) != null;
}
