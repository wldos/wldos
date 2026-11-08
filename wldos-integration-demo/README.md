# WLDOS 第三方集成示例（MyBatis-Plus）

演示在自有 Spring Boot 项目中依赖 `wldos-framework`、采用**标准 MyBatis-Plus** 接入 wldos（统一 `Result`、分页、`WldosContext` / 写隔离等），**不**使用 `EntityController` / `EntityService` / `BaseDao`。

## 代码结构

```
com.example.myapp/
├── entity/Article.java
├── param/ArticlePageParam.java
├── result/ArticlePageResult.java
├── mapper/ArticleMapper.java
├── mapper/mapping/ArticleMapper.xml
├── service/ArticleService.java
├── controller/ArticleController.java
├── service/ArticleCacheService.java        （Redis 缓存与锁，直接用 Spring Boot 的 StringRedisTemplate）
├── controller/ArticleCacheController.java
├── result/ArticleCacheView.java
└── config/DemoMpDatabaseInitializer.java
```

## 运行

```bash
cd wldos-integration-demo
mvn spring-boot:run
```

- Swagger：http://localhost:8080/doc.html  
- MP 分页（GET）：http://localhost:8080/api/articles/mp-list?current=1&pageSize=10  
- MP 分页（POST JSON）：`/api/articles/mp-page`  
- MP 新增：`POST /api/articles/mp-add`
以下两个接口使用 Redis，需要先启动本机 Redis（连接在 `application.yml` 的 `spring.data.redis.*`）：

- 文章缓存读取：`GET /api/articles/detail/{id}`（`source`：`db` 读库并回写，`redis` 命中）
- 阅读数 +1：`POST /api/articles/detail/{id}/view`（先拿锁 `demo:lock:article:view:{id}`）

Redis 是 Spring Boot 的标准能力，缓存和分布式锁由业务应用自己实现，wldos 不提供封装。键请用应用自己的前缀，不要用 `wldos:`。其余接口不连 Redis。

## 配置

`application.yml` 中 `wldos.framework.base-package: com.example.myapp`；MyBatis-Plus 逻辑删与线上一致（`normal` / `deleted`）。

## 文档

- 模块说明：`docs/MP金标准-文章模块.md`
- 平台文档：`docs/framework/第三方MyBatis-Plus快速入门.md`
