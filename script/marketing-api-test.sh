#!/bin/bash
# Marketing 模块接口连通性测试
# 用法: bash script/marketing-api-test.sh [marketing_port] [admin_port]
# 默认端口 marketing: 8024, admin: 8030

MKT_PORT="${1:-8024}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${MKT_PORT}"
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
echo " Marketing 模块接口连通性测试"
echo "  目标: $BASE"
echo "=========================================="
echo ""

echo "--- 1. 优惠券管理 ---"
test_api POST "/v1/coupon/searchByPage" '{"pageNo":1,"pageSize":10}' "优惠券分页"
test_api GET "/v1/coupon/findById" "id=1" "优惠券详情"
test_api POST "/v1/coupon/insert" '{"name":"测试优惠券","type":1,"value":10}' "新增优惠券"
test_api POST "/v1/coupon/update" '{"id":1,"name":"更新优惠券"}' "修改优惠券"
test_api POST "/v1/coupon/deleteByIds" '{"ids":[1]}' "删除优惠券"

echo ""
echo "--- 2. 优惠券发放记录 ---"
test_api POST "/v1/couponUserProvide/searchByPage" '{"pageNo":1,"pageSize":10}' "发放记录分页"
test_api GET "/v1/couponUserProvide/findById" "id=1" "发放记录详情"
test_api POST "/v1/couponUserProvide/insert" '{"couponId":1,"userId":1}' "新增发放记录"
test_api POST "/v1/couponUserProvide/update" '{"id":1,"couponId":1}' "修改发放记录"
test_api POST "/v1/couponUserProvide/deleteByIds" '{"ids":[1]}' "删除发放记录"

echo ""
echo "--- 3. 优惠券领取记录 ---"
test_api POST "/v1/couponUserReceive/searchByPage" '{"pageNo":1,"pageSize":10}' "领取记录分页"
test_api GET "/v1/couponUserReceive/findById" "id=1" "领取记录详情"
test_api POST "/v1/couponUserReceive/insert" '{"couponId":1,"userId":1}' "新增领取记录"
test_api POST "/v1/couponUserReceive/update" '{"id":1,"couponId":1}' "修改领取记录"
test_api POST "/v1/couponUserReceive/deleteByIds" '{"ids":[1]}' "删除领取记录"

echo ""
echo "--- 4. 秒杀商品 ---"
test_api POST "/v1/seckillProduct/searchByPage" '{"pageNo":1,"pageSize":10}' "秒杀商品分页"
test_api GET "/v1/seckillProduct/findById" "id=1" "秒杀商品详情"
test_api POST "/v1/seckillProduct/insert" '{"name":"测试秒杀","price":9.9,"stock":100}' "新增秒杀商品"
test_api POST "/v1/seckillProduct/update" '{"id":1,"name":"更新秒杀"}' "修改秒杀商品"
test_api POST "/v1/seckillProduct/deleteByIds" '{"ids":[1]}' "删除秒杀商品"

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
