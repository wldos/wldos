import React from 'react';
import { Tag, Tooltip } from 'antd';

const MENU_RESOURCE_TYPES = new Set([
  'menu',
  'admin_menu',
  'plugin_menu',
  'admin_plugin_menu',
]);

const BUTTON_RESOURCE_TYPES = new Set([
  'button',
  'admin_button',
  'plugin_button',
  'admin_plugin_button',
]);

const MENU_NODE_KIND_COLORS = {
  nav_dir: 'purple',
  menu_root: 'blue',
  route: 'cyan',
  link: 'geekblue',
};

/** 行内 Tag 仅用短文案；完整语义见 tip（Tooltip / 图例说明） */
const MENU_NODE_KIND_SHORT = {
  nav_dir: '目录',
  menu_root: '根',
  route: '页',
  link: '链',
};

const RESOURCE_TYPE_SHORT = {
  menu: '菜单',
  admin_menu: '菜单',
  plugin_menu: '菜单',
  admin_plugin_menu: '菜单',
  button: '操作',
  admin_button: '操作',
  plugin_button: '操作',
  admin_plugin_button: '操作',
  webServ: '服务',
  dataServ: '数据',
  _static: '静态',
  other: '其他',
};

const RESOURCE_TYPE_COLORS = {
  admin_button: 'orange',
  button: 'orange',
  plugin_button: 'orange',
  admin_plugin_button: 'orange',
  webServ: 'gold',
  dataServ: 'lime',
  _static: 'default',
  other: 'default',
};

export const isMenuResourceType = (type) => type && MENU_RESOURCE_TYPES.has(type);

export const isButtonResourceType = (type) => type && BUTTON_RESOURCE_TYPES.has(type);

export const buildEnumLabelMap = (options) => {
  const map = {};
  (options || []).forEach((item) => {
    if (item?.value != null) {
      map[item.value] = item.label;
    }
  });
  return map;
};

const fullTip = (short, fullFromEnum, fallback) => fullFromEnum || fallback || short;

/**
 * 穿梭框行内类型 Tag：短 label + 完整 tip。
 */
export const resolveAuthResTypeTag = (item, resourceTypeLabels, menuNodeKindLabels) => {
  const resourceType = item?.resourceType;
  const menuNodeKind = item?.menuNodeKind;

  if (isButtonResourceType(resourceType)) {
    const short = RESOURCE_TYPE_SHORT[resourceType] || '操作';
    return {
      label: short,
      tip: fullTip(short, resourceTypeLabels[resourceType], '操作 / API 鉴权'),
      color: RESOURCE_TYPE_COLORS[resourceType] || 'orange',
      key: `btn:${resourceType}`,
    };
  }

  if (isMenuResourceType(resourceType) && menuNodeKind && MENU_NODE_KIND_SHORT[menuNodeKind]) {
    const short = MENU_NODE_KIND_SHORT[menuNodeKind];
    return {
      label: short,
      tip: fullTip(short, menuNodeKindLabels[menuNodeKind], short),
      color: MENU_NODE_KIND_COLORS[menuNodeKind] || 'blue',
      key: `mk:${menuNodeKind}`,
    };
  }

  if (resourceType && (RESOURCE_TYPE_SHORT[resourceType] || resourceTypeLabels[resourceType])) {
    const short = RESOURCE_TYPE_SHORT[resourceType] || '菜单';
    return {
      label: short,
      tip: fullTip(short, resourceTypeLabels[resourceType], short),
      color: RESOURCE_TYPE_COLORS[resourceType] || 'blue',
      key: `rt:${resourceType}`,
    };
  }

  return null;
};

const tagStyle = { margin: 0, flexShrink: 0, lineHeight: '16px', fontSize: 11, padding: '0 4px' };

export const AuthResRowTitle = ({ text, typeTag }) => (
  <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, maxWidth: '100%', minWidth: 0 }}>
    {typeTag ? (
      <Tooltip title={typeTag.tip}>
        <Tag color={typeTag.color} style={tagStyle}>
          {typeTag.label}
        </Tag>
      </Tooltip>
    ) : null}
    <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', minWidth: 0 }}>{text}</span>
  </span>
);

export const mapTreeWithRowTitles = (nodes, renderRowTitle) => {
  if (!nodes || nodes.length === 0) {
    return [];
  }
  return nodes.map(({ children, ...node }) => ({
    ...node,
    title: renderRowTitle(node),
    children: mapTreeWithRowTitles(children, renderRowTitle),
  }));
};
