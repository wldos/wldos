# MP 金标准：文章（Article）模块



本页对应代码路径，演示**真实实体** `com.example.myapp.entity.Article`。



## 目录与职责



| 文件 | 职责 |

|------|------|

| `entity/Article.java` | 继承 `BaseEntity`，`@TableName("demo_article")` |

| `param/ArticlePageParam.java` | 继承 `PageParam`，含 `author`、`status`、`keyword` |

| `result/ArticlePageResult.java` | 列表行 VO |

| `mapper/ArticleMapper.java` + `mapper/mapping/ArticleMapper.xml`（与 Mapper 同包树） | `BaseMapper` + XML 分页 |

| `service/ArticleService.java` | `ServiceImpl` + `param.toPageQuery()` + `WldosScope` |

| `controller/ArticleController.java` | 不继承 `EntityController`；`WldosContext` / `WldosWriteScope` |

| `config/DemoMpDatabaseInitializer.java` | 启动时加载 `db/demo-article-h2.sql` |

| `resources/db/demo-article-h2.sql` | H2 表结构与种子数据 |


## 借鉴第三方写法（非复制业务）



常见习惯：`XxxParam` 继承分页基类、`pageNum`/`pageSize`、列表 **POST JSON**、列表出参 **Result VO**。demo 用 `ArticlePageParam` / `ArticlePageResult` 表达，响应仍为 wldos `PageData`。



## 调用示例



```bash

# GET 分页（ProTable 扁平）

curl "http://localhost:8080/api/articles/mp-list?current=1&pageSize=10&author=张三"



# POST 分页（Body 可用 pageNum）

curl -X POST "http://localhost:8080/api/articles/mp-page" -H "Content-Type: application/json" -d "{\"pageNum\":1,\"pageSize\":10,\"author\":\"张三\"}"



# 新增（审计字段由 MyMetaObjectHandler 写入）

curl -X POST "http://localhost:8080/api/articles/mp-add" ^

  -H "Content-Type: application/json" ^

  -d "{\"title\":\"MP 测试\",\"content\":\"正文\",\"author\":\"演示\"}"

```



响应经 `GlobalResponseHandler` 包装为 `Result`，`data` 内为 `PageData`（`rows` 为 `ArticlePageResult`）或单条 `Article`。



## 前端 ProTable



`request` 传参保持扁平：`current`、`pageSize`、以及 `ArticlePageParam` 上的业务字段；`sorter` 传 JSON 字符串，与现网 `PageQuery` 一致。



更多说明见 `docs/framework/第三方MyBatis-Plus快速入门.md`.

## Redis（跨实例共享缓存与锁）

`ArticleCacheService` 直接注入 Spring Boot 的 `StringRedisTemplate`，连接配置是 `spring.data.redis.*`，缓存和锁都由业务应用自己实现，wldos 不提供封装。Redis 不可用时接口直接报错。

- `GET /api/articles/detail/1983123456789010001` 先读 `demo:article:{id}`，未命中查库并回写，10 分钟过期。
- `POST /api/articles/detail/1983123456789010001/view` 用 `demo:lock:article:view:{id}` 加锁后再加阅读数，并删掉文章缓存。

平台自带的 `this.cache`（`ICache`）始终是本机内存，与 Redis、Mesh 无关。业务键用应用自己的前缀，不要用 `wldos:`。

