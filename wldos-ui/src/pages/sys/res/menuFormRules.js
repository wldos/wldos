/** 门户 menu / plugin_menu：展示区域仅对「可点击根菜单」及存量未标注节点生效 */
export function isMenuRegionApplicable(resourceType, menuNodeKind) {
  if (resourceType !== 'menu' && resourceType !== 'plugin_menu') {
    return false;
  }
  if (menuNodeKind === 'route' || menuNodeKind === 'nav_dir') {
    return false;
  }
  return true;
}

export function shouldHideFromMainNav(item) {
  if (!item) return true;
  if (item.hideInMenu) return true;
  if (item.menuNodeKind === 'route') return true;
  return false;
}

export function stripHiddenNavMenus(menus) {
  if (!menus || !Array.isArray(menus)) return [];
  return menus
    .filter((item) => !shouldHideFromMainNav(item))
    .map((item) => ({
      ...item,
      children: item.children?.length ? stripHiddenNavMenus(item.children) : item.children,
    }));
}
