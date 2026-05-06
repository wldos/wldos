import wldosStorage from '@/utils/wldostorage';

/**
 * 桌面 JCEF 嵌入环境：将站点 origin 与 accessToken 同步到 Java（cefQuery），供自媒体委托轮询等使用。
 * 非桌面环境无 window.cefQuery，调用为 no-op。
 */
export function notifyDesktopEmbeddedSession() {
  if (typeof window === 'undefined' || typeof window.cefQuery !== 'function') {
    return;
  }
  try {
    const origin = window.location && window.location.origin;
    if (origin) {
      window.cefQuery({
        request: `wldos:setSite:${origin}`,
        persistent: false,
        onSuccess: () => {},
        onFailure: () => {},
      });
    }
    const token = wldosStorage.get('accessToken');
    if (token) {
      window.cefQuery({
        request: `wldos:setToken:${token}`,
        persistent: false,
        onSuccess: () => {},
        onFailure: () => {},
      });
    }
  } catch (e) {
    // ignore
  }
}

export function isDesktopEmbedded() {
  return typeof window !== 'undefined' && typeof window.cefQuery === 'function';
}

function sendDesktopQuery(request) {
  if (!isDesktopEmbedded() || !request) {
    return false;
  }
  try {
    window.cefQuery({
      request,
      persistent: false,
      onSuccess: () => {},
      onFailure: () => {},
    });
    return true;
  } catch (e) {
    return false;
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
 * 账号删除联动：彻底销毁账号专属的 CefRequestContext 并删除磁盘 Profile 目录。
 * 与 closeDesktopExternal 区别：close 仅 release（保留 ctx 给下次绑定/发布复用）；
 * purge 是终态销毁，仅在 wldos-ui 删除账号成功后调用，fire-and-forget。
 * Web 环境无桥时直接 resolve（无副作用）。
 */
export function purgeDesktopExternalAccount(accountId) {
  const id = String(accountId || '').trim();
  if (!id) return Promise.resolve('');
  return sendDesktopQuery(`wldos:external:purge:${encodeURIComponent(id)}`);
}

/** 将外部浏览器会话从旧 channel（如 pending-*）迁到新 channel，同一 CefRequestContext，保留 Cookie */
export function migrateDesktopExternalChannel(fromChannel, toChannel) {
  const from = encodeURIComponent(String(fromChannel || '').trim());
  const to = encodeURIComponent(String(toChannel || '').trim());
  if (!from || !to) return false;
  return sendDesktopQuery(`wldos:external:migrate:${from}:${to}`);
}

/**
 * 与 {@link migrateDesktopExternalChannel} 相同语义，但等待 cefQuery 完成后再 resolve。
 * 壳端 migrate 在 EDT 同步执行后才会 callback.success，用于绑定写库后立即 show/mount，避免竞态白屏。
 */
export function migrateDesktopExternalChannelAsync(fromChannel, toChannel) {
  const from = encodeURIComponent(String(fromChannel || '').trim());
  const to = encodeURIComponent(String(toChannel || '').trim());
  if (!from || !to) {
    return Promise.resolve(false);
  }
  if (!isDesktopEmbedded()) {
    return Promise.resolve(false);
  }
  return new Promise((resolve, reject) => {
    try {
      window.cefQuery({
        request: `wldos:external:migrate:${from}:${to}`,
        persistent: false,
        onSuccess: () => resolve(true),
        onFailure: (code, msg) => reject(new Error(msg || String(code))),
      });
    } catch (e) {
      reject(e);
    }
  });
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

export function unmountDesktopExternalRect() {
  return sendDesktopQuery('wldos:external:unmount');
}

/**
 * 主窗内打开 Ant 模态/重要弹层时，临时隐藏 JCEF 外部站点叠层，避免原生层挡住 message/Modal（不断开登录会话）。
 * @param {boolean} suppressed true=隐藏叠层，false=按当前 mount 区域恢复
 */
export function setDesktopExternalModalSuppressed(suppressed) {
  return sendDesktopQuery(
    `wldos:external:modal-suppress:${suppressed ? '1' : '0'}`,
  );
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
