/**
 * Rollup 配置文件
 * 用于构建 ESM 格式的插件包
 * 
 * 多入口构建：为每个路由组件生成独立的 ESM 文件
 * 与 UmiJS chunk 命名规则一致：./home → esm/home.js
 */

import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import babel from '@rollup/plugin-babel';
import replace from '@rollup/plugin-replace';
import path from 'path';
import { fileURLToPath } from 'url';
import globals from './rollup-plugin-globals.js';
const __dirname = path.dirname(fileURLToPath(import.meta.url));

// 从路由配置中提取组件路径，生成多入口配置
// 注意：Rollup 配置文件是 ES 模块，可以直接 import
import routesConfig from './config/routes.js';

const routes = routesConfig.default || routesConfig;

const inputs = {};
routes.forEach(route => {
  if (route.component) {
    // 提取组件路径：'./home' → 'home'
    const componentPath = route.component.replace(/^\.\//, '');
    // 输入：src/pages/home/index.jsx
    // 输出：dist/esm/home.js
    inputs[componentPath] = `src/pages/${componentPath}/index.jsx`;
  }
});

export default {
  input: inputs,
  output: {
    dir: 'dist/esm',
    format: 'es',
    entryFileNames: '[name].js',
    exports: 'auto', // 自动检测导出方式（支持 default 和 named exports）
  },
  external: (id) => {
    // 外部依赖不打包，从全局变量获取
    const externals = ['react', 'react-dom', 'antd'];
    return externals.some(dep => 
      id === dep || id.startsWith(`${dep}/`)
    );
  },
  plugins: [
    replace({
      'process.env.NODE_ENV': JSON.stringify('production'),
      'process.env.PLUGIN_VERSION': JSON.stringify(process.env.PLUGIN_VERSION || '1.0.0'),
      preventAssignment: true,
    }),
    resolve({
      extensions: ['.js', '.jsx'],
      browser: true,
    }),
    commonjs(),
    babel({
      babelHelpers: 'bundled',
      exclude: 'node_modules/**',
      extensions: ['.js', '.jsx'],
      presets: [
        ['@babel/preset-env', { 
          modules: false,
          targets: {
            browsers: ['> 1%', 'last 2 versions', 'not ie <= 10']
          }
        }],
        ['@babel/preset-react', {
          runtime: 'classic'  // 使用 React.createElement，避免 react/jsx-runtime 的导入问题
        }],
      ],
    }),
    globals(), // 将外部依赖的 import 转换为全局变量
  ],
};

