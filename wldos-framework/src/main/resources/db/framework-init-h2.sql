/*
 * H2 兼容的框架层初始化脚本
 * 桌面版使用 H2 嵌入式数据库时加载
 */

-- wo_file 文件存储表
CREATE TABLE IF NOT EXISTS wo_file (
  id BIGINT NOT NULL,
  name VARCHAR(255) NULL,
  path VARCHAR(500) NULL,
  mime_type VARCHAR(100) NULL,
  is_valid CHAR(1) NULL,
  create_by BIGINT NULL,
  create_time TIMESTAMP NULL,
  create_ip VARCHAR(50) NULL,
  update_by BIGINT NULL,
  update_time TIMESTAMP NULL,
  update_ip VARCHAR(50) NULL,
  delete_flag VARCHAR(10) NULL,
  versions INT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_file_is_valid_del ON wo_file(is_valid, delete_flag);
CREATE INDEX IF NOT EXISTS idx_file_mime_type ON wo_file(mime_type);
CREATE INDEX IF NOT EXISTS idx_file_path ON wo_file(path);
CREATE INDEX IF NOT EXISTS idx_file_create_time ON wo_file(create_time);
