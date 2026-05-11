/**
 * 社区开源分支占位模块。
 *
 * <p>config/routes.index.js 会被前端打包，webpack 会静态解析 {@code require('./routes.commercial')}。
 * 社区分支删除真实商业路由文件后，若无此占位则构建报 {@code Module not found}。
 *
 * <p>默认导出与 {@link ./routes.community} 一致，与「无 commercial 文件则 fallback」语义相同。
 * 商业仓库/分支请用真实 {@code routes.commercial} 实现覆盖本文件。
 */
export { default } from './routes.community';
