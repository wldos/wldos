/**
 * 从 plugin.yml 读取插件编码并生成 plugin.js 配置文件
 * 确保插件编码单一数据源
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 插件根目录（从 src/main/ui 向上三级）
const pluginRoot = path.resolve(__dirname, '../../../..');
const pluginYmlPath = path.join(pluginRoot, 'plugin.yml');
const pluginJsPath = path.join(__dirname, '../src/config/plugin.js');

// 读取 plugin.yml
function readPluginYml() {
  try {
    const ymlContent = fs.readFileSync(pluginYmlPath, 'utf-8');
    // 简单解析：查找 code: xxx 和 version: xxx 行
    const codeMatch = ymlContent.match(/^code:\s*(.+)$/m);
    const versionMatch = ymlContent.match(/^version:\s*(.+)$/m);
    
    if (!codeMatch || !codeMatch[1]) {
      throw new Error('无法从 plugin.yml 中找到 code 字段');
    }
    
    if (!versionMatch || !versionMatch[1]) {
      throw new Error('无法从 plugin.yml 中找到 version 字段');
    }
    
    return {
      code: codeMatch[1].trim(),
      version: versionMatch[1].trim()
    };
  } catch (error) {
    console.error(`读取 plugin.yml 失败: ${error.message}`);
    console.error(`文件路径: ${pluginYmlPath}`);
    throw error;
  }
}

// 生成 plugin.js
function generatePluginConfig(pluginCode, pluginVersion) {
  const content = `/**
 * 插件配置
 * 插件编码和版本配置，用于 API 请求自动添加前缀和构建路径
 * 
 * 注意：此文件由 scripts/generate-plugin-config.js 自动生成
 * 请勿手动修改，插件编码和版本请修改 plugin.yml 中的对应字段
 */
export const PLUGIN_CODE = '${pluginCode}';
export const PLUGIN_VERSION = '${pluginVersion}';

export default {
  PLUGIN_CODE,
  PLUGIN_VERSION,
};
`;

  // 确保目录存在
  const pluginJsDir = path.dirname(pluginJsPath);
  if (!fs.existsSync(pluginJsDir)) {
    fs.mkdirSync(pluginJsDir, { recursive: true });
  }

  // 写入文件
  fs.writeFileSync(pluginJsPath, content, 'utf-8');
  console.log(`✓ 已生成插件配置: ${pluginJsPath}`);
  console.log(`  插件编码: ${pluginCode}`);
  console.log(`  插件版本: ${pluginVersion}`);
  
  // 同时设置环境变量，供构建时使用
  process.env.PLUGIN_VERSION = pluginVersion;
}

// 主函数
function main() {
  try {
    const { code, version } = readPluginYml();
    generatePluginConfig(code, version);
  } catch (error) {
    console.error('生成插件配置失败:', error);
    process.exit(1);
  }
}

main();

