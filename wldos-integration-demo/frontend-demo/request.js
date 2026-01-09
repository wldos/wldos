/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * WLDOS 统一响应格式处理
 * 
 * 演示如何在前端处理 WLDOS 框架的统一响应格式
 */

// API 基础地址
const API_BASE_URL = 'http://localhost:8080';

/**
 * 统一的请求方法
 * 自动处理 WLDOS 框架的统一响应格式
 * 
 * @param {string} url - 请求URL
 * @param {object} options - 请求选项（method, body等）
 * @returns {Promise} 返回处理后的数据
 */
async function request(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...(options.headers || {}),
        },
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, finalOptions);
        const data = await response.json();
        
        // WLDOS 框架统一响应格式处理
        // 格式：{ code: 200, message: "", data: {...}, success: true }
        
        // 判断业务是否成功
        if (data.code === 200 || data.success === true) {
            // 业务成功，返回数据
            return {
                success: true,
                data: data.data,
                message: data.message || '',
                code: data.code,
            };
        } else {
            // 业务失败，返回错误信息
            return {
                success: false,
                data: null,
                message: data.message || '请求失败',
                code: data.code,
            };
        }
    } catch (error) {
        // 网络错误或其他异常
        console.error('请求失败:', error);
        return {
            success: false,
            data: null,
            message: error.message || '网络请求失败',
            code: 500,
        };
    }
}

/**
 * GET 请求
 */
async function get(url) {
    return request(url, { method: 'GET' });
}

/**
 * POST 请求
 */
async function post(url, data) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

/**
 * PUT 请求
 */
async function put(url, data) {
    return request(url, {
        method: 'PUT',
        body: JSON.stringify(data),
    });
}

/**
 * DELETE 请求
 */
async function del(url, data) {
    return request(url, {
        method: 'DELETE',
        body: JSON.stringify(data),
    });
}

/**
 * 显示结果
 */
function showResult(elementId, result, isSuccess = true) {
    const element = document.getElementById(elementId);
    element.style.display = 'block';
    element.className = `result ${isSuccess ? 'success' : 'error'}`;
    
    if (result.success) {
        element.textContent = JSON.stringify(result, null, 2);
    } else {
        element.textContent = `错误: ${result.message}\n状态码: ${result.code}\n\n详细信息:\n${JSON.stringify(result, null, 2)}`;
    }
}

