/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.plugin.demo.service;

import com.wldos.plugin.demo.entity.DemoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 演示服务类
 * 作为插件开发者的参考示例
 *
 * @author WLDOS Team
 * @date 2025/12/12
 */
@Slf4j
@Service
public class DemoService {
    
    // 使用内存存储作为示例（实际开发中应使用数据库）
    private final Map<Long, DemoEntity> dataStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * 获取所有演示数据
     */
    public List<DemoEntity> getAllDemos() {
        return new ArrayList<>(dataStore.values());
    }
    
    /**
     * 根据ID获取演示数据
     */
    public DemoEntity getDemoById(Long id) {
        return dataStore.get(id);
    }
    
    /**
     * 创建演示数据
     */
    public DemoEntity createDemo(DemoEntity demo) {
        Long id = idGenerator.getAndIncrement();
        demo.setId(id);
        demo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        demo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        if (demo.getStatus() == null) {
            demo.setStatus(1); // 默认启用
        }
        dataStore.put(id, demo);
        log.info("创建演示数据: {}", demo);
        return demo;
    }
    
    /**
     * 更新演示数据
     */
    public DemoEntity updateDemo(DemoEntity demo) {
        if (demo.getId() == null || !dataStore.containsKey(demo.getId())) {
            throw new RuntimeException("演示数据不存在");
        }
        demo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        dataStore.put(demo.getId(), demo);
        log.info("更新演示数据: {}", demo);
        return demo;
    }
    
    /**
     * 删除演示数据
     */
    public void deleteDemo(Long id) {
        if (!dataStore.containsKey(id)) {
            throw new RuntimeException("演示数据不存在");
        }
        dataStore.remove(id);
        log.info("删除演示数据: id={}", id);
    }
    
    /**
     * 初始化示例数据
     */
    public void initDemoData() {
        if (dataStore.isEmpty()) {
            DemoEntity demo1 = new DemoEntity();
            demo1.setName("示例数据1");
            demo1.setDescription("这是第一个示例数据");
            demo1.setStatus(1);
            createDemo(demo1);
            
            DemoEntity demo2 = new DemoEntity();
            demo2.setName("示例数据2");
            demo2.setDescription("这是第二个示例数据");
            demo2.setStatus(1);
            createDemo(demo2);
            
            log.info("初始化示例数据完成");
        }
    }
}

