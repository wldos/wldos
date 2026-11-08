/**
 * 扫描 Umi 路由与页面 service.js，生成「页面 → 实际调用 API」清单。
 * 产物供资源管理「检查遗漏 API」使用。
 */
const fs = require('fs');
const path = require('path');

const root = path.join(__dirname, '..');
const routesPath = path.join(root, 'config', 'routes.js');
const srcDir = path.join(root, 'src', 'pages');
const publicOut = path.join(root, 'public', 'wldos', 'page-api-manifest.json');
const platformOut = path.join(
  root,
  '..',
  'modules',
  'wldos-platform',
  'src',
  'main',
  'resources',
  'wldos',
  'page-api-manifest.json',
);

const METHODS = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'];

function normalizeComponent(component) {
  if (!component || typeof component !== 'string') return null;
  let c = component.replace(/^\.\//, '');
  if (c.endsWith('/index')) {
    c = c.slice(0, -6);
  }
  return c;
}

function collectRoutes(routes, basePath = '') {
  const out = [];
  if (!Array.isArray(routes)) return out;
  for (const route of routes) {
    if (!route || typeof route !== 'object') continue;
    let segment = route.path || '';
    if (segment.includes(':')) continue;
    let fullPath = segment.startsWith('/')
      ? segment
      : `${basePath}/${segment}`.replace(/\/+/g, '/');
    if (fullPath.length > 1 && fullPath.endsWith('/')) {
      fullPath = fullPath.slice(0, -1);
    }
    const component = normalizeComponent(route.component);
    if (component && fullPath.startsWith('/') && !route.redirect) {
      out.push({ path: fullPath, component });
    }
    if (Array.isArray(route.routes)) {
      const nextBase = route.path
        ? route.path.startsWith('/')
          ? route.path
          : `${basePath}/${route.path}`.replace(/\/+/g, '/')
        : basePath;
      out.push(...collectRoutes(route.routes, nextBase));
    }
  }
  return out;
}

function resolveServiceFiles(component) {
  const files = new Set();
  const pageDir = path.join(srcDir, ...component.split('/'));
  const localService = path.join(pageDir, 'service.js');
  if (fs.existsSync(localService)) {
    files.add(localService);
  }
  const indexFile = path.join(pageDir, 'index.jsx');
  if (fs.existsSync(indexFile)) {
    const indexContent = fs.readFileSync(indexFile, 'utf8');
    const importRe = /from\s+['"](@\/pages\/[^'"]+\/service|\.\/service)['"]/g;
    let m;
    while ((m = importRe.exec(indexContent)) !== null) {
      let p = m[1];
      if (p.startsWith('@/pages/')) {
        p = path.join(srcDir, p.replace('@/pages/', ''));
      } else {
        p = path.join(pageDir, 'service.js');
      }
      if (!p.endsWith('.js')) p += '.js';
      if (fs.existsSync(p)) files.add(p);
    }
  }
  return [...files];
}

function extractRequestCallBody(content, startIndex) {
  let i = startIndex;
  while (i < content.length && content[i] !== '(') {
    i += 1;
  }
  if (i >= content.length) {
    return null;
  }
  let depth = 0;
  const bodyStart = i;
  for (; i < content.length; i += 1) {
    const ch = content[i];
    if (ch === '(') {
      depth += 1;
    } else if (ch === ')') {
      depth -= 1;
      if (depth === 0) {
        return content.slice(bodyStart, i + 1);
      }
    }
  }
  return null;
}

function extractApisFromFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  const apis = [];
  const seen = new Set();

  const normalizeApiPath = (rawPath) => {
    if (!rawPath || typeof rawPath !== 'string') return null;
    let path = rawPath.trim();
    const qIdx = path.indexOf('?');
    if (qIdx >= 0) {
      path = path.slice(0, qIdx);
    }
    path = path.replace(/\$\{([^}]+)\}/g, '{$1}');
    return path;
  };

  const addApi = (method, apiPath) => {
    const normalized = normalizeApiPath(apiPath);
    if (!normalized || !normalized.startsWith('/')) return;
    const key = `${method}:${normalized}`;
    if (seen.has(key)) return;
    seen.add(key);
    apis.push({ method, path: normalized });
  };

  const requestRe = /request\s*\(\s*/g;
  let rm;
  while ((rm = requestRe.exec(content)) !== null) {
    const callBody = extractRequestCallBody(content, rm.index);
    if (!callBody) continue;
    const pathMatch =
      callBody.match(/`\$\{prefix\}([^`]+)`/) ||
      callBody.match(/['"]\$\{prefix\}([^'"]+)['"]/);
    if (!pathMatch) continue;
    const methodMatch = callBody.match(/method:\s*['"](GET|POST|PUT|DELETE|PATCH)['"]/i);
    const method = methodMatch ? methodMatch[1].toUpperCase() : 'GET';
    addApi(method, pathMatch[1]);
  }

  return apis;
}

function loadRoutes() {
  const raw = fs.readFileSync(routesPath, 'utf8');
  const marker = 'const routes = [';
  const arrStart = raw.indexOf(marker);
  if (arrStart < 0) throw new Error('routes.js: cannot find route array');
  const bracketStart = arrStart + marker.length - 1;
  let depth = 0;
  let end = -1;
  for (let i = bracketStart; i < raw.length; i += 1) {
    if (raw[i] === '[') depth += 1;
    if (raw[i] === ']') {
      depth -= 1;
      if (depth === 0) {
        end = i + 1;
        break;
      }
    }
  }
  if (end < 0) throw new Error('routes.js: unclosed array');
  // eslint-disable-next-line no-eval
  return eval(raw.slice(bracketStart, end));
}

function main() {
  const routes = loadRoutes();
  const pages = collectRoutes(routes);
  const manifest = {};

  for (const { path: pagePath, component } of pages) {
    const serviceFiles = resolveServiceFiles(component);
    const apis = [];
    const apiSeen = new Set();
    for (const sf of serviceFiles) {
      for (const api of extractApisFromFile(sf)) {
        const k = `${api.method}:${api.path}`;
        if (!apiSeen.has(k)) {
          apiSeen.add(k);
          apis.push(api);
        }
      }
    }
    if (apis.length > 0) {
      manifest[pagePath] = { component, apis };
    }
  }

  const json = `${JSON.stringify(manifest, null, 2)}\n`;
  fs.mkdirSync(path.dirname(publicOut), { recursive: true });
  fs.writeFileSync(publicOut, json, 'utf8');
  fs.mkdirSync(path.dirname(platformOut), { recursive: true });
  fs.writeFileSync(platformOut, json, 'utf8');
  console.log(`[page-api-manifest] pages=${Object.keys(manifest).length} -> ${publicOut}`);
}

main();
