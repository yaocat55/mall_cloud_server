#!/bin/bash
# Product 模块接口连通性测试
# 用法: bash script/product-api-test.sh [product_port]
# 默认端口: 8023
# 说明: B端接口需要 admin 角色，C端接口无需认证

PRODUCT_PORT="${1:-8023}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${PRODUCT_PORT}"
ADMIN_BASE="http://localhost:${ADMIN_PORT}"
PASS=0
FAIL=0

GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

# 获取 admin token（用于 B端接口认证）
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
    local use_token="$5"  # true/false

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

echo ""
echo "=========================================="
echo " Product 模块接口连通性测试"
echo "  目标: $BASE"
echo "=========================================="
echo ""

echo "--- 1. 商品中心（B端，需 token）---"
test_api POST "/v1/product/searchByPage" '{"pageNo":1,"pageSize":10}' "商品分页" true
test_api GET "/v1/product/findById" "id=1" "商品详情" true
test_api POST "/v1/product/insert" '{"name":"测试商品","price":99}' "新增商品" true
test_api POST "/v1/product/update" '{"id":1,"name":"测试商品"}' "修改商品" true
test_api POST "/v1/product/deleteByIds" '{"ids":[1]}' "删除商品" true

echo ""
echo "--- 2. 分类管理（B端，需 token）---"
test_api POST "/v1/category/searchByPage" '{"pageNo":1,"pageSize":10}' "分类分页" true
test_api GET "/v1/category/findById" "id=1" "分类详情" true
test_api POST "/v1/category/insert" '{"name":"测试分类"}' "新增分类" true

echo ""
echo "--- 3. 品牌管理（B端，需 token）---"
test_api POST "/v1/brand/searchByPage" '{"pageNo":1,"pageSize":10}' "品牌分页" true
test_api GET "/v1/brand/findById" "id=1" "品牌详情" true

echo ""
echo "--- 4. 属性管理（B端，需 token）---"
test_api POST "/v1/attribute/searchByPage" '{"pageNo":1,"pageSize":10}' "属性分页" true
test_api GET "/v1/attribute/findById" "id=1" "属性详情" true
test_api POST "/v1/attributeValue/searchByPage" '{"pageNo":1,"pageSize":10}' "属性值分页" true

echo ""
echo "--- 5. 单位管理（B端，需 token）---"
test_api POST "/v1/unit/searchByPage" '{"pageNo":1,"pageSize":10}' "单位分页" true
test_api GET "/v1/unit/findById" "id=1" "单位详情" true

echo ""
echo "--- 6. 商品分组（B端，需 token）---"
test_api POST "/v1/productGroup/searchByPage" '{"pageNo":1,"pageSize":10}' "分组分页" true

echo ""
echo "--- 7. 商品图片（B端，需 token）---"
test_api POST "/v1/productPhoto/searchByPage" '{"pageNo":1,"pageSize":10}' "图片分页" true

echo ""
echo "--- 8. 商品-属性关联（B端，需 token）---"
test_api POST "/v1/productAttribute/searchByPage" '{"pageNo":1,"pageSize":10}' "商品属性分页" true

echo ""
echo "--- 9. 首页管理（B端，需 token）---"
test_api POST "/v1/indexCarouselImage/searchByPage" '{"pageNo":1,"pageSize":10}' "轮播图分页" true
test_api POST "/v1/indexNotice/searchByPage" '{"pageNo":1,"pageSize":10}' "公告分页" true
test_api POST "/v1/indexProduct/searchByPage" '{"pageNo":1,"pageSize":10}' "首页推荐分页" true

echo ""
echo "--- 10. 浏览记录（B端，需 token）---"
test_api POST "/v1/productViewRecord/searchByPage" '{"pageNo":1,"pageSize":10}' "浏览记录分页" true

echo ""
echo "--- 11. 购物车（B端，需 token）---"
test_api POST "/v1/shoppingCart/searchByPage" '{"pageNo":1,"pageSize":10}' "购物车分页" true

echo ""
echo "--- 12. 移动端（C端，无需 token）---"
test_api GET "/v1/mobile/category" "" "移动端分类" false
test_api POST "/v1/mobile/product/search" '{}' "移动端商品搜索" false
test_api GET "/v1/mobile/product/detail" "id=1" "移动端商品详情" false
test_api GET "/v1/mobile/index" "" "移动端首页" false

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
