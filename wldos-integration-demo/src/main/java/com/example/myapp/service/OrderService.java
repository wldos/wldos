/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.service;

import com.wldos.framework.mvc.service.NonEntityService;
import org.springframework.stereotype.Service;

/**
 * 订单服务类
 * 演示使用 NonEntityService 进行非实体业务操作
 * 
 * NonEntityService 提供了：
 * - 用户信息获取（getUserId, getToken, getUserIp等）
 * - 租户信息获取（getTenantId, isMultiTenancy等）
 * - 域名信息获取（getDomain, getDomainId等）
 * - 缓存操作（cache）
 * - JWT工具（jwtTool）
 * - 存储服务（store）
 * 等等...
 */
@Service
public class OrderService extends NonEntityService {
    
    /**
     * 创建订单
     * 演示使用 NonEntityService 提供的工具方法
     */
    public String createOrder(Long productId, Integer quantity) {
        // 获取当前用户ID
        Long userId = this.getUserId();
        
        // 获取当前用户Token
        String token = this.getToken();
        
        // 获取用户IP
        String userIp = this.getUserIp();
        
        // 获取租户ID（如果启用多租户）
        Long tenantId = this.getTenantId();
        
        // 获取域名ID（如果启用多域名）
        Long domainId = this.getDomainId();
        
        // 使用缓存
        // cache.set("order:" + orderId, orderData, 3600);
        
        // 使用JWT工具
        // String newToken = jwtTool.generateToken(userId);
        
        // 业务逻辑...
        return "订单创建成功，用户ID: " + userId + ", 产品ID: " + productId;
    }
    
    /**
     * 查询订单列表
     */
    public java.util.List<String> getOrderList() {
        // 获取当前用户ID
        Long userId = this.getUserId();
        
        // 业务逻辑...
        return java.util.Arrays.asList("订单1", "订单2", "订单3");
    }
}

