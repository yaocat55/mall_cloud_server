#!/bin/bash
set -e

# 编译所有模块并构建 Docker 镜像
# 用法: ./deploy/build.sh

echo "=== Maven 编译 ==="
cd "$(dirname "$0")/.."
mvn clean package -DskipTests -q

echo ""
echo "=== 构建 Docker 镜像 ==="
SERVICES=("gateway" "auth" "basic" "product" "order" "pay" "marketing" "recommend" "message")
for svc in "${SERVICES[@]}"; do
  echo "  -> mall-$svc:latest"
  docker build -t "mall-$svc:latest" "mall-$svc/"
done

echo ""
echo "=== 完成 ==="
docker images | grep mall-
