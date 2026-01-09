/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.auth.impl;

import com.wldos.framework.support.auth.JWTTool;
import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.auth.vo.Token;
import io.jsonwebtoken.Claims;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/25
 */
@ConditionalOnMissingClass("com.wldos.support.auth.impl.JWTToolImpl")
@Component
public class DefaultJWTToolImpl implements JWTTool {

    @Override
    public Claims verifyToken(String token, String secretKey, int tokenTimeout) {
        return null;
    }

    @Override
    public Claims verifyToken(String token) {
        return null;
    }

    @Override
    public JWT popJwt(String token, String userIp, String reqUri, String domain, Long domainId) {
        return null;
    }

    @Override
    public Token refresh(String domain, String token, Long userId, Long tenantId, Long domainId, long expireTime, int refresh) {
        return null;
    }

    @Override
    public Token refreshToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public int getTokenTimeout() {
        return 0;
    }

    @Override
    public Token genToken(Long userId, Long tenantId, Long domainId, int tokenTimeoutMinutes) {
        return null;
    }

    @Override
    public void recLog(String domain, Token token, HttpServletRequest request) {

    }

    @Override
    public boolean isExpired(JWT jwt) {
        return false;
    }

    @Override
    public String takTokenRec(JWT jwt) {
        return null;
    }

    @Override
    public String takTokenRec(Long domainId, Long userId) {
        return null;
    }

    @Override
    public List<Object> takTokenRec() {
        return null;
    }

    @Override
    public void recLog(String domain, JWT jwt, HttpServletRequest request) {

    }

    @Override
    public void delToken(Long domainId, String token) {

    }

    @Override
    public void delTokenById(Long domainId, Long userId) {

    }

    @Override
    public void setTokenHeader(ServerHttpResponse response, Token token) {

    }
}
