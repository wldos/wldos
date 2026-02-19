#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 MySQL init.sql 转换为 H2 兼容的 init-h2.sql
cd wldos 执行如下命令
python modules/wldos-platform/scripts/mysql-to-h2.py
"""
import re
import sys
from pathlib import Path

# 占位符：用于 \\ 的临时替换，避免 \n \t \r 被误解析
_PLACEHOLDER_BS = '\x01'


def _transform_mysql_escapes_in_string(inner: str) -> str | None:
    """
    将 MySQL 字符串字面量内的转义序列转为 H2 等价形式。
    MySQL: \\n->换行 \\t->制表 \\r->回车 \\\\->反斜杠 \\"->双引号
    H2: 不解释反斜杠，需用 CHAR(10) 等或字面量。
    返回 None 表示无需转换。
    """
    if '\\' not in inner:
        return None
    # 先保护 \\，避免 \n \t \r \" 中的 \ 被误解析
    s = inner.replace('\\\\', _PLACEHOLDER_BS)
    # MySQL \" 在单引号串内表示双引号，H2 直接写 " 即可
    s = s.replace('\\"', '"')
    # 仅有 \" 时也需返回转换结果（如 JSON 串），否则 H2 会按字面量存储
    if '\\n' not in s and '\\t' not in s and '\\r' not in s and _PLACEHOLDER_BS not in s:
        return "'" + s.replace("'", "''") + "'"
    parts = re.split(r'(\\n|\\t|\\r)', s)
    result = []
    for i, p in enumerate(parts):
        if i % 2 == 0:
            escaped = p.replace(_PLACEHOLDER_BS, '\\').replace("'", "''")
            result.append("'" + escaped + "'")
        else:
            if p == '\\n':
                result.append('CHAR(10)')
            elif p == '\\t':
                result.append('CHAR(9)')
            elif p == '\\r':
                result.append('CHAR(13)')
    return ' || '.join(result)


def _transform_mysql_escapes_in_strings(content: str) -> str:
    """遍历所有 SQL 字符串字面量，将 MySQL 转义转为 H2 等价。"""
    def repl(m):
        inner = m.group(1)
        transformed = _transform_mysql_escapes_in_string(inner)
        if transformed is not None:
            return transformed
        return m.group(0)
    return re.sub(r"'((?:[^']|'')*)'", repl, content)


def transform(content: str) -> str:
    # 1. 移除 MySQL 特有 SET 语句（re.M 使 ^/$ 匹配行首/行尾）
    content = re.sub(r'^SET NAMES utf8mb4;\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^SET FOREIGN_KEY_CHECKS = 0;\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'\nSET FOREIGN_KEY_CHECKS = 1;\s*$', '', content, flags=re.MULTILINE)

    # 2. 移除表尾 ENGINE/CHARACTER SET/ROW_FORMAT/COMMENT
    content = re.sub(
        r'\)\s*ENGINE\s*=\s*InnoDB\s*(?:CHARACTER SET\s*=\s*\w+\s*)?(?:COLLATE\s*=\s*\w+\s*)?(?:COMMENT\s*=\s*[^;]*)?\s*ROW_FORMAT\s*=\s*Dynamic\s*;',
        ');',
        content,
        flags=re.IGNORECASE
    )
    # 处理无 ROW_FORMAT 的情况
    content = re.sub(
        r'\)\s*ENGINE\s*=\s*InnoDB\s*(?:CHARACTER SET\s*=\s*\w+\s*)?(?:COLLATE\s*=\s*\w+\s*)?(?:COMMENT\s*=\s*[^;]*)?\s*;',
        ');',
        content,
        flags=re.IGNORECASE
    )

    # 3. 移除列定义中的 CHARACTER SET / COLLATE
    content = re.sub(
        r'\s+CHARACTER SET\s+utf8mb4\s+COLLATE\s+utf8mb4_\w+\s*',
        ' ',
        content,
        flags=re.IGNORECASE
    )

    # 4. bigint(20) UNSIGNED -> BIGINT
    content = re.sub(r'bigint\s*\(\s*20\s*\)\s+UNSIGNED', 'BIGINT', content, flags=re.IGNORECASE)
    content = re.sub(r'bigint\s*\(\s*\d+\s*\)', 'BIGINT', content, flags=re.IGNORECASE)

    # 5. int(n) UNSIGNED -> INT
    content = re.sub(r'int\s*\(\s*\d+\s*\)\s+UNSIGNED', 'INT', content, flags=re.IGNORECASE)

    # 6. tinytext -> VARCHAR(255)
    content = re.sub(r'tinytext\b', 'VARCHAR(255)', content, flags=re.IGNORECASE)

    # 7. mediumtext -> CLOB
    content = re.sub(r'mediumtext\b', 'CLOB', content, flags=re.IGNORECASE)

    # 8. 移除 USING BTREE
    content = re.sub(r'\s+USING\s+BTREE', '', content, flags=re.IGNORECASE)

    # 9. 索引长度 (191) -> H2 不支持索引前缀，改为完整列
    content = re.sub(r'\(\s*`(\w+)`\s*\(\s*\d+\s*\)\s*\)', r'(`\1`)', content)

    # 10. 无效日期 '0000-00-00 00:00:00' -> '1970-01-01 00:00:00'
    content = content.replace("'0000-00-00 00:00:00'", "'1970-01-01 00:00:00'")

    # 10.5 MySQL 反斜杠转义单引号 \' -> SQL 标准 ''（H2 不支持 \' 转义）
    content = content.replace("\\'", "''")

    # 10.6 MySQL 字符串转义 \n \t \r \\ -> H2 等价（H2 不解释反斜杠转义，需用 CHAR() 或字面量）
    content = _transform_mysql_escapes_in_strings(content)

    # 11. H2 索引名全局唯一：为 INDEX/UNIQUE INDEX 加表名前缀，避免 meta_key/post_id/idx_user_id 等重复
    lines = content.split('\n')
    result = []
    current_table = None
    for line in lines:
        m = re.match(r'CREATE TABLE `(\w+)`', line)
        if m:
            current_table = m.group(1)
        if current_table and ('INDEX `' in line or 'UNIQUE INDEX `' in line):
            def repl(m):
                idx_name = m.group(2)
                # 已包含表名前缀则不再添加，避免 k_pubmeta_meta_key -> k_pubmeta_k_pubmeta_meta_key
                if idx_name.startswith(current_table + '_'):
                    return m.group(0)
                return m.group(1) + ' `' + current_table + '_' + idx_name + '`'
            line = re.sub(r'(INDEX|UNIQUE INDEX) `(\w+)`', repl, line)
        if line.strip() == ');':
            current_table = None
        result.append(line)
    content = '\n'.join(result)

    return content

def main():
    import os
    import shutil
    base = Path(__file__).resolve().parent.parent
    src = base / 'src' / 'main' / 'resources' / 'db' / 'init.sql'
    dst = base / 'src' / 'main' / 'resources' / 'db' / 'init-h2.sql'
    tmp = Path(os.environ.get('TEMP', '/tmp')) / 'init-h2-output.sql'

    if not src.exists():
        print(f'Error: {src} not found')
        sys.exit(1)

    content = src.read_text(encoding='utf-8')
    transformed = transform(content)
    tmp.write_text(transformed, encoding='utf-8')
    try:
        shutil.copy(tmp, dst)
        tmp.unlink(missing_ok=True)
        print(f'Generated {dst}')
    except (PermissionError, OSError) as e:
        print(f'Warning: Could not overwrite {dst}: {e}')
        print(f'Output saved to {tmp} - please manually copy to {dst}')

if __name__ == '__main__':
    main()
