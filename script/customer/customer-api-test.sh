#!/bin/bash
# Customer 模块接口连通性测试
# 用法: bash script/customer-api-test.sh [customer_port] [admin_port]
# 默认端口 customer: 8025, admin: 8030

CUS_PORT="${1:-8025}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${CUS_PORT}"
ADMIN_BASE="http://localhost:${ADMIN_PORT}"
PASS=0
FAIL=0

GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

echo "--- 获取 Token ---"
TOKEN_RESP=$(curl -s -X POST "${ADMIN_BASE}/v1/auth/user/testLogin" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')
TOKEN=$(echo "$TOKEN_RESP" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "  ${RED}❌ 获取 Token 失败${NC}"
    exit 1
fi
echo -e "  ${GREEN}✅${NC} Token 获取成功"
PASS=$((PASS + 1))

test_api() {
    local method="$1"
    local path="$2"
    local data="$3"
    local desc="$4"
    local use_token="${5:-true}"

    if [ "$use_token" = "true" ]; then
        headers=(-H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN")
    else
        headers=(-H "Content-Type: application/json")
    fi

    if [ "$method" = "GET" ]; then
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${headers[@]}" "${BASE}${path}${data:+?$data}" 2>/dev/null)
    else
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${headers[@]}" -d "${data:-{}}" "${BASE}${path}" 2>/dev/null)
    fi

    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
        echo -e "  ${GREEN}✅${NC} $HTTP_CODE $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}❌${NC} $HTTP_CODE $desc ($method $path)"
        FAIL=$((FAIL + 1))
    fi
}

echo ""
echo "=========================================="
echo " Customer 模块接口连通性测试"
echo "  目标: $BASE"
echo "=========================================="
echo ""

echo "--- 1. C端用户（用 admin token 模拟）---"
test_api POST "/v1/mobile/user/register" '{"phone":"13800138001","password":"123456","code":"8888"}' "用户注册" true
test_api POST "/v1/mobile/user/login" '{"username":"admin","password":"admin123"}' "用户登录" true
test_api POST "/v1/mobile/user/loginByPhone" '{"phone":"13800138001","code":"8888"}' "手机号登录" true
test_api GET "/v1/mobile/user/getCode" "phone=13800138001" "获取验证码" true
test_api GET "/v1/mobile/user/detail" "" "用户详情" true
test_api POST "/v1/mobile/user/update" '{"nickName":"测试用户"}' "修改信息" true
test_api POST "/v1/mobile/user/logout" "" "退出登录" true

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
