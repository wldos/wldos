/**
 * 节假日日历 管理端 API。
 * 后端：modules/wldos-platform → com.wldos.platform.calendar.controller.HolidayAdminController
 *      网关前缀 /api，类级 @RequestMapping("/admin/sys/calendar/holiday")。
 */
import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryByYear(year) {
  return request(`${prefix}/admin/sys/calendar/holiday/list`, { params: { year } });
}

export async function saveItem(data) {
  return request(`${prefix}/admin/sys/calendar/holiday/save`, { method: 'POST', data });
}

export async function batchSave(items) {
  return request(`${prefix}/admin/sys/calendar/holiday/batch-save`, { method: 'POST', data: items });
}

// 删除：复用父类 EntityController.remove(@RequestBody E)，与 ceos-ads / ceos-place 等管理端模块一致，
// 用 data: { id } body 传递；后端按 id 物理删（日历条目可重建，无需软删）。
export async function removeItem(id) {
  return request(`${prefix}/admin/sys/calendar/holiday/delete`, {
    method: 'DELETE',
    data: { id },
  });
}

// 注意：utils/request.js 当前仅在 method=GET 时透传 params，POST/PUT/DELETE 会丢弃；
// 项目级 bug 修复风险面较大（22 个 service 文件可能受影响），此处用显式拼 URL query 绕过。
export async function syncYear(year, fallback = false) {
  const qs = `year=${encodeURIComponent(year)}&fallback=${encodeURIComponent(fallback)}`;
  return request(`${prefix}/admin/sys/calendar/holiday/sync?${qs}`, { method: 'POST' });
}

export async function importBuiltin(year) {
  const qs = `year=${encodeURIComponent(year)}`;
  return request(`${prefix}/admin/sys/calendar/holiday/import-builtin?${qs}`, { method: 'POST' });
}

export async function builtinYears() {
  return request(`${prefix}/admin/sys/calendar/holiday/builtin-years`);
}

export async function fetchEnums() {
  return request(`${prefix}/admin/sys/calendar/holiday/enums`);
}
