#!/bin/bash
# Shell自动化脚本示例
#
# 功能：
# 1. 文件操作
# 2. API调用
# 3. 日志记录
#
# 使用方法：
# 将此脚本内容复制到任务配置的scriptContent字段

set -e  # 遇到错误立即退出

echo "=== 开始Shell自动化任务 ==="

# ========== 步骤1: 创建工作目录 ==========
echo "步骤1: 创建工作目录"
WORK_DIR="/tmp/airdrop_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$WORK_DIR"
echo "工作目录: $WORK_DIR"

# ========== 步骤2: 记录任务信息 ==========
echo "步骤2: 记录任务信息"
cat > "$WORK_DIR/task_info.txt" << EOF
任务ID: $taskId
任务名称: $taskName
执行时间: $(date '+%Y-%m-%d %H:%M:%S')
用户ID: $userId
EOF

echo "任务信息已保存到: $WORK_DIR/task_info.txt"

# ========== 步骤3: 调用API ==========
echo "步骤3: 调用API"

if [ -n "$apiUrl" ]; then
    echo "调用API: $apiUrl"
    
    # 构建请求数据
    REQUEST_DATA=$(cat <<EOF
{
    "taskId": "$taskId",
    "taskName": "$taskName",
    "userId": "$userId",
    "timestamp": $(date +%s)
}
EOF
)
    
    # 发送POST请求
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$apiUrl" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $apiKey" \
        -d "$REQUEST_DATA")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "200" ]; then
        echo "API调用成功: $BODY"
        echo "$BODY" > "$WORK_DIR/api_response.json"
    else
        echo "API调用失败: HTTP $HTTP_CODE"
        echo "$BODY" > "$WORK_DIR/api_error.json"
    fi
else
    echo "未配置API URL，跳过API调用"
fi

# ========== 步骤4: 执行自定义命令 ==========
echo "步骤4: 执行自定义命令"

# 示例：检查网络连接
if command -v ping &> /dev/null; then
    echo "检查网络连接..."
    ping -c 3 8.8.8.8 > "$WORK_DIR/network_test.txt" 2>&1 || true
    echo "网络测试完成"
fi

# ========== 步骤5: 生成执行报告 ==========
echo "步骤5: 生成执行报告"

REPORT_FILE="$WORK_DIR/execution_report.txt"
cat > "$REPORT_FILE" << EOF
========================================
执行报告
========================================
任务ID: $taskId
任务名称: $taskName
执行时间: $(date '+%Y-%m-%d %H:%M:%S')
执行状态: 成功
工作目录: $WORK_DIR
========================================

文件列表:
$(ls -lh "$WORK_DIR")

========================================
EOF

cat "$REPORT_FILE"

# ========== 步骤6: 清理（可选） ==========
# echo "步骤6: 清理临时文件"
# rm -rf "$WORK_DIR"

echo "=== Shell自动化任务完成 ==="
echo "工作目录: $WORK_DIR"
echo "报告文件: $REPORT_FILE"

