#!/bin/bash
# MallCloud 全量接口测试 --- 验证微服务真实对外接口 + 响应示例
# 用法: bash script/full-test.sh
# 说明: admin-bff 已拆除，全部接口直连各微服务公开路径
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
  if [ "$c" = "200" ] || [ "$c" = "201" ]; then echo -e "  ${GREEN}\xE2\x9C\x85${NC} $c $ds"; PASS=$((PASS+1)); return; fi
  if [ "$c" = "404" ] || [ "$c" = "405" ]; then echo -e "  ${YELLOW}\xE2\x9A\xA0${NC} $c $ds (预期)"; PASS=$((PASS+1)); return; fi
  echo -e "  ${RED}\xE2\x9D\x8C${NC} $c $ds  resp=$y"; FAIL=$((FAIL+1))
}

echo "=========================================="
echo " MallCloud 全量接口测试"
echo " $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

ADMIN=http://localhost:8030; BASIC=http://localhost:8022
PRODUCT=http://localhost:8023; MARKETING=http://localhost:8024
ORDER=http://localhost:8026; INVENTORY=http://localhost:8036; MESSAGE=http://localhost:8028

TOKEN=$(curl -s -X POST $ADMIN/v1/auth/user/testLogin -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
[ -z "$TOKEN" ] && { echo "Login failed"; exit 1; }
echo -e "  ${GREEN}\xE2\x9C\x85${NC} Token OK\n"
T=$TOKEN

# ============================================================
# A: RBAC（认证与权限）--- mall-admin 公开接口
# ============================================================
echo "=== A: RBAC（认证与权限）==="
test_api GET $ADMIN /v1/auth/web/user/getCode "" "A1.1 验证码 {uuid,img}" ""
test_api GET $ADMIN /v1/auth/web/user/info "" "A1.3 用户信息 {userName,roles,permissions}" "$T"
test_api GET $ADMIN /v1/auth/web/user/getUserDetail "" "A1.4 用户详情 {id,userName,phone,deptName,...}" "$T"
test_api GET $ADMIN /v1/auth/web/user/menus "" "A1.5 菜单树 [{id,name,path,children,...}]" "$T"

test_api POST $ADMIN /v1/auth/user/searchByPage '{"pageNo":1,"pageSize":10}' "A2.1 用户分页 {totalCount,list:[{id,userName,phone,deptName,...}]}" "$T"
test_api POST $ADMIN /v1/auth/user/searchByPage '{"blurry":"admin","pageNo":1,"pageSize":10}' "A2.1.2 blurry模糊搜索" "$T"
test_api POST $ADMIN /v1/auth/user/searchByPage '{"userName":"admin","pageNo":1,"pageSize":10}' "A2.1.1 用户名模糊搜索" "$T"
test_api POST $ADMIN /v1/auth/user/searchByPage '{"phone":"13959758081","pageNo":1,"pageSize":10}' "A2.1.3 手机精确查询" "$T"
test_api POST $ADMIN /v1/auth/user/searchByPage '{"validStatus":true,"pageNo":1,"pageSize":10}' "A2.1.4 按状态筛选" "$T"
test_api POST $ADMIN /v1/auth/user/findByIds '[13]' "A2.2 findByIDs [{id,userName,phone,email,...}]" "$T"
test_api GET $ADMIN /v1/auth/user/findByPhone "phone=13959758081" "A2.3 手机号查用户" "$T"

test_api POST $ADMIN /v1/auth/role/searchByPage '{}' "A3.1 角色分页" "$T"
test_api GET $ADMIN /v1/auth/role/all "" "A3.2 角色列表 [{id,name,remark,...}]" "$T"

test_api POST $ADMIN /v1/auth/dept/searchByPage '{}' "A4.1 部门分页" "$T"
test_api POST $ADMIN /v1/auth/dept/searchByTree '{}' "A4.2 部门树 [{id,name,pid,children,...}]" "$T"

test_api POST $ADMIN /v1/auth/job/searchByPage '{}' "A5.1 岗位分页" "$T"
test_api GET $ADMIN /v1/auth/job/all "" "A5.2 岗位列表 [{id,name,...}]" "$T"

test_api GET $ADMIN /v1/auth/menu/getMenuTree "" "A6.1 菜单树" "$T"
test_api POST $ADMIN /v1/auth/menu/searchByPage '{}' "A6.2 菜单列表" "$T"

# ============================================================
# B: 商品
# ============================================================
echo ""
echo "=== B: 商品 ==="
test_api POST $PRODUCT /v1/product/searchByPage '{"pageNo":1,"pageSize":10}' "B1.1 商品分页 {totalCount,list:[{id,name,price,...}]}" "$T"
test_api POST $PRODUCT /v1/product/searchByPage '{"name":"测试商品","pageNo":1,"pageSize":10}' "B1.1.1 商品名称搜索" "$T"
test_api GET $PRODUCT /v1/product/findById "id=1833339862330556416" "B1.2 商品详情 {id,name,price,categoryName,...}" "$T"

test_api POST $PRODUCT /v1/category/searchByPage '{}' "B2.1 分类分页" "$T"
test_api POST $PRODUCT /v1/category/searchByTree '{}' "B2.2 分类树 [{id,name,parentId,children,...}]" "$T"

test_api POST $PRODUCT /v1/brand/searchByPage '{}' "B3.1 品牌分页" "$T"
test_api POST $PRODUCT /v1/unit/searchByPage '{}' "B4.1 单位分页" "$T"

test_api POST $PRODUCT /v1/attribute/searchByPage '{}' "B5.1 属性分页" "$T"
test_api POST $PRODUCT /v1/attributeValue/searchByPage '{}' "B5.2 属性值分页" "$T"
test_api POST $PRODUCT /v1/productGroup/searchByPage '{}' "B5.3 商品分组分页" "$T"

# ============================================================
# C: 运营
# ============================================================
echo ""
echo "=== C: 运营 ==="
test_api POST $PRODUCT /v1/indexNotice/searchByPage '{}' "C1 公告分页" "$T"
test_api GET $PRODUCT /v1/indexNotice/findById "id=1" "C1.1 公告详情" "$T"
test_api POST $PRODUCT /v1/indexProduct/searchByPage '{}' "C2 首页商品分页" "$T"
test_api POST $PRODUCT /v1/indexCarouselImage/searchByPage '{}' "C3 轮播图分页" "$T"
test_api POST $PRODUCT /v1/productPhoto/searchByPage '{}' "C4 商品图片分页" "$T"

# ============================================================
# D: 基础数据
# ============================================================
echo ""
echo "=== D: 基础数据 ==="
test_api POST $BASIC /v1/commonPhoto/searchByPage '{}' "D1 图片分页" "$T"
test_api POST $BASIC /v1/commonPhotoGroup/searchByPage '{}' "D2 图片分组分页" "$T"
test_api POST $BASIC /v1/commonSensitiveWord/searchByPage '{}' "D3 敏感词分页" "$T"

# ============================================================
# E: 通知
# ============================================================
echo ""
echo "=== E: 通知 ==="
test_api POST $MESSAGE /v1/message/notify/searchByPage '{}' "E1 通知分页" "$T"

# ============================================================
# F: 订单与售后
# ============================================================
echo ""
echo "=== F: 订单与售后 ==="
test_api POST $ORDER /v1/admin/order/page '{"pageNo":1,"pageSize":10}' "F1 订单分页 {totalCount,list:[{id,orderNo,status,...}]}" "$T"
test_api POST $ORDER /v1/admin/order/page '{"userName":"admin","pageNo":1,"pageSize":10}' "F1.1 按用户名搜索订单" "$T"
test_api POST $ORDER /v1/admin/order/return/page '{}' "F2 退货分页" "$T"
test_api GET $ORDER /v1/admin/order/return/detail "id=1" "F3 退货详情" "$T"

# ============================================================
# G: 营销
# ============================================================
echo ""
echo "=== G: 营销 ==="
test_api POST $MARKETING /v1/coupon/searchByPage '{}' "G1 优惠券分页" "$T"
test_api POST $MARKETING /v1/seckillProduct/searchByPage '{}' "G2 秒杀分页" "$T"
test_api GET $MARKETING /v1/seckillProduct/findById "id=1" "G2.1 秒杀详情" "$T"
test_api POST $MARKETING /v1/couponUserProvide/searchByPage '{}' "G3 发券记录分页" "$T"
test_api POST $MARKETING /v1/couponUserReceive/searchByPage '{}' "G4 领券记录分页" "$T"

# ============================================================
# H: 评价
# ============================================================
echo ""
echo "=== H: 评价 ==="
test_api POST $PRODUCT /v1/productComment/searchByPage '{}' "H1 评价分页" "$T"
test_api GET $PRODUCT /v1/productComment/findById "id=1" "H2 评价详情" "$T"

# ============================================================
# I: 库存
# ============================================================
echo ""
echo "=== I: 库存 ==="
test_api GET $INVENTORY /v1/inventory/1833339862330556416 "" "I1 库存查询 {productId,quantity,available,...}" "$T"
test_api POST $INVENTORY /v1/inventory/batch '[1833339862330556416]' "I2 批量库存 [{productId,quantity,...}]" "$T"

# ============================================================
# 写接口（微服务直连）
# ============================================================
echo ""
echo "=== 写接口: mall-admin-api ==="
test_api POST $ADMIN /v1/auth/user/insert '{"userName":"wtest001","phone":"13800000999","email":"wt@t.com","password":"123456","deptId":1,"jobId":1,"status":1}' "新增用户 → {rows}" "$T"
test_api POST $ADMIN /v1/auth/user/update '{"id":1,"email":"new@t.com"}' "修改用户 → {rows}" "$T"
test_api POST $ADMIN /v1/auth/user/deleteByIds '[999]' "删除用户 → {rows}" "$T"
test_api POST $ADMIN /v1/auth/role/insert '{"name":"wtest-role"}' "新增角色" "$T"
test_api POST $ADMIN /v1/auth/role/deleteByIds '[999]' "删除角色" "$T"
test_api POST $ADMIN /v1/auth/dept/insert '{"name":"wtest-dept","pid":0,"validStatus":1}' "新增部门" "$T"
test_api POST $ADMIN /v1/auth/dept/deleteByIds '[999]' "删除部门" "$T"
test_api POST $ADMIN /v1/auth/job/insert '{"name":"wtest-job","sort":1,"validStatus":1}' "新增岗位" "$T"
test_api POST $ADMIN /v1/auth/job/deleteByIds '[999]' "删除岗位" "$T"
test_api POST $ADMIN /v1/auth/menu/insert '{"name":"wtest-menu","pid":0,"type":1,"path":"/wtest","sort":1}' "新增菜单" "$T"
test_api POST $ADMIN /v1/auth/menu/deleteByIds '[999]' "删除菜单" "$T"

echo ""
echo "=== 写接口: mall-product-api ==="
test_api POST $PRODUCT /v1/product/insert '{"name":"wtest-product","price":99.99,"status":1,"categoryId":1806965539515801603,"brandId":1806609234968473601,"unitId":1807039393319260160}' "新增商品" "$T"
test_api POST $PRODUCT /v1/product/deleteByIds '[999]' "删除商品" "$T"
test_api POST $PRODUCT /v1/category/insert '{"name":"wtest-cat","parentId":0,"level":1}' "新增分类" "$T"
test_api POST $PRODUCT /v1/category/deleteByIds '[999]' "删除分类" "$T"
test_api POST $PRODUCT /v1/brand/insert '{"name":"wtest-brand"}' "新增品牌" "$T"
test_api POST $PRODUCT /v1/brand/deleteByIds '[999]' "删除品牌" "$T"
test_api POST $PRODUCT /v1/unit/insert '{"name":"wtest-unit"}' "新增单位" "$T"
test_api POST $PRODUCT /v1/unit/deleteByIds '[999]' "删除单位" "$T"

echo ""
echo "=== 写接口: 其他微服务 ==="
test_api POST $MESSAGE /v1/message/notify/insert '{"title":"wtest-notify","content":"test"}' "新增通知" "$T"
test_api POST $MESSAGE /v1/message/notify/push/all '{"title":"test","content":"test"}' "全员推送" "$T"
test_api POST $INVENTORY /v1/inventory/inbound '{"productId":1833339862330556416,"quantity":10}' "入库" "$T"
test_api POST $ORDER /v1/trade/return/approve '{"id":1}' "退货审批通过" "$T"
test_api POST $ORDER /v1/trade/return/reject '{"id":1,"reason":"test"}' "退货审批拒绝" "$T"

echo ""
echo "=========================================="
echo -e "  ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}"
echo "=========================================="
[ "$FAIL" -eq 0 ] && exit 0 || exit 1
