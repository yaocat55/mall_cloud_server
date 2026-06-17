#!/bin/bash
set -e

# 一键部署到 Kind 集群
# 用法: ./deploy/deploy.sh [集群名称]

CLUSTER_NAME="${1:-mall}"
cd "$(dirname "$0")/.."

echo "=== 1/5 编译项目 ==="
mvn clean package -DskipTests -q

echo ""
echo "=== 2/5 构建 Docker 镜像 ==="
SERVICES=("gateway" "auth" "basic" "product" "order" "pay" "marketing" "recommend" "message")
for svc in "${SERVICES[@]}"; do
  echo "  -> mall-$svc:latest"
  docker build -t "mall-$svc:latest" -q "mall-$svc/"
done

echo ""
echo "=== 3/5 加载镜像到 Kind ==="
for svc in "${SERVICES[@]}"; do
  echo "  -> mall-$svc:latest"
  kind load docker-image "mall-$svc:latest" --name "$CLUSTER_NAME"
done

echo ""
echo "=== 4/5 创建命名空间 ==="
kubectl apply -f deploy/k8s/namespace.yaml

echo ""
echo "=== 5/5 部署服务 ==="
for svc in "${SERVICES[@]}"; do
  echo "  -> mall-$svc"
  kubectl apply -f "deploy/k8s/mall-$svc.yaml"
done

echo ""
echo "=== 部署完成 ==="
echo "查看状态: kubectl get pods -n mall -w"
echo "暴露 Gateway 到本地: kubectl port-forward -n mall svc/mall-gateway 8080:8080"
