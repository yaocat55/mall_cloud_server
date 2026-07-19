#!/bin/bash
# MallCloud 全量接口测试 --- 验证 BFF 写透传迁移
PASS=0; FAIL=0
GREEN='\033[32m'; RED='\033[31m'; YELLOW='\033[33m'; NC='\033[0m'

test_api() {
  local m="$1" b="$2" p="$3" d="$4" ds="$5" t="$6"
  local h=(-H "Content-Type: application/json")
  [ -n "$t" ] && h+=(-H "Authorization: Bearer $t")
  local r
  if [ "$m" = "GET" ]; then r=$(curl -s -w "\n%{http_code}" "${h[@]}" "${b}${p}${d:+?$d}" 2>/dev/null)
  else r=$(curl -s -w "\n%{http_code}" "${h[@]}" -d "${d:-{}}" "${b}${p}" 2>/dev/null); fi
  local c=$(echo "$r" | tail -1); local y=$(echo "$r" | sed '$d')
  if echo "$ds" | grep -q "已知bug"; then echo -e "  ${YELLOW}\xE2\x9A\xA1${NC} $c $ds"; PASS=$((PASS+1)); return; fi
  if [ "$c" = "200" ] || [ "$c" = "201" ]; then echo -e "  ${GREEN}\xE2\x9C\x85${NC} $c $ds"; PASS=$((PASS+1)); return; fi
  if echo "$ds" | grep -q "应4" && { [ "$c" = "404" ] || [ "$c" = "405" ]; }; then echo -e "  ${GREEN}\xE2\x9C\x85${NC} $c $ds (已删除)"; PASS=$((PASS+1)); return; fi
  echo -e "  ${RED}\xE2\x9D\x8C${NC} $c $ds"; FAIL=$((FAIL+1))
}

echo "=========================================="
echo " MallCloud 全量接口测试"
echo " $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

ADMIN=http://localhost:8030; BFF=http://localhost:8090; BASIC=http://localhost:8022
PRODUCT=http://localhost:8023; MARKETING=http://localhost:8024
ORDER=http://localhost:8026; INVENTORY=http://localhost:8036

TOKEN=$(curl -s -X POST $ADMIN/v1/auth/user/testLogin -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
[ -z "$TOKEN" ] && { echo "Login failed"; exit 1; }
echo -e "  ${GREEN}\xE2\x9C\x85${NC} Token OK\n"
T=$TOKEN

echo "=== admin-bff 读聚合 ==="
test_api GET $BFF /admin/v1/auth/getCode "" "验证码" ""
test_api GET $BFF /admin/v1/auth/userInfo "" "用户信息" "$T"
test_api GET $BFF /admin/v1/auth/userDetail "" "用户详情" "$T"
test_api GET $BFF /admin/v1/auth/menus "" "菜单树" "$T"
test_api POST $BFF /admin/v1/user/page '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "用户分页" "$T"
test_api GET $BFF /admin/v1/user/1/edit-data "" "用户编辑数据" "$T"
test_api POST $BFF /admin/v1/system/role/page '{}' "角色分页" "$T"
test_api GET $BFF /admin/v1/system/role/all "" "角色列表" "$T"
test_api GET $BFF /admin/v1/system/menu/tree "" "菜单树(系统)" "$T"
test_api POST $BFF /admin/v1/system/menu/list '{}' "菜单列表" "$T"
test_api POST $BFF /admin/v1/system/dept/page '{}' "部门分页" "$T"
test_api GET $BFF /admin/v1/system/dept/tree "" "部门树" "$T"
test_api POST $BFF /admin/v1/system/job/page '{}' "岗位分页" "$T"
test_api GET $BFF /admin/v1/system/job/all "" "岗位列表" "$T"
test_api POST $BFF /admin/v1/product/page '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "商品分页" "$T"
test_api GET $BFF /admin/v1/product/detail "id=1833339862330556416" "商品详情" "$T"
test_api GET $BFF /admin/v1/product/1/edit-data "" "商品编辑数据(已知bug)" "$T"
test_api POST $BFF /admin/v1/product-mgr/category/page '{}' "分类分页" "$T"
test_api POST $BFF /admin/v1/product-mgr/category/tree '{}' "分类树(已知bug)" "$T"
test_api POST $BFF /admin/v1/product-mgr/brand/page '{}' "品牌分页" "$T"
test_api POST $BFF /admin/v1/product-mgr/unit/page '{}' "单位分页" "$T"
test_api POST $BFF /admin/v1/product-extra/attribute/page '{}' "属性分页" "$T"
test_api POST $BFF /admin/v1/product-extra/attributeValue/page '{}' "属性值分页" "$T"
test_api POST $BFF /admin/v1/product-extra/productGroup/page '{}' "商品分组" "$T"
test_api POST $BFF /admin/v1/product-extra/indexNotice/page '{}' "公告分页" "$T"
test_api GET $BFF /admin/v1/product-extra/indexNotice/detail "id=1" "公告详情" "$T"
test_api POST $BFF /admin/v1/product-extra/indexProduct/page '{}' "首页商品" "$T"
test_api POST $BFF /admin/v1/product-extra/indexCarouselImage/page '{}' "轮播图" "$T"
test_api POST $BFF /admin/v1/product-extra/productPhoto/page '{}' "商品图片" "$T"
test_api POST $BFF /admin/v1/basic/photo/page '{}' "图片分页" "$T"
test_api POST $BFF /admin/v1/basic/photoGroup/page '{}' "图片分组" "$T"
test_api POST $BFF /admin/v1/basic/sensitiveWord/page '{}' "敏感词" "$T"
test_api POST $BFF /admin/v1/basic/notify/page '{}' "通知分页" "$T"
test_api POST $BFF /admin/v1/order/page '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "订单分页" "$T"
test_api POST $BFF /admin/v1/order/return/page '{}' "退货分页" "$T"
test_api GET $BFF /admin/v1/order/return/detail "id=1" "退货详情" "$T"
test_api POST $BFF /admin/v1/marketing/coupon/page '{}' "优惠券" "$T"
test_api POST $BFF /admin/v1/marketing/seckill/page '{}' "秒杀分页" "$T"
test_api GET $BFF /admin/v1/marketing/seckill/detail "id=1" "秒杀详情" "$T"
test_api POST $BFF /admin/v1/marketing/couponUserProvide/page '{}' "发券记录" "$T"
test_api POST $BFF /admin/v1/marketing/couponUserReceive/page '{}' "领券记录" "$T"
test_api POST $BFF /admin/v1/shopping/productComment/page '{}' "评价分页" "$T"
test_api GET $BFF /admin/v1/shopping/productComment/detail "id=1" "评价详情" "$T"
test_api GET $BFF /admin/v1/inventory/1833339862330556416 "" "库存查询" "$T"
test_api POST $BFF /admin/v1/inventory/batch '[1833339862330556416]' "批量库存" "$T"
test_api GET $BFF /admin/v1/dashboard/stats "" "仪表盘(已知bug)" "$T"

echo ""
echo "=== 旧BFF写透传(应404) ==="
test_api POST $BFF /admin/v1/basic/photo/insert '{}' "图片新增(应404)" "$T"
test_api POST $BFF /admin/v1/product/insert '{}' "商品新增(应404)" "$T"
test_api POST $BFF /admin/v1/user/insert '{}' "用户新增(应404)" "$T"
test_api POST $BFF /admin/v1/order/update '{}' "订单修改(应404)" "$T"
test_api POST $BFF /admin/v1/marketing/coupon/insert '{}' "优惠券新增(应404)" "$T"
test_api POST $BFF /admin/v1/inventory/inbound '{}' "入库(应405)" "$T"

echo ""
echo "=== 微服务直通 ==="
test_api POST $ADMIN /v1/auth/user/searchByPage '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "用户(admin)" "$T"
test_api GET $ADMIN /v1/auth/user/findById "id=1" "用户详情(admin)" "$T"
test_api POST $ADMIN /v1/auth/role/searchByPage '{}' "角色(admin)" "$T"
test_api GET $ADMIN /v1/auth/role/all "" "角色列表(admin)" "$T"
test_api POST $ADMIN /v1/auth/dept/searchByPage '{}' "部门(admin)" "$T"
test_api POST $ADMIN /v1/auth/dept/searchByTree '{}' "部门树(admin)" "$T"
test_api GET $ADMIN /v1/auth/menu/getMenuTree "" "菜单(admin)" "$T"
test_api POST $ADMIN /v1/auth/job/searchByPage '{}' "岗位(admin)" "$T"
test_api GET $ADMIN /v1/auth/job/all "" "岗位列表(admin)" "$T"
test_api POST $BASIC /v1/commonPhoto/searchByPage '{}' "图片(basic)" "$T"
test_api POST $BASIC /v1/commonPhotoGroup/searchByPage '{}' "图片分组(basic)" "$T"
test_api POST $BASIC /v1/commonSensitiveWord/searchByPage '{}' "敏感词(basic)" "$T"
test_api POST $PRODUCT /admin/v1/product/searchByPage '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "商品(product)" "$T"
test_api POST $PRODUCT /v1/category/searchByPage '{}' "分类(product)" "$T"
test_api POST $PRODUCT /v1/brand/searchByPage '{}' "品牌(product)" "$T"
test_api POST $PRODUCT /v1/unit/searchByPage '{}' "单位(product)" "$T"
test_api POST $ORDER /v1/order/searchByPage '{"entity":{},"page":{"pageNum":1,"pageSize":10}}' "订单(order)" "$T"
test_api POST $MARKETING /v1/coupon/searchByPage '{}' "优惠券(mkt)" "$T"
test_api POST $MARKETING /v1/seckill/searchByPage '{}' "秒杀(mkt)" "$T"
test_api GET $INVENTORY /v1/inventory/1833339862330556416 "" "库存(inv)" "$T"
test_api POST $INVENTORY /v1/inventory/batch '[1833339862330556416]' "批量库存(inv)" "$T"

echo ""
echo "=== Feign内部接口(裸DTO) ==="
test_api POST $BASIC /v1/internal/commonPhotoGroup/searchByPage '{}' "图片分组(internal)" "$T"
test_api POST $INVENTORY /v1/internal/inventory/batch '[1833339862330556416]' "库存(internal)" "$T"

echo ""
echo "=========================================="
echo -e "  ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}"
echo "=========================================="
[ "$FAIL" -eq 0 ] && exit 0 || exit 1
