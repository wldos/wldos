/**
 * UMD 插件加载器
 * 提供脚本 Promise 缓存、加载超时、失败清理、版本指纹、依赖注入等能力
 */

import request from '@/utils/request';
import config from '@/utils/config';
import { loadPluginManifest } from '@/services/pluginManifest';
import { reportPluginLoadMetric } from '@/services/metrics';

const { prefix } = config;

// 脚本 Promise 缓存：Map<key, Promise>
const scriptCache = new Map();

// 配置常量
const DEFAULT_TIMEOUT = 15000; // 15秒超时
const LOAD_RETRY_COUNT = 1; // 重试次数

// 埋点数据收集（本地缓存，支持批量上报）
const metrics = {
  uiInfoTime: [],
  scriptTime: [],
  parseErrors: [],
  firstPaintTime: []
};

// 埋点批量上报间隔（毫秒），默认30秒上报一次
const REPORT_INTERVAL = 30000;
let reportTimer = null;

/**
 * 批量上报埋点数据（定时上报）
 */
async function batchReportMetrics() {
  const metricsData = getMetrics();

  // 如果没有任何埋点数据，跳过上报
  if (
    metricsData.uiInfoTime.length === 0 &&
    metricsData.scriptTime.length === 0 &&
    metricsData.parseErrors.length === 0 &&
    metricsData.firstPaintTime.length === 0
  ) {
    return;
  }

  try {
    const { reportUmdMetrics } = await import('@/services/metrics');
    await reportUmdMetrics(metricsData);

    // 上报成功后清空本地缓存
    clearMetrics();

    if (process.env.NODE_ENV === 'development') {
      console.log('[UMDLoader] 批量上报埋点数据成功');
    }
  } catch (err) {
    // 静默失败，不打印错误（避免上报失败影响用户体验）
    if (process.env.NODE_ENV === 'development') {
      console.warn('[UMDLoader] 批量上报埋点数据失败', err);
    }
  }
}

/**
 * 启动定时批量上报
 */
function startBatchReport() {
  if (reportTimer) {
    return; // 已启动，不重复启动
  }

  reportTimer = setInterval(batchReportMetrics, REPORT_INTERVAL);

  // 页面卸载时上报并清理
  if (typeof window !== 'undefined') {
    window.addEventListener('beforeunload', () => {
      batchReportMetrics();
      if (reportTimer) {
        clearInterval(reportTimer);
        reportTimer = null;
      }
    });
  }
}

// 自动启动批量上报
if (typeof window !== 'undefined') {
  startBatchReport();
}


/**
 * 初始化运行时 API（仅初始化一次）
 */
function initRuntime() {
  if (!window.wldos) {
    window.wldos = {};
  }

  if (!window.wldos.runtime) {
    window.wldos.runtime = {
      _m: {}, // 内部存储 Map<name, object>

      /**
       * 提供对象/函数到运行时
       * @param {string} name - 名称
       * @param {any} obj - 对象或函数
       */
      provide(name, obj) {
        this._m[name] = obj;
        if (process.env.NODE_ENV === 'development') {
          console.log(`[Runtime] 已提供: ${name}`);
        }
      },

      /**
       * 请求对象/函数
       * @param {string} name - 名称
       * @returns {any|null} 对象或函数，不存在返回 null
       */
      request(name) {
        return this._m[name] || null;
      },

      /**
       * 确保资源存在（根据 manifest 检查和注入依赖）
       * 如果依赖缺失，自动尝试从 CDN 或本地资源加载
       * @param {object} requiredComponents - 需要的组件，如 { "antd": ["Button", "Table"] }
       * @param {string[]} requiredAPIs - 需要的 API，如 ["http", "i18n"]
       * @param {boolean} autoLoad - 是否自动加载缺失的依赖（默认 true）
       * @param {object} providedComponents - 插件自身提供的依赖（跳过外部检查），如 { "lodash": [] }
       * @returns {Promise<object>} 提供的资源对象
       */
      async ensureResources(requiredComponents = {}, requiredAPIs = [], autoLoad = true, providedComponents = {}) {
        const resources = {};
        const missing = [];

        // 1. 检查并注入 antd 组件
        if (requiredComponents.antd && Array.isArray(requiredComponents.antd)) {
          if (!window.antd) {
            missing.push('antd (全局对象不存在)');
          } else {
            // antd 已通过 externals 在主应用中加载，直接使用
            resources.antd = window.antd;

            // 验证需要的组件是否存在
            const missingComponents = requiredComponents.antd.filter(compName => {
              return !window.antd[compName] && !window.antd[compName.toLowerCase()];
            });

            if (missingComponents.length > 0) {
              console.warn(`[Runtime] antd 组件缺失: ${missingComponents.join(', ')}`);
              // 注意：这里不报错，因为可能通过其他方式加载（如按需导入）
            }
          }
        }

        // 2. 检查并注入其他 UI 库组件（如 umi、lodash、moment、echarts 等）
        Object.keys(requiredComponents).forEach(libName => {
          if (libName === 'antd') return; // antd 已处理

          const components = requiredComponents[libName];
          if (Array.isArray(components)) {
            // 如果依赖是插件自身提供的，跳过外部检查（延迟到插件加载后）
            if (providedComponents && providedComponents[libName]) {
              if (process.env.NODE_ENV === 'development') {
                console.log(`[Runtime] 依赖 ${libName} 由插件自身提供，跳过外部检查`);
              }
              // 暂时跳过，插件加载后会自动注册
              return;
            }

            // 尝试从全局对象获取
            let lib = window[libName];

            // 检查是否是插件自身提供的依赖（插件加载后注册）
            if (!lib && this.getProvidedDependency) {
              lib = this.getProvidedDependency(libName);
            }

            if (!lib) {
              missing.push(`${libName} (全局对象不存在)`);
            } else {
              resources[libName] = lib;
            }
          }
        });

        // 3. 检查并注入 API
        requiredAPIs.forEach(apiName => {
          const api = this.request(apiName);
          if (!api) {
            // API 不能自动加载，必须由主应用提供
            missing.push(`${apiName} (API未注册)`);
          } else {
            resources[apiName] = api;
          }
        });

        // 4. 如果仍有缺失的关键依赖，抛出错误
        if (missing.length > 0) {
          const error = new Error(`插件依赖缺失: ${missing.join(', ')}`);
          error.missing = missing;
          throw error;
        }

        if (process.env.NODE_ENV === 'development') {
          console.log('[Runtime] 依赖检查通过:', {
            components: Object.keys(requiredComponents),
            apis: requiredAPIs,
            resources: Object.keys(resources)
          });
        }

        return resources;
      }
    };

    // 初始化时注入常用 API
    window.wldos.runtime.provide('http', request);

    // 插件自身提供的依赖注册表（运行时动态注册）
    // 格式：{ libName: { value: any, provider: string, version?: string } }
    window.wldos.runtime._providedDeps = window.wldos.runtime._providedDeps || {};

    // 依赖提供者追踪：记录每个依赖被哪些插件使用
    // 格式：{ libName: Set<pluginCode> }
    window.wldos.runtime._dependencyProviders = window.wldos.runtime._dependencyProviders || {};

    /**
     * 注册插件自身提供的依赖（由插件在加载后调用）
     * @param {string} libName - 库名称
     * @param {any} libValue - 库的值（可以是全局对象或模块对象）
     * @param {string} pluginCode - 插件编码（用于追踪）
     * @param {string} version - 依赖版本（可选，用于冲突检测）
     * @returns {object} { success: boolean, conflict?: boolean, message?: string }
     */
    window.wldos.runtime.registerProvidedDependency = function(libName, libValue, pluginCode = 'unknown', version = null) {
      const existingDep = this._providedDeps[libName];
      let conflict = false;
      let message = '';

      // 如果依赖已经存在（被其他插件提供），检测冲突
      if (existingDep) {
        // 比较版本（如果提供了版本信息）
        if (version && existingDep.version && version !== existingDep.version) {
          conflict = true;
          message = `依赖冲突: ${libName} 已由插件 ${existingDep.provider} (v${existingDep.version}) 提供，插件 ${pluginCode} 尝试注册 v${version}`;
          console.warn(`[Runtime] ${message}`);

          // 不覆盖已有依赖，但记录冲突
          if (!this._dependencyProviders[libName]) {
            this._dependencyProviders[libName] = new Set();
          }
          this._dependencyProviders[libName].add(pluginCode);
          this._dependencyProviders[libName].add(existingDep.provider);

          return { success: false, conflict: true, message };
        } else {
          // 版本相同或未提供版本信息，共享依赖（不覆盖）
          message = `依赖共享: ${libName} 已由插件 ${existingDep.provider} 提供，插件 ${pluginCode} 将复用该依赖`;
          if (process.env.NODE_ENV === 'development') {
            console.log(`[Runtime] ${message}`);
          }

          // 记录依赖提供者
          if (!this._dependencyProviders[libName]) {
            this._dependencyProviders[libName] = new Set();
          }
          this._dependencyProviders[libName].add(pluginCode);
          this._dependencyProviders[libName].add(existingDep.provider);

          return { success: true, conflict: false, message, shared: true };
        }
      }

      // 新依赖，注册
      this._providedDeps[libName] = {
        value: libValue,
        provider: pluginCode,
        version: version || null
      };

      // 如果是全局对象，也挂载到 window（兼容性）
      if (typeof libName === 'string' && libValue) {
        const globalVar = libName;

        // 如果全局变量已存在，检测冲突
        if (window[globalVar] && window[globalVar] !== libValue) {
          conflict = true;
          message = `全局变量冲突: ${globalVar} 已被其他代码设置，插件 ${pluginCode} 尝试注册 ${libName}`;
          console.warn(`[Runtime] ${message}`);
          // 不覆盖，但记录警告
        } else if (!window[globalVar]) {
          // 只有在全局变量不存在时才挂载
          window[globalVar] = libValue;
        }
      }

      // 记录依赖提供者
      if (!this._dependencyProviders[libName]) {
        this._dependencyProviders[libName] = new Set();
      }
      this._dependencyProviders[libName].add(pluginCode);

      if (process.env.NODE_ENV === 'development') {
        console.log(`[Runtime] 已注册插件提供的依赖: ${libName} (由插件 ${pluginCode} 提供)`);
      }

      return { success: true, conflict: false, message: `依赖 ${libName} 已注册` };
    };

    /**
     * 获取插件提供的依赖
     * @param {string} libName - 库名称
     * @param {string} pluginCode - 插件编码（可选，用于统计）
     * @returns {any|null}
     */
    window.wldos.runtime.getProvidedDependency = function(libName, pluginCode = null) {
      const dep = this._providedDeps[libName];
      if (dep) {
        // 记录使用统计（可选）
        if (pluginCode && this._dependencyProviders[libName]) {
          this._dependencyProviders[libName].add(pluginCode);
        }
        return dep.value;
      }
      return null;
    };

    /**
     * 获取依赖提供者信息
     * @param {string} libName - 库名称
     * @returns {object|null} { provider: string, version?: string, users: string[] }
     */
    window.wldos.runtime.getDependencyInfo = function(libName) {
      const dep = this._providedDeps[libName];
      if (!dep) {
        return null;
      }

      return {
        provider: dep.provider,
        version: dep.version || null,
        users: Array.from(this._dependencyProviders[libName] || [])
      };
    };

    if (process.env.NODE_ENV === 'development') {
      console.log('[UMDLoader] 运行时 API 已初始化');
    }
  }

  return window.wldos.runtime;
}

/**
 * 确保 request 工具已注入到全局作用域（仅注入一次）
 */
export function ensureRequestInjected() {
  // 先初始化运行时
  initRuntime();

  // 确保 request 已注入（兼容旧代码）
  if (!window.request) {
    window.request = request;
    if (process.env.NODE_ENV === 'development') {
      console.log('[UMDLoader] request 工具已注入到全局作用域');
    }
  }
}

/**
 * 生成脚本缓存 key
 * @param {string} code - 插件编码
 * @param {string} entryPoint - 入口文件名
 * @param {string} version - 版本号或hash
 * @returns {string}
 */
function getCacheKey(code, entryPoint, version) {
  return `${code}::${entryPoint}::${version || 'unknown'}`;
}

/**
 * 构建带版本指纹的脚本 URL
 * @param {string} code - 插件编码
 * @param {string} entryPoint - 入口文件名
 * @param {string} version - 版本号或hash
 * @returns {string}
 */
function buildScriptUrl(code, entryPoint, version) {
  // 同部署方案路径：/plugin-assets/{code}/{version}/{entry}
  if (version) {
    return `/plugin-assets/${code}/${version}/${entryPoint}`;
  }
  // 如果没有版本，使用旧格式（向后兼容）
  return `/plugin-assets/${code}/entry/${entryPoint}`;
}

/**
 * 加载脚本内容（带超时）
 * @param {string} url - 脚本 URL
 * @param {number} timeout - 超时时间（毫秒）
 * @returns {Promise<string>}
 */
function loadScriptContent(url, timeout = DEFAULT_TIMEOUT) {
  return new Promise((resolve, reject) => {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
      controller.abort();
      reject(new Error(`脚本加载超时 (${timeout}ms): ${url}`));
    }, timeout);

    fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/javascript, text/javascript, */*',
      },
      signal: controller.signal,
    })
      .then(async (response) => {
        clearTimeout(timeoutId);
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        const content = await response.text();
        resolve(content);
      })
      .catch((error) => {
        clearTimeout(timeoutId);
        if (error.name === 'AbortError') {
          reject(new Error(`脚本加载超时 (${timeout}ms): ${url}`));
        } else {
          reject(error);
        }
      });
  });
}

/**
 * 执行脚本内容并注入依赖
 * @param {string} scriptContent - 脚本内容
 * @param {string} code - 插件编码（用于自动注册到注册表）
 * @returns {Promise<object>} 返回插件模块对象
 */
/**
 * 从 UMD 代码中解析依赖名称（webpack externals）
 * UMD 包装器格式：function(e, t) { ... require("React"), require("antd") ... }
 * 或者：define(["React", "antd"], function(React, antd) { ... })
 */
function extractDependenciesFromScript(scriptContent) {
  const dependencies = new Set();

  // 模式1: UMD 包装器中的 require 调用
  // 匹配: require("React"), require("antd"), require('lodash') 等
  const requirePattern = /require\s*\(\s*["']([^"']+)["']\s*\)/g;
  let match;
  while ((match = requirePattern.exec(scriptContent)) !== null) {
    const depName = match[1];
    if (depName && !depName.startsWith('.') && !depName.startsWith('/')) {
      dependencies.add(depName);
    }
  }

  // 模式2: AMD define 中的依赖数组
  // 匹配: define(["React", "antd"], function(...) { ... })
  const definePattern = /define\s*\(\s*\[([^\]]+)\]/g;
  while ((match = definePattern.exec(scriptContent)) !== null) {
    const depsStr = match[1];
    // 提取字符串中的依赖名
    const depNames = depsStr.match(/["']([^"']+)["']/g);
    if (depNames) {
      depNames.forEach(dep => {
        const depName = dep.replace(/["']/g, '');
        if (depName && !depName.startsWith('.') && !depName.startsWith('/')) {
          dependencies.add(depName);
        }
      });
    }
  }

  // 模式3: UMD 包装器参数
  // 匹配: !function(e,t){...}(this,(e,t)=>(()=>{...}))(this,require("React"),require("antd"))
  // 实际上更常见的是在函数签名中看到依赖
  const umdPattern = /\(this,\s*\((?:[^)]*,\s*)*\)\s*=>/;
  // 这个模式比较复杂，暂时不处理

  return Array.from(dependencies);
}

/**
 * 检查主应用是否提供了依赖
 *
 * 核心原则（与后端一致）：
 * - 如果主应用已提供依赖（如 antd、React），插件使用主应用的（避免重复和冲突）
 * - 如果主应用未提供，插件自行维护和加载（类似后端插件自带某些依赖）
 *
 * 这类似于后端插件依赖主应用的 Spring Boot 容器，而不是创建自己的容器
 */
function checkMainAppProvidesDependency(depName) {
  const globalVar = getGlobalVarName(depName);

  // 检查全局变量是否存在（主应用是否提供了该依赖）
  if (window[globalVar]) {
    // 检查是否为主应用提供的（通常是对象且有完整功能）
    const dep = window[globalVar];
    if (typeof dep === 'object' && dep !== null) {
      // antd: 检查是否有常见组件
      if (depName.toLowerCase() === 'antd') {
        return dep.Button && dep.Card && dep.Typography;
      }
      // React: 检查是否有 createElement
      if (depName.toLowerCase() === 'react') {
        return typeof dep.createElement === 'function';
      }
      // ReactDOM: 检查是否有 render
      if (depName.toLowerCase() === 'react-dom') {
        return typeof dep.render === 'function';
      }
      // 其他库：只要存在就认为可用（主应用已提供）
      return true;
    }
    // 函数类型的依赖（如 moment）
    if (typeof dep === 'function') {
      return true;
    }
  }

  // 主应用未提供该依赖，插件需要自行加载
  return false;
}

/**
 * 通用的运行时依赖确保机制
 *
 * 核心原则（与后端一致）：
 * 1. 优先使用主应用提供的依赖（如果前端已有，就用前端的，避免重复和冲突）
 * 2. 如果主应用未提供，插件自行维护和加载（类似后端插件自带某些依赖）
 *
 * 这类似于后端插件依赖主应用的 Spring Boot，而不是创建自己的容器
 *
 * @param {Array<Object|string>} dependencies - 依赖声明列表
 *   - 新格式（推荐）：[{ name: 'antd', expectFromMainApp: true, required: true, components: [...] }]
 *   - 旧格式（兼容）：['React', 'antd', 'lodash'] - 字符串数组，默认 expectFromMainApp=true
 */
async function ensureRuntimeDependencies(dependencies = []) {
  if (!dependencies || dependencies.length === 0) {
    return;
  }

  const runtime = initRuntime();
  const requiredComponents = {};
  const missingDeps = [];

  // 检查每个依赖：根据 expectFromMainApp 决定策略
  for (const dep of dependencies) {
    // 支持新格式（对象）和旧格式（字符串）
    const depInfo = typeof dep === 'string'
      ? { name: dep, expectFromMainApp: true, required: true } // 旧格式，默认期望主应用提供
      : dep; // 新格式，使用声明

    const depName = depInfo.name;
    const expectFromMainApp = depInfo.expectFromMainApp !== false; // 默认 true
    const required = depInfo.required !== false; // 默认 true
    const components = depInfo.components || [];

    // 如果 expectFromMainApp=false，插件自行维护，不检查主应用
    if (!expectFromMainApp) {
      // 插件自行维护，直接加载（不检查主应用）
      missingDeps.push(depName);
      if (DEPENDENCY_CONFIG[depName]) {
        requiredComponents[depName] = components.length > 0 ? components : [];
      }
      if (process.env.NODE_ENV === 'development') {
        console.log(`[UMDLoader] 插件自行维护依赖: ${depName}（不检查主应用）`);
      }
      continue;
    }

    // expectFromMainApp=true：优先使用主应用提供的
    // 1. 检查主应用是否提供了该依赖（类似后端检查主应用的 Spring Boot）
    if (checkMainAppProvidesDependency(depName)) {
      // 主应用已提供，插件使用主应用的（避免重复和冲突）
      if (process.env.NODE_ENV === 'development') {
        console.log(`[UMDLoader] 使用主应用提供的依赖: ${depName}（避免重复加载）`);
      }
      continue; // 跳过，使用主应用的依赖
    }

    // 2. 主应用未提供，插件需要自行维护和加载（类似后端插件自带某些依赖）
    missingDeps.push(depName);

    // 将依赖名称映射到 requiredComponents 格式
    if (DEPENDENCY_CONFIG[depName]) {
      const components = getComponentsForDependency(depName);
      if (components !== null) {
        // null 表示不需要组件列表（如 lodash、moment）
        requiredComponents[depName] = components;
      } else {
        // 空数组表示加载整个库（对于需要组件列表的库，如 antd）
        requiredComponents[depName] = [];
      }
    } else {
      // 没有配置的依赖，可能是自定义的或不需要加载的
      if (process.env.NODE_ENV === 'development') {
        console.warn(`[UMDLoader] 依赖 ${depName} 没有配置，跳过自动加载`);
      }
    }
  }

  // 3. 自动加载缺失的依赖（主应用未提供时，插件自行维护和加载）
  if (Object.keys(requiredComponents).length > 0) {
    try {
      await runtime.ensureResources(requiredComponents, [], true, {});
      if (process.env.NODE_ENV === 'development') {
        console.log(`[UMDLoader] 插件自行加载依赖（主应用未提供）: ${missingDeps.join(', ')}`);
      }
    } catch (error) {
      console.warn(`[UMDLoader] 插件依赖自动加载失败:`, error);
      // 不阻止插件加载，让插件自己处理依赖缺失
      // 如果加载失败，插件代码中应该有 fallback 处理
    }
  } else if (missingDeps.length > 0) {
    // 有缺失的依赖但没有配置，可能是插件应该自行处理（或依赖未在 DEPENDENCY_CONFIG 中配置）
    if (process.env.NODE_ENV === 'development') {
      console.warn(`[UMDLoader] 以下依赖缺失但未配置自动加载（插件需要自行处理）: ${missingDeps.join(', ')}`);
    }
  }

  // 特殊处理：确保 React 在全局作用域（如果 ReactDOM 存在）
  const hasReactDep = dependencies.some(d => {
    const depName = typeof d === 'string' ? d : d.name;
    return depName && depName.toLowerCase() === 'react';
  });
  if (hasReactDep && typeof window.React === 'undefined' && typeof window.ReactDOM !== 'undefined') {
    try {
      if (window.ReactDOM.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED) {
        window.React = window.ReactDOM.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner;
      }
      if (typeof window.React === 'undefined' && typeof React !== 'undefined') {
        window.React = React;
      }
    } catch (e) {
      // 忽略错误
    }
  }
}

/**
 * 获取依赖的全局变量名称
 */
function getGlobalVarName(depName) {
  // 常见的映射关系
  const mappings = {
    'react': 'React',
    'react-dom': 'ReactDOM',
    'lodash': '_',
    'moment': 'moment',
    'echarts': 'echarts',
  };

  return mappings[depName.toLowerCase()] || depName;
}

/**
 * 获取依赖需要的组件列表（如果有的话）
 * 某些库（如 antd）需要指定组件列表，其他库（如 lodash、moment）不需要
 */
function getComponentsForDependency(depName) {
  // 不再使用 CDN 配置，返回 null 表示不需要组件列表
  return null;
}

function executeScript(scriptContent, code, dependencies = null) {
  return new Promise(async (resolve, reject) => {
    try {
      // 1. 如果未提供依赖信息，尝试从代码中解析（兜底机制）
      let finalDependencies = dependencies;
      if (!finalDependencies || finalDependencies.length === 0) {
        // 从代码中解析到的只是依赖名称（字符串数组），转换为新格式
        const parsedDeps = extractDependenciesFromScript(scriptContent);
        if (parsedDeps && parsedDeps.length > 0) {
          finalDependencies = parsedDeps.map(name => ({
            name: name,
            expectFromMainApp: true, // 解析到的依赖默认期望主应用提供
            required: true
          }));
          if (process.env.NODE_ENV === 'development') {
            console.log(`[UMDLoader] 从代码中解析到依赖（默认期望主应用提供）: ${parsedDeps.join(', ')}`);
          }
        }
      }

      // 2. 确保运行时依赖存在（用于 webpack externals）
      // 根据依赖声明中的 expectFromMainApp 决定策略
      if (finalDependencies && finalDependencies.length > 0) {
        await ensureRuntimeDependencies(finalDependencies);
      }

      // 3. 确保依赖已注入
      ensureRequestInjected();

      // 注入依赖的包装代码
      const injectedScript = `
        // 注入依赖 - 确保React在全局作用域中可用
        if (typeof window.React === 'undefined' && typeof window.ReactDOM !== 'undefined') {
          if (window.ReactDOM.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED) {
            window.React = window.ReactDOM.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner;
          }
        }

        // 执行插件代码
        ${scriptContent}

        // 插件加载后，自动注册插件自身提供的依赖到 runtime
        if (window.wldos && window.wldos.runtime && typeof window.wldos.runtime.registerProvidedDependency === 'function') {
          try {
            // 插件可以主动调用 runtime.registerProvidedDependency 注册依赖
            // 这里提供一个便捷的自动发现机制（可选）
            // 插件也可以通过 pluginConfig.providedDependencies 声明
          } catch (err) {
            console.warn('[UMDLoader] 插件依赖自动注册失败:', err);
          }
        }
      `;

      // 创建 script 标签并执行
      const script = document.createElement('script');
      script.textContent = injectedScript;

      script.onerror = (error) => {
        if (document.head.contains(script)) {
          document.head.removeChild(script);
        }
        reject(new Error(`脚本执行失败: ${error.message || '未知错误'}`));
      };

      // 延迟执行以确保注入完成
      setTimeout(() => {
        document.head.appendChild(script);
        // 等待脚本执行完成，然后尝试获取并注册模块
        setTimeout(() => {
          try {
            // 尝试从 window 获取插件模块
            const pluginModule = window[code] ||
                                 window[`plugin_${code}`] ||
                                 window[`${code}Plugin`] ||
                                 window[code.toUpperCase()];

            // 如果找到模块且注册表可用，自动注册
            if (pluginModule && window.WLDOSPlugins && !window.WLDOSPlugins.has(code)) {
              window.WLDOSPlugins.register(code, pluginModule);
              if (process.env.NODE_ENV === 'development') {
                console.log(`[UMDLoader] 插件 ${code} 已自动注册到注册表`);
              }
            }

            // 如果插件提供了依赖（通过 pluginConfig.providedDependencies 或其他方式），注册到 runtime
            if (pluginModule && pluginModule.pluginConfig && pluginModule.pluginConfig.providedDependencies) {
              const runtime = initRuntime();
              if (runtime.registerProvidedDependency) {
                const pluginVersion = pluginModule.pluginConfig.version || null;

                Object.keys(pluginModule.pluginConfig.providedDependencies).forEach(libName => {
                  const depInfo = pluginModule.pluginConfig.providedDependencies[libName];

                  // 支持两种格式：
                  // 1. 简单格式：providedDependencies: { 'lodash': lodash }
                  // 2. 详细格式：providedDependencies: { 'lodash': { value: lodash, version: '4.17.21' } }
                  let libValue = null;
                  let libVersion = null;

                  if (typeof depInfo === 'object' && depInfo !== null && depInfo.value !== undefined) {
                    libValue = depInfo.value;
                    libVersion = depInfo.version || null;
                  } else {
                    libValue = depInfo;
                    libVersion = null;
                  }

                  if (libValue) {
                    const result = runtime.registerProvidedDependency(libName, libValue, code, libVersion);

                    if (process.env.NODE_ENV === 'development') {
                      if (result.conflict) {
                        console.warn(`[UMDLoader] 插件 ${code} 注册依赖 ${libName} 时发生冲突:`, result.message);
                      } else if (result.shared) {
                        console.log(`[UMDLoader] 插件 ${code} 将共享已存在的依赖 ${libName}:`, result.message);
                      } else {
                        console.log(`[UMDLoader] 插件 ${code} 已注册自身提供的依赖: ${libName}`);
                      }
                    }
                  }
                });
              }
            }

            resolve(pluginModule || {});
          } catch (err) {
            console.warn(`[UMDLoader] 获取插件模块失败: ${code}`, err);
            resolve({});
          }
        }, 50);
      }, 0);
    } catch (error) {
      reject(error);
    }
  });
}

/**
 * 加载 UMD 插件脚本（带缓存、超时、失败清理）
 * @param {string} code - 插件编码
 * @param {string} entryPoint - 入口文件名（可选，从 uiInfo 获取）
 * @param {object} options - 选项
 * @param {number} options.timeout - 超时时间（毫秒），默认 15000
 * @param {boolean} options.useCache - 是否使用缓存，默认 true
 * @returns {Promise<object>} 返回 { uiInfo, scriptContent, version }
 */
export async function loadUMDScript(code, entryPoint = null, options = {}) {
  const {
    timeout = DEFAULT_TIMEOUT,
    useCache = true,
  } = options;

  const startTime = performance.now();

  try {
    // 1. 从 manifest 获取插件信息（同部署方案）
    const uiInfoStartTime = performance.now();
    const manifest = await loadPluginManifest();
    const pluginInfo = manifest?.plugins?.[code];
    const uiInfoTime = performance.now() - uiInfoStartTime;
    metrics.uiInfoTime.push({ code, time: uiInfoTime });

    if (!pluginInfo) {
      throw new Error(`插件 ${code} 未在 manifest 中找到，可能未安装`);
    }

    const resolvedEntryPoint = entryPoint || pluginInfo.entry || 'index.js';
    const version = pluginInfo.version;
    const moduleFormat = pluginInfo.moduleFormat || 'umd';

    // 从 manifest 中提取依赖信息（如果存在）
    const requiredComponents = pluginInfo.requiredComponents || {};
    const requiredAPIs = pluginInfo.requiredAPIs || [];
    // 插件自身提供的依赖（不需要外部加载）
    const providedComponents = pluginInfo.providedComponents || {};

    if (!version) {
      throw new Error('插件版本未指定');
    }

    if (!resolvedEntryPoint) {
      throw new Error('插件入口文件未指定');
    }

    // 2. 确保依赖存在（如果 manifest 声明了依赖）
    // 注意：providedComponents 中的依赖会跳过外部检查，延迟到插件加载后验证
    if ((requiredComponents && Object.keys(requiredComponents).length > 0) || requiredAPIs.length > 0) {
      const runtime = initRuntime();
      try {
        await runtime.ensureResources(requiredComponents, requiredAPIs, true, providedComponents);
        if (process.env.NODE_ENV === 'development') {
          console.log(`[UMDLoader] 插件 ${code} 依赖检查通过`);
          if (providedComponents && Object.keys(providedComponents).length > 0) {
            console.log(`[UMDLoader] 插件 ${code} 自身提供依赖:`, Object.keys(providedComponents));
          }
        }
      } catch (error) {
        // 依赖缺失，抛出错误（阻止插件加载）
        console.error(`[UMDLoader] 插件 ${code} 依赖检查失败:`, error);
        throw new Error(`插件依赖缺失，无法加载: ${error.message}`);
      }
    }

    // 3. 构建缓存 key
    const cacheKey = getCacheKey(code, resolvedEntryPoint, version);

    // 4. 检查缓存（依赖检查通过后）
    if (useCache && scriptCache.has(cacheKey)) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`[UMDLoader] 使用缓存加载插件: ${code}`);
      }
      const cachedResult = await scriptCache.get(cacheKey);
      return cachedResult;
    }

    // 5. 构建脚本 URL（带版本指纹）
    const scriptUrl = buildScriptUrl(code, resolvedEntryPoint, version);

    // 6. 加载脚本（带超时和重试）
    const scriptStartTime = performance.now();
    let scriptContent = null;
    let lastError = null;

    for (let attempt = 0; attempt <= LOAD_RETRY_COUNT; attempt++) {
      try {
        if (attempt > 0) {
          console.log(`[UMDLoader] 重试加载插件脚本 (${attempt}/${LOAD_RETRY_COUNT}): ${code}`);
        }
        scriptContent = await loadScriptContent(scriptUrl, timeout);
        break; // 成功则跳出重试循环
      } catch (error) {
        lastError = error;
        if (attempt === LOAD_RETRY_COUNT) {
          // 最后一次重试失败，清理缓存
          scriptCache.delete(cacheKey);
          throw error;
        }
      }
    }

    const scriptTime = performance.now() - scriptStartTime;
    metrics.scriptTime.push({ code, time: scriptTime, url: scriptUrl });

    // 7. 从 manifest/uiInfo 中提取前端依赖声明（plugin.yml 中的 dependencies.frontend）
    // 支持两种格式：
    // 1. 旧格式：requiredComponents（兼容）
    // 2. 新格式：dependencies.frontend（推荐，明确声明期望主应用提供还是自行维护）
    let frontendDependencies = uiInfo.dependencies?.frontend || uiInfo.data?.dependencies?.frontend || null;

    // 如果没有新格式，尝试从旧格式转换
    if (!frontendDependencies && requiredComponents && Object.keys(requiredComponents).length > 0) {
      // 将旧格式转换为新格式（默认 expectFromMainApp=true）
      frontendDependencies = Object.keys(requiredComponents).map(name => ({
        name: name,
        expectFromMainApp: true, // 默认期望主应用提供
        required: true,
        components: requiredComponents[name] || []
      }));
    }

    // 8. 执行脚本并注入依赖（自动注册到注册表）
    // 传递依赖声明信息，executeScript 会根据 expectFromMainApp 决定策略
    const pluginModule = await executeScript(scriptContent, code, frontendDependencies);

    const totalTime = performance.now() - startTime;

    // 实时上报成功埋点（静默上报）
    reportPluginLoadMetric(code, {
      type: 'success',
      code,
      uiInfoTime,
      scriptTime,
      totalTime,
      version,
      entryPoint: resolvedEntryPoint,
      timestamp: Date.now(),
    }).catch(err => {
      // 静默失败
    });

    const result = {
      uiInfo,
      scriptContent,
      version,
      entryPoint: resolvedEntryPoint,
      metrics: {
        uiInfoTime,
        scriptTime,
        totalTime,
      },
    };

    // 8. 缓存结果（仅成功时）
    if (useCache) {
      scriptCache.set(cacheKey, Promise.resolve(result));
    }

    // 控制台去噪：仅在开发环境打印详细日志
    if (process.env.NODE_ENV === 'development') {
      console.log(`[UMDLoader] 插件加载成功: ${code} (${totalTime.toFixed(2)}ms)`);
    }

    return result;
  } catch (error) {
    const totalTime = performance.now() - startTime;

    // 错误分类埋点
    const errorCategory = error.message.includes('超时') ? 'timeout' :
                         error.message.includes('HTTP') ? 'http' :
                         error.message.includes('DNS') ? 'dns' :
                         'unknown';

    // 批量上报的字段名：使用 time（MetricErrorVO 期望）
    const errorMetricForBatch = {
      code,
      error: error.message,
      category: errorCategory,
      time: totalTime, // 批量上报使用 time 字段
    };

    // 单个上报的字段名：使用 totalTime（PluginMetricVO 期望）
    const errorMetricForSingle = {
      code,
      error: error.message,
      category: errorCategory,
      totalTime: totalTime, // 单个上报使用 totalTime 字段
    };

    metrics.parseErrors.push(errorMetricForBatch);

    // 实时上报错误埋点（静默上报，不阻塞）
    reportPluginLoadMetric(code, {
      type: 'error',
      ...errorMetricForSingle,
      timestamp: Date.now(),
    }).catch(err => {
      // 静默失败，不打印错误（避免上报失败影响用户体验）
    });

    // 控制台去噪：仅在开发环境打印详细错误
    if (process.env.NODE_ENV === 'development') {
      console.error(`[UMDLoader] 插件加载失败: ${code}`, error);
    } else {
      // 生产环境仅打印简化错误信息
      console.error(`[UMDLoader] 插件加载失败: ${code} - ${errorCategory}`);
    }

    throw error;
  }
}

/**
 * 获取埋点数据（用于上报）
 * @returns {object}
 */
export function getMetrics() {
  return {
    uiInfoTime: [...metrics.uiInfoTime],
    scriptTime: [...metrics.scriptTime],
    parseErrors: [...metrics.parseErrors],
    firstPaintTime: [...metrics.firstPaintTime],
  };
}

/**
 * 清空埋点数据
 */
export function clearMetrics() {
  metrics.uiInfoTime = [];
  metrics.scriptTime = [];
  metrics.parseErrors = [];
  metrics.firstPaintTime = [];
}

/**
 * 清空脚本缓存（用于强制重新加载）
 * @param {string} code - 插件编码（可选，不传则清空所有）
 */
export function clearScriptCache(code = null) {
  if (code) {
    // 清空指定插件的所有缓存
    const keysToDelete = [];
    scriptCache.forEach((value, key) => {
      if (key.startsWith(`${code}::`)) {
        keysToDelete.push(key);
      }
    });
    keysToDelete.forEach(key => scriptCache.delete(key));
    console.log(`[UMDLoader] 已清空插件 ${code} 的脚本缓存`);
  } else {
    scriptCache.clear();
    console.log('[UMDLoader] 已清空所有脚本缓存');
  }
}

/**
 * 发现组件（优先级：注册表 → Components → default → 根级导出 → window[code]）
 * @param {string} code - 插件编码
 * @param {string} componentName - 组件名称
 * @returns {function|null} 组件函数，不存在返回 null
 */
/**
 * 扩展依赖配置（允许运行时添加新的依赖配置）
 * @param {string} libName - 库名称
 * @param {object} config - 依赖配置 { js: string|string[], css?: string|string[], globalVar?: string, checkFn?: function }
 */
export function extendDependencyConfig(libName, config) {
  if (!libName || !config || !config.js) {
    throw new Error('依赖配置必须包含 libName 和 js 属性');
  }

  DEPENDENCY_CONFIG[libName] = {
    ...config,
    globalVar: config.globalVar || libName,
  };

  if (process.env.NODE_ENV === 'development') {
    console.log(`[UMDLoader] 已扩展依赖配置: ${libName}`);
  }
}

/**
 * 手动加载依赖（允许插件或主应用主动加载依赖）
 * @param {string} libName - 库名称
 * @param {object} config - 依赖配置（可选）
 * @returns {Promise<void>}
 */
export function ensureDependencyLoaded(libName, config = null) {
  return loadDependency(libName, config);
}

export function discoverComponent(code, componentName) {
  // 优先从注册表获取
  if (window.WLDOSPlugins && window.WLDOSPlugins.has(code)) {
    const module = window.WLDOSPlugins.get(code);

    // 调试：输出模块结构和组件查找过程
    if (process.env.NODE_ENV === 'development') {
      const targetInComponents = module && module.Components ? (componentName in module.Components) : false;
      const targetComponent = module && module.Components ? module.Components[componentName] : undefined;
      const targetInModule = module ? (componentName in module) : false;
      const directTarget = module ? module[componentName] : undefined;

      console.log(`[discoverComponent] 查找组件 ${code}.${componentName}:`, {
        hasModule: !!module,
        hasComponents: !!(module && module.Components),
        componentsKeys: module && module.Components ? Object.keys(module.Components) : [],
        moduleKeys: module ? Object.keys(module) : [],
        targetInComponents: targetInComponents,
        targetComponent: targetComponent,
        targetComponentType: typeof targetComponent,
        targetComponentIsUndefined: targetComponent === undefined,
        targetInModule: targetInModule,
        directTarget: directTarget,
        directTargetType: typeof directTarget
      });

      // 如果组件是 undefined，输出更详细的调试信息
      if (targetInComponents && targetComponent === undefined) {
        console.error(`[discoverComponent] 警告：组件 ${componentName} 在 Components 中存在，但值为 undefined！`, {
          Components: module.Components,
          componentName: componentName
        });
      }

      // 调试：检查 antd 是否可用
      if (code === 'umd-demo' && process.env.NODE_ENV === 'development') {
        console.log(`[discoverComponent] umd-demo 调试信息:`, {
          windowAntd: typeof window.antd !== 'undefined' ? '存在' : '不存在',
          windowAntdType: typeof window.antd,
          windowAntdKeys: window.antd ? Object.keys(window.antd).slice(0, 10) : [],
          module: module,
          moduleKeys: Object.keys(module)
        });
      }
    }

    if (module.Components && componentName in module.Components) {
      const comp = module.Components[componentName];
      // 确保返回的是有效的组件（不是 undefined）
      if (comp !== undefined && comp !== null) {
        return comp;
      }
    }
    if (module && componentName in module) {
      const comp = module[componentName];
      if (comp !== undefined && comp !== null) {
        return comp;
      }
    }
    if (module.default && module.default[componentName]) {
      const comp = module.default[componentName];
      if (comp !== undefined && comp !== null) {
        return comp;
      }
    }
    if (module.default && module.default.Component) {
      const comp = module.default.Component;
      if (comp !== undefined && comp !== null) {
        return comp;
      }
    }
    if (module.Component) {
      const comp = module.Component;
      if (comp !== undefined && comp !== null) {
        return comp;
      }
    }
  }

  // 回退到旧 window 路径
  const pluginModule = window[code] ||
                       window[`plugin_${code}`] ||
                       window[`${code}Plugin`] ||
                       window[code.toUpperCase()];

  if (!pluginModule) {
    return null;
  }

  // 尝试多种方式获取组件
  if (pluginModule.Components && pluginModule.Components[componentName]) {
    const comp = pluginModule.Components[componentName];
    return (comp !== undefined && comp !== null) ? comp : null;
  }
  if (pluginModule[componentName]) {
    const comp = pluginModule[componentName];
    return (comp !== undefined && comp !== null) ? comp : null;
  }
  if (pluginModule.Component) {
    const comp = pluginModule.Component;
    return (comp !== undefined && comp !== null) ? comp : null;
  }
  if (pluginModule.default && pluginModule.default[componentName]) {
    const comp = pluginModule.default[componentName];
    return (comp !== undefined && comp !== null) ? comp : null;
  }
  if (pluginModule.default && pluginModule.default.Component) {
    const comp = pluginModule.default.Component;
    return (comp !== undefined && comp !== null) ? comp : null;
  }

  return null;
}

