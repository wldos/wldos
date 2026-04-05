/**
 * 运行时语言：与 Umi plugin-locale 的 localStorage 键一致；仅允许主站完整维护的语言，
 * 避免浏览器或历史存储落在 pt-BR 等片段 locale 上导致 antd/moment 与页面文案不一致。
 */
export const UMI_LOCALE_STORAGE_KEY = 'umi_locale';

/** 与 src/locales 及业务页面 locales 对齐的完整语言集 */
const ALLOWED_LOCALES = ['zh-CN', 'zh-TW', 'en-US'];

import zhCN from 'antd/es/locale/zh_CN';
import zhTW from 'antd/es/locale/zh_TW';
import enUS from 'antd/es/locale/en_US';

function normalizeLegacyStored(stored) {
  if (!stored || typeof stored !== 'string') return null;
  if (ALLOWED_LOCALES.includes(stored)) return stored;
  const lower = stored.toLowerCase();
  if (lower.startsWith('zh')) {
    if (lower.includes('tw') || lower.includes('hk') || lower.includes('mo')) return 'zh-TW';
    return 'zh-CN';
  }
  return 'en-US';
}

/**
 * 供 app.js `export const locale = { getLocale }` 使用。
 * 优先级：localStorage（合法或可归一化）→ 浏览器语言（zh* → 简中/繁中，否则英文）→ zh-CN。
 */
export function resolveRuntimeLocale() {
  if (typeof window === 'undefined') {
    return 'zh-CN';
  }
  try {
    const raw = window.localStorage.getItem(UMI_LOCALE_STORAGE_KEY);
    const normalized = normalizeLegacyStored(raw);
    if (normalized) {
      if (raw && raw !== normalized) {
        try {
          window.localStorage.setItem(UMI_LOCALE_STORAGE_KEY, normalized);
        } catch (e2) {
          // ignore
        }
      }
      return normalized;
    }
  } catch (e) {
    // ignore
  }
  const nav = ((navigator.languages && navigator.languages[0]) || navigator.language || '').toLowerCase();
  if (nav.startsWith('zh')) {
    if (nav === 'zh-tw' || nav === 'zh-hk' || nav === 'zh-mo') return 'zh-TW';
    return 'zh-CN';
  }
  return 'en-US';
}

export function getAntdLocaleForUmiLocale(umiLocale) {
  if (umiLocale === 'zh-TW') return zhTW;
  if (umiLocale && String(umiLocale).toLowerCase().startsWith('zh')) return zhCN;
  return enUS;
}

/** 与 resolveRuntimeLocale 结果对应的 moment 语言代码 */
export function getMomentLocaleTag(umiLocale) {
  if (umiLocale === 'zh-TW') return 'zh-tw';
  if (umiLocale && String(umiLocale).toLowerCase().startsWith('zh')) return 'zh-cn';
  return 'en';
}
