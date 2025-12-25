/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.plugin.demo.controller;

import com.wldos.plugin.demo.entity.DemoEntity;
import com.wldos.plugin.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 演示控制器
 * 作为插件开发者的参考示例
 *
 * @author WLDOS Team
 * @date 2025/12/12
 */
@Slf4j
@RestController
@RequestMapping("/")
public class DemoController {
    
    @Autowired
    private DemoService demoService;
    
    /**
     * 获取所有演示数据
     */
    @GetMapping("/list")
    public ResponseEntity<List<DemoEntity>> getAllDemos() {
        List<DemoEntity> demos = demoService.getAllDemos();
        return ResponseEntity.ok(demos);
    }
    
    /**
     * 根据ID获取演示数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemoEntity> getDemoById(@PathVariable Long id) {
        DemoEntity demo = demoService.getDemoById(id);
        if (demo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(demo);
    }
    
    /**
     * 创建演示数据
     */
    @PostMapping("/create")
    public ResponseEntity<DemoEntity> createDemo(@RequestBody DemoEntity demo) {
        DemoEntity created = demoService.createDemo(demo);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新演示数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemoEntity> updateDemo(@PathVariable Long id, @RequestBody DemoEntity demo) {
        demo.setId(id);
        DemoEntity updated = demoService.updateDemo(demo);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除演示数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDemo(@PathVariable Long id) {
        demoService.deleteDemo(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 初始化示例数据
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initDemoData() {
        demoService.initDemoData();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "初始化示例数据成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        health.put("service", "plugin-demo");
        return ResponseEntity.ok(health);
    }
}

