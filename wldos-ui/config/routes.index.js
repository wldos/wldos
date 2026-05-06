import communityRoutes from './routes.community';
import { getRouteTitleByPath } from './routes';

/**
 * 社区分支合并时会删除 routes.commercial.js；用 require + try/catch 替代静态 import，
 * 让"社区源码 + 默认 npm start"也能走完 config 加载阶段（默认 APP_FLAVOR=commercial 会回退到 community 路由）。
 * 商业分支因 routes.commercial.js 仍存在，require 命中真实文件，行为不变。
 */
function loadCommercialRoutesSafely() {
  try {
    // eslint-disable-next-line global-require, import/no-unresolved
    const mod = require('./routes.commercial');
    return mod && mod.default ? mod.default : mod;
  } catch (e) {
    if (process.env.APP_FLAVOR !== 'community') {
      // 默认 APP_FLAVOR=commercial 但目录已删，提示开发者建议切到 community 脚本
      // eslint-disable-next-line no-console
      console.warn(
        '[wldos][routes] routes.commercial.js 不存在，默认/commercial 模式将 fallback 到 community 路由。'
        + ' 建议社区分支显式使用 APP_FLAVOR=community（npm run start:community:dev）。',
      );
    }
    return null;
  }
}

const flavor = process.env.APP_FLAVOR || 'commercial';
const commercialRoutes = loadCommercialRoutesSafely();

const routes = flavor === 'community' || !commercialRoutes ? communityRoutes : commercialRoutes;

export { getRouteTitleByPath };
export const routeTitleByPath = getRouteTitleByPath(routes);
export default routes;
