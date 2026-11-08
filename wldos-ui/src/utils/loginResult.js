/**
 * 解析返回 Login 形态的接口（register/reset/changePasswd 等）。
 * @returns {{ ok: boolean, news?: string }}
 */
export function parseLoginActionResult(res) {
  if (res?.code !== 200) {
    return { ok: false };
  }
  const data = res?.data;
  if (!data) {
    return { ok: false, news: '操作失败，请重试' };
  }
  if (data.status === 'error') {
    return { ok: false, news: data.news || '操作失败' };
  }
  if (data.status === 'ok') {
    return { ok: true };
  }
  return { ok: false, news: '操作失败，请重试' };
}
