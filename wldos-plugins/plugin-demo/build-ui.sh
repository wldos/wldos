#!/bin/bash
# 插件演示 UI 构建脚本 (Linux/Mac)

cd "$(dirname "$0")/src/main/ui"

echo "开始构建插件演示 UI..."

# 检查 Node.js 和 Yarn
if ! command -v node &> /dev/null; then
    echo "错误: 未找到 Node.js，请先安装 Node.js"
    exit 1
fi

if ! command -v yarn &> /dev/null; then
    echo "错误: 未找到 Yarn，请先安装 Yarn"
    exit 1
fi

# 安装依赖
echo "安装依赖..."
yarn install

# 构建 ESM 格式
echo "构建 ESM 格式..."
yarn run build:esm

echo "UI 构建完成！"
echo "构建输出目录: src/main/ui/dist/esm/"

