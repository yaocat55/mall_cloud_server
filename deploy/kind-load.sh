#!/bin/bash
set -e

# 将已构建的镜像加载到 Kind 集群
# 用法: ./deploy/kind-load.sh [集群名称]
# 默认集群名称: mall

CLUSTER_NAME="${1:-mall}"

echo "=== 加载镜像到 Kind 集群: $CLUSTER_NAME ==="
SERVICES=("gateway" "basic" "product" "order" "pay" "marketing" "recommend" "message")
for svc in "${SERVICES[@]}"; do
  echo "  -> mall-$svc:latest"
  kind load docker-image "mall-$svc:latest" --name "$CLUSTER_NAME"
done

echo "=== 完成 ==="
