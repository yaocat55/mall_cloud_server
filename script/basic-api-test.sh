#!/bin/bash
# Basic 模块接口连通性测试
# 用法: bash script/basic-api-test.sh [basic_port] [admin_port]
# 默认端口 basic: 8022, admin: 8030
# 说明: 第三方服务(AI/短信/上传等)未接入，暂不测试

BASIC_PORT="${1:-8022}"
ADMIN_PORT="${2:-8030}"
BASE="http://localhost:${BASIC_PORT}"
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
        HTTP_CODE=$(curl -s -o /tmp/basic_resp.txt -w "%{http_code}" "${headers[@]}" "${BASE}${path}${data:+?$data}" 2>/dev/null)
    else
        HTTP_CODE=$(curl -s -o /tmp/basic_resp.txt -w "%{http_code}" "${headers[@]}" -d "${data:-{}}" "${BASE}${path}" 2>/dev/null)
    fi

    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "  ${GREEN}✅${NC} 200 $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}❌${NC} $HTTP_CODE $desc ($method $path)"
        FAIL=$((FAIL + 1))
    fi
}

echo ""
echo "=========================================="
echo " Basic 模块接口连通性测试"
echo "  目标: $BASE"
echo "=========================================="
echo ""

echo "--- 1. 行政区域 ---"
test_api POST "/v1/commonArea/searchByPage" '{"pageNo":1,"pageSize":10}' "区域分页"
test_api GET "/v1/commonArea/findById" "id=1842558401954140160" "区域详情"
test_api POST "/v1/commonArea/insert" '{"name":"广州","parentId":0,"sort":1,"type":1}' "新增区域"
test_api POST "/v1/commonArea/update" '{"id":1842558401954140160,"name":"更新区域"}' "修改区域"
test_api POST "/v1/commonArea/deleteByIds" '{"ids":[1]}' "删除区域"

echo ""
echo "--- 2. 数据字典 ---"
test_api POST "/v1/dict/searchByPage" '{"pageNo":1,"pageSize":10}' "字典分页"
test_api GET "/v1/dict/findById" "id=1" "字典详情"
test_api POST "/v1/dict/insert" '{"dictName":"test_dict","dictDescription":"测试字典"}' "新增字典"
test_api POST "/v1/dictDetail/searchDictDetail" '{"dictName":"order_status"}' "字典明细查询"
test_api GET "/v1/dictDetail/findById" "id=1" "字典明细详情"
test_api POST "/v1/dictDetail/searchByPage" '{"pageNo":1,"pageSize":10}' "字典明细分页"

echo ""
echo "--- 3. 定时任务 ---"
test_api POST "/v1/commonJob/searchByPage" '{"pageNo":1,"pageSize":10}' "任务分页"
test_api GET "/v1/commonJob/findById" "id=1" "任务详情(id=1)"
test_api POST "/v1/commonJobLog/searchByPage" '{"pageNo":1,"pageSize":10}' "任务日志分页"

echo ""
echo "--- 4. 敏感词 ---"
test_api POST "/v1/commonSensitiveWord/searchByPage" '{"pageNo":1,"pageSize":10}' "敏感词分页"
test_api GET "/v1/commonSensitiveWord/findById" "id=1806974472433483777" "敏感词详情"
test_api POST "/v1/commonSensitiveWord/insert" '{"name":"测试敏感词","type":1}' "新增敏感词"
test_api POST "/v1/commonSensitiveWord/update" '{"id":1806974472433483777,"name":"更新敏感词"}' "修改敏感词"
test_api POST "/v1/commonSensitiveWord/deleteByIds" '{"ids":[1806974472433483777]}' "删除敏感词"

echo ""
echo "--- 5. 移动端(C端) ---"
test_api GET "/v1/mobile/area/queryByParentId" "parentId=1842558399248814080" "按父ID查地区"

echo ""
echo "=========================================="
echo -e " ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  总计: $((PASS + FAIL))"
echo "=========================================="
echo ""

[ "$FAIL" -gt 0 ] && exit 1 || exit 0
