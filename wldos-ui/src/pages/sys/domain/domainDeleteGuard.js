/** 与后端 DomainService.assertDeletable 对齐的前端预判（用户数仍以后端为准） */
export function canDeleteDomainRecord(record, { primaryDomainId, templateScenarioMap }) {
  if (record == null || record.id == null) {
    return false;
  }
  const id = String(record.id);
  if (primaryDomainId != null && primaryDomainId !== '' && id === String(primaryDomainId)) {
    return false;
  }
  if (templateScenarioMap?.has?.(id)) {
    return false;
  }
  return true;
}

export function domainDeleteBlockReason(record, { primaryDomainId, templateScenarioMap }) {
  if (record == null || record.id == null) {
    return '无效站点';
  }
  const id = String(record.id);
  if (primaryDomainId != null && primaryDomainId !== '' && id === String(primaryDomainId)) {
    return '平台运营主站不可删除';
  }
  const linked = templateScenarioMap?.get?.(id);
  if (linked?.length) {
    return '租户入驻克隆模板站不可删除';
  }
  return null;
}
