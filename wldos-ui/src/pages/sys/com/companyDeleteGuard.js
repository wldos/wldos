/** 与后端 Constants.TOP_COM_ID 一致：超管公司树虚拟根，不落库 */
export const VIRTUAL_PLATFORM_COM_ID = '0';

export const PLATFORM_OPERATOR_COM_CODE = 'platform-operator';

/** 列表/架构树是否展示「删除」（仍以后端 assertDeletable 为准） */
export function canDeleteCompanyRecord(record) {
  if (record == null || record.id == null) {
    return false;
  }
  if (String(record.id) === VIRTUAL_PLATFORM_COM_ID) {
    return false;
  }
  if (record.comCode === PLATFORM_OPERATOR_COM_CODE) {
    return false;
  }
  if (record.children?.length) {
    return false;
  }
  return true;
}

export function companyDeleteBlockReason(record) {
  if (record == null || record.id == null) {
    return '无效公司';
  }
  if (String(record.id) === VIRTUAL_PLATFORM_COM_ID) {
    return '「平台」为虚拟树顶，仅用于挂载租户，不可删除';
  }
  if (record.comCode === PLATFORM_OPERATOR_COM_CODE) {
    return '平台官方运营主体不可删除，请在「平台运营」维护';
  }
  if (record.children?.length) {
    return '存在子公司，请先处理子公司';
  }
  return null;
}
