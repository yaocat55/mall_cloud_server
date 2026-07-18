#!/bin/bash
# Admin BFF 接口测试
# 用法: bash script/admin-bff-test.sh

BASE="http://localhost:8090"
PASS=0
FAIL=0
GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

test_api() {
    local method="$1" path="$2" data="$3" desc="$4" token="$5"
    local headers=(-H "Content-Type: application/json")
    [ -n "$token" ] && headers+=(-H "Authorization: Bearer $token")

    if [ "$method" = "GET" ]; then
        resp=$(curl -s -w "\n%{http_code}" "${headers[@]}" "${BASE}${path}${data:+?$data}" 2>/dev/null)
    else
        resp=$(curl -s -w "\n%{http_code}" "${headers[@]}" -d "${data:-{}}" "${BASE}${path}" 2>/dev/null)
    fi

    http_code=$(echo "$resp" | tail -1)
    body=$(echo "$resp" | sed '$d')

    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        echo -e "  ${GREEN}✅${NC} $http_code $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}❌${NC} $http_code $desc"
        echo "     Body: $body"
        FAIL=$((FAIL + 1))
    fi
}

echo ""
echo "=========================================="
echo " Admin BFF 接口测试"
echo " 目标: $BASE"
echo "=========================================="
echo ""

# 登录
echo "--- Step 1: 获取 Token ---"
TOKEN_RESP=$(curl -s -X POST "http://localhost:8030/v1/auth/user/testLogin" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')
TOKEN=$(echo "$TOKEN_RESP" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ 登录失败${NC}"
    echo "$TOKEN_RESP"
    exit 1
fi
echo -e "  ${GREEN}✅${NC} Token 获取成功"
echo ""

T=$TOKEN

# ==========================================
# 商品管理
# ==========================================
echo "--- 商品管理 ---"
test_api "POST" "/admin/v1/product/page" '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "商品分页" "$T"
test_api "GET" "/admin/v1/product/detail" "id=1833339862330556416" "商品详情" "$T"
test_api "GET" "/admin/v1/product/1/edit-data" "" "商品编辑数据" "$T"

# ==========================================
# 库存管理
# ==========================================
echo "--- 库存管理 ---"
test_api "GET" "/admin/v1/inventory/1833339862330556416" "" "查询商品库存" "$T"
test_api "POST" "/admin/v1/inventory/batch" '[1833339862330556416]' "批量查询库存" "$T"

# ==========================================
# 仪表盘
# ==========================================
echo "--- 仪表盘 ---"
test_api "GET" "/admin/v1/dashboard/stats" "" "统计数据" "$T"

# ==========================================
# 认证
# ==========================================
echo "--- 认证 ---"
test_api "GET" "/admin/v1/auth/getCode" "" "验证码" ""
test_api "GET" "/admin/v1/auth/userInfo" "" "当前用户信息" "$T"
test_api "GET" "/admin/v1/auth/menus" "" "菜单树" "$T"

# ==========================================
# 用户管理
# ==========================================
echo "--- 用户管理 ---"
test_api "POST" "/admin/v1/user/page" '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "用户分页" "$T"

# ==========================================
# 分类
# ==========================================
echo "--- 分类 ---"
test_api "POST" "/admin/v1/product-mgr/category/tree" "{}" "分类树" "$T"

# ==========================================
# 订单
# ==========================================
echo "--- 订单 ---"
test_api "POST" "/admin/v1/order/page" '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "订单分页" "$T"

# ==========================================
# 营销
# ==========================================
echo "--- 营销 ---"
test_api "POST" "/admin/v1/marketing/coupon/page" "{}" "优惠券分页" "$T"

# ==========================================
# 评价
# ==========================================
echo "--- 评价 ---"
test_api "POST" "/admin/v1/shopping/productComment/page" "{}" "评价分页" "$T"

# ==========================================
# 基础数据
# ==========================================
echo "--- 基础数据 ---"
test_api "POST" "/admin/v1/basic/photo/page" "{}" "图片分页" "$T"

# ==========================================
# 汇总
# ==========================================
echo ""
echo "=========================================="
echo -e "  ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}"
echo "=========================================="

[ "$FAIL" -eq 0 ] && exit 0 || exit 1
