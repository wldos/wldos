/** 列表是否展示删除（后端 preDelete + deletable 字段为准） */
export function canDeleteRoleRecord(record) {
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

export function roleDeleteBlockReason(record) {
  if (record == null || record.id == null) {
    return '无效角色';
  }
  if (record.deletable === false) {
    if (record.systemBuiltIn || record.kindTemplate) {
      return '系统内置会员角色或身份角色不可删除';
    }
    return '该角色不可删除';
  }
  if (record.children?.length) {
    return '存在子角色，请先处理子角色';
  }
  return null;
}
