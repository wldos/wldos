/** 列表是否展示删除（后端 preDelete + deletable 字段为准） */
export function canDeleteOrgRecord(record) {
  if (record == null || record.id == null) {
    return false;
  }
  if (record.deletable === false) {
    return false;
  }
  if (record.children?.length) {
    return false;
  }
  return true;
}

export function orgDeleteBlockReason(record) {
  if (record == null || record.id == null) {
    return '无效组织';
  }
  if (record.deletable === false) {
    if (record.systemBuiltIn || record.kindTemplate) {
      return '系统内置会员分群或身份组织不可删除';
    }
    return '该组织不可删除';
  }
  if (record.children?.length) {
    return '存在下级组织，请先处理子组织';
  }
  return null;
}
