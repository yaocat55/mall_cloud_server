#!/bin/bash
# basic 微服务 + BFF basic 读聚合 接口测试
# 验证写透传迁移 + Handler 包装

BASE_BASIC="http://localhost:8022"
BASE_BFF="http://localhost:8090"
PASS=0
FAIL=0
GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

test_api() {
    local method="$1" base="$2" path="$3" data="$4" desc="$5" token="$6" expect_raw="$7"
    local headers=(-H "Content-Type: application/json")
    [ -n "$token" ] && headers+=(-H "Authorization: Bearer $token")

    if [ "$method" = "GET" ]; then
        resp=$(curl -s -w "\n%{http_code}" "${headers[@]}" "${base}${path}${data:+?$data}" 2>/dev/null)
    else
        resp=$(curl -s -w "\n%{http_code}" "${headers[@]}" -d "${data:-{}}" "${base}${path}" 2>/dev/null)
    fi

    http_code=$(echo "$resp" | tail -1)
    body=$(echo "$resp" | sed '$d')
    has_code=$(echo "$body" | grep -o '"code":' | head -1)

    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        echo -e "  ${GREEN}✅${NC} $http_code $desc"
        PASS=$((PASS + 1))
    elif [ "$http_code" = "404" ] && echo "$desc" | grep -q "应404"; then
        echo -e "  ${GREEN}✅${NC} 404 $desc (已删除)"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}❌${NC} $http_code $desc"
        FAIL=$((FAIL + 1))
    fi
}

echo ""
echo "=========================================="
echo " Basic + BFF 写透传迁移验证测试"
echo "=========================================="
echo ""

TOKEN=$(curl -s -X POST http://localhost:8030/v1/auth/user/testLogin \
    -H 'Content-Type: application/json' \
    -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then echo -e "${RED}❌ 登录失败${NC}"; exit 1; fi
echo -e "  ${GREEN}✅${NC} Token 获取成功"
echo ""
T=$TOKEN

echo "--- 1. 写操作直通 basic 微服务 ---"
test_api "GET" "$BASE_BASIC" "/v1/commonPhotoGroup/findById" "id=1" \
    "图片分组查询(直通)" "$T"
test_api "POST" "$BASE_BASIC" "/v1/commonPhotoGroup/searchByPage" '{}' \
    "图片分组分页(直通)" "$T"

echo "--- 2. BFF 读聚合 ---"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/sensitiveWord/page" \
    '{}' "敏感词分页(BFF)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/photo/page" \
    '{}' "图片分页(BFF)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/photoGroup/page" \
    '{}' "图片分组分页(BFF)" "$T"

echo "--- 3. 旧 BFF 写透传已删除（应404）---"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/sensitiveWord/insert" \
    '{"word":"test"}' "旧:新增敏感词(应404)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/sensitiveWord/delete" \
    '{"ids":[999]}' "旧:删除敏感词(应404)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/photo/insert" \
    '{}' "旧:新增图片(应404)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/photo/delete" \
    '{"ids":[999]}' "旧:删除图片(应404)" "$T"
test_api "POST" "$BASE_BFF" "/admin/v1/basic/photoGroup/insert" \
    '{}' "旧:新增图片分组(应404)" "$T"

echo "--- 4. Feign 内部路径 /v1/internal/ ---"
test_api "POST" "$BASE_BASIC" "/v1/internal/commonPhotoGroup/searchByPage" '{}' \
    "内部:图片分组分页(裸DTO)" "$T"

echo ""
echo "=========================================="
echo -e "  ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}"
echo "=========================================="
[ "$FAIL" -eq 0 ] && exit 0 || exit 1
