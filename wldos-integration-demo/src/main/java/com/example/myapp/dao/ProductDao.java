/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.dao;

import com.wldos.framework.mvc.dao.BaseDao;
import com.example.myapp.entity.Product;

/**
 * 产品数据访问层
 * 演示使用 BaseDao 进行数据访问
 * 
 * 注意：
 * 1. 这是一个接口定义，实际项目中需要使用 Spring Data JDBC 或 MyBatis-Plus 来实现
 * 2. 使用 Spring Data JDBC 时，只需要定义接口，框架会自动实现
 * 3. 使用 MyBatis-Plus 时，需要继承 BaseMapper<Product>
 * 
 * 示例（Spring Data JDBC）：
 * @Repository
 * public interface ProductDao extends BaseDao<Product, Long> {
 *     // 可以添加自定义查询方法
 *     List<Product> findByName(String name);
 * }
 * 
 * 示例（MyBatis-Plus）：
 * @Repository
 * public interface ProductDao extends BaseMapper<Product> {
 *     // MyBatis-Plus 会自动提供 CRUD 方法
 * }
 */
public interface ProductDao extends BaseDao<Product, Long> {
}

