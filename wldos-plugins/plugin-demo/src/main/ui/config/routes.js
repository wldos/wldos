/**
 * 插件路由配置
 * 
 * 按照主应用的路径约定配置路由
 * UmiJS 路由配置中：
 * - ./ 表示 src/pages 目录（不是 src 目录）
 * - component 路径按照实际的组件位置配置，不需要和 path 一致
 * - UmiJS 会根据 component 路径自动生成 chunk 名称
 */
const routes = [
  {
    path: '/',
    component: './placeholder',
  },
  // 按照 plugin.yml 中的 path 配置路由，component 使用实际的组件路径
  {
    path: '/admin/demo/home',
    component: './home',
  },
  {
    path: '/admin/demo/example',
    component: './example',
  },
];

export default routes;

