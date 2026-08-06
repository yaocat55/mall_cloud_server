#!/bin/bash
# Message 模块接口连通性测试
# 用法: bash script/message-api-test.sh [port] [admin_port]
MSG_PORT="${1:-8029}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${MSG_PORT}"
ADMIN_BASE="http://localhost:${ADMIN_PORT}"
PASS=0; FAIL=0

GREEN='\033[32m'; RED='\033[31m'; NC='\033[0m'

echo "--- 获取 Token ---"
TOKEN=$(curl -s -X POST "${ADMIN_BASE}/v1/auth/user/testLogin" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
[ -z "$TOKEN" ] && echo -e "  ${RED}❌ Token 失败${NC}" && exit 1
echo -e "  ${GREEN}✅${NC} Token 获取成功"; PASS=$((PASS+1))

test_api() {
    local m="$1"; local p="$2"; local d="$3"; local t="$4"
    local h=(-H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN")
    if [ "$m" = "GET" ]; then
        c=$(curl -s -o /dev/null -w "%{http_code}" "${h[@]}" "${BASE}${p}${d:+?$d}" 2>/dev/null)
    else
        c=$(curl -s -o /dev/null -w "%{http_code}" "${h[@]}" -d "${d:-{}}" "${BASE}${p}" 2>/dev/null)
    fi
    if [ "$c" = "200" ]; then echo -e "  ${GREEN}✅${NC} $c $t"; PASS=$((PASS+1))
    else echo -e "  ${RED}❌${NC} $c $t ($m $p)"; FAIL=$((FAIL+1)); fi
}

echo ""; echo "=========================================="; echo " Message 模块接口测试"; echo "  目标: $BASE"; echo "=========================================="; echo ""

echo "--- 1. 消息通知 ---"
test_api POST "/v1/message/notify/searchByPage" '{"pageNo":1,"pageSize":10}' "通知分页"
test_api GET "/v1/message/notify/findById" "id=1" "通知详情"
test_api POST "/v1/message/notify/push/all" '{"content":"测试全站通知"}' "全站推送"

echo ""; echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS+FAIL))"
echo "=========================================="; echo ""
[ "$FAIL" -gt 0 ] && exit 1 || exit 0
