/**
 * Rollup 插件：将外部依赖的 import 转换为全局变量
 * 用于 ESM 格式构建，将 react、react-dom、antd 的 import 转换为从 window 获取
 */

export default function globals() {
  return {
    name: 'globals',
    renderChunk(code, chunk, options) {
      // 先处理混合导入：import React, { useState, useEffect } from 'react'
      // 必须在处理单独导入之前处理，因为混合导入的正则更具体
      code = code.replace(
        /import\s+(\w+)\s*,\s*\{([^}]+)\}\s+from\s+['"]react['"]/g,
        (match, defaultName, namedImports) => {
          const vars = namedImports.split(',').map(v => v.trim()).join(', ');
          return `const ${defaultName} = window.React;\nconst { ${vars} } = window.React`;
        }
      );
      
      // 替换 import React from 'react' 为 const React = window.React
      code = code.replace(
        /import\s+(\w+)\s+from\s+['"]react['"]/g,
        'const $1 = window.React'
      );
      code = code.replace(
        /import\s+\*\s+as\s+(\w+)\s+from\s+['"]react['"]/g,
        'const $1 = window.React'
      );
      
      // 处理 react 的命名导入：import { useState, useEffect } from 'react'
      code = code.replace(
        /import\s+\{([^}]+)\}\s+from\s+['"]react['"]/g,
        (match, imports) => {
          const vars = imports.split(',').map(v => v.trim()).join(', ');
          return `const { ${vars} } = window.React`;
        }
      );
      
      // 处理 react-dom 的混合导入
      code = code.replace(
        /import\s+(\w+)\s*,\s*\{([^}]+)\}\s+from\s+['"]react-dom['"]/g,
        (match, defaultName, namedImports) => {
          const vars = namedImports.split(',').map(v => v.trim()).join(', ');
          return `const ${defaultName} = window.ReactDOM;\nconst { ${vars} } = window.ReactDOM`;
        }
      );
      
      // 替换 import ReactDOM from 'react-dom' 为 const ReactDOM = window.ReactDOM
      code = code.replace(
        /import\s+(\w+)\s+from\s+['"]react-dom['"]/g,
        'const $1 = window.ReactDOM'
      );
      code = code.replace(
        /import\s+\*\s+as\s+(\w+)\s+from\s+['"]react-dom['"]/g,
        'const $1 = window.ReactDOM'
      );
      
      // 处理 react-dom 的命名导入
      code = code.replace(
        /import\s+\{([^}]+)\}\s+from\s+['"]react-dom['"]/g,
        (match, imports) => {
          const vars = imports.split(',').map(v => v.trim()).join(', ');
          return `const { ${vars} } = window.ReactDOM`;
        }
      );
      
      // 处理 antd 的混合导入
      code = code.replace(
        /import\s+(\w+)\s*,\s*\{([^}]+)\}\s+from\s+['"]antd['"]/g,
        (match, defaultName, namedImports) => {
          const vars = namedImports.split(',').map(v => v.trim()).join(', ');
          return `const ${defaultName} = window.antd;\nconst { ${vars} } = window.antd`;
        }
      );
      
      // 替换 import { ... } from 'antd' 为从 window.antd 解构
      code = code.replace(
        /import\s+\{([^}]+)\}\s+from\s+['"]antd['"]/g,
        (match, imports) => {
          const vars = imports.split(',').map(v => v.trim()).join(', ');
          return `const { ${vars} } = window.antd`;
        }
      );
      code = code.replace(
        /import\s+\*\s+as\s+(\w+)\s+from\s+['"]antd['"]/g,
        'const $1 = window.antd'
      );
      
      // 替换 react/jsx-runtime 的 import
      // jsx 和 jsxs 是 React 17+ 的新 JSX 转换函数
      // 我们需要从 window.React 获取这些函数
      code = code.replace(
        /import\s+\{([^}]+)\}\s+from\s+['"]react\/jsx-runtime['"]/g,
        (match, imports) => {
          // 提取导入的函数名（如 jsx, jsxs）
          const funcs = imports.split(',').map(v => v.trim());
          // 创建从 window.React 获取这些函数的代码
          // 注意：React 17+ 的 jsx 函数在 React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED 中
          // 但更简单的方式是使用 React.createElement 的包装
          // 或者直接使用全局的 React 对象
          const declarations = funcs.map(func => {
            // 如果 React 有这些函数，直接使用；否则使用 React.createElement 的包装
            return `const ${func} = window.React.${func} || ((type, props, ...children) => window.React.createElement(type, props, ...children))`;
          }).join(';\n');
          return declarations;
        }
      );
      
      return { code, map: null };
    },
  };
}

