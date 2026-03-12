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

import com.example.myapp.dto.UserCreateDTO;
import com.example.myapp.vo.UserVO;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 
 * 这是一个简单的内存实现，仅用于演示
 */
@Service
public class UserService {
    
    // 使用内存存储，仅用于演示
    private final ConcurrentHashMap<Long, UserVO> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * 初始化一些示例数据
     */
    public UserService() {
        // 初始化示例数据
        userStore.put(1L, new UserVO(1L, "zhangsan", "zhangsan@example.com", "张三"));
        userStore.put(2L, new UserVO(2L, "lisi", "lisi@example.com", "李四"));
        userStore.put(3L, new UserVO(3L, "wangwu", "wangwu@example.com", "王五"));
        idGenerator.set(4L);
    }
    
    /**
     * 获取用户列表
     */
    public List<UserVO> getUserList() {
        return new ArrayList<>(userStore.values());
    }

    /**
     * 分页列表（演示 Map + PageQuery + PageData 用法）
     * Mock 实现：从内存过滤、分页
     *
     * @param pageQuery 分页查询参数（condition 可含 username、nickname 等）
     * @return 分页数据
     */
    public PageData<UserVO> pageList(PageQuery pageQuery) {
        List<UserVO> all = new ArrayList<>(userStore.values());
        if (pageQuery != null && pageQuery.getCondition() != null) {
            Object username = pageQuery.getCondition().get("username");
            if (!ObjectUtils.isBlank(username)) {
                String kw = username.toString().toLowerCase();
                all = all.stream().filter(u -> u.getUsername() != null && u.getUsername().toLowerCase().contains(kw)).collect(Collectors.toList());
            }
            Object nickname = pageQuery.getCondition().get("nickname");
            if (!ObjectUtils.isBlank(nickname)) {
                String kw = nickname.toString();
                all = all.stream().filter(u -> u.getNickname() != null && u.getNickname().contains(kw)).collect(Collectors.toList());
            }
        }
        int total = all.size();
        int current = pageQuery != null ? pageQuery.getCurrent() : 1;
        int pageSize = pageQuery != null ? pageQuery.getPageSize() : 10;
        int from = Math.min((current - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        List<UserVO> rows = all.subList(from, to);
        return new PageData<>(total, current, pageSize, rows);
    }
    
    /**
     * 根据ID获取用户
     */
    public UserVO getUserById(Long id) {
        return userStore.get(id);
    }
    
    /**
     * 创建用户
     */
    public UserVO createUser(UserCreateDTO dto) {
        Long id = idGenerator.getAndIncrement();
        UserVO user = new UserVO(id, dto.getUsername(), dto.getEmail(), dto.getNickname());
        userStore.put(id, user);
        return user;
    }
    
    /**
     * 更新用户
     */
    public UserVO updateUser(Long id, UserCreateDTO dto) {
        UserVO user = userStore.get(id);
        if (user == null) {
            return null;
        }
        
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        userStore.put(id, user);
        
        return user;
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        return userStore.remove(id) != null;
    }
}

