import { getAuthority } from '@/utils/authority';

/**
 * 解析当前端有效操作权限码列表。
 * 管理端优先 adminAuthorities；门户用 currentAuthority / localStorage。
 */
export function resolveEffectiveAuthorities({
  adminAuthorities,
  sessionFlags,
  portalAuthorities,
  isAdminRoute,
}) {
  if (sessionFlags?.isAdmin) {
    return ['*'];
  }
  if (isAdminRoute && Array.isArray(adminAuthorities) && adminAuthorities.length > 0) {
    return adminAuthorities;
  }
  const portal = portalAuthorities || getAuthority();
  if (Array.isArray(portal)) {
    return portal;
  }
  return portal ? [portal] : [];
}

export function hasAuthority(code, authorities, isAdmin) {
  if (!code) {
    return true;
  }
  if (isAdmin || (authorities && authorities.includes('*'))) {
    return true;
  }
  const list = authorities || [];
  if (list.length === 0) {
    return false;
  }
  if (Array.isArray(code)) {
    return code.some((c) => list.includes(c));
  }
  return list.includes(code);
}

/** 供 connect 页面一次性构造 { authorities, hasAuthority, isAdmin } */
export function buildAuthorityHelper(state, pathname) {
  const isAdminRoute = (pathname || '').startsWith('/admin');
  const authorities = resolveEffectiveAuthorities({
    adminAuthorities: state.adminAuthorities,
    sessionFlags: state.sessionFlags,
    portalAuthorities: state.currentUser?.currentAuthority,
    isAdminRoute,
  });
  const isAdmin = !!state.sessionFlags?.isAdmin || authorities.includes('*');
  const isTenantAdmin = !!state.sessionFlags?.tenantAdmin;
  return {
    authorities,
    isAdmin,
    isTenantAdmin,
    hasAuthority: (code) => hasAuthority(code, authorities, isAdmin),
  };
}

export default { resolveEffectiveAuthorities, hasAuthority, buildAuthorityHelper };
