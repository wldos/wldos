/**
 * 资源路径与请求方法的前端校验器（CreateForm / UpdateForm 共用）。
 *
 * 设计要点：
 * 1. 资源类型分类校验，互不干扰：
 *    - API 类（*_button）：必须是以 / 开头的相对路径，使用 Spring AntPath 语法（{id}、{slug:.+}、/**），不允许外链；要求 requestMethod。
 *    - 菜单类（*_menu / menu）：允许相对路径或外链（http(s)://、协议相对 //host/...），不强制 requestMethod。
 * 2. 通用拦截：
 *    - 禁 ${...}（JS 模板字符串语法，会被 AntPath 当字面字符串而永远 0 命中）；
 *    - 禁空格 / 换行；
 *    - 禁连续 //（外链 https:// 与协议相对 //host 例外）。
 *
 * 与后端 ResourceProbeService 校验保持等价语义；后端是真值匹配（含 Spring 路由扫描），前端只做语法/形态级。
 */

const API_TYPES = new Set([
  'button',
  'admin_button',
  'plugin_button',
  'admin_plugin_button',
]);

const HTTP_METHODS = new Set(['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS']);

const ABS_URL_RE = /^[a-zA-Z][a-zA-Z\d+\-.]*:\/\//; // http://、https://、ftp://...
const PROTOCOL_REL_RE = /^\/\//; // //cdn.xxx.com/...
const TEMPLATE_LITERAL_RE = /\$\{/;
const WHITESPACE_RE = /\s/;
const VAR_SYNTAX_RE = /^[^{}]*(\{[A-Za-z_][\w]*(?::[^{}]+)?\}[^{}]*)*$/;

export const isApiType = (type) => API_TYPES.has(type);

export const isAbsoluteUrl = (v) => ABS_URL_RE.test(v);
export const isProtocolRelative = (v) => PROTOCOL_REL_RE.test(v);

/**
 * 构造资源路径 Form 校验器。
 * @param {() => string} getResourceType 取当前 resourceType 的函数（避免闭包旧值）
 * @param {(key: string, def: string) => string} t  i18n 文案函数 (k, defaultMessage)
 */
export const buildResourcePathValidator = (getResourceType, t) => ({
  validator: (_, raw) => {
    if (raw === undefined || raw === null || raw === '') return Promise.resolve();
    const v = String(raw).trim();
    if (!v) return Promise.resolve();

    // 通用 1：禁 JS 模板字符串
    if (TEMPLATE_LITERAL_RE.test(v)) {
      return Promise.reject(
        t(
          'sys.res.rule.resourcePathInvalid',
          '资源路径疑似使用了 JS 模板字符串写法 ${...}，Spring AntPath 应使用 {变量名}，例如 /admin/agreement/{id}/active',
        ),
      );
    }
    // 通用 2：无空格
    if (WHITESPACE_RE.test(v)) {
      return Promise.reject(t('sys.res.rule.resourcePathNoSpace', '资源路径不能含空格或换行'));
    }
    // 通用 3：变量必须成对、变量名合法（不影响 ** 通配）
    if (!VAR_SYNTAX_RE.test(v)) {
      return Promise.reject(
        t('sys.res.rule.resourcePathVarInvalid', '路径变量请使用 {name} 或 {name:regex}，且不可嵌套大括号'),
      );
    }

    const apiType = isApiType(getResourceType());
    const absUrl = isAbsoluteUrl(v);
    const proRel = isProtocolRelative(v);

    if (apiType) {
      // API 类：禁外链 + 必须 / 开头 + 不允许 //
      if (absUrl || proRel) {
        return Promise.reject(
          t('sys.res.rule.apiPathExternal', 'API 资源不可配置为外链 URL，请填写以 / 开头的相对路径'),
        );
      }
      if (!v.startsWith('/')) {
        return Promise.reject(t('sys.res.rule.apiPathLeadSlash', 'API 资源路径必须以 / 开头'));
      }
      if (/\/\//.test(v)) {
        return Promise.reject(t('sys.res.rule.apiPathDoubleSlash', '路径中不能含连续斜杠 //'));
      }
      return Promise.resolve();
    }

    // 菜单/外链/静态：允许 / 开头 或 绝对/协议相对 URL
    if (v.startsWith('/') || absUrl || proRel) {
      return Promise.resolve();
    }
    return Promise.reject(
      t('sys.res.rule.menuPathInvalid', '请填写以 / 开头的相对路径，或合法的 http(s):// URL'),
    );
  },
});

/**
 * 构造请求方法校验器：仅 API 类强制必填且大写枚举。
 */
export const buildRequestMethodValidator = (getResourceType, t) => ({
  validator: (_, raw) => {
    const apiType = isApiType(getResourceType());
    if (!apiType) {
      return Promise.resolve(); // 菜单类不强制 method
    }
    if (!raw) {
      return Promise.reject(t('sys.res.rule.requestMethodRequired', 'API 资源必须选择请求方法'));
    }
    if (!HTTP_METHODS.has(String(raw))) {
      return Promise.reject(
        t('sys.res.rule.requestMethodEnum', '请求方法仅允许 GET/POST/PUT/DELETE/PATCH，全大写'),
      );
    }
    return Promise.resolve();
  },
});
