import { useCallback, useEffect, useMemo, useState } from 'react';
import { fetchPlatformOperatorContext } from '@/pages/sys/platform-operator/service';
import { getComSelectOption, getSessionComSelectOption } from '@/pages/sys/com/service';
import { queryPage as queryDomainPage } from '@/pages/sys/domain/service';
import { querySiteSeo } from '@/services/user';
import { formatDomainOptionLabel, resolveLoginDomainId } from '@/utils/domain';
import {
  adminComToolbarOptions,
  buildComLabelEnum,
  buildDomainLabelMap,
  filterDomainRowsForToolbarScope,
  isAdminListScopeReady,
  isPlatformScopeComId,
  isToolbarScopeComId,
  isValidScopeComId,
  resolveAdminInitialTargetComId,
} from '@/utils/orgCreateUserDomain';

/**
 * 超管代管 / 租户管理员会话下的默认租户（不含平台 0）。
 */
export function resolveSessionDefaultComId(isAdmin, currentUser) {
  if (isAdmin) {
    return undefined;
  }
  const operatorComId = currentUser?.operatorComId;
  const tenantId = currentUser?.tenantId;
  if (operatorComId != null && String(operatorComId) !== '0') {
    return String(operatorComId);
  }
  if (tenantId != null && String(tenantId) !== '0') {
    return String(tenantId);
  }
  return undefined;
}

/**
 * 列表工具栏：目标租户 com（0=平台 / &gt;0=具体租户）+ 目标站点 domainId。
 *
 * @param adminDefaultAxis `platform` 默认平台运营主体 com；`tenant-required` 须先选租户（插槽/主体/会员分群等页）
 * @param allowPlatformScopeInToolbar 仅超管且为 true 时下拉含 comId=0（角色管理等）；默认 false 防越权
 * @param toolbarEnabled 为 false 时不加载工具栏域列表（如组织内嵌用户清单）
 * @param platformOperatorLock 平台运营菜单固定 scope：{ comId, domainId, comName, siteLabel }
 * @param skipSessionDomainDefault 为 true 时不按当前登录站点预选 domainId（审计列表等）
 * @param domainToolbarEnabled 为 false 时站点管不展示目标站点下拉（多站须切站登录）
 *
 * 返回值 `showDomainSelectInToolbar`：当前管理范围内站点数大于 1，列表「站点」列与工具栏「目标站点」共用。
 */
export function useOperationalScope({
  isAdmin,
  currentUser,
  intl,
  adminDefaultAxis = 'platform',
  allowPlatformScopeInToolbar = false,
  toolbarEnabled = true,
  platformOperatorLock = null,
  skipSessionDomainDefault = false,
  domainToolbarEnabled = true,
}) {
  const defaultComId = useMemo(
    () => resolveSessionDefaultComId(isAdmin, currentUser),
    [isAdmin, currentUser],
  );
  const [targetComId, setTargetComId] = useState(() => (
    resolveAdminInitialTargetComId(isAdmin, defaultComId, adminDefaultAxis)
  ));
  const [targetDomainId, setTargetDomainId] = useState(undefined);
  const [domainOptions, setDomainOptions] = useState([]);
  const [allDomainRows, setAllDomainRows] = useState([]);
  const [domainLabelMap, setDomainLabelMap] = useState({});
  const [comLabelEnum, setComLabelEnum] = useState({});
  const [showDomainSelect, setShowDomainSelect] = useState(false);
  const [domainToolbarReady, setDomainToolbarReady] = useState(false);
  const [listScopeReady, setListScopeReady] = useState(false);
  const [comListArray, setComListArray] = useState([]);

  const effectiveComId = useMemo(() => {
    if (platformOperatorLock?.comId) {
      return String(platformOperatorLock.comId);
    }
    if (isValidScopeComId(defaultComId)) {
      return String(defaultComId);
    }
    if (isToolbarScopeComId(targetComId)) {
      return String(targetComId);
    }
    return undefined;
  }, [defaultComId, targetComId, platformOperatorLock]);

  const tenantComOptions = useMemo(
    () => (comListArray || []).filter((item) => isValidScopeComId(item?.value)),
    [comListArray],
  );

  const scopeReadyForList = useMemo(
    () => isAdminListScopeReady(isAdmin, effectiveComId, adminDefaultAxis),
    [isAdmin, effectiveComId, adminDefaultAxis],
  );

  const applyDomainSelection = useCallback((options, preferredDomainId) => {
    if (!options?.length) {
      setDomainOptions([]);
      setShowDomainSelect(false);
      setTargetDomainId(undefined);
      setDomainToolbarReady(true);
      return;
    }
    setDomainOptions(options);
    if (!domainToolbarEnabled) {
      setShowDomainSelect(false);
      let sessionPreferred = preferredDomainId && options.some((item) => item.value === preferredDomainId)
        ? preferredDomainId
        : undefined;
      if (!sessionPreferred && options.length === 1) {
        sessionPreferred = options[0].value;
      }
      if (sessionPreferred) {
        setTargetDomainId(sessionPreferred);
      } else {
        setTargetDomainId(undefined);
      }
      setDomainToolbarReady(true);
      return;
    }
    const showSiteInToolbar = adminDefaultAxis === 'audit-unscoped'
      ? options.length > 0
      : options.length > 1;
    setShowDomainSelect(showSiteInToolbar);
    let preferred = preferredDomainId && options.some((item) => item.value === preferredDomainId)
      ? preferredDomainId
      : undefined;
    if (!preferred && options.length === 1 && adminDefaultAxis !== 'audit-unscoped') {
      preferred = options[0].value;
    }
    if (!preferred) {
      setTargetDomainId(undefined);
      setDomainToolbarReady(true);
      return;
    }
    setTargetDomainId(preferred);
    setDomainToolbarReady(true);
  }, [adminDefaultAxis, domainToolbarEnabled]);

  const resetScopeSelection = useCallback(() => {
    setListScopeReady(false);
    setDomainToolbarReady(false);
    setShowDomainSelect(false);
    setTargetDomainId(undefined);
  }, []);

  const onTargetComChange = useCallback((value) => {
    resetScopeSelection();
    setTargetComId(value || undefined);
  }, [resetScopeSelection]);

  useEffect(() => {
    if (platformOperatorLock?.comId && platformOperatorLock?.domainId) {
      const comOpt = [{
        label: platformOperatorLock.comName || platformOperatorLock.comId,
        value: String(platformOperatorLock.comId),
      }];
      setComListArray(comOpt);
      setComLabelEnum(buildComLabelEnum(comOpt, intl.formatMessage));
      setTargetComId(String(platformOperatorLock.comId));
      setTargetDomainId(String(platformOperatorLock.domainId));
      const domainOpt = [{
        label: platformOperatorLock.siteLabel || platformOperatorLock.domainId,
        value: String(platformOperatorLock.domainId),
      }];
      setDomainOptions(domainOpt);
      setShowDomainSelect(domainOpt.length > 1);
      setDomainToolbarReady(true);
      setListScopeReady(true);
      return undefined;
    }
    if (!toolbarEnabled) {
      setListScopeReady(true);
      setDomainToolbarReady(true);
      return undefined;
    }
    let cancelled = false;
    (async () => {
      setListScopeReady(false);
      setDomainToolbarReady(false);
      const comTask = isAdmin
        ? getComSelectOption().then((res) => res?.data ?? [])
        : getSessionComSelectOption(defaultComId);
      const operatorTask = (isAdmin && adminDefaultAxis === 'platform')
        ? fetchPlatformOperatorContext().catch(() => null)
        : Promise.resolve(null);
      const skipOperatorDefault = adminDefaultAxis === 'audit-unscoped';
      const tasks = [
        comTask,
        queryDomainPage({ current: 1, pageSize: 500, sorter: { displayOrder: 'ascend' } }),
        querySiteSeo(),
        operatorTask,
      ];
      const results = await Promise.all(tasks);
      if (cancelled) {
        return;
      }
      const comArr = results[0] ?? [];
      const domainRes = results[1];
      const siteRes = results[2];
      const operatorRes = results[3];
      const operatorCtx = (operatorRes?.code === 200 && operatorRes?.data?.ready)
        ? operatorRes.data
        : null;
      setComLabelEnum(buildComLabelEnum(comArr, intl.formatMessage));
      if (isAdmin) {
        setComListArray(adminComToolbarOptions(comArr, intl.formatMessage, {
          allowPlatformScope: !!allowPlatformScopeInToolbar,
        }));
      }
      let scopeComId = effectiveComId;
      const userPickedScope = isToolbarScopeComId(targetComId);
      if (!skipOperatorDefault && operatorCtx && adminDefaultAxis === 'platform' && !userPickedScope) {
        scopeComId = String(operatorCtx.operatorComId);
        if (!cancelled) {
          setTargetComId(scopeComId);
          if (operatorCtx.primaryDomainId != null && operatorCtx.primaryDomainId !== '') {
            setTargetDomainId(String(operatorCtx.primaryDomainId));
          }
        }
      } else if (userPickedScope) {
        scopeComId = String(targetComId);
        if (isPlatformScopeComId(scopeComId) && !cancelled) {
          setTargetDomainId(undefined);
        }
      }
      const siteInfo = siteRes?.data || {};
      const domainRowsFull = domainRes?.data?.rows ?? [];
      setAllDomainRows(domainRowsFull);
      setDomainLabelMap(buildDomainLabelMap(domainRowsFull, intl.formatMessage, formatDomainOptionLabel));
      let domainRows = filterDomainRowsForToolbarScope(domainRowsFull, scopeComId);
      if (isAdmin && !scopeComId) {
        domainRows = [];
      }
      const platformAxis = isPlatformScopeComId(scopeComId);
      const preferredFromOperator = !skipOperatorDefault && !platformAxis && operatorCtx?.primaryDomainId != null
        ? String(operatorCtx.primaryDomainId)
        : undefined;
      const skipAutoDomain = skipOperatorDefault && isAdmin;
      const currentDomainId = platformAxis
        ? undefined
        : (preferredFromOperator
          || (!skipAutoDomain && !skipSessionDomainDefault ? resolveLoginDomainId(domainRows, siteInfo) : undefined));
      const options = domainRows.map((item) => ({
        label: formatDomainOptionLabel(item, currentDomainId, intl.formatMessage),
        value: String(item.id),
      }));
      if (platformAxis && allowPlatformScopeInToolbar && domainToolbarEnabled) {
        setDomainOptions(options);
        setShowDomainSelect(options.length > 0);
        setTargetDomainId(undefined);
        setDomainToolbarReady(true);
      } else {
        applyDomainSelection(options, currentDomainId);
      }
      if (!cancelled) {
        setListScopeReady(scopeReadyForList);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [intl, isAdmin, defaultComId, effectiveComId, targetComId, applyDomainSelection, adminDefaultAxis, allowPlatformScopeInToolbar, scopeReadyForList, toolbarEnabled, platformOperatorLock, skipSessionDomainDefault, domainToolbarEnabled]);

  useEffect(() => {
    if (!toolbarEnabled) {
      return;
    }
    if (scopeReadyForList && domainToolbarReady) {
      setListScopeReady(true);
    } else if (isAdmin && adminDefaultAxis === 'tenant-required' && !isValidScopeComId(effectiveComId)) {
      setListScopeReady(false);
    }
  }, [scopeReadyForList, domainToolbarReady, isAdmin, adminDefaultAxis, effectiveComId, toolbarEnabled]);

  return {
    defaultComId,
    targetComId,
    setTargetComId,
    onTargetComChange,
    targetDomainId,
    setTargetDomainId,
    effectiveComId,
    comListArray,
    comLabelEnum,
    tenantComOptions,
    domainOptions,
    allDomainRows,
    domainLabelMap,
    showDomainSelect,
    showDomainSelectInToolbar: domainToolbarEnabled && domainToolbarReady
      && (showDomainSelect || (allowPlatformScopeInToolbar && isPlatformScopeComId(effectiveComId))),
    listScopeReady,
    domainToolbarReady,
    resetScopeSelection,
    adminDefaultAxis,
  };
}

export default useOperationalScope;
