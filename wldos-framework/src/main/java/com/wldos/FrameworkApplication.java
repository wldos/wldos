/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WLDOS开发框架启动类，启动此类需要配置数据库参数，子模块种配置了可以直接引用该模块。
 *
 * @author 元悉宇宙
 * @date 2021/12/23
 * @version 1.0
 */
@SpringBootApplication
public class FrameworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrameworkApplication.class, args);
	}
}
