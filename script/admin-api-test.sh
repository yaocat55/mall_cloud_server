#!/bin/bash
# Admin API 接口自动化测试脚本
# 用法: bash script/admin-api-test.sh [admin_port]
# 默认端口: 8030

set -e

ADMIN_PORT="${1:-8030}"
BASE="http://localhost:${ADMIN_PORT}"
PASS=0
FAIL=0

# 颜色
GREEN='\033[32m'
RED='\033[31m'
NC='\033[0m'

# 测试函数
test_api() {
    local method="$1"
    local path="$2"
    local data="$3"
    local desc="$4"
    local token="$5"

    # 构建 curl 参数
    local headers=(-H "Content-Type: application/json")
    [ -n "$token" ] && headers+=(-H "Authorization: Bearer $token")

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
echo " Admin 模块接口测试"
echo " 目标: $BASE"
echo "=========================================="
echo ""

# ==========================================
# Step 1: 获取 Token
# ==========================================
echo "--- 1. 登录认证 ---"
TOKEN_RESP=$(curl -s -X POST "${BASE}/v1/auth/user/testLogin" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo "$TOKEN_RESP" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "  ${RED}❌ 获取 Token 失败${NC}"
    echo "  响应: $TOKEN_RESP"
    exit 1
fi
echo -e "  ${GREEN}✅${NC} Token 获取成功"
PASS=$((PASS + 1))

# ==========================================
# Step 2: 用户管理
# ==========================================
echo ""
echo "--- 2. 用户管理 ---"
test_api POST "/v1/auth/user/searchByPage" '{"page":1,"pageSize":10}' "用户分页查询" "$TOKEN"
test_api GET "/v1/auth/user/findById" "id=1" "用户详情" "$TOKEN"
test_api POST "/v1/auth/user/resetPwd" '{"id":1,"password":"newpassword123"}' "重置密码" "$TOKEN"

# ==========================================
# Step 3: 角色管理
# ==========================================
echo ""
echo "--- 3. 角色管理 ---"
test_api POST "/v1/auth/role/searchByPage" '{"page":1,"pageSize":10}' "角色分页" "$TOKEN"
test_api GET "/v1/auth/role/all" "" "查询所有角色" "$TOKEN"
test_api GET "/v1/auth/role/findById" "id=1" "角色详情" "$TOKEN"

# ==========================================
# Step 4: 菜单管理
# ==========================================
echo ""
echo "--- 4. 菜单管理 ---"
test_api POST "/v1/auth/menu/searchByPage" '{"page":1,"pageSize":20}' "菜单分页" "$TOKEN"
test_api POST "/v1/auth/menu/getMenu" '{"page":1,"pageSize":50}' "菜单列表" "$TOKEN"
test_api GET "/v1/auth/menu/getMenuTree" "" "菜单树" "$TOKEN"
test_api GET "/v1/auth/menu/findById" "id=1" "菜单详情" "$TOKEN"
test_api GET "/v1/auth/menu/getChild" "id=1" "子菜单查询" "$TOKEN"

# ==========================================
# Step 5: 部门管理
# ==========================================
echo ""
echo "--- 5. 部门管理 ---"
test_api POST "/v1/auth/dept/searchByPage" '{"page":1,"pageSize":10}' "部门分页" "$TOKEN"
test_api POST "/v1/auth/dept/searchByTree" '{"page":1,"pageSize":50}' "部门树" "$TOKEN"
test_api GET "/v1/auth/dept/findById" "id=1" "部门详情" "$TOKEN"

# ==========================================
# Step 6: 岗位管理
# ==========================================
echo ""
echo "--- 6. 岗位管理 ---"
test_api POST "/v1/auth/job/searchByPage" '{"page":1,"pageSize":10}' "岗位分页" "$TOKEN"
test_api GET "/v1/auth/job/findById" "id=1" "岗位详情" "$TOKEN"

# ==========================================
# Step 7: 合并接口（原 internal 已合并到 /v1/auth/*，不重复已有测试）
# ==========================================
echo ""
echo "--- 7. 合并接口（原 internal → /v1/auth/） ---"
test_api POST "/v1/auth/user/findByIds" '{"ids":[1,2]}' "批量查用户" "$TOKEN"
test_api GET "/v1/auth/user/findByPhone" "phone=13800138000" "手机号查用户" "$TOKEN"
test_api GET "/v1/auth/job/all" "" "查询所有岗位" "$TOKEN"

# ==========================================
# 结果汇总
# ==========================================
echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

# 有失败则退出码非零
[ "$FAIL" -gt 0 ] && exit 1 || exit 0
