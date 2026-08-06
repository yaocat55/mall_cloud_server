"""
自动推断 DTO 字段的 @Schema example 值
用法: python3 patch_dto_example.py <目录...>

仅处理字段级别的 @Schema，不会碰类级别的注解。
"""

import re
import sys
import os
from pathlib import Path

# (字段名正则, 类型正则, example值)
EXAMPLE_RULES = [
    (r'(?i)userName|nickName|name\b(?!\w)', 'string', '"admin"'),
    (r'(?i)password|pwd|tokenSecret', 'string', '"******"'),
    (r'(?i)email', 'string', '"user@example.com"'),
    (r'(?i)phone|mobile', 'string', '"13800138000"'),
    (r'(?i)avatarUrl|avatar|imageUrl|image|url|cover|icon|photo', 'string', '"https://example.com/1.png"'),
    (r'(?i)remark|desc\b|description|note|intro', 'string', '"备注信息"'),
    (r'(?i)address', 'string', '"北京市朝阳区"'),
    (r'(?i)token', 'string', '"eyJhbGciOiJIUzI1NiJ9.xxx"'),
    (r'(?i)(^id$|Id$)', '(Long|long|Integer|int)', '"1"'),
    (r'(?i)deptId|roleId|jobId|menuId|userId|parentId|createUserId|updateUserId|avatarId', '(Long|long|Integer|int)', '"1"'),
    (r'(?i)sort|orderNum|weight|level', '(int|integer|Integer|Long)', '"0"'),
    (r'(?i)page$|pageSize|pageNo|totalPage|totalCount', '(int|integer|Integer|Long)', '"0"'),
    (r'(?i)status|validStatus|enabled|isDel|deleted', 'boolean', '"true"'),
    (r'(?i)sex|gender', '(int|integer)', '"1"'),
    (r'(?i)(birthday|createTime|updateTime|lastLoginTime|lastChangePasswordTime)', 'Date', '"2024-01-01 00:00:00"'),
    (r'(?i)count|totalCount|total', 'long', '"0"'),
    (r'(?i)permission|perms', 'string', '"system:user:list"'),
    (r'(?i)dataScope|scope', 'string', '"all"'),
    (r'(?i)className|beanName', 'string', '"cn.net.mall.service.XxxService"'),
    (r'(?i)cronExpression|cron', 'string', '"0 0 12 * * ?"'),
    (r'(?i)dictType|dictCode', 'string', '"sys_user_sex"'),
    (r'(?i)label', 'string', '"男"'),
    (r'(?i)value', 'string', '"0"'),
    (r'(?i)key|code', 'string', '"CODE_001"'),
    (r'(?i)content|message|msg', 'string', '"内容"'),
    (r'(?i)title|subject', 'string', '"标题"'),
    (r'(?i)config|configKey|configValue', 'string', '"config_value"'),
]


def infer_example(field_type, field_name):
    for name_pat, type_pat, ex in EXAMPLE_RULES:
        if re.search(name_pat, field_name) and re.search(type_pat, field_type, re.IGNORECASE):
            return ex
    ft = field_type
    if 'String' in ft:      return '"-"'
    if re.search(r'int|long|Long|Integer|double|Double|float|Float|BigDecimal|short|Short|byte|Byte', ft): return '"0"'
    if re.search(r'bool', ft, re.I): return '"true"'
    if re.search(r'Date|LocalDateTime|LocalDate', ft): return '"2024-01-01 00:00:00"'
    if re.search(r'List|Set|Collection|\[\]', ft): return '"[]"'
    return '"-"'


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.read().split('\n')

    modified = False
    result = []
    i = 0

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # 识别字段定义行: 可能包含 private ... Type fieldName;
        fm = re.match(r'^(\s*)(?:public\s+)?private\s+(?:static\s+)?(?:final\s+)?'
                      r'(\S+(?:\s*<[^>]*>)?)\s+(\w+)\s*[;=]', stripped)

        if fm:
            indent = fm.group(1)
            field_type = fm.group(2)
            field_name = fm.group(3)

            # 检查字段上方是否有 @Schema（跳过其他注解行和空行）
            found_schema = False
            needs_example = False
            existing_line_idx = -1

            lookback = 1
            while i - lookback >= 0 and lookback <= 8:
                check_line = lines[i - lookback].strip()
                if check_line == '':
                    lookback += 1
                    continue
                if check_line.startswith('@Schema'):
                    found_schema = True
                    existing_line_idx = i - lookback
                    if 'example' not in check_line:
                        needs_example = True
                    break
                if not check_line.startswith('@'):  # 遇到非注解行停止
                    break
                lookback += 1

            if found_schema:
                if needs_example:
                    ex = infer_example(field_type, field_name)
                    old = lines[existing_line_idx]
                    new = re.sub(r'@Schema\(', f'@Schema(example = {ex}, ', old)
                    if new != old:
                        lines[existing_line_idx] = new
                        modified = True
                # 已有 example 则跳过
            else:
                # 没有 @Schema，在字段行前插入
                ex = infer_example(field_type, field_name)
                result.append(f'{indent}@Schema(example = {ex})')
                modified = True

        result.append(line)
        i += 1

    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(result))
        return True
    return False


def main():
    dirs = sys.argv[1:] if len(sys.argv) > 1 else []
    if not dirs:
        print("用法: python3 patch_dto_example.py <目录...>")
        sys.exit(1)

    total = patched = 0
    for d in dirs:
        d = d.replace('/', os.sep)
        if not os.path.isdir(d):
            print(f"[跳过] 不存在: {d}")
            continue
        cnt = 0
        for f in sorted(Path(d).rglob("*.java")):
            total += 1
            if process_file(str(f)):
                patched += 1
                cnt += 1
        print(f"[{os.path.basename(d.rstrip(os.sep))}] {cnt} files patched")

    print(f"\nTotal: scanned {total}, patched {patched}")


if __name__ == '__main__':
    main()
