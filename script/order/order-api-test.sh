#!/bin/bash
# Order 模块接口连通性测试
# 用法: bash script/order-api-test.sh [order_port] [admin_port]
# 默认端口 order: 8026, admin: 8030
# 说明: C端和B端接口都需要认证

ORDER_PORT="${1:-8026}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${ORDER_PORT}"
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

    local headers=(-H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN")

    if [ "$method" = "GET" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" "${headers[@]}" "${BASE}${path}${data:+?$data}" 2>/dev/null)
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" "${headers[@]}" -d "${data:-{}}" "${BASE}${path}" 2>/dev/null)
    fi

    if [ "$response" = "200" ] || [ "$response" = "201" ]; then
        echo -e "  ${GREEN}✅${NC} $response $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}❌${NC} $response $desc ($method $path)"
        FAIL=$((FAIL + 1))
    fi
}

echo ""
echo "=========================================="
echo " Order 模块接口连通性测试"
echo "  目标: $BASE"
echo "=========================================="
echo ""

echo "--- 1. 订单C端接口 ---"
test_api GET "/v1/mobile/trade/getUserOrderTradeCount" "" "获取订单数量统计"
test_api POST "/v1/mobile/trade/search" '{"pageNo":1,"pageSize":10}' "分页查询订单"
test_api GET "/v1/mobile/trade/detail/1" "" "查询订单详情"
test_api POST "/v1/mobile/trade/getTradeItem" '{"tradeCode":"TEST"}' "查询订单明细"
test_api POST "/v1/mobile/trade/confirm" '{}' "订单确认/预览"
test_api POST "/v1/mobile/trade/submit" '{}' "提交订单"

echo ""
echo "--- 2. 退货C端接口 ---"
test_api POST "/v1/mobile/trade/return/search" '{"pageNo":1,"pageSize":10}' "分页查询退货"
test_api GET "/v1/mobile/trade/return/detail/TEST" "" "退货详情"

echo ""
echo "--- 3. B端配送地址管理 ---"
test_api POST "/v1/tradeDeliveryAddress/searchByPage" '{"pageNo":1,"pageSize":10}' "分页查询配送地址"
test_api GET "/v1/tradeDeliveryAddress/findById" "id=1" "配送地址详情"

echo ""
echo "--- 4. B端退货审核 ---"
test_api POST "/v1/trade/return/searchByPage" '{"pageNo":1,"pageSize":10}' "分页查询退货申请"

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
