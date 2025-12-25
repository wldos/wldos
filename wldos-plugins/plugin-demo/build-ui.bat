@echo off
REM 插件演示 UI 构建脚本 (Windows)

cd /d "%~dp0src\main\ui"

echo 开始构建插件演示 UI...

REM 检查 Node.js 和 Yarn
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 Node.js，请先安装 Node.js
    exit /b 1
)

where yarn >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 Yarn，请先安装 Yarn
    exit /b 1
)

REM 安装依赖
echo 安装依赖...
call yarn install

REM 构建 ESM 格式
echo 构建 ESM 格式...
call yarn run build:esm

echo UI 构建完成！
echo 构建输出目录: src\main\ui\dist\esm\

