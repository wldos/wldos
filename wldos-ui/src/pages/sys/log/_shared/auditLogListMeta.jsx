import { useCallback, useEffect, useMemo, useState } from 'react';
import { useIntl } from 'umi';
import { getComSelectOption } from '@/pages/sys/com/service';
import { queryPage as queryDomainPage } from '@/pages/sys/domain/service';
import { formatDomainOptionLabel } from '@/utils/domain';
import {
  PLATFORM_SCOPE_COM_ID,
  buildDomainLabelMap,
  isValidScopeComId,
  resolveTenantScopeComId,
} from '@/utils/orgCreateUserDomain';

function domainRowsForComFilter(allRows, comId) {
  if (!Array.isArray(allRows) || !allRows.length) {
    return [];
  }
  if (comId == null || comId === '') {
    return allRows;
  }
  if (String(comId) === PLATFORM_SCOPE_COM_ID) {
    return allRows;
  }
  if (isValidScopeComId(comId)) {
    return allRows.filter((item) => String(item.comId) === String(comId));
  }
  return [];
}

function toDomainSelectOptions(rows, intl) {
  return (rows || []).map((item) => ({
    label: formatDomainOptionLabel(item, null, intl.formatMessage),
    value: String(item.id),
  }));
}

/** 三日志列表：公司/站点下拉与表格站点列 label（不含查询 scope 状态） */
export function useAuditLogListMeta({ isAdmin, isTenantAdmin, currentUser }) {
  const intl = useIntl();
  const tenantComId = useMemo(
    () => resolveTenantScopeComId(currentUser),
    [currentUser],
  );
  const [comSelectOptions, setComSelectOptions] = useState([]);
  const [allDomainRows, setAllDomainRows] = useState([]);
  const [domainLabelMap, setDomainLabelMap] = useState({});
  const [optionsReady, setOptionsReady] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setOptionsReady(false);
      const tasks = [queryDomainPage({ current: 1, pageSize: 500, sorter: { displayOrder: 'ascend' } })];
      if (isAdmin) {
        tasks.unshift(getComSelectOption().then((res) => res?.data ?? []));
      }
      const results = await Promise.all(tasks);
      if (cancelled) {
        return;
      }
      let domainRows;
      if (isAdmin) {
        const comArr = results[0] ?? [];
        domainRows = results[1]?.data?.rows ?? [];
        const tenants = (comArr || [])
          .filter((item) => isValidScopeComId(item?.value))
          .map((item) => ({ label: item.label, value: String(item.value) }));
        const platformLabel = intl.formatMessage({ id: 'sys.scope.platform', defaultMessage: '平台' });
        setComSelectOptions([{ value: PLATFORM_SCOPE_COM_ID, label: platformLabel }, ...tenants]);
      } else {
        domainRows = results[0]?.data?.rows ?? [];
      }
      setAllDomainRows(domainRows);
      setDomainLabelMap(buildDomainLabelMap(domainRows, intl.formatMessage, formatDomainOptionLabel));
      setOptionsReady(true);
    })();
    return () => {
      cancelled = true;
    };
  }, [isAdmin, intl]);

  const domainOptionsForCom = useCallback((comId) => {
    const filterCom = isAdmin ? comId : tenantComId;
    return toDomainSelectOptions(domainRowsForComFilter(allDomainRows, filterCom), intl);
  }, [allDomainRows, intl, isAdmin, tenantComId]);

  const showDomainColumn = useMemo(() => {
    const rows = isAdmin
      ? allDomainRows
      : domainRowsForComFilter(allDomainRows, tenantComId);
    return rows.length > 1;
  }, [allDomainRows, isAdmin, tenantComId]);

  return {
    isAdmin,
    isTenantAdmin,
    comSelectOptions,
    domainOptionsForCom,
    domainLabelMap,
    showDomainColumn,
    optionsReady,
  };
}
