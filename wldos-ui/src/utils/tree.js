// 通用：分页聚合 + 扁平转树

/**
 * 通过分页接口聚合全量数据
 * 要求接口返回 { data: { total, rows } }
 * @param {Function} fetchPage - (params) => Promise<{data:{total:number, rows:any[]}}>
 * @param {Object} baseParams - 额外查询参数，例如过滤条件
 * @param {number} pageSize - 每页大小，默认1000
 * @returns {Promise<any[]>}
 */
export async function fetchAllPages(fetchPage, baseParams = {}, pageSize = 1000) {
  const all = [];
  let current = 1;
  let total = Infinity;

  while (all.length < total) {
    // 兼容 Ant ProTable 常用分页参数：current/pageSize
    const res = await fetchPage({ ...baseParams, current, pageSize });
    const rows = res?.data?.rows || [];
    total = typeof res?.data?.total === 'number' ? res.data.total : rows.length;

    if (rows.length === 0) break;
    all.push(...rows);
    current += 1;
  }
  return all;
}

/**
 * 扁平列表转树
 * @param {any[]} list - 扁平数组
 * @param {Object} options
 * @param {string} options.idKey - 唯一键，默认 'id'
 * @param {string} options.parentKey - 父ID键，默认 'parentId'
 * @param {Function} options.nodeMapper - (item) => ({ title, value, key, disabled, raw })
 * @returns {any[]} 树形数组（children）
 */
export function buildTreeFromList(list, {
  idKey = 'id',
  parentKey = 'parentId',
  nodeMapper = (item) => ({ title: item.name || item.title || String(item[idKey]), value: item[idKey], key: item[idKey] })
} = {}) {
  if (!Array.isArray(list) || list.length === 0) return [];

  const idToNode = new Map();
  const roots = [];

  // 先创建节点映射
  for (const item of list) {
    const mapped = nodeMapper(item);
    const node = { ...mapped };
    if (!node.key) node.key = mapped.value;
    idToNode.set(item[idKey], node);
  }

  // 二次遍历挂载到父节点
  for (const item of list) {
    const node = idToNode.get(item[idKey]);
    const pid = item[parentKey];
    if (pid == null || pid === 0 || !idToNode.has(pid)) {
      roots.push(node);
    } else {
      const parent = idToNode.get(pid);
      if (!parent.children) parent.children = [];
      parent.children.push(node);
    }
  }

  return roots;
}

// 节点规范化
export function normalizeCompanyNode(item) {
  return {
    title: item.comName || item.label || item.name || item.title || String(item.id || item.value),
    value: item.value != null ? item.value : item.id,
    key: item.id != null ? item.id : item.value,
    disabled: String(item.isValid) === '0',
    isLeaf: Array.isArray(item.children) ? item.children.length === 0 : false,
    raw: item,
  };
}

export function normalizeArchNode(item) {
  return {
    title: item.archName || item.label || item.name || item.title || String(item.id || item.value),
    value: item.value != null ? item.value : item.id,
    key: item.id != null ? item.id : item.value,
    disabled: String(item.isValid) === '0',
    isLeaf: Array.isArray(item.children) ? item.children.length === 0 : false,
    raw: item,
  };
}


