#!/bin/bash
# Order 模块接口连通性测试
# 用法: bash script/order-api-test.sh [order_port]
# 默认端口: 8026

ORDER_PORT="${1:-8026}"
BASE="http://localhost:${ORDER_PORT}"
PASS=0
FAIL=0

GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

test_api() {
    local method="$1"
    local path="$2"
    local data="$3"
    local desc="$4"

    local headers=(-H "Content-Type: application/json")

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

# ==========================================
# Step 1: 服务存活检测
# ==========================================
echo "--- 1. 服务基础检测 ---"
test_api GET "/v1/mobile/trade/getUserOrderTradeCount" "" "获取订单数量统计"

echo ""
echo "--- 2. 订单C端接口 ---"
test_api POST "/v1/mobile/trade/search" '{"pageNo":1,"pageSize":10}' "分页查询订单"
test_api GET "/v1/mobile/trade/detail/1" "" "查询订单详情(id=1)"
test_api POST "/v1/mobile/trade/getTradeItem" '{"tradeCode":"TEST"}' "查询订单明细"
test_api POST "/v1/mobile/trade/confirm" '{}' "订单确认/预览"
test_api POST "/v1/mobile/trade/submit" '{}' "提交订单"
test_api POST "/v1/mobile/trade/getUserOrderCount" '{}' "用户订单数量"

echo ""
echo "--- 3. 退货C端接口 ---"
test_api POST "/v1/mobile/trade/return/search" '{"pageNo":1,"pageSize":10}' "分页查询退货"
test_api GET "/v1/mobile/trade/return/detail/TEST" "" "退货详情"

echo ""
echo "--- 4. B端配送地址管理 ---"
test_api POST "/v1/tradeDeliveryAddress/searchByPage" '{"pageNo":1,"pageSize":10}' "分页查询配送地址"
test_api GET "/v1/tradeDeliveryAddress/findById" "id=1" "配送地址详情"

echo ""
echo "--- 5. B端退货审核 ---"
test_api POST "/v1/trade/return/searchByPage" '{"pageNo":1,"pageSize":10}' "分页查询退货申请"

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
