#!/bin/bash

# 添加测试商品脚本
# 接口文档: http://localhost:8023/swagger-ui/index.html (假设 Swagger 路径)
# 接口地址: http://localhost:8023/v1/product/insert

# 使用从数据库查询到的有效 ID
# Category ID: 1825743046605824001
# Brand ID: 1
# Unit ID: 1
# Attribute ID (SPU): 1 (长), Value ID: 1 (100)
# Attribute ID (SKU): 2 (宽), Value ID: 3 (200)

curl -X POST http://localhost:8023/v1/product/insert \
-H "Content-Type: application/json" \
-d '{
  "categoryId": 1825743046605824001,
  "brandId": 1,
  "unitId": 1,
  "name": "自动测试商品",
  "quantity": 100,
  "price": 99.99,
  "isNew": true,
  "attributeValueIds": "1,3",
  "spuAttributeEntityList": [
      { "id": 1, "attributeId": 1, "value": "100", "attributeName": "长" }
  ],
  "skuAttributeEntityList": [
      { "id": 3, "attributeId": 2, "value": "200", "attributeName": "宽" }
  ]
}'

echo -e "\n测试商品添加请求已发送。"
