/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.res;

/**
 * WLDOS业务状态码规范
 * 使用HTTP状态码语义，200=成功，其他值表示失败
 *
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "success"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请重新登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "参数验证失败"),
    
    // 业务错误 5xx
    BUSINESS_ERROR(500, "业务处理失败"),
    DATA_NOT_FOUND(501, "数据不存在"),
    DATA_ALREADY_EXISTS(502, "数据已存在"),
    OPERATION_FAILED(503, "操作失败"),
    
    // 系统错误 5xx
    INTERNAL_ERROR(500, "系统异常"),
    DATABASE_ERROR(510, "数据库异常"),
    NETWORK_ERROR(511, "网络异常"),
    THIRD_PARTY_ERROR(512, "第三方服务异常"),
    
    // 认证错误
    TOKEN_INVALID(401, "Token无效"),
    TOKEN_EXPIRED(401, "Token已过期"),
    TOKEN_FORBIDDEN(403, "Token权限不足");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() { 
        return code; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public boolean isSuccess() {
        return code == 200;
    }
}

