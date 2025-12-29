/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.service;

import com.example.myapp.dto.UserCreateDTO;
import com.example.myapp.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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

