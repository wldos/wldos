export function canMoveUpInPagedList(index, pageCurrent) {
  if (index < 0) {
    return false;
  }
  return index > 0 || (pageCurrent || 1) > 1;
}

export function canMoveDownInPagedList(index, listLen, pageCurrent, pageSize, total) {
  if (index < 0) {
    return false;
  }
  const size = pageSize || 30;
  const lastPage = Math.max(1, Math.ceil((Number(total) || 0) / size));
  return index < listLen - 1 || (pageCurrent || 1) < lastPage;
}

/** 与 GET /space/book 列表一致的筛选字段（排序/微调请求共用）。 */
export function workbenchListScopeFields({ pubTypes, keyword }) {
  const fields = {};
  if (pubTypes?.length) {
    fields.pubTypes = pubTypes;
  }
  const kw = (keyword || '').trim();
  if (kw) {
    fields.keyword = kw;
  }
  return fields;
}

/** 拖动排序请求体。 */
export function buildWorkbenchReorderBody({ orderedIds, pubTypes, keyword }) {
  return { orderedIds, ...workbenchListScopeFields({ pubTypes, keyword }) };
}

/** 拖动排序：当前页 id 顺序一次提交，后端在全量序中替换对应连续段。 */
export async function persistWorkbenchListReorder({ orderedIds, reorderPage }) {
  if (!orderedIds?.length || !reorderPage) {
    return false;
  }
  const res = await reorderPage(orderedIds);
  return res?.code === 200;
}
