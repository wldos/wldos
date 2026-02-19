import request from '@/utils/request';
import config from '@/utils/config';
import { getRouteAuthCache, setRouteAuthCache } from '@/utils/apiCache';

const { prefix } = config;

export async function queryUsers() {
  return request(`${prefix}/user/users`);
}
export async function queryCurrent() {
  return request(`${prefix}/user/currentUser`);
}
export async function queryCurAccount() {
  return request(`${prefix}/user/curAccount`);
}
export async function queryMenu() {
  return request(`${prefix}/admin/sys/user/adminMenu`);
}
export async function queryUserMenu(userId) {
  return request(`${prefix}/user/menu/${userId}`);
}
export async function queryUserAdminMenu(userId) {
  return request(`${prefix}/admin/sys/user/adminMenu/${userId}`);
}
export async function querySiteSeo() {
  return request(`${prefix}/user/curDomain`);
}
export async function querySiteSlogan() {
  return request(`${prefix}/user/slogan`);
}
/**
 * 轻量轮询：仅取条数，用于角标与“是否有新消息”判断；不拉完整列表。
 */
export async function queryNoticesCount() {
  const { prefix } = config;
  try {
    const res = await request(`${prefix}/user/notices/count`, { method: 'GET' });
    const data = res?.data?.data ?? res?.data;
    if (data && typeof data.totalCount === 'number') {
      return { totalCount: data.totalCount, unreadCount: data.unreadCount ?? data.totalCount };
    }
  } catch (e) {}
  return { totalCount: 0, unreadCount: 0 };
}

/**
 * 消息气泡数据源（通知、消息、待办）
 * 优先使用统一通知接口 GET /user/notices；若无则降级为工单列表映射为「通知」（工单提醒）。
 * 站内信、邮件由后端在统一接口中返回 type=message/notification 即可。
 * 建议：轮询用 queryNoticesCount，点击气泡时再调本接口拉全量。
 */
export async function queryNotices() {
  const { prefix } = config;
  try {
    const res = await request(`${prefix}/user/notices`, { method: 'GET' });
    const data = res?.data?.data ?? res?.data;
    if (data && Array.isArray(data)) return data;
    if (data?.list && Array.isArray(data.list)) return data.list;
    if (data?.rows && Array.isArray(data.rows)) return data.rows;
  } catch (e) {
    // 统一接口可能未实现，降级为工单列表
  }
  try {
    const res = await request(`${prefix}/ticket/list`, { method: 'GET', params: { current: 1, pageSize: 8 } });
    const raw = res?.data?.data ?? res?.data;
    const rows = raw?.rows ?? raw?.list ?? (Array.isArray(raw) ? raw : []);
    return (rows || []).map((row) => ({
      id: `ticket-${row.id}`,
      type: 'notification',
      title: row.subject ? `工单：${row.subject}` : '工单',
      description: row.subject || row.ticketNo || '-',
      datetime: row.createTime,
      read: row.status === 'CLOSED',
      link: `/ticket/${row.id}`,
    }));
  } catch (e) {
    return [];
  }
}

/**
 * 标记工单已读（用户侧气泡点击时调用，持久化已读状态）
 */
export async function markTicketRead(ticketId) {
  if (!ticketId) return;
  try {
    await request(`${prefix}/ticket/${ticketId}/read`, { method: 'POST' });
  } catch (e) {
    // 忽略失败，角标下次拉取会更新
  }
}

/**
 * 管理端标记工单已读（点击气泡内某条或进入详情后调用，角标与列表按客服已读过滤）
 */
export async function markAdminTicketRead(ticketId) {
  if (!ticketId) return;
  try {
    await request(`${prefix}/admin/ticket/${ticketId}/read`, { method: 'POST' });
  } catch (e) {
    // 忽略失败，角标下次拉取会更新
  }
}

/**
 * 管理端工单待处理数量（顶部通知角标），轮询用
 */
export async function queryAdminTicketNoticesCount() {
  try {
    const res = await request(`${prefix}/admin/ticket/notices/count`, { method: 'GET' });
    const data = res?.data?.data ?? res?.data;
    if (data && typeof data.totalCount === 'number') {
      return { totalCount: data.totalCount, unreadCount: data.unreadCount ?? data.totalCount };
    }
  } catch (e) {}
  return { totalCount: 0, unreadCount: 0 };
}

/**
 * 管理端工单待处理列表（通知下拉）
 */
export async function queryAdminTicketNotices(maxCount = 20) {
  try {
    const res = await request(`${prefix}/admin/ticket/notices`, { method: 'GET', params: { maxCount } });
    const data = res?.data?.data ?? res?.data;
    if (data && Array.isArray(data)) return data;
    if (data?.list && Array.isArray(data.list)) return data.list;
    if (data?.rows && Array.isArray(data.rows)) return data.rows;
  } catch (e) {}
  return [];
}

export async function fetchRoutes() {
  return request(`${prefix}/user/routes`);
}
/**
 * 路由权限校验（带 pathname 缓存）
 * 同一 pathname 在会话内复用结果，减少重复请求
 */
export async function isAuth(params) {
  const pathname = params?.route;
  const cached = pathname ? getRouteAuthCache(pathname) : undefined;
  if (cached !== undefined) {
    return { code: 200, data: cached, success: true };
  }
  const res = await request(`${prefix}/user/route`, { params });
  const code = res?.data;
  if (pathname && (code === '200' || code === '401' || code === '403')) {
    setRouteAuthCache(pathname, code);
  }
  return res;
}
export async function fetchDynRoutes() { // 获取后端设置的首页动态模板，不是动态路由
  return request(`${prefix}/user/dynamite`);
}