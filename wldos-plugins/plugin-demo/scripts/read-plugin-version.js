/**
 * 从 plugin.yml 读取插件版本号
 * 供 Maven 构建时使用
 */

const fs = require('fs');
const path = require('path');

// 插件根目录（脚本所在目录的父目录）
const pluginRoot = path.resolve(__dirname, '..');
const pluginYmlPath = path.join(pluginRoot, 'plugin.yml');

// 读取 plugin.yml
function readPluginVersion() {
  try {
    const ymlContent = fs.readFileSync(pluginYmlPath, 'utf-8');
    // 简单解析：查找 version: xxx 行
    const versionMatch = ymlContent.match(/^version:\s*(.+)$/m);
    
    if (!versionMatch || !versionMatch[1]) {
      throw new Error('无法从 plugin.yml 中找到 version 字段');
    }
    
    return versionMatch[1].trim();
  } catch (error) {
    console.error(`读取 plugin.yml 失败: ${error.message}`);
    console.error(`文件路径: ${pluginYmlPath}`);
    throw error;
  }
}

// 主函数
function main() {
  try {
    const version = readPluginVersion();
    // 输出版本号到标准输出，供 Maven 使用
    console.log(version);
  } catch (error) {
    console.error('读取插件版本号失败:', error);
    process.exit(1);
  }
}

main();

