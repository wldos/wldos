/** 站点自建 nav_dir：menu_node_kind=nav_dir 且 scope_level=domain */
export function isSiteOwnedNavDir(record) {
  return record?.menuNodeKind === 'nav_dir' && record?.scopeLevel === 'domain';
}

export function isNavDirKind(record) {
  return record?.menuNodeKind === 'nav_dir';
}

/** 递归过滤树：移除站点自建 nav_dir 节点 */
export function filterTreeHideSiteNavDir(nodes) {
  if (!nodes?.length) {
    return [];
  }
  return nodes
    .filter((node) => !isSiteOwnedNavDir(node))
    .map((node) => {
      if (!node.children?.length) {
        return node;
      }
      return { ...node, children: filterTreeHideSiteNavDir(node.children) };
    });
}
