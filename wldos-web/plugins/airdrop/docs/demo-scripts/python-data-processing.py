#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Python数据处理脚本示例

功能：
1. 处理任务数据
2. 调用API
3. 生成报告

使用方法：
将此脚本内容复制到任务配置的scriptContent字段
"""

import json
import time
import requests
from datetime import datetime

print("=== 开始Python数据处理任务 ===")

# ========== 步骤1: 使用上下文变量 ==========
print(f"任务名称: {taskName}")
print(f"用户ID: {userId}")
print(f"钱包地址: {walletAddress}")

# ========== 步骤2: 数据处理 ==========
print("\n步骤2: 处理数据")

# 构建数据对象
task_data = {
    "taskName": taskName,
    "userId": userId,
    "walletAddress": walletAddress,
    "timestamp": int(time.time()),
    "datetime": datetime.now().isoformat(),
    "status": "processing"
}

# 转换为JSON
data_json = json.dumps(task_data, ensure_ascii=False, indent=2)
print(f"处理的数据:\n{data_json}")

# ========== 步骤3: 调用API（可选） ==========
if 'apiUrl' in globals() and apiUrl:
    print(f"\n步骤3: 调用API - {apiUrl}")
    try:
        response = requests.post(
            apiUrl,
            json=task_data,
            headers={
                'Content-Type': 'application/json',
                'Authorization': f'Bearer {apiKey}' if 'apiKey' in globals() else None
            },
            timeout=10
        )
        
        if response.status_code == 200:
            result = response.json()
            print(f"API调用成功: {result}")
        else:
            print(f"API调用失败: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"API调用异常: {str(e)}")

# ========== 步骤4: 生成报告 ==========
print("\n步骤4: 生成报告")

report = {
    "task": taskName,
    "status": "completed",
    "processedAt": datetime.now().isoformat(),
    "data": task_data
}

report_json = json.dumps(report, ensure_ascii=False, indent=2)
print(f"生成的报告:\n{report_json}")

print("\n=== Python数据处理任务完成 ===")

# 返回结果（通过print输出，会被捕获为标准输出）
print(f"RESULT:{json.dumps({'success': True, 'report': report}, ensure_ascii=False)}")

