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
package com.example.myapp.controller;

import com.example.myapp.service.OrderService;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.Result;
import com.wldos.framework.mvc.controller.NonEntityController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理 Controller
 * 
 * 演示使用 NonEntityController 基类进行非实体业务操作
 * 
 * NonEntityController 提供的功能：
 * - 用户信息获取（getUserId, getToken, getUserIp等）
 * - 租户信息获取（getTenantId, isMultiTenancy等）
 * - 域名信息获取（getDomain, getDomainId等）
 * - 管理员判断（isAdmin, isCanTrust等）
 * - 分页查询工具（applyDomainFilter, applyTenantFilter等）
 * 
 * 框架自动处理：
 * - 统一响应格式（自动包装为 Result）
 * - 异常处理（自动捕获并返回统一格式）
 */
@Api(tags = "订单管理（NonEntityController示例）")
@RestController
@RequestMapping("/api/orders")
public class OrderController extends NonEntityController<OrderService> {
    
    /**
     * 创建订单
     * 演示使用 NonEntityController 提供的工具方法
     * 
     * 访问：POST http://localhost:8080/api/orders/create?productId=1&quantity=2
     */
    @ApiOperation(value = "创建订单", notes = "演示使用 NonEntityController 提供的工具方法")
    @PostMapping("/create")
    public Result createOrder(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        
        // 使用基类提供的工具方法
        Long userId = this.getUserId();
        String token = this.getToken();
        String userIp = this.getUserIp();
        
        // 调用 Service 创建订单
        String orderInfo = service.createOrder(productId, quantity);
        
        return Result.ok(orderInfo);
    }
    
    /**
     * 查询订单列表
     *
     * 访问：GET http://localhost:8080/api/orders/list
     */
    @ApiOperation(value = "查询订单列表", notes = "演示使用 NonEntityController 进行业务查询")
    @GetMapping("/list")
    public Result getOrderList() {
        Long userId = this.getUserId();
        return Result.ok(service.getOrderList());
    }

    /**
     * 管理端订单分页列表（框架自动解析 PageQuery）
     *
     * 访问：GET /api/orders/admin-list?current=1&pageSize=10
     */
    @ApiOperation(value = "订单分页列表（管理端）", notes = "PageQuery 由框架自动解析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码", dataType = "int", paramType = "query", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int", paramType = "query", example = "10")
    })
    @GetMapping("/admin-list")
    public Result<PageData<String>> adminList(PageQuery pageQuery) {
        return Result.ok(service.adminList(pageQuery));
    }
    
    /**
     * 获取当前用户信息
     * 演示 NonEntityController 提供的各种工具方法
     * 
     * 访问：GET http://localhost:8080/api/orders/user-info
     */
    @ApiOperation(value = "获取当前用户信息", notes = "演示 NonEntityController 提供的各种工具方法")
    @GetMapping("/user-info")
    public Result getUserInfo() {
        java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
        
        // 用户信息
        userInfo.put("userId", this.getUserId());
        userInfo.put("token", this.getToken());
        userInfo.put("userIp", this.getUserIp());
        
        // 租户信息
        userInfo.put("isMultiTenancy", this.isMultiTenancy());
        userInfo.put("tenantId", this.getTenantId());
        
        // 域名信息
        userInfo.put("isMultiDomain", this.isMultiDomain());
        userInfo.put("domain", this.getDomain());
        userInfo.put("domainId", this.getDomainId());
        userInfo.put("platDomain", this.getPlatDomain());
        
        // Token信息
        userInfo.put("tokenExpTime", this.getTokenExpTime());
        
        return Result.ok(userInfo);
    }
    
    /**
     * 管理员操作示例
     * 演示使用 isAdmin 方法判断管理员权限
     * 
     * 访问：GET http://localhost:8080/api/orders/admin-operation
     */
    @ApiOperation(value = "管理员操作示例", notes = "演示使用 isAdmin 方法判断管理员权限")
    @GetMapping("/admin-operation")
    public Result adminOperation() {
        Long userId = this.getUserId();
        
        // 判断是否为管理员
        if (!this.isAdmin(userId)) {
            return Result.error(403, "权限不足，需要管理员权限");
        }
        
        // 管理员操作...
        return Result.ok("管理员操作成功");
    }
}

