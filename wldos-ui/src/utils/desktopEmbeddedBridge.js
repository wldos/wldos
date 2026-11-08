import wldosStorage from '@/utils/wldostorage';

/**
 * 桌面嵌入环境（JCEF 或 Electron）：将站点 origin 与 accessToken 同步到桌面壳，供自媒体委托轮询等使用。
 * 非桌面环境无 window.cefQuery / window.wldosDesktop，调用为 no-op。
 *
 * 两种桌面壳共用同一套"编码字符串请求"协议（如 `wldos:setToken:xxx`、
 * `wldos:external:open2:<channel>:<url>`），仅传输通道不同：
 * - JCEF：window.cefQuery({ request, onSuccess, onFailure })
 * - Electron（桌面壳技术栈迁移评估 Phase 1）：window.wldosDesktop.request(request) 返回 Promise
 * 因此本文件的导出函数签名保持不变，上层业务代码（AccountManageModal.jsx / BookView.jsx / index.jsx 等）
 * 不需要感知底层是哪种壳。
 */
function getTransport() {
  if (typeof window === 'undefined') {
    return null;
  }
  if (window.__WLDOS_DESKTOP_EMBEDDED__ === true) {
    return 'electron';
  }
  if (typeof window.cefQuery === 'function') {
    return 'jcef';
  }
  if (window.wldosDesktop && typeof window.wldosDesktop.request === 'function') {
    return 'electron';
  }
  return null;
}

export function isDesktopEmbedded() {
  if (getTransport() !== null) {
    return true;
  }
  if (typeof window === 'undefined') {
    return false;
  }
  // 壳端 openContent 统一追加；preload 偶发未注入时仍识别为桌面嵌入，避免 ProLayout 继续展示云端 WLDOS 品牌
  try {
    return new URLSearchParams(window.location.search || '').get(DESKTOP_EMBEDDED_QUERY) === '1';
  } catch (e) {
    return false;
  }
}

export const DESKTOP_DEFAULT_PRODUCT_TITLE = '数字管家';
export const DESKTOP_DEFAULT_ENTRY_PATH = '/space/book';
export const DESKTOP_DEFAULT_DOMAIN_NAME = 'localhost';
/** 生产 file:// 单入口 SPA 由壳注入目标路由（见 buildUiUrl / bootstrapDesktopEmbeddedApp） */
export const DESKTOP_SHELL_ROUTE_QUERY = '_wldos_shell_route';
/** 壳端内容叠层 URL 标记，供 isDesktopEmbedded 兜底识别 */
export const DESKTOP_EMBEDDED_QUERY = '_wldos_desktop';

/** 读取壳在 loadURL 时写入的一次性目标路径（如 /admin、/account/center） */
export function consumeDesktopShellBootRoute() {
  if (typeof window === 'undefined') {
    return null;
  }
  try {
    const boot = (new URLSearchParams(window.location.search || '').get(DESKTOP_SHELL_ROUTE_QUERY) || '').trim();
    return boot.startsWith('/') ? boot : null;
  } catch (e) {
    return null;
  }
}

/** 桌面壳配置的品牌信息（产品标题、Logo），Electron preload 或 JCEF 注入提供 */
export function getDesktopShellBranding() {
  if (!isDesktopEmbedded()) {
    return null;
  }
  const b = typeof window !== 'undefined' ? window.__WLDOS_DESKTOP_BRANDING__ : null;
  if (b && typeof b === 'object') {
    return {
      productTitle: String(b.productTitle || '').trim() || DESKTOP_DEFAULT_PRODUCT_TITLE,
      logoUrl: String(b.logoUrl || '').trim(),
      entryPath: String(b.entryPath || DESKTOP_DEFAULT_ENTRY_PATH).trim() || DESKTOP_DEFAULT_ENTRY_PATH,
      domainName: String(b.domainName || DESKTOP_DEFAULT_DOMAIN_NAME).trim() || DESKTOP_DEFAULT_DOMAIN_NAME,
    };
  }
  return {
    productTitle: DESKTOP_DEFAULT_PRODUCT_TITLE,
    logoUrl: '',
    entryPath: DESKTOP_DEFAULT_ENTRY_PATH,
    domainName: DESKTOP_DEFAULT_DOMAIN_NAME,
  };
}

/** 桌面嵌入时以壳配置覆盖站点 logo/标题，支持纯文字品牌（如「数字管家」） */
export function mergeDesktopShellSite(site) {
  if (!isDesktopEmbedded()) {
    return site || {};
  }
  const branding = getDesktopShellBranding();
  const merged = { ...(site || {}) };
  merged.siteTitle = branding.productTitle;
  merged.siteName = branding.productTitle;
  merged.siteDescription = '';
  if (branding.logoUrl) {
    merged.siteLogo = branding.logoUrl;
  } else {
    merged.siteLogo = '';
  }
  return merged;
}

/**
 * ProLayout 顶栏品牌：桌面壳内**只用壳配置**的标题/logo，绝不回退云端站点 WLDOS 图/名。
 * @returns {{ title: string, logo: string|false }}
 */
export function resolveDesktopProLayoutBrand(site) {
  if (!isDesktopEmbedded()) {
    const s = site || {};
    const title = `${s.siteTitle || ''}_${s.siteDescription || ''}`.replace(/_+$/, '');
    return {
      title: title || s.siteTitle || '',
      logo: s.siteLogo || false,
    };
  }
  const branding = getDesktopShellBranding();
  return {
    title: branding.productTitle || DESKTOP_DEFAULT_PRODUCT_TITLE,
    logo: branding.logoUrl || false,
  };
}

/** 桌面壳启动：默认进入创作工作台，并监听 JCEF 延迟注入的品牌事件 */
export function bootstrapDesktopEmbeddedApp(history) {
  if (typeof window === 'undefined' || !isDesktopEmbedded()) {
    return;
  }
  const goEntry = () => {
    const entry =
      consumeDesktopShellBootRoute()
      || getDesktopShellBranding()?.entryPath
      || DESKTOP_DEFAULT_ENTRY_PATH;
    const path = window.location.pathname || '/';
    if (path === '/' || path === '/index.html') {
      history.replace(entry);
    }
  };
  goEntry();
  window.addEventListener('wldos:desktop-branding-ready', goEntry);
}

/** ProLayout / 窗口标题：桌面端用壳产品名，避免云端站点 SEO 长标题 */
export function resolveDesktopLayoutTitle(site, pageTitle) {
  const branding = getDesktopShellBranding();
  const base = branding?.productTitle || site?.siteTitle || '';
  if (!base) {
    return pageTitle || '';
  }
  const page = String(pageTitle || '').trim();
  if (!page || page === base) {
    return base;
  }
  if (page.startsWith(`${base} -`) || page.startsWith(`${base}-`)) {
    return page;
  }
  return `${base} - ${page}`;
}

function sendDesktopQuery(request) {
  const transport = getTransport();
  if (!transport || !request) {
    return false;
  }
  try {
    if (transport === 'jcef') {
      window.cefQuery({
        request,
        persistent: false,
        onSuccess: () => {},
        onFailure: () => {},
      });
    } else {
      window.wldosDesktop.request(request).catch(() => {});
    }
    return true;
  } catch (e) {
    return false;
  }
}

/**
 * 与 sendDesktopQuery 相同的请求协议，但返回真 Promise：resolve 表示壳端确认执行完成，
 * reject 表示壳端返回失败。用于必须等待壳端真正完成才能继续的场景（见 purgeDesktopExternalAccount）。
 */
function sendDesktopQueryAsync(request) {
  const transport = getTransport();
  if (!transport || !request) {
    return Promise.resolve(false);
  }
  if (transport === 'electron') {
    return window.wldosDesktop.request(request).then(() => true);
  }
  return new Promise((resolve, reject) => {
    try {
      window.cefQuery({
        request,
        persistent: false,
        onSuccess: () => resolve(true),
        onFailure: (code, msg) => reject(new Error(msg || String(code))),
      });
    } catch (e) {
      reject(e);
    }
  });
}

export function notifyDesktopEmbeddedSession() {
  if (!isDesktopEmbedded()) {
    return;
  }
  try {
    // 登出后 accessToken 会被清空——这里必须也把"空值"同步给桌面壳，否则壳侧的"记住登录"
    // 状态（DesktopSessionBridge 落盘的 token）永远不会跟着登出清掉，下次重启桌面壳
    // 仍会恢复出登出前的登录态，与用户在 wldos-ui 里已经登出的事实矛盾。
    const token = wldosStorage.get('accessToken') || '';
    sendDesktopQuery(`wldos:setToken:${token}`);
  } catch (e) {
    // ignore
  }
}

/**
 * 通用 channel 规范：biz.module.scene[.entity]
 * 例如：social.account.bind.wechat / social.account.bind.wechat.12345
 */
export function buildDesktopExternalChannel({ biz, module, scene, entity } = {}) {
  const parts = [biz, module, scene, entity]
    .map((v) => String(v || '').trim())
    .filter(Boolean)
    .map((v) => v.replace(/\s+/g, '-').replace(/[^a-zA-Z0-9._-]/g, '_'));
  return parts.join('.') || 'default.external';
}

export function openDesktopExternal(channel, url) {
  const safeChannel = String(channel || '').trim();
  const safeUrl = String(url || '').trim();
  if (!safeChannel || !safeUrl) {
    return false;
  }
  return sendDesktopQuery(
    `wldos:external:open2:${encodeURIComponent(safeChannel)}:${encodeURIComponent(safeUrl)}`,
  );
}

/** 先关闭该 channel 再 open2（壳端会重新 loadURL）；用于必须整页重载的登录页等 */
export function openDesktopExternalForce(channel, url) {
  const safeChannel = String(channel || '').trim();
  const safeUrl = String(url || '').trim();
  if (!safeChannel || !safeUrl) {
    return false;
  }
  return sendDesktopQuery(
    `wldos:external:open2force:${encodeURIComponent(safeChannel)}:${encodeURIComponent(safeUrl)}`,
  );
}

export function showDesktopExternal(channel) {
  const safeChannel = String(channel || '').trim();
  if (!safeChannel) {
    return false;
  }
  return sendDesktopQuery(`wldos:external:show:${encodeURIComponent(safeChannel)}`);
}

export function closeDesktopExternal(channel) {
  const safeChannel = String(channel || '').trim();
  if (!safeChannel) {
    return sendDesktopQuery('wldos:external:close');
  }
  return sendDesktopQuery(`wldos:external:close:${encodeURIComponent(safeChannel)}`);
}

/**
 * 账号删除联动：彻底销毁账号专属的会话（JCEF：CefRequestContext + 磁盘 Profile；
 * Electron：session.fromPartition 对应分区的 storage/cache）。
 * 与 closeDesktopExternal 区别：close 仅 release（保留会话给下次绑定/发布复用）；
 * purge 是终态销毁。
 *
 * <p>返回真 Promise，resolve 时机由壳端保证在清理动作真正执行完成之后，而不是"消息已提交"就算数
 * ——历史坑：调用方若不等真正完成就紧接着重新登录，会命中"旧 cookie 还没清干净"的竞态（表现为
 * 二维码刷新失败，反复点刷新等一会才恢复）。需要严格等待完成的场景（如重新登录前清理）必须
 * `await`；仅做尽力清理的场景可 `.catch(() => {})`。Web 环境无桥时直接 resolve（无副作用）。
 */
export function purgeDesktopExternalAccount(accountId) {
  const id = String(accountId || '').trim();
  if (!id || !isDesktopEmbedded()) {
    return Promise.resolve(false);
  }
  return sendDesktopQueryAsync(`wldos:external:purge:${encodeURIComponent(id)}`);
}

/** 将外部浏览器会话从旧 channel（如 pending-*）迁到新 channel，同一会话，保留登录态 */
export function migrateDesktopExternalChannel(fromChannel, toChannel) {
  const from = encodeURIComponent(String(fromChannel || '').trim());
  const to = encodeURIComponent(String(toChannel || '').trim());
  if (!from || !to) return false;
  return sendDesktopQuery(`wldos:external:migrate:${from}:${to}`);
}

/**
 * 与 {@link migrateDesktopExternalChannel} 相同语义，但等待壳端完成后再 resolve。
 * 用于绑定写库后立即 show/mount，避免竞态白屏。
 */
export function migrateDesktopExternalChannelAsync(fromChannel, toChannel) {
  const from = encodeURIComponent(String(fromChannel || '').trim());
  const to = encodeURIComponent(String(toChannel || '').trim());
  if (!from || !to || !isDesktopEmbedded()) {
    return Promise.resolve(false);
  }
  return sendDesktopQueryAsync(`wldos:external:migrate:${from}:${to}`);
}

export function evalDesktopExternal(channel, jsCode) {
  const safeChannel = String(channel || '').trim();
  const safeJs = String(jsCode || '').trim();
  if (!safeJs) {
    return false;
  }
  if (!safeChannel) {
    return sendDesktopQuery(`wldos:external:eval:${encodeURIComponent(safeJs)}`);
  }
  return sendDesktopQuery(
    `wldos:external:eval2:${encodeURIComponent(safeChannel)}:${encodeURIComponent(safeJs)}`,
  );
}

export function mountDesktopExternalRect(rect) {
  if (!rect) return false;
  const x = Math.max(0, Math.round(Number(rect.x) || 0));
  const y = Math.max(0, Math.round(Number(rect.y) || 0));
  const width = Math.max(0, Math.round(Number(rect.width) || 0));
  const height = Math.max(0, Math.round(Number(rect.height) || 0));
  const viewportWidth = Math.max(1, Math.round(Number(rect.viewportWidth) || 0));
  const viewportHeight = Math.max(1, Math.round(Number(rect.viewportHeight) || 0));
  return sendDesktopQuery(`wldos:external:mount:${x},${y},${width},${height},${viewportWidth},${viewportHeight}`);
}

/**
 * 嵌入页坐标系 viewport：用 clientWidth/Height，避免 overflow 滚动条出现/消失时 innerWidth 抖动
 * 导致壳端 scale 来回变、BrowserView 晃动。
 */
export function readDesktopEmbedViewport() {
  if (typeof document === 'undefined') {
    return { viewportWidth: 1, viewportHeight: 1 };
  }
  const root = document.documentElement;
  return {
    viewportWidth: Math.max(1, root.clientWidth || window.innerWidth || 1),
    viewportHeight: Math.max(1, root.clientHeight || window.innerHeight || 1),
  };
}

/** 测量 mount 槽在嵌入页内的矩形（含稳定 viewport） */
export function measureDesktopEmbedMountRect(mountEl) {
  if (!mountEl || typeof mountEl.getBoundingClientRect !== 'function') return null;
  const r = mountEl.getBoundingClientRect();
  if (r.width < 4 || r.height < 4) return null;
  const { viewportWidth, viewportHeight } = readDesktopEmbedViewport();
  return {
    x: r.left,
    y: r.top,
    width: r.width,
    height: r.height,
    viewportWidth,
    viewportHeight,
  };
}

export function unmountDesktopExternalRect() {
  return sendDesktopQuery('wldos:external:unmount');
}

/**
 * 主窗内打开 Ant 模态/重要弹层时，临时隐藏桌面壳外部站点叠层，避免原生层挡住 message/Modal（不断开登录会话）。
 * @param {boolean} suppressed true=隐藏叠层，false=按当前 mount 区域恢复
 */
export function setDesktopExternalModalSuppressed(suppressed) {
  return sendDesktopQuery(
    `wldos:external:modal-suppress:${suppressed ? '1' : '0'}`,
  );
}

/**
 * 请求主进程卸载当前内容叠层（对应 10 号设计文档 H4 `wldos:shell:closeContent`，
 * F5/W7 账号管理面板的关闭按钮、以及其他 headless 内容页需要"关闭自己"时复用）。
 *
 * <p>命名对齐说明：`wldos:shell:closeContent` 这个通道名已由 Electron 主进程侧
 * `ShellHomeIpc.ts` 通过 `ipcMain.handle('wldos:shell:closeContent', ...)` 落地，
 * 但该 handle 目前只挂在原生 Shell 渲染层专用的 `shell-preload.ts`（`window.wldosShell`），
 * 与本文件走的 `window.wldosDesktop.request` / `wldos:query` 编码字符串协议（`WldosIpcDispatcher.ts`）
 * 是两条独立通道——内容页面（如本账号管理面板）目前只能访问后者。是否在 `WldosIpcDispatcher.ts`
 * 里识别并转发本字符串到 `BrowserShellWindow.closeContent()`，由并行的 Electron 主进程任务负责；
 * 本函数按既定通道名先行发出请求，不等待对方任务完成——对方任务落地前，本调用会因
 * `unrecognized request` 静默失败（见 `sendDesktopQuery` 的 `.catch(() => {})`），
 * 调用方应始终自带非 Electron/失败兜底（如 `history.back()`）。
 *
 * @returns {boolean} 是否已成功把请求发给桌面壳传输层；不代表叠层已真正卸载
 */
export function requestCloseShellContent() {
  return sendDesktopQuery('wldos:shell:closeContent');
}

/**
 * 桌面壳检测到外部平台登录就绪后，向主窗派发 {@code CustomEvent('wldos:social-bind-session-ready', { detail })}。
 * detail: { platform, scene:'bind', channel, sourceUrl, detectedAccountName?, avatarUrl?, profileExtra? }
 */
export function subscribeSocialBindSessionReady(handler) {
  if (typeof window === 'undefined' || typeof handler !== 'function') {
    return () => {};
  }
  const fn = (ev) => {
    handler(ev?.detail || {});
  };
  window.addEventListener('wldos:social-bind-session-ready', fn);
  return () => window.removeEventListener('wldos:social-bind-session-ready', fn);
}
