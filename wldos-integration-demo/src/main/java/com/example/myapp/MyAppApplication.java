/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WLDOS 第三方集成示例应用主类
 * 
 * 启动后访问：
 * - API 文档：http://localhost:8080/doc.html
 * - 健康检查：http://localhost:8080/api/users/list
 */
@SpringBootApplication
public class MyAppApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MyAppApplication.class, args);
    }
}

