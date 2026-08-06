#!/bin/bash
# ============================================================
# Mall Cloud — 管理后台全量 Page 接口自动化测试
# 从 script/full-test.http 提取所有 page/list 类接口并测试
# 用法: bash script/test-all-pages.sh
# ============================================================

set -e

# -------------------- 配置 --------------------
ADMIN="http://localhost:8030"
BASIC="http://localhost:8022"
PRODUCT="http://localhost:8023"
MARKETING="http://localhost:8024"
ORDER="http://localhost:8026"
INVENTORY="http://localhost:8036"
MESSAGE="http://localhost:8028"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

PASS=0
FAIL=0
SKIP=0
RESULTS=()

# -------------------- 工具函数 --------------------
now() { date '+%H:%M:%S'; }

log_pass() { echo -e "  ${GREEN}✓ PASS${NC}  $1"; PASS=$((PASS+1)); RESULTS+=("PASS|$1|$2|$3"); }
log_fail() { echo -e "  ${RED}✗ FAIL${NC}  $1 — $2"; FAIL=$((FAIL+1)); RESULTS+=("FAIL|$1|$2|$3"); }
log_skip() { echo -e "  ${YELLOW}⊘ SKIP${NC}  $1 — $2"; SKIP=$((SKIP+1)); RESULTS+=("SKIP|$1|$2|"); }

# curl 封装: 返回 HTTP 状态码 + 耗时
# $1=method $2=url $3=token $4=body(optional)
do_get() {
  local method="$1" url="$2" token="$3" body="$4"
  local http_code elapsed body_file
  body_file=$(mktemp)
  if [ "$method" = "GET" ]; then
    http_code=$(curl -s -o "$body_file" -w "%{http_code}" --connect-timeout 10 --max-time 30 \
      -H "Authorization: Bearer ${token}" "$url" 2>/dev/null || echo "000")
    elapsed="N/A"
  else
    local extra_args=""
    if [ -n "$body" ]; then extra_args="-d" "$body"; fi
    http_code=$(curl -s -o "$body_file" -w "%{http_code}" --connect-timeout 10 --max-time 30 \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer ${token}" \
      $extra_args "$url" 2>/dev/null || echo "000")
    elapsed="N/A"
  fi
  echo "${http_code}|${body_file}"
}

# -------------------- Step 0: 获取 Token --------------------
echo ""
echo -e "${BOLD}${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║  Mall Cloud — 管理后台全量 Page 接口自动化测试              ║${NC}"
echo -e "${BOLD}${CYAN}║  时间: $(date '+%Y-%m-%d %H:%M:%S')                                  ║${NC}"
echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${BOLD}[Step 0] 获取 Token...${NC}"
LOGIN_RESULT=$(curl -s --connect-timeout 10 --max-time 30 \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  "${ADMIN}/v1/auth/user/testLogin" 2>/dev/null || echo '{"code":0,"data":{}}')

TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('data',{}).get('token',''))" 2>/dev/null || echo "")

if [ -z "$TOKEN" ]; then
  echo -e "${RED}✗ 无法获取 Token，退出。请确认 admin 服务是否正常运行。${NC}"
  echo "  响应: $LOGIN_RESULT"
  exit 1
fi
echo -e "${GREEN}✓ Token 获取成功${NC}"
echo ""

# -------------------- 测试用例列表 --------------------
# 格式: "标签|方法|URL|请求体(可选)"
# 仅测试 page/list/tree/all/stats/detail 等数据查询接口
# 跳过写操作(insert/update/delete/approve/reject/push) 和文件上传

declare -a TESTS=(
  # ===== A: RBAC (mall-admin 直连) =====
  "A1.1 验证码(无需Token) |GET|${ADMIN}/v1/auth/web/user/getCode"
  "A1.3 用户信息           |GET|${ADMIN}/v1/auth/web/user/info"
  "A1.4 用户详情           |GET|${ADMIN}/v1/auth/web/user/getUserDetail"
  "A1.5 菜单树             |GET|${ADMIN}/v1/auth/web/user/menus"
  "A2.1 用户分页           |POST|${ADMIN}/v1/auth/user/searchByPage|{\"pageNo\":1,\"pageSize\":10}"
  "A3.1 角色分页           |POST|${ADMIN}/v1/auth/role/searchByPage|{}"
  "A3.2 角色列表           |GET|${ADMIN}/v1/auth/role/all"
  "A4.1 部门分页           |POST|${ADMIN}/v1/auth/dept/searchByPage|{}"
  "A4.2 部门树             |POST|${ADMIN}/v1/auth/dept/searchByTree|{}"
  "A5.1 岗位分页           |POST|${ADMIN}/v1/auth/job/searchByPage|{}"
  "A5.2 岗位列表           |GET|${ADMIN}/v1/auth/job/all"
  "A6.1 菜单树(系统)       |GET|${ADMIN}/v1/auth/menu/getMenuTree"
  "A6.2 菜单列表           |POST|${ADMIN}/v1/auth/menu/searchByPage|{}"

  # ===== B: 商品 (mall-product 直连) =====
  "B1.1 商品分页           |POST|${PRODUCT}/v1/product/searchByPage|{\"pageNo\":1,\"pageSize\":10}"
  "B1.2 商品详情           |GET|${PRODUCT}/v1/product/findById?id=1833339862330556416"
  "B2.1 分类分页           |POST|${PRODUCT}/v1/category/searchByPage|{}"
  "B2.2 分类树             |POST|${PRODUCT}/v1/category/searchByTree|{}"
  "B3.1 品牌分页           |POST|${PRODUCT}/v1/brand/searchByPage|{}"
  "B4.1 单位分页           |POST|${PRODUCT}/v1/unit/searchByPage|{}"
  "B5.1 属性分页           |POST|${PRODUCT}/v1/attribute/searchByPage|{}"
  "B5.2 属性值分页         |POST|${PRODUCT}/v1/attributeValue/searchByPage|{}"
  "B5.3 商品分组分页       |POST|${PRODUCT}/v1/productGroup/searchByPage|{}"

  # ===== C: 运营 =====
  "C1 公告分页            |POST|${PRODUCT}/v1/indexNotice/searchByPage|{}"
  "C2 首页商品分页        |POST|${PRODUCT}/v1/indexProduct/searchByPage|{}"
  "C3 轮播图分页          |POST|${PRODUCT}/v1/indexCarouselImage/searchByPage|{}"
  "C4 商品图片分页        |POST|${PRODUCT}/v1/productPhoto/searchByPage|{}"

  # ===== D: 基础数据 (mall-basic 直连) =====
  "D1 图片库分页          |POST|${BASIC}/v1/commonPhoto/searchByPage|{}"
  "D2 图片分组分页        |POST|${BASIC}/v1/commonPhotoGroup/searchByPage|{}"
  "D3 敏感词分页          |POST|${BASIC}/v1/commonSensitiveWord/searchByPage|{}"

  # ===== E: 通知 (mall-message 直连) =====
  "E1 通知分页            |POST|${MESSAGE}/v1/message/notify/searchByPage|{}"

  # ===== F: 订单与售后 (mall-order 直连) =====
  "F1 订单分页            |POST|${ORDER}/v1/admin/order/page|{\"pageNo\":1,\"pageSize\":10}"
  "F2 退货分页            |POST|${ORDER}/v1/admin/order/return/page|{}"
  "F3 退货详情            |GET|${ORDER}/v1/admin/order/return/detail?id=1"

  # ===== G: 营销 (mall-marketing 直连) =====
  "G1 优惠券分页          |POST|${MARKETING}/v1/coupon/searchByPage|{}"
  "G2 秒杀分页            |POST|${MARKETING}/v1/seckillProduct/searchByPage|{}"
  "G3 发券记录分页        |POST|${MARKETING}/v1/couponUserProvide/searchByPage|{}"
  "G4 领券记录分页        |POST|${MARKETING}/v1/couponUserReceive/searchByPage|{}"

  # ===== H: 评价 (mall-product 直连) =====
  "H1 评价分页            |POST|${PRODUCT}/v1/productComment/searchByPage|{}"
  "H2 评价详情            |GET|${PRODUCT}/v1/productComment/findById?id=1"

  # ===== I: 库存 (mall-inventory 直连) =====
  "I1 库存查询            |GET|${INVENTORY}/v1/inventory/1833339862330556416"
  "I2 批量库存查询        |POST|${INVENTORY}/v1/inventory/batch|[1833339862330556416]"
)

TOTAL_TESTS=${#TESTS[@]}

echo -e "${BOLD}共 ${TOTAL_TESTS} 个 page/query 接口待测试${NC}"
echo ""

# -------------------- 执行测试 --------------------
INDEX=0
for TEST_LINE in "${TESTS[@]}"; do
  INDEX=$((INDEX+1))

  # 解析字段
  IFS='|' read -r LABEL METHOD URL BODY <<< "$TEST_LINE"
  LABEL=$(echo "$LABEL" | xargs)   # trim
  METHOD=$(echo "$METHOD" | xargs)
  URL=$(echo "$URL" | xargs)
  BODY=$(echo "$BODY" | xargs)

  printf "${BOLD}[%2d/%2d]${NC} %s ... " "$INDEX" "$TOTAL_TESTS" "$LABEL"

  # A1.1 验证码不需要 Token
  if [[ "$LABEL" == *"验证码"* ]]; then
    result=$(do_get "$METHOD" "$URL" "" "$BODY")
  else
    result=$(do_get "$METHOD" "$URL" "$TOKEN" "$BODY")
  fi

  HTTP_CODE="${result%%|*}"
  BODY_FILE="${result#*|}"

  # 判断结果
  RESP_SUMMARY=""
  if [ -f "$BODY_FILE" ] && [ -s "$BODY_FILE" ]; then
    # 尝试解析 JSON 获取 code 字段
    JSON_CODE=$(python3 -c "
import sys,json
try:
    d=json.load(open('$BODY_FILE'))
    c=d.get('code', 'NO_CODE')
    # 如果是分页接口，额外显示 total
    total=d.get('data',{}).get('total','')
    msg=d.get('msg','')
    if total != '':
        print(f'code={c} total={total}')
    elif c==200:
        print('code=200 OK')
    else:
        print(f'code={c} msg={msg}')
except:
    print('INVALID_JSON')
" 2>/dev/null || echo "PARSE_ERR")
    RESP_SUMMARY="$JSON_CODE"
  else
    RESP_SUMMARY="EMPTY_BODY"
  fi

  # 分类判定
  if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
    if echo "$RESP_SUMMARY" | grep -q "code=200"; then
      log_pass "$LABEL" "$HTTP_CODE" "$RESP_SUMMARY"
    else
      # HTTP 200 但业务 code != 200
      log_fail "$LABEL" "HTTP $HTTP_CODE, $RESP_SUMMARY" "$HTTP_CODE"
    fi
  elif [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
    log_fail "$LABEL" "认证失败 HTTP $HTTP_CODE" "$HTTP_CODE"
  elif [ "$HTTP_CODE" = "404" ]; then
    log_fail "$LABEL" "接口不存在 404" "$HTTP_CODE"
  elif [ "$HTTP_CODE" = "000" ]; then
    log_fail "$LABEL" "连接超时/服务不可达" "$HTTP_CODE"
  elif [ "$HTTP_CODE" = "500" ]; then
    log_fail "$LABEL" "服务器内部错误 500 — $RESP_SUMMARY" "$HTTP_CODE"
  else
    log_fail "$LABEL" "HTTP $HTTP_CODE — $RESP_SUMMARY" "$HTTP_CODE"
  fi

  rm -f "$BODY_FILE"
done

# -------------------- 汇总报告 --------------------
echo ""
echo -e "${BOLD}${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║                    测 试 报 告 汇 总                         ║${NC}"
echo -e "${BOLD}${CYAN}╠══════════════════════════════════════════════════════════════╣${NC}"
printf  "${BOLD}${CYAN}║${NC}  总计: %-3d  ${GREEN}通过: %-3d${NC}  ${RED}失败: %-3d${NC}  ${YELLOW}跳过: %-3d${NC}         ${BOLD}${CYAN}║${NC}\n" "$TOTAL_TESTS" "$PASS" "$FAIL" "$SKIP"
echo -e "${BOLD}${CYAN}╠══════════════════════════════════════════════════════════════╣${NC}"

# 按服务域分组显示失败项
if [ "$FAIL" -gt 0 ]; then
  echo -e "${BOLD}${CYAN}║${NC} ${RED}失败明细:${NC}                                                  ${BOLD}${CYAN}║${NC}"
  for row in "${RESULTS[@]}"; do
    IFS='|' read -r STATUS LABEL DETAIL HTTP_C <<< "$row"
    if [ "$STATUS" = "FAIL" ]; then
      printf "${BOLD}${CYAN}║${NC}  ${RED}✗${NC} %-48s ${RED}%s${NC} ${BOLD}${CYAN}║${NC}\n" "$LABEL" "$DETAIL"
    fi
  done
fi

echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# 按模块分组统计
echo -e "${BOLD}按功能域分组统计:${NC}"
echo ""

MODULES=("A:RBAC" "B:商品" "C:运营" "D:基础数据" "E:通知" "F:订单售后" "G:营销" "H:评价" "I:库存" "J:仪表盘")
for MOD in "${MODULES[@]}"; do
  PREFIX="${MOD%%:*}"
  MNAME="${MOD##*:}"
  M_TOTAL=0; M_PASS=0; M_FAIL=0
  for row in "${RESULTS[@]}"; do
    IFS='|' read -r STATUS LABEL DETAIL HTTP_C <<< "$row"
    if [[ "$LABEL" == ${PREFIX}* ]]; then
      M_TOTAL=$((M_TOTAL+1))
      [ "$STATUS" = "PASS" ] && M_PASS=$((M_PASS+1))
      [ "$STATUS" = "FAIL" ] && M_FAIL=$((M_FAIL+1))
    fi
  done
  if [ "$M_TOTAL" -gt 0 ]; then
    if [ "$M_FAIL" -eq 0 ]; then
      printf "  ${GREEN}✓${NC} %-12s  %d/%d 通过\n" "$MNAME" "$M_PASS" "$M_TOTAL"
    else
      printf "  ${RED}✗${NC} %-12s  %d/%d 通过  ${RED}%d 失败${NC}\n" "$MNAME" "$M_PASS" "$M_TOTAL" "$M_FAIL"
    fi
  fi
done

echo ""
echo -e "${BOLD}测试完成时间: $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo ""

# 退出码
if [ "$FAIL" -gt 0 ]; then
  exit 1
else
  exit 0
fi
