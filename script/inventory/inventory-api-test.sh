#!/bin/bash
# Inventory 模块接口连通性测试
# 用法: bash script/inventory-api-test.sh [inventory_port] [admin_port]
# 默认端口: 8036, admin: 8030
# 说明: 库存接口已配置白名单，无需 Token

INVENTORY_PORT="${1:-8036}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${INVENTORY_PORT}"
ADMIN_BASE="http://localhost:${ADMIN_PORT}"
PASS=0
FAIL=0

GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

# 获取 admin token（部分接口需要认证）
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
    local use_token="$5"

    local headers=(-H "Content-Type: application/json")
    [ "$use_token" = "true" ] && [ -n "$TOKEN" ] && headers+=(-H "Authorization: Bearer $TOKEN")

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

# 获取第一个有库存的商品ID
FIRST_PID=$(docker exec mysql81 mysql -uroot -p123456 cloud_mall_inventory -e "SELECT product_id FROM inventory LIMIT 1;" 2>/dev/null | grep -v Warning | tail -1)

echo ""
echo "=========================================="
echo " Inventory 库存模块接口连通性测试"
echo "  目标: $BASE"
echo "  商品ID: $FIRST_PID"
echo "=========================================="
echo ""

echo "--- 1. 库存查询 ---"
test_api GET "/v1/inventory/${FIRST_PID}" "" "查询单个商品库存" true
test_api POST "/v1/inventory/batch" "[${FIRST_PID}]" "批量查询库存" true

echo ""
echo "--- 2. 冻结库存 ---"
test_api POST "/v1/inventory/freeze" "{\"productId\":${FIRST_PID},\"quantity\":1,\"orderId\":20260717001}" "冻结库存（Redis Lua 原子扣减）" true

echo ""
echo "--- 3. 确认扣减 ---"
test_api POST "/v1/inventory/confirm" "{\"productId\":${FIRST_PID},\"quantity\":1,\"orderId\":20260717001}" "确认扣减（支付回调）" true

echo ""
echo "--- 4. 释放冻结 ---"
test_api POST "/v1/inventory/unfreeze" "{\"productId\":${FIRST_PID},\"quantity\":1,\"orderId\":20260717003}" "释放冻结（取消订单）" true

echo ""
echo "--- 5. 回库（退货）---"
test_api POST "/v1/inventory/return" "{\"productId\":${FIRST_PID},\"quantity\":1,\"orderId\":20260717004,\"remark\":\"退货测试\"}" "回库（退货）" true

echo ""
echo "--- 6. 入库（采购）---"
test_api POST "/v1/inventory/inbound" "{\"productId\":${FIRST_PID},\"quantity\":100,\"batchNo\":\"PO20260717-TEST\",\"supplier\":\"测试供应商\",\"purchasePrice\":99.99,\"warehouse\":\"A区\"}" "入库（新增批次）" true

echo ""
echo "--- 7. 查询验证 ---"
test_api GET "/v1/inventory/${FIRST_PID}" "" "查询最终库存状态" true

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
