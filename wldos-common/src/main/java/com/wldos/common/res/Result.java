/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.res;

/**
 * 统一响应格式（新版本，不兼容旧格式）
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
public class Result {
    
    /**
     * 业务状态码：200=成功，非200=失败
     */
    private Integer code = 200;

    private static final String defaultMessage = "";
    
    /**
     * 响应消息：成功时为空字符串，失败时为错误信息
     */
    private String message = defaultMessage;
    
    /**
     * 响应数据：业务数据
     */
    private Object data;
    
    /**
     * 操作是否成功：true=成功，false=失败
     */
    private Boolean success = true;
    
    public Result() {
    }
    
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.success = (code != null && code == 200);
    }
    
    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = (code != null && code == 200);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static Result ok() {
        return new Result(200, defaultMessage, null);
    }
    
    /**
     * 成功响应（有数据）
     */
    public static Result ok(Object data) {
        return new Result(200, defaultMessage, data);
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static Result ok(String message, Object data) {
        return new Result(200, message, data);
    }
    
    /**
     * 错误响应
     */
    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }
    
    /**
     * 错误响应（使用WLDOSResultCode枚举，使用枚举的默认消息）
     */
    public static Result error(ResultCode resultCode) {
        return new Result(resultCode.getCode(), resultCode.getMessage(), null);
    }
    
    /**
     * 错误响应（使用WLDOSResultCode枚举，自定义消息）
     * 使用枚举的code，但使用自定义的message
     */
    public static Result error(ResultCode resultCode, String message) {
        return new Result(resultCode.getCode(), message, null);
    }
    
    // Getters and Setters
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
        this.success = (code != null && code == 200);
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
        // 同步code字段
        if (success != null && !success && code == 200) {
            this.code = 500; // 默认失败码
        }
    }
}
