#!/bin/bash
# 检查所有分页接口是否有数据
GREEN='\033[32m'; RED='\033[31m'; YELLOW='\033[33m'; NC='\033[0m'

ADMIN=http://localhost:8030; BFF=http://localhost:8090; BASIC=http://localhost:8022
PRODUCT=http://localhost:8023; MARKETING=http://localhost:8024; ORDER=http://localhost:8026

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

echo "========== BFF 分页接口数据检查 =========="
check_page "用户分页" -X POST "$BFF/admin/v1/user/page" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "角色分页" -X POST "$BFF/admin/v1/system/role/page" -d '{}'
check_page "菜单列表" -X POST "$BFF/admin/v1/system/menu/list" -d '{}'
check_page "部门分页" -X POST "$BFF/admin/v1/system/dept/page" -d '{}'
check_page "岗位分页" -X POST "$BFF/admin/v1/system/job/page" -d '{}'
check_page "商品分页" -X POST "$BFF/admin/v1/product/page" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "分类分页" -X POST "$BFF/admin/v1/product-mgr/category/page" -d '{}'
check_page "品牌分页" -X POST "$BFF/admin/v1/product-mgr/brand/page" -d '{}'
check_page "单位分页" -X POST "$BFF/admin/v1/product-mgr/unit/page" -d '{}'
check_page "属性分页" -X POST "$BFF/admin/v1/product-extra/attribute/page" -d '{}'
check_page "属性值分页" -X POST "$BFF/admin/v1/product-extra/attributeValue/page" -d '{}'
check_page "商品分组" -X POST "$BFF/admin/v1/product-extra/productGroup/page" -d '{}'
check_page "公告分页" -X POST "$BFF/admin/v1/product-extra/indexNotice/page" -d '{}'
check_page "首页商品" -X POST "$BFF/admin/v1/product-extra/indexProduct/page" -d '{}'
check_page "轮播图" -X POST "$BFF/admin/v1/product-extra/indexCarouselImage/page" -d '{}'
check_page "商品图片" -X POST "$BFF/admin/v1/product-extra/productPhoto/page" -d '{}'
check_page "图片分页" -X POST "$BFF/admin/v1/basic/photo/page" -d '{}'
check_page "图片分组" -X POST "$BFF/admin/v1/basic/photoGroup/page" -d '{}'
check_page "敏感词" -X POST "$BFF/admin/v1/basic/sensitiveWord/page" -d '{}'
check_page "通知分页" -X POST "$BFF/admin/v1/basic/notify/page" -d '{}'
check_page "订单分页" -X POST "$BFF/admin/v1/order/page" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "退货分页" -X POST "$BFF/admin/v1/order/return/page" -d '{}'
check_page "优惠券" -X POST "$BFF/admin/v1/marketing/coupon/page" -d '{}'
check_page "秒杀分页" -X POST "$BFF/admin/v1/marketing/seckill/page" -d '{}'
check_page "发券记录" -X POST "$BFF/admin/v1/marketing/couponUserProvide/page" -d '{}'
check_page "领券记录" -X POST "$BFF/admin/v1/marketing/couponUserReceive/page" -d '{}'
check_page "评价分页" -X POST "$BFF/admin/v1/shopping/productComment/page" -d '{}'

echo ""
echo "========== 微服务分页接口数据检查 =========="
check_page "用户(admin)" -X POST "$ADMIN/v1/auth/user/searchByPage" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "角色(admin)" -X POST "$ADMIN/v1/auth/role/searchByPage" -d '{}'
check_page "部门(admin)" -X POST "$ADMIN/v1/auth/dept/searchByPage" -d '{}'
check_page "岗位(admin)" -X POST "$ADMIN/v1/auth/job/searchByPage" -d '{}'
check_page "图片(basic)" -X POST "$BASIC/v1/commonPhoto/searchByPage" -d '{}'
check_page "图片分组(basic)" -X POST "$BASIC/v1/commonPhotoGroup/searchByPage" -d '{}'
check_page "敏感词(basic)" -X POST "$BASIC/v1/commonSensitiveWord/searchByPage" -d '{}'
check_page "商品(product)" -X POST "$PRODUCT/admin/v1/product/searchByPage" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "分类(product)" -X POST "$PRODUCT/v1/category/searchByPage" -d '{}'
check_page "品牌(product)" -X POST "$PRODUCT/v1/brand/searchByPage" -d '{}'
check_page "单位(product)" -X POST "$PRODUCT/v1/unit/searchByPage" -d '{}'
check_page "订单(order)" -X POST "$ORDER/v1/order/searchByPage" -d '{"entity":{},"page":{"pageNum":1,"pageSize":10}}'
check_page "优惠券(mkt)" -X POST "$MARKETING/v1/coupon/searchByPage" -d '{}'
check_page "秒杀(mkt)" -X POST "$MARKETING/v1/seckill/searchByPage" -d '{}'
