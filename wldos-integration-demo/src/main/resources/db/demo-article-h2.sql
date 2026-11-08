-- MP 金标准示例表（H2），与 com.example.myapp.entity.Article + BaseEntity 字段对齐

CREATE TABLE IF NOT EXISTS demo_article (
  id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content CLOB,
  author VARCHAR(64),
  views INT DEFAULT 0,
  status INT DEFAULT 0,
  create_by BIGINT,
  create_time TIMESTAMP,
  create_ip VARCHAR(50),
  update_by BIGINT,
  update_time TIMESTAMP,
  update_ip VARCHAR(50),
  delete_flag VARCHAR(10) DEFAULT 'normal',
  is_valid VARCHAR(10) DEFAULT '1',
  versions INT DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_demo_article_author ON demo_article(author);
CREATE INDEX IF NOT EXISTS idx_demo_article_status ON demo_article(status);

-- 种子数据（与 Mock 内存示例语义一致，供 MP 列表接口查询）
INSERT INTO demo_article (id, title, content, author, views, status, create_time, update_time, delete_flag, is_valid, versions)
SELECT 1983123456789010001, 'Spring Boot 入门指南', 'Spring Boot 让 Java 开发更简单...', '张三', 120, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'normal', '1', 0
WHERE NOT EXISTS (SELECT 1 FROM demo_article WHERE id = 1983123456789010001);

INSERT INTO demo_article (id, title, content, author, views, status, create_time, update_time, delete_flag, is_valid, versions)
SELECT 1983123456789010002, 'MyBatis-Plus 实战', 'LambdaQueryWrapper 与分页插件...', '李四', 88, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'normal', '1', 0
WHERE NOT EXISTS (SELECT 1 FROM demo_article WHERE id = 1983123456789010002);

INSERT INTO demo_article (id, title, content, author, views, status, create_time, update_time, delete_flag, is_valid, versions)
SELECT 1983123456789010003, '数据库设计规范', '良好的数据库设计是系统稳定的基础...', '王五', 150, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'normal', '1', 0
WHERE NOT EXISTS (SELECT 1 FROM demo_article WHERE id = 1983123456789010003);
