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

import com.wldos.framework.mvc.service.EntityService;
import com.example.myapp.dao.ProductDao;
import com.example.myapp.entity.Product;
import org.springframework.stereotype.Service;

/**
 * 产品服务类
 * 演示使用 EntityService 进行实体 CRUD 操作
 * 
 * 注意：
 * 1. 这是一个示例代码，实际项目中需要实现 ProductDao 接口（使用 Spring Data JDBC 或 MyBatis-Plus）
 * 2. EntityService 已经提供了完整的 CRUD 方法：
 *    - insertSelective: 插入实体
 *    - update: 更新实体
 *    - delete: 删除实体
 *    - findById: 根据ID查询
 *    - findAll: 查询所有
 *    - findAll: 分页查询
 *    等等...
 * 3. 如果需要自定义业务方法，可以在这里添加
 */
@Service
public class ProductService extends EntityService<ProductDao, Product, Long> {
    
    // 注意：由于 ProductDao 是接口，实际项目中需要实现这个接口
    // 可以使用 Spring Data JDBC 或 MyBatis-Plus 来实现
    // 示例：
    // @Repository
    // public interface ProductDao extends BaseDao<Product, Long> {
    //     // Spring Data JDBC 会自动实现
    // }
}

