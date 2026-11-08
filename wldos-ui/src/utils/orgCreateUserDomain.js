/** 工具栏「平台」轴，与站点管理、后端 Constants.TOP_COM_ID 一致 */
export const PLATFORM_SCOPE_COM_ID = '0';

/** 租户管理员会话绑定的公司 id（operatorComId 优先，与后端 getWorkComId 一致） */
export function resolveTenantScopeComId(currentUser) {
  if (!currentUser) {
    return undefined;
  }
  const operatorComId = currentUser.operatorComId;
  const tenantId = currentUser.tenantId;
  if (operatorComId != null && String(operatorComId) !== '0') {
    return String(operatorComId);
  }
  if (tenantId != null && String(tenantId) !== '0') {
    return String(tenantId);
  }
  return undefined;
}

/** 平台超管新建/删公司（租户）；租户管理员仅维护本租户配置与下级组织/体系 */
export function canProvisionCompany(isAdmin, isTenantAdmin) {
  return !!isAdmin && !isTenantAdmin;
}

/**
 * 组织节点新增用户时的目标站点解析（与后端 resolveRegisterDomainId 优先级对齐的前端侧）。
 */
export function isPlatformScopeComId(comId) {
  return comId != null && comId !== '' && String(comId) === PLATFORM_SCOPE_COM_ID;
}

export function isValidScopeComId(comId) {
  return comId != null && comId !== '' && String(comId) !== '0';
}

/** 工具栏已选租户轴（含平台 0 或具体租户 com>0） */
export function isToolbarScopeComId(comId) {
  return isPlatformScopeComId(comId) || isValidScopeComId(comId);
}

export function buildComLabelEnum(comOptions, formatMessage) {
  const enumMap = {
    [PLATFORM_SCOPE_COM_ID]: {
      text: formatMessage({ id: 'sys.scope.platform', defaultMessage: '平台' }),
    },
  };
  (comOptions || []).forEach((item) => {
    if (item?.value != null && item.value !== '') {
      enumMap[String(item.value)] = { text: item.label || String(item.value) };
    }
  });
  return enumMap;
}

export function comLabelText(comLabelEnum, comId) {
  if (comId == null || String(comId) === '0') {
    return undefined;
  }
  return comLabelEnum[String(comId)]?.text;
}

export function buildDomainLabelMap(allDomainRows, formatMessage, formatDomainOptionLabel) {
  const map = {};
  (allDomainRows || []).forEach((item) => {
    if (item?.id == null) {
      return;
    }
    const label = formatDomainOptionLabel
      ? formatDomainOptionLabel(item, null, formatMessage)
      : (item.siteName || item.siteDomain || String(item.id));
    map[String(item.id)] = label || String(item.id);
  });
  return map;
}

/**
 * 超管目标租户下拉。默认仅 com&gt;0，避免非超管或会员分群等页误切平台轴越权/串数据。
 * {@code allowPlatformScope} 为 true 时首项为 comId=0（I 类身份、共享模板）；站点管理仍用原始 getComSelectOption。
 */
export function adminComToolbarOptions(comOptions, formatMessage, { allowPlatformScope = false } = {}) {
  const tenants = (comOptions || []).filter((item) => isValidScopeComId(item?.value));
  if (!allowPlatformScope) {
    return tenants;
  }
  const platformLabel = formatMessage
    ? formatMessage({ id: 'sys.scope.platform', defaultMessage: '平台' })
    : '平台';
  return [{ value: PLATFORM_SCOPE_COM_ID, label: platformLabel }, ...tenants];
}

/**
 * 超管工具栏初始 com：platform=由运营主体 context 异步注入；tenant-required=须手动选租户。
 */
export function resolveAdminInitialTargetComId(isAdmin, sessionDefaultComId, adminDefaultAxis = 'platform') {
  if (!isAdmin || sessionDefaultComId) {
    return undefined;
  }
  return undefined;
}

export function isAdminListScopeReady(isAdmin, effectiveComId, adminDefaultAxis = 'platform') {
  if (!isAdmin) {
    return true;
  }
  if (adminDefaultAxis === 'tenant-required') {
    return isValidScopeComId(effectiveComId);
  }
  if (adminDefaultAxis === 'platform') {
    return isToolbarScopeComId(effectiveComId);
  }
  if (adminDefaultAxis === 'audit-unscoped') {
    return true;
  }
  return isToolbarScopeComId(effectiveComId);
}

/** 管理范围内是否多站点（与 useOperationalScope.showDomainSelectInToolbar 一致，用于隐藏列表「站点」列） */
export function isMultiSiteOperationalScope(showDomainSelectInToolbar) {
  return !!showDomainSelectInToolbar;
}

export function filterDomainRowsForToolbarScope(allDomainRows, effectiveComId) {
  if (!Array.isArray(allDomainRows) || !effectiveComId) {
    return [];
  }
  /** 平台轴：可选目标站点（全站列表），不强制绑定 */
  if (isPlatformScopeComId(effectiveComId)) {
    return allDomainRows;
  }
  if (isValidScopeComId(effectiveComId)) {
    return allDomainRows.filter((item) => String(item.comId) === String(effectiveComId));
  }
  return [];
}

export function resolveCreateUserDomainId(orgRecord, { targetDomainId, domainOptions = [] } = {}) {
  const orgDomain = orgRecord?.domainId;
  if (orgDomain != null && orgDomain !== '' && Number(orgDomain) > 0) {
    return String(orgDomain);
  }
  if (targetDomainId != null && targetDomainId !== '') {
    return String(targetDomainId);
  }
  if (domainOptions.length === 1 && domainOptions[0]?.value != null) {
    return String(domainOptions[0].value);
  }
  return undefined;
}

export function isOrgKindTemplateScope(orgRecord) {
  return orgRecord?.kindTemplate === true
    || (orgRecord?.systemBuiltIn === true
      && (orgRecord?.domainId == null || orgRecord.domainId === '' || Number(orgRecord.domainId) <= 0));
}

/** 会员分群模板：弹窗内选目标租户/站点，不要求先选工具栏站点 */
export function canPickMemberScopeInModal(orgRecord) {
  return isOrgKindTemplateScope(orgRecord);
}

export function needsTargetSiteSelection(orgRecord, { showDomainSelect, targetDomainId, domainOptions = [] } = {}) {
  const isKindTemplate = isOrgKindTemplateScope(orgRecord);
  if (isKindTemplate && !resolveCreateUserDomainId(orgRecord, { targetDomainId, domainOptions })) {
    return true;
  }
  if (resolveCreateUserDomainId(orgRecord, { targetDomainId, domainOptions })) {
    return false;
  }
  return showDomainSelect || domainOptions.length > 1;
}

export function filterDomainOptionsForCom(allDomainRows, comId) {
  if (!Array.isArray(allDomainRows) || !isValidScopeComId(comId)) {
    return [];
  }
  return allDomainRows
    .filter((item) => String(item.comId) === String(comId))
    .map((item) => ({
      label: item.siteName || item.siteDomain || String(item.id),
      value: String(item.id),
    }));
}

/**
 * 新建/提交时继承列表工具栏的目标租户与站点。
 * comId：表单为空或平台 0 时用工具栏所选租户（不含 0 时仍表示平台视图）。
 * domainId：表单未指定站点时始终继承工具栏目标站点（与表单是否已填 comId 无关）。
 */
/** 超管：选「平台」仅 comId=0；选具体租户须再选目标站点（与角色管理工具栏一致）。 */
export function isTermAdminListScopeReady(isAdmin, listScopeReady, effectiveComId, targetDomainId) {
  if (!listScopeReady) {
    return false;
  }
  if (!isAdmin) {
    return true;
  }
  if (isPlatformScopeComId(effectiveComId)) {
    return true;
  }
  return isValidScopeComId(effectiveComId) && targetDomainId != null && targetDomainId !== '';
}

export function buildTermListQueryScope({ isAdmin, effectiveComId, targetDomainId }) {
  const domainPart = (targetDomainId != null && targetDomainId !== '')
    ? { domainId: targetDomainId }
    : {};
  if (!isAdmin) {
    return domainPart;
  }
  if (isPlatformScopeComId(effectiveComId)) {
    return { comId: PLATFORM_SCOPE_COM_ID };
  }
  if (!isValidScopeComId(effectiveComId) || targetDomainId == null || targetDomainId === '') {
    return null;
  }
  return { comId: effectiveComId, domainId: targetDomainId };
}

export function mergeToolbarWriteScope(fields, { effectiveComId, targetDomainId } = {}) {
  if (!fields || typeof fields !== 'object') {
    return fields;
  }
  const rowComId = fields.comId;
  const useToolbarCom = rowComId == null || rowComId === '' || String(rowComId) === '0';
  const next = { ...fields };
  if (useToolbarCom && isPlatformScopeComId(effectiveComId)) {
    next.comId = PLATFORM_SCOPE_COM_ID;
    next.domainId = undefined;
  } else if (useToolbarCom && isValidScopeComId(effectiveComId)) {
    next.comId = effectiveComId;
  }
  const rowDomain = next.domainId;
  const useToolbarDomain = rowDomain == null || rowDomain === '' || Number(rowDomain) <= 0;
  if (!isPlatformScopeComId(effectiveComId)
    && useToolbarDomain && targetDomainId != null && targetDomainId !== '') {
    next.domainId = targetDomainId;
  }
  return next;
}

