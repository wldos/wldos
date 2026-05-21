import {
  Space,
  Table,
  Tag,
  message,
  Popconfirm,
  Button,
  Tooltip,
  Spin,
  Empty,
} from 'antd';
import { CloseOutlined, PushpinOutlined, PushpinFilled } from '@ant-design/icons';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  queryMyLoginLogs,
  queryMyOpLogs,
  queryMyShortcuts,
  updateMyShortcut,
  deleteMyShortcut,
  queryUserAdminMenu,
} from '@/services/user';
import { normalizePath } from '@/utils/portalMenuVisit';
import styles from '../Center.less';

export const TAB_TYPE_LABELS = {
  info: '我的信息',
  book: '我的作品',
  applications: '我关注的',
  projects: '我喜欢的',
  permission: '我的权限',
  shortcuts: '我的常用',
  login_log: '我的登录日志',
  op_log: '我的操作记录',
};

export function resolveTabTitle(def) {
  if (def && def.title) return def.title;
  const t = def && def.type;
  return (t && TAB_TYPE_LABELS[t]) || t || '';
}

function formatDateTime(val) {
  if (val == null || val === '') return '';
  if (typeof val === 'number') return new Date(val).toLocaleString();
  return String(val).replace('T', ' ').slice(0, 19);
}

/** 管理后台菜单（与管理端 ResourceEnum 一致） */
const ADMIN_MENU_RESOURCE_TYPES = new Set(['admin_menu', 'admin_plugin_menu']);

/** 仅管理类菜单资源，用于「我的权限」分模块展示 */
function isValidAdminMenuResource(node) {
  if (!node || typeof node !== 'object') return false;
  const name = (node.name || '').trim();
  if (!name) return false;
  const path = (node.path || '').trim();
  if (!path) return false;
  const lower = path.toLowerCase();
  if (lower.includes('://') || lower.startsWith('javascript:') || lower.startsWith('data:')) return false;
  if (!path.startsWith('/')) return false;
  const t = (node.type || '').trim();
  if (
    t === 'button' ||
    t === 'admin_button' ||
    t === 'admin_plugin_button' ||
    t === 'webServ'
  ) {
    return false;
  }
  if (!ADMIN_MENU_RESOURCE_TYPES.has(t)) return false;
  return true;
}

function moduleKeyForRoot(root, source) {
  if (!root) return 'misc';
  if (root.id != null) return `${source}-r-${root.id}`;
  const slug = String(root.path || root.name || 'root').replace(/\s+/g, '-');
  return `${source}-r-${slug}`;
}

/**
 * 按顶级「管理」菜单分块；子区域仅展示后代节点，不在标签中重复顶级标题。
 */
function buildAdminMenuModules(adminMenu) {
  const globalSeen = new Set();
  const modules = [];

  const roots = Array.isArray(adminMenu) ? adminMenu : [];
  const sorted = [...roots].sort(
    (a, b) => (Number(a.displayOrder) || 0) - (Number(b.displayOrder) || 0),
  );

  for (const root of sorted) {
    const leaves = [];
    const walk = (node, isModuleRoot) => {
      if (!isModuleRoot && isValidAdminMenuResource(node)) {
        const lk = node.id != null ? `id:${node.id}` : `name:${node.name}:${node.path}`;
        if (!globalSeen.has(lk)) {
          globalSeen.add(lk);
          leaves.push({
            key: lk,
            label: (node.name || '').trim(),
            path: normalizePath(node.path),
          });
        }
      }
      (node.children || []).forEach((c) => walk(c, false));
    };
    walk(root, true);
    if (leaves.length === 0) continue;
    const title = (root.name || '').trim() || '未命名';
    modules.push({
      moduleKey: moduleKeyForRoot(root, 'a'),
      title,
      leaves,
    });
  }

  return modules;
}

/** 按管理端菜单路径前缀，将常用项归入与「我的权限」相同的模块 */
function findModuleForShortcutPath(shortcutPath, permModules) {
  const normS = normalizePath(shortcutPath || '/');
  let best = null;
  let bestLen = -1;
  for (const m of permModules) {
    for (const leaf of m.leaves) {
      const normP = leaf.path;
      if (!normP || normP === '/') continue;
      if (normS === normP || normS.startsWith(`${normP}/`)) {
        if (normP.length > bestLen) {
          bestLen = normP.length;
          best = m;
        }
      }
    }
  }
  return best;
}

/** 仅归入能匹配到管理端叶子路径的项；不匹配的不展示（避免出现「其他」混杂门户入口等） */
function groupShortcutsByAdminModules(shortcutItems, permModules) {
  const byKey = new Map();
  permModules.forEach((m) => byKey.set(m.moduleKey, []));

  (shortcutItems || []).forEach((row) => {
    const mod = findModuleForShortcutPath(row.path, permModules);
    if (mod) byKey.get(mod.moduleKey).push(row);
  });

  const sections = [];
  for (const m of permModules) {
    const rows = byKey.get(m.moduleKey);
    if (rows && rows.length) {
      sections.push({ moduleKey: m.moduleKey, title: m.title, rows });
    }
  }
  return sections;
}

/** 与个人中心「我的信息/作品」等 ProList 空列表一致：简笔画图标 +「暂无数据」 */
function AccountCenterEmpty() {
  return (
    <div className={styles.accountCenterEmpty}>
      <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无数据" />
    </div>
  );
}

/** 与个人中心「我的信息/作品」等 ProList 页一致：透明底、分区标题、无整页色块 */
function AccountCenterPane({ loading, children }) {
  return (
    <div className={styles.accountCenterPane}>
      <Spin spinning={loading}>
        <div className={styles.accountCenterPaneInner}>{children}</div>
      </Spin>
    </div>
  );
}

function AccountNativeSection({ title, children }) {
  return (
    <div className={styles.accountNativeSection}>
      <div className={styles.accountNativeSectionTitle}>{title}</div>
      <div className={styles.accountNativeSectionBody}>{children}</div>
    </div>
  );
}

export function AccountPermissionsPanel({ userId }) {
  const [loading, setLoading] = useState(false);
  const [adminMenu, setAdminMenu] = useState([]);

  useEffect(() => {
    if (!userId) return;
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const adminRes = await queryUserAdminMenu(userId).catch(() => ({ data: [] }));
        if (cancelled) return;
        const rawAdmin = adminRes?.data?.data ?? adminRes?.data;
        setAdminMenu(Array.isArray(rawAdmin) ? rawAdmin : []);
      } catch (e) {
        if (!cancelled) {
          message.error('加载管理菜单失败');
          setAdminMenu([]);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [userId]);

  const modules = useMemo(() => buildAdminMenuModules(adminMenu), [adminMenu]);

  return (
    <AccountCenterPane loading={loading}>
      {!loading && modules.length === 0 ? (
        <AccountCenterEmpty />
      ) : (
        modules.map((m) => (
          <AccountNativeSection key={m.moduleKey} title={m.title}>
            <Space wrap size={[8, 8]}>
              {m.leaves.map((leaf) => (
                <Tag key={leaf.key} className={styles.accountCenterTag}>
                  {leaf.label}
                </Tag>
              ))}
            </Space>
          </AccountNativeSection>
        ))
      )}
    </AccountCenterPane>
  );
}

/** 「我的常用」标签文案：以持久化表里的 title 为准（与 wo_user_portal_shortcut.title 一致） */
function shortcutTableTitle(row) {
  return (row.title == null ? '' : String(row.title)).trim();
}

function ShortcutMatrixTag({ row, onPin, onRemove }) {
  const href = normalizePath(row.path || '/');
  const label = shortcutTableTitle(row);
  return (
    <span className={styles.accountShortcutMatrixItem}>
      <Tag className={styles.accountCenterTag}>
        <a
          className={styles.accountShortcutMatrixLink}
          href={href}
          target="_blank"
          rel="noopener noreferrer"
        >
          {label}
        </a>
      </Tag>
      <span className={styles.accountShortcutMatrixHover}>
        <Tooltip title={row.pinned ? '取消置顶' : '置顶'}>
          <Button
            type="text"
            size="small"
            className={styles.accountShortcutMatrixIconBtn}
            aria-label={row.pinned ? '取消置顶' : '置顶'}
            icon={row.pinned ? <PushpinFilled /> : <PushpinOutlined />}
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              onPin(row, !row.pinned);
            }}
          />
        </Tooltip>
        <Popconfirm title="删除该条常用记录？" onConfirm={() => onRemove(row.id)}>
          <Button
            type="text"
            size="small"
            className={styles.accountShortcutMatrixCloseBtn}
            icon={<CloseOutlined />}
            aria-label="删除"
            onClick={(e) => e.stopPropagation()}
          />
        </Popconfirm>
      </span>
    </span>
  );
}

export function AccountShortcutsPanel({ userId }) {
  const [items, setItems] = useState([]);
  const [adminMenu, setAdminMenu] = useState([]);
  const [loading, setLoading] = useState(false);

  const reload = useCallback(async () => {
    if (!userId) {
      setItems([]);
      setAdminMenu([]);
      return;
    }
    setLoading(true);
    try {
      const [shortRes, adminRes] = await Promise.all([
        queryMyShortcuts(),
        queryUserAdminMenu(userId).catch(() => ({ data: [] })),
      ]);
      let rows = shortRes?.data?.data ?? shortRes?.data;
      if (!Array.isArray(rows)) rows = [];
      setItems(rows);
      const rawAdmin = adminRes?.data?.data ?? adminRes?.data;
      setAdminMenu(Array.isArray(rawAdmin) ? rawAdmin : []);
    } catch (e) {
      message.error('加载我的常用失败');
      setItems([]);
      setAdminMenu([]);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    reload();
  }, [reload]);

  const permModules = useMemo(() => buildAdminMenuModules(adminMenu), [adminMenu]);

  const sections = useMemo(() => {
    const visible = (items || []).filter(
      (row) =>
        shortcutTableTitle(row) && findModuleForShortcutPath(row.path, permModules),
    );
    return groupShortcutsByAdminModules(visible, permModules);
  }, [items, permModules]);

  const handleRemove = async (id) => {
    try {
      await deleteMyShortcut(id);
      message.success('已删除');
      reload();
    } catch (e) {
      message.error(e?.response?.message || e?.message || '删除失败');
    }
  };

  const handlePin = async (row, pinned) => {
    try {
      await updateMyShortcut(row.id, { pinned });
      message.success(pinned ? '已置顶' : '已取消置顶');
      reload();
    } catch (e) {
      message.error(e?.response?.message || e?.message || '操作失败');
    }
  };

  if (!userId) {
    return (
      <AccountCenterPane loading={false}>
        <AccountCenterEmpty />
      </AccountCenterPane>
    );
  }

  return (
    <AccountCenterPane loading={loading}>
      {!loading && sections.length === 0 ? (
        <AccountCenterEmpty />
      ) : (
        sections.map((sec) => (
          <AccountNativeSection key={sec.moduleKey} title={sec.title}>
            <Space wrap size={[8, 8]}>
              {sec.rows.map((row) => (
                <ShortcutMatrixTag
                  key={row.id}
                  row={row}
                  onPin={handlePin}
                  onRemove={handleRemove}
                />
              ))}
            </Space>
          </AccountNativeSection>
        ))
      )}
    </AccountCenterPane>
  );
}

export function AccountLoginLogsPanel() {
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState({ current: 1, pageSize: 10 });

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await queryMyLoginLogs({
        current: page.current,
        pageSize: page.pageSize,
      });
      const body = res?.data?.data ?? res?.data;
      setRows(body?.rows || []);
      setTotal(Number(body?.total) || 0);
    } finally {
      setLoading(false);
    }
  }, [page.current, page.pageSize]);

  useEffect(() => {
    load();
  }, [load]);

  const columns = [
    { title: '时间', dataIndex: 'occurAt', key: 'occurAt', render: formatDateTime },
    { title: '事件', dataIndex: 'eventType', key: 'eventType', ellipsis: true },
    { title: '结果', dataIndex: 'result', key: 'result', width: 100 },
    { title: '账号', dataIndex: 'loginAccount', key: 'loginAccount', ellipsis: true },
    { title: 'IP', dataIndex: 'ip', key: 'ip', width: 120 },
  ];

  return (
    <div className={styles.accountCenterPane}>
      <Table
        className={styles.accountCenterTable}
        size="small"
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={rows}
        pagination={{
          current: page.current,
          pageSize: page.pageSize,
          total,
          showSizeChanger: true,
          onChange: (c, ps) => setPage({ current: c, pageSize: ps }),
        }}
      />
    </div>
  );
}

export function AccountOpLogsPanel() {
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState({ current: 1, pageSize: 10 });

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await queryMyOpLogs({
        current: page.current,
        pageSize: page.pageSize,
      });
      const body = res?.data?.data ?? res?.data;
      setRows(body?.rows || []);
      setTotal(Number(body?.total) || 0);
    } finally {
      setLoading(false);
    }
  }, [page.current, page.pageSize]);

  useEffect(() => {
    load();
  }, [load]);

  const columns = [
    { title: '时间', dataIndex: 'occurAt', key: 'occurAt', width: 170, render: formatDateTime },
    { title: '模块', dataIndex: 'module', key: 'module', width: 120, ellipsis: true },
    { title: '操作', dataIndex: 'action', key: 'action', width: 120, ellipsis: true },
    {
      title: '资源',
      key: 'resource',
      ellipsis: true,
      render: (_, r) => r.resourceTypeLabel || r.resourceType || '-',
    },
    {
      title: '资源名',
      dataIndex: 'resourceName',
      key: 'resourceName',
      ellipsis: true,
    },
    { title: '结果码', dataIndex: 'resultCode', key: 'resultCode', width: 90 },
    { title: 'IP', dataIndex: 'ip', key: 'ip', width: 120 },
  ];

  return (
    <div className={styles.accountCenterPane}>
      <Table
        className={styles.accountCenterTable}
        size="small"
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={rows}
        pagination={{
          current: page.current,
          pageSize: page.pageSize,
          total,
          showSizeChanger: true,
          onChange: (c, ps) => setPage({ current: c, pageSize: ps }),
        }}
      />
    </div>
  );
}
