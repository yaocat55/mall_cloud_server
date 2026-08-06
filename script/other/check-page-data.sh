#!/bin/bash
# 检查所有分页接口是否有数据
GREEN='\033[32m'; RED='\033[31m'; YELLOW='\033[33m'; NC='\033[0m'

ADMIN=http://localhost:8030; BASIC=http://localhost:8022
PRODUCT=http://localhost:8023; MARKETING=http://localhost:8024; ORDER=http://localhost:8026
INVENTORY=http://localhost:8036; MESSAGE=http://localhost:8028

TOKEN=$(curl -s -X POST $ADMIN/v1/auth/user/testLogin -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
T=$TOKEN

check_page() {
  local label="$1"; shift
  local curl_args=("$@")
  local resp=$(curl -s "${curl_args[@]}" -H "Authorization: Bearer $T" -H "Content-Type: application/json" 2>/dev/null)
  local code=$(echo "$resp" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('code','?'))" 2>/dev/null || echo "?")
  local total=$(echo "$resp" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('data',{}).get('total','?'))" 2>/dev/null || echo "?")
  local list_len=$(echo "$resp" | python3 -c "import sys,json; d=json.load(sys.stdin); data=d.get('data',{}); lst=data.get('list',data.get('records',[])); print(len(lst) if lst else '?')" 2>/dev/null || echo "?")
  if [ "$total" != "?" ] && [ "$total" != "0" ] && [ "$total" != "null" ] && [ "$list_len" != "?" ] && [ "$list_len" != "0" ]; then
    printf "  ${GREEN}✓${NC} %-30s total=%s items=%s\n" "$label" "$total" "$list_len"
  elif [ "$total" = "0" ] || [ "$list_len" = "0" ]; then
    printf "  ${RED}✗${NC} %-30s total=%s items=%s (无数据!)\n" "$label" "$total" "$list_len"
  else
    printf "  ${YELLOW}?${NC} %-30s code=%s total=%s items=%s (解析异常)\n" "$label" "$code" "$total" "$list_len"
  fi
}

echo "========== 微服务分页接口数据检查 =========="
check_page "用户(admin)" -X POST "$ADMIN/v1/auth/user/searchByPage" -d '{"pageNo":1,"pageSize":10}'
check_page "角色(admin)" -X POST "$ADMIN/v1/auth/role/searchByPage" -d '{}'
check_page "部门(admin)" -X POST "$ADMIN/v1/auth/dept/searchByPage" -d '{}'
check_page "岗位(admin)" -X POST "$ADMIN/v1/auth/job/searchByPage" -d '{}'
check_page "菜单(admin)" -X POST "$ADMIN/v1/auth/menu/searchByPage" -d '{}'
check_page "图片(basic)" -X POST "$BASIC/v1/commonPhoto/searchByPage" -d '{}'
check_page "图片分组(basic)" -X POST "$BASIC/v1/commonPhotoGroup/searchByPage" -d '{}'
check_page "敏感词(basic)" -X POST "$BASIC/v1/commonSensitiveWord/searchByPage" -d '{}'
check_page "商品(product)" -X POST "$PRODUCT/v1/product/searchByPage" -d '{"pageNo":1,"pageSize":10}'
check_page "分类(product)" -X POST "$PRODUCT/v1/category/searchByPage" -d '{}'
check_page "品牌(product)" -X POST "$PRODUCT/v1/brand/searchByPage" -d '{}'
check_page "单位(product)" -X POST "$PRODUCT/v1/unit/searchByPage" -d '{}'
check_page "属性(product)" -X POST "$PRODUCT/v1/attribute/searchByPage" -d '{}'
check_page "属性值(product)" -X POST "$PRODUCT/v1/attributeValue/searchByPage" -d '{}'
check_page "商品分组(product)" -X POST "$PRODUCT/v1/productGroup/searchByPage" -d '{}'
check_page "公告(product)" -X POST "$PRODUCT/v1/indexNotice/searchByPage" -d '{}'
check_page "首页商品(product)" -X POST "$PRODUCT/v1/indexProduct/searchByPage" -d '{}'
check_page "轮播图(product)" -X POST "$PRODUCT/v1/indexCarouselImage/searchByPage" -d '{}'
check_page "评价(product)" -X POST "$PRODUCT/v1/productComment/searchByPage" -d '{}'
check_page "库存(inventory)" -X POST "$INVENTORY/v1/inventory/page" -d '{}'
check_page "通知(message)" -X POST "$MESSAGE/v1/message/notify/searchByPage" -d '{}'
check_page "订单(order)" -X POST "$ORDER/v1/admin/order/page" -d '{"pageNo":1,"pageSize":10}'
check_page "退货(order)" -X POST "$ORDER/v1/admin/order/return/page" -d '{}'
check_page "优惠券(mkt)" -X POST "$MARKETING/v1/coupon/searchByPage" -d '{}'
check_page "秒杀(mkt)" -X POST "$MARKETING/v1/seckillProduct/searchByPage" -d '{}'
check_page "发券记录(mkt)" -X POST "$MARKETING/v1/couponUserProvide/searchByPage" -d '{}'
check_page "领券记录(mkt)" -X POST "$MARKETING/v1/couponUserReceive/searchByPage" -d '{}'
